package com.playereden.gridmates.gridhunt

import android.os.Bundle
import android.graphics.Paint
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
private fun GridHuntScreen() {
    val targets = WoodlandTrainingMap.objectives
    var step by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var hintStage by rememberSaveable { mutableIntStateOf(0) }
    var feedback by rememberSaveable {
        mutableStateOf("Read RIGHT to the easting, then UP to the northing.")
    }
    val finished = step >= targets.size
    val active = targets.getOrNull(step)
    val selectedCell = selected?.let { GridReference.parse(it) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    val bounds = WoodlandTrainingMap.bounds
    // Persist the *world* viewpoint and relative zoom, not pixel coordinates,
    // so rotation or a new viewport does not reset the user's map exploration.
    var savedCenterE by rememberSaveable { mutableStateOf(bounds.center.easting) }
    var savedCenterN by rememberSaveable { mutableStateOf(bounds.center.northing) }
    var savedZoom by rememberSaveable { mutableStateOf(1.0) }

    fun updateCamera(next: MapCamera) {
        camera = next
        savedCenterE = next.center.easting
        savedCenterN = next.center.northing
        if (viewport.width > 80 && viewport.height > 80) {
            val fit = MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble())
            savedZoom = next.pixelsPerMeter / fit.pixelsPerMeter
        }
    }

    fun resetView() {
        if (viewport.width > 80 && viewport.height > 80) {
            updateCamera(MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()))
        }
    }

    fun zoomBy(factor: Double) {
        if (viewport.width <= 0 || viewport.height <= 0) return
        camera?.transform(
            bounds = bounds, width = viewport.width.toDouble(), height = viewport.height.toDouble(),
            centroid = PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
            pan = PixelPoint(0.0, 0.0), zoom = factor
        )?.let(::updateCamera)
    }

    fun stepSelection(eastDelta: Int, northDelta: Int) {
        if (finished) return
        val origin = selectedCell ?: GridCell(26, 40)
        val east = (origin.eastIndex + eastDelta).coerceIn(24, 29)
        val north = (origin.northIndex + northDelta).coerceIn(37, 42)
        selected = GridCell(east, north).reference
        feedback = "Selected " + selected + ". Adjust or tap Check."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text("GRIDMATES  /  GRID HUNT", color = Forest, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("First Bearings", fontSize = 25.sp, fontWeight = FontWeight.Bold, color = Ink)
                Text("Woodland training grid  ·  GH-01", fontSize = 12.sp, color = Muted)
            }
            Text("N  ↑", color = Forest, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Surface(
            color = Color(0xFFFFFBF1), shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    if (finished) "HUNT COMPLETE" else "FIND  •  " + (step + 1) + " OF " + targets.size,
                    fontSize = 11.sp, color = Forest, fontWeight = FontWeight.Bold
                )
                Text(
                    if (finished) "All three sites found!" else active!!.reference,
                    color = Ink, fontSize = if (finished) 23.sp else 34.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    if (finished) "Nice work. Replay the same training round, or explore your map."
                    else "Find the whole square. Easting first, northing second.",
                    fontSize = 12.sp, color = Muted
                )
            }
        }
        Spacer(modifier = Modifier.height(9.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(min = 190.dp)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, Color(0xFFA19D80), RoundedCornerShape(18.dp))
                .background(Color(0xFFDADFCB))
        ) {
            TrainingMapCanvas(
                bounds = bounds, camera = camera, selectedCell = selectedCell,
                completed = targets.take(step.coerceAtMost(targets.size)),
                viewport = viewport,
                onViewport = { size ->
                    if (size != viewport) {
                        viewport = size
                        if (size.width > 80 && size.height > 80) {
                            val fit = MapCamera.fit(bounds, size.width.toDouble(), size.height.toDouble())
                            val desired = MapCamera(
                                WorldPoint(savedCenterE, savedCenterN),
                                fit.pixelsPerMeter * savedZoom
                            )
                            camera = desired.transform(
                                bounds, size.width.toDouble(), size.height.toDouble(),
                                PixelPoint(size.width / 2.0, size.height / 2.0),
                                PixelPoint(0.0, 0.0), 1.0
                            )
                        }
                    }
                },
                onCamera = ::updateCamera,
                onSelect = { reference ->
                    if (!finished) {
                        selected = reference
                        feedback = "Selected " + reference + ". Tap Check or choose another square."
                    }
                }
            )
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                color = Color(0xF5FFFAED),
                shape = RoundedCornerShape(7.dp)
            ) {
                Text(
                    "N ↑  ·  1 km squares",
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 5.dp),
                    color = Forest, fontSize = 10.sp, fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (finished) "Complete  •  3 / 3" else "Progress " + step + " / 3   ·   Selected: " + (selected ?: "—"),
            fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Forest
        )
        Text(feedback, fontSize = 12.sp, color = Ink, modifier = Modifier.padding(vertical = 3.dp))
        if (!finished && hintStage > 0) {
            Text(
                if (hintStage == 1) "Hint: The first two digits name a vertical easting line. The last two name a horizontal northing line."
                else "Hint: Find their crossing, then select the square immediately above and to the right.",
                color = Forest, fontSize = 12.sp, modifier = Modifier.padding(bottom = 2.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GridStepButton("←", "Move selected square west") { stepSelection(-1, 0) }
            GridStepButton("→", "Move selected square east") { stepSelection(1, 0) }
            GridStepButton("↑", "Move selected square north") { stepSelection(0, 1) }
            GridStepButton("↓", "Move selected square south") { stepSelection(0, -1) }
            Spacer(modifier = Modifier.weight(1f))
            GridStepButton("+", "Zoom in") { zoomBy(1.6) }
            GridStepButton("−", "Zoom out") { zoomBy(1.0 / 1.6) }
        }
        Spacer(modifier = Modifier.height(5.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp),
                onClick = {
                    selected = null
                    feedback = "Selection cleared. Pick another square."
                },
                enabled = !finished
            ) { Text("Undo", fontSize = 12.sp) }
            OutlinedButton(
                modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp),
                onClick = { hintStage = (hintStage + 1).coerceAtMost(2) },
                enabled = !finished
            ) { Text("Hint", fontSize = 12.sp) }
            OutlinedButton(
                modifier = Modifier.weight(1f), contentPadding = PaddingValues(0.dp),
                onClick = ::resetView
            ) { Text("Fit map", fontSize = 12.sp) }
        }
        Button(
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(top = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            enabled = finished || selectedCell != null,
            onClick = {
                if (finished) {
                    step = 0
                    selected = null
                    hintStage = 0
                    feedback = "New training round. Read right, then up."
                    resetView()
                } else {
                    val expected = active!!
                    if (HuntRules.isCorrect(expected, selectedCell)) {
                        step++
                        selected = null
                        hintStage = 0
                        feedback = if (step == targets.size) "All coordinates checked. Well done!"
                        else "Correct! That entire square belongs to " + expected.reference + ". Find the next."
                    } else {
                        feedback = HuntRules.errorText(expected, selectedCell!!)
                    }
                }
            }
        ) {
            Text(if (finished) "New hunt  ↻" else "Check square", fontWeight = FontWeight.Bold)
        }
        Text(
            "Fictional MGRS-style practice  ·  Offline proof of concept",
            modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
            fontSize = 9.sp, color = Muted, textAlign = TextAlign.Center
        )
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
    onSelect: (String) -> Unit
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged(onViewport)
            .semantics {
                contentDescription = "Fictional north-up 6 by 6 training grid. Eastings run right, northings run up. Tap a square or use the Nudge controls."
            }
            // Separate detectors: transforms consume drag/pinch, cancelling any pending tap.
            .pointerInput(viewport) {
                detectTapGestures(onTap = { tap ->
                    val c = camera
                    if (c != null && viewport.width > 0 && viewport.height > 0) {
                        val point = c.screenToWorld(
                            PixelPoint(tap.x.toDouble(), tap.y.toDouble()),
                            viewport.width.toDouble(), viewport.height.toDouble()
                        )
                        if (bounds.contains(point)) onSelect(GridReference.format(point, 4))
                    }
                })
            }
            .pointerInput(viewport) {
                detectTransformGestures(panZoomLock = true) { centroid, pan, zoom, _ ->
                    val current = camera
                    if (current != null && viewport.width > 0 && viewport.height > 0) {
                        onCamera(
                            current.transform(
                                bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                                PixelPoint(centroid.x.toDouble(), centroid.y.toDouble()),
                                PixelPoint(pan.x.toDouble(), pan.y.toDouble()),
                                zoom.toDouble()
                            )
                        )
                    }
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
        }
        // Drawing outside the map box would produce confusing tick labels; keep them in bounds.
        drawIntoCanvas { canvas ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = android.graphics.Color.rgb(38, 68, 56)
                textSize = (12f * density).coerceAtMost(18f)
                isFakeBoldText = true
            }
            for (easting in 24000..29000 step 1000) {
                val origin = p(easting.toDouble(), bounds.south)
                canvas.nativeCanvas.drawText((easting / 1000).toString(), origin.x + 4f, bottom - 7f, paint)
            }
            for (northing in 37000..42000 step 1000) {
                val origin = p(bounds.west, northing.toDouble())
                canvas.nativeCanvas.drawText((northing / 1000).toString(), left + 6f, origin.y - 6f, paint)
            }
        }
        drawRect(
            color = Color(0xFF67785F), topLeft = Offset(left, top),
            size = Size(right - left, bottom - top), style = Stroke(2.2f)
        )
    }
}
