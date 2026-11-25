package com.playereden.gridmates.gridhunt

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.platform.app.InstrumentationRegistry
import android.content.Context
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Executed on Android 35 emulator in PR CI; physical phone acceptance is separate. */
@RunWith(AndroidJUnit4::class)
class HomeNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private fun openQuickIfAtHome() {
        if (compose.onAllNodesWithTag("openQuickPlay").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("openQuickPlay").performScrollTo().performClick()
        }
    }

    private fun dismissGuideIfShown() {
        if (compose.onAllNodesWithTag("dismissGridGuide").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("dismissGridGuide").performClick()
        }
    }

    @Test fun hubOffersBothPlayableModes() {
        compose.onNodeWithText("Grid Hunt").assertExists()
        compose.onNodeWithTag("openAdventure").assertExists()
        compose.onNodeWithTag("openQuickPlay").assertExists()
        compose.onNodeWithTag("openCustomPlay").assertExists()
        compose.onNodeWithTag("homeIllustration").assertExists()
        compose.onNodeWithTag("homeAdventureProgress").assertExists()
        compose.onNodeWithText("Little Woodland").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openIdentify").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openExpedition").assertExists()
        compose.onNodeWithText("Choose your puzzle").assertExists()
    }

    @Test fun threePillarNavigationAndAdventureChapterAreReal() {
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithText("The Missing Field Notes").assertExists()
        compose.onNodeWithTag("adventureProgress").assertExists()
        compose.onNodeWithTag("adventureRouteMap").assertExists()
        compose.onNodeWithTag("openAdventureChapter").performClick()
        compose.onNodeWithText("The Missing Field Notes").assertExists()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.onNodeWithTag("adventureBack").performClick()
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customStart").assertExists()
        compose.onNodeWithTag("customLength_3").assertExists()
        compose.onNodeWithTag("customBack").performClick()
        compose.onNodeWithTag("openAdventure").assertExists()
    }

    @Test fun journeyCanEnterAndLeaveEachMode() {
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("Trail Treasures").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithText("Golden Spots").assertExists()
        compose.onNodeWithTag("reverseHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").assertExists()
    }

    @Test fun retiredMapToggleDoesNotObstructPuzzles() {
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithTag("mapLookToggle").assertDoesNotExist()
        compose.onNodeWithText("Trail Treasures").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()

        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("mapLookToggle").assertDoesNotExist()
        compose.onNodeWithText("Golden Spots").assertExists()
    }

    @Test fun firstTreasureCanBeFoundAndSurvivesRelaunch() {
        // Isolate this test from prior emulator navigation / completion tests.
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_first_bearings_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("CLUE 1 OF 3").assertExists()
        compose.onNodeWithTag("trailModeTitle").assertExists()
        // The tagged trail caption must exist. Rendered ellipsis/visibility is
        // reviewed from actual emulator + phone screenshots, not text semantics.
        compose.onNodeWithTag("trailLocation").assertExists()

        if (compose.onAllNodesWithTag("huntToolOptions").fetchSemanticsNodes().isNotEmpty()) {
            compose.onNodeWithTag("huntToolOptions").performClick()
            val preferences = InstrumentationRegistry.getInstrumentation().targetContext
                .getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
            if (!preferences.getBoolean("precise_selection", false))
                compose.onNodeWithTag("huntPreciseSelection").performClick()
            compose.onNodeWithTag("huntCloseTools").performClick()
        }
        // Default arrow origin is 2640; 2 west and 3 south yields canonical 2437.
        repeat(2) {
            compose.onNodeWithContentDescription("Move selected square west").performClick()
        }
        repeat(3) {
            compose.onNodeWithContentDescription("Move selected square south").performClick()
        }
        compose.onNodeWithText("YOUR PICK  2437", substring = true).assertExists()
        compose.onNodeWithText("Try this square").performClick()
        compose.onNodeWithText("FOUND 1 OF 3").assertExists()
        compose.onNodeWithText("CLUE 2 OF 3").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("CLUE 2 OF 3").assertExists()
    }

    @Test fun goldenSpotCodeAdvancesAndSurvivesRelaunch() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithText("GOLDEN SPOT 1 OF 5").assertExists()
        // Golden spot A is at E=24650, N=40620, so its containing square is 2440.
        compose.onNodeWithTag("reverseCodeInput").performTextInput("2440")
        compose.onNodeWithTag("checkReverse").performClick()
        compose.onNodeWithText("GOLDEN SPOT 2 OF 5").assertExists()
        compose.onNodeWithText("Next golden spot! Find its dot and read the square.").assertExists()
        compose.onNodeWithText("0000").assertDoesNotExist()
        compose.onNodeWithTag("reverseHome").performClick()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithText("GOLDEN SPOT 2 OF 5").assertExists()
    }

    @Test fun completedTrailCanReturnHomeWithoutForcingReplay() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_first_bearings_v1", Context.MODE_PRIVATE)
            .edit().putInt("hunt_index", 2).putInt("objective_index", 3)
            .putBoolean("guide_seen", true).commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithText("Woodland adventure complete!").assertExists()
        compose.onNodeWithTag("finishHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishContinue").performClick()
        compose.onNodeWithText("CLUE 1 OF 3").assertExists()
    }

    @Test fun goldenSpotFinishCanReturnHomeAndReplayExplicitly() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
            .edit().putInt("step", 5).commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithText("Every golden spot found!").assertExists()
        compose.onNodeWithTag("finishHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishContinue").performClick()
        compose.onNodeWithText("GOLDEN SPOT 1 OF 5").assertExists()
    }

    @Test fun treasureChecklistMarksCorrectLandmarkAndResumes() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_treasure_checklist_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").performScrollTo().performClick()
        compose.onNodeWithText("0 of 4 woodland discoveries").assertExists()
        compose.onNodeWithTag("treasureItem_mushroom-nook").assertExists()
        compose.onNodeWithTag("treasureCodeInput").performTextInput("2539")
        compose.onNodeWithTag("checkTreasure").performClick()
        // A wrong number must not cross off the target.
        compose.onNodeWithText("0 of 4 woodland discoveries").assertExists()
        compose.onNodeWithTag("treasureCodeInput").performTextReplacement("2538")
        compose.onNodeWithTag("checkTreasure").performClick()
        compose.onNodeWithText("1 of 4 woodland discoveries").assertExists()

        compose.onNodeWithTag("treasureItem_acorn-nook").performClick()
        compose.onNodeWithTag("treasureCodeInput").performTextInput("2641")
        compose.onNodeWithTag("checkTreasure").performClick()
        compose.onNodeWithText("2 of 4 woodland discoveries").assertExists()
        compose.onNodeWithTag("treasureHome").performClick()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").performScrollTo().performClick()
        compose.onNodeWithText("2 of 4 woodland discoveries").assertExists()
    }

    @Test fun mapGuideShowsRealSymbolsWithoutSpoilersOrProgressReset() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_treasure_checklist_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").performScrollTo().performClick()
        compose.onNodeWithTag("openWoodlandMapGuide").performClick()
        compose.onNodeWithTag("woodlandMapGuide").assertExists()
        listOf("mushroom-nook", "buttercup-glade", "acorn-nook", "old-waystone")
            .forEach { id ->
                compose.onNodeWithTag("guideSymbol_$id").assertExists()
            }
        compose.onNodeWithText("Creek").assertExists()
        compose.onNodeWithText("Trail").assertExists()
        // The guide teaches symbol *types*; it cannot reveal the secret square.
        compose.onNodeWithText("2538").assertDoesNotExist()
        compose.onNodeWithTag("dismissWoodlandMapGuide").performClick()
        compose.onNodeWithText("0 of 4 woodland discoveries").assertExists()
        compose.onNodeWithTag("treasureItem_mushroom-nook").assertExists()
        compose.onNodeWithTag("activeTreasurePreview").assertExists()
        compose.onNodeWithContentDescription("Mushroom Nook map symbol").assertExists()
        compose.onNodeWithTag("treasureCodeInput").performTextInput("2538")
        compose.onNodeWithTag("checkTreasure").performClick()
        compose.onNodeWithText("1 of 4 woodland discoveries").assertExists()
    }

    @Test fun treasureChecklistHasAnExplicitReplayAfterFourFinds() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_treasure_checklist_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").performScrollTo().performClick()
        listOf("2538", "2840", "2641", "2941").forEachIndexed { index, ref ->
            compose.onNodeWithTag("treasureCodeInput").performTextInput(ref)
            compose.onNodeWithTag("checkTreasure").performClick()
            compose.onNodeWithText("${index + 1} of 4 woodland discoveries").assertExists()
        }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openTreasure").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishContinue").performClick()
        compose.onNodeWithText("0 of 4 woodland discoveries").assertExists()
    }

    @Test fun identifyGridRejectsWrongPictureThenResumesWithNextQuestion() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_identify_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openIdentify").performScrollTo().performClick()
        compose.onNodeWithText("0 of 4 identified").assertExists()
        compose.onNodeWithTag("identifyReference").assertExists()
        compose.onNodeWithTag("identifyOption_buttercup-glade").performClick()
        compose.onNodeWithTag("identifyCheck").performClick()
        compose.onNodeWithText("0 of 4 identified").assertExists()
        compose.onNodeWithTag("identifyOption_mushroom-nook").performClick()
        compose.onNodeWithTag("identifyCheck").performClick()
        compose.onNodeWithText("1 of 4 identified").assertExists()
        compose.onNodeWithText("2840").assertExists()

        compose.onNodeWithTag("identifyGuide").performClick()
        compose.onNodeWithTag("woodlandMapGuide").assertExists()
        compose.onNodeWithTag("dismissWoodlandMapGuide").performClick()
        compose.onNodeWithText("1 of 4 identified").assertExists()

        compose.onNodeWithTag("identifyHome").performClick()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openIdentify").performScrollTo().performClick()
        compose.onNodeWithText("1 of 4 identified").assertExists()
        compose.onNodeWithText("2840").assertExists()
    }

    @Test fun identifyGridFourCorrectPicturesEarnFinishAndExplicitReplay() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_identify_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openIdentify").performScrollTo().performClick()

        listOf("mushroom-nook", "buttercup-glade", "acorn-nook", "old-waystone")
            .forEachIndexed { index, id ->
                compose.onNodeWithTag("identifyOption_$id").performClick()
                compose.onNodeWithTag("identifyCheck").performClick()
                compose.onNodeWithText("${index + 1} of 4 identified").assertExists()
            }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishHome").performClick()
        openQuickIfAtHome()
        compose.onNodeWithTag("openIdentify").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishContinue").performClick()
        compose.onNodeWithText("0 of 4 identified").assertExists()
        compose.onNodeWithText("2538").assertExists()
    }

    private fun playFirstMixedSquare() {
        repeat(2) {
            compose.onNodeWithContentDescription("Expedition move west").performClick()
        }
        repeat(3) {
            compose.onNodeWithContentDescription("Expedition move south").performClick()
        }
        compose.onNodeWithTag("mixedSquarePick").assertExists()
        compose.onNodeWithTag("mixedCheck").performClick()
    }

    @Test fun earnedChapterStepSurvivesImmediateHomeWithoutChangingOtherChapter() {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val first = ctx.getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
        val second = ctx.getSharedPreferences("gridhunt_woodland_supplies_v1", Context.MODE_PRIVATE)
        check(first.edit().clear().commit())
        check(second.edit().clear().commit())
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithTag("openAdventureChapter").performClick()
        playFirstMixedSquare()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.onNodeWithText("1 of 4 pages recovered").assertExists()
        org.junit.Assert.assertEquals(1, first.getInt("step", -1))
        org.junit.Assert.assertEquals(-1, second.getInt("step", -1))
        compose.onNodeWithTag("openAdventureChapter").performClick()
        compose.onNodeWithText("1 of 4 pages found").assertExists()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.onNodeWithTag("openSecondChapter").assertIsNotEnabled()
    }

    @Test fun mixedChapterValidatesEachVerbAndPreservesProgressAfterReopen() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openExpedition").performScrollTo().performClick()
        compose.onNodeWithText("0 of 4 pages found").assertExists()

        // Stage 1 needs a MAP square selection, not an object tile.
        playFirstMixedSquare()
        compose.onNodeWithText("1 of 4 pages found").assertExists()

        // Stage 2 reads the actual golden dot; wrong guesses do not advance.
        compose.onNodeWithTag("mixedCodeInput").performTextInput("2538")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("1 of 4 pages found").assertExists()
        compose.onNodeWithTag("mixedCodeInput").performTextReplacement("2440")
        compose.onNodeWithTag("mixedCodeInput").performImeAction()
        compose.onNodeWithText("2 of 4 pages found").assertExists()

        compose.onNodeWithTag("mixedHome").performClick()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openExpedition").performScrollTo().performClick()
        compose.onNodeWithText("2 of 4 pages found").assertExists()

        // Stage 3 uses the actual Mushroom Nook artwork and its square.
        compose.onNodeWithTag("mixedCodeInput").performTextInput("2538")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("3 of 4 pages found").assertExists()

        // Stage 4 asks WHAT lives at 2941. Code entry is no longer valid.
        compose.onNodeWithTag("mixedOption_mushroom-nook").performClick()
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("3 of 4 pages found").assertExists()
        compose.onNodeWithTag("mixedOption_old-waystone").performClick()
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("4 of 4 pages found").assertExists()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
    }

    @Test fun secondChapterStaysLockedUntilFirstIsFinishedThenCanBePlayed() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
            .edit().putInt("step", 0).commit()
        context.getSharedPreferences("gridhunt_woodland_supplies_v1", Context.MODE_PRIVATE)
            .edit().clear().commit()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithTag("openSecondChapter").assertExists()
        compose.onNodeWithTag("openSecondChapter").assertIsNotEnabled()
        compose.onNodeWithTag("adventureBack").performClick()

        context.getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
            .edit().putInt("step", 4).commit()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithTag("openSecondChapter").assertIsEnabled().performClick()
        compose.onNodeWithText("The Lost Supplies").assertExists()

        compose.onNodeWithTag("mixedCodeInput").performTextInput("2840")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("0 of 4 clues solved").assertExists()
        compose.onNodeWithTag("mixedCodeInput").performTextReplacement("2742")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("1 of 4 clues solved").assertExists()
        compose.onNodeWithTag("mixedCodeInput").performTextInput("2840")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("2 of 4 clues solved").assertExists()
        compose.onNodeWithTag("mixedOption_acorn-nook").performClick()
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithText("3 of 4 clues solved").assertExists()
        compose.onNodeWithTag("mixedCodeInput").performTextInput("2941")
        compose.onNodeWithTag("mixedCheck").performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.onNodeWithTag("secondChapterStatus").assertExists()
        compose.onNodeWithTag("openSecondChapter").assertIsEnabled()
    }

    @Test fun mixedChapterFinishRemainsUntilExplicitReplay() {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
            .edit().putInt("step", 4).commit()
        compose.activityRule.scenario.recreate()

        openQuickIfAtHome()
        compose.onNodeWithTag("openExpedition").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.activityRule.scenario.recreate()
        openQuickIfAtHome()
        compose.onNodeWithTag("openExpedition").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishContinue").performClick()
        compose.onNodeWithText("0 of 4 pages found").assertExists()
    }

    @Test fun legacyMapChoiceIsRetiredButPlayOptionsRemain() {
        openQuickIfAtHome()
        compose.onNodeWithTag("appearanceToggle").performScrollTo().performClick()
        compose.onNodeWithTag("style_SURVEY_TABLE").assertDoesNotExist()
        compose.onNodeWithTag("style_FIELD_GUIDE").assertDoesNotExist()
        compose.onNodeWithText("Play options").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performScrollTo().performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("Trail Treasures").assertExists()
    }
    @Test fun customHuntGeneratesUniqueTargetsAndResumesWithoutChangingCampaign() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val custom = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val campaign = context.getSharedPreferences("gridhunt_mixed_woodland_v1", Context.MODE_PRIVATE)
        val before = campaign.all.toMap()
        check(custom.edit().clear().commit())
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customLength_6").performScrollTo().performClick()
        compose.onNodeWithTag("customStart").performScrollTo().performClick()
        compose.onNodeWithTag("customMap").assertExists()
        val codes = custom.getString("codes", "")!!.split(",")
        org.junit.Assert.assertEquals(6, codes.size)
        org.junit.Assert.assertEquals(6, codes.toSet().size)
        org.junit.Assert.assertTrue(codes.all { it.length == 4 && it.take(2).toInt() in 24..29 && it.takeLast(2).toInt() in 37..42 })
        compose.onNodeWithTag("customBack").performClick()
        compose.onNodeWithTag("customBack").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customResume").performScrollTo().performClick()
        compose.onNodeWithTag("customMap").assertExists()
        org.junit.Assert.assertEquals(codes.joinToString(","), custom.getString("codes", ""))
        org.junit.Assert.assertEquals(before, campaign.all.toMap())
    }

    @Test fun customHuntCompletionAndReplacementProtection() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val custom = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val precision = context.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
        val oldPrecision = precision.getBoolean("precise_selection", false)
        val authored = listOf("gridhunt_first_bearings_v1", "gridhunt_reverse_v1",
            "gridhunt_mixed_woodland_v1", "gridhunt_woodland_supplies_v1",
            "gridhunt_academy_exploration_v1").map { context.getSharedPreferences(it, Context.MODE_PRIVATE) }
        val before = authored.map { it.all.toMap() }
        check(custom.edit().clear().commit())
        check(precision.edit().putBoolean("precise_selection", true).commit())
        try {
            compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
            compose.onNodeWithTag("customLength_3").performScrollTo().performClick()
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            compose.onNodeWithTag("customMap").assertExists()
            val codes = custom.getString("codes", "")!!.split(",")
            org.junit.Assert.assertEquals(3, codes.size)
            compose.onNodeWithTag("customBack").performClick()
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            compose.onNodeWithTag("customCancelReplace").performClick()
            org.junit.Assert.assertEquals(codes.joinToString(","), custom.getString("codes", ""))
            compose.onNodeWithTag("customResume").performScrollTo().performClick()
            codes.forEachIndexed { index, code ->
                val e = code.take(2).toInt()
                val n = code.takeLast(2).toInt()
                // Select via the optional precision controls, including the origin itself.
                val startE = if (e == 26 && n == 40) 25 else 26
                if (startE == 25) compose.onNodeWithContentDescription("Move selected square west").performClick()
                repeat((startE-e).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square west").performClick() }
                repeat((e-startE).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square east").performClick() }
                repeat((40-n).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square south").performClick() }
                repeat((n-40).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square north").performClick() }
                compose.onNodeWithTag("checkChapter").performClick()
                compose.waitForIdle()
                org.junit.Assert.assertEquals(index+1, custom.getInt("step",0))
            }
            compose.onNodeWithTag("puzzleFinishCard").assertExists()
            compose.onNodeWithTag("finishHome").performClick()
            compose.onNodeWithTag("openCustomPlay").assertExists()
            compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
            compose.onNodeWithTag("customResume").assertDoesNotExist()
            org.junit.Assert.assertEquals(before, authored.map { it.all.toMap() })
        } finally {
            precision.edit().putBoolean("precise_selection", oldPrecision).commit()
        }
    }

    @Test fun generatedGoldenSpotsCanResumeAndComplete() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val custom = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val reverse = context.getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
        val before = reverse.all.toMap()
        check(custom.edit().clear().commit())
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customMode_gold").performScrollTo().performClick()
        compose.onNodeWithTag("customLength_3").performScrollTo().performClick()
        compose.onNodeWithTag("customStart").performScrollTo().performClick()
        compose.onNodeWithTag("customGoldInput").assertExists()
        val codes = custom.getString("codes", "")!!.split(",")
        org.junit.Assert.assertEquals("gold", custom.getString("mode", ""))
        compose.onNodeWithTag("customGoldInput").performTextInput("0000")
        compose.onNodeWithTag("customGoldCheck").performClick()
        org.junit.Assert.assertEquals(0, custom.getInt("step", 0))
        compose.onNodeWithTag("customGoldInput").performTextReplacement(codes.first())
        compose.onNodeWithTag("customGoldCheck").performClick()
        compose.onNodeWithTag("customBack").performClick()
        compose.onNodeWithTag("customBack").performClick()
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customMode_grid").performScrollTo().performClick()
        compose.onNodeWithTag("customResume").performScrollTo().performClick()
        codes.drop(1).forEach { code ->
            compose.onNodeWithTag("customGoldInput").performTextReplacement(code)
            compose.onNodeWithTag("customGoldCheck").performClick()
        }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        org.junit.Assert.assertEquals(3, custom.getInt("step", 0))
        org.junit.Assert.assertEquals(before, reverse.all.toMap())
    }

    @Test fun generatedLandmarkModesCompleteWithoutChangingAuthoredSaves() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val custom = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val authored = listOf("gridhunt_treasure_checklist_v1", "gridhunt_woodland_identify_v1")
            .map { context.getSharedPreferences(it, Context.MODE_PRIVATE) }
        val before = authored.map { it.all.toMap() }
        check(custom.edit().clear().commit())
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customMode_identify").performScrollTo().performClick()
        compose.onNodeWithTag("customStart").performScrollTo().performClick()
        val ids = mapOf("2538" to "mushroom-nook", "2840" to "buttercup-glade",
            "2641" to "acorn-nook", "2941" to "old-waystone")
        val codes = custom.getString("codes", "")!!.split(",")
        org.junit.Assert.assertEquals(3, codes.toSet().size)
        codes.forEach { code ->
            compose.onNodeWithTag("customIdentify_${ids.getValue(code)}").performClick()
            compose.onNodeWithTag("customIdentifyCheck").performClick()
        }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        compose.onNodeWithTag("finishHome").performClick()
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customMode_checklist").performScrollTo().performClick()
        compose.onNodeWithTag("customStart").performScrollTo().performClick()
        val checklist = custom.getString("codes", "")!!.split(",")
        org.junit.Assert.assertEquals(3, checklist.toSet().size)
        checklist.forEach { code ->
            compose.onNodeWithTag("customChecklistItem_${ids.getValue(code)}").assertExists()
            compose.onNodeWithTag("customChecklistInput").performTextReplacement(code)
            compose.onNodeWithTag("customChecklistCheck").performClick()
        }
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        org.junit.Assert.assertEquals(3, custom.getInt("step", 0))
        org.junit.Assert.assertEquals(before, authored.map { it.all.toMap() })
    }

    @Test fun mixedCustomExpeditionResumesExactSequenceAndCompletes() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val custom = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val precision = context.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
        val oldPrecision = precision.getBoolean("precise_selection", false)
        check(custom.edit().clear().commit())
        check(precision.edit().putBoolean("precise_selection", true).commit())
        try {
            compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
            compose.onNodeWithTag("customMode_mixed").performScrollTo().performClick()
            compose.onNodeWithTag("customLength_6").performScrollTo().performClick()
            compose.onNodeWithTag("customStart").performScrollTo().performClick()
            val codes = custom.getString("codes", "")!!
            val kinds = custom.getString("kinds", "")!!
            val objectives = kinds.split(",").zip(codes.split(","))
            org.junit.Assert.assertEquals(6, objectives.size)
            org.junit.Assert.assertEquals(setOf("grid", "gold", "identify", "checklist"), objectives.map { it.first }.toSet())
            val ids = mapOf("2538" to "mushroom-nook", "2840" to "buttercup-glade", "2641" to "acorn-nook", "2941" to "old-waystone")
            objectives.forEachIndexed { index, (kind, code) ->
                when (kind) {
                    "grid" -> {
                        val e = code.take(2).toInt()
                        val n = code.takeLast(2).toInt()
                        val startE = if (e == 26 && n == 40) 25 else 26
                        if (startE == 25) compose.onNodeWithContentDescription("Move selected square west").performClick()
                        repeat((startE-e).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square west").performClick() }
                        repeat((e-startE).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square east").performClick() }
                        repeat((40-n).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square south").performClick() }
                        repeat((n-40).coerceAtLeast(0)) { compose.onNodeWithContentDescription("Move selected square north").performClick() }
                        compose.onNodeWithTag("checkChapter").performClick()
                    }
                    "gold", "checklist" -> {
                        val prefix = if (kind == "gold") "customGold" else "customChecklist"
                        compose.onNodeWithTag(prefix + "Input").performTextReplacement(code)
                        compose.onNodeWithTag(prefix + "Check").performClick()
                    }
                    "identify" -> {
                        compose.onNodeWithTag("customIdentify_${ids.getValue(code)}").performClick()
                        compose.onNodeWithTag("customIdentifyCheck").performClick()
                    }
                }
                compose.waitForIdle()
                org.junit.Assert.assertEquals(index+1, custom.getInt("step",0))
                if (index == 2) {
                    compose.onNodeWithTag("customBack").performClick()
                    compose.onNodeWithTag("customBack").performClick()
                    compose.activityRule.scenario.recreate()
                    compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
                    compose.onNodeWithTag("customResume").performScrollTo().performClick()
                    org.junit.Assert.assertEquals(codes, custom.getString("codes",""))
                    org.junit.Assert.assertEquals(kinds, custom.getString("kinds",""))
                    org.junit.Assert.assertEquals(3, custom.getInt("step",0))
                }
            }
            compose.onNodeWithTag("puzzleFinishCard").assertExists()
            compose.onNodeWithTag("finishContinue").performClick()
            org.junit.Assert.assertEquals("mixed", custom.getString("mode",""))
            org.junit.Assert.assertEquals(6, custom.getInt("count",0))
            org.junit.Assert.assertEquals(0, custom.getInt("step",0))
        } finally {
            precision.edit().putBoolean("precise_selection", oldPrecision).commit()
        }
    }

}
