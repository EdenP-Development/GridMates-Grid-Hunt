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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.ExplorerAcademy
import com.playereden.gridhunt.core.AcademyTrainingGrid
import com.playereden.gridhunt.core.AcademyMapTask

@Composable
internal fun ExplorerAcademyScreen(onHome: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember(context) {
        context.getSharedPreferences(ExplorerAcademy.saveName, Context.MODE_PRIVATE)
    }
    var earned by remember { mutableStateOf(ExplorerAcademy.restore(
        prefs.getStringSet("earned", emptySet())?.toSet() ?: emptySet()
    )) }
    val active = ExplorerAcademy.current(earned)
    var feedback by rememberSaveable(active?.id) { mutableStateOf("") }
    var selected by rememberSaveable(active?.id) { mutableStateOf<String?>(null) }
    var success by rememberSaveable { mutableStateOf("") }
    Column(
        Modifier.fillMaxSize().background(Paper).statusBarsPadding()
            .navigationBarsPadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("Explorer's Academy", fontSize = 26.sp, color = Ink,
                    fontWeight = FontWeight.ExtraBold)
                Text("School of Exploration", color = Forest)
            }
            TextButton(onClick = onHome, modifier = Modifier.testTag("academyHome")) {
                Text("Home")
            }
        }
        Text("Beginner practice · ${earned.size} of ${ExplorerAcademy.exercises.size}",
            color = Forest, modifier = Modifier.testTag("academyProgress"))
        if (success.isNotBlank()) Text(success, color = Forest,
            modifier = Modifier.testTag("academySuccess"))
        if (active != null) {
            Surface(color = Color(0xFFFFF0CF), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.fillMaxWidth().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(active.lesson, color = Ink, fontSize = 20.sp,
                        fontWeight = FontWeight.Bold)
                    Text(active.teaching, color = Ink)
                    if (active.id == "north" || active.id == "south") {
                        Text("↑ N    ·    E →\n← W    ·    S ↓", color = Forest,
                            fontSize = 22.sp)
                    }
                    Text(active.prompt, color = Ink, fontWeight = FontWeight.Bold)
                    val submit: (String) -> Unit = { choice ->
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
                    val task = active.mapTask
                    if (task != null) {
                        AcademyGrid(task, selected) { selected = it }
                    }
                    if (task?.selectSquare == true) {
                        Text("Selected: " + (selected ?: "Tap a square"), color = Forest)
                        Button(onClick = { selected?.let(submit) },
                            enabled = selected != null,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                .testTag("academyCheckSquare")) { Text("Check square") }
                    } else active.choices.forEachIndexed { index, choice ->
                        OutlinedButton(onClick = { submit(choice) },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                .testTag("academyChoice_$index")) { Text(choice) }
                    }
                    if (feedback.isNotBlank()) Text(feedback, color = Forest,
                        modifier = Modifier.testTag("academyFeedback"))
                }
            }
        } else {
            Text("Practice complete!", color = Forest, fontSize = 23.sp,
                fontWeight = FontWeight.Bold, modifier = Modifier.testTag("academyPracticeComplete"))
            Text("You've completed the first compass and grid exercises. More lessons and graduation are in development.", color = Ink)
            Button(onClick = {
                prefs.edit().remove("earned").apply()
                earned = emptySet()
                success = ""
            }, modifier = Modifier.fillMaxWidth().testTag("academyReplay")) {
                Text("Practice again")
            }
        }
        Text("Learn at your own pace. No timer or penalties.", color = Muted)
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
                Text(n.toString(), color = Forest, modifier = Modifier.width(26.dp),
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
