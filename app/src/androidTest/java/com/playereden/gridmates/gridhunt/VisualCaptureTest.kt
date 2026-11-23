package com.playereden.gridmates.gridhunt

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Real screenshots from the running app on an Android 35 emulator.
 *
 * Captures are inspection evidence, not automated proof of visual quality.
 * This isolated emulator test may seed progress in its own app-private data;
 * it never modifies the owner's installed phone or published puzzles.
 */
@RunWith(AndroidJUnit4::class)
class VisualCaptureTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val target get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun capture(filename: String) {
        compose.waitForIdle()
        val image = requireNotNull(
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        ) { "Emulator screenshot was unavailable" }
        val root = requireNotNull(target.getExternalFilesDir("visual-qa")) {
            "Emulator screenshot output directory was unavailable"
        }
        check(root.isDirectory || root.mkdirs()) { "Could not create screenshot directory" }
        val output = File(root, filename)
        output.outputStream().use { stream ->
            check(image.compress(Bitmap.CompressFormat.PNG, 100, stream)) {
                "PNG write failed for $filename"
            }
        }
        image.recycle()
        check(output.length() > 0L) { "Empty screenshot $filename" }
    }

    private fun prepare() {
        listOf("gridhunt_first_bearings_v1", "gridhunt_reverse_v1").forEach { name ->
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

    @Test fun captureHomeBothModesBothLooksAndBothFinishCards() {
        prepare()
        compose.onNodeWithTag("homeIllustration").assertExists()
        capture("01_home.png")

        compose.onNodeWithTag("openChapter").performClick()
        dismissGuide()
        compose.onNodeWithTag("checkChapter").assertExists()
        capture("02_trail_treasures.png")
        compose.onNodeWithTag("mapLookToggle").performClick()
        capture("03_trail_other_map_style.png")

        compose.onNodeWithTag("chapterHome").performClick()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("reverseCodeInput").assertExists()
        capture("04_golden_spots.png")
        compose.onNodeWithTag("reverseHome").performClick()

        // A completed chapter is staged ONLY in this emulator's test preferences.
        check(target.getSharedPreferences("gridhunt_first_bearings_v1", Context.MODE_PRIVATE)
            .edit().putInt("hunt_index", 2).putInt("objective_index", 3)
            .putBoolean("guide_seen", true).commit())
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openChapter").performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("05_treasure_finish.png")
        compose.onNodeWithTag("finishHome").performClick()

        check(target.getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
            .edit().putInt("step", 5).commit())
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("06_golden_finish.png")
    }
}
