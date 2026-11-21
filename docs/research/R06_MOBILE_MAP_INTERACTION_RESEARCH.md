# R06 — Android Touch Controls and Precision Grid Selection

**Research date:** 2026-11-21  
**Research question:** Which touch, map movement, zoom, precision-selection and accessible controls make Grid Hunt feel like a casual puzzle **while retaining exact 1 km / 100 m grid-area semantics**?  
**Status:** EXTERNAL RESEARCH COMPLETE; DESIGN HYPOTHESES & TEST PLAN DOCUMENTED. **No interaction code, running prototype, physical-device test, timings or preferred variant has been verified.**  
**Scope:** on-device interaction only. [R05](R05_GRID_ACCURATE_FEATURE_GEOMETRY_AND_ANCHORS.md) owns canonical feature locations and [R07](OPEN_DESIGN_RESEARCH_BACKLOG.md) will own randomized objectives.

## 1. Owner constraints and experience goal

- Early puzzle: immediately understandable, **tap a whole 4-digit (1 km) grid square**, no unnecessary operational map vocabulary.
- Later: larger maps and reverse mode; eventually 6-digit / 100 m sub-square plotting and mixed objectives.
- The challenge must be **reading and interpreting the map**, not hitting a 2-pixel target, fighting gesture recognizers, or wrestling with a GPS app.
- Offline first, Android touch, approachable portrait and landscape, optional help, no compulsory timer and no required account.
- Use the same world-coordinate validator regardless of how the player selects a square.

## 2. Primary technical and accessibility sources

| Official/technical source | What it establishes | Grid Hunt consequence | Evidence limit |
|---|---|---|---|
| [Android — Understand gestures](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures) | Compose has high-level tap, drag and transform gesture detectors. Top-level detectors aren't simply sequential in the **same** pointerInput coroutine because the first detector blocks. Pointer consumption/hit testing affects competing interactions. UI tests can inject gestures. | Deliberately architect **one coherent gesture policy**, or documented cooperating modifiers/handlers, with cancellation and touch-slop behavior. | Does not choose the best UX for our puzzle. |
| [Android — Multitouch / transformable](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/multi-touch) | Panning, zoom and rotation are supported; `transformable` or `detectTransformGestures` may be used. | Support pinch zoom and map pan; ignore rotation in early north-up training. Anchor zoom near gesture centroid by using our exact world-screen transform. | Does not guarantee good gesture behavior without implementation tests. |
| [Android — Tap and press](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/tap-and-press) | `detectTapGestures` exposes tap coordinates to custom input. Standard semantic `clickable` has benefits for controls. | A canvas map can convert a tap into a world coordinate; standard buttons should stay semantic. | Native tap recognition alone doesn't solve conflicts with pan. |
| [Android — Compose Semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics) | Custom-drawn UI needs explicit accessible semantics/actions; Compose semantics underpin accessibility and testing. | Map painting is not automatically a navigable accessibility tree; expose meaningful actions such as select cell, move reticle, check and undo. | We must implement and test actual accessibility behavior. |
| [Android — Accessible touch target guidance](https://developer.android.com/guide/topics/ui/accessibility/apps) and [Compose API defaults](https://developer.android.com/develop/ui/compose/accessibility/api-defaults) | Recommend at least **48 dp × 48 dp** targets for interactive UI; certain Compose components handle minimum targets. | Keep Check, Undo, Help, Zoom +/- and Back comfortably touchable. Tiny map cells require a different selection affordance, rather than distorting their coordinate meaning. | 48 dp is a target-size UI guideline, **not** a mandatory ground-square size or a fix for precise map selection. |
| [W3C WCAG 2.2 — Dragging Movements](https://www.w3.org/WAI/WCAG22/Understanding/dragging-movements) and [Pointer Gestures](https://www.w3.org/WAI/WCAG22/Understanding/pointer-gestures) | Web-accessibility standards call for alternatives to dragging and multipoint/path-based gestures unless essential. | Design a single-tap/button alternative to dragging pins or relying on pinch-to-zoom. | WCAG is a **web** standard and useful inclusive design reference, not a claim that every native Android game must be certified against this exact rule. |
| [Android — UI test APIs](https://developer.android.com/develop/ui/compose/testing/apis) and [gesture testing guidance](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures) | Compose tests can perform touch interactions and inspect semantic state; use modern `performTouchInput` patterns. | Automated pan/tap/pinch/undo tests before phone QA. | Automated gestures are not proof of a good *physical* finger feel. |
| [MapLibre — Gesture detector examples](https://maplibre.org/maplibre-native/android/examples/camera/gesture-detector/) | Real map SDKs separate gesture categories/listeners including move and scale; configurable UI gesture policy. | Study mature interaction separation **without importing a mapping SDK** for our fictional grid. | Map app patterns may be excessive for a puzzle; copy concepts, not entire UI/system. |

## 3. Four candidate interaction patterns

| Pattern | How it works | Best at | Major risks | R06 recommendation |
|---|---|---|---|---|
| **A. Tap square, instant answer** | One tap both selects and checks a four-digit cell. | Earliest tiny puzzles; extremely fast loop. | Accidental touches are graded as knowledge errors; no correction window; map tap/pan ambiguity. | Prototype only as comparison. |
| **B. Tap square, preview, then confirm** | Tap selects and highlights **whole area**; the player can tap a different cell or Undo, and press **Check** when ready. | Beginner/fixed 4-digit hunts; fair and understandable. | A separate Check action might become tedious on 10-target sessions. | **Preferred first test for GH-01.** |
| **C. Drag a pin** | Drag a marker and drop at a location. | Visually tactile for free-point tasks. | Finger covers target; release changes point; dragging conflicts with map panning; impossible for some users. | **Don't require** pin drag for numeric grid hunts. |
| **D. Stationary reticle + move map** | Crosshair stays centered; pan/zoom the map beneath; confirm selected cell, optionally with 10×10 inset. | 6-digit / 100 m tasks when cells become too small to tap. | More controls; reticle center can obscure feature; on a small screen may require magnifier/arrow buttons. | **Preferred experiment for GH-03**, not needed in first 4-digit tutorial. |

**The core insight:** do not let the UI's choice of pin/reticle change the mathematical answer. A numeric reference identifies a cell area, not an exact selected point or artwork icon.

## 4. Proposed default progression

### Step 1 — Small introductory grid (4-digit)
- Fit 4×4 or 5×5 numbered squares comfortably on screen; minimal gestures until needed.
- Prompt: "Find square 2437" (optionally `24 | 37` with right/up cue).
- **Tap anywhere inside that cell** -> outline the entire selected 1 km square and show the reference found.
- A separate **Check** button confirms. A second tap can simply replace the selected cell; an optional Undo clears.
- Wrong selection triggers **explanatory feedback**, not punitive points for a slipped finger; hints remain available.
- Do not present full-screen map settings or introduce MGRS terminology before first success.

### Step 2 — Larger map / normal hunts (still 4-digit)
- **One-finger drag pans** once movement exceeds the chosen system touch slop; movement below threshold resolves as tap if released and not cancelled.
- **Two-finger pinch zooms** about the centroid, without rotating grid north.
- Add visible **Zoom + / -** and **Reset / Fit** controls so the map is usable with one hand or without multitouch.
- Zoom and pan alter camera state only; selected world cell and landmark anchors remain unchanged.
- Keep grid labels visible and the objective ribbon from occluding selectable areas.
- At initial scale if all needed cells fit, preserve the same simple tap-first experience.

### Step 3 — 6-digit 100 m precision
- Choose a known 1 km parent cell, then **zoom into a 10×10 subdivision** or open a small, clearly labeled magnifier.
- Offer two candidate controls to compare:
  - **D1: 10×10 picker:** tap a 100 m subcell in a magnified inset; preview and confirm.
  - **D2: center reticle:** move the map beneath the crosshair, with buttons for stepwise repositioning; show currently selected 100 m cell; confirm.
- The 100 m cell must be understandable and hittable **at the effective zoom**; a physically tiny cell never requires a miraculous finger tap.
- Preserve easting/northing order and floor/truncation (no rounding); if a target spans a stylized building sprite, select its canonical reference subcell, not the icon center.

### Step 4 — Reverse Hunt / symbol selection
- When asked for the **grid of the cabin**, highlight or identify a canonical point feature; provide a forgiving typed-digit or digit-slot input with **leading zeros preserved**.
- When asked to **tap the cabin itself**, use a semantic icon hit area separate from world-grid cell validation. Overlapping nearby symbols need an explicit choose-which-feature response or zoom.
- Don't automatically accept a grid answer simply because an oversized sprite overlaps that square. See R05.

## 5. Gesture arbitration rules (engineering proposal)

1. Distinguish **pointer down → tentative tap**, **movement past touch slop → pan**, **two pointers → transform**, **gesture cancel**, **release**.
2. Once a gesture is identified as a pan/pinch, do **not** create or move a puzzle marker during its release.
3. A two-finger gesture must never lead to a phantom tap when one finger lifts before the other; explicitly test this.
4. Pinch zoom must preserve a world location under the centroid as described in [map camera equations](MAP_ENGINE_MATH_AND_VIEWPORT.md).
5. Recenter/fit, rotating screen or temporarily switching UI mode may move the camera, **not** canonical world markers.
6. Clamp zoom to a testable range that preserves labels and makes chosen resolution selectable; do not force arbitrary fixed zoom levels before testing.
7. Keep north-up for beginner exercises; allowing rotation would complicate grid-reading and map labels without clear added puzzle value.
8. Standard Compose controls should retain their semantics/hit behavior; Canvas pointer handlers should not steal events from Check, Hint, Undo or navigation controls.
9. A fully custom recognizer is a **fallback**, not mandatory if documented Compose modifiers/handlers prove compatible. Do not chain two blocking detectors in a single pointerInput handler.

### State diagram (conceptual, unimplemented)
```text
IDLE
  pointer down -> TAP_CANDIDATE
TAP_CANDIDATE
  movement exceeds slop -> PANNING
  second pointer -> TRANSFORMING
  release (unchanged) -> PREVIEW_SELECTED_CELL
  cancel -> IDLE
PANNING
  release/cancel -> IDLE
TRANSFORMING
  pointer(s) lift -> IDLE (suppress residual tap)
PREVIEW_SELECTED_CELL
  tap another area -> PREVIEW_SELECTED_CELL (replace)
  Undo -> IDLE
  Check -> VALIDATE (core grid-area lookup, not finger precision)
```

This is **desired behavior**, not a directive to write a new state machine if existing Android components handle it safely.

## 6. A precise answer contract

- A 4-digit `2437` selection is correct if the chosen point falls within E [24000,25000), N [37000,38000) in the fictional 100 km training coordinate system.
- A 6-digit `243378` selection is correct if it falls in E [24300,24400), N [37800,37900).
- **Integer canonical coordinates / grid IDs** are stable; camera pixel math is an implementation detail.
- Boundary policy: west/south included, east/north excluded; avoid ambiguous render antialiasing near cell lines and give snap/selection preview.
- Don't use 48 dp touch-padding to secretly expand the numeric answer across adjacent grid cells. Use the whole visible cell at normal zoom, a **selection preview**, or magnifier if a cell becomes small.
- Don't silently correct wrong squares based on nearest target location. The game should evaluate the **chosen cell** and teach correct grid-reading.
- Suppress feedback grading until the player explicitly commits if using Pattern B.

## 7. Accessibility, comfort and phone-specific concerns

**Required to research/validate in prototype:**
- At least 48 dp buttons for Check, Undo, Hint, +/- and Fit; control contrast and visual state do not rely only on color.
- **Accessible selection path:** after locating a general area, allow stepping easting/northing with visible buttons or entering the reference directly; make it possible to solve without multi-finger gestures or precise drag.
- Semantics: announce objective, current selected cell, errors, hints and completion. Decorative terrain artwork should not overwhelm TalkBack navigation.
- Preserve minimum labels and actual hit targets in portrait, landscape, small/large text, and high display scaling. Optionally collapse objective ribbon; never cover the map silently.
- One-hand test: zoom by +/- without moving other hand, simple tap-to-cell and tap-to-replace, accessible Fit button.
- Avoid mandatory time limits and unnecessary haptics/flash; expose feedback settings later if tests indicate need.
- Consider finger occlusion by previewing selected **whole area** rather than requiring a precise pin.
- Ensure alternate selection also uses the same validator; accessibility mode must not change grade precision.

**Constraint:** a complex custom Canvas drawing has no automatic child semantics for each painted 100 m cell. A deliberate semantic layer or non-Canvas alternative (grid stepping/digit input) is required before claiming full screen-reader support.

## 8. Controlled playtests — not run

### Comparison A vs B: 4-digit beginner
Same little map, same objectives, same number of targets, same typography, same coordinate engine.

- **A:** one tap auto-checks.
- **B:** tap previews, Check confirms; second tap replaces answer.

Collect: unintended submissions; mistake corrections; confusion about selected square; time per objective; where people pause; willingness to play another hunt; whether the Check step feels safe or annoyingly redundant.

**Expected hypothesis:** B is fairer for new players. If B proves too slow, experiment with auto-check after a brief preview, or one confirm per multi-target batch rather than a separate Check each time. Do not choose without actual experience data.

### Comparison D1 vs D2: 6-digit precision
Same reference and 10×10 world cells, same map/camera and feedback.

- **D1:** zoomed grid picker.
- **D2:** stationary reticle/move-map with step buttons.

Observe accidental wrong subcells, physical finger occlusion, time, user confidence, one-handed operation, accessible alternative, and whether someone understands **the 100 m area**.

**Hypothesis:** D1 is easier to learn; D2 may be useful once the map is spatially rich. Both remain open until tested.

## 9. Planned QA matrix (none executed yet)

| ID | Scenario | Pass condition |
|---|---|---|
| I01 | Simple tap on any part of correct 1 km cell | Whole correct cell previews, not arbitrary center-point check |
| I02 | Tap another cell | Preview visibly replaces prior choice, no hidden double marking |
| I03 | Drag beyond touch slop | Camera pans; no placed pin or scored answer |
| I04 | Two-finger pinch in/out, one finger lifts first | Camera changes; **no phantom tap** |
| I05 | Zoom at off-center centroid | Same world location anchored under fingers within measured pixel tolerance |
| I06 | Fit/Reset after panning and zooming | Camera restores bounds; current world marker remains |
| I07 | Select cell along top/right boundary | Deterministic neighboring cell chosen by world interval policy |
| I08 | Wrong cell -> Undo -> correct | No irrecoverable failure or unintended scoring |
| I09 | Rotate device with marker selected | Canonical world cell and mission state unchanged |
| I10 | Zoom to minimum and try to select small cell | Either selectable fairly or explicit magnifier/step control offered |
| I11 | Subsquare D1 on a new 6-digit task | Correct 100 m cell resolved at new map scale |
| I12 | Crosshair D2 with step buttons | Correct area and next step within world coordinate rules |
| I13 | Touch icon vs tap surrounding numeric area | Distinct Landmark Hunt vs Coordinate Hunt semantics |
| I14 | TalkBack/switch and non-multipoint alternate | Objective/selected cell reachable and selectable; no mandatory drag |
| I15 | 48 dp controls, enlarged text, narrow/landscape | Control targets usable and map labels not covered |
| I16 | Airplane mode, app background/resume | Same selected objective/camera/world coordinates; no network dependency |
| I17 | Long press/double tap if implemented | Explicit meaning and no accidental answer/zoom conflicts |
| I18 | Multi-target batch, select/replace/Check | All selections and feedback correspond to intended objective; no stale refs |
| I19 | Compose instrumented injection of tap/swipe/pinch | Same expected world grid area across input paths |
| I20 | Physical phone portrait, landscape, one-handed | Tester can complete 4-digit hunt and use controls without fighting gestures |

Record specific source commit, actual executed tests, Android device/API/density, screen captures of failures, APK identity, map revision and test observations. **A compiled UI test is not a run test**.

## 10. Proposed outcome and what's next

**R06 research conclusion (unvalidated):** start with **tap entire 4-digit cell → highlight/preview → Check**, allow ordinary pan/pinch and visible zoom/fit buttons, then test a **magnified 10×10 selector** against a center-reticle control for 6-digit missions. Separate gesture recognition, world/camera transform, answer validation, and screen-reader controls. The goal is real map-reading practice **without motor-precision penalties**.

**Not built:** no prototype, no APK, no measured frame rate, no Android tests, no real user observations.

**Next one-at-a-time research:** **R07 — how one map produces varied, repeatable, valid missions**. Explore seeded objective generation and unique targets, not a whole-world procedural terrain engine.