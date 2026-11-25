package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Compact native-game quick-play tiles. Each tile has ONE interactive action:
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
        border = JournalPanelBorder,
        shape = RoundedCornerShape(19.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            Modifier.fillMaxWidth().fillMaxHeight().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                PuzzleModeIllustration(tag, glyph, Modifier.size(38.dp))
                Text(label, fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold, color = Ink, modifier = Modifier.weight(1f))
            }
            Text(instruction, fontSize = 11.sp, color = Muted)
            Text(progress, fontSize = 11.sp,
                fontWeight = FontWeight.Bold, color = Forest)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 48.dp).testTag(tag),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp),
                shape = JournalActionShape,
                colors = ButtonDefaults.buttonColors(containerColor = Forest)
            ) {
                Text("Play", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Original field-journal illustrations; no puzzle answers encoded in decorative art. */
@Composable
internal fun PuzzleModeIllustration(tag: String, glyph: String, modifier: Modifier) {
    val asset = when (tag) {
        "openChapter" -> R.drawable.gh_tool_map
        "openReverse" -> R.drawable.gh_tool_marker
        "openTreasure" -> R.drawable.gh_tool_checklist
        "openIdentify" -> R.drawable.gh_tool_magnifier
        "mixed" -> R.drawable.gh_tool_notebook
        else -> null
    }
    if (asset == null) {
        Text(glyph, fontSize = 29.sp, color = Forest)
    } else {
        Image(painterResource(asset), contentDescription = null, modifier = modifier)
    }
}
