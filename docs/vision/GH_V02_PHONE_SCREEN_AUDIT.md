# GH-V02 — Owner Android Screenshots: UI/Visual Playtest Audit

**Date:** 2026-11-22
**Evidence:** seven screenshots supplied in conversation by the owner, showing the home hub, First Bearings (initial guide, selected wrong square and corrective feedback), and Look Again (marker A entered and marker B after acceptance).
**Build identity:** not visible in screenshots; do not assume a particular APK, an in-place QA update, or signing success from these images.

## Observed and supported

- Navigation surfaces clearly expose First Bearings and Look Again as two different practices.
- First Bearings shows the four-digit goal `2437`, an orange selected square `2840`, and helpful non-punitive feedback after Check. This is useful evidence of selection, display and wrong-answer flow, **not proof of all coordinate rules**.
- Look Again shows marker A with `2440` entered and a subsequent marker B / 2 of 5 screen with positive feedback. This supports that reverse marker advancement is occurring in the photographed phone session; not proof all five tasks work.
- On the same portrait phone, the four-line inline How to Play surface materially reduces map height. After dismissing it, the map has much more breathing room.
- The map remains a schematic, with repeated circular canopies, a single blue creek, a dashed path and fine grid lines. It communicates coordinates but is not yet a richly illustrated puzzle world.
- At larger apparent text scaling, body text frequently wraps (including chapter survey name, reverse instruction, field journal text). Large controls and repeated instructions leave limited room for the actual map.
- Axis numbers at viewport edges can be partly clipped as the user zooms/pans; verify exact legibility through gesture/device tests rather than assuming numeric data is wrong.
- **Real UI defect:** Look Again's bar tracked `answer.length / 4`. With `2440` entered at marker 1/5, it appears full, misleadingly implying the whole survey is complete.

## Targeted source fixes in PR #12

1. Reverse progress now uses accepted marker count / total markers, independent of how many digits are typed. This retains the same canonical reverse validator.
2. How to Play becomes a modal dialog, not a block that reduces available map size; the player can dismiss and reopen it with the existing help control.
3. The First Bearings objective ribbon uses a shorter east/north instruction instead of repeating the hunt title below the header.
4. A compact in-puzzle A/B map-look control is added in both modes to allow same-objective appearance comparison, preserving the ongoing map camera and puzzle state.
5. Android UI test source is extended to navigate both game modes and exercise the toggle; the current CI **compiles** instrumentation test source, but does not execute it on a phone.

## Critical watch-outs

- The additional map-look header control may crowd the title, north arrow and Home/? controls on the owner's large-font portrait configuration. Treat this as **not owner-accepted**, even if Android compilation passes. Prefer a responsive control placement in follow-up rather than shrinking the actual map or forcing smaller accessibility text.
- The dialog improves available map area once dismissed, but its first-run wording and dismissal behavior still require a physical user test.
- APK QA22→QA23 successfully installed over in an Android emulator with app-private data preserved; the owner's real-device upgrade was not shown or identified in these images. Keep issue #11 open.
- Existing pan/pinch, grid-rail and zoom behavior need a physical phone pass. A screenshot is not a gesture trace.
- Public QA signing identity is not a private release key.

## Next visual/game-feel arc, in order

1. **GH-V02a accessibility/layout:** check phone portrait, landscape and increased font sizes. Keep clear primary action and map, avoid clipped labels, crowding, keyboard covering Check and UI segments that force scrolling mid-puzzle.
2. **GH-V02b readable map:** explicit easting/northing line labels on distinct axis rails, grid contrast and label collision safeguards at zoom/viewport edges; add device tests and preserve numeric grid math.
3. **GH-V02c environmental language:** evolve simple repeated circles and strokes into a cohesive, restrained woodland field-map art language (varied canopy silhouettes, meadow patches, creek banks, trail details, landmarks) *while using one canonical data/anchor layer* and without implying inaccurate terrain or accidental target hints.
4. **GH-V02d game feel:** light result confirmation/mission-completion stamp, subtle original SFX and optional haptics, no interruption of repeat attempts or saved progress.
5. **GH-V02e full owner acceptance:** source-matched QA APK, verified package/signing/version preflight, emulator instrumented UI and install-over checks, then a 10–15-minute owner device playtest. Track problems and fixes by source commit and APK hash before calling visuals ready.

**Design intent:** a friendly, tactile digital puzzle-board experience that can genuinely teach grid references, not a sterile calculator, real-world GPS navigator or overly decorated military simulator. Prioritize an excellent, readable phone map over expanding the chapter catalog.
