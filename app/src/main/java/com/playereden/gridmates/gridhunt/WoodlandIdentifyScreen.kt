package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.WoodlandIdentifyMission
import com.playereden.gridhunt.core.WoodlandLandmark
import com.playereden.gridhunt.core.WoodlandTrainingMap
import com.playereden.gridhunt.core.WorldPoint

/**
 * GH-V12: coordinate -> object recognition. Unlike Treasure Checklist, players
 * are NOT shown the target object in advance. Every answer tile shares the
 * exact original drawing code with the map and optional Map Guide.
 *
 * State is isolated from the older modes; completed discoveries are retained
 * until the player explicitly chooses replay.
 */
@Composable
internal fun WoodlandIdentifyScreen(
    onHome: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val prefs = remember(context) {
        context.getSharedPreferences(WoodlandIdentifyMission.saveName, Context.MODE_PRIVATE)
    }
    val mission = WoodlandIdentifyMission
    var step by rememberSaveable {
        mutableStateOf(mission.restoreStep(prefs.savedInt("step", 0)))
    }
    var chosenId by rememberSaveable {
        mutableStateOf(prefs.savedString("chosen_id", null)?.takeIf { id ->
            mission.choices.any { it.id == id }
        })
    }
    var feedback by rememberSaveable {
        mutableStateOf("Read the square, explore the map, and choose what you find.")
    }
    var showGuide by rememberSaveable { mutableStateOf(false) }
    val question = mission.questionAt(step)
    val finished = mission.finished(step)

    LaunchedEffect(step, chosenId) {
        prefs.edit().putInt("step", step).putString("chosen_id", chosenId).apply()
    }

    val bounds = WoodlandTrainingMap.bounds
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    var centerE by rememberSaveable { mutableStateOf(bounds.center.easting) }
    var centerN by rememberSaveable { mutableStateOf(bounds.center.northing) }
    var zoom by rememberSaveable { mutableStateOf(1.0) }

    fun setCamera(next: MapCamera) {
        camera = next
        centerE = next.center.easting
        centerN = next.center.northing
        if (viewport.width > 80 && viewport.height > 80) {
            zoom = next.pixelsPerMeter /
                MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble())
                    .pixelsPerMeter
        }
    }

    fun fit() {
        if (viewport.width > 80 && viewport.height > 80) {
            setCamera(MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()))
        }
    }

    fun zoomBy(factor: Double) {
        if (viewport.width > 80 && viewport.height > 80) {
            camera?.transform(
                bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
                PixelPoint(0.0, 0.0), factor
            )?.let(::setCamera)
        }
    }

    val onViewport: (IntSize) -> Unit = { size ->
        if (size != viewport) {
            viewport = size
            if (size.width > 80 && size.height > 80) {
                val fitted = MapCamera.fit(
                    bounds, size.width.toDouble(), size.height.toDouble()
                )
                camera = MapCamera(WorldPoint(centerE, centerN), fitted.pixelsPerMeter * zoom)
                    .transform(
                        bounds, size.width.toDouble(), size.height.toDouble(),
                        PixelPoint(size.width / 2.0, size.height / 2.0),
                        PixelPoint(0.0, 0.0), 1.0
                    )
            }
        }
    }

    val check: () -> Unit = {
        if (question != null && chosenId != null) {
            if (mission.correct(step, chosenId)) {
                if (hapticsEnabled) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
                step = mission.accept(step, chosenId)
                chosenId = null
                feedback = if (mission.finished(step)) {
                    "Every woodland mystery identified!"
                } else {
                    "Found it! A new square is waiting."
                }
            } else {
                feedback = "Not quite. Check the square's grid lines and try another picture."
            }
        }
    }

    val replay: () -> Unit = {
        step = 0
        chosenId = null
        feedback = "A fresh round of woodland mysteries is ready."
        fit()
    }

    BoxWithConstraints(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val wide = maxWidth > maxHeight
        val sideWidth = (maxWidth * 0.44f).coerceAtMost(370.dp)
        val solved = mission.questions.take(step).map { it.cell }
        if (wide) {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HuntMapPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    bounds = bounds, camera = camera, selectedCell = null,
                    completed = solved, viewport = viewport, onViewport = onViewport,
                    onCamera = ::setCamera, onSelect = {}, enableSelect = false,
                    mapLook = mapLook, onLook = onLook
                )
                Column(
                    Modifier.width(sideWidth).fillMaxHeight()
                        .journalScroll(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IdentifyHeader(step, mission.questions.size,
                        onGuide = { showGuide = true }, onHome = onHome)
                    if (finished) {
                        IdentifyFinish(onHome, replay)
                    } else if (question != null) {
                        IdentifyPrompt(question.cell.reference)
                        IdentifyAnswers(mission.choices, chosenId, mapLook) {
                            chosenId = it
                            feedback = "Ready to check your choice?"
                        }
                        IdentifyActions(feedback, chosenId != null,
                            onZoom = ::zoomBy, onFit = ::fit, onCheck = check)
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                IdentifyHeader(step, mission.questions.size,
                    onGuide = { showGuide = true }, onHome = onHome)
                if (question != null) {
                    IdentifyPrompt(question.cell.reference)
                }
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    bounds = bounds, camera = camera, selectedCell = null,
                    completed = solved, viewport = viewport, onViewport = onViewport,
                    onCamera = ::setCamera, onSelect = {}, enableSelect = false,
                    mapLook = mapLook, onLook = onLook
                )
                if (finished) {
                    IdentifyFinish(onHome, replay)
                } else {
                    IdentifyAnswers(mission.choices, chosenId, mapLook) {
                        chosenId = it
                        feedback = "Ready to check your choice?"
                    }
                    IdentifyActions(feedback, chosenId != null,
                        onZoom = ::zoomBy, onFit = ::fit, onCheck = check)
                }
            }
        }
    }
    if (showGuide) {
        WoodlandMapGuideDialog(mapLook = mapLook, onDismiss = { showGuide = false })
    }
}

@Composable
private fun IdentifyHeader(
    done: Int,
    total: Int,
    onGuide: () -> Unit,
    onHome: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text("What's at This Grid?", color = Ink, fontWeight = FontWeight.ExtraBold,
            fontSize = 21.sp, maxLines = 1)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("$done of $total identified", fontSize = 11.sp, color = Forest,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f).testTag("identifyProgress"))
            TextButton(onClick = onGuide, modifier = Modifier.testTag("identifyGuide")) {
                Text("Map Guide", color = Forest, fontSize = 11.sp)
            }
            TextButton(onClick = onHome, modifier = Modifier.testTag("identifyHome")) {
                Text("Home", color = Forest, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun IdentifyPrompt(reference: String) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFFFF1D1)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text("LOOK INSIDE THIS SQUARE", fontSize = 10.sp,
                    color = Forest, fontWeight = FontWeight.Bold)
                Text("Which landmark is there?", fontSize = 12.sp, color = Muted)
            }
            Text(reference, color = Ink, fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.testTag("identifyReference"))
        }
    }
}

@Composable
private fun IdentifyAnswers(
    choices: List<WoodlandLandmark>,
    selectedId: String?,
    mapLook: MapLook,
    onSelect: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        choices.chunked(2).forEach { pair ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                pair.forEach { choice ->
                    val selected = choice.id == selectedId
                    OutlinedButton(
                        onClick = { onSelect(choice.id) },
                        modifier = Modifier.weight(1f).heightIn(min = 66.dp)
                            .testTag("identifyOption_${choice.id}"),
                        contentPadding = PaddingValues(horizontal = 5.dp, vertical = 3.dp),
                        shape = RoundedCornerShape(13.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (selected) Color(0xFFF7DDA2)
                                else Color(0xFFFFFBF0)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            WoodlandLandmarkSwatch(
                                choice.id, mapLook, symbolSize = 38.dp
                            )
                            Text(choice.name, color = Ink, fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold, maxLines = 2,
                                modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IdentifyActions(
    feedback: String,
    selected: Boolean,
    onZoom: (Double) -> Unit,
    onFit: () -> Unit,
    onCheck: () -> Unit
) {
    Text(feedback, color = Muted, fontSize = 11.sp, maxLines = 2,
        modifier = Modifier.fillMaxWidth().testTag("identifyFeedback"))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        HuntToolButton(ExplorerTool.ZOOM_IN, Modifier.weight(0.85f), { onZoom(1.6) })
        HuntToolButton(ExplorerTool.ZOOM_OUT, Modifier.weight(0.85f), { onZoom(1.0 / 1.6) })
        HuntToolButton(ExplorerTool.FIT, Modifier.weight(0.85f), onFit)
        Button(
            onClick = onCheck, enabled = selected,
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            modifier = Modifier.weight(1.8f).heightIn(min = 50.dp)
                .testTag("identifyCheck"),
            contentPadding = PaddingValues(horizontal = 5.dp)
        ) {
            Text("Check find", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun IdentifyFinish(onHome: () -> Unit, onReplay: () -> Unit) {
    PuzzleFinishCard(
        title = "Every square identified!",
        description = "Four little landmarks discovered by reading the map.",
        discoveries = "4 OF 4 LANDMARKS",
        primaryLabel = "Identify them again",
        onPrimary = onReplay,
        onHome = onHome
    )
}
