package com.playereden.gridmates.gridhunt

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.platform.app.InstrumentationRegistry
import android.content.Context
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
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
        compose.onNodeWithTag("homeIllustration").assertExists()
        compose.onNodeWithText("Choose your puzzle").assertExists()
    }

    @Test fun threePillarNavigationAndAdventureChapterAreReal() {
        compose.onNodeWithTag("openAdventure").performClick()
        compose.onNodeWithText("The Missing Field Notes").assertExists()
        compose.onNodeWithTag("adventureProgress").assertExists()
        compose.onNodeWithTag("openAdventureChapter").performClick()
        compose.onNodeWithText("The Missing Field Notes").assertExists()
        compose.onNodeWithTag("mixedHome").performClick()
        compose.onNodeWithTag("adventureBack").performClick()
        compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
        compose.onNodeWithTag("customNotReady").assertExists()
        compose.onNodeWithTag("customOpenQuick").performClick()
        compose.onNodeWithText("Quick Play").assertExists()
        compose.onNodeWithTag("quickBack").performClick()
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

    @Test fun mapLookSwitchKeepsBothPuzzlesOpen() {
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performClick()
        dismissGuideIfShown()
        compose.onNodeWithTag("mapLookToggle").assertExists().performClick()
        compose.onNodeWithText("Trail Treasures").assertExists()
        compose.onNodeWithTag("chapterHome").performClick()

        openQuickIfAtHome()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("mapLookToggle").assertExists().performClick()
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
        compose.onNodeWithTag("mixedCheck").performClick()
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

    @Test fun visualMapChoiceIsReachable() {
        openQuickIfAtHome()
        compose.onNodeWithTag("appearanceToggle").performScrollTo().performClick()
        compose.onNodeWithTag("style_SURVEY_TABLE").performScrollTo().performClick()
        compose.onNodeWithTag("style_FIELD_GUIDE").assertExists()
        openQuickIfAtHome()
        compose.onNodeWithTag("openChapter").performScrollTo().performClick()
        dismissGuideIfShown()
        compose.onNodeWithText("Trail Treasures").assertExists()
    }
}
