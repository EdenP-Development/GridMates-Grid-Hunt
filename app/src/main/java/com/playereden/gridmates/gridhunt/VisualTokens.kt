package com.playereden.gridmates.gridhunt

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

internal val Ink = Color(0xFF213D35)
internal val Forest = Color(0xFF315C4D)
internal val Pine = Color(0xFF467B61)
internal val Paper = Color(0xFFF8F1DF)
internal val Gold = Color(0xFFD1A350)
internal val Muted = Color(0xFF63746A)


internal enum class MapLook(val label: String, val detail: String) {
    FIELD_GUIDE("Cozy Woods", "Warm, leafy storybook map"),
    SURVEY_TABLE("Cool Sketch", "A mint-and-ink map");
    companion object {
        fun restore(name: String?): MapLook =
            entries.firstOrNull { it.name == name } ?: FIELD_GUIDE
    }
}


// GH-V27: quiet shared paper treatment. No image allocation or random frame
// noise; purely decorative, never participates in semantics or hit testing.
internal val JournalEdge = Color(0xFFD4C5A2)
internal val JournalPanelBorder = BorderStroke(1.dp, JournalEdge)

internal fun Modifier.journalPaper(): Modifier = drawBehind {
    drawRect(Brush.verticalGradient(listOf(Color(0xFFFCF7E9), Paper, Color(0xFFF1E7D0))))
    val spacing = 19.dp.toPx()
    val columns = (size.width / spacing).toInt().coerceIn(1, 80)
    val rows = (size.height / spacing).toInt().coerceIn(1, 120)
    for (row in 0 until rows) {
        for (column in 0 until columns) {
            val seed = row * 137 + column * 73
            val x = (column + .18f + (seed % 7) * .09f) * spacing
            val y = (row + .18f + (seed % 11) * .055f) * spacing
            drawCircle(Color(0x0B795C36), (.35f + (seed % 3) * .10f).dp.toPx(), Offset(x,y))
        }
    }
}

/** Shared action shape; circular map tools and route nodes remain distinct. */
internal val JournalActionShape = RoundedCornerShape(13.dp)

/** Explicit smaller text must not inherit the default 24sp body line height. */
internal val JournalTypography = Typography().let { defaults ->
    defaults.copy(
        bodyLarge = defaults.bodyLarge.copy(lineHeight = TextUnit.Unspecified, letterSpacing = 0.sp),
        bodyMedium = defaults.bodyMedium.copy(lineHeight = TextUnit.Unspecified, letterSpacing = 0.sp),
        bodySmall = defaults.bodySmall.copy(lineHeight = TextUnit.Unspecified, letterSpacing = 0.sp),
        labelLarge = defaults.labelLarge.copy(lineHeight = TextUnit.Unspecified, letterSpacing = 0.sp)
    )
}
