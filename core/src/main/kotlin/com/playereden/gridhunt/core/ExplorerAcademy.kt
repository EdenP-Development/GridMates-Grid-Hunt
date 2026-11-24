package com.playereden.gridhunt.core

/** First playable Academy slice. Only published exercises count toward progress. */
data class AcademyExercise(
    val id: String, val lesson: String, val teaching: String,
    val prompt: String, val choices: List<String>, val answer: String,
    val explanation: String,
    val mapTask: AcademyMapTask? = null
) {
    init {
        require(id.isNotBlank() && choices.size == 4 && choices.distinct().size == 4)
        require(answer in choices)
        if (mapTask != null) require(answer == mapTask.target && AcademyTrainingGrid.contains(answer))
    }
}

data class AcademyMapTask(val target: String, val guided: Boolean, val selectSquare: Boolean)

/** Small north-up training map. Labels identify west and south boundaries. */
object AcademyTrainingGrid {
    val eastings = 24..27
    val northings = 37..40
    fun reference(east: Int, north: Int): String {
        require(east in eastings && north in northings)
        return GridCell(east, north).reference
    }
    fun contains(code: String): Boolean =
        eastings.any { e -> northings.any { n -> reference(e, n) == code } }
}

object ExplorerAcademy {
    const val schoolId = "exploration-v1"
    const val saveName = "gridhunt_academy_exploration_v1"
    val exercises = listOf(
        AcademyExercise("north", "Finding your bearings",
            "On our north-up maps, north is up and east is right.",
            "Which direction is toward the right edge of a north-up map?",
            listOf("North", "West", "East", "South"), "East",
            "East is right on a north-up map; west is left."),
        AcademyExercise("south", "Finding your bearings",
            "The compass shows the four main directions. North and south are opposites.",
            "Which direction is opposite north?",
            listOf("South", "East", "West", "North"), "South",
            "South is down on a north-up map."),
        AcademyExercise("grid-order", "Cracking the grid",
            "A four-digit grid address names a square. Read its left boundary, then its bottom boundary.",
            "Read the highlighted campsite square. What is its address?",
            listOf("3926", "2639", "2739", "2640"), "2639",
            "Across first, then up: 26 followed by 39.",
            AcademyMapTask("2639", guided = true, selectSquare = false)),
        AcademyExercise("grid-practice", "Cracking the grid",
            "Always read the west and south boundaries, not the far edges.",
            "Tap square 2437 on the training map, then check your selection.",
            listOf("2438", "3724", "2537", "2437"), "2437",
            "24 across and 37 up gives 2437.",
            AcademyMapTask("2437", guided = false, selectSquare = true)),
        AcademyExercise("grid-field", "Field practice",
            "Find the left boundary first, then the bottom boundary. This time, try without highlighted lines.",
            "Tap square 2740, then check your selection.",
            listOf("2740", "2640", "2739", "2639"), "2740",
            "The square begins at 27 across and 40 up.",
            AcademyMapTask("2740", guided = false, selectSquare = true))
    )
    fun restore(ids: Set<String>): Set<String> =
        ids.intersect(exercises.map { it.id }.toSet())
    fun current(ids: Set<String>): AcademyExercise? =
        exercises.firstOrNull { it.id !in restore(ids) }
    fun accept(ids: Set<String>, exerciseId: String, choice: String): Set<String> {
        val known = restore(ids)
        val active = current(known) ?: return known
        return if (active.id == exerciseId && active.answer == choice)
            known + active.id else known
    }
    fun practiceComplete(ids: Set<String>) = restore(ids).size == exercises.size
    // Graduation deliberately unavailable until the full foundational school
    // and independent field expedition are authored and verified.
}
