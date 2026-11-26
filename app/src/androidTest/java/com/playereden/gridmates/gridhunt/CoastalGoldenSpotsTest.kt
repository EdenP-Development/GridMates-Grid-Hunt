package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.SurveyMarkerCatalog
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Exercise actual gameplay without publishing the coastal destination. */
class CoastalGoldenSpotsTest {
    @get:Rule val compose = createComposeRule()

    @Test fun coastalPlaythroughPersistsAndNeverWritesWoodlandProgress() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val coast = context.getSharedPreferences("gridhunt_reverse_whispering-coast-01_r1_v1", Context.MODE_PRIVATE)
        val woodland = context.getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
        val originalWoodland = woodland.all.toMap()
        check(coast.edit().clear().commit())
        val visible = mutableStateOf(true)
        val look = mutableStateOf(MapLook.SURVEY_TABLE)
        compose.setContent {
            if (visible.value) ReversePracticeScreen(
                onBack = { visible.value = false },
                mapLook = look.value, onLook = { look.value = it },
                hapticsEnabled = false, catalog = SurveyMarkerCatalog.coastalDraft
            )
        }
        fun step(expected: Int) {
            compose.waitUntil(10000) { coast.getInt("step", -1) == expected }
        }
        fun submit(code: String) {
            compose.onNodeWithTag("reverseCodeInput").performTextReplacement(code)
            compose.onNodeWithTag("checkReverse").performClick()
        }
        try {
            step(0)
            submit("0000")
            compose.onNodeWithText("Not quite!", substring = true).assertExists()
            step(0)
            submit("4256")
            step(1)
            compose.onNodeWithTag("reverseHome").performClick()
            compose.runOnIdle { visible.value = true }
            compose.onNodeWithText("GOLDEN SPOT 2 OF 5").assertExists()
            compose.runOnIdle { look.value = MapLook.values().first { it != MapLook.SURVEY_TABLE } }
            listOf("4354", "4453", "4252", "4556").forEachIndexed { index, code ->
                submit(code)
                step(index + 2)
            }
            compose.onNodeWithTag("puzzleFinishCard").assertExists()
            compose.onNodeWithTag("reverseHome").performClick()
            compose.runOnIdle { visible.value = true }
            compose.onNodeWithTag("puzzleFinishCard").assertExists()
            compose.onNodeWithText("Find them again").performClick()
            step(0)
            compose.onNodeWithText("GOLDEN SPOT 1 OF 5").assertExists()
            compose.runOnIdle { visible.value = false }
            assertEquals(originalWoodland, woodland.all)
        } finally {
            compose.runOnIdle { visible.value = false }
            check(coast.edit().clear().commit())
        }
    }
}
