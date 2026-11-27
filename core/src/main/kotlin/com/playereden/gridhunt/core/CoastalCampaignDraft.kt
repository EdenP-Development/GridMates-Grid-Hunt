package com.playereden.gridhunt.core

/** Integration candidate only. Never used by live navigation or persistence. */
internal object CoastalCampaignDraft {
    const val worldId = "whispering-coast-v1"
    private val woodland = GridHuntCampaign.registry.worlds.single()
    private val runs = listOf(CoastalBeaconExpedition.run, CoastalTideExpedition.run, CoastalLightExpedition.run)
    private val coast = CampaignWorld(
        id = worldId, revision = 1, name = "Whispering Coast",
        mapId = WhisperingCoastDraft.map.id,
        chapters = runs.mapIndexed { index, run ->
            CampaignChapter(
                chapter = run.chapter, worldId = worldId, order = index,
                prerequisiteChapterIds = woodland.chapters.map { it.chapter.id }.toSet() +
                    runs.take(index).map { it.chapter.id },
                progressKey = run.saveName
            )
        }
    )
    val registry = CampaignRegistry(listOf(woodland, coast))
    val selection = WorldSelection(registry, listOf(
        WorldDestination(woodland, ExplorationMaps.woodland),
        WorldDestination(coast, WhisperingCoastDraft.map)
    ))
}

/** Authored preview entry points, not publication or navigation authorization. */
object CoastalExpeditionPreview {
    private val runs = listOf(CoastalBeaconExpedition.run, CoastalTideExpedition.run, CoastalLightExpedition.run)
    fun chapter(number: Int): ExpeditionPlaythrough = runs[number - 1]
    fun numberOf(mission: ExpeditionPlaythrough): Int? =
        runs.indexOfFirst { it === mission }.takeIf { it >= 0 }?.plus(1)
    fun nextName(number: Int): String? = runs.getOrNull(number)?.chapter?.name
}
