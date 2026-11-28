package com.playereden.gridmates.gridhunt

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.WoodlandFieldSurveyCandidate
import org.junit.Assert.assertEquals
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

    @Test fun selectorRefreshesProgressAndSurvivesActivityRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val run = WoodlandFieldSurveyCandidate.run
        val prefs = context.getSharedPreferences(run.saveName, Context.MODE_PRIVATE)
        val before = prefs.all.toMap()
        val protected = listOf("gridhunt_campaign_earned_v1", "gridhunt_campaign_earned_preview_v1")
            .map { context.getSharedPreferences(it, Context.MODE_PRIVATE) }
        val ledgers = protected.map { it.all.toMap() }
        try {
            check(prefs.edit().clear().putInt("step", 3).commit())
            compose.onNodeWithTag("openAdventure").performScrollTo().performClick()
            compose.onNodeWithTag("expandedAdventure").performClick()
            compose.onNodeWithText("3 of 12 discoveries").assertExists()
            compose.onNodeWithTag("expandedChapter0").performScrollTo().performClick()
            compose.onNodeWithTag("mixedProgress").assertTextEquals("3 of 12 discoveries recorded")
            compose.activityRule.scenario.recreate()
            compose.onNodeWithTag("mixedProgress").assertTextEquals("3 of 12 discoveries recorded")
            compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            compose.onNodeWithTag("expandedBack").assertExists()
            check(prefs.edit().putInt("step", 4).commit())
            compose.activityRule.scenario.recreate()
            compose.onNodeWithText("4 of 12 discoveries").assertExists()
            compose.activityRule.scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            compose.onNodeWithTag("expandedAdventure").assertExists()
            assertEquals(ledgers, protected.map { it.all.toMap() })
        } finally {
            val edit = prefs.edit().clear()
            before.forEach { (k,v) ->
                when(v) {
                    is Int -> edit.putInt(k,v)
                    is String -> edit.putString(k,v)
                    is Boolean -> edit.putBoolean(k,v)
                    is Long -> edit.putLong(k,v)
                    is Float -> edit.putFloat(k,v)
                    is Set<*> -> edit.putStringSet(k,v.filterIsInstance<String>().toSet())
                }
            }
            check(edit.commit())
        }
    }
}
