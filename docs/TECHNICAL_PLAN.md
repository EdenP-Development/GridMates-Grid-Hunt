# Technical Foundation — Android Grid Hunt

**Status:** architecture proposal; no project scaffolding/build yet.

## Native Android direction

Use **Kotlin + Jetpack Compose** consistent with existing GridMates Android apps, with a small **pure Kotlin grid/puzzle engine** separately unit-testable from the Android UI. Reuse ideas from Sudoku's [reusable foundation audit](https://github.com/PlayerEden/SudokuTogether/blob/main/docs/REUSABLE_ANDROID_PUZZLE_FOUNDATION.md), not its Sudoku models or a hypothetical ready-made template.

### Proposed minimal folders/modules

- **:app** — Android identity, main activity, Compose screens, map painter, gestures, accessibility, local settings and navigation.
- **:core** — meter-based training grid coordinate rules, objective modes, deterministic generator, answer validation and tests (no Android imports).
- **app assets / map packs** — versioned map manifest, vector/bitmap art, symbols and level definitions.
- **docs/** — this design system, evidence, decisions, roadmap and QA receipts.

Start with two modules maximum. Extract common services only when repeated implementation proves an interface is worthwhile.

## The crucial shared engine

World coordinates -> viewport transform -> exact map pixel projection -> tap/reticle converts back into world coordinates -> resolve containing grid square at objective precision -> validator checks canonical square. Reverse Hunt uses the identical coordinate conversion in the opposite direction.

The map imagery must never be authoritative for the answer. The data is.

## Stage-by-stage dependencies

1. **Grid parser/formatter/quantizer:** four/six-digit rules with deterministic unit tests, including leading zeroes and boundaries.
2. **Map viewport:** north-up grid with legible ticks; pan/zoom and inverse transforms, no web scroll behavior.
3. **Puzzle controller:** active objective, markers, select/change/undo/verify/result state.
4. **Small bundled map manifest:** Woodland training map and target set; later validated random selection.
5. **Resume/settings:** versioned local saving with explicit migration test fixtures.
6. **Broader content/profile:** only once phone gameplay is enjoyable.

## Android packaging and trust

- Own unique package/application ID, e.g. **proposed** `com.playereden.gridmates.gridhunt` (check before scaffold; not a shipped identity).
- QA and production builds must use deliberately documented and distinct package/signing strategies. Do not borrow Sudoku/Word Search signing identity, assets or secrets.
- Keep QA signing stable for honest install-over tests; archive source commit, APK hash, package ID, signer fingerprint, workflow ID and compatibility conclusions.
- No required cloud backend or account. Runtime offline should not fail because network is unavailable.
- Release/AAB/signing/store requirements need fresh verification at launch, not assumptions from sibling apps.

## Gestures and layout risks

- Implement native pinch-to-zoom + one-finger pan; separate marker placement or use crosshair + confirm. Avoid accidentally placing a target while panning.
- Coordinate taps consistently on phone screens where fingers obscure points; six-digit tasks may need a reticle/magnifier.
- Check map remains readable at compact portrait widths, tall phones, landscape, large text and accessibility scaling.
- Prevent clipping of objective ribbon and map labels; add visible pan/zoom affordances and obvious reset view.

## Engineering rules

No massive procedural landscape engine; no required GPS/location permissions; no runtime network tile fetching; no dependency on art quality for coordinate correctness; no premature multiplayer abstraction. Keep puzzle mode validators small and explicit, then add mode-specific tests.

## Evidence gates

A successful Gradle build is not proof of functional navigation, Android UI execution, physical phone acceptance, safe in-place upgrades or a publishable package. Maintain those as distinct verification states and preserve rollback commits.