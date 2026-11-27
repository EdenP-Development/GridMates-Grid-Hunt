package com.playereden.gridmates.gridhunt

import android.os.Bundle
import android.content.Context
import android.graphics.Paint
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.activity.compose.setContent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.ChapterPosition
import com.playereden.gridhunt.core.FirstBearingsChapter
import com.playereden.gridhunt.core.ExplorationMaps
import com.playereden.gridhunt.core.MapTrailChapter
import com.playereden.gridhunt.core.TrailChapterCatalog
import com.playereden.gridhunt.core.TreasureChecklistMission
import com.playereden.gridhunt.core.WoodlandIdentifyMission
import com.playereden.gridhunt.core.WoodlandExpedition
import com.playereden.gridhunt.core.GridHuntCampaign
import com.playereden.gridhunt.core.WoodlandSupplyExpedition
import com.playereden.gridhunt.core.WoodlandReturnExpedition
import com.playereden.gridhunt.core.CampaignChapterStatus
import com.playereden.gridhunt.core.ReverseTraining
import com.playereden.gridhunt.core.GridCell
import com.playereden.gridhunt.core.GridReference
import com.playereden.gridhunt.core.HuntRules
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.WoodlandTrainingMap
import com.playereden.gridhunt.core.WorldBounds
import com.playereden.gridhunt.core.WorldPoint

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Parchment UI and cream startup both require dark system-bar icons.
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            MaterialTheme(
                typography = JournalTypography,
                colorScheme = lightColorScheme(
                    primary = Forest,
                    onPrimary = Color.White,
                    background = Paper,
                    onBackground = Ink,
                    surface = Color(0xFFFFFBF1),
                    onSurface = Ink,
                    onSurfaceVariant = Muted,
                    outline = Color(0xFF8D907D),
                    outlineVariant = JournalEdge,
                    secondary = Pine,
                    onSecondary = Color.White
                )
            ) {
                StartupBranding(showInitially = savedInstanceState == null,
                    seriesDrawable = R.drawable.gridmates_v1_runtime_transparent) {
                    GridHuntScreen()
                }
            }
        }
    }
}

@Composable
private fun GridHuntScreen() {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences("gridhunt_visual_lab_v1", Context.MODE_PRIVATE)
    }
    var screen by rememberSaveable { mutableStateOf("home") }
    var hapticsEnabled by rememberSaveable {
        mutableStateOf(prefs.savedBoolean("haptics", true))
    }
    // Alpha: one illustrated map per world.
    val mapLook = MapLook.FIELD_GUIDE
    LaunchedEffect(hapticsEnabled) {
        prefs.edit().putString("map_look", mapLook.name)
            .putBoolean("haptics", hapticsEnabled).apply()
    }
    // Preserve the originating hub when returning from a puzzle.
    var returnHub by rememberSaveable { mutableStateOf("quick") }
    var settingsOrigin by rememberSaveable { mutableStateOf("home") }
    val goBack = {
        screen = when (screen) {
            "settings" -> settingsOrigin
            "quick", "adventure", "custom", "academy" -> "home"
            else -> returnHub
        }
    }
    BackHandler(enabled = screen != "home") { goBack() }
    // Read active chapter progress and permanent accomplishments separately.
    val completion = remember(context) {
        CampaignCompletionStore(context, com.playereden.gridhunt.core.PublishedWorlds.selection, false)
    }
    fun currentCampaignSnapshot() = completion.snapshot()
    val campaignSnapshot = currentCampaignSnapshot()
    val campaignState = requireNotNull(GridHuntCampaign.registry.state(
        WoodlandExpedition.chapter.id, campaignSnapshot
    ))
    val supplyState = requireNotNull(GridHuntCampaign.registry.state(
        WoodlandSupplyExpedition.chapter.id, campaignSnapshot
    ))
    val returnState = requireNotNull(GridHuntCampaign.registry.state(
        WoodlandReturnExpedition.chapter.id, campaignSnapshot
    ))
    val expeditionProgress = campaignState.completedObjectives

    when (screen) {
        "home" -> ExplorerDeskHome(
            worldProgress = GridHuntCampaign.registry.worldProgress(GridHuntCampaign.littleWoodlandId, campaignSnapshot),
            onAdventure = { screen = "adventure" },
            onQuickPlay = { screen = "quick" },
            onCustom = { screen = "custom" },
            onAcademy = { screen = "academy" },
            onSettings = { settingsOrigin = "home"; screen = "settings" }
        )
        "settings" -> GridHuntSettings(hapticsEnabled, { hapticsEnabled = it }, mapLook,
            onBack = { screen = settingsOrigin })
        "academy" -> ExplorerAcademyScreen(onHome = { screen = "home" }, mapLook = mapLook, onLook = { _ -> })
        "adventure" -> WoodlandAdventureJourney(
            progress = expeditionProgress,
            secondStatus = supplyState.status,
            secondProgress = supplyState.completedObjectives,
            thirdStatus = returnState.status,
            thirdProgress = returnState.completedObjectives,
            firstEarned = campaignState.everCompleted,
            secondEarned = supplyState.everCompleted,
            thirdEarned = returnState.everCompleted,
            onThirdChapter = {
                if (returnState.status != CampaignChapterStatus.LOCKED) {
                    returnHub = "adventure"
                    screen = "returnTrail"
                }
            },
            onSecondChapter = {
                if (supplyState.status != CampaignChapterStatus.LOCKED) {
                    returnHub = "adventure"
                    screen = "supplies"
                }
            },
            onChapter = {
                returnHub = "adventure"
                screen = "expedition"
            },
            onBack = { screen = "home" }
        )
        "custom" -> CustomHuntScreen(mapLook = mapLook, onLook = { _ -> }, onHome = { screen = "home" })
        "chapter" -> FirstBearingsScreen(
            onHome = { screen = returnHub },
            mapLook = mapLook,
            onLook = { _ -> },
            hapticsEnabled = hapticsEnabled
        )
        "reverse" -> ReversePracticeScreen(
            onBack = { screen = returnHub },
            mapLook = mapLook,
            onLook = { _ -> },
            hapticsEnabled = hapticsEnabled
        )
        "treasure" -> TreasureChecklistScreen(
            onHome = { screen = returnHub },
            mapLook = mapLook,
            onLook = { _ -> },
            hapticsEnabled = hapticsEnabled
        )
        "identify" -> WoodlandIdentifyScreen(
            onHome = { screen = returnHub },
            mapLook = mapLook,
            onLook = { _ -> },
            hapticsEnabled = hapticsEnabled
        )
        "expedition" -> WoodlandMixedExpeditionScreen(
            onHome = { screen = returnHub },
            mapLook = mapLook,
            onLook = { _ -> },
            hapticsEnabled = hapticsEnabled,
            onNextChapter = if (returnHub == "adventure") ({
                // Read current persisted progress, not the snapshot from before the final clue.
                val snapshot = currentCampaignSnapshot()
                val next = GridHuntCampaign.registry.state(
                    WoodlandSupplyExpedition.chapter.id, snapshot
                )
                if (next != null && next.status != CampaignChapterStatus.LOCKED) {
                    returnHub = "adventure"
                    screen = "supplies"
                }
            }) else null
        )
        "supplies", "returnTrail" -> {
            val isReturn = screen == "returnTrail"
            val status = if (isReturn) returnState.status else supplyState.status
            if (status == CampaignChapterStatus.LOCKED) {
                LaunchedEffect(screen) { screen = "adventure" }
            } else WoodlandMixedExpeditionScreen(
                onHome = { screen = "adventure" },
                mapLook = mapLook,
                onLook = { _ -> },
                hapticsEnabled = hapticsEnabled,
                chapterTwo = !isReturn,
                chapterThree = isReturn,
                onNextChapter = if (!isReturn) ({
                    val next = GridHuntCampaign.registry.state(
                        WoodlandReturnExpedition.chapter.id, currentCampaignSnapshot()
                    )
                    if (next?.status != CampaignChapterStatus.LOCKED && next != null) {
                        returnHub = "adventure"
                        screen = "returnTrail"
                    }
                }) else null
            )
        }
        "quick" -> GridHuntHome(
            mapLook = mapLook,
            onLook = { _ -> },
            onSettings = { settingsOrigin = "quick"; screen = "settings" },
            onChapter = {
                returnHub = "quick"
                screen = "chapter"
            },
            onReverse = {
                returnHub = "quick"
                screen = "reverse"
            },
            onTreasure = {
                returnHub = "quick"
                screen = "treasure"
            },
            onIdentify = {
                returnHub = "quick"
                screen = "identify"
            },
            onExpedition = {
                returnHub = "quick"
                screen = "expedition"
            },
            onReturnHome = { screen = "home" }
        )
    }
}

/** GH-V14 Quick Play: existing modes and settings moved out of the main Home hub. */
@Composable
private fun GridHuntHome(
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    onSettings: () -> Unit,
    onChapter: () -> Unit,
    onReverse: () -> Unit,
    onTreasure: () -> Unit,
    onIdentify: () -> Unit,
    onExpedition: () -> Unit,
    onReturnHome: () -> Unit
) {
    val context = LocalContext.current
    val chapterPrefs = remember(context) {
        context.getSharedPreferences("gridhunt_first_bearings_v1", Context.MODE_PRIVATE)
    }
    val reversePrefs = remember(context) {
        context.getSharedPreferences("gridhunt_reverse_v1", Context.MODE_PRIVATE)
    }
    val treasurePrefs = remember(context) {
        context.getSharedPreferences("gridhunt_treasure_checklist_v1", Context.MODE_PRIVATE)
    }
    val identifyPrefs = remember(context) {
        context.getSharedPreferences(WoodlandIdentifyMission.saveName, Context.MODE_PRIVATE)
    }
    val expeditionPrefs = remember(context) {
        context.getSharedPreferences(WoodlandExpedition.saveName, Context.MODE_PRIVATE)
    }
    val expeditionCount = WoodlandExpedition.restoreStep(expeditionPrefs.savedInt("step", 0))
    val identifyCount = WoodlandIdentifyMission.restoreStep(
        identifyPrefs.savedInt("step", 0)
    )
    val treasureCount = TreasureChecklistMission.restoreCompleted(
        (treasurePrefs.savedString("completed_ids", "") ?: "")
            .split(',').filter { it.isNotBlank() }.toSet()
    ).size
    val chapterState = FirstBearingsChapter.restore(
        chapterPrefs.savedInt("hunt_index", 0), chapterPrefs.savedInt("objective_index", 0)
    )
    val chapterCompleted = FirstBearingsChapter.hunts.take(chapterState.huntIndex)
        .sumOf { it.objectives.size } + chapterState.objectiveIndex
    val reverseCompleted = reversePrefs.savedInt("step", 0).coerceIn(0, ReverseTraining.markers.size)
    val allTrailsFinished = chapterState.huntIndex == FirstBearingsChapter.hunts.lastIndex &&
        chapterState.objectiveIndex == FirstBearingsChapter.hunts.last().objectives.size
    val allGoldenSpotsFinished = reverseCompleted == ReverseTraining.markers.size

    Column(
        modifier = Modifier.fillMaxSize().journalPaper()
            .statusBarsPadding().navigationBarsPadding()
            .journalScroll()
            .padding(horizontal = 18.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Quick Play", fontWeight = FontWeight.ExtraBold,
                    color = Ink, fontSize = 26.sp)
                Text("Pick a favorite puzzle.", color = Muted, fontSize = 12.sp)
            }
            TextButton(onClick = onReturnHome, modifier = Modifier.testTag("quickBack")) {
                Text("Home", color = Forest)
            }
        }
        Surface(shape = RoundedCornerShape(17.dp), color = Color(0xFFDFEAD4), border = JournalPanelBorder) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)) {
                Text("✦  YOUR LITTLE DISCOVERIES", color = Forest,
                    fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Spacer(Modifier.height(4.dp))
                Text("$chapterCompleted of 9 squares found", color = Ink, fontSize = 12.sp)
                Text("$reverseCompleted of 5 golden spots", color = Ink, fontSize = 12.sp)
            }
        }
        Text("Choose your puzzle", color = Ink, fontSize = 17.sp,
            fontWeight = FontWeight.Bold)
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            QuickPlayModeTile(
                label = "Trail Treasures", glyph = "◇", instruction = "Find the square",
                progress = "$chapterCompleted of 9 found",
                tag = "openChapter", color = Color(0xFFE2EBD8),
                onClick = onChapter, modifier = Modifier.weight(1f).fillMaxHeight()
            )
            QuickPlayModeTile(
                label = "Golden Spots", glyph = "✦", instruction = "Read the marker",
                progress = "$reverseCompleted of 5 found",
                tag = "openReverse", color = Color(0xFFF5E7C7),
                onClick = onReverse, modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            QuickPlayModeTile(
                label = "Treasure Checklist", glyph = "☑",
                instruction = "Locate the landmarks",
                progress = "$treasureCount of 4 found",
                tag = "openTreasure", color = Color(0xFFFFF4D8),
                onClick = onTreasure, modifier = Modifier.weight(1f).fillMaxHeight()
            )
            QuickPlayModeTile(
                label = "What's at This Grid?", glyph = "⌖",
                instruction = "Identify the object",
                progress = "$identifyCount of 4 identified",
                tag = "openIdentify", color = Color(0xFFE1ECE7),
                onClick = onIdentify, modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
        Surface(shape = RoundedCornerShape(16.dp), color = Color(0xFFFFE8CA), border = JournalPanelBorder) {
            Row(
                Modifier.fillMaxWidth().padding(11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                PuzzleModeIllustration("mixed", "", Modifier.size(38.dp))
                Column(Modifier.weight(1f)) {
                    Text("The Missing Field Notes", color = Ink,
                        fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Mixed expedition  ·  $expeditionCount of 4 pages",
                        color = Muted, fontSize = 11.sp)
                }
                Button(
                    onClick = onExpedition,
                    modifier = Modifier.heightIn(min = 48.dp).testTag("openExpedition"),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest)
                ) {
                    Text("Play", fontWeight = FontWeight.Bold)
                }
            }
        }
        OutlinedButton(onClick = onSettings,
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("quickSettings")) {
            Text("Settings")
        }
        Text("Play offline. Your puzzle progress stays on this device.",
            fontSize = 11.sp, color = Muted)
    }
}

@Composable
internal fun FirstBearingsScreen(
    onHome: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean,
    chapter: MapTrailChapter = TrailChapterCatalog.woodland
) {
    require(TrailChapterCatalog.forAuthoredMap(chapter.map) === chapter)
    androidx.compose.runtime.key(chapter.map.id, chapter.map.revision) {
        MapTrailContent(onHome, mapLook, onLook, hapticsEnabled, chapter)
    }
}

@Composable
private fun MapTrailContent(onHome: () -> Unit, mapLook: MapLook, onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean, chapter: MapTrailChapter) {
    val map = chapter.map
    val woodland = map == ExplorationMaps.woodland
    val scene = if (woodland) publishedMapScene(map, mapLook) else coastalTerrainDraftScene(map, mapLook)
    val region = if (woodland) "woodland" else "coastal"
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val prefs = remember(context, map) {
        context.getSharedPreferences(chapter.saveName, Context.MODE_PRIVATE)
    }
    val initial = remember(prefs) {
        chapter.restore(
            prefs.savedInt("hunt_index", 0),
            prefs.savedInt("objective_index", 0)
        )
    }
    var huntIndex by rememberSaveable { mutableIntStateOf(initial.huntIndex) }
    var step by rememberSaveable { mutableIntStateOf(initial.objectiveIndex) }
    var selected by rememberSaveable {
        mutableStateOf(prefs.savedString("selected", null)?.takeIf { ref ->
            runCatching {
                val cell = GridReference.parse(ref)
                cell.digits == 4 && cell in map.cells
            }.getOrDefault(false)
        })
    }
    var hintStage by rememberSaveable { mutableIntStateOf(prefs.savedInt("hint_stage", 0).coerceIn(0, 2)) }
    var feedback by rememberSaveable {
        mutableStateOf("Ready for your first clue? Pick a square, then check your guess.")
    }
    var showGuide by rememberSaveable {
        mutableStateOf(!prefs.savedBoolean("guide_seen", false))
    }
    // A discovery is a brief celebration, not persisted reward currency.
    var discoveryMessage by remember { mutableStateOf<String?>(null) }
    var discoverySequence by remember { mutableIntStateOf(0) }
    LaunchedEffect(discoverySequence) {
        if (discoverySequence > 0) {
            kotlinx.coroutines.delay(1700L)
            discoveryMessage = null
        }
    }

    val hunt = chapter.hunts[huntIndex]
    val targets = hunt.objectives
    val finished = step >= targets.size
    val chapterFinished = finished && huntIndex == chapter.hunts.lastIndex
    val active = targets.getOrNull(step)
    val selectedCell = selected?.let(GridReference::parse)
    val bounds = map.bounds

    // Save objective/hunt progress promptly, including hints and pending selection,
    // so returning after Android closes the activity resumes the same authored task.
    LaunchedEffect(huntIndex, step, selected, hintStage) {
        prefs.edit()
            .putInt("hunt_index", huntIndex)
            .putInt("objective_index", step)
            .putString("selected", selected)
            .putInt("hint_stage", hintStage)
            .apply()
    }

    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    var savedCenterE by rememberSaveable { mutableStateOf(bounds.center.easting) }
    var savedCenterN by rememberSaveable { mutableStateOf(bounds.center.northing) }
    var savedZoom by rememberSaveable { mutableStateOf(1.0) }

    fun updateCamera(next: MapCamera) {
        camera = next
        savedCenterE = next.center.easting
        savedCenterN = next.center.northing
        if (viewport.width > 80 && viewport.height > 80) {
            savedZoom = next.pixelsPerMeter /
                MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()).pixelsPerMeter
        }
    }

    fun resetView() {
        if (viewport.width > 80 && viewport.height > 80) {
            updateCamera(MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()))
        }
    }

    fun zoomBy(factor: Double) {
        if (viewport.width <= 80 || viewport.height <= 80) return
        camera?.transform(
            bounds, viewport.width.toDouble(), viewport.height.toDouble(),
            PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
            PixelPoint(0.0, 0.0), factor
        )?.let(::updateCamera)
    }

    fun moveSelected(eastDelta: Int, northDelta: Int) {
        if (finished) return
        selected = chapter.moveSelection(selectedCell, eastDelta, northDelta).reference
        feedback = "Square " + selected + " is your pick. Check it or choose another!"
    }

    val mapSizeChanged: (IntSize) -> Unit = { size ->
        if (size != viewport) {
            viewport = size
            if (size.width > 80 && size.height > 80) {
                val fit = MapCamera.fit(bounds, size.width.toDouble(), size.height.toDouble())
                val desired = MapCamera(
                    WorldPoint(savedCenterE, savedCenterN), fit.pixelsPerMeter * savedZoom
                )
                camera = desired.transform(
                    bounds, size.width.toDouble(), size.height.toDouble(),
                    PixelPoint(size.width / 2.0, size.height / 2.0),
                    PixelPoint(0.0, 0.0), 1.0
                )
            }
        }
    }
    val onMapSelect: (String) -> Unit = { ref ->
        if (!finished) {
            selected = ref
            feedback = "Square " + ref + " is your pick. Ready to try it?"
        }
    }

    val onCheck: () -> Unit = {
        if (finished) {
            val next = chapter.nextHuntOrReplay(ChapterPosition(huntIndex, step))
            val replayed = next.huntIndex == 0
            huntIndex = next.huntIndex
            step = next.objectiveIndex
            selected = null
            hintStage = 0
            feedback = if (replayed) "A fresh walk! Your first clue is waiting."
            else "A new stretch of trail! Find the next hiding spot."
            resetView()
            discoveryMessage = null
        } else {
            val expected = active!!
            val attempt = selectedCell
            if (attempt != null) {
                if (HuntRules.isCorrect(expected, attempt)) {
                    if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    step = chapter.acceptCorrect(
                        ChapterPosition(huntIndex, step)
                    ).objectiveIndex
                    selected = null
                    hintStage = 0
                    discoveryMessage = if (step >= targets.size) "Trail complete!"
                        else "A new discovery!"
                    discoverySequence += 1
                    feedback = when {
                        step >= targets.size && huntIndex == chapter.hunts.lastIndex ->
                            "What a trail! You found all nine $region squares."
                        step >= targets.size ->
                            "Beautifully done! Your next trail is ready."
                        else -> "Nice find! ✦ " + expected.reference + " was the spot. Another clue awaits."
                    }
                } else {
                    discoveryMessage = null
                    feedback = "Not quite! " + HuntRules.errorText(expected, attempt)
                }
            }
        }
    }

    val completed = targets.take(step.coerceIn(0, targets.size))
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
            .background(Paper)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val wide = maxWidth > maxHeight
        val sidePanelWidth = (maxWidth * 0.43f).coerceAtMost(340.dp)
        if (wide) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HuntMapPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    bounds = bounds,
                    camera = camera,
                    selectedCell = selectedCell,
                    completed = completed,
                    viewport = viewport,
                    onViewport = mapSizeChanged,
                    onCamera = ::updateCamera,
                    onSelect = onMapSelect, mapLook = mapLook,
                    discoveryMessage = discoveryMessage,
                    onLook = onLook, scene = scene, map = map
                )
                Column(
                    modifier = Modifier
                        .width(sidePanelWidth)
                        .fillMaxHeight()
                        .journalScroll(),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    HuntHeader(
                        compact = true, huntName = hunt.name,
                        huntNumber = huntIndex + 1, huntTotal = chapter.hunts.size,
                        onGuide = { showGuide = !showGuide },
                        onHome = onHome
                    )
                    if (!finished) ObjectiveRibbon(
                        step = step, total = targets.size, active = active,
                        huntName = hunt.name, chapterFinished = chapterFinished
                    )
                    if (showGuide) GridReadingGuide {
                        showGuide = false
                        prefs.edit().putBoolean("guide_seen", true).apply()
                    }
                    if (finished) {
                        PuzzleFinishCard(
                            title = if (chapterFinished) (if (woodland) "Woodland adventure complete!" else "Coastal adventure complete!") else "Trail complete!",
                            description = if (chapterFinished)
                                if (woodland) "Nine clues found across three trails. The whole little woodland is yours to revisit." else "Nine clues found across three trails. The coast is yours to revisit."
                            else "You found every square on this trail. There's another path to explore!",
                            discoveries = if (chapterFinished) "9 OF 9 LITTLE DISCOVERIES"
                                else "${targets.size} OF ${targets.size} SQUARES FOUND",
                            primaryLabel = if (chapterFinished) "Explore again" else "Next trail",
                            onPrimary = onCheck,
                            onHome = onHome
                        )
                    } else HuntControlPanel(
                        step = step, total = targets.size, selected = selected,
                        feedback = feedback, hintStage = hintStage, finished = finished,
                        onMove = ::moveSelected, onZoom = ::zoomBy,
                        onUndo = {
                            selected = null
                            feedback = "All clear! Pick a new square whenever you are ready."
                        },
                        onHint = { hintStage = (hintStage + 1).coerceAtMost(2) },
                        onFit = ::resetView,
                        onCheck = onCheck,
                        actionLabel = when {
                            chapterFinished -> "Explore again  ↻"
                            finished -> "Next trail  →"
                            else -> "Try this square"
                        }
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                HuntHeader(
                    compact = false, huntName = hunt.name,
                    huntNumber = huntIndex + 1, huntTotal = chapter.hunts.size,
                    onGuide = { showGuide = !showGuide },
                    onHome = onHome
                )
                Spacer(Modifier.height(5.dp))
                if (!finished) ObjectiveRibbon(
                    step = step, total = targets.size, active = active,
                    huntName = hunt.name, chapterFinished = chapterFinished
                )
                if (showGuide) GridReadingGuide {
                    showGuide = false
                    prefs.edit().putBoolean("guide_seen", true).apply()
                }
                Spacer(Modifier.height(6.dp))
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    bounds = bounds,
                    camera = camera,
                    selectedCell = selectedCell,
                    completed = completed,
                    viewport = viewport,
                    onViewport = mapSizeChanged,
                    onCamera = ::updateCamera,
                    onSelect = onMapSelect, mapLook = mapLook,
                    discoveryMessage = discoveryMessage,
                    onLook = onLook, scene = scene, map = map
                )
                Spacer(Modifier.height(4.dp))
                if (finished) {
                    PuzzleFinishCard(
                        title = if (chapterFinished) (if (woodland) "Woodland adventure complete!" else "Coastal adventure complete!") else "Trail complete!",
                        description = if (chapterFinished)
                            if (woodland) "Nine clues found across three trails. The whole little woodland is yours to revisit." else "Nine clues found across three trails. The coast is yours to revisit."
                        else "You found every square on this trail. There's another path to explore!",
                        discoveries = if (chapterFinished) "9 OF 9 LITTLE DISCOVERIES"
                            else "${targets.size} OF ${targets.size} SQUARES FOUND",
                        primaryLabel = if (chapterFinished) "Explore again" else "Next trail",
                        onPrimary = onCheck,
                        onHome = onHome
                    )
                } else HuntControlPanel(
                    step = step, total = targets.size, selected = selected,
                    feedback = feedback, hintStage = hintStage, finished = finished,
                    onMove = ::moveSelected, onZoom = ::zoomBy,
                    onUndo = {
                        selected = null
                        feedback = "All clear! Pick a new square whenever you are ready."
                    },
                    onHint = { hintStage = (hintStage + 1).coerceAtMost(2) },
                    onFit = ::resetView,
                    onCheck = onCheck,
                    actionLabel = when {
                        chapterFinished -> "Explore again  ↻"
                        finished -> "Next trail  →"
                        else -> "Try this square"
                    }
                )
            }
        }
    }
}


