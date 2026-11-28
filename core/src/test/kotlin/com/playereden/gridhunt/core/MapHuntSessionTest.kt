package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class MapHuntSessionTest {
    private val woodland = ExplorationMaps.woodland
    private val session = MapHuntSession(woodland.id, woodland.revision, "grid", "2437,2538,2639", step = 1)

    @Test fun exactIdentityRequiredEvenWithIdenticalCoordinates() {
        val other = woodland.copy(id = "test-overlapping-map")
        assertNull(MapHuntSessions.restore(session, other))
        assertNull(MapHuntSessions.restore(session, woodland.copy(revision = 2)))
        assertNull(MapHuntSessions.restorePublished(session.copy(mapId = other.id)))
        assertNull(MapHuntSessions.restorePublished(session.copy(mapRevision = 2)))
        assertNotNull(MapHuntSessions.restore(session.copy(mapId = other.id), other))
    }

    @Test fun validSessionsRetainOrderAndProgress() {
        val restored = requireNotNull(MapHuntSessions.restorePublished(session))
        assertEquals(woodland, restored.map)
        assertEquals(listOf("2437", "2538", "2639"), restored.objectives.map { it.cell.reference })
        assertEquals(1, restored.step)
        assertEquals(3, MapHuntSessions.restorePublished(session.copy(step = 3))!!.step)
        val generator = MapHuntGenerator(woodland)
        for (seed in 0..99) {
            val mixed = generator.generateFreshMixed(9, emptySet(), Random(seed))
            val saved = MapHuntSession(woodland.id, woodland.revision, "mixed",
                generator.encode(mixed.map { it.cell }), mixed.joinToString(",") { it.type }, 4)
            assertEquals(mixed, MapHuntSessions.restorePublished(saved)!!.objectives)
        }
    }

    @Test fun malformedSessionsFailWithoutFallback() {
        for (bad in listOf(session.copy(mode = "unknown"), session.copy(step = -1),
            session.copy(step = 4), session.copy(codes = "2437,2437,2639"),
            session.copy(codes = "2437,3040,2639"), session.copy(kinds = "grid"),
            session.copy(mode = "identify"), session.copy(mode = "mixed"))) {
            assertNull(bad.toString(), MapHuntSessions.restorePublished(bad))
        }
    }

    @Test fun historicalWoodlandProgressCanBeClampedWithoutWriting() {
        val restored = MapHuntSessions.legacyWoodland("grid", "2437,2538,2639", "", 99)!!
        assertEquals(3, restored.step)
        assertEquals(woodland, restored.map)
        assertEquals(0, MapHuntSessions.legacyWoodland("grid", "2437,2538,2639", "", -1)!!.step)
        assertNull(MapHuntSessions.legacyWoodland("unknown", "2437,2538,2639", "", 1))
    }
}
