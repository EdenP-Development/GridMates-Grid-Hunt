# Roadmap — GridMates: Grid Hunt

## Current roadmap — GH-V45-A (2026-11-27)

See [current development handoff](CURRENT_DEVELOPMENT_HANDOFF.md) for exact source and QA receipts. This supersedes historical status below.

| Workstream | Current state |
| --- | --- |
| Little Woodland | Existing three Adventure chapters, five Quick modes, Academy and Custom preserved |
| Multi-world foundation | Exact map/campaign identity, scoped selection, terrain scenes and isolated sessions implemented |
| Whispering Coast | Six landmarks, three four-objective chapters, four Quick modes and five Custom modes; tested preview only |
| Regional navigation | Shared controller, guarded next chapter, published/preview separation; QA533 verified |
| Regional presentation | Illustrated Atlas portraits and compact hubs; 29 screenshot gate; final pixel/device acceptance open |
| Published Atlas | Published-only host and tests verified QA533; normal Home entry implemented GH44Y |
| MainActivity acceptance | QA534 all 64 tests finished successfully but screenshot transfer failed; GH44Z retry and new recreation test awaiting full verification |
| Save preservation | QA534 signed install-over passed; dedicated earned-ledger and legacy replay migration still open |
| Chapter expansion | 12 meaningful levels target, minimum 10 mature; explicit non-destructive migration required |
| Large-scale exploration | Synthetic scaling fixture, camera/clipping, larger authored map, persistent multi-session expedition; eventual 1,000+ discoveries |
| Release | No merge/publication/release; actual device review, production signing and store readiness pending |

Last fully green QA533 run 46096770163. QA536 run 46099562525 was in progress at inspection, art #116 passed. Documentation commits may cause a new run; verify newest source. Preserve existing saves, approved branding and QA signer.

Next: consolidated green QA; actual Home/Atlas visual review and playable device candidate; earned ledger and legacy progression tests; explicit coast publication decision; chapter expansion and large-map infrastructure. See [chapter standard](ADVENTURE_CHAPTER_LEVEL_STANDARD.md) and [large-scale vision](LARGE_SCALE_EXPLORATION_VISION.md).

## Historical roadmap and milestones


**Latest actual milestone:** **GH-V14-A three-pillar Explorer Desk navigation BUILT and QA157 CI green.** QA157 game SHA `17a95c84e2a6d6105f740ecdd3db25ae3c44d645`, **both** Android/core/signing and executed emulator jobs passed, with **18** required screenshots. [QA157 APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45303371335/artifacts/14878854515). Physical QA157 phone approval/install-over remain open. [Draft PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12) remains unmerged/not production.\n**Existing play:** Four standalone puzzle modes and one four-step Mixed Expedition remain accessible with their independent saved progress; Adventure gives that story a Little Woodland chapter path. The Custom screen is an honest future-feature preview, not a generator.\n**Specification:** [Explorer Desk design Bible](GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md) and [QA157 handoff](vision/GH_V14_NAVIGATION_SHELL_QA157.md). No art library expansion yet.\n\n## Near-term milestones\n\n1. **GH-V14-A COMPLETE / PHONE PENDING:** new Home / Adventure / Quick / Custom routes, real chapter launch, native navigation tests and 18 emulator screenshots.\n2. **GH-V14-B NEXT:** owner phone QA157; refine chapter path visuals, Quick Play density and menu touch/scroll ergonomics. Only create essential art when it fixes an observed problem.\n3. **GH-V15:** stable multi-chapter/world registry, prerequisites and progress preservation.\n4. **GH-V16+:** thoughtful new authored puzzles/landmarks, difficulty progression, deterministic Custom Play; later Journal, cohesion art pass and store signing.\n\n**Explicit owner rule:** organize systems and content first; make individual assets when needed for legibility/fairness, with a fuller cohesive art pass after the core modes prove fun.

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
