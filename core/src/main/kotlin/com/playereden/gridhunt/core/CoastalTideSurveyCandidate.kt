package com.playereden.gridhunt.core

/** GH-V45-Z: independent coastal route-check candidate, never published. */
object CoastalTideSurveyCandidate {
    private val legacy = CoastalTideExpedition.chapter
    private val map = legacy.map
    private fun square(id: String, title: String, prompt: String, ref: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.FIND_SQUARE, title, prompt,
            requireNotNull(map.cell(ref)), discovery = note, map = map)
    private fun marker(id: String, markerId: String, title: String, prompt: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.READ_MARKER, title, prompt,
            requireNotNull(SurveyMarkerCatalog.coastalDraft.find(markerId)).cell,
            markerId = markerId, discovery = note, map = map)
    private fun landmark(id: String) = requireNotNull(map.landmark(id))

    val chapter = legacy.copy(revision = 2, objectives = legacy.objectives + listOf(
        square("tide-survey-east", "Start the route cross-check",
            "The last supply stop is recorded. From Shell Beach's square count two squares east and one north. Select the result.",
            "4752", "The eastern checkpoint is recorded. Identify the feature one square north."),
        ExpeditionObjective("tide-survey-jetty", ExpeditionVerb.IDENTIFY_LANDMARK,
            "Recognize the eastern feature",
            "From the checkpoint at 4752 count one square north. Which landmark occupies that square?",
            landmark("old-jetty").cell, landmarkId = "old-jetty",
            discovery = "Old Jetty is confirmed. Check the central survey light.", map = map),
        marker("tide-survey-b", "B", "Read the central reference",
            "Read the four-digit square containing the active golden survey marker B.",
            "The central reference is recorded. Plot a waypoint north of it."),
        square("tide-survey-north", "Plot the northern checkpoint",
            "From marker B's square count one square east and two north. Select the result.",
            "4456", "The northern checkpoint is recorded. Confirm the nearby lighthouse."),
        ExpeditionObjective("tide-survey-beacon", ExpeditionVerb.LOCATE_LANDMARK,
            "Confirm the lighthouse reference",
            "Locate Beacon Point and enter its four-digit grid square.",
            landmark("beacon-point").cell, landmarkId = "beacon-point",
            discovery = "The lighthouse reference is confirmed. Check the western survey light.", map = map),
        marker("tide-survey-a", "A", "Read the western reference",
            "Read the four-digit square containing the active golden survey marker A.",
            "The western reference is recorded. Compare the next checkpoint to its position."),
        square("tide-survey-south", "Plot the southern checkpoint",
            "From marker A's square count one square east and three south. Select the resulting square.",
            "4353", "The southern checkpoint is recorded. Close the route record with one more offset."),
        square("tide-survey-close", "Complete the tide route record",
            "From the preceding checkpoint at 4353 count two squares east and one north. Select the result.",
            "4554", "The tide route record is complete, with eight additional checks in your journal.")
    ))
    val plan = ChapterExpansionPlan(legacy, chapter)
    val run = ExpeditionPlaythrough(chapter, "gridhunt_candidate_tide_survey_r2")
}
