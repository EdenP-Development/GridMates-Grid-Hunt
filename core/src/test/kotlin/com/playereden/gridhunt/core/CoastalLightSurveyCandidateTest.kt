package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CoastalLightSurveyCandidateTest {
    private val candidate = CoastalLightSurveyCandidate

    @Test fun exactLegacyPrefixAndIndependentCandidate() {
        val legacy = CoastalLightExpedition.chapter
        val objectives = candidate.chapter.objectives
        assertEquals(legacy.objectives, objectives.take(4))
        assertEquals(12, objectives.size)
        assertEquals(12, objectives.map { it.expectedCell }.distinct().size)
        assertEquals(WhisperingCoastDraft.map, candidate.chapter.map)
        assertTrue(objectives.all { it.map == candidate.chapter.map })
        assertNull(GridHuntCampaign.registry.findChapter(legacy.id))
        assertNotEquals(CoastalLightExpedition.saveName, candidate.run.saveName)
        assertNotEquals(CoastalBeaconSurveyCandidate.run.saveName, candidate.run.saveName)
        assertNotEquals(CoastalTideSurveyCandidate.run.saveName, candidate.run.saveName)
    }

    @Test fun verifiedOffsetsAndFullPlaythrough() {
        val objectives = candidate.chapter.objectives
        assertEquals(listOf("4551", "4753", "4455", "4656", "4555", "4654",
            "4453", "4352", "4252", "4255", "4556", "4354"),
            objectives.map { it.expectedCell.reference })
        fun offset(from: Int, east: Int, north: Int, to: Int) {
            val a = objectives[from].expectedCell
            val z = objectives[to].expectedCell
            assertEquals(a.eastIndex + east, z.eastIndex)
            assertEquals(a.northIndex + north, z.northIndex)
        }
        offset(3, -1, -1, 4)
        offset(4, 1, -1, 5)
        offset(8, 0, 3, 9)
        offset(10, -2, -2, 11)
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

    @Test fun preserveLegacyProgressAndEntitlement() {
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
