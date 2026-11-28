package com.playereden.gridhunt.core

/** Unpublished coastal finale. No timed hazards or unrendered marker dependencies. */
internal object CoastalLightExpedition {
    private val map = WhisperingCoastDraft.map
    private fun landmark(id: String) = requireNotNull(map.landmark(id))
    const val saveName = "gridhunt_coast_light_v1"
    val chapter = ExpeditionChapter(
        id = "coast-last-light-v1",
        revision = 1,
        name = "The Last Light",
        story = "The field notes are reunited. Before the survey closes, check four final chart entries and return the completed record to Beacon Point.",
        objectives = listOf(
            ExpeditionObjective("light-read-shell", ExpeditionVerb.LOCATE_LANDMARK,
                "Check the southern entry",
                "Find Shell Beach, the fan-shaped shell near the southern edge. Enter its four-digit square.",
                landmark("shell-beach").cell, landmarkId = "shell-beach",
                discovery = "The southern entry is confirmed. The next entry lies two squares east and two north.", map = map),
            ExpeditionObjective("light-offset-jetty", ExpeditionVerb.FIND_SQUARE,
                "Check the eastern entry",
                "From Shell Beach's square, count two squares east and two north. Select the resulting square.",
                landmark("old-jetty").cell, discovery = "The eastern entry is confirmed. Compare the next chart entry at 4455.", map = map),
            ExpeditionObjective("light-identify-gull", ExpeditionVerb.IDENTIFY_LANDMARK,
                "Confirm the northern feature",
                "The chart records square 4455. Which illustrated landmark matches that entry?",
                landmark("gull-rock").cell, landmarkId = "gull-rock",
                discovery = "The northern feature is confirmed. The final square is two east and one north of Gull Rock.", map = map),
            ExpeditionObjective("light-return-beacon", ExpeditionVerb.FIND_SQUARE,
                "Return to the beacon",
                "From Gull Rock's square, count two squares east and one north. Select the square containing the lighthouse.",
                landmark("beacon-point").cell,
                discovery = "The coastal survey is complete. Your journal holds a verified record of Whispering Coast.", map = map)
        ), map = map
    )
    val run = ExpeditionPlaythrough(chapter, saveName)
}
