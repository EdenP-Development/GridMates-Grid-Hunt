# GH-V16 — The Lost Supplies / Two-Chapter Little Woodland

**Checkpoint:** 2026-11-23 local / 2026-11-24 UTC. **Branch:** `feature/gh-v02-in-puzzle-map-look` in [draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12); not production merged or signed.

## Product direction and real behavior

Grid Hunt is a cozy GridMates puzzle adventure. Preserve the warm, practical native Android/Compose approach and keep the player's real map coordinates separate from illustration. Make genuinely different challenges using canonical data first; create asset art only as individual needs emerge. The campaign world **Little Woodland** now contains **two authored chapters**, not placeholder chapters:

1. **The Missing Field Notes** — original four-stage expedition (`woodland-missing-notes-v1`, save `gridhunt_mixed_woodland_v1`) unchanged.
2. **The Lost Supplies** — **new, playable four-stage** woodland expedition (`woodland-lost-supplies-v1`, save `gridhunt_woodland_supplies_v1`), requires original Chapter 1 4/4 completion to enter.

### Real Chapter 2 clue chain

| Stage | Action | Real visible map feature | Canonical correct value |
| --- | --- | --- | --- |
| A glimmer on the trail | Read marker | Golden Spot B (north of main grove) | `2742` |
| The flower clearing | Find landmark grid | Buttercup Glade | `2840` |
| A clue in the north | Identify picture at coordinate | Acorn Nook at `2641` | `acorn-nook` |
| The last known stop | Find landmark grid | Old Waystone | `2941` |

All content is authored against **existing** `ReverseTraining` and `WoodlandLandmarks`; `ExpeditionChapter` validation rejects mismatched canonical marker/landmark cells. Clues use the same native map and 4-digit square conventions, and incorrect answers never advance the stage.

The story identifies the supplies' **last known stop**, not a cache icon or located physical supplies. Do not claim a new cache or building graphic was drawn.

## Implementation, save safety and unlock

- [Core: WoodlandSupplyExpedition.kt](../../core/src/main/kotlin/com/playereden/gridhunt/core/WoodlandSupplyExpedition.kt) authors the story and a reusable `ExpeditionPlaythrough` controller.
- [Core: CampaignRegistry.kt](../../core/src/main/kotlin/com/playereden/gridhunt/core/CampaignRegistry.kt) now registers two real chapters in World 1, with Chapter 2 prerequisite = Chapter 1's stable ID, isolated save namespace, separate step values.
- [Android gameplay](../../app/src/main/java/com/playereden/gridmates/gridhunt/WoodlandMixedExpeditionScreen.kt) reuses the original expedition map camera/pan/zoom, code field, picture options, Map Guide and completion card. Chapter 2 uses **clues solved** and **CLUE 1 OF 4**, while Chapter 1 retains **pages found** and PAGE wording.
- [Android navigation](../../app/src/main/java/com/playereden/gridmates/gridhunt/MainActivity.kt) checks prerequisite before routing to `supplies`, including restored-route guards. Leaving a puzzle returns to Adventure; independent preferences are not modified by returning to Home.
- [Adventure route](../../app/src/main/java/com/playereden/gridmates/gridhunt/ExplorerNavigation.kt) displays both genuine destinations as accessible Compose buttons on a drawn woodland trail. Chapter 2 is disabled until earned; locked, ready, ongoing and complete statuses are described beneath the map. Route's Canvas trees/roads are decorative, never a source of puzzle answers.
- **Do not** delete/rename `gridhunt_mixed_woodland_v1` or Quick Play mode save keys. Version increments must not silently migrate old saves. The signer's identity and physical install-over still need owner testing.

## Verification — exact tested versions

- **QA185** ([GitHub Actions green, both jobs](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45316024306)) at source SHA `cc401342bdee58df3c850eba685a88d1a253a6ff`: compiled Android app, Kotlin core chapter tests, real Android emulator navigations including locked→earned Chapter 2 unlock, wrong-answer handling and four-stage completion. [QA185 APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45316024306/artifacts/14885566007) · [QA185 emulator evidence](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45316024306/artifacts/14885304520).
- **GH-V16-B follow-on candidate:** new road destination placement, Chapter 2-specific "clues" vocabulary and **three extra screenshot gates** `20_chapter_two_locked.png`, `21_chapter_two_unlocked.png`, `22_lost_supplies_opening.png`. [GitHub Actions run](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45317169615). **Do not label this candidate fully green until both actual jobs pass**; code source SHA must be verified from run. QA185 is the last confirmed all-green baseline as of this note.

The visual screenshot test seeds Chapter 1 4/4 **on an isolated CI emulator** only to show Chapter 2's unlocked layout, while the dedicated gameplay instrumentation test earns an unlock through the Chapter 1 progress key and plays all four Chapter 2 tasks. Screenshot presence does not establish owner phone ergonomics or artwork quality.

## Next product / quality milestones

1. Check QA for new two-stop Adventure path and Chapter 2's own text; if a CI check fails, fix it and supersede its candidate.
2. On a real Android phone, install over the existing QA app without clearing data; inspect Home, two-node path, locked/unlocked feedback, four stages, 4/4 finish and replay; ensure Quick Play progress still intact.
3. Refine Chapter 2 narrative and landmark specificity. For future repeated-category clues, use the existing uniqueness/fairness validator; do not generate ambiguous puzzles.
4. Future campaign expansion: additional authored levels, different maps as actual content requires, seeded Custom Play once backed by real generator. Then cohesive asset-library production and whole-game Play Store quality audit.
5. Existing **GH-V14 Explorer Desk design Bible** is the styling source of truth; don't revert to book-shelf UI or claim this is a full campaign.

## Resume next chat

'Continue PlayerEden/GridMates-Grid-Hunt from `docs/vision/GH_V16_LOST_SUPPLIES_TWO_CHAPTERS.md`, `docs/GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md` and draft PR #12. GH-V16 Chapter 2 is authored, isolated-save, earned-unlock, QA185 fully green. Follow-on route placement/accurate clue labels + visual screenshot gates have separate CI. Read latest branch/CI before modifications. Real-phone test still open; avoid mass art and preserve prior save keys.'
