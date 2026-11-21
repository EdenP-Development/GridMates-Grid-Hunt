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