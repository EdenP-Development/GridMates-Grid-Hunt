package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CoastalTideExpeditionTest {
    @Test fun directionalCluesMatchCanonicalCells() {
        val map = WhisperingCoastDraft.map
        fun cell(id: String) = requireNotNull(map.landmark(id)).cell
        fun offset(c: GridCell, east: Int, north: Int) =
            GridCell(c.eastIndex + east, c.northIndex + north)
        val objectives = CoastalTideExpedition.chapter.objectives
        assertEquals(offset(cell("beacon-point"), 0, -2), objectives[0].expectedCell)
        assertEquals(cell("tidepool-bend"), objectives[1].expectedCell)
        assertEquals(offset(cell("tidepool-bend"), -3, -2), objectives[2].expectedCell)
        assertEquals(offset(cell("driftwood-cove"), 2, -1), objectives[3].expectedCell)
    }

    @Test fun chapterCompletesWithoutAcceptingWrongDirectionsOrAnswerTypes() {
        val run = CoastalTideExpedition.run
        assertEquals(0, run.acceptGrid(0, "4655"))
        assertEquals(1, run.acceptGrid(0, "4654"))
        assertEquals(1, run.acceptGrid(1, "4654"))
        assertEquals(2, run.acceptLandmark(1, "tidepool-bend"))
        assertEquals(2, run.acceptGrid(2, "4353"))
        assertEquals(3, run.acceptGrid(2, "4352"))
        assertEquals(3, run.acceptLandmark(3, "old-waystone"))
        assertEquals(4, run.acceptLandmark(3, "shell-beach"))
        assertTrue(run.finished(4))
        assertEquals(4, run.discoveries(4).size)
        assertNotEquals(CoastalBeaconExpedition.saveName, run.saveName)
        assertNull(GridHuntCampaign.registry.findChapter(run.chapter.id))
    }
}
