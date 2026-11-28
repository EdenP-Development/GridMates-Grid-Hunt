package com.playereden.gridhunt.core

import java.util.UUID
import java.util.Collections

/**
 * Pure candidate model for a persistent project. No disk/network access.
 * A project ID is independent of its authored content fingerprint.
 */
data class ExpeditionIdentity(
    val projectId: UUID,
    val chapterId: String,
    val chapterRevision: Int,
    val manifestSchema: Int,
    val manifestFingerprint: String
)

class ExpeditionProgressContract(chapter: ExpeditionChapter, projectId: UUID) {
    val identity = ExpeditionIdentity(projectId, chapter.id, chapter.revision,
        ChapterManifest.schemaVersion, ChapterManifest.fingerprint(chapter))
    private val objectiveIds = chapter.objectives.map { it.id }.toSet()

    /** Immutable defensive snapshot; membership is checked by the contract. */
    class Snapshot internal constructor(
        val identity: ExpeditionIdentity,
        solved: Set<String>
    ) {
        val solvedObjectiveIds: Set<String> =
            Collections.unmodifiableSet(LinkedHashSet(solved))
    }

    fun restore(identity: ExpeditionIdentity, solved: Set<String>): Snapshot? {
        if (identity != this.identity || !solved.all { it in objectiveIds }) return null
        return Snapshot(identity, solved)
    }

    fun empty(): Snapshot = Snapshot(identity, emptySet())

    /**
     * Union only already validated discoveries from trusted replicas.
     * Membership is not proof of solving an answer; authentication and
     * gameplay event validation must precede any future remote import.
     * No historical campaign entitlements or replay resets are merged.
     */
    fun reconcile(left: Snapshot, right: Snapshot): Snapshot? {
        if (left.identity != identity || right.identity != identity) return null
        return restore(identity, left.solvedObjectiveIds + right.solvedObjectiveIds)
    }
}
