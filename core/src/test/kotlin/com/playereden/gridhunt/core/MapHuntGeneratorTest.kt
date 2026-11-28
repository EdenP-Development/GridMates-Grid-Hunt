package com.playereden.gridhunt.core

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class MapHuntGeneratorTest {
    private val other = ExplorationMap("test-coast", 1, "Test coast",
        WorldBounds(1000.0, 2000.0, 5000.0, 6000.0),
        listOf(
            WoodlandLandmark("a", "A", WorldPoint(1500.0, 2500.0), "A", "A"),
            WoodlandLandmark("b", "B", WorldPoint(2500.0, 3500.0), "B", "B"),
            WoodlandLandmark("c", "C", WorldPoint(3500.0, 4500.0), "C", "C")
        ))

    @Test fun independentMapGeneratesAndRestoresOnlyItsOwnCells() {
        val generator = MapHuntGenerator(other)
        val known = other.cells.toSet()
        for (seed in 0..99) {
            val targets = generator.generateFresh(9, emptySet(), Random(seed))
            assertEquals(9, targets.toSet().size)
            assertTrue(targets.all { it in known })
            assertEquals(targets, generator.restore(generator.encode(targets)))
            assertTrue(CustomHuntGenerator.restore(generator.encode(targets)).isEmpty())
            val mixed = generator.generateFreshMixed(6, targets.toSet(), Random(seed))
            assertEquals(6, mixed.map { it.cell }.toSet().size)
            assertTrue(mixed.all { it.cell in known })
            assertEquals(setOf("grid", "gold", "identify", "checklist"), mixed.map { it.type }.toSet())
            assertEquals(mixed, generator.restoreMixed(generator.encode(mixed.map { it.cell }), mixed.joinToString(",") { it.type }))
        }
    }

    @Test fun smallMapsExposeOnlySupportedModesAndLengths() {
        val small = MapHuntGenerator(other.copy(bounds = WorldBounds(1000.0, 2000.0, 3000.0, 4000.0), landmarks = emptyList()))
        assertEquals(listOf(3), small.lengths)
        assertTrue(small.landmarkLengths.isEmpty())
        assertTrue(small.mixedLengths.isEmpty())
        assertThrows(IllegalArgumentException::class.java) { small.generate(6) }
        assertThrows(IllegalArgumentException::class.java) { small.generateLandmarks() }
        assertThrows(IllegalArgumentException::class.java) { small.generateFreshMixed(6, emptySet()) }
    }

    @Test fun woodlandFacadeRetainsSeededOrderingAndLegacyRestore() {
        val generator = MapHuntGenerator(ExplorationMaps.woodland)
        for (seed in 0..99) {
            assertEquals(CustomHuntGenerator.generate(9, Random(seed)), generator.generate(9, Random(seed)))
            assertEquals(CustomHuntGenerator.generateMixed(6, Random(seed)), generator.generateMixed(6, Random(seed)))
        }
        assertEquals(listOf("2437", "2538", "2639"), generator.restore("2437,2538,2639").map { it.reference })
        assertTrue(generator.restore("0102,0203,0304").isEmpty())
    }
}
