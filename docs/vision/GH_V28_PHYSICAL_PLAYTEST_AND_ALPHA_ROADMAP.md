# GH-V28 — Physical playtest and alpha roadmap reset

2026-11-24 owner physical review, 19 screenshots (8580–8598), apparently QA313 based on visible latest artwork; installed version not independently read. QA313 run 45529337991 attempt 2 passed after retry of emulator SDK extraction failure. No release approval.

## Owner direction
Grid Hunt foundation exists but is NOT ready for first itch.io beta comparable to Sudoku. Continue active alpha development. Owner favors retiring Cool Sketch for now and exploring distinct seasonal/environmental adventure areas, potentially a later or side space expedition, inspired by the existing GridMates Word Search space book. World names and exact sequence not approved. Preserve underlying alternate renderer where practical; propose hiding player-facing toggle and safely normalizing legacy saved preference to warm map without altering progression. Defer launcher icon until substantial visual and content work is ready.

## Observed physical evidence
Home, Adventure, Academy (6/9), Quick Play (6/9 squares, 5/5 gold, 4/4 checklist, 4/4 identification), Custom setup; chapter 1 progressed through all four clues, changed map style, completed 4/4, journal entries visible, chapter 2 unlocked. Earlier progress visible after update; not comprehensive migration test. No claim that chapter 2, all custom modes, or graduation were physically completed.

## Prioritized work
P0: remove warm/cool overlay from player-facing map; preserve camera/grid and normalize old style preference; ensure keyboard IME Done can submit valid four-digit answer and confirmation remains reachable. Fix Map Guide visual/text mismatch (one red mushroom, one yellow flower, brown acorn cap in warm artwork; use style-appropriate descriptions or neutral shared wording; reuse actual trees and scenery). Add visible scroll affordance to long menus and journal/guide; verify content end is reachable and controls visible.
P1: equal-height Quick Play cards and aligned buttons; shrink/reposition redundant Home postcard label; avoid duplicate Academy subtitle; prioritize next chapter over replay on completion; improve journal presentation with readable page treatment; improve map landmark visibility without moving canonical coordinates or obscuring grid. Retain accessibility and large touch targets.
P2: improve cohesive illustrated terrain composition, creek/route appearance without changing answer-bearing geometry; richer authored Little Woodland campaign and replay variety; plan versioned per-world maps/landmarks and progression before creating mass assets. Candidate themes: autumn, winter, coast, space; proposals only. Consider season packs and cross-title thematic continuity. Define meaningful progression, difficulty and content depth before beta gate. 
P3: dedicated launcher icon, comprehensive physical and automated regression, portable saves/upgrade rehearsal, signed beta candidate and owner approval. No merge/publication before approval.

## Guardrails
Do not assume decorative scenery is a new answer-bearing landmark. Preserve existing canonical IDs/coordinates, package/signers and saves. No public beta schedule or arbitrary readiness percentage. Continue draft PR #12. 
