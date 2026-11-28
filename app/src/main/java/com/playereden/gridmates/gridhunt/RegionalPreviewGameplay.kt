package com.playereden.gridmates.gridhunt

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.playereden.gridhunt.core.*

/** Opt-in internal host only; no new MainActivity entry point. */
@Composable
internal fun RegionalPreviewGameplay(hapticsEnabled: Boolean = false, onExit: () -> Unit) {
    AtlasPreviewNavigation(onExit) { destination, mode, chapter, onBack, onNext ->
        val map = destination.map
        val look = MapLook.FIELD_GUIDE
        when (mode) {
            RegionalMode.ADVENTURE -> {
                val canonical = destination.world.chapters.firstOrNull { it == chapter }
                val coastalRun = if (map == WhisperingCoastDraft.map && canonical != null)
                    (1..3).map(CoastalExpeditionPreview::chapter).firstOrNull {
                        it.chapter == canonical.chapter && it.saveName == canonical.progressKey
                    } else null
                if (coastalRun != null) {
                    WoodlandMixedExpeditionScreen(onHome = onBack, mapLook = look,
                        onLook = {}, hapticsEnabled = hapticsEnabled, coastalPreview = coastalRun, onNextChapter = onNext)
                } else if (map == ExplorationMaps.woodland && canonical != null &&
                    canonical.chapter.id in setOf(WoodlandExpedition.chapter.id,
                        WoodlandSupplyExpedition.chapter.id,WoodlandReturnExpedition.chapter.id)) {
                    WoodlandMixedExpeditionScreen(onHome = onBack, mapLook = look,
                        onLook = {}, hapticsEnabled = hapticsEnabled, onNextChapter = onNext,
                        chapterTwo = canonical.chapter.id == WoodlandSupplyExpedition.chapter.id,
                        chapterThree = canonical.chapter.id == WoodlandReturnExpedition.chapter.id)
                } else PreviewRouteUnavailable(onBack)
            }
            RegionalMode.CUSTOM_PLAY -> CustomHuntScreen(look, {}, onBack, map)
            RegionalMode.QUICK_PLAY -> RegionalQuickPlayPreview(map, hapticsEnabled, onBack)
        }
    }
}

@Composable
private fun RegionalQuickPlayPreview(map: ExplorationMap, hapticsEnabled: Boolean, onBack: () -> Unit) {
    val trail = TrailChapterCatalog.forAuthoredMap(map)
    val reverse = SurveyMarkerCatalog.forAuthoredMap(map)
    val treasure = TreasureChecklistCatalog.forAuthoredMap(map)
    val identify = IdentifyMissionCatalog.forAuthoredMap(map)
    var mode by rememberSaveable(map.id,map.revision) { mutableStateOf<String?>(null) }
    val returnToModes: () -> Unit = { mode = null }
    BackHandler(enabled = mode != null) { returnToModes() }
    val look = MapLook.FIELD_GUIDE
    when (mode) {
        "trail" -> if (trail != null) FirstBearingsScreen(returnToModes,look,{},hapticsEnabled,trail)
            else PreviewRouteUnavailable(returnToModes)
        "gold" -> if (reverse != null) ReversePracticeScreen(returnToModes,look,{},hapticsEnabled,reverse)
            else PreviewRouteUnavailable(returnToModes)
        "treasure" -> if (treasure != null) TreasureChecklistScreen(returnToModes,look,{},hapticsEnabled,treasure)
            else PreviewRouteUnavailable(returnToModes)
        "identify" -> if (identify != null) WoodlandIdentifyScreen(returnToModes,look,{},hapticsEnabled,identify)
            else PreviewRouteUnavailable(returnToModes)
        "mixed" -> if (map == ExplorationMaps.woodland)
            WoodlandMixedExpeditionScreen(onHome = returnToModes, mapLook = look,
                onLook = {}, hapticsEnabled = hapticsEnabled)
            else PreviewRouteUnavailable(returnToModes)
        else -> Column(Modifier.fillMaxSize().journalPaper().statusBarsPadding()
            .navigationBarsPadding().journalScroll().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(map.name, color = Ink, fontSize = 24.sp)
            Text("Quick Play", color = Forest, fontSize = 18.sp)
            listOf(Triple("trail","Trail Treasures",trail != null),
                Triple("gold","Golden Spots",reverse != null),
                Triple("treasure","Treasure Checklist",treasure != null),
                Triple("identify","What's at This Grid?",identify != null),
                Triple("mixed","The Missing Field Notes",map == ExplorationMaps.woodland))
                .filter { it.third }.forEach { (id,label,_) ->
                    Button(onClick = { mode = id }, shape = JournalActionShape,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("regionalQuick_$id")) {
                        Text(label)
                    }
                }
            TextButton(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)
                .testTag("regionalQuickBack")) { Text("Region") }
        }
    }
}

@Composable
private fun PreviewRouteUnavailable(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().journalPaper().statusBarsPadding().padding(16.dp)) {
        Text("This puzzle is unavailable.", color = Ink)
        Button(onClick = onBack, modifier = Modifier.heightIn(min = 48.dp)) { Text("Back") }
    }
}
