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
    val markerId: String? = null,
    val discovery: String = "",
    val map: ExplorationMap = ExplorationMaps.woodland
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
        require(map.bounds.contains(
            WorldPoint(expectedCell.westMeters.toDouble(), expectedCell.southMeters.toDouble())
        ))
    }
}

data class ExpeditionChapter(
    val id: String,
    val revision: Int,
    val name: String,
    val story: String,
    val objectives: List<ExpeditionObjective>,
    val map: ExplorationMap = ExplorationMaps.woodland
) {
    init {
        require(id.isNotBlank() && revision > 0 && name.isNotBlank() && story.isNotBlank())
        require(objectives.isNotEmpty())
        require(objectives.map { it.id }.distinct().size == objectives.size)
        // Fail publishing if authored story data diverges from canonical content.
        for (objective in objectives) {
            require(objective.map == map) { "Objective map must match chapter map" }
            when (objective.verb) {
                ExpeditionVerb.FIND_SQUARE -> Unit
                ExpeditionVerb.READ_MARKER -> require(
                    SurveyMarkerCatalog.forAuthoredMap(map)?.find(objective.markerId ?: "")?.cell ==
                        objective.expectedCell
                )
                ExpeditionVerb.LOCATE_LANDMARK, ExpeditionVerb.IDENTIFY_LANDMARK -> require(
                    map.landmark(objective.landmarkId ?: "")?.cell ==
                        objective.expectedCell &&
                        map.landmarks.count { it.cell == objective.expectedCell } == 1
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
        story = "A woodland explorer has lost four pages of a field journal. Follow the notes to piece the journal together.",
        objectives = listOf(
            ExpeditionObjective(
                "locate-trailhead", ExpeditionVerb.FIND_SQUARE, "Find the first page",
                "The journal cover lists the first page at 2437. Tap that square to begin the trail.",
                GridReference.parse("2437"),
                discovery = "First page recovered. It mentions a golden light." 
            ),
            ExpeditionObjective(
                "read-glow-a", ExpeditionVerb.READ_MARKER, "Follow the golden glow",
                "The first page describes a golden light. Read the four-digit square containing the dot.",
                ReverseTraining.markers.first { it.id == "A" }.cell, markerId = "A",
                discovery = "Second page recovered. A mushroom sketch leads onward."
            ),
            ExpeditionObjective(
                "find-mushrooms", ExpeditionVerb.LOCATE_LANDMARK, "Visit Mushroom Nook",
                "The mushroom sketch points to Mushroom Nook. Find it and enter its four-digit square.",
                WoodlandLandmarks.mushrooms.cell, landmarkId = WoodlandLandmarks.mushrooms.id,
                discovery = "Third page recovered. The final note gives a grid address."
            ),
            ExpeditionObjective(
                "identify-waystone", ExpeditionVerb.IDENTIFY_LANDMARK, "The last mystery",
                "The third page gives the last address: 2941. Which illustrated landmark is inside?",
                WoodlandLandmarks.waystone.cell, landmarkId = WoodlandLandmarks.waystone.id,
                discovery = "Final page recovered. The field journal is whole again."
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
