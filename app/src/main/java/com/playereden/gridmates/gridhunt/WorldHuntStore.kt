package com.playereden.gridhunt

import android.content.Context
import com.playereden.gridhunt.core.ExplorationMap
import com.playereden.gridhunt.core.MapHuntSession
import com.playereden.gridhunt.core.MapHuntSessions
import com.playereden.gridhunt.core.RestoredMapHunt

/** Separate store for future map-scoped sessions. Never accesses legacy preferences. */
internal class WorldHuntStore(context: Context, private val map: ExplorationMap) {
    init {
        require(map.id.matches(Regex("[a-z0-9][a-z0-9-]{0,79}")))
    }
    internal val namespace = "gridhunt_world_hunt_v1_${map.id}_r${map.revision}"
    private val prefs = context.getSharedPreferences(namespace, Context.MODE_PRIVATE)

    sealed interface ReadResult {
        object Empty : ReadResult
        object Invalid : ReadResult
        data class Ready(val session: MapHuntSession, val restored: RestoredMapHunt) : ReadResult
    }

    /** Read one coherent snapshot. Missing or wrong-typed fields are not repaired. */
    fun read(): ReadResult {
        val values = prefs.all
        if (values.isEmpty()) return ReadResult.Empty
        if (values["schema"] != 1) return ReadResult.Invalid
        val session = MapHuntSession(
            mapId = values["map_id"] as? String ?: return ReadResult.Invalid,
            mapRevision = values["map_revision"] as? Int ?: return ReadResult.Invalid,
            mode = values["mode"] as? String ?: return ReadResult.Invalid,
            codes = values["codes"] as? String ?: return ReadResult.Invalid,
            kinds = values["kinds"] as? String ?: return ReadResult.Invalid,
            step = values["step"] as? Int ?: return ReadResult.Invalid
        )
        val restored = MapHuntSessions.restore(session, map) ?: return ReadResult.Invalid
        return ReadResult.Ready(session, restored)
    }

    /** UI path: validate then update the complete envelope atomically in memory; disk flush is asynchronous. */
    fun apply(session: MapHuntSession): Boolean {
        val editor = editorFor(session) ?: return false
        editor.apply()
        return true
    }

    /** Explicit replacement only, with validation before a single atomic editor commit. */
    fun write(session: MapHuntSession): Boolean {
        return editorFor(session)?.commit() ?: false
    }

    private fun editorFor(session: MapHuntSession): android.content.SharedPreferences.Editor? {
        if (MapHuntSessions.restore(session, map) == null) return null
        return prefs.edit()
            .putInt("schema", 1)
            .putString("map_id", session.mapId)
            .putInt("map_revision", session.mapRevision)
            .putString("mode", session.mode)
            .putString("codes", session.codes)
            .putString("kinds", session.kinds)
            .putInt("step", session.step)
    }
}
