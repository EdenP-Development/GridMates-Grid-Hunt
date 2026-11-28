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
    // Journal history is derived from earned progress, never a second save.
    fun discoveries(raw: Int): List<ExpeditionObjective> =
        chapter.objectives.take(restoreStep(raw))
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
            chapter.map.landmark(choiceId ?: "") == null) return step
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
                "A supply note points to golden marker B. " +
                    "Read the four-digit square containing its glow.",
                ReverseTraining.markers.single { it.id == "B" }.cell,
                markerId = "B",
                discovery = "The marker note mentions a clearing of pale flowers."
            ),
            ExpeditionObjective(
                "visit-buttercups", ExpeditionVerb.LOCATE_LANDMARK,
                "The flower clearing",
                "Follow the flower clue from the marker note. " +
                    "Find Buttercup Glade and enter its grid square.",
                WoodlandLandmarks.flowers.cell,
                landmarkId = WoodlandLandmarks.flowers.id,
                discovery = "The clearing note records another stop at 2641."
            ),
            ExpeditionObjective(
                "decode-acorn", ExpeditionVerb.IDENTIFY_LANDMARK,
                "A clue in the north",
                "The clearing note gives the next stop: square 2641. " +
                    "Which landmark appears in that square?",
                WoodlandLandmarks.acorn.cell,
                landmarkId = WoodlandLandmarks.acorn.id,
                discovery = "The acorn note names the last stop: the Old Waystone."
            ),
            ExpeditionObjective(
                "last-seen-waystone", ExpeditionVerb.LOCATE_LANDMARK,
                "The last known stop",
                "The acorn note says the missing supplies were last seen " +
                    "near the Old Waystone. Locate the stone and read its four-digit square.",
                WoodlandLandmarks.waystone.cell,
                landmarkId = WoodlandLandmarks.waystone.id,
                discovery = "The last known supply stop is recorded in your journal."
            )
        )
    )
    val run = ExpeditionPlaythrough(chapter, saveName)
}
