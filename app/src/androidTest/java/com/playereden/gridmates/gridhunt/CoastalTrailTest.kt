package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.TrailChapterCatalog
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CoastalTrailTest {
    @get:Rule val compose = createComposeRule()

    @Test fun allNineSquaresAdvanceResumeAndReplayWithoutWoodlandChanges() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val chapter = TrailChapterCatalog.coastalDraft
        val coast = context.getSharedPreferences(chapter.saveName, Context.MODE_PRIVATE)
        val woodland = context.getSharedPreferences(TrailChapterCatalog.woodland.saveName, Context.MODE_PRIVATE)
        val settings = context.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
        val original = woodland.all.toMap()
        val originalPrecision = settings.all["precise_selection"]
        check(coast.edit().clear().putBoolean("guide_seen",true).commit())
        check(settings.edit().putBoolean("precise_selection",true).commit())
        val visible = mutableStateOf(true)
        val look = mutableStateOf(MapLook.SURVEY_TABLE)
        compose.setContent {
            if (visible.value) FirstBearingsScreen(
                onHome = { visible.value = false }, mapLook = look.value,
                onLook = { look.value = it }, hapticsEnabled = false, chapter = chapter
            )
        }
        fun progress(hunt: Int, step: Int) {
            compose.waitUntil(10000) {
                coast.getInt("hunt_index",-1)==hunt && coast.getInt("objective_index",-1)==step
            }
        }
        fun move(direction: String) {
            compose.onNodeWithContentDescription("Move selected square $direction").performClick()
        }
        fun select(east: Int,north: Int) {
            // Clamp to southwest corner regardless of previous selection.
            repeat(6) { move("west") }
            repeat(6) { move("south") }
            repeat(east-42) { move("east") }
            repeat(north-51) { move("north") }
            compose.onNodeWithText("YOUR PICK  $east$north",substring=true).assertExists()
        }
        fun reopen(tag: String) {
            compose.onNodeWithTag(tag).performClick()
            compose.waitForIdle()
            compose.runOnIdle { visible.value = true }
        }
        try {
            progress(0,0)
            select(42,51)
            compose.onNodeWithText("Try this square").performClick()
            progress(0,0)
            chapter.hunts.forEachIndexed { index, hunt ->
                hunt.objectives.forEachIndexed { step, cell ->
                    select(cell.eastIndex,cell.northIndex)
                    compose.onNodeWithText("Try this square").performClick()
                    progress(index,step+1)
                    if (index==0 && step==0) {
                        reopen("chapterHome")
                        progress(0,1)
                        compose.onNodeWithText("CLUE 2 OF 3").assertExists()
                        compose.runOnIdle { look.value = MapLook.values().first { it != MapLook.SURVEY_TABLE } }
                    }
                }
                compose.onNodeWithTag("puzzleFinishCard").assertExists()
                if (index < chapter.hunts.lastIndex) {
                    compose.onNodeWithTag("finishContinue").performClick()
                    progress(index+1,0)
                }
            }
            compose.onNodeWithText("Coastal adventure complete!").assertExists()
            reopen("finishHome")
            progress(2,3)
            compose.onNodeWithText("Coastal adventure complete!").assertExists()
            compose.onNodeWithTag("finishContinue").performClick()
            progress(0,0)
            compose.runOnIdle { visible.value = false }
            assertEquals(original,woodland.all)
        } finally {
            compose.runOnIdle { visible.value = false }
            check(coast.edit().clear().commit())
            val editor = settings.edit()
            if (originalPrecision is Boolean) editor.putBoolean("precise_selection",originalPrecision)
            else editor.remove("precise_selection")
            check(editor.commit())
        }
    }
}
