# GH-02C — Fuller Two-Mode Android Test, Stable QA Updates (QA15)

**Date:** 2026-11-22  
**CI status:** **SUCCESS** — [QA15 run](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45060871909).  
**Source built:** `afbe7b8e7cf33541f6505f01b5875dacfd1e4c48`  
**APK artifact:** [QA15 stable ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45060871909/artifacts/14769596333).  
**What is *not* verified yet:** Android phone install-over, first use of Reverse screen, actual keyboard/map layouts, landscape, or human skill/experience feedback. Compile/test success is not a physical playtest.

## Why this is a fuller gameplay build

**Original curated First Bearings chapter** remains available: three hunts of three valid four-digit coordinates each, saved locally.
- Trailhead Survey: `2437` · `2539` · `2741`.
- Across the Brook: `2938` · `2642` · `2839`.
- The Final Notes: `2537` · `2841` · `2940`.

**New distinct Reverse Practice mode:**
- Tap **Reverse** in the chapter header, then inspect the **gold marker** on the same fictional woodland grid.
- Read the containing square's **easting (left/west line) then northing (below/south line)**.
- Type the exact **four-digit reference string** (preserves zeros), then **Check reference**.
- Five fresh authored world-point targets: survey A `2440`, B `2742`, C `2838`, D `2541`, E `2942`.
- Helpful wrong-axis feedback, two-tier optional hints, pinch/zoom/Fit, return to Chapter, and Reverse progress saved locally. The five Reverse cells are **not** any of the chapter's nine cells, to avoid success by memorizing earlier objective answers.

The Reverse mode is an exploratory GH-02C playtest feature. It is **not** a final chapter, approved art, realistic surveying simulator, or real MGRS encoding.

## Two consecutive signed builds: objective evidence

| Evidence | First fixed-key QA14 | QA15 update candidate |
|---|---|---|
| Workflow | [run #14](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45060724899) SUCCESS | [run #15](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45060871909) SUCCESS |
| Source SHA | `8df2e166e4e09d579273d2691e722d34fe8cc501` | `afbe7b8e7cf33541f6505f01b5875dacfd1e4c48` |
| Package ID | `com.playereden.gridmates.gridhunt.qa` | **SAME** |
| Version code | `10014` | `10015` (**higher**) |
| Version name | `0.3.0-gh02c-qa.14` | `0.3.0-gh02c-qa.15` |
| Signing certificate SHA-256 | `0ff2e763e5667bdfda45aab662639ea6c1c7efa813f82cd4a5cdba8225febd52` | **SAME** |
| APK SHA-256 | `f662aefca384b64f69d098a6727970af021885cc81a034cad8873107eefc3560` | `3dc6f1a5f68a37e240fb3ae7faf5b6ffaec54490a3f4a2bd2b6c6252efe65a02` |
| ZIP contents | APK, APK_SHA256, SOURCE_SHA, SIGNER, INSTALL_NOTES | **SAME evidence types** |
| Android physical update test | **NOT RUN** | **NOT RUN** |

Both GitHub Action runs executed `:core:test` and `:app:assembleDebug`, signer/manifest verification and artifact upload successfully. **22 authored JUnit test methods**: 11 grid/camera + 6 chapter + 5 Reverse. Each ZIP was downloaded, passed `unzip -t`, and the extracted APK SHA-256 was recomputed and matched `APK_SHA256.txt`. Manifest package/versions and signer hashes were compared across both builds.

## How to verify updates on the owner's phone

**If you have QA10 or an older APK installed:** uninstall it **once**. Those builds use an incompatible ephemeral signing certificate; there is no safe way to install a fixed-key QA build over an unknown old key.

**Simplest path:** install **QA15** directly, play, and wait for a future QA16+ to install over QA15 without uninstalling.

**If you want an immediate proof:** install **QA14**, complete at least one **Chapter** objective, leave the app, then install **QA15 over QA14** using the normal Android installer *without uninstalling QA14*. Reopen: chapter objective/progress and other persisted data should remain. This has not been physically confirmed and requires user testing.

If Android says **App not installed**, capture the exact message. Before assuming version or signer trouble, verify correct package/cert via the artifacts. Version code 10015 is greater than 10014. Avoid uninstalling during this test or progress-persistence evidence will be lost.

## Explicit security warning

This is a throwaway, **PUBLICLY known QA signing key** committed only to support install-over testing. It is **not trusted production signing**, and anyone with access to that public key could sign a malicious APK under the QA package. Install APKs only from the verified project's own links, **never untrusted sources**. The production `com.playereden.gridmates.gridhunt` app must use a separate privately protected release key; no private release key is in the repo. [Full signing details](STABLE_QA_INSTALL_OVER_SIGNING.md).

## Next acceptance checks

1. QA15 starts and switches between **First Bearings** and **Reverse**.
2. Reverse marker always remains in its canonical map world cell during zoom/drag; tapping the map in Reverse **does not grade a coordinate**.
3. Reverse user can type correct 4-digit references, get honest wrong-E/wrong-N feedback and use free hints.
4. Chapter and Reverse progress survive process restart.
5. Portrait and landscape controls are reachable; IME does not bury Submit.
6. User either confirms QA14 → QA15 install-over with saved Chapter data or continues using QA15 ready for the next version.
7. Continue visual polish only after physical feedback. GitHub [gesture issue #10](https://github.com/PlayerEden/GridMates-Grid-Hunt/issues/10) remains open for unverified touch/rotation cases.

**Final status:** QA15 code and artifacts verified; **phone installation/update and game usability still pending**.
