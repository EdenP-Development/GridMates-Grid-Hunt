package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.TreasureChecklistCatalog
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Test-only host; never publishes the coastal destination. */
class CoastalTreasureChecklistTest {
    @get:Rule val compose = createComposeRule()

    @Test fun discoverOutOfOrderResumeCompleteAndReplayWithoutChangingWoodland() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val coast = context.getSharedPreferences("gridhunt_treasure_checklist_whispering-coast-01_r1_v1", Context.MODE_PRIVATE)
        val woodland = context.getSharedPreferences("gridhunt_treasure_checklist_v1", Context.MODE_PRIVATE)
        val original = woodland.all.toMap()
        check(coast.edit().clear().commit())
        val visible = mutableStateOf(true)
        val look = mutableStateOf(MapLook.SURVEY_TABLE)
        val mission = TreasureChecklistCatalog.coastalDraft
        compose.setContent {
            if (visible.value) TreasureChecklistScreen(
                onHome = { visible.value = false }, mapLook = look.value,
                onLook = { look.value = it }, hapticsEnabled = false, mission = mission
            )
        }
        fun progress(count: Int) {
            compose.waitUntil(10000) {
                coast.getString("completed_ids", "")!!.split(',').count { it.isNotBlank() } == count
            }
            compose.onNodeWithTag("treasureProgress").assertTextEquals("$count of 6 coastal discoveries")
        }
        fun submit(code: String) {
            compose.onNodeWithTag("treasureCodeInput").performTextReplacement(code)
            compose.onNodeWithTag("checkTreasure").performClick()
        }
        fun reopen() {
            compose.onNodeWithTag("treasureHome").performClick()
            compose.waitForIdle()
            compose.runOnIdle { visible.value = true }
        }
        try {
            progress(0)
            compose.onNodeWithTag("openWoodlandMapGuide").performClick()
            compose.onNodeWithTag("coastalMapGuide").assertExists()
            mission.targets.forEach { compose.onNodeWithTag("guideSymbol_${it.id}").assertExists() }
            compose.onNodeWithTag("dismissCoastalMapGuide").performClick()
            val order = mission.targets.reversed()
            val first = order.first()
            compose.onNodeWithTag("treasureItem_${first.id}").performClick()
            compose.onNodeWithContentDescription(ExplorerTool.HINT.label).performClick()
            compose.onNodeWithTag("treasureHint").assertTextEquals(first.clue)
            compose.onNodeWithContentDescription(ExplorerTool.HINT.label).performClick()
            submit("0000")
            progress(0)
            submit(first.cell.reference)
            progress(1)
            reopen()
            progress(1)
            compose.runOnIdle { look.value = MapLook.values().first { it != MapLook.SURVEY_TABLE } }
            order.drop(1).forEachIndexed { index, target ->
                compose.onNodeWithTag("treasureItem_${target.id}").performClick()
                submit(target.cell.reference)
                progress(index + 2)
            }
            compose.onNodeWithText("Your coastal checklist is complete!").assertExists()
            reopen()
            compose.onNodeWithText("Your coastal checklist is complete!").assertExists()
            compose.onNodeWithText("Explore again").performClick()
            progress(0)
            compose.runOnIdle { visible.value = false }
            assertEquals(original, woodland.all)
        } finally {
            compose.runOnIdle { visible.value = false }
            check(coast.edit().clear().commit())
        }
    }
}
