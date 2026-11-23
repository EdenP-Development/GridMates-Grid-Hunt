package com.playereden.gridmates.gridhunt

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Compiles in CI. Actual instrumentation execution requires an Android emulator/device. */
@RunWith(AndroidJUnit4::class)
class HomeNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun dismissGuideIfShown() {
        if (compose.onAllNodesWithTag("dismissGridGuide").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("dismissGridGuide").performClick()
        }
    }

    @Test fun hubOffersBothPlayableModes() {
        compose.onNodeWithText("Grid Hunt").assertExists()
        compose.onNodeWithTag("openChapter").assertExists()
        compose.onNodeWithTag("openReverse").assertExists()
        compose.onNodeWithText("VISUAL PLAYTEST").assertExists()
    }

    @Test fun journeyCanEnterAndLeaveEachMode() {
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("First Bearings").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()
        compose.onNodeWithTag("openReverse").performClick()
        compose.onNodeWithText("Look Again").assertExists()
        compose.onNodeWithTag("reverseHome").performClick()
        compose.onNodeWithTag("openChapter").assertExists()
    }

    @Test fun mapLookSwitchKeepsBothPuzzlesOpen() {
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithTag("mapLookToggle").assertExists().performClick()
        compose.onNodeWithText("First Bearings").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()

        compose.onNodeWithTag("openReverse").performClick()
        compose.onNodeWithTag("mapLookToggle").assertExists().performClick()
        compose.onNodeWithText("Look Again").assertExists()
    }

    @Test fun visualMapChoiceIsReachable() {
        compose.onNodeWithTag("style_SURVEY_TABLE").performScrollTo().performClick()
        compose.onNodeWithTag("style_FIELD_GUIDE").assertExists()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("First Bearings").assertExists()
    }
}
