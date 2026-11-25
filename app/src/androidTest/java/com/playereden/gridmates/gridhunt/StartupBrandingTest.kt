package com.playereden.gridmates.gridhunt

import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Isolated overlay tests; no actual device animation settings are modified. */
@RunWith(AndroidJUnit4::class)
class StartupBrandingTest {
    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private fun mount(show: Boolean = true, animate: Boolean = true, series: Int? =
        R.drawable.gridmates_v1_runtime_transparent) {
        compose.mainClock.autoAdvance = false
        compose.setContent {
            StartupBranding(showInitially = show, seriesDrawable = series,
                animationsEnabled = { animate }) {
                Text("Game ready", Modifier.testTag("startupTestHome"))
            }
        }
        compose.mainClock.advanceTimeByFrame()
    }

    private fun homeReady() {
        compose.mainClock.advanceTimeBy(64)
        compose.onNodeWithTag("startup_branding").assertDoesNotExist()
        compose.onNodeWithTag("startupTestHome").assertExists()
    }

    @Test fun correctSeriesAndStandaloneArtworkOrder() {
        assertEquals(listOf(R.drawable.spudacus_v1_full_transparent), startupBrandingStages())
        assertEquals(listOf(R.drawable.spudacus_v1_full_transparent,
            R.drawable.gridmates_v1_runtime_transparent),
            startupBrandingStages(R.drawable.gridmates_v1_runtime_transparent))
    }

    @Test fun tapSkipsEntireSequenceFromFirstLogo() {
        mount()
        compose.onNodeWithTag("startup_branding").assertExists()
        compose.onNodeWithContentDescription("Spudacus").assertExists()
        compose.onNodeWithTag("startupTestHome").assertDoesNotExist()
        compose.onNodeWithTag("startup_branding").performClick()
        homeReady()
    }

    @Test fun secondLogoFollowsFirstAndCanBeSkipped() {
        mount()
        compose.mainClock.advanceTimeBy(1900)
        compose.onNodeWithContentDescription("GridMates").assertExists()
        compose.onNodeWithContentDescription("Spudacus").assertDoesNotExist()
        compose.onNodeWithTag("startup_branding").performClick()
        homeReady()
    }

    @Test fun normalSequenceFinishesWithoutInput() {
        mount()
        compose.mainClock.advanceTimeBy(3500)
        homeReady()
    }

    @Test fun backSkipsEntireSequence() {
        mount()
        compose.runOnIdle { compose.activity.onBackPressedDispatcher.onBackPressed() }
        homeReady()
    }

    @Test fun reducedMotionShowsGameImmediately() {
        mount(animate = false)
        homeReady()
    }

    @Test fun restoredActivityDoesNotReplay() {
        mount(show = false)
        homeReady()
    }

    @Test fun stopAndResumeDoesNotReplay() {
        mount()
        compose.activityRule.scenario.moveToState(Lifecycle.State.CREATED)
        compose.activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
        homeReady()
    }
}
