package com.playereden.gridhunt.core

/** GH-V45-X: unpublished coastal chart extension, separate from the original preview. */
object CoastalBeaconSurveyCandidate {
    private val legacy = CoastalBeaconExpedition.chapter
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
        square("beacon-survey-first", "Extend the recovered chart",
            "The four chart entries are recovered. From Old Jetty's square count one square west and one north. Select the result.",
            "4654", "The eastern survey square is recorded. Check the central survey light."),
        marker("beacon-survey-c", "C", "Read the central light",
            "Read the four-digit square containing the active golden survey marker C.",
            "The central reference is recorded. Locate the southern inlet."),
        ExpeditionObjective("beacon-survey-cove", ExpeditionVerb.IDENTIFY_LANDMARK,
            "Identify the inlet feature",
            "From marker C's square count one square west and one south. Which landmark occupies that square?",
            landmark("driftwood-cove").cell, landmarkId = "driftwood-cove",
            discovery = "Driftwood Cove is confirmed. Record the southern beach next.", map = map),
        ExpeditionObjective("beacon-survey-shell", ExpeditionVerb.LOCATE_LANDMARK,
            "Record the beach",
            "Find Shell Beach and enter the four-digit square containing its fan-shaped shell.",
            landmark("shell-beach").cell, landmarkId = "shell-beach",
            discovery = "The southern beach is recorded. Check the western survey light.", map = map),
        marker("beacon-survey-d", "D", "Read the western light",
            "Read the four-digit square containing the active golden survey marker D.",
            "The western reference is recorded. Plot a waypoint northeast of it."),
        square("beacon-survey-northeast", "Plot the middle waypoint",
            "From marker D's square count two squares east and two north. Select the resulting square.",
            "4454", "The middle waypoint is recorded. Check the northern survey light."),
        marker("beacon-survey-e", "E", "Read the northern light",
            "Read the four-digit square containing the active golden survey marker E.",
            "The northern reference is recorded. Close the chart with one final offset."),
        square("beacon-survey-close", "Complete the chart appendix",
            "From marker E's square count two squares west and one south. Select the resulting square.",
            "4355", "The recovered chart now includes eight additional coastal survey entries.")
    ))
    val plan = ChapterExpansionPlan(legacy, chapter)
    val run = ExpeditionPlaythrough(chapter, "gridhunt_candidate_beacon_survey_r2")
}
