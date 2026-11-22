package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class FirstBearingsChapterTest {
    @Test fun threeCuratedHuntsHaveDistinctPlayableCells() {
        assertEquals(3, FirstBearingsChapter.hunts.size)
        val all = FirstBearingsChapter.hunts.flatMap { it.objectives }
        assertEquals(9, all.size)
        assertEquals(9, all.distinct().size)
        assertTrue(all.all { it.digits == 4 })
        assertEquals(listOf("2437", "2539", "2741"),
            FirstBearingsChapter.hunts[0].objectives.map { it.reference })
    }

    @Test fun everyTargetIsCompletelyInsideTheFictionalMap() {
        val bounds = WoodlandTrainingMap.bounds
        FirstBearingsChapter.hunts.flatMap { it.objectives }.forEach { cell ->
            assertTrue(bounds.contains(WorldPoint(cell.westMeters.toDouble(), cell.southMeters.toDouble())))
            assertTrue(bounds.contains(WorldPoint(cell.westMeters + 999.99, cell.southMeters + 999.99)))
        }
    }

    @Test fun correctSolutionsAdvanceOnlyOneObjective() {
        val a = ChapterPosition(0, 0)
        val b = FirstBearingsChapter.acceptCorrect(a)
        assertEquals(ChapterPosition(0, 1), b)
        assertFalse(FirstBearingsChapter.finished(b))
        assertEquals(ChapterPosition(0, 2), FirstBearingsChapter.acceptCorrect(b))
        assertTrue(FirstBearingsChapter.finished(FirstBearingsChapter.acceptCorrect(ChapterPosition(0, 2))))
    }

    @Test fun huntBoundaryAndExplicitReplay() {
        val endFirst = ChapterPosition(0, 3)
        assertEquals(ChapterPosition(1, 0), FirstBearingsChapter.nextHuntOrReplay(endFirst))
        assertEquals(ChapterPosition(2, 0),
            FirstBearingsChapter.nextHuntOrReplay(ChapterPosition(1, 3)))
        assertEquals(ChapterPosition(0, 0),
            FirstBearingsChapter.nextHuntOrReplay(ChapterPosition(2, 3)))
    }

    @Test fun cannotSkipAnUnfinishedHuntOrSubmitToFinishedOne() {
        assertThrows(IllegalArgumentException::class.java) {
            FirstBearingsChapter.nextHuntOrReplay(ChapterPosition(1, 1))
        }
        assertThrows(IllegalArgumentException::class.java) {
            FirstBearingsChapter.acceptCorrect(ChapterPosition(0, 3))
        }
    }

    @Test fun corruptSavedIndicesRecoverSafely() {
        assertEquals(ChapterPosition(0, 0), FirstBearingsChapter.restore(-44, -10))
        assertEquals(ChapterPosition(2, 3), FirstBearingsChapter.restore(88, 99))
        assertEquals(ChapterPosition(1, 2), FirstBearingsChapter.restore(1, 2))
    }
}
