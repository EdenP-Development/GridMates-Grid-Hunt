package com.playereden.gridhunt.core

/**
 * GH-V12: a read-the-grid-then-identify-the-picture puzzle.
 *
 * The four introductory questions are backed by the same fixed point/area data
 * as Treasure Checklist. A four-digit cell is an AREA; this mode is published
 * only when exactly one landmark occupies each question cell.
 */
data class WoodlandIdentifyQuestion(
    val id: String,
    val cell: GridCell,
    val answerLandmarkId: String
)

object WoodlandIdentifyMission {
    const val missionId = "little-woodland-identify-v1"
    const val saveName = "gridhunt_identify_v1"
    val choices: List<WoodlandLandmark> = WoodlandLandmarks.all
    val questions: List<WoodlandIdentifyQuestion> = choices.map { landmark ->
        WoodlandIdentifyQuestion(
            id = "identify-${landmark.id}",
            cell = landmark.cell,
            answerLandmarkId = landmark.id
        )
    }

    init {
        require(questions.isNotEmpty())
        require(questions.map { it.id }.distinct().size == questions.size)
        require(questions.map { it.answerLandmarkId }.distinct().size == questions.size)
        require(questions.map { it.cell }.distinct().size == questions.size) {
            "A 4-digit identification question must not contain multiple answers"
        }
        require(questions.all { question ->
            choices.count { it.cell == question.cell } == 1 &&
                WoodlandLandmarks.byId(question.answerLandmarkId)?.cell == question.cell
        })
    }

    fun restoreStep(rawStep: Int) = rawStep.coerceIn(0, questions.size)

    fun questionAt(rawStep: Int): WoodlandIdentifyQuestion? =
        questions.getOrNull(restoreStep(rawStep))

    /** Unknown IDs and wrong options never advance, even on repeated taps. */
    fun correct(rawStep: Int, choiceId: String?): Boolean {
        val question = questionAt(rawStep) ?: return false
        return choices.any { it.id == choiceId } &&
            question.answerLandmarkId == choiceId
    }

    fun accept(rawStep: Int, choiceId: String?): Int {
        val step = restoreStep(rawStep)
        return if (correct(step, choiceId)) step + 1 else step
    }

    fun finished(rawStep: Int) = restoreStep(rawStep) == questions.size

    /** True only for the four curated, unambiguous source questions. */
    fun validateQuestion(question: WoodlandIdentifyQuestion): Boolean {
        val target = WoodlandLandmarks.byId(question.answerLandmarkId) ?: return false
        return question.cell.digits == 4 &&
            target.cell == question.cell &&
            choices.count { it.cell == question.cell } == 1
    }
}
