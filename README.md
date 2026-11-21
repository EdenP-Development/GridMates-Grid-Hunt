# GridMates: Grid Hunt

**Status: PRE-PRODUCTION / DESIGN ONLY — November 21, 2026.** This repository currently contains planning and research documents; it does **not** contain a playable Android game, validated puzzle engine, or APK.

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

**Next implementation target:** an isolated native Android proof-of-concept that renders one deterministic training grid and accepts/verifies three four-digit square selections. See [GH-01](docs/ROADMAP.md).

## Research library (2026-11-21)

- [Research library index](docs/research/README.md) — primary sources, historical studies, licensing, learning approach.
- [2010 UK military map-reading manual — annotated source review](docs/research/MILITARY_MAP_READING_2010_SOURCE_NOTES.md) — page-linked insights from the owner-provided 44-page reference.
- [Skill-to-puzzle matrix](docs/research/SKILL_TO_PUZZLE_MATRIX.md) — how real map-reading techniques become game objectives.

The full source PDF is **linked, not republished**, because it contains third-party Ordnance Survey maps requiring a separate rights check. No PDF figures or map images have been imported. The game remains in the **design** stage.


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
