# GridMates: Grid Hunt

**Status: GH-01 Android debug build passed CI — November 22, 2026.** The pure Kotlin coordinate engine and native Compose game code have compiled, **nine authored core JUnit tests ran**, and a debug-signed APK artifact was uploaded. **Physical Android installation, gameplay feel and accessibility remain untested by the owner.**

**Premise:** A welcoming, offline-first map-grid puzzle hunt that teaches transferable land-navigation habits through play. Find grid references on illustrated maps, reverse-read coordinates from landmarks, and eventually complete multi-step hunts. Fun first; technically honest coordinate rules underneath.

**Companion GridMates games:** [Sudoku](https://github.com/PlayerEden/SudokuTogether), [Word Search](https://github.com/PlayerEden/GridMates-Word-Search), [Crossroads](https://github.com/PlayerEden/GridMates-Crossroads). Grid Hunt is a **separate app**, repository, package and save space.

## Start here

1. [Project status and navigation](docs/START_HERE.md) — current truth, decisions, open choices and next action.
2. [Master game design](docs/MASTER_GAME_DESIGN.md) — gameplay, modes, difficulty, chapter structure and UX.
3. [Map-reading accuracy reference](docs/MAP_READING_ACCURACY.md) — grid semantics, examples, grading rules and educational boundaries.
4. [Maps, puzzles and generation](docs/PUZZLE_AND_CONTENT_SYSTEM.md) — hybrid fixed+generated content and deterministic validation.
5. [Technical architecture](docs/TECHNICAL_PLAN.md) — realistic Android build plan and data boundaries.
6. [Milestone roadmap](docs/ROADMAP.md) — incremental delivery with concrete acceptance gates.
7. [QA and playtest strategy](docs/QA_AND_PLAYTEST.md) — math, UI, device and APK safeguards.
8. [Other GridMates repository audit](docs/GRIDMATES_REUSE_AUDIT.md) — what to borrow and what not to copy.
9. [Research and sources](docs/RESEARCH_AND_SOURCES.md) — official references and unresolved research.

## Project rules

- **Puzzle before polish:** prove one map, one four-digit hunt and correct answer checking before expanding the art/catalogue.
- **Technique without deception:** distinguish grid *resolution* from real-world *accuracy*. Early fictional maps are training simulations, not operational MGRS geolocation.
- **No invented status:** DESIGN / IMPLEMENTED / AUTOMATED TESTED / DEVICE TESTED / RELEASED are different states.
- **Portable by design:** offline solo first, no required account, later nearby play only if worthwhile and independently tested.
- **Make the map usable:** readable labels, generous touch targets, zoom/pan, and an uncluttered play screen.
- **Respect separate apps:** no shared package identifiers, secrets, saves or assumed drop-in Sudoku/Word Search engine.

**Current implementation target:** verify the first GH-01 native Android proof-of-concept (one fixed fictional grid, three validated four-digit reference tasks). See [implementation status](docs/GH01_IMPLEMENTATION_STATUS.md) and [CI workflow](.github/workflows/gh01-android-qa.yml). **A GitHub commit alone is not a working APK or phone test.**

## Research library (2026-11-21)

- [Research library index](docs/research/README.md) — primary sources, historical studies, licensing, learning approach.
- [2010 UK military map-reading manual — annotated source review](docs/research/MILITARY_MAP_READING_2010_SOURCE_NOTES.md) — page-linked insights from the owner-provided 44-page reference.
- [Skill-to-puzzle matrix](docs/research/SKILL_TO_PUZZLE_MATRIX.md) — how real map-reading techniques become game objectives.

The full source PDF is **linked, not republished**, because it contains third-party Ordnance Survey maps requiring a separate rights check. No PDF figures or map images have been imported. The first GH-01 code has been added; narrative/art/gameplay expansions remain design proposals.


## Mapping technology research (2026-11-21)

The [mapping technical reference index](docs/research/MAPPING_TECHNICAL_REFERENCE_INDEX.md) collects official Android mapping/gesture docs, MapLibre, Mapsforge, Organic Maps, NGA GEOTRANS, GeographicLib, PROJ, offline map formats and licensing. Read the [technology recommendation](docs/research/MAPPING_TECH_SURVEY_2026-11-21.md) and [camera/coordinate design](docs/research/MAP_ENGINE_MATH_AND_VIEWPORT.md) before changing the proposed app architecture. **Research only: no mapping engine or APK has been implemented.**

- [Open Design Research Backlog](docs/research/OPEN_DESIGN_RESEARCH_BACKLOG.md) — prioritized questions and experiments for accessible game feel, grid-accurate map assets, randomized objectives, contour-line introduction and customizable hunts.

- [R01 — First 30 seconds beginner-experience study](docs/research/R01_FIRST_30_SECONDS_ONBOARDING_RESEARCH.md): focused source-backed research on learning-through-play and a first-square tutorial, awaiting actual playtesting.

- [R02 — Making the Hunt fun and replayable](docs/research/R02_GAME_FEEL_AND_REPLAYABILITY_RESEARCH.md): research-backed gameplay loop comparison, difficulty and feedback guidance; experimental until playable testing.

- [R03 — Beginner-friendly coordinate clues and grid labels](docs/research/R03_COORDINATE_PRESENTATION_AND_GRID_LABELS_RESEARCH.md): evidence and proposed tests for correct grid-line numbering, familiar clue format and gentle guidance.

- [R04 — Map creation and artwork options](docs/research/R04_MAP_CREATION_AND_ARTWORK_RESEARCH.md): compares code-drawn, full illustration and hybrid approaches, with an identical-data three-map test plan. Research-only; map prototypes and art approval remain open.

- [R05 — Accurate feature placement and sprite anchors](docs/research/R05_GRID_ACCURATE_FEATURE_GEOMETRY_AND_ANCHORS.md): source-backed point/line/area map model, object reference accuracy, bridges/rivers, and planned content integrity tests; unimplemented.

- [R06 — Mobile map touch and precision controls](docs/research/R06_MOBILE_MAP_INTERACTION_RESEARCH.md): official Android/W3C sources, four-digit tapping versus preview, precision magnifier/reticle, and planned gesture/phone QA; not built.

- [Gameplay mechanics research track (GM01–GM08)](docs/research/GAMEPLAY_MECHANICS_RESEARCH_TRACK.md) — actual puzzle rules, distinct challenge types, difficulty, rewards, practice and Custom Hunt research ahead of content generation.
- [GM01 — Core gameplay rules](docs/research/GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md) — source-backed Find & Mark/Discovery/Clue Chain rule comparisons, win/retry conditions and an **unrun** prototype test.

- [GM03 — Fair Clues and Puzzle Quality](docs/research/GM03_PUZZLE_QUALITY_AND_CLUE_DESIGN_RESEARCH.md): research into real deductions, puzzle uniqueness, difficulty, answer-leak prevention and future quality checks. **Research only.**

- [GM03 — Puzzle quality and fairness research](docs/research/GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md): constraints for solvable, fair, interesting grid puzzles before generating them; includes 20 planned tests, no code built.

- [GM04 — Help, mistakes, scoring and rewards](docs/research/GM04_HINTS_FEEDBACK_SCORING_REWARDS_RESEARCH.md): evidence-informed low-pressure puzzle feedback/hint/reward alternatives, with unexecuted A/B and QA plans.

- [GM05 — Chapters, spaced practice and skill transfer](docs/research/GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md): source-backed progression hypothesis and proposed separate gameplay/retention/transfer tests; no implemented levels.

- [GM06 — Environmental storytelling and mini-mission design](docs/research/GM06_MISSION_THEMES_AND_ENVIRONMENTAL_STORYTELLING.md): researched mission templates, narrative/map consistency requirements and unrun A/B player tests; no story systems implemented.

- [GM07 — Quick Hunt and Custom Hunt design research](docs/research/GM07_QUICK_AND_CUSTOM_HUNT_PLAYER_CHOICE_RESEARCH.md): progressive settings, user agency and strict setup compatibility proposals, with unrun testing plans.

- [GM08 — Playtest evidence and decision gates](docs/research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md): four separate evaluation domains, controlled tests, error taxonomy and source-matched phone QA plan. [First phone session sheet](docs/research/GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md) included. **Neither is executed.**


## GH-01 code & QA (2026-11-22)

- [GH-01 actual implementation status and limits](docs/GH01_IMPLEMENTATION_STATUS.md).
- [Android source](app/src/main/java/com/playereden/gridmates/gridhunt/MainActivity.kt), [pure Kotlin grid engine](core/src/main/kotlin/com/playereden/gridhunt/core/GridMath.kt) and [grid tests](core/src/test/kotlin/com/playereden/gridhunt/core/GridMathTest.kt).
- [GH-01 Android Actions workflow](.github/workflows/gh01-android-qa.yml): runs core JVM tests and, if successful, uploads a debug-signed QA APK with SHA/source/signer evidence. **The debug signing identity may change between runs**; don't claim install-over compatibility or production signing.


### First GH-01 APK (debug QA)

[Download APK archive from successful GitHub Actions run #3](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45028853252/artifacts/14756424559) · [Full build receipt (source SHA, APK hash, signing certificate)](docs/qa/GH01_QA_RUN_3.md). This is **debug-signed per GitHub runner** and may not install over a later differently signed QA build; no physical phone testing yet.

- [GH-01 unattended QA pass #4](docs/qa/GH01_QA_RUN_4.md) — 11 core unit tests, camera/rotation state and 48dp control improvements, successful Android build, verified artifact, and outstanding physical-device validation. **New debug signer differs from #3; uninstall older QA before installing.**

- **Owner phone feedback (2026-11-22):** QA4 proved the three-square hunt completable, but dragging/pinching were unreliable. [Issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10) is open; [QA6 candidate gesture fix](docs/qa/GH01_QA_RUN_6_GESTURE_FIX.md) compiled successfully and needs a second **physical phone test**. [Download QA6 APK ZIP from GitHub](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45036299405/artifacts/14760210702).

- **GH-02A map-first QA8 (2026-11-22):** [QA report](docs/qa/GH02A_MAP_FIRST_QA8.md), [successful Android build](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45053066515), and [signed debug APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45053066515/artifacts/14766263499). Compact target ribbon, flexible map and landscape side panel are built. **Phone screenshot/interaction acceptance pending**; debug signature changed from QA6.
