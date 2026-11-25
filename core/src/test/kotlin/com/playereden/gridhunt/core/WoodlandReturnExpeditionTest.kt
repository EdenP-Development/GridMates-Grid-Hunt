package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandReturnExpeditionTest {
    private val mission = WoodlandReturnExpedition.run

    @Test fun candidateHasAllFourVerbsAndDistinctCanonicalCells() {
        val objectives = mission.chapter.objectives
        assertEquals(4, objectives.size)
        assertEquals(ExpeditionVerb.values().toSet(), objectives.map { it.verb }.toSet())
        assertEquals(4, objectives.map { it.expectedCell }.distinct().size)
        assertNotEquals(WoodlandExpedition.saveName, mission.saveName)
        assertNotEquals(WoodlandSupplyExpedition.saveName, mission.saveName)
        assertNull(GridHuntCampaign.registry.findChapter(mission.chapter.id))
    }

    @Test fun completeInOrderAndRejectWrongVerbOrAnswer() {
        assertEquals(0, mission.acceptGrid(0, "2941"))
        assertEquals(0, mission.acceptLandmark(0, WoodlandLandmarks.acorn.id))
        assertEquals(1, mission.acceptLandmark(0, WoodlandLandmarks.waystone.id))
        assertEquals(1, mission.acceptGrid(1, "2941"))
        assertEquals(2, mission.acceptGrid(1, ReverseTraining.markers.single { it.id == "A" }.cell.reference))
        assertEquals(2, mission.acceptLandmark(2, WoodlandLandmarks.acorn.id))
        assertEquals(3, mission.acceptGrid(2, WoodlandLandmarks.acorn.cell.reference))
        assertEquals(4, mission.acceptGrid(3, "2437"))
        assertTrue(mission.finished(4))
        assertEquals(4, mission.discoveries(4).size)
        assertEquals(2, mission.discoveries(2).size)
        assertEquals(0, mission.restoreStep(-1))
        assertEquals(4, mission.restoreStep(99))
    }
}
