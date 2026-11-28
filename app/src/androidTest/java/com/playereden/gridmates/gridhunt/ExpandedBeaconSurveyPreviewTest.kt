package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ExpandedBeaconSurveyPreviewTest {
    @get:Rule val compose = createComposeRule()

    @Test fun coastalPlaythroughResumeReplayAndIsolation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val run = CoastalBeaconSurveyCandidate.run
        val prefs = context.getSharedPreferences(run.saveName, Context.MODE_PRIVATE)
        val protected = listOf(WoodlandExpedition.saveName, WoodlandSupplyExpedition.saveName,
            WoodlandReturnExpedition.saveName, WoodlandFieldSurveyCandidate.run.saveName,
            WoodlandSupplySurveyCandidate.run.saveName, WoodlandReturnSurveyCandidate.run.saveName,
            CoastalExpeditionPreview.chapter(1).saveName, CoastalExpeditionPreview.chapter(2).saveName,
            CoastalExpeditionPreview.chapter(3).saveName,
            "gridhunt_campaign_earned_v1", "gridhunt_campaign_earned_preview_v1").map {
            context.getSharedPreferences(it, Context.MODE_PRIVATE)
        }
        val before = protected.map { it.all.toMap() }
        val original = prefs.all.toMap()
        val visible = mutableStateOf(true)
        try {
            check(prefs.edit().clear().commit())
            compose.setContent { if (visible.value) ExpandedBeaconSurveyPreview { visible.value = false } }
            fun progress(n: Int) {
                compose.waitUntil(10000) { prefs.getInt("step", -1) == n }
                compose.onNodeWithTag("mixedProgress").assertTextEquals("$n of 12 discoveries recorded")
            }
            fun move(direction: String) {
                compose.onNodeWithContentDescription("Expedition move $direction").performClick()
            }
            val minE = run.chapter.map.cells.minOf { it.eastIndex }
            val minN = run.chapter.map.cells.minOf { it.northIndex }
            fun pick(cell: GridCell) {
                repeat(8) { move("west") }
                repeat(8) { move("south") }
                repeat(cell.eastIndex - minE) { move("east") }
                repeat(cell.northIndex - minN) { move("north") }
            }
            progress(0)
            compose.onNodeWithTag("mixedGuide").performClick()
            compose.onNodeWithTag("coastalMapGuide").assertExists()
            compose.onNodeWithTag("dismissCoastalMapGuide").performClick()
            run.chapter.objectives.forEachIndexed { i, objective ->
                when (objective.verb) {
                    ExpeditionVerb.FIND_SQUARE -> pick(objective.expectedCell)
                    ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK ->
                        compose.onNodeWithTag("mixedCodeInput").performTextReplacement(objective.expectedCell.reference)
                    ExpeditionVerb.IDENTIFY_LANDMARK ->
                        compose.onNodeWithTag("mixedOption_" + objective.landmarkId).performClick()
                }
                compose.onNodeWithTag("mixedCheck").performClick()
                progress(i + 1)
                if (i == 3 || i == 7) {
                    compose.onNodeWithTag("mixedHome").performClick()
                    compose.runOnIdle { visible.value = true }
                    progress(i + 1)
                    compose.onNodeWithTag("mixedJournal").performClick()
                    compose.onNodeWithTag("journalProgress").assertTextEquals("${i + 1} of 12 discoveries recorded")
                    compose.onNodeWithTag("closeJournal").performClick()
                }
            }
            compose.onNodeWithText("Coastal chart complete!").assertExists()
            compose.onNodeWithTag("finishJournal").performClick()
            compose.onNodeWithTag("journalEntry_" + run.chapter.objectives.last().id)
                .performScrollTo().assertIsDisplayed()
            compose.onNodeWithText("CHART · ENTRY 8").performScrollTo().assertIsDisplayed()
            compose.onNodeWithTag("closeJournal").performClick()
            compose.onNodeWithTag("finishContinue").performClick()
            progress(0)
            assertEquals(before, protected.map { it.all.toMap() })
        } finally {
            compose.runOnIdle { visible.value = false }
            val editor = prefs.edit().clear()
            original.forEach { (k,v) ->
                when(v) {
                    is Int -> editor.putInt(k,v)
                    is String -> editor.putString(k,v)
                    is Boolean -> editor.putBoolean(k,v)
                    is Long -> editor.putLong(k,v)
                    is Float -> editor.putFloat(k,v)
                    is Set<*> -> editor.putStringSet(k,v.filterIsInstance<String>().toSet())
                }
            }
            check(editor.commit())
        }
    }
}
