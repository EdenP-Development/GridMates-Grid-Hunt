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

/** Executed on Android 35 emulator in PR CI; physical phone acceptance is separate. */
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
        compose.onNodeWithTag("homeIllustration").assertExists()
        compose.onNodeWithText("Choose your next puzzle").assertExists()
    }

    @Test fun journeyCanEnterAndLeaveEachMode() {
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("Trail Treasures").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithText("Golden Spots").assertExists()
        compose.onNodeWithTag("reverseHome").performClick()
        compose.onNodeWithTag("openChapter").assertExists()
    }

    @Test fun mapLookSwitchKeepsBothPuzzlesOpen() {
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithTag("mapLookToggle").assertExists().performClick()
        compose.onNodeWithText("Trail Treasures").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()

        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("mapLookToggle").assertExists().performClick()
        compose.onNodeWithText("Golden Spots").assertExists()
    }

    @Test fun visualMapChoiceIsReachable() {
        compose.onNodeWithTag("appearanceToggle").performScrollTo().performClick()
        compose.onNodeWithTag("style_SURVEY_TABLE").performScrollTo().performClick()
        compose.onNodeWithTag("style_FIELD_GUIDE").assertExists()
        compose.onNodeWithTag("openChapter").performScrollTo().performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("Trail Treasures").assertExists()
    }
}
