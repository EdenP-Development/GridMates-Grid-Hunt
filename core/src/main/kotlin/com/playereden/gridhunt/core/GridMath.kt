package com.playereden.gridhunt.core

import kotlin.math.floor
import kotlin.math.min

/** The single fictional [0,100000) meter training square: not a full MGRS address. */
data class WorldPoint(val easting: Double, val northing: Double)
data class PixelPoint(val x: Double, val y: Double)

/** West/south included, east/north excluded, in canonical world meters. */
data class WorldBounds(
    val west: Double,
    val south: Double,
    val east: Double,
    val north: Double
) {
    init { require(west < east && south < north) }
    val center get() = WorldPoint((west + east) / 2, (south + north) / 2)
    fun contains(point: WorldPoint) =
        point.easting >= west && point.easting < east &&
        point.northing >= south && point.northing < north
}

data class GridCell(val eastIndex: Int, val northIndex: Int, val digits: Int = 4) {
    init {
        require(digits in setOf(4, 6, 8, 10))
        val count = GridReference.divisions(digits)
        require(eastIndex in 0 until count && northIndex in 0 until count)
    }
    val sizeMeters: Int get() = 100000 / GridReference.divisions(digits)
    val westMeters: Int get() = eastIndex * sizeMeters
    val southMeters: Int get() = northIndex * sizeMeters
    val reference: String get() = eastIndex.toString().padStart(digits / 2, '0') +
        northIndex.toString().padStart(digits / 2, '0')
    fun contains(point: WorldPoint): Boolean =
        point.easting >= westMeters && point.easting < westMeters + sizeMeters &&
        point.northing >= southMeters && point.northing < southMeters + sizeMeters
}

object GridReference {
    fun divisions(digits: Int): Int = when (digits) {
        4 -> 100
        6 -> 1000
        8 -> 10000
        10 -> 100000
        else -> throw IllegalArgumentException("Unsupported grid reference precision")
    }

    /** Floor/truncate; never round to a neighbouring cell. */
    fun fromWorld(point: WorldPoint, digits: Int = 4): GridCell {
        val count = divisions(digits)
        require(point.easting.isFinite() && point.northing.isFinite())
        require(point.easting >= 0.0 && point.easting < 100000.0)
        require(point.northing >= 0.0 && point.northing < 100000.0)
        val metersPerCell = 100000.0 / count
        return GridCell(
            floor(point.easting / metersPerCell).toInt(),
            floor(point.northing / metersPerCell).toInt(),
            digits
        )
    }

    fun parse(text: String): GridCell {
        require(text.length in setOf(4, 6, 8, 10)) { "Grid reference needs 4, 6, 8 or 10 digits" }
        require(text.all { it in '0'..'9' }) { "Grid reference must contain only digits" }
        val half = text.length / 2
        return GridCell(text.substring(0, half).toInt(), text.substring(half).toInt(), text.length)
    }

    fun format(point: WorldPoint, digits: Int = 4) = fromWorld(point, digits).reference
}

/** One authored six-by-six-kilometre map, not a simulated outdoor location. */
object WoodlandTrainingMap {
    const val mapId = "woodland-training-01"
    const val revision = 1
    val bounds = WorldBounds(24000.0, 37000.0, 30000.0, 43000.0)
    val objectives: List<GridCell> = listOf("2437", "2539", "2741").map(GridReference::parse)
    init {
        require(objectives.distinct().size == objectives.size)
        require(objectives.all { bounds.contains(WorldPoint(it.westMeters.toDouble(), it.southMeters.toDouble())) })
    }
}

object HuntRules {
    fun isCorrect(expected: GridCell, attempted: GridCell?): Boolean = attempted == expected

    fun errorText(expected: GridCell, attempted: GridCell): String {
        require(expected.digits == attempted.digits)
        return when {
            expected.eastIndex == attempted.eastIndex ->
                "Right easting. Check the northing going up the map."
            expected.northIndex == attempted.northIndex ->
                "Right northing. Check the easting along the bottom."
            else -> "Not quite. Read across to the easting, then up to the northing."
        }
    }
}

/** Exact world↔screen mapping. Screen Y grows down; world northing grows up. */
data class MapCamera(
    val center: WorldPoint,
    val pixelsPerMeter: Double
) {
    init { require(pixelsPerMeter.isFinite() && pixelsPerMeter > 0.0) }

    fun worldToScreen(world: WorldPoint, width: Double, height: Double) = PixelPoint(
        (world.easting - center.easting) * pixelsPerMeter + width / 2.0,
        (center.northing - world.northing) * pixelsPerMeter + height / 2.0
    )

    fun screenToWorld(pixel: PixelPoint, width: Double, height: Double) = WorldPoint(
        center.easting + (pixel.x - width / 2.0) / pixelsPerMeter,
        center.northing - (pixel.y - height / 2.0) / pixelsPerMeter
    )

    /** Positive screen pan moves the map toward the pointer. */
    fun transform(
        bounds: WorldBounds, width: Double, height: Double,
        centroid: PixelPoint, pan: PixelPoint, zoom: Double
    ): MapCamera {
        val fitScale = fit(bounds, width, height).pixelsPerMeter
        val newScale = (pixelsPerMeter * zoom).coerceIn(fitScale, fitScale * 5.0)
        val anchor = screenToWorld(centroid, width, height)
        val east = anchor.easting - (centroid.x + pan.x - width / 2.0) / newScale
        val north = anchor.northing + (centroid.y + pan.y - height / 2.0) / newScale
        val eVisible = width / newScale
        val nVisible = height / newScale
        val constrainedE = when {
            eVisible >= bounds.east - bounds.west -> bounds.center.easting
            else -> east.coerceIn(bounds.west + eVisible / 2.0, bounds.east - eVisible / 2.0)
        }
        val constrainedN = when {
            nVisible >= bounds.north - bounds.south -> bounds.center.northing
            else -> north.coerceIn(bounds.south + nVisible / 2.0, bounds.north - nVisible / 2.0)
        }
        return MapCamera(WorldPoint(constrainedE, constrainedN), newScale)
    }

    companion object {
        fun fit(bounds: WorldBounds, width: Double, height: Double, padding: Double = 34.0): MapCamera {
            require(width > padding * 2 && height > padding * 2)
            val scale = min(
                (width - 2.0 * padding) / (bounds.east - bounds.west),
                (height - 2.0 * padding) / (bounds.north - bounds.south)
            )
            return MapCamera(bounds.center, scale)
        }
    }
}
