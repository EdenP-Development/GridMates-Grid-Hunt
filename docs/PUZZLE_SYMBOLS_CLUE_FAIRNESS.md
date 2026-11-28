# Grid Hunt — Map Guide, Symbols & Fair Clue Design

**Owner design direction:** 2026-11-23, after QA98 first real-phone Treasure Checklist screenshots.  
**State (2026-11-23):** **P1 Map Guide QA113 green; P3 typed clue-validation engine QA125 green; GH-V12 first coordinate→object identification pilot QA132 green** (four canonical woodland landmarks, visually matched option tiles). Multi-church gameplay, geometry-based water/road or elevation clues, mixed story chapters, generic custom generator and new world maps remain **DESIGN ONLY**.  
**Read with:** [Campaign, Gameplay & UI Blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md), [Map Reading Accuracy](MAP_READING_ACCURACY.md), [Puzzle/Content System](PUZZLE_AND_CONTENT_SYSTEM.md).  
**Current source:** draft [PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12). Keep documentation/version history durable across chats.

## 1. Why we need this

QA98's first real-phone Treasure Checklist was playable, and the owner completed the four items. But the map's mushroom, flowers, acorn and stone are tiny, without a clear explanation that those decorations are search targets. Players should **not** have to guess what an artist meant by a small shape. A game can be approachable and still gradually ask for sophisticated map reasoning.

The owner also wants **multiple instances of the same feature category** (for example three churches) and specific clues such as *the church beside the river* or *the church east of a bridge*, with optional elevation/road/area constraints in advanced chapters. The underlying game must distinguish the **specific intended landmark**, not count any church as correct.

**Core promise:** the game teaches reading its own illustrated maps, then combines symbol recognition, four-digit grid areas and fair logical clue-solving. Difficulty comes from meaningful relationships, not tiny art, obscure guesses or arbitrary penalties.

## 2. A friendly in-game map guide

Working name **Map Guide** (could be 'Explore the Map', 'Symbol Guide' or 'Explorer's Notes' after owner feedback). Avoid a full bureaucratic/military legend page.

- Visible **? / Map Guide** access in Treasure Checklist and future object-based puzzles, preferably near the objective/checklist, not buried in Home settings.
- Opens a compact, **scrollable and dismissible** in-game dialog/sheet. The game returns to the same map, active target, zoom and unfinished input; opening a guide must never mark an answer or reset progress.
- Show only symbol types actually present on that map pack, ideally as a **matching sample of the same map symbol** (not unrelated emoji or vague colored circles), followed by a clear name and short explanation. Map styles may change colors, but meaning stays the same.
- Separate **landmarks you can hunt** from **map features used to orient yourself** (trees, clearings, creek, dashed trail, roads, elevation if present). Make it explicit that a symbol key tells you *what* an icon is but **does not show the hidden answer square**.
- Include a short **fictional, off-map** example: the first two digits refer to the easting line at the bottom, and the last two to the northing at the side; the containing square is above/right of that intersection. A four-digit reference refers to a **1 km square**, not an exact pin on an object. Never include an actual active puzzle answer as a guide example.
- Accessible text and visual redundancy: do not rely only on color; clear text labels, adequate hit target sizes, scalable font, TalkBack descriptions. At small/phone sizes no large permanent legend covering the board.
- Optional later: tap-to-identify in **Inspect** mode to show a landmark's name (and map symbol meaning), but **never the answer grid**; use a deliberate mode to avoid interfering with pan/zoom/selection. Review whether this makes beginner puzzles too trivial before enabling by default.
- Guide should use the **canonical map/landmark registry**, not a hand-maintained list of unrelated icons. No guide entry for a landmark absent from that map; no unknown drawable symbols for which the game cannot show a meaning.

### Current Little Woodland key (authoritative existing meaning; matching visual assets still being improved)

| Visible on map | Meaning | Gameplay role |
| --- | --- | --- |
| Two red-capped mushrooms near the southern trail | **Mushroom Nook** | Checklist landmark, stable ID `mushroom-nook` |
| Tiny pale five-petal flowers in an eastern clearing | **Buttercup Glade** | Checklist landmark, ID `buttercup-glade` |
| A small brown acorn toward the north | **Acorn Nook** | Checklist landmark, ID `acorn-nook` |
| Gray-green upright stone toward northeast | **Old Waystone** | Checklist landmark, ID `old-waystone` |
| Blue winding line | Creek/stream | Orientation, possible future relation clue |
| Dashed winding brown/gray line | Woodland trail | Orientation, possible future relation clue |
| Round layered green crowns | Trees/groves | Scenery/orientation, not all target objects |
| Pale green patches | Clearings/meadows | Scenery/orientation, possible authored regions |
| Small golden check stamp on shaded square | Already solved | Completion feedback; **not** an unsolved target |
| Golden spot with dark center | Active Golden Spots target | A different mode's target; distinguish from completed stamp |

**Important:** current `WoodlandLandmark.glyph` values are rough text stand-ins (`●`/`◆` etc.) and must **not** be blindly used as accurate visual legend swatches. Use actual matched mini drawings or properly registered symbols when implementing.

## 3. Stable object identity and map data

A landmark has a **unique, immutable instance ID**, category/symbol type, display name, authoritative world point or area, four-digit containing square, map pack/revision, and relevant relations (region, nearby water/road/building, elevation etc. **only where authored data exists**). Examples:

- `church-old-hill` and `church-riverside` share type `church` but **not** instance IDs.
- `church-riverside`: category church, region east village, adjacent to validated creek feature `creek-02`.
- `church-old-hill`: category church, region high meadow, elevation **only if the map actually defines elevation**.

A checklist objective has its **own immutable ID** plus the *specific target instance ID*. The displayed clue is authored from validated constraints; completion tracks the target **ID**, not its label, category or just square. Several church icons cannot all satisfy a one-church request.

Authoritative answer = the target's **canonical** grid reference computed from its world coordinates and requested precision. The map art must render from those points, not have separate positions. Renderer sprites, camera scale, palette or label visibility cannot change correctness.

If multiple distinct object IDs fall inside **the same four-digit square**, the four-digit answer alone cannot prove which instance was found. Do not misrepresent it: author a fair unambiguous object/clue, allow a multi-part response or higher precision when supported, or prohibit that objective in this mode. A painted pinpoint does not magically grant extra coordinate precision.

## 4. Clue difficulty ladder (examples, not published content)

| Tier | Player task | Example | Validity rule |
| --- | --- | --- | --- |
| **1 — Recognize** | Find the only item of a type on a simple map | 'Find the church' (exactly one church visible) | One candidate, icon and guide understandable |
| **2 — Read symbols** | Use the map guide to recognize less obvious features | 'Find the well' | Guide shows a matching symbol; feature visually readable |
| **3 — Distinguish** | Choose among repeated icon types using one map relation | 'Find the church beside the creek' | Exactly one church meets a **defined** beside/near relation |
| **4 — Reason spatially** | Use directional or regional constraints relative to a known feature | 'Find the church east of the footbridge' | East, region boundaries and referenced bridge are defined unambiguously |
| **5 — Hybrid chain** | Find, identify, read coordinates, follow next clue | 'Find the church north of the mill; its square reveals the next landmark' | Every stage uniquely resolvable, no future clues leaked early |

Increasing tier should reflect more **real map reasoning**: more candidates, relationships, longer but understandable clue chains. Avoid difficulty from smaller icons, low contrast, input friction, arbitrary hidden knowledge, or surprise answer precision.

## 5. Fairness & publication gate (non-negotiable)

Before publishing a clue into a chapter or Custom Play, automatically validate:

1. **Map truth:** all referenced object/feature IDs exist on that map/revision with stable world anchors and visible/renderable symbols.
2. **Comprehensible symbol:** new object type is represented in the guide (or deliberately introduced as a learning moment with the same visual).
3. **All clue predicates are real:** never ask about hill elevation, adjacency, road names or regions unless the authored map data supports them. No visual-only approximations that could differ on device.
4. **Candidate evaluation:** filter the whole visible candidate set by category, region, exact authored relationship and direction. **Exactly one object ID must match the objective** for a single-target clue; zero matches or multiple matches invalidate the puzzle. Do not silently accept the 'first' match.
5. **Unique answer semantics:** if the input is only a four-digit grid, ensure the chosen objective/answer set is not ambiguous across relevant candidates or explicitly define how any same-square instances are treated.
6. **Distance/direction policy:** use explicit world units for 'near', 'beside', 'north of', 'nearest'; define threshold and whether ties invalidate. Avoid arbitrary 'looks next to' judgement on different screen sizes.
7. **Readability:** no target hidden behind persistent UI, labels, another symbol or solved stamp; touch/zoom are adequate. Test both color looks, portrait and landscape, and large text.
8. **No spoiler leaks:** guide can identify symbol **types**, not list hidden exact answers; solved stamps only reveal already-found locations; clue-chain stages reveal in order.
9. **Progress integrity:** IDs are stable, validators versioned; reopening and updating the app cannot reshuffle an unfinished challenge. Wrong guesses or visiting the guide cannot complete it.
10. **Answer honesty:** never treat the four-digit square as proof of a specific building's pinpoint. Encourage precision only with matching game rules and UI.

### Example: three churches

Authored map: `church-riverside`, `church-hilltop`, `church-village`.

- **Invalid**: `Find the church` (3 category matches).
- **Valid only when evaluated uniquely**: `Find the church beside the creek` (one authored church adjacent to a mapped creek).
- **Conditional**: `Find the church at the highest elevation` requires actual comparable elevation values for all three and a unique maximum; otherwise reject.
- **Conditional**: `Find the church east of the footbridge and north of the school` requires exact known footbridge/school IDs and unique matching instance.
- **Bad even with one correct-looking answer**: using `2641` when multiple candidate churches occupy square 2641 and the task asks the player to distinguish their identities by coordinate alone.

### GH-V11 implemented clue validator and limits

The pure-Kotlin [`ClueValidation.kt`](../core/src/main/kotlin/com/playereden/gridhunt/core/ClueValidation.kt) implements `ClueFeature`, `ClueMapPack`, `ClueObjective`, typed `ClueCondition`, `FeatureRelation` and a fail-closed `ClueVerdict` suitable for **content publication validation**. It checks known/published symbols, real feature IDs, regions with complete metadata, explicit authored BESIDE links, world-meter EastOf/NorthOf/NearPoint conditions, unique matching landmark ID, correct authored target ID and unique **four-digit** grid square among same-category candidates. It returns a `Publishable` verdict or a specific rejection code: `INVALID_SPEC`, `MISSING_FEATURE`, `UNRECOGNIZABLE_SYMBOL`, `UNSUPPORTED_RELATION`, `NO_MATCH`, `AMBIGUOUS`, `TARGET_MISMATCH`, `SAME_GRID_SQUARE`.

A **linear** creek/road feature cannot be interpreted as a point to fake distances or compass directions. `NearPoint`/EastOf/NorthOf require an actual point feature; a relation such as *beside the creek* must be separately authored and verified against the real drawn waterway. The engine currently trusts reviewed, explicitly authored BESIDE links; it does **not** independently prove that the art matches those relationships, nor does it infer elevation or road adjacency. Unimplemented conditions fail closed, not silently.

[`ClueValidationTest.kt`](../core/src/test/kotlin/com/playereden/gridhunt/core/ClueValidationTest.kt) uses **synthetic three-church fixtures**, including missing features, unknown symbols, ambiguous category, region, combined directions, invalid distances, a same-square second church and a linear creek. **No churches were added to the actual woodland map.** The `WoodlandClueMap` adapter reads the existing four landmark points and IDs unchanged, without fabricating creek/road/region relationships.

Next: inspect completed GH-V11 CI evidence and, once stable, build a very small **What's at This Grid?** identification slice on the four known woodland objects. Do **not** construct a huge natural-language clue interpreter or multiply content before those mechanics are playable and fair.

## 6. Near-term build order

**First real QA insight:** the owner completed QA98's four-object checklist but was unsure what some tiny landmarks represented. Fix clarity before making those clues more sophisticated.

**P1 — Symbol guide vertical slice: IMPLEMENTED / CI-TESTED (QA113).** The checklist's optional scrollable **Explore the Map** guide shows actual previews from the same Compose shape renderer used by the woodland map (mushrooms, flowers, acorn, waystone), plus creek, trail, groves/clearings and four-digit reading rules. Each active checklist target also displays a matching smaller preview. No clue coordinates are shown. CI verifies opening/dismissal, entries, no answer leak, and progression. **Phone legibility/comfort still needs owner review.**

**P2 — Target affordance:** Make current target recognizably legible on phone. Consider a focused object detail/preview matching the actual map artwork, localized zoom or carefully framed help. Don't reveal grid answer automatically.

**P3 — Clue fairness foundation: CODED / JVM CORE TESTS PASSING (GH-V11).** Typed category, region, authored BESIDE, directional and point-nearness rules, rejection diagnostics and three-church fixtures live in `core`. The real woodland feature positions are adapted unchanged. Confirm the **latest** source-matched CI run before marking all Android jobs green; no new playable mode or church art has been introduced.

**P4 — First distinct repeated-object chapter:** One map/mission with at most a few same-category features and one unambiguous relation (creek / trail / region). Playtest before multiplying clue types.

**P5 — Mixed/object-identification:** Reuse the guide/identity system for 'What's at This Grid?' and hybrid chapters; continue adhering to [Campaign Blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md).

**Assets policy:** organize rules, models and UI first. Create just the one swatch/icon/landmark drawing required to make the mechanic fair; later unify the art library.

### GH-V12 implemented identification mode

[GH-V12 tested vertical slice](vision/GH_V12_IDENTIFY_BY_GRID_VERTICAL_SLICE.md) presents four authored four-digit square references, requires the player to choose a matching woodland landmark illustration/name, and validates each square has exactly one identifiable canonical object. It uses the same art/map coordinate source and separate local save. [QA132 full green](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45295252097). This intro mode has its own simple unique-cell validation; **the generalized GH-V11 typed clue publication gate is separately core-tested**, and will become relevant to future repeated-category authored chapters. No hilltop churches or fabricated road/elevation data were added.

## 7. Living handoff

- **Blueprint entry:** [GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md).
- **P1 verified implementation:** code source [`0790d2eb`](https://github.com/PlayerEden/GridMates-Grid-Hunt/commit/0790d2eb53b04c1839b4f9fb1399b250716bc43a) · [QA113 full green CI](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45273290877) · [QA113 test APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45273290877/artifacts/14865234254) · [executed Android tests and 11 screenshot captures](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45273290877/artifacts/14865474110). Production signing and actual phone acceptance not claimed. The user's QA98 phone images identified the tiny landmark ambiguity; the guide and matching active preview are the current candidate remedy. **Next:** owner checks the real phone Map Guide; typed repeated-category relation/uniqueness tests are now in GH-V11. The first coordinate-to-object picture tray is now implemented and QA132 emulator-tested; obtain physical-phone feedback before increasing content complexity.
- **New-chat instruction:** 'Continue Grid Hunt from the Campaign Blueprint and `docs/PUZZLE_SYMBOLS_CLUE_FAIRNESS.md`. Check the real GitHub PR and last green QA; build the next P1–P5 slice, maintaining exact map answers and our test key.'
- **Do not assume documentation equals feature completion.** The **Map Guide and matching landmark previews** are implemented and QA113 emulator-tested. The **typed repeated-category clue validator is GH-V11 core code with green QA125 tests**; the first **What’s at This Grid?** four-question pilot is **GH-V12 / QA132 green**. Multi-church gameplay levels, elevation/road geometry, generalized authored clue integration and mixed campaigns remain proposals only.
