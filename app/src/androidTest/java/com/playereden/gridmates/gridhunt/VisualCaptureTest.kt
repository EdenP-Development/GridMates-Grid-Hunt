package com.playereden.gridmates.gridhunt

import android.content.Context
import android.graphics.Bitmap
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
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

        // Return to the live chapter and earn every finish state legitimately.
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

        compose.onNodeWithTag("openReverse").performScrollTo().performClick()
        listOf("2440", "2742", "2838", "2541", "2942").forEach(::enterSpotCode)
        compose.onNodeWithTag("puzzleFinishCard").assertExists()
        capture("06_golden_finish.png")
    }
}
