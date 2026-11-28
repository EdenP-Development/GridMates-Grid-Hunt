package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandFieldSurveyCandidateTest {
    private val candidate = WoodlandFieldSurveyCandidate
    @Test fun exactLegacyPrefixAndDistinctCells() {
        val chapter = candidate.chapter
        assertEquals(12, chapter.objectives.size)
        assertEquals(WoodlandExpedition.chapter.objectives, chapter.objectives.take(4))
        assertEquals(12, chapter.objectives.map { it.expectedCell }.distinct().size)
        assertEquals(1, GridHuntCampaign.registry.findChapter(chapter.id)!!.chapter.revision)
        assertNotEquals(WoodlandExpedition.saveName, candidate.run.saveName)
    }

    @Test fun authoredAnswersAndOffsets() {
        val refs = listOf("2437", "2440", "2538", "2941", "2537", "2838",
            "2840", "2639", "2641", "2541", "2741", "2942")
        assertEquals(refs, candidate.chapter.objectives.map { it.expectedCell.reference })
        fun offset(from: Int, east: Int, north: Int, to: Int) {
            val a = candidate.chapter.objectives[from].expectedCell
            val z = candidate.chapter.objectives[to].expectedCell
            assertEquals(a.eastIndex + east, z.eastIndex)
            assertEquals(a.northIndex + north, z.northIndex)
        }
        offset(2, 0, -1, 4)
        offset(5, 0, 2, 6)
        offset(6, -2, -1, 7)
        offset(9, 2, 0, 10)
        var step = 0
        for (objective in candidate.chapter.objectives) {
            val run = candidate.run
            if (objective.verb == ExpeditionVerb.IDENTIFY_LANDMARK) {
                assertEquals(step, run.acceptLandmark(step, "unknown"))
                assertEquals(step, run.acceptGrid(step, objective.expectedCell.reference))
                step = run.acceptLandmark(step, objective.landmarkId)
            } else {
                assertEquals(step, run.acceptGrid(step, "0000"))
                assertEquals(step, run.acceptLandmark(step, "old-waystone"))
                step = run.acceptGrid(step, objective.expectedCell.reference)
            }
            assertEquals(candidate.chapter.objectives.indexOf(objective) + 1, step)
        }
        assertTrue(candidate.run.finished(step))
        assertEquals(12, candidate.run.discoveries(step).size)
    }

    @Test fun legacyProgressAndEarnedReplayRemainSeparate() {
        for (step in 0..4) {
            val p = requireNotNull(candidate.plan.project(step, false))
            assertEquals(step, candidate.plan.completedObjectives(p))
            assertEquals(step == 4, p.legacyEntitlement)
            assertFalse(candidate.plan.isExpandedComplete(p))
        }
        val replay = requireNotNull(candidate.plan.project(1, true))
        assertTrue(replay.legacyEntitlement)
        assertEquals(1, replay.solvedObjectiveIds.size)
        assertNull(candidate.plan.project(5, false))
        assertNull(candidate.plan.project(-1, false))
    }
}
