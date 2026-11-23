package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

/** Three fictional churches are DATA FIXTURES, not published woodland art. */
class ClueValidationTest {
    private val bounds = WoodlandTrainingMap.bounds
    private val besideCreek = FeatureRelation(FeatureRelationKind.BESIDE, "east-creek")

    private fun fixture(
        secondRiversideChurchInSameSquare: Boolean = false,
        recognizeChurch: Boolean = true,
        includeRegions: Boolean = true
    ): ClueMapPack {
        val features = mutableListOf(
            ClueFeature(
                "church-riverside", "church", WorldPoint(24550.0, 40400.0),
                "church", if (includeRegions) "riverbank" else null,
                setOf(besideCreek)
            ),
            ClueFeature(
                "church-hilltop", "church", WorldPoint(26800.0, 41600.0),
                "church", if (includeRegions) "high-meadow" else null
            ),
            ClueFeature(
                "church-village", "church", WorldPoint(28700.0, 39000.0),
                "church", if (includeRegions) "east-village" else null
            ),
            // Creek is a map feature whose BESIDE relationship is EXPLICITLY
            // authored. NearPoint is never used to approximate its polyline.
            ClueFeature("east-creek", "waterway", WorldPoint(24900.0, 40900.0), "creek"),
            ClueFeature("footbridge", "bridge", WorldPoint(26300.0, 40500.0), "bridge"),
            ClueFeature("school", "school", WorldPoint(27000.0, 39900.0), "school"),
            ClueFeature("orchard", "orchard", WorldPoint(28800.0, 39250.0), "orchard")
        )
        if (secondRiversideChurchInSameSquare) {
            features.add(
                ClueFeature(
                    "church-riverside-2", "church", WorldPoint(24790.0, 40600.0),
                    "church", if (includeRegions) "riverbank" else null
                )
            )
        }
        val symbols = setOf("creek", "bridge", "school", "orchard") +
            if (recognizeChurch) setOf("church") else emptySet()
        return ClueMapPack("fictional-three-churches", 1, bounds, features, symbols)
    }

    private fun objective(
        target: String = "church-riverside",
        conditions: List<ClueCondition> = emptyList(),
        category: String = "church"
    ) = ClueObjective("test-clue-01", target, category, conditions)

    private fun rejected(
        map: ClueMapPack,
        clue: ClueObjective,
        reason: ClueRejection
    ): ClueVerdict.Rejected {
        val verdict = ClueValidation.validate(map, clue)
        assertTrue("Expected rejection $reason, got $verdict", verdict is ClueVerdict.Rejected)
        val result = verdict as ClueVerdict.Rejected
        assertEquals(reason, result.reason)
        return result
    }

    private fun accepted(
        map: ClueMapPack,
        clue: ClueObjective,
        id: String,
        grid: String
    ) {
        val verdict = ClueValidation.validate(map, clue)
        assertTrue("Expected publishable $id, got $verdict",
            verdict is ClueVerdict.Publishable)
        val result = verdict as ClueVerdict.Publishable
        assertEquals(id, result.targetFeatureId)
        assertEquals(grid, result.answer.reference)
        assertEquals(4, result.answer.digits)
    }

    @Test fun plainChurchClueIsAmbiguousWhenThreeChurchesExist() {
        val result = rejected(fixture(), objective(), ClueRejection.AMBIGUOUS)
        assertEquals(
            setOf("church-riverside", "church-hilltop", "church-village"),
            result.candidateIds.toSet()
        )
    }

    @Test fun explicitlyAuthoredCreekAdjacencyFindsOnlyOneChurch() {
        accepted(fixture(),
            objective(conditions = listOf(ClueCondition.BesideFeature("east-creek"))),
            "church-riverside", "2440")
        // The relation is never inferred just because a creek point is nearby.
        val withoutRelation = fixture().copy(
            features = fixture().features.map {
                if (it.id == "church-riverside") it.copy(authoredRelations = emptySet())
                else it
            }
        )
        rejected(withoutRelation,
            objective(conditions = listOf(ClueCondition.BesideFeature("east-creek"))),
            ClueRejection.NO_MATCH)
    }

    @Test fun directionalCluesCombineToPickOneFeature() {
        accepted(
            fixture(),
            objective("church-hilltop", listOf(
                ClueCondition.EastOf("footbridge"),
                ClueCondition.NorthOf("school")
            )),
            "church-hilltop", "2641"
        )
        rejected(
            fixture(),
            objective("church-hilltop", listOf(ClueCondition.EastOf("footbridge"))),
            ClueRejection.AMBIGUOUS
        )
    }

    @Test fun regionsRequireActualMetadataForEverySameTypeCandidate() {
        accepted(fixture(),
            objective("church-village", listOf(ClueCondition.InRegion("east-village"))),
            "church-village", "2839")
        rejected(
            fixture(includeRegions = false),
            objective(conditions = listOf(ClueCondition.InRegion("riverbank"))),
            ClueRejection.UNSUPPORTED_RELATION
        )
    }

    @Test fun nearnessUsesWorldMetersNotScreenPixelsAndOnlyPointAnchors() {
        accepted(fixture(),
            objective("church-village", listOf(ClueCondition.NearPoint("orchard", 300.0))),
            "church-village", "2839")
        rejected(fixture(),
            objective("church-village", listOf(ClueCondition.NearPoint("orchard", 100.0))),
            ClueRejection.NO_MATCH)
        rejected(fixture(),
            objective(conditions = listOf(ClueCondition.NearPoint("orchard", -4.0))),
            ClueRejection.INVALID_SPEC)
        rejected(fixture(),
            objective(conditions = listOf(ClueCondition.NearPoint("orchard", Double.NaN))),
            ClueRejection.INVALID_SPEC)
    }

    @Test fun wrongTargetIdCannotSneakThroughAnOtherwiseUniqueClue() {
        rejected(
            fixture(),
            objective("church-hilltop", listOf(ClueCondition.BesideFeature("east-creek"))),
            ClueRejection.TARGET_MISMATCH
        )
        rejected(fixture(), objective(target = "ghost-church"), ClueRejection.MISSING_FEATURE)
        rejected(
            fixture(),
            objective(target = "footbridge", category = "church"),
            ClueRejection.TARGET_MISMATCH
        )
    }

    @Test fun missingAnchorOrUnknownMapSymbolRejectsRatherThanGuessing() {
        rejected(
            fixture(),
            objective(conditions = listOf(ClueCondition.BesideFeature("deleted-creek"))),
            ClueRejection.MISSING_FEATURE
        )
        rejected(
            fixture(recognizeChurch = false),
            objective(conditions = listOf(ClueCondition.BesideFeature("east-creek"))),
            ClueRejection.UNRECOGNIZABLE_SYMBOL
        )
        val noCreekSymbol = fixture().copy(
            publishedSymbolKeys = fixture().publishedSymbolKeys - "creek"
        )
        rejected(
            noCreekSymbol,
            objective(conditions = listOf(ClueCondition.BesideFeature("east-creek"))),
            ClueRejection.UNRECOGNIZABLE_SYMBOL
        )
    }

    @Test fun multipleInstancesInSameFourDigitSquareAreNotDistinguishable() {
        val result = rejected(
            fixture(secondRiversideChurchInSameSquare = true),
            objective(conditions = listOf(ClueCondition.BesideFeature("east-creek"))),
            ClueRejection.SAME_GRID_SQUARE
        )
        assertEquals(
            setOf("church-riverside", "church-riverside-2"),
            result.candidateIds.toSet()
        )
    }

    @Test fun unsupportedElevationAndMalformedConditionsFailClosed() {
        rejected(
            fixture(),
            objective(conditions = listOf(ClueCondition.Unsupported("highest-elevation"))),
            ClueRejection.UNSUPPORTED_RELATION
        )
        rejected(
            fixture(),
            objective(conditions = listOf(ClueCondition.InRegion(""))),
            ClueRejection.INVALID_SPEC
        )
        rejected(
            fixture(),
            objective(conditions = listOf(ClueCondition.EastOf("footbridge", Double.POSITIVE_INFINITY))),
            ClueRejection.INVALID_SPEC
        )
        rejected(
            fixture(),
            objective(conditions = listOf(ClueCondition.NorthOf("school", -1.0))),
            ClueRejection.INVALID_SPEC
        )
        rejected(
            fixture(),
            objective(conditions = listOf(ClueCondition.BesideFeature(""))),
            ClueRejection.INVALID_SPEC
        )
        rejected(
            fixture(),
            objective().copy(digits = 6),
            ClueRejection.INVALID_SPEC
        )
    }

    @Test fun sameLandmarkCatalogFeedsOldAndNewGameSystemsWithoutRewritingCoords() {
        val map = WoodlandClueMap.pack
        assertEquals(WoodlandLandmarks.mapId, map.id)
        assertEquals(WoodlandLandmarks.mapRevision, map.revision)
        assertEquals(WoodlandLandmarks.all.map { it.id }, map.features.map { it.id })
        for (landmark in WoodlandLandmarks.all) {
            val clueCategory = map.features.single { it.id == landmark.id }.categoryId
            accepted(map, objective(landmark.id, category = clueCategory),
                landmark.id, landmark.cell.reference)
        }
    }

    @Test fun badMapDataIsRejectedBeforeAnyClueCanBePublished() {
        val map = fixture()
        assertThrows(IllegalArgumentException::class.java) {
            map.copy(features = map.features + map.features.first())
        }
        assertThrows(IllegalArgumentException::class.java) {
            map.copy(features = map.features + ClueFeature(
                "offmap", "church", WorldPoint(99999.0, 99999.0), "church"
            ))
        }
    }
}
