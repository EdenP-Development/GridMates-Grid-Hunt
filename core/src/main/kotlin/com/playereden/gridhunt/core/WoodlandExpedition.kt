package com.playereden.gridhunt.core

/**
 * GH-V13: ordered, mode-neutral objectives within ONE authored chapter.
 *
 * Each step uses the canonical 1 km grid, landmark, or Golden Spot data. A
 * chapter's stable ID, revision and objective IDs are saved independently of
 * the standalone modes. No unlocked stage is inferred from visual decoration.
 */
enum class ExpeditionVerb {
    FIND_SQUARE,
    READ_MARKER,
    LOCATE_LANDMARK,
    IDENTIFY_LANDMARK
}

data class ExpeditionObjective(
    val id: String,
    val verb: ExpeditionVerb,
    val title: String,
    val prompt: String,
    val expectedCell: GridCell,
    val landmarkId: String? = null,
    val markerId: String? = null
) {
    init {
        require(id.isNotBlank() && title.isNotBlank() && prompt.isNotBlank())
        require(expectedCell.digits == 4)
        when (verb) {
            ExpeditionVerb.FIND_SQUARE ->
                require(landmarkId == null && markerId == null)
            ExpeditionVerb.READ_MARKER ->
                require(markerId != null && landmarkId == null)
            ExpeditionVerb.LOCATE_LANDMARK,
            ExpeditionVerb.IDENTIFY_LANDMARK ->
                require(landmarkId != null && markerId == null)
        }
        require(WoodlandTrainingMap.bounds.contains(
            WorldPoint(expectedCell.westMeters.toDouble(), expectedCell.southMeters.toDouble())
        ))
    }
}

data class ExpeditionChapter(
    val id: String,
    val revision: Int,
    val name: String,
    val story: String,
    val objectives: List<ExpeditionObjective>
) {
    init {
        require(id.isNotBlank() && revision > 0 && name.isNotBlank() && story.isNotBlank())
        require(objectives.isNotEmpty())
        require(objectives.map { it.id }.distinct().size == objectives.size)
        // Fail publishing if authored story data diverges from canonical content.
        for (objective in objectives) {
            when (objective.verb) {
                ExpeditionVerb.FIND_SQUARE -> Unit
                ExpeditionVerb.READ_MARKER -> require(
                    ReverseTraining.markers.singleOrNull { it.id == objective.markerId }?.cell ==
                        objective.expectedCell
                )
                ExpeditionVerb.LOCATE_LANDMARK, ExpeditionVerb.IDENTIFY_LANDMARK -> require(
                    WoodlandLandmarks.byId(objective.landmarkId ?: "")?.cell ==
                        objective.expectedCell &&
                        WoodlandLandmarks.all.count { it.cell == objective.expectedCell } == 1
                )
            }
        }
    }
}

object WoodlandExpedition {
    const val saveName = "gridhunt_mixed_woodland_v1"
    val chapter = ExpeditionChapter(
        id = "woodland-missing-notes-v1",
        revision = 1,
        name = "The Missing Field Notes",
        story = "Four clues are scattered through the woodland. Each asks for a different kind of discovery.",
        objectives = listOf(
            ExpeditionObjective(
                "locate-trailhead", ExpeditionVerb.FIND_SQUARE, "Find the first page",
                "One page has a four-digit address. Tap that square on the map: 2437.",
                GridReference.parse("2437")
            ),
            ExpeditionObjective(
                "read-glow-a", ExpeditionVerb.READ_MARKER, "Follow the golden glow",
                "A golden spot marks the next page. What four-digit square is the dot inside?",
                ReverseTraining.markers.first { it.id == "A" }.cell, markerId = "A"
            ),
            ExpeditionObjective(
                "find-mushrooms", ExpeditionVerb.LOCATE_LANDMARK, "Visit Mushroom Nook",
                "Match the little mushrooms on the map, then enter their square's four digits.",
                WoodlandLandmarks.mushrooms.cell, landmarkId = WoodlandLandmarks.mushrooms.id
            ),
            ExpeditionObjective(
                "identify-waystone", ExpeditionVerb.IDENTIFY_LANDMARK, "The last mystery",
                "Read square 2941. Which illustrated landmark can you find inside it?",
                WoodlandLandmarks.waystone.cell, landmarkId = WoodlandLandmarks.waystone.id
            )
        )
    )
    val size get() = chapter.objectives.size

    fun restoreStep(raw: Int): Int = raw.coerceIn(0, size)
    fun objectiveAt(step: Int): ExpeditionObjective? =
        chapter.objectives.getOrNull(restoreStep(step))
    fun finished(step: Int) = restoreStep(step) == size
    fun solvedCells(step: Int): List<GridCell> =
        chapter.objectives.take(restoreStep(step)).map { it.expectedCell }

    /** Grid-square answers for map tapping and four-digit code entry. */
    fun acceptGrid(step: Int, submitted: String?): Int {
        val index = restoreStep(step)
        val objective = objectiveAt(index) ?: return index
        if (objective.verb == ExpeditionVerb.IDENTIFY_LANDMARK) return index
        if (submitted == null || submitted.length != 4 ||
            submitted.any { it !in '0'..'9' }) return index
        val attempted = runCatching { GridReference.parse(submitted) }.getOrNull()
        return if (attempted == objective.expectedCell) index + 1 else index
    }

    /** Stable ID rather than comparing a picture label or visible list index. */
    fun acceptLandmark(step: Int, submittedId: String?): Int {
        val index = restoreStep(step)
        val objective = objectiveAt(index) ?: return index
        if (objective.verb != ExpeditionVerb.IDENTIFY_LANDMARK ||
            WoodlandLandmarks.byId(submittedId ?: "") == null) return index
        return if (submittedId == objective.landmarkId) index + 1 else index
    }
}
