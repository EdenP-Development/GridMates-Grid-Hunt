package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class ExplorerAcademyTest {
    @Test fun graduationRequiresPracticeAndAwardsOnceAcrossReplay() {
        val academy = ExplorerAcademy
        val practice = academy.exercises.map { it.id }.toSet()
        val empty = emptySet<String>()
        assertNull(academy.graduationCurrent(empty, empty))
        assertEquals(empty, academy.acceptGraduation(empty, empty, "grad-start", "2641"))
        assertEquals(empty, academy.qualifications(empty, practice, empty))
        assertEquals(empty, academy.acceptGraduation(practice, empty, "grad-route", "2840"))
        var earned = empty
        for (exercise in academy.graduation) {
            assertEquals(earned, academy.acceptGraduation(practice, earned, exercise.id, "0000"))
            earned = academy.acceptGraduation(practice, earned, exercise.id, exercise.answer)
        }
        assertTrue(academy.graduated(practice, earned))
        val award = academy.qualifications(empty, practice, earned)
        assertEquals(setOf(academy.qualificationId), award)
        assertEquals(award, academy.qualifications(award, practice, earned))
        assertEquals(award, academy.qualifications(award, empty, empty))
        assertFalse(academy.graduated(empty, empty))
    }

    @Test fun routesResolveAndRejectOutOfBounds() {
        val route = AcademyRoute(GridReference.parse("2538"), listOf(
            AcademyRouteLeg(AcademyDirection.EAST, 2),
            AcademyRouteLeg(AcademyDirection.NORTH, 1)))
        assertEquals("2739", route.destination.reference)
        assertThrows(IllegalArgumentException::class.java) {
            AcademyRoute(GridReference.parse("2942"), listOf(
                AcademyRouteLeg(AcademyDirection.NORTH, 1)))
        }
    }

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
        val field = ExplorerAcademy.accept(restored, "grid-field", "2740")
        assertEquals("woodland-field", ExplorerAcademy.current(field)?.id)
        assertFalse(ExplorerAcademy.practiceComplete(field))
        assertEquals(field, ExplorerAcademy.accept(field, "woodland-field", "2641"))
        val woodland = ExplorerAcademy.accept(field, "woodland-field", WoodlandLandmarks.mushrooms.cell.reference)
        assertEquals("woodland-symbol", ExplorerAcademy.current(woodland)?.id)
        val symbol = ExplorerAcademy.accept(woodland, "woodland-symbol", WoodlandLandmarks.flowers.cell.reference)
        assertEquals("woodland-route", ExplorerAcademy.current(symbol)?.id)
        assertEquals(symbol, ExplorerAcademy.accept(symbol, "woodland-route", "2738"))
        val route = ExplorerAcademy.accept(symbol, "woodland-route", "2739")
        assertEquals("woodland-deduction", ExplorerAcademy.current(route)?.id)
        assertEquals(route, ExplorerAcademy.accept(route, "woodland-deduction", "2840"))
        val completed = ExplorerAcademy.accept(route, "woodland-deduction", "2941")
        assertTrue(ExplorerAcademy.practiceComplete(completed))
        assertNull(ExplorerAcademy.current(completed))
    }
}
