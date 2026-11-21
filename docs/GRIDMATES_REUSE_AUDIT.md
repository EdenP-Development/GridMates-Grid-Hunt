# GridMates Sibling-Repository Reuse Audit

**Review date:** 2026-11-21  
**Repositories inspected:** [SudokuTogether](https://github.com/PlayerEden/SudokuTogether), [GridMates-Word-Search](https://github.com/PlayerEden/GridMates-Word-Search), [GridMates-Crossroads](https://github.com/PlayerEden/GridMates-Crossroads). Findings are design guidance, **not a claim of ported source**.

## Sudoku — high-value patterns

- Native Kotlin/Compose **:app + pure Kotlin :core** split and JVM unit tests.
- Gradle/GitHub Actions workflows, Android tests and source-matched QA receipt discipline.
- Local saves, achievements, nearby play, asset registries, onboarding and accessibility considerations.
- Existing [reusable Android puzzle foundation plan](https://github.com/PlayerEden/SudokuTogether/blob/main/docs/REUSABLE_ANDROID_PUZZLE_FOUNDATION.md) explicitly says the game is **not yet a drop-in generic framework**.
- Important warning from README: a CI-green source build is not necessarily a signed production-safe update. QA app identities, signing continuity and real phone testing are tracked separately.

**Reuse:** ideas, tested structural patterns, selected self-contained utilities *after reviewing current code*. **Do not clone:** Sudoku cell rules, Sudoku-specific network protocol, massive cosmetic catalog, signing secrets or app ID.

## Word Search — high-value patterns

- [Master design](https://github.com/PlayerEden/GridMates-Word-Search/blob/main/docs/MASTER_GAME_DESIGN.md) that distinguishes DECIDED vs PROPOSED vs IMPLEMENTED.
- [START_HERE](https://github.com/PlayerEden/GridMates-Word-Search/blob/main/docs/START_HERE.md), roadmap and QA notes for handoffs across sessions.
- Deterministic puzzle/content-generation plans and explicit QA for content rights and editorial quality.
- Strong attention to zoom/pan, touch selection, layout on real phones, printable-book feel and chapter/book collections.
- Caution: on the inspected default `main`, much of the current history is **documentation**; playable development/QA is described on draft/feature branches. Verify a specific branch/source SHA before borrowing code.

**Reuse:** design contracts, content versioning, user guidance, adaptive touch lessons and art pipeline provenance. **Do not clone:** word-selection engine, letter-grid UI or large word-library program.

## Crossroads — series reference only for now

Default `main` inspected as a minimal README, while other project work has been described elsewhere. **Do not infer current playable Crossroads features from this main branch**. Revisit active feature branches before using specific code or asset claims.

## Shared series identity

- Distinct **standalone** apps with a familiar, welcoming GridMates visual language.
- Offline-first, no required account; polished Solo is valuable in its own right.
- Friendly human-centered nearby play is a **series philosophy**, not a compulsory v1 requirement.
- Cozy puzzle-book spirit with modern accessibility and nonliteral paper textures.
- Original asset provenance, large-tap controls, soft progress celebration, honest records and quality-gated APKs.

## Grid Hunt-specific non-reusable parts

Coordinate system semantics; meter-space map viewport/inverse transforms; 1 km/100 m quantization; landmarks/legend; grid-reference input; map authoring; quality of map interpolation; local training vs real-world MGRS distinctions.

## Anti-patterns to avoid

- Branch/document drift without a canonical current handoff.
- Large content/collection expansion before excellent core gameplay.
- Mistaking compiled tests for executed tests.
- Accidental phone save loss from signing/package changes.
- Explaining a hypothetical game framework as if it exists.
- Duplicate navigation menus or network infrastructure simply because siblings have them.

## Decision

Start Grid Hunt with **its own small Kotlin/Compose app and coordinate core**. Reuse compatible conventions and selectively copy audited utilities later. Only claim source-level reuse after a recorded code review and regression tests.