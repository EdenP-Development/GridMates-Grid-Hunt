package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CoastalBeaconExpeditionTest {
    @Test fun fourObjectivesCompleteWithCorrectMapAnswersOnly() {
        val run = CoastalBeaconExpedition.run
        assertEquals(4, run.size)
        assertEquals(0, run.acceptGrid(0, "2437"))
        assertEquals(1, run.acceptGrid(0, "4256"))
        assertEquals(2, run.acceptGrid(1, "4656"))
        assertEquals(2, run.acceptGrid(2, "4455"))
        assertEquals(2, run.acceptLandmark(2, "old-waystone"))
        assertEquals(2, run.acceptLandmark(2, "shell-beach"))
        assertEquals(3, run.acceptLandmark(2, "gull-rock"))
        assertEquals(4, run.acceptGrid(3, "4753"))
        assertTrue(run.finished(4))
        assertEquals(4, run.discoveries(4).size)
        assertNull(GridHuntCampaign.registry.findChapter(run.chapter.id))
    }

    @Test fun mismatchedChapterMapAndLandmarkAnswersAreRejected() {
        val chapter = CoastalBeaconExpedition.chapter
        assertThrows(IllegalArgumentException::class.java) {
            chapter.copy(map = ExplorationMaps.woodland)
        }
        assertThrows(IllegalArgumentException::class.java) {
            chapter.copy(objectives = listOf(chapter.objectives[1].copy(
                expectedCell = GridReference.parse("4352")
            )))
        }
    }
}
