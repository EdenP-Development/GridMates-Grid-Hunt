# Grid Hunt Android Update Investigation — QA22 → QA23 Emulator Proof

**Date:** 2026-11-22. **Conclusion:** The exact archived QA22 APK **successfully updated in place to the exact archived QA23 APK** on an Android 35 emulator, with no uninstall or data clear, and retained a test app-private canary and the existing First Bearings SharedPreferences storage. **The owner's earlier physical-phone QA22 upgrade failure is not fully explained**, because the prior installed package/certificate and Android error were not captured.

## What actually differed between Grid Hunt APKs

| Artifact | App ID | versionCode | Public certificate SHA-256 |
|---|---|---:|---|
| QA10 | `com.playereden.gridmates.gridhunt.qa` | 2 | `bf54f0d3b2603353f6b3aa9d1ecb2ab1485151c7f53e7d27952d3df05b032231` |
| QA14 | same | 10014 | `0ff2e763e5667bdfda45aab662639ea6c1c7efa813f82cd4a5cdba8225febd52` |
| QA15 | same | 10015 | same fixed QA certificate |
| QA22 | same | 10022 | same fixed QA certificate |
| QA23 | same | 10023 | same fixed QA certificate |

**QA10→QA22 is NOT a compatible Android update.** QA14/QA15→QA22 and QA22→QA23 meet the usual certificate, package, and newer version criteria, but device-specific install behavior still needs device evidence.

## Independent actual APK verification

- QA22: [workflow #22](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45065901934), [exact ZIP artifact](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45065901934/artifacts/14771287081). APK SHA-256 `cb85b8b93d3c31f50389065b7ce996b13a0b662d2ddb74c52712f28818ea55ff`.
- QA23: [workflow #23](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45067543280), [exact ZIP artifact](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45067543280/artifacts/14772016502). APK SHA-256 `7c0e44af7abca70e15c14c46fca3115e6d8beaee5c0dac90dfd80abbc94d2ff5`.
- QA23 contains `UPDATE_PREFLIGHT.json` reporting `PREFLIGHT_PASS` against QA22's pinned identity.
- Both ZIPs inspected for manifest package/version and `apksigner` certificate; exact APK checksums independently verified.

## Real emulator rehearsal result

[Emulator workflow, run #1 — SUCCESS](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45067741823). [Full public evidence archive](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45067741823/artifacts/14772256457).

This workflow:
1. Downloaded **original QA22 and QA23 binaries**, with source pinned by existing GitHub run IDs; no approximation or rebuilding of either version.
2. Rechecked both APKs with `aapt` and `apksigner` against the exact QA22 baseline, including SHA-256.
3. Booted Android 35 emulator, `adb install` QA22, and launched the genuine app.
4. With app stopped, created test app-private file `files/update-canary.txt` and seeded the **actual chapter storage path** `shared_prefs/gridhunt_first_bearings_v1.xml` with huntIndex=1, objectiveIndex=2, hintStage=1 (zero-based indices).
5. Ran `adb install -r` **QA23 without uninstalling or clearing data**; result `Success`.
6. Compared private canary and the full SharedPreferences XML before/after upgrade; both matched byte-for-byte.
7. Launched QA23 and re-read SharedPreferences after first launch: the same real saved chapter state remained. `dumpsys package` reported `versionCode=10023`; screenshot captured.

**Scope caveats:** The chapter state was a synthetic test fixture in the real app storage location, not a human-earned puzzle state. It proves the app-private save survives this emulator update and the app launches; it does **not** prove an Android OEM installer on the owner's physical device will accept the same APK, and does not prove future private release signing.

## Improvements adopted from other projects

- [SudokuTogether's QA update workflow](https://github.com/PlayerEden/SudokuTogether/blob/main/.github/workflows/qa-debug.yml) pins certificate/version and uses a **private QA key stored in repository secrets**. [Its emulator upgrade rehearsal](https://github.com/PlayerEden/SudokuTogether/blob/main/.github/workflows/gridmates-beta2-upgrade-rehearsal.yml) proves a real old→new install and saved state.
- [Sector Rebound's read-only preflight](https://github.com/PlayerEden/Ricochet-Rumble/blob/main/tools/android-update-preflight.mjs) can compare an APK to the **actually installed device APK** via ADB and refuses signer/version mismatches. [Its emulator workflow](https://github.com/PlayerEden/Ricochet-Rumble/blob/main/.github/workflows/android-emulator-upgrade-rehearsal.yml) also tests `adb install -r` with a private-data canary.
- Grid Hunt now has an explicit [QA22 baseline policy](../../distribution/qa-update-baseline.json), [read-only compatibility script](../../scripts/qa_update_preflight.py) and Python policy tests, a **pre-upload fail-closed** candidate check in its main workflow, and a [pinned exact-binary emulator install-over workflow](../../.github/workflows/qa-install-over-emulator.yml).

**Security caution:** Grid Hunt currently uses a **publicly known disposable QA signing key**. It is not a legitimate production distribution identity: anyone can produce a malicious APK signed with this key. Do not install nonproject APKs with this QA package. We intentionally did not rotate QA22's key again; rotation would force another app migration. For production use a separate **privately protected signing key** and app release channel; for a future private QA channel use a distinct package ID if needed to avoid confusing this public test lineage.

## Owner's next safe phone test

**If QA22 is installed now:** use QA23 from the verified GitHub artifact, **install directly over QA22 without uninstalling** and check saved progress. This is an intentional update rehearsal, not a new gameplay milestone.

**If Android rejects it:** do **not uninstall** or clear data; capture the exact installer wording/screenshot and where the APK was opened (Files, browser, GitHub ZIP extraction, etc.). If using a trusted computer with ADB, the installed `versionCode` and certificate can be collected read-only to separate signature conflict from OEM installer behavior. The earlier app identity was lost at the time of uninstall, so we cannot now prove which build QA22 failed to replace.

**If it succeeds:** mark the QA22→QA23 owner device acceptance gate as passed and retain the same QA signing identity for future QA builds.

Issue: [#11](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/11). Do not close until the physical phone update/retained progress is confirmed.
