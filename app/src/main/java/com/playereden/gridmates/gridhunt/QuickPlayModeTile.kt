package com.playereden.gridmates.gridhunt

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
            Text(glyph, fontSize = 29.sp, color = Forest)
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
