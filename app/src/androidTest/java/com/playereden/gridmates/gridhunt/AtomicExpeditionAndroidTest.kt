package com.playereden.gridmates.gridhunt

import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.AtomicExpeditionStore
import com.playereden.gridhunt.core.ExpeditionProgressContract
import com.playereden.gridhunt.core.WoodlandExpedition
import java.io.File
import java.util.UUID
import org.junit.Assert.*
import org.junit.Test

/** Candidate store only. Never touches existing campaign or puzzle preferences. */
class AtomicExpeditionAndroidTest {
    @Test fun appPrivateFileReplacementAndReopen() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val root = File(context.filesDir,"expedition-qa-${UUID.randomUUID()}")
        val chapter = WoodlandExpedition.chapter
        val contract = ExpeditionProgressContract(chapter,UUID.randomUUID())
        val ids = chapter.objectives.map { it.id }
        fun snapshot(vararg indices: Int) = requireNotNull(
            contract.restore(contract.identity,indices.map { ids[it] }.toSet()))
        try {
            val store = AtomicExpeditionStore(root,contract)
            assertEquals(AtomicExpeditionStore.ReadResult.Missing,store.read())
            assertTrue(store.save(snapshot(0)))
            val active = File(root,"${contract.identity.projectId}.ghex")
            assertTrue(active.isFile)
            val firstBytes = active.readBytes()
            assertTrue(store.save(snapshot(0,1,3)))
            assertFalse(firstBytes.contentEquals(active.readBytes()))
            val reopened = AtomicExpeditionStore(root,contract)
            val ready = reopened.read() as AtomicExpeditionStore.ReadResult.Ready
            assertEquals(setOf(ids[0],ids[1],ids[3]),ready.snapshot.solvedObjectiveIds)
            assertFalse(reopened.save(snapshot(0)))
            File(root,"${contract.identity.projectId}-interrupted.pending")
                .writeBytes(byteArrayOf(1,2,3))
            val restored = AtomicExpeditionStore(root,contract).read()
                as AtomicExpeditionStore.ReadResult.Ready
            assertEquals(ready.snapshot.solvedObjectiveIds,restored.snapshot.solvedObjectiveIds)
            val damaged = byteArrayOf(4,5,6)
            active.writeBytes(damaged)
            assertEquals(AtomicExpeditionStore.ReadResult.Invalid,reopened.read())
            assertFalse(reopened.save(snapshot(0,1,2,3)))
            assertArrayEquals(damaged,active.readBytes())
        } finally {
            check(!root.exists() || root.deleteRecursively())
        }
    }
}
