package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandSupplyExpeditionTest {
    private val mission = WoodlandSupplyExpedition.run

    @Test fun fourStepsReferenceRealVisibleFeaturesAndUseMoreThanOnePuzzleVerb() {
        val objectives = mission.chapter.objectives
        assertEquals("woodland-lost-supplies-v1", mission.chapter.id)
        assertEquals(listOf(
            ExpeditionVerb.READ_MARKER,
            ExpeditionVerb.LOCATE_LANDMARK,
            ExpeditionVerb.IDENTIFY_LANDMARK,
            ExpeditionVerb.LOCATE_LANDMARK
        ), objectives.map { it.verb })
        assertEquals(listOf("2742", "2840", "2641", "2941"),
            objectives.map { it.expectedCell.reference })
        assertEquals("B", objectives[0].markerId)
        assertEquals(WoodlandLandmarks.flowers.id, objectives[1].landmarkId)
        assertEquals(WoodlandLandmarks.acorn.id, objectives[2].landmarkId)
        assertEquals(WoodlandLandmarks.waystone.id, objectives[3].landmarkId)
        assertEquals(4, objectives.map { it.id }.distinct().size)
        assertNotEquals(WoodlandExpedition.saveName, mission.saveName)
        assertNotEquals(WoodlandExpedition.chapter.id, mission.chapter.id)
    }

    @Test fun wrongVerbAndWrongAnswersCannotSkipAnObjective() {
        assertEquals(0, mission.acceptGrid(0, "2840"))
        assertEquals(0, mission.acceptLandmark(0, WoodlandLandmarks.acorn.id))
        assertEquals(1, mission.acceptGrid(0, "2742"))
        assertEquals(1, mission.acceptGrid(1, "2742"))
        assertEquals(2, mission.acceptGrid(1, "2840"))
        assertEquals(2, mission.acceptGrid(2, "2641"))
        assertEquals(2, mission.acceptLandmark(2, "old-waystone"))
        assertEquals(3, mission.acceptLandmark(2, "acorn-nook"))
        assertEquals(3, mission.acceptLandmark(3, "old-waystone"))
        assertEquals(4, mission.acceptGrid(3, "2941"))
        assertTrue(mission.finished(4))
        assertEquals(4, mission.acceptGrid(4, "2742"))
        assertNull(mission.objectiveAt(4))
    }

    @Test fun progressClampsWithoutChangingChapterOne() {
        assertEquals(0, mission.restoreStep(-1))
        assertEquals(4, mission.restoreStep(999))
        assertEquals(2, mission.solvedCells(2).size)
        assertEquals(4, mission.solvedCells(4).size)
        assertEquals(4, WoodlandExpedition.chapter.objectives.size)
        assertEquals("gridhunt_mixed_woodland_v1", WoodlandExpedition.saveName)
    }
}
