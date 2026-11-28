package com.playereden.gridhunt.core

/** GH-V45-T: isolated supply-route extension; no live registry or preference migration. */
object WoodlandSupplySurveyCandidate {
    private val legacy = WoodlandSupplyExpedition.chapter
    private fun square(id: String, title: String, prompt: String, ref: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.FIND_SQUARE, title, prompt,
            requireNotNull(legacy.map.cell(ref)), discovery = note)
    private fun marker(id: String, markerId: String, title: String, prompt: String, note: String) =
        ExpeditionObjective(id, ExpeditionVerb.READ_MARKER, title, prompt,
            ReverseTraining.markers.single { it.id == markerId }.cell, markerId = markerId, discovery = note)

    val chapter = legacy.copy(revision = 2, objectives = legacy.objectives + listOf(
        square("supply-route-east", "Trace the return route",
            "The last known stop is recorded. From Old Waystone's square count two squares south. Select that square.",
            "2939", "The first return-route entry is recorded. Check the southern survey light."),
        marker("supply-light-c", "C", "Read the southern light",
            "Read the four-digit square containing the active golden survey marker C.",
            "The southern reference is confirmed. Trace the route west along the same northing."),
        ExpeditionObjective("supply-identify-mushroom", ExpeditionVerb.IDENTIFY_LANDMARK,
            "Recognize the western stop",
            "From marker C's square count three squares west. Which landmark is in the resulting square?",
            WoodlandLandmarks.mushrooms.cell, landmarkId = WoodlandLandmarks.mushrooms.id,
            discovery = "Mushroom Nook is identified. Record the next route square to the southwest."),
        square("supply-southwest", "Record the southwest waypoint",
            "From Mushroom Nook's square count one square west and one south. Select the resulting square.",
            "2437", "Southwest waypoint recorded. Check the western survey light."),
        marker("supply-light-a", "A", "Confirm the western light",
            "Read the four-digit square containing the active golden survey marker A.",
            "The western reference is confirmed. The route now turns northeast."),
        square("supply-northeast", "Plot the northeast waypoint",
            "From marker A's square count two squares east and two north. Select the resulting square.",
            "2642", "Northeast waypoint recorded. Confirm the last survey light."),
        marker("supply-light-e", "E", "Check the final light",
            "Read the four-digit square containing the active golden survey marker E.",
            "The final light is recorded. One last grid entry will close the route record."),
        square("supply-route-close", "Complete the route record",
            "From marker E's square count two squares west and two south. Select the resulting square.",
            "2740", "The supply route record is complete. Your journal now contains the last known stop and eight additional route checks.")
    ))
    val plan = ChapterExpansionPlan(legacy, chapter)
    val run = ExpeditionPlaythrough(chapter, "gridhunt_candidate_supply_survey_r2")
}
