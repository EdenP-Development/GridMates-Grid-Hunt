package com.playereden.gridmates.gridhunt

import android.content.Context
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
    var count by rememberSaveable { mutableIntStateOf(prefs.getInt("count", 3).let { if (it in CustomHuntGenerator.lengths) it else 3 }) }
    var codes by rememberSaveable { mutableStateOf(prefs.getString("codes", "") ?: "") }
    val targets = remember(codes) {
        CustomHuntGenerator.restore(codes)
    }
    var step by rememberSaveable { mutableIntStateOf(prefs.getInt("step", 0).coerceIn(0, targets.size)) }
    var playing by rememberSaveable { mutableStateOf(false) }
    var replaceHunt by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = playing) { playing = false }
    var selected by rememberSaveable { mutableStateOf<String?>(null) }
    var feedback by rememberSaveable { mutableStateOf("Tap the matching square.") }
    var hint by rememberSaveable { mutableIntStateOf(0) }
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    val bounds = WoodlandTrainingMap.bounds
    fun fit() {
        if (viewport.width > 80 && viewport.height > 80)
            camera = MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble())
    }
    fun start() {
        codes = CustomHuntGenerator.encode(CustomHuntGenerator.generate(count))
        step = 0; selected = null; hint = 0; feedback = "Tap the matching square."
        prefs.edit().putInt("count", count).putString("codes", codes).putInt("step", 0).apply()
        playing = true
        fit()
    }
    if (replaceHunt) AlertDialog(
        onDismissRequest = { replaceHunt = false },
        title = { Text("Start a new hunt?") },
        text = { Text("Your unfinished custom hunt will be replaced.") },
        confirmButton = { TextButton(onClick = { replaceHunt = false; start() },
            modifier = Modifier.testTag("customConfirmReplace")) { Text("New hunt") } },
        dismissButton = { TextButton(onClick = { replaceHunt = false },
            modifier = Modifier.testTag("customCancelReplace")) { Text("Keep hunt") } }
    )
    Column(Modifier.fillMaxSize().background(Paper).statusBarsPadding().navigationBarsPadding()
        .padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Custom Play", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Ink,
                modifier = Modifier.weight(1f))
            TextButton(onClick = { if (playing) playing = false else onHome() },
                modifier = Modifier.testTag("customBack")) { Text(if (playing) "Setup" else "Home") }
        }
        if (!playing) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WoodlandPostcard(Modifier.fillMaxWidth().height(130.dp))
                Text("Little Woodland", fontWeight = FontWeight.Bold, color = Ink, fontSize = 18.sp)
                Text("Find the grid square", color = Forest)
                Text("Discoveries", color = Ink, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CustomHuntGenerator.lengths.forEach { n ->
                        FilterChip(selected = count == n, onClick = { count = n },
                            label = { Text(n.toString()) }, modifier = Modifier.weight(1f).testTag("customLength_$n"))
                    }
                }
                if (targets.isNotEmpty() && step < targets.size) {
                    OutlinedButton(onClick = { playing = true }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("customResume")) {
                        Text("Resume · $step/${targets.size}")
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
            if (active != null) ObjectiveRibbon(step, targets.size, active, "Little Woodland", false)
            HuntMapPanel(modifier = Modifier.fillMaxWidth().weight(1f).testTag("customMap"),
                bounds = bounds, camera = camera,
                selectedCell = selected?.let { runCatching { GridReference.parse(it) }.getOrNull() },
                completed = targets.take(step), viewport = viewport,
                onViewport = { if (it != viewport || camera == null) { viewport = it; fit() } },
                onCamera = { camera = it }, onSelect = { selected = it }, markerPoint = null,
                enableSelect = active != null, mapLook = mapLook, onLook = onLook)
            if (active == null) PuzzleFinishCard("Your hunt is complete!", "A little woodland adventure, all yours.",
                "${targets.size} OF ${targets.size} FOUND", "New hunt", ::start, { playing = false })
            else HuntControlPanel(step, targets.size, selected, feedback, hint, false,
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
                    } else feedback = "Not quite. Try another square."
                }, actionLabel = "Try this square")
        }
    }
}
