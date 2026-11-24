package com.playereden.gridhunt.core

import kotlin.random.Random

/** Version 1 woodland session contract. Stable grid references, independent of artwork. */
object CustomHuntGenerator {
    val lengths = listOf(3, 6, 9)
    private val cells = (24..29).flatMap { e -> (37..42).map { n -> GridCell(e, n) } }
    private val byReference = cells.associateBy { it.reference }

    val landmarkLengths = listOf(3)
    fun generateLandmarks(random: Random = Random.Default): List<GridCell> =
        WoodlandLandmarks.all.map { it.cell }.distinct().shuffled(random).take(3)

    fun restoreLandmarks(encoded: String): List<GridCell> {
        val targets = restore(encoded)
        val valid = WoodlandLandmarks.all.map { it.cell }.toSet()
        return targets.takeIf { it.size in landmarkLengths && it.all { cell -> cell in valid } } ?: emptyList()
    }

    fun generate(count: Int, random: Random = Random.Default): List<GridCell> {
        require(count in lengths)
        return cells.shuffled(random).take(count)
    }

    fun encode(targets: List<GridCell>): String {
        require(targets.size in lengths && targets.distinct().size == targets.size)
        require(targets.all { byReference[it.reference] == it })
        return targets.joinToString(",") { it.reference }
    }

    /** Reject a malformed session as a whole; never shift saved progress by dropping targets. */
    fun restore(encoded: String): List<GridCell> {
        val refs = encoded.split(",")
        if (refs.size !in lengths || refs.distinct().size != refs.size) return emptyList()
        return refs.map { byReference[it] ?: return emptyList() }
    }
}
