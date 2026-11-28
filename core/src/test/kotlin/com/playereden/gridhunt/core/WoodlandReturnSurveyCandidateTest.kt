package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandReturnSurveyCandidateTest {
    private val candidate = WoodlandReturnSurveyCandidate

    @Test fun legacyPrefixAndUniqueAnswers() {
        val legacy = WoodlandReturnExpedition.chapter
        assertEquals(legacy.objectives, candidate.chapter.objectives.take(4))
        assertEquals(12, candidate.chapter.objectives.size)
        assertEquals(12, candidate.chapter.objectives.map { it.expectedCell }.distinct().size)
        assertEquals(legacy, GridHuntCampaign.registry.findChapter(legacy.id)!!.chapter)
        assertEquals(4, setOf(legacy.id, WoodlandReturnExpedition.saveName,
            WoodlandFieldSurveyCandidate.run.saveName, candidate.run.saveName).size)
        assertNotEquals(WoodlandSupplySurveyCandidate.run.saveName, candidate.run.saveName)
    }

    @Test fun correctOffsetsAndPlaythrough() {
        val objectives = candidate.chapter.objectives
        assertEquals(listOf("2941", "2440", "2641", "2437", "2638", "2538",
            "2838", "2840", "2740", "2541", "2642", "2841"),
            objectives.map { it.expectedCell.reference })
        fun offset(from: Int, east: Int, north: Int, to: Int) {
            val a = objectives[from].expectedCell
            val z = objectives[to].expectedCell
            assertEquals(a.eastIndex + east, z.eastIndex)
            assertEquals(a.northIndex + north, z.northIndex)
        }
        offset(3, 2, 1, 4)
        offset(6, 0, 2, 7)
        offset(7, -1, 0, 8)
        offset(9, 1, 1, 10)
        offset(10, 2, -1, 11)
        var step = 0
        objectives.forEachIndexed { i, objective ->
            if (objective.verb == ExpeditionVerb.IDENTIFY_LANDMARK) {
                assertEquals(step, candidate.run.acceptLandmark(step, "unknown"))
                assertEquals(step, candidate.run.acceptGrid(step, objective.expectedCell.reference))
                step = candidate.run.acceptLandmark(step, objective.landmarkId)
            } else {
                assertEquals(step, candidate.run.acceptGrid(step, "0000"))
                assertEquals(step, candidate.run.acceptLandmark(step, "old-waystone"))
                step = candidate.run.acceptGrid(step, objective.expectedCell.reference)
            }
            assertEquals(i + 1, step)
        }
        assertTrue(candidate.run.finished(step))
        assertEquals(12, candidate.run.discoveries(step).size)
    }

    @Test fun retainLegacyProgressAndEntitlement() {
        for (step in 0..4) {
            val p = requireNotNull(candidate.plan.project(step, false))
            assertEquals(candidate.chapter.objectives.take(step).map { it.id }.toSet(), p.solvedObjectiveIds)
            assertEquals(step == 4, p.legacyEntitlement)
            assertFalse(candidate.plan.isExpandedComplete(p))
        }
        val replay = requireNotNull(candidate.plan.project(0, true))
        assertTrue(replay.legacyEntitlement)
        assertTrue(replay.solvedObjectiveIds.isEmpty())
        assertNull(candidate.plan.project(-1, false))
        assertNull(candidate.plan.project(5, false))
    }
}
