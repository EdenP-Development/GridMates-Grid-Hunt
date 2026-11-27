package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.CampaignChapter
import com.playereden.gridhunt.core.CampaignChapterStatus
import com.playereden.gridhunt.core.WorldDestination

/**
 * Catalog-driven regional hub. A preview host must supply its own isolated
 * navigation store and explicit gameplay callbacks. This is not a publication
 * switch and does not change existing Home or Adventure navigation.
 */
@Composable
internal fun RegionGameplayHub(
    navigation: WorldNavigationStore,
    destination: WorldDestination,
    onChapter: (CampaignChapter) -> Unit,
    onQuickPlay: (WorldDestination) -> Unit,
    onCustomPlay: (WorldDestination) -> Unit,
    onBack: () -> Unit
) {
    fun available() = navigation.choices().firstOrNull {
        it.destination == destination && it.unlocked
    }
    val choice = available()
    Column(
        Modifier.fillMaxSize().journalPaper()
            .statusBarsPadding().navigationBarsPadding().journalScroll()
            .padding(horizontal = 17.dp, vertical = 8.dp)
            .testTag("regionGameplayHub"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(destination.world.name, color = Ink, fontWeight = FontWeight.ExtraBold,
                    fontSize = 27.sp)
                Text("Your regional adventures", color = Muted, fontSize = 12.sp)
            }
            TextButton(onClick = onBack,
                modifier = Modifier.heightIn(min = 48.dp).testTag("regionBack")) {
                Text("Atlas", color = Forest)
            }
        }
        if (choice == null) {
            Text("This region is unavailable. Return to the Atlas to choose another region.",
                color = Ink, fontSize = 14.sp, modifier = Modifier.testTag("regionUnavailable"))
        } else {
            Text("${choice.completedChapters} of ${choice.chapters.size} chapters complete",
                color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp,
                modifier = Modifier.testTag("regionProgress"))
            Text("Adventure", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
            destination.world.chapters.sortedBy { it.order }.forEach { chapter ->
                val progress = choice.chapters.first { it.chapterId == chapter.chapter.id }
                val locked = progress.status == CampaignChapterStatus.LOCKED
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp), border = JournalPanelBorder,
                    color = if (locked) Color(0xFFF3E9D2) else Color(0xFFE4EDDA)
                ) {
                    Column(Modifier.fillMaxWidth().padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        Text("CHAPTER ${chapter.order + 1}", color = Forest,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(chapter.chapter.name, color = Ink,
                            fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(if (locked) "Complete prerequisite chapters to unlock"
                            else "${progress.completedObjectives} of ${progress.totalObjectives} objectives solved",
                            color = Muted, fontSize = 12.sp)
                        if (progress.everCompleted) {
                            Text("✓ Previously completed", color = Forest, fontSize = 12.sp,
                                modifier = Modifier.testTag("regionEarned_${chapter.chapter.id}"))
                        }
                        Button(
                            onClick = {
                                if (available() != null) {
                                    navigation.openChapter(destination.world.id,chapter.chapter.id)
                                        ?.takeIf { it == chapter }?.let(onChapter)
                                }
                            },
                            enabled = !locked,
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                                .testTag("regionChapter_${chapter.chapter.id}"),
                            shape = JournalActionShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Forest)
                        ) {
                            Text(when (progress.status) {
                                CampaignChapterStatus.LOCKED -> "Locked"
                                CampaignChapterStatus.COMPLETE -> "Revisit chapter"
                                CampaignChapterStatus.IN_PROGRESS -> if (progress.everCompleted) "Continue replay" else "Continue chapter"
                                CampaignChapterStatus.READY -> if (progress.everCompleted) "Revisit chapter" else "Start chapter"
                            })
                        }
                    }
                }
            }
            Text("Choose your own path", color = Ink, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Button(onClick = { available()?.destination?.let(onQuickPlay) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("regionQuickPlay"),
                colors = ButtonDefaults.buttonColors(containerColor = Forest), shape = JournalActionShape) {
                Text("Quick Play")
            }
            Button(onClick = { available()?.destination?.let(onCustomPlay) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("regionCustomPlay"),
                colors = ButtonDefaults.buttonColors(containerColor = Forest), shape = JournalActionShape) {
                Text("Custom Play")
            }
        }
    }
}
