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

/** A small playtest hub, not a promise of all future modes. */
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

    Column(
        modifier = Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("GRIDMATES  /  FIELD NOTES", fontWeight = FontWeight.Bold,
            color = Forest, fontSize = 11.sp)
        Text("Grid Hunt", fontWeight = FontWeight.ExtraBold,
            color = Ink, fontSize = 34.sp)
        Text("Good puzzles. Good company.", color = Muted, fontSize = 13.sp)
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFDFE7D7)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text("YOUR FIELD JOURNAL", fontWeight = FontWeight.Bold,
                    color = Forest, fontSize = 10.sp)
                Spacer(Modifier.height(5.dp))
                Text("The woodland survey", fontWeight = FontWeight.SemiBold,
                    color = Ink, fontSize = 20.sp)
                Text("Find landmarks by reading the grid — no timer, no pressure.",
                    fontSize = 12.sp, color = Muted)
                Spacer(Modifier.height(9.dp))
                Text("$chapterCompleted / 9 chapter squares  ·  $reverseCompleted / 5 reverse markers",
                    fontSize = 12.sp, color = Forest, fontWeight = FontWeight.Medium)
            }
        }
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFFFBF1)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("01   FIRST BEARINGS", color = Forest, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold)
                Text("Three short woodland hunts · 9 grid squares",
                    fontSize = 12.sp, color = Muted)
                Spacer(Modifier.height(7.dp))
                Button(
                    onClick = onChapter,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("openChapter"),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) { Text(if (chapterCompleted > 0) "Continue chapter" else "Start chapter") }
            }
        }
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFFFBF1)) {
            Column(Modifier.fillMaxWidth().padding(14.dp)) {
                Text("02   LOOK AGAIN", color = Forest, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold)
                Text("Reverse practice · 5 marked locations, read the reference",
                    fontSize = 12.sp, color = Muted)
                Spacer(Modifier.height(7.dp))
                OutlinedButton(onClick = onReverse,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("openReverse")) {
                    Text(if (reverseCompleted > 0) "Continue reverse practice" else "Try reverse practice")
                }
            }
        }
        Text("VISUAL PLAYTEST", color = Forest, fontSize = 11.sp,
            fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
        Text("Compare two map treatments using the same real puzzle rules.",
            fontSize = 12.sp, color = Muted)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MapLook.entries.forEach { look ->
                val chosen = mapLook == look
                OutlinedButton(
                    onClick = { onLook(look) },
                    modifier = Modifier.weight(1f).heightIn(min = 64.dp).testTag("style_" + look.name),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (chosen) Color(0xFFDDE7D6) else Color(0xFFFFFBF1)
                    ),
                    contentPadding = PaddingValues(6.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (chosen) "✓  "+look.label else look.label,
                            fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(if (look == MapLook.FIELD_GUIDE) "Warm" else "Cool",
                            fontSize = 10.sp)
                    }
                }
            }
        }
        OutlinedButton(
            onClick = { onHaptics(!hapticsEnabled) },
            modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp)
        ) {
            Text("Completion vibration: " + if (hapticsEnabled) "On" else "Off")
        }
        Text("Design preview · Offline · Fictional grid · Progress saves on this device",
            fontSize = 10.sp, color = Muted)
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


