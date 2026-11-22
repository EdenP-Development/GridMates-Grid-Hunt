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

private val Ink = Color(0xFF213D35)
private val Forest = Color(0xFF315C4D)
private val Pine = Color(0xFF467B61)
private val Paper = Color(0xFFF8F1DF)
private val Gold = Color(0xFFD1A350)
private val Muted = Color(0xFF63746A)

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

private enum class MapLook(val label: String, val detail: String) {
    FIELD_GUIDE("Field Guide", "Warm paper, natural woodland tones"),
    SURVEY_TABLE("Survey Table", "Cool drafting board, crisp grid");
    companion object {
        fun restore(name: String?): MapLook =
            entries.firstOrNull { it.name == name } ?: FIELD_GUIDE
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
            hapticsEnabled = hapticsEnabled
        )
        "reverse" -> ReversePracticeScreen(
            onBack = { screen = "home" },
            mapLook = mapLook,
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
                    onClick = onChapter, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
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
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)) {
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
                    modifier = Modifier.weight(1f).heightIn(min = 64.dp),
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
    hapticsEnabled: Boolean
) {
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
                    onSelect = onMapSelect
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
                    onSelect = onMapSelect
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


@Composable
private fun ReversePracticeScreen(
    onBack: () -> Unit,
    mapLook: MapLook,
    hapticsEnabled: Boolean
) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
    }
    val targets = ReverseTraining.markers
    var step by rememberSaveable {
        mutableIntStateOf(prefs.getInt("step", 0).coerceIn(0, targets.size))
    }
    var answer by rememberSaveable {
        mutableStateOf(prefs.getString("answer", "")?.take(4)?.filter { it in '0'..'9' } ?: "")
    }
    var hint by rememberSaveable { mutableIntStateOf(prefs.getInt("hint", 0).coerceIn(0, 2)) }
    var feedback by rememberSaveable {
        mutableStateOf("Read the gold survey marker's grid square. Enter its four digits.")
    }
    LaunchedEffect(step, answer, hint) {
        prefs.edit().putInt("step", step).putString("answer", answer)
            .putInt("hint", hint).apply()
    }
    val active = targets.getOrNull(step)
    val finished = active == null
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
        if (viewport.width > 80 && viewport.height > 80)
            zoom = next.pixelsPerMeter /
                MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()).pixelsPerMeter
    }
    fun fit() {
        if (viewport.width > 80 && viewport.height > 80)
            setCamera(MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()))
    }
    fun zoomBy(scale: Double) {
        if (viewport.width > 80 && viewport.height > 80)
            camera?.transform(
                bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
                PixelPoint(0.0, 0.0), scale
            )?.let(::setCamera)
    }
    val resized: (IntSize) -> Unit = { size ->
        if (size != viewport) {
            viewport = size
            if (size.width > 80 && size.height > 80) {
                val fitCamera = MapCamera.fit(bounds, size.width.toDouble(), size.height.toDouble())
                camera = MapCamera(WorldPoint(centerE, centerN), fitCamera.pixelsPerMeter * zoom)
                    .transform(bounds, size.width.toDouble(), size.height.toDouble(),
                        PixelPoint(size.width / 2.0, size.height / 2.0), PixelPoint(0.0, 0.0), 1.0)
            }
        }
    }
    val check: () -> Unit = {
        if (finished) {
            step = 0; answer = ""; hint = 0
            feedback = "Reverse practice restarted. Read the gold survey point."
            fit()
        } else {
            val target = active!!
            if (ReverseTraining.check(target, answer)) {
                step += 1
                answer = ""
                hint = 0
                feedback = if (step == targets.size)
                    "All five survey points recorded. Nice work!"
                else "Correct! Now read the next marked point."
            } else {
                feedback = ReverseTraining.feedback(target, answer)
            }
        }
    }
    BoxWithConstraints(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val wide = maxWidth > maxHeight
        val controlsWidth = (maxWidth * 0.43f).coerceAtMost(340.dp)
        if (wide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                HuntMapPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(), bounds = bounds,
                    camera = camera, selectedCell = null, completed = emptyList(),
                    viewport = viewport, onViewport = resized, onCamera = ::setCamera,
                    onSelect = {}, markerPoint = active?.point, enableSelect = false
                )
                Column(
                    Modifier.width(controlsWidth).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ReverseHeader(onBack)
                    ReverseGoal(step, targets.size, active?.title)
                    ReverseControls(
                        answer, { answer = it.filter { c -> c in '0'..'9' }.take(4) },
                        feedback, hint, finished, { hint = (hint + 1).coerceAtMost(2) },
                        ::fit, ::zoomBy, check
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                ReverseHeader(onBack)
                Spacer(Modifier.height(5.dp))
                ReverseGoal(step, targets.size, active?.title)
                Spacer(Modifier.height(6.dp))
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f), bounds = bounds,
                    camera = camera, selectedCell = null, completed = emptyList(),
                    viewport = viewport, onViewport = resized, onCamera = ::setCamera,
                    onSelect = {}, markerPoint = active?.point, enableSelect = false
                )
                Spacer(Modifier.height(5.dp))
                ReverseControls(
                    answer, { answer = it.filter { c -> c in '0'..'9' }.take(4) },
                    feedback, hint, finished, { hint = (hint + 1).coerceAtMost(2) },
                    ::fit, ::zoomBy, check
                )
            }
        }
    }
}

@Composable
private fun ReverseHeader(onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text("GRIDMATES / GRID HUNT", fontSize = 10.sp, color = Forest, fontWeight = FontWeight.Bold)
            Text("Look Again", fontSize = 21.sp, color = Ink, fontWeight = FontWeight.Bold)
            Text("REVERSE SURVEY · PRACTICE", fontSize = 10.sp, color = Muted)
        }
        Text("N ↑", fontSize = 16.sp, color = Forest)
        TextButton(onClick = onBack) { Text("Home", fontSize = 12.sp) }
    }
}

@Composable
private fun ReverseGoal(step: Int, total: Int, title: String?) {
    Surface(color = Color(0xFFFFFBF1), shape = RoundedCornerShape(14.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(if (title == null) "PRACTICE COMPLETE" else "READ THE MARKER · " + (step + 1) + "/" + total,
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Forest)
                Text(title ?: "All five points recorded", fontSize = 12.sp, color = Muted)
            }
            Text(if (title == null) "✓" else "?", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Ink)
        }
    }
}

@Composable
private fun ReverseControls(
    answer: String, onAnswer: (String) -> Unit,
    feedback: String, hint: Int, finished: Boolean, onHint: () -> Unit,
    onFit: () -> Unit, onZoom: (Double) -> Unit, onCheck: () -> Unit
) {
    Text(
        if (finished) "Reverse practice complete" else "Find the gold point · Read west line, then south line",
        color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 12.sp
    )
    if (!finished) {
        OutlinedTextField(
            value = answer, onValueChange = onAnswer,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true, label = { Text("Four-digit grid reference") },
            placeholder = { Text("0000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
    Text(
        if (hint == 1 && !finished) "Hint: Read the easting line LEFT of the gold point."
        else if (hint >= 2 && !finished) "Hint: Then read the northing line BELOW the gold point; keep 4 digits."
        else feedback,
        fontSize = 11.sp, color = Ink, lineHeight = 15.sp,
        maxLines = 3, modifier = Modifier.fillMaxWidth().heightIn(min = 26.dp)
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
            onClick = onHint, enabled = !finished,
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("Hint") }
        OutlinedButton(
            onClick = { onZoom(1.6) },
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("+") }
        OutlinedButton(
            onClick = { onZoom(1.0 / 1.6) },
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("−") }
        OutlinedButton(
            onClick = onFit,
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("Fit") }
    }
    Button(
        onClick = onCheck, enabled = finished || answer.length == 4,
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Forest)
    ) { Text(if (finished) "Replay Reverse Practice" else "Check reference", fontWeight = FontWeight.Bold) }
}

@Composable
private fun HuntHeader(
    compact: Boolean,
    huntName: String,
    huntNumber: Int,
    huntTotal: Int,
    onGuide: () -> Unit,
    onHome: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("GRIDMATES / GRID HUNT", color = Forest, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(
                "First Bearings", color = Ink,
                fontSize = if (compact) 19.sp else 21.sp,
                fontWeight = FontWeight.Bold, lineHeight = 24.sp
            )
            Text(
                "HUNT $huntNumber / $huntTotal  ·  $huntName",
                fontSize = 10.sp, color = Muted, maxLines = 1
            )
        }
        Text("N ↑", color = Forest, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        TextButton(
            onClick = onHome,
            contentPadding = PaddingValues(horizontal = 3.dp, vertical = 2.dp)
        ) { Text("Home", color = Forest, fontSize = 11.sp) }
        TextButton(
            onClick = onGuide,
            modifier = Modifier.semantics { contentDescription = "Show or hide grid reading guide" },
            contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp)
        ) {
            Text("?", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Forest)
        }
    }
}

@Composable
private fun ObjectiveRibbon(
    step: Int, total: Int, active: GridCell?,
    huntName: String, chapterFinished: Boolean
) {
    val done = active == null
    Surface(
        color = Color(0xFFFFFBF1), shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    when {
                        chapterFinished -> "CHAPTER COMPLETE"
                        done -> "HUNT COMPLETE"
                        else -> "FIND SQUARE  " + (step + 1) + " / " + total
                    },
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Forest
                )
                Text(
                    if (done) "Nice work. Survey recorded."
                    else "East first, then north  ·  $huntName",
                    fontSize = 11.sp, maxLines = 2, color = Muted
                )
            }
            Text(
                if (done) "✓" else active.reference,
                fontSize = if (done) 32.sp else 33.sp,
                color = Ink, fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun GridReadingGuide(onDismiss: () -> Unit) {
    Surface(
        color = Color(0xFFE8EEDC),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HOW TO PLAY · Read 24 on the bottom, then 37 on the side. " +
                    "For 2437, tap the square above-right of their crossing. " +
                    "Preview and Check; hints are free.",
                fontSize = 11.sp, lineHeight = 14.sp, color = Ink,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onDismiss) { Text("Got it", fontSize = 11.sp) }
        }
    }
}


@Composable
private fun HuntMapPanel(
    modifier: Modifier,
    bounds: WorldBounds,
    camera: MapCamera?,
    selectedCell: GridCell?,
    completed: List<GridCell>,
    viewport: IntSize,
    onViewport: (IntSize) -> Unit,
    onCamera: (MapCamera) -> Unit,
    onSelect: (String) -> Unit,
    markerPoint: WorldPoint? = null,
    enableSelect: Boolean = true
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF929D81), RoundedCornerShape(16.dp))
            .background(Color(0xFFD1DFCF))
    ) {
        TrainingMapCanvas(
            bounds = bounds, camera = camera, selectedCell = selectedCell,
            completed = completed, viewport = viewport, onViewport = onViewport,
            onCamera = onCamera, onSelect = onSelect,
            markerPoint = markerPoint, enableSelect = enableSelect
        )
    }
}

@Composable
private fun HuntControlPanel(
    step: Int,
    total: Int,
    selected: String?,
    feedback: String,
    hintStage: Int,
    finished: Boolean,
    onMove: (Int, Int) -> Unit,
    onZoom: (Double) -> Unit,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onFit: () -> Unit,
    onCheck: () -> Unit,
    actionLabel: String
) {
    val notice = when {
        finished -> feedback
        hintStage == 1 -> "Hint 1: First two digits are eastings; last two are northings."
        hintStage >= 2 -> "Hint 2: Choose the square above-right of those grid lines."
        else -> feedback
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            if (finished) "COMPLETE  " + total + " / " + total
            else "FOUND  " + step + " / " + total,
            color = Forest, fontWeight = FontWeight.Bold, fontSize = 12.sp
        )
        Text(
            if (finished) "✓ ALL SITES" else "SELECTED  " + (selected ?: "—"),
            color = Forest, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
        )
    }
    Text(
        notice,
        color = if (hintStage > 0 && !finished) Forest else Ink,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        maxLines = 3,
        modifier = Modifier.fillMaxWidth().heightIn(min = 30.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("SELECT SQUARE", fontSize = 9.sp, color = Muted, fontWeight = FontWeight.Bold)
        Text("ZOOM", fontSize = 9.sp, color = Muted, fontWeight = FontWeight.Bold)
    }
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GridStepButton("←", "Move selected square west") { onMove(-1, 0) }
        GridStepButton("→", "Move selected square east") { onMove(1, 0) }
        GridStepButton("↑", "Move selected square north") { onMove(0, 1) }
        GridStepButton("↓", "Move selected square south") { onMove(0, -1) }
        Spacer(Modifier.width(6.dp))
        GridStepButton("+", "Zoom in") { onZoom(1.6) }
        GridStepButton("−", "Zoom out") { onZoom(1.0 / 1.6) }
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        OutlinedButton(
            onClick = onUndo, enabled = !finished,
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("Undo", fontSize = 12.sp) }
        OutlinedButton(
            onClick = onHint, enabled = !finished,
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("Hint", fontSize = 12.sp) }
        OutlinedButton(
            onClick = onFit,
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("Fit map", fontSize = 12.sp) }
    }
    Button(
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
        enabled = finished || selected != null,
        onClick = onCheck,
        colors = ButtonDefaults.buttonColors(containerColor = Forest)
    ) {
        Text(actionLabel, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GridStepButton(label: String, description: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.size(48.dp).semantics { contentDescription = description },
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(9.dp)
    ) {
        Text(label, fontSize = 17.sp, color = Forest)
    }
}

@Composable
private fun TrainingMapCanvas(
    bounds: WorldBounds,
    camera: MapCamera?,
    selectedCell: GridCell?,
    completed: List<GridCell>,
    viewport: IntSize,
    onViewport: (IntSize) -> Unit,
    onCamera: (MapCamera) -> Unit,
    onSelect: (String) -> Unit,
    markerPoint: WorldPoint? = null,
    enableSelect: Boolean = true
) {
    // Stable pointer coroutine with fresh state. Capturing 'camera' directly in
    // a long-lived pointerInput(viewport) block can retain the initial null camera.
    val liveCamera by rememberUpdatedState(camera)
    val liveOnCamera by rememberUpdatedState(onCamera)
    val liveOnSelect by rememberUpdatedState(onSelect)
    val liveEnableSelect by rememberUpdatedState(enableSelect)
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged(onViewport)
            .semantics {
                contentDescription = if (enableSelect)
                    "Fictional training grid. Tap a square to select; drag and pinch to navigate."
                else
                    "Reverse survey: read the four-digit reference of the marked gold point. Drag and pinch to navigate."
            }
            // A single event loop arbitrates between one-finger taps, drags and
            // two-finger transforms. Any multi-touch sequence can NEVER select a cell.
            .pointerInput(viewport) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    var transformed = false
                    var usedMultipleFingers = false
                    var accumulatedPan = Offset.Zero
                    // Keep a synchronous camera during rapid pointer events. Compose
                    // recomposition may not publish the new camera by the next frame.
                    var gestureCamera = liveCamera
                    var lastTapPosition = down.position
                    var canceled = false
                    do {
                        val event = awaitPointerEvent()
                        val pan = event.calculatePan()
                        val zoom = event.calculateZoom()
                        val centroid = event.calculateCentroid(useCurrent = false)

                        if (event.changes.size > 1) usedMultipleFingers = true
                        if (event.changes.any { it.isConsumed }) canceled = true
                        if (!transformed) accumulatedPan += pan
                        if (usedMultipleFingers ||
                            accumulatedPan.getDistance() > viewConfiguration.touchSlop
                        ) transformed = true

                        val current = gestureCamera
                        if (transformed && current != null &&
                            viewport.width > 0 && viewport.height > 0 &&
                            (pan != Offset.Zero || zoom != 1f)
                        ) {
                            val next = current.transform(
                                bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                                PixelPoint(centroid.x.toDouble(), centroid.y.toDouble()),
                                PixelPoint(pan.x.toDouble(), pan.y.toDouble()),
                                zoom.toDouble()
                            )
                            gestureCamera = next
                            liveOnCamera(next)
                            event.changes.forEach { it.consume() }
                        }

                        if (event.changes.size == 1) {
                            lastTapPosition = event.changes[0].position
                        }
                        val allReleased = event.changes.none { it.pressed }
                        if (allReleased) {
                            if (!transformed && !usedMultipleFingers && !canceled &&
                                viewport.width > 0 && viewport.height > 0
                            ) {
                                val point = liveCamera?.screenToWorld(
                                    PixelPoint(lastTapPosition.x.toDouble(), lastTapPosition.y.toDouble()),
                                    viewport.width.toDouble(), viewport.height.toDouble()
                                )
                                if (liveEnableSelect && point != null && bounds.contains(point)) {
                                    liveOnSelect(GridReference.format(point, 4))
                                }
                            }
                            break
                        }
                    } while (true)
                }
            }
    ) {
        val cam = camera ?: return@Canvas
        val width = size.width.toDouble()
        val height = size.height.toDouble()
        fun p(e: Double, n: Double): Offset {
            val result = cam.worldToScreen(WorldPoint(e, n), width, height)
            return Offset(result.x.toFloat(), result.y.toFloat())
        }
        val northWest = p(bounds.west, bounds.north)
        val southEast = p(bounds.east, bounds.south)
        val left = northWest.x
        val top = northWest.y
        val right = southEast.x
        val bottom = southEast.y

        drawRect(
            brush = Brush.linearGradient(
                listOf(Color(0xFFCFDBCB), Color(0xFFB4C9B3)),
                start = Offset(0f, 0f), end = Offset(size.width, size.height)
            )
        )
        drawRect(
            color = Color(0xFFEDE4CB),
            topLeft = Offset(left, top), size = Size(right - left, bottom - top)
        )
        clipRect(left, top, right, bottom) {
            // Decorative woodland: never used for answer grading.
            val trees = listOf(
                24600.0 to 42250.0, 25150.0 to 41900.0, 25700.0 to 42500.0,
                27750.0 to 41950.0, 28350.0 to 41500.0, 29400.0 to 42300.0,
                24450.0 to 39600.0, 24900.0 to 40200.0, 27200.0 to 38500.0,
                29350.0 to 37450.0, 28700.0 to 38100.0, 26500.0 to 37650.0
            )
            trees.forEachIndexed { i, pair ->
                val center = p(pair.first, pair.second)
                val radius = (270.0 * cam.pixelsPerMeter).toFloat().coerceIn(8f, 48f)
                drawCircle(
                    color = if (i % 3 == 0) Color(0xFF88AA84) else Color(0xFFAAC098),
                    center = center, radius = radius
                )
                drawCircle(
                    color = Color(0x557EA079), center = center + Offset(9f, 5f), radius = radius * 0.62f
                )
            }
            val stream = Path().apply {
                val a = p(24900.0, 43000.0)
                moveTo(a.x, a.y)
                val b = p(25650.0, 40900.0)
                val c = p(26200.0, 39700.0)
                val d = p(27900.0, 37800.0)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            }
            drawPath(stream, Color(0xFFBCD6D9), style = Stroke(width = 17f))
            drawPath(stream, Color(0xFF6F9FA9), style = Stroke(width = 7f))
            val trail = Path().apply {
                val a = p(24000.0, 38950.0)
                moveTo(a.x, a.y)
                val b = p(26100.0, 38600.0)
                val c = p(27900.0, 39800.0)
                val d = p(30000.0, 40700.0)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            }
            drawPath(
                trail, Color(0xFFA68858),
                style = Stroke(width = 3.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 7f)))
            )
            // Labels belong to the kilometer GRID LINES, not cell centers.
            for (easting in 24000..30000 step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                drawLine(Color(0x88778667), Offset(x, top), Offset(x, bottom), strokeWidth = 1.5f)
            }
            for (northing in 37000..43000 step 1000) {
                val y = p(bounds.west, northing.toDouble()).y
                drawLine(Color(0x88778667), Offset(left, y), Offset(right, y), strokeWidth = 1.5f)
            }
            completed.forEach { cell ->
                val tl = p(cell.westMeters.toDouble(), (cell.southMeters + 1000).toDouble())
                val side = (1000 * cam.pixelsPerMeter).toFloat()
                drawRect(Color(0x3C397C65), topLeft = tl, size = Size(side, side))
                drawRect(Color(0xFF4E7D60), topLeft = tl, size = Size(side, side), style = Stroke(2f))
            }
            selectedCell?.let { cell ->
                val tl = p(cell.westMeters.toDouble(), (cell.southMeters + 1000).toDouble())
                val side = (1000 * cam.pixelsPerMeter).toFloat()
                drawRect(Color(0x66E8BD63), topLeft = tl, size = Size(side, side))
                drawRect(Color(0xFFE1A947), topLeft = tl, size = Size(side, side), style = Stroke(3.8f))
            }
            markerPoint?.let { point ->
                val center = p(point.easting, point.northing)
                drawCircle(Color(0xFFFFF8E5), radius = 20f, center = center)
                drawCircle(Color(0xFFBF8331), radius = 14f, center = center)
                drawCircle(Color(0xFF244236), radius = 5f, center = center)
            }
        }
        // Separate axis rails prevent the 24/37 overlap seen at the SW corner.
        // When zoomed, anchor labels near the viewport edge instead of leaving
        // the only useful ticks far offscreen with the map's original boundary.
        drawIntoCanvas { canvas ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(38, 68, 56)
                textSize = (12f * density).coerceAtMost(18f)
                isFakeBoldText = true
            }
            val eastingBaseline = (bottom + 21f).coerceAtMost(size.height - 7f)
            val northingColumn = (left - 26f).coerceAtLeast(6f)
            for (easting in 24000..29000 step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                if (x >= 4f && x + 26f < size.width) {
                    canvas.nativeCanvas.drawText((easting / 1000).toString(), x + 3f, eastingBaseline, paint)
                }
            }
            for (northing in 37000..42000 step 1000) {
                val y = p(bounds.west, northing.toDouble()).y
                if (y >= 12f && y < size.height - 12f) {
                    canvas.nativeCanvas.drawText((northing / 1000).toString(), northingColumn, y + 5f, paint)
                }
            }
        }
        drawRect(
            color = Color(0xFF67785F), topLeft = Offset(left, top),
            size = Size(right - left, bottom - top), style = Stroke(2.2f)
        )
    }
}
