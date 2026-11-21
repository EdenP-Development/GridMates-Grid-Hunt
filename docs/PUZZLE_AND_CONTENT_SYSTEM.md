# Maps, Puzzles and Procedural Content

**Status:** proposed content architecture, not implemented.

## Recommended strategy: hybrid

1. **Handcrafted base maps** with a small, readable set of verified landmarks, paths, water, and terrain textures.
2. **Curated chapter missions** with intentionally chosen targets and lessons.
3. **Seeded Free Hunt missions** that draw different sets of valid locations or features from the same approved map.

Do *not* generate arbitrary topographic heights/contours, fake geographic coordinates or entire new realistic map worlds for v1. A modest library of reusable maps is sufficient.

## Data contract (conceptual)

**MapDefinition:** stable mapId, revision, title, local coordinate extent in meters, north-up orientation, grid origin/interval, art layer IDs, landmarks, available target points, attribution/source metadata, and optional training-vs-real-grid descriptor.

**Landmark:** stable ID, display label/category, local easting/northing in meters, map symbol, visibility, and approved clue tags. Visual art and a landmark's exact coordinate are separate data; moving the art asset does not silently move the coordinate.

**ObjectiveDefinition:** stable ID, mode, requested numeric precision, answer type (square/pin/text), canonical target position or feature ID, clue text, optional dependency on an earlier stage, allowable hints and validator version.

**PuzzleInstance:** stable instance ID, generator version, seed, map ID/revision, ordered objective IDs/coordinates, difficulty, assist/score flags, and deterministic content hash. A saved game must reload the same puzzle after upgrades.

## Generator responsibilities

- Pick a valid map for the requested skill level and precision.
- Draw from **verified eligible locations**; a random square may be a valid plotting target even when no landmark is attached.
- Add meaningful distractor landmarks, avoiding excessively crowded tap targets.
- Enforce unique target IDs, answerability, legible distances and no accidental clue ambiguity.
- Keep target count within calibrated range; avoid duplicate identical references unless specifically designed.
- Deterministically produce the **same result from map pack version + generator version + seed**.
- Retry boundedly; on failure use a verified fallback puzzle, never silently publish an unvalidated one.
- Preserve/identify version changes so old active saves and Daily results do not mutate.

## Chapter vs Free Hunt

**Chapter puzzle:** immutable level ID and objective manifest, fixed map revision, authored progression, known solution, repeatable replay.

**Free Hunt:** map + seed + puzzle version, random valid objective set; no assumption that every random combination is equally interesting. Maintain a filter/novelty policy so repeated sessions don't constantly choose the same visible landmarks.

**Daily (future):** deterministic date, locale/date-rollover policy and generator version must be documented before launch; never promise identical daily puzzles without pinned content.

## Illustrative puzzle set

- **Woodland 01 — First Pins:** select three four-digit 1 km grid squares.
- **Woodland 02 — Wrong Turn:** four four-digit squares with distractor cabins/trails.
- **Woodland 03 — What's at That Grid?:** plot references, then choose the actual landmark shown.
- **Woodland 04 — Read the Sign:** reverse plot two unique symbols.
- **Woodland 05 — Closer Look:** first six-digit 100 m location with a magnified sub-square overlay.
- **Woodland 06 — Supply Trail:** a deliberately curated two-step coordinate and landmark hunt.

These are design examples, **not playable content**.

## Content authoring and rights

- Prefer original vector/icon art with a consistent license/source ledger; store exact file, source/license, crop/transform and revision.
- If USGS real maps are explored later, record the individual product/dataset's usage terms, map date, datum, reference system and whether third-party rights exist. Don't assume every attached map element is freely redistributable.
- Generated images are illustration layers only. Numeric coordinates and puzzle answers come from the structured data, **never** image interpretation.
- New map packs pass both scripted puzzle checks and human visual/legibility review before release.

## Implementation order

First hardcode one small training map with 3–5 verified squares and two landmarks. Generalize data only after at least one Coordinate Hunt and Reverse Hunt work correctly. Do not create a CMS, hundreds of assets or extensive authoring UI prematurely.