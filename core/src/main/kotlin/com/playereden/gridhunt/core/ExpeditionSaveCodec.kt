package com.playereden.gridhunt.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.security.MessageDigest

/**
 * Candidate-only bounded binary envelope for trusted local project progress.
 * The checksum detects accidental damage, not malicious modification.
 * No disk writes, remote authentication, migration or live save integration.
 */
object ExpeditionSaveCodec {
    const val schemaVersion = 1
    const val maxBytes = 2 * 1024 * 1024
    private const val maxIds = 20000
    private const val checksumBytes = 32
    private const val magic = 0x47484558 // GHEX

    private fun digest(bytes: ByteArray) =
        MessageDigest.getInstance("SHA-256").digest(bytes)

    // DataOutputStream.writeUTF uses modified UTF-8, including surrogate code
    // units and a two-byte length prefix. Match its exact wire size.
    private fun utfSize(value: String): Int {
        var length = 0
        for (ch in value) {
            length += when (ch.code) {
                in 1..127 -> 1
                in 0..2047 -> 2
                else -> 3
            }
            require(length <= 65535) { "UTF field exceeds wire limit" }
        }
        return length + 2
    }

    fun encode(contract: ExpeditionProgressContract,
               snapshot: ExpeditionProgressContract.Snapshot): ByteArray {
        require(contract.restore(snapshot.identity,snapshot.solvedObjectiveIds) != null)
        require(snapshot.solvedObjectiveIds.size <= maxIds)
        val identity = snapshot.identity
        // Fixed fields: magic, schema, UUID, revisions and count = 36 bytes.
        var bodySize = 36 + utfSize(identity.chapterId) +
            utfSize(identity.manifestFingerprint)
        require(bodySize <= maxBytes - checksumBytes)
        for (objectiveId in snapshot.solvedObjectiveIds) {
            bodySize += utfSize(objectiveId)
            require(bodySize <= maxBytes - checksumBytes) { "Save exceeds byte limit" }
        }
        val buffer = ByteArrayOutputStream(bodySize)
        DataOutputStream(buffer).use { out ->
            out.writeInt(magic)
            out.writeInt(schemaVersion)
            val id = snapshot.identity
            out.writeLong(id.projectId.mostSignificantBits)
            out.writeLong(id.projectId.leastSignificantBits)
            out.writeUTF(id.chapterId)
            out.writeInt(id.chapterRevision)
            out.writeInt(id.manifestSchema)
            out.writeUTF(id.manifestFingerprint)
            out.writeInt(snapshot.solvedObjectiveIds.size)
            snapshot.solvedObjectiveIds.sorted().forEach { out.writeUTF(it) }
        }
        val body = buffer.toByteArray()
        check(body.size == bodySize)
        return body + digest(body)
    }

    /** Reject damaged, incompatible, duplicate, oversized or trailing data. */
    fun decode(contract: ExpeditionProgressContract, bytes: ByteArray):
        ExpeditionProgressContract.Snapshot? {
        if (bytes.size <= checksumBytes || bytes.size > maxBytes) return null
        val body = bytes.copyOfRange(0,bytes.size-checksumBytes)
        val checksum = bytes.copyOfRange(body.size,bytes.size)
        if (!MessageDigest.isEqual(digest(body),checksum)) return null
        return try {
            DataInputStream(ByteArrayInputStream(body)).use { input ->
                if (input.readInt() != magic || input.readInt() != schemaVersion) return null
                val identity = ExpeditionIdentity(
                    java.util.UUID(input.readLong(),input.readLong()),
                    input.readUTF(),input.readInt(),input.readInt(),input.readUTF())
                if (identity != contract.identity) return null
                val count = input.readInt()
                if (count !in 0..maxIds) return null
                val solved = linkedSetOf<String>()
                repeat(count) {
                    if (!solved.add(input.readUTF())) return null
                }
                if (input.read() != -1) return null
                contract.restore(identity,solved)
            }
        } catch (_: IOException) {
            null
        }
    }
}
