package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.*

@Composable
internal fun ExpandedAdventureChoice(onClassic: () -> Unit, onExpanded: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().journalPaper().statusBarsPadding().navigationBarsPadding()
        .verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Adventure", Modifier.weight(1f), color = Ink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onBack) { Text("Home") }
        }
        Text("Choose your expedition", color = Muted)
        Button(onClick = onClassic, modifier = Modifier.fillMaxWidth().testTag("classicAdventure")) {
            Text("Classic Adventure")
        }
        Text("Continue your existing campaign.", color = Muted)
        OutlinedButton(onClick = onExpanded, modifier = Modifier.fillMaxWidth().testTag("expandedAdventure")) {
            Text("Expanded Adventure")
        }
        Text("Development playtest · 72 discoveries · Independent progress", color = Muted)
    }
}

private val expandedRuns = listOf(WoodlandFieldSurveyCandidate.run, WoodlandSupplySurveyCandidate.run,
    WoodlandReturnSurveyCandidate.run, CoastalBeaconSurveyCandidate.run, CoastalTideSurveyCandidate.run,
    CoastalLightSurveyCandidate.run)

@Composable
internal fun ExpandedAdventureSelector(onExit: () -> Unit) {
    val context = LocalContext.current
    var selected by rememberSaveable { mutableIntStateOf(-1) }
    val returnToSelector = { selected = -1 }
    BackHandler(enabled = selected in expandedRuns.indices) { returnToSelector() }
    when (selected) {
        0 -> ExpandedFieldSurveyPreview(returnToSelector)
        1 -> ExpandedSupplySurveyPreview(returnToSelector)
        2 -> ExpandedReturnSurveyPreview(returnToSelector)
        3 -> ExpandedBeaconSurveyPreview(returnToSelector)
        4 -> ExpandedTideSurveyPreview(returnToSelector)
        5 -> ExpandedLightSurveyPreview(returnToSelector)
        else -> Column(Modifier.fillMaxSize().journalPaper().statusBarsPadding().navigationBarsPadding()
            .verticalScroll(rememberScrollState()).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Expanded Adventure", Modifier.weight(1f), color = Ink, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onExit, modifier = Modifier.testTag("expandedBack")) { Text("Back") }
            }
            Text("Development playtest · Independent progress", color = Muted)
            listOf("Little Woodland", "Whispering Coast").forEachIndexed { world, name ->
                Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, border = JournalPanelBorder) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(name, color = Forest, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Text("36 discoveries · 3 chapters", color = Muted, fontSize = 12.sp)
                        (world * 3 until world * 3 + 3).forEach { index ->
                            val run = expandedRuns[index]
                            val raw = context.getSharedPreferences(run.saveName, Context.MODE_PRIVATE).all["step"]
                            val count = (raw as? Int)?.takeIf { it in 0..run.size } ?: 0
                            OutlinedButton(onClick = { selected = index },
                                modifier = Modifier.fillMaxWidth().testTag("expandedChapter" + index)) {
                                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(run.chapter.name, fontWeight = FontWeight.Bold)
                                    Text("$count of ${run.size} discoveries", color = Muted, fontSize = 12.sp)
                                    LinearProgressIndicator(progress = { count.toFloat() / run.size }, modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
