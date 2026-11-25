package com.playereden.gridhunt.core

/** A published destination must have an authored campaign and an exact map revision. */
data class WorldDestination(
    val world: CampaignWorld,
    val map: ExplorationMap
) {
    init { require(world.mapId == map.id) }
}

data class WorldChoice(
    val destination: WorldDestination,
    val chapters: List<CampaignChapterProgress>
) {
    val unlocked: Boolean get() = chapters.any { it.status != CampaignChapterStatus.LOCKED }
    val complete: Boolean get() = chapters.isNotEmpty() && chapters.all { it.status == CampaignChapterStatus.COMPLETE }
    val completedChapters: Int get() = chapters.count { it.status == CampaignChapterStatus.COMPLETE }
}

/** Read-only navigation policy. Unpublished destinations are never selectable. */
class WorldSelection(
    private val campaign: CampaignRegistry,
    val destinations: List<WorldDestination>
) {
    init {
        require(destinations.map { it.world.id }.distinct().size == destinations.size)
        require(destinations.all { it.world in campaign.worlds })
    }

    fun choices(snapshot: CampaignSnapshot): List<WorldChoice> =
        destinations.map { WorldChoice(it, campaign.worldProgress(it.world.id, snapshot)) }

    fun select(worldId: String, snapshot: CampaignSnapshot): WorldChoice? =
        choices(snapshot).firstOrNull { it.destination.world.id == worldId && it.unlocked }

    /** Keep an unlocked selection; otherwise return the first available world. */
    fun resolve(savedWorldId: String?, snapshot: CampaignSnapshot): WorldChoice? =
        savedWorldId?.let { select(it, snapshot) } ?: choices(snapshot).firstOrNull { it.unlocked }
}

object PublishedWorlds {
    val selection = WorldSelection(
        GridHuntCampaign.registry,
        listOf(WorldDestination(
            GridHuntCampaign.registry.worlds.single { it.id == GridHuntCampaign.littleWoodlandId },
            ExplorationMaps.woodland
        ))
    )
}
