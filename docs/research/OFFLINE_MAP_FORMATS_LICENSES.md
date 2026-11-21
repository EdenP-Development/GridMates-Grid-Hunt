# Offline Map Formats, SDK Integration and Map-Data Rights
**Status:** research; no third-party map source or SDK included in Grid Hunt.

## Formats worth knowing
| Format | Purpose | When to consider |
|---|---|---|
| **First-party JSON/vector map manifest** | Illustrated training map, world points, feature IDs, symbols and puzzle targets | **MVP choice**; few small offline map packs |
| **GeoJSON** | Exchange geographic features/polygons in a defined coordinate reference convention | Optional import/export if real GIS feature data is introduced |
| **MapLibre style + vector/raster tiles** | Style rules, basemap tiles and symbol layers | When there are genuinely georeferenced maps and many visible map features |
| **MBTiles** | Single SQLite tile archive | Future bundled tile data; reader and licence required |
| **PMTiles** | Single-file tile pyramid / range-based tile access | MapLibre Android v11.7.0+ examples support local asset PMTiles URLs; no offline-pack download/caching for PMTiles source. [Official example](https://maplibre.org/maplibre-native/android/examples/data/PMTiles/) |
| **OGC GeoPackage** | SQLite container for vector geospatial features and tile matrices | Future professional GIS source interchange. [Standard](https://www.ogc.org/standards/geopackage/) |
| **Mapsforge `.map`** | Compact OSM-derived offline vector data | Only if choosing Mapsforge toolchain. [Project](https://github.com/mapsforge/mapsforge) |

**File formats do not solve MGRS conversion by themselves.** Offline storage of geographic tiles is not a coordinate validator.

## Licensing and policy findings
- **MapLibre engine code, map styles, glyphs and basemap datasets are separate assets with separate rights.** [MapLibre style specification](https://maplibre.org/maplibre-style-spec/).
- **Mapsforge** uses LGPLv3 with a special upstream waiver for specific linking/relinking provisions; inspect its full license/NOTICE before shipping. [Upstream license summary](https://github.com/mapsforge/mapsforge).
- **Public OSM raster tiles** at `tile.openstreetmap.org` prohibit bulk downloading/prefetch for offline packs. [OSMF raster tile policy](https://operations.osmfoundation.org/policies/tiles/). Public **vector tiles** have their own bulk-download restrictions. [OSMF vector policy](https://operations.osmfoundation.org/policies/vector/). Obtain offline data from a provider/data source that explicitly permits the intended redistribution and follow required attribution/ODbL terms.
- **Google Maps SDK** map overlay support does not grant permission to download and redistribute Google basemap content. [Google Maps policies](https://developers.google.com/maps/documentation/android-sdk/policies); [usage and billing](https://developers.google.com/maps/documentation/android-sdk/usage-and-billing).
- **GeographicLib C++** is MIT licensed and includes geographic conversion/MGRS routines. The **Java implementation** is a separate geodesic package and must not be assumed to include MGRS conversion. [C++ docs](https://geographiclib.sourceforge.io/doc/library.html); [Java project](https://github.com/geographiclib/geographiclib-java).
- **Owner-provided UK 2010 training PDF** includes third-party Ordnance Survey map examples; its source review already advises linking rather than copying full PDF/maps into public repo. [Research rights register](SOURCE_REGISTER.md).

## Recommended data supply for v1
- Author a handful of original maps and reusable vector symbols (nature, paths, rivers, cabins, bridges).
- Store map/world geometry and landmark coordinates separately from art.
- Bundle maps and missions in APK (offline, no GPS/location permission).
- Add stable map revision, attribution/source ledger and checksums. Include no remote basemap tiles by default.
- Only scale up to licensed map tile archives after a tangible map-size/reality requirement and tests.

## Integration gate for any library or dataset
Record exact package/version and upstream commit, licence/NOTICE, map-data licence and download rights, attribution UI, APK size, offline performance, supported Android levels, expected data source, security/privacy, install-over effects, rollback and tested coordinate fixtures. No library should be added solely because other GPS apps use it.