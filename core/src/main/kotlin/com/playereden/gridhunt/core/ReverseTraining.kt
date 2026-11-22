package com.playereden.gridhunt.core

/** Authored point markers: the player reads their containing FOUR-digit 1km reference. */
data class SurveyMarker(val id: String, val title: String, val point: WorldPoint) {
    val cell: GridCell get() = GridReference.fromWorld(point, 4)
}

object ReverseTraining {
    val markers = listOf(
        SurveyMarker("A", "Cabin survey point", WorldPoint(24375.0, 37860.0)),
        SurveyMarker("B", "Bridge survey point", WorldPoint(25750.0, 39250.0)),
        SurveyMarker("C", "Lookout survey point", WorldPoint(27120.0, 41330.0)),
        SurveyMarker("D", "Camp survey point", WorldPoint(28350.0, 38210.0)),
        SurveyMarker("E", "Spring survey point", WorldPoint(29520.0, 40380.0))
    )
    init {
        require(markers.map { it.id }.distinct().size == markers.size)
        require(markers.map { it.cell }.distinct().size == markers.size)
        require(markers.all { WoodlandTrainingMap.bounds.contains(it.point) })
    }

    /** Deliberately keep the answer as a four-digit STRING (never parse into Int). */
    fun check(marker: SurveyMarker, answer: String): Boolean {
        if (answer.length != 4 || answer.any { it !in '0'..'9' }) return false
        return marker.cell == GridReference.parse(answer)
    }

    fun feedback(marker: SurveyMarker, answer: String): String {
        if (answer.length != 4 || answer.any { it !in '0'..'9' })
            return "Enter four digits: easting first, then northing."
        val attempted = GridReference.parse(answer)
        return if (check(marker, answer)) "Correct grid reference."
        else HuntRules.errorText(marker.cell, attempted)
    }
}
