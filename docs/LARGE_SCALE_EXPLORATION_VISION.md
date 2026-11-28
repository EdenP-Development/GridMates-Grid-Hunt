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


## Persistent long-form hunts — owner clarification, 2026-11-27

The intended experience is analogous to a 1,000+ piece jigsaw puzzle left in a family game room and assembled in short sessions throughout days or weeks. Large maps are not merely larger arenas for short missions: they should themselves support a satisfying, persistent exploration project.

Proposed working mode name: Grand Expeditions (not finalized). A single expedition can contain hundreds or 1,000+ meaningful discoveries, grouped into regions and manageable clue clusters. The map remains stable across sessions; progress is cumulative, visible and resumable. No forced daily reset, expiring clues, streak penalty, time pressure or mandatory daily attendance. A player can solve one clue or many and return later exactly where they stopped. Do not require every empty grid cell to be clicked. Use authored or deterministically generated meaningful objectives, distinct from map-cell count.

Potential features: persistent solved overlays, regional completion, journal and clue bookmarking, optional hints, resume viewport/zoom/active objective, discovery history, milestones and an overall completion keepsake. Nonlinear exploration where dependencies allow; players may choose another clue when stuck. Family-room analogy describes pacing and shared appeal, not a confirmed multiplayer requirement; cooperative/shared expedition support is a possible later design decision.

Persistence requirements: stable expedition ID, map ID/revision, generation seed or explicit objective manifest with version, stable objective IDs, solved set, clue dependencies, per-objective state, safe checkpoint writes and migration/version handling. Never regenerate unfinished content on reopen. Use bounded storage and incremental updates; validate restart, process death, upgrade, corruption handling and long-duration completion. Store elapsed play time only if useful, never use it to penalize breaks. Preserve standalone modes and Adventure saves. Larger atlas rendering, long-form session storage and content quality need separate performance and accessibility tests.

Prioritize current coastal integration and existing save gates, then prototype a smaller persistent expedition before scaling to 1,000+ objectives. No new gameplay implemented by this design amendment; counts, labels, progression rules and family features require gameplay validation.
