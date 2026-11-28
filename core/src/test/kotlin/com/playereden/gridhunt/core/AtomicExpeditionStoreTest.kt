package com.playereden.gridhunt.core

import java.io.File
import java.util.UUID
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class AtomicExpeditionStoreTest {
    @get:Rule val folder = TemporaryFolder()
    private val chapter = WoodlandExpedition.chapter
    private val contract = ExpeditionProgressContract(chapter,UUID(1,2))
    private val ids = chapter.objectives.map { it.id }
    private fun snapshot(vararg indices: Int) = requireNotNull(
        contract.restore(contract.identity,indices.map { ids[it] }.toSet()))

    @Test fun progressSurvivesNewStoreInstanceAndNeverRegresses() {
        val root = folder.newFolder()
        val store = AtomicExpeditionStore(root,contract)
        assertEquals(AtomicExpeditionStore.ReadResult.Missing,store.read())
        assertTrue(store.save(snapshot(0)))
        assertTrue(store.save(snapshot(0,2)))
        val reopened = AtomicExpeditionStore(root,contract)
        assertFalse(reopened.save(snapshot(0)))
        val ready = reopened.read() as AtomicExpeditionStore.ReadResult.Ready
        assertEquals(setOf(ids[0],ids[2]),ready.snapshot.solvedObjectiveIds)
    }

    @Test fun abandonedPartialWriteDoesNotReplaceCommittedSave() {
        val root = folder.newFolder()
        val store = AtomicExpeditionStore(root,contract)
        assertTrue(store.save(snapshot(0)))
        File(root,"${contract.identity.projectId}-abandoned.pending").writeBytes(byteArrayOf(1,2))
        val ready = AtomicExpeditionStore(root,contract).read() as AtomicExpeditionStore.ReadResult.Ready
        assertEquals(setOf(ids[0]),ready.snapshot.solvedObjectiveIds)
    }

    @Test fun invalidCommittedSaveIsNotSilentlyOverwritten() {
        val root = folder.newFolder()
        val active = File(root,"${contract.identity.projectId}.ghex")
        val damaged = byteArrayOf(3,4,5)
        active.writeBytes(damaged)
        val store = AtomicExpeditionStore(root,contract)
        assertEquals(AtomicExpeditionStore.ReadResult.Invalid,store.read())
        assertFalse(store.save(snapshot(0)))
        assertArrayEquals(damaged,active.readBytes())
    }

    @Test fun incompatibleProjectAndUnavailableDirectoryFailSafely() {
        val root = folder.newFolder()
        val store = AtomicExpeditionStore(root,contract)
        val other = ExpeditionProgressContract(chapter,UUID(2,3))
        assertFalse(store.save(other.empty()))
        assertEquals(AtomicExpeditionStore.ReadResult.Missing,store.read())
        val blocked = folder.newFile()
        assertFalse(AtomicExpeditionStore(blocked,contract).save(snapshot(0)))
    }
}
