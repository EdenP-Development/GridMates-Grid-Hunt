package com.playereden.gridhunt.core

import kotlin.math.hypot

/**
 * GH-V11: a small, typed, fail-closed publishing gate for authored object clues.
 *
 * This layer decides whether a clue names exactly ONE real feature and whether a
 * FOUR-digit grid answer can distinguish it. It does not render art, infer
 * relationships from a picture, generate puzzle prose, or award discoveries.
 */
enum class ClueGeometry { POINT, LINEAR }

data class ClueFeature(
    val id: String,
    val categoryId: String,
    val point: WorldPoint,
    val symbolKey: String,
    val regionId: String? = null,
    val authoredRelations: Set<FeatureRelation> = emptySet(),
    val geometry: ClueGeometry = ClueGeometry.POINT
) {
    init {
        require(id.isNotBlank() && categoryId.isNotBlank() && symbolKey.isNotBlank())
        require(point.easting.isFinite() && point.northing.isFinite())
        require(regionId == null || regionId.isNotBlank())
    }
}

enum class FeatureRelationKind { BESIDE }

/**
 * Authored, manually reviewed relationship. A BESIDE creek relation must be
 * supported by actual map geometry in the content-review step; a creek's single
 * anchor point is NOT sufficient to infer adjacency to a winding waterway.
 */
data class FeatureRelation(val kind: FeatureRelationKind, val otherFeatureId: String) {
    init { require(otherFeatureId.isNotBlank()) }
}

data class ClueMapPack(
    val id: String,
    val revision: Int,
    val bounds: WorldBounds,
    val features: List<ClueFeature>,
    val publishedSymbolKeys: Set<String>
) {
    init {
        require(id.isNotBlank() && revision > 0)
        require(features.map { it.id }.distinct().size == features.size) {
            "Feature IDs must be unique within a map revision"
        }
        require(features.all { bounds.contains(it.point) }) {
            "All authored points must lie inside the map"
        }
        require(publishedSymbolKeys.none { it.isBlank() })
    }
}

/**
 * Conditions are typed; we deliberately do not evaluate natural-language
 * guesses such as 'near the river' using device pixels or fake elevation data.
 */
sealed interface ClueCondition {
    data class InRegion(val regionId: String) : ClueCondition
    data class BesideFeature(val featureId: String) : ClueCondition
    data class EastOf(val featureId: String, val minMeters: Double = 0.0) : ClueCondition
    data class WestOf(val featureId: String, val minMeters: Double = 0.0) : ClueCondition
    data class NorthOf(val featureId: String, val minMeters: Double = 0.0) : ClueCondition
    data class NearPoint(val featureId: String, val maxMeters: Double) : ClueCondition

    /** Reserved for authored features not supported by this version (e.g. elevation). */
    data class Unsupported(val relationName: String) : ClueCondition
}

/** The authored target ID is checked; it is never guessed from the first match. */
data class ClueObjective(
    val id: String,
    val targetFeatureId: String,
    val categoryId: String,
    val conditions: List<ClueCondition>,
    val digits: Int = 4
)

enum class ClueRejection {
    INVALID_SPEC,
    MISSING_FEATURE,
    UNRECOGNIZABLE_SYMBOL,
    UNSUPPORTED_RELATION,
    NO_MATCH,
    AMBIGUOUS,
    TARGET_MISMATCH,
    SAME_GRID_SQUARE
}

sealed interface ClueVerdict {
    data class Publishable(
        val targetFeatureId: String,
        val answer: GridCell
    ) : ClueVerdict

    data class Rejected(
        val reason: ClueRejection,
        val candidateIds: List<String> = emptyList()
    ) : ClueVerdict
}

object ClueValidation {
    fun validate(map: ClueMapPack, clue: ClueObjective): ClueVerdict {
        fun reject(reason: ClueRejection, ids: List<String> = emptyList()) =
            ClueVerdict.Rejected(reason, ids)

        if (clue.id.isBlank() || clue.targetFeatureId.isBlank() ||
            clue.categoryId.isBlank() || clue.digits != 4) {
            return reject(ClueRejection.INVALID_SPEC)
        }

        val byId = map.features.associateBy { it.id }
        val intended = byId[clue.targetFeatureId]
            ?: return reject(ClueRejection.MISSING_FEATURE)
        if (intended.categoryId != clue.categoryId) {
            return reject(ClueRejection.TARGET_MISMATCH)
        }

        val eligible = map.features.filter { it.categoryId == clue.categoryId }
        // An unknown symbol could hide a real competing answer. Never silently
        // discard that candidate and call the clue uniquely solvable.
        if (eligible.any { it.symbolKey !in map.publishedSymbolKeys }) {
            return reject(ClueRejection.UNRECOGNIZABLE_SYMBOL)
        }

        for (condition in clue.conditions) {
            when (condition) {
                is ClueCondition.Unsupported ->
                    return reject(ClueRejection.UNSUPPORTED_RELATION)

                is ClueCondition.InRegion -> {
                    if (condition.regionId.isBlank()) {
                        return reject(ClueRejection.INVALID_SPEC)
                    }
                    if (eligible.any { it.regionId == null }) {
                        return reject(ClueRejection.UNSUPPORTED_RELATION)
                    }
                }

                is ClueCondition.BesideFeature,
                is ClueCondition.EastOf,
                is ClueCondition.WestOf,
                is ClueCondition.NorthOf,
                is ClueCondition.NearPoint -> {
                    val featureId = when (condition) {
                        is ClueCondition.BesideFeature -> condition.featureId
                        is ClueCondition.WestOf -> condition.featureId
                        is ClueCondition.EastOf -> condition.featureId
                        is ClueCondition.NorthOf -> condition.featureId
                        is ClueCondition.NearPoint -> condition.featureId
                        else -> error("unreachable condition")
                    }
                    if (featureId.isBlank()) return reject(ClueRejection.INVALID_SPEC)
                    val anchor = byId[featureId]
                        ?: return reject(ClueRejection.MISSING_FEATURE)
                    if (anchor.symbolKey !in map.publishedSymbolKeys) {
                        return reject(ClueRejection.UNRECOGNIZABLE_SYMBOL)
                    }
                    // A creek/road polyline cannot be represented by distance
                    // or direction from its arbitrary label/anchor POINT.
                    // BesideFeature uses an independently authored relationship.
                    if (condition !is ClueCondition.BesideFeature &&
                        anchor.geometry != ClueGeometry.POINT) {
                        return reject(ClueRejection.UNSUPPORTED_RELATION)
                    }
                    val distance = when (condition) {
                        is ClueCondition.WestOf -> condition.minMeters
                        is ClueCondition.EastOf -> condition.minMeters
                        is ClueCondition.NorthOf -> condition.minMeters
                        is ClueCondition.NearPoint -> condition.maxMeters
                        else -> 0.0
                    }
                    if (!distance.isFinite() || distance < 0.0) {
                        return reject(ClueRejection.INVALID_SPEC)
                    }
                }
            }
        }

        val matches = eligible.filter { candidate ->
            clue.conditions.all { condition ->
                when (condition) {
                    is ClueCondition.InRegion ->
                        candidate.regionId == condition.regionId

                    is ClueCondition.BesideFeature ->
                        candidate.id != condition.featureId &&
                            FeatureRelation(
                                FeatureRelationKind.BESIDE, condition.featureId
                            ) in candidate.authoredRelations

                    is ClueCondition.EastOf -> {
                        val reference = byId.getValue(condition.featureId)
                        candidate.id != reference.id &&
                            candidate.point.easting >
                            reference.point.easting + condition.minMeters
                    }

                    is ClueCondition.WestOf -> {
                        val reference = byId.getValue(condition.featureId)
                        candidate.id != reference.id && candidate.point.easting < reference.point.easting - condition.minMeters
                    }

                    is ClueCondition.NorthOf -> {
                        val reference = byId.getValue(condition.featureId)
                        candidate.id != reference.id &&
                            candidate.point.northing >
                            reference.point.northing + condition.minMeters
                    }

                    is ClueCondition.NearPoint -> {
                        val reference = byId.getValue(condition.featureId)
                        candidate.id != reference.id &&
                            hypot(
                                candidate.point.easting - reference.point.easting,
                                candidate.point.northing - reference.point.northing
                            ) <= condition.maxMeters
                    }

                    is ClueCondition.Unsupported -> false
                }
            }
        }

        if (matches.isEmpty()) return reject(ClueRejection.NO_MATCH)
        if (matches.size > 1) {
            return reject(ClueRejection.AMBIGUOUS, matches.map { it.id })
        }
        val resolved = matches.single()
        if (resolved.id != intended.id) {
            return reject(ClueRejection.TARGET_MISMATCH, listOf(resolved.id))
        }

        val grid = GridReference.fromWorld(resolved.point, clue.digits)
        // Inputting the same 4 digits cannot establish WHICH of two same-type
        // objects was recognized, even if only one satisfies the prose clue.
        val sameSquarePeers = eligible.filter {
            it.id != resolved.id &&
                GridReference.fromWorld(it.point, clue.digits) == grid
        }
        if (sameSquarePeers.isNotEmpty()) {
            return reject(
                ClueRejection.SAME_GRID_SQUARE,
                listOf(resolved.id) + sameSquarePeers.map { it.id }
            )
        }
        return ClueVerdict.Publishable(resolved.id, grid)
    }
}

/**
 * Read-only adapter: existing illustrated object positions remain canonical.
 * Each existing object has a distinct type; NO road/water/region relations are
 * fabricated for Little Woodland merely because lines or scenery are drawn.
 */
object WoodlandClueMap {
    val pack: ClueMapPack = ClueMapPack(
        id = WoodlandLandmarks.mapId,
        revision = WoodlandLandmarks.mapRevision,
        bounds = WoodlandTrainingMap.bounds,
        features = WoodlandLandmarks.all.map { landmark ->
            ClueFeature(
                id = landmark.id,
                categoryId = when (landmark.id) {
                    WoodlandLandmarks.mushrooms.id -> "mushrooms"
                    WoodlandLandmarks.flowers.id -> "flowers"
                    WoodlandLandmarks.acorn.id -> "acorn"
                    WoodlandLandmarks.waystone.id -> "waystone"
                    else -> error("Unknown landmark category")
                },
                point = landmark.point,
                symbolKey = landmark.id
            )
        },
        publishedSymbolKeys = WoodlandLandmarks.all.mapTo(linkedSetOf()) { it.id }
    )
}

/** All four visible landmarks are candidates for spatial deduction, not just a named type. */
object WoodlandSpatialClues {
    val pack = WoodlandClueMap.pack.copy(
        features = WoodlandClueMap.pack.features.map { it.copy(categoryId = "landmark") }
    )

    fun resolve(id: String, targetId: String, conditions: List<ClueCondition>): GridCell {
        require(conditions.isNotEmpty()) { "Spatial clue needs a condition" }
        val verdict = ClueValidation.validate(pack,
            ClueObjective(id, targetId, "landmark", conditions))
        require(verdict is ClueVerdict.Publishable) { "Unfair spatial clue: $id ($verdict)" }
        return verdict.answer
    }
}
