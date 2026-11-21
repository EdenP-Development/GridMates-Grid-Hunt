# Grid Reference Accuracy — Nonnegotiable Rules

**Status:** technical/educational specification; implementation and cross-check tests pending.  
**References:** [NGA coordinate systems](https://earth-info.nga.mil/?action=coordsys&dir=coordsys), [USGS UTM on topo maps](https://www.usgs.gov/faqs/how-are-utm-coordinates-measured-usgs-topographic-maps), [USGS education](https://www.usgs.gov/educational-resources/27-ideas-teaching-topographic-maps-0). See [sources](RESEARCH_AND_SOURCES.md).

## Ground truths

- Read **easting first (right)**, **northing second (up)**. In a north-up map, eastings increase rightward and northings upward.
- MGRS is more than 4–10 numeric digits: a fully identified reference normally includes **grid zone designation + 100 km square identifier + numeric easting/northing**. Short references only work when map context supplies the omitted prefix.
- The numeric component uses equal digits per axis. **4 digits = 1,000 m square; 6 = 100 m square; 8 = 10 m square; 10 = 1 m square**.
- A grid reference identifies a **square area**, not the exact center and not an accuracy guarantee. The terms resolution and ground accuracy are not interchangeable.
- A point is within a square using the *lower-left (southwest) origin*: easting in [E, E+size), northing in [N, N+size). Do not round coordinates before creating a coarser reference; truncate/floor to containing square.
- Test the behavior at line boundaries, near the map edge and where the coordinate digit groups have leading zeroes. Never interchange rows and columns.

## Controlled training-grid model for MVP

Use a **fictional, north-up, single-square training plane**. Its local coordinates are meters from a 100 km-square southwest corner, with E and N each in [0, 100000). Render consistent 1 km lines; optionally interpolate ten 100 m sub-divisions within one 1 km square.

This teaches the same **local read/plot technique**, but it is **not** a geographically valid MGRS point until linked to a real MGRS grid-zone designation, 100 km square identification and datum/projection. Label it "Training Grid / MGRS-style practice"; do not describe an invented prefix as real or output fictitious GPS coordinates.

### Reference encoding within one known 100 km square

Let E and N be **integer meters from that square's southwest origin**.

- Four-digit numeric reference = 2 digits of floor(E/1000), then 2 digits of floor(N/1000).
- Six-digit numeric reference = 3 digits of floor(E/100), then 3 digits of floor(N/100).
- Eight-digit numeric reference = 4 digits of floor(E/10), then 4 digits of floor(N/10).
- Pad easting and northing independently with zeroes; never derive the result from a display pixel coordinate.

**Worked example:** E=24,375 m, N=37,860 m relative to the 100 km-square origin.

| Precision | Numeric reference | Ground area represented |
|---|---|---|
| Four | **2437** | E 24,000–24,999 m; N 37,000–37,999 m |
| Six | **243378** | E 24,300–24,399 m; N 37,800–37,899 m |
| Eight | **24373786** | E 24,370–24,379 m; N 37,860–37,869 m |

Examples are local numeric components, not standalone world locations.

## Checking answers

**Coordinate Hunt:** player selects a **cell at the requested resolution**. Compare quantized E/N square indexes to the target. A six-digit target is correct anywhere in its identified 100 m cell. The placement UI may offer a zoom/reticle; don't require hitting the cell center.

**Reverse Hunt:** a map feature has an authoritative local E/N coordinate. Player enters the corresponding *truncated* reference with the specified number of digits. Do not round because the feature is near the top/right boundary of its cell.

**Landmark Hunt:** target must be unique under its clue; prevent two indistinguishable landmarks sharing the same target square at that precision without a secondary disambiguator.

**Multi-step mission:** intermediate outputs must come from canonical map coordinates and specified distance/direction semantics; no assumptions from artwork pixel size.

## Map screen math

- Use one stable **world-in-meters coordinate space**. Define viewport transforms both ways; invert the y-axis exactly once for north-up display.
- Pan/zoom manipulates the camera transform, not the underlying coordinate system. A reticle's world position is stable across zoom.
- Use exact gridline anchors and numeric label origins, not visual approximation.
- Interpolation is measured as a fraction of the 1 km square. Verify with deterministic test fixtures at 0%, 10%, 50%, 90%, and just below/at each boundary.
- Pins, decorative icons, shadows and line thickness must not change which world square is selected.
- Provide a visible map scale and north arrow when the exercise requires either. Future real geodata must carry provenance, coordinate system, projection and datum.

## Future fully-qualified MGRS gate

Before any claim of **real-world coordinate output**, add validated UTM/MGRS conversion or use a well-tested licensed library; choose a genuine single-zone/single-100-km map pack; verify known official-reference points including digit parsing, zone/100 km square identification and map-boundary conditions. No guessed coordinates, no GPS use claim and no misleading military endorsement.

## Required test matrix

Correct floor/truncation; digit order; leading zeroes; 1 km/100 m/10 m scales; 100 km-square boundaries rejected or handled explicitly; taps at gridline boundaries; reverse hunt parity; zoom/pan invariance; portrait/landscape transformations; save/load preserving objective coordinates and pins. Include an independent manual worked-example review before publishing instructional copy.