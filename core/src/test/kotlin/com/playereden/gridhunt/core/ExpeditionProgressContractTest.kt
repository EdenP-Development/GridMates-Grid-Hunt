package com.playereden.gridhunt.core

import java.util.UUID
import org.junit.Assert.*
import org.junit.Test

class ExpeditionProgressContractTest {
    private val chapter = WoodlandExpedition.chapter
    private val project = UUID.fromString("8f9bca10-17d4-4f11-bc5d-08c4d9254401")
    private val contract = ExpeditionProgressContract(chapter,project)
    private val ids = chapter.objectives.map { it.id }

    private fun snapshot(vararg indices: Int) =
        requireNotNull(contract.restore(contract.identity,indices.map { ids[it] }.toSet()))

    @Test fun overlappingOfflineDiscoveriesMergeOnceAndConverge() {
        val a = snapshot(0,1)
        val b = snapshot(1,2)
        val ab = requireNotNull(contract.reconcile(a,b))
        val ba = requireNotNull(contract.reconcile(b,a))
        assertEquals(setOf(ids[0],ids[1],ids[2]),ab.solvedObjectiveIds)
        assertEquals(ab.solvedObjectiveIds,ba.solvedObjectiveIds)
        assertEquals(ab.solvedObjectiveIds,
            requireNotNull(contract.reconcile(ab,ab)).solvedObjectiveIds)
        assertEquals(setOf(ids[0],ids[1]),a.solvedObjectiveIds)
    }

    @Test fun threeReplicaMergeIsAssociative() {
        val a = snapshot(0)
        val b = snapshot(1,2)
        val c = snapshot(2,3)
        val ab = requireNotNull(contract.reconcile(a,b))
        val bc = requireNotNull(contract.reconcile(b,c))
        assertEquals(requireNotNull(contract.reconcile(ab,c)).solvedObjectiveIds,
            requireNotNull(contract.reconcile(a,bc)).solvedObjectiveIds)
    }

    @Test fun separateProjectsAndChangedContentCannotMerge() {
        val other = ExpeditionProgressContract(chapter,UUID(0,1))
        val revised = ExpeditionProgressContract(chapter.copy(revision=2),project)
        val changed = ExpeditionProgressContract(chapter.copy(story="Different story"),project)
        assertNull(contract.reconcile(contract.empty(),other.empty()))
        assertNull(contract.reconcile(contract.empty(),revised.empty()))
        assertNull(contract.reconcile(contract.empty(),changed.empty()))
        assertNull(contract.restore(contract.identity.copy(manifestSchema=99),emptySet()))
        assertNull(contract.restore(contract.identity,setOf("unknown")))
    }

    @Test fun snapshotsAreDefensiveAndEmptyMergePreservesProgress() {
        val input = mutableSetOf(ids[0])
        val saved = requireNotNull(contract.restore(contract.identity,input))
        input.clear()
        assertEquals(setOf(ids[0]),saved.solvedObjectiveIds)
        assertEquals(saved.solvedObjectiveIds,
            requireNotNull(contract.reconcile(saved,contract.empty())).solvedObjectiveIds)
    }
}
