package com.playereden.gridhunt.core

/** GH-V45-P: isolated revision-two candidate; never registered or persisted by live UI. */
object WoodlandFieldSurveyCandidate {
    private val legacy = WoodlandExpedition.chapter
    private fun square(id: String, title: String, prompt: String, ref: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.FIND_SQUARE, title, prompt,
            requireNotNull(legacy.map.cell(ref)), discovery = note)
    private fun marker(id: String, markerId: String, title: String, prompt: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.READ_MARKER, title, prompt,
            ReverseTraining.markers.single { it.id == markerId }.cell, markerId = markerId, discovery = note)
    val chapter = legacy.copy(
        revision = 2,
        objectives = legacy.objectives + listOf(
            square("survey-south-baseline", "Open the survey appendix",
                "The journal is recovered. Begin a separate survey: from Mushroom Nook's square count one square south. Tap the result.",
                "2537", "Southern baseline recorded. Check the eastern survey light next."),
            marker("survey-glow-c", "C", "Read the eastern light",
                "Read the four-digit square containing the active golden survey marker C.",
                "Eastern light recorded. Compare its position with the flower clearing."),
            ExpeditionObjective("survey-identify-flowers", ExpeditionVerb.IDENTIFY_LANDMARK,
                "Compare the clearing", "From marker C's square count two squares north. Which landmark occupies that square?",
                WoodlandLandmarks.flowers.cell, landmarkId = WoodlandLandmarks.flowers.id,
                discovery = "The flower clearing confirms the eastern column."),
            square("survey-west-offset", "Cross-check the middle",
                "From Buttercup Glade's square count two squares west and one south. Tap the resulting square.",
                "2639", "Middle survey square recorded. Check the northern acorn."),
            ExpeditionObjective("survey-locate-acorn", ExpeditionVerb.LOCATE_LANDMARK,
                "Record the acorn", "Find Acorn Nook and enter its four-digit square to record the northern landmark.",
                WoodlandLandmarks.acorn.cell, landmarkId = WoodlandLandmarks.acorn.id,
                discovery = "Acorn Nook is recorded. Read the western survey light."),
            marker("survey-glow-d", "D", "Read the western light",
                "Read the four-digit square containing the active golden survey marker D.",
                "Western light recorded. The final survey line runs east."),
            square("survey-east-offset", "Complete the survey line",
                "From marker D's square count two squares east, keeping the northing unchanged. Tap the resulting square.",
                "2741", "The survey line is complete. Record the final northern light."),
            marker("survey-glow-e", "E", "Close the appendix",
                "Read the four-digit square containing the active golden survey marker E.",
                "The field survey appendix is complete. The recovered journal now includes eight additional survey entries.")
        )
    )
    val plan = ChapterExpansionPlan(legacy, chapter)
    val run = ExpeditionPlaythrough(chapter, "gridhunt_candidate_field_survey_r2")
}
