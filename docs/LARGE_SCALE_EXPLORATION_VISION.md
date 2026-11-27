# Large-scale exploration direction — GH-V43-D

Owner direction, 2026-11-27: eventually provide a very large grid map to hunt through all kinds of discoveries, especially as players advance. This is a long-term product requirement, not authorization to publish or replace existing worlds.

## Proposed progression (design targets, not implemented commitments)

| Stage | Example map dimensions | Four-digit cells |
| --- | --- | --- |
| Introductory | 6 × 6 km | 36 |
| Developing explorer | 12 × 12 km | 144 |
| Regional explorer | 20 × 20 km | 400 |
| Advanced explorer | 40 × 40 km | 1,600 |
| Large atlas sheet | 100 × 100 km | 10,000 |

Current Woodland and Coast are each 6 × 6 km and remain unchanged. These sizes are illustrative, not mandatory progression gates. Larger maps should introduce meaningful terrain, regional landmarks, navigation, multi-stage clues, optional precision objectives and useful overview tools rather than empty cells or artificial difficulty. The current coordinate system is fictional, with a single 100 km square; beyond that extent require explicit sheet/region identity, never wrap or silently reuse a bare grid code.

## Architecture requirements

- World selection is an initial navigation layer, not the final limit of the game. Support an atlas of regions and eventually neighboring authored map sheets. Do not imply current Woodland and Coast are geographically adjacent.
- Preserve exact map ID/revision, chapter ID, objective identity and original save namespaces. Each generated session must retain its map identity. No relocation or reinterpretation of earned progress.
- GridMath supports four/six/eight/ten-digit references, but current playable maps primarily use four digits. Do not advertise unsupported precision gameplay.
- MapCamera.transform currently caps zoom at five times fit; larger regions require scale-aware zoom limits, viewport-aware grid density, overview/minimap or region jump, and tested world-to-screen invariance. Preserve original camera defaults until separately verified.
- ExplorationMap currently eagerly enumerates cells and indexes by reference. Up to 10,000 four-digit cells is feasible as a core design target but not a measured performance claim. Never enumerate all fine-precision cells. Future generation should support eligible terrain subsets and spatial queries. Existing 36-cell behavior remains stable.
- Terrain rendering must not scale an entire detailed atlas at every frame. Use visible bounds, level-of-detail, regional tiles or chunks, and stable world-coordinate anchors; profile on modest Android devices before expanding.
- Do not generate a giant map simply by scaling up the current six-kilometer art. Design actual varied regions and landmarks. Four-digit identification requires unambiguous landmark cells; dense maps may need finer precision or explicit disambiguation.
- Keep standalone modes, Adventure and Custom Play reusable across map regions. Maintain a 12 meaningful level target per chapter (10 minimum mature); protect existing completion and unlock entitlements through explicit migration.
- Offer large-map exploration progressively; initial map reading remains approachable. Reward exploration and mastery, not mandatory repetitive clearing. Avoid premature storefront promises.

## Implementation sequence

1. Finish current Custom Play and coastal Adventure verification, then safe world-selection/navigation integration.
2. Add a synthetic larger-map core test fixture (no published content), verify generation, save identity and coordinate boundaries.
3. Audit renderer and camera scaling; introduce tested scale-aware policy and viewport clipping with legacy defaults.
4. Prototype a larger authored region with regional navigation, landmarks and varied terrain; verify performance, readability and gameplay on device.
5. Expand to atlas/sheet navigation and persistent discovery only after the prior gates pass.

No new map assets, map sizes, unlock policy or production behavior changed by this document. Final size and region theme require iterative gameplay validation.
