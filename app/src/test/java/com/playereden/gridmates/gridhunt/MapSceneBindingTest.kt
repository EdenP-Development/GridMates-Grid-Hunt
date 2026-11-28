package com.playereden.gridmates.gridhunt

import com.playereden.gridhunt.core.ExplorationMaps
import com.playereden.gridhunt.core.WorldBounds
import org.junit.Assert.assertThrows
import org.junit.Test

class MapSceneBindingTest {
    private val woodland = ExplorationMaps.woodland

    @Test fun exactSceneMapAndBoundsAccepted() {
        requireSceneMap(woodland, woodland, woodland.bounds)
    }

    @Test fun wrongMapIdentityRevisionAndBoundsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            requireSceneMap(woodland, woodland.copy(id = "unpublished-test-map"), woodland.bounds)
        }
        assertThrows(IllegalArgumentException::class.java) {
            requireSceneMap(woodland, woodland.copy(revision = woodland.revision + 1), woodland.bounds)
        }
        assertThrows(IllegalArgumentException::class.java) {
            requireSceneMap(woodland, woodland, WorldBounds(42000.0, 51000.0, 48000.0, 57000.0))
        }
    }
}
