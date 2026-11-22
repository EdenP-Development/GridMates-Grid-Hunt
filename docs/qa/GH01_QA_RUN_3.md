# GH-01 — Android QA Run #3 (first successful compiled APK)

**Date:** 2026-11-22  
**Workflow:** [Grid Hunt GH-01 Android QA — run #3](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45028853252)  
**Run ID:** `45028853252`  
**CI conclusion:** **SUCCESS** — Gradle `:core:test` and `:app:assembleDebug` completed, APK signed with runner Android Debug signing key, checksum/certificate captured, artifact upload successful.  
**Built source commit:** `6b65a1a6a4e9767c4cc9f4ea10a4ed49b4328863`  
**Artifact:** [gridhunt-gh01-debug-3](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45028853252/artifacts/14756424559), ZIP containing the APK and 4 evidence files.

## APK evidence

| Field | Verified value |
|---|---|
| APK file in artifact | `GridHunt-GH01-debug.apk` |
| APK byte size | `10,130,369` |
| APK SHA-256 | `0c3c00dea6ff06fe46941765859da7a7b4bcbdbdff48430b433c650b5a54ae50` |
| Package ID expected from build configuration | `com.playereden.gridmates.gridhunt.qa` |
| Version configured | `versionCode 1` / `0.1.0-gh01-qa` |
| Signing certificate subject, per `apksigner verify --print-certs` | `C=US, O=Android, CN=Android Debug` |
| Signing certificate SHA-256 | `f1cfce66e90b0de77aa45dba8aa503ecfe1585b7440979afc182985546294f59` |
| Build type | debug, offline/no INTERNET or location permission in source manifest |
| CI tests invoked | `:core:test` (9 authored JUnit methods), `:app:assembleDebug` |
| Executed device/emulator instrumentation tests | **NONE** |
| Owner-installed physical Android test | **PENDING** |

**Evidence verification:** The run completed successfully; the uploaded artifact was independently downloaded, its ZIP tested for integrity, its APK SHA-256 independently recomputed and matched against `APK_SHA256.txt`; signer printout and source SHA were checked in the archive. `9 authored JUnit methods` is a source count, not a claim of nine independent user sessions.

**Important caveat:** Debug signing key is generated on the GitHub-hosted runner and is not a stable install-over identity. A new build might require uninstalling prior QA package and lose its saved local records. Never distribute or commit keystore secrets to the public repository.

## Build corrections recorded

- **Run #1:** GitHub setup-android@v3 attempted obsolete SDK `tools` package; workflow failed before compilation.
- **Run #2:** SDK issue fixed and pure Kotlin `:core:test` ran; Android UI compiler rejected two incorrect import paths (`layout.weight` and `foundation.onSizeChanged`).
- **Run #3:** corrected Compose imports and completed the JVM test/build/verify/upload stages successfully.

## First owner phone checks required

1. Install artifact APK on physical Android; test cold start in airplane mode.
2. Read grid labels, north orientation, and select correct `2437`, `2539`, `2741` **whole kilometre squares** (not pixel centers).
3. Intentionally select wrong E or N, verify explanatory feedback and Undo/recovery.
4. Pinch, drag, zoom +/- and Fit; verify pan does not commit accidental square.
5. Try nudge arrows for accessibility and enlarged-text display.
6. Rotate/background/resume; verify progress retained; repeat hunt.
7. Report screenshots, text clipping, touch confusion, dull/pleasant game feel, and whether you would replay without prompting.

**Acceptance remains OPEN:** GitHub compilation/test success is real, but this build has **never been observed running on the owner's physical Android phone**. Do not mark the GH-01 real-device milestone complete until this happens.
