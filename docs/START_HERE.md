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


## GM05 research — chapter pacing, practice and retention (2026-11-21)

[GM05 chapter progression](research/GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md) is now researched: keep the existing six-chapter theme skeleton, reintroduce earlier objective verbs during later themed hunts, fade mandatory training without removing optional help, and measure novel-map/paper-map retention separately from immediate completion. **Proposal only; no chapter content or follow-up study created. GM06 mission narrative research next.**


## GM06 research: story-light expeditions (2026-11-21)

[GM06 mission themes and environmental storytelling](research/GM06_MISSION_THEMES_AND_ENVIRONMENTAL_STORYTELLING.md) reviews map-driven storytelling, intrinsic gameplay integration and narrative distraction risks. Proposal: test a **one-sentence Mini Expedition** and optional discovery journal against a clean no-story puzzle, with the **same exact map and answers**. Research only: **no story system, illustrations, Android code or playtests built**. Next GM07 Quick vs Custom choices.


## GM07 research — instant Quick, reliable Custom (2026-11-21)

[GM07 Quick Hunt vs Custom Hunt research](research/GM07_QUICK_AND_CUSTOM_HUNT_PLAYER_CHOICE_RESEARCH.md) proposes one-action Quick using supported familiar puzzle types; Custom Hunt with Mode, Length, Map and Challenge upfront and secondary controls on request. Crucially, Custom must **explain incompatible setups instead of silently replacing explicit choices**. Source-backed options and 20 planned test cases only; **no generator, presets, configuration UI, code or phone test built**. **Next GM08 structured gameplay playtesting research.**


## All eight gameplay research topics documented — GM08, November 22, 2026

[GM08 playtesting methodology](research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md) and [one-page first Android session sheet](research/GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md) close the GM01–GM08 research track *as documentation*. Separate canonical correctness, novice usability, enjoyment/replay and observed grid skill; no human test, APK, Kotlin engine or UI has been produced. **Recommended next tangible milestone: GH-01 tiny offline Android four-digit hunt and source-matched signed APK for owner playtest**, before a large procedural map/mission system. R07 seeded-generator research remains open.


## GH-01 actual build: first QA APK ready, not phone-tested (2026-11-22)

The [GH-01 status](GH01_IMPLEMENTATION_STATUS.md) now links to [verified Android QA run #3 receipt](qa/GH01_QA_RUN_3.md). Source built at `6b65a1a`, `:core:test` and `:app:assembleDebug` succeeded, signed debug APK archived with checksum and cert. **Next: install and review on physical Android** before calling the prototype accepted or scaling maps. This is the first implemented gameplay code; it supersedes older statements in this file that the repo is docs-only.


## GH-01 unattended QA pass #4 (2026-11-22)

[QA run #4](qa/GH01_QA_RUN_4.md) passed `:core:test` (11 authored JUnit cases) and APK build. Camera re-fit after rotation and 48dp directional/zoom controls were improved in source. **Newest APK is debug QA run #4; run #3 may not install-over due to different signing certificates.** Still no emulator/device use or real player feedback. Continue with actual phone tests when practical; resist expanding maps or game modes beforehand.


## First owner QA feedback and QA6 candidate fix (2026-11-22)

Owner actually installed QA4 and completed three grid objectives but found dragging and pinch zoom unreliable, and screenshots showed small map/overlapping labels. [Issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10) remains open. [QA6 Android receipt](qa/GH01_QA_RUN_6_GESTURE_FIX.md) documents fresh touch-input arbitration, camera-state fixes, axis label improvements, passing CI and checksum-verified debug APK. **Owner QA6 phone retest is next, not story mode or content expansion.** Note different debug signature may require uninstall of QA4.


## GH-02A — Map-first Android layout QA8 ready (2026-11-22)

[GH-02A build receipt](qa/GH02A_MAP_FIRST_QA8.md) records source `f9c4f8e`, QA run #8 SUCCESS, compact objective ribbon, map-centered portrait design and landscape side panel. APK SHA-256 and debug signer were verified from artifact archive. **This version hasn't been visually or gesture-tested by the owner**; ask for screenshots/feedback before extending gameplay. Next suggested slice if accepted: GH-02B First Bearings curated chapter, onboarding and reliable progress/resume.


## GH-02B chapter QA10 awaiting phone test (2026-11-22)

[QA10 build/verification report](qa/GH02B_FIRST_BEARINGS_CHAPTER_QA10.md): three short authored hunts (nine distinct coordinates), explicit Next Hunt/Replay Chapter and optional first-use guide, with saved hunt/objective/selection/hint state. [GH-02B QA10 GitHub artifact](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45059027122/artifacts/14768283897); core JVM tests and APK assembly passed, hash/version/signer verified. **Owner test not yet run**; install likely requires uninstall of QA8 owing to differing debug signer.


## GH-02C: new Reverse mode + repeatable QA update chain (2026-11-22)

[GH-02C QA15 evidence](qa/GH02C_STABLE_UPDATE_QA15.md): 9 chapter square tasks + 5 fresh marked-point reverse-reference tasks, on-device state saving, 22 authored JVM test methods, successful CI and independently checked APK hashes. GitHub QA14/QA15 use identical public debug-only signature and package with increasing codes (10014→10015), intended to support install-over. **One uninstall needed from previous ephemeral-key QA builds, then future updates should preserve progress.** Test on Android before claiming compatibility. Public QA key is insecure and never suitable for production.


## GH-V01 Vision Lab — visual foundation source (2026-11-22)

[GH-V01 vision/playtest plan](vision/GH_V01_FOUNDATION_AND_PHONE_PLAYTEST.md) describes the new hub (First Bearings + Look Again), two switchable map treatments, optional completion vibration, progress UI and refactored Compose components. Underlying references/validators remain unchanged; local progress and fixed public QA test signing continue. Source alone is not phone verification. The immediate gate is **CI compile plus owner visual/A-B/interaction test**, before mass visual production or new puzzle modes.
