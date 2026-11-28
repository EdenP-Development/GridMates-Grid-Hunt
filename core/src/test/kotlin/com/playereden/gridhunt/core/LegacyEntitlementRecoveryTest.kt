package com.playereden.gridhunt.core

import org.junit.Assert.*
import org.junit.Test

class LegacyEntitlementRecoveryTest {
    private val registry = GridHuntCampaign.registry
    private val first = WoodlandExpedition.chapter.id
    private val second = WoodlandSupplyExpedition.chapter.id
    private val third = WoodlandReturnExpedition.chapter.id
    private val recovery = LegacyEntitlementRecovery(registry,setOf(second,third))

    @Test fun downstreamProgressSuggestsHistoricalPrerequisitesButNotOwnCompletion() {
        val snapshot = CampaignSnapshot(mapOf(first to 1,second to 2,third to 0))
        val result = recovery.propose(snapshot)
        assertEquals(setOf(first),result.inferredPrerequisiteIds)
        assertTrue(result.explicitEarnedIds.isEmpty())
        assertEquals(1,snapshot.rawStepsByChapterId[first])
        assertFalse(second in result.inferredPrerequisiteIds)
    }

    @Test fun thirdChapterProgressRecoversEarlierEntitlements() {
        val result = recovery.propose(CampaignSnapshot(mapOf(first to 0,second to 0,third to 1)))
        assertEquals(setOf(first,second),result.inferredPrerequisiteIds)
    }

    @Test fun zeroUnknownAndMalformedStepsAreNotEvidence() {
        val result = recovery.propose(CampaignSnapshot(mapOf(second to -1,third to 5,"unknown" to 4)))
        assertEquals(setOf(second,third),result.invalidStepChapterIds)
        assertTrue(result.inferredPrerequisiteIds.isEmpty())
        assertTrue(recovery.propose(CampaignSnapshot()).inferredPrerequisiteIds.isEmpty())
    }

    @Test fun explicitEarnedRecordsRemainSeparateAndUnknownKeysAreIgnored() {
        val result = recovery.propose(CampaignSnapshot(earnedChapterIds=setOf(first,third,"unknown")))
        assertEquals(setOf(first,third),result.explicitEarnedIds)
        assertEquals(setOf(second),result.inferredPrerequisiteIds)
    }

    @Test fun unauditedEntryPathsCannotInferAccess() {
        val result = LegacyEntitlementRecovery(registry,emptySet())
            .propose(CampaignSnapshot(mapOf(third to 2)))
        assertTrue(result.inferredPrerequisiteIds.isEmpty())
    }
}
