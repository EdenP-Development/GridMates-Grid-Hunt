# GH-V03 — An Inviting Puzzle-Book, Not a Field Exercise

**Date:** 2026-11-22 (owner direction). **Status:** implementation candidate in draft PR #12, awaiting build/Android instrumented and physical acceptance.

## Owner's design brief
The app **still looks and feels like a military or official training app**, not a friendly puzzle game. This is a product-identity problem, not an objection to the four-digit grid mechanic. Keep the actual land-grid puzzle, but remove intimidating or administrative framing. Treat the next owner playtest as a judgement of *fun, welcome and clarity*, not just accuracy.

## Design promise
**Grid Hunt — a little map, a new mystery, one satisfying discovery at a time.**

- **Emotional identity:** woodland picture book, tabletop map, gentle mystery and tiny discoveries. Suitable for someone who has never read coordinates before.
- **Interaction:** tap, drag, pinch, check your pick, gentle hints and quick progress. No timer or required accounts.
- **Narrative:** small self-contained woodland puzzles, not orders, drills, evaluations, surveys, military exercises or geographic certification.
- **Color/shape:** warm cream, leafy greens, soft gold stamps, layered map paper; rounded cards and easy-to-see controls; generous map-first space and accessible type.
- **Precision:** four-digit references still mean their exact 1 km containing squares; no aesthetic layer can change a coordinate or conceal required labels.

## Player-facing language (stable gameplay IDs unchanged)

| Previous player-visible label | New style |
|---|---|
| GRIDMATES / FIELD NOTES | GridMates • Puzzle Adventures |
| Your Field Journal, Woodland Survey | Your Little Discoveries, Little Woodland |
| FIRST BEARINGS | Trail Treasures — find the square |
| LOOK AGAIN / Reverse Survey | Golden Spots — find the four-digit code |
| survey markers A-E | Golden spots A-E |
| VISUAL PLAYTEST | Make it yours: maps & options (collapsed by default) |
| Check square / Check reference | Try this square / Try this code |
| Hint / Undo / Fit map | Clue / Clear / See all |

**Always keep the puzzle explanation nearby:** First two digits = easting at the bottom, last two = northing on the side; choose the square above/right of those lines. Technical terms may remain inside optional help, not as the app's main personality.

## GH-V03 implementation pass

- Original vector scene on home — winding trail, soft hills/trees, warm sunrise, golden destination dot. Art is a decorative hero, *not answer data*.
- Home now leads with game identity, a tiny map adventure invitation, actual saved discovery counts, then two clearly explained modes. Appearance choices move behind a single expandable option; pref keys, IDs and data intact.
- More readable friendly chapter names/purposes and gold spot labels, keeping IDs/targets and all 4-digit solutions unchanged.
- Both puzzle titles, objective copy, hints, feedback and buttons rephrased to feel like a friendly guess-and-discover game.
- Warm gold-accented clue tokens for both modes, visibly distinct from the map but not an award/score.
- North arrow placed on the map rather than squeezing the mobile game header.
- CI Android emulator navigation tests updated for the player-facing labels and settings visibility.

## Do NOT overclaim

- A changed string or green CI build **does not establish that the app is fun**. An owner phone playtest remains required, with screenshots and candid impressions.
- Golden `✦` markers and a decorative map hero are not a loot or currency economy. No fabricated stars, level unlocks, collection counts or artificial reward promises.
- Existing QA key is public/disposable; app updates must keep current package/signing continuity for the QA lineage. No production signing claim.
- Never convert coordinate education into guesswork by hiding important grid reference labels.

## Follow-on design arc (not yet implemented)

1. **Playtest welcome:** Can a new player understand the two game types within 10 seconds, and start a puzzle without reading instructions?
2. **In-puzzle appeal:** Are the map and clue card easy to see at the owner's screen/text scale, without overwhelming controls or horizontal scrolling?
3. **Delight:** Use short, optional original sounds and a restrained completion stamp rather than bureaucratic toasts or flashy victory animations.
4. **World:** Add a cohesive original woodland asset/symbol language to the real answer-bearing map; avoid visual clutter and accidental hints. Place more authored *landmarks* only when corresponding canonical world data exists.
5. **Depth:** After a fun baseline passes, add a few distinct chapters and rewarding puzzle sequences before procedural level growth.

## QA gate
CI source compilation, JVM core tests, APK signer/version preflight, and executed emulator UI tests should pass. Then test a new version **over** the installed fixed-signer QA package without uninstalling; capture actual Android error rather than deleting progress if rejected. Owner checks portrait, landscape, zoom/pan, high text scale, first launch, both game modes, return Home, and progress persistence.
