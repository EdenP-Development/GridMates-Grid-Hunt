package com.playereden.gridhunt.core

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class WhisperingCoastDraftTest {
    private val map = WhisperingCoastDraft.map
    private val generator = MapHuntGenerator(map)

    @Test fun candidateCoordinatesMatchTheDesignAndRemainUnpublished() {
        assertEquals(36, map.cells.size)
        assertEquals(listOf("4656", "4352", "4551", "4753", "4455", "4654"),
            map.landmarks.map { it.cell.reference })
        assertEquals(6, map.landmarks.map { it.cell }.distinct().size)
        assertNull(ExplorationMaps.find(map.id, map.revision))
        assertTrue(PublishedWorlds.selection.destinations.none { it.map.id == map.id })
        assertTrue(map.cells.intersect(ExplorationMaps.woodland.cells.toSet()).isEmpty())
    }

    @Test fun coastalLandmarkHuntsCanAvoidAllPreviousTargets() {
        repeat(100) { seed ->
            val previous = generator.generateLandmarks(Random(seed))
            val next = generator.generateFreshLandmarks(previous.toSet(), Random(seed + 1))
            assertEquals(3, next.distinct().size)
            assertTrue(next.intersect(previous.toSet()).isEmpty())
            assertEquals(next, generator.restoreLandmarks(generator.encode(next)))
        }
    }

    @Test fun mixedHuntsRoundTripOnTheCorrectMapOnly() {
        repeat(100) { seed ->
            for (count in generator.mixedLengths) {
                val objectives = generator.generateMixed(count, Random(seed))
                val cells = objectives.map { it.cell }
                val codes = generator.encode(cells)
                val kinds = objectives.joinToString(",") { it.type }
                assertEquals(count, cells.distinct().size)
                assertEquals(objectives, generator.restoreMixed(codes, kinds))
                assertTrue(MapHuntGenerator(ExplorationMaps.woodland).restoreMixed(codes, kinds).isEmpty())
            }
        }
    }
}
