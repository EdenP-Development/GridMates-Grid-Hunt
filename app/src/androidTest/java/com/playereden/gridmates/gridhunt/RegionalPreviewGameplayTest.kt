package com.playereden.gridmates.gridhunt

import android.content.Context
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.WorldHuntStore
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class RegionalPreviewGameplayTest {
    @get:Rule val compose = createComposeRule()

    @Test fun atlasOpensActualCoastalPuzzlesAndReturnsToSelectedRegion() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selection = CoastalExpeditionPreview.selection
        val woodland = selection.destinations.first { it.map == ExplorationMaps.woodland }
        val coast = selection.destinations.first { it.map == WhisperingCoastDraft.map }
        val chapterKeys = selection.destinations.flatMap { it.world.chapters }.map { it.progressKey }
        val woodlandKeys = woodland.world.chapters.map { it.progressKey } + listOf(
            TrailChapterCatalog.woodland.saveName, "gridhunt_reverse_v1",
            "gridhunt_treasure_checklist_v1", IdentifyMissionCatalog.woodland.saveName,
            "gridhunt_custom_v1", "gridhunt_world_selection_v1", "gridhunt_campaign_earned_v1")
        val coastalKeys = listOf(TrailChapterCatalog.coastalDraft.saveName,
            "gridhunt_reverse_whispering-coast-01_r1_v1",
            "gridhunt_treasure_checklist_whispering-coast-01_r1_v1",
            IdentifyMissionCatalog.coastalDraft.saveName,
            "gridhunt_world_hunt_v1_whispering-coast-01_r1")
        val stores = (chapterKeys + woodlandKeys + coastalKeys + listOf(
            "gridhunt_world_selection_preview_v1","gridhunt_campaign_earned_preview_v1",
            "gridhunt_ui_preferences")).distinct().associateWith {
            context.getSharedPreferences(it,Context.MODE_PRIVATE)
        }
        val originals = stores.mapValues { it.value.all.toMap() }
        val visible = mutableStateOf(true)
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
        fun click(tag: String) {
            compose.onNodeWithTag(tag).performScrollTo().performClick()
        }
        try {
            stores.values.forEach { check(it.edit().clear().commit()) }
            woodland.world.chapters.forEach { chapter ->
                check(stores.getValue(chapter.progressKey).edit()
                    .putInt("step",chapter.chapter.objectives.size).commit())
            }
            check(stores.getValue(TrailChapterCatalog.coastalDraft.saveName)
                .edit().putBoolean("guide_seen",true).commit())
            val woodlandBefore = woodlandKeys.associateWith { stores.getValue(it).all.toMap() }
            compose.setContent {
                if (visible.value) RegionalPreviewGameplay { visible.value=false }
            }
            compose.onNodeWithTag("regionPortrait_"+coast.map.id).performScrollTo().assertIsDisplayed()
            capture("28_atlas_coast_unlocked.png")
            click("atlasOpen_"+coast.world.id)
            compose.onNodeWithTag("regionGameplayHub").assertExists()
            compose.onNodeWithTag("regionPortrait_"+coast.map.id).performScrollTo().assertIsDisplayed()
            capture("29_coastal_region_hub.png")
            val first = coast.world.chapters.first()
            compose.onNodeWithTag("regionChapter_"+coast.world.chapters.last().chapter.id)
                .assertIsNotEnabled()
            click("regionChapter_"+first.chapter.id)
            compose.onNodeWithText(first.chapter.name).assertExists()
            compose.onNodeWithTag("mixedGuide").performClick()
            compose.onNodeWithTag("coastalMapGuide").assertExists()
            compose.onNodeWithTag("dismissCoastalMapGuide").performClick()
            compose.onNodeWithTag("mixedHome").performClick()
            click("regionQuickPlay")
            compose.onNodeWithTag("regionalQuick_mixed").assertDoesNotExist()
            click("regionalQuick_trail")
            compose.onNodeWithText("CLUE 1 OF 3").assertExists()
            compose.onNodeWithTag("chapterHome").performClick()
            click("regionalQuick_gold")
            compose.onNodeWithTag("reverseCodeInput").performTextReplacement("4256")
            compose.onNodeWithTag("checkReverse").performClick()
            compose.onNodeWithText("GOLDEN SPOT 2 OF 5").assertExists()
            compose.onNodeWithTag("reverseHome").performClick()
            click("regionalQuick_treasure")
            compose.onNodeWithTag("treasureProgress").assertTextEquals("0 of 6 coastal discoveries")
            compose.onNodeWithTag("treasureHome").performClick()
            click("regionalQuick_identify")
            compose.onNodeWithTag("identifyProgress").assertTextEquals("0 of 6 identified")
            compose.onNodeWithTag("identifyHome").performClick()
            click("regionalQuickBack")
            click("regionCustomPlay")
            click("customStart")
            val session = WorldHuntStore(context,coast.map).read() as WorldHuntStore.ReadResult.Ready
            assertEquals(coast.map.id,session.session.mapId)
            compose.onNodeWithTag("customBack").performClick()
            compose.onNodeWithTag("customBack").performClick()
            click("regionBack")
            compose.onNodeWithTag("atlasSelected_"+coast.world.id).assertExists()
            assertEquals(woodlandBefore,woodlandKeys.associateWith { stores.getValue(it).all.toMap() })
            assertEquals(1,stores.getValue("gridhunt_reverse_whispering-coast-01_r1_v1").getInt("step",0))
        } finally {
            compose.runOnIdle { visible.value=false }
            stores.forEach { (key,store) -> restore(store,originals.getValue(key)) }
        }
    }

    @Test fun atlasOpensWoodlandPuzzlesAndPreservesCoastalProgress() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selection = CoastalExpeditionPreview.selection
        val woodland = selection.destinations.first { it.map == ExplorationMaps.woodland }
        val coast = selection.destinations.first { it.map == WhisperingCoastDraft.map }
        val chapterKeys = selection.destinations.flatMap { it.world.chapters }.map { it.progressKey }
        val woodlandKeys = woodland.world.chapters.map { it.progressKey } + listOf(
            TrailChapterCatalog.woodland.saveName, "gridhunt_reverse_v1",
            "gridhunt_treasure_checklist_v1", IdentifyMissionCatalog.woodland.saveName,
            "gridhunt_custom_v1", "gridhunt_world_selection_v1", "gridhunt_campaign_earned_v1")
        val coastalKeys = listOf(TrailChapterCatalog.coastalDraft.saveName,
            "gridhunt_reverse_whispering-coast-01_r1_v1",
            "gridhunt_treasure_checklist_whispering-coast-01_r1_v1",
            IdentifyMissionCatalog.coastalDraft.saveName,
            "gridhunt_world_hunt_v1_whispering-coast-01_r1")
        val stores = (chapterKeys + woodlandKeys + coastalKeys + listOf(
            "gridhunt_world_selection_preview_v1","gridhunt_campaign_earned_preview_v1",
            "gridhunt_ui_preferences")).distinct().associateWith {
            context.getSharedPreferences(it,Context.MODE_PRIVATE)
        }
        val originals = stores.mapValues { it.value.all.toMap() }
        val visible = mutableStateOf(true)
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
        fun click(tag: String) {
            compose.onNodeWithTag(tag).performScrollTo().performClick()
        }
        try {
            stores.values.forEach { check(it.edit().clear().commit()) }
            check(stores.getValue(TrailChapterCatalog.woodland.saveName)
                .edit().putBoolean("guide_seen",true).commit())
            val coastalBefore = (coastalKeys + coast.world.chapters.map { it.progressKey })
                .associateWith { stores.getValue(it).all.toMap() }
            compose.setContent {
                if (visible.value) RegionalPreviewGameplay { visible.value=false }
            }
            compose.onNodeWithTag("atlasOpen_"+coast.world.id).assertIsNotEnabled()
            compose.onNodeWithTag("regionPortrait_"+woodland.map.id).performScrollTo().assertIsDisplayed()
            capture("26_atlas_woodland.png")
            click("atlasOpen_"+woodland.world.id)
            val first = woodland.world.chapters.first()
            compose.onNodeWithTag("regionPortrait_"+woodland.map.id).performScrollTo().assertIsDisplayed()
            capture("27_woodland_region_hub.png")
            compose.onNodeWithTag("regionChapter_"+woodland.world.chapters.last().chapter.id)
                .assertIsNotEnabled()
            click("regionChapter_"+first.chapter.id)
            compose.onNodeWithText(first.chapter.name).assertExists()
            compose.onNodeWithTag("mixedGuide").performClick()
            compose.onNodeWithTag("woodlandMapGuide").assertExists()
            compose.onNodeWithTag("dismissWoodlandMapGuide").performClick()
            compose.onNodeWithTag("mixedHome").performClick()
            click("regionQuickPlay")
            click("regionalQuick_mixed")
            compose.onNodeWithText(first.chapter.name).assertExists()
            compose.onNodeWithTag("mixedHome").performClick()
            compose.onNodeWithTag("regionalQuick_mixed").assertExists()
            click("regionalQuick_trail")
            compose.onNodeWithText("CLUE 1 OF 3").assertExists()
            compose.onNodeWithTag("chapterHome").performClick()
            click("regionalQuick_gold")
            compose.onNodeWithTag("reverseCodeInput").performTextReplacement("2440")
            compose.onNodeWithTag("checkReverse").performClick()
            compose.onNodeWithText("GOLDEN SPOT 2 OF 5").assertExists()
            compose.onNodeWithTag("reverseHome").performClick()
            click("regionalQuick_treasure")
            compose.onNodeWithTag("treasureProgress").assertTextEquals("0 of 4 woodland discoveries")
            compose.onNodeWithTag("treasureHome").performClick()
            click("regionalQuick_identify")
            compose.onNodeWithTag("identifyProgress").assertTextEquals("0 of 4 identified")
            compose.onNodeWithTag("identifyHome").performClick()
            click("regionalQuickBack")
            click("regionCustomPlay")
            click("customStart")
            val custom = stores.getValue("gridhunt_custom_v1")
            val targets = MapHuntGenerator(woodland.map).restore(custom.getString("codes","")!!)
            assertEquals(3,targets.size)
            assertTrue(targets.all { it in woodland.map.cells })
            compose.onNodeWithTag("customBack").performClick()
            compose.onNodeWithTag("customBack").performClick()
            click("regionBack")
            compose.onNodeWithTag("atlasSelected_"+woodland.world.id).assertExists()
            compose.onNodeWithTag("atlasOpen_"+coast.world.id).assertIsNotEnabled()
            assertEquals(coastalBefore,coastalBefore.keys.associateWith { stores.getValue(it).all.toMap() })
            assertEquals(1,stores.getValue("gridhunt_reverse_v1").getInt("step",0))
            assertTrue(stores.getValue("gridhunt_world_selection_v1").all.isEmpty())
        } finally {
            compose.runOnIdle { visible.value=false }
            stores.forEach { (key,store) -> restore(store,originals.getValue(key)) }
        }
    }

    private fun capture(filename: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.waitForIdleSync()
        android.os.SystemClock.sleep(500L)
        val image = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
        val resolver = instrumentation.targetContext.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/GridHuntVisualQA")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val uri = requireNotNull(resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        try {
            requireNotNull(resolver.openOutputStream(uri)).use { stream ->
                check(image.compress(Bitmap.CompressFormat.PNG, 100, stream))
            }
            check(resolver.update(uri, ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }, null, null) == 1)
        } finally { image.recycle() }
    }
}
