# GridMates: Grid Hunt — current development handoff

Updated 2026-11-24. Read this first when resuming in a new chat. This is a checkpoint, not a replacement for source or design Bibles. Update this document as milestones change.

## Repository and verified build
- Repo: PlayerEden/GridMates-Grid-Hunt; branch feature/gh-v02-in-puzzle-map-look; draft PR #12. Work on this branch, not main. Do not merge or publish without owner acceptance.
- Verified game source: 1679c9e02b1967743bc499ca6e3ef70eee49a030 (GH-V18-B3).
- QA217: https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45335519555 — both JVM/instrumentation compile/signed test APK and executed Android emulator jobs succeeded.
- APK ZIP: https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45335519555/artifacts/14893347454
- Physical phone install-over, visual/touch/landscape and novice playtesting remain open. QA signing is not production release signing. Recheck current PR head and CI rather than assuming this checkpoint remains latest.

## Design sources, in reading order
- [Campaign/product blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md)
- [Explorer desk and navigation Bible](GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md)
- [Two-chapter campaign checkpoint](vision/GH_V16_LOST_SUPPLIES_TWO_CHAPTERS.md)
- [Academy learning and graduation design](vision/GH_V18_EXPLORERS_ACADEMY.md)
- [Symbol and clue fairness](PUZZLE_SYMBOLS_CLUE_FAIRNESS.md)
Older status text and three-home-action direction are superseded where the Academy addition or later source changes conflict.

## Owner direction
Cozy native Android/Compose map puzzle adventure, practical readable UI, warm woodland/parchment/forest explorer identity, distinct from GridMates bookshelf games. Assume zero map-reading knowledge. Mechanics and content first, individual necessary art as needed, mass asset production later. No fabricated playable destinations, rewards, landmarks, or unverified map relations. Keep working on actual code and tests, with verified APK links for owner phone review. Four pillars: Adventure, Academy, Quick Play, Custom Play; eventual Explorer Profile for achievements, cosmetics, qualifications and stats. Academy optional, no forced tutorial, no timers or penalties. First-use coaching, persistent Map Guide, replayable training, hints teach rather than immediately spoil.

## Implemented
- Four standalone Quick Play puzzle families, existing Map Guide and map appearance settings. Custom Play remains honest preview, not playable.
- Little Woodland two authored chapters: Missing Field Notes and Lost Supplies. Chapter 2 unlocks on Chapter 1 completion; supply trail locates last known stop, not a drawn supply cache.
- GH-V17: connected narrative prompts/discovery messages, earned-only chapter journal derived from progress, distinct success feedback, completed journal, campaign-only direct next chapter navigation.
- GH-V18-A: WoodlandSpatialClues resolver validates multiple conditions across all four real landmarks; not yet a playable spatial puzzle.
- GH-V18-B through B3: Academy Home entry, native screen, six exercises: north/east, south, guided grid reading, independent grid selection twice, real woodland Mushroom Nook field exercise. Shared renderer, camera, zoom/reset, Map Guide, directional selection controls. Saves independently, unlimited retries, replay. Core and Android end-to-end Academy tests verified in QA217. No graduation yet. The real map exercise introduces controls but does not require demonstrated mastery of each gesture. More beginner UX polish is needed.

## Stable save/content constraints
- Chapter 1 gridhunt_mixed_woodland_v1 / woodland-missing-notes-v1; chapter 2 gridhunt_woodland_supplies_v1 / woodland-lost-supplies-v1. Preserve objective IDs, order, answers, revision compatibility, and standalone saves.
- Academy gridhunt_academy_exploration_v1, earned StringSet keyed by stable IDs north, south, grid-order, grid-practice, grid-field, woodland-field. Add exercises without invalidating existing earned IDs. No parallel save for journal.
- Key source files: core ExplorerAcademy.kt, ClueValidation.kt, WoodlandExpedition.kt, WoodlandSupplyExpedition.kt, CampaignRegistry.kt; app ExplorerAcademyScreen.kt, ExplorerNavigation.kt, MainActivity.kt, WoodlandMixedExpeditionScreen.kt. Tests ExplorerAcademyTest.kt and AcademyTrainingTest.kt. Update tests that assume current curriculum length when adding exercises.
- Canonical map answers: mushrooms 2538, flowers 2840, acorn 2641, waystone 2941. Use canonical points, not art pixels. Spatial example east of Acorn Nook and north of Buttercup Glade resolves waystone; reject ambiguity. Same-type duplicate landmarks and terrain relations require proper authored map data and art.

## Next implementation order
1. Academy landmark/symbol recognition with real map and legend; route following using cardinal moves and bounded canonical cells; spatial deduction with validated unique answers. Improve teaching feedback and map handling exercises. Add core and real Android UI tests alongside changes.
2. Complete School of Exploration mini-campaign: lesson stages, guided practice, independent examples and graduation expedition combining skills. Introduce permanent earned-once school qualification (Junior Explorer / bronze compass concept), separate from replayable course progress. No paid shortcuts, no duplicate rewards, no fake graduation. Optional first-launch invitation and Academy course selector as functionality matures.
3. GH-V18 spatial/route challenges usable in gameplay; GH-V19 third woodland chapter with genuinely new mechanics; GH-V20 playable Custom Play; GH-V21 new maps/landmarks and later coordinated visual polish. Preserve original chapters.
4. Optional future schools: Navigation (directions/routes/distance), Cartography (precision/scale/terrain), Discovery (deduction/mysteries), later Surveying, Wilderness, Urban, Maritime, Expedition Leadership only with supporting mechanics. Possible titles/insignias, journal covers, profile decorations, Grand Explorer for mastery. These are concepts, not implemented features.
5. Test physical phone and novice comprehension, accessibility, orientation, scrolling, install-over, and maintain stable QA builds. Revisit UI cohesion and assets when mechanics settle.

## Resume prompt
Continue GridMates: Grid Hunt from docs/CURRENT_DEVELOPMENT_HANDOFF.md on feature/gh-v02-in-puzzle-map-look in draft PR #12. Inspect live CI and source first. Continue the next unfinished Academy milestone with actual implementation, tests and commits. Preserve saves and approved product direction. Report verified versus pending, link latest verified APK when useful, and keep handoff current.

## GH-V18-C through E — newer pending checkpoint
- QA217 remains the last confirmed verified build in this handoff until new workflow results are inspected.
- Added real flower symbol matching, bounded directional routes and uniquely validated spatial deduction. Nine practice IDs, adding woodland-symbol, woodland-route, woodland-deduction.
- Added four graduation stages grad-start, grad-route, grad-symbol, grad-deduction. Graduation requires all nine practice exercises. Store graduation StringSet separately in existing Academy prefs; qualifications StringSet contains junior-explorer-v1, earned once and retained across replay. No dedicated collectible artwork/profile yet.
- School overview now shows actual lesson progress, graduation checkpoints and qualification, with begin/resume action. Only one school is playable. Android tests extended to cover overview and full graduation including persistence; core tests cover route boundaries and qualification rules. All these changes are pending CI confirmation; inspect latest run and fix any failures before expanding content. Physical phone QA still open.
- Next: verify and repair latest build; polish graduation presentation and Academy navigation/first-use guidance; obtain owner phone feedback. Then implement reusable spatial/route gameplay for Adventure and third chapter. Optional advanced schools remain planned.

## QA228 physical phone feedback and visual stabilization
- Verified QA228 source 0b2835c81669e4af6b0e121db9441b232b3261ef: both CI jobs passed, run https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45425446296, APK ZIP artifact 14935549406. Owner provided nine physical phone screenshots of Home, Adventure, Quick Play, Academy overview and early lessons; not evidence of graduation completion on phone.
- Observations: consistent palette/readability but prototype-level geometric woodland artwork, generic icons/cards, tall Home hero and destination strips hide Custom Play, oversized Quick Play cards, checklist-like Academy overview, scrolling loses headers and clips answer choices; text-only compass is inadequate; grid boundary labels need exact alignment. Keep native layout, canonical map coordinates, existing save IDs and artwork cohesion. Do not shrink accessibility touch targets simply to eliminate scrolling.
- First corrective commit 9b6ac357afc2263d4e1fb924b9de1feaa546c031 pins Academy header/progress, resets lesson scroll, uses two-column answers and a drawn compass rose; grid label alignment adjusted, needs visual check at multiple font scales. CI pending.
- Next: verify corrective build, compact Home/Quick Play and Adventure layouts, audit real woodland map screens, add coherent illustrated explorer assets and graduation presentation using art Bible; polish Academy overview and novice guidance, verify Android screenshots and physical phone again. No new world or advanced school until stabilization.

## GH-V19 visual development checkpoint
- Home and Quick Play compact layout commits bdffe78591d6b4ca5f6c95378f62954247a8a80b and c6fd813fa20d48bcddc1de00093e25185a19e2da. Home postcard uses original layered evergreen/broadleaf vector scenery (1bb0084cf1622e37e8032d5f07cd038ff72b5578); Adventure uses matching decorative scenery (388482934e3c24374cfa4a6e0f1d3335f63065f8). Canonical puzzle map unchanged.
- CI run 45429002039 passed JVM, compile and packaging but 1/25 Android tests failed due to performScrollTo on pinned academyHome. Corrected in 3f1e31d3c724151481aca1bc05638b1e9adc21d3; rerun status must be checked. QA228 remains last confirmed full pass in this checkpoint.
- ExplorerInsignia.kt adds reusable school shield and Bronze Compass medal. Academy overview crest and actual earned qualification display plus graduation card integrated in b1bf51ae9c804f479d0dfc9fe392ca0fa9f23535. No new entitlement or save keys.
- QuickPlayModeTile.kt now renders four original native vector illustrations keyed by existing route tags, commit 4547d1e3df4555f168b9c15b8707302771e655f7. Preserve fallback glyph and action tags. All latest art changes still require CI/phone visual review.
- Next: finish CI regression verification, consolidate shared art tokens/helpers and inspect full screen captures. Improve actual puzzle UI and map readability before adding more decorative content. No claims of final painted asset pack or production readiness. Owner acceptance and PR merge remain pending.
