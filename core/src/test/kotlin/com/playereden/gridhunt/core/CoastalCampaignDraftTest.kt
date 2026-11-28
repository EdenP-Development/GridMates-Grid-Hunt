package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CoastalCampaignDraftTest {
    private val draft = CoastalCampaignDraft.registry
    private val selection = CoastalCampaignDraft.selection
    private val coastId = CoastalCampaignDraft.worldId
    private val woodland = GridHuntCampaign.registry.chapters
    private val coast = draft.chapters.filter { it.worldId == coastId }

    @Test fun allWoodlandChaptersRequiredBeforeCoastUnlocks() {
        val complete = woodland.associate { it.chapter.id to it.chapter.objectives.size }
        for (chapter in woodland) {
            val partial = CampaignSnapshot(complete - chapter.chapter.id)
            assertNull(selection.select(coastId, partial))
        }
        val snapshot = CampaignSnapshot(complete)
        val choice = requireNotNull(selection.select(coastId, snapshot))
        assertEquals(listOf(CampaignChapterStatus.READY, CampaignChapterStatus.LOCKED,
            CampaignChapterStatus.LOCKED), choice.chapters.map { it.status })
        assertEquals(GridHuntCampaign.littleWoodlandId,
            selection.resolve(coastId, CampaignSnapshot())?.destination?.world?.id)
    }

    @Test fun cumulativeCoastalProgressUnlocksInOrderAndAllowsRevisit() {
        val steps = woodland.associate { it.chapter.id to it.chapter.objectives.size }.toMutableMap()
        steps[coast[2].chapter.id] = 4
        assertEquals(CampaignChapterStatus.LOCKED, draft.state(coast[2].chapter.id, CampaignSnapshot(steps))?.status)
        steps.remove(coast[2].chapter.id)
        for (chapter in coast) {
            assertEquals(CampaignChapterStatus.READY, draft.state(chapter.chapter.id, CampaignSnapshot(steps))?.status)
            steps[chapter.chapter.id] = 1
            assertEquals(CampaignChapterStatus.IN_PROGRESS, draft.state(chapter.chapter.id, CampaignSnapshot(steps))?.status)
            steps[chapter.chapter.id] = chapter.chapter.objectives.size
        }
        assertTrue(requireNotNull(selection.select(coastId, CampaignSnapshot(steps))).complete)
        assertTrue(requireNotNull(selection.select(GridHuntCampaign.littleWoodlandId, CampaignSnapshot(steps))).complete)
        assertEquals(6, draft.chapters.map { it.progressKey }.distinct().size)
        assertEquals(1, PublishedWorlds.selection.destinations.size)
        assertNull(PublishedWorlds.selection.select(coastId, CampaignSnapshot(steps)))
    }

    @Test fun destinationRejectsWrongMapRevision() {
        val destination = selection.destinations.single { it.world.id == coastId }
        assertThrows(IllegalArgumentException::class.java) {
            destination.copy(map = destination.map.copy(revision = destination.map.revision + 1))
        }
    }
}
