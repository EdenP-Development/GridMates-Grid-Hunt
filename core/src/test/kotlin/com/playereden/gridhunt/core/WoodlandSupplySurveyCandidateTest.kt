package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandSupplySurveyCandidateTest {
    private val candidate = WoodlandSupplySurveyCandidate

    @Test fun exactPrefixAndIndependentCandidate() {
        val legacy = WoodlandSupplyExpedition.chapter
        assertEquals(12, candidate.chapter.objectives.size)
        assertEquals(legacy.objectives, candidate.chapter.objectives.take(4))
        assertEquals(12, candidate.chapter.objectives.map { it.expectedCell }.distinct().size)
        assertEquals(legacy, GridHuntCampaign.registry.findChapter(legacy.id)!!.chapter)
        assertNotEquals(WoodlandSupplyExpedition.saveName, candidate.run.saveName)
        assertNotEquals(WoodlandFieldSurveyCandidate.run.saveName, candidate.run.saveName)
    }

    @Test fun verifiedRouteAndFullPlaythrough() {
        val objectives = candidate.chapter.objectives
        assertEquals(listOf("2742", "2840", "2641", "2941", "2939", "2838",
            "2538", "2437", "2440", "2642", "2942", "2740"),
            objectives.map { it.expectedCell.reference })
        fun offset(from: Int, east: Int, north: Int, to: Int) {
            val a = objectives[from].expectedCell
            val z = objectives[to].expectedCell
            assertEquals(a.eastIndex + east, z.eastIndex)
            assertEquals(a.northIndex + north, z.northIndex)
        }
        offset(3, 0, -2, 4)
        offset(5, -3, 0, 6)
        offset(6, -1, -1, 7)
        offset(8, 2, 2, 9)
        offset(10, -2, -2, 11)
        var step = 0
        for (objective in objectives) {
            val previous = step
            if (objective.verb == ExpeditionVerb.IDENTIFY_LANDMARK) {
                assertEquals(step, candidate.run.acceptLandmark(step, "unknown"))
                assertEquals(step, candidate.run.acceptGrid(step, objective.expectedCell.reference))
                step = candidate.run.acceptLandmark(step, objective.landmarkId)
            } else {
                assertEquals(step, candidate.run.acceptGrid(step, "0000"))
                assertEquals(step, candidate.run.acceptLandmark(step, "old-waystone"))
                step = candidate.run.acceptGrid(step, objective.expectedCell.reference)
            }
            assertEquals(previous + 1, step)
        }
        assertTrue(candidate.run.finished(step))
        assertEquals(12, candidate.run.discoveries(step).size)
    }

    @Test fun preserveEveryLegacyStepAndHistoricalEntitlement() {
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
