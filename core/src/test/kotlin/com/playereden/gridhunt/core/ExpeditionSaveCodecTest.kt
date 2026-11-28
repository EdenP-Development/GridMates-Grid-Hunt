package com.playereden.gridhunt.core

import java.util.UUID
import java.security.MessageDigest
import org.junit.Assert.*
import org.junit.Test

class ExpeditionSaveCodecTest {
    private val chapter = WoodlandExpedition.chapter
    private val contract = ExpeditionProgressContract(chapter,UUID(1,2))
    private val ids = chapter.objectives.map { it.id }
    private fun encode(indices: Set<Int>): ByteArray =
        ExpeditionSaveCodec.encode(contract,requireNotNull(contract.restore(
            contract.identity,indices.map { ids[it] }.toSet())))

    @Test fun emptyPartialAndCompleteRoundTrip() {
        listOf(emptySet(),setOf(0,2),ids.indices.toSet()).forEach { indices ->
            val decoded = requireNotNull(ExpeditionSaveCodec.decode(contract,encode(indices)))
            assertEquals(contract.identity,decoded.identity)
            assertEquals(indices.map { ids[it] }.toSet(),decoded.solvedObjectiveIds)
        }
    }

    @Test fun deterministicOrdering() {
        assertArrayEquals(encode(linkedSetOf(2,0)),encode(linkedSetOf(0,2)))
    }

    @Test fun corruptionTruncationAndWrongProjectAreRejected() {
        val bytes = encode(setOf(0,1))
        val corrupt = bytes.copyOf()
        corrupt[10] = (corrupt[10].toInt() xor 1).toByte()
        assertNull(ExpeditionSaveCodec.decode(contract,corrupt))
        assertNull(ExpeditionSaveCodec.decode(contract,bytes.copyOf(bytes.size-1)))
        assertNull(ExpeditionSaveCodec.decode(contract,ByteArray(ExpeditionSaveCodec.maxBytes+1)))
        assertNull(ExpeditionSaveCodec.decode(
            ExpeditionProgressContract(chapter,UUID(1,3)),bytes))
        assertNull(ExpeditionSaveCodec.decode(
            ExpeditionProgressContract(chapter.copy(revision=2),UUID(1,2)),bytes))
    }

    @Test fun modifiedUtfAndOversizedFields() {
        val first = chapter.objectives.first()
        val unicode = first.copy(id="clue-\u0000-é-🌿")
        val revised = chapter.copy(objectives=listOf(unicode))
        val local = ExpeditionProgressContract(revised,UUID(3,4))
        val snapshot = requireNotNull(local.restore(local.identity,setOf(unicode.id)))
        val bytes = ExpeditionSaveCodec.encode(local,snapshot)
        assertEquals(snapshot.solvedObjectiveIds,
            requireNotNull(ExpeditionSaveCodec.decode(local,bytes)).solvedObjectiveIds)
        val huge = first.copy(id="x".repeat(65536))
        val oversized = ExpeditionProgressContract(chapter.copy(objectives=listOf(huge)),UUID(3,4))
        val progress = requireNotNull(oversized.restore(oversized.identity,setOf(huge.id)))
        assertTrue(runCatching { ExpeditionSaveCodec.encode(oversized,progress) }
            .exceptionOrNull() is IllegalArgumentException)
    }

    @Test fun rechecksummedInvalidCountsDuplicatesAndUnknownIdsAreRejected() {
        val id = contract.identity
        fun envelope(count: Int, values: List<String>): ByteArray {
            val buffer = java.io.ByteArrayOutputStream()
            java.io.DataOutputStream(buffer).use { out ->
                out.writeInt(0x47484558)
                out.writeInt(1)
                out.writeLong(id.projectId.mostSignificantBits)
                out.writeLong(id.projectId.leastSignificantBits)
                out.writeUTF(id.chapterId)
                out.writeInt(id.chapterRevision)
                out.writeInt(id.manifestSchema)
                out.writeUTF(id.manifestFingerprint)
                out.writeInt(count)
                values.forEach { out.writeUTF(it) }
            }
            val body = buffer.toByteArray()
            return body + MessageDigest.getInstance("SHA-256").digest(body)
        }
        assertNull(ExpeditionSaveCodec.decode(contract,envelope(-1,emptyList())))
        assertNull(ExpeditionSaveCodec.decode(contract,envelope(20001,emptyList())))
        assertNull(ExpeditionSaveCodec.decode(contract,envelope(2,listOf(ids[0],ids[0]))))
        assertNull(ExpeditionSaveCodec.decode(contract,envelope(1,listOf("unknown"))))
        assertNull(ExpeditionSaveCodec.decode(contract,envelope(2,listOf(ids[0]))))
    }

    @Test fun unknownSchemaAndTrailingBytesRejectedEvenWithValidChecksum() {
        val bytes = encode(setOf(0))
        val body = bytes.copyOf(bytes.size-32)
        fun seal(value: ByteArray) =
            value + MessageDigest.getInstance("SHA-256").digest(value)
        val unknown = body.copyOf()
        unknown[7] = 99
        assertNull(ExpeditionSaveCodec.decode(contract,seal(unknown)))
        assertNull(ExpeditionSaveCodec.decode(contract,seal(body+byteArrayOf(0))))
    }
}
