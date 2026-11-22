package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class GridMathTest {
    @Test fun workedExampleAndPrecisions() {
        val point = WorldPoint(24375.0, 37860.0)
        assertEquals("2437", GridReference.format(point, 4))
        assertEquals("243378", GridReference.format(point, 6))
        assertEquals("24373786", GridReference.format(point, 8))
        assertEquals("2437537860", GridReference.format(point, 10))
    }

    @Test fun southWestInclusiveNorthEastExclusive() {
        val cell = GridReference.parse("2437")
        assertTrue(cell.contains(WorldPoint(24000.0, 37000.0)))
        assertTrue(cell.contains(WorldPoint(24999.999, 37999.999)))
        assertFalse(cell.contains(WorldPoint(25000.0, 37999.0)))
        assertFalse(cell.contains(WorldPoint(24999.0, 38000.0)))
        assertEquals("2537", GridReference.format(WorldPoint(25000.0, 37999.0)))
        assertEquals("2438", GridReference.format(WorldPoint(24999.0, 38000.0)))
        assertEquals("2538", GridReference.format(WorldPoint(25000.0, 38000.0)))
    }

    @Test fun hundredMeterBoundaryNoRounding() {
        assertEquals("243378", GridReference.format(WorldPoint(24399.999, 37899.999), 6))
        assertEquals("244379", GridReference.format(WorldPoint(24400.0, 37900.0), 6))
    }

    @Test fun zerosArePreservedAndMalformedRejected() {
        assertEquals("0417", GridReference.parse("0417").reference)
        assertEquals("000001", GridReference.parse("000001").reference)
        listOf("417", "041", "04170", "04 7", "-417", "abc1", "04170111a").forEach { text ->
            assertThrows(IllegalArgumentException::class.java) { GridReference.parse(text) }
        }
    }

    @Test fun outOfBoundsAndNonFiniteAreRejected() {
        listOf(
            WorldPoint(-0.1, 1000.0), WorldPoint(100000.0, 0.0),
            WorldPoint(0.0, 100000.0), WorldPoint(Double.NaN, 0.0),
            WorldPoint(Double.POSITIVE_INFINITY, 0.0)
        ).forEach { point ->
            assertThrows(IllegalArgumentException::class.java) { GridReference.fromWorld(point) }
        }
    }

    @Test fun fixedObjectivesValidAndUnique() {
        assertEquals(3, WoodlandTrainingMap.objectives.size)
        assertEquals(3, WoodlandTrainingMap.objectives.distinct().size)
        assertEquals(listOf("2437", "2539", "2741"), WoodlandTrainingMap.objectives.map { it.reference })
        assertTrue(WoodlandTrainingMap.objectives.all {
            WoodlandTrainingMap.bounds.contains(WorldPoint(it.westMeters.toDouble(), it.southMeters.toDouble()))
        })
    }

    @Test fun cameraRoundTripAndNorthUp() {
        val b = WoodlandTrainingMap.bounds
        val camera = MapCamera.fit(b, 400.0, 750.0)
        val center = WorldPoint(24375.0, 37860.0)
        val p = camera.worldToScreen(center, 400.0, 750.0)
        val back = camera.screenToWorld(p, 400.0, 750.0)
        assertEquals(center.easting, back.easting, 1e-8)
        assertEquals(center.northing, back.northing, 1e-8)
        val up = camera.worldToScreen(WorldPoint(24375.0, 39000.0), 400.0, 750.0)
        assertTrue(up.y < p.y)
    }

    @Test fun zoomAroundCentroidAndPanDoNotChangeWorldPoint() {
        val b = WoodlandTrainingMap.bounds
        val width = 400.0
        val height = 600.0
        val camera = MapCamera.fit(b, width, height)
        val finger = PixelPoint(205.0, 280.0)
        val before = camera.screenToWorld(finger, width, height)
        val zoomed = camera.transform(b, width, height, finger, PixelPoint(0.0, 0.0), 2.0)
        val after = zoomed.screenToWorld(finger, width, height)
        assertEquals(before.easting, after.easting, 1e-6)
        assertEquals(before.northing, after.northing, 1e-6)
        val panned = zoomed.transform(b, width, height, finger, PixelPoint(-40.0, 25.0), 1.0)
        assertEquals(zoomed.pixelsPerMeter, panned.pixelsPerMeter, 0.0)
        val world = WorldPoint(24375.0, 37860.0)
        val back = panned.screenToWorld(panned.worldToScreen(world, width, height), width, height)
        assertEquals(world.easting, back.easting, 1e-6)
        assertEquals(world.northing, back.northing, 1e-6)
    }

    @Test fun validationChecksCellsNotPixelCenters() {
        val correct = GridReference.parse("2437")
        assertTrue(HuntRules.isCorrect(correct, GridReference.fromWorld(WorldPoint(24999.0, 37999.0))))
        assertFalse(HuntRules.isCorrect(correct, GridReference.parse("2537")))
        assertEquals(
            "Right northing. Check the easting along the bottom.",
            HuntRules.errorText(correct, GridReference.parse("2537"))
        )
    }
}
