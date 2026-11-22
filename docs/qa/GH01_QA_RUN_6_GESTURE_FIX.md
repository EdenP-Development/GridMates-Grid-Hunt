# GH-01 — QA6 Gesture Fix (Owner Feedback Follow-up)

**Date:** 2026-11-22  
**User bug report:** [Issue #10 — drag/pinch unreliable and map labels crowded](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10)  
**Build CI:** [GitHub Actions Run #6](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45036299405) — **SUCCESS**  
**Built source:** `79bb940d523f6f337468fbbfbbd1290be0939e53`  
**QA artifact:** [gridhunt-gh01-debug-6](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45036299405/artifacts/14760210702)

## Confirmed owner phone observations on QA4

- The Android app installed and displayed a complete 6×6 woodland grid.
- The player selected **2437**, **2539**, **2741** and completed all three targets, with screenshots of progression and completion.
- Owner said: **“The dragging and zooming like kinda don't work and all wonky.”**
- Screenshots also showed the map rendered relatively small within a wide panel, a redundant `N ↑ · 1 km squares` overlay obscuring northeast map area, overlap near SW coordinate numbers and clipped map edges when zoomed.
- No instrumented gesture trace was supplied, so exact cause is a code-based diagnosis **rather than proven on-device root cause**.

## Source changes being tested

- Replace two separate long-lived pointer detectors with a **single `awaitEachGesture` recognizer** for tap, single-finger drag and multi-touch pinch.
- Distinguish tap vs movement beyond Android touch slop and **suppress tap selection after any two-finger sequence**.
- Read current camera/callbacks through `rememberUpdatedState` rather than a stale captured camera during pointer processing.
- Maintain a **synchronous camera within the ongoing gesture** so fast drag/pinch updates don't wait on UI recomposition.
- Remove redundant map overlay, separate easting/northing tick rails to prevent SW label overlap, shorten some prompt/feedback text, display a short drag/pinch instruction.
- Keep the existing valid 1 km grid math, fixed three target references, and nudge/zoom/Fit alternative buttons unchanged.

## QA6 verified evidence

| Field | Value |
|---|---|
| Android workflow | Run #6 — SUCCESS |
| Gradle commands | `:core:test :app:assembleDebug` |
| Authored JVM tests | 11 (no new test methods this revision) |
| Archived ZIP | Downloaded and verified with `unzip -t` |
| APK in ZIP | `GridHunt-GH01-debug.apk` |
| APK size | 10,130,369 bytes |
| APK SHA-256 | `7b3dde35ed9f8d613fdf1db329e749f5c9db50925871319cf6780fe42922f221` |
| Signer subject | `C=US, O=Android, CN=Android Debug` |
| Signer certificate SHA-256 | `c23df74d1f65b189095196b03bba29c24a71ea18bd4f2a6f7199a0d0b3bb54e2` |
| App QA ID from Gradle | `com.playereden.gridmates.gridhunt.qa` |
| Actual QA6 phone gesture test | **NOT YET RUN** |

**Signing continuity warning:** QA6 has a different ephemeral runner debug signing certificate from QA4; the device may need QA4 uninstalled first. This loses any QA4 local data. These are non-production test builds.

## Next owner's two-minute verification

1. Install **QA6** after uninstalling QA4 if the signer prevents update.
2. Solve/tap one square as before; **do not accidentally select while dragging**.
3. Tap **+** once or twice, then drag with one finger. At initial Fit all map geometry is already visible, so little or no panning may be intentional; after zoom, panning should visibly move the map.
4. Pinch two fingers apart/together several times around different points. Ensure map follows continuously, no random square selected.
5. Try Fit map and inspect easting/northing labels, especially southwest corner.
6. Report if pan/zoom still jerks, stops, or falsely selects; share another screenshot or short description. No screen recording required.

**Release gate remains open:** QA6 is compiled and hash-verified, **not demonstrated physically functional**. Do not close [Issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10) until a physical phone confirms.
