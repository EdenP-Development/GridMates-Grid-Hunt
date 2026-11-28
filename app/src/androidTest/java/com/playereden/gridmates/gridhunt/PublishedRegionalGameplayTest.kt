package com.playereden.gridmates.gridhunt

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class PublishedRegionalGameplayTest {
    @get:Rule val compose = createComposeRule()

    @Test fun publishedAtlasIgnoresDraftSelectionAndEarnedFlagsAndUsesOriginalWoodlandSaves() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val woodland = PublishedWorlds.selection.destinations.single()
        val coast = CoastalExpeditionPreview.selection.destinations.single { it.map == WhisperingCoastDraft.map }
        val chapters = woodland.world.chapters.sortedBy { it.order }
        val keys = (chapters.map { it.progressKey } + coast.world.chapters.map { it.progressKey } +
            listOf("gridhunt_world_selection_v1", "gridhunt_world_selection_preview_v1",
                "gridhunt_campaign_earned_v1", "gridhunt_campaign_earned_preview_v1",
                "gridhunt_reverse_v1", "gridhunt_ui_preferences",
                "gridhunt_reverse_whispering-coast-01_r1_v1")).distinct()
        val stores = keys.associateWith { context.getSharedPreferences(it,Context.MODE_PRIVATE) }
        val original = stores.mapValues { it.value.all.toMap() }
        val visible = mutableStateOf(true)
        var exits = 0
        fun restore(store: SharedPreferences, values: Map<String,*>) {
            val edit = store.edit().clear()
            values.forEach { (k,v) ->
                when(v) {
                    is String -> edit.putString(k,v)
                    is Int -> edit.putInt(k,v)
                    is Boolean -> edit.putBoolean(k,v)
                    is Long -> edit.putLong(k,v)
                    is Float -> edit.putFloat(k,v)
                    is Set<*> -> edit.putStringSet(k,v.filterIsInstance<String>().toSet())
                }
            }
            check(edit.commit())
        }
        fun click(tag: String) = compose.onNodeWithTag(tag).performScrollTo().performClick()
        try {
            stores.values.forEach { check(it.edit().clear().commit()) }
            val first = chapters.first()
            check(stores.getValue("gridhunt_world_selection_preview_v1").edit()
                .putString("world_id",coast.world.id).commit())
            check(stores.getValue("gridhunt_campaign_earned_preview_v1").edit()
                .putBoolean("earned_${first.chapter.id}_r${first.chapter.revision}",true).commit())
            check(stores.getValue(coast.world.chapters.first().progressKey).edit()
                .putInt("step",2).commit())
            check(stores.getValue("gridhunt_reverse_v1").edit().putInt("step",1).commit())
            val isolatedKeys = coast.world.chapters.map { it.progressKey } + listOf(
                "gridhunt_world_selection_preview_v1","gridhunt_campaign_earned_preview_v1",
                "gridhunt_reverse_whispering-coast-01_r1_v1")
            val before = isolatedKeys.associateWith { stores.getValue(it).all.toMap() }
            compose.setContent {
                if (visible.value) PublishedRegionalGameplay(hapticsEnabled=false) {
                    exits++
                    visible.value=false
                }
            }
            compose.onNodeWithTag("atlasWorld_"+coast.world.id).assertDoesNotExist()
            compose.onNodeWithTag("atlasProgress_"+woodland.world.id)
                .assertTextEquals("0 of 3 chapters complete")
            click("atlasOpen_"+woodland.world.id)
            compose.onNodeWithTag("regionChapter_"+chapters[1].chapter.id).assertIsNotEnabled()
            click("regionQuickPlay")
            click("regionalQuick_gold")
            compose.onNodeWithText("GOLDEN SPOT 2 OF 5").assertExists()
            compose.onNodeWithTag("reverseHome").performClick()
            click("regionalQuick_mixed")
            compose.onNodeWithText(first.chapter.name).assertExists()
            compose.onNodeWithTag("mixedHome").performClick()
            click("regionalQuickBack")
            click("regionBack")
            compose.onNodeWithTag("atlasSelected_"+woodland.world.id).assertExists()
            compose.onNodeWithTag("atlasWorld_"+coast.world.id).assertDoesNotExist()
            click("atlasBack")
            compose.runOnIdle { assertEquals(1,exits) }
            assertEquals(woodland.world.id,stores.getValue("gridhunt_world_selection_v1").getString("world_id",null))
            assertEquals(1,stores.getValue("gridhunt_reverse_v1").getInt("step",0))
            assertTrue(stores.getValue("gridhunt_campaign_earned_v1").all.isEmpty())
            assertEquals(before,isolatedKeys.associateWith { stores.getValue(it).all.toMap() })
        } finally {
            compose.runOnIdle { visible.value=false }
            stores.forEach { (k,v) -> restore(v,original.getValue(k)) }
        }
    }
}
