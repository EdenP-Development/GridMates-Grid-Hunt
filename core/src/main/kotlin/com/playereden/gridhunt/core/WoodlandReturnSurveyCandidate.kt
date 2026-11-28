package com.playereden.gridhunt.core

/** GH-V45-V: isolated second navigation circuit after the original return trail. */
object WoodlandReturnSurveyCandidate {
    private val legacy = WoodlandReturnExpedition.chapter
    private fun square(id: String, title: String, prompt: String, ref: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.FIND_SQUARE, title, prompt,
            requireNotNull(legacy.map.cell(ref)), discovery = note)
    private fun marker(id: String, markerId: String, title: String, prompt: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.READ_MARKER, title, prompt,
            ReverseTraining.markers.single { it.id == markerId }.cell, markerId = markerId, discovery = note)

    val chapter = legacy.copy(revision = 2, objectives = legacy.objectives + listOf(
        square("return-survey-first", "Begin the navigation circuit",
            "The original circuit is complete. For a final navigation exercise, count two squares east and one north from the trailhead at 2437. Select the result.",
            "2638", "The first practice waypoint is recorded. Locate the nearby mushroom landmark."),
        ExpeditionObjective("return-survey-mushrooms", ExpeditionVerb.LOCATE_LANDMARK,
            "Confirm the woodland landmark",
            "Find Mushroom Nook and enter its four-digit square to establish a reference for the next leg.",
            WoodlandLandmarks.mushrooms.cell, landmarkId = WoodlandLandmarks.mushrooms.id,
            discovery = "The mushroom reference is recorded. Continue to the eastern survey light."),
        marker("return-survey-c", "C", "Read the eastern reference",
            "Read the four-digit square containing the active golden survey marker C.",
            "The eastern reference is recorded. Compare it with a landmark to the north."),
        ExpeditionObjective("return-survey-flowers", ExpeditionVerb.IDENTIFY_LANDMARK,
            "Identify the northern clearing",
            "From marker C's square count two squares north, keeping the easting unchanged. Which landmark occupies that square?",
            WoodlandLandmarks.flowers.cell, landmarkId = WoodlandLandmarks.flowers.id,
            discovery = "The clearing is confirmed. The next exercise turns west."),
        square("return-survey-west", "Plot the western waypoint",
            "From Buttercup Glade's square count one square west. Select the resulting square.",
            "2740", "The western waypoint is recorded. Read the northwest survey light."),
        marker("return-survey-d", "D", "Read the northwest reference",
            "Read the four-digit square containing the active golden survey marker D.",
            "The northwest reference is recorded. Compare the next waypoint to its position."),
        square("return-survey-north", "Check the northern waypoint",
            "From marker D's square count one square east and one north. Select the resulting square.",
            "2642", "The northern waypoint is confirmed. Record one last navigation checkpoint."),
        square("return-survey-close", "Close the navigation record",
            "From the preceding waypoint at 2642 count two squares east and one south. Select the resulting square.",
            "2841", "The second navigation circuit is complete. The woodland journal now contains eight additional navigation checks.")
    ))
    val plan = ChapterExpansionPlan(legacy, chapter)
    val run = ExpeditionPlaythrough(chapter, "gridhunt_candidate_return_survey_r2")
}
