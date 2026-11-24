package com.playereden.gridhunt.core

/** First playable Academy slice. Only published exercises count toward progress. */
data class AcademyExercise(
    val id: String, val lesson: String, val teaching: String,
    val prompt: String, val choices: List<String>, val answer: String,
    val explanation: String
) {
    init {
        require(id.isNotBlank() && choices.size == 4 && choices.distinct().size == 4)
        require(answer in choices)
    }
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
            "A square has left boundary 26 and bottom boundary 39. What is its address?",
            listOf("3926", "2639", "2739", "2640"), "2639",
            "Across first, then up: 26 followed by 39."),
        AcademyExercise("grid-practice", "Cracking the grid",
            "Always read the west and south boundaries, not the far edges.",
            "The left boundary is 24 and the bottom boundary is 37. Enter the square's address.",
            listOf("2438", "3724", "2537", "2437"), "2437",
            "24 across and 37 up gives 2437.")
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
