# GH-V14-C — Explorer Home Hierarchy (QA169)

**Checkpoint:** 2026-11-23 local / 2026-11-24 UTC GitHub QA.  
**Game source SHA:** `2ccdfdea693367a873804e23bae0bdf10270be3c`.  
**Status:** IMPLEMENTED / FULL ANDROID CI GREEN / OWNER PHONE REVIEW PENDING.  
**Repository:** [Draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12), still unmerged and not production-signed.

## Why this iteration exists

Owner QA157 screenshots confirmed the structural Adventure / Quick Play / Custom Play split works, but Home still appeared as three oversized near-identical app-form cards. Adventure had a duplicate postcard/card hierarchy; Quick Play was too long. GH-V14-B solved the first playable Adventure route and compact mode selection, with [QA167 fully green](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45306545507). GH-V14-C **finishes the next Home hierarchy iteration** using existing artwork only.

## Implemented in the Android app

- Home now features **one primary illustrated Adventure area** with existing WoodlandPostcard artwork, World 1 / Little Woodland, the real 0–4 page progress or completion message, and a clear 52dp+ Adventure launch action.
- **Quick Play** and **Custom Play** are smaller distinct destination strips instead of full-height copies of the Adventure card. They retain separate real navigation actions and minimum-height 48dp targets.
- Custom Play clearly says **Coming later**; its preview isn't a fake session generator.
- Home uses the original Grid Hunt palette and native Compose UI, not a new image pack or WebView.
- Adventure's woodland route still contains only **one** genuinely playable chapter; the route is a UI illustration, not authoritative puzzle geometry.
- Quick Play remains the **2×2 mode picker** with independently persisted true progress for Trail Treasures, Golden Spots, Treasure Checklist and What's at This Grid?, plus a separate Mixed Expedition shortcut.
- Existing map appearance/haptics options, chapter progress, map coordinates and old save namespaces are unchanged.

## Exact full-green CI evidence

[QA169 GitHub Actions](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45307635461), code SHA `2ccdfdea693367a873804e23bae0bdf10270be3c`, passed:
- Kotlin unit tests, Android compilation, instrumentation-test compile, APK signing and update checks.
- Actual Android emulator navigation, existing puzzle playthroughs, Home assertions and required screenshot collection (including `16_explorer_desk_home.png`, `17_adventure_journey.png`, `19_compact_quick_play.png`).
- [QA169 installable APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45307635461/artifacts/14880534912).
- [QA169 executed UI-test reports + screenshot artifact](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45307635461/artifacts/14882265750).

**Physical user-device visual approval/install-over is still not verified.** The stable QA signer is for testing only, not the production Play Store key. Do not uninstall or clear the existing QA app to bypass an update error without deliberate backup/data-preservation steps.

## Phone checklist

- Does the single featured Adventure panel feel meaningfully different from Quick and Custom rather than three identical cards?
- Does Home show the primary action without excessive scrolling on the owner's device?
- Does World 1 / Little Woodland progress read accurately after completing 1–4 pages?
- Is the Quick Play 2×2 picker legible and comfortable on portrait and landscape screens, especially titles with two lines?
- Is the route's live Chapter 1 button discoverable and correctly connected to The Missing Field Notes?
- Can the user navigate back and forth between Home, Adventure, Quick Play, Custom and every existing puzzle without losing the saved session?
- Are the Map Guide, warm/cool maps and haptics controls still reachable?

## Next development plan

1. **Phone QA169 owner review:** inspect these screens and resolve concrete clipping/spacing/touch issues.
2. **GH-V15 content registry and chapter architecture:** plan stable world/chapter IDs, chapter prerequisites and a progression path that can grow beyond one authored chapter. The next implementation should not invent playable levels just to fill the map.
3. **Just-in-time art:** new chapter pin, journal stamp or map-layer assets only when the finalized navigation actually needs them; later a cohesive reuse-first sprite catalog.
4. **Custom Play and broader campaign:** only once deterministic content, difficulty and player setup controls are real, not placeholders.

## Resume in a new chat

'Continue GridMates: Grid Hunt from `docs/GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md`, `docs/vision/GH_V14C_HOME_QA169.md` and `docs/GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md`. Draft PR #12 remains open. GH-V14-C game SHA `2ccdfdea...`, QA169 fully green, phone review not complete. Next prepare GH-V15 chapter registry while preserving all saves and original map truth; do not generate a wholesale new art pack.'
