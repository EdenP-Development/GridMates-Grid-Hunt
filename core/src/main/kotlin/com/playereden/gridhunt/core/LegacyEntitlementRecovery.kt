package com.playereden.gridhunt.core

/**
 * Candidate-only recovery proposal for trusted, local, canonical legacy saves.
 * Does not mutate preferences or change CampaignRegistry behavior.
 *
 * A valid positive step in a chapter that was historically gated is evidence
 * that its prerequisites were once completed. A zero step alone is not evidence
 * of access: a player may never have opened the chapter.
 *
 * Inference is limited to explicitly audited chapter IDs whose historical
 * entry paths enforced these prerequisites. Do not use for remote/imported
 * progress, changed prerequisite graphs, or unknown save revisions.
 */
class LegacyEntitlementRecovery(
    private val registry: CampaignRegistry,
    auditedGatedChapterIds: Set<String>
) {
    private val audited = auditedGatedChapterIds.toSet()

    init {
        require(audited.all { registry.findChapter(it) != null })
    }

    data class Proposal(
        val explicitEarnedIds: Set<String>,
        val inferredPrerequisiteIds: Set<String>,
        val invalidStepChapterIds: Set<String>
    )

    fun propose(snapshot: CampaignSnapshot): Proposal {
        val explicit = registry.chapters.map { it.chapter.id }
            .filter { it in snapshot.earnedChapterIds }.toSet()
        val invalid = registry.chapters.filter {
            val raw = snapshot.rawStepsByChapterId[it.chapter.id]
            raw != null && raw !in 0..it.chapter.objectives.size
        }.map { it.chapter.id }.toSet()
        val inferred = mutableSetOf<String>()

        fun visitPrerequisites(id: String) {
            val item = registry.findChapter(id) ?: return
            // Only traverse edges whose historical gating has been audited.
            if (id !in audited) return
            for (dependency in item.prerequisiteChapterIds) {
                if (inferred.add(dependency)) visitPrerequisites(dependency)
            }
        }

        registry.chapters.forEach { item ->
            val id = item.chapter.id
            val raw = snapshot.rawStepsByChapterId[id] ?: 0
            if (id in explicit || (id !in invalid && raw > 0)) {
                visitPrerequisites(id)
            }
        }

        return Proposal(explicit, inferred - explicit, invalid)
    }
}
