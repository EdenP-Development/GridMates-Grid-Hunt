package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.ExplorerAcademy
import com.playereden.gridhunt.core.AcademyTrainingGrid
import com.playereden.gridhunt.core.AcademyMapTask
import com.playereden.gridhunt.core.GridReference
import com.playereden.gridhunt.core.MapCamera
import com.playereden.gridhunt.core.PixelPoint
import com.playereden.gridhunt.core.WoodlandTrainingMap

@Composable
internal fun ExplorerAcademyScreen(onHome: () -> Unit, mapLook: MapLook, onLook: (MapLook) -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(ExplorerAcademy.saveName, Context.MODE_PRIVATE)
    }
    var earned by remember { mutableStateOf(ExplorerAcademy.restore(
        prefs.getStringSet("earned", emptySet())?.toSet() ?: emptySet()
    )) }
    var fieldEarned by remember { mutableStateOf(ExplorerAcademy.restoreGraduation(
        prefs.getStringSet("graduation", emptySet())?.toSet() ?: emptySet())) }
    var qualifications by remember { mutableStateOf(
        prefs.getStringSet("qualifications", emptySet())?.toSet() ?: emptySet()) }
    var inGraduation by rememberSaveable { mutableStateOf(fieldEarned.isNotEmpty()) }
    val practiceDone = ExplorerAcademy.practiceComplete(earned)
    val graduated = ExplorerAcademy.graduated(earned, fieldEarned)
    var showOverview by rememberSaveable { mutableStateOf(true) }
    val active = if (inGraduation && practiceDone)
        ExplorerAcademy.graduationCurrent(earned, fieldEarned) else ExplorerAcademy.current(earned)
    var feedback by rememberSaveable(active?.id) { mutableStateOf("") }
    var selected by rememberSaveable(active?.id) { mutableStateOf<String?>(active?.route?.start?.reference) }
    var success by rememberSaveable { mutableStateOf("") }
    var showLessonHelp by rememberSaveable(active?.id) { mutableStateOf(false) }
    val mapLesson = !showOverview && active?.woodlandField == true
    val scroll = rememberScrollState()
    LaunchedEffect(active?.id, showOverview) { scroll.scrollTo(0) }
    Column(
        Modifier.fillMaxSize().background(Paper).statusBarsPadding()
            .navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("Explorer's Academy", fontSize = 22.sp, color = Ink,
                    fontWeight = FontWeight.ExtraBold)
                if (!mapLesson) Text("School of Exploration", color = Forest)
            }
            TextButton(onClick = onHome, modifier = Modifier.testTag("academyHome")) {
                Text("Home")
            }
        }
        if (ExplorerAcademy.qualificationId in qualifications) {
            Text("★ Junior Explorer · School of Exploration", color = Forest,
                fontWeight = FontWeight.Bold, modifier = Modifier.testTag("academyQualification"))
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(if (inGraduation && practiceDone) "Graduation · ${fieldEarned.size}/${ExplorerAcademy.graduation.size}"
                else "Practice · ${earned.size}/${ExplorerAcademy.exercises.size}",
                color = Forest, modifier = Modifier.weight(1f).testTag("academyProgress"))
            if (mapLesson) TextButton(onClick = { showOverview = true }, modifier = Modifier.testTag("academyOverview")) { Text("Overview") }
        }
        Column(Modifier.weight(1f).verticalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (showOverview) {
            AcademySchoolOverview(earned, fieldEarned, qualifications,
                onContinue = { showOverview = false })
        } else {
        if (!mapLesson) TextButton(onClick = { showOverview = true }, modifier = Modifier.testTag("academyOverview")) {
            Text("School overview")
        }
        if (success.isNotBlank() && active?.woodlandField != true) Text(success, color = Forest,
            fontSize = 12.sp, modifier = Modifier.testTag("academySuccess"))
        if (active != null) {
            Surface(color = Color(0xFFFFF0CF), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(if (mapLesson) 8.dp else 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (mapLesson) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(active.lesson, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f))
                            TextButton(onClick = { showLessonHelp = !showLessonHelp },
                                contentPadding = PaddingValues(horizontal = 4.dp),
                                modifier = Modifier.testTag("academyLessonHelp")) {
                                Text("Help", color = Forest)
                            }
                        }
                        if (showLessonHelp) AlertDialog(onDismissRequest = { showLessonHelp = false },
                            title = { Text(active.lesson) }, text = { Text(active.teaching) },
                            confirmButton = { TextButton(onClick = { showLessonHelp = false }) { Text("Got it") } })
                    } else {
                        Text(active.lesson, color = Ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(active.teaching, color = Ink)
                    }
                    if (active.id == "north" || active.id == "south") {
                        AcademyCompass()
                    }
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(active.prompt, color = Ink, fontWeight = FontWeight.Bold,
                            fontSize = if (mapLesson) 14.sp else 16.sp, modifier = Modifier.weight(1f))
                        if (mapLesson) active.symbolId?.let { id ->
                            WoodlandLandmarkSwatch(id, mapLook, symbolSize = 48.dp)
                        }
                    }
                    val submit: (String) -> Unit = { choice ->
                        if (inGraduation && practiceDone) {
                            val next = ExplorerAcademy.acceptGraduation(earned, fieldEarned, active.id, choice)
                            if (next != fieldEarned) {
                                val awards = ExplorerAcademy.qualifications(qualifications, earned, next)
                                prefs.edit().putStringSet("graduation", next)
                                    .putStringSet("qualifications", awards).apply()
                                fieldEarned = next
                                qualifications = awards
                                success = "✓ " + active.explanation
                            } else {
                                success = ""
                                feedback = "Not quite. Review the clue and Map Guide, then try again."
                            }
                        } else {
                        val next = ExplorerAcademy.accept(earned, active.id, choice)
                        if (next != earned) {
                            prefs.edit().putStringSet("earned", next).apply()
                            success = "✓ " + active.explanation
                            earned = next
                        } else {
                            success = ""
                            feedback = active.explanation + " Try again."
                        }
                        }
                    }
                    val task = active.mapTask
                    if (task != null) {
                        AcademyGrid(task, selected) { selected = it }
                    }
                    if (active.woodlandField) {
                        AcademyWoodlandMap(mapLook, onLook, selected) { selected = it }
                        active.route?.let { route ->
                            OutlinedButton(onClick = { selected = route.start.reference },
                                modifier = Modifier.fillMaxWidth().testTag("academyRouteReset")) {
                                Text("Return to start · " + route.start.reference)
                            }
                        }
                    }
                    if (task?.selectSquare == true || active.woodlandField) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Selected: " + (selected ?: "Tap a square"), color = Forest,
                            modifier = Modifier.weight(1f), fontSize = 12.sp)
                        Button(onClick = { selected?.let(submit) },
                            enabled = selected != null,
                            modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                .testTag("academyCheckSquare"), contentPadding = PaddingValues(horizontal = 6.dp)) { Text("Check square") }
                        }
                    } else active.choices.chunked(2).forEachIndexed { rowIndex, choices ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            choices.forEachIndexed { columnIndex, choice ->
                                OutlinedButton(onClick = { submit(choice) },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp)
                                        .testTag("academyChoice_${rowIndex * 2 + columnIndex}")) { Text(choice) }
                            }
                        }
                    }
                    if (active.woodlandField && success.isNotBlank()) Text(success, color = Forest,
                        fontSize = 12.sp, modifier = Modifier.testTag("academySuccess"))
                    if (feedback.isNotBlank()) Text(feedback, color = Forest,
                        modifier = Modifier.testTag("academyFeedback"))
                }
            }
        } else {
            if (graduated) {
                AcademyGraduationCard()
            } else {
                Text("Practice complete!", color = Forest, fontSize = 23.sp,
                    fontWeight = FontWeight.Bold, modifier = Modifier.testTag("academyPracticeComplete"))
                Text("Your field assignment awaits. Apply your skills in four woodland challenges.", color = Ink)
            }
            if (!graduated) Button(onClick = { inGraduation = true; success = "" },
                modifier = Modifier.fillMaxWidth().testTag("academyStartGraduation")) {
                Text("Begin graduation expedition")
            }
            Button(onClick = {
                prefs.edit().remove("earned").remove("graduation").apply()
                earned = emptySet()
                fieldEarned = emptySet()
                inGraduation = false
                success = ""
            }, modifier = Modifier.fillMaxWidth().testTag("academyReplay")) {
                Text("Practice again")
            }
        }
        }
        Text("Learn at your own pace. No timer or penalties.", color = Muted, fontSize = 12.sp)
        }
    }
}

/** A small accessible schematic; labels sit on the west and south grid edges. */
@Composable
private fun AcademyGrid(task: AcademyMapTask, selected: String?, onSelect: (String) -> Unit) {
    val targetE = task.target.take(2).toInt()
    val targetN = task.target.takeLast(2).toInt()
    Column(Modifier.fillMaxWidth().testTag("academyGrid")) {
        Text("N ↑", color = Forest, fontWeight = FontWeight.Bold)
        for (n in AcademyTrainingGrid.northings.reversed()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text(n.toString(), color = Forest, modifier = Modifier.width(26.dp).offset(y = 8.dp),
                    fontWeight = if (task.guided && n == targetN) FontWeight.Bold else FontWeight.Normal)
                for (e in AcademyTrainingGrid.eastings) {
                    val code = AcademyTrainingGrid.reference(e, n)
                    val target = task.guided && code == task.target
                    val shade = when {
                        code == selected -> Color(0xFFF8DCA0)
                        target -> Color(0xFFADC398)
                        task.guided && (e == targetE || n == targetN) -> Color(0xFFE3E7BE)
                        else -> Color(0xFFF5F0DC)
                    }
                    Box(
                        Modifier.weight(1f).heightIn(min = 48.dp)
                            .background(shade).border(1.dp, Forest)
                            .then(if (task.selectSquare) Modifier
                                .clickable { onSelect(code) }
                                .testTag("academyCell_$code")
                                .semantics { contentDescription = "Square at column ${e}, row ${n}" }
                                else Modifier),
                        contentAlignment = Alignment.Center
                    ) {
                        if (target) Text("Camp", color = Ink, fontSize = 12.sp)
                        else if (code == selected) Text("✓", color = Forest)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.width(26.dp))
            for (e in AcademyTrainingGrid.eastings) {
                Text(e.toString(), color = Forest, modifier = Modifier.weight(1f),
                    fontWeight = if (task.guided && e == targetE) FontWeight.Bold else FontWeight.Normal)
            }
        }
        Text("Read across first, then up.", color = Muted, fontSize = 12.sp)
    }
}

/** Same map renderer and camera as Adventure, without touching campaign saves. */
@Composable
private fun AcademyWoodlandMap(
    mapLook: MapLook, onLook: (MapLook) -> Unit,
    selected: String?, onSelect: (String) -> Unit
) {
    val bounds = WoodlandTrainingMap.bounds
    var viewport by remember { mutableStateOf(IntSize.Zero) }
    var camera by remember { mutableStateOf<MapCamera?>(null) }
    var guide by rememberSaveable { mutableStateOf(false) }
    fun fit() {
        if (viewport.width > 80 && viewport.height > 80)
            camera = MapCamera.fit(bounds, viewport.width.toDouble(), viewport.height.toDouble())
    }
    fun zoom(factor: Double) {
        if (viewport.width > 80 && viewport.height > 80) {
            camera = camera?.transform(bounds, viewport.width.toDouble(), viewport.height.toDouble(),
                PixelPoint(viewport.width / 2.0, viewport.height / 2.0),
                PixelPoint(0.0, 0.0), factor)
        }
    }
    HuntMapPanel(
        modifier = Modifier.fillMaxWidth().height(320.dp).testTag("academyWoodlandMap"),
        bounds = bounds, camera = camera,
        selectedCell = selected?.let { runCatching { GridReference.parse(it) }.getOrNull() },
        completed = emptyList(), viewport = viewport,
        onViewport = { size -> if (size != viewport) { viewport = size; fit() } },
        onCamera = { camera = it }, onSelect = onSelect, markerPoint = null,
        enableSelect = true, mapLook = mapLook, onLook = onLook
    )
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            Triple(ExplorerTool.ZOOM_IN, "academyZoomIn", { zoom(1.6) }),
            Triple(ExplorerTool.ZOOM_OUT, "academyZoomOut", { zoom(1.0 / 1.6) }),
            Triple(ExplorerTool.FIT, "academyFit", { fit() }),
            Triple(ExplorerTool.GUIDE, "academyGuide", { guide = true })
        ).forEach { (tool, tag, action) ->
            AcademyToolButton(tool, tag, Modifier.weight(1f), action)
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(Triple(ExplorerTool.WEST, -1, 0), Triple(ExplorerTool.EAST, 1, 0),
            Triple(ExplorerTool.NORTH, 0, 1), Triple(ExplorerTool.SOUTH, 0, -1)).forEach { (tool, dx, dy) ->
            val direction = tool.name.lowercase().replaceFirstChar { it.uppercase() }
            AcademyToolButton(tool, "academyMove$direction", Modifier.weight(1f)) {
                val base = GridReference.parse(selected ?: "2640")
                val e = (base.eastIndex + dx).coerceIn(24, 29)
                val n = (base.northIndex + dy).coerceIn(37, 42)
                onSelect(com.playereden.gridhunt.core.GridCell(e, n).reference)
            }
        }
    }
    if (guide) WoodlandMapGuideDialog(mapLook = mapLook, onDismiss = { guide = false })
}

@Composable
private fun AcademySchoolOverview(
    earned: Set<String>, fieldEarned: Set<String>, qualifications: Set<String>,
    onContinue: () -> Unit
) {
    val complete = ExplorerAcademy.graduated(earned, fieldEarned)
    val practiceDone = ExplorerAcademy.practiceComplete(earned)
    Surface(color = Color(0xFFFFF0CF), shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().testTag("academySchoolOverview")) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ExplorerInsignia(Modifier.size(72.dp).testTag("academyCrest"))
                Column(Modifier.weight(1f)) {
                    Text("School of Exploration", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("Your first steps from map reader to explorer.", color = Forest, fontSize = 12.sp)
                }
            }
            Text("Field training", color = Ink, fontWeight = FontWeight.Bold)
            ExplorerAcademy.exercises.groupBy { it.lesson }.forEach { (lesson, exercises) ->
                val count = exercises.count { it.id in earned }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(if (count == exercises.size) "✓" else "○", color = Forest,
                        modifier = Modifier.width(24.dp))
                    Text(lesson, color = Ink, modifier = Modifier.weight(1f))
                    Text("$count / ${exercises.size}", color = Muted, fontSize = 12.sp)
                }
            }
            HorizontalDivider(color = Forest.copy(alpha = 0.25f))
            Text("The Explorer's First Journey", color = Ink, fontWeight = FontWeight.Bold)
            Text(if (practiceDone) "${fieldEarned.size} / ${ExplorerAcademy.graduation.size} checkpoints"
                else "Complete field training to unlock graduation.", color = Forest)
            if (ExplorerAcademy.qualificationId in qualifications) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExplorerInsignia(Modifier.size(48.dp), medal = true)
                    Column {
                        Text("Junior Explorer", color = Forest, fontWeight = FontWeight.Bold)
                        Text("Bronze Compass · Qualification earned", color = Muted, fontSize = 12.sp)
                    }
                }
            }
            Button(onClick = onContinue, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                .testTag("academyContinueSchool")) {
                Text(when {
                    complete -> "View graduation"
                    practiceDone -> "Continue to graduation"
                    earned.isEmpty() -> "Begin field training"
                    else -> "Continue field training"
                })
            }
        }
    }
}

@Composable
private fun AcademyCompass() {
    Box(Modifier.fillMaxWidth().height(132.dp)
        .semantics { contentDescription = "Compass rose: north up, east right, south down, west left" },
        contentAlignment = Alignment.Center) {
        Box(Modifier.size(132.dp)) {
            androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                val r = size.minDimension * 0.31f
                val center = this.center
                drawCircle(Forest.copy(alpha = 0.10f), r, center)
                drawCircle(Forest, r, center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.dp.toPx()))
                val needle = androidx.compose.ui.graphics.Path().apply {
                    moveTo(center.x, center.y-r)
                    lineTo(center.x+r*0.23f, center.y)
                    lineTo(center.x, center.y+r)
                    lineTo(center.x-r*0.23f, center.y)
                    close()
                }
                drawPath(needle, Forest)
                val south = androidx.compose.ui.graphics.Path().apply {
                    moveTo(center.x-r*0.23f, center.y)
                    lineTo(center.x+r*0.23f, center.y)
                    lineTo(center.x, center.y+r)
                    close()
                }
                drawPath(south, Color(0xFFC69D53))
                drawLine(Forest, center.copy(x = center.x-r), center.copy(x = center.x+r), 1.dp.toPx())
                drawCircle(Paper, 3.dp.toPx(), center)
            }
            Text("N", Modifier.align(Alignment.TopCenter), color = Forest, fontWeight = FontWeight.Bold)
            Text("S", Modifier.align(Alignment.BottomCenter), color = Forest)
            Text("W", Modifier.align(Alignment.CenterStart), color = Forest)
            Text("E", Modifier.align(Alignment.CenterEnd), color = Forest)
        }
    }
}

@Composable
private fun AcademyGraduationCard() {
    Surface(color = Color(0xFFFFF0CF), shape = RoundedCornerShape(18.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Gold, RoundedCornerShape(18.dp))) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("SCHOOL OF EXPLORATION", color = Forest, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            ExplorerInsignia(Modifier.size(136.dp).testTag("academyGraduationMedal"), medal = true)
            Text("Congratulations, Junior Explorer!", color = Forest, fontSize = 23.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.testTag("academyPracticeComplete"),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("Bronze Compass · Qualification earned", color = Ink, fontWeight = FontWeight.SemiBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            HorizontalDivider(color = Gold.copy(alpha = .45f))
            Text("You completed The Explorer’s First Journey. Your qualification is yours to keep, even when you replay.",
                color = Ink, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun AcademyToolButton(tool: ExplorerTool, tag: String, modifier: Modifier, action: () -> Unit) {
    OutlinedButton(onClick = action,
        modifier = modifier.heightIn(min = 48.dp).testTag(tag)
            .semantics { contentDescription = tool.label },
        contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ExplorerToolIcon(tool)
            Text(tool.label.removePrefix("Move "), fontSize = 10.sp, color = Forest,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
