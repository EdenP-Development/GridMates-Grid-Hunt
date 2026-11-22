# Grid Hunt Research Library

**Status:** curated reference notes. These files support design; they are not imported code or playable levels.

1. [Source register and licensing](SOURCE_REGISTER.md) — official authorities, provenance, priority, reuse policy.
2. [2010 UK military map-reading source study](MILITARY_MAP_READING_2010_SOURCE_NOTES.md) — 44-page owner-provided booklet, page-by-page skill extraction and caution.
3. [Skill-to-puzzle matrix](SKILL_TO_PUZZLE_MATRIX.md) — techniques mapped to modes, chapters, scoring and tests.
4. [Learning experience principles](LEARNING_DESIGN_PRINCIPLES.md) — fun-first, skill-accurate product and future validation.

The user-supplied original PDF is **not committed** due third-party OS map rights uncertainty. The source study stores exact provenance and a public reference URL.

For implemented accuracy contracts see [../MAP_READING_ACCURACY.md](../MAP_READING_ACCURACY.md), and for overall sources see [../RESEARCH_AND_SOURCES.md](../RESEARCH_AND_SOURCES.md).

## Mapping engineering and GPS app references (2026-11-21)

- [Technical sources and documentation index](MAPPING_TECHNICAL_REFERENCE_INDEX.md)
- [SDK/renderer technology comparison](MAPPING_TECH_SURVEY_2026-11-21.md)
- [Exact map camera transforms and input architecture](MAP_ENGINE_MATH_AND_VIEWPORT.md)
- [Offline map file formats and licensing](OFFLINE_MAP_FORMATS_LICENSES.md)
- [Coordinate/viewport test vectors and implementation spikes](MAP_ENGINE_TEST_AND_SPIKE_PLAN.md)

These are research/design notes. None of the discussed libraries or code are currently integrated.

## Active research decisions and open questions

- [Open Design Questions and Research Backlog](OPEN_DESIGN_RESEARCH_BACKLOG.md) — prioritized game feel, map/art accuracy, puzzle generation, contours, chapter progression and Custom Hunt questions. These are **not yet finalized decisions**.


## Completed scoped research reports

- [R01 — First 30 seconds: beginner onboarding research](R01_FIRST_30_SECONDS_ONBOARDING_RESEARCH.md) — source-backed beginner UX findings, proposed first puzzle, competing tutorial approaches and an **unexecuted** playtest plan. R02 and other research questions remain open.

- [R02 — Making Coordinate Hunt fun and replayable](R02_GAME_FEEL_AND_REPLAYABILITY_RESEARCH.md) — evidence review, Classic Find vs Discovery Hunt comparison, difficulty/feedback hypotheses and unrun playtest; **R03 is next**.

- [R03 — Friendly and technically accurate coordinate clues](R03_COORDINATE_PRESENTATION_AND_GRID_LABELS_RESEARCH.md) — FGDC/USGS-backed grid-label rules, progressive display proposals, error taxonomy, accessibility and an unrun comparison test. **R04 is next.**


## Focused research sequence — R04

- [R04 — Map creation and artwork research](R04_MAP_CREATION_AND_ARTWORK_RESEARCH.md): compares code-first, illustration-first and hybrid maps; includes a controlled five-landmark 6×6 test fixture, art QA criteria, licensing safeguards and a proposed phone comparison. **Research documented; no prototypes or art style approved.** Next: R05 landmark geometry, anchors and answer integrity.

- [R05 — Grid-accurate landmarks and feature placement](R05_GRID_ACCURATE_FEATURE_GEOMETRY_AND_ANCHORS.md) — sourced point/line/polygon geometry, symbol anchor vs physical footprint, crossing/overlap rules, seed-safe placement and an unexecuted 18-case verification matrix. **R06 next.**

- [R06 — Mobile map interaction and precision controls](R06_MOBILE_MAP_INTERACTION_RESEARCH.md) — tap-preview-check, pan/zoom arbitration, 100 m magnifier vs reticle, accessible alternatives and unexecuted 20-case QA plan. **Next: R07 objective generation.**


## Gameplay mechanics (not map-engine implementation)

- [Mechanics research track GM01–GM08](GAMEPLAY_MECHANICS_RESEARCH_TRACK.md) — rule systems, objective design, difficulty, hints/scoring, skill practice, mission character, customization and player testing.
- [GM01 — Core gameplay rules, choices, win/retry conditions](GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md) — three mechanic variants with state changes and a controlled prototype plan. **Research only; next GM02.**

- [GM02 — Distinct hunt and answer mechanics](GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md) — Coordinate, Reverse, Landmark, Precision and Mixed task contracts; **research only**.
- [GM03 — What makes a good puzzle?](GM03_PUZZLE_QUALITY_AND_CLUE_DESIGN_RESEARCH.md) — published source review, fair-clue criteria, examples of repaired invalid puzzles, hard/soft quality gates and 20 **unexecuted** tests. **GM04 next.**

- [GM02 — Distinct hunt mechanics](GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md) — different player verbs/answer types and proposed 12-puzzle comparison; unimplemented.
- [GM03 — Puzzle quality, fairness and difficulty](GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md) — source-backed research on clue sufficiency, unambiguous answers, meaningful distractors, difficulty dimensions, hard/soft quality gates and 20 **planned** tests. **GM04 next.**

- [GM04 — Hints, mistakes, scoring and rewards](GM04_HINTS_FEEDBACK_SCORING_REWARDS_RESEARCH.md) — studies guidance and reward evidence; compares free staged hints, actionable feedback, relaxed completion and optional mastery. **Unimplemented/unplaytested; GM05 next.**

- [GM05 — Chapter progression and skill revisit](GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md) — reviews spacing/interleaving/guidance fading and game-transfer boundaries; proposes a spiral six-chapter arc, local practice rotation and 20 **unrun** QA checks. **GM06 next.**

- [GM06 — Small missions, environmental storytelling and discovery](GM06_MISSION_THEMES_AND_ENVIRONMENTAL_STORYTELLING.md) — Jenkins/intrinsic-integration/story-attention evidence, six optional thematic mission templates, narrative-validity rules, A/B comparison, and 20 **unrun** QA scenarios. **GM07 next.**

- [GM07 — Quick vs Custom Hunt settings and player agency](GM07_QUICK_AND_CUSTOM_HUNT_PLAYER_CHOICE_RESEARCH.md) — primary Android/NNg sources, choice-overload/agency research, valid option combinations, three UX variants and 20 **unexecuted** test cases. **GM08 next.**

- [GM08 — Game playtesting methods, evidence and next-build gate](GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md) — research on observation/interviews, four evidence domains, exact build/source integrity, test error taxonomy and 20 **unexecuted** cases. [One-page Android test script](GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md) ready for use when a real APK exists. **GM01–GM08 research complete; GH-01 prototype proposed next.**
