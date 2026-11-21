# Source Study — Military Map Reading, v2.0 (December 2010)

**Source ID:** UK-RSMS-2010-01  
**Status:** RESEARCH / HISTORICAL TRAINING REFERENCE — not app content, not a test execution and not current military doctrine validation.  
**Prepared:** 2026-11-21

## Source record

- **Title:** *Military Map Reading*.
- **Attributed preparer:** Geospatial Information Management Wing / Royal School of Military Survey, UK Defence Intelligence Security Centre.
- **Version/date/pages:** Version 2.0, December 2010, 44 PDF pages; 43 numbered instructional pages.
- **Underlying primary publication named on p. 1:** *Manual of Map Reading and Land Navigation*, Army Code 71874, Issue 1.0, April 2009. This manual was **not** supplied or independently reviewed.
- **File provided by project owner:** `23bcf-military-map-reading-v2.0-2010-12.pdf`.
- **Uploaded file SHA-256:** `edfa4433f8a4c674896d3ba5ffa27f60098ae350193628fc111911283fe7c7ad`.
- **Public third-party copy for convenient reference:** https://bootcampmilitaryfitnessinstitute.com/wp-content/uploads/2025/10/23bcf-military-map-reading-v2.0-2010-12.pdf . This is a **mirror**, not a verified official download.
- **Copyright/rights:** PDF p. 43 permits acknowledged use of MoD material **unchanged**, but explicitly excludes unlicensed publication of the Ordnance Survey mapping. Page 41 also flags OS map reproduction restrictions. See [rights policy](SOURCE_REGISTER.md). **Do not commit the full mixed-rights PDF or crop/extract its mapped illustrations to this public GitHub repository without a specific permissions review.** We store original summaries and page references instead.
- **Citation label for internal docs:** RSMS *Military Map Reading* (2010), p. X. Do not represent it as a US Army manual.

## Faithful study notes, tied to the booklet's printed pages

| Pages | What the booklet teaches | Consequence for Grid Hunt |
|---|---|---|
| 3–4 | Easting/northing grid order; four figures identify a 1 km square; subdivide into tenths for six figures; leading zeroes matter. | First game mechanic: east first, north second. Four-digit target = whole square. Six-digit = 100 m area. |
| 5 | MGRS includes numeric reference, 100 km square letters and grid zone designation; datum must match. Conventional signs depend on the specific map legend. | Never call numeric-only training refs standalone real-world MGRS. Provide a map legend and don't assume all maps use identical symbols. |
| 6–7 | Romer aligns to grid and uses correct map scale; finer divisions beyond source map's usefulness are cautioned against. | Optional digital plotting overlay later, not required for MVP; do not grade pixel-center accuracy. |
| 8–10 | Scale ratios, direct vs route/indirect distance, measuring with scale line. | Future Scale Hunt / route-distance mode using real distances from game world coordinates. |
| 11–14 | Contour interval/spacing; hills, ridges, saddles, valleys, re-entrants/draws, spurs. | Optional contour symbol and terrain matching modules after basic plotting; any drawn contours must be mathematically coherent. |
| 15–17 | Grid, magnetic, true north; bearing measurement and grid–magnetic angle; example conventions are British-period specific. | Begin with **grid** north and simple cardinal directions. Defer real compass conversions until modeled datum, place, date and declination are specified. |
| 18–23 | Lightweight/prismatic compass and RA protractor operation. | Inspiration for optional interactive protractor; does not justify a cosmetic-only fake compass. |
| 24–25 | Comparing map and surroundings with direction, distance, signs, relief, alignment, proximity, shape (DDCRAPS). | Clue Hunt should ask learners to combine multiple consistent spatial clues, not guess icons by color. |
| 26–29 | Orienting a map, intersection, back bearings and resection. | Later "Locate Me" or "Unknown Landmark" puzzle, after bearings can be modeled and verified. |
| 30–33 | Approximate solar/stellar navigation in various hemispheres. | Research-only; not needed for a small mobile map-grid MVP. Do not translate approximate historic heuristics into universal safety advice. |
| 34–36 | Route selection factors and route cards. | Optional multi-stage expedition: choose a route, calculate distance and record checkpoints; use noncombat scenarios. |
| 37–40 | Pacing, distance/time, handrails, aiming off, catching features, recovering from losing orientation. | Optional advanced route reasoning; some techniques require outdoor practice, not just simulated tapping. |
| 41–43 | Continuation training and practical outdoor work; publication rights note on final instructional page. | Treat the game as practice/skill introduction, not a replacement for supervised field navigation or proof of qualification. |

## Source-specific caution — don't silently generalize

1. This is a **British** booklet from **2010**. Its UK National Grid discussion, local map sheets, example terminology and compass conventions may differ from US military instruction. Modern game rules that claim MGRS accuracy must be checked against NGA/FGDC/USGS sources, not copied from this booklet alone.
2. Page 5 says MGRS cannot be used on British/Irish *maps* in the booklet's context; it does **not** mean MGRS is physically impossible in those countries. These issued maps generally use other printed grid systems. Do not hard-code geographic exclusion.
3. The booklet sometimes uses everyday language such as a four-figure reference being "accurate to 1 kilometre." In software we distinguish **cell resolution** from field measurement accuracy. Four digits designate a 1 km square; six designate 100 m square.
4. Its time-sensitive compass/GPS/equipment advice reflects the date of publication and must not become current GPS reliability claims.
5. Booklet figure/page references are instructional inspiration. We produce **original** diagrams, fictional terrain and asset art. Do not trace or copy protected OS map content.
6. No source alone certifies that our game teaches the skill effectively. That needs working controls, correct answers, usability testing and ideally learning checks.

## Independent cross-checks consulted

- [NGA Coordinate Systems / MGRS](https://earth-info.nga.mil/?action=coordsys&dir=coordsys) — modern coordinate-system reference.
- [Federal Geographic Data Committee, How to Read USNG](https://www.fgdc.gov/usng/how-to-read-usng) — accessible U.S. official examples for 4-, 6-, 8-digit local references.
- [USGS, How are UTM coordinates measured on USGS topographic maps?](https://www.usgs.gov/faqs/how-are-utm-coordinates-measured-usgs-topographic-maps) — eastings/northings, map grid and interpolation.
- [Ordnance Survey, Publishing Licence](https://www.ordnancesurvey.co.uk/licensing/publishing-licence) — verify reuse of OS imagery before public distribution.

## How to use this document

- Cite **source ID + printed page number** in new puzzle concepts and tutorial drafts.
- Put technical claims needing objective checks into [accuracy rules](../MAP_READING_ACCURACY.md), not just prose.
- Convert skills into testable objectives via [skill-to-puzzle matrix](SKILL_TO_PUZZLE_MATRIX.md).
- Review the [source register](SOURCE_REGISTER.md) for publication rights, dates and confidence.
- **Not in scope:** images copied from the booklet, sample military mission maps, private training data or real-world navigation guarantees.
