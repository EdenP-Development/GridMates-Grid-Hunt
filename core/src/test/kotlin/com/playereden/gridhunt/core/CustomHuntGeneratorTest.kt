package com.playereden.gridhunt.core

import kotlin.random.Random
import org.junit.Assert.*
import org.junit.Test

class CustomHuntGeneratorTest {
    @Test fun supportedLengthsAreUniqueAndValidAcrossSeeds() {
        for (seed in 0..99) for (count in CustomHuntGenerator.lengths) {
            val targets = CustomHuntGenerator.generate(count, Random(seed))
            assertEquals(count, targets.size)
            assertEquals(count, targets.toSet().size)
            assertTrue(targets.all { it.eastIndex in 24..29 && it.northIndex in 37..42 })
            assertEquals(targets, CustomHuntGenerator.restore(CustomHuntGenerator.encode(targets)))
            assertEquals(targets, CustomHuntGenerator.generate(count, Random(seed)))
        }
    }

    @Test fun invalidSessionsAreRejectedWithoutChangingTargetOrder() {
        for (bad in listOf("", "2437", "2437,2437,2438", "2437,3040,2538",
            "2437,invalid,2538", "2437,2438,2538,", "02437,2438,2538")) {
            assertTrue(bad, CustomHuntGenerator.restore(bad).isEmpty())
        }
        assertEquals(listOf("2942","2437","2640"),
            CustomHuntGenerator.restore("2942,2437,2640").map { it.reference })
        assertThrows(IllegalArgumentException::class.java) { CustomHuntGenerator.generate(4) }
    }
}
