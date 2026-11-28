package com.playereden.gridhunt.core

/** GH-V45-AB: independent final chart verification after the original survey closes. */
object CoastalLightSurveyCandidate {
    private val legacy = CoastalLightExpedition.chapter
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
        square("light-survey-west", "Open the verification record",
            "The original survey is complete. For an independent chart check, count one square west and one south from Beacon Point. Select the result.",
            "4555", "The opening checkpoint is recorded. Identify the pool to the southeast."),
        ExpeditionObjective("light-survey-pool", ExpeditionVerb.IDENTIFY_LANDMARK,
            "Identify the pool reference",
            "From the checkpoint at 4555 count one square east and one south. Which landmark occupies that square?",
            landmark("tidepool-bend").cell, landmarkId = "tidepool-bend",
            discovery = "Tidepool Bend is confirmed. Read the central survey light.", map = map),
        marker("light-survey-c", "C", "Read the central light",
            "Read the four-digit square containing the active golden survey marker C.",
            "The central reference is recorded. Locate the inlet to its southwest."),
        ExpeditionObjective("light-survey-cove", ExpeditionVerb.LOCATE_LANDMARK,
            "Confirm the inlet reference",
            "Find Driftwood Cove and enter its four-digit grid square.",
            landmark("driftwood-cove").cell, landmarkId = "driftwood-cove",
            discovery = "The inlet reference is confirmed. Check the western survey light.", map = map),
        marker("light-survey-d", "D", "Read the western light",
            "Read the four-digit square containing the active golden survey marker D.",
            "The western reference is recorded. Plot a checkpoint three squares north."),
        square("light-survey-north", "Plot the western checkpoint",
            "From marker D's square count three squares north, keeping the easting unchanged. Select the result.",
            "4255", "The western checkpoint is recorded. Read the northern survey light."),
        marker("light-survey-e", "E", "Confirm the northern light",
            "Read the four-digit square containing the active golden survey marker E.",
            "The northern reference is confirmed. Record the final verification square."),
        square("light-survey-close", "Seal the coastal record",
            "From marker E's square count two squares west and two south. Select the resulting square.",
            "4354", "The independent chart verification is complete. Your journal holds the original survey and eight additional checks.")
    ))
    val plan = ChapterExpansionPlan(legacy, chapter)
    val run = ExpeditionPlaythrough(chapter, "gridhunt_candidate_light_survey_r2")
}
