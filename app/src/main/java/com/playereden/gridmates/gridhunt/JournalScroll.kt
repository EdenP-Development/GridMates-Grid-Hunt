package com.playereden.gridmates.gridhunt

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * A passive journal-style scroll rail: only visible when content overflows.
 * The rail is outside the scrolling content and never consumes gestures.
 * Padding reserves space so text is not obscured; RTL uses the trailing edge.
 */
@Composable
internal fun Modifier.journalScroll(state: ScrollState = rememberScrollState()): Modifier =
    this.drawWithContent {
        drawContent()
        val overflow = state.maxValue
        if (overflow > 0 && overflow != Int.MAX_VALUE && size.height > 0f) {
            val inset = 3.dp.toPx()
            val trackHeight = (size.height - 2 * inset).coerceAtLeast(0f)
            if (trackHeight > 0f) {
                val thumbHeight = (trackHeight * size.height / (size.height + overflow))
                    .coerceIn(24.dp.toPx().coerceAtMost(trackHeight), trackHeight)
                val y = inset + (trackHeight - thumbHeight) *
                    (state.value.toFloat() / overflow).coerceIn(0f, 1f)
                val width = 3.dp.toPx()
                val x = if (layoutDirection == LayoutDirection.Ltr) size.width - width - 1.dp.toPx() else 1.dp.toPx()
                val corner = CornerRadius(width / 2f)
                drawRoundRect(Color(0x24795C36), Offset(x, inset), Size(width, trackHeight), corner)
                drawRoundRect(Forest.copy(alpha = .58f), Offset(x, y), Size(width, thumbHeight), corner)
            }
        }
    }.padding(end = 7.dp).verticalScroll(state)
