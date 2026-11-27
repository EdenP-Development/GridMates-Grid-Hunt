package com.playereden.gridmates.gridhunt

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import com.playereden.gridhunt.core.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class CoastalAdventureTest {
    @get:Rule val compose = createComposeRule()

    @Test fun completeThreeChaptersJournalResumeAndReplayWithoutWoodlandChanges() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val runs = (1..3).map(CoastalExpeditionPreview::chapter)
        val coastal = runs.map { context.getSharedPreferences(it.saveName,Context.MODE_PRIVATE) }
        val woodland = listOf(WoodlandExpedition.saveName,WoodlandSupplyExpedition.saveName,WoodlandReturnExpedition.saveName)
            .map { context.getSharedPreferences(it,Context.MODE_PRIVATE) }
        val original = woodland.map { it.all.toMap() }
        coastal.forEach { check(it.edit().clear().commit()) }
        val number = mutableStateOf(1)
        val visible = mutableStateOf(true)
        val look = mutableStateOf(MapLook.SURVEY_TABLE)
        compose.setContent {
            if (visible.value) WoodlandMixedExpeditionScreen(
                onHome = { visible.value=false },
                mapLook = look.value, onLook = { look.value=it }, hapticsEnabled=false,
                coastalPreview=CoastalExpeditionPreview.chapter(number.value),
                onNextChapter=if (number.value<3) ({ number.value += 1 }) else null
            )
        }
        fun progress(chapter: Int, step: Int) {
            compose.waitUntil(10000) { coastal[chapter-1].getInt("step",-1)==step }
            compose.onNodeWithTag("mixedProgress").assertTextEquals("$step of 4 clues solved")
        }
        fun move(direction: String) {
            compose.onNodeWithContentDescription("Expedition move $direction").performClick()
        }
        fun pick(east: Int,north: Int) {
            repeat(6) { move("west") }
            repeat(6) { move("south") }
            repeat(east-42) { move("east") }
            repeat(north-51) { move("north") }
            compose.onNodeWithTag("mixedSquarePick").assertTextEquals("YOUR PICK  $east$north")
        }
        fun reopen(tag: String) {
            compose.onNodeWithTag(tag).performClick()
            compose.waitForIdle()
            compose.runOnIdle { visible.value=true }
        }
        try {
            progress(1,0)
            compose.onNodeWithTag("mixedGuide").performClick()
            compose.onNodeWithTag("coastalMapGuide").assertExists()
            compose.onNodeWithTag("dismissCoastalMapGuide").performClick()
            pick(42,51)
            compose.onNodeWithTag("mixedCheck").performClick()
            progress(1,0)
            runs.forEachIndexed { i,run ->
                val chapter=i+1
                progress(chapter,0)
                run.chapter.objectives.forEachIndexed { index, objective ->
                    when(objective.verb) {
                        ExpeditionVerb.FIND_SQUARE -> pick(objective.expectedCell.eastIndex,objective.expectedCell.northIndex)
                        ExpeditionVerb.READ_MARKER, ExpeditionVerb.LOCATE_LANDMARK ->
                            compose.onNodeWithTag("mixedCodeInput").performTextReplacement(objective.expectedCell.reference)
                        ExpeditionVerb.IDENTIFY_LANDMARK ->
                            compose.onNodeWithTag("mixedOption_"+objective.landmarkId).performClick()
                    }
                    compose.onNodeWithTag("mixedCheck").performClick()
                    progress(chapter,index+1)
                    if(index==0) {
                        reopen("mixedHome")
                        progress(chapter,1)
                        compose.onNodeWithTag("mixedJournal").performClick()
                        compose.onNodeWithText("WHISPERING COAST · CHAPTER $chapter").assertExists()
                        compose.onNodeWithTag("journalEntry_"+objective.id).assertExists()
                        compose.onNodeWithTag("journalCurrent").assertExists()
                        compose.onNodeWithTag("journalEntry_"+run.chapter.objectives.last().id).assertDoesNotExist()
                        compose.onNodeWithTag("closeJournal").performClick()
                        if(chapter==1) compose.runOnIdle { look.value=MapLook.values().first { it!=MapLook.SURVEY_TABLE } }
                    }
                }
                compose.onNodeWithText(run.chapter.name+" complete!").assertExists()
                compose.onNodeWithTag("finishJournal").performClick()
                compose.onNodeWithTag("journalComplete").assertExists()
                compose.onNodeWithTag("closeJournal").performClick()
                if(chapter<3) compose.onNodeWithTag("nextExpedition").performClick()
            }
            assertTrue(coastal.all { it.getInt("step",-1)==4 })
            reopen("finishHome")
            progress(3,4)
            compose.onNodeWithTag("finishContinue").performClick()
            progress(3,0)
            assertEquals(4,coastal[0].getInt("step",-1))
            assertEquals(4,coastal[1].getInt("step",-1))
            assertEquals(original,woodland.map { it.all })
        } finally {
            compose.runOnIdle { visible.value=false }
            coastal.forEach { check(it.edit().clear().commit()) }
        }
    }
}
