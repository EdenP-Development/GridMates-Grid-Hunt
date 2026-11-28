package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.IdentifyMissionCatalog
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CoastalIdentifyTest {
    @get:Rule val compose = createComposeRule()

    @Test fun identifyAllSixResumeAndReplayWithoutWoodlandChanges() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val mission = IdentifyMissionCatalog.coastalDraft
        val coast = context.getSharedPreferences(mission.saveName, Context.MODE_PRIVATE)
        val woodland = context.getSharedPreferences(IdentifyMissionCatalog.woodland.saveName, Context.MODE_PRIVATE)
        val original = woodland.all.toMap()
        check(coast.edit().clear().commit())
        val visible = mutableStateOf(true)
        val look = mutableStateOf(MapLook.SURVEY_TABLE)
        compose.setContent {
            if (visible.value) WoodlandIdentifyScreen(
                onHome = { visible.value = false }, mapLook = look.value,
                onLook = { look.value = it }, hapticsEnabled = false, mission = mission
            )
        }
        fun progress(step: Int) {
            compose.waitUntil(10000) { coast.getInt("step",-1) == step }
            compose.onNodeWithTag("identifyProgress").assertTextEquals("$step of 6 identified")
        }
        fun submit(id: String) {
            compose.onNodeWithTag("identifyOption_$id").performClick()
            compose.onNodeWithTag("identifyCheck").performClick()
        }
        fun reopen() {
            compose.onNodeWithTag("identifyHome").performClick()
            compose.waitForIdle()
            compose.runOnIdle { visible.value = true }
        }
        try {
            progress(0)
            compose.onNodeWithTag("identifyGuide").performClick()
            compose.onNodeWithTag("coastalMapGuide").assertExists()
            compose.onNodeWithTag("dismissCoastalMapGuide").performClick()
            submit(mission.choices.last().id)
            compose.onNodeWithTag("identifyFeedback").assertTextContains("Not quite",substring=true)
            progress(0)
            mission.questions.forEachIndexed { index, question ->
                compose.onNodeWithTag("identifyReference").assertTextEquals(question.cell.reference)
                submit(question.answerLandmarkId)
                progress(index+1)
                if (index == 0) {
                    reopen()
                    progress(1)
                    compose.runOnIdle { look.value = MapLook.values().first { it != MapLook.SURVEY_TABLE } }
                }
            }
            compose.onNodeWithText("Every square identified!").assertExists()
            reopen()
            progress(6)
            compose.onNodeWithText("Identify them again").performClick()
            progress(0)
            compose.runOnIdle { visible.value = false }
            assertEquals(original,woodland.all)
        } finally {
            compose.runOnIdle { visible.value = false }
            check(coast.edit().clear().commit())
        }
    }
}
