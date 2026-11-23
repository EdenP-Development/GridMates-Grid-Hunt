# GridMates: Grid Hunt — Master Game Design

**Version:** original design baseline 0.1 (2026-11-21), with a new expansion plan as of 2026-11-23.  
**Status:** Historical design foundation; some simple modes are implemented, while additional modes remain proposed. **For the owner's current vision, implementation order, and chat handoff use [Gameplay, Campaign & UI Blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md).** That blueprint adds object checklists, identify-by-grid puzzles, mixed chapters, Custom/Quick Play and native-game UI guidance. It supersedes illustrative chapter/mode examples here where they differ; it does not claim those future modes are built.

**Current checked prototype:** [QA85 CI green](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45261941012), 9 authored Trail Treasures square finds and 5 Golden Spots reverse references in draft [PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12). QA85 phone acceptance and production release remain unverified.

## Product pillars

1. **Puzzle first:** short, replayable, satisfying hunts; learning is embedded rather than lecture-driven.
2. **Real navigation habits:** right/eastings then up/northings; careful interpolation and reference precision.
3. **Clarity at phone scale:** the map is the hero, with pinch-to-zoom, pan, visible north indicator and unobtrusive target list.
4. **Multiple ways to search:** plotting, reverse reading, map interpretation and eventually chained missions.
5. **Honest progress:** skills are introduced gradually; no false precision or arbitrary difficulty penalties.
6. **GridMates DNA:** warm puzzle-book modernity, offline solo, accessible controls, respectful rewards and optional social play much later.

## Core play loop

Choose chapter or Free Hunt -> read a short brief and objectives -> inspect illustrated map -> plot/identify each objective -> review/submit -> see correct/incorrect with helpful explanation -> earn completion and move on.

Required objectives vary by mode. A board contains additional plausible landmarks, *not* only the solutions. The list should remain visible or accessible without obscuring the map.

## Search modes

| Mode | Player receives | Player performs | Intro milestone |
|---|---|---|---|
| **Coordinate Hunt** | 4-digit references then 6-digit references | Select correct map squares/sub-squares | GH-01/GH-03 |
| **Reverse Hunt** | Highlighted landmark(s) | Type or assemble matching grid reference | GH-03 |
| **Landmark Hunt** | Map legend and unambiguous landmark clues | Find the landmark and optionally state its grid reference | GH-04 |
| **Route Hunt** | Start + simple stepwise direction/distance clues | Plot successive points and goal | Future |
| **Precision Hunt** | Finer references on appropriately zoomable maps | Interpolate within a 1 km square | GH-03 (6 digits), future 8-digit |
| **Expedition Hunt** | Linked brief with varied objectives | Complete sequential mixed stages | GH-04 |

**One engine, different objective validators.** Do not build separate incompatible board engines for each mode.

## Illustrative chapter arc (proposed)

- **1. First Bearings — Woodland:** finding grid lines and four-digit 1 km squares.
- **2. Look Again — Countryside:** reverse plotting and correct easting/northing reading.
- **3. Clues on the Map — Lakeside:** symbols, legend and landmark distractors.
- **4. Finer Details — Highlands:** six-digit 100 m interpolation and zoom.
- **5. Across the Map — Mixed Landscapes:** combined objective sequences and optional simple directions.
- **6. The Expedition — Mixed Challenges:** multi-stage puzzle hunts without mandatory countdowns.

Prefer a **small polished pilot**, e.g. 8–12 chapter puzzles across the first two skills, before promising 100+ chapters. Numbers are proposals, not commitments.

## Difficulty is multi-dimensional

- Beginner: 3–4 targets, clear 1 km squares, full helpful labels and hints.
- Easy: 4–6 four-digit targets and more visually similar distractors.
- Normal: 4–7 targets, some reverse plot and six-digit 100 m sub-squares after instruction.
- Hard: 6–10 targets, richer features and multi-step tasks; six-digit plotting remains fair.
- Expert: mixed precision, reasoning and harder interpolation. Eight-digit 10 m challenges only if map detail, control magnification and true coordinate accuracy are demonstrably adequate.

**Precision is not difficulty by itself:** never substitute unreadably tiny labels or impossible finger targeting for skill.

## Player UX

- Map fills most of screen; compact objective ribbon that can collapse.
- North arrow, grid labels, scale/context and legend stay reachable.
- Plotting uses distinct **inspect/pan** and **place marker** behavior (or a forgiving gesture model) to prevent accidental taps while moving.
- Selected target clearly indicated; removable/adjustable pins, optional magnifier and undo.
- Progressive hints: remind reading order -> emphasize appropriate easting/northing lines -> guide a sub-square, with assistance recorded fairly.
- Results show which *area* the reference designates, why a response passed/failed, and the landmark if applicable.
- Large-text, color-blind-safe markers, haptic/sound toggles and touch accessibility are part of quality, not decorations.

## Content modes

- **Chapters:** fixed, curated puzzles for teaching and story-lite exploration.
- **Free Hunt:** seeded selection of valid objectives from approved map/landmark data; optional map, difficulty, mode, 3–10 targets.
- **Daily (later):** deterministic date seed and separate record with no compulsory streak.
- **Nearby co-op (later):** only when solo fully works and real two-phone testing is feasible.

## Retention without bloat

Optional stars/medals and a modest completion journal; collect simple map badges or cosmetic stamps later. Avoid launch-blocking economies, ads during play, multiplayer dependency and overbuilt profile systems.

## Explicit non-goals for v1

Real-time GPS navigation, GPS-precise outdoor use, actual operational map coordinates, combat, huge terrain simulation, 3D scenery, aerial imagery download, authoritative military qualification and 10-digit precision scoring.

## Core user story

“Given a clean fictional woodland map, I can select the correct square for a 4-digit reference, understand why it is correct, and eventually locate 100 m sub-squares and read coordinates from landmarks—without needing to study a manual first.”

## Core gameplay mechanics research note — 2026-11-21

[GM01](research/GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md) proposes **but does not finalize** a recoverable three-target Find & Mark proof, independent objective order, whole-cell validation, no compulsory timer/lives, and an A/B comparison against a small Discovery Hunt payoff. Clue Chains are deferred until prerequisites are understood. Different objective rules, score/hint economy and Custom Hunt constraints belong to the separate [GM research track](research/GAMEPLAY_MECHANICS_RESEARCH_TRACK.md). No playable implementation or user preference finding exists.


## GM05 chapter progression research (2026-11-21)

The [GM05 source-backed report](research/GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md) preserves the six themed **illustrative** chapter names above while proposing a *spiral* sequencing method: introduce one skill, use short contextual help, offer independent fresh examples, revisit earlier skills in later chapters, and reserve mixed finales for already-learned operations. Completion and novel-map retention are distinct; paper-map transfer is yet another proposed measurement. Research only: **no actual chapter levels, practice scheduler or transfer study built**. Next GM06.


## GM06 story and discovery research note (2026-11-21)

[GM06 micro-mission narrative report](research/GM06_MISSION_THEMES_AND_ENVIRONMENTAL_STORYTELLING.md) recommends **prototyping** one-sentence map-grounded mission purposes, small optional discovery/stamp rewards, and a short cohesive end card while preserving the existing no-story puzzle baseline. Story wording must not give away canonical grid answers or invent geography. No journal, narrative assets, level content or player study has been implemented; user approval remains open. **Next GM07 Quick/Custom Hunt choice research.**


## GM07 Quick vs Custom Hunt research note — 2026-11-21

[GM07 report](research/GM07_QUICK_AND_CUSTOM_HUNT_PLAYER_CHOICE_RESEARCH.md) proposes the same objective-validator engine for **Quick Hunt** (one-action, short, supported familiar tasks) and **Custom Hunt** (Mode, Length, Map, Challenge; optional more settings). Explicit Custom constraints should never be silently weakened when a combination is invalid. This is untested research, **not** an implemented menu or proof of preferred defaults. Next GM08 formal gameplay evaluation.


## GM08 research closure — planned GH-01 game proof (2026-11-22)

[GM08 evaluation plan](research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md) and [phone session script](research/GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md) complete the eight-topic *research* track; not a functional build. Four independent checks: math/content truth, human usability, desire to replay and genuine use of map-reading reasoning. Recommended small first GH-01 Android proof: one map, three four-digit tasks, accurate area selection, undo/confirm, helpful feedback, actual owner phone test. **No code, APK, UI regression run or user observation has been done in this research task.**
