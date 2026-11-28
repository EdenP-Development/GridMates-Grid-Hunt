package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandExpeditionTest {
    private val mission = WoodlandExpedition

    @Test fun aChapterUsesAllFourPuzzleVerbsWithCanonicalAuthoredMapData() {
        val steps = mission.chapter.objectives
        assertEquals(4, steps.size)
        assertEquals(ExpeditionVerb.entries.toList(), steps.map { it.verb })
        assertEquals(listOf("2437", "2440", "2538", "2941"),
            steps.map { it.expectedCell.reference })
        assertEquals(4, steps.map { it.id }.distinct().size)
        assertEquals(WoodlandLandmarks.mushrooms.id, steps[2].landmarkId)
        assertEquals(WoodlandLandmarks.waystone.id, steps[3].landmarkId)
    }

    @Test fun publishedChaptersHaveDistinctEarnedDiscoveryMessages() {
        listOf(mission.chapter, WoodlandSupplyExpedition.chapter).forEach { chapter ->
            val discoveries = chapter.objectives.map { it.discovery }
            assertTrue(discoveries.all { it.isNotBlank() })
            assertEquals(chapter.objectives.size, discoveries.distinct().size)
        }
    }

    @Test fun journalShowsOnlyEarnedEntriesAndIsIsolatedByChapter() {
        val first = ExpeditionPlaythrough(mission.chapter, mission.saveName)
        val second = WoodlandSupplyExpedition.run
        for (run in listOf(first, second)) {
            assertTrue(run.discoveries(-1).isEmpty())
            assertTrue(run.discoveries(0).isEmpty())
            for (step in 1..run.size) {
                assertEquals(run.chapter.objectives.take(step), run.discoveries(step))
                assertFalse(run.discoveries(step).any { it.id == run.objectiveAt(step)?.id })
            }
            assertEquals(run.size, run.discoveries(999).size)
            assertTrue(run.discoveries(0).isEmpty())
        }
        assertNotEquals(first.discoveries(1).first().id, second.discoveries(1).first().id)
    }

    @Test fun orderedAnswersOnlyAdvanceCurrentPuzzleVerb() {
        assertEquals(0, mission.acceptGrid(0, "2440"))
        assertEquals(0, mission.acceptGrid(0, "2941"))
        assertEquals(0, mission.acceptGrid(0, "243"))
        assertEquals(0, mission.acceptGrid(0, "abcd"))
        assertEquals(0, mission.acceptGrid(0, null))
        assertEquals(0, mission.acceptLandmark(0, WoodlandLandmarks.mushrooms.id))
        val first = mission.acceptGrid(0, "2437")
        assertEquals(1, first)
        assertEquals(1, mission.acceptGrid(first, "2437"))
        assertEquals(2, mission.acceptGrid(first, ReverseTraining.markers.first().cell.reference))
        assertEquals(2, mission.acceptLandmark(2, WoodlandLandmarks.mushrooms.id))
        assertEquals(3, mission.acceptGrid(2, WoodlandLandmarks.mushrooms.cell.reference))
        assertEquals(3, mission.acceptGrid(3, WoodlandLandmarks.waystone.cell.reference))
        assertEquals(3, mission.acceptLandmark(3, WoodlandLandmarks.mushrooms.id))
        assertEquals(4, mission.acceptLandmark(3, WoodlandLandmarks.waystone.id))
        assertTrue(mission.finished(4))
        assertNull(mission.objectiveAt(4))
    }

    @Test fun completedStepsRemainCompletedUntilExplicitReplay() {
        assertEquals(0, mission.restoreStep(-90))
        assertEquals(2, mission.restoreStep(2))
        assertEquals(4, mission.restoreStep(900))
        assertEquals(4, mission.acceptGrid(4, "2437"))
        assertEquals(4, mission.acceptLandmark(4, WoodlandLandmarks.waystone.id))
        assertEquals(listOf("2437", "2440"), mission.solvedCells(2).map { it.reference })
        assertEquals(4, mission.solvedCells(4).size)
        assertEquals(0, mission.restoreStep(0))
    }

    @Test fun invalidLandmarkTargetsOrMarkerRefsFailBeforeChapterPublication() {
        val base = mission.chapter
        val first = base.objectives.first()
        assertThrows(IllegalArgumentException::class.java) {
            base.copy(objectives = base.objectives + first)
        }
        assertThrows(IllegalArgumentException::class.java) {
            base.copy(objectives = base.objectives.map {
                if (it.verb == ExpeditionVerb.IDENTIFY_LANDMARK)
                    it.copy(expectedCell = GridReference.parse("2539")) else it
            })
        }
        assertThrows(IllegalArgumentException::class.java) {
            base.copy(objectives = base.objectives.map {
                if (it.verb == ExpeditionVerb.READ_MARKER) it.copy(markerId = "unknown")
                else it
            })
        }
    }
}
