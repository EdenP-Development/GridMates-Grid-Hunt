package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.ExplorerAcademy
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AcademyTrainingTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun learnPracticeResumeAndCompleteWoodlandExercise() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val prefs = context.getSharedPreferences(ExplorerAcademy.saveName, Context.MODE_PRIVATE)
        check(prefs.edit().clear().commit())
        val campaign = context.getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
        val before = campaign.all.toMap()
        compose.onNodeWithTag("openAcademy").performScrollTo().performClick()
        compose.onNodeWithTag("academyChoice_0").performScrollTo().performClick()
        compose.onNodeWithTag("academyFeedback").assertExists()
        assertEquals(emptySet<String>(), prefs.getStringSet("earned", emptySet()))
        compose.onNodeWithTag("academyChoice_2").performScrollTo().performClick()
        compose.onNodeWithTag("academyChoice_0").performScrollTo().performClick()
        compose.onNodeWithTag("academyGrid").assertExists()
        compose.onNodeWithTag("academyChoice_1").performScrollTo().performClick()
        compose.onNodeWithTag("academyCheckSquare").assertIsNotEnabled()
        compose.onNodeWithTag("academyCell_2437").performScrollTo().performClick()
        compose.onNodeWithTag("academyCheckSquare").performScrollTo().performClick()
        compose.onNodeWithTag("academyCell_2740").performScrollTo().performClick()
        compose.onNodeWithTag("academyCheckSquare").performScrollTo().performClick()
        compose.onNodeWithTag("academyWoodlandMap").assertExists()
        compose.onNodeWithTag("academyHome").performScrollTo().performClick()
        compose.onNodeWithTag("openAcademy").performScrollTo().performClick()
        compose.onNodeWithTag("academyWoodlandMap").assertExists()
        compose.onNodeWithTag("academyZoomIn").performScrollTo().performClick()
        compose.onNodeWithTag("academyFit").performScrollTo().performClick()
        compose.onNodeWithTag("academyGuide").performScrollTo().performClick()
        compose.onNodeWithTag("dismissWoodlandMapGuide").performClick()
        compose.onNodeWithTag("academyMoveWest").performScrollTo().performClick()
        repeat(2) {
            compose.onNodeWithTag("academyMoveSouth").performScrollTo().performClick()
        }
        compose.onNodeWithTag("academyCheckSquare").assertIsEnabled()
        compose.onNodeWithTag("academyCheckSquare").performScrollTo().performClick()
        compose.onNodeWithText("Reading the landscape").assertExists()
        repeat(2) { compose.onNodeWithTag("academyMoveEast").performScrollTo().performClick() }
        compose.onNodeWithTag("academyCheckSquare").performScrollTo().performClick()
        compose.onNodeWithText("Following the trail").assertExists()
        compose.onNodeWithTag("academyRouteReset").performScrollTo().performClick()
        repeat(2) { compose.onNodeWithTag("academyMoveEast").performScrollTo().performClick() }
        compose.onNodeWithTag("academyMoveNorth").performScrollTo().performClick()
        compose.onNodeWithTag("academyCheckSquare").performScrollTo().performClick()
        compose.onNodeWithText("Thinking like an explorer").assertExists()
        repeat(3) { compose.onNodeWithTag("academyMoveEast").performScrollTo().performClick() }
        compose.onNodeWithTag("academyMoveNorth").performScrollTo().performClick()
        compose.onNodeWithTag("academyCheckSquare").performScrollTo().performClick()
        compose.onNodeWithTag("academyPracticeComplete").assertExists()
        assertEquals(9, prefs.getStringSet("earned", emptySet())?.size)
        assertEquals(before, campaign.all.toMap())
        compose.onNodeWithTag("academyReplay").performScrollTo().performClick()
        compose.onNodeWithText("Finding your bearings").assertExists()
        assertEquals(emptySet<String>(), prefs.getStringSet("earned", emptySet()))
    }
}
