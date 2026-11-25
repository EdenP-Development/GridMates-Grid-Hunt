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
    onLook: ((MapLook) -> Unit)? = null
) {
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
    mapLook: MapLook
) {
    // Stable pointer coroutine with fresh state. Capturing 'camera' directly in
    // a long-lived pointerInput(viewport) block can retain the initial null camera.
    val liveCamera by rememberUpdatedState(camera)
    val liveOnCamera by rememberUpdatedState(onCamera)
    val liveOnSelect by rememberUpdatedState(onSelect)
    val liveEnableSelect by rememberUpdatedState(enableSelect)
    val surveyStyle = mapLook == MapLook.SURVEY_TABLE
    val landmarkSprites = if (surveyStyle) emptyMap() else mapOf(
        WoodlandLandmarks.mushrooms.id to woodlandLandmarkSprite(WoodlandLandmarks.mushrooms.id),
        WoodlandLandmarks.flowers.id to woodlandLandmarkSprite(WoodlandLandmarks.flowers.id),
        WoodlandLandmarks.acorn.id to woodlandLandmarkSprite(WoodlandLandmarks.acorn.id),
        WoodlandLandmarks.waystone.id to woodlandLandmarkSprite(WoodlandLandmarks.waystone.id)
    )
    val oakSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_wood_oak_sm)
    val pineSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_wood_pine_sm)
    val rocksSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_rocks)
    val fernsSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_ferns)
    val logSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_log)
    val pondSprite = if (surveyStyle) null else ImageBitmap.imageResource(R.drawable.gh_env_pond)
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
            // Soft irregular world-anchored washes, not new geographic features.
            // Preserve the original clearing centers and extents.
            fun organicPatch(e: Double, n: Double, re: Double, rn: Double, seed: Int, ink: Color) {
                val points = (0 until 12).map { i ->
                    val angle = i * Math.PI / 6.0
                    val variation = 0.82 + ((i * 37 + seed * 19) % 17) / 100.0
                    p(e + kotlin.math.cos(angle) * re * variation,
                        n + kotlin.math.sin(angle) * rn * variation)
                }
                val patch = Path().apply {
                    val start = (points.last() + points.first()) / 2f
                    moveTo(start.x, start.y)
                    points.forEachIndexed { i, point ->
                        val next = points[(i + 1) % points.size]
                        val midpoint = (point + next) / 2f
                        quadraticBezierTo(point.x, point.y, midpoint.x, midpoint.y)
                    }
                    close()
                }
                drawPath(patch, ink)
            }
            val patches = listOf(
                doubleArrayOf(24480.0, 41100.0, 510.0, 360.0),
                doubleArrayOf(26720.0, 41900.0, 630.0, 400.0),
                doubleArrayOf(29000.0, 40300.0, 700.0, 450.0),
                doubleArrayOf(25800.0, 38100.0, 640.0, 510.0),
                doubleArrayOf(28100.0, 39350.0, 390.0, 300.0)
            )
            patches.forEachIndexed { i, oval ->
                if (surveyStyle) landEllipse(oval[0], oval[1], oval[2], oval[3],
                    if (i % 2 == 0) meadow else meadowLight)
                else {
                    val tint = if (i % 2 == 0) Color(0x307DA263) else Color(0x38D6C58C)
                    organicPatch(oval[0], oval[1], oval[2] * 1.16, oval[3] * 1.16, i, tint.copy(alpha = .10f))
                    organicPatch(oval[0], oval[1], oval[2], oval[3], i, tint)
                    organicPatch(oval[0], oval[1], oval[2] * .72, oval[3] * .70, i + 7,
                        if (i % 2 == 0) Color(0x18A2B77A) else Color(0x20F1DFAC))
                }
            }
            if (!surveyStyle) {
                // Quiet broad ground washes unify the scene at all zoom levels.
                organicPatch(24800.0, 42400.0, 1000.0, 740.0, 11, Color(0x197FAD78))
                organicPatch(28700.0, 41900.0, 1150.0, 650.0, 12, Color(0x167FAD78))
                organicPatch(28000.0, 37800.0, 1480.0, 600.0, 13, Color(0x1F9FB37C))
            }
            // GH-V36-I: layered, subdued paper terrain. These are washes, not
            // contour lines, named features, or clues. Keep the survey view intact.
            if (!surveyStyle) {
                organicPatch(25300.0, 41350.0, 1450.0, 1050.0, 21, Color(0x197BA36D))
                organicPatch(28500.0, 42300.0, 1400.0, 820.0, 22, Color(0x227BA36D))
                organicPatch(24700.0, 39300.0, 930.0, 1050.0, 23, Color(0x1C9EAF77))
                organicPatch(28200.0, 38050.0, 1700.0, 1000.0, 24, Color(0x217BA36D))
                organicPatch(26800.0, 40200.0, 1150.0, 760.0, 25, Color(0x25F7DDA3))
                // Sparse grass strokes are subordinate to every playable symbol.
                // World-space positions remain stable as the camera moves.
                for (i in 0 until 84) {
                    val e = 24200.0 + ((i * 977 + 131) % 5600)
                    val n = 37200.0 + ((i * 643 + 307) % 5600)
                    if (WoodlandLandmarks.all.any {
                        kotlin.math.abs(it.point.easting - e) < 450.0 &&
                            kotlin.math.abs(it.point.northing - n) < 450.0
                    }) continue
                    val center = p(e, n)
                    val tuft = (65.0 * cam.pixelsPerMeter).toFloat().coerceAtMost(4.dp.toPx())
                    if (tuft < 1.dp.toPx()) continue
                    val ink = Color(0x3B71885A)
                    drawLine(ink, center, center + Offset(-tuft * .65f, -tuft),
                        strokeWidth = .65.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                    drawLine(ink, center, center + Offset(tuft * .55f, -tuft * .85f),
                        strokeWidth = .65.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                }
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
            // Small companion trees form groves around existing decorative
            // anchors. They never enter the core map vocabulary or save state.
            if (!surveyStyle) {
                val companions = listOf(
                    Triple(24320.0, 42580.0, 290.0),
                    Triple(24760.0, 42720.0, 330.0),
                    Triple(25890.0, 42800.0, 270.0),
                    Triple(29120.0, 42660.0, 310.0),
                    Triple(29650.0, 41930.0, 280.0),
                    Triple(24190.0, 39870.0, 260.0),
                    Triple(26300.0, 37340.0, 310.0),
                    Triple(26840.0, 37410.0, 270.0),
                    Triple(29060.0, 37240.0, 300.0)
                )
                companions.forEachIndexed { i, (e, n, meters) ->
                    val half = meters / 2.0
                    val clear = bounds.contains(WorldPoint(e-half,n-half)) &&
                        bounds.contains(WorldPoint(e+half,n+half)) &&
                        WoodlandLandmarks.all.none { landmark ->
                            kotlin.math.abs(landmark.point.easting-e) < half+700.0 &&
                            kotlin.math.abs(landmark.point.northing-n) < half+700.0
                        }
                    val bitmap = if (i % 2 == 0) pineSprite else oakSprite
                    val side = (meters * cam.pixelsPerMeter).toFloat().coerceAtMost(44.dp.toPx())
                    if (clear && bitmap != null && side >= 8.dp.toPx()) {
                        drawWoodlandSprite(bitmap, p(e,n), side, alpha = .58f)
                    }
                }
            }
            trees.forEachIndexed { i, pair ->
                val center = p(pair.first, pair.second)
                val base = (205.0 * cam.pixelsPerMeter).toFloat().coerceIn(7f, 40f)
                val radius = base * (0.9f + (i % 4) * 0.07f)
                val treeSprite = if (i % 3 == 0) pineSprite else oakSprite
                if (treeSprite != null) {
                    // A subtle ground shadow helps the existing illustrated trees
                    // sit on the terrain rather than appear as floating stickers.
                    drawOval(Color(0x2862774E),
                        topLeft = center + Offset(-radius * .86f, radius * .40f),
                        size = Size(radius * 1.72f, radius * .48f))
                    drawWoodlandSprite(treeSprite, center, radius * 2.65f, alpha = 0.78f)
                    return@forEachIndexed
                }
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
            // GH-V26-C: quiet decorative accents anchored in world meters.
            // Keep them smaller and softer than answer-bearing landmarks. Never
            // use these positions for objectives, clues, selection or persistence.
            // The stream, trail, canonical symbols and grid are drawn afterward.
            if (!surveyStyle) {
                fun scenery(bitmap: ImageBitmap?, e: Double, n: Double, meters: Double) {
                    if (bitmap == null) return
                    val half = meters / 2.0
                    if (!bounds.contains(WorldPoint(e-half,n-half)) ||
                        !bounds.contains(WorldPoint(e+half,n+half))) return
                    // Reserve a generous exclusion zone around canonical symbols.
                    if (WoodlandLandmarks.all.any { landmark ->
                            kotlin.math.abs(landmark.point.easting-e) < half+700.0 &&
                            kotlin.math.abs(landmark.point.northing-n) < half+700.0
                        }) return
                    val side = (meters * cam.pixelsPerMeter).toFloat().coerceAtMost(64.dp.toPx())
                    if (side >= 8.dp.toPx()) drawWoodlandSprite(bitmap, p(e,n), side, alpha = .70f)
                }
                scenery(rocksSprite, 24650.0, 40800.0, 420.0)
                scenery(fernsSprite, 27480.0, 41250.0, 360.0)
                scenery(logSprite, 28900.0, 38800.0, 480.0)
                scenery(pondSprite, 24600.0, 37800.0, 620.0)
                scenery(fernsSprite, 28150.0, 37450.0, 360.0)
            }
            val stream = Path().apply {
                val a = p(24900.0, 43000.0)
                moveTo(a.x, a.y)
                val b = p(25650.0, 40900.0)
                val c = p(26200.0, 39700.0)
                val d = p(27900.0, 37800.0)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            }
            // Original centerline is unchanged. Round, layered banks and water
            // replace the ruler-like central stripe in the illustrated map.
            val creekWidth = (90.0 * cam.pixelsPerMeter).toFloat().coerceIn(9f, 25f)
            if (surveyStyle) {
                drawPath(stream, Color(0xFFAAC4C3), style = Stroke(width = creekWidth + 8f))
                drawPath(stream, Color(0xFFA1CCD2), style = Stroke(width = creekWidth))
                drawPath(stream, streamInk, style = Stroke(width = creekWidth * 0.27f))
            } else {
                val round = androidx.compose.ui.graphics.StrokeCap.Round
                fun creekStroke(color: Color, width: Float) {
                    drawPath(stream, color, style = Stroke(width = width, cap = round,
                        join = androidx.compose.ui.graphics.StrokeJoin.Round))
                }
                creekStroke(Color(0x387D9D69), creekWidth * 1.90f)
                creekStroke(Color(0xFFB4B693), creekWidth * 1.48f)
                creekStroke(Color(0xFFDFD0A4), creekWidth * 1.19f)
                creekStroke(Color(0xFF719EA4), creekWidth)
                creekStroke(Color(0xFF9BC5C6), creekWidth * .77f)
                creekStroke(Color(0x99D5E7D9), creekWidth * .25f)
            }
            val trail = Path().apply {
                val a = p(24000.0, 38950.0)
                moveTo(a.x, a.y)
                val b = p(26100.0, 38600.0)
                val c = p(27900.0, 39800.0)
                val d = p(30000.0, 40700.0)
                cubicTo(b.x, b.y, c.x, c.y, d.x, d.y)
            }
            if (surveyStyle) {
                drawPath(trail, Color(0x33809F9B), style = Stroke(width = 10f))
                drawPath(trail, trailInk, style = Stroke(width = 3f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(11f, 7f))))
            } else {
                val pathWidth = (75.0 * cam.pixelsPerMeter).toFloat()
                    .coerceIn(3.dp.toPx(), 9.dp.toPx())
                drawPath(trail, Color(0x447A8056), style = Stroke(width = pathWidth * 1.6f,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round))
                drawPath(trail, Color(0xFFD7BE8B), style = Stroke(width = pathWidth,
                    cap = androidx.compose.ui.graphics.StrokeCap.Round))
                drawPath(trail, Color(0xFF987B50), style = Stroke(width = 1.1.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(5.dp.toPx(), 4.dp.toPx()))))
            }
            // The exact same glyph renderer powers the interactive Map Guide.
            // World positions are shared with the core answer data, not copied.
            val landmarkScale = if (surveyStyle) (130.0 * cam.pixelsPerMeter).toFloat().coerceIn(10f, 29f)
                else (150.0 * cam.pixelsPerMeter).toFloat().coerceIn(5.dp.toPx(), 15.dp.toPx())
            WoodlandLandmarks.all.forEach { landmark ->
                drawWoodlandLandmark(
                    landmarkId = landmark.id,
                    center = p(landmark.point.easting, landmark.point.northing),
                    landmarkScale = landmarkScale,
                    surveyStyle = surveyStyle,
                    sprite = landmarkSprites[landmark.id]
                )
            }
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
            for (easting in 24000..29000 step 1000) {
                val x = p(easting.toDouble(), bounds.south).x
                val labelX = x + inset
                if (x >= 0f && labelX + labelWidth <= size.width - inset) {
                    gridLabel((easting / 1000).toString(), labelX, eastingBaseline)
                }
            }
            for (northing in 37000..42000 step 1000) {
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
