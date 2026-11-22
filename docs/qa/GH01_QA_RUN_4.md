# GH-01 — Unattended QA Pass #4 (No Owner Phone Required)

**Date:** 2026-11-22  
**CI:** [Grid Hunt GH-01 Android QA, run #4](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45030382385) — **SUCCESS**  
**Build source SHA:** `92b11463bc8214222792b473c1d9ba8259c7d400`  
**Artifact:** [gridhunt-gh01-debug-4](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45030382385/artifacts/14756798911)  
**GitHub Actions:** `:core:test :app:assembleDebug` completed successfully; nine original plus two added camera unit tests = **11 authored JUnit methods**.

## Verified artifact evidence

- Archive ZIP successfully downloaded and tested for corruption.
- Contains APK, checksum manifest, source SHA, signer record and installation notes.
- APK: `GridHunt-GH01-debug.apk`, **10,130,369 bytes**
- **APK SHA-256:** `463c8679573d4c310a9fd9432152320647151eb1432bc91557587c36ec7d6822`. Independently recomputed from extracted APK; matches archive `APK_SHA256.txt`.
- Signer certificate: `C=US, O=Android, CN=Android Debug`
- **Signer certificate SHA-256:** `1c80b17cccfaed80364c52d15308fc61a998203d074d1453dc16dcd77bb009e6`
- Android app QA package (configured): `com.playereden.gridmates.gridhunt.qa`, version `0.1.0-gh01-qa`, versionCode `1`.

## Focused changes since run #3

1. Store map camera's world center and zoom factor through `rememberSaveable`, reconstruct from new viewport fit when size/orientation changes, instead of silently returning to initial center/zoom.
2. Enlarged directional/zoom buttons from **43dp to 48dp** and removed a nonessential row label to keep controls fitting on smaller screens.
3. Added pure JVM tests for viewport reconstruction and map-bound pan clamping, without changing canonical coordinate rules.

## Evidence boundaries

- **Verified:** core JVM task success, APK compilation/signing/upload, archive integrity and exact source/hash identity.
- **NOT run:** Android emulator instrumentation, Android TalkBack, real phone gestures, screen orientation usability, actual save persistence after force-stop, human first-time experience and gameplay enjoyment.
- Compose's separate tap/transform pointer handlers follow the documented high-level pattern, **but the actual interplay is not established until instrumented/phone gesture tests**.
- GitHub-hosted runner uses a **new ephemeral Android debug certificate**: signer fingerprint differs from run #3; do not claim update-install compatibility. Uninstall the previous Grid Hunt QA package before installing run #4, if run #3 was installed. Progress may be lost. This is not production signing.

**Current decision:** keep GH-01 content frozen at three four-digit targets. The highest-value remaining gate is the owner's physical Android playtest; expand gameplay only after feedback. `R07` generator and additional modes remain deferred.
