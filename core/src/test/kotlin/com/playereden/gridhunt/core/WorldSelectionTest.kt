package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class WorldSelectionTest {
    @Test fun originalWorldRemainsSelectableAtEveryProgressLevel() {
        val selection = PublishedWorlds.selection
        val id = GridHuntCampaign.littleWoodlandId
        val initial = selection.resolve(null, GridHuntCampaign.snapshot(0, 0, 0))!!
        assertEquals(id, initial.destination.world.id)
        assertEquals(ExplorationMaps.woodland, initial.destination.map)
        assertTrue(initial.unlocked)
        assertFalse(initial.complete)
        assertEquals(0, initial.completedChapters)
        val complete = selection.select(id, GridHuntCampaign.snapshot(4, 4, 4))!!
        assertTrue(complete.complete)
        assertEquals(3, complete.completedChapters)
    }

    @Test fun unknownSelectionFallsBackWithoutInventingDestinations() {
        val selection = PublishedWorlds.selection
        val snapshot = GridHuntCampaign.snapshot(0, 0, 0)
        assertNull(selection.select("unpublished-coast", snapshot))
        assertEquals(GridHuntCampaign.littleWoodlandId,
            selection.resolve("unpublished-coast", snapshot)!!.destination.world.id)
        assertEquals(1, selection.choices(snapshot).size)
    }

    @Test fun mismatchedMapAndDuplicateDestinationsAreRejected() {
        val destination = PublishedWorlds.selection.destinations.single()
        assertThrows(IllegalArgumentException::class.java) {
            WorldDestination(destination.world, destination.map.copy(id = "wrong-map"))
        }
        assertThrows(IllegalArgumentException::class.java) {
            WorldSelection(GridHuntCampaign.registry, listOf(destination, destination))
        }
    }
}
