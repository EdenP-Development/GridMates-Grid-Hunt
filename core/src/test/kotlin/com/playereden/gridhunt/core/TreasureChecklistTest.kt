package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class TreasureChecklistTest {
    @Test fun landmarkManifestMatchesOriginalWoodlandArtAndHasUniqueSquares() {
        val all = WoodlandLandmarks.all
        assertEquals(4, all.size)
        assertEquals(listOf("2538", "2840", "2641", "2941"),
            all.map { it.cell.reference })
        assertEquals(all.size, all.map { it.id }.distinct().size)
        assertEquals(all.size, all.map { it.cell }.distinct().size)
        assertTrue(all.all { WoodlandTrainingMap.bounds.contains(it.point) })
        assertEquals(WoodlandTrainingMap.mapId, WoodlandLandmarks.mapId)
    }

    @Test fun onlyTheExactFourDigitAreaOfNamedItemIsAccepted() {
        val m = WoodlandLandmarks.mushrooms
        assertTrue(TreasureChecklistMission.correct(m.id, "2538"))
        assertFalse(TreasureChecklistMission.correct(m.id, "2539"))
        assertFalse(TreasureChecklistMission.correct(m.id, "2540"))
        assertFalse(TreasureChecklistMission.correct(m.id, "25 38"))
        assertFalse(TreasureChecklistMission.correct(m.id, "253"))
        assertFalse(TreasureChecklistMission.correct(m.id, "12538"))
        assertFalse(TreasureChecklistMission.correct(m.id, "abcd"))
        assertFalse(TreasureChecklistMission.correct("unpublished-landmark", "2538"))
    }

    @Test fun checklistCrossesOffOnlyTheCorrectNamedLandmark() {
        val mission = TreasureChecklistMission
        assertEquals(emptySet<String>(), mission.accept(emptySet(), "mushroom-nook", "2641"))
        val found = mission.accept(emptySet(), "mushroom-nook", "2538")
        assertEquals(setOf("mushroom-nook"), found)
        assertFalse(mission.completed(found))
        // An answer that belongs to an entirely different item must not cross it off.
        assertEquals(found, mission.accept(found, "buttercup-glade", "2538"))
        assertEquals(found, mission.accept(found, "mushroom-nook", "2538"))
    }

    @Test fun restorePreservesStableIdsAndDropsUnknownOnesWithoutMutation() {
        val old = setOf("mushroom-nook", "old-waystone", "future-object", "")
        val restored = TreasureChecklistMission.restoreCompleted(old)
        assertEquals(setOf("mushroom-nook", "old-waystone"), restored)
        assertEquals(4, old.size)
        val preferred = TreasureChecklistMission.activeTarget(restored, "buttercup-glade")
        assertEquals("buttercup-glade", preferred?.id)
        assertEquals("buttercup-glade",
            TreasureChecklistMission.activeTarget(restored, "mushroom-nook")?.id)
    }

    @Test fun completeFourItemsInAnyOrderAndNeverAutoReplay() {
        val m = TreasureChecklistMission
        var found = emptySet<String>()
        for (id in listOf("old-waystone", "buttercup-glade", "mushroom-nook", "acorn-nook")) {
            val answer = WoodlandLandmarks.byId(id)!!.cell.reference
            found = m.accept(found, id, answer)
        }
        assertTrue(m.completed(found))
        assertNull(m.activeTarget(found, null))
        assertEquals(found, m.accept(found, "mushroom-nook", "2538"))
        // Explicit replay is a UI choice: the saved set stays complete until cleared.
    }
}
