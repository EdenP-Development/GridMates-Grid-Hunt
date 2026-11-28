package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class MapIdentifyMissionTest {
    @Test fun coastalQuestionsAreUniqueAndRejectWrongAnswers() {
        val mission = IdentifyMissionCatalog.coastalDraft
        assertEquals(listOf("4656","4352","4551","4753","4455","4654"), mission.questions.map { it.cell.reference })
        mission.questions.forEachIndexed { step, question ->
            assertTrue(mission.validateQuestion(question))
            assertEquals(step, mission.accept(step, null))
            assertEquals(step, mission.accept(step, "unknown"))
            mission.choices.filter { it.id != question.answerLandmarkId }.forEach {
                assertEquals(step, mission.accept(step,it.id))
            }
            assertEquals(step+1, mission.accept(step,question.answerLandmarkId))
            assertFalse(mission.validateQuestion(question.copy(cell = mission.questions[(step+1)%6].cell)))
        }
        assertTrue(mission.finished(6))
        assertNull(mission.questionAt(6))
        assertEquals(0,mission.restoreStep(-1))
        assertEquals(6,mission.restoreStep(99))
    }

    @Test fun woodlandCompatibilityAndMapIsolation() {
        val woodland = IdentifyMissionCatalog.woodland
        val coast = IdentifyMissionCatalog.coastalDraft
        assertEquals(WoodlandIdentifyMission.questions,woodland.questions)
        assertEquals(WoodlandIdentifyMission.saveName,woodland.saveName)
        assertNotEquals(woodland.saveName,coast.saveName)
        assertFalse(coast.correct(0,woodland.choices.first().id))
        assertFalse(woodland.validateQuestion(coast.questions.first()))
        assertNull(IdentifyMissionCatalog.forAuthoredMap(coast.map.copy(revision=2)))
        assertNull(ExplorationMaps.find(coast.map.id,coast.map.revision))
    }
}
