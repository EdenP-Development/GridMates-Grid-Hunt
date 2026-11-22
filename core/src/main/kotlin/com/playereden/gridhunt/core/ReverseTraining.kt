package com.playereden.gridhunt.core

/** Authored point markers: the player reads their containing FOUR-digit 1km reference. */
data class SurveyMarker(val id: String, val title: String, val point: WorldPoint) {
    val cell: GridCell get() = GridReference.fromWorld(point, 4)
}

object ReverseTraining {
    val markers = listOf(
        SurveyMarker("A", "Survey marker A", WorldPoint(24650.0, 40620.0)),
        SurveyMarker("B", "Survey marker B", WorldPoint(27620.0, 42130.0)),
        SurveyMarker("C", "Survey marker C", WorldPoint(28680.0, 38250.0)),
        SurveyMarker("D", "Survey marker D", WorldPoint(25870.0, 41220.0)),
        SurveyMarker("E", "Survey marker E", WorldPoint(29660.0, 42840.0))
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
