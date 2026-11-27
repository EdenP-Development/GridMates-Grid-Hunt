package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.WorldHuntStore
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CoastalCustomHuntTest {
    @get:Rule val compose = createComposeRule()

    @Test fun allModesGenerateValidSessionsAndProtectReplacement() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val map = WhisperingCoastDraft.map
        val store = WorldHuntStore(context,map)
        val prefs = context.getSharedPreferences("gridhunt_world_hunt_v1_whispering-coast-01_r1", Context.MODE_PRIVATE)
        val legacy = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val original = legacy.all.toMap()
        check(prefs.edit().clear().commit())
        val visible = mutableStateOf(true)
        compose.setContent {
            if (visible.value) CustomHuntScreen(MapLook.SURVEY_TABLE, {}, { visible.value=false },map)
        }
        fun click(tag: String) {
            compose.onNodeWithTag(tag).performScrollTo().performClick()
        }
        fun ready(): WorldHuntStore.ReadResult.Ready {
            compose.waitForIdle()
            return store.read() as WorldHuntStore.ReadResult.Ready
        }
        fun setup() { compose.onNodeWithTag("customBack").performClick() }
        try {
            listOf("gold","identify","checklist","grid","mixed").forEachIndexed { index, mode ->
                click("customMode_$mode")
                if (mode == "mixed") click("customLength_6")
                if (mode == "grid" || mode == "gold") click("customLength_3")
                val before = prefs.all.toMap()
                click("customStart")
                if (index > 0) {
                    compose.onNodeWithTag("customCancelReplace").performClick()
                    assertEquals(before,prefs.all)
                    click("customStart")
                    compose.onNodeWithTag("customConfirmReplace").performClick()
                }
                val saved = ready()
                assertEquals(mode,saved.session.mode)
                assertEquals(0,saved.session.step)
                assertEquals(if (mode=="mixed") 6 else 3,saved.restored.objectives.size)
                assertTrue(saved.restored.objectives.all { it.cell in map.cells })
                assertEquals(saved.restored.objectives.size,saved.restored.objectives.map { it.cell }.distinct().size)
                if (mode=="mixed") assertTrue(saved.restored.objectives.map { it.type }.containsAll(listOf("grid","gold","identify","checklist")))
                if (mode=="gold") {
                    compose.onNodeWithTag("customGoldInput").performTextReplacement(saved.restored.objectives.first().cell.reference)
                    compose.onNodeWithTag("customGoldCheck").performClick()
                    assertEquals(1,ready().session.step)
                }
                setup()
                val snapshot = ready().session
                compose.onNodeWithTag("customBack").performClick()
                compose.waitForIdle()
                compose.runOnIdle { visible.value=true }
                click("customResume")
                assertEquals(snapshot,ready().session)
                setup()
            }
            assertEquals(original,legacy.all)
        } finally {
            compose.runOnIdle { visible.value=false }
            check(prefs.edit().clear().commit())
        }
    }

    @Test fun invalidEnvelopeIsNotSilentlyOverwritten() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val map = WhisperingCoastDraft.map
        val prefs = context.getSharedPreferences("gridhunt_world_hunt_v1_whispering-coast-01_r1",Context.MODE_PRIVATE)
        check(prefs.edit().clear().putInt("schema",999).putString("codes","unknown").commit())
        val original = prefs.all.toMap()
        val visible = mutableStateOf(true)
        compose.setContent {
            if (visible.value) CustomHuntScreen(MapLook.SURVEY_TABLE, {}, {},map)
        }
        try {
            compose.onNodeWithText("Saved hunt could not be restored. It has not been changed.").assertExists()
            compose.onNodeWithTag("customResume").assertDoesNotExist()
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            compose.onNodeWithTag("customCancelReplace").performClick()
            assertEquals(original,prefs.all)
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            compose.onNodeWithTag("customConfirmReplace").performClick()
            compose.waitForIdle()
            assertTrue(WorldHuntStore(context,map).read() is WorldHuntStore.ReadResult.Ready)
        } finally {
            compose.runOnIdle { visible.value=false }
            check(prefs.edit().clear().commit())
        }
    }

    @Test fun mixedExpeditionSolvesEveryObjectiveAndReplaysWithFreshCells() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val map = WhisperingCoastDraft.map
        val store = WorldHuntStore(context,map)
        val prefs = context.getSharedPreferences("gridhunt_world_hunt_v1_whispering-coast-01_r1",Context.MODE_PRIVATE)
        val precision = context.getSharedPreferences("gridhunt_ui_preferences",Context.MODE_PRIVATE)
        val originalPrecision = precision.all["precise_selection"]
        val legacy = context.getSharedPreferences("gridhunt_custom_v1",Context.MODE_PRIVATE)
        val original = legacy.all.toMap()
        check(prefs.edit().clear().commit())
        check(precision.edit().putBoolean("precise_selection",true).commit())
        val visible = mutableStateOf(true)
        compose.setContent {
            if (visible.value) CustomHuntScreen(MapLook.SURVEY_TABLE, {}, {},map)
        }
        fun ready(): WorldHuntStore.ReadResult.Ready {
            compose.waitForIdle()
            return store.read() as WorldHuntStore.ReadResult.Ready
        }
        fun move(direction: String) {
            compose.onNodeWithContentDescription("Move selected square $direction").performClick()
        }
        try {
            compose.onNodeWithTag("customMode_mixed").performScrollTo().performClick()
            compose.onNodeWithTag("customLength_6").performScrollTo().performClick()
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            val first = ready()
            first.restored.objectives.forEachIndexed { index, objective ->
                when (objective.type) {
                    "grid" -> {
                        repeat(6) { move("west") }
                        repeat(6) { move("south") }
                        repeat(objective.cell.eastIndex-42) { move("east") }
                        repeat(objective.cell.northIndex-51) { move("north") }
                        compose.onNodeWithText("Try this square").performClick()
                    }
                    "gold", "checklist" -> {
                        val prefix = if (objective.type=="gold") "customGold" else "customChecklist"
                        compose.onNodeWithTag(prefix+"Input").performTextReplacement(objective.cell.reference)
                        compose.onNodeWithTag(prefix+"Check").performClick()
                    }
                    "identify" -> {
                        val landmark = map.landmarks.single { it.cell==objective.cell }
                        compose.onNodeWithTag("customIdentify_"+landmark.id).performClick()
                        compose.onNodeWithTag("customIdentifyCheck").performClick()
                    }
                    else -> error("Unexpected objective type")
                }
                assertEquals(index+1,ready().session.step)
            }
            compose.onNodeWithText("Your hunt is complete!").assertExists()
            compose.onNodeWithTag("finishContinue").performClick()
            val next = ready()
            assertEquals(0,next.session.step)
            assertEquals("mixed",next.session.mode)
            assertEquals(6,next.restored.objectives.size)
            val previous = first.restored.objectives.map { it.cell }.toSet()
            val nextCells = next.restored.objectives.map { it.cell }.toSet()
            val unusedLandmarks = map.landmarks.count { it.cell !in previous }
            // Two distinct landmark objectives are mandatory. Reuse is only
            // necessary when fewer than two unused landmark cells remain.
            assertEquals(6,nextCells.size)
            assertEquals(maxOf(0,2-unusedLandmarks),previous.intersect(nextCells).size)
            assertEquals(original,legacy.all)
        } finally {
            compose.runOnIdle { visible.value=false }
            check(prefs.edit().clear().commit())
            val editor = precision.edit()
            if (originalPrecision is Boolean) editor.putBoolean("precise_selection",originalPrecision)
            else editor.remove("precise_selection")
            check(editor.commit())
        }
    }
}
