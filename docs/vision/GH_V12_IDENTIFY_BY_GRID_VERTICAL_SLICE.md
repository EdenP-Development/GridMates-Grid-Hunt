# GH-V12 — What's at This Grid? Playable Vertical Slice

**Checkpoint date:** 2026-11-23 (CI completed 2026-11-24 UTC).  
**State:** IMPLEMENTED, Kotlin core + Android emulator CI VERIFIED, owner physical-device UX review PENDING.  
**Exact game-source SHA:** `d065937e1847e2e09d2157b5304f6979d167144b`.  
**Branch/PR:** `feature/gh-v02-in-puzzle-map-look` / [draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12), unmerged.

## One-sentence gameplay

The player receives a **four-digit grid square**, explores the existing woodland map, and chooses **which illustrated landmark belongs to that square** from four picture/name tiles. This reverses Treasure Checklist's object → coordinates challenge and introduces distinct gameplay without a new art pack.

## Implementation truth

- [Core mission](../../core/src/main/kotlin/com/playereden/gridhunt/core/WoodlandIdentifyMission.kt): stable `little-woodland-identify-v1`, four authored questions based on `WoodlandLandmarks`. Each cell/landmark pair is canonical and must be unique; a 4-digit cell is an area, not an exact object pin.
- Exact intro question cells in chapter order: `2538` (Mushroom Nook), `2840` (Buttercup Glade), `2641` (Acorn Nook), `2941` (Old Waystone). **These are developer/test references, not shown together to players.**
- [Android screen](../../app/src/main/java/com/playereden/gridmates/gridhunt/WoodlandIdentifyScreen.kt): map takes priority; two rows of labeled, selectable artwork tiles; color+shape emphasis for current choice; **Check find** disabled until selected. Incorrect selection does not advance; correct selection moves to next question. Shows a true 4/4 completion/replay card.
- Uses exactly the same `HuntMapPanel` world map, pan/zoom, in-place Warm/Cool appearance switching, original shared `WoodlandLandmarkSwatch` renderer and optional illustrated `WoodlandMapGuideDialog`. No second, inconsistent symbol library.
- **Important anti-spoiler:** the prompt presents the *coordinates* and four answer candidates, but does not show a special active-target marker or preview the correct landmark. Solved squares alone get earned check stamps.
- **Isolated save:** `gridhunt_identify_v1` (current step and selected tile), retained across leaving/reopening the mode and Android recreation. Explicit replay resets only the identify session. Existing saves for Trail Treasures, Golden Spots and Treasure Checklist remain untouched.
- Home includes a fourth game-mode card with actual `0–4` progress and accurate completed-state action. No new image assets, account, cloud feature or new map data required.
- **Not implemented:** general campaign controller, procedural Custom Play, repeated-church level with rendered churches, arbitrary-landmark clue generation, scoring/achievements, premium-style hub overhaul, 6-digit precision or production signing.

## Verified CI

**[QA132 run — full green](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45295252097)**, same code SHA above:

- Kotlin/JUnit core mission tests: correct canonical questions, unique grid cells, wrong/unknown choices, step/finish/replay, corrupted-step clamps and malformed-question rejection.
- Android compiler, source/signing/update checks and fixed test signing identity verification: passed.
- Executed emulator Compose navigation tests: wrong answer stays on question; correct answer advances; guide opens/closes; session resumes after returning Home and recreation; genuine 4/4 completion is retained; explicit replay restarts.
- Emulated gameplay screenshot checks: **13** required screenshots now, including `12_identify_woodland.png` and `13_identify_finish.png`. A screenshot existence check is *not* proof of pixel-perfect art or physical tap comfort.

**Artifacts:** [QA132 signed test APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45295252097/artifacts/14876800727) · [Android test results / visual captures](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45295252097/artifacts/14875922864). GitHub may require sign-in. Preserve old QA installation on update failure rather than uninstalling it.

## Real phone test — still outstanding

Ask owner for screenshots/experience of: Home's fourth card and scroll, first prompt `2538`, all four picture/name choices, selected state, wrong guess copy, zoom/pan and map legibility, portrait and landscape on their device, after one correct answer, 4/4 finish card, replay, returning to Home and restarting. Confirm the Map Guide still helps identify the symbols while never revealing locations. Verify Android keyboard is **not** needed for this mode.

The owner has already reported older tiny landmark art was difficult to recognize. The guide and same-source preview art help, but **QA132's real-device appearance has not been reviewed**. A full cohesive game-UI and sprite pass remains a separate milestone, intentionally deferred until the mode structure is proven.

## Next practical development priorities

1. **Owner QA132 acceptance** for whether the identification choices feel like a game, not a form. Fix any screenshot/touch/readability blockers; do not remove compatible APK data.
2. **Puzzles as chapters:** design a small reusable objective/mode switch so Trail Treasures, Golden Spots, Treasure Checklist and Identify can appear in a sequence without duplicate map engines. Safely migrate only when the save contract is explicit.
3. **Clue fairness integration:** [GH-V11 typed validator](../../core/src/main/kotlin/com/playereden/gridhunt/core/ClueValidation.kt) exists and has synthetic multi-church tests. GH-V12 also independently validates unique canonical cells. Future authored maps and mixed missions should use a shared publication gate. Do not fake creek polyline proximity or elevation from drawn decoration.
4. **Targeted just-in-time art + game-level UI polish** when real playtests demonstrate a specific recognition/navigation issue, followed by a later overall style audit.

## Next-chat handoff

'Continue GridMates: Grid Hunt using `docs/GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md`, `docs/PUZZLE_SYMBOLS_CLUE_FAIRNESS.md`, and `docs/vision/GH_V12_IDENTIFY_BY_GRID_VERTICAL_SLICE.md`. Check GitHub draft PR #12 and latest green CI. GH-V12 identification is implemented and QA132 emulator-verified; phone acceptance and campaign architecture are next. Do not confuse the source SHA with documentation-only commits.'
