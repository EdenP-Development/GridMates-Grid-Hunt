package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.IntSize
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

/** Test-only map host, never reachable from production navigation. */
class CoastalPreviewTest {
    @get:Rule val compose = createComposeRule()

    @Test fun bothLooksUseSameMapAndSelectionAtFitAndZoom() {
        // Test fixture mirrors candidate coordinates; no publication or save writes.
        val map = ExplorationMap("whispering-coast-01",1,"Whispering Coast",
            WorldBounds(42000.0,51000.0,48000.0,57000.0),
            listOf(
                WoodlandLandmark("beacon-point","Beacon Point",WorldPoint(46600.0,56100.0),"Lighthouse","▴"),
                WoodlandLandmark("driftwood-cove","Driftwood Cove",WorldPoint(43800.0,52200.0),"Log","⌁"),
                WoodlandLandmark("shell-beach","Shell Beach",WorldPoint(45200.0,51300.0),"Shell","✺"),
                WoodlandLandmark("old-jetty","Old Jetty",WorldPoint(47600.0,53300.0),"Pier","▰"),
                WoodlandLandmark("gull-rock","Gull Rock",WorldPoint(44500.0,55300.0),"Gull","◆"),
                WoodlandLandmark("tidepool-bend","Tidepool Bend",WorldPoint(46100.0,54500.0),"Pool","◉")
            ))
        val look = mutableStateOf(MapLook.SURVEY_TABLE)
        val selection = mutableStateOf<String?>(null)
        val camera = mutableStateOf<MapCamera?>(null)
        val viewport = mutableStateOf(IntSize.Zero)
        compose.setContent {
            val scene = coastalTerrainDraftScene(map,look.value)
            HuntMapPanel(
                modifier = Modifier.fillMaxSize(), bounds = map.bounds,
                camera = camera.value, selectedCell = selection.value?.let { GridReference.parse(it) },
                completed = emptyList(), viewport = viewport.value,
                onViewport = { size ->
                    viewport.value = size
                    if (size.width > 68 && size.height > 68 && camera.value == null)
                        camera.value = MapCamera.fit(map.bounds,size.width.toDouble(),size.height.toDouble())
                },
                onCamera = { camera.value = it }, onSelect = { selection.value = it },
                mapLook = look.value, scene = scene, map = map
            )
        }
        compose.waitUntil(10000) { camera.value != null }
        compose.onNodeWithContentDescription("North is up").assertExists()
        fun selectCenter() {
            compose.onNodeWithContentDescription("Fictional training grid. Tap a square to select; drag and pinch to navigate.")
                .performTouchInput { click(center) }
            compose.runOnIdle { assertEquals("4554",selection.value) }
        }
        selectCenter()
        compose.runOnIdle {
            look.value = MapLook.values().first { it != MapLook.SURVEY_TABLE }
            val fit = requireNotNull(camera.value)
            camera.value = fit.copy(pixelsPerMeter = fit.pixelsPerMeter * 2)
            selection.value = null
        }
        selectCenter()
    }
}
