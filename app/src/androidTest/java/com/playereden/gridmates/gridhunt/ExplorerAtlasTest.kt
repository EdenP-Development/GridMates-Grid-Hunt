package com.playereden.gridmates.gridhunt

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.key
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ExplorerAtlasTest {
    @get:Rule val compose = createComposeRule()

    @Test fun atlasGuardsLockedRegionsAndRestoresEarnedSelection() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selection = CoastalExpeditionPreview.selection
        val woodland = selection.destinations.first { it.map == ExplorationMaps.woodland }
        val coast = selection.destinations.first { it.map == WhisperingCoastDraft.map }
        val chapterStores = selection.destinations.flatMap { it.world.chapters }
            .associate { it.chapter.id to context.getSharedPreferences(it.progressKey,Context.MODE_PRIVATE) }
        fun prefs(name: String) = context.getSharedPreferences(name,Context.MODE_PRIVATE)
        val previewSelection = prefs("gridhunt_world_selection_preview_v1")
        val previewEarned = prefs("gridhunt_campaign_earned_preview_v1")
        val publishedSelection = prefs("gridhunt_world_selection_v1")
        val publishedEarned = prefs("gridhunt_campaign_earned_v1")
        val stores = chapterStores.values.toList() + listOf(previewSelection,previewEarned,publishedSelection,publishedEarned)
        val originals = stores.map { it.all.toMap() }
        val generation = mutableIntStateOf(0)
        val visible = mutableStateOf(true)
        val opened = mutableListOf<WorldDestination>()
        var backCount = 0
        fun restore(store: SharedPreferences, snapshot: Map<String,*>) {
            val editor = store.edit().clear()
            snapshot.forEach { (k,v) ->
                when(v) {
                    is Boolean -> editor.putBoolean(k,v)
                    is Int -> editor.putInt(k,v)
                    is Long -> editor.putLong(k,v)
                    is Float -> editor.putFloat(k,v)
                    is String -> editor.putString(k,v)
                    is Set<*> -> editor.putStringSet(k,v.filterIsInstance<String>().toSet())
                }
            }
            check(editor.commit())
        }
        try {
            stores.forEach { check(it.edit().clear().commit()) }
            compose.setContent {
                if (visible.value) key(generation.intValue) {
                    ExplorerAtlasScreen(
                        navigation = WorldNavigationStore(context,selection,true),
                        onOpenWorld = { opened.add(it); visible.value=false },
                        onBack = { backCount++; visible.value=false }
                    )
                }
            }
            val w = woodland.world.id
            val c = coast.world.id
            compose.onNodeWithTag("explorerAtlas").assertExists()
            compose.onNodeWithTag("atlasSelected_$w").assertExists()
            compose.onNodeWithTag("atlasOpen_$w").assertIsEnabled()
            compose.onNodeWithTag("atlasOpen_$c").performScrollTo().assertIsNotEnabled()
            compose.onNodeWithTag("atlasProgress_$c").assertTextEquals("0 of 3 chapters complete")
            assertTrue(opened.isEmpty())
            assertTrue(previewSelection.all.isEmpty())

            woodland.world.chapters.forEach { chapter ->
                check(chapterStores.getValue(chapter.chapter.id).edit()
                    .putInt("step",chapter.chapter.objectives.size).commit())
            }
            assertTrue(CampaignCompletionStore(context,selection,true).captureCompleted())
            val first = woodland.world.chapters.first()
            check(chapterStores.getValue(first.chapter.id).edit().putInt("step",1).commit())
            val before = chapterStores.values.map { it.all.toMap() }
            compose.runOnIdle { generation.intValue++ }
            compose.onNodeWithTag("atlasProgress_$w").assertTextEquals("3 of 3 chapters complete")
            compose.onNodeWithText("Replaying: 1 of 4 objectives solved").assertExists()
            compose.onNodeWithTag("atlasOpen_$c").performScrollTo().assertIsEnabled().performClick()
            compose.runOnIdle {
                assertEquals(listOf(coast),opened)
                assertEquals(c,previewSelection.getString("world_id",null))
                assertTrue(publishedSelection.all.isEmpty())
                assertTrue(publishedEarned.all.isEmpty())
                visible.value=true
                generation.intValue++
            }
            compose.onNodeWithTag("atlasSelected_$c").assertExists()
            compose.onNodeWithTag("atlasProgress_$w").assertTextEquals("3 of 3 chapters complete")
            compose.onNodeWithTag("atlasBack").performScrollTo().performClick()
            compose.runOnIdle { assertEquals(1,backCount) }
            assertEquals(before,chapterStores.values.map { it.all })
            assertEquals(c,WorldNavigationStore(context,selection,true).current()?.destination?.world?.id)
        } finally {
            compose.runOnIdle { visible.value=false }
            stores.zip(originals).forEach { (store,snapshot) -> restore(store,snapshot) }
        }
    }
}
