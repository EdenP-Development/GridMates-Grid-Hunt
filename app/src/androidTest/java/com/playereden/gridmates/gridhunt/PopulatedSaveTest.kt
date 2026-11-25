package com.playereden.gridmates.gridhunt

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.ExplorerAcademy
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Populated legacy-key fixture; activity recreation is not an APK upgrade. */
@RunWith(AndroidJUnit4::class)
class PopulatedSaveTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun populatedSavesRemainIndependentAcrossNavigationAndRecreation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        fun prefs(name: String) = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val first = prefs("gridhunt_mixed_woodland_v1")
        val second = prefs("gridhunt_woodland_supplies_v1")
        val third = prefs("gridhunt_woodland_return_v1")
        val trail = prefs("gridhunt_first_bearings_v1")
        val reverse = prefs("gridhunt_reverse_v1")
        val academy = prefs(ExplorerAcademy.saveName)
        val custom = prefs("gridhunt_custom_v1")
        val identify = prefs("gridhunt_identify_v1")
        val checklist = prefs("gridhunt_treasure_checklist_v1")
        val ui = prefs("gridhunt_ui_preferences")
        val stores = listOf(first, second, third, trail, reverse, academy, custom, identify, checklist, ui)
        val original = stores.map { it.all.toMap() }
        fun seed(p: SharedPreferences, block: SharedPreferences.Editor.() -> Unit) {
            val edit = p.edit().clear()
            edit.block()
            check(edit.commit())
        }
        try {
            seed(first) { putInt("step", 4) }
            seed(second) { putInt("step", 4) }
            seed(third) { putInt("step", 2) }
            seed(trail) { putInt("hunt_index", 0); putInt("objective_index", 1) }
            seed(reverse) { putInt("step", 2); putString("answer", "24") }
            seed(academy) {
                putStringSet("earned", ExplorerAcademy.exercises.map { it.id }.toSet())
                putStringSet("graduation", ExplorerAcademy.graduation.map { it.id }.toSet())
                putStringSet("qualifications", setOf(ExplorerAcademy.qualificationId))
            }
            seed(custom) {
                putString("mode", "grid"); putInt("count", 3)
                putString("codes", "2437,2538,2639"); putString("kinds", ""); putInt("step", 1)
            }
            seed(identify) { putInt("step", 2) }
            seed(checklist) { putString("completed_ids", "") }
            seed(ui) { putBoolean("precise_selection", true) }
            val populated = stores.map { it.all.toMap() }

            compose.activityRule.scenario.recreate()
            compose.onNodeWithText("2 of 3 chapters complete").assertExists()
            compose.onNodeWithText("2 of 4 objectives solved").assertExists()
            compose.onNodeWithTag("openAcademy").performScrollTo().performClick()
            compose.onNodeWithTag("academyQualification").assertExists()
            compose.onNodeWithTag("academyHome").performClick()
            compose.onNodeWithTag("openQuickPlay").performScrollTo().performClick()
            compose.onNodeWithText("1 of 9 squares found   •   2 of 5 golden spots").assertExists()
            compose.onNodeWithTag("quickBack").performClick()
            compose.onNodeWithTag("openCustomPlay").performScrollTo().performClick()
            compose.onNodeWithTag("customResume").assertExists()
            compose.onNodeWithText("Resume · Trail Treasures · 1/3").assertExists()
            compose.onNodeWithTag("customBack").performClick()
            compose.onNodeWithTag("openSettings").performClick()
            compose.onNodeWithTag("settingsPrecise").assertExists()
            compose.onNodeWithTag("settingsBack").performClick()
            compose.waitForIdle()
            assertEquals(populated, stores.map { it.all.toMap() })
        } finally {
            stores.zip(original).forEach { (store, snapshot) ->
                val edit = store.edit().clear()
                snapshot.forEach { (key, value) ->
                    when (value) {
                        is Int -> edit.putInt(key, value)
                        is Long -> edit.putLong(key, value)
                        is Float -> edit.putFloat(key, value)
                        is Boolean -> edit.putBoolean(key, value)
                        is String -> edit.putString(key, value)
                        is Set<*> -> edit.putStringSet(key, value.filterIsInstance<String>().toSet())
                    }
                }
                check(edit.commit())
            }
        }
    }
}
