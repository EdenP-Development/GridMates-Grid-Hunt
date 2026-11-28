package com.playereden.gridmates.gridhunt

import android.content.SharedPreferences

// Preserve the established save schema. Wrong-typed values are treated as
// absent at read time; do not clear unrelated keys or entire save files.
internal fun SharedPreferences.savedInt(key: String, fallback: Int): Int =
    try { getInt(key, fallback) } catch (_: ClassCastException) { fallback }

internal fun SharedPreferences.savedBoolean(key: String, fallback: Boolean): Boolean =
    try { getBoolean(key, fallback) } catch (_: ClassCastException) { fallback }

internal fun SharedPreferences.savedString(key: String, fallback: String?): String? =
    try { getString(key, fallback) } catch (_: ClassCastException) { fallback }

internal fun SharedPreferences.savedStringSet(key: String, fallback: Set<String>): Set<String> =
    try { getStringSet(key, fallback)?.filterIsInstance<String>()?.toSet() ?: fallback.toSet() }
    catch (_: ClassCastException) { fallback.toSet() }
