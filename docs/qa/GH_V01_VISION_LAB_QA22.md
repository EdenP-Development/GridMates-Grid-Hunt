# GH-V01 — Vision Lab QA22: Verified Android Foundation Build

**Date:** 2026-11-22  
**Source built:** `7dfe83cd6f48d33e0b6d7189ef1e2ae3ca30008f`  
**GitHub Actions:** [Run #22](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45065901934), **SUCCESS**.  
**Artifact:** [gridhunt-visionlab-22](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45065901934/artifacts/14771287081) (ZIP containing APK, checksum, source SHA, certificate, install notes).

## Implemented foundation

- **Home hub** with two real playable choices: First Bearings (three authored hunts, nine 4-digit square answers) and Look Again (five Reverse survey markers, four-digit typed answers).
- **Live local progress readout** for the chapter and Reverse Practice, resume behavior and clear Home/Back navigation.
- **Controlled visual A/B exploration:** Field Guide (warm paper/woodland) and Survey Table (cooler drafting palette with more pronounced grid). Switch on hub; the same canonical map/answer data under both presentations.
- **Game-feel building blocks:** completion-only optional vibration (on/off saved locally) and a progress line. No invented scores or feature inflation.
- **Refactored Android source** into MainActivity/home+chapter, ReversePracticeScreen, GridMapCanvas shared by both modes, HuntUi components, and VisualTokens for theme decisions. The pure Kotlin core validator and QA6 gesture engine remain stable.
- **Three authored Compose instrumentation smoke-test scenarios**: hub access, chapter/Reverse navigation, and visual-style toggle. CI **compiles** the Android test APK; it **does not execute UI tests on a device or emulator**.

## Verified CI and APK evidence

| Evidence | Value |
|---|---|
| Run | #22 |
| Conclusion | SUCCESS |
| Gradle commands | `:core:test :app:assembleDebug :app:assembleDebugAndroidTest` |
| JVM test source | **22 authored methods**; `:core:test` passed |
| UI instrumentation tests | **3 compiled; 0 executed** |
| APK | `GridHunt-VisionLab-QA.apk` |
| APK size | **10,212,297 bytes** |
| SHA-256 | `cb85b8b93d3c31f50389065b7ce996b13a0b662d2ddb74c52712f28818ea55ff` |
| App package | `com.playereden.gridmates.gridhunt.qa` |
| Version code | **10022** |
| Version name | `0.3.0-gh02c-qa.22` (scaffold marketing label retained; the build number and source identify this Vision Lab feature set) |
| Signing certificate SHA-256 | `0ff2e763e5667bdfda45aab662639ea6c1c7efa813f82cd4a5cdba8225febd52` |
| Comparison | QA15 package/certificate match; its code was **10015**, so QA22's 10022 qualifies for a normal install-over attempt |
| Local artifact validation | ZIP `unzip -t` clean; extracted APK checksum independently recomputed/matched; signer file inspected; actual package/version read from archived `aapt dump badging` |
| Owner QA22 phone result | **PENDING** |

**Security:** the QA signing key in the repository is intentionally public, disposable, and not an authentic publisher credential. Never use it for release. Install only from the trusted project's GitHub source/archive and do not substitute untrusted APKs.

## Suggested focused phone evaluation (10 minutes)

1. **Update-in-place:** If QA15 is installed, try QA22 *directly without uninstalling*. Check whether saved chapter/Reverse progress remains. Do not claim success until real phone evidence.
2. **First impression:** Does the new hub feel like GridMates and invite someone to play? Do mode cards make sense without an explanation?
3. **Map comparison:** Try one **same four-digit objective** under both Field Guide and Survey Table. Which color/style feels readable and inviting? Do grid labels lose contrast in either?
4. **Flow:** Solve an objective, observe progress/vibration, go Home, enter Reverse and back. Ensure current task resumes.
5. **Rotate/zoom/input:** Verify QA6 pinch, pan and Fit; ensure Reverse numeric keyboard doesn't hide Submit.
6. **Accessibility:** optional vibration toggle, one-handed buttons, Android large text, TalkBack and landscape review as possible.
7. **Feedback:** identify what feels too plain, too busy, repetitive, or lacking a satisfying completion moment.

## Next recommended development after feedback

A **visual/game-feel arc** rather than more modes: create a cohesive map-art/feature/icon registry and visual bible, refine Home+HUD typography, test a brief nonblocking success animation and optional original audio, and make mission completion more satisfying. Hold off on large seeded mission libraries or six-digit interaction until the art/interaction system is ready.

[Full foundation and owner playtest plan](../vision/GH_V01_FOUNDATION_AND_PHONE_PLAYTEST.md).

**Important evidence boundary:** build/test-package success is not a device playtest, Android install-over confirmation, final design approval, or proof that a 14-task game is enjoyable.
