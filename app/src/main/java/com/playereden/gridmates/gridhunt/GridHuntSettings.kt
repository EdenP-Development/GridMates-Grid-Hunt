package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

/** Central entry to existing preferences; no migration, new account or reset. */
@Composable
internal fun GridHuntSettings(
    haptics: Boolean, onHaptics: (Boolean) -> Unit, mapLook: MapLook,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val controls = remember(context) {
        context.getSharedPreferences("gridhunt_ui_preferences", Context.MODE_PRIVATE)
    }
    var precise by remember { mutableStateOf(controls.getBoolean("precise_selection", false)) }
    var guide by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxSize().journalPaper().statusBarsPadding().navigationBarsPadding()
            .journalScroll().padding(17.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Settings", color = Ink, fontWeight = FontWeight.ExtraBold,
                fontSize = 27.sp, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack, modifier = Modifier.testTag("settingsBack")) {
                Text("Home", color = Forest)
            }
        }
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFF5E8C9),
            border = JournalPanelBorder) {
            Column(Modifier.fillMaxWidth().padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("PLAY & ACCESSIBILITY", color = Forest, fontWeight = FontWeight.Bold,
                    fontSize = 11.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Victory vibration", color = Ink, fontWeight = FontWeight.Bold)
                        Text("A little buzz on supported puzzle successes.", color = Muted, fontSize = 12.sp)
                    }
                    Switch(checked = haptics, onCheckedChange = onHaptics,
                        modifier = Modifier.testTag("settingsHaptics"))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Precise selection arrows", color = Ink, fontWeight = FontWeight.Bold)
                        Text("Optional directional buttons for selecting grid squares.", color = Muted, fontSize = 12.sp)
                    }
                    Switch(checked = precise, onCheckedChange = {
                        precise = it
                        controls.edit().putBoolean("precise_selection", it).apply()
                    }, modifier = Modifier.testTag("settingsPrecise"))
                }
            }
        }
        OutlinedButton(onClick = { guide = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("settingsGuide")) {
            Text("Map guide")
        }
        Surface(shape = RoundedCornerShape(18.dp), color = Color(0xFFE4EDDA),
            border = JournalPanelBorder) {
            Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("GridMates: Grid Hunt", color = Ink, fontWeight = FontWeight.Bold)
                Text("Little maps. Big discoveries.", color = Muted, fontSize = 13.sp)
                Text("Play offline. Puzzle progress stays on your device.", color = Muted, fontSize = 12.sp)
            }
        }
    }
    if (guide) WoodlandMapGuideDialog(mapLook = mapLook, onDismiss = { guide = false })
}
