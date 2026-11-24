package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.ExplorerAcademy

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
                    active.choices.forEachIndexed { index, choice ->
                        OutlinedButton(onClick = {
                            val next = ExplorerAcademy.accept(earned, active.id, choice)
                            if (next != earned) {
                                prefs.edit().putStringSet("earned", next).apply()
                                earned = next
                            } else feedback = active.explanation + " Try again."
                        }, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
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
            }, modifier = Modifier.fillMaxWidth().testTag("academyReplay")) {
                Text("Practice again")
            }
        }
        Text("Learn at your own pace. No timer or penalties.", color = Muted)
    }
}
