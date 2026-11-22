# GH-02A — Map-First HUD Redesign: QA8

**Date:** 2026-11-22  
**Status:** source implemented, GitHub CI successful, APK checksum and signer verified; **no owner visual/gesture retest of this layout yet**.  
**Scope:** layout and screen-space use on mobile, **not** new puzzle mechanics or art generation.

## Why we built this

The owner completed all three four-digit objectives on QA4 and reported unreliable pinch/drag. QA6 replaced competing gesture detectors and received positive real-phone feedback (“MUCH better for the zoom and such”). Screenshots still showed the map relatively small compared with an oversized header/objective card, as documented in [Issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10). GH-02A follows the explicitly agreed next priority: **make the map the hero**.

## Implemented in source

- Compact header with GridMates name, First Bearings, woodland/training context, and north arrow.
- Small horizontal objective ribbon, showing current target (`2437`, `2539`, `2741`), hunt number and brief right/up reminder.
- Portrait layout: map owns a **weighted flexible main region**, with compact status, guidance, labeled selection/zoom groups and reachable controls below.
- Landscape layout: map fills the left working area; a scrollable right-hand panel holds the objective, progress and controls to prevent the map collapsing into a short band.
- Clear selection status and brief corrective feedback; staged hints use the existing UI interaction.
- Six 48dp nudge/zoom controls, Undo/Hint/Fit, and Check/New Hunt retained.
- Existing QA6 single pointer gesture recognizer and per-gesture synchronous camera updates retained **unchanged**. No authoritative coordinate math changed.
- No chapter expansion, seeded generator, six-digit plotting, MGRS/GPS features, accounts or online dependency introduced.

## CI and APK evidence

| Evidence | Value |
|---|---|
| Workflow | [Grid Hunt Android QA run #8](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45053066515) |
| Workflow conclusion | **SUCCESS** |
| Build source commit | `f9c4f8e15d417e9754f8d4b422103e77fffa0ee4` |
| Gradle command | `:core:test :app:assembleDebug` |
| Coordinate test methods in repository | 11 authored JUnit methods (successful JVM test task, **not** device tests) |
| Artifact | [gridhunt-gh01-debug-8](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45053066515/artifacts/14766263499) |
| APK filename inside archive | `GridHunt-GH01-debug.apk` (legacy filename from scaffold) |
| APK byte size | `10,146,753` |
| APK SHA-256 | `8b1edd12b0c96d649679a9d6f9fb3f0b5dea0a48f54d486ed13f1c3a98f456b3` |
| Debug signer SHA-256 | `66febd44bf8ef0712bb764cb9fbc62bd02c80ac6f2a9e79b626b24cf0ac9d15d` |
| QA app package | `com.playereden.gridmates.gridhunt.qa` |
| app version configured | `0.1.0-gh01-qa`, code 1 (still scaffold version naming; should be updated in later QA stage) |
| Owner QA8 phone check | **PENDING** |

GitHub's artifact ZIP was downloaded, `unzip -t` reported no errors, and APK SHA-256 was independently computed and matched `APK_SHA256.txt`. Source SHA and `SIGNER.txt` were present and read. The default GitHub runner debug signer differs from previous QA6; installation **over** QA6 is not guaranteed. Uninstalling prior QA may lose local test progress.

**A passed build is not a visual screenshot comparison or device usability test.** No Android emulator instrumentation or physical portrait/landscape QA was executed for QA8.

## Next owner's quick 3-minute checks

1. **Portrait at Fit:** Map should take substantially more visual space than QA6; grid-line labels should remain readable and not overlap.
2. **Correct squares:** Select and check `2437`, `2539`, and `2741`; no answers should change.
3. **Zoom + drag:** Repeat pinch/drag, confirm nothing accidentally selects while navigating; Fit restores the full area.
4. **Instructions and controls:** Hint, Undo, direction arrows, +/− and Check usable without hunting through menus.
5. **Rotation:** In landscape, map left and controls right. The right column may scroll on a short phone screen, but all essential controls must remain reachable.
6. **Resume and large text:** Look for clipping/overlap. Report any screenshots or tactile awkwardness.

## Decisions after phone evidence

- If map visibility and controls pass, advance to **GH-02B**: small authored chapter hunts, onboarding, local progress/resume and brief, satisfying completion.
- If map is still undersized, adjust viewport proportions, remove additional chrome or offer a focused map view **before** adding content.
- Keep [Issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10) open for remaining pan/lift/orientation checks; improvement on QA6 is not universal proof of touch-gesture correctness.
- Future build quality: durable QA signing for install-over updates and actual Android UI/gesture instrumentation tests remain worthwhile engineering follow-ups.

**State:** GH-02A *source/CI complete; user visual acceptance pending*. No claim that GH-02 as a whole is complete.
