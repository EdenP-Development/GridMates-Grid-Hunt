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
import androidx.compose.ui.platform.testTag
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

@Composable
internal fun HuntMapPanel(
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
    enableSelect: Boolean = true,
    mapLook: MapLook,
    discoveryMessage: String? = null
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
            markerPoint = markerPoint, enableSelect = enableSelect,
            mapLook = mapLook
        )
        if (discoveryMessage != null) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .testTag("discoveryMoment"),
                color = Color(0xFFFFF2CB),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp
            ) {
                Text("✦  " + discoveryMessage,
                    color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    maxLines = 2)
            }
        }
        // The north cue belongs to the map, not the crowded game header.
        // Keep it compact and translucent so a marker near the edge stays visible.
        Text(
            "N ↑", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xCFFFF9EA))
                .padding(horizontal = 7.dp, vertical = 4.dp)
        )
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
    enableSelect: Boolean = true,
    mapLook: MapLook
) {
    // Stable pointer coroutine with fresh state. Capturing 'camera' directly in
    // a long-lived pointerInput(viewport) block can retain the initial null camera.
    val liveCamera by rememberUpdatedState(camera)
    val liveOnCamera by rememberUpdatedState(onCamera)
    val liveOnSelect by rememberUpdatedState(onSelect)
    val liveEnableSelect by rememberUpdatedState(enableSelect)
    val surveyStyle = mapLook == MapLook.SURVEY_TABLE
    val mapBackdrop = if (surveyStyle) Color(0xFFB1C7C6) else Color(0xFFD1DFCF)
    val mapPaper = if (surveyStyle) Color(0xFFDCEBE7) else Color(0xFFEDE4CB)
    val treeDark = if (surveyStyle) Color(0xFF68938F) else Color(0xFF88AA84)
    val treeLight = if (surveyStyle) Color(0xFF9CBFBA) else Color(0xFFAAC098)
    val gridInk = if (surveyStyle) Color(0xB74C6F76) else Color(0x88778667)
    val streamInk = if (surveyStyle) Color(0xFF4E8C9E) else Color(0xFF6F9FA9)
    val trailInk = if (surveyStyle) Color(0xFF6D8090) else Color(0xFFA68858)
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
                if (surveyStyle) listOf(mapBackdrop, Color(0xFF8EADAE))
                else listOf(Color(0xFFCFDBCB), Color(0xFFB4C9B3)),
                start = Offset(0f, 0f), end = Offset(size.width, size.height)
            )
        )
        drawRect(
            color = mapPaper,
            topLeft = Offset(left, top), size = Size(right - left, bottom - top)
        )
        clipRect(left, top, right, bottom) {
            // GH-V02: original, deterministic field-map artwork. Every shape is
            // anchored in world meters so zoom/pan cannot slide it under the grid.
            // These details are decorative only: the core owns every answer.
            val meadow = if (surveyStyle) Color(0x1F5A928B) else Color(0x347FB36B)
            val meadowLight = if (surveyStyle) Color(0x1FA2C5BC) else Color(0x39D9CD91)
            val groveShadow = if (surveyStyle) Color(0x33527770) else Color(0x315A7651)
            val canopyRim = if (surveyStyle) Color(0xFF7DABA1) else Color(0xFF96B18B)
            val canopyHighlight = if (surveyStyle) Color(0xFFA5C7BC) else Color(0xFFB8C6A1)
            fun landEllipse(e: Double, n: Double, radiusE: Double, radiusN: Double, ink: Color) {
                val nw = p(e - radiusE, n + radiusN)
                val se = p(e + radiusE, n - radiusN)
                drawOval(ink, topLeft = nw, size = Size(se.x - nw.x, se.y - nw.y))
            }
            // Irregular clearings add texture without inventing navigable features.
            listOf(
                doubleArrayOf(24480.0, 41100.0, 510.0, 360.0),
                doubleArrayOf(26720.0, 41900.0, 630.0, 400.0),
                doubleArrayOf(29000.0, 40300.0, 700.0, 450.0),
                doubleArrayOf(25800.0, 38100.0, 640.0, 510.0),
                doubleArrayOf(28100.0, 39350.0, 390.0, 300.0)
            ).forEachIndexed { i, oval ->
                landEllipse(oval[0], oval[1], oval[2], oval[3],
                    if (i % 2 == 0) meadow else meadowLight)
            }
            // Fixed flecks suggest grasses and paper grain. No random frame jitter.
            for (i in 0 until 46) {
                val e = 24120.0 + ((i * 977 + 103) % 5700)
                val n = 37100.0 + ((i * 643 + 419) % 5700)
                val center = p(e, n)
                val radius = (55.0 * cam.pixelsPerMeter).toFloat().coerceIn(1.3f, 4.5f)
                drawCircle(if (i % 3 == 0) meadow else meadowLight, radius, center)
            }
            // Trees use a soft multi-lobed silhouette, a warm highlight and a
            // consistent shadow, rather than twelve unrelated solid circles.
            val trees = listOf(
                24600.0 to 42250.0, 25150.0 to 41900.0, 25700.0 to 42500.0,
                27750.0 to 41950.0, 28350.0 to 41500.0, 29400.0 to 42300.0,
                24450.0 to 39600.0, 24900.0 to 40200.0, 27200.0 to 38500.0,
                29350.0 to 37450.0, 28700.0 to 38100.0, 26500.0 to 37650.0
            )
            trees.forEachIndexed { i, pair ->
                val center = p(pair.first, pair.second)
                val base = (205.0 * cam.pixelsPerMeter).toFloat().coerceIn(7f, 40f)
                val radius = base * (0.9f + (i % 4) * 0.07f)
                drawCircle(groveShadow, radius * 1.08f, center + Offset(radius * 0.16f, radius * 0.18f))
                val foliage = if (i % 3 == 0) treeDark else treeLight
                drawCircle(foliage, radius, center)
                drawCircle(canopyRim, radius * 0.63f,
                    center + Offset(-radius * 0.38f, radius * 0.12f))
                drawCircle(foliage, radius * 0.59f,
                    center + Offset(radius * 0.32f, -radius * 0.22f))
                drawCircle(canopyHighlight, radius * 0.38f,
                    center + Offset(-radius * 0.23f, -radius * 0.3f))
            }
            val stream = Path().apply {
                val a = p(24900.0, 43000.0)
                moveTo(a.x, a.y)
                val b = p(25650.0, 40900.0)
                val c = p(26200.0, 39700.0)
                val d = p(27900.0, 37800.0)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            }
            // Layered creek banks/water; keep the original centerline exact.
            val creekWidth = (90.0 * cam.pixelsPerMeter).toFloat().coerceIn(9f, 25f)
            drawPath(stream, if (surveyStyle) Color(0xFFAAC4C3) else Color(0xFFB6C5AD),
                style = Stroke(width = creekWidth + 8f))
            drawPath(stream, if (surveyStyle) Color(0xFFA1CCD2) else Color(0xFFB5D7D8),
                style = Stroke(width = creekWidth))
            drawPath(stream, streamInk, style = Stroke(width = creekWidth * 0.27f))
            val trail = Path().apply {
                val a = p(24000.0, 38950.0)
                moveTo(a.x, a.y)
                val b = p(26100.0, 38600.0)
                val c = p(27900.0, 39800.0)
                val d = p(30000.0, 40700.0)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            }
            drawPath(trail, if (surveyStyle) Color(0x33809F9B) else Color(0x44BAA078),
                style = Stroke(width = 10f))
            drawPath(trail, trailInk,
                style = Stroke(width = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(11f, 7f))))
            // Labels belong to the kilometer GRID LINES, not cell centers.
            for (easting in 24000..30000 step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                drawLine(gridInk, Offset(x, top), Offset(x, bottom), strokeWidth = 1.5f)
            }
            for (northing in 37000..43000 step 1000) {
                val y = p(bounds.west, northing.toDouble()).y
                drawLine(gridInk, Offset(left, y), Offset(right, y), strokeWidth = 1.5f)
            }
            // Solved discoveries stay on the map as little gold stamps.
            // Only already-accepted answers appear here; future targets stay hidden.
            completed.forEach { cell ->
                val tl = p(cell.westMeters.toDouble(), (cell.southMeters + 1000).toDouble())
                val side = (1000 * cam.pixelsPerMeter).toFloat()
                drawRect(Color(0x264D8A63), topLeft = tl, size = Size(side, side))
                drawRect(Color(0xFF548066), topLeft = tl, size = Size(side, side),
                    style = Stroke(2f))
                val center = p(cell.westMeters + 500.0, cell.southMeters + 500.0)
                val stamp = (155.0 * cam.pixelsPerMeter).toFloat().coerceIn(7f, 22f)
                drawCircle(Color(0x59543F27), radius = stamp * 1.08f,
                    center = center + Offset(2f, 2f))
                drawCircle(Color(0xFFFAEDC5), radius = stamp, center = center)
                drawCircle(Color(0xFFD29C49), radius = stamp * 0.81f, center = center)
                val tick = Path().apply {
                    moveTo(center.x - stamp * 0.44f, center.y)
                    lineTo(center.x - stamp * 0.08f, center.y + stamp * 0.35f)
                    lineTo(center.x + stamp * 0.51f, center.y - stamp * 0.41f)
                }
                drawPath(tick, Color(0xFFFFFCED),
                    style = Stroke(width = (stamp * 0.22f).coerceAtLeast(1.8f),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round))
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
                color = if (surveyStyle) android.graphics.Color.rgb(30, 64, 78)
                else android.graphics.Color.rgb(38, 68, 56)
                textSize = (12f * density).coerceAtMost(18f)
                isFakeBoldText = true
            }
            // Tiny white paper halos preserve label contrast over forest/creek
            // artwork and keep labels tied to the true kilometer lines.
            val halo = Paint(paint).apply {
                color = if (surveyStyle) android.graphics.Color.rgb(223, 236, 233)
                    else android.graphics.Color.rgb(247, 239, 220)
                style = Paint.Style.STROKE
                strokeWidth = 3f * density
                strokeJoin = Paint.Join.ROUND
            }
            fun gridLabel(text: String, x: Float, y: Float) {
                canvas.nativeCanvas.drawText(text, x, y, halo)
                canvas.nativeCanvas.drawText(text, x, y, paint)
            }
            val eastingBaseline = (bottom + 21f).coerceAtMost(size.height - 8f)
            val northingColumn = (left - 26f).coerceAtLeast(7f)
            for (easting in 24000..29000 step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                if (x >= 3f && x + 26f < size.width) {
                    gridLabel((easting / 1000).toString(), x + 3f, eastingBaseline)
                }
            }
            for (northing in 37000..42000 step 1000) {
                val y = p(bounds.west, northing.toDouble()).y
                if (y >= 12f && y < size.height - 12f) {
                    gridLabel((northing / 1000).toString(), northingColumn, y + 5f)
                }
            }
        }
        drawRect(
            color = Color(0xFF67785F), topLeft = Offset(left, top),
            size = Size(right - left, bottom - top), style = Stroke(2.2f)
        )
    }
}
