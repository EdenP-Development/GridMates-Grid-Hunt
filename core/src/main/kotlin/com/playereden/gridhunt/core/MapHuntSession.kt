package com.playereden.gridhunt.core

/** Persistable, versioned session envelope for future multi-world play. */
data class MapHuntSession(
    val mapId: String,
    val mapRevision: Int,
    val mode: String,
    val codes: String,
    val kinds: String = "",
    val step: Int = 0
)

data class RestoredMapHunt(
    val map: ExplorationMap,
    val mode: String,
    val objectives: List<CustomHuntGenerator.Objective>,
    val step: Int
)

/** Read-only validation. Never regenerate, rewrite or silently change a saved map. */
object MapHuntSessions {
    fun restore(
        session: MapHuntSession,
        expectedMap: ExplorationMap
    ): RestoredMapHunt? {
        if (session.mapId != expectedMap.id || session.mapRevision != expectedMap.revision) return null
        val generator = MapHuntGenerator(expectedMap)
        val objectives = when (session.mode) {
            "mixed" -> generator.restoreMixed(session.codes, session.kinds)
            "grid", "gold", "identify", "checklist" -> {
                if (session.kinds.isNotEmpty()) return null
                val cells = if (session.mode in listOf("identify", "checklist"))
                    generator.restoreLandmarks(session.codes)
                else generator.restore(session.codes)
                cells.map { CustomHuntGenerator.Objective(session.mode, it) }
            }
            else -> return null
        }
        if (objectives.isEmpty() || session.step !in 0..objectives.size) return null
        return RestoredMapHunt(expectedMap, session.mode, objectives, session.step)
    }

    fun restorePublished(session: MapHuntSession): RestoredMapHunt? {
        val map = ExplorationMaps.find(session.mapId, session.mapRevision) ?: return null
        return restore(session, map)
    }

    /** Only the historical Woodland namespace may supply an implicit map identity. */
    fun legacyWoodland(mode: String, codes: String, kinds: String, rawStep: Int): RestoredMapHunt? {
        val map = ExplorationMaps.woodland
        val session = MapHuntSession(map.id, map.revision, mode, codes, kinds)
        val restored = restore(session, map) ?: return null
        // Match the original Woodland UI's clamped progress behavior.
        return restored.copy(step = rawStep.coerceIn(0, restored.objectives.size))
    }
}
