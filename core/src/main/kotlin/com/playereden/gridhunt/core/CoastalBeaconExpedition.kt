package com.playereden.gridhunt.core

/** First unpublished coastal chapter. Uses only actual draft-map features. */
internal object CoastalBeaconExpedition {
    private val map = WhisperingCoastDraft.map
    private fun landmark(id: String) = requireNotNull(map.landmark(id))
    const val saveName = "gridhunt_coast_beacon_v1"
    val chapter = ExpeditionChapter(
        id = "coast-beacon-message-v1",
        revision = 1,
        name = "The Beacon's Message",
        story = "A coastal explorer has misplaced a survey chart. Follow four journal clues to reconstruct its route.",
        objectives = listOf(
            ExpeditionObjective(
                "chart-first-square", ExpeditionVerb.FIND_SQUARE, "Begin the survey",
                "The chart index gives 4256. Select that grid square.",
                GridReference.parse("4256"), discovery = "The first note points toward the lighthouse.", map = map
            ),
            ExpeditionObjective(
                "locate-beacon", ExpeditionVerb.LOCATE_LANDMARK, "Find Beacon Point",
                "Locate the striped lighthouse and enter its four-digit grid square.",
                landmark("beacon-point").cell, landmarkId = "beacon-point",
                discovery = "The lighthouse note names the next square: 4455.", map = map
            ),
            ExpeditionObjective(
                "identify-gull-rock", ExpeditionVerb.IDENTIFY_LANDMARK, "Read the coastal chart",
                "Which landmark appears in square 4455?",
                landmark("gull-rock").cell, landmarkId = "gull-rock",
                discovery = "The chart sketch points toward the wooden jetty.", map = map
            ),
            ExpeditionObjective(
                "locate-jetty", ExpeditionVerb.LOCATE_LANDMARK, "The final chart entry",
                "Find Old Jetty and enter the four-digit square containing its seaward tip.",
                landmark("old-jetty").cell, landmarkId = "old-jetty",
                discovery = "All four chart entries are recovered. The coastal survey can begin.", map = map
            )
        ),
        map = map
    )
    val run = ExpeditionPlaythrough(chapter, saveName)
}
