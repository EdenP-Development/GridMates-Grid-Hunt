package com.playereden.gridhunt.core

/** Stable authored map identity and revision, independent of rendering assets. */
data class ExplorationMap(
    val id: String,
    val revision: Int,
    val name: String,
    val bounds: WorldBounds,
    val landmarks: List<WoodlandLandmark>
) {
    init {
        require(id.isNotBlank() && name.isNotBlank() && revision > 0)
        require(listOf(bounds.west, bounds.south, bounds.east, bounds.north).all {
            it.isFinite() && it in 0.0..100000.0 && it % 1000.0 == 0.0
        }) { "Introductory maps require whole kilometer grid boundaries" }
        require(landmarks.map { it.id }.distinct().size == landmarks.size)
        require(landmarks.all { bounds.contains(it.point) })
        require(landmarks.map { it.cell }.distinct().size == landmarks.size) {
            "Four-digit landmark identification requires unambiguous cells"
        }
    }

    val cells: List<GridCell> = (bounds.west.toInt() / 1000 until bounds.east.toInt() / 1000)
        .flatMap { east ->
            (bounds.south.toInt() / 1000 until bounds.north.toInt() / 1000)
                .map { north -> GridCell(east, north) }
        }
    private val byReference = cells.associateBy { it.reference }
    private val byLandmarkId = landmarks.associateBy { it.id }

    fun cell(reference: String): GridCell? = byReference[reference]
    fun landmark(id: String): WoodlandLandmark? = byLandmarkId[id]
}

/** Publish only complete authored map definitions, never placeholder worlds. */
object ExplorationMaps {
    val woodland = ExplorationMap(
        WoodlandTrainingMap.mapId, WoodlandTrainingMap.revision,
        "Little Woodland", WoodlandTrainingMap.bounds, WoodlandLandmarks.all
    )
    val all: List<ExplorationMap> = listOf(woodland)
    fun find(id: String, revision: Int): ExplorationMap? =
        all.firstOrNull { it.id == id && it.revision == revision }
}
