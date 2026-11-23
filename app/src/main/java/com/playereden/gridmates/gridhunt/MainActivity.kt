package com.playereden.gridmates.gridhunt

import android.os.Bundle
import android.content.Context
import android.graphics.Paint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.ChapterPosition
import com.playereden.gridhunt.core.FirstBearingsChapter
import com.playereden.gridhunt.core.ReverseTraining
import com.playereden.gridhunt.core.GridCell
import com.playereden.gridhunt.core.GridReference
import com.playereden.gridhunt.core.HuntRules
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.WoodlandTrainingMap
import com.playereden.gridhunt.core.WorldBounds
import com.playereden.gridhunt.core.WorldPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Forest,
                    onPrimary = Color.White,
                    background = Paper,
                    onBackground = Ink,
                    surface = Color(0xFFFFFBF1)
                )
            ) {
                GridHuntScreen()
            }
        }
    }
}

@Composable
private fun GridHuntScreen() {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("gridhunt_visual_lab_v1", Context.MODE_PRIVATE)
    }
    var screen by rememberSaveable { mutableStateOf("home") }
    var lookName by rememberSaveable {
        mutableStateOf(MapLook.restore(prefs.getString("map_look", null)).name)
    }
    var hapticsEnabled by rememberSaveable {
        mutableStateOf(prefs.getBoolean("haptics", true))
    }
    val mapLook = MapLook.restore(lookName)
    LaunchedEffect(lookName, hapticsEnabled) {
        prefs.edit().putString("map_look", lookName)
            .putBoolean("haptics", hapticsEnabled).apply()
    }
    BackHandler(enabled = screen != "home") { screen = "home" }

    when (screen) {
        "chapter" -> FirstBearingsScreen(
            onHome = { screen = "home" },
            mapLook = mapLook,
            onLook = { lookName = it.name },
            hapticsEnabled = hapticsEnabled
        )
        "reverse" -> ReversePracticeScreen(
            onBack = { screen = "home" },
            mapLook = mapLook,
            onLook = { lookName = it.name },
            hapticsEnabled = hapticsEnabled
        )
        else -> GridHuntHome(
            mapLook = mapLook,
            onLook = { lookName = it.name },
            hapticsEnabled = hapticsEnabled,
            onHaptics = { hapticsEnabled = it },
            onChapter = { screen = "chapter" },
            onReverse = { screen = "reverse" }
        )
    }
}

/** Player-facing home: a cozy puzzle-book hub rather than a training dashboard. */
@Composable
private fun GridHuntHome(
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean,
    onHaptics: (Boolean) -> Unit,
    onChapter: () -> Unit,
    onReverse: () -> Unit
) {
    val context = LocalContext.current
    val chapterPrefs = remember(context) {
        context.getSharedPreferences("gridhunt_first_bearings_v1", Context.MODE_PRIVATE)
    }
    val reversePrefs = remember(context) {
        context.getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
    }
    val chapterState = FirstBearingsChapter.restore(
        chapterPrefs.getInt("hunt_index", 0), chapterPrefs.getInt("objective_index", 0)
    )
    val chapterCompleted = FirstBearingsChapter.hunts.take(chapterState.huntIndex)
        .sumOf { it.objectives.size } + chapterState.objectiveIndex
    val reverseCompleted = reversePrefs.getInt("step", 0).coerceIn(0, ReverseTraining.markers.size)
    var showAppearance by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text("GRIDMATES  •  PUZZLE ADVENTURES", fontWeight = FontWeight.Bold,
            color = Forest, fontSize = 10.sp)
        Text("Grid Hunt", fontWeight = FontWeight.ExtraBold,
            color = Ink, fontSize = 36.sp)
        Text("A little map. A new mystery. Where will you explore?",
            color = Muted, fontSize = 13.sp)
        WoodlandPostcard(modifier = Modifier.height(144.dp))
        Surface(shape = RoundedCornerShape(17.dp), color = Color(0xFFDFEAD4)) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp)) {
                Text("✦  YOUR LITTLE DISCOVERIES", color = Forest,
                    fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(Modifier.height(4.dp))
                Text("$chapterCompleted of 9 squares found   •   $reverseCompleted of 5 golden spots",
                    color = Ink, fontSize = 12.sp)
            }
        }
        Text("Choose your next puzzle", color = Ink, fontSize = 19.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        Surface(shape = RoundedCornerShape(19.dp), color = Color(0xFFFFFAEC),
            shadowElevation = 2.dp) {
            Column(Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("✦   TRAIL TREASURES", color = Forest, fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold)
                Text("Follow a number clue to find its hiding place on the map.",
                    color = Muted, fontSize = 12.sp)
                Button(
                    onClick = onChapter,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("openChapter"),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) {
                    Text(if (chapterCompleted > 0) "Keep exploring" else "Let's find a square!",
                        fontWeight = FontWeight.Bold)
                }
            }
        }
        Surface(shape = RoundedCornerShape(19.dp), color = Color(0xFFFFFAEC),
            shadowElevation = 2.dp) {
            Column(Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("✦   GOLDEN SPOTS", color = Forest, fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold)
                Text("See the golden marker? Find its secret four-digit code.",
                    color = Muted, fontSize = 12.sp)
                OutlinedButton(
                    onClick = onReverse,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("openReverse")
                ) {
                    Text(if (reverseCompleted > 0) "Find another golden spot" else "Try Golden Spots",
                        fontWeight = FontWeight.Bold)
                }
            }
        }
        OutlinedButton(
            onClick = { showAppearance = !showAppearance },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("appearanceToggle")
        ) {
            Text(if (showAppearance) "Hide map styles & options  ↑"
                else "Make it yours: maps & options  ↓")
        }
        if (showAppearance) {
            Surface(shape = RoundedCornerShape(17.dp), color = Color(0xFFF0E8D5)) {
                Column(Modifier.fillMaxWidth().padding(13.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)) {
                    Text("Pick your map's look", color = Ink, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp)
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        MapLook.entries.forEach { look ->
                            val chosen = mapLook == look
                            OutlinedButton(
                                onClick = { onLook(look) },
                                modifier = Modifier.weight(1f).heightIn(min = 54.dp)
                                    .testTag("style_" + look.name),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (chosen) Color(0xFFD2E1C5)
                                        else Color(0xFFFFFAEC)
                                ),
                                contentPadding = PaddingValues(4.dp)
                            ) {
                                Text((if (chosen) "✓ " else "") + look.label,
                                    fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                                    maxLines = 2)
                            }
                        }
                    }
                    OutlinedButton(
                        onClick = { onHaptics(!hapticsEnabled) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 45.dp)
                    ) {
                        Text("Little victory buzz: " + if (hapticsEnabled) "On" else "Off")
                    }
                }
            }
        }
        Text("Play offline. Your puzzle progress stays on this device.",
            fontSize = 11.sp, color = Muted)
    }
}

@Composable
private fun FirstBearingsScreen(
    onHome: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("gridhunt_first_bearings_v1", Context.MODE_PRIVATE)
    }
    val initial = remember(prefs) {
        FirstBearingsChapter.restore(
            prefs.getInt("hunt_index", 0),
            prefs.getInt("objective_index", 0)
        )
    }
    var huntIndex by rememberSaveable { mutableIntStateOf(initial.huntIndex) }
    var step by rememberSaveable { mutableIntStateOf(initial.objectiveIndex) }
    var selected by rememberSaveable {
        mutableStateOf(prefs.getString("selected", null)?.takeIf { ref ->
            runCatching {
                val cell = GridReference.parse(ref)
                cell.digits == 4 && WoodlandTrainingMap.bounds.contains(
                    WorldPoint(cell.westMeters.toDouble(), cell.southMeters.toDouble())
                )
            }.getOrDefault(false)
        })
    }
    var hintStage by rememberSaveable { mutableIntStateOf(prefs.getInt("hint_stage", 0).coerceIn(0, 2)) }
    var feedback by rememberSaveable {
        mutableStateOf("Read eastings first, then northings. Your chapter saves automatically.")
    }
    var showGuide by rememberSaveable {
        mutableStateOf(!prefs.getBoolean("guide_seen", false))
    }

    val hunt = FirstBearingsChapter.hunts[huntIndex]
    val targets = hunt.objectives
    val finished = step >= targets.size
    val chapterFinished = finished && huntIndex == FirstBearingsChapter.hunts.lastIndex
    val active = targets.getOrNull(step)
    val selectedCell = selected?.let(GridReference::parse)
    val bounds = WoodlandTrainingMap.bounds

    // Save objective/hunt progress promptly, including hints and pending selection,
    // so returning after Android closes the activity resumes the same authored task.
    LaunchedEffect(huntIndex, step, selected, hintStage) {
        prefs.edit()
            .putInt("hunt_index", huntIndex)
            .putInt("objective_index", step)
            .putString("selected", selected)
            .putInt("hint_stage", hintStage)
            .apply()
    }

    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    var savedCenterE by rememberSaveable { mutableStateOf(bounds.center.easting) }
    var savedCenterN by rememberSaveable { mutableStateOf(bounds.center.northing) }
    var savedZoom by rememberSaveable { mutableStateOf(1.0) }

    fun updateCamera(next: MapCamera) {
        camera = next
        savedCenterE = next.center.easting
        savedCenterN = next.center.northing
        if (viewport.width > 80 && viewport.height > 80) {
            savedZoom = next.pixelsPerMeter /
                MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()).pixelsPerMeter
        }
    }

    fun resetView() {
        if (viewport.width > 80 && viewport.height > 80) {
            updateCamera(MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()))
        }
    }

    fun zoomBy(factor: Double) {
        if (viewport.width <= 80 || viewport.height <= 80) return
        camera?.transform(
            bounds, viewport.width.toDouble(), viewport.height.toDouble(),
            PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
            PixelPoint(0.0, 0.0), factor
        )?.let(::updateCamera)
    }

    fun moveSelected(eastDelta: Int, northDelta: Int) {
        if (finished) return
        val origin = selectedCell ?: GridCell(26, 40)
        selected = GridCell(
            (origin.eastIndex + eastDelta).coerceIn(24, 29),
            (origin.northIndex + northDelta).coerceIn(37, 42)
        ).reference
        feedback = "Selected " + selected + ". Check or adjust the square."
    }

    val mapSizeChanged: (IntSize) -> Unit = { size ->
        if (size != viewport) {
            viewport = size
            if (size.width > 80 && size.height > 80) {
                val fit = MapCamera.fit(bounds, size.width.toDouble(), size.height.toDouble())
                val desired = MapCamera(
                    WorldPoint(savedCenterE, savedCenterN), fit.pixelsPerMeter * savedZoom
                )
                camera = desired.transform(
                    bounds, size.width.toDouble(), size.height.toDouble(),
                    PixelPoint(size.width / 2.0, size.height / 2.0),
                    PixelPoint(0.0, 0.0), 1.0
                )
            }
        }
    }
    val onMapSelect: (String) -> Unit = { ref ->
        if (!finished) {
            selected = ref
            feedback = "Selected " + ref + ". Check or choose another square."
        }
    }

    val onCheck: () -> Unit = {
        if (finished) {
            val next = FirstBearingsChapter.nextHuntOrReplay(ChapterPosition(huntIndex, step))
            val replayed = next.huntIndex == 0
            huntIndex = next.huntIndex
            step = next.objectiveIndex
            selected = null
            hintStage = 0
            feedback = if (replayed) "Chapter restarted. Find the first grid square."
            else "Next hunt ready. New references, same woodland map."
            resetView()
        } else {
            val expected = active!!
            val attempt = selectedCell
            if (attempt != null) {
                if (HuntRules.isCorrect(expected, attempt)) {
                    if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    step = FirstBearingsChapter.acceptCorrect(
                        ChapterPosition(huntIndex, step)
                    ).objectiveIndex
                    selected = null
                    hintStage = 0
                    feedback = when {
                        step >= targets.size && huntIndex == FirstBearingsChapter.hunts.lastIndex ->
                            "Chapter complete! The woodland survey is finished."
                        step >= targets.size ->
                            "Hunt complete! Your next survey is ready."
                        else -> "Correct: " + expected.reference + ". Find the next square."
                    }
                } else {
                    feedback = HuntRules.errorText(expected, attempt)
                }
            }
        }
    }

    val completed = targets.take(step.coerceIn(0, targets.size))
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val wide = maxWidth > maxHeight
        val sidePanelWidth = (maxWidth * 0.43f).coerceAtMost(340.dp)
        if (wide) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HuntMapPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    bounds = bounds,
                    camera = camera,
                    selectedCell = selectedCell,
                    completed = completed,
                    viewport = viewport,
                    onViewport = mapSizeChanged,
                    onCamera = ::updateCamera,
                    onSelect = onMapSelect, mapLook = mapLook
                )
                Column(
                    modifier = Modifier
                        .width(sidePanelWidth)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    HuntHeader(
                        compact = true, huntName = hunt.name,
                        huntNumber = huntIndex + 1, huntTotal = FirstBearingsChapter.hunts.size,
                        mapLook = mapLook,
                        onLook = onLook,
                        onGuide = { showGuide = !showGuide },
                        onHome = onHome
                    )
                    ObjectiveRibbon(
                        step = step, total = targets.size, active = active,
                        huntName = hunt.name, chapterFinished = chapterFinished
                    )
                    if (showGuide) GridReadingGuide {
                        showGuide = false
                        prefs.edit().putBoolean("guide_seen", true).apply()
                    }
                    HuntControlPanel(
                        step = step, total = targets.size, selected = selected,
                        feedback = feedback, hintStage = hintStage, finished = finished,
                        onMove = ::moveSelected, onZoom = ::zoomBy,
                        onUndo = {
                            selected = null
                            feedback = "Selection cleared. Choose another square."
                        },
                        onHint = { hintStage = (hintStage + 1).coerceAtMost(2) },
                        onFit = ::resetView,
                        onCheck = onCheck,
                        actionLabel = when {
                            chapterFinished -> "Replay chapter  ↻"
                            finished -> "Next hunt  →"
                            else -> "Check square"
                        }
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                HuntHeader(
                    compact = false, huntName = hunt.name,
                    huntNumber = huntIndex + 1, huntTotal = FirstBearingsChapter.hunts.size,
                    mapLook = mapLook,
                    onLook = onLook,
                    onGuide = { showGuide = !showGuide },
                    onHome = onHome
                )
                Spacer(Modifier.height(5.dp))
                ObjectiveRibbon(
                    step = step, total = targets.size, active = active,
                    huntName = hunt.name, chapterFinished = chapterFinished
                )
                if (showGuide) GridReadingGuide {
                    showGuide = false
                    prefs.edit().putBoolean("guide_seen", true).apply()
                }
                Spacer(Modifier.height(6.dp))
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    bounds = bounds,
                    camera = camera,
                    selectedCell = selectedCell,
                    completed = completed,
                    viewport = viewport,
                    onViewport = mapSizeChanged,
                    onCamera = ::updateCamera,
                    onSelect = onMapSelect, mapLook = mapLook
                )
                Spacer(Modifier.height(4.dp))
                HuntControlPanel(
                    step = step, total = targets.size, selected = selected,
                    feedback = feedback, hintStage = hintStage, finished = finished,
                    onMove = ::moveSelected, onZoom = ::zoomBy,
                    onUndo = {
                        selected = null
                        feedback = "Selection cleared. Choose another square."
                    },
                    onHint = { hintStage = (hintStage + 1).coerceAtMost(2) },
                    onFit = ::resetView,
                    onCheck = onCheck,
                    actionLabel = when {
                        chapterFinished -> "Replay chapter  ↻"
                        finished -> "Next hunt  →"
                        else -> "Check square"
                    }
                )
            }
        }
    }
}


