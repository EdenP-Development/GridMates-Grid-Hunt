# GH-V01 — Visual and Game-Feel Foundation / Owner Phone Test

**Date:** 2026-11-22  
**Scope:** iterate on the actual playable Grid Hunt experience, without expanding into a random generator, huge level library, real-world MGRS, or premature cosmetics.  
**Source status:** home hub + two map looks + optional tactile completion feedback + progress UI + smaller Kotlin/Compose screen modules implemented. **UI test source compiled in CI only when successful; actual UI tests/phone tests still need execution.**

## Why this checkpoint matters

Earlier owner-installed QA builds demonstrated that the three-reference chapter could be solved and that the portrait map-first layout and QA6 gestures were substantial improvements. They also revealed the limits of a testing utility: app launched directly into a map, Reverse mode was a small action in the header, and there was no meaningful place to compare visual treatments or judge the emotional flow.

**This milestone makes a controlled design baseline, not an assertion of finished artwork.**

## Functional baseline — what the build should include

- **A home/field journal hub** with two prominent routes: **First Bearings** (three curated mini-hunts, nine verified four-digit area answers) and **Look Again** (five authored Reverse Practice survey markers; user enters the correct four-digit grid-reference string).
- Honest local progress snapshots for both modes, with no account or network.
- A **visual playtest selector** with two code-drawn map treatments: **Field Guide** (warm paper, sage woodland) and **Survey Table** (cooler drafting colors/stronger grid). Both use **identical canonical map geometry, answers, modes, hints and gestures**. This is A/B aesthetic exploration, not two map datasets.
- Optional **completion vibration**, persisted locally, and visible objective progress lines. No fabricated “mastery” stars or rewards earned without gameplay.
- A straightforward Home return path, including Android Back, without silently resetting stored progress.
- Source refactored into separate files:
  - `MainActivity.kt`: entry point, hub and chapter orchestration
  - `ReversePracticeScreen.kt`: Reverse-specific UX
  - `GridMapCanvas.kt`: one shared map and gesture renderer
  - `HuntUi.kt`: common controls, objective card and hints
  - `VisualTokens.kt`: reusable theme and palette definitions
  - `core/`: canonical map math, authored chapter and Reverse answer validators, unchanged
- Android UI smoke test **source** in `app/src/androidTest` covering the home, navigation and theme choice. CI assembles this test package; **compiling it does not mean the test was run on a device.**

## One key research rule

**Visual choice changes presentation, not mathematical answer truth.** A 1 km cell `2437` remains the same canonical easting/northing area under either style, any zoom, and either device orientation. Tapping, panning and pinch rules remain constant across art styles.

## Suggested 10–15 minute owner phone playtest

1. **First impression:** Start on Hub. Does it feel like the entrance to a game rather than a QA tool? Are chapter and Reverse practice easy to tell apart?
2. **Map A/B:** Choose Field Guide; start a chapter and inspect Fit/zoom and reading labels. Return Home, choose Survey Table, open the same chapter/reference. Which style better conveys calm, friendly puzzle-book exploration? Are labels clearer in one?
3. **Gameplay pacing:** Solve three new squares in First Bearings. Is confirming each answer satisfying? Does the progress indication aid orientation, or feel bureaucratic?
4. **Reverse difference:** Start Look Again; read and type a marked point reference. Do the two modes require genuinely different thinking? Does the keyboard overlap Check?
5. **Control feel:** Test tapping, changing a selected cell, Undo, pan/pinch, Fit, physical Back/Home, changing orientation, and a small-screen font scale.
6. **Haptics:** Turn completion vibration Off in the hub, solve another task, then optionally turn it On to compare. Is a soft completion cue welcome?
7. **Resume/update:** Background or kill/reopen and confirm chapter/Reverse progress survives. If already installed QA15 fixed-key build, install the new higher-code APK *over it without uninstalling*. **This must be owner-confirmed.**
8. **What would you improve?** Observe whether you voluntarily begin another round or immediately want to stop. Note which parts feel like a worksheet and which feel like an adventure.

## Observe separately

| Dimension | Acceptable signs | Reasons to revise |
|---|---|---|
| **Orientation** | Hub paths clear without explanation | Reverse buried, impossible to return or find resumed mission |
| **Visual identity** | Warm/cool styles feel intentionally different while legible | Style selection seems cosmetic/imperceptible or harms label contrast |
| **Play feel** | Confirmation feels rewarding but not interruptive | Repetitive/flat rounds, annoying confirmations or feedback wall |
| **Controls** | Map keeps accurate tap/drag/pinch interpretation | Phantom marks, camera stutter, finger obscures key labels |
| **Accessibility** | UI reachable and readable in larger font/landscape | Buttons clipped, keyboard hides essential Check, inaccessible map |
| **Integrity** | Both modes use accurate canonical world references | Numeric answer mismatch or theme alters solver logic |
| **Updates** | Greater versionCode and same test signer install over prior fixed QA | Android rejects APK, local progress unexpectedly lost |

## Explicit limitations and next build sequence

**Not yet in this foundation:** final illustrations, sound package, haptic tuning by device, icon refresh, chapter art packs, real map symbols/legend puzzles, six-digit 100m grid selection, pathfinding, seeded Quick/Custom, final release signing, embedded real-device test instrumentation, or demonstrated learning/retention.

If owner approves the hub and overall flow, a **visual polish arc** can prioritize (1) consistent illustration language and a grid/map asset design bible, (2) richer but non-spoiling map features/decoration, (3) component-level typography, responsive layouts and contrast, (4) small correct-answer animation and optional short original sounds, (5) delightful small mission completion/stamp screen, before adding more modes and hundreds of puzzles.

Never claim real-device pass from a green build or compiled UI instrumentation tests. **GH-V01 is a *playtest foundation*, not the final UI/art approval.**
