package com.playereden.gridhunt.core

/** Map-scoped authored Golden Spots. Separate from generated Custom gold targets. */
class MapSurveyMarkers(val map: ExplorationMap, val markers: List<SurveyMarker>) {
    init {
        require(markers.isNotEmpty())
        require(markers.all { it.id.isNotBlank() && it.title.isNotBlank() && map.bounds.contains(it.point) })
        require(markers.map { it.id }.distinct().size == markers.size)
        require(markers.map { it.cell }.distinct().size == markers.size)
        require(markers.all { it.cell in map.cells })
    }
    fun find(id: String): SurveyMarker? = markers.firstOrNull { it.id == id }
    fun check(id: String, answer: String): Boolean =
        find(id)?.let { ReverseTraining.check(it, answer) } ?: false
}

/** Draft only: not a published map or a live save namespace. */
object SurveyMarkerCatalog {
    val woodland by lazy { MapSurveyMarkers(ExplorationMaps.woodland, ReverseTraining.markers) }
    val coastalDraft by lazy {
        MapSurveyMarkers(WhisperingCoastDraft.map, listOf(
            SurveyMarker("A", "Golden spot A", WorldPoint(42640.0, 56580.0)),
            SurveyMarker("B", "Golden spot B", WorldPoint(43780.0, 54820.0)),
            SurveyMarker("C", "Golden spot C", WorldPoint(44620.0, 53860.0)),
            SurveyMarker("D", "Golden spot D", WorldPoint(42920.0, 52160.0)),
            SurveyMarker("E", "Golden spot E", WorldPoint(45540.0, 56280.0))
        ))
    }
    /** Exact map definition, not merely an ID. Unknown revisions fail closed. */
    fun forAuthoredMap(map: ExplorationMap): MapSurveyMarkers? = when (map) {
        ExplorationMaps.woodland -> woodland
        WhisperingCoastDraft.map -> coastalDraft
        else -> null
    }
}
