package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CoastalLightExpeditionTest {
    @Test fun finalCluesMatchMapGeometry() {
        val map = WhisperingCoastDraft.map
        fun cell(id: String) = requireNotNull(map.landmark(id)).cell
        fun offset(c: GridCell, east: Int, north: Int) =
            GridCell(c.eastIndex + east, c.northIndex + north)
        val objectives = CoastalLightExpedition.chapter.objectives
        assertEquals(cell("shell-beach"), objectives[0].expectedCell)
        assertEquals(offset(cell("shell-beach"), 2, 2), objectives[1].expectedCell)
        assertEquals(cell("old-jetty"), objectives[1].expectedCell)
        assertEquals(cell("gull-rock"), objectives[2].expectedCell)
        assertEquals(offset(cell("gull-rock"), 2, 1), objectives[3].expectedCell)
        assertEquals(cell("beacon-point"), objectives[3].expectedCell)
    }

    @Test fun finaleCompletesAndRejectsWrongAnswers() {
        val run = CoastalLightExpedition.run
        assertEquals(0, run.acceptGrid(0, "2437"))
        assertEquals(1, run.acceptGrid(0, "4551"))
        assertEquals(1, run.acceptGrid(1, "4752"))
        assertEquals(2, run.acceptGrid(1, "4753"))
        assertEquals(2, run.acceptGrid(2, "4455"))
        assertEquals(2, run.acceptLandmark(2, "old-waystone"))
        assertEquals(3, run.acceptLandmark(2, "gull-rock"))
        assertEquals(4, run.acceptGrid(3, "4656"))
        assertTrue(run.finished(4))
        assertEquals(4, run.discoveries(4).size)
        assertEquals(4, run.acceptGrid(4, "4656"))
    }

    @Test fun allThreeDraftChaptersHaveSeparateIdentitiesAndStayUnpublished() {
        val runs = listOf(CoastalBeaconExpedition.run, CoastalTideExpedition.run,
            CoastalLightExpedition.run)
        assertEquals(3, runs.map { it.saveName }.distinct().size)
        assertEquals(3, runs.map { it.chapter.id }.distinct().size)
        assertEquals(12, runs.sumOf { it.size })
        assertTrue(runs.all { it.chapter.map == WhisperingCoastDraft.map })
        assertTrue(runs.all { GridHuntCampaign.registry.findChapter(it.chapter.id) == null })
    }
}
