package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SafeSaveReadsTest {
    private val prefs = InstrumentationRegistry.getInstrumentation().targetContext
        .getSharedPreferences("gridhunt_save_read_test_only", Context.MODE_PRIVATE)

    @Test fun missingValuesUseDefaults() {
        try {
            check(prefs.edit().clear().commit())
            assertEquals(0, prefs.savedInt("step", 0))
            assertEquals(true, prefs.savedBoolean("haptics", true))
            assertNull(prefs.savedString("selected", null))
            assertEquals(emptySet<String>(), prefs.savedStringSet("earned", emptySet()))
        } finally { prefs.edit().clear().commit() }
    }

    @Test fun wrongTypesDoNotCrashOrEraseOtherValues() {
        try {
            check(prefs.edit().clear().putString("step", "bad")
                .putInt("haptics", 1).putBoolean("selected", true)
                .putString("earned", "bad").putInt("other", 4).commit())
            val before = prefs.all.toMap()
            assertEquals(0, prefs.savedInt("step", 0))
            assertEquals(false, prefs.savedBoolean("haptics", false))
            assertNull(prefs.savedString("selected", null))
            assertEquals(emptySet<String>(), prefs.savedStringSet("earned", emptySet()))
            assertEquals(4, prefs.savedInt("other", 0))
            assertEquals(before, prefs.all.toMap())
        } finally { prefs.edit().clear().commit() }
    }

    @Test fun validValuesRemainExact() {
        try {
            check(prefs.edit().clear().putInt("step", 3).putBoolean("haptics", false)
                .putString("selected", "2437").putStringSet("earned", setOf("a", "b")).commit())
            assertEquals(3, prefs.savedInt("step", 0))
            assertEquals(false, prefs.savedBoolean("haptics", true))
            assertEquals("2437", prefs.savedString("selected", null))
            assertEquals(setOf("a", "b"), prefs.savedStringSet("earned", emptySet()))
        } finally { prefs.edit().clear().commit() }
    }
}
