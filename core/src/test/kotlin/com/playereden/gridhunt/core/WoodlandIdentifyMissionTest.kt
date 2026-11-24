package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WoodlandIdentifyMissionTest {
    private val mission = WoodlandIdentifyMission

    @Test fun fourExistingLandmarksHaveOneHonestAnswerInEachFourDigitCell() {
        assertEquals(4, mission.questions.size)
        assertEquals(listOf("2538", "2840", "2641", "2941"),
            mission.questions.map { it.cell.reference })
        assertEquals(WoodlandLandmarks.all.map { it.id },
            mission.questions.map { it.answerLandmarkId })
        assertTrue(mission.questions.all { mission.validateQuestion(it) })
        assertEquals(4, mission.choices.map { it.cell }.toSet().size)
    }

    @Test fun incorrectObjectAndMissingSelectionDoNotAdvance() {
        assertEquals(0, mission.accept(0, null))
        assertEquals(0, mission.accept(0, "ghost-feature"))
        assertEquals(0, mission.accept(0, WoodlandLandmarks.flowers.id))
        assertFalse(mission.correct(0, WoodlandLandmarks.waystone.id))
        assertFalse(mission.finished(0))
    }

    @Test fun correctChoicesAdvanceOneQuestionAtATimeAndComplete() {
        var step = 0
        for ((index, question) in mission.questions.withIndex()) {
            assertEquals(index, step)
            assertEquals(question, mission.questionAt(step))
            assertTrue(mission.correct(step, question.answerLandmarkId))
            step = mission.accept(step, question.answerLandmarkId)
            assertEquals(index + 1, step)
        }
        assertTrue(mission.finished(step))
        assertNull(mission.questionAt(step))
        assertEquals(step, mission.accept(step, WoodlandLandmarks.mushrooms.id))
    }

    @Test fun restoredProgressIsClampedAndCompletionRemainsUntilExplicitReplay() {
        assertEquals(0, mission.restoreStep(-10))
        assertEquals(4, mission.restoreStep(888))
        assertEquals(2, mission.restoreStep(2))
        assertTrue(mission.finished(999))
        assertEquals(4, mission.accept(999, "mushroom-nook"))
        assertEquals(0, mission.restoreStep(0))
    }

    @Test fun malformedQuestionsFailValidationInsteadOfPublishing() {
        val first = mission.questions.first()
        assertFalse(mission.validateQuestion(first.copy(cell = GridReference.parse("2440"))))
        assertFalse(mission.validateQuestion(first.copy(answerLandmarkId = "missing")))
        assertFalse(mission.validateQuestion(first.copy(cell = GridReference.parse("257389"))))
    }
}
