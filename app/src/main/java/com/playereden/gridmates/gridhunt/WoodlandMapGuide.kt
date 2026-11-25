package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.WoodlandLandmarks

/**
 * The map guide is a vocabulary reference, never an answer key.
 * Icon previews are rendered by the SAME source used by the woodland map.
 * No coordinates, revealed target positions, or hidden puzzle answers appear.
 */
@Composable
internal fun WoodlandMapGuideDialog(mapLook: MapLook, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("woodlandMapGuide"),
        title = {
            Text("Explore the Map", color = Ink, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                Modifier.heightIn(max = 480.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(
                    "Every important symbol has a meaning. Match the little drawings " +
                        "below to the ones you see on the woodland map.",
                    color = Ink, fontSize = 13.sp
                )
                Text("PLACES TO DISCOVER", color = Forest, fontWeight = FontWeight.Bold,
                    fontSize = 11.sp)
                WoodlandLandmarks.all.forEach { landmark ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .testTag("guideSymbol_${landmark.id}"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(11.dp)
                    ) {
                        WoodlandLandmarkSwatch(landmark.id, mapLook)
                        Column(Modifier.weight(1f)) {
                            Text(landmark.name, color = Ink, fontWeight = FontWeight.Bold,
                                fontSize = 13.sp)
                            Text(
                                when (landmark.id) {
                                    WoodlandLandmarks.mushrooms.id ->
                                        if (mapLook == MapLook.SURVEY_TABLE) "Two mushroom caps" else "A red-capped mushroom with white spots"
                                    WoodlandLandmarks.flowers.id ->
                                        if (mapLook == MapLook.SURVEY_TABLE) "Three pale flower symbols" else "A yellow flower with green leaves"
                                    WoodlandLandmarks.acorn.id ->
                                        if (mapLook == MapLook.SURVEY_TABLE) "An acorn with a green cap" else "A golden-brown acorn with a textured cap"
                                    WoodlandLandmarks.waystone.id ->
                                        "A small upright stone with a carved mark"
                                    else -> "Woodland landmark"
                                },
                                color = Muted, fontSize = 11.sp
                            )
                        }
                    }
                }
                Text("PATHS AND WATER", color = Forest,
                    fontWeight = FontWeight.Bold, fontSize = 11.sp)
                MapFeatureGuideRow(
                    "Creek", "Blue winding water; helps you find your way.",
                    mapLook, "creek"
                )
                MapFeatureGuideRow(
                    "Trail", "Dashed line showing a path through the woods.",
                    mapLook, "trail"
                )
                Text("SCENERY", color = Forest, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("These details help you recognize the landscape. They are not additional named discovery targets in Little Woodland.",
                    color = Muted, fontSize = 11.sp)
                if (mapLook == MapLook.SURVEY_TABLE) {
                    MapFeatureGuideRow("Trees and clearings", "Treetop symbols and pale open patches.", mapLook, "grove")
                } else {
                    SceneryGuideRow("Oak trees", "Broad, leafy canopies.", R.drawable.gh_wood_oak_sm)
                    SceneryGuideRow("Pine trees", "Tall, pointed evergreen trees.", R.drawable.gh_wood_pine_sm)
                    SceneryGuideRow("Mossy rocks", "Low clusters of rounded stones.", R.drawable.gh_env_rocks)
                    SceneryGuideRow("Ferns", "Low green fronds.", R.drawable.gh_env_ferns)
                    SceneryGuideRow("Fallen logs", "Weathered wood resting on the ground.", R.drawable.gh_env_log)
                    SceneryGuideRow("Ponds", "Small pools of water edged with reeds.", R.drawable.gh_env_pond)
                    Text("Pale open patches represent clearings.", color = Muted, fontSize = 11.sp)
                }
                Text("READING A FOUR-DIGIT SQUARE", color = Forest,
                    fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(
                    "Read the first two numbers along the bottom (eastings), " +
                        "then the last two along the side (northings). " +
                        "Choose the square above and right of those grid lines. " +
                        "The code names a whole square, not an exact dot.",
                    fontSize = 12.sp, color = Ink
                )
                Text(
                    "A golden check means you already found something. " +
                        "This guide explains symbols, not the answers.",
                    fontSize = 11.sp, color = Muted
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismissWoodlandMapGuide")
            ) { Text("Back to the hunt", color = Forest, fontWeight = FontWeight.Bold) }
        },
        containerColor = Color(0xFFFFF8E9)
    )
}

@Composable
private fun MapFeatureGuideRow(
    name: String, description: String, mapLook: MapLook, kind: String
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Canvas(Modifier.size(62.dp)) {
            val survey = mapLook == MapLook.SURVEY_TABLE
            when (kind) {
                "creek" -> {
                    val stream = Path().apply {
                        moveTo(size.width * 0.2f, 0f)
                        cubicTo(size.width * 0.75f, size.height * 0.3f,
                            size.width * 0.1f, size.height * 0.5f,
                            size.width * 0.83f, size.height)
                    }
                    drawPath(stream, if (survey) Color(0xFFA1CCD2) else Color(0xFFB5D7D8),
                        style = Stroke(width = 12f))
                    drawPath(stream, Color(0xFF719DA2), style = Stroke(width = 3f))
                }
                "trail" -> {
                    drawLine(if (survey) Color(0xFF5F7D7C) else Color(0xFF856E50),
                        Offset(4f, size.height * 0.68f),
                        Offset(size.width - 4f, size.height * 0.32f), 3.4f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f)))
                }
                "grove" -> {
                    drawCircle(if (survey) Color(0xFF7DABA1) else Color(0xFF87A77A),
                        size.minDimension * 0.27f,
                        Offset(size.width * 0.38f, size.height * 0.36f))
                    drawCircle(if (survey) Color(0xFF527F79) else Color(0xFF547F5C),
                        size.minDimension * 0.22f,
                        Offset(size.width * 0.62f, size.height * 0.58f))
                    drawOval(Color(0x44B1CAA0),
                        topLeft = Offset(size.width * 0.11f, size.height * 0.77f),
                        size = Size(size.width * 0.76f, size.height * 0.15f))
                }
            }
        }
        Column(Modifier.weight(1f)) {
            Text(name, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(description, color = Muted, fontSize = 11.sp)
        }
    }
}

/** Use the same original drawable as the warm map, not a substitute drawing. */
@Composable
private fun SceneryGuideRow(name: String, description: String, resource: Int) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Image(painterResource(resource), contentDescription = null, modifier = Modifier.size(62.dp))
        Column(Modifier.weight(1f)) {
            Text(name, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(description, color = Muted, fontSize = 11.sp)
        }
    }
}
