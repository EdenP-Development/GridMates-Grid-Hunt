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
            .padding(horizontal = 17.dp, vertical = 11.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("GRIDMATES  /  PUZZLE ADVENTURES", color = Forest,
            fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text("Grid Hunt", color = Ink, fontWeight = FontWeight.ExtraBold,
            fontSize = 34.sp)
        Text("Little maps. Big discoveries.", color = Muted, fontSize = 13.sp)

        // One primary illustrated journey feature; don't repeat a big hero
        // postcard above a second oversized Adventure information card.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE0EBD4),
            shape = RoundedCornerShape(22.dp),
            shadowElevation = 3.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(11.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WoodlandPostcard(Modifier.fillMaxWidth().height(142.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("ADVENTURE  ·  WORLD 1", color = Forest,
                            fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        Text("Little Woodland", color = Ink,
                            fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(
                            when (expeditionProgress) {
                                0 -> "The Missing Field Notes awaits"
                                WoodlandExpedition.size -> "Chapter 1 complete"
                                else -> "$expeditionProgress of 4 pages recovered"
                            },
                            color = Muted, fontSize = 12.sp,
                            modifier = Modifier.testTag("homeAdventureProgress")
                        )
                    }
                    Text("✦", color = Gold, fontSize = 27.sp)
                }
                Button(
                    onClick = onAdventure,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp)
                        .testTag("openAdventure"),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest),
                    shape = RoundedCornerShape(15.dp)
                ) {
                    Text(if (expeditionProgress > 0 &&
                            expeditionProgress < WoodlandExpedition.size)
                            "Continue your journey" else "Explore Adventure",
                        fontWeight = FontWeight.Bold)
                }
            }
        }
        Text("Or choose your own path", color = Ink, fontSize = 17.sp,
            fontWeight = FontWeight.Bold)
        HomeDestinationStrip(
            icon = "◇",
            title = "Quick Play",
            description = "Four puzzle styles. Jump straight in.",
            actionLabel = "Choose",
            color = Color(0xFFF5E8C9),
            tag = "openQuickPlay",
            onClick = onQuickPlay
        )
        HomeDestinationStrip(
            icon = "✎",
            title = "Custom Play",
            description = "Build your own hunt  ·  Coming later",
            actionLabel = "Preview",
            color = Color(0xFFE5E9E3),
            tag = "openCustomPlay",
            onClick = onCustom
        )
        Text("Offline adventures. Discoveries stay on your device.",
            color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun HomeDestinationStrip(
    icon: String,
    title: String,
    description: String,
    actionLabel: String,
    color: Color,
    tag: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            Modifier.fillMaxWidth().padding(11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Surface(
                color = Color(0xFFFFFAED),
                shape = RoundedCornerShape(13.dp)
            ) {
                Text(icon, modifier = Modifier.padding(10.dp),
                    color = Forest, fontWeight = FontWeight.Bold,
                    fontSize = 23.sp)
            }
            Column(Modifier.weight(1f)) {
                Text(title, color = Ink, fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp)
                Text(description, color = Muted, fontSize = 11.sp)
            }
            Button(
                onClick = onClick,
                modifier = Modifier.heightIn(min = 48.dp)
                    .testTag(tag),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 11.dp, vertical = 4.dp
                ),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(actionLabel, fontSize = 12.sp,
                    fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * First map-route chapter selector. One real story destination, no fake unlocks.
 * The future journey can grow with a versioned mission registry.
 */
/**
 * GH-V14-B: the playable chapter is a destination ON the route. Decorative
 * scenery is drawn with the existing palette; the destination itself remains
 * an accessible, real Compose button, not an unlabelled Canvas tap target.
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
            .padding(horizontal = 17.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Adventure", color = Ink, fontWeight = FontWeight.ExtraBold,
                    fontSize = 27.sp)
                Text("Your journey through Little Woodland", color = Muted,
                    fontSize = 12.sp)
            }
            TextButton(onClick = onBack, modifier = Modifier.testTag("adventureBack")) {
                Text("Home", color = Forest)
            }
        }
        Surface(shape = RoundedCornerShape(21.dp),
            color = Color(0xFFE4EDDA), shadowElevation = 2.dp) {
            Column(Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("WORLD 1  ·  LITTLE WOODLAND", color = Forest,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f))
                    Text("1 chapter", color = Muted, fontSize = 11.sp)
                }
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier.fillMaxWidth().height(246.dp)
                        .testTag("adventureRouteMap")
                ) {
                    Canvas(Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        // The map path is world-themed illustration, not puzzle
                        // coordinate geometry, and never changes a correct answer.
                        drawRoundRect(
                            color = Color(0xFFD4E4C9),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(25f)
                        )
                        drawOval(
                            color = Color(0xFFA5C99F),
                            topLeft = Offset(-w * 0.10f, h * 0.38f),
                            size = androidx.compose.ui.geometry.Size(w * 0.87f, h * 0.56f)
                        )
                        drawOval(
                            color = Color(0xFF87B193),
                            topLeft = Offset(w * 0.42f, h * 0.62f),
                            size = androidx.compose.ui.geometry.Size(w * 0.7f, h * 0.43f)
                        )
                        val route = Path().apply {
                            moveTo(w * 0.12f, h * 0.86f)
                            cubicTo(w * 0.36f, h * 0.74f,
                                w * 0.13f, h * 0.50f,
                                w * 0.47f, h * 0.49f)
                            cubicTo(w * 0.81f, h * 0.47f,
                                w * 0.62f, h * 0.16f,
                                w * 0.85f, h * 0.13f)
                        }
                        drawPath(route, Color(0xFFB99762), style = Stroke(width = 15f))
                        drawPath(route, Color(0xFFF8E5B6), style = Stroke(width = 9f))
                        // Gentle layered woodland crown silhouettes.
                        listOf(
                            Triple(0.13f,0.26f,0.12f),
                            Triple(0.35f,0.16f,0.09f),
                            Triple(0.89f,0.49f,0.13f),
                            Triple(0.19f,0.61f,0.075f),
                            Triple(0.67f,0.87f,0.12f)
                        ).forEach { (x,y,r) ->
                            val point = Offset(w*x,h*y)
                            val radius = w*r
                            drawCircle(Color(0xFF567F60),radius,point)
                            drawCircle(Color(0xFF86AD83),radius*0.75f,
                                point+Offset(-radius*0.17f,-radius*0.18f))
                            drawCircle(Color(0xFFAAD19C),radius*0.27f,
                                point+Offset(-radius*0.38f,-radius*0.4f))
                        }
                        // Trail starts at a compass-style dot and leads to one
                        // *actual* playable destination; no fake chapter nodes.
                        drawCircle(Forest, 13f, Offset(w*0.12f,h*0.86f))
                        drawCircle(Color(0xFFFFF6E2), 7f, Offset(w*0.12f,h*0.86f))
                        drawCircle(Gold, 20f, Offset(w*0.85f,h*0.13f))
                        drawCircle(Color(0xFFFFF6E2), 11f, Offset(w*0.85f,h*0.13f))
                    }
                    Text(
                        "START",
                        modifier = Modifier.align(Alignment.BottomStart)
                            .padding(start = 11.dp, bottom = 9.dp),
                        color = Forest, fontWeight = FontWeight.Bold, fontSize = 10.sp
                    )
                    // The only actual chapter is prominently reachable in the
                    // route itself. A 50dp-tall action gives a proper target.
                    Button(
                        onClick = onChapter,
                        modifier = Modifier.align(Alignment.Center)
                            .padding(top = 18.dp)
                            .heightIn(min = 54.dp)
                            .testTag("openAdventureChapter"),
                        colors = ButtonDefaults.buttonColors(containerColor = Forest),
                        shape = RoundedCornerShape(17.dp)
                    ) {
                        Text(
                            if (progress == WoodlandExpedition.size)
                                "✓  Revisit Chapter 1"
                            else if (progress > 0)
                                "▶  Continue Chapter 1"
                            else "★  Begin Chapter 1",
                            fontSize = 13.sp, fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text("CHAPTER 1", color = Forest,
                    fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("The Missing Field Notes",
                    color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text("Four kinds of clues. Recover every missing page.",
                    color = Muted, fontSize = 12.sp)
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("$progress of 4 pages recovered",
                        color = Forest, fontSize = 12.sp,
                        modifier = Modifier.testTag("adventureProgress"))
                    if (progress == WoodlandExpedition.size) {
                        Text("✓ Complete", color = Forest,
                            fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Text("A larger journey is planned. For now, this woodland has one " +
            "real chapter you can play and revisit.",
            color = Muted, fontSize = 12.sp)
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
