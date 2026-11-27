package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Test

class WorldNavigationStoreTest {
    @Test fun previewNavigationValidatesPrerequisitesAndKeepsPublishedSelectionSeparate() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val selection = CoastalExpeditionPreview.selection
        val woodland = selection.destinations.first { it.map == ExplorationMaps.woodland }
        val coast = selection.destinations.first { it.map == WhisperingCoastDraft.map }
        val chapters = selection.destinations.flatMap { it.world.chapters }
        val chapterPrefs = chapters.map { context.getSharedPreferences(it.progressKey,Context.MODE_PRIVATE) }
        val previewPrefs = context.getSharedPreferences("gridhunt_world_selection_preview_v1",Context.MODE_PRIVATE)
        val publishedPrefs = context.getSharedPreferences("gridhunt_world_selection_v1",Context.MODE_PRIVATE)
        val earnedPrefs = context.getSharedPreferences("gridhunt_campaign_earned_preview_v1",Context.MODE_PRIVATE)
        val publishedEarned = context.getSharedPreferences("gridhunt_campaign_earned_v1",Context.MODE_PRIVATE)
        val allPrefs = chapterPrefs + listOf(previewPrefs,publishedPrefs,earnedPrefs,publishedEarned)
        val saved = allPrefs.map { it.all.toMap() }
        fun restore(prefs: android.content.SharedPreferences, values: Map<String, *>) {
            val editor = prefs.edit().clear()
            values.forEach { (key,value) ->
                when(value) {
                    is String -> editor.putString(key,value)
                    is Int -> editor.putInt(key,value)
                    is Boolean -> editor.putBoolean(key,value)
                    is Long -> editor.putLong(key,value)
                    is Float -> editor.putFloat(key,value)
                    is Set<*> -> editor.putStringSet(key,value.filterIsInstance<String>().toSet())
                }
            }
            check(editor.commit())
        }
        try {
            allPrefs.forEach { check(it.edit().clear().commit()) }
            val store = WorldNavigationStore(context,selection,true)
            assertEquals(woodland.world.id,store.current()?.destination?.world?.id)
            assertFalse(store.choose(coast.world.id))
            assertFalse(store.choose("unknown"))
            assertNull(store.openChapter(coast.world.id,coast.world.chapters.first().chapter.id))
            assertNull(store.openChapter(woodland.world.id,woodland.world.chapters.last().chapter.id))
            assertTrue(previewPrefs.all.isEmpty())
            woodland.world.chapters.forEach { chapter ->
                check(context.getSharedPreferences(chapter.progressKey,Context.MODE_PRIVATE)
                    .edit().putInt("step",chapter.chapter.objectives.size).commit())
            }
            val before = chapterPrefs.map { it.all.toMap() }
            assertTrue(store.choose(coast.world.id))
            assertEquals(coast.world.id,WorldNavigationStore(context,selection,true).current()?.destination?.world?.id)
            assertNotNull(store.openChapter(coast.world.id,coast.world.chapters.first().chapter.id))
            assertNull(store.openChapter(coast.world.id,coast.world.chapters.last().chapter.id))
            assertNull(store.openChapter(coast.world.id,woodland.world.chapters.first().chapter.id))
            assertFalse(WorldNavigationStore(context).choose(coast.world.id))
            assertTrue(publishedPrefs.all.isEmpty())
            assertEquals(before,chapterPrefs.map { it.all })
            check(previewPrefs.edit().putString("world_id","unknown").commit())
            assertEquals(woodland.world.id,store.current()?.destination?.world?.id)
            assertEquals("unknown",previewPrefs.getString("world_id",null))
            val ledger = CampaignCompletionStore(context,selection,true)
            assertTrue(ledger.captureCompleted())
            assertEquals(woodland.world.chapters.map { it.chapter.id }.toSet(),ledger.snapshot().earnedChapterIds)
            assertEquals(before,chapterPrefs.map { it.all })
            assertTrue(publishedEarned.all.isEmpty())
            assertTrue(store.choose(coast.world.id))
            val first = woodland.world.chapters.first()
            check(context.getSharedPreferences(first.progressKey,Context.MODE_PRIVATE).edit().putInt("step",0).commit())
            val reopened = WorldNavigationStore(context,selection,true)
            assertEquals(coast.world.id,reopened.current()?.destination?.world?.id)
            assertNotNull(reopened.openChapter(coast.world.id,coast.world.chapters.first().chapter.id))
            val replay = reopened.choices().first { it.destination.world.id == woodland.world.id }.chapters.first()
            assertEquals(0,replay.completedObjectives)
            assertTrue(replay.everCompleted)
            assertTrue(ledger.captureCompleted())
            assertEquals(woodland.world.chapters.map { it.chapter.id }.toSet(),ledger.snapshot().earnedChapterIds)
        } finally {
            allPrefs.zip(saved).forEach { (prefs,values) -> restore(prefs,values) }
        }
    }
}
