package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class ExplorerAcademyTest {
    @Test fun gridUsesWestAndSouthBoundaries() {
        assertEquals("2437", AcademyTrainingGrid.reference(24, 37))
        assertEquals("2740", AcademyTrainingGrid.reference(27, 40))
        assertTrue(AcademyTrainingGrid.contains("2639"))
        assertFalse(AcademyTrainingGrid.contains("3926"))
        assertFalse(AcademyTrainingGrid.contains("2841"))
    }

    @Test fun incorrectAndOutOfOrderAnswersDoNotAdvance() {
        val empty = emptySet<String>()
        assertEquals(empty, ExplorerAcademy.accept(empty, "north", "West"))
        assertEquals(empty, ExplorerAcademy.accept(empty, "grid-field", "2740"))
        assertEquals(setOf("north"), ExplorerAcademy.accept(empty, "north", "East"))
    }

    @Test fun existingPracticeSavesResumeAtNewFieldExercise() {
        val old = setOf("north", "south", "grid-order", "grid-practice", "unknown")
        val restored = ExplorerAcademy.restore(old)
        assertEquals(4, restored.size)
        assertEquals("grid-field", ExplorerAcademy.current(restored)?.id)
        assertFalse(ExplorerAcademy.practiceComplete(restored))
        assertEquals(restored, ExplorerAcademy.accept(restored, "grid-field", "2739"))
        val completed = ExplorerAcademy.accept(restored, "grid-field", "2740")
        assertTrue(ExplorerAcademy.practiceComplete(completed))
        assertNull(ExplorerAcademy.current(completed))
    }
}
