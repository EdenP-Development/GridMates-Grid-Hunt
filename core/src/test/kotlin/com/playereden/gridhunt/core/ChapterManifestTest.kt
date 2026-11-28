package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class ChapterManifestTest {
    private val chapter = WoodlandExpedition.chapter
    private val original = ChapterManifest.fingerprint(chapter)

    @Test fun deterministicAcrossEquivalentCopies() {
        assertTrue(original.matches(Regex("[0-9a-f]{64}")))
        assertEquals(original,ChapterManifest.fingerprint(chapter.copy(
            objectives=chapter.objectives.map { it.copy() })))
    }

    @Test fun revisionOrderAndAnswerChangesAlterIdentity() {
        assertNotEquals(original,ChapterManifest.fingerprint(chapter.copy(revision=2)))
        assertNotEquals(original,ChapterManifest.fingerprint(chapter.copy(
            objectives=chapter.objectives.reversed())))
        val first = chapter.objectives.first()
        val changed = first.copy(expectedCell=GridReference.parse("2537"))
        assertNotEquals(original,ChapterManifest.fingerprint(chapter.copy(
            objectives=listOf(changed)+chapter.objectives.drop(1))))
    }

    @Test fun textAndOtherChapterChangeIdentity() {
        val changed = chapter.objectives.first().copy(prompt="A revised clue")
        assertNotEquals(original,ChapterManifest.fingerprint(chapter.copy(
            objectives=listOf(changed)+chapter.objectives.drop(1))))
        assertNotEquals(original,ChapterManifest.fingerprint(WoodlandSupplyExpedition.chapter))
    }

    @Test fun fieldBoundariesAreUnambiguous() {
        val a = chapter.copy(name="ab",story="c")
        val b = chapter.copy(name="a",story="bc")
        assertNotEquals(ChapterManifest.fingerprint(a),ChapterManifest.fingerprint(b))
    }
}
