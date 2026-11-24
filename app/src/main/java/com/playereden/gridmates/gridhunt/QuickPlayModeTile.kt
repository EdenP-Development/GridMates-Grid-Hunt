package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact native-game quick-play tiles. Full tile is ONE interactive action:
 * there is no nested decorative button or tiny separate icon hit target.
 */
@Composable
internal fun QuickPlayModeTile(
    label: String,
    glyph: String,
    instruction: String,
    progress: String,
    tag: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(19.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            PuzzleModeIllustration(tag, glyph, Modifier.size(58.dp))
            Text(label, fontSize = 14.sp, maxLines = 2,
                fontWeight = FontWeight.ExtraBold, color = Ink)
            Text(instruction, fontSize = 11.sp, maxLines = 2, color = Muted)
            Text(progress, fontSize = 11.sp, maxLines = 1,
                fontWeight = FontWeight.Bold, color = Forest)
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 48.dp).testTag(tag),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Forest)
            ) {
                Text("Play", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Original field-journal illustrations; no puzzle answers encoded in decorative art. */
@Composable
private fun PuzzleModeIllustration(tag: String, glyph: String, modifier: Modifier) {
    if (tag !in setOf("openChapter", "openReverse", "openTreasure", "openIdentify")) {
        Text(glyph, fontSize = 29.sp, color = Forest)
        return
    }
    Canvas(modifier) {
        val s = size.minDimension
        fun p(x: Float,y: Float) = Offset(s*x,s*y)
        val paper = Color(0xFFF8EAC7)
        val gold = Color(0xFFD5A75E)
        val bronze = Color(0xFF947044)
        val green = Color(0xFF416C51)
        val light = Color(0xFFA7BB82)
        drawCircle(Color(0xFFFAF3DE),s*.46f,p(.5f,.5f))
        drawCircle(bronze.copy(alpha=.55f),s*.45f,p(.5f,.5f),style=Stroke(s*.012f))
        when(tag) {
            "openChapter", "openReverse" -> {
                drawRoundRect(paper,p(.17f,.20f),Size(s*.66f,s*.60f),CornerRadius(s*.045f))
                drawRoundRect(bronze,p(.17f,.20f),Size(s*.66f,s*.60f),CornerRadius(s*.045f),style=Stroke(s*.025f))
                drawRect(light,p(.39f,.40f),Size(s*.22f,s*.20f))
                for(i in 1..2) {
                    drawLine(green,p(.17f+i*.22f,.20f),p(.17f+i*.22f,.80f),s*.013f)
                    drawLine(green,p(.17f,.20f+i*.20f),p(.83f,.20f+i*.20f),s*.013f)
                }
                if(tag=="openChapter") {
                    drawRect(gold,p(.40f,.41f),Size(s*.20f,s*.18f),style=Stroke(s*.035f))
                    val route=Path().apply {
                        moveTo(s*.23f,s*.72f);quadraticBezierTo(s*.35f,s*.48f,s*.50f,s*.50f)
                    }
                    drawPath(route,green,style=Stroke(s*.033f))
                    drawCircle(green,s*.035f,p(.50f,.50f))
                } else {
                    val star=Path().apply {
                        moveTo(s*.50f,s*.28f);lineTo(s*.55f,s*.44f)
                        lineTo(s*.71f,s*.50f);lineTo(s*.55f,s*.56f)
                        lineTo(s*.50f,s*.72f);lineTo(s*.45f,s*.56f)
                        lineTo(s*.29f,s*.50f);lineTo(s*.45f,s*.44f);close()
                    }
                    drawPath(star,gold)
                    drawPath(star,bronze,style=Stroke(s*.02f))
                }
            }
            "openTreasure" -> {
                drawRoundRect(bronze,p(.23f,.18f),Size(s*.54f,s*.66f),CornerRadius(s*.045f))
                drawRoundRect(paper,p(.27f,.23f),Size(s*.46f,s*.56f),CornerRadius(s*.025f))
                drawRoundRect(gold,p(.38f,.15f),Size(s*.24f,s*.13f),CornerRadius(s*.03f))
                for(i in 0..2) {
                    val y=.36f+i*.14f
                    drawRect(green,p(.32f,y),Size(s*.09f,s*.09f),style=Stroke(s*.016f))
                    if(i<2) {
                        drawLine(green,p(.33f,y+.04f),p(.36f,y+.075f),s*.022f)
                        drawLine(green,p(.36f,y+.075f),p(.43f,y-.01f),s*.022f)
                    }
                    drawLine(bronze,p(.48f,y+.045f),p(.66f,y+.045f),s*.02f)
                }
            }
            "openIdentify" -> {
                drawLine(bronze,p(.61f,.62f),p(.80f,.81f),s*.10f)
                drawLine(gold,p(.62f,.62f),p(.79f,.79f),s*.05f)
                drawCircle(bronze,s*.26f,p(.43f,.43f))
                drawCircle(gold,s*.23f,p(.43f,.43f))
                drawCircle(paper,s*.19f,p(.43f,.43f))
                drawRoundRect(bronze,p(.40f,.43f),Size(s*.06f,s*.13f),CornerRadius(s*.018f))
                val cap=Path().apply {
                    moveTo(s*.29f,s*.44f)
                    quadraticBezierTo(s*.43f,s*.18f,s*.57f,s*.44f)
                    quadraticBezierTo(s*.43f,s*.48f,s*.29f,s*.44f);close()
                }
                drawPath(cap,Color(0xFFB06D47))
                drawCircle(paper,s*.022f,p(.43f,.34f))
                drawCircle(paper,s*.018f,p(.38f,.39f))
            }
        }
    }
}
