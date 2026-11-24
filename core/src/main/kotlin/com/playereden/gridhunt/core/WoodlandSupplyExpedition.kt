package com.playereden.gridhunt.core

/**
 * Shared, small expedition runtime. Content is authored separately; saves are
 * isolated by each chapter's namespace. The original Chapter 1 object remains
 * intact so old installed games keep their progress and public core tests.
 */
class ExpeditionPlaythrough(
    val chapter: ExpeditionChapter,
    val saveName: String
) {
    init { require(saveName.isNotBlank()) }
    val size: Int get() = chapter.objectives.size
    fun restoreStep(raw: Int) = raw.coerceIn(0, size)
    fun objectiveAt(raw: Int): ExpeditionObjective? =
        chapter.objectives.getOrNull(restoreStep(raw))
    fun finished(raw: Int): Boolean = restoreStep(raw) == size
    fun solvedCells(raw: Int): List<GridCell> =
        chapter.objectives.take(restoreStep(raw)).map { it.expectedCell }

    fun acceptGrid(raw: Int, submitted: String?): Int {
        val step = restoreStep(raw)
        val objective = objectiveAt(step) ?: return step
        if (objective.verb == ExpeditionVerb.IDENTIFY_LANDMARK ||
            submitted == null || submitted.length != 4 ||
            submitted.any { it !in '0'..'9' }) return step
        return if (runCatching { GridReference.parse(submitted) }.getOrNull() ==
            objective.expectedCell) step + 1 else step
    }

    fun acceptLandmark(raw: Int, choiceId: String?): Int {
        val step = restoreStep(raw)
        val objective = objectiveAt(step) ?: return step
        if (objective.verb != ExpeditionVerb.IDENTIFY_LANDMARK ||
            WoodlandLandmarks.byId(choiceId ?: "") == null) return step
        return if (choiceId == objective.landmarkId) step + 1 else step
    }
}

/**
 * A second *real* Little Woodland chapter, built entirely with the four
 * canonical visible woodland landmarks and an existing Golden Spot.
 * No invented supply-cache marker is depicted as present on the map.
 */
object WoodlandSupplyExpedition {
    const val saveName = "gridhunt_woodland_supplies_v1"
    val chapter = ExpeditionChapter(
        id = "woodland-lost-supplies-v1",
        revision = 1,
        name = "The Lost Supplies",
        story = "A little bundle of supplies has been misplaced on the trail. " +
            "Follow four notes to discover the last place it was seen.",
        objectives = listOf(
            ExpeditionObjective(
                "follow-glow-b", ExpeditionVerb.READ_MARKER,
                "A glimmer on the trail",
                "The first note says to check golden marker B. " +
                    "Read the four-digit square containing its glow.",
                ReverseTraining.markers.single { it.id == "B" }.cell,
                markerId = "B"
            ),
            ExpeditionObjective(
                "visit-buttercups", ExpeditionVerb.LOCATE_LANDMARK,
                "The flower clearing",
                "The next note mentions pale flowers in an eastern clearing. " +
                    "Find Buttercup Glade and enter its grid square.",
                WoodlandLandmarks.flowers.cell,
                landmarkId = WoodlandLandmarks.flowers.id
            ),
            ExpeditionObjective(
                "decode-acorn", ExpeditionVerb.IDENTIFY_LANDMARK,
                "A clue in the north",
                "The trail journal points to square 2641. " +
                    "Which landmark appears in that square?",
                WoodlandLandmarks.acorn.cell,
                landmarkId = WoodlandLandmarks.acorn.id
            ),
            ExpeditionObjective(
                "last-seen-waystone", ExpeditionVerb.LOCATE_LANDMARK,
                "The last known stop",
                "A final message says the missing supplies were last seen " +
                    "near the Old Waystone. Locate the stone and read its four-digit square.",
                WoodlandLandmarks.waystone.cell,
                landmarkId = WoodlandLandmarks.waystone.id
            )
        )
    )
    val run = ExpeditionPlaythrough(chapter, saveName)
}
