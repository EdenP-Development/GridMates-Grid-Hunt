package com.playereden.gridhunt.core

/**
 * First playable chapter: three intentionally authored four-digit hunts.
 * All references designate 1km AREAS in the fictional WoodlandTrainingMap.
 * Chapter content never changes canonical grid mathematics.
 */
data class ChapterHunt(
    val id: String,
    val name: String,
    val purpose: String,
    val objectives: List<GridCell>
) {
    init {
        require(id.isNotBlank() && name.isNotBlank())
        require(objectives.isNotEmpty() && objectives.distinct().size == objectives.size)
        require(objectives.all { it.digits == 4 })
    }
}

data class ChapterPosition(val huntIndex: Int, val objectiveIndex: Int)

object FirstBearingsChapter {
    const val chapterId = "first-bearings-v1"
    private fun authored(vararg references: String): List<GridCell> =
        references.map(GridReference::parse)

    val hunts = listOf(
        ChapterHunt(
            "trailhead", "A Walk in the Woods",
            "Follow the first three clues through the trees.",
            authored("2437", "2539", "2741")
        ),
        ChapterHunt(
            "brook", "Across the Brook",
            "Discover three more hidden squares near the water.",
            authored("2938", "2642", "2839")
        ),
        ChapterHunt(
            "final-notes", "One Last Discovery",
            "Track down the final three woodland clues.",
            authored("2537", "2841", "2940")
        )
    )

    init {
        val all = hunts.flatMap { it.objectives }
        require(hunts.map { it.id }.distinct().size == hunts.size)
        require(all.distinct().size == all.size)
        require(all.all { cell ->
            WoodlandTrainingMap.bounds.contains(
                WorldPoint(cell.westMeters.toDouble(), cell.southMeters.toDouble())
            ) && WoodlandTrainingMap.bounds.contains(
                WorldPoint(cell.westMeters + 999.0, cell.southMeters + 999.0)
            )
        })
    }

    /** Clamp persisted data after upgrades or storage corruption. */
    fun restore(huntIndex: Int, objectiveIndex: Int): ChapterPosition {
        val h = huntIndex.coerceIn(0, hunts.lastIndex)
        val step = objectiveIndex.coerceIn(0, hunts[h].objectives.size)
        return ChapterPosition(h, step)
    }

    fun finished(position: ChapterPosition): Boolean {
        val p = restore(position.huntIndex, position.objectiveIndex)
        return p.objectiveIndex == hunts[p.huntIndex].objectives.size
    }

    /** Advance only after the validator accepts one canonical 1km answer. */
    fun acceptCorrect(position: ChapterPosition): ChapterPosition {
        val p = restore(position.huntIndex, position.objectiveIndex)
        require(!finished(p)) { "Cannot accept an answer after hunt completion" }
        return p.copy(objectiveIndex = p.objectiveIndex + 1)
    }

    /** Finishing the third hunt returns to the first on an explicit replay. */
    fun nextHuntOrReplay(position: ChapterPosition): ChapterPosition {
        val p = restore(position.huntIndex, position.objectiveIndex)
        require(finished(p)) { "Complete this hunt before advancing" }
        return if (p.huntIndex == hunts.lastIndex) ChapterPosition(0, 0)
        else ChapterPosition(p.huntIndex + 1, 0)
    }
}
