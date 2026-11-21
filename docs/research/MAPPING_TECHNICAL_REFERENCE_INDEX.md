# Mapping Technical Reference Library
**Updated 2026-11-21 — official documentation and open-source app reference links, not imported code.**

## Android drawing, gestures, performance, storage
- [Compose drawing / DrawScope](https://developer.android.com/develop/ui/compose/graphics/draw/overview)
- [Compose gesture handling, touch slop and multi-pointer transforms](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures)
- [Compose panning and zooming](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/multi-touch)
- [Compose drawWithCache and draw modifiers](https://developer.android.com/develop/ui/compose/graphics/draw/modifiers)
- [Android offline-first app architecture](https://developer.android.com/topic/architecture/data-layer/offline-first)
- [Android DataStore](https://developer.android.com/topic/libraries/architecture/datastore/) and [Room](https://developer.android.com/training/data-storage/room)

## Mapping applications / map SDKs
- [MapLibre Native Android](https://maplibre.org/maplibre-native/docs/book/), [style specification](https://maplibre.org/maplibre-style-spec/), [offline regions](https://maplibre.org/maplibre-native/android/api/-map-libre%20-native%20-android/org.maplibre.android.offline/index.html), [PMTiles Android](https://maplibre.org/maplibre-native/android/examples/data/PMTiles/)
- [Mapsforge](https://github.com/mapsforge/mapsforge), [compact binary map format](https://github.com/mapsforge/mapsforge/blob/master/docs/Specification-Binary-Map-File.md)
- [Organic Maps source code](https://github.com/organicmaps/organicmaps) — complete offline GPS app architecture; not a turn-key puzzle map renderer
- [Google Maps SDK Android](https://developers.google.com/maps/documentation/android-sdk/overview), [restrictions](https://developers.google.com/maps/documentation/android-sdk/policies)

## Spatial references and conversion
- [NGA GEOTRANS and MGRS](https://earth-info.nga.mil/)
- [FGDC USNG reading reference](https://www.fgdc.gov/usng/how-to-read-usng)
- [GeographicLib C++](https://geographiclib.sourceforge.io/doc/library.html) and [GeographicLib-Java geodesic routines](https://github.com/geographiclib/geographiclib-java)
- [PROJ coordinate operations](https://proj.org/en/stable/operations/index.html), [USGS The National Map](https://www.usgs.gov/programs/national-geospatial-program/national-map)

## Offline formats and tile policy
- [PMTiles](https://docs.protomaps.com/pmtiles/), [OGC GeoPackage](https://www.ogc.org/standards/geopackage/)
- [OSM raster tile usage policy](https://operations.osmfoundation.org/policies/tiles/) and [OSM vector tile policy](https://operations.osmfoundation.org/policies/vector/)
- [Mapsforge .map](https://github.com/mapsforge/mapsforge/blob/master/docs/Specification-Binary-Map-File.md)

## Grid Hunt synthesis
- [Technology survey and decision](MAPPING_TECH_SURVEY_2026-11-21.md)
- [World/screen math and rendering design](MAP_ENGINE_MATH_AND_VIEWPORT.md)
- [Offline formats and licences](OFFLINE_MAP_FORMATS_LICENSES.md)
- [Verification and engineering spikes](MAP_ENGINE_TEST_AND_SPIKE_PLAN.md)

For each library adopted in a future PR, record **exact version, API signature, compatible Android target, license, actual test and replacement path**. Do not treat a document link as proof that code has been integrated.