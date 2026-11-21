# Map Engine Math, Coordinate Contracts and Hit Testing
**Status: PROPOSED ENGINEERING DESIGN — not implemented or unit-tested.**

## Coordinate spaces
1. **Local world space (meters)** is authoritative. E=easting increases right/east; N=northing increases up/north. Use integer meters for canonical landmarks and quiz answers, Double when a camera needs sub-meter interpolation.
2. **Grid-reference space** quantizes world coordinates into areas. Within one *fictional* 100 km training square, 4-digit `2437` means E [24000,25000), N [37000,38000). 6-digit `243378` means E [24300,24400), N [37800,37900).
3. **Screen space (pixels)** uses X right, Y down; view size/density varies across devices.
4. **Art space** contains decorative shapes/sprites anchored to corresponding world coordinates.

**This training square is NOT geographically valid full MGRS.** Full global references additionally need grid-zone designation, 100 km square identifier, datum/projection and official conversion tests. See [accuracy spec](../MAP_READING_ACCURACY.md) and [NGA](https://earth-info.nga.mil/?action=coordsys&dir=coordsys).

## Camera math (north up, no rotation)
Let camera center `(cE,cN)`, scale `s > 0` pixels/metre, screen viewport `W,H`, world point `(E,N)`:
```text
screenX = (E - cE)*s + W/2
screenY = (cN - N)*s + H/2
E = cE + (screenX - W/2)/s
N = cN - (screenY - H/2)/s
```
**Invert the vertical axis exactly once.** Grid labels are generated from world meter anchors; never from image pixel indices.

For content-moving pan by screen `(dx,dy)`:
```text
cEnew = cE - dx/s
cNnew = cN + dy/s
```
For zoom `s -> s2` about finger centroid `(px,py)`, first convert centroid to old world point `(E0,N0)`, then:
```text
cEnew = E0 - (px-W/2)/s2
cNnew = N0 + (py-H/2)/s2
```
The same world point stays under the zoom centroid. For combined zoom+pan explicitly specify event order and test it. Clamp zoom/camera to accessible map boundaries without silently altering world locations.

## Rendering layers (back to front)
1. Original background and terrain shapes.
2. Paths, roads, streams, hills (contours only if topographically coherent).
3. 1 km grid lines + world-derived ticks/labels.
4. Optional 100 m subdivisions and magnifier, displayed only at suitable zoom.
5. Landmarks, active target, placed markers, solution feedback.
6. HUD on top, outside clipped map viewport: target list, undo, plot confirmation, legend, grid-north indicator.

A map pack stores revisioned **world-space geometries** and landmark positions. Even if art changes, canonical coordinates and puzzle IDs never silently move.

## Input contracts
- Tap-to-plot only in explicit Plot mode or with proven gesture disambiguation.
- Single finger drag beyond touch slop pans; two fingers transform around centroid; no rotation in GH-01.
- Screen tapping calls **screenToWorld** first, then a containing-square lookup at the objective's chosen resolution. Compare canonical area IDs, **never arbitrary pixel-center distance**.
- 100 m missions offer reticle/magnification or keyboard/adjacent-cell accessibility fallback. Players should not need 1–3-pixel tapping.
- Markers support select/move/undo, don't move when camera pans, and remain anchored through orientation changes.
- Android documents that high-level detectors such as `detectTapGestures` and `detectTransformGestures` consume input and cannot simply be chained as consecutive detectors in one pointerInput handler; design gesture routing explicitly. [Source](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures).

## API boundary (concept, not implemented)
```text
WorldPoint(E,N) / GridCell(swE,swN,sizeM)
TrainingMap(mapId,revision,bounds,geometry,landmarks)
MapCamera(centerE,centerN,pixelsPerMeter,viewport)
MapTransform.worldToScreen(point,camera)
MapTransform.screenToWorld(pixel,camera)
GridRef.format(point,digits) / parseNumeric(reference,context)
PuzzleEngine.validate(submittedCell,expectedCell)
```
Rendering has **no** privileged access to hidden answers; the core engine must not import Android or Compose.

## Future GIS adapter
If real map data is added, build an explicit module for map CRS/datum, UTM or projected coordinates and full MGRS. Don't mistake Web Mercator display positions for local UTM meters. NGA GEOTRANS, GeographicLib C++ and [PROJ](https://proj.org/en/stable/operations/index.html) are candidate references—not currently installed or integrated. Map tiling and geographic coordinate conversion are separate concerns.