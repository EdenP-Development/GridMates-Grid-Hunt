# Map Engine Verification and Implementation Spikes
**Status: Planned test cases only. No tests or Android builds executed in this research pass.**

## Coordinate test vectors
| ID | E,N (meters inside fictional 100 km square) | Requested | Expected |
|---|---|---|---|
| G01 | 24375,37860 | 4-digit | `2437` = [24000,25000)×[37000,38000) |
| G02 | 24375,37860 | 6-digit | `243378` = [24300,24400)×[37800,37900) |
| G03 | 24999,37999 | 4-digit | `2437` (still inside) |
| G04 | 25000,38000 | 4-digit | `2538` (new cell at boundary) |
| G05 | 400,800 | 4-digit | `0000` (leading zeroes) |
| G06 | 400,800 | 6-digit | `004008` (leading zeroes per axis) |
| G07 | negative coordinate, 100000 boundary, odd digit string | any | reject or explicit bounded behavior |
| G08 | same point across 4/6/8 digit encodings | each | smaller cell is inside correct parent square |
| G09 | landmark just under/at 100 m boundary | Reverse Hunt | correct **truncated** reference, never rounded |
| G10 | E/N transposed | any | not accepted as same target |

## Screen/camera tests
- V01: world→screen→world roundtrip across zooms, device aspect ratios and density.
- V02: fixed world marker moves on-screen but **does not change world location** when camera pans.
- V03: off-center pinch zoom anchors chosen world location under fingers; verify with numeric tolerance.
- V04: labels increase east/right and north/up; gridlines align with world 1 km anchors.
- V05: tap just to either side of boundary chooses correct cell without arbitrary pixel threshold.
- V06: single-finger pan never creates phantom marker; Plot mode tap succeeds; two-finger zoom works.
- V07: 100 m cell usable with zoom/reticle without microscopic taps; alternative input accessible.
- V08: portrait/landscape, enlarged text, narrow phones and TalkBack-friendly controls.
- V09: airplane-mode cold launch and resumed puzzle with intact map revision, seed and pins.
- V10: no map visual/cached rendering changes invalidate answer checking.

## Generator/content tests
Same (mapId, mapRevision, generatorVersion, seed) -> identical objectives. Unique verified targets, no ambiguous clue/landmark pairs, fallback when seed candidate invalid, old saved game never silently rerolled, assets/licence entries present and intact.

## Vertical slices
**S1 / GH-01:** pure Kotlin reference parser+validator, original 6×6 training map, Compose canvas, 3–5 four-digit objectives, plot/undo/check. **Gate:** tested core + real Android tap/pan/zoom acceptance.

**S2:** Reverse Hunt and six-digit 10×10 sub-square picker on same map, unambiguous landmark. **Gate:** boundary fixtures, world precision preserved, reticle usability.

**S3:** versioned content manifest, fixed chapter puzzles and seeded Free Hunt using verified points, no full-world terrain generation. **Gate:** deterministic seed replay, answer uniqueness, save continuity.

**S4 only if justified:** benchmark custom canvas against MapLibre/Mapsforge using identical real map requirements. Compare APK size, cold launch, memory, responsiveness, font/labels, true offline, accurate coordinates and licence overhead. **Do not replace a working engine for hypothetical benefits.**

## Evidence discipline
Track source commit, automated JVM tests, actual emulator instrumentation (not compile-only), APK identity and signer fingerprint, physical phone review and safe upgrade checks separately. Game completion does not certify outdoor navigation proficiency.

[Camera design](MAP_ENGINE_MATH_AND_VIEWPORT.md) · [Roadmap](../ROADMAP.md) · [Accuracy contract](../MAP_READING_ACCURACY.md).