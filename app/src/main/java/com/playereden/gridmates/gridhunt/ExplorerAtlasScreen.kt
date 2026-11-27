package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.CampaignChapterStatus
import com.playereden.gridhunt.core.WorldChoice
import com.playereden.gridhunt.core.WorldDestination

/**
 * Atlas foundation. The caller supplies an explicitly scoped navigation store.
 * This screen does not publish maps or authorize gameplay routes by itself.
 * No placeholder destination or future mode is presented as playable.
 */
@Composable
internal fun ExplorerAtlasScreen(
    navigation: WorldNavigationStore,
    onOpenWorld: (WorldDestination) -> Unit,
    onBack: () -> Unit
) {
    var refresh by remember(navigation) { mutableIntStateOf(0) }
    val choices = remember(navigation, refresh) { navigation.choices() }
    val selectedId = remember(navigation, refresh) { navigation.current()?.destination?.world?.id }

    Column(
        Modifier.fillMaxSize().journalPaper()
            .statusBarsPadding().navigationBarsPadding().journalScroll()
            .padding(horizontal = 17.dp, vertical = 8.dp)
            .testTag("explorerAtlas"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("Explorer’s Atlas", color = Ink, fontWeight = FontWeight.ExtraBold,
                    fontSize = 27.sp)
                Text("Choose a region to explore.", color = Muted, fontSize = 12.sp)
            }
            TextButton(onClick = onBack,
                modifier = Modifier.heightIn(min = 48.dp).testTag("atlasBack")) {
                Text("Back", color = Forest)
            }
        }
        choices.forEach { choice ->
            AtlasWorldCard(choice, choice.destination.world.id == selectedId) {
                // Revalidate availability at the moment of selection.
                if (navigation.choose(choice.destination.world.id)) {
                    val resolved = navigation.current()
                    refresh++
                    if (resolved?.destination?.world?.id == choice.destination.world.id) {
                        onOpenWorld(resolved.destination)
                    }
                } else refresh++
            }
        }
        if (choices.isEmpty()) {
            Text("No exploration regions are available.", color = Muted, fontSize = 13.sp)
        }
        Text("Your discoveries stay on your device.", color = Muted, fontSize = 11.sp)
    }
}

@Composable
private fun AtlasWorldCard(choice: WorldChoice, selected: Boolean, onOpen: () -> Unit) {
    val world = choice.destination.world
    val map = choice.destination.map
    val widthKm = (map.bounds.east - map.bounds.west).toInt() / 1000
    val heightKm = (map.bounds.north - map.bounds.south).toInt() / 1000
    val active = choice.chapters.firstOrNull {
        !it.everCompleted && it.status == CampaignChapterStatus.IN_PROGRESS
    }
    val replay = choice.chapters.firstOrNull {
        it.everCompleted && it.status == CampaignChapterStatus.IN_PROGRESS
    }
    val background = if (choice.unlocked) Color(0xFFE4EDDA) else Color(0xFFF3E9D2)
    Surface(
        modifier = Modifier.fillMaxWidth().testTag("atlasWorld_${world.id}"),
        shape = RoundedCornerShape(18.dp), color = background,
        border = JournalPanelBorder, shadowElevation = if (choice.unlocked) 2.dp else 0.dp
    ) {
        Column(Modifier.fillMaxWidth().padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                RegionMapPortrait(map,Modifier.size(84.dp))
                Column(Modifier.weight(1f)) {
                    Text(world.name, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text("$widthKm × $heightKm km · ${map.cells.size} grid squares",
                        color = Muted, fontSize = 12.sp)
                }
            }
            Text("${choice.completedChapters} of ${choice.chapters.size} chapters complete",
                color = Forest, fontWeight = FontWeight.Bold, fontSize = 12.sp,
                modifier = Modifier.testTag("atlasProgress_${world.id}"))
            if (selected && choice.unlocked) {
                Text("Selected region", color = Forest, fontSize = 11.sp,
                    modifier = Modifier.testTag("atlasSelected_${world.id}"))
            }
            if (active != null) {
                Text("${active.completedObjectives} of ${active.totalObjectives} objectives solved",
                    color = Muted, fontSize = 12.sp)
            }
            if (replay != null) {
                Text("Replaying: ${replay.completedObjectives} of ${replay.totalObjectives} objectives solved",
                    color = Muted, fontSize = 12.sp)
            }
            if (!choice.unlocked) {
                Text("Complete prerequisite adventures to unlock this region.",
                    color = Ink, fontSize = 12.sp)
            }
            Button(
                onClick = onOpen, enabled = choice.unlocked,
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                    .testTag("atlasOpen_${world.id}"),
                colors = ButtonDefaults.buttonColors(containerColor = Forest),
                shape = JournalActionShape
            ) {
                Text(when {
                    !choice.unlocked -> "Locked"
                    choice.complete -> "Revisit region"
                    active != null -> "Continue exploring"
                    else -> "Explore region"
                }, fontWeight = FontWeight.Bold)
            }
        }
    }
}
