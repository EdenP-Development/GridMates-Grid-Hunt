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

import androidx.compose.ui.graphics.drawscope.DrawScope

/** Woodland-specific scenery; shared canvas owns grid, gestures and gameplay overlays. */
internal fun DrawScope.drawWoodlandTerrain(
    bounds: WorldBounds, cam: MapCamera, surveyStyle: Boolean,
    landmarkSprites: Map<String, ImageBitmap?>,
    oakSprite: ImageBitmap?, pineSprite: ImageBitmap?,
    rocksSprite: ImageBitmap?, fernsSprite: ImageBitmap?,
    logSprite: ImageBitmap?, pondSprite: ImageBitmap?,
    treeDark: Color, treeLight: Color, streamInk: Color, trailInk: Color,
    p: (Double, Double) -> Offset
) {
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
}
