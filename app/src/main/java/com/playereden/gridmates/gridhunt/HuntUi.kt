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

/**
 * Compact A/B control in the header, deliberately leaving map height untouched.
 * Theme changes are purely visual: grid math, selection and camera are retained.
 */
@Composable
internal fun QuickMapLookButton(mapLook: MapLook, onLook: (MapLook) -> Unit) {
    val next = if (mapLook == MapLook.FIELD_GUIDE) MapLook.SURVEY_TABLE else MapLook.FIELD_GUIDE
    TextButton(
        onClick = { onLook(next) },
        modifier = Modifier.testTag("mapLookToggle").semantics {
            contentDescription = "Switch map look from ${mapLook.label} to ${next.label}"
        },
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Text(
            if (mapLook == MapLook.FIELD_GUIDE) "Warm" else "Cool",
            color = Forest, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
internal fun HuntHeader(
    compact: Boolean,
    huntName: String,
    huntNumber: Int,
    huntTotal: Int,
    onGuide: () -> Unit,
    onHome: () -> Unit
) {
    // Dedicated full-width trail caption: don't make a story title compete with
    // three navigation buttons. Map appearance now lives on the map surface.
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Trail Treasures", color = Ink,
                modifier = Modifier.weight(1f).testTag("trailModeTitle"),
                fontSize = if (compact) 18.sp else 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            TextButton(
                onClick = onHome,
                modifier = Modifier.testTag("chapterHome"),
                contentPadding = PaddingValues(horizontal = 3.dp, vertical = 2.dp)
            ) { Text("Home", color = Forest, fontSize = 12.sp) }
            TextButton(
                onClick = onGuide,
                modifier = Modifier.semantics {
                    contentDescription = "Show or hide grid reading guide"
                },
                contentPadding = PaddingValues(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text("?", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = Forest)
            }
        }
        Text(
            "Trail $huntNumber/$huntTotal  ·  $huntName",
            modifier = Modifier.fillMaxWidth().testTag("trailLocation"),
            fontSize = 11.sp, color = Muted, maxLines = 1
        )
    }
}

@Composable
internal fun ObjectiveRibbon(
    step: Int, total: Int, active: GridCell?,
    huntName: String, chapterFinished: Boolean
) {
    val done = active == null
    Surface(
        color = Color(0xFFFFF7E0), shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, Color(0xFFE7CF9D)),
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
                        chapterFinished -> "ALL CLUES FOUND!"
                        done -> "TRAIL COMPLETE"
                        else -> "CLUE " + (step + 1) + " OF " + total
                    },
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Forest
                )
                Text(
                    if (done) "Every square found. Nice exploring!"
                    else "Find this square on the map",
                    fontSize = 11.sp, maxLines = 2, color = Muted
                )
            }
            Surface(
                shape = RoundedCornerShape(13.dp),
                color = Color(0xFFF6DFA9)
            ) {
                Text(
                    if (done) "✓" else active.reference,
                    fontSize = if (done) 29.sp else 31.sp,
                    color = Ink, fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
internal fun GridReadingGuide(onDismiss: () -> Unit) {
    // A dialog keeps the map at its actual play size while instructions are open.
    // Once dismissed, tapping the ? button in the header opens it again.
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("A little map-reading trick", color = Ink, fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Every square has a four-digit address. First, follow the first two " +
                    "numbers along the bottom. Then, follow the last two numbers " +
                    "up the left side. For 2437, find the crossing of 24 and 37, " +
                    "then tap the square just above and to the right. " +
                    "Try a guess — you can change it anytime! Hints are free.",
                color = Ink
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("dismissGridGuide")) {
                Text("Got it", color = Forest, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFFFFFBF1),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.testTag("gridReadingGuide")
    )
}


@Composable
internal fun HuntControlPanel(
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
            if (finished) "FOUND ALL " + total
            else "FOUND " + step + " OF " + total,
            color = Forest, fontWeight = FontWeight.Bold, fontSize = 12.sp
        )
        Text(
            if (finished) "✦ WELL DONE" else "YOUR PICK  " + (selected ?: "—"),
            color = Forest, fontSize = 12.sp, fontWeight = FontWeight.SemiBold
        )
    }
    LinearProgressIndicator(
        progress = { step.toFloat() / total.coerceAtLeast(1).toFloat() },
        modifier = Modifier.fillMaxWidth().height(5.dp),
        color = Forest, trackColor = Color(0xFFE0E5D8)
    )
    Text(
        notice,
        color = if (hintStage > 0 && !finished) Forest else Ink,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        maxLines = 3,
        modifier = Modifier.fillMaxWidth().heightIn(min = 30.dp)
    )
    val context = LocalContext.current
    val controls = remember(context) { context.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE) }
    var precise by remember { mutableStateOf(controls.getBoolean("precise_selection", false)) }
    var options by rememberSaveable { mutableStateOf(false) }
    if (precise) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        GridStepButton("←", "Move selected square west", Modifier.weight(1f)) { onMove(-1, 0) }
        GridStepButton("→", "Move selected square east", Modifier.weight(1f)) { onMove(1, 0) }
        GridStepButton("↑", "Move selected square north", Modifier.weight(1f)) { onMove(0, 1) }
        GridStepButton("↓", "Move selected square south", Modifier.weight(1f)) { onMove(0, -1) }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        HuntToolButton(ExplorerTool.ZOOM_IN, Modifier.weight(1f)) { onZoom(1.6) }
        HuntToolButton(ExplorerTool.ZOOM_OUT, Modifier.weight(1f)) { onZoom(1.0 / 1.6) }
        HuntToolButton(ExplorerTool.FIT, Modifier.weight(1f), onFit)
        OutlinedButton(onClick = { options = true },
            modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("huntToolOptions")
                .semantics { contentDescription = "More map tools" },
            contentPadding = PaddingValues(0.dp)) { Text("⋯", color = Forest, fontSize = 22.sp) }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedButton(onClick = onUndo, enabled = !finished,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
            contentPadding = PaddingValues(0.dp)) { Text("Clear", fontSize = 12.sp) }
        OutlinedButton(onClick = onHint, enabled = !finished,
            modifier = Modifier.weight(1f).heightIn(min = 48.dp),
            contentPadding = PaddingValues(0.dp)) { Text("Clue", fontSize = 12.sp) }
    }
    if (options) androidx.compose.material3.AlertDialog(
        onDismissRequest = { options = false },
        title = { Text("Map tools") },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Precise selection arrows", modifier = Modifier.weight(1f))
                androidx.compose.material3.Switch(checked = precise, onCheckedChange = {
                    precise = it
                    controls.edit().putBoolean("precise_selection", it).apply()
                }, modifier = Modifier.testTag("huntPreciseSelection"))
            }
        },
        confirmButton = { TextButton(onClick = { options = false },
            modifier = Modifier.testTag("huntCloseTools")) { Text("Done") } }
    )
    Button(
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("checkChapter"),
        enabled = finished || selected != null,
        onClick = onCheck,
        colors = ButtonDefaults.buttonColors(containerColor = Forest)
    ) {
        Text(actionLabel, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun GridStepButton(label: String, description: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp).semantics { contentDescription = description },
        contentPadding = PaddingValues(0.dp),
        shape = RoundedCornerShape(9.dp)
    ) {
        Text(label, fontSize = 17.sp, color = Forest)
    }
}


@Composable
private fun HuntToolButton(tool: ExplorerTool, modifier: Modifier, action: () -> Unit) {
    OutlinedButton(onClick = action, modifier = modifier.heightIn(min = 48.dp)
        .semantics { contentDescription = tool.label }, contentPadding = PaddingValues(0.dp)) {
        ExplorerToolIcon(tool)
    }
}
