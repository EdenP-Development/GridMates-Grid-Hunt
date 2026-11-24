package com.playereden.gridhunt.core

/** First playable Academy slice. Only published exercises count toward progress. */
data class AcademyExercise(
    val id: String, val lesson: String, val teaching: String,
    val prompt: String, val choices: List<String>, val answer: String,
    val explanation: String,
    val mapTask: AcademyMapTask? = null,
    val woodlandField: Boolean = false,
    val symbolId: String? = null,
    val route: AcademyRoute? = null
) {
    init {
        require(id.isNotBlank() && choices.size == 4 && choices.distinct().size == 4)
        require(answer in choices)
        if (woodlandField) require(mapTask == null && (route?.destination?.reference == answer || WoodlandLandmarks.all.any { it.cell.reference == answer }))
        if (symbolId != null) require(woodlandField && WoodlandLandmarks.byId(symbolId)?.cell?.reference == answer)
        if (route != null) require(woodlandField && route.destination.reference == answer)
        if (mapTask != null) require(answer == mapTask.target && AcademyTrainingGrid.contains(answer))
    }
}

data class AcademyMapTask(val target: String, val guided: Boolean, val selectSquare: Boolean)

/** Small north-up training map. Labels identify west and south boundaries. */
object AcademyTrainingGrid {
    val eastings = 24..27
    val northings = 37..40
    fun reference(east: Int, north: Int): String {
        require(east in eastings && north in northings)
        return GridCell(east, north).reference
    }
    fun contains(code: String): Boolean =
        eastings.any { e -> northings.any { n -> reference(e, n) == code } }
}

enum class AcademyDirection(val dx: Int, val dy: Int) {
    NORTH(0, 1), EAST(1, 0), SOUTH(0, -1), WEST(-1, 0)
}
data class AcademyRouteLeg(val direction: AcademyDirection, val squares: Int) {
    init { require(squares in 1..5) }
}
data class AcademyRoute(val start: GridCell, val legs: List<AcademyRouteLeg>) {
    val destination: GridCell
        get() {
            require(start.digits == 4 && legs.isNotEmpty())
            var e = start.eastIndex
            var n = start.northIndex
            require(e in 24..29 && n in 37..42)
            for (leg in legs) repeat(leg.squares) {
                e += leg.direction.dx
                n += leg.direction.dy
                require(e in 24..29 && n in 37..42) { "Route leaves woodland map" }
            }
            return GridCell(e, n)
        }
    init { destination }
}

object ExplorerAcademy {
    const val schoolId = "exploration-v1"
    const val saveName = "gridhunt_academy_exploration_v1"
    val exercises = listOf(
        AcademyExercise("north", "Finding your bearings",
            "On our north-up maps, north is up and east is right.",
            "Which direction is toward the right edge of a north-up map?",
            listOf("North", "West", "East", "South"), "East",
            "East is right on a north-up map; west is left."),
        AcademyExercise("south", "Finding your bearings",
            "The compass shows the four main directions. North and south are opposites.",
            "Which direction is opposite north?",
            listOf("South", "East", "West", "North"), "South",
            "South is down on a north-up map."),
        AcademyExercise("grid-order", "Cracking the grid",
            "A four-digit grid address names a square. Read its left boundary, then its bottom boundary.",
            "Read the highlighted campsite square. What is its address?",
            listOf("3926", "2639", "2739", "2640"), "2639",
            "Across first, then up: 26 followed by 39.",
            AcademyMapTask("2639", guided = true, selectSquare = false)),
        AcademyExercise("grid-practice", "Cracking the grid",
            "Always read the west and south boundaries, not the far edges.",
            "Tap square 2437 on the training map, then check your selection.",
            listOf("2438", "3724", "2537", "2437"), "2437",
            "24 across and 37 up gives 2437.",
            AcademyMapTask("2437", guided = false, selectSquare = true)),
        AcademyExercise("grid-field", "Field practice",
            "Find the left boundary first, then the bottom boundary. This time, try without highlighted lines.",
            "Tap square 2740, then check your selection.",
            listOf("2740", "2640", "2739", "2639"), "2740",
            "The square begins at 27 across and 40 up.",
            AcademyMapTask("2740", guided = false, selectSquare = true)),
        AcademyExercise("woodland-field", "Your first woodland map",
            "Use + to zoom in, drag to move around, and See all to return to the full map. The Map Guide explains the symbols.",
            "Find Mushroom Nook and select the square containing the mushrooms.",
            listOf("2538", "2641", "2840", "2941"), WoodlandLandmarks.mushrooms.cell.reference,
            "Mushroom Nook is in square 2538. Read its west and south grid boundaries.",
            woodlandField = true),
        AcademyExercise("woodland-symbol", "Reading the landscape",
            "A map symbol is a small picture representing a real feature. Match the sample to the map.",
            "Find the matching flower symbol and select its square.",
            listOf("2538", "2641", "2840", "2941"), WoodlandLandmarks.flowers.cell.reference,
            "The flowers mark Buttercup Glade, in square 2840.",
            woodlandField = true, symbolId = WoodlandLandmarks.flowers.id),
        AcademyExercise("woodland-route", "Following the trail",
            "Start at a known square. East increases the first two digits; north increases the last two.",
            "Start at 2538. Move two squares east, then one square north. Select your destination.",
            listOf("2739", "2639", "2738", "2541"), "2739",
            "Two east brings you to 2738; one north brings you to 2739.",
            woodlandField = true, route = AcademyRoute(GridReference.parse("2538"),
                listOf(AcademyRouteLeg(AcademyDirection.EAST, 2), AcademyRouteLeg(AcademyDirection.NORTH, 1))),
        AcademyExercise("woodland-deduction", "Thinking like an explorer",
            "Compare landmarks one condition at a time. East means farther right; north means farther up.",
            "Find the landmark east of Acorn Nook AND north of Buttercup Glade. Select its square.",
            listOf("2538", "2641", "2840", "2941"),
            WoodlandSpatialClues.resolve("academy-deduction", WoodlandLandmarks.waystone.id,
                listOf(ClueCondition.EastOf(WoodlandLandmarks.acorn.id),
                    ClueCondition.NorthOf(WoodlandLandmarks.flowers.id))).reference,
            "Only the Old Waystone satisfies both directions. Its square is 2941.",
            woodlandField = true)
    )
    fun restore(ids: Set<String>): Set<String> =
        ids.intersect(exercises.map { it.id }.toSet())
    fun current(ids: Set<String>): AcademyExercise? =
        exercises.firstOrNull { it.id !in restore(ids) }
    fun accept(ids: Set<String>, exerciseId: String, choice: String): Set<String> {
        val known = restore(ids)
        val active = current(known) ?: return known
        return if (active.id == exerciseId && active.answer == choice)
            known + active.id else known
    }
    fun practiceComplete(ids: Set<String>) = restore(ids).size == exercises.size
    // Graduation deliberately unavailable until the full foundational school
    // and independent field expedition are authored and verified.
}
