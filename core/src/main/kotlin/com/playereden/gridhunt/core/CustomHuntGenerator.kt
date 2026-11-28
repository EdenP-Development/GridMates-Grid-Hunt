package com.playereden.gridhunt.core

import kotlin.random.Random

/** Stable Woodland v1 compatibility facade. Existing Android callers and saves are unchanged. */
object CustomHuntGenerator {
    data class Objective(val type: String, val cell: GridCell)
    private val engine = MapHuntGenerator(ExplorationMaps.woodland)
    val lengths get() = engine.lengths
    val landmarkLengths get() = engine.landmarkLengths
    val mixedLengths get() = engine.mixedLengths

    fun generate(count: Int, random: Random = Random.Default) = engine.generate(count, random)
    fun generateLandmarks(random: Random = Random.Default) = engine.generateLandmarks(random)
    fun generateMixed(count: Int, random: Random = Random.Default) = engine.generateMixed(count, random)
    fun generateFresh(count: Int, previous: Set<GridCell>, random: Random = Random.Default) =
        engine.generateFresh(count, previous, random)
    fun generateFreshLandmarks(previous: Set<GridCell>, random: Random = Random.Default) =
        engine.generateFreshLandmarks(previous, random)
    fun generateFreshMixed(count: Int, previous: Set<GridCell>, random: Random = Random.Default) =
        engine.generateFreshMixed(count, previous, random)
    fun encode(targets: List<GridCell>) = engine.encode(targets)
    fun restore(encoded: String) = engine.restore(encoded)
    fun restoreLandmarks(encoded: String) = engine.restoreLandmarks(encoded)
    fun restoreMixed(codes: String, types: String) = engine.restoreMixed(codes, types)
}
