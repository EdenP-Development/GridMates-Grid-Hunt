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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Layered illustrated woodland postcard for Home and Custom Play.
 * It is intentionally a picture-book scene, NOT the answer-bearing grid.
 * The real puzzle map and all its world coordinates remain in GridMapCanvas.
 */
@Composable
internal fun WoodlandPostcard(modifier: Modifier = Modifier) {
    val oakArt = ImageBitmap.imageResource(R.drawable.gh_wood_oak_sm)
    val pineArt = ImageBitmap.imageResource(R.drawable.gh_wood_pine_sm)
    val rocksArt = ImageBitmap.imageResource(R.drawable.gh_env_rocks)
    val fernsArt = ImageBitmap.imageResource(R.drawable.gh_env_ferns)
    val logArt = ImageBitmap.imageResource(R.drawable.gh_env_log)
    val pondArt = ImageBitmap.imageResource(R.drawable.gh_env_pond)
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

            // Original layered storybook woodland: tapered evergreens,
            // broadleaf silhouettes, trunk details and tiny meadow marks.
            // All positions are decorative and independent of puzzle data.
            fun pine(x: Float, y: Float, tall: Float, alpha: Float = 1f) {
                val t = h * tall
                drawWoodlandSprite(pineArt, point(x, y) - Offset(0f, t * .40f), t * 1.18f, alpha)
            }
            fun oak(x: Float, y: Float, tall: Float) {
                val t = h * tall
                drawWoodlandSprite(oakArt, point(x, y) - Offset(0f, t * .40f), t * 1.18f)
            }
            // Distant silhouettes, then foreground grove, avoiding the trail.
            listOf(Triple(.05f,.52f,.28f),Triple(.15f,.52f,.23f),
                Triple(.39f,.47f,.24f),Triple(.46f,.47f,.19f),
                Triple(.89f,.45f,.26f),Triple(.96f,.49f,.30f)).forEach { (x,y,t) ->
                pine(x,y,t,alpha=.66f)
            }
            oak(.12f,.78f,.54f)
            pine(.23f,.73f,.42f)
            oak(.38f,.75f,.39f)
            pine(.91f,.79f,.56f)
            oak(.82f,.85f,.38f)
            pine(.98f,.91f,.47f)
            // Decorative foreground accents; leave the golden marker and trail clear.
            drawWoodlandSprite(rocksArt, point(.30f, .59f), h * .24f)
            drawWoodlandSprite(fernsArt, point(.46f, .86f), h * .26f)
            drawWoodlandSprite(logArt, point(.87f, .87f), h * .32f)
            drawWoodlandSprite(pondArt, point(.66f, .35f), h * .30f)
            // Fine meadow marks and golden blossoms at fixed decorative points.
            repeat(26) { i ->
                val x = ((i*37+11)%97)/100f
                val y = .78f+((i*19)%19)/100f + (i%4)*.035f
                val p = point(x,y)
                val g = h*.025f
                drawLine(Color(0xFF527954),p,p+Offset(-g*.4f,-g),strokeWidth=1.dp.toPx())
                drawLine(Color(0xFF527954),p,p+Offset(g*.45f,-g*.8f),strokeWidth=1.dp.toPx())
                if(i%4==0) drawCircle(Color(0xFFF2D48B),h*.012f,p+Offset(0f,-g))
            }
            // Restrained paper-grain specks, deterministic across recompositions.
            repeat(95) { i ->
                drawCircle(Color(0x12695335), .55.dp.toPx(),
                    point(((i*43+7)%101)/101f,((i*67+13)%103)/103f))
            }
            // Little golden destination marker: a playful visual promise.
            val treasure = point(.62f, .73f)
            drawCircle(Color(0xFFFDF6DA), h * .062f, treasure)
            drawCircle(Color(0xFFD28F42), h * .042f, treasure)
            drawLine(Color.White, treasure + Offset(-7f, -7f),
                treasure + Offset(7f, 7f), strokeWidth = 1.5.dp.toPx())
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
