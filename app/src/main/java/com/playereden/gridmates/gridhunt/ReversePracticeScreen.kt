package com.playereden.gridmates.gridhunt

import android.os.Bundle
import android.content.Context
import android.graphics.Paint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
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

@Composable
internal fun ReversePracticeScreen(
    onBack: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean
) {
    val haptic = LocalHapticFeedback.current
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
        mutableStateOf("Find the golden dot! What four-digit square is it hiding in?")
    }
    var discoveryMessage by remember { mutableStateOf<String?>(null) }
    var discoverySequence by remember { mutableIntStateOf(0) }
    LaunchedEffect(discoverySequence) {
        if (discoverySequence > 0) {
            kotlinx.coroutines.delay(1700L)
            discoveryMessage = null
        }
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
            feedback = "Another adventure! Find the first golden spot again."
            discoveryMessage = null
            fit()
        } else {
            val target = active!!
            if (ReverseTraining.check(target, answer)) {
                if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                step += 1
                answer = ""
                hint = 0
                discoveryMessage = if (step == targets.size) "All golden spots found!"
                    else "Golden spot discovered!"
                discoverySequence += 1
                feedback = if (step == targets.size)
                    "Golden explorer! You found all five hiding spots."
                else "You got it! ✦ The next golden spot is waiting."
            } else {
                discoveryMessage = null
                feedback = "Not quite! " + ReverseTraining.feedback(target, answer)
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
                    camera = camera, selectedCell = null, completed = targets.take(step).map { it.cell },
                    viewport = viewport, onViewport = resized, onCamera = ::setCamera,
                    onSelect = {}, markerPoint = active?.point, enableSelect = false,
                    mapLook = mapLook, discoveryMessage = discoveryMessage
                )
                Column(
                    Modifier.width(controlsWidth).fillMaxHeight().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ReverseHeader(onBack, mapLook, onLook)
                    ReverseGoal(step, targets.size, active?.title)
                    ReverseControls(
                        answer, { answer = it.filter { c -> c in '0'..'9' }.take(4) },
                        feedback, hint, finished, step, targets.size,
                        { hint = (hint + 1).coerceAtMost(2) },
                        ::fit, ::zoomBy, check
                    )
                }
            }
        } else {
            Column(Modifier.fillMaxSize()) {
                ReverseHeader(onBack, mapLook, onLook)
                Spacer(Modifier.height(5.dp))
                ReverseGoal(step, targets.size, active?.title)
                Spacer(Modifier.height(6.dp))
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f), bounds = bounds,
                    camera = camera, selectedCell = null, completed = targets.take(step).map { it.cell },
                    viewport = viewport, onViewport = resized, onCamera = ::setCamera,
                    onSelect = {}, markerPoint = active?.point, enableSelect = false,
                    mapLook = mapLook, discoveryMessage = discoveryMessage
                )
                Spacer(Modifier.height(5.dp))
                ReverseControls(
                    answer, { answer = it.filter { c -> c in '0'..'9' }.take(4) },
                    feedback, hint, finished, step, targets.size,
                    { hint = (hint + 1).coerceAtMost(2) },
                    ::fit, ::zoomBy, check
                )
            }
        }
    }
}

@Composable
private fun ReverseHeader(
    onBack: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit
) {
    Row(
        Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(Modifier.weight(1f)) {
            Text("GRID HUNT  •  THE LITTLE WOODLAND", fontSize = 10.sp, color = Forest, fontWeight = FontWeight.Bold)
            Text("Golden Spots", fontSize = 21.sp, color = Ink, fontWeight = FontWeight.Bold)
            Text("FIND THE SECRET FOUR-DIGIT CODE", fontSize = 10.sp, color = Muted)
        }
        QuickMapLookButton(mapLook = mapLook, onLook = onLook)
        TextButton(onClick = onBack, modifier = Modifier.testTag("reverseHome")) {
            Text("Home", fontSize = 12.sp)
        }
    }
}

@Composable
private fun ReverseGoal(step: Int, total: Int, title: String?) {
    Surface(color = Color(0xFFFFF7E0), shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE7CF9D))) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(if (title == null) "ALL SPOTS FOUND!" else "GOLDEN SPOT " + (step + 1) + " OF " + total,
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Forest)
                Text(title ?: "Five golden discoveries, all yours!", fontSize = 12.sp, color = Muted)
            }
            Surface(color = Color(0xFFF6DFA9), shape = RoundedCornerShape(13.dp)) {
                Text(if (title == null) "✓" else "✦",
                    fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Ink,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
private fun ReverseControls(
    answer: String, onAnswer: (String) -> Unit,
    feedback: String, hint: Int, finished: Boolean,
    completed: Int, total: Int, onHint: () -> Unit,
    onFit: () -> Unit, onZoom: (Double) -> Unit, onCheck: () -> Unit
) {
    Text(
        if (finished) "Every golden spot found!" else "Find the gold dot, then read its map square.",
        color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 12.sp
    )
    LinearProgressIndicator(
        // Actual chapter progress, not the number of digits typed in this answer.
        progress = { completed.toFloat() / total.coerceAtLeast(1).toFloat() },
        modifier = Modifier.fillMaxWidth().height(5.dp).testTag("reverseChapterProgress"),
        color = Forest, trackColor = Color(0xFFE0E5D8)
    )
    if (!finished) {
        OutlinedTextField(
            value = answer, onValueChange = onAnswer,
            modifier = Modifier.fillMaxWidth().testTag("reverseCodeInput"),
            singleLine = true, label = { Text("Four-digit map code") },
            placeholder = { Text("0000") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
    Text(
        if (hint == 1 && !finished) "Clue: Read the easting line to the left of the gold dot."
        else if (hint >= 2 && !finished) "Clue: Then read the northing line below it. Four digits!"
        else feedback,
        fontSize = 11.sp, color = Ink, lineHeight = 15.sp,
        maxLines = 3, modifier = Modifier.fillMaxWidth().heightIn(min = 26.dp)
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        OutlinedButton(
            onClick = onHint, enabled = !finished,
            modifier = Modifier.weight(1f).heightIn(min = 46.dp),
            contentPadding = PaddingValues(0.dp)
        ) { Text("Clue") }
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
        ) { Text("See all") }
    }
    Button(
        onClick = onCheck, enabled = finished || answer.length == 4,
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("checkReverse"),
        colors = ButtonDefaults.buttonColors(containerColor = Forest)
    ) { Text(if (finished) "Find them again" else "Try this code", fontWeight = FontWeight.Bold) }
}

