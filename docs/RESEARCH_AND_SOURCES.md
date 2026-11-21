# Research & Reference Sources

**Prepared:** 2026-11-21  
**Purpose:** support a friendly game with real, correctly represented map-reading habits. External sources inform rules; the game does not claim military certification.

## Primary/official references

1. **National Geospatial-Intelligence Agency — Coordinate Systems / MGRS.** https://earth-info.nga.mil/?action=coordsys&dir=coordsys  
   Official context for UTM and MGRS, zone/square designators and eastings/northings. Recheck any detailed MGRS conversion claims against its current documentation and validated test fixtures.

2. **USGS — How are UTM coordinates measured on USGS topographic maps?** https://www.usgs.gov/faqs/how-are-utm-coordinates-measured-usgs-topographic-maps  
   UTM grid lines/tick marks (often 1,000 m) and measuring/interpolating on US topo products.

3. **USGS — 27 Ideas for Teaching with Topographic Maps.** https://www.usgs.gov/educational-resources/27-ideas-teaching-topographic-maps-0  
   Educational reference for grids, scale, contours, landmarks and map interpretation; shows why higher map precision requires matching map resolution.

4. **USGS — The National Map.** https://www.usgs.gov/programs/national-geospatial-program/national-map  
   Potential future source of georeferenced U.S. terrain and map data. Any exact map chosen needs a recorded source, date, map scale, datum/projection and rights review.

5. **NGA Geomatics / coordinate system documentation.** https://earth-info.nga.mil/  
   Further MGRS technical review before claiming fully-qualified real-world references.

## Research findings relevant to game decisions

- Eastings and northings are measured in meters in UTM/MGRS context. The numeric 4/6/8/10-digit portion names progressively smaller **areas**, not location-certainty claims.
- USGS map sources can eventually help with real terrain, but first release does not require live GIS, GPS, map downloads or complicated topography.
- A simplified invented woodland map with rigorous coordinate transforms is a valid **training simulation**; it does **not** automatically become real-world MGRS just because the digits look similar.
- Map art and symbols can be playful if grid orientation, spacing, map scale and questions stay consistent.

## Sibling project references

- [Sudoku README](https://github.com/PlayerEden/SudokuTogether/blob/main/README.md) and [Android foundation plan](https://github.com/PlayerEden/SudokuTogether/blob/main/docs/REUSABLE_ANDROID_PUZZLE_FOUNDATION.md).
- [GridMates brand/series vision](https://github.com/PlayerEden/SudokuTogether/blob/main/docs/GRIDMATES_BRAND_AND_SERIES.md).
- [Word Search master design](https://github.com/PlayerEden/GridMates-Word-Search/blob/main/docs/MASTER_GAME_DESIGN.md) and [parity-first quality plan](https://github.com/PlayerEden/GridMates-Word-Search/blob/main/docs/PARITY_FIRST_QUALITY_PLAN.md).

## Open research tasks before full build

- Verify selected Android SDK/Compose/Gradle versions against current supported stable releases before installing dependencies.
- Decide whether map drawing begins vector-only (preferred for clean zoom) or uses a compact image background with vector grid overlay.
- Independently cross-check reference encoder/truncation and boundary fixtures with at least one trusted MGRS implementation if moving beyond local training grids.
- Review individual external imagery, symbols and fonts before packaging: source link, license, scope, attribution and whether redistribution is allowed.
- If adding real geodata later: confirm zone, square, datum, map georeferencing, real location validity, and source/version tests.

**No images, map packs or borrowed code have been imported by this documentation pass.**