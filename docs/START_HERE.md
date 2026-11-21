# START HERE — GridMates: Grid Hunt

**As of:** 2026-11-21  
**Status:** DESIGN ONLY. The initial repo has been organized; there is no implemented app or APK.  
**Source of truth:** this document + [master design](MASTER_GAME_DESIGN.md) + [roadmap](ROADMAP.md).

## Product in one sentence

A casual grid-map puzzle hunt that quietly teaches real eastings/northings, grid reference precision and map interpretation, progressing from finding a 1 km square to reading and plotting smaller squares and combining skills.

## Decisions carried from owner discussions

- Separate **GridMates: Grid Hunt** Android app, in its own repository.
- A puzzle-game experience, **not** a combat game or a high-fidelity land-nav simulator.
- Simplified, appealing illustrated maps are welcome; authentic coordinate **techniques** are nonnegotiable.
- Main loop: locate given grid references among many landmarks/distractors; also reverse-read coordinates from selected features.
- Difficulty primarily means **more precise plotting and more complicated tasks**, not merely tiny squares or more taps.
- Chapters introduce skills, cycle through modes, and culminate in mixed missions.
- Use **both** crafted chapter puzzles and reusable maps with seeded/randomized valid objectives.
- Solo, lightweight, touch-first, offline-capable foundation. Nearby multiplayer, complicated terrain and elaborate cosmetic systems are later possibilities.

## Practical boundaries

**Build first:** 1 map, simple four-digit square hunt, precise grid labeling, native pan/zoom, 3–5 objectives, feedback, repeatable test puzzles.

**Then:** reverse hunt, six-digit 100 m sub-square plotting, legend/landmark clues, seeded free hunt, chapter progression.

**Defer:** 8/10-digit play, georeferenced real-world map support, azimuth/distance route missions, online services, LAN, marketplace cosmetics, protractor and elaborate procedural terrain.

## Status language

- **DECIDED:** owner-established direction above.
- **PROPOSED:** implementation detail, numeric tuning or UI still to playtest.
- **IMPLEMENTED:** source exists, verified in correct branch/commit.
- **AUTOMATED TESTED:** named test/CI actually executed with results.
- **DEVICE TESTED:** human verified on physical Android device.
- **RELEASED:** actual distributable package and audience documented.

Do not claim a feature is built because it appears in design documentation.

## Current prioritized steps

1. Implement and unit-test [grid rules](MAP_READING_ACCURACY.md) in a pure Kotlin module.
2. Show a compact, smooth map viewport with correct transforms and north-up labeling.
3. Deliver the GH-01 end-to-end sample puzzle and source-matched QA build.
4. Collect actual phone feedback before more maps, collectibles, multiplayer or new modes.

## Decisions to resolve after prototype

- Final player-facing title, logo and accent art (working name is Grid Hunt).
- Tutorial pacing, hints/assistance scoring and chapter length.
- Whether fully-qualified real-world MGRS map packs add enough benefit to justify geospatial data work.
- How much of the optional GridMates profile/collection system is worth porting for this game's first release.

**Important distinction:** early training-grid references behave like the numeric portion of MGRS but do not claim a valid location on Earth. A full MGRS implementation must supply a genuine grid-zone designation and 100 km square identifier tied to a real coordinate frame.

## Research foundation (2026-11-21)

The owner supplied *Military Map Reading* (Royal School of Military Survey, UK, 2010). It has been studied and documented in the [research index](research/README.md), [source notes](research/MILITARY_MAP_READING_2010_SOURCE_NOTES.md), and [skill-to-puzzle matrix](research/SKILL_TO_PUZZLE_MATRIX.md). Use these to justify future chapters and identify what should be tested. Since it is a historic British manual, official modern NGA/USGS/FGDC sources take precedence for current geospatial conventions. The PDF itself is **not stored in the public repo** because embedded OS mapping rights require review.


## Current engineering recommendation (research addendum, 2026-11-21)

Build our own small **meter-based training-grid math + Compose map viewport**, not a full GPS/OSM engine. The [technology survey](research/MAPPING_TECH_SURVEY_2026-11-21.md), [camera equations](research/MAP_ENGINE_MATH_AND_VIEWPORT.md), [offline format rules](research/OFFLINE_MAP_FORMATS_LICENSES.md), and [test vectors](research/MAP_ENGINE_TEST_AND_SPIKE_PLAN.md) explain what to borrow from professional mapping apps and what to defer. The engineering study is a **documentation milestone only**.

## Active design research questions (2026-11-21)

Review [Open Design Questions and Research Backlog](research/OPEN_DESIGN_RESEARCH_BACKLOG.md) before treating illustration, code-drawn maps, terrain procedural generation, grid hit-testing, or Custom Hunt settings as locked. The priority is to test an approachable first-puzzle experience and compare a small number of viable map/art treatments before building an elaborate mapping engine.


## Focused research sessions

**R01 beginner first-30-seconds research completed as documentation on 2026-11-21.** Read [R01 source-backed report](research/R01_FIRST_30_SECONDS_ONBOARDING_RESEARCH.md) for proposed tutorial and playtest criteria; neither prototype nor real user trial has been executed. **Next research topic: R02 puzzle feel / replayable hunt.**


## Focused research session — R02, November 21, 2026

[R02 game-feel and replayability research](research/R02_GAME_FEEL_AND_REPLAYABILITY_RESEARCH.md) now compares Classic Find and Discovery Hunt using published player-motivation, difficulty and feedback evidence. The recommendation is a **controlled prototype comparison**, not an approved feature. No gameplay prototype or human trial has yet occurred. **Next research: R03 beginner grid-clue presentation.**


## Focused research session — R03, November 21, 2026

[R03 friendly coordinate presentation and grid labels](research/R03_COORDINATE_PRESENTATION_AND_GRID_LABELS_RESEARCH.md) now documents technical/UX source findings, genuine 4-digit area selection, temporary split-digit clues and a proposed 6-digit lesson. **Research only** — no tested clue UI or approval. **R04 map production comparisons are next.**


## Focused research session — R05, November 21, 2026

[R05 feature geometry, anchors and grid integrity](research/R05_GRID_ACCURATE_FEATURE_GEOMETRY_AND_ANCHORS.md) documents a source-backed, small local-meter point/line/polygon model, art anchor/reference rules, bridges at stream crossings, and boundary/placement test cases. **Research only; no engine, art, coordinate tests or phone prototype implemented.** Next R06 mobile map interaction research.


## Focused research session — R06, November 21, 2026

[R06 Android touch and precision selection research](research/R06_MOBILE_MAP_INTERACTION_RESEARCH.md) documents official Android gesture mechanics, preview-before-submit and precision selection hypotheses, W3C accessibility alternatives, and **unexecuted** touchscreen QA. No UI or game has been implemented. **Next research: R07 seeded puzzle and objective generation.**


## Current research priority — actual game mechanics (2026-11-21)

The owner asked to study *game rules, decisions and mechanics* more deeply instead of progressing immediately to seed generation. [Gameplay mechanics track](research/GAMEPLAY_MECHANICS_RESEARCH_TRACK.md) and [GM01 mechanics report](research/GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md) are now documented. The research compares a simple **Find & Mark** loop with **Discovery Hunt** and later **Clue Chains**, specifying actual state transitions and win/retry rules. **No gameplay or playtest implemented. NEXT: GM02 objective families; R07 procedural generation postponed, not canceled.**


## Focused mechanics research — GM03, November 21, 2026

[GM03 fair puzzle/clue design research](research/GM03_PUZZLE_QUALITY_AND_CLUE_DESIGN_RESEARCH.md) examines verified clue sufficiency, one well-defined answer, relevant distractors and human puzzle-quality tests. It recommends strict hard correctness gates before random content and **does not** claim those validators or playtests exist. **Next GM04: hints, scoring, mistakes and rewards.**


## Game mechanics research — GM03, November 21, 2026

[GM03 puzzle quality/fairness](research/GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md) is now documented alongside [GM02 objective mechanics](research/GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md). The study separates **hard validity gates** (clue sufficiency, unique or explicitly multi-answer targets, correct map-data semantics) from **soft quality** (meaningful reasoning, novelty, legibility and satisfaction). Research only: no quality checker, generator, gameplay, APK or playtest built. **GM04 feedback/hints/rewards next**; R07 generator remains later.


## GM04 research: help without punishment (2026-11-21)

[GM04 hints, feedback, scoring and rewards](research/GM04_HINTS_FEEDBACK_SCORING_REWARDS_RESEARCH.md) compares graduated on-demand hints, supportive error explanations, normal assisted completion and optional independent-practice stamps against more competitive alternatives. **Literature-informed hypotheses; no game, playtest or feature implementation. GM05 skill sequencing next.**
