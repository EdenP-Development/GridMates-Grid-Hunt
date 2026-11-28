package com.playereden.gridmates.gridhunt

import android.content.pm.ActivityInfo
import android.content.Context
import android.graphics.Bitmap
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Real screenshots from the running app on an Android 35 emulator.
 *
 * Captures are inspection evidence, not automated proof of visual quality.
 * End-state captures are earned by solving the actual 9+5 authored puzzles
 * through the live UI, not by seeding progress preferences. No owner's phone
 * or published puzzles are modified.
 */
@RunWith(AndroidJUnit4::class)
class VisualCaptureTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val target get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun capture(filename: String) {
        compose.waitForIdle()
        // Compose semantics can settle before the emulator's last rendered frame.
        // Give SurfaceFlinger a moment to present the correct navigation state;
        // this delay runs only in visual QA, never in the shipped game.
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        android.os.SystemClock.sleep(500L)
        val image = requireNotNull(
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        ) { "Emulator screenshot was unavailable" }
        // App-specific external files disappear when Gradle uninstalls the test
        // app. Save into public Pictures through MediaStore instead so adb can
        // still pull the evidence after connectedDebugAndroidTest completes.
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/GridHuntVisualQA")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = target.contentResolver
        val uri = requireNotNull(
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        ) { "Could not create MediaStore image $filename" }
        try {
            val output = requireNotNull(resolver.openOutputStream(uri)) {
                "Could not open output for $filename"
            }
            output.use { stream ->
                check(image.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                    "PNG write failed for $filename"
                }
            }
            val visible = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            check(resolver.update(uri, visible, null, null) == 1) {
                "Could not publish $filename"
            }
        } finally {
            image.recycle()
        }
    }

    private fun openQuickIfAtHome() {
        if (compose.onAllNodesWithTag("openQuickPlay").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("openQuickPlay").performScrollTo().performClick()
        }
    }

    private fun prepare() {
        // Explicitly initialize precision controls used by scripted visual playthroughs.
        check(target.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
            .edit().putBoolean("precise_selection", true).commit())
        listOf("gridhunt_first_bearings_v1", "gridhunt_reverse_v1",
            "gridhunt_treasure_checklist_v1", "gridhunt_identify_v1",
            "gridhunt_mixed_woodland_v1",
            "gridhunt_woodland_supplies_v1").forEach { name ->
            check(target.getSharedPreferences(name, Context.MODE_PRIVATE)
                .edit().clear().commit()) { "Could not reset test-only $name" }
        }
        compose.activityRule.scenario.recreate()
    }

    private fun dismissGuide() {
        if (compose.onAllNodesWithTag("dismissGridGuide").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("dismissGridGuide").performClick()
        }
    }

    private fun chooseSquare(reference: String) {
        require(reference.length == 4 && reference.all { it.isDigit() })
        val e = reference.substring(0, 2).toInt()
        val n = reference.substring(2, 4).toInt()
        // Each accepted answer clears selection; arrow movement starts from 2640.
        repeat((26 - e).coerceAtLeast(0)) {
            compose.onNodeWithContentDescription("Move selected square west").performClick()
        }
        repeat((e - 26).coerceAtLeast(0)) {
            compose.onNodeWithContentDescription("Move selected square east").performClick()
        }
        repeat((40 - n).coerceAtLeast(0)) {
            compose.onNodeWithContentDescription("Move selected square south").performClick()
        }
        repeat((n - 40).coerceAtLeast(0)) {
            compose.onNodeWithContentDescription("Move selected square north").performClick()
        }
        // The next clue/finish assertions verify that this answer advanced.
        compose.onNodeWithTag("checkChapter").performClick()
    }

    private fun enterSpotCode(reference: String) {
        compose.onNodeWithTag("reverseCodeInput").performTextInput(reference)
        compose.onNodeWithTag("checkReverse").performClick()
    }

    @Test fun captureExplorerHomeAdventureAndCustomShell() {
        prepare()
        compose.onNodeWithTag("openAdventure").assertExists()
        capture("16_explorer_desk_home.png")
        // Actual published route in MainActivity, not the separate preview activity.
        val woodland = com.playereden.gridhunt.core.PublishedWorlds.selection.destinations.single()
        val prefs = target.getSharedPreferences("gridhunt_world_selection_v1", Context.MODE_PRIVATE)
        val previous = prefs.all["world_id"]
        try {
            compose.onNodeWithTag("openAtlas").performScrollTo().performClick()
            compose.onNodeWithTag("explorerAtlas").assertExists()
            capture("30_published_atlas.png")
            compose.onNodeWithTag("atlasOpen_"+woodland.world.id).performScrollTo().performClick()
            compose.onNodeWithTag("regionGameplayHub").assertExists()
            capture("31_published_region.png")
            compose.onNodeWithTag("regionBack").performScrollTo().performClick()
            compose.onNodeWithTag("atlasBack").performScrollTo().performClick()
        } finally {
            val edit = prefs.edit()
            when(previous) {
                is String -> edit.putString("world_id",previous)
                is Int -> edit.putInt("world_id",previous)
                is Boolean -> edit.putBoolean("world_id",previous)
                is Long -> edit.putLong("world_id",previous)
                is Float -> edit.putFloat("world_id",previous)
                is Set<*> -> edit.putStringSet("world_id",previous.filterIsInstance<String>().toSet())
                else -> edit.remove("world_id")
            }
            check(edit.commit())
        }
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithTag("adventureRouteMap").assertExists()
        compose.onNodeWithTag("openAdventureChapter").assertExists()
        capture("17_adventure_journey.png")
        compose.onNodeWithTag("openSecondChapter").assertIsNotEnabled()
        capture("20_chapter_two_locked.png")
        compose.onNodeWithTag("adventureBack").performClick()
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customStart").assertExists()
        capture("18_custom_play_setup.png")
        compose.onNodeWithTag("customBack").performClick()
        compose.onNodeWithTag("openQuickPlay").performScrollTo().performClick()
        compose.onNodeWithTag("openIdentify").assertExists()
        capture("19_compact_quick_play.png")
        // Activity recreation preserves the current route. Return to Home explicitly.
        compose.onNodeWithTag("quickBack").performClick()
        check(target.getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
            .edit().putInt("step", 4).commit())
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithTag("openSecondChapter").assertIsEnabled()
        capture("21_chapter_two_unlocked.png")
        compose.onNodeWithTag("openSecondChapter").performClick()
        compose.onNodeWithText("The Lost Supplies").assertExists()
        capture("22_lost_supplies_opening.png")
    }

    @Test fun captureIntroTreasureChecklistAndEarnedFinish() {
        prepare()
        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").performScrollTo().performClick()
        compose.onNodeWithTag("treasureCodeInput").assertExists()
        capture("09_woodland_checklist.png")
        compose.onNodeWithTag("openWoodlandMapGuide").performClick()
        compose.onNodeWithTag("woodlandMapGuide").assertExists()
        capture("11_woodland_map_guide.png")
        compose.onNodeWithTag("dismissWoodlandMapGuide").performClick()
        listOf("2538", "2840", "2641", "2941").forEach { code ->
            compose.onNodeWithTag("treasureCodeInput").performTextInput(code)
            compose.onNodeWithTag("checkTreasure").performClick()
        }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("10_woodland_checklist_finish.png")
    }

    @Test fun captureIdentifyByGridOpeningAndFourCorrectAnswers() {
        prepare()
        openQuickIfAtHome()
        compose.onNodeWithTag("openIdentify").performScrollTo().performClick()
        compose.onNodeWithTag("identifyReference").assertExists()
        capture("12_identify_woodland.png")
        listOf("mushroom-nook", "buttercup-glade", "acorn-nook", "old-waystone")
            .forEach { id ->
                compose.onNodeWithTag("identifyOption_$id").performClick()
                compose.onNodeWithTag("identifyCheck").performClick()
            }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("13_identify_finish.png")
    }

    @Test fun captureMixedAdventureAndEarnedFinish() {
        prepare()
        openQuickIfAtHome()
        compose.onNodeWithTag("openExpedition").performScrollTo().performClick()
        compose.onNodeWithTag("mixedCheck").assertExists()
        capture("14_mixed_expedition.png")
        compose.onNodeWithTag("mixedJournal").performClick()
        compose.onNodeWithTag("journalEmpty").assertExists()
        compose.onNodeWithTag("journalPageCollection").assertExists()
        compose.onNodeWithTag("journalCurrent").assertExists()
        compose.onNodeWithTag("closeJournal").performClick()
        repeat(2) {
            compose.onNodeWithContentDescription("Expedition move west").performClick()
        }
        repeat(3) {
            compose.onNodeWithContentDescription("Expedition move south").performClick()
        }
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithTag("mixedJournal").performClick()
        compose.onNodeWithTag("journalEntry_locate-trailhead").assertExists()
        compose.onNodeWithTag("journalCurrent").assertExists()
        check(compose.onAllNodesWithTag("journalEntry_read-glow-a")
            .fetchSemanticsNodes().isEmpty())
        compose.onNodeWithTag("closeJournal").performClick()
        compose.onNodeWithTag("mixedCodeInput").performTextInput("2440")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithTag("mixedCodeInput").performTextInput("2538")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithTag("mixedOption_old-waystone").performClick()
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("15_mixed_expedition_finish.png")
        // Quick Play must not expose the campaign-only shortcut.
        check(compose.onAllNodesWithTag("nextExpedition").fetchSemanticsNodes().isEmpty())
        compose.onNodeWithTag("finishJournal").performClick()
        compose.onNodeWithTag("journalEntry_identify-waystone").assertExists()
        compose.onNodeWithTag("journalComplete").assertExists()
        compose.onNodeWithTag("closeJournal").performClick()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.onNodeWithTag("quickBack").performClick()
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithTag("openAdventureChapter").performClick()
        compose.onNodeWithTag("finishReplay").assertExists()
        compose.onNodeWithTag("nextExpedition").performClick()
        compose.onNodeWithText("The Lost Supplies").assertExists()
        compose.onNodeWithTag("mixedProgress").assertExists()
    }

    @Test fun captureLandscapePuzzleLayouts() {
        prepare()
        try {
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            compose.waitUntil(10_000) {
                target.resources.configuration.orientation ==
                    android.content.res.Configuration.ORIENTATION_LANDSCAPE
            }
        openQuickIfAtHome()
            compose.onNodeWithTag("openChapter").performScrollTo().performClick()
            dismissGuide()
            compose.onNodeWithTag("checkChapter").assertExists()
            capture("07_trail_landscape.png")
            compose.onNodeWithTag("chapterHome").performClick()
        openQuickIfAtHome()
            compose.onNodeWithTag("openReverse").performScrollTo().performClick()
            compose.onNodeWithTag("reverseCodeInput").assertExists()
            capture("08_golden_landscape.png")
        } finally {
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    @Test fun captureHomeBothModesBothLooksAndBothFinishCards() {
        prepare()
        compose.onNodeWithTag("homeIllustration").assertExists()
        capture("01_home.png")

        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuide()
        compose.onNodeWithTag("checkChapter").assertExists()
        capture("02_trail_treasures.png")
        compose.onNodeWithTag("mapLookToggle").assertDoesNotExist()
        capture("03_trail_unobstructed_map.png")

        compose.onNodeWithTag("chapterHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("reverseCodeInput").assertExists()
        capture("04_golden_spots.png")
        compose.onNodeWithTag("reverseHome").performClick()

        // Return to the live chapter and earn every finish state legitimately.
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performScrollTo().performClick()
        dismissGuide()
        val trails = listOf(
            listOf("2437", "2539", "2741"),
            listOf("2938", "2642", "2839"),
            listOf("2537", "2841", "2940")
        )
        trails.forEachIndexed { index, references ->
            references.forEach(::chooseSquare)
            compose.onNodeWithTag("puzzleFinishCard").assertExists()
            if (index < trails.lastIndex) {
                compose.onNodeWithTag("finishContinue").performClick()
            }
        }
        capture("05_treasure_finish.png")
        compose.onNodeWithTag("finishHome").performClick()

        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        listOf("2440", "2742", "2838", "2541", "2942").forEach(::enterSpotCode)
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("06_golden_finish.png")
    }
    @Test fun captureGeneratedCustomGameplayWithDefaultTouchControls() {
        val custom = target.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val precision = target.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
        val previous = precision.getBoolean("precise_selection", false)
        check(custom.edit().clear().commit())
        check(precision.edit().putBoolean("precise_selection", false).commit())
        try {
            compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
            listOf("grid", "gold", "identify", "checklist", "mixed").forEachIndexed { index, mode ->
                compose.onNodeWithTag("customMode_$mode").performScrollTo().performClick()
                compose.onNodeWithTag("customStart").performScrollTo().performClick()
                if (index > 0) compose.onNodeWithTag("customConfirmReplace").performClick()
                compose.onNodeWithTag("customMap").assertExists()
                when(mode) {
                    "grid" -> {
                        compose.onNodeWithContentDescription("Move selected square west").assertDoesNotExist()
                        compose.onNodeWithTag("checkChapter").assertIsNotEnabled()
                    }
                    "gold" -> compose.onNodeWithTag("customGoldInput").assertExists()
                    "identify" -> compose.onNodeWithTag("customIdentifyCheck").assertExists()
                    "checklist" -> compose.onNodeWithTag("customChecklistInput").assertExists()
                }
                capture("custom_${mode}_portrait.png")
                compose.onNodeWithTag("customBack").performClick()
            }
            compose.onNodeWithTag("customBack").performClick()
        } finally {
            precision.edit().putBoolean("precise_selection", previous).commit()
            custom.edit().clear().commit()
        }
    }

    @Test fun customRotationPreservesSessionAndEnteredCode() {
        val custom = target.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        check(custom.edit().clear().commit())
        try {
            compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
            compose.onNodeWithTag("customMode_gold").performScrollTo().performClick()
            compose.onNodeWithTag("customLength_3").performScrollTo().performClick()
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            val codes = requireNotNull(custom.getString("codes", null))
            val first = codes.substringBefore(",")
            compose.onNodeWithTag("customGoldInput").performTextInput(first)
            compose.onNodeWithTag("customGoldCheck").assertIsEnabled()
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
            compose.waitUntil(10_000) {
                target.resources.configuration.orientation ==
                    android.content.res.Configuration.ORIENTATION_LANDSCAPE
            }
            compose.waitForIdle()
            compose.onNodeWithTag("customMap").assertExists()
            compose.onNodeWithTag("customGoldCheck").assertIsEnabled()
            org.junit.Assert.assertEquals(codes, custom.getString("codes", null))
            org.junit.Assert.assertEquals(0, custom.getInt("step", -1))
            capture("custom_gold_landscape.png")
            compose.onNodeWithTag("customGoldCheck").performClick()
            compose.waitForIdle()
            org.junit.Assert.assertEquals(1, custom.getInt("step", -1))
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            compose.waitUntil(10_000) {
                target.resources.configuration.orientation ==
                    android.content.res.Configuration.ORIENTATION_PORTRAIT
            }
            compose.waitForIdle()
            compose.onNodeWithTag("customMap").assertExists()
            org.junit.Assert.assertEquals(codes, custom.getString("codes", null))
            org.junit.Assert.assertEquals(1, custom.getInt("step", -1))
            capture("custom_gold_rotated_portrait.png")
        } finally {
            compose.activityRule.scenario.onActivity {
                it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
            custom.edit().clear().commit()
        }
    }

}
