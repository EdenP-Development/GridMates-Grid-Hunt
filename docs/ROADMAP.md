# Roadmap — GridMates: Grid Hunt

**Original baseline:** 2026-11-21. Historical GH-00–GH-05 proposals below are retained for context.  
**Current game:** Four standalone woodland puzzle modes plus the playable first **GH-V13 Mixed Expedition story chapter**. The chapter combines a map square tap, golden marker reading, landmark coordinate reading and object identification in one native screen, with isolated saves and real 4/4 completion.  
**Last source-matched fully green Android QA:** [QA146](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810), source SHA `351ca9bb138998218fb758c63e33c1c4e86e03c6`, with passing Kotlin/core, Android/signing/update tests, executed emulator UI and **15** captured screenshots. Owner real-device appearance, feel and install-over not yet approved. [QA146 ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810/artifacts/14877122779).  
**Development:** [Draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12), **unmerged**; not Play Store release-signed.  
**Forward spec:** [Gameplay Blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md) · [GH-V13 test/handoff](vision/GH_V13_MIXED_EXPEDITION_VERTICAL_SLICE.md). Small testable steps, canonical coordinates, game-first UX, just-in-time illustration.

## Active next phases (2026-11-23)

1. **GH-V13 — IMPLEMENTED + QA146 GREEN:** first four-verb Mixed Expedition; phone check for portrait/landscape map size, navigation, symbol guide, numeric keyboard, selection, progression, completion/replay and saved state.
2. **GH-V11 — IMPLEMENTED core:** typed clue fairness gate with synthetic multiple-same-category fixtures; only reviewed authored relations may be published, never guessed creek proximity/elevation.
3. **GH-V12 — IMPLEMENTED + QA132 GREEN:** picture/name grid identification, four canonical woodland landmark questions and isolated saved progress; phone feedback pending.
4. **GH-V14 — NEXT:** chapter navigation and light mission registry grouping **Adventure Chapters / Quick Play**, with eventual Custom Play, avoiding six-plus generic cards on Home. Preserve all existing saved games and old chapter IDs.
5. **Later:** additional authored map/landmark content, unique relation clues, difficulty progression, Quick/Custom generation, native game UI/illustration polish, then full beta and production signing/store compliance. Don't generate a huge art catalogue before the mechanics need it.

**Explicit owner rule:** organize systems and content first; make individual assets when needed for legibility/fairness, with a fuller cohesive art pass after the core modes prove fun.

---

## GH-00 — Documentation and design foundation

**Goal:** define product, accuracy contract, hybrid content plan, sibling-repo reuse principles and an inspectable roadmap.

**Deliverables:** README, project docs, research bibliography, clear source/implementation truth boundaries. This is documentation, **not gameplay completion**.

## GH-01 — First playable coordinate hunt [NEXT]

**Goal:** installable offline Android proof-of-concept with one fictional training map and 4-digit grid puzzle.

**Tasks:** native app skeleton; pure Kotlin grid formatter/parser/validator; 1 km labeled grid with north indicator; 3–5 generated/fixed target squares; marker interaction/undo/submit; result explanation; unit tests; reproducible QA APK.

**Acceptance:** four-digit squares select correctly, including boundaries and leading zeroes; pan/zoom does not move a selected world point; no internet needed; emulator tests execute; owner installs on a real phone and confirms plotting/legibility. Record source SHA/APK identity/tests and real-device notes separately.

## GH-02 — Usable puzzle experience

**Goal:** turn working math into an enjoyable game.

**Tasks:** onboarding; chapter selection; polished objective ribbon; forgiving gestures; resume; hints; results; a small curated set of chapter missions; accessibility checks; stable install-over QA process.

**Acceptance:** first-time player can complete a chapter without outside instruction; interrupted puzzle resumes identically; actual Android portrait/landscape/large-text checks; owner feedback addressed.

## GH-03 — Reverse Hunt and six-digit precision

**Goal:** real two-way skill progression.

**Tasks:** reverse coordinate entry, six-digit 100 m sub-squares, zoom/reticle aid, tutorial on truncation, validated fixtures, independent objective and score handling.

**Acceptance:** no center-click penalty; exact reference interpretation; reverse read matches selected landmark coordinate; leading-zeroes, bounds and zoom invariance pass unit/device tests.

## GH-04 — Mixed missions and seeded Free Hunt

**Goal:** replayability beyond repeated fixed boards.

**Tasks:** map pack manifest, multiple maps or environments, curated landmark clues, seeded generation, novelty/ambiguity rejection, mixed stage controller, progression and journal.

**Acceptance:** same seed/version/map yields identical puzzle; bulk generated missions contain no invalid/ambiguous answers; save/load preserves older missions; real-device map usability verified.

## GH-05 — Beta polish and honest release readiness

**Goal:** stable, coherent standalone puzzle app.

**Tasks:** UI/audio/accessibility refinements, profile or cosmetic selection *if justified*, on-device regression, cold/resume/update tests, privacy/art/source rights checks, sustainable APK signing and owner-approved release checklist.

**Acceptance:** owner-approved quality plus source-matched automated and physical QA, validated signer continuity, stable progress and accurate public feature claims.

## Later / optional — not launch blockers

Daily seeded mission; six-mode expansion (Route/Expedition); digital protractor; fully-qualified real-world MGRS datasets; shareable cards; nearby co-op; 8/10-digit high-precision challenges; cosmetics/monetization. Each requires independent design and test gates.

## Pacing guardrail

Do not add menus, endless collections, new maps or elaborate aesthetic systems before the **map interaction** and **grid validator** are proven fun and reliable on an actual phone. A small excellent puzzle beats a large collection of unverified systems.

## GH-00T — mapping technical research complete as documentation (2026-11-21)

Map renderer/SDK comparison, viewport math, offline map formats/rights and coordinate verification plan added under [research](research/MAPPING_TECHNICAL_REFERENCE_INDEX.md). Implementation remains **PLANNED**; first build must still pass GH-01 coordinate and real-phone tests. Reassess a third-party map SDK only against a demonstrated product need.


## GM01–GM08 research handoff (2026-11-22)

[Gameplay mechanics research track](research/GAMEPLAY_MECHANICS_RESEARCH_TRACK.md) is **documented, not implemented or playtested**. [GM08 playtest gate](research/GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md) recommends starting with existing **GH-01** minimal offline 6×6 four-digit Coordinate Hunt, correct whole-area math, three objectives, forgiving touch and reproducible signed APK, then actual owner Android playtest. **R07 seed/generator research is still open**; don't assume mass-generation is necessary to verify the basic game loop. All milestones above remain planned.


## GH-01 implementation started (2026-11-22)

Android/Compose skeleton and Kotlin grid engine, fixed three-target hunt UI, test source and Android QA workflow are now committed. The milestone remains **not accepted** until real GitHub CI results, an installable APK, and owner physical phone results are documented. [Source-vs-evidence report](GH01_IMPLEMENTATION_STATUS.md). R07 remains open; do not generate masses of objectives before validating the first experience.


## GH-01 first automated build milestone (2026-11-22)

[Android Actions run #3](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45028853252) compiled the app, executed `:core:test`, packaged and signer-verified a debug APK, and uploaded the artifact. Exact receipt: [GH01_QA_RUN_3.md](qa/GH01_QA_RUN_3.md). **GH-01 is NOT accepted yet:** physical Android install/interaction/owner feedback remains outstanding; no emulator instrumentation test run yet. Further work must validate gesture arbitration and map usability before expanding content.


## GH-02A Map-first interface — QA8 built (2026-11-22)

Owner QA6 screenshots supported shifting GH-02 toward a **larger map and smaller objective chrome**. [GH-02A QA8 report](qa/GH02A_MAP_FIRST_QA8.md) documents the compact objective ribbon, weighted portrait map, landscape side panel, preserved gesture engine, successful Android compilation/coordinate tests and verified downloadable QA8 artifact. **No actual QA8 device visual/landscape test has occurred.** GH-02A awaits owner acceptance before GH-02B chapter missions and persisted progress. This section supersedes the old opening “all milestones planned” claim for GH-01/GH-02A only; broader roadmap milestones remain proposed.


## GH-02B First Bearings mini-chapter — QA10 (2026-11-22)

[QA10 evidence](qa/GH02B_FIRST_BEARINGS_CHAPTER_QA10.md) records source-implemented **three authored four-digit hunts** on one fictional woodland map, explicit Next Hunt/Replay Chapter progression, brief optional guide, and local on-device progress restore. Six additional chapter unit tests (17 authored combined) run under successful GitHub `:core:test` with an APK artifact. **Not yet confirmed physically:** chapter resume after process death, landscape readability, gestures through mission transitions, or whether nine squares feels enjoyable. This is a small content/persistence slice, **not the full GH-02 milestone acceptance**.


## GH-02C — fuller phone test and fixed QA update signing (2026-11-22)

[GH-02C QA15 receipt](qa/GH02C_STABLE_UPDATE_QA15.md) documents an additional **Reverse Practice** mechanic with five fresh 4-digit map points; the prior 3×3 curated First Bearings sequence remains. Stable QA signing is supported by a **public throwaway key only**, never a release secret. CI runs #14 and #15 succeeded with **identical cert/package** and versionCode 10014→10015. Android install-over and resume/portrait/landscape gameplay still need physical verification. GH-03 precision and broader chapter content remain further milestones.


## GH-V01 visual / game-feel foundation (2026-11-22)

This [GH-V01 milestone](vision/GH_V01_FOUNDATION_AND_PHONE_PLAYTEST.md) is a controlled Android visual playtest hub and code architecture checkpoint, **not** GH-03 six-digit precision: two modes (9 authored map finds, 5 Reverse tasks), Field Guide / Survey Table palette comparison on identical real puzzles, optional haptic completion, more explicit progress and modular screen/rendering source. Android CI is being extended to **compile** Compose UI smoke tests; no actual emulator/phone instrumentation is implied. Owner acceptance is still required for art/readability/navigation/game feel.
