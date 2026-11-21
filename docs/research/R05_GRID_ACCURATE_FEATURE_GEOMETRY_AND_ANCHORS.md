# R05 — Grid-Accurate Landmarks: Feature Geometry, Anchors and Placement Rules

**Researched:** 2026-11-21  
**Focus:** How each tree, cabin, bridge, river, road, lake, or other map feature corresponds to a mathematically correct place on a fictional grid, even when stylized art is drawn at a different apparent size.  
**Status:** SOURCE-BACKED TECHNICAL RESEARCH + PROPOSED DATA CONTRACT. **No feature engine, art pack, map prototype, Android build, simulation, source code tests, or player tests exist yet.**  
**Previous:** [R04 — Map creation and artwork](R04_MAP_CREATION_AND_ARTWORK_RESEARCH.md). **Next:** R06 — mobile tap, marker, reticle, pan and zoom interaction.  
**Scope:** map world truth, geometry, asset placement and content-validation semantics. Do **not** turn this into a GIS platform or full GPS project.

## 1. Research conclusion

**Keep three things separate:**
1. **Feature geometry / reference location** — where an object actually is within one authoritative world-meters map.
2. **Display art** — sprites, texture, symbols, offsets, sizes, labels and decorations; they can be visually larger or smaller than the physical feature.
3. **Interaction / answer semantics** — whether a specific puzzle asks to select a 1 km cell, report a 100 m cell, pick a named symbol, select a polyline segment, or identify an area.

**The puzzle answer derives from 1 and the explicitly chosen rule in 3, never from pixels in 2.**

A large illustrated cabin may overlap two adjacent grid squares on screen and still have one canonical point (an entrance, center/survey mark, etc.). Conversely, a lake or forest genuinely spans multiple squares and should not be silently treated as a point unless the question clearly specifies a named point on that feature.

## 2. External documentation and the actual lessons

| Source | Supported technical fact | What Grid Hunt should learn | Important caveat |
|---|---|---|---|
| [IETF RFC 7946, GeoJSON](https://datatracker.ietf.org/doc/html/rfc7946#section-3.1) | Geometric feature types include **Point, LineString, Polygon**, including polygon holes and multi-feature types. | Represent a cabin by a point, trail/stream by a polyline, lake/forest by an area. | **RFC 7946 GeoJSON uses WGS84 longitude/latitude** ([§4](https://datatracker.ietf.org/doc/html/rfc7946#section-4)). Our invented **E,N meter arrays are NOT valid standard GeoJSON coordinates**; use our own explicit local schema, not a falsely labeled GeoJSON file. |
| [Tiled — Working with Objects](https://doc.mapeditor.org/en/stable/manual/objects/) | Map editor supports point, rectangular/ellipse/polygon/polyline and tile objects with their own position and properties. | Store semantic feature type and stable coordinates, with optional editor tooling later. | Tiled default editor coordinates and sprite alignment are editor-specific; do not confuse its pixels with meters or its Y-down convention with north-up world coordinates. |
| [Tiled — Object Alignment / tilesets](https://doc.mapeditor.org/en/stable/manual/editing-tilesets/) | Tile objects have configurable **object alignment**; unspecified tile alignment is generally bottom-left, except isometric bottom-center. | A sprite's visual pivot must be explicit (center, base, etc.), not guessed based on transparent PNG bounds. | These defaults belong to Tiled. We can choose our own consistent sprite anchors, and do not need to adopt Tiled. |
| [MapLibre Style Spec — Symbol layers](https://maplibre.org/maplibre-style-spec/layers/) | Icon/text symbol styling distinguishes anchor placement, offsets and collision/padding rules. | Keep a landmark's **world anchor**, sprite visual alignment, label offset and touch-hit policy separate. | MapLibre is an architectural reference, not an installed dependency. Label collision avoidance is not a puzzle-answer validator. |
| [USGS — US Topo Cartographic Symbol Guide](https://www.usgs.gov/ngp-standards-and-specifications/us-topo-cartographic-specifications-map-symbol-guide) | Cartography depicts features using appropriate **point, line or area representations**; even a small reservoir may become a point symbol at one scale and a polygon at another. | Switching visual representation with zoom is acceptable, but authoritative feature geometry must stay constant. | We should create our **own original game symbols** with an understandable legend; don't copy protected maps or imply operational map certification. |
| [PostGIS ST_Contains vs ST_Covers](https://postgis.net/docs/ST_Contains.html), [ST_Covers](https://postgis.net/docs/ST_Covers.html) | A geometry's boundary has meaningful spatial-predicate semantics; `contains` does not include all boundary-only relationships, whereas `covers` includes boundary. | Explicitly define what a tap on a feature edge means and avoid accidental ambiguous answer rules. | We do **not** need PostGIS in Android. Our simple local geometry routines can implement documented consistent policies. |
| [Android — Graphics in Compose](https://developer.android.com/develop/ui/compose/graphics/draw/overview) | Compose Canvas uses top-left origin and Y-down **screen pixels**, with drawing and transforms; world space can be distinct. | Convert world (E,N) through one reversible camera; never let bitmap pixels become authoritative reference coordinates. | Android drawing APIs do not supply land-navigation projection, MGRS or feature-to-grid grading. |

### Coordinate-system warning

In **Grid Hunt's fictional training square** we can choose local `EastingMeters` and `NorthingMeters`. This teaches east-first/north-second reading and the numeric portion of a grid reference. It is **not** globally valid MGRS/USNG without a true zone, square, datum and verified conversion. Source: [accuracy contract](../MAP_READING_ACCURACY.md), [FGDC USNG](https://www.fgdc.gov/usng/how-to-read-usng).

## 3. Proposed geometry model — very small and game-focused

We do **not** need OGC/GEOS or a complete GIS geometry engine. A minimal sealed type / discriminated union is enough:

- **Point:** one canonical world coordinate. Used for a survey mark, cabin reference point, lookout, campsite pin, bridge crossing reference.
- **Polyline:** ordered world coordinate segments. Used for road, trail, stream, fence, ridge-line *symbol*.
- **Polygon:** ordered outer world-space ring, optionally holes. Used for forest, marsh, lake, physical building footprint, terrain area.
- **Multi geometry (later if justified):** several separated forest areas or lake islands under one name; can initially author these as separate feature IDs linked by group ID.

**Store feature geometry separately from decorative art**, so a road can be accurately drawn and queried while trees are stylistic embellishments.

### The data interface (illustrative, not a committed runtime schema)

```json
{
  "mapId": "woodland-training-01",
  "revision": 1,
  "crs": "gridhunt-local-meters-v1",
  "boundsMeters": {"west": 24000, "south": 37000, "east": 30000, "north": 43000},
  "gridSpacingMeters": 1000,
  "features": [
    {
      "id": "cabin-01",
      "kind": "point",
      "geometryMeters": [24375, 37860],
      "referencePolicy": "point-anchor",
      "display": {"spriteId": "cabin-woodland", "spriteAnchor": [0.5, 1.0], "labelOffsetDp": [0, -8]},
      "tags": ["shelter", "building"]
    },
    {
      "id": "stream-01",
      "kind": "polyline",
      "geometryMeters": [[25400, 39250], [26000, 39250]],
      "display": {"strokeStyle": "stream-blue"}
    },
    {
      "id": "trail-01",
      "kind": "polyline",
      "geometryMeters": [[25750, 39000], [25750, 39500]],
      "display": {"strokeStyle": "trail-dash"}
    },
    {
      "id": "bridge-01",
      "kind": "point",
      "geometryMeters": [25750, 39250],
      "referencePolicy": "crossing-point",
      "display": {"spriteId": "bridge-wood", "spriteAnchor": [0.5, 0.5]},
      "relations": {"crosses": ["stream-01", "trail-01"]}
    },
    {
      "id": "lake-01",
      "kind": "polygon",
      "geometryMeters": [[[26400, 37800], [27200, 37800], [27200, 38500], [26400, 38500], [26400, 37800]]],
      "referencePolicy": "area-only",
      "display": {"fillStyle": "water"}
    }
  ]
}
```

- The `[0.5,1.0]` sprite anchor is a **normalized image coordinate**, e.g. center-bottom. It is **not** the cabin's easting/northing and does **not** define its reference square.
- `labelOffsetDp` is presentation only, in Android density-independent units. It is NOT a meter distance on the map.
- The example stream and trail meet at (25,750,39,250), precisely the bridge's world reference point.
- This simple JSON is **our custom local-meter schema**, **not** standards-compliant RFC 7946 GeoJSON; use a distinct filename/format identifier.
- R05 does not define how all polygons and lines will be selected on screen; R06 will test interaction and accessible alternatives.

## 4. Correctness of the canonical point

An authoritative cabin point at `E=24,375m; N=37,860m` lies in:

| Resolution | Easting range | Northing range | Training reference |
|---|---|---|---|
| 4-digit (1 km) | [24,000,25,000) | [37,000,38,000) | `2437` |
| 6-digit (100 m) | [24,300,24,400) | [37,800,37,900) | `243378` |

A four-digit map clue asks the player to select the **square**, not to touch the cabin icon. In Reverse Hunt, the named cabin **point** generates the string for its containing area using floor/truncation rather than rounding.

**Grid-line convention:** Every cell includes its west/south edge and excludes its east/north edge. Thus a canonical point at E=25,000, N=38,000 belongs to `2538`, **not** `2437`. Maintain this convention across zoom and screen density. A landmark on an exact line is allowed, but avoid it for novice decorative targets because its art can create confusion.

For a **bridge** at E=25,750,N=39,250, the four-digit area is `2539`, six-digit is `257392`.

## 5. Distinguish geographic footprint, symbol art, hit area, and puzzle answer

| Layer of truth | Example | Allowed to change when reskinning? | Affects grid answer? |
|---|---|---|---|
| Canonical reference | Cabin point (24375,37860) | **No**, unless content/map revision intentionally migrates | **Yes** |
| Physical/world geometry | Lake polygon or path vertices | Only deliberate map/content revision | **Yes** for shape/area-based tasks |
| Symbol/art sprite | Cabin image, transparency, shadow, offset | **Yes** | **No** |
| Text/label placement | Name floats above symbol | **Yes** | **No** |
| Touch/hit target | Generous 48dp icon selection zone if mode requires tapping icon | **Yes, within explicit interaction rules** | **Not** for plotting a numeric grid area |
| Answer rule | e.g. "select the bridge's reference square" vs "tap the bridge symbol" | Changes only with mission spec | **Yes** |

**Most important safeguard:** enlarging an icon's hit target **cannot enlarge a 1 km or 100 m numeric answer square**. In Coordinate Hunt, use world-to-cell mapping; in Landmark Hunt, use symbol/feature selection. If icon hit targets overlap, offer disambiguation or a map zoom, not silent arbitrary winner selection.

## 6. Boundary and geometry policies that need decisions

### Point on a grid-line boundary
Use floor for each positive local coordinate to assign one and only one containing square. [24,000,25,000) includes 24,000 and excludes 25,000. Exact floating values may suffer rounding: prefer integer-meter canonical positions and test screen→world boundary tolerance separately.

### Line or polygon crossing multiple squares
The feature has **no single natural four-digit grid reference**. Don't ask `what is the grid of the lake?` unless the mission explicitly identifies a **reference point** (e.g. "southern tip of the lake") or asks for **all intersected squares**. A big forest is not automatically assigned to its geometric centroid; a centroid can even lie outside an odd shape or in a hole.

### Tap exactly on polygon boundary
Different spatial predicates include/exclude shared boundaries differently (see PostGIS `contains` vs `covers`). **Proposal:** for freehand feature inspection, allow a boundary hit, but if multiple features meet at the tap provide a choice or prefer the explicitly highlighted mission target; **never** use this rule to change canonical grid-cell boundaries.

### Symbols near grid edges
Choose a declared **map reference point**, visually show its location with a small dot when necessary, and optionally avoid ambiguous large symbols near grid boundaries in beginner chapters. Art can spill over lines while the point stays fixed. If art implies a whole building physically occupies multiple areas, the mission should ask for the marked entrance, center pin or other explicit point.

### A path and stream crossing
Require the bridge's canonical point to match the crossing geometry **within a documented world-meter geometry tolerance** (ideally exactly on both segments for a handcrafted map). Do not snap a bridge using image pixels after export. Geometry intersection or point-to-segment tests suffice; full routing/pathfinding is not necessary.

### Lake or forest obstacles
If later mission asks a route to avoid a lake, use the **actual polygon boundary** and a declared player route width/buffer, **not** a painted shoreline blur. Defer such gameplay until R17/R22 route/distance research.

## 7. Placement restrictions for replayable / random maps

**Begin with approved positions, not unconstrained random coordinates.** For each landmark category, define a set of valid authored world-space positions or placement predicates:

| Category | Placement rule hypothesis | Test |
|---|---|---|
| Cabin/campsite | At known dry ground, optional short access to trail; outside lake/marsh polygons | Point must be in dry region; icon readable |
| Bridge | At a declared stream/road crossing | Point lies on both paths; crossing has one named bridge ID |
| Lookout | At authored vantage point; if contour model exists later, at consistent higher ground | Height/topology verified where relevant |
| Spring | At authored point on/near a water feature if description implies it | Never place arbitrary spring just because a blue icon fits |
| Tree/rock decoration | In approved zones, no occlusion of labels, symbols or selection UI | Visual density and symbol collision lint |
| Lake/forest | Versioned polygon authored or generated by validated terrain rules | Closed valid rings; no self-intersections; no unapproved overlaps |

**Randomizable:** target selection, clue order, optional decorative trees, and approved landmark choices.  
**Careful with:** moving cabins/bridges. When a landmark moves, all its coordinate-derived clues, geometry relations, IDs, saved mission version, and any prepainted art must update in the **same deterministic map revision/seed**.  
**Not first-prototype scope:** generating fully realistic elevation, stream networks, bridges and topography from unconstrained noise.

**Do not hard-reject every overlap.** Roads cross rivers and forests may touch streams; whether overlap is permissible depends on the feature types and explicit map rules.

## 8. One-source-of-truth and content versioning

- Stable `mapId`, `revision`, `featureId`, `geometryVersion` (optional) and `generatorVersion`.
- References use **canonical feature geometry**, never screen pixels, asset image bounds or manual grid strings stored alongside points without validation.
- Chapter puzzles pin to approved map revision/feature IDs. Seeded missions reproduce `map revision + generator revision + seed` and persist expanded target list for robust saved games.
- Every icon includes `assetId`, source/licence, reference-point anchor, style category and visibility range; cosmetic asset revision is independent of geometry revision where possible.
- A renderer may use the same canonical data to present code-only, illustrated and hybrid maps without shifting answers.
- A lightweight debug mode should overlay feature IDs, canonical reference dots, line/polygon paths, active 1 km cell and icon hit targets, enabling visual audit by artists/developers.

## 9. R05 test matrix (PROPOSED; not executed)

| ID | Fixture / action | Correct outcome |
|---|---|---|
| L01 | Cabin E24375,N37860 | 4-digit `2437`; 6-digit `243378` |
| L02 | Cabin sprite changes shape, dimensions and transparent padding | **Same** canonical reference |
| L03 | Cabin sprite is 200 screen px wide and crosses into next grid cell | Numeric grid selection and Reverse Hunt unchanged |
| L04 | Point E24999,N37999 then point E25000,N38000 | `2437` then `2538` |
| L05 | Bridge E25750,N39250 on authored trail/stream | Both polyline tests pass; `2539`, `257392` |
| L06 | Bridge sprite moved on screen but canonical point unchanged | Geometry relation and answer unchanged; flag excessive visual misalignment |
| L07 | Bridge world point moved off stream 50 m with no updated stream path | Placement validator rejects/flags |
| L08 | Lake polygon overlaps several 1 km cells | No unqualified single `lake grid` answer |
| L09 | Polygon point on edge and at polygon hole | Edge/holes follow documented interaction predicate |
| L10 | Giant decorative forest art covers cabin | Occlusion QA flags; actual position and answer unchanged |
| L11 | Duplicate named camps at same requested resolution | Generator demands disambiguating clue / rejects ambiguous mission |
| L12 | Point/line/polygon with out-of-bounds coordinates, empty line or malformed polygon | Content validator rejects |
| L13 | Map changes visual theme while maintaining geometry revision | All refs and answer keys unchanged |
| L14 | Same authored positions + map revision + random seed | Same candidate locations and references |
| L15 | Zoom/pan, screen density, portrait/landscape | Feature world point remains identical, symbol anchored; no drift |
| L16 | Numeric square selection vs tapping its oversized decoration | Different declared mode semantics; no accidental acceptance from sprite size |
| L17 | Feature label offset or accessible 48dp landmark hit area grows | Does not enlarge canonical 100 m / 1 km coordinate answer area |
| L18 | Map pack claims standardized GeoJSON but contains fictional projected meters | Reject the misleading format claim; use explicit custom local-meter schema |

**Pass gate before adding random landmark placement:** L01–L18 in automated geometry/content tests, plus a human map-authoring visual pass on multiple zoom levels and at least one physical Android screen. A manually audited fixed map may be introduced earlier if it passes the relevant subset.

## 10. Concrete first experiment

Use the [R04 shared 6×6 map](R04_MAP_CREATION_AND_ARTWORK_RESEARCH.md) and **five identical verified landmark coordinates** across all visual treatments. Author:
1. One cabin point with a large center-bottom anchored illustration.
2. One bridge point placed at a road–stream crossing.
3. One 4-corner lake polygon crossing cell boundaries.
4. A short trail and a river as polylines.
5. A debug overlay with canonical dots, world geometry, 1 km cell boundaries and visually drawn icon hit circles.

**Observe (future):** if zoom or art changes shift a landmark's plotted numeric reference, the data/sprite/camera separation has failed. If an outline/large icon causes novice confusion at a cell boundary, revise the **presentation**, not the MGRS-style square semantics.

## R05 outcome

**Recommendation:** commit a minimal *local-meter* feature registry with explicit point/line/polygon geometry, sprite pivot, optional canonical feature point, placement relations and **mode-dependent** interaction/answer policy. This is the smallest architecture likely to support fun illustrated maps plus correct map-reading practice. Use official reference materials for geometry vocabulary, not as code dependencies.

**Not decided/implemented:** renderer, map art appearance, coordinate core, geometry library, editor software, actual random landscape generator, gesture behavior, or Android APK.

**Next focused research (R06):** on-phone selection: full square tap, marker placement, reticle for 100 m precision, pinch pan zoom, undo and accessibility. Do not mix that UI research into the underlying geometry/coordinate rules.
