package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.*

/** Independent custom session: never writes authored campaign or Quick Play saves. */
@Composable
internal fun CustomHuntScreen(mapLook: MapLook, onLook: (MapLook) -> Unit, onHome: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) { context.getSharedPreferences("gridhunt_custom_v1", Context.MODE_PRIVATE) }
    var count by rememberSaveable { mutableIntStateOf(prefs.getInt("count", 3).let { if (prefs.getString("mode", "grid") == "mixed" && it !in CustomHuntGenerator.mixedLengths) 6 else if (it in CustomHuntGenerator.lengths) it else 3 }) }
    var mode by rememberSaveable { mutableStateOf(prefs.getString("mode", "grid")?.takeIf { it in listOf("grid", "gold", "identify", "checklist", "mixed") } ?: "grid") }
    var nextMode by rememberSaveable { mutableStateOf(mode) }
    var answer by rememberSaveable { mutableStateOf("") }
    var codes by rememberSaveable { mutableStateOf(prefs.getString("codes", "") ?: "") }
    var kinds by rememberSaveable { mutableStateOf(prefs.getString("kinds", "") ?: "") }
    val mixed = remember(codes, kinds, mode) { if (mode == "mixed") CustomHuntGenerator.restoreMixed(codes, kinds) else emptyList() }
    val targets = remember(codes, mode, mixed) {
        if (mode == "mixed") mixed.map { it.cell }
        else if (mode in listOf("identify", "checklist")) CustomHuntGenerator.restoreLandmarks(codes) else CustomHuntGenerator.restore(codes)
    }
    var step by rememberSaveable { mutableIntStateOf(prefs.getInt("step", 0).coerceIn(0, targets.size)) }
    var playing by rememberSaveable { mutableStateOf(false) }
    var replaceHunt by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = playing) { playing = false }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    fun instruction(type: String) = when(type) { "gold" -> "Read the gold dot’s square."; "identify" -> "Which landmark is there?"; "checklist" -> "Find the landmark and read its square."; else -> "Tap the matching square." }
    var feedback by rememberSaveable { mutableStateOf(instruction(if (mode == "mixed") mixed.getOrNull(step)?.type ?: "grid" else mode)) }
    var showGuide by rememberSaveable { mutableStateOf(false) }
    var hint by rememberSaveable { mutableIntStateOf(0) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    val bounds = WoodlandTrainingMap.bounds
    fun fit() {
        if (viewport.width > 80 && viewport.height > 80)
            camera = MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble())
    }
    fun zoom(scale: Double) {
        if (viewport.width > 80 && viewport.height > 80)
            camera = camera?.transform(bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                PixelPoint(viewport.width/2.0,viewport.height/2.0),PixelPoint(0.0,0.0),scale)
    }
    fun start() {
        val generated = if (nextMode == "mixed") CustomHuntGenerator.generateMixed(count) else emptyList()
        kinds = generated.joinToString(",") { it.type }
        codes = CustomHuntGenerator.encode(if (generated.isNotEmpty()) generated.map { it.cell } else if (nextMode in listOf("identify", "checklist")) CustomHuntGenerator.generateLandmarks() else CustomHuntGenerator.generate(count))
        mode = nextMode
        step = 0; selected = null; answer = ""; hint = 0; feedback = instruction(generated.firstOrNull()?.type ?: mode)
        prefs.edit().putInt("count", count).putString("codes", codes).putString("mode", mode).putString("kinds", kinds).putInt("step", 0).apply()
        playing = true
        fit()
    }
    if (showGuide) WoodlandMapGuideDialog(mapLook = mapLook, onDismiss = { showGuide = false })
    if (replaceHunt) AlertDialog(
        onDismissRequest = { replaceHunt = false },
        title = { Text("Start a new hunt?") },
        text = { Text("Your unfinished custom hunt will be replaced.") },
        confirmButton = { TextButton(onClick = { replaceHunt = false; start() },
            modifier = Modifier.testTag("customConfirmReplace")) { Text("New hunt") } },
        dismissButton = { TextButton(onClick = { replaceHunt = false },
            modifier = Modifier.testTag("customCancelReplace")) { Text("Keep hunt") } }
    )
    Column(Modifier.fillMaxSize().background(Paper).statusBarsPadding().navigationBarsPadding().imePadding()
        .padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Custom Play", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink,
                modifier = Modifier.weight(1f))
            if (playing) HuntToolButton(ExplorerTool.GUIDE, Modifier.size(48.dp).testTag("customGuide"), { showGuide = true })
            TextButton(onClick = { if (playing) playing = false else onHome() },
                modifier = Modifier.testTag("customBack")) { Text(if (playing) "Setup" else "Home") }
        }
        if (!playing) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                WoodlandPostcard(Modifier.fillMaxWidth().height(96.dp))
                Text("Choose your puzzle", fontWeight = FontWeight.Bold, color = Ink, fontSize = 16.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("grid" to "Trail Treasures", "gold" to "Golden Spots").forEach { (id,label) ->
                        FilterChip(selected = nextMode == id, onClick = { nextMode = id },
                            label = { CustomModeLabel(id, label) }, colors = customChipColors(), modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("customMode_$id"))
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("identify" to "What's at This Grid?", "checklist" to "Treasure Checklist").forEach { (id,label) ->
                        FilterChip(selected = nextMode == id, onClick = { nextMode = id; count = 3 },
                            label = { CustomModeLabel(id, label) }, colors = customChipColors(), modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("customMode_$id"))
                    }
                }
                FilterChip(selected = nextMode == "mixed", onClick = { nextMode = "mixed"; if (count !in CustomHuntGenerator.mixedLengths) count = 6 },
                    label = { CustomModeLabel("mixed", "Mixed Expedition") }, colors = customChipColors(), modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("customMode_mixed"))
                if (nextMode !in listOf("identify", "checklist")) Text("Discoveries", color = Ink, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    (when(nextMode) { "mixed" -> CustomHuntGenerator.mixedLengths; "identify", "checklist" -> CustomHuntGenerator.landmarkLengths; else -> CustomHuntGenerator.lengths }).forEach { n ->
                        if (nextMode in listOf("identify", "checklist")) Text("3 discoveries", color = Forest, modifier = Modifier.testTag("customLength_$n"))
                        else FilterChip(selected = count == n, onClick = { count = n },
                            label = { Text(n.toString(), modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) }, colors = customChipColors(), modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("customLength_$n"))
                    }
                }
                if (targets.isNotEmpty() && step < targets.size) {
                    OutlinedButton(onClick = { playing = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("customResume")) {
                        Text("Resume · " + (when(mode) { "gold" -> "Golden Spots"; "identify" -> "Landmark Identification"; "checklist" -> "Treasure Checklist"; "mixed" -> "Mixed Expedition"; else -> "Trail Treasures" }) + " · $step/${targets.size}")
                    }
                }
                Button(onClick = { if (targets.isNotEmpty() && step < targets.size) replaceHunt = true else start() }, modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp).testTag("customStart")) {
                    Text("New hunt")
                }
            }
        } else if (targets.isEmpty()) {
            Text("This hunt could not be restored.", color = Forest)
            Button(onClick = { playing = false }) { Text("Return to setup") }
        } else {
            val active = targets.getOrNull(step)
            val objectiveMode = if (mode == "mixed") mixed.getOrNull(step)?.type ?: "grid" else mode
            val landmark = if (objectiveMode == "checklist") WoodlandLandmarks.all.firstOrNull { it.cell == active } else null
            val prompt: @Composable () -> Unit = {
            if (active != null) {
                if (objectiveMode == "gold") Text("Golden spot ${step+1} of ${targets.size}", color = Forest, fontWeight = FontWeight.Bold)
                else if (objectiveMode == "identify") Text("Which landmark is at ${active.reference}? · ${step+1}/${targets.size}", fontWeight = FontWeight.Bold, color = Forest)
                else if (objectiveMode == "checklist") {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        val checklistTargets = if (mode == "mixed") listOf(active) else targets
                        checklistTargets.forEachIndexed { index, cell ->
                            val item = WoodlandLandmarks.all.first { it.cell == cell }
                            Surface(modifier = Modifier.weight(1f), color = if (mode != "mixed" && index < step)
                                androidx.compose.ui.graphics.Color(0xFFDDE9D4) else if (mode == "mixed" || index == step)
                                androidx.compose.ui.graphics.Color(0xFFF8DDA0) else androidx.compose.ui.graphics.Color(0xFFFFFAEC)) {
                                Column(Modifier.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    WoodlandLandmarkSwatch(item.id, mapLook, symbolSize = 32.dp)
                                    Text((if (mode != "mixed" && index < step) "✓ " else "") + item.name, fontSize = 10.sp,
                                        modifier = Modifier.testTag("customChecklistItem_${item.id}"))
                                }
                            }
                        }
                    }
                    Text("Find: " + (landmark?.name ?: ""), fontWeight = FontWeight.Bold, color = Forest)
                } else ObjectiveRibbon(step, targets.size, active, "Little Woodland", false)
            }
            }
            val mapPanel: @Composable (Modifier) -> Unit = { mapModifier ->
            HuntMapPanel(modifier = mapModifier.testTag("customMap"),
                bounds = bounds, camera = camera,
                selectedCell = selected?.let { runCatching { GridReference.parse(it) }.getOrNull() },
                completed = targets.take(step), viewport = viewport,
                onViewport = { if (it != viewport || camera == null) { viewport = it; fit() } },
                onCamera = { camera = it }, onSelect = { selected = it },
                markerPoint = if (objectiveMode == "gold") active?.let { WorldPoint(it.eastIndex * 1000.0 + 500.0, it.northIndex * 1000.0 + 500.0) } else null,
                enableSelect = active != null && objectiveMode == "grid", mapLook = mapLook, onLook = onLook)
            }
            val controls: @Composable () -> Unit = {
            if (active == null) PuzzleFinishCard("Your hunt is complete!", "A little woodland adventure, all yours.",
                "${targets.size} OF ${targets.size} FOUND", "New hunt", { nextMode = mode; count = targets.size; start() }, onHome)
            else if (objectiveMode in listOf("gold", "checklist")) {
                OutlinedTextField(value = answer, onValueChange = { answer = it.filter { c -> c in '0'..'9' }.take(4) },
                    label = { Text("Four-digit map code") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag(if (objectiveMode == "gold") "customGoldInput" else "customChecklistInput"))
                Text(if (objectiveMode == "checklist" && hint > 0) landmark?.clue ?: feedback else when(hint) {
                    1 -> "Read the grid line left of the dot."
                    2 -> "Then read the line below the dot."
                    else -> feedback
                }, color = Forest, fontSize = 12.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    HuntToolButton(ExplorerTool.HINT, Modifier.weight(1f), { hint = (hint+1).coerceAtMost(2) })
                    HuntToolButton(ExplorerTool.ZOOM_IN, Modifier.weight(1f), { zoom(1.6) })
                    HuntToolButton(ExplorerTool.ZOOM_OUT, Modifier.weight(1f), { zoom(1.0/1.6) })
                    HuntToolButton(ExplorerTool.FIT, Modifier.weight(1f), ::fit)
                }
                Button(onClick = {
                    if (answer == active.reference) {
                        step += 1; answer = ""; selected = null; hint = 0; feedback = "Found it!"
                        prefs.edit().putInt("step",step).apply()
                    } else { hint = 0; feedback = "Not quite. Try another code." }
                }, enabled = answer.length == 4,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag(if (objectiveMode == "gold") "customGoldCheck" else "customChecklistCheck")) {
                    Text(if (objectiveMode == "gold") "Try this code" else "Mark found")
                }
            } else if (objectiveMode == "identify") {
                WoodlandLandmarks.all.chunked(2).forEach { pair ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        pair.forEach { landmark ->
                            OutlinedButton(onClick = { answer = landmark.id }, modifier = Modifier.weight(1f).heightIn(min = 60.dp).testTag("customIdentify_${landmark.id}"),
                                contentPadding = PaddingValues(4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(containerColor = if (answer == landmark.id) androidx.compose.ui.graphics.Color(0xFFF7DDA2) else androidx.compose.ui.graphics.Color(0xFFFFFBF0))) {
                                WoodlandLandmarkSwatch(landmark.id, mapLook, symbolSize = 32.dp)
                                Text(landmark.name, fontSize = 11.sp, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                Text(feedback, color = Forest, fontSize = 12.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    HuntToolButton(ExplorerTool.ZOOM_IN, Modifier.weight(1f), { zoom(1.6) })
                    HuntToolButton(ExplorerTool.ZOOM_OUT, Modifier.weight(1f), { zoom(1.0/1.6) })
                    HuntToolButton(ExplorerTool.FIT, Modifier.weight(1f), ::fit)
                }
                Button(onClick = {
                    if (WoodlandLandmarks.byId(answer)?.cell == active) {
                        step += 1; answer = ""; selected = null; hint = 0; feedback = "Found it!"
                        prefs.edit().putInt("step", step).apply()
                    } else feedback = "Not quite. Look inside the square."
                }, enabled = answer.isNotEmpty(), modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("customIdentifyCheck")) { Text("Check find") }
            } else HuntControlPanel(step, targets.size, selected, feedback, hint, false,
                onMove = { dx,dy ->
                    val cell = GridReference.parse(selected ?: "2640")
                    selected = GridCell((cell.eastIndex+dx).coerceIn(24,29),(cell.northIndex+dy).coerceIn(37,42)).reference
                },
                onZoom = { scale ->
                    camera = camera?.transform(bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                        PixelPoint(viewport.width/2.0,viewport.height/2.0),PixelPoint(0.0,0.0),scale)
                },
                onUndo = { selected = null }, onHint = { hint = (hint+1).coerceAtMost(2) }, onFit = ::fit,
                onCheck = {
                    if (selected == active.reference) {
                        step += 1; selected = null; hint = 0; feedback = "Found it! On to the next square."
                        prefs.edit().putInt("step",step).apply()
                    } else { hint = 0; feedback = "Not quite. Try another square." }
                }, actionLabel = "Try this square")
            }
            BoxWithConstraints(Modifier.weight(1f).fillMaxWidth()) {
                val wide = maxWidth >= 600.dp
                if (wide) {
                    Row(Modifier.fillMaxSize().testTag("customWideLayout"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        mapPanel(Modifier.weight(1.25f).fillMaxHeight())
                        Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            prompt()
                            controls()
                        }
                    }
                } else {
                    // On short viewports (including the open IME), keep a usable map
                    // and allow the control area to scroll rather than collapsing it.
                    val short = maxHeight < 420.dp
                    if (short) {
                        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).testTag("customShortLayout"),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            prompt()
                            mapPanel(Modifier.fillMaxWidth().height(240.dp))
                            controls()
                        }
                    } else {
                        Column(Modifier.fillMaxSize().testTag("customPortraitLayout"),
                            verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            prompt()
                            mapPanel(Modifier.fillMaxWidth().weight(1f))
                            controls()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun customChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = androidx.compose.ui.graphics.Color(0xFFFFFAEC), labelColor = Ink,
    selectedContainerColor = Forest, selectedLabelColor = androidx.compose.ui.graphics.Color.White
)

/** Same icon artwork as Quick Play; replacing a shared illustration updates both menus. */
@Composable
private fun CustomModeLabel(id: String, label: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        val tag = when(id) {
            "grid" -> "openChapter"
            "gold" -> "openReverse"
            "identify" -> "openIdentify"
            "checklist" -> "openTreasure"
            else -> "mixed"
        }
        if (id == "mixed") ExplorerInsignia(Modifier.size(32.dp))
        else PuzzleModeIllustration(tag, "", Modifier.size(32.dp))
        Text(label, modifier = Modifier.weight(1f), fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold)
    }
}
