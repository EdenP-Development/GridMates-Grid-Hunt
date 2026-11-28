# GH-V14-A — Explorer Desk Navigation Shell (QA157)

**Product direction approved:** 2026-11-23. **Implementation checkpoint:** 2026-11-23 local / 2026-11-24 UTC GitHub Actions.  
**App/game source SHA tested:** `17a95c84e2a6d6105f740ecdd3db25ae3c44d645`.  
**Status:** IMPLEMENTED and FULL Android CI GREEN, **owner real-phone acceptance PENDING**. Not production-signed; draft PR #12 remains unmerged.

## What the owner wants

A real Google Play-quality GridMates puzzle game with **its own explorer/map-and-journal identity**, not another bookshelf-style GridMates title or a giant list of form-like mode cards. Main menu should have three clear choices: **Adventure**, **Quick Play**, **Custom Play**. Adventure chapters should eventually follow a route across different authored world maps, with fun mixed-mode puzzle content; Quick Play accesses favorite puzzle verbs directly; Custom will offer map, difficulty and session count only once backed by actual deterministic generation. Assets are intentionally organized/created **just-in-time**, not pre-generated wholesale.

See the complete screen-by-screen approved product plan and visual Bible: [GH-V14 Explorer Desk and Navigation](../GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md).

## What is actually implemented in GH-V14-A

**Main Home / Explorer Desk** in native Compose:
- Top Grid Hunt title, original woodland postcard artwork, friendly short subtitle.
- Three separately legible, large-tap, labeled menu sections: **Adventure**, **Quick Play**, **Custom Play**.
- Adventure teaser/progress reads real `gridhunt_mixed_woodland_v1` progress. No invented chapter rewards or fictitious map unlocks.
- Existing mode-list page is **Quick Play**, accessible via the new hub. It contains the already-built Trail Treasures, Golden Spots, Treasure Checklist and What's at This Grid? (and an existing Mixed Expedition shortcut), with existing saved progress and map style/haptics utilities.
- **Adventure Journey:** World 1 Little Woodland uses the *existing* original woodland artwork and a simple illustrated connected trail, with a single fully playable destination **The Missing Field Notes** (GH-V13). Chapter progress, Start/Continue/Completed labels come from the actual saved 0–4 story stage. Future worlds are clearly marked **not built** instead of faking playable maps.
- **Custom Play:** honest 'in development' screen with future requirements described and a working **Go to Quick Play** action; no pretend random game or settings controls.
- Android hardware Back and the game-screen Home buttons return through the appropriate hub. The new hub navigation is view state only; **all previous puzzle save namespaces and canonical coordinates are unchanged**.
- No new generated image assets, WebView, new church/road/elevation data, networking, production release, or asset-library change.

**Implementation files:** [ExplorerNavigation.kt](../../app/src/main/java/com/playereden/gridmates/gridhunt/ExplorerNavigation.kt), [MainActivity.kt](../../app/src/main/java/com/playereden/gridmates/gridhunt/MainActivity.kt).  
**Future design contract:** [GH-V14 Bible](../GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md).

## Evidence — exact QA157

[GitHub Actions QA157 — SUCCESS both jobs](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45303371335) at source SHA `17a95c84e2a6d6105f740ecdd3db25ae3c44d645`:
- Kotlin core, Android build, instrumentation-test compile, fixed QA signer/update preflight passed.
- Actual Android emulator tests passed, including navigating Home → Adventure → chapter → Adventure; Custom → Quick; old mode links, legacy puzzle gameplay, progress and completed chapters.
- **18 screenshot gates** passed, including `16_explorer_desk_home.png`, `17_adventure_journey.png`, `18_custom_play_preview.png`. Visual evidence does not establish real-device layout approval.

**Download:** [QA157 Android APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45303371335/artifacts/14878854515) (extract APK; requires GitHub login for some users).  
**Android emulator evidence:** [QA157 UI test reports/screenshots](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45303371335/artifacts/14879703093).  
The APK uses the established **test-only** QA signing identity and an increased versionCode. Do NOT uninstall/clear existing app if installing over fails; capture exact error. Release signing and store checks remain separate.

## Phone acceptance checklist — outstanding

Look for: Home's three distinct choices visible with comfortable scrolling, large enough illustrated postcard, Adventure chapter destination reading like a **journey** rather than a form, actual first chapter launch/resume, return navigation, Quick Play scroll/navigation to every mode, Custom Play's honest explanation and its Quick Play link, Cozy Woods/Cool Sketch and haptics reachable, portrait/landscape + larger text, old saves not reset on install-over.

In particular, get the owner's impression of **menu game feel**. This is a first UI/navigation shell; the three Home cards and existing Quick Play list are functional, not final-quality illustrated game art. Do not claim the unified professional menu design is finished.

## Explicit next steps (GH-V14-B and beyond)

1. Owner real-phone QA157 screenshots and interaction impressions; fix layout/clipping/scroll/Back or unclear art before broadening.
2. **GH-V14-B:** make Adventure journey a more distinct, polished connected map route and Quick Play a compact game-like mode picker; consider a small, truly necessary map pin/stamp asset once layout is approved.
3. **GH-V15:** versioned **multi-chapter/world registry** and earned chapter unlock state, built from canonical map/mode data, without corrupting first chapter saves.
4. Add small chapters with genuinely different types of clues, then deterministic Custom Play and Journal/Collection if supported by content.
5. Whole-game visual/asset cohesion audit, accessibility, performance, production signing and store readiness.

## Hand off to a future chat

'Continue GridMates: Grid Hunt from `docs/GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md`, `docs/vision/GH_V14_NAVIGATION_SHELL_QA157.md` and `docs/GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md`. Check draft PR #12 and current CI. GH-V14-A first three-pillar home, single-world Adventure route and Custom placeholder are **built and QA157 CI green**; real phone acceptance and GH-V14-B presentation polish are next. Preserve all standalone save keys; do not generate a bulk asset library.'

**Future status rule:** code/CI SHA refers to the gameplay checkpoint; newer documentation commits are not new game features.
