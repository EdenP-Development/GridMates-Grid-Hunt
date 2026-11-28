package com.playereden.gridmates.gridhunt

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.playereden.gridhunt.core.WoodlandFieldSurveyCandidate
import org.junit.Rule
import org.junit.Test

class ExpandedJournalTest {
    @get:Rule val compose = createComposeRule()

    @Test fun expandedJournalShowsProgressAndScrollsToFinalDiscovery() {
        val run = WoodlandFieldSurveyCandidate.run
        val step = mutableStateOf(4)
        val visible = mutableStateOf(true)
        compose.setContent {
            if (visible.value) ExpeditionJournalDialog(run, step.value, 1) {
                visible.value = false
            }
        }
        compose.onNodeWithTag("journalProgress").assertTextEquals("4 of 12 discoveries recorded")
        compose.onNodeWithContentDescription("Discovery 5, current objective").performScrollTo().assertIsDisplayed()
        compose.onNodeWithContentDescription("Discovery 12, not yet recovered").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("journalEntry_" + run.chapter.objectives.last().id).assertDoesNotExist()
        compose.onNodeWithTag("journalCurrent").performScrollTo().assertIsDisplayed()
        compose.runOnIdle { step.value = 12 }
        compose.onNodeWithTag("journalProgress").performScrollTo().assertTextEquals("12 of 12 discoveries recorded")
        compose.onNodeWithTag("journalEntry_" + run.chapter.objectives.last().id).performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("journalComplete").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("journalCurrent").assertDoesNotExist()
        compose.onNodeWithTag("closeJournal").performClick()
        compose.onNodeWithTag("expeditionJournal").assertDoesNotExist()
    }
}
