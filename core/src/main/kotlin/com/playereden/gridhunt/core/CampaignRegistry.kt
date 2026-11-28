package com.playereden.gridhunt.core

/**
 * GH-V15: a versioned campaign publication registry, independent of UI and
 * SharedPreferences. This registry contains ONLY genuinely authored chapters.
 *
 * A preview/locked destination is NOT a chapter; it must never be published
 * into worlds until its playable objectives and map data actually exist.
 */
data class CampaignChapter(
    val chapter: ExpeditionChapter,
    val worldId: String,
    val order: Int,
    val prerequisiteChapterIds: Set<String> = emptySet(),
    val progressKey: String
) {
    init {
        require(worldId.isNotBlank() && order >= 0 && progressKey.isNotBlank())
        require(chapter.id.isNotBlank() && chapter.revision > 0)
        require(chapter.id !in prerequisiteChapterIds)
        require(prerequisiteChapterIds.none { it.isBlank() })
    }
}

data class CampaignWorld(
    val id: String,
    val revision: Int,
    val name: String,
    val mapId: String,
    val chapters: List<CampaignChapter>
) {
    init {
        require(id.isNotBlank() && revision > 0 && name.isNotBlank() && mapId.isNotBlank())
        require(chapters.isNotEmpty())
        require(chapters.all { it.worldId == id })
        require(chapters.map { it.order }.distinct().size == chapters.size)
        require(chapters.map { it.chapter.id }.distinct().size == chapters.size)
        require(chapters.map { it.progressKey }.distinct().size == chapters.size)
    }
}

/** Persisted raw objective steps are read by a UI/data adapter, never rewritten. */
data class CampaignSnapshot(
    val rawStepsByChapterId: Map<String, Int> = emptyMap(),
    /** Explicit earned completions; legacy snapshots retain their original behavior. */
    val earnedChapterIds: Set<String> = emptySet()
)

enum class CampaignChapterStatus { LOCKED, READY, IN_PROGRESS, COMPLETE }

data class CampaignChapterProgress(
    val chapterId: String,
    val status: CampaignChapterStatus,
    val completedObjectives: Int,
    val totalObjectives: Int,
    val everCompleted: Boolean = status == CampaignChapterStatus.COMPLETE
)

/**
 * Validate every authored world in a single consistent publication pass.
 * Invalid/missing/cyclic prerequisites cause startup validation failure:
 * they are not silently treated as unlocked.
 */
class CampaignRegistry(val worlds: List<CampaignWorld>) {
    val chapters: List<CampaignChapter> = worlds.flatMap { it.chapters }

    init {
        require(worlds.isNotEmpty())
        require(worlds.map { it.id }.distinct().size == worlds.size)
        require(chapters.map { it.chapter.id }.distinct().size == chapters.size)
        require(chapters.map { it.progressKey }.distinct().size == chapters.size)

        val byId = chapters.associateBy { it.chapter.id }
        for (chapter in chapters) {
            require(chapter.prerequisiteChapterIds.all { it in byId }) {
                "Missing prerequisite for ${chapter.chapter.id}"
            }
            require(chapter.prerequisiteChapterIds.all { prereq ->
                val dependency = byId.getValue(prereq)
                // A chapter cannot depend on a later destination in its own
                // world. Cross-world dependencies are explicit and cycle-checked.
                dependency.worldId != chapter.worldId || dependency.order < chapter.order
            })
        }
        val visited = mutableSetOf<String>()
        val active = mutableSetOf<String>()
        fun visit(id: String) {
            require(id !in active) { "Cyclic chapter prerequisite: $id" }
            if (id in visited) return
            active += id
            for (dependency in byId.getValue(id).prerequisiteChapterIds) visit(dependency)
            active -= id
            visited += id
        }
        chapters.forEach { visit(it.chapter.id) }
    }

    fun findChapter(id: String): CampaignChapter? = chapters.firstOrNull { it.chapter.id == id }

    /** Unknown/old snapshot keys are ignored; no new metadata gets invented. */
    fun state(chapterId: String, snapshot: CampaignSnapshot): CampaignChapterProgress? {
        val item = findChapter(chapterId) ?: return null
        val total = item.chapter.objectives.size
        val raw = snapshot.rawStepsByChapterId[chapterId] ?: 0
        val completed = raw.coerceIn(0, total)
        val unlocked = chapterId in snapshot.earnedChapterIds || item.prerequisiteChapterIds.all { dependency ->
            val prerequisite = findChapter(dependency)!!
            val n = prerequisite.chapter.objectives.size
            dependency in snapshot.earnedChapterIds || (snapshot.rawStepsByChapterId[dependency] ?: 0).coerceIn(0, n) == n
        }
        return CampaignChapterProgress(
            chapterId,
            when {
                !unlocked -> CampaignChapterStatus.LOCKED
                completed == total -> CampaignChapterStatus.COMPLETE
                completed > 0 -> CampaignChapterStatus.IN_PROGRESS
                else -> CampaignChapterStatus.READY
            },
            completed,
            total,
            everCompleted = chapterId in snapshot.earnedChapterIds || (unlocked && completed == total)
        )
    }

    fun worldProgress(worldId: String, snapshot: CampaignSnapshot): List<CampaignChapterProgress> =
        worlds.firstOrNull { it.id == worldId }?.chapters
            ?.sortedBy { it.order }
            ?.mapNotNull { state(it.chapter.id, snapshot) }
            ?: emptyList()
}

object GridHuntCampaign {
    const val littleWoodlandId = "little-woodland-v1"

    val registry: CampaignRegistry = CampaignRegistry(
        listOf(
            CampaignWorld(
                id = littleWoodlandId,
                revision = 1,
                name = "Little Woodland",
                mapId = WoodlandLandmarks.mapId,
                chapters = listOf(
                    CampaignChapter(
                        chapter = WoodlandExpedition.chapter,
                        worldId = littleWoodlandId,
                        order = 0,
                        progressKey = WoodlandExpedition.saveName
                    ),
                    CampaignChapter(
                        chapter = WoodlandSupplyExpedition.chapter,
                        worldId = littleWoodlandId,
                        order = 1,
                        prerequisiteChapterIds = setOf(WoodlandExpedition.chapter.id),
                        progressKey = WoodlandSupplyExpedition.saveName
                    ),
                    CampaignChapter(
                        chapter = WoodlandReturnExpedition.chapter,
                        worldId = littleWoodlandId,
                        order = 2,
                        prerequisiteChapterIds = setOf(WoodlandExpedition.chapter.id, WoodlandSupplyExpedition.chapter.id),
                        progressKey = WoodlandReturnExpedition.saveName
                    )
                )
            )
        )
    )

    /** Original read-only bridge kept for existing callers/tests. */
    fun snapshotForExistingExpedition(rawStep: Int) = CampaignSnapshot(
        mapOf(WoodlandExpedition.chapter.id to WoodlandExpedition.restoreStep(rawStep))
    )

    /** Two isolated save namespaces; Chapter 2 never alters Chapter 1. */
    fun snapshot(rawFirst: Int, rawSecond: Int, rawThird: Int = 0) = CampaignSnapshot(
        mapOf(
            WoodlandExpedition.chapter.id to WoodlandExpedition.restoreStep(rawFirst),
            WoodlandSupplyExpedition.chapter.id to WoodlandSupplyExpedition.run.restoreStep(rawSecond),
            WoodlandReturnExpedition.chapter.id to WoodlandReturnExpedition.run.restoreStep(rawThird)
        )
    )
}
