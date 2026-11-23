package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.GridReference
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.TreasureChecklistMission
import com.playereden.gridhunt.core.WoodlandLandmark
import com.playereden.gridhunt.core.WoodlandTrainingMap
import com.playereden.gridhunt.core.WorldPoint

/**
 * First playable object-finding list on the already-illustrated woodland map.
 * This is NOT a new map engine. Names, answer areas, and stamps come from the
 * shared core. Existing Trail Treasures and Golden Spots saves stay separate.
 */
@Composable
internal fun TreasureChecklistScreen(
    onHome: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("gridhunt_treasure_checklist_v1", Context.MODE_PRIVATE)
    }
    val mission = TreasureChecklistMission
    var savedIds by rememberSaveable {
        mutableStateOf(prefs.getString("completed_ids", "") ?: "")
    }
    var preferredId by rememberSaveable {
        mutableStateOf(prefs.getString("selected_id", null))
    }
    var answer by rememberSaveable {
        mutableStateOf(prefs.getString("answer", "") ?: "")
    }
    var hintVisible by rememberSaveable { mutableStateOf(false) }
    var showMapGuide by rememberSaveable { mutableStateOf(false) }
    var feedback by rememberSaveable {
        mutableStateOf("Find a landmark, then enter the square where it belongs.")
    }
    fun readCompleted(text: String) = mission.restoreCompleted(
        text.split(',').filter { it.isNotBlank() }.toSet()
    )
    val completed = readCompleted(savedIds)
    val active = mission.activeTarget(completed, preferredId)
    val finished = active == null
    LaunchedEffect(savedIds, preferredId, answer) {
        prefs.edit()
            .putString("completed_ids", savedIds)
            .putString("selected_id", preferredId)
            .putString("answer", answer).apply()
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
                MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()).pixelsPerMeter
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
                val fitted = MapCamera.fit(bounds, size.width.toDouble(), size.height.toDouble())
                camera = MapCamera(WorldPoint(centerE, centerN), fitted.pixelsPerMeter * zoom)
                    .transform(bounds, size.width.toDouble(), size.height.toDouble(),
                        PixelPoint(size.width / 2.0, size.height / 2.0),
                        PixelPoint(0.0, 0.0), 1.0)
            }
        }
    }
    val choose: (WoodlandLandmark) -> Unit = { landmark ->
        if (landmark.id in completed) {
            feedback = "${landmark.name} was found at ${landmark.cell.reference}. Nice discovery!"
        } else {
            preferredId = landmark.id
            answer = ""
            hintVisible = false
            feedback = "Look for the ${landmark.name} on the map and read its square."
        }
    }
    val check: () -> Unit = {
        val current = active
        if (current != null && answer.length == 4) {
            val next = mission.accept(completed, current.id, answer)
            if (next.size > completed.size) {
                if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                savedIds = mission.targets.filter { it.id in next }.joinToString(",") { it.id }
                preferredId = mission.activeTarget(next, null)?.id
                answer = ""
                hintVisible = false
                feedback = if (mission.completed(next))
                    "Every woodland discovery found! Your journal is complete."
                else "Found it! ${current.name} is crossed off your list."
            } else {
                val attempted = runCatching { GridReference.parse(answer) }.getOrNull()
                feedback = if (attempted == null)
                    "Enter four digits: easting first, then northing."
                else "Not quite! " +
                    com.playereden.gridhunt.core.HuntRules.errorText(current.cell, attempted)
            }
        }
    }
    val replay: () -> Unit = {
        savedIds = ""
        preferredId = null
        answer = ""
        hintVisible = false
        feedback = "A fresh woodland checklist is ready."
        fit()
    }

    BoxWithConstraints(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val wide = maxWidth > maxHeight
        val sidePanelWidth = (maxWidth * 0.43f).coerceAtMost(340.dp)
        if (wide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HuntMapPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    bounds = bounds, camera = camera, selectedCell = null,
                    completed = mission.targets.filter { it.id in completed }.map { it.cell },
                    viewport = viewport, onViewport = onViewport, onCamera = ::setCamera,
                    onSelect = {}, enableSelect = false, mapLook = mapLook, onLook = onLook
                )
                Column(
                    Modifier.width(sidePanelWidth)
                        .fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TreasureChecklistHeader(completed.size, mission.targets.size,
                        onGuide = { showMapGuide = true }, onHome = onHome)
                    if (finished) {
                        TreasureChecklistFinished(onHome, replay)
                    } else {
                        TreasureChecklistTargets(mission.targets, completed, active?.id, choose)
                        TreasureChecklistAnswer(active, answer, { answer = it },
                            feedback, hintVisible, { hintVisible = !hintVisible },
                            ::zoomBy, ::fit, check)
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                TreasureChecklistHeader(completed.size, mission.targets.size,
                        onGuide = { showMapGuide = true }, onHome = onHome)
                if (!finished) {
                    TreasureChecklistTargets(mission.targets, completed, active?.id, choose)
                }
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    bounds = bounds, camera = camera, selectedCell = null,
                    completed = mission.targets.filter { it.id in completed }.map { it.cell },
                    viewport = viewport, onViewport = onViewport, onCamera = ::setCamera,
                    onSelect = {}, enableSelect = false, mapLook = mapLook, onLook = onLook
                )
                if (finished) {
                    TreasureChecklistFinished(onHome, replay)
                } else {
                    TreasureChecklistAnswer(active, answer, { answer = it }, feedback,
                        hintVisible, { hintVisible = !hintVisible }, ::zoomBy, ::fit, check)
                }
            }
        }
    }
    if (showMapGuide) {
        WoodlandMapGuideDialog(mapLook = mapLook, onDismiss = { showMapGuide = false })
    }
}

@Composable
private fun TreasureChecklistHeader(
    found: Int, total: Int, onGuide: () -> Unit, onHome: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text("Treasure Checklist", color = Ink, fontWeight = FontWeight.ExtraBold,
            fontSize = 21.sp, maxLines = 1)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("$found of $total woodland discoveries", color = Forest,
                fontSize = 11.sp, fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f).testTag("treasureProgress"))
            TextButton(onClick = onGuide, modifier = Modifier.testTag("openWoodlandMapGuide")) {
                Text("Map Guide", color = Forest, fontSize = 12.sp)
            }
            TextButton(onClick = onHome, modifier = Modifier.testTag("treasureHome")) {
                Text("Home", color = Forest, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun TreasureChecklistTargets(
    targets: List<WoodlandLandmark>,
    completed: Set<String>,
    activeId: String?,
    onChoose: (WoodlandLandmark) -> Unit
) {
    Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFFF4D8)) {
        Column(Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text("✦  FIND & CROSS OFF", color = Forest, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            targets.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    pair.forEach { target ->
                        val solved = target.id in completed
                        val active = target.id == activeId && !solved
                        OutlinedButton(
                            onClick = { onChoose(target) },
                            modifier = Modifier.weight(1f).heightIn(min = 43.dp)
                                .testTag("treasureItem_${target.id}"),
                            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 3.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = when {
                                    solved -> Color(0xFFDDE9D4)
                                    active -> Color(0xFFF8DDA0)
                                    else -> Color(0xFFFFFAEC)
                                }
                            )
                        ) {
                            Text(
                                (if (solved) "✓ " else if (active) "◆ " else "○ ") + target.name,
                                color = Ink, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TreasureChecklistAnswer(
    active: WoodlandLandmark?,
    answer: String,
    onAnswer: (String) -> Unit,
    feedback: String,
    showHint: Boolean,
    onHint: () -> Unit,
    onZoom: (Double) -> Unit,
    onFit: () -> Unit,
    onCheck: () -> Unit
) {
    if (active == null) return
    Text("Find: ${active.name}", color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    if (showHint) {
        Text(active.clue, color = Muted, fontSize = 11.sp,
            modifier = Modifier.testTag("treasureHint"))
    } else {
        Text(feedback, color = Muted, fontSize = 11.sp, maxLines = 2)
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        OutlinedTextField(
            value = answer,
            onValueChange = { onAnswer(it.filter { c -> c in '0'..'9' }.take(4)) },
            singleLine = true,
            label = { Text("Four-digit square") },
            placeholder = { Text("Enter 4 digits") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f).testTag("treasureCodeInput")
        )
        Button(
            onClick = onCheck, enabled = answer.length == 4,
            modifier = Modifier.heightIn(min = 56.dp).testTag("checkTreasure"),
            colors = ButtonDefaults.buttonColors(containerColor = Forest)
        ) { Text("Mark found", fontSize = 12.sp, fontWeight = FontWeight.Bold) }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        OutlinedButton(onClick = onHint, modifier = Modifier.weight(1f).heightIn(min = 44.dp),
            contentPadding = PaddingValues(0.dp)) { Text("Clue") }
        OutlinedButton(onClick = { onZoom(1.6) }, modifier = Modifier.weight(1f).heightIn(min = 44.dp),
            contentPadding = PaddingValues(0.dp)) { Text("+") }
        OutlinedButton(onClick = { onZoom(1.0 / 1.6) }, modifier = Modifier.weight(1f).heightIn(min = 44.dp),
            contentPadding = PaddingValues(0.dp)) { Text("−") }
        OutlinedButton(onClick = onFit, modifier = Modifier.weight(1f).heightIn(min = 44.dp),
            contentPadding = PaddingValues(0.dp)) { Text("See all", fontSize = 11.sp) }
    }
}

@Composable
private fun TreasureChecklistFinished(onHome: () -> Unit, onReplay: () -> Unit) {
    PuzzleFinishCard(
        title = "Your woodland checklist is complete!",
        description = "Four little places discovered. Every item is marked in your journal.",
        discoveries = "4 OF 4 WOODLAND FINDS",
        primaryLabel = "Explore again",
        onPrimary = onReplay,
        onHome = onHome
    )
}
