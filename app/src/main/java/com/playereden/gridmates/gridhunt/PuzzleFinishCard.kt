package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.BorderStroke
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
    onHome: () -> Unit
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
            Text("✦  $discoveries", color = Forest,
                fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(title, color = Ink, fontWeight = FontWeight.ExtraBold,
                fontSize = 19.sp, maxLines = 2)
            Text(description, color = Muted, fontSize = 12.sp, maxLines = 3)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onPrimary,
                    modifier = Modifier.weight(1f).heightIn(min = 46.dp)
                        .testTag("finishContinue"),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Text(primaryLabel, fontWeight = FontWeight.Bold,
                        fontSize = 12.sp, maxLines = 2)
                }
                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier.weight(0.54f).heightIn(min = 46.dp)
                        .testTag("finishHome"),
                    contentPadding = PaddingValues(horizontal = 7.dp)
                ) {
                    Text("Home", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
