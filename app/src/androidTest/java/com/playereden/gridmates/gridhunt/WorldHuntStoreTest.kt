package com.playereden.gridhunt

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.ExplorationMaps
import com.playereden.gridhunt.core.MapHuntSession
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WorldHuntStoreTest {
    @Test fun isolatedMapsAndRevisionsPreserveExactSessionsAndLegacyStore() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val base = ExplorationMaps.woodland.copy(id = "qa-store-isolation-a")
        val other = base.copy(id = "qa-store-isolation-b")
        val revised = base.copy(revision = base.revision + 1)
        val a = WorldHuntStore(context, base)
        val b = WorldHuntStore(context, other)
        val c = WorldHuntStore(context, revised)
        val stores = listOf(a,b,c).map { context.getSharedPreferences(it.namespace, Context.MODE_PRIVATE) }
        val legacy = context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE)
        val before = legacy.all.toMap()
        try {
            stores.forEach { assertTrue(it.edit().clear().commit()) }
            assertSame(WorldHuntStore.ReadResult.Empty, a.read())
            val first = MapHuntSession(base.id, base.revision, "grid", "2437,2538,2639", step = 1)
            val second = first.copy(mapId = other.id, step = 2)
            val third = first.copy(mapRevision = revised.revision, step = 3)
            assertTrue(a.write(first))
            assertTrue(b.write(second))
            assertTrue(c.write(third))
            assertEquals(first, (WorldHuntStore(context, base).read() as WorldHuntStore.ReadResult.Ready).session)
            assertEquals(second, (b.read() as WorldHuntStore.ReadResult.Ready).session)
            assertEquals(third, (c.read() as WorldHuntStore.ReadResult.Ready).session)
            assertFalse(a.write(second))
            assertFalse(a.write(third))
            assertFalse(a.write(first.copy(step = 4)))
            assertEquals(first, (a.read() as WorldHuntStore.ReadResult.Ready).session)
            assertEquals(before, legacy.all)
        } finally {
            stores.forEach { it.edit().clear().commit() }
        }
    }

    @Test fun malformedRecordsAreNotSilentlyRepaired() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val map = ExplorationMaps.woodland.copy(id = "qa-store-corruption")
        val store = WorldHuntStore(context, map)
        val prefs = context.getSharedPreferences(store.namespace, Context.MODE_PRIVATE)
        try {
            assertTrue(prefs.edit().clear().commit())
            assertTrue(store.write(MapHuntSession(map.id, map.revision, "grid", "2437,2538,2639")))
            assertTrue(prefs.edit().putString("step", "wrong type").commit())
            val corrupted = prefs.all.toMap()
            assertSame(WorldHuntStore.ReadResult.Invalid, store.read())
            assertEquals(corrupted, prefs.all)
            assertTrue(prefs.edit().remove("map_id").commit())
            assertSame(WorldHuntStore.ReadResult.Invalid, store.read())
        } finally {
            prefs.edit().clear().commit()
        }
    }
}
