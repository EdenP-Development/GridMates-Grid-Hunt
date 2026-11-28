package com.playereden.gridmates.gridhunt

import android.content.Context
import com.playereden.gridhunt.core.CampaignChapter
import com.playereden.gridhunt.core.CampaignSnapshot
import com.playereden.gridhunt.core.WorldSelection
import com.playereden.gridhunt.core.PublishedWorlds

/** Separate durable accomplishments. Existing chapter preference files are read only. */
internal class CampaignCompletionStore(
    private val context: Context,
    private val selection: WorldSelection,
    preview: Boolean
) {
    internal val namespace = if (preview) "gridhunt_campaign_earned_preview_v1" else "gridhunt_campaign_earned_v1"
    private val prefs = context.getSharedPreferences(namespace, Context.MODE_PRIVATE)
    private val chapters = selection.destinations.flatMap { it.world.chapters }
    // Preview can recognize exact published chapter accomplishments, but never
    // writes to the published ledger. Candidate-only chapters cannot inherit keys.
    private val publishedPrefs = if (preview) context.getSharedPreferences(
        "gridhunt_campaign_earned_v1", Context.MODE_PRIVATE) else null
    private val sharedPublishedChapters = if (preview) PublishedWorlds.selection.destinations
        .flatMap { it.world.chapters }.toSet() else emptySet<CampaignChapter>()
    private fun key(item: CampaignChapter) =
        "earned_${item.chapter.id}_r${item.chapter.revision}"

    fun snapshot(): CampaignSnapshot {
        val steps = chapters.associate { item ->
            item.chapter.id to (context.getSharedPreferences(item.progressKey,Context.MODE_PRIVATE)
                .all["step"] as? Int ?: 0)
        }
        val earned = prefs.all
        val publishedEarned = publishedPrefs?.all.orEmpty()
        return CampaignSnapshot(steps, chapters.filter {
            earned[key(it)] == true || (it in sharedPublishedChapters && publishedEarned[key(it)] == true)
        }
            .map { it.chapter.id }.toSet())
    }

    /**
     * Record only completed, accessible canonical chapters. Repeated passes are
     * idempotent. A synchronous commit is required before a destructive replay;
     * callers should invoke this on an IO dispatcher.
     * The old completion step remains intact if persistence fails.
     */
    fun captureCompleted(): Boolean {
        val snapshot = snapshot()
        val completed = selection.choices(snapshot).flatMap { it.chapters }
            .filter { it.status == com.playereden.gridhunt.core.CampaignChapterStatus.COMPLETE }
            .map { it.chapterId }.toSet()
        val pending = chapters.filter { it.chapter.id in completed &&
            it.chapter.id !in snapshot.earnedChapterIds &&
            snapshot.rawStepsByChapterId[it.chapter.id] == it.chapter.objectives.size }
        if (pending.isEmpty()) return true
        val editor = prefs.edit()
        pending.forEach { editor.putBoolean(key(it),true) }
        return editor.commit()
    }
}
