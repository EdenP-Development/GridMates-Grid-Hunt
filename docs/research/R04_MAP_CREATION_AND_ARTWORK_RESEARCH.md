# R04 — How Grid Hunt Should Create Its Maps and Artwork

**Research date:** 2026-11-21  
**Question:** For a friendly, offline-first grid-reading puzzle game, should maps be drawn in code, illustrated as full backgrounds, assembled from original assets, or combined?  
**Status:** **RESEARCHED; DESIGN RECOMMENDATION PROPOSED; NO MAP PROTOTYPES OR DEVICE TESTS YET.**  
**Scope:** production/visual architecture, maintenance, artistic flexibility, grid legibility, and a controlled three-way art experiment. R05 owns landmark data/anchors and boundaries, R06 owns tap/zoom behavior, R07 owns objective generation, and R12 owns actual elevation/contours.

## 1. Design brief — what this map must do

Grid Hunt is a **puzzle game first**, not a GPS app or military simulator. Opening screens should show a delightful, uncluttered **numbered grid** with 3–4 enjoyable finds; later maps can reveal landmarks, paths, water, contours and mixed tasks gradually. The grid exercises authentic **easting first, then northing** habits. Early numeric references are **fictional, locally contextual training references**, not GPS-ready MGRS positions.

A good production approach must make it easy to:
- show a **whole 1 km grid cell** accurately for four-digit puzzles;
- introduce 100 m subdivisions for six-digit puzzles **without small, un-hittable targets**;
- make forests, cabins, tracks and water charming but unambiguous;
- edit or reskin artwork **without changing true coordinates or saved puzzle answers**;
- reuse an authored map for many varied hunts without producing hundreds of unique map images;
- operate **offline**, keep Android app footprint reasonable, and support future map collections.

**Research question is not** “which map SDK can do everything?” It is “what is the smallest delightful map authoring and drawing method that remains trustworthy?”

## 2. Evidence and established technical facts

| Source | Directly supported fact | Practical implication for our prototype |
|---|---|---|
| [Android Developers — Graphics in Compose](https://developer.android.com/develop/ui/compose/graphics/draw/overview) | Compose drawing supports custom lines, paths, bitmaps, scaling, translating and other transforms via DrawScope/Canvas. | We can build grid lines, roads, waterways, coordinate ticks, map backgrounds and sprite images without a geographic map SDK. |
| [Android — Graphics modifiers and drawWithCache](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers) | Custom drawing has explicit draw ordering; cacheable drawing objects can reduce allocations. | Maintain distinct render layers; cache complex paths only after profiling indicates value. |
| [Android — Bitmap vs vector](https://developer.android.com/develop/ui/compose/graphics/images/compare) and [Vector Asset Studio](https://developer.android.com/studio/write/vector-asset-studio) | Raster (PNG/WEBP) and vector/XML ImageVector are different representations; Android's vector import supports only a subset of SVG. | Use vector or code paths for crisp lines/icons, raster for painterly scenery; **validate imported SVGs** and high-zoom appearance. |
| [Android — Drawable resources](https://developer.android.com/guide/topics/resources/drawable-resource) | Android supports PNG and WEBP as application drawables. | Pack original decorative sprites locally; measure size/quality rather than assuming one format always wins. |
| [MapLibre — Style specification](https://maplibre.org/maplibre-style-spec/) and [Layers](https://maplibre.org/maplibre-style-spec/layers/) | Mature map renderers distinguish source geometry from ordered style layers, including fills, lines, symbols and text. | Borrow the **data-versus-style/layers** principle; do **not** adopt MapLibre itself for the initial fictional-map prototype. |
| [USGS — Cartographic representation](https://www.usgs.gov/centers/cegis/science/cartographic-representation) and [US Topo symbol guide](https://www.usgs.gov/ngp-standards-and-specifications/us-topo-map-symbol-guide) | Map features are represented by symbols at appropriate levels of detail; some features are areas, some lines, some points, and generalization changes appearance with scale. | A lake need not behave like a pin. Keep geometry and drawing representation explicitly separate, with a symbol legend players can learn. |
| [Godot — Using TileMaps](https://docs.godotengine.org/en/stable/tutorials/2d/using_tilemaps.html) | Tile-based authoring makes repeatable patches/terrains practical. | Tile/pattern authoring is an alternative **reference**, not a reason to move an existing native Android project into Godot. |
| [Kenney — Licensing support](https://kenney.nl/support) | Kenney states its downloadable asset packs use CC0, with individual asset and included licenses to consult. | Licensed placeholder sprites are possible for rapid evaluation; production style should be cohesive, with asset provenance recorded. |

**Caveat:** these are platform, cartographic and authoring references, **not a controlled comparison of Grid Hunt visual designs**. No source above proves which approach our players will prefer, which is smaller in *our* APK, or which takes fewer hours for *our* team.

## 3. Compare the candidate production methods

| Candidate | How it would work | Potential advantage | Main risk | Best use |
|---|---|---|---|---|
| **A. Code-first vector map** | Draw terrain fills, paths, stream curves, grid and simple building symbols with canvas geometry. | Deterministic placement, consistent lines at different zoom levels, relatively little standalone art. | Can feel like a technical plotting worksheet if shapes/textures remain too plain. Elaborate painterly terrain is time-consuming to emulate. | Fast reference baseline and onboarding clarity test. |
| **B. Illustration-first map** | Produce one beautiful full-map image; display it beneath a math-accurate overlay of grid labels, markers and canonical symbol positions. | Strong handcrafted character and atmosphere with fewer dynamic drawing routines. | Revising one river/building can require new full-image versions; pre-painted landmarks can conflict with actual coordinates; zoom may reveal resolution problems; asset burden grows per map. | Curated hero maps/background experiments, **not** the authoritative map logic. |
| **C. Hybrid map** | World-space vector paths/polygons for structural terrain, grid in code, and separate original illustration assets for camps, cabins, rocks, trees and decorative textures. | Coordinates stay exact while art can remain expressive; a single sprite collection can serve many maps and chapters. | More visual layers and art QA; needs consistent anchors, legibility and occlusion rules; poorly mixed styles will look inconsistent. | **Preferred hypothesis** for a native offline puzzle game. |
| **D. Tile/pattern map** | Repeat compatible grass/forest/path tiles with separate world-anchored landmarks and grid overlay. | Quick variants and standardized art palette. | Visible seams/repetition; map tiles aren't the 1 km answer grid; harder to make natural rivers and coherent contours if treated as random tiles. | Optional technique inside C, not a fourth complete app architecture. |

### Preliminary assessment (not a measured score)
- **Technical accuracy:** all three can be correct *if* canonical coordinate data lives outside the art. A beautiful image is not inherently inaccurate, but embedded features demand painstaking image alignment and audits.
- **Art direction:** B and C offer easy painterly personality; A needs stronger graphic design to avoid a utilitarian feel.
- **Replayability:** independent objective sampling works for all; moving/swapping landmarks and scenery is naturally easier when they are independent assets (C/D).
- **Maintainability:** A or C make it easier to correct a road/landmark position without re-exporting every complete map; this is a design inference to measure, not a universal performance guarantee.
- **Scale and later topo features:** coordinate-anchored paths, features and a coherent height model are important regardless of drawing approach.

**Proposed verdict:** build a small **hybrid** proof first, but compare A and B on the **exact same world data** before locking a final art pipeline.

## 4. Keep artistic style separate from map truth

The engine should treat the map as a set of **world-space feature records**, and the renderer as a changeable view of those records:

```text
TrainingMap(mapId,revision,boundsMeters,gridOrigin,gridSpacing)
  Feature(id,kind,worldGeometry,styleId,optionalSymbolAnchor)
    kind: point | line | area
    worldGeometry: WorldPoint | Polyline | Polygon
  Landmark(id,canonicalE,canonicalN,assetId,visualAnchor,displaySize)
  TerrainLayer(featureIds,renderOrder,style)
  PuzzleObjective(expectedCellOrFeatureId,resolution,clue)
```

Example: a cabin's **canonical point** is E 24,375 m / N 37,860 m; it therefore belongs to four-digit training cell `2437` and six-digit cell `243378`. Its sprite may be wide enough to visually cross another cell; that does **not** move the canonical location. A stream needs a line/path; a lake needs an area/polygon; a bridge should be placed against an actual **crossing** of path and stream geometry, not a conveniently painted pixel.

The existing [map/camera mathematics](MAP_ENGINE_MATH_AND_VIEWPORT.md) describes world-to-screen transforms and inverse hit testing. R04 **adopts those as the proposed separation principle**, while R05 will still specify anchors/footprints and validate edge cases. No R05 decision is prematurely closed by this study.

### Rendering order to trial (back to front)
1. Soft paper/background color or subtle texture
2. Terrain fills, woodland and water polygons, roads/trails/streams
3. **Legible numbered grid** and correctly grounded scale/legend elements
4. Landmarks and decorative independent art with constrained visibility
5. Player selection, solution outlines, hints and temporary effects
6. Fixed-screen clue card, progress, accessible controls

The grid should stay readable without hiding the map's delight. At advanced zoom levels, density and detail can change; **features' world locations may not**. Use a consistent north-up map at the start. No expensive camera tilt, satellite imagery, online basemap, real-time GPS or true geodesic routing.

## 5. Carefully controlled R04 visual experiment

**Fixture:** one **6 km × 6 km**, north-up fictional training area. Eastings from **24 to 30**; northings from **37 to 43**; **1 km** per major grid cell. World bounds E [24,000,30,000) and N [37,000,43,000). In this small region, compare A, B and C using the **same**:

| Canonical feature | Coordinates (m) | Correct 4-digit area | Purpose |
|---|---|---|---|
| Cabin | E 24,375 / N 37,860 | `2437` | Easy-to-spot point landmark |
| Bridge | E 25,750 / N 39,250 | `2539` | Must visually cross a coded stream/path |
| Lookout | E 27,120 / N 41,330 | `2741` | Distinct silhouette and later reverse-hunt target |
| Camp | E 28,350 / N 38,210 | `2838` | Discovery-reveal option |
| Spring | E 29,520 / N 40,380 | `2940` | Smaller point symbol / later legend test |

For all candidates use exactly the same camera, axis labels, object identities, objective sequence and selection validation. Do **not** change gameplay, target count or tutorial between conditions. First puzzle: find `2437` and two other **area** references; no landmark can substitute for actually reading the axes. Terrain should be plausible, but full contour science is R12.

**Deliverables:** three phone screenshots, one short interactive test APK with selectable art treatment if practical, an asset manifest, and basic device measurements.

**Score each on a 1–5 rubric (no scores assigned yet):**
1. **Immediate comprehension:** can a novice locate the grid line labels and know which cell is being asked for?
2. **Character and appeal:** does it look like a pleasant puzzle game, not a tactical GIS display?
3. **Readability:** map, labels, grid and icons at fit-to-screen and zoomed-in view; color and contrast accessibility.
4. **Coordinate integrity:** symbols/geometry anchored correctly after zoom and pan; area answers identical for A/B/C.
5. **Editing effort:** time/actions to move a trail or reskin the cabin without disturbing map answers.
6. **Asset and APK cost:** count of required assets, packed size, memory and draw responsiveness on a real phone.
7. **Future scale:** ability to add a second region or change a discovery objective without repainting the first map.

**Suggested user test:** 3–5 map-reading novices + at least one experienced navigator if feasible. Give the same puzzle sequence under counterbalanced visual conditions, ask neutral clarity/appeal questions, and observe genuine grid use. Do not report any preference or speed until actually measured.

**Decision gate:** select an art pipeline when **correctness is identical across three variants** and one has the best blend of comprehension, appeal and reasonable upkeep. If B clearly wins visually but costs too much to maintain, borrow its textures and silhouettes into C. If A is just as appealing and clearer, choose A. “Hybrid” is a **working recommendation**, not a loyalty test.

## 6. Asset production and rights rules

- **Original geometry first:** grid lines, coordinate labels, road/stream paths, contours and answer markers are data/geometry, not baked into painterly image files.
- **Original sprites next:** cabin, watchtower, campsite, bridge, rock, tree clusters; each has stable `assetId`, intended anchor (e.g. base-center), an outline-readable silhouette, intended zoom range and visual dimensions.
- **Textures last:** subtle paper/grass grain can add warmth but should never reduce clarity or encode mission answers.
- **Style sheet:** choose palette, outline thickness, highlight color, sprite lighting/perspective, label treatment and symbol meanings; document variants. Keep sprite perspective consistent (near-top-down rather than mixing isometric and flat).
- **Generated art is *concept material* until checked:** image generation can speed exploration of a style; no generated forest map image is accepted as proof of accurate geometry. Inspect/edit any candidate sprite, remove confusing duplicate landmarks, verify clean transparency and anchor.
- **Licensing:** store source, date, creator, licence, permitted commercial use, modifications, attribution and raw source file for every asset. Third-party maps/tiles/fonts and printed military manual figures are **not automatically free to redistribute**. The owner-supplied UK manual includes third-party Ordnance Survey mapping; we will not reuse its figures in shipped assets without appropriate rights. See [source register](SOURCE_REGISTER.md).

**Likely first asset set, not yet commissioned:** 1 backdrop texture, 3 terrain fills, 1 stream line style, 1 trail style, 5 identifiable landmark sprites, 3 decorative ground sprites and a small grid/marker styling set. Reuse before expansion; resist generating dozens of decorative buildings or a massive atlas for a 6×6 puzzle.

## 7. Decision record and next focused question

**R04 result: RESEARCHED / PROPOSED.** A hybrid authored map is the leading **prototype** candidate: code-drawn numbered grid + editable world-space terrain geometry + restrained cohesive landmark art. A pure code and an illustration-first rendering of the **same five-location test map** remain the fair comparison before the user approves a visual system.

**Not accomplished:** no finished map artwork, no screenshots, no interactive APK, no timing/memory measurements, no novice preference testing, no approved visual style. Do not mark them otherwise.

**Next one-at-a-time item: [R05 — world coordinates, feature geometry, sprite anchors and hit areas](OPEN_DESIGN_RESEARCH_BACKLOG.md).** That will specify exactly how an art asset maps to the authoritative training grid, including center/base anchors, large areas and grid-boundary tests. R06 then assesses how a player interacts with that accurate map.
