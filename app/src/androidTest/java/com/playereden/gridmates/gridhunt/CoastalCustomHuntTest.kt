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
}
