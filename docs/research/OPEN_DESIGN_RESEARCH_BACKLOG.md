# Grid Hunt — Open Design Questions and Research Backlog

**Created:** 2026-11-21  
**Status:** WORKING RESEARCH AGENDA, **NOT** a finalized game design, implementation plan or completed research report.
**Owner direction:** casual puzzle **game first**; repeated practice of genuine military-style map-reading habits occurs naturally through accessible play. Not a military simulator, GPS app, map platform, or training-certification tool.

## Locked intent vs unresolved design

**Established intent:** distinct offline-first GridMates Android puzzle game; first exercises involve finding whole grid squares; gradually introduce reverse plotting, landmarks, smaller coordinate cells, contours, direction/distance and combinations; optional customizable/generated hunts; warm approachable style; map math must be accurate even when illustration is simplified.

**Not yet decided:** map rendering architecture; visual style and asset pipeline; amount of authored vs generated terrain; tap vs reticle details; chapter length and mode mix; custom-mission settings; clue design and grading; extent of simulation/advanced skills; release-specific art and progression. Existing engineering research recommends a small custom Canvas renderer as a **prototype hypothesis**, not an immutable commitment.

## Research questions, prioritized

**P0 = resolve or prototype before extensive development. P1 = resolve during core game expansion. P2 = later, not a first playable blocker.** Research should produce a concrete decision, small test, comparison, or reference-backed rule, rather than a large unbounded essay.

| ID | Priority | Question to investigate | Proposed evidence or deliverable |
|---|---|---|---|
| R01 | P0 | What does a 30-second first puzzle look like for someone who has never read a map? | Screen flow and 3–5 novice playtests: start, solve, understand without military jargon |
| R02 | P0 | What makes the **hunt** satisfying rather than a worksheet? | Compare reveal/plot/identify loops, mixed objectives, feedback, corrections, objective count |
| R03 | P0 | How do numeric eastings and northings appear without overwhelming casual players or teaching the wrong order? | Microtutorial, 4-digit sample, clear legend and error-feedback mockups |
| R04 | P0 | Should maps be code-drawn, preillustrated, or hybrid? | **Three small equivalent map proofs** using the same coordinate data; compare clarity, editability, asset burden and phone render feel |
| R05 | P0 | How do objects/images correspond to precise grid world coordinates? | Landmark contract (E,N, stable ID, symbol ID, visible footprint, visual anchor, interaction hit target) with boundary tests |
| R06 | P0 | How should players place and edit four-digit pins, then 100 m six-digit selections? | Compare tap-to-select, movable pin, crosshair/reticle, 10×10 sub-square magnifier; test zoom/pan and accessibility |
| R07 | P0 | How can a small map support many correct, nonrepetitive hunts? | One fixed map + verified target pool + seeded objective sampler; uniqueness/novelty tests |
| R08 | P0 | What minimum engine is necessary on Android? | Compose Canvas + pure Kotlin world-space core spike; compare against actual constraints before adding GIS SDK |
| R09 | P0 | How do we preserve authentic grid semantics with fictional maps? | Test vectors for E first/N second, leading zeroes, 1 km and 100 m **areas**, map edge/axis labels; clear training-grid disclaimer |
| R10 | P1 | How to introduce Reverse Hunt and coordinate entry without fiddly keyboards? | Input prototypes (number slots vs keyboard), missing-zeros and swapped-axis error tests |
| R11 | P1 | How do landmark symbols, map legends, and distractors work? | Original coherent symbol catalog; icon readability, landmark category, legend interaction, unambiguous target fixtures |
| R12 | P1 | How can contour lines and terrain features be credible but approachable? | Small coherent heightfield/contour prototype **versus** curated terrain-pattern templates; verify hill, ridge, saddle, valley and slope consistency |
| R13 | P1 | How to increase difficulty through **skill**, not only a denser or smaller grid? | Curriculum matrix with precision, number of objectives, map complexity, modes mixed, hints removed and multi-stage depth separately scored |
| R14 | P1 | What modes offer genuinely different thought processes? | Test Coordinate Hunt, Reverse Hunt, Landmark Hunt, and 2-step Mixed Hunt before planning more |
| R15 | P1 | How should authored chapters, random Free Hunt and optional Daily share content? | Immutable chapter/mission IDs, versioned map registry, deterministic seed/replay and old save tests |
| R16 | P1 | What is a fair answer/score/hint policy? | Correct square-area validation, undo/retry, assisted vs unassisted and optional time stats; avoid compulsory timer |
| R17 | P1 | How should map scale and distance show up in puzzles? | Distance/symbol exercises tied to world meters and legitimate map scales, not decorative scale bars |
| R18 | P1 | Can a new player acquire and retain the correct map-reading habit? | Baseline, immediate transfer to unseen map, optional later retention check; separate intuitive game success from real outdoor competency |
| R19 | P1 | How do maps and assets stay consistent with GridMates' cozy-modern identity? | Small asset guide with icon anchor placement, readable palettes, label hierarchy, font/licence ledger and themed map examples |
| R20 | P1 | What does customizable Free Hunt **actually allow**? | Options dependency matrix for map, 4/6 digits, target count, mode mix, landmarks/terrain/distance; invalid combinations rejected |
| R21 | P2 | How much genuine procedural geography is useful? | Compare fixed base map with randomized targets vs moving approved objects vs algorithmically generated landforms; reject unplayable/implausible outcomes |
| R22 | P2 | When/if to add bearings, grid vs magnetic north, route planning, intersection/resection? | Official source cross-check and dedicated math fixtures; defer until base coordinate/contour work earns it |
| R23 | P2 | How to scale collections, themed regions, achievements and optional cosmetic visuals? | Evidence-based retention without overwhelming the central map/puzzle experience |
| R24 | P2 | Nearby co-op/versus, user-authored missions, map sharing or real geodata? | Separate roadmap evaluation only after a polished standalone game; no accounts/GPS dependencies by default |

## Art-placement data contract — research hypothesis

A **map and puzzle are data, not a flattened screenshot with guessed answers**.

```text
MapDefinition:
  mapId, revision, worldBoundsMeters, gridOrigin, gridSpacing, terrainLayers, legend

Landmark:
  landmarkId, category, canonicalEastingMeters, canonicalNorthingMeters,
  symbolAssetId, imageAnchor, visualFootprintMeters, hitTargetPolicy,
  mapPlacementRules, label, optionalClueTags

Objective:
  type, mapId, targetId or canonicalCell, digits/resolution, allowedAnswerType,
  uniqueClue, linkedStages, generatorVersion

PuzzleInstance:
  instanceId, mapRevision, seed, chosenObjectives, hintPolicy, savedMarkers
```

- The **canonical location** is not the sprite's visual center or a guess from a generated image.
- A big building sprite may span multiple areas. Explicitly choose its **map symbol reference point** (entrance / marked feature location / point symbol) and clearly define what the player is asked to select; avoid ambiguous boundary fixtures.
- Larger terrain features (lake, forest, hill) may be **areas or lines**, not only a point. Represent polygons/paths and pick an appropriate question/selection rule rather than pretend every object is a dot.
- Asset dimensions and anchor offsets are *presentation data*; changing sprite art does not silently change a mission answer.
- For contour puzzles, surface height topology should determine isolines, not arbitrary pretty lines. If hand-authored, verify the shapes and intervals with a terrain reference.
- World/screen transforms, clipping and touch selection are detailed in [map engine math](MAP_ENGINE_MATH_AND_VIEWPORT.md).

## Candidate map/art experiment — three equivalent versions

Test the SAME tiny 6×6 training map and same 5 landmark coordinates in three formats:

1. **Code-first:** Canvas-drawn fills, roads, stream, basic vector symbols and grid.
2. **Illustration-first:** one decorative illustrated background, with authoritative grid and landmarks drawn over it; check image-to-world alignment and art editing costs.
3. **Hybrid:** vector terrain/roads and math-backed grid + separate original landmark sprites with defined anchors.

**Judge each on:** immediate readability, fun/identity, coordinate integrity, how easily a map changes or randomizes, APK asset weight, 4-digit and 6-digit zoom legibility, accessibility/contrast, development maintenance, and realistic art QA. This is a comparison, **not** a commitment to produce three complete game engines.

## Difficulty and chapter hypotheses to test

- Start with **friendly four-digit square hunts**: simple numbered grid, 3 targets, no compulsory glossary.
- Introduce **reverse reading** after square selection is comfortable.
- Use a **magnified ten-by-ten view** for six-digit 100 m references, then add landmark clues.
- Add **topographically coherent contours** only once players already understand their map, with short visual examples.
- Mix basic hunt rounds into advanced chapters: complexity goes up through varied reasoning, *not continuous stress or ever-shrinking targets*.
- Custom Hunt initially offers **presets** plus limited compatible toggles; don't ship a giant options menu that creates impossible missions.

## Research method and decision record

For each R## item: write a concise finding with (1) source references/current-vs-historical status, (2) prototypes/screens if applicable, (3) observed results or explicit untested hypothesis, (4) recommendation and alternatives, (5) risks/rights, (6) acceptance test, (7) decision status **OPEN / PROPOSED / TESTED / APPROVED / DEFERRED**.

First suggested design session: **R01/R02/R03** (what the first puzzle feels like), then **R04/R05/R06** (three comparable map treatments and correct touch selection), then **R07/R08/R09** (seeded engine and accuracy tests). Build only as far as needed to answer the research questions.

## Reference starting points

- [Map technology survey](MAPPING_TECH_SURVEY_2026-11-21.md)
- [Coordinate/display math](MAP_ENGINE_MATH_AND_VIEWPORT.md)
- [Mapping sources](MAPPING_TECHNICAL_REFERENCE_INDEX.md)
- [Historical military source study](MILITARY_MAP_READING_2010_SOURCE_NOTES.md)
- [Skill-to-puzzle matrix](SKILL_TO_PUZZLE_MATRIX.md)
- [Master design](../MASTER_GAME_DESIGN.md)
- [Roadmap](../ROADMAP.md)

No feature, art pipeline, lesson, or SDK is marked implemented by this document.

## Research progress — R01 (2026-11-21)

**R01: RESEARCHED; PROTOTYPE AND PLAYER VALIDATION OPEN.** See [first-30-seconds research report](R01_FIRST_30_SECONDS_ONBOARDING_RESEARCH.md). Contextual help, progressive disclosure and fading guidance support a gentle first-puzzle prototype; these are **external general findings**, not evidence from our game. Do not mark the 30-second target as measured or user-approved. **R02 is next; no broader research topics were closed by this pass.**
