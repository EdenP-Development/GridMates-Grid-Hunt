# GH-01 — First Playable Android Slice

**Started:** 2026-11-22  
**Status:** GH-01 source and **CI-built debug APK verified** as of [run #3](qa/GH01_QA_RUN_3.md). **No emulator instrumentation or owner physical-phone test yet.**  
**Scope:** one offline fictional north-up 6×6 km woodland grid and three 4-digit coordinate tasks.

## Implemented in source
- Native `app` module, Jetpack Compose Canvas map, no network or location permission.
- Pure Kotlin `core` with 4/6/8/10-digit numeric **training** grid parsing/formatting, half-open south/west boundaries, area comparison and north-up camera math.
- Fixed objective sequence `2437`, `2539`, `2741`; no seed generator yet. Cell highlight, submitted correction, Hint, Undo, replay, basic pan/pinch and Fit.
- Button-based Nudge controls for cell selection without precise taps (initial accessibility alternative).
- JUnit fixture tests for precision, reference boundaries, leading zeros, camera, valid fixed objectives.
- GitHub Action builds **debug-signed** `.qa` APK and captures source SHA, APK SHA-256 and debug signing certificate if it succeeds.

## Explicit limits
- Story, audio, elaborate art, chapters, profile, reward economy, random generator, LAN, real MGRS, device GPS, land-nav field transfer and six-digit interaction are **not** here.
- Map decorations are code-drawn, not approved assets.
- This action intentionally uses the **ephemeral default debug signing key** on GitHub runners. It is installable for *first tests* but **not** a guaranteed install-over signature. A separate private stable QA key/secret is needed for reliable update-in-place continuity. Never commit signing secrets.
- Existing install identity for QA: `com.playereden.gridmates.gridhunt.qa`; default release namespace reserved separately. No network permission.
- No claim of passing CI or installed Android/physical usability until results are inspected.
- Notes/changes may reflect in-progress unverified code until real tests run.

## Evidence to record on each iteration
Git commit SHA, workflow run ID and conclusion, core test run counts, compiled APK hash/signing certificate, Android OS/device and orientation, real Playtest observed confusion/error type, regression issue and fix. See [QA/playtest](QA_AND_PLAYTEST.md) and [GM08 session sheet](research/GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md).

## Next acceptance gates
1. GitHub CI **successfully executes** JVM unit tests and packages APK.
2. Source matched sideload APK installs on Android in airplane mode.
3. Actual owner check: plotting at boundaries, N/E order, pinch/pan ghost taps, selection recovery, readability, hints, replay, background/resume, portrait/landscape.
4. Observe actual enjoyment and desire for another hunt before making many generated levels.


## First successful build evidence — 2026-11-22

[GH-01 Run #3 full receipt](qa/GH01_QA_RUN_3.md): built source `6b65a1a`, Gradle `:core:test` (nine authored JUnit methods) and `:app:assembleDebug` successful, APK SHA-256 `0c3c00dea6ff06fe46941765859da7a7b4bcbdbdff48430b433c650b5a54ae50`, signer certificate `f1cfce66e90b0de77aa45dba8aa503ecfe1585b7440979afc182985546294f59`. [GitHub artifact ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45028853252/artifacts/14756424559). **Physical Android acceptance remains pending.**


## Unattended quality pass after first APK (2026-11-22)

[Run #4 QA receipt](qa/GH01_QA_RUN_4.md): source `92b11463`, 11 authored JVM unit tests executed via `:core:test`, Android `:app:assembleDebug` succeeded, artifact checksum and signer verified. Camera center/relative zoom are now saved across screen resize and the six direction/zoom controls are 48dp. **Physical phone/emulator instrumentation and force-stop persistence still not validated.** New debug signer differs from run #3; don't install over a differently signed APK. Owner phone testing remains the milestone gate.
