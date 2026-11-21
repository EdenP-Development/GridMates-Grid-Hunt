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
