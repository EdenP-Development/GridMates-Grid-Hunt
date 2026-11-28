package com.playereden.gridmates.gridhunt

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.playereden.gridhunt.core.*

/**
 * Isolated candidate navigation. Not installed in MainActivity.
 * Hosts provide the actual regional gameplay composable, never a fake success screen.
 */
internal enum class RegionalMode { ADVENTURE, QUICK_PLAY, CUSTOM_PLAY }

@Composable
internal fun AtlasPreviewNavigation(
    onExit: () -> Unit,
    gameplay: @Composable (WorldDestination, RegionalMode, CampaignChapter?, () -> Unit, (() -> Unit)?) -> Unit
) {
    val context = LocalContext.current
    val navigation = remember(context) {
        WorldNavigationStore(context, CoastalExpeditionPreview.selection, true)
    }
    AtlasNavigation(navigation,onExit,gameplay)
}

/** Published-only entry point. The default store cannot select a draft world. */
@Composable
internal fun PublishedAtlasNavigation(
    onExit: () -> Unit,
    gameplay: @Composable (WorldDestination, RegionalMode, CampaignChapter?, () -> Unit, (() -> Unit)?) -> Unit
) {
    val context = LocalContext.current
    val navigation = remember(context) { WorldNavigationStore(context) }
    AtlasNavigation(navigation,onExit,gameplay)
}

/** Shared route controller. Its validated store owns all availability decisions. */
@Composable
private fun AtlasNavigation(
    navigation: WorldNavigationStore,
    onExit: () -> Unit,
    gameplay: @Composable (WorldDestination, RegionalMode, CampaignChapter?, () -> Unit, (() -> Unit)?) -> Unit
) {
    var regionId by rememberSaveable { mutableStateOf<String?>(null) }
    var modeName by rememberSaveable { mutableStateOf<String?>(null) }
    var chapterId by rememberSaveable { mutableStateOf<String?>(null) }

    fun returnToRegion() {
        modeName = null
        chapterId = null
    }
    fun returnToAtlas() {
        returnToRegion()
        regionId = null
    }
    BackHandler {
        when {
            modeName != null -> returnToRegion()
            regionId != null -> returnToAtlas()
            else -> onExit()
        }
    }

    val choice = navigation.choices().firstOrNull {
        it.destination.world.id == regionId && it.unlocked
    }
    val mode = RegionalMode.values().firstOrNull { it.name == modeName }
    val chapter = if (mode == RegionalMode.ADVENTURE && choice != null) {
        chapterId?.let { navigation.openChapter(choice.destination.world.id,it) }
    } else null

    when {
        choice == null -> {
            if (regionId != null) LaunchedEffect(regionId) { returnToAtlas() }
            ExplorerAtlasScreen(navigation,
                onOpenWorld = { destination ->
                    returnToRegion()
                    regionId = destination.world.id
                },
                onBack = onExit)
        }
        modeName == null -> RegionGameplayHub(
            navigation = navigation,
            destination = choice.destination,
            onChapter = { selected ->
                chapterId = selected.chapter.id
                modeName = RegionalMode.ADVENTURE.name
            },
            onQuickPlay = {
                chapterId = null
                modeName = RegionalMode.QUICK_PLAY.name
            },
            onCustomPlay = {
                chapterId = null
                modeName = RegionalMode.CUSTOM_PLAY.name
            },
            onBack = { returnToAtlas() }
        )
        mode == null || (mode == RegionalMode.ADVENTURE && chapter == null) -> {
            LaunchedEffect(modeName,chapterId) { returnToRegion() }
        }
        else -> key(choice.destination.world.id,mode,chapter?.chapter?.id) {
            val current = chapter
            val next = current?.let { active ->
                choice.destination.world.chapters.sortedBy { it.order }
                    .dropWhile { it != active }.drop(1).firstOrNull()
            }
            val advance: (() -> Unit)? = if (mode == RegionalMode.ADVENTURE &&
                current != null && next != null) ({
                val fresh = navigation.choices().firstOrNull {
                    it.destination == choice.destination && it.unlocked
                }
                val finished = fresh?.chapters?.firstOrNull { it.chapterId == current.chapter.id }
                if (regionId == choice.destination.world.id &&
                    modeName == RegionalMode.ADVENTURE.name &&
                    chapterId == current.chapter.id &&
                    finished?.status == CampaignChapterStatus.COMPLETE &&
                    navigation.openChapter(choice.destination.world.id,next.chapter.id) == next) {
                    chapterId = next.chapter.id
                }
            }) else null
            gameplay(choice.destination,mode,chapter,{ returnToRegion() },advance)
        }
    }
}
