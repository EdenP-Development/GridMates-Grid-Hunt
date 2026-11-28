package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

/**
 * A small book-end to a real completed puzzle. No invented points, coins or
 * unlocks: the progress claim comes directly from the existing save state.
 *
 * Keeps the map visible and provides an explicit replay / next-trail choice;
 * returning Home does not reset a completed chapter or golden-spot puzzle.
 */
@Composable
internal fun PuzzleFinishCard(
    title: String,
    description: String,
    discoveries: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    onHome: () -> Unit,
    primaryTag: String = "finishContinue"
) {
    Surface(
        modifier = Modifier.fillMaxWidth().testTag("puzzleFinishCard"),
        shape = RoundedCornerShape(19.dp),
        color = Color(0xFFFFF4D8),
        border = BorderStroke(1.dp, Color(0xFFD8B779)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                DiscoverySeal(Modifier.size(60.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(discoveries, color = Forest, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Text(title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                }
            }
            Text(description, color = Muted, fontSize = 12.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onPrimary,
                    shape = JournalActionShape,
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                        .testTag(primaryTag),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(primaryLabel, fontWeight = FontWeight.Bold,
                        fontSize = 12.sp, maxLines = 2)
                }
                OutlinedButton(
                    onClick = onHome,
                    shape = JournalActionShape,
                    modifier = Modifier.weight(0.54f).heightIn(min = 48.dp)
                        .testTag("finishHome"),
                    contentPadding = PaddingValues(horizontal = 7.dp)
                ) {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

/** Completion decoration only; not an Academy qualification or collectible. */
@Composable
private fun DiscoverySeal(modifier: Modifier) {
    Canvas(modifier) {
        val s = size.minDimension
        val c = center
        val rim = Color(0xFF97713E)
        val gold = Color(0xFFD5A75E)
        val cream = Color(0xFFF6E8C6)
        for (i in 0 until 16) {
            val a = i * Math.PI / 8.0
            drawCircle(gold, s*.09f, c + Offset(kotlin.math.cos(a).toFloat(), kotlin.math.sin(a).toFloat()) * s*.36f)
        }
        drawCircle(gold,s*.39f,c)
        drawCircle(rim,s*.365f,c,style=Stroke(s*.018f))
        drawCircle(cream,s*.30f,c)
        drawCircle(rim,s*.27f,c,style=Stroke(s*.012f))
        val a = c + Offset(-s*.14f,0f)
        val mid = c + Offset(-s*.035f,s*.10f)
        val end = c + Offset(s*.16f,-s*.12f)
        drawLine(Forest,a,mid,s*.055f,cap=StrokeCap.Round)
        drawLine(Forest,mid,end,s*.055f,cap=StrokeCap.Round)
    }
}
