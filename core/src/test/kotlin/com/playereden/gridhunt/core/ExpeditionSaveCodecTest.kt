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
