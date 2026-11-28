package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class MapTreasureChecklistTest {
    private val coast = TreasureChecklistCatalog.coastalDraft
    private val woodland = TreasureChecklistCatalog.woodland

    @Test fun allSixCoastalFindsCanBeCompletedInAnyOrder() {
        assertEquals(6, coast.targets.size)
        assertEquals(listOf("4656","4352","4551","4753","4455","4654"), coast.targets.map { it.cell.reference })
        var completed = emptySet<String>()
        for (target in coast.targets.reversed()) {
            assertEquals(target, coast.activeTarget(completed, target.id))
            assertEquals(completed, coast.accept(completed,target.id,"0000"))
            assertEquals(completed, coast.accept(completed,target.id,"123"))
            completed = coast.accept(completed,target.id,target.cell.reference)
            assertTrue(target.id in completed)
            assertEquals(completed, coast.accept(completed,target.id,target.cell.reference))
        }
        assertTrue(coast.completed(completed))
        assertNull(coast.activeTarget(completed,null))
        assertTrue(woodland.restoreCompleted(completed).isEmpty())
    }

    @Test fun woodlandCompatibilityAndUnknownMaps() {
        assertEquals(TreasureChecklistMission.targets, woodland.targets)
        val mixed = setOf(woodland.targets.first().id, coast.targets.first().id, "unknown")
        assertEquals(TreasureChecklistMission.restoreCompleted(mixed), woodland.restoreCompleted(mixed))
        assertEquals(setOf(coast.targets.first().id), coast.restoreCompleted(mixed))
        assertNull(TreasureChecklistCatalog.forAuthoredMap(coast.map.copy(revision=2)))
        assertNull(ExplorationMaps.find(coast.map.id,coast.map.revision))
        assertFalse(coast.correct(woodland.targets.first().id,woodland.targets.first().cell.reference))
    }
}
