package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CoastalTideSurveyCandidateTest {
    private val candidate = CoastalTideSurveyCandidate

    @Test fun exactPrefixAndDistinctNewAnswers() {
        val legacy = CoastalTideExpedition.chapter
        val objectives = candidate.chapter.objectives
        assertEquals(legacy.objectives, objectives.take(4))
        assertEquals(12, objectives.size)
        assertEquals(3, objectives.take(4).map { it.expectedCell }.distinct().size)
        assertEquals(8, objectives.drop(4).map { it.expectedCell }.distinct().size)
        assertTrue(objectives.take(4).map { it.expectedCell }.toSet()
            .intersect(objectives.drop(4).map { it.expectedCell }.toSet()).isEmpty())
        assertEquals(WhisperingCoastDraft.map, candidate.chapter.map)
        assertTrue(objectives.all { it.map == candidate.chapter.map })
        assertNull(GridHuntCampaign.registry.findChapter(legacy.id))
        assertNotEquals(CoastalTideExpedition.saveName, candidate.run.saveName)
        assertNotEquals(CoastalBeaconSurveyCandidate.run.saveName, candidate.run.saveName)
    }

    @Test fun routeOffsetsAndFullPlaythrough() {
        val objectives = candidate.chapter.objectives
        assertEquals(listOf("4654", "4654", "4352", "4551", "4752", "4753",
            "4354", "4456", "4656", "4256", "4353", "4554"),
            objectives.map { it.expectedCell.reference })
        fun offset(from: Int, east: Int, north: Int, to: Int) {
            val a = objectives[from].expectedCell
            val z = objectives[to].expectedCell
            assertEquals(a.eastIndex + east, z.eastIndex)
            assertEquals(a.northIndex + north, z.northIndex)
        }
        offset(3, 2, 1, 4)
        offset(4, 0, 1, 5)
        offset(6, 1, 2, 7)
        offset(9, 1, -3, 10)
        offset(10, 2, 1, 11)
        var step = 0
        objectives.forEachIndexed { i, objective ->
            if (objective.verb == ExpeditionVerb.IDENTIFY_LANDMARK) {
                assertEquals(step, candidate.run.acceptLandmark(step, "unknown"))
                assertEquals(step, candidate.run.acceptGrid(step, objective.expectedCell.reference))
                step = candidate.run.acceptLandmark(step, objective.landmarkId)
            } else {
                assertEquals(step, candidate.run.acceptGrid(step, "0000"))
                assertEquals(step, candidate.run.acceptLandmark(step, "beacon-point"))
                step = candidate.run.acceptGrid(step, objective.expectedCell.reference)
            }
            assertEquals(i + 1, step)
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
