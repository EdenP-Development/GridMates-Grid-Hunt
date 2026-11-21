# Skill-to-Puzzle Matrix — Real Technique -> Enjoyable Game

**Status:** ORIGINAL DESIGN / PROPOSED. This translates researched concepts into possible puzzle mechanics. Nothing in this file is built, tested or a promised release.  
**Source baseline:** [RSMS (2010) study notes](MILITARY_MAP_READING_2010_SOURCE_NOTES.md), independently cross-checked for grid conventions with [NGA](https://earth-info.nga.mil/?action=coordsys&dir=coordsys) and [FGDC](https://www.fgdc.gov/usng/how-to-read-usng).

## Learning ladder and content gates

| Skill | Booklet pages | Puzzle disguise | Player action | Evidence of correct skill | Earliest stage |
|---|---|---|---|---|---|
| Grid direction | 3 | "Find the campsite" | Read eastings first, then northings. | Selects requested square without an axis-order prompt. | Intro |
| 4-figure grid squares | 3–4 | Treasure cache hunt | Select 1 km cell among other landmarks. | Correct cell; neighboring cell rejected; no center-pixel trick. | GH-01 |
| Reverse references | 3–4 | Surveyor's notebook | Read a marked landmark's grid coordinates. | Correct numeric string with leading zeroes. | GH-03 |
| 6-figure interpolation | 4, 6–7 | Supply drop hunt | Choose correct 100 m sub-square using 10×10 subdivision. | Exact same 100 m cell at any zoom. | GH-03 |
| Symbols and legend | 5 | "Which bridge?" | Use symbol legend and spatial clues. | Unique landmark/cell chosen; no ambiguous distractors. | GH-04 |
| Scale / straight distance | 8–10 | "How far is camp?" | Compare grid separation or map scale line. | Valid distance tolerance tied to modeled coordinates/map scale. | Later |
| Contour recognition | 11–14 | "Find the saddle" | Identify hill/ridge/valley from coherent contour shapes. | Correct terrain-feature classification on original drawings. | Later |
| Grid bearings | 15–17 | Compass clue | Use grid north and clockwise azimuth to locate point. | Bearing line/point correct; magnetic correction **not** silently assumed. | Later |
| Terrain association | 24–25 | "Where did the explorer go?" | Combine road, stream, hill shape and relative position. | One solution supported by all clues. | Later |
| Intersection | 27 | Mystery feature | Combine plotted directions from two known places. | Canonical intersection within explicit tolerance. | Later |
| Resection | 28–29 | "Locate me" | Infer unknown observer location from known landmarks. | Computed unique intersection/region with error model. | Later |
| Route choice | 34–40 | Expedition | Evaluate path, landmarks, distance and checkpoints. | Valid objective-dependent route; alternatives explained. | Later |

**Avoid a curriculum that is only "click more tiny things."** New difficulty should add spatial reasoning, precision, mixed tasks and ambiguity control while preserving touch fairness.

## A small recommended opening sequence

| Tutorial puzzle | Gameplay | Skill secretly rehearsed |
|---|---|---|
| 1. First Marker | Tap a clearly labeled square from a 4-digit reference. | Reading right/up |
| 2. Hidden Camps | Find four references among cabins, bridges and forests. | Stable 1 km grid selection |
| 3. Wrong Side of the Line | Spot and fix a marker in adjacent square. | Origin/line boundary |
| 4. What Grid Is It? | Look at a unique landmark and enter its 4 digits. | Reverse reading |
| 5. Treasure Ledger | Match several grid refs to named features, not just any icon. | Coordinate plus map interpretation |
| 6. Ten Little Squares | Use magnified subdivision to find a 6-digit 100 m area. | Interpolating tenths |
| 7. Mixed Search | Plot two grids, reverse one landmark, match one clue. | Switching skills and reducing memorization |
| 8. Woodland Challenge | A 5-object hunt with no compulsory hints. | Independent retrieval |

**These titles are draft content, not existing levels.** Unlock structure may change after first owner phone testing.

## Difficulty model

Use `precision`, `objectiveCount`, `mapComplexity`, `hintLevel`, `taskMix`, and `multiStageDepth` as **separate** parameters. Beginner at 4 digits is not automatically "wrong" if an expert still finds a large map/chained challenge fun.

- Easy success = correct **grid square**, not object-centered tap.
- Six digits = a 100 m **cell**; game should allow reticle, zoom or sub-square picker.
- Reverse Hunt must have at least one unambiguously specified location and properly formatted entry.
- Landmark Hunt must verify clue uniqueness, not just expected map art.
- Story prompt cannot reference a feature whose position is absent from the deterministic map data.
- Hints can progressively reveal axis order, neighboring lines and subdivisions; record assisted solves but avoid punitive scoring before calibration.

## Mission authoring checklist

Every mission manifest should declare:
1. Source references (source ID + booklet pages, plus independent authority where needed).
2. Skill(s) trained and expected player action.
3. World-space canonical answer and grid precision.
4. Map revision, legend and distractor pool.
5. Deterministic seed (if generated), solution uniqueness and difficulty settings.
6. Hint ladder and useful explanation after a mistake.
7. Tests for coordinate math, accessibility/touch fairness and reproducibility.

**Educational claim boundary:** Completing a chapter demonstrates success in that game's tested exercise. It does not prove the player could safely navigate outdoors without practice.