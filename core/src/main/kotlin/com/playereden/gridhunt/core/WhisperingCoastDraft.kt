package com.playereden.gridhunt.core

/**
 * GH-V40-G: unpublished geographic candidate, for validation only.
 * Do not add to ExplorationMaps or PublishedWorlds until art and campaign gates pass.
 * Positions match the coordinate composition study, not final terrain anchors.
 * Public visibility permits the Android development preview to consume the same
 * definition; it does not register or publish this map.
 */
object WhisperingCoastDraft {
    val map = ExplorationMap(
        id = "whispering-coast-01",
        revision = 1,
        name = "Whispering Coast",
        bounds = WorldBounds(42000.0, 51000.0, 48000.0, 57000.0),
        landmarks = listOf(
            WoodlandLandmark("beacon-point", "Beacon Point", WorldPoint(46600.0, 56100.0),
                "Find the striped lighthouse in the northern part of the map.", "▴"),
            WoodlandLandmark("driftwood-cove", "Driftwood Cove", WorldPoint(43800.0, 52200.0),
                "Look for the branched log beside the southern inlet.", "⌁"),
            WoodlandLandmark("shell-beach", "Shell Beach", WorldPoint(45200.0, 51300.0),
                "Find the fan-shaped shell near the southern edge.", "✺"),
            WoodlandLandmark("old-jetty", "Old Jetty", WorldPoint(47600.0, 53300.0),
                "Look for the wooden pier extending east into the water.", "▰"),
            WoodlandLandmark("gull-rock", "Gull Rock", WorldPoint(44500.0, 55300.0),
                "Find the gull above the angular rock.", "◆"),
            WoodlandLandmark("tidepool-bend", "Tidepool Bend", WorldPoint(46100.0, 54500.0),
                "Find the ring of rocks surrounding a small pool.", "◉")
        )
    )
}
