package com.playereden.gridhunt.core

/** Unpublished chapter 2: directions refer to grid squares, not walking routes. */
internal object CoastalTideExpedition {
    private val map = WhisperingCoastDraft.map
    private fun landmark(id: String) = requireNotNull(map.landmark(id))
    const val saveName = "gridhunt_coast_tide_v1"
    val chapter = ExpeditionChapter(
        id = "coast-tide-trail-v1",
        revision = 1,
        name = "Along the Tide Trail",
        story = "The recovered chart lists four survey stops. Reconnect the scattered field notes and locate the last recorded supply stop.",
        objectives = listOf(
            ExpeditionObjective("tide-from-beacon", ExpeditionVerb.FIND_SQUARE,
                "Count south from the beacon",
                "Start at Beacon Point's square. Count two grid squares south, keeping the easting unchanged. Select the resulting square.",
                landmark("tidepool-bend").cell, discovery = "The survey note describes a ring of rocks around a pool.", map = map),
            ExpeditionObjective("identify-tidepool", ExpeditionVerb.IDENTIFY_LANDMARK,
                "Recognize the survey feature",
                "Which landmark is in square 4654?", landmark("tidepool-bend").cell,
                landmarkId = "tidepool-bend", discovery = "The pool note points three squares west and two squares south.", map = map),
            ExpeditionObjective("tide-to-cove", ExpeditionVerb.FIND_SQUARE,
                "Follow the grid offsets",
                "From Tidepool Bend's square, count three squares west and two squares south. Select the resulting square.",
                landmark("driftwood-cove").cell, discovery = "The cove note records the last stop two squares east and one south.", map = map),
            ExpeditionObjective("identify-shell-stop", ExpeditionVerb.IDENTIFY_LANDMARK,
                "Record the last supply stop",
                "From Driftwood Cove's square, count two squares east and one south. Which landmark is in the resulting square?",
                landmark("shell-beach").cell, landmarkId = "shell-beach",
                discovery = "The field notes are reunited and the last known supply stop is recorded.", map = map)
        ), map = map
    )
    val run = ExpeditionPlaythrough(chapter, saveName)
}
