package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class ChapterExpansionPlanTest {
    private val legacy = WoodlandExpedition.chapter
    private val expanded = legacy.copy(revision = legacy.revision + 1,
        objectives = legacy.objectives + (1..8).map {
            // Synthetic fixture only; never published as authored content.
            legacy.objectives.first().copy(id = "synthetic-$it")
        })
    private val plan = ChapterExpansionPlan(legacy, expanded)

    @Test fun everyLegacyStepRetainsExactlyItsSolvedObjectiveIds() {
        for (step in 0..legacy.objectives.size) {
            val progress = requireNotNull(plan.project(step,false))
            assertEquals(legacy.objectives.take(step).map { it.id }.toSet(), progress.solvedObjectiveIds)
            assertEquals(step,plan.completedObjectives(progress))
            assertEquals(step == legacy.objectives.size,progress.legacyEntitlement)
            assertFalse(plan.isExpandedComplete(progress))
        }
    }

    @Test fun historicalEntitlementDoesNotInventReplayDiscoveries() {
        val progress = requireNotNull(plan.project(1,true))
        assertTrue(progress.legacyEntitlement)
        assertEquals(1,plan.completedObjectives(progress))
        assertFalse(plan.isExpandedComplete(progress))
    }

    @Test fun invalidStepsAndUnknownIdentitiesAreRejected() {
        assertNull(plan.project(-1,false))
        assertNull(plan.project(5,false))
        assertEquals(0,plan.completedObjectives(requireNotNull(plan.project(null,false))))
        val progress = requireNotNull(plan.project(4,false))
        assertFalse(plan.accepts(progress.copy(revision=legacy.revision)))
        assertFalse(plan.accepts(progress.copy(chapterId="other")))
        assertFalse(plan.accepts(progress.copy(solvedObjectiveIds=setOf("unknown"))))
    }

    @Test fun changedLegacyPrefixCannotBeExpandedSilently() {
        val changed = expanded.copy(objectives = expanded.objectives.reversed())
        assertTrue(runCatching { ChapterExpansionPlan(legacy,changed) }.isFailure)
        assertTrue(runCatching { ChapterExpansionPlan(legacy,expanded.copy(revision=legacy.revision)) }.isFailure)
    }
}
