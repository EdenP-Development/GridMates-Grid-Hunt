# Source Register, Provenance and Rights

**Purpose:** a single inspectable inventory for Grid Hunt research. This is not a game-asset library.  
**Updated:** 2026-11-21

## Priority order when sources disagree

1. Official **current** coordinate/technical authority for our specific rule (e.g., NGA MGRS; FGDC USNG; USGS UTM).
2. Official instructional publications for examples and vocabulary.
3. Historical manuals for possible lessons/techniques, explicitly labeled with their jurisdiction/date.
4. Third-party explainers only as pointers, not as independent technical proof.

Keep the distinction between **source statements**, **our educational interpretations**, **proposed game mechanics**, and **tested behavior** visible in docs.

## Registered sources

| ID | Source and link | Role | Status / caution |
|---|---|---|---|
| UK-RSMS-2010-01 | [*Military Map Reading*, UK Royal School of Military Survey, v2.0 Dec 2010 — third-party PDF mirror](https://bootcampmilitaryfitnessinstitute.com/wp-content/uploads/2025/10/23bcf-military-map-reading-v2.0-2010-12.pdf) | Core lesson inspiration: grids, Romers, scale, contours, bearings, map-ground association and routes. | User uploaded and 44-page content reviewed. Historical UK training context. **Do not republish entire PDF**; contains OS illustrations with separate rights. See [study notes](MILITARY_MAP_READING_2010_SOURCE_NOTES.md). |
| NGA-MGRS | [NGA Coordinate Systems](https://earth-info.nga.mil/?action=coordsys&dir=coordsys) | MGRS syntax, grid zones, 100 km identifiers, datum and convention cross-check. | Official authority; implementation still needs fixture tests. |
| FGDC-USNG | [FGDC How to Read USNG](https://www.fgdc.gov/usng/how-to-read-usng) | Beginner-friendly US national grid reading and reference resolution examples. | Official US source. USNG is not a substitute for validating all MGRS global details. |
| USGS-UTM | [USGS UTM map measurements](https://www.usgs.gov/faqs/how-are-utm-coordinates-measured-usgs-topographic-maps) | Coordinates on actual topo maps; interpolation and meaningful scale. | Official US mapping educational source. |
| USGS-TOPO | [USGS National Map](https://www.usgs.gov/programs/national-geospatial-program/national-map) | Future possible georeferenced terrain inputs. | Not imported; check each map product's datum, data rights and suitability. |
| OS-LICENSING | [Ordnance Survey Publishing Licence](https://www.ordnancesurvey.co.uk/licensing/publishing-licence) | Rights check for maps shown in UK booklet. | Certain OS OpenData products are separately permissive; do not assume the booklet's OS extracts are. |
| GM-SUDOKU | [Sudoku reusable foundation audit](https://github.com/PlayerEden/SudokuTogether/blob/main/docs/REUSABLE_ANDROID_PUZZLE_FOUNDATION.md) | Existing Android design/QA/reuse boundaries. | Reference design, **not** ready-to-import shared engine. |
| GM-WORDSEARCH | [Word Search master design](https://github.com/PlayerEden/GridMates-Word-Search/blob/main/docs/MASTER_GAME_DESIGN.md) | Chapters, deterministic puzzle supply and UX policy. | Check active code branches before claiming implementation parity. |

## Rights checklist for importing a reference

- Is the material **text, diagram, full map, font, software, photo, or data**? Rights can differ.
- Is it the publisher/original owner or a third-party mirror? A public download isn't an open licence.
- What uses are allowed: read, quote, reproduce unchanged, adapt, commercial release, redistribute?
- Is third-party map data embedded? (This UK source **does** contain Ordnance Survey content.)
- If unclear: link and paraphrase for internal study; don't add full PDF, figures, scans or derived map tiles to public repo/app.
- Give attribution and preserve the exact source/version/page mapping and uploaded-file hash.
- Prefer newly drawn, original game assets and numerical fixtures rather than scanned manual diagrams.

## Source corrections and update protocol

For material technical issues, log: claim -> file/page -> official current cross-check -> proposed correction -> test fixture -> reviewer status. Old lessons can remain as historical context, but should never override tested present-day coordinate rules. 

### Known issues to monitor

1. **British National Grid vs MGRS:** the UK booklet's p. 5 says the latter cannot be used on British/Irish *maps*; interpret as printed map-system context, **not a universal geographic prohibition**.
2. **Precision vs accuracy:** 4/6/8-digit MGRS numeric portion references 1 km/100 m/10 m squares; a tap/phone display does not guarantee field accuracy.
3. **Magnetic direction:** location, date and map datum/declination matter. Never adopt a 2010 conversion example as the current north correction in a new area.
4. **Training grid vs real georeference:** invented map cannot become operational MGRS without real projection/zone/square/datum.
5. **Rights vs accessibility:** public availability of a PDF or map image does not prove redistribution rights.

## Future additions

Current U.S. Army land-navigation training circular (verify exact edition on official publisher), official terrain-feature symbol reference, accessibility/touch map research, navigation education evidence, and validated MGRS conversion/fixtures.