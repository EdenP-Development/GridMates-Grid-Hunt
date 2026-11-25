package com.playereden.gridmates.gridhunt

import android.animation.ValueAnimator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.asImageBitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

// Adapted from the owner-approved shared game startup v1.0 Sudoku pilot.
internal fun startupBrandingStages(seriesDrawable: Int? = null): List<Int> =
    listOfNotNull(R.drawable.spudacus_v1_full_transparent, seriesDrawable)

@Composable
internal fun StartupBranding(showInitially: Boolean, seriesDrawable: Int? = null, content: @Composable () -> Unit) {
    val stages = remember(seriesDrawable) { startupBrandingStages(seriesDrawable) }
    var stage by remember { mutableIntStateOf(if (showInitially && ValueAnimator.areAnimatorsEnabled()) 0 else stages.size) }
    val opacity = remember { Animatable(0f) }
    val visible = stage < stages.size
    val lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_STOP) stage = stages.size
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
    LaunchedEffect(stage) {
        if (stage >= stages.size) return@LaunchedEffect
        if (!ValueAnimator.areAnimatorsEnabled()) {
            stage = stages.size
            return@LaunchedEffect
        }
        opacity.snapTo(0f)
        opacity.animateTo(1f, tween(250, easing = androidx.compose.animation.core.LinearEasing))
        delay(1000)
        opacity.animateTo(0f, tween(250))
        stage++
    }
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().then(if (visible) Modifier.clearAndSetSemantics { } else Modifier)) { content() }
        BackHandler(visible) { stage = stages.size }
        if (visible) {
        // Keep the cream surface continuous between both stages.
        Box(
            Modifier.fillMaxSize().background(Color(0xFFFAE7CD))
                .clickable(onClickLabel = "Skip startup") { stage = stages.size }
                .testTag("startup_branding"),
            contentAlignment = Alignment.Center
        ) {
            val resources = LocalContext.current.resources
            val drawable = stages[stage]
            val bitmap = remember(drawable, resources) {
                runCatching { BitmapFactory.decodeResource(resources, drawable)?.asImageBitmap() }.getOrNull()
            }
            if (bitmap == null) {
                LaunchedEffect(drawable) { stage = stages.size }
            } else Image(
                bitmap = bitmap,
                contentDescription = if (stage == 0) "Spudacus" else "GridMates",
                contentScale = ContentScale.Fit,
                modifier = Modifier.safeDrawingPadding().padding(24.dp)
                    .widthIn(max = 420.dp).fillMaxWidth(.82f)
                    .heightIn(max = 260.dp).alpha(opacity.value)
            )
        }
    }
    }
}
