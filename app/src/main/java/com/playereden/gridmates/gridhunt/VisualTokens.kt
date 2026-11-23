package com.playereden.gridmates.gridhunt

import androidx.compose.ui.graphics.Color

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

