# R02 — Making Coordinate Hunt Fun and Replayable

**Research date:** 2026-11-21  
**Scope:** moment-to-moment **puzzle game feel** and replay motivation, *not* the underlying map renderer, asset pipeline, full map-generation technology or final scoring economy.  
**Status:** RESEARCHED / PROTOTYPE HYPOTHESES. No Grid Hunt prototype, player study or production gameplay has been tested.  
**Parent:** [Open design research backlog](OPEN_DESIGN_RESEARCH_BACKLOG.md), R02. **Previous:** [R01 first 30 seconds](R01_FIRST_30_SECONDS_ONBOARDING_RESEARCH.md).

## 1. Core research question

A coordinate reference plus a map is a mathematically valid exercise, but **does it feel like an interesting puzzle?** What makes searching, selecting, correcting and completing a mission enjoyable enough to play repeatedly **without** obscuring the genuine easting/northing skill?

The owner wants a friendly GridMates puzzle game: small early maps, increasing precision and skill combinations, custom Free Hunt later; **not** a military simulator or Google Maps. This report explores **player-facing gameplay only**.

## 2. External evidence and caveats

| Source | Evidence actually reported | Useful implication | Caution |
|---|---|---|---|
| **Lomas et al., CHI 2017**, [TU Delft peer-reviewed publication record](https://research.tudelft.nl/en/publications/is-difficulty-overrated-the-effects-of-choice-novelty-and-suspens/), DOI [10.1145/3025453.3025638](https://doi.org/10.1145/3025453.3025638) | Three experiments over **20,000** online educational-game sessions: easier games were especially motivating when difficulty was assigned blindly; **moderate novelty** helped more than low/high novelty, and close outcomes showed benefits. Choice affects how difficulty is perceived. | Early play should be straightforward, with a measured cadence of small surprises and optional challenge choices. A puzzle can remain satisfying without escalating constantly. | One online educational game and its particular measurements, not empirical proof of Grid Hunt's specific design. Suspense via near-win in a competitive game need not translate to a solitary grid puzzle. |
| **Brändle et al., Scientific Reports (2025)**, [PubMed](https://pubmed.ncbi.nlm.nih.gov/41034230/), DOI [10.1038/s41598-025-14628-2](https://doi.org/10.1038/s41598-025-14628-2) | Large observational sets from a puzzle game (**7,994 levels / 376,341 votes**), racing game and platformer, plus controlled experiments: intermediate difficulty was generally preferred; **expected difficulty and success** also mattered. | A level labeled "Easy" should not spring unexpectedly advanced tasks; give frequent visible success, but make optional challenges discoverable. Measure enjoyment separately from raw level difficulty. | Observational ratings do not establish cause; different game genres and tasks limit direct transfer. |
| **Denisova et al. (2023)**, [pre-registered study](https://pmc.ncbi.nlm.nih.gov/articles/PMC9890114/) | Their experimentally manipulated difficulty–skill balance **did not** significantly change engagement/enjoyment in their particular AI-controlled game, challenging a universal flow "sweet spot" claim. | Don't assume a formula equating difficulty to skill automatically makes a fun game. Test clarity, curiosity, autonomy and perceived progress too. | A null result in one game is not proof difficulty never matters. |
| **Kristensen et al. (2024)**, [mobile puzzle difficulty-modeling research](https://onlinelibrary.wiley.com/doi/10.1155/2024/5592373) | Perceived puzzle difficulty differs between players/cohorts; attempts, action counts and other observable metrics can help predict difficulty, with limits. | Prototype difficulty knobs separately (precision, clue ambiguity, target count, map complexity, task combination) and observe actual errors/effort. | Their model was studied in a particular commercial puzzle game. We do **not** need player-tracking infrastructure or AI adaptation for MVP. |
| **Kao et al., CHI 2024**, [author's research summary](https://nickballou.com/publication/2024-kao-et-al-juicy/) | Pre-registered online experiment (**n=1,699**) in an action RPG: feedback amplification alone could **reduce** motivation, whereas legible action/outcome relationships and curiosity mattered. | Prefer crisp selection, a small reveal and precise success/failure explanation over excessive celebration, forced popups or vibrating for everything. | Different genre, outcome measures and context; does not directly establish perfect sound/haptic timing for Grid Hunt. |
| **Game Accessibility Guidelines**, [full list](https://gameaccessibilityguidelines.com/full-list/) | Practitioner guidelines emphasize clear objectives, contextual help, alternatives to precise gestures, adjustable difficulty and accessible touch targets. | Keep target list visible, give undo, make selecting a grid cell comfortable and allow untimed play. | Design recommendations, not empirical Grid Hunt usability results. |
| **De Kegel & Haahr (2020)**, [IEEE puzzle-generation survey](https://www.tara.tcd.ie/items/58270d70-f98a-462e-bdf3-5a21672dd3ba) | Reviews procedural puzzle generation approaches; repeated content and ensuring playable puzzle constraints are recurring issues. | Random objectives alone can add variety; they must still be valid, unambiguous and worth solving. | Detailed generator methodology belongs to **R07**, not this R02 study. |
| **Jonathan Blow interview (NYU Game Center)**, [designer perspective](https://gamecenter.nyu.edu/game-designers-in-detail-jonathan-blow/) | Argues a puzzle should have a clear purpose and reward proportional to the effort demanded. | A square should not require six taps and a lengthy confirmation sequence. The satisfaction should come from a successful discovery. | Practitioner opinion, not a controlled experiment. |

**Evidence synthesis:** the safest product bet is **easy entry + meaningful discovery + controlled novelty + explicit progress + player choice**. No academic source in this review tests this exact bundle in a map-grid puzzle, so all experience recommendations are hypotheses to prototype and observe.

## 3. What makes this a puzzle instead of a worksheet?

**A weak loop:** display `2437` -> tap square -> "correct" -> repeat 10 times. It may rehearse basic lookup, but risks feeling mechanical once the rule is learned.

**A stronger loop (HYPOTHESIS):**
1. **Purpose:** "We need to find the missing supplies. Three clues are on this map."
2. **Search:** read a numeric reference (or later a landmark clue) and visually inspect the illustrated map.
3. **Decide:** choose the square; player can preview, reposition or undo before confirming.
4. **Discover:** the game affirms the correct **area** and reveals the landmark/short story detail tied to that region.
5. **Accumulate:** each correct find adds a small piece to the mission (map stamps, a trail, an unfolding clue list), without masking the next objective.
6. **Finish:** a concise coherent result screen shows found locations, assistance used and a **Play Next** button.

**Important:** hiding the answers behind pure guesswork is not puzzle depth. The grid reference must remain the actual basis for solving. Likewise, showing only one campsite and asking players to tap it is object recognition, not grid reading.

## 4. Three candidate loops to compare on the SAME training grid

| Variant | Objective | Reward/feedback | Potential value | Potential failure |
|---|---|---|---|---|
| **A. Classic Find** | Given 3–5 references; tap the correct **whole 1 km square** for each. | Each grid cell is marked, objective crossed off; modest completion reveal. | Closest to Sudoku/Word Search; clean and replayable. | Could be rote lookup once player understands eastings/northings. |
| **B. Discovery Hunt** **(recommended first test)** | Same 3–5 real grid references, but each found square reveals one of several map discoveries (camp, note, cache, bridge). Some are decoys. | Short discovery card/image appears on confirmation; a tiny narrative thread finishes on the last target. | Adds curiosity and a reason to complete the set without extra rules. | Reveals may feel like busywork or slow the pace; players may chase icons and skip grid reading. |
| **C. Mixed Detective** | On one map: locate reference, report grid of a shown landmark, then use one unambiguous clue. | Final "solved expedition" moment. | Tests transfer and varied skills while feeling like one mission. | Too much for Chapter 1; introduced only after Reverse Hunt/Legend rules are learned. |

**Evaluation rule:** compare A vs B **before** investing in C. Use identical grid, coordinates, difficulty, touch controls and art style; vary the discovery wrapper. This isolates whether the wrapper increases desire to replay versus adding friction.

## 5. Difficulty must be skill-based and player-facing

Do **not** define difficulty simply as "more objectives = harder" or "smaller cells = always more fun".

| Design dimension | Relaxed setting | Advanced setting | What we would measure |
|---|---|---|---|
| Reference resolution | 4-digit, 1 km area | 6-digit, 100 m area (once learned) | Wrong sub-square vs wrong easting/northing |
| Target count | 3–4 | 7–10 | Fatigue, elapsed time, repeated-error patterns |
| Map feature density | Sparse with a few distinct landmarks | More useful decoys/terrain symbols | Visual search confusion vs skill difficulty |
| Help | Contextual right/up support | Optional hints only | Can they independently read a *new* reference? |
| Mode mix | Coordinate Hunt only | Coordinate + Reverse + Landmark | Transfer, objective comprehension and frustration |
| Sequencing | One independent objective at a time | Short linked stages | Whether mixed missions feel meaningful vs bureaucratic |

**Research-backed caution:** difficulty is subjective. Early content must make players feel successful without giving away every answer, while optional expert play should offer genuine additional reasoning. Avoid presenting "Easy" as second-class.

## 6. Feedback and reward recommendations — proposals, not implementations

- After a correct tap: clear cell outline + small check + short optional haptic/audio. The map does not jump, flash repeatedly or hide the next task.
- After a wrong tap: show selected cell and a helpful optional hint (for example, "you reached the right easting; check the northing"), with no punitive forced failure screen.
- The feedback should reveal **why** it was correct or incorrect at the current precision; for 4 digits grade a **1 km cell**, not its center pixel.
- Allow multiple target squares in one mission, with marker corrections; do not introduce accuracy scores finer than the requested reference.
- Show "3/5 locations found", optional assistance status and a simple completion journal. No compulsory timer, three-star pressure or ads between every target in first prototype.
- A tiny story reveal can be tested, but avoid interruptive popups that block the actual map hunt.

## 7. Replayability without new maps every time

This R02 scope does **not** specify a terrain generator. It proposes **experience variety** from:
- Different targets on the same approved map, with a stable deterministic seed for a session.
- Reordered objective lists and changing target *patterns*, not just swapping one number.
- Occasional optional landmark discoveries and short story-light missions.
- Rotating familiar skill types as chapters progress; simpler rounds continue to appear between complex puzzles.
- Later Free Hunt presets so experienced players can choose relaxing or demanding practice.

**Do not randomize answers blindly:** generator validity, duplicate references, unique clues, map feature anchors, and deterministic saves are **R07** and [existing content design](../PUZZLE_AND_CONTENT_SYSTEM.md). R02 only establishes *why* variety might help enjoyment.

## 8. Small comparison playtest: test fun, not just correctness

**Participants:** a handful of fresh nonexpert players, ideally plus a navigation-experienced player as a skill-check. First compare A and B, with order counterbalanced to avoid all players getting the same version first. Use the **same underlying square-selection controls**; swap only the discovery/reward treatment.

**Observe (no verbal coaching):**
- Number of actions from reading objective to confidently selecting a square.
- Correctness, common wrong-square patterns and whether errors reflect misunderstanding vs imprecise touch.
- Pauses after finding each target; whether reveal card is skipped, enjoyed or resented.
- Whether the player voluntarily selects another puzzle when both variants are offered.
- Short neutral questions: "What were you trying to do?", "What did you enjoy/skip?", "What made that easy or difficult?"
- Whether a new puzzle with unfamiliar targets is still understandable unaided.

**Criteria (proposal, not measured):**
- At least 4/5 first-time players can identify the core objective and complete a short hunt using only in-game help.
- Most players don't experience an irritating extra confirmation step per target.
- The preferred variant produces observable enthusiasm/voluntary "one more" behavior **without decreasing use of actual coordinate-reading logic**.
- A navigation-experienced tester confirms that game feedback teaches correct easting/northing order and 1 km square semantics.

**Do not claim** the game improves real-world navigation proficiency or memory retention from these short tests; separately investigate those at R18.

## 9. Risks / explicit design exclusions for this study

- **Too educational:** lecture cards and military terminology before the first successful hunt.
- **Too shallow:** one grid square appears obvious from the largest icon, requiring no coordinate reading.
- **Too much ceremony:** multiple submit/identify/dropdown steps for every trivial reference.
- **Too much randomness:** targets without clues or maps without believable placements.
- **Confused difficulty:** smaller touch targets mistaken for higher navigation skill.
- **Unfair grading:** 4-digit or 6-digit references treated as single exact pin locations instead of containing areas.
- **Overgamification:** streak pressure, paid skips, mandatory countdowns or excessive collectible distractions before the core is fun.

## 10. Decision and next work

**R02 finding:** **test** a clean Coordinate Hunt against a slightly more evocative Discovery Hunt, holding grid/math and controls constant; retain what increases satisfying discovery **without** distracting from actual grid reading. Research supports ease, novelty, understandable feedback and player agency as relevant considerations, but does **not** settle our exact loop.

**R02 status:** *research documented; playable prototype and player validation open*. Nothing in this report is marked implemented or user-approved.

**Next research item:** **R03 — How to present 4-digit grid references/eastings/northings intuitively and accurately without overwhelming casual players?** R04 (map art treatments) and R07 (generator) come later.