package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.WoodlandExpedition

/** GH-V14 Explorer Desk navigation shell. No new modes, art pack or saved-game keys. */
@Composable
internal fun ExplorerDeskHome(
    expeditionProgress: Int,
    onAdventure: () -> Unit,
    onQuickPlay: () -> Unit,
    onCustom: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("GRIDMATES  /  PUZZLE ADVENTURES", color = Forest,
            fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text("Grid Hunt", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 34.sp)
        Text("Little maps. Big discoveries.", color = Muted, fontSize = 13.sp)
        WoodlandPostcard(modifier = Modifier.fillMaxWidth().height(165.dp))
        Text("Where shall we explore?", color = Ink, fontSize = 17.sp,
            fontWeight = FontWeight.Bold)

        ExplorerMenuCard(
            title = "Adventure",
            subtitle = when (expeditionProgress) {
                0 -> "Follow the woodland trail and uncover the first story."
                WoodlandExpedition.size -> "Your first chapter is complete. Revisit its discoveries."
                else -> "The Missing Field Notes  ·  $expeditionProgress of 4 pages found"
            },
            accent = Color(0xFFDCE8CD),
            primary = true,
            tag = "openAdventure",
            onClick = onAdventure
        )
        ExplorerMenuCard(
            title = "Quick Play",
            subtitle = "Choose a favorite puzzle and jump straight in.",
            accent = Color(0xFFF6E7C5),
            primary = false,
            tag = "openQuickPlay",
            onClick = onQuickPlay
        )
        ExplorerMenuCard(
            title = "Custom Play",
            subtitle = "Your own expedition settings  ·  In development",
            accent = Color(0xFFE5E9E3),
            primary = false,
            tag = "openCustomPlay",
            onClick = onCustom
        )
        Text("Your discoveries stay on this device. No account needed.",
            color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun ExplorerMenuCard(
    title: String, subtitle: String, accent: Color,
    primary: Boolean, tag: String, onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(19.dp),
        color = accent,
        shadowElevation = if (primary) 3.dp else 1.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            Text(subtitle, color = Muted, fontSize = 12.sp)
            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(tag),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (primary) Forest else Pine
                )
            ) {
                Text(when (title) {
                    "Adventure" -> "Open your journey"
                    "Quick Play" -> "Choose a puzzle"
                    else -> "See what is planned"
                }, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * First map-route chapter selector. One real story destination, no fake unlocks.
 * The future journey can grow with a versioned mission registry.
 */
@Composable
internal fun WoodlandAdventureJourney(
    progress: Int,
    onChapter: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 17.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Adventure", color = Ink, fontWeight = FontWeight.ExtraBold,
                fontSize = 26.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack, modifier = Modifier.testTag("adventureBack")) {
                Text("Home", color = Forest)
            }
        }
        Text("Your journey begins in Little Woodland.", color = Muted, fontSize = 13.sp)
        Surface(shape = RoundedCornerShape(21.dp), color = Color(0xFFE5EDDC)) {
            Column(Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text("WORLD 1  ·  LITTLE WOODLAND", color = Forest,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold)
                WoodlandPostcard(Modifier.fillMaxWidth().height(154.dp))
                Text("A little trail through clues and discoveries.",
                    color = Muted, fontSize = 12.sp)
                JourneyPathDecoration()
                Text("CHAPTER 1", color = Forest, fontSize = 10.sp,
                    fontWeight = FontWeight.Bold)
                Text("The Missing Field Notes", color = Ink, fontSize = 19.sp,
                    fontWeight = FontWeight.Bold)
                Text("$progress of 4 pages recovered", color = Muted,
                    fontSize = 12.sp, modifier = Modifier.testTag("adventureProgress"))
                Button(
                    onClick = onChapter,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
                        .testTag("openAdventureChapter"),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) {
                    Text(when (progress) {
                        0 -> "Begin Chapter 1"
                        WoodlandExpedition.size -> "Revisit completed chapter"
                        else -> "Continue Chapter 1"
                    }, fontWeight = FontWeight.Bold)
                }
            }
        }
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF1EAD9)) {
            Column(Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("More destinations ahead", color = Ink,
                    fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("New maps and chapters will join the trail as they are built. " +
                    "Little Woodland is the only playable world right now.",
                    color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun JourneyPathDecoration() {
    Canvas(Modifier.fillMaxWidth().height(36.dp).testTag("journeyTrail")) {
        val start = Offset(size.width * 0.08f, size.height * 0.60f)
        val finish = Offset(size.width * 0.87f, size.height * 0.37f)
        val path = Path().apply {
            moveTo(start.x, start.y)
            cubicTo(size.width * 0.36f, -size.height * 0.10f,
                size.width * 0.60f, size.height * 1.0f, finish.x, finish.y)
        }
        drawPath(path, Gold, style = Stroke(
            width = 5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 8f))
        ))
        drawCircle(Forest, radius = 9f, center = start)
        drawCircle(Gold, radius = 12f, center = finish)
        drawCircle(Paper, radius = 5f, center = finish)
    }
}

/** Deliberately honest pre-feature state, never an inert pretend generator. */
@Composable
internal fun WoodlandCustomPlayPreview(
    onQuickPlay: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(19.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Custom Play", color = Ink,
                fontSize = 25.sp, fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f))
            TextButton(onClick = onBack, modifier = Modifier.testTag("customBack")) {
                Text("Home", color = Forest)
            }
        }
        WoodlandPostcard(Modifier.fillMaxWidth().height(170.dp))
        Text("Build your own little expedition", color = Ink,
            fontWeight = FontWeight.Bold, fontSize = 19.sp)
        Text("Custom puzzle sessions are coming later. The plan is to choose " +
            "a map, puzzle style, difficulty, number of discoveries and hints. " +
            "Those settings are not available yet.",
            modifier = Modifier.testTag("customNotReady"),
            color = Muted, fontSize = 13.sp)
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFF6E8C8)) {
            Column(Modifier.fillMaxWidth().padding(13.dp)) {
                Text("Want to play now?", color = Ink,
                    fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Quick Play has all four real puzzle types.",
                    color = Muted, fontSize = 12.sp)
            }
        }
        Button(
            onClick = onQuickPlay,
            modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
                .testTag("customOpenQuick"),
            colors = ButtonDefaults.buttonColors(containerColor = Forest)
        ) {
            Text("Go to Quick Play", fontWeight = FontWeight.Bold)
        }
    }
}
