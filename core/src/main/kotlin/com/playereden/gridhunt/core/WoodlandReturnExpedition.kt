package com.playereden.gridhunt.core

/**
 * Authored Chapter 3 candidate. Intentionally not published in CampaignRegistry
 * until navigation, isolated saves and Android completion coverage are ready.
 * All objectives reference the existing map; no decorative scenery is a target.
 */
object WoodlandReturnExpedition {
    const val saveName = "gridhunt_woodland_return_v1"
    val chapter = ExpeditionChapter(
        id = "woodland-return-trail-v1",
        revision = 1,
        name = "The Return Trail",
        story = "The notes and supplies have been accounted for. Make a final circuit and record the route back to the trailhead.",
        objectives = listOf(
            ExpeditionObjective(
                "return-waystone", ExpeditionVerb.IDENTIFY_LANDMARK,
                "A familiar meeting place",
                "Start at square 2941. Which landmark marks this meeting place?",
                WoodlandLandmarks.waystone.cell, landmarkId = WoodlandLandmarks.waystone.id,
                discovery = "The meeting place is recorded. The route note points to golden marker A."
            ),
            ExpeditionObjective(
                "return-glow", ExpeditionVerb.READ_MARKER,
                "A light along the route",
                "Read the four-digit square containing the golden marker.",
                ReverseTraining.markers.single { it.id == "A" }.cell, markerId = "A",
                discovery = "The light is recorded. The next note names Acorn Nook."
            ),
            ExpeditionObjective(
                "return-acorn", ExpeditionVerb.LOCATE_LANDMARK,
                "The acorn stop",
                "Find Acorn Nook and enter its four-digit square.",
                WoodlandLandmarks.acorn.cell, landmarkId = WoodlandLandmarks.acorn.id,
                discovery = "The acorn stop is recorded. The final route note gives 2437."
            ),
            ExpeditionObjective(
                "return-trailhead", ExpeditionVerb.FIND_SQUARE,
                "Back to the trailhead",
                "Tap square 2437 to mark your return to the starting point.",
                GridReference.parse("2437"),
                discovery = "The return trail is recorded. Your woodland circuit is complete."
            )
        )
    )
    val run = ExpeditionPlaythrough(chapter, saveName)
}
