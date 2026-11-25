package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.WoodlandExpedition
import com.playereden.gridhunt.core.CampaignChapterStatus
import com.playereden.gridhunt.core.WoodlandSupplyExpedition

/** GH-V14 Explorer Desk navigation shell. No new modes, art pack or saved-game keys. */
@Composable
internal fun ExplorerDeskHome(
    expeditionProgress: Int,
    onAdventure: () -> Unit,
    onQuickPlay: () -> Unit,
    onCustom: () -> Unit,
    onAcademy: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().journalPaper()
            .statusBarsPadding().navigationBarsPadding()
            .journalScroll()
            .padding(horizontal = 17.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("GRIDMATES  /  PUZZLE ADVENTURES", color = Forest,
            fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text("Grid Hunt", color = Ink, fontWeight = FontWeight.ExtraBold,
            fontSize = 30.sp)
        Text("Little maps. Big discoveries.", color = Muted, fontSize = 13.sp)

        // One primary illustrated journey feature; don't repeat a big hero
        // postcard above a second oversized Adventure information card.
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE0EBD4),
            border = JournalPanelBorder,
            shape = RoundedCornerShape(22.dp),
            shadowElevation = 3.dp
        ) {
            Column(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                WoodlandPostcard(Modifier.fillMaxWidth().height(116.dp), showLabel = false)
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
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
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
        Text("Or choose your own path", color = Ink, fontSize = 16.sp,
            fontWeight = FontWeight.Bold)
        HomeDestinationStrip(
            icon = "✦",
            title = "Explorer’s Academy",
            description = "Learn to read maps.",
            actionLabel = "Learn",
            color = Color(0xFFE4EDDA),
            tag = "openAcademy",
            onClick = onAcademy
        )
        HomeDestinationStrip(
            icon = "◇",
            title = "Quick Play",
            description = "Choose a puzzle style.",
            actionLabel = "Choose",
            color = Color(0xFFF5E8C9),
            tag = "openQuickPlay",
            onClick = onQuickPlay
        )
        HomeDestinationStrip(
            icon = "✎",
            title = "Custom Play",
            description = "Build a fresh hunt",
            actionLabel = "Play",
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
        border = JournalPanelBorder,
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
                val artwork = when (tag) {
                    "openAcademy" -> R.drawable.gh_tool_compass
                    "openQuickPlay" -> R.drawable.gh_tool_map
                    "openCustomPlay" -> R.drawable.gh_tool_notebook
                    else -> null
                }
                if (artwork != null) {
                    Image(painterResource(artwork), contentDescription = null,
                        modifier = Modifier.size(38.dp).padding(2.dp))
                } else {
                    Text(icon, modifier = Modifier.padding(7.dp),
                        color = Forest, fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                }
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
    onBack: () -> Unit,
    secondStatus: CampaignChapterStatus = CampaignChapterStatus.LOCKED,
    secondProgress: Int = 0,
    onSecondChapter: () -> Unit = {}
) {
    val oakArt = ImageBitmap.imageResource(R.drawable.gh_wood_oak_sm)
    val pineArt = ImageBitmap.imageResource(R.drawable.gh_wood_pine_sm)
    val rocksArt = ImageBitmap.imageResource(R.drawable.gh_env_rocks)
    val fernsArt = ImageBitmap.imageResource(R.drawable.gh_env_ferns)
    val logArt = ImageBitmap.imageResource(R.drawable.gh_env_log)
    val pondArt = ImageBitmap.imageResource(R.drawable.gh_env_pond)
    Column(
        Modifier.fillMaxSize().journalPaper()
            .statusBarsPadding().navigationBarsPadding()
            .journalScroll()
            .padding(horizontal = 17.dp, vertical = 8.dp),
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
            color = Color(0xFFE4EDDA), border = JournalPanelBorder, shadowElevation = 2.dp) {
            Column(Modifier.fillMaxWidth().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("WORLD 1  ·  LITTLE WOODLAND", color = Forest,
                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f))
                    Text("2 chapters", color = Muted, fontSize = 11.sp)
                }
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth().height(210.dp)
                        .testTag("adventureRouteMap")
                        .clip(RoundedCornerShape(14.dp))
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
                        // Shared postcard-inspired palette and layered tree forms.
                        // Decorative journey only: never infer puzzle geometry here.
                        fun tree(x: Float, y: Float, t: Float, evergreen: Boolean) {
                            val s = h * t
                            drawWoodlandSprite(
                                if (evergreen) pineArt else oakArt,
                                Offset(w * x, h * y - s * .40f),
                                s * 1.18f
                            )
                        }
                        tree(.10f,.41f,.29f,true)
                        tree(.22f,.40f,.24f,false)
                        tree(.35f,.34f,.26f,true)
                        tree(.10f,.65f,.21f,false)
                        tree(.89f,.67f,.34f,true)
                        tree(.77f,.76f,.27f,false)
                        tree(.66f,.98f,.33f,true)
                        tree(.96f,.97f,.30f,false)
                        // Scenery stays outside chapter controls and route nodes.
                        drawWoodlandSprite(rocksArt, Offset(w*.42f,h*.26f), h*.18f)
                        drawWoodlandSprite(fernsArt, Offset(w*.48f,h*.83f), h*.19f)
                        drawWoodlandSprite(logArt, Offset(w*.80f,h*.91f), h*.24f)
                        drawWoodlandSprite(pondArt, Offset(w*.65f,h*.57f), h*.27f)
                        repeat(72) { i ->
                            drawCircle(Color(0x12695335),.55.dp.toPx(),
                                Offset(w*((i*43+7)%101)/101f,h*((i*67+13)%103)/103f))
                        }
                        // The two real chapter buttons sit on the painted route.
                        drawCircle(Forest, 13f, Offset(w*0.12f,h*0.86f))
                        drawCircle(Color(0xFFFFF6E2), 7f, Offset(w*0.12f,h*0.86f))

                    }
                    Text(
                        "START",
                        modifier = Modifier.align(Alignment.BottomStart)
                            .padding(start = 11.dp, bottom = 9.dp),
                        color = Forest, fontWeight = FontWeight.Bold, fontSize = 10.sp
                    )
                    // Align compact real buttons with the actual painted route nodes.
                    // Chapter information lives below the scene, not on top of it.
                    val nodeSize = 48.dp
                    val firstLabel = when {
                        progress == WoodlandExpedition.size -> "Chapter 1, The Missing Field Notes, complete"
                        progress > 0 -> "Chapter 1, The Missing Field Notes, in progress"
                        else -> "Chapter 1, The Missing Field Notes, ready"
                    }
                    val secondLabel = "Chapter 2, The Lost Supplies, " + when (secondStatus) {
                        CampaignChapterStatus.LOCKED -> "locked"
                        CampaignChapterStatus.COMPLETE -> "complete"
                        CampaignChapterStatus.IN_PROGRESS -> "in progress"
                        CampaignChapterStatus.READY -> "ready"
                    }
                    AdventureRouteNode(
                        number = 1, label = firstLabel,
                        complete = progress == WoodlandExpedition.size,
                        enabled = true, tag = "openAdventureChapter", onClick = onChapter,
                        modifier = Modifier.offset(x = (maxWidth * .47f - nodeSize / 2).coerceAtLeast(0.dp),
                            y = maxHeight * .49f - nodeSize / 2).size(nodeSize)
                    )
                    AdventureRouteNode(
                        number = 2, label = secondLabel,
                        complete = secondStatus == CampaignChapterStatus.COMPLETE,
                        enabled = secondStatus != CampaignChapterStatus.LOCKED,
                        tag = "openSecondChapter", onClick = onSecondChapter,
                        modifier = Modifier.offset(x = (maxWidth * .85f - nodeSize / 2)
                            .coerceIn(0.dp, (maxWidth - nodeSize).coerceAtLeast(0.dp)),
                            y = (maxHeight * .13f - nodeSize / 2).coerceAtLeast(0.dp)).size(nodeSize)
                    )
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
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF3E9D2)) {
            Column(Modifier.fillMaxWidth().padding(13.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("CHAPTER 2  ·  THE LOST SUPPLIES", color = Forest,
                    fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Text("Four new clues across Little Woodland.", color = Ink, fontSize = 13.sp)
                Text(when (secondStatus) {
                    CampaignChapterStatus.LOCKED ->
                        "Locked — finish Chapter 1 to follow the supply trail"
                    CampaignChapterStatus.READY -> "Ready for a new adventure"
                    CampaignChapterStatus.IN_PROGRESS ->
                        "$secondProgress of 4 clues solved"
                    CampaignChapterStatus.COMPLETE -> "4 of 4 clues solved · Complete"
                }, color = Muted, fontSize = 12.sp,
                    modifier = Modifier.testTag("secondChapterStatus"))

            }
        }
        Text("Choose a numbered destination to explore.",
            color = Muted, fontSize = 12.sp)
    }
}

/** Compact accessible route marker with a full 48dp target. */
@Composable
private fun AdventureRouteNode(
    number: Int, label: String, complete: Boolean, enabled: Boolean,
    tag: String, onClick: () -> Unit, modifier: Modifier
) {
    Button(
        onClick = onClick, enabled = enabled,
        modifier = modifier.testTag(tag).semantics { contentDescription = label },
        shape = RoundedCornerShape(24.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Forest,
            disabledContainerColor = Color(0xFFEFE5D2),
            disabledContentColor = Color(0xFF53685E)),
        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD5B575))
    ) {
        Text(if (complete) "✓" else number.toString(),
            fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** Deliberately honest pre-feature state, never an inert pretend generator. */
@Composable
internal fun WoodlandCustomPlayPreview(
    onQuickPlay: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().journalPaper()
            .statusBarsPadding().navigationBarsPadding()
            .journalScroll()
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
