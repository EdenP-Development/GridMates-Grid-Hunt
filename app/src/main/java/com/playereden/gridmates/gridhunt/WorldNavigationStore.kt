package com.playereden.gridmates.gridhunt

import android.content.Context
import com.playereden.gridhunt.core.*

/** Read-only campaign progress bridge and isolated world selection preference.
 * Default callers can only select published destinations. Preview callers
 * must explicitly supply a candidate selection and never share its preference.
 */
internal class WorldNavigationStore(
    private val context: Context,
    private val selection: WorldSelection = PublishedWorlds.selection,
    private val preview: Boolean = false
) {
    init { require(preview || selection === PublishedWorlds.selection) }
    private val prefs = context.getSharedPreferences(
        if (preview) "gridhunt_world_selection_preview_v1" else "gridhunt_world_selection_v1",
        Context.MODE_PRIVATE
    )

    fun snapshot(): CampaignSnapshot = CampaignSnapshot(
        selection.destinations.flatMap { it.world.chapters }.associate { item ->
            val raw = context.getSharedPreferences(item.progressKey, Context.MODE_PRIVATE)
                .all["step"] as? Int ?: 0
            item.chapter.id to raw
        }
    )

    fun choices(): List<WorldChoice> = selection.choices(snapshot())

    fun current(): WorldChoice? = selection.resolve(
        prefs.all["world_id"] as? String, snapshot()
    )

    /** Locked or unknown destinations never change stored selection. */
    fun choose(worldId: String): Boolean {
        val choice = selection.select(worldId, snapshot()) ?: return false
        prefs.edit().putString("world_id", choice.destination.world.id).apply()
        return true
    }

    /** Recheck prerequisites when opening a chapter, not just when rendering cards. */
    fun openChapter(worldId: String, chapterId: String): CampaignChapter? {
        val choice = selection.select(worldId, snapshot()) ?: return null
        val progress = choice.chapters.firstOrNull { it.chapterId == chapterId } ?: return null
        if (progress.status == CampaignChapterStatus.LOCKED) return null
        return choice.destination.world.chapters.firstOrNull { it.chapter.id == chapterId }
    }
}
