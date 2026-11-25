package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class ExplorationMapTest {
    @Test fun woodlandDefinitionPreservesExistingCoordinatesAndIdentity() {
        val map = ExplorationMaps.woodland
        assertEquals(WoodlandTrainingMap.mapId, map.id)
        assertEquals(WoodlandTrainingMap.revision, map.revision)
        assertEquals(WoodlandTrainingMap.bounds, map.bounds)
        assertEquals(WoodlandLandmarks.all, map.landmarks)
        assertEquals((24..29).flatMap { e -> (37..42).map { n -> GridCell(e, n) } }, map.cells)
        assertEquals(map, ExplorationMaps.find(map.id, map.revision))
        assertNull(ExplorationMaps.find(map.id, map.revision + 1))
        assertNull(map.cell("3042"))
        assertEquals(WoodlandLandmarks.mushrooms, map.landmark("mushroom-nook"))
    }

    @Test fun independentMapSupportsDifferentBoundsWithoutPublishingPlaceholder() {
        val map = ExplorationMap("test-only", 1, "Test map",
            WorldBounds(1000.0, 2000.0, 3000.0, 4000.0), emptyList())
        assertEquals(listOf("0102", "0103", "0202", "0203"), map.cells.map { it.reference })
        assertNull(map.cell("2437"))
        assertNull(ExplorationMaps.find(map.id, 1))
    }

    @Test fun mapValidationRejectsAmbiguousLandmarksAndFractionalBoundaries() {
        val map = ExplorationMaps.woodland
        assertThrows(IllegalArgumentException::class.java) {
            map.copy(landmarks = listOf(WoodlandLandmarks.mushrooms,
                WoodlandLandmarks.mushrooms.copy(id = "duplicate-cell")))
        }
        assertThrows(IllegalArgumentException::class.java) {
            map.copy(bounds = WorldBounds(24001.0, 37000.0, 30000.0, 43000.0))
        }
        assertThrows(IllegalArgumentException::class.java) {
            map.copy(landmarks = listOf(WoodlandLandmarks.mushrooms.copy(point = WorldPoint(30000.0, 38900.0))))
        }
    }
}
