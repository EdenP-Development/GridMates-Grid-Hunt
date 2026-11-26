package com.playereden.gridhunt.core

/**
 * Real authored landmark answer data for the Little Woodland.
 *
 * These stable meter-world positions match the already-drawn original map
 * decorations. Renderers MUST use these points rather than duplicating numbers
 * in Canvas code. The artwork is not itself an answer source.
 */
data class WoodlandLandmark(
    val id: String,
    val name: String,
    val point: WorldPoint,
    val clue: String,
    val glyph: String
) {
    init {
        require(id.isNotBlank() && name.isNotBlank() && glyph.isNotBlank())
    }
    val cell: GridCell get() = GridReference.fromWorld(point, 4)
}

object WoodlandLandmarks {
    const val mapId = WoodlandTrainingMap.mapId
    const val mapRevision = WoodlandTrainingMap.revision

    val mushrooms = WoodlandLandmark(
        "mushroom-nook", "Mushroom Nook", WorldPoint(25750.0, 38900.0),
        "A pair of little mushrooms sits beside the southern trail.", "🍄"
    )
    val flowers = WoodlandLandmark(
        "buttercup-glade", "Buttercup Glade", WorldPoint(28480.0, 40350.0),
        "Look for the tiny pale flowers in an eastern clearing.", "✿"
    )
    val acorn = WoodlandLandmark(
        "acorn-nook", "Acorn Nook", WorldPoint(26580.0, 41650.0),
        "The acorn rests in a clearing toward the north.", "●"
    )
    val waystone = WoodlandLandmark(
        "old-waystone", "Old Waystone", WorldPoint(29050.0, 41600.0),
        "A small stone stands toward the northeast edge.", "◆"
    )
    val all: List<WoodlandLandmark> = listOf(mushrooms, flowers, acorn, waystone)
    private val indexed = all.associateBy { it.id }

    init {
        require(all.map { it.id }.distinct().size == all.size)
        // Distinct four-digit squares are intentional for this *intro* mission.
        // Later multi-feature cells need an explicit answer ambiguity policy.
        require(all.map { it.cell }.distinct().size == all.size)
        require(all.all { WoodlandTrainingMap.bounds.contains(it.point) })
    }
    fun byId(id: String): WoodlandLandmark? = indexed[id]
}

/**
 * Introductory Word Search-style checklist. Completion is by stable landmark ID,
 * not by list position. The answer is a FOUR-character grid square (area), not
 * the landmark's exact GPS point. No randomization or fabricated rewards.
 */
object TreasureChecklistMission {
    const val missionId = "little-woodland-findings-v1"
    val targets: List<WoodlandLandmark> = WoodlandLandmarks.all
    private val targetIds: Set<String> = targets.mapTo(linkedSetOf()) { it.id }

    fun restoreCompleted(rawIds: Set<String>): Set<String> =
        rawIds.filterTo(linkedSetOf()) { it in targetIds }

    fun activeTarget(completedIds: Set<String>, preferredId: String?): WoodlandLandmark? {
        val completed = restoreCompleted(completedIds)
        if (completed.size == targets.size) return null
        return targets.firstOrNull { it.id == preferredId && it.id !in completed }
            ?: targets.first { it.id !in completed }
    }

    fun correct(targetId: String, answer: String): Boolean {
        val target = WoodlandLandmarks.byId(targetId) ?: return false
        if (target.id !in targetIds || answer.length != 4 ||
            answer.any { it !in '0'..'9' }) return false
        return target.cell == GridReference.parse(answer)
    }

    /** Wrong/duplicate answers do not advance or clear discoveries. */
    fun accept(completedIds: Set<String>, targetId: String, answer: String): Set<String> {
        val known = restoreCompleted(completedIds)
        return if (targetId in known || !correct(targetId, answer)) known
        else known + targetId
    }

    fun completed(completedIds: Set<String>): Boolean =
        restoreCompleted(completedIds).size == targets.size
}

/** Independent map-specific checklist; legacy facade remains unchanged. */
class MapTreasureChecklist(val map: ExplorationMap, val missionId: String) {
    val targets: List<WoodlandLandmark> = map.landmarks
    init { require(missionId.isNotBlank() && targets.isNotEmpty()) }
    private val targetIds: Set<String> = targets.mapTo(linkedSetOf()) { it.id }

    fun restoreCompleted(rawIds: Set<String>): Set<String> =
        rawIds.filterTo(linkedSetOf()) { it in targetIds }

    fun activeTarget(completedIds: Set<String>, preferredId: String?): WoodlandLandmark? {
        val completed = restoreCompleted(completedIds)
        if (completed.size == targets.size) return null
        return targets.firstOrNull { it.id == preferredId && it.id !in completed }
            ?: targets.first { it.id !in completed }
    }

    fun correct(targetId: String, answer: String): Boolean {
        val target = map.landmark(targetId) ?: return false
        if (target.id !in targetIds || answer.length != 4 ||
            answer.any { it !in '0'..'9' }) return false
        return target.cell == GridReference.parse(answer)
    }

    /** Wrong/duplicate answers do not advance or clear discoveries. */
    fun accept(completedIds: Set<String>, targetId: String, answer: String): Set<String> {
        val known = restoreCompleted(completedIds)
        return if (targetId in known || !correct(targetId, answer)) known
        else known + targetId
    }

    fun completed(completedIds: Set<String>): Boolean =
        restoreCompleted(completedIds).size == targets.size
}

object TreasureChecklistCatalog {
    val woodland by lazy { MapTreasureChecklist(ExplorationMaps.woodland, TreasureChecklistMission.missionId) }
    val coastalDraft by lazy { MapTreasureChecklist(WhisperingCoastDraft.map, "whispering-coast-findings-v1") }
    fun forAuthoredMap(map: ExplorationMap): MapTreasureChecklist? = when (map) {
        ExplorationMaps.woodland -> woodland
        WhisperingCoastDraft.map -> coastalDraft
        else -> null
    }
}
