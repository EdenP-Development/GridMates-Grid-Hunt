package com.playereden.gridmates.gridhunt

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import com.playereden.gridhunt.core.ExpeditionVerb
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
    private fun capture(filename: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        android.os.SystemClock.sleep(500L)
        val bitmap = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        val resolver = instrumentation.targetContext.contentResolver
        var uri: android.net.Uri? = null
        try {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/GridHuntVisualQA")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val created = requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
            uri = created
            requireNotNull(resolver.openOutputStream(created)).use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            }
            check(resolver.update(created, ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }, null, null) == 1)
        } catch (failure: Throwable) {
            uri?.let { resolver.delete(it, null, null) }
            throw failure
        } finally {
            bitmap.recycle()
        }
    }


    @Test fun actualDiscoveryRefreshesSelectorAndCapturesMenus() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val run = WoodlandFieldSurveyCandidate.run
        val prefs = context.getSharedPreferences(run.saveName, Context.MODE_PRIVATE)
        val before = prefs.all.toMap()
        try {
            check(prefs.edit().clear().putInt("step", 0).commit())
            compose.onNodeWithTag("openAdventure").performScrollTo().performClick()
            capture("36_adventure_choice.png")
            compose.onNodeWithTag("expandedAdventure").performClick()
            compose.onNodeWithTag("expandedChapter0").assertExists()
            capture("37_expanded_adventure_selector.png")
            compose.onNodeWithTag("expandedChapter0").performScrollTo().performClick()
            val objective = run.chapter.objectives.first()
            when (objective.verb) {
                ExpeditionVerb.FIND_SQUARE -> {
                    fun move(direction: String) {
                        compose.onNodeWithContentDescription("Expedition move $direction").performClick()
                    }
                    repeat(8) { move("west") }
                    repeat(8) { move("south") }
                    repeat(objective.expectedCell.eastIndex - run.chapter.map.cells.minOf { it.eastIndex }) { move("east") }
                    repeat(objective.expectedCell.northIndex - run.chapter.map.cells.minOf { it.northIndex }) { move("north") }
                }
                ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK ->
                    compose.onNodeWithTag("mixedCodeInput").performTextReplacement(objective.expectedCell.reference)
                ExpeditionVerb.IDENTIFY_LANDMARK ->
                    compose.onNodeWithTag("mixedOption_" + objective.landmarkId).performClick()
            }
            compose.onNodeWithTag("mixedCheck").performClick()
            compose.waitUntil(10000) { prefs.getInt("step", -1) == 1 }
            compose.onNodeWithTag("mixedProgress").assertTextEquals("1 of 12 discoveries recorded")
            compose.onNodeWithTag("mixedHome").performClick()
            compose.onNodeWithTag("expandedChapter0").assert(hasText("1 of 12 discoveries", substring = true) or hasAnyDescendant(hasText("1 of 12 discoveries")))
            capture("38_expanded_selector_progress.png")
            compose.onNodeWithTag("expandedChapter5").performScrollTo()
            capture("39_expanded_selector_coast.png")
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
