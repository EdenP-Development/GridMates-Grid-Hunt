package com.playereden.gridmates.gridhunt

import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ExpandedFieldSurveyPreviewTest {
    @get:Rule val compose = createComposeRule()

    private fun capture(filename: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        android.os.SystemClock.sleep(500L)
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        val resolver = instrumentation.targetContext.contentResolver
        var uri: android.net.Uri? = null
        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/GridHuntVisualQA")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val created = requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
            uri = created
            requireNotNull(resolver.openOutputStream(created)).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
            check(resolver.update(created, ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }, null, null) == 1)
        } catch (failure: Throwable) {
            uri?.let { resolver.delete(it, null, null) }
            throw failure
        } finally {
            bitmap.recycle()
        }
    }

    @Test fun playAllTwelveResumeAndReplayWithoutPublishedWrites() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val run = WoodlandFieldSurveyCandidate.run
        val prefs = context.getSharedPreferences(run.saveName, Context.MODE_PRIVATE)
        val protected = listOf(WoodlandExpedition.saveName, WoodlandSupplyExpedition.saveName,
            WoodlandReturnExpedition.saveName, "gridhunt_campaign_earned_v1",
            "gridhunt_campaign_earned_preview_v1").map {
            context.getSharedPreferences(it, Context.MODE_PRIVATE)
        }
        val before = protected.map { it.all.toMap() }
        val original = prefs.all.toMap()
        val visible = mutableStateOf(true)
        try {
            check(prefs.edit().clear().commit())
            compose.setContent { if (visible.value) ExpandedFieldSurveyPreview { visible.value = false } }
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
                    if (i == 3) {
                        capture("32_expanded_survey_opening.png")
                        compose.onNodeWithTag("mixedJournal").performClick()
                        compose.onNodeWithTag("journalProgress").assertTextEquals("4 of 12 discoveries recorded")
                        capture("33_expanded_journal_progress.png")
                        compose.onNodeWithTag("closeJournal").performClick()
                    }
                }
            }
            compose.onNodeWithText("Field survey complete!").assertExists()
            capture("34_expanded_survey_finish.png")
            compose.onNodeWithTag("finishJournal").performClick()
            compose.onNodeWithTag("journalEntry_" + run.chapter.objectives.last().id)
                .performScrollTo().assertIsDisplayed()
            capture("35_expanded_journal_final_entry.png")
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
