package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.playereden.gridhunt.core.WoodlandLandmarks

/**
 * One original shape renderer used BOTH by the real woodland map and its
 * Map Guide previews. This prevents a guide showing a different icon from
 * the tiny feature the player must find. All positions stay in core data.
 */
internal fun DrawScope.drawWoodlandLandmark(
    landmarkId: String,
    center: Offset,
    landmarkScale: Float,
    surveyStyle: Boolean
) {
    val moss = if (surveyStyle) Color(0xFF69958D) else Color(0xFF6E956A)
    val deepMoss = if (surveyStyle) Color(0xFF426F72) else Color(0xFF416D4E)
    val terracotta = if (surveyStyle) Color(0xFF9C7D70) else Color(0xFFC17B55)
    val ivory = if (surveyStyle) Color(0xFFE6F1EA) else Color(0xFFFFE9BB)
    when (landmarkId) {
        WoodlandLandmarks.mushrooms.id -> {
            val m = center
            val r = landmarkScale
            drawOval(Color(0x3B506C4A),
                topLeft = m + Offset(-r * 1.2f, r * 0.45f),
                size = Size(r * 2.4f, r * 0.5f))
            for (j in 0..1) {
                val x = m.x + if (j == 0) -r * 0.43f else r * 0.42f
                val y = m.y + if (j == 0) r * 0.08f else r * 0.19f
                val scale = if (j == 0) 0.95f else 0.68f
                drawLine(ivory, Offset(x, y), Offset(x, y + r * 0.72f * scale),
                    strokeWidth = r * 0.35f * scale)
                drawOval(terracotta,
                    topLeft = Offset(x - r * 0.57f * scale, y - r * 0.32f * scale),
                    size = Size(r * 1.14f * scale, r * 0.59f * scale))
                drawCircle(ivory, radius = r * 0.095f * scale,
                    center = Offset(x - r * 0.12f * scale, y - r * 0.1f * scale))
            }
        }
        WoodlandLandmarks.flowers.id -> {
            val c = center
            for (j in 0 until 3) {
                val bloomCenter = c + Offset((j - 1) * landmarkScale * 0.92f,
                    if (j == 1) -landmarkScale * 0.27f else landmarkScale * 0.4f)
                val bloom = landmarkScale * (if (j == 1) 0.38f else 0.28f)
                for (petal in 0 until 5) {
                    val theta = petal * 6.2831853f / 5f
                    val dx = kotlin.math.cos(theta) * bloom
                    val dy = kotlin.math.sin(theta) * bloom
                    drawCircle(ivory, bloom * 0.50f,
                        bloomCenter + Offset(dx, dy))
                }
                drawCircle(terracotta, bloom * 0.31f, bloomCenter)
            }
        }
        WoodlandLandmarks.acorn.id -> {
            val a = center
            val r = landmarkScale * 0.75f
            drawOval(terracotta, topLeft = a + Offset(-r * 0.65f, -r * 0.18f),
                size = Size(r * 1.3f, r * 1.35f))
            drawOval(deepMoss, topLeft = a + Offset(-r * 0.75f, -r * 0.4f),
                size = Size(r * 1.5f, r * 0.55f))
            drawLine(deepMoss, a + Offset(0f, -r * 0.45f),
                a + Offset(r * 0.12f, -r * 0.80f), strokeWidth = r * 0.16f)
        }
        WoodlandLandmarks.waystone.id -> {
            val c = center
            val r = landmarkScale * 0.84f
            drawOval(Color(0x3B5C6E62), topLeft = c + Offset(-r, r * 0.52f),
                size = Size(r * 2f, r * 0.42f))
            drawOval(if (surveyStyle) Color(0xFF8DACA4) else Color(0xFFAAA88C),
                topLeft = c + Offset(-r * 0.73f, -r),
                size = Size(r * 1.46f, r * 1.82f))
            drawLine(moss, c + Offset(-r * 0.2f, -r * 0.2f),
                c + Offset(r * 0.18f, r * 0.11f), strokeWidth = r * 0.12f)
        }
        else -> error("Unknown authored woodland symbol ID: $landmarkId")
    }
}

@Composable
internal fun WoodlandLandmarkSwatch(
    landmarkId: String,
    mapLook: MapLook,
    modifier: Modifier = Modifier,
    symbolSize: androidx.compose.ui.unit.Dp = 66.dp
) {
    val surveyStyle = mapLook == MapLook.SURVEY_TABLE
    val color = if (surveyStyle) Color(0xFFDCEFEB) else Color(0xFFF3E8CE)
    Box(
        modifier = modifier
            .size(symbolSize)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .semantics {
                contentDescription =
                    (WoodlandLandmarks.byId(landmarkId)?.name ?: "Woodland") + " map symbol"
            }
    ) {
        Canvas(Modifier.size(symbolSize)) {
            drawWoodlandLandmark(
                landmarkId = landmarkId,
                center = Offset(size.width / 2f, size.height / 2f),
                landmarkScale = size.minDimension * 0.25f,
                surveyStyle = surveyStyle
            )
        }
    }
}
