package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.WhisperingCoastDraft
import com.playereden.gridhunt.core.WoodlandLandmark

/** Coordinate-free vocabulary guide for the unpublished coastal map. */
@Composable
internal fun CoastalLandmarkSwatch(landmark: WoodlandLandmark, look: MapLook, symbolSize: Dp = 62.dp) {
    val resource = when (landmark.id) {
        "beacon-point" -> R.drawable.gh_coast_beacon_point
        "driftwood-cove" -> R.drawable.gh_coast_driftwood_cove
        "shell-beach" -> R.drawable.gh_coast_shell_beach
        "gull-rock" -> R.drawable.gh_coast_gull_rock
        "tidepool-bend" -> R.drawable.gh_coast_tidepool_bend
        else -> null
    }
    if (landmark.id == "old-jetty") {
        Canvas(Modifier.size(symbolSize)) {
            val a = Offset(size.width*.15f,size.height*.45f)
            val b = Offset(size.width*.85f,size.height*.55f)
            drawLine(Color(0xFF806447),a,b,size.width*.16f)
            drawLine(Color(0xFFC29B6E),a,b,size.width*.09f)
            drawCircle(Color(0xFF655E4C),size.width*.035f,b)
        }
    } else if (resource != null) {
        // Draft guide uses the illustrated vocabulary in both appearances.
        // Survey-specific swatch parity is a separate visual approval gate.
        Image(painterResource(resource), contentDescription = landmark.name,
            modifier = Modifier.size(symbolSize))
    }
}

@Composable
internal fun CoastalMapGuideDialog(look: MapLook, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("coastalMapGuide"),
        title = { Text("Explore the Coast", color = Ink, fontWeight = FontWeight.Bold) },
        text = {
            Column(Modifier.heightIn(max = 480.dp).journalScroll(),
                verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Match these landmarks to the drawings on your map. The Survey Table uses simpler versions of the same shapes.",
                    color = Ink, fontSize = 13.sp)
                WhisperingCoastDraft.map.landmarks.forEach { landmark ->
                    Row(Modifier.fillMaxWidth().testTag("guideSymbol_${landmark.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CoastalLandmarkSwatch(landmark, look)
                        Text(landmark.name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
                Text("SCENERY", color = Forest, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Grass, shrubs, small rocks and waves are decorative. The six named landmarks are your checklist targets.",
                    color = Muted, fontSize = 12.sp)
                Text("READING A FOUR-DIGIT SQUARE", color = Forest, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Read eastings first, then northings. Use the square above and right of the numbered grid lines.",
                    color = Ink, fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("dismissCoastalMapGuide")) {
                Text("Back to the hunt", color = Forest, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFFFFF8E9)
    )
}
