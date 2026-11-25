package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.ExpeditionObjective
import com.playereden.gridhunt.core.ExpeditionVerb
import com.playereden.gridhunt.core.GridReference
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.ReverseTraining
import com.playereden.gridhunt.core.WoodlandExpedition
import com.playereden.gridhunt.core.WoodlandSupplyExpedition
import com.playereden.gridhunt.core.ExpeditionPlaythrough
import com.playereden.gridhunt.core.WoodlandIdentifyMission
import com.playereden.gridhunt.core.WoodlandLandmarks
import com.playereden.gridhunt.core.WoodlandTrainingMap
import com.playereden.gridhunt.core.WorldPoint

/**
 * GH-V13: first mixed-verb story chapter, one native Android screen, one map.
 * The stage's authored ID/answer is validated by pure Kotlin core, never
 * derived from where the icon happens to be painted.
 *
 * Dedicated save space: replaying the expedition NEVER resets existing
 * Trail Treasures, Golden Spots, Treasure Checklist or Identify progress.
 */
@Composable
internal fun WoodlandMixedExpeditionScreen(
    onHome: () -> Unit,
    mapLook: MapLook,
    onLook: (MapLook) -> Unit,
    hapticsEnabled: Boolean,
    chapterTwo: Boolean = false,
    onNextChapter: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val mission = remember(chapterTwo) {
        if (chapterTwo) WoodlandSupplyExpedition.run
        else ExpeditionPlaythrough(WoodlandExpedition.chapter, WoodlandExpedition.saveName)
    }
    val prefs = remember(context, mission.saveName) {
        context.getSharedPreferences(mission.saveName, Context.MODE_PRIVATE)
    }
    var step by androidx.compose.runtime.saveable.rememberSaveable(mission.chapter.id) {
        mutableStateOf(mission.restoreStep(prefs.getInt("step", 0)))
    }
    var selectedCell by rememberSaveable(mission.chapter.id) {
        mutableStateOf(prefs.getString("selected_cell", null)?.takeIf {
            runCatching { GridReference.parse(it).digits == 4 }.getOrDefault(false)
        })
    }
    var enteredCode by rememberSaveable(mission.chapter.id) {
        mutableStateOf((prefs.getString("entered_code", "") ?: "")
            .filter { it in '0'..'9' }.take(4))
    }
    var chosenLandmark by rememberSaveable(mission.chapter.id) {
        mutableStateOf(prefs.getString("chosen_landmark", null)?.takeIf { id ->
            WoodlandLandmarks.byId(id) != null
        })
    }
    var showGuide by rememberSaveable(mission.chapter.id) { mutableStateOf(false) }
    var showJournal by rememberSaveable(mission.chapter.id) { mutableStateOf(false) }
    var discoveryEarned by rememberSaveable(mission.chapter.id) { mutableStateOf(false) }
    var feedback by rememberSaveable(mission.chapter.id) {
        mutableStateOf(mission.chapter.objectives.getOrNull(step - 1)?.discovery
            ?.takeIf { it.isNotBlank() } ?: if (chapterTwo)
            "Follow the journal notes to trace the missing supplies."
            else "Follow the next clue to recover the missing field notes.")
    }
    val active = mission.objectiveAt(step)
    val finished = mission.finished(step)
    LaunchedEffect(mission.chapter.id, step, selectedCell, enteredCode, chosenLandmark) {
        prefs.edit()
            .putInt("step", step)
            .putString("selected_cell", selectedCell)
            .putString("entered_code", enteredCode)
            .putString("chosen_landmark", chosenLandmark)
            .apply()
    }

    val bounds = WoodlandTrainingMap.bounds
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    var centerE by rememberSaveable { mutableStateOf(bounds.center.easting) }
    var centerN by rememberSaveable { mutableStateOf(bounds.center.northing) }
    var zoom by rememberSaveable { mutableStateOf(1.0) }

    fun setCamera(next: MapCamera) {
        camera = next
        centerE = next.center.easting
        centerN = next.center.northing
        if (viewport.width > 80 && viewport.height > 80) {
            zoom = next.pixelsPerMeter /
                MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble())
                    .pixelsPerMeter
        }
    }
    fun fit() {
        if (viewport.width > 80 && viewport.height > 80) {
            setCamera(MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble()))
        }
    }
    fun zoomBy(factor: Double) {
        if (viewport.width > 80 && viewport.height > 80) {
            camera?.transform(
                bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
                PixelPoint(0.0, 0.0), factor
            )?.let(::setCamera)
        }
    }
    val onViewport: (IntSize) -> Unit = { size ->
        if (size != viewport) {
            viewport = size
            if (size.width > 80 && size.height > 80) {
                val fitted = MapCamera.fit(bounds, size.width.toDouble(), size.height.toDouble())
                camera = MapCamera(WorldPoint(centerE, centerN), fitted.pixelsPerMeter * zoom)
                    .transform(
                        bounds, size.width.toDouble(), size.height.toDouble(),
                        PixelPoint(size.width / 2.0, size.height / 2.0),
                        PixelPoint(0.0, 0.0), 1.0
                    )
            }
        }
    }
    fun moveSelection(dx: Int, dy: Int) {
        val base = selectedCell?.let {
            runCatching { GridReference.parse(it) }.getOrNull()
        } ?: GridReference.parse("2640")
        val east = (base.eastIndex + dx).coerceIn(24, 29)
        val north = (base.northIndex + dy).coerceIn(37, 42)
        selectedCell = com.playereden.gridhunt.core.GridCell(east, north).reference
    }
    val check: () -> Unit = {
        val current = active
        if (current != null) {
            val next = when (current.verb) {
                ExpeditionVerb.FIND_SQUARE -> mission.acceptGrid(step, selectedCell)
                ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK ->
                    mission.acceptGrid(step, enteredCode)
                ExpeditionVerb.IDENTIFY_LANDMARK ->
                    mission.acceptLandmark(step, chosenLandmark)
            }
            if (next > step) {
                if (hapticsEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                // Persist the earned objective before navigating away or
                // recomposing. A sudden Home tap cannot lose the unlock.
                prefs.edit().putInt("step", next)
                    .remove("selected_cell").remove("entered_code")
                    .remove("chosen_landmark").apply()
                step = next
                selectedCell = null
                enteredCode = ""
                chosenLandmark = null
                discoveryEarned = true
                feedback = current.discovery.takeIf { it.isNotBlank() } ?: if (mission.finished(step)) {
                    if (chapterTwo) "The last stop has been found! The supplies trail is complete."
                    else "Every missing page recovered. Your woodland chapter is complete!"
                } else if (chapterTwo) "A new trail note found! Follow the next clue."
                else "A page found! The next clue asks for something different."
            } else {
                discoveryEarned = false
                feedback = when (current.verb) {
                    ExpeditionVerb.FIND_SQUARE ->
                        "Not quite. Use the eastings along the bottom, then northings up the side."
                    ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK ->
                        "Not quite. Check the four-digit grid square; hints are in the Map Guide."
                    ExpeditionVerb.IDENTIFY_LANDMARK ->
                        "That picture isn't in the requested square. Look again!"
                }
            }
        }
    }
    val replay: () -> Unit = {
        prefs.edit().putInt("step", 0)
            .remove("selected_cell").remove("entered_code")
            .remove("chosen_landmark").apply()
        step = 0
        discoveryEarned = false
        selectedCell = null
        enteredCode = ""
        chosenLandmark = null
        feedback = if (chapterTwo) "The supply clues are ready to trace again."
            else "All four notes have been hidden again. Enjoy the hunt!"
        fit()
    }
    val onMapSelect: (String) -> Unit = { reference ->
        if (active?.verb == ExpeditionVerb.FIND_SQUARE) {
            selectedCell = reference
        }
    }
    val marker = if (active?.verb == ExpeditionVerb.READ_MARKER) {
        ReverseTraining.markers.singleOrNull { it.id == active.markerId }?.point
    } else null

    BoxWithConstraints(
        Modifier.fillMaxSize().background(Paper)
            .statusBarsPadding().navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        val wide = maxWidth > maxHeight
        val sideWidth = (maxWidth * 0.45f).coerceAtMost(365.dp)
        val selected = if (active?.verb == ExpeditionVerb.FIND_SQUARE) {
            selectedCell?.let { runCatching { GridReference.parse(it) }.getOrNull() }
        } else null
        if (wide) {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HuntMapPanel(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    bounds = bounds, camera = camera,
                    selectedCell = selected, completed = mission.solvedCells(step),
                    viewport = viewport, onViewport = onViewport, onCamera = ::setCamera,
                    onSelect = onMapSelect, markerPoint = marker,
                    enableSelect = active?.verb == ExpeditionVerb.FIND_SQUARE,
                    mapLook = mapLook, onLook = onLook
                )
                Column(
                    Modifier.width(sideWidth).fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    MixedExpeditionHeader(mission.chapter.name, chapterTwo, step, mission.size,
                        onHome, onGuide = { showGuide = true }, onJournal = { showJournal = true })
                    if (finished) MixedExpeditionFinish(chapterTwo, onHome, replay,
                        onJournal = { showJournal = true }, onNextChapter = onNextChapter)
                    else if (active != null) {
                        MixedExpeditionPrompt(active, chapterTwo, step + 1, mission.size)
                        MixedExpeditionControls(
                            active, mapLook, selectedCell, enteredCode, chosenLandmark,
                            feedback, discoveryEarned, onSelected = { selectedCell = it },
                            onCode = { enteredCode = it }, onChoose = { chosenLandmark = it },
                            onMove = ::moveSelection, onZoom = ::zoomBy,
                            onFit = ::fit, onCheck = check
                        )
                    }
                }
            }
        } else {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MixedExpeditionHeader(mission.chapter.name, chapterTwo, step, mission.size, onHome,
                    onGuide = { showGuide = true }, onJournal = { showJournal = true })
                if (active != null) MixedExpeditionPrompt(active, chapterTwo, step + 1, mission.size)
                HuntMapPanel(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    bounds = bounds, camera = camera,
                    selectedCell = selected, completed = mission.solvedCells(step),
                    viewport = viewport, onViewport = onViewport, onCamera = ::setCamera,
                    onSelect = onMapSelect, markerPoint = marker,
                    enableSelect = active?.verb == ExpeditionVerb.FIND_SQUARE,
                    mapLook = mapLook, onLook = onLook
                )
                if (finished) MixedExpeditionFinish(chapterTwo, onHome, replay,
                        onJournal = { showJournal = true }, onNextChapter = onNextChapter)
                else if (active != null) {
                    MixedExpeditionControls(
                        active, mapLook, selectedCell, enteredCode, chosenLandmark,
                        feedback, discoveryEarned, onSelected = { selectedCell = it },
                        onCode = { enteredCode = it }, onChoose = { chosenLandmark = it },
                        onMove = ::moveSelection, onZoom = ::zoomBy,
                        onFit = ::fit, onCheck = check
                    )
                }
            }
        }
    }
    if (showJournal) {
        ExpeditionJournalDialog(mission, step, chapterTwo) { showJournal = false }
    }
    if (showGuide) {
        WoodlandMapGuideDialog(mapLook = mapLook, onDismiss = { showGuide = false })
    }
}

@Composable
private fun MixedExpeditionHeader(
    chapterName: String, chapterTwo: Boolean, step: Int, total: Int,
    onHome: () -> Unit, onGuide: () -> Unit, onJournal: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Text(chapterName, color = Ink,
            fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, maxLines = 1)
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(if (chapterTwo) "$step of $total clues solved"
             else "$step of $total pages found", color = Forest, fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f).testTag("mixedProgress"))
            TextButton(onClick = onJournal, modifier = Modifier.testTag("mixedJournal")) {
                Text("Journal", color = Forest, fontSize = 11.sp)
            }
            TextButton(onClick = onGuide, modifier = Modifier.testTag("mixedGuide")) {
                Text("Map Guide", color = Forest, fontSize = 11.sp)
            }
            TextButton(onClick = onHome, modifier = Modifier.testTag("mixedHome")) {
                Text("Home", color = Forest, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun MixedExpeditionPrompt(
    objective: ExpeditionObjective, chapterTwo: Boolean, stage: Int, total: Int
) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFFFF0CF)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text((if (chapterTwo) "CLUE" else "PAGE") + " $stage OF $total · " + when (objective.verb) {
                ExpeditionVerb.FIND_SQUARE -> "FIND THE SQUARE"
                ExpeditionVerb.READ_MARKER -> "READ THE GOLDEN DOT"
                ExpeditionVerb.LOCATE_LANDMARK -> "FIND THE LANDMARK"
                ExpeditionVerb.IDENTIFY_LANDMARK -> "IDENTIFY THE OBJECT"
            }, color = Forest, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(objective.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(objective.prompt, color = Ink, fontSize = 12.sp,
                modifier = Modifier.testTag("mixedPrompt"))
        }
    }
}

@Composable
private fun MixedExpeditionControls(
    active: ExpeditionObjective,
    mapLook: MapLook,
    selectedCell: String?,
    enteredCode: String,
    chosenLandmark: String?,
    feedback: String,
    discoveryEarned: Boolean,
    onSelected: (String?) -> Unit,
    onCode: (String) -> Unit,
    onChoose: (String) -> Unit,
    onMove: (Int, Int) -> Unit,
    onZoom: (Double) -> Unit,
    onFit: () -> Unit,
    onCheck: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val submitCode: () -> Unit = {
        if (enteredCode.length == 4) {
            focusManager.clearFocus()
            onCheck()
        }
    }
    val actionReady = when (active.verb) {
        ExpeditionVerb.FIND_SQUARE -> selectedCell != null
        ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK ->
            enteredCode.length == 4
        ExpeditionVerb.IDENTIFY_LANDMARK -> chosenLandmark != null
    }
    when (active.verb) {
        ExpeditionVerb.FIND_SQUARE -> {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically) {
                listOf(
                    Triple("←", -1, 0), Triple("→", 1, 0),
                    Triple("↑", 0, 1), Triple("↓", 0, -1)
                ).forEach { (symbol, dx, dy) ->
                    OutlinedButton(
                        onClick = { onMove(dx, dy) },
                        modifier = Modifier.weight(1f).heightIn(min = 46.dp)
                            .semantics { contentDescription = when (symbol) {
                                "←" -> "Expedition move west"
                                "→" -> "Expedition move east"
                                "↑" -> "Expedition move north"
                                else -> "Expedition move south"
                            } },
                        contentPadding = PaddingValues(0.dp)
                    ) { Text(symbol, fontSize = 17.sp, color = Forest) }
                }
                OutlinedButton(
                    onClick = { onSelected(null) },
                    modifier = Modifier.weight(0.8f).heightIn(min = 46.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { Text("Clear", fontSize = 10.sp, color = Forest) }
            }
            Text("YOUR PICK  " + (selectedCell ?: "Tap a square"),
                fontSize = 11.sp, color = Forest, fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("mixedSquarePick"))
        }
        ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK -> {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (active.verb == ExpeditionVerb.LOCATE_LANDMARK) {
                    WoodlandLandmarkSwatch(active.landmarkId!!, mapLook, symbolSize = 48.dp)
                }
                OutlinedTextField(
                    value = enteredCode,
                    onValueChange = { input ->
                        onCode(input.filter { it in '0'..'9' }.take(4))
                    },
                    singleLine = true,
                    placeholder = { Text("Enter 4 digits") },
                    label = { Text("Grid square") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submitCode() }),
                    modifier = Modifier.weight(1f).testTag("mixedCodeInput")
                )
            }
        }
        ExpeditionVerb.IDENTIFY_LANDMARK -> {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                WoodlandIdentifyMission.choices.chunked(2).forEach { pair ->
                    Row(Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        pair.forEach { landmark ->
                            OutlinedButton(
                                onClick = { onChoose(landmark.id) },
                                modifier = Modifier.weight(1f).heightIn(min = 65.dp)
                                    .testTag("mixedOption_${landmark.id}"),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 3.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (chosenLandmark == landmark.id)
                                        Color(0xFFF8DCA0) else Color(0xFFFFFBF2)
                                )
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    WoodlandLandmarkSwatch(
                                        landmark.id, mapLook, symbolSize = 36.dp
                                    )
                                    Text(landmark.name, fontSize = 10.sp, color = Ink,
                                        maxLines = 2,
                                        modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    Text(if (discoveryEarned) "✓ $feedback" else feedback,
        fontSize = 11.sp, color = if (discoveryEarned) Forest else Muted,
        fontWeight = if (discoveryEarned) FontWeight.SemiBold else FontWeight.Normal,
        modifier = Modifier.fillMaxWidth().testTag("mixedFeedback"))
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        HuntToolButton(ExplorerTool.ZOOM_IN, Modifier.weight(0.85f), { onZoom(1.6) })
        HuntToolButton(ExplorerTool.ZOOM_OUT, Modifier.weight(0.85f), { onZoom(1.0 / 1.6) })
        HuntToolButton(ExplorerTool.FIT, Modifier.weight(0.85f), onFit)
        Button(
            onClick = { if (active.verb == ExpeditionVerb.READ_MARKER || active.verb == ExpeditionVerb.LOCATE_LANDMARK) submitCode() else onCheck() }, enabled = actionReady,
            modifier = Modifier.weight(1.6f).heightIn(min = 50.dp)
                .testTag("mixedCheck"),
            colors = ButtonDefaults.buttonColors(containerColor = Forest),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Text("Check clue", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
private fun MixedExpeditionFinish(
    chapterTwo: Boolean, onHome: () -> Unit, onReplay: () -> Unit,
    onJournal: () -> Unit, onNextChapter: (() -> Unit)?
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        PuzzleFinishCard(
            title = if (chapterTwo) "The supply trail is complete!"
                else "The field notes are together!",
            description = if (chapterTwo)
                "You traced the supplies to their last known stop."
                else "Every lost page has been recovered. Your field journal is whole.",
            discoveries = if (chapterTwo) "4 OF 4 CLUES SOLVED" else "4 OF 4 PAGES RECOVERED",
            primaryLabel = "Explore again",
            onPrimary = onReplay,
            onHome = onHome
        )
        if (!chapterTwo && onNextChapter != null) {
            Button(onClick = onNextChapter,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("nextExpedition"),
                colors = ButtonDefaults.buttonColors(containerColor = Forest)) {
                Text("Continue · The Lost Supplies", fontWeight = FontWeight.Bold)
            }
        }
        TextButton(onClick = onJournal,
            modifier = Modifier.fillMaxWidth().testTag("finishJournal")) {
            Text("Read completed journal", color = Forest)
        }
    }
}

/** Only earned entries and the active prompt are rendered; no future answers. */
@Composable
private fun ExpeditionJournalDialog(
    mission: ExpeditionPlaythrough, step: Int, chapterTwo: Boolean,
    onDismiss: () -> Unit
) {
    val earned = mission.discoveries(step)
    val active = mission.objectiveAt(step)
    val unit = if (chapterTwo) "Clue" else "Page"
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.testTag("expeditionJournal"),
        containerColor = Color(0xFFFFF0CF),
        title = { Text("Explorer's Journal", color = Ink, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(mission.chapter.name, color = Ink, fontWeight = FontWeight.Bold)
                Text(mission.chapter.story, color = Ink, fontSize = 13.sp)
                Text(if (chapterTwo) "${earned.size} of ${mission.size} clues solved"
                    else "${earned.size} of ${mission.size} pages found",
                    color = Forest, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.testTag("journalProgress"))
                if (earned.isEmpty()) {
                    Text("Your discoveries will appear here as you explore.",
                        color = Muted, modifier = Modifier.testTag("journalEmpty"))
                }
                earned.forEachIndexed { index, objective ->
                    Surface(
                        color = Color(0xFFFFFBF2), shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("journalEntry_${objective.id}")
                    ) {
                        Column(Modifier.padding(10.dp)) {
                            Text("$unit ${index + 1} · ${objective.title}", color = Forest,
                                fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(objective.discovery.ifBlank { "Discovery recorded." },
                                color = Ink, fontSize = 13.sp)
                        }
                    }
                }
                if (active != null) {
                    Surface(color = Color(0xFFF8DCA0), shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("journalCurrent")) {
                        Column(Modifier.padding(10.dp)) {
                            Text("CURRENT OBJECTIVE", color = Forest,
                                fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text(active.title, color = Ink, fontWeight = FontWeight.Bold)
                            Text(active.prompt, color = Ink, fontSize = 13.sp)
                        }
                    }
                } else {
                    Text("Expedition complete. All discoveries recorded.", color = Forest,
                        fontWeight = FontWeight.Bold, modifier = Modifier.testTag("journalComplete"))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.testTag("closeJournal")) {
                Text("Return to exploration", color = Forest)
            }
        }
    )
}
