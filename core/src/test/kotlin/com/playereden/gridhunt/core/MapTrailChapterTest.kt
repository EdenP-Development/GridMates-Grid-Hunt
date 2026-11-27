package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class MapTrailChapterTest {
    @Test fun nineCoastalObjectivesAdvanceAcrossThreeTrailsAndReplay() {
        val chapter = TrailChapterCatalog.coastalDraft
        assertEquals(listOf("4256","4455","4656","4354","4453","4654","4252","4352","4551"),
            chapter.hunts.flatMap { it.objectives }.map { it.reference })
        var position = chapter.restore(0,0)
        chapter.hunts.forEachIndexed { index, hunt ->
            assertEquals(ChapterPosition(index,0),position)
            hunt.objectives.forEachIndexed { step, cell ->
                assertTrue(cell in chapter.map.cells)
                assertEquals(ChapterPosition(index,step),position)
                assertThrows(IllegalArgumentException::class.java) { chapter.nextHuntOrReplay(position) }
                position = chapter.acceptCorrect(position)
            }
            assertTrue(chapter.finished(position))
            assertThrows(IllegalArgumentException::class.java) { chapter.acceptCorrect(position) }
            position = chapter.nextHuntOrReplay(position)
        }
        assertEquals(ChapterPosition(0,0),position)
        assertEquals(ChapterPosition(0,0),chapter.restore(-1,-1))
        assertEquals(ChapterPosition(2,3),chapter.restore(99,99))
    }

    @Test fun directionalMovementUsesCorrectMapBoundaries() {
        val coast = TrailChapterCatalog.coastalDraft
        val woodland = TrailChapterCatalog.woodland
        assertEquals("4454",coast.moveSelection(null,0,0).reference)
        assertEquals("2640",woodland.moveSelection(null,0,0).reference)
        for (chapter in listOf(coast,woodland)) {
            chapter.map.cells.forEach { cell ->
                for (east in -1..1) for (north in -1..1) {
                    val moved = chapter.moveSelection(cell,east,north)
                    assertTrue(moved in chapter.map.cells)
                    assertEquals((cell.eastIndex+east).coerceIn(chapter.map.cells.minOf { it.eastIndex },chapter.map.cells.maxOf { it.eastIndex }),moved.eastIndex)
                    assertEquals((cell.northIndex+north).coerceIn(chapter.map.cells.minOf { it.northIndex },chapter.map.cells.maxOf { it.northIndex }),moved.northIndex)
                }
            }
        }
        assertEquals("4251",coast.moveSelection(GridReference.parse("4251"),-1,-1).reference)
        assertEquals("4756",coast.moveSelection(GridReference.parse("4756"),1,1).reference)
        assertEquals("4454",coast.moveSelection(GridReference.parse("2640"),0,0).reference)
    }

    @Test fun woodlandCompatibilityAndUnpublishedCoast() {
        val woodland = TrailChapterCatalog.woodland
        val coast = TrailChapterCatalog.coastalDraft
        assertEquals(FirstBearingsChapter.hunts,woodland.hunts)
        assertEquals("gridhunt_first_bearings_v1",woodland.saveName)
        assertNotEquals(woodland.saveName,coast.saveName)
        assertNull(TrailChapterCatalog.forAuthoredMap(coast.map.copy(revision=2)))
        assertNull(ExplorationMaps.find(coast.map.id,coast.map.revision))
        assertThrows(IllegalArgumentException::class.java) {
            MapTrailChapter(coast.map,"invalid","invalid",woodland.hunts)
        }
    }
}
