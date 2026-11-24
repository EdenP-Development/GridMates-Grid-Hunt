# GH-V14 — Explorer Desk, Adventure Journey, Quick Play & Custom Play

**Status:** owner-approved product direction and phased implementation contract (2026-11-23). This document describes BOTH intended future screens and the smaller navigation shell being developed now; inspect CI and source before claiming a screen is implemented.  
**Reads with:** [Campaign Blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md), [Map Symbol Fairness](PUZZLE_SYMBOLS_CLUE_FAIRNESS.md), [GH-V13 Tested Checkpoint](vision/GH_V13_MIXED_EXPEDITION_VERTICAL_SLICE.md).  
**Priority:** navigation architecture and game feel FIRST, then assets as needed, not a giant generated sprite pack.

## Product identity — distinctly Grid Hunt, recognizably GridMates

The owner wants the warmth and accessibility of GridMates while departing from the bookshelf/library layouts already used by other family titles. **Grid Hunt becomes a cozy explorer desk, travel journal and map journey**: real puzzles, discovery, gentle progression, no military training/bureaucratic UI.

**Three primary home actions (locked direction):**
1. **Adventure** — authored campaign chapters on a visual journey through map regions.
2. **Quick Play** — direct access to individually replayable puzzle families (Trail Treasures, Golden Spots, Treasure Checklist, What's at This Grid?). Mixed Expedition can be featured but should not be confused with an instant randomized mode.
3. **Custom Play** — eventually choose available map, puzzle verb, difficulty and count. At the first GH-V14 navigation shell, show a polished, **honest preview/coming-later explanation**; don't render choices that pretend to start a generator that isn't implemented.

**Secondary utilities (future):** Journal/Discovery Collection, Map Guide, Settings. No empty button or fake saved collection. The existing map-look and haptics controls remain accessible until an actual Settings screen is implemented.

## Screen-by-screen information architecture

### 1. Home / Explorer Desk

- Clear title **GridMates: Grid Hunt**; one generous illustrated **woodland postcard/map** using the existing original artwork.
- Three prominent, tactile, finger-friendly menu surfaces with distinct hierarchy:
  - **Adventure — Continue the journey** (primary; shows actual chapter progress)
  - **Quick Play — Pick a puzzle** (secondary; direct mode library)
  - **Custom Play — Make your own hunt** (third; explicitly 'Coming later' until real session settings exist)
- Calm background paper/forest tones, warm gold discovery accent, decorative texture only if it contributes legibility. Avoid five stacked mode forms and tiny utility rows.
- One quiet progress cue, no made-up badges/rewards.
- No accounts, web links, map tiles, or cloud requirement.

### 2. Adventure / Journey Map

- Distinct from a bookshelf: **illustrated connected trail / pinned chapter destinations**, with journal-note labels and earned stamps.
- **World 1: Little Woodland** is the only authored map. Reuse its real map art and the existing mixed chapter rather than inventing other worlds.
- **First playable chapter node: The Missing Field Notes**. Route to existing GH-V13 mixed screen and respect its saved 0–4 completion/replay.
- Future chapters and regions may appear as **teasers** clearly saying they are not playable. Never silently show them unlocked or pretend an unimplemented quest exists.
- Long-term: authored world registry, stable chapter IDs and revisions, chapter-specific maps/legend data and world-level progress. Do NOT make an image the coordinate authority.

### 3. Quick Play / Mode Picker

- Four genuine standalone modes:
  - **Trail Treasures** — coordinate → tap its grid square (9 existing targets).
  - **Golden Spots** — point → type its 4-digit grid area (5 existing targets).
  - **Treasure Checklist** — find each illustrated landmark → type its grid (4 existing objects).
  - **What's at This Grid?** — coordinate → choose the correct picture/name (4 existing questions).
- Each shows true in-place progress from its existing preferences and a truthful Continue/Replay/Finished status. Reuse existing game screens/IDs/save namespaces.
- Avoid replicating the full home illustration and large title in the Quick Play picker after its first shell works. Eventually use small native game pieces rather than generic Material cards.
- Mixed Expedition can appear as 'Featured Adventure' linking to the same stable chapter; it is not equivalent to a random Quick Play mode.

### 4. Custom Play / Composer (future behavior)

- Launch from Home without any unlock requirement once implemented.
- Initial options: compatible map, mode family, Relaxed/Explorer/Expert, count/hints; no precision level unless actual engine can accept it.
- One-tap sane defaults; advanced options tucked away. Show a valid preview, disable impossible combinations, reproduce an unfinished session with seed+generator version+map revision+objective IDs.
- **Current GH-V14 scope:** readable empty state explaining Custom Play is in development; link back to Home / open real Quick Play if player wants instant play now. **No invented random session, completed game or fake preference persistence.**

### 5. Journal / Collections (future)

- Discovery stamps, actual found landmarks, completed chapter notes and optional art. Do not auto-populate fictional rewards from merely opening a screen. Defer until campaign and data contracts warrant it.

### 6. Settings & Map Guide

- Retain existing Cozy Woods / Cool Sketch and haptics. Map Guide should use the correct current world’s landmark registry and matching image samples; keep independent puzzle/saved-map state.
- When a dedicated Settings view is created, migrate the controls without changing their preferences keys.

## Visual design Bible v1 — no large asset pass

**Concept:** cozy illustrated explorer desk. The *map* is the protagonist; journal papers, brass compass accents, trail lines and discoverable stamps support the map. No military seals/inspection checklists.

**Palette:** preserve existing canonical Compose tokens in `VisualTokens.kt`: Paper `#F8F1DF`, Forest `#315C4D`, Ink `#213D35`, Gold `#D1A350`, Pine `#467B61`, Muted `#63746A`. Warm cream panels and muted cool/blue accents may distinguish options. **Use semantic contrast**, don't scatter unrelated gradients across screens.

**Typography:** strong readable title, compact chapter subtitles, plain-language controls; avoid very small story text on low-res phones. One friendly type family hierarchy, not decorative type across every paragraph.

**Materials and shape:** map sheets, pinned journal tabs, cream cards with dark text, rounded low-elevation game buttons, purposeful strokes/stamps. Distinct Adventure route node vs Quick Play tiles vs Custom setup surfaces. No generic full-screen column of Material buttons as a finished design.

**Art policy:** reuse original woodland postcard, map and vector symbols for GH-V14. The **only early new artwork** should address a proven gap: e.g., one map-route node or background layer required for destination legibility. Later, after navigation and content are stable, conduct a cohesive icon/map asset library audit and build a consistent reusable set. Never copy another GridMates shelf/book interface merely for cohesion.

**Motion & feedback:** brief pressed state, earned stamp and chapter transition when relevant, optional muted sound/haptic later. No obligatory animation slowing play.

**Mobile quality:** portrait and landscape; map must remain large in play; 48dp-ish touch targets; contrast/accessible labels and text scaling; sensible system Back; no keyboard for answer tiles; scroll hints only when content overflows; restore chapter/puzzle state and exact map appearance. Native Compose is already used; visual/game interaction is the polish target, not a WebView rewrite.

## Scope sequence and exit gates

| Phase | Deliverable | Evidence / acceptance |
| --- | --- | --- |
| **GH-V14-A (NOW)** | Home explorer hub with Adventure / Quick Play / Custom and clearly truthful progress. Adventure displays one authored chapter node. Quick Play routes four existing modes. Custom is explicitly not built. | Build and actual Compose navigation tests; existing game save namespaces untouched; orientation screenshots; no fake enabled mode. |
| **GH-V14-B** | Tighten adventure map-route visual and distinct Quick Play tiles after phone screenshots. | Owner recognizability, low clutter and reachable controls. |
| **GH-V15** | Versioned multi-chapter registry and earned chapter navigation. | Validate map/step IDs, prerequisite logic, no retroactive save corruption. |
| **GH-V16** | Additional authored chapter/puzzle list (only essential icons or maps), difficulty progression. | Real puzzle fairness, uniqueness, hint logic, stable resume/replay. |
| **Later** | Seeded Custom Play, Journal/collections, cohesive art & Play Store release readiness. | Truthful supported settings, deterministic content, accessibility, production signing and store checklist. |

**Protect the user's data:** existing separate preferences keys remain unchanged. A hub refactor cannot silently reset or migrate progress. New hub screen state can be disposable in memory; content state is persisted by the existing modes.

**Stop rule:** GH-V14-A is navigation architecture, NOT permission to fabricate new maps, change old puzzles, or call Custom Play implemented.

## Durable handoff for a future chat

'Continue GridMates: Grid Hunt from `docs/GH_V14_EXPLORER_DESK_AND_NAVIGATION_BIBLE.md` and the main Campaign Blueprint. Read draft PR #12 and the latest code-matched Android CI, then continue the GH-V14 explorer home / Adventure / Quick / Custom shell, checking real-phone fit before visual asset expansion. Old modes and their saved progress must survive.'

Update this file after actual tested commits with **exact SHA**, run number, artifact and phone acceptance status; never substitute the documentation commit for an executed game build.
