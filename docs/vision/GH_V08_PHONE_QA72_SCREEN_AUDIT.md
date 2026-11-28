# GH-V08 — QA72 Real-Phone Screenshot Audit & Layout Cleanup

**Date:** 2026-11-23. **Evidence:** eight owner-provided QA72 portrait screenshots from a real Android phone (Home top/scrolled, Trail Treasures clue and trail finish, Golden Spots in progress and 5/5 finish). **Scope:** visual analysis; no touch trace, installation logs, font-scaling recording, or actual landscape screenshot provided.

## What is visibly working

- The Grid Hunt home screen reads more like a gentle woodland puzzle app than the original technical training dashboard.
- Trail Treasures offers a legible cream-and-gold four-digit clue, a large map, visible selected square, movement controls and a clear submit action.
- Golden Spots displays previously discovered squares as gold check stamps, its active marker, and a map-focused 5/5 completion card.
- The illustrated world is consistent between modes (stream, trees, trail, flower patch, mushroom and acorn areas).
- Both completion cards show a specific discovery count, allow a replay, and offer Home.
- The screenshots suggest progression through the author's chapters/spots, but still images alone do not prove gesture ergonomics or that an Android install-over kept prior saves.

## Problems actually visible in QA72

1. **Trail Treasures header wraps badly.** The top eyebrow breaks into two lines, the main title occupies another, and the subtitle is cut to 'A Walk in the...'. This wastes vertical space above the map.
2. **Golden Spots shows stale success feedback.** During a later uncompleted Golden Spot, the field still says 'You got it! The next golden spot is waiting' from the previous answer.
3. **The empty code input looks populated.** `0000` appears inside the input, visually similar to an entered answer, especially with focus.
4. **The gold active point is small against the forest illustration.** Distinguish it from decorative trees and already-completed check stamps.
5. **Home completion actions need to reflect finished content.** The home choices were written as if more spots/trails remain even when all known puzzles are solved.
6. **The map is clearer and bigger, but the visual identity is still restrained.** Decorations are tiny; dense control rows still read slightly utilitarian. This is a future aesthetic pass, not a reason to abandon accurate grid labels.

## GH-V08 implementation

- Rebuild the Trail Treasures header as two simple rows: main title + Home/help; below it the trail name and Warm/Cool. Remove the oversized all-caps eyebrow and shorten `Trail 1/3`.
- Align Golden Spots to the same restrained two-row hierarchy, retaining the in-puzzle palette toggle.
- On a correct Golden Spots answer, advance to an instruction for the **new** dot instead of leaving the old 'You got it' feedback. Completion still has its own congratulation.
- Replace the misleading `0000` code placeholder with an explicit, non-answer hint.
- Improve the active Golden Spot point's cream halo, gold ring and dark center at a fixed readable screen size.
- Home now accurately recognizes complete 9/9 or 5/5 states in its call-to-action.
- Extend actual Android tests to check the trail name and the new post-success Golden Spots instruction.

## Acceptance gates

- A green GitHub Actions Android build, core tests, fixed public QA test signer/version preflight, executed emulator navigation/gameplay tests and exported screenshots.
- The next owner's **phone** install-over should keep package and data (physical-device install/update issue #11 remains open; screenshots alone do not settle it).
- Check that the new header shows **A Walk in the Woods** in full and Golden Spots prompts belong to the **current**, not previous, puzzle.
- Confirm the increased active dot does not cover the containing grid line or its label; the exact point/answer mapping stays unchanged.
- Check portrait and landscape tap/zoom and both mode styles. Still no production signer or release claim.
