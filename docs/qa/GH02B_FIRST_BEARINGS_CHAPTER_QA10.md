# GH-02B — First Bearings Mini-Chapter, QA10

**Date:** 2026-11-22  
**State:** Android Kotlin source implemented, CI build and core JVM tests **PASSED**; APK independently hash-verified. **No owner QA10 installation, chapter playthrough, resume test, or landscape/accessibility instrumentation yet.**  
**Previous visual basis:** owner QA8 portrait screenshots show a substantially larger map, readable objective ribbon and real wrong-answer recovery. A screenshot of a wrong `2740` answer for the `2437` objective confirms an incorrect response was not accepted. That is **visual evidence**, not a pass for every interaction, orientation or pinch case.

## Implemented now

- **One real (small) First Bearings chapter:** three authored mini-hunts on the existing offline fictional 6×6 woodland grid, each with three distinct four-digit 1km target squares:
  - Hunt 1, **Trailhead Survey**: `2437`, `2539`, `2741`
  - Hunt 2, **Across the Brook**: `2938`, `2642`, `2839`
  - Hunt 3, **The Final Notes**: `2537`, `2841`, `2940`
- Explicit **Next hunt** after a completed set; **Replay chapter** after hunt 3, with no auto-skip or timers.
- The game starts in the current hunt on app reopen. On-device `SharedPreferences` saves hunt index, objective index, pending selection and hint stage. State has a sanitizer so corrupt/stale persisted indices do not crash the chapter.
- An optional short **How to play** card explains eastings then northings, with a `?` action to show it again after dismissal; no permanent tutorial wall.
- Existing full-width GH-02A portrait map and landscape-side-panel layout; QA6 touch gesture code, MapCamera and canonical 1km validation remain unchanged.
- Increment app version to **`0.2.0-gh02b-qa` / code 2**. The CI install notes now extract the actual package/version from the built APK instead of hardcoding a version number.
- Still no randomized missions, new map art, reverse reference input, six-digit interaction, real MGRS location, GPS, accounts or networking.

## Build and artifact evidence

| Field | Verified |
|---|---|
| CI workflow | [Run #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45059027122) |
| CI conclusion | **SUCCESS** |
| Source commit built | `f8a15cbc1b0ca00e1e1ef7d8f645d235ca0ff319` |
| Commands | `:core:test :app:assembleDebug` |
| Test source | 11 coordinate/camera JUnit methods + **6 chapter tests**, 17 authored total; `:core:test` succeeded |
| Artifact | [gridhunt-gh02b-debug-10](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45059027122/artifacts/14768283897) |
| APK in ZIP | `GridHunt-GH02B-debug.apk`, **10,163,141 bytes** |
| APK SHA-256 | `9530bfef12e18877ef03a21849d310a9372ec66d2e3dd3872f78eb2e0eed9247` |
| Android manifest (from `aapt dump badging`) | `com.playereden.gridmates.gridhunt.qa`, versionCode `2`, versionName `0.2.0-gh02b-qa` |
| Debug signer cert SHA-256 | `bf54f0d3b2603353f6b3aa9d1ecb2ab1485151c7f53e7d27952d3df05b032231` |
| ZIP evidence | Downloaded, `unzip -t` clean, APK independently extracted and SHA-256 recomputed; matched manifest |
| Android emulator instrumentation | **NOT RUN** |
| Owner QA10 physical phone | **PENDING** |

**Signing warning:** Github's hosted ephemeral debug signer differs from QA8. The device might reject install-over; uninstalling the previous QA package may erase test save data. This is **not** stable release/QA update signing.

## First QA10 phone test checklist

1. Open the new APK and check **HUNT 1 / 3 — Trailhead Survey** appears; optional help card visible on a fresh install.
2. Dismiss/reopen the `?` guide; keep the full map visible after dismissal. Test the same tap, pinch, pan and Fit functions from QA6/QA8.
3. Complete `2437`, `2539`, `2741` in hunt 1. The button should now say **Next hunt**.
4. Tap Next hunt. Confirm **HUNT 2 / 3 — Across the Brook** with different targets, then solve one square.
5. Background and force-stop/reopen the app. Confirm it resumes hunt 2 at the correct square; pending selection and hint state should persist when relevant.
6. Complete the second and third authored mini-hunts, observe **CHAPTER COMPLETE** and **Replay chapter**, then verify replay returns to hunt 1.
7. Check portrait control accessibility and optionally rotate into landscape to ensure the map on left and scrollable controls on right remain usable.
8. Report any confusing mission transition, accidental input, progress reset, guide obstruction, data persistence failure, or content repetition. Screenshots are useful.

## Acceptance and future direction

**Ready for human evaluation, not approved.** First observe whether the new 3×3 sequence feels more like a game than replaying three targets, and whether the `?` intro is understandable. If successful, next sensible work is chapter pacing and meaningful map/mission variety, rather than adding new calculations before gameplay feel is approved.

Keep [issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10) open until touch/rotation checks are explicitly confirmed. **No outdoor navigation transfer claim.**
