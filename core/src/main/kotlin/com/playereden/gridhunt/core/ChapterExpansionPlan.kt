package com.playereden.gridhunt.core

/**
 * Read-only expansion candidate. No persistence, campaign unlock or live chapter
 * changes. The exact legacy objective prefix is retained, not just its length.
 */
class ChapterExpansionPlan(
    val legacy: ExpeditionChapter,
    val expanded: ExpeditionChapter
) {
    init {
        require(legacy.id == expanded.id)
        require(expanded.revision > legacy.revision)
        require(legacy.map == expanded.map)
        require(expanded.objectives.size > legacy.objectives.size)
        require(expanded.objectives.take(legacy.objectives.size) == legacy.objectives) {
            "Legacy objective identity and answer contract must remain unchanged"
        }
    }

    private val legacyIds = legacy.objectives.map { it.id }
    private val expandedIds = expanded.objectives.map { it.id }.toSet()

    /**
     * Project only valid legacy steps. Missing/malformed values cannot grant
     * accomplishments. Explicit historical entitlement does not imply a
     * currently completed replay. Caller must supply revision-validated earned
     * status from the existing ledger.
     */
    fun project(rawLegacyStep: Int?, legacyEarned: Boolean): ExpansionProgress? {
        val step = rawLegacyStep ?: 0
        if (step !in 0..legacyIds.size) return null
        return ExpansionProgress(
            chapterId = expanded.id,
            revision = expanded.revision,
            solvedObjectiveIds = legacyIds.take(step).toSet(),
            legacyEntitlement = legacyEarned || step == legacyIds.size
        )
    }

    /** Validate exact chapter/revision and objective membership before use. */
    fun accepts(progress: ExpansionProgress): Boolean =
        progress.chapterId == expanded.id &&
            progress.revision == expanded.revision &&
            progress.solvedObjectiveIds.all { it in expandedIds }

    fun completedObjectives(progress: ExpansionProgress): Int {
        require(accepts(progress))
        return progress.solvedObjectiveIds.size
    }

    fun isExpandedComplete(progress: ExpansionProgress): Boolean =
        completedObjectives(progress) == expandedIds.size
}

/** Separate historical access from actual solved objectives in this run. */
data class ExpansionProgress(
    val chapterId: String,
    val revision: Int,
    val solvedObjectiveIds: Set<String>,
    val legacyEntitlement: Boolean
)
