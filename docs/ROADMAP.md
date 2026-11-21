# Roadmap — GridMates: Grid Hunt

**Baseline date:** 2026-11-21  
**All milestones currently PLANNED.** No game source, APK or passing CI evidence exists yet.  
**Method:** small vertical slices, evidence-gated. Reorder after owner phone feedback.

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
