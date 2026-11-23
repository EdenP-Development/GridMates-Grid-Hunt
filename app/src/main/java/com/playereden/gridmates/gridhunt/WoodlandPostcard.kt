package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Original vector illustration for the home screen.
 * It is intentionally a picture-book scene, NOT the answer-bearing grid.
 * The real puzzle map and all its world coordinates remain in GridMapCanvas.
 */
@Composable
internal fun WoodlandPostcard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFE2ECCC))
            .testTag("homeIllustration")
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            fun point(x: Float, y: Float) = Offset(x * w, y * h)

            drawRect(Brush.linearGradient(
                listOf(Color(0xFFF9E8BA), Color(0xFFD3E5BE), Color(0xFFB9D7B0)),
                start = Offset.Zero, end = Offset(w, h)
            ))
            // Hills are layers of friendly paper-cut shapes, not map landmarks.
            val backHill = Path().apply {
                moveTo(0f, h * .52f)
                cubicTo(w * .18f, h * .34f, w * .36f, h * .63f, w * .55f, h * .49f)
                cubicTo(w * .78f, h * .30f, w * .91f, h * .47f, w, h * .37f)
                lineTo(w, h); lineTo(0f, h); close()
            }
            drawPath(backHill, Color(0xFFA7C99B))
            val frontHill = Path().apply {
                moveTo(0f, h * .72f)
                cubicTo(w * .2f, h * .53f, w * .4f, h * .83f, w * .64f, h * .64f)
                cubicTo(w * .8f, h * .55f, w * .92f, h * .76f, w, h * .64f)
                lineTo(w, h); lineTo(0f, h); close()
            }
            drawPath(frontHill, Color(0xFF81AA85))
            // Winding stepping path, with a brighter underlay.
            val trail = Path().apply {
                val start = point(.10f, 1f)
                moveTo(start.x, start.y)
                cubicTo(w * .55f, h * .80f, w * .20f, h * .54f, w * .55f, h * .50f)
                cubicTo(w * .72f, h * .46f, w * .72f, h * .35f, w * .78f, h * .23f)
            }
            drawPath(trail, Color(0xFF8A9D70), style = Stroke(width = 21f))
            drawPath(trail, Color(0xFFF5DCAB), style = Stroke(width = 15f))
            val sunlight = point(.80f, .18f)
            drawCircle(Color(0xFFFFF2C4), h * .12f, sunlight)
            drawCircle(Color(0xFFEABF71), h * .085f, sunlight)

            // Three soft, distinct tree groups at fixed picture-book positions.
            val groves = listOf(
                Triple(.10f, .49f, .12f),
                Triple(.35f, .57f, .15f),
                Triple(.90f, .57f, .14f),
                Triple(.57f, .29f, .095f)
            )
            groves.forEachIndexed { i, (x, y, fraction) ->
                val center = point(x, y)
                val radius = h * fraction
                drawCircle(Color(0x7C426F4E), radius * 1.06f,
                    center + Offset(radius * .16f, radius * .20f))
                drawCircle(if (i % 2 == 0) Color(0xFF547E59) else Color(0xFF66936A),
                    radius, center)
                drawCircle(Color(0xFF91B584), radius * .65f,
                    center + Offset(-radius * .36f, -radius * .26f))
                drawCircle(Color(0xFFAED09A), radius * .34f,
                    center + Offset(-radius * .43f, -radius * .40f))
            }
            // Little golden destination marker: a playful visual promise.
            val treasure = point(.62f, .73f)
            drawCircle(Color(0xFFFDF6DA), h * .062f, treasure)
            drawCircle(Color(0xFFD28F42), h * .042f, treasure)
            drawLine(Color.White, treasure + Offset(-7f, -7f),
                treasure + Offset(7f, 7f), strokeWidth = 3f)
            drawLine(Color.White, treasure + Offset(7f, -7f),
                treasure + Offset(-7f, 7f), strokeWidth = 3f)
        }
        Surface(
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
            color = Color(0xFFFDF8EA),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Text("THE LITTLE WOODLAND", color = Ink, fontWeight = FontWeight.Bold,
                fontSize = 11.sp, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp))
        }
    }
}
