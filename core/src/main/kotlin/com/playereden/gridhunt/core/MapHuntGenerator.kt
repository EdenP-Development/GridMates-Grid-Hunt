package com.playereden.gridhunt.core

import kotlin.random.Random

/** Map-scoped generation and validation; no Android or persistence dependencies. */
class MapHuntGenerator(val map: ExplorationMap) {
    val lengths = listOf(3, 6, 9).filter { it <= map.cells.size }
    private val cells = map.cells
    private val byReference = cells.associateBy { it.reference }

    val landmarkLengths = listOf(3).filter { it <= map.landmarks.size }
    fun generateLandmarks(random: Random = Random.Default): List<GridCell> {
        require(3 in landmarkLengths)
        return map.landmarks.map { it.cell }.distinct().shuffled(random).take(3)
    }

    fun restoreLandmarks(encoded: String): List<GridCell> {
        val targets = restore(encoded)
        val valid = map.landmarks.map { it.cell }.toSet()
        return targets.takeIf { it.size in landmarkLengths && it.all { cell -> cell in valid } } ?: emptyList()
    }

    val mixedLengths = listOf(6, 9).filter { it <= cells.size && map.landmarks.size >= 2 }
    private val mixedTypes = listOf("grid", "gold", "identify", "checklist")

    fun generateMixed(count: Int, random: Random = Random.Default): List<CustomHuntGenerator.Objective> {
        require(count in mixedLengths)
        val types = (mixedTypes + List(count - mixedTypes.size) { if (it % 2 == 0) "grid" else "gold" }).shuffled(random)
        val landmarks = map.landmarks.map { it.cell }.distinct().shuffled(random).take(2)
        val ordinary = cells.filter { it !in landmarks }.shuffled(random).iterator()
        val landmarkIterator = landmarks.iterator()
        return types.map { type ->
            CustomHuntGenerator.Objective(type, if (type == "identify" || type == "checklist") landmarkIterator.next() else ordinary.next())
        }
    }

    fun restoreMixed(codes: String, types: String): List<CustomHuntGenerator.Objective> {
        val targets = restore(codes)
        val kinds = types.split(",")
        if (targets.size !in mixedLengths || kinds.size != targets.size || kinds.any { it !in mixedTypes } ||
            !kinds.containsAll(mixedTypes)) return emptyList()
        val landmarks = map.landmarks.map { it.cell }.toSet()
        if (kinds.zip(targets).any { (kind, cell) -> kind in listOf("identify", "checklist") && cell !in landmarks }) return emptyList()
        return kinds.zip(targets).map { (kind, cell) -> CustomHuntGenerator.Objective(kind, cell) }
    }

    fun generate(count: Int, random: Random = Random.Default): List<GridCell> {
        require(count in lengths)
        return cells.shuffled(random).take(count)
    }

    /**
     * Prefer cells not present in the immediately preceding hunt. If the pool
     * is small, reuse only as many as necessary. This affects new sessions
     * only; encoded sessions continue to restore byte-for-byte in their order.
     */
    private fun freshOrder(pool: List<GridCell>, previous: Set<GridCell>, random: Random): List<GridCell> =
        pool.filterNot { it in previous }.shuffled(random) +
            pool.filter { it in previous }.shuffled(random)

    fun generateFresh(count: Int, previous: Set<GridCell>, random: Random = Random.Default): List<GridCell> {
        require(count in lengths)
        return freshOrder(cells, previous, random).take(count)
    }

    fun generateFreshLandmarks(previous: Set<GridCell>, random: Random = Random.Default): List<GridCell> {
        require(3 in landmarkLengths)
        return freshOrder(map.landmarks.map { it.cell }.distinct(), previous, random).take(3)
    }

    fun generateFreshMixed(count: Int, previous: Set<GridCell>, random: Random = Random.Default): List<CustomHuntGenerator.Objective> {
        require(count in mixedLengths)
        val types = (mixedTypes + List(count - mixedTypes.size) { if (it % 2 == 0) "grid" else "gold" }).shuffled(random)
        val landmarks = freshOrder(map.landmarks.map { it.cell }.distinct(), previous, random).take(2)
        val ordinary = freshOrder(cells.filterNot { it in landmarks }, previous, random).iterator()
        val landmarkIterator = landmarks.iterator()
        return types.map { type ->
            CustomHuntGenerator.Objective(type, if (type == "identify" || type == "checklist") landmarkIterator.next() else ordinary.next())
        }
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
