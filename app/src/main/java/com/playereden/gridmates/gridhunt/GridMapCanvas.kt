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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
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
import com.playereden.gridhunt.core.ExplorationMap
import com.playereden.gridhunt.core.ExplorationMaps
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.WoodlandLandmarks
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
    discoveryMessage: String? = null,
    onLook: ((MapLook) -> Unit)? = null,
    scene: MapScene? = null,
    map: ExplorationMap = ExplorationMaps.woodland
) {
    val activeScene = scene ?: publishedMapScene(map, mapLook)
    requireSceneMap(activeScene.map, map, bounds)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFF98805A), RoundedCornerShape(16.dp))
            .background(Color(0xFFD1DFCF))
    ) {
        TrainingMapCanvas(
            bounds = bounds, camera = camera, selectedCell = selectedCell,
            completed = completed, viewport = viewport, onViewport = onViewport,
            onCamera = onCamera, onSelect = onSelect,
            markerPoint = markerPoint, enableSelect = enableSelect,
            mapLook = mapLook, scene = activeScene
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
        // Compact, fixed north-up cartographic cue. Decorative only: no
        // pointer input, no camera rotation, no new interaction surface.
        Row(
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(7.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xE8FFF5DA))
                .border(1.dp, Color(0xAA9A855A), RoundedCornerShape(8.dp))
                .padding(horizontal = 5.dp, vertical = 3.dp)
                .semantics { contentDescription = "North is up" }
                .testTag("mapNorthCompass"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text("N", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
            Canvas(Modifier.size(16.dp)) {
                val r = size.minDimension * .45f
                drawCircle(Color(0xFF9A855A), r, center, style = Stroke(1.dp.toPx()))
                val needle = Path().apply {
                    moveTo(center.x, center.y - r)
                    lineTo(center.x - r * .40f, center.y)
                    lineTo(center.x, center.y + r)
                    lineTo(center.x + r * .40f, center.y)
                    close()
                }
                drawPath(needle, Color(0xFFDDC595))
                drawPath(Path().apply {
                    moveTo(center.x, center.y - r)
                    lineTo(center.x - r * .40f, center.y)
                    lineTo(center.x + r * .40f, center.y)
                    close()
                }, Color(0xFF315A47))
                drawPath(needle, Color(0xFF6D674C), style = Stroke(.65.dp.toPx()))
            }
        }
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
    mapLook: MapLook,
    scene: MapScene
) {
    // Stable pointer coroutine with fresh state. Capturing 'camera' directly in
    // a long-lived pointerInput(viewport) block can retain the initial null camera.
    val liveCamera by rememberUpdatedState(camera)
    val liveOnCamera by rememberUpdatedState(onCamera)
    val liveOnSelect by rememberUpdatedState(onSelect)
    val liveEnableSelect by rememberUpdatedState(enableSelect)
    val surveyStyle = mapLook == MapLook.SURVEY_TABLE
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
            .pointerInput(viewport, bounds) {
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
                scene.backdrop,
                start = Offset(0f, 0f), end = Offset(size.width, size.height)
            )
        )
        drawRect(
            color = scene.paper,
            topLeft = Offset(left, top), size = Size(right - left, bottom - top)
        )
        clipRect(left, top, right, bottom) {
            scene.drawTerrain(this, bounds, cam, ::p)
            // Labels belong to the kilometer GRID LINES, not cell centers.
            for (easting in bounds.west.toInt()..bounds.east.toInt() step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                drawLine(scene.gridInk, Offset(x, top), Offset(x, bottom), strokeWidth = 1.5f)
            }
            for (northing in bounds.south.toInt()..bounds.north.toInt() step 1000) {
                val y = p(bounds.west, northing.toDouble()).y
                drawLine(scene.gridInk, Offset(left, y), Offset(right, y), strokeWidth = 1.5f)
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
                // Phone QA: the tiny active dot got lost among green tree canopies.
                // A fixed-screen-size cream halo and dark center clearly distinguish
                // the active puzzle target from completed gold checkmark stamps.
                val center = p(point.easting, point.northing)
                drawCircle(Color(0x99FFF9E8), radius = 30f, center = center)
                drawCircle(Color(0xFFFFFDF0), radius = 23f, center = center)
                drawCircle(Color(0xFFC5892F), radius = 17.5f, center = center)
                drawCircle(Color(0xFF244236), radius = 7f, center = center)
            }
        }
        // Double rule stays on the existing world edge; it does not inset
        // the grid, alter the camera viewport or reserve touchable map space.
        if (!surveyStyle) {
            drawRect(Color(0xFFD5C092), topLeft = Offset(left, top),
                size = Size(right - left, bottom - top), style = Stroke(3.dp.toPx()))
        }
        drawRect(
            color = if (surveyStyle) Color(0xFF67785F) else Color(0xFF786849),
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top), style = Stroke(1.dp.toPx())
        )
        // Separate axis rails prevent the 24/37 overlap seen at the SW corner.
        // When zoomed, anchor labels near the viewport edge instead of leaving
        // the only useful ticks far offscreen with the map's original boundary.
        drawIntoCanvas { canvas ->
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = if (surveyStyle) android.graphics.Color.rgb(30, 64, 78)
                else android.graphics.Color.rgb(38, 68, 56)
                textSize = 11.sp.toPx().coerceAtMost((1000 * cam.pixelsPerMeter).toFloat() * .28f).coerceAtLeast(9f)
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
            val labelHeight = paint.fontMetrics.descent - paint.fontMetrics.ascent
            val labelWidth = paint.measureText("00")
            val inset = 3.dp.toPx()
            val eastingBaseline = (bottom + labelHeight + inset)
                .coerceAtMost(size.height - inset - paint.fontMetrics.descent)
            val northingColumn = (left - labelWidth - inset).coerceAtLeast(inset)
            // Reserve the lower-left intersection for northing labels, and
            // use measured glyph width rather than raw-pixel guesses.
            for (easting in bounds.west.toInt() until bounds.east.toInt() step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                val labelX = x + inset
                if (x >= 0f && labelX + labelWidth <= size.width - inset) {
                    gridLabel((easting / 1000).toString(), labelX, eastingBaseline)
                }
            }
            for (northing in bounds.south.toInt() until bounds.north.toInt() step 1000) {
                val y = p(bounds.west, northing.toDouble()).y
                val centered = y - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f
                // Keep the southwest northing above the easting row instead
                // of suppressing the first easting (24) when the rails meet.
                val baseline = if (centered + paint.fontMetrics.descent >
                    eastingBaseline + paint.fontMetrics.ascent - inset &&
                    y >= size.height - labelHeight - 2 * inset)
                    eastingBaseline + paint.fontMetrics.ascent - paint.fontMetrics.descent - inset
                else centered
                if (y in 0f..size.height && baseline + paint.fontMetrics.ascent >= inset &&
                    baseline + paint.fontMetrics.descent <= size.height - inset) {
                    gridLabel((northing / 1000).toString(), northingColumn, baseline)
                }
            }
        }

    }
}
