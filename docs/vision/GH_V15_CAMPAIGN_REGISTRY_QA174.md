# GH-V15 — Versioned Campaign Registry and Progress Safety

**Checkpoint:** 2026-11-23 local / 2026-11-24 UTC GitHub CI.  
**Game-source SHA:** `1eeff5b2c982cfa3a5bf3240410ca1838f589e70`.  
**Status:** IMPLEMENTED, Kotlin core tests GREEN, full Android emulator regression GREEN. Physical owner phone approval still pending.  
**Branch/PR:** `feature/gh-v02-in-puzzle-map-look`, [draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12), not merged; test signed, not a Play Store build.

## What was actually built

GH-V15 adds a **pure Kotlin, versioned, authored campaign registry** and connects the existing Adventure Home progress display to it. It is a safe foundation for multiple future worlds and chapters, **not a second chapter or a feature-rich chapter select UI**.

- [CampaignRegistry.kt](../../core/src/main/kotlin/com/playereden/gridhunt/core/CampaignRegistry.kt): `CampaignWorld`, `CampaignChapter`, `CampaignSnapshot`, `CampaignChapterStatus`, `CampaignChapterProgress`, `CampaignRegistry`, and `GridHuntCampaign`.
- The sole published world is `little-woodland-v1` and its only registered playable chapter is the real, existing `woodland-missing-notes-v1` with **four** authored objectives.
- The existing save namespace `gridhunt_mixed_woodland_v1` and `step` value are **read in place**, with a clamp to 0–4. **No migration, copied save, or write-back**. Quick Play and the other four puzzle-mode preferences are unchanged.
- Chapter records have unique stable IDs, version revisions, world IDs, order, prerequisite IDs, and existing persistence keys. Invalid duplicates, missing references, same-world forward dependencies, and cycles across worlds fail publication validation.
- Chapter status is one of `LOCKED`, `READY`, `IN_PROGRESS`, `COMPLETE`. Dependencies unlock only when all prerequisite chapters have genuinely reached their last authored objective.
- The [Android MainActivity](../../app/src/main/java/com/playereden/gridmates/gridhunt/MainActivity.kt) uses this registry's progress result to present existing Little Woodland completion values. The actual puzzle screen still owns advancing/finishing its separate save.
- No second world, fictitious landmarks, fake chapter nodes, random Custom Play generator, new UI asset pack, or production signing added.

## Exact source-matched QA

[QA174 — both GitHub Actions jobs PASS](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45309295666), app SHA `1eeff5b2c982cfa3a5bf3240410ca1838f589e70`:
- Android/JVM compilation, [CampaignRegistryTest.kt](../../core/src/test/kotlin/com/playereden/gridhunt/core/CampaignRegistryTest.kt), and all existing core tests: PASS.
- Stable QA APK packaging and signing/update checks: PASS.
- Executed Compose Android emulator full navigation/gameplay suite and screenshot collection: PASS.
- [QA174 signed test APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45309295666/artifacts/14882132407) · [Android emulator tests and screenshots](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45309295666/artifacts/14883480293).

QA174 requires owner phone visual/installation confirmation. The app uses a disposable test signer, not a Play Store release identity. Preserve any existing QA installation and data if an update fails.

## Content validity and next phases

1. **GH-V15A DONE:** foundation registry, valid authored single chapter, deterministic unlock state, legacy save compatibility, successful regression checks.
2. **GH-V15B:** create a *small second chapter* in the current world only when its narrative, learning arc, map objects, unique correct answers and progression are all authored and tested. Link its prerequisite to completed Missing Field Notes and give it its **own** separate versioned save key. The present registry is already capable of expressing that relationship.
3. **Adventure UI:** once a second chapter truly exists, render two actual nodes from registry data and locked/unlocked states. Do not draw a false playable stop or imply a future world is released.
4. **Future world content:** new world ID, map ID, revised symbol guide, valid canonical coordinate truth; create only the needed assets when those chapters are ready.
5. **Eventually:** deterministic Custom Play, collections, broader difficulty progression and professional asset cohesion pass. Retain cozy explorer-desk identity and GridMates accessibility.

## New chat handoff

'Continue GridMates: Grid Hunt from `docs/GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md`, `docs/vision/GH_V15_CAMPAIGN_REGISTRY_QA174.md`, and `docs/GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md`. GH-V15A versioned campaign registry and read-only legacy progress integration are built and QA174 fully green at source SHA `1eeff5b2`. One real world/one real story chapter only. Draft PR #12 unmerged. Next thoughtfully author the second genuinely unique chapter and unlock it after Chapter 1; do not invent extra map assets or overwrite old saves. Physical phone acceptance still open.'
