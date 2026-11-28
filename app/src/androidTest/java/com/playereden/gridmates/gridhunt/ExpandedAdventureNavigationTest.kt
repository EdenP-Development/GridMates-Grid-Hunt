package com.playereden.gridmates.gridhunt

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExpandedAdventureNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun allSixExpandedChaptersOpenAndReturnToSelector() {
        compose.onNodeWithTag("openAdventure").performScrollTo().performClick()
        compose.onNodeWithTag("classicAdventure").assertExists()
        compose.onNodeWithTag("expandedAdventure").performClick()
        repeat(6) { index ->
            compose.onNodeWithTag("expandedChapter" + index).performScrollTo().performClick()
            compose.onNodeWithTag("mixedProgress").assertExists()
            compose.onNodeWithTag("mixedHome").performClick()
            compose.onNodeWithTag("expandedBack").assertExists()
        }
        compose.onNodeWithTag("expandedBack").performClick()
        compose.onNodeWithTag("classicAdventure").performClick()
        compose.onNodeWithTag("adventureRouteMap").assertExists()
        compose.onNodeWithTag("adventureBack").performClick()
        compose.onNodeWithTag("openAdventure").assertExists()
    }
}
