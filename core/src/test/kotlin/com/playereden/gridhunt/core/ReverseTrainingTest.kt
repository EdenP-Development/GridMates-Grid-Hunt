package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class ReverseTrainingTest {
    @Test fun allMarkersHaveUniqueCanonicalAnswers() {
        assertEquals(5, ReverseTraining.markers.size)
        assertEquals(listOf("2437", "2539", "2741", "2838", "2940"),
            ReverseTraining.markers.map { it.cell.reference })
    }

    @Test fun allSurveyPointsLieWithinFictionalMap() {
        val bounds = WoodlandTrainingMap.bounds
        assertTrue(ReverseTraining.markers.all { bounds.contains(it.point) })
    }

    @Test fun exactFourDigitResponsesRequired() {
        val cabin = ReverseTraining.markers.first()
        assertTrue(ReverseTraining.check(cabin, "2437"))
        assertFalse(ReverseTraining.check(cabin, "243"))
        assertFalse(ReverseTraining.check(cabin, "02437"))
        assertFalse(ReverseTraining.check(cabin, "2438"))
        assertFalse(ReverseTraining.check(cabin, "24 7"))
        assertFalse(ReverseTraining.check(cabin, "abcd"))
    }

    @Test fun mistakenAxisGetsActionableCorrection() {
        val bridge = ReverseTraining.markers[1]
        assertEquals(
            "Right easting. Check the northing going up the map.",
            ReverseTraining.feedback(bridge, "2538")
        )
        assertEquals(
            "Right northing. Check the easting along the bottom.",
            ReverseTraining.feedback(bridge, "2639")
        )
    }

    @Test fun malformedReverseAnswerNeverThrows() {
        val marker = ReverseTraining.markers[2]
        assertFalse(ReverseTraining.check(marker, ""))
        assertFalse(ReverseTraining.check(marker, "777777"))
        assertTrue(ReverseTraining.feedback(marker, "?").contains("four digits"))
    }
}
