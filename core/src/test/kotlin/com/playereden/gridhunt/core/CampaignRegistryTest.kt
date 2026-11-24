package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class CampaignRegistryTest {
    private val first = WoodlandExpedition.chapter

    private fun chapter(id: String) = first.copy(id = id)
    private fun item(
        id: String,
        worldId: String = "wood-a",
        order: Int = 0,
        requires: Set<String> = emptySet()
    ) = CampaignChapter(
        chapter = chapter(id),
        worldId = worldId,
        order = order,
        prerequisiteChapterIds = requires,
        progressKey = "fictional-save-$id"
    )
    private fun world(
        id: String,
        vararg entries: CampaignChapter
    ) = CampaignWorld(
        id = id,
        revision = 1,
        name = "Fixture World $id",
        mapId = WoodlandLandmarks.mapId,
        chapters = entries.toList()
    )

    @Test fun liveRegistryExposesOnlyTheRealAuthoredLittleWoodlandChapter() {
        val registry = GridHuntCampaign.registry
        assertEquals(1, registry.worlds.size)
        assertEquals(1, registry.chapters.size)
        assertEquals(GridHuntCampaign.littleWoodlandId, registry.worlds.single().id)
        assertEquals(WoodlandLandmarks.mapId, registry.worlds.single().mapId)
        assertEquals(WoodlandExpedition.chapter.id, registry.chapters.single().chapter.id)
        assertEquals(WoodlandExpedition.saveName, registry.chapters.single().progressKey)
        assertEquals(4, registry.chapters.single().chapter.objectives.size)
        assertNull(registry.findChapter("fictional-church-chapter"))
        assertTrue(registry.worldProgress("not-built-yet", CampaignSnapshot()).isEmpty())
    }

    @Test fun olderIndependentSaveIsReadWithoutMigrationOrReset() {
        val registry = GridHuntCampaign.registry
        val id = WoodlandExpedition.chapter.id
        assertEquals(CampaignChapterStatus.READY,
            registry.state(id, GridHuntCampaign.snapshotForExistingExpedition(0))?.status)
        assertEquals(CampaignChapterStatus.IN_PROGRESS,
            registry.state(id, GridHuntCampaign.snapshotForExistingExpedition(2))?.status)
        assertEquals(CampaignChapterStatus.COMPLETE,
            registry.state(id, GridHuntCampaign.snapshotForExistingExpedition(4))?.status)
        assertEquals(4, registry.state(id,
            GridHuntCampaign.snapshotForExistingExpedition(999))?.completedObjectives)
        assertEquals(0, registry.state(id,
            GridHuntCampaign.snapshotForExistingExpedition(-300))?.completedObjectives)
        assertNull(registry.state("unknown", CampaignSnapshot()))
    }

    @Test fun prerequisitesUnlockOnlyWhenEveryReferencedChapterIsComplete() {
        val a = item("a")
        val b = item("b", order = 1, requires = setOf("a"))
        val c = item("c", order = 2, requires = setOf("a", "b"))
        val registry = CampaignRegistry(listOf(world("wood-a", a,b,c)))
        val initial = registry.worldProgress("wood-a", CampaignSnapshot())
        assertEquals(listOf(
            CampaignChapterStatus.READY,
            CampaignChapterStatus.LOCKED,
            CampaignChapterStatus.LOCKED
        ), initial.map { it.status })
        val completedA = CampaignSnapshot(mapOf("a" to 4))
        assertEquals(CampaignChapterStatus.READY, registry.state("b", completedA)?.status)
        assertEquals(CampaignChapterStatus.LOCKED, registry.state("c", completedA)?.status)
        val completedBoth = CampaignSnapshot(mapOf("a" to 4, "b" to 4))
        assertEquals(CampaignChapterStatus.READY, registry.state("c", completedBoth)?.status)
        val finished = CampaignSnapshot(mapOf("a" to 4, "b" to 4, "c" to 4))
        assertEquals(CampaignChapterStatus.COMPLETE, registry.state("c", finished)?.status)
    }

    @Test fun invalidRegistryRejectsDuplicateIdsMissingLinksAndReverseOrder() {
        val a = item("a")
        assertThrows(IllegalArgumentException::class.java) {
            CampaignRegistry(listOf(world("wood-a", a, a.copy(order = 1))))
        }
        assertThrows(IllegalArgumentException::class.java) {
            CampaignRegistry(listOf(world("wood-a", item("b", requires = setOf("missing")))))
        }
        assertThrows(IllegalArgumentException::class.java) {
            CampaignRegistry(listOf(world("wood-a",
                item("a", order = 0, requires = setOf("b")),
                item("b", order = 1))))
        }
        assertThrows(IllegalArgumentException::class.java) {
            CampaignRegistry(listOf(world("wood-a", a), world("wood-a", item("b"))))
        }
    }

    @Test fun crossWorldCyclicDependenciesCannotPublish() {
        val a = item("a", "wood-a", requires = setOf("b"))
        val b = item("b", "wood-b", requires = setOf("a"))
        assertThrows(IllegalArgumentException::class.java) {
            CampaignRegistry(listOf(world("wood-a", a), world("wood-b", b)))
        }
    }

    @Test fun unrelatedSnapshotKeysAndMalformedStepsCannotUnlockDependentChapters() {
        val a = item("a")
        val b = item("b", order = 1, requires = setOf("a"))
        val registry = CampaignRegistry(listOf(world("wood-a", a, b)))
        val garbage = CampaignSnapshot(mapOf("wrong-key" to 900, "a" to -8, "b" to 999))
        val state = registry.state("b", garbage)!!
        assertEquals(CampaignChapterStatus.LOCKED, state.status)
        assertEquals(4, state.completedObjectives)
        assertEquals(CampaignChapterStatus.READY, registry.state(
            "a", garbage)?.status)
    }
}
