# GH-V13 — The Missing Field Notes: First Mixed Expedition

**Checkpoint:** 2026-11-23 local / 2026-11-24 UTC CI.  
**Status:** IMPLEMENTED + FULL ANDROID EMULATOR CI GREEN. **Not yet owner phone-approved or production-signed.**  
**Exact app/game source SHA:** `351ca9bb138998218fb758c63e33c1c4e86e03c6` (following documentation-only commits may have a newer branch SHA).  
**Branch:** `feature/gh-v02-in-puzzle-map-look` · [draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12) → `main` (unmerged).

## The user-approved direction

Grid Hunt should feel like a **cozy puzzle adventure rather than a formal/military map-training tool**. Chapters should gradually vary the actual puzzle task, with multiple kinds of map-reading and treasure-hunting logic. Custom Play later offers standalone repeatable modes. Do not generate an entire new asset library before organizing rules, progression, native-feeling UI, and real content.

This milestone builds the first **small vertical slice of a hybrid chapter**, not the whole campaign or a generic level generator.

## What's actually playable

**The Missing Field Notes** is one authored four-stage woodland story on the existing world grid. All stages take place inside **ONE responsive map + controls screen**. A correct answer reveals the next *kind* of task.

| Stage ID | Puzzle verb | Player action | Canonical reference |
| --- | --- | --- | --- |
| `locate-trailhead` | `FIND_SQUARE` | Tap 2437 on the map, or use accessible nudge arrows to adjust the selected square | Authored existing grid `2437` |
| `read-glow-a` | `READ_MARKER` | Find the original Golden Spot A dot; enter its four-digit containing square | Original marker A = `2440` |
| `find-mushrooms` | `LOCATE_LANDMARK` | Recognize Mushroom Nook with the matching tiny picture; enter its four-digit square | Existing mushroom landmark = `2538` |
| `identify-waystone` | `IDENTIFY_LANDMARK` | Read square `2941` and select the matching original map landmark picture/name from four options | Original Old Waystone ID = `old-waystone` |

**Developer-only references above are answer fixtures, not a player-visible list of all answers.** Player sees only the active authored clue. Finished stages receive earned map stamps. Error feedback is friendly and a wrong answer never advances the stage.

The chapter includes an optional **Map Guide**, map pan/zoom, Cozy Woods/Cool Sketch style toggle, haptics support, 4/4 end card and **explicit replay**. Home has a new chapter card with a truthful 0–4 progress count.

## Source of truth and progression integrity

- [Core chapter and engine](../../core/src/main/kotlin/com/playereden/gridhunt/core/WoodlandExpedition.kt): `ExpeditionVerb`, `ExpeditionObjective`, `ExpeditionChapter`, `WoodlandExpedition`. IDs/expected answer cells are fixed and validated against existing canonical `GridReference`, `WoodlandLandmarks`, and `ReverseTraining`. Invalid, missing or inconsistent authored landmark/marker content fails the chapter integrity gate.
- [Native Compose mixed screen](../../app/src/main/java/com/playereden/gridmates/gridhunt/WoodlandMixedExpeditionScreen.kt): one map/camera implementation reused across every stage. Stage UI switches between tapping/selecting a square, entering a 4-digit grid, and choosing an illustrated answer tile. No new duplicate map-renderer or art atlas.
- **Save namespace:** `gridhunt_mixed_woodland_v1`, with current step, pending selected square, pending numeric answer, and pending picture option. **No writes** to standalone `gridhunt_first_bearings_v1`, `gridhunt_reverse_v1`, `gridhunt_treasure_checklist_v1`, or `gridhunt_identify_v1`. Completed chapter stays complete on Home/reopen/recreate until deliberate replay.
- **No fake external location/MGRS promises**: the training map still uses fictional 4-digit 1 km areas. The source artwork never determines answer truth; native code does.
- **Do not overstate:** this is the **first chapter controller/data contract** that supports all four verbs inside one mission. It is **not yet** a generalized multi-chapter progression system, random objective generator, route-finding engine, elevation-aware system, or polished chapter-selection hub.

## Exact CI and artifact evidence

**[GitHub Actions QA146: both jobs green](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810)** at source SHA `351ca9bb138998218fb758c63e33c1c4e86e03c6`:

- Compiled all Kotlin code, ran prior core tests plus [`WoodlandExpeditionTest`](../../core/src/test/kotlin/com/playereden/gridhunt/core/WoodlandExpeditionTest.kt), built Android APK, checked fixed QA certificate/package/version compatibility: passed.
- Executed Android 35 emulator instrumentation across existing puzzle modes and new GH-V13 tests: passes for four-verb completion, incorrect-guess non-advancement, return/Home/recreate resumption, persisted finish and explicit replay.
- Required **15** emulator screenshots, including `14_mixed_expedition.png` and `15_mixed_expedition_finish.png` after a **real submitted playthrough**, were exported. Existence and successful UI navigation are NOT automatic proof of ideal pixel layout or accessible phone touch controls.
- [QA146 APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810/artifacts/14877122779) · [Executed Android UI results and captures](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810/artifacts/14878400312). GitHub may require account sign-in.
- QA builds use an established **throwaway public test certificate**, not the future Play Store production release key. Do not uninstall/clear the user's existing app if installation-over fails; preserve user data and collect the exact error.

## Still needs owner phone inspection

Check the new **The Missing Field Notes** card on the scrolling Home screen, stage 1 map-square touch and arrow adjustments, golden marker visibility during stage 2, Mushroom Nook recognition and code entry with phone keyboard during stage 3, picture option tile recognition in stage 4, transitions and status messages, portrait/landscape map size, zoom and shared Map Guide, completion/replay, Home/reopen retained state. **Actual device install-over, data preservation, touch usability and visual quality are NOT VERIFIED by emulator CI alone.**

The growing list of separate Home cards is an acknowledged product design concern. **Do not continue indefinitely adding new top-level cards for each chapter**: the next useful UX architecture may split **Adventure Chapters** versus **Quick Play** and later **Custom Play**, without abandoning existing content or save state.

## Next phase ideas (not implemented by GH-V13)

1. **GH-V13 phone acceptance** first: fix any map scale, clipping, text, input or gesture issue before moving to a major new map.
2. **GH-V14 campaign navigation/contracts:** group chapters into an accessible story journey; introduce small shared mission registry and current chapter display rather than the fifth/sixth/seventh generic home card. Preserve stable IDs, a versioned content manifest, isolated progress, and direct replay.
3. **Next authored chapter:** build a distinct reason to solve map puzzles (e.g. missing supplies) using existing landmarks before requiring additional artwork. If a building/feature is necessary, create just that one matching asset at the point it becomes essential.
4. **Advanced clue content:** use GH-V11's pure-Kotlin uniquely-solvable authored relation gate and explicit repeated-category constraints, not visual guesses about creek distance or imagined elevation.
5. **Professional UI identity:** improve game-hub, chapter route, objective journal, and results structure through native Compose; only then do a cohesive illustration/sprite/style audit; keep GridMates friendliness but make Grid Hunt distinct.

## Future-chat handoff

'Continue GridMates: Grid Hunt from `docs/GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md`, `docs/PUZZLE_SYMBOLS_CLUE_FAIRNESS.md`, and `docs/vision/GH_V13_MIXED_EXPEDITION_VERTICAL_SLICE.md` in `PlayerEden/GridMates-Grid-Hunt`. GH-V13 first mixed chapter is code-complete and QA146 green (game SHA `351ca9b`), but awaits owner phone QA. Draft PR #12 is not merged. Verify latest GitHub state, then work on GH-V14 campaign navigation after any immediate phone fixes. Do not regenerate an art library or overwrite established standalone saves.'
