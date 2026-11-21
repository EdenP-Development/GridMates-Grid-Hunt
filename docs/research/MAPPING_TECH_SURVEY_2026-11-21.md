# Grid Hunt — Mapping Technology Survey
**Researched 2026-11-21. Status: RESEARCH / DECISIONS PROPOSED. No mapping library, sample code, APK, or test has been implemented in this pass.**

## Conclusion
For **fictional training maps** and a handful of verified landmarks, use a **custom Kotlin / Jetpack Compose Canvas** renderer with an independent **integer-meter grid and puzzle engine**. Save external map SDKs for a demonstrated need for genuinely georeferenced real-world map packs. Our first release is a puzzle, not GPS navigation software.

## Candidate comparison
| Option | What real mapping apps use it for | Grid Hunt fit | Reference |
|---|---|---|---|
| **Compose Canvas + gesture APIs** | Native shapes, transforms, graphics, pinch/pan input | **GH-01 choice**: simple original woodland map, numbered grid, marker selection | [Drawing](https://developer.android.com/develop/ui/compose/graphics/draw/overview); [touch/gestures](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures) |
| **MapLibre Native Android** | Vector/raster map styling, interactive basemaps, offline regions | **Later** if real georeferenced map packs or dense vector features justify it; requires separately licensed map data and proper CRS | [MapLibre Native](https://maplibre.org/maplibre-native/docs/book/); [Android offline](https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.offline/index.html) |
| **Mapsforge** | Compact offline OSM vector map files, hillshade and custom styles | **Later alternative** for locally bundled real terrain | [Project and LGPL waiver](https://github.com/mapsforge/mapsforge) |
| **Organic Maps** | Complete open-source offline navigation application with custom C++ core and Android glue | **Architecture inspiration only**, not a small Kotlin map widget to copy | [Project source](https://github.com/organicmaps/organicmaps) |
| **Google Maps SDK** | Hosted basemaps, polygons, markers and tile overlays | **Reject for MVP**: API/service terms, billing/credentials, unnecessary online dependencies | [SDK](https://developers.google.com/maps/documentation/android-sdk/overview); [data policy](https://developers.google.com/maps/documentation/android-sdk/policies) |
| **GeographicLib C++** | Coordinate conversions including UTM/UPS/MGRS and geodesics | **Later adapter/cross-check** if real georeferenced coordinates are used | [Library](https://geographiclib.sourceforge.io/doc/library.html) |
| **NGA GEOTRANS** | Official military geospatial coordinate and datum conversion tool | **Authoritative cross-check** for a real-MGRS upgrade, not needed for numeric training grid | [NGA](https://earth-info.nga.mil/) |
| **PROJ** | Conversions between coordinate reference systems and datums | **Later** for geodata import/reprojection; not an Android map UI | [PROJ](https://proj.org/en/stable/) |

**Important pitfall:** [GeographicLib-Java](https://github.com/geographiclib/geographiclib-java) describes a **Java implementation of geodesic routines**. Do not assume that its Java JAR directly includes GeographicLib C++'s MGRS converter. Verify an actual MGRS-capable implementation and licensing before integration.

## What we should borrow from GPS/map architecture
1. **World coordinates are authoritative.** Every feature and marker has an (E,N) in meters; artwork never decides the answer.
2. **A camera transforms world to screen** and has a reliable inverse for touch selection. Pan/zoom only change the camera.
3. **Independent layers**: backdrop, physical features, grid lines, labels, landmarks, player pins and puzzle HUD.
4. **View only what is needed**: clip/cull off-screen graphics; cache complex paths with [drawWithCache](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers) when profiling justifies it.
5. **Offline assets and content versioning**: a simple map manifest plus fixed chapter levels and deterministic Free Hunt seeds.
6. **Source/data/license separation**: an open-source map engine doesn't grant rights to map tiles, map illustrations, labels, fonts or derivatives.
7. **Precise training language**: 4-digit numeric references name 1 km **areas**, 6-digit references name 100 m **areas**. Neither is a GPS field-accuracy promise.

## Gates for changing architecture
- **GH-01:** standalone Compose renderer, 4-digit correct square, pan/zoom, 3–5 objectives; test on a real phone.
- **GH-03:** 100 m subdivisions and reverse plotting using the SAME world/camera math; show a zoom/reticle to avoid tiny tap targets.
- **Only if needed:** compare MapLibre/Mapsforge against custom renderer on real use case, measuring APK size, offline ease, startup, gesture responsiveness, licensing and correctness.
- **Before claiming true MGRS:** real zone and 100 km square identifiers, datum, projection, validated conversion and source georeferencing, with official cross-check fixtures.

Sources and integration policy: [Technical reference index](MAPPING_TECHNICAL_REFERENCE_INDEX.md).