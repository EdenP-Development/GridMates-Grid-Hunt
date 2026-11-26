package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class MapSurveyMarkersTest {
    private val coast = SurveyMarkerCatalog.coastalDraft
    private val woodland = SurveyMarkerCatalog.woodland

    @Test fun authoredCoastalAnswersAreDistinctAndMapSpecific() {
        val expected = listOf("4256","4354","4453","4252","4556")
        assertEquals(expected, coast.markers.map { it.cell.reference })
        coast.markers.zip(expected).forEach { (marker, answer) ->
            assertTrue(coast.check(marker.id, answer))
            assertFalse(woodland.check(marker.id, answer))
            assertFalse(coast.check(marker.id, "0000"))
            assertFalse(coast.check(marker.id, "425"))
            assertFalse(coast.check(marker.id, "abcd"))
        }
        assertFalse(coast.check("unknown", expected.first()))
        assertEquals(ReverseTraining.markers, woodland.markers)
        assertNull(SurveyMarkerCatalog.forAuthoredMap(coast.map.copy(revision = 2)))
        assertNull(ExplorationMaps.find(coast.map.id, coast.map.revision))
    }

    @Test fun invalidMarkerCatalogsAreRejected() {
        val first = coast.markers.first()
        assertThrows(IllegalArgumentException::class.java) {
            MapSurveyMarkers(coast.map, listOf(first, first.copy(id = "other")))
        }
        assertThrows(IllegalArgumentException::class.java) {
            MapSurveyMarkers(coast.map, listOf(first, coast.markers[1].copy(id = first.id)))
        }
        assertThrows(IllegalArgumentException::class.java) {
            MapSurveyMarkers(coast.map, woodland.markers)
        }
    }

    @Test fun expeditionReadMarkerUsesItsOwnMap() {
        val marker = coast.markers.first()
        val objective = ExpeditionObjective("read-a", ExpeditionVerb.READ_MARKER,
            "Read the light", "Read the marked square.", marker.cell,
            markerId = marker.id, map = coast.map)
        fun chapter(item: ExpeditionObjective) = ExpeditionChapter(
            "test-coast", 1, "Test", "Test story", listOf(item), coast.map)
        assertEquals(marker.cell, chapter(objective).objectives.single().expectedCell)
        assertThrows(IllegalArgumentException::class.java) {
            chapter(objective.copy(expectedCell = coast.markers[1].cell))
        }
    }
}
