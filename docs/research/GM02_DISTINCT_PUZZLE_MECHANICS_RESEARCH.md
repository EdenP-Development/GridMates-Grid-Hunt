# GM02 — Distinct Puzzle Mechanics: Five Hunts, Different Thinking

**Research date:** 2026-11-21  
**State:** Evidence-backed design research and proposed experiments, **not approved mechanics, implemented code, an APK, or completed playtests**.  
**Scope:** player objective, verb, decision, answer type, exact correctness rule, failure recovery, and mode differentiation. **Out of scope:** scoring/economy (GM04), skill scheduling (GM05), full procedural generation (R07), map rendering (R04–R06).  
**Related:** [GM01 core loop](GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md) and [game-mechanics research track](GAMEPLAY_MECHANICS_RESEARCH_TRACK.md).

## 1. Decision-relevant finding

**Different hunt names do not automatically mean different games.** The proposed modes should differ in what the player reasons about or produces, not merely which words appear on the objective card:

- **Coordinate Hunt:** *given reference → select map area* (read the grid).
- **Reverse Hunt:** *given marked feature/point → produce reference* (encode a location).
- **Landmark Hunt:** *given descriptive evidence → identify a unique feature* (map interpretation).
- **Precision Hunt:** *given detailed reference → first select kilometre cell, then its 100 m subdivision* (interpolation). This is a precision layer on Coordinate Hunt unless the second-step plotting interaction adds meaningful reasoning.
- **Mixed Hunt:** *combine already learned task types*, possibly with a genuine information-dependent clue chain. Mixed is a **format**, not an independent fundamental verb.

**Recommendation to prototype, not final approval:** establish Coordinate + Reverse as truly distinct baseline mechanics, then trial Landmark selection; give Precision a 10×10 plotting tool; make Mixed a short later-stage combination, not the beginner's entry screen.

## 2. Research sources, exact contributions and limits

| Source | Actual contribution to this design question | Important limitation |
|---|---|---|
| Clara Fernández-Vara, *Puzzle Writing: Best Practices*, GDC session overview | A puzzle must present enough information to solve it while retaining a challenge; integrate the puzzle into the game world. | Public abstract, **not** a detailed transcript or experimental result. https://www.gdcvault.com/play/1013851/puzzle-writing-best |
| Randy Smith, *Helping Your Players Feel Smart: Puzzles as User Interface*, summarized in Game Developer (2013) | User-centered puzzles use visibility, mapping, visual language, feedback, affordances, conceptual models and help on demand. | Practitioner talk summary, not proof of a particular UI choice. https://www.gamedeveloper.com/design/video-designing-puzzles-that-make-players-feel-smart |
| Damien Allan, *Designing Video Game Puzzles* (2017) | Strong simple core mechanics can be explored, reversed and combined; trial-and-error configurations without informative clues risk weak puzzles. | Practitioner experience, not universal law. https://www.gamedeveloper.com/design/designing-video-game-puzzles |
| Shokeen et al. (2020), *Designing Failure and Feedback within Puzzles*, CHI PLAY extended abstract | Their case-based preliminary findings distinguish feedback supporting productive frustration from confusing feedback that inhibits play and learning. | Work-in-progress, particular cybersecurity educational game; **not** a general causal guarantee. https://doi.org/10.1145/3383668.3419901 |
| Sweetser & Wyeth (2005), *GameFlow* | Clear goals, appropriate challenge, player control and feedback are useful heuristic dimensions. | Originally examined in a different game context. https://doi.org/10.1145/1077246.1077253 |
| Kristensen et al. (2024), *Difficulty Modelling in Mobile Puzzle Games* | Perceived difficulty depends on players, not only a level's static properties; actions, mistakes and completion time are useful but incomplete indicators. | Results from a specific commercial puzzle-game context, not Grid Hunt. https://onlinelibrary.wiley.com/doi/full/10.1155/2024/5592373 |
| **User-provided** *Military Map Reading*, Royal School of Military Survey / MAPRIC, v2.0, Dec 2010 | pp. 3–5: eastings then northings, four- and six-figure references, leading zero and reference context; p. 5: conventional signs are symbolic; pp. 11–14: contour patterns; pp. 24–25: matching map and ground features. | British military revision booklet, **not** a current universal U.S. Army doctrine source. Military/GPS and regional claims require separate modern verification. **Do not copy the illustrated mapping into the public game/repository**: Ordnance Survey materials are independently restricted (p. 41), and the booklet is copyrighted. Cite ideas, create original graphics. |

**Interpretation:** These sources inform **design criteria** and authentic map-reading tasks, but none directly tests the five Grid Hunt modes. Every mode below remains a hypothesis until people play it.

## 3. Common rules shared across modes

- The **map data** owns world coordinates, gridded cell boundaries, feature IDs and geometry; art has no authority over correct answers.
- Define a **declared reference context** for all fictional grids; never imply that a bare 4- or 6-digit example is a complete global MGRS coordinate.
- North-up for first prototypes; eastings increase right, northings up. Four digits identify a **whole 1 km square** by southwest corner; six digits identify a **100 m square** within the appropriate larger grid.
- Coordinates are strings where necessary: **0417** stays four characters, never becomes 417. The source manual explicitly warns about this on p. 4.
- For an ambiguous location exactly on a line, define **half-open** assignment [minimum,maximum), so the same location cannot count in adjacent cells. Explain map-scale limits rather than promising satellite-grade precision.
- A feature has an explicit canonical point or specified geometry; selecting a forest polygon cannot silently be graded as selecting its icon's center.
- Selection before submission is freely adjustable. A wrong submission supplies task-specific feedback, contextual help, and a retry. Never create a faster-finger requirement to simulate difficulty.
- Single-player offline operation; no GPS, real military coordinates, modern intelligence maps or network dependencies required.

## 4. Mode contract: exact decisions and validators

| Mode / purpose | Player sees | Player must decide / do | Response / validator | Beginner or later? |
|---|---|---|---|---|
| **A. Coordinate Hunt** — decode reference | “Find 2437”, visible grid labels | Find vertical line 24, horizontal line 37; tap **square northeast** of intersection | One 1 km cell ID; exact world-derived easting/northing bin | **First** |
| **B. Reverse Hunt** — encode location | Visibly marked canonical map point or distinguishable landmark | Read line values from that feature to form numeric reference | Four fixed digits; syntactic checks + equality to canonical 1 km cell; 6-digit variant only after precision taught | **Early** |
| **C. Landmark Hunt** — interpret spatial clues | “Find the bridge where the path crosses the stream”; multiple plausible map features, optional legend | Identify the **right feature** from symbols and relationships, not type its coordinates | Feature ID selected; cross-check type, relations and location from authored valid instance | **After basic grid literacy** |
| **D. Precision Hunt** — interpolate | “Find 243378”; 1 km square plus a precision tool | Find 24/37 then choose 3 east and 8 north tenths inside that square | Exact 100 m cell ID (E 24300–24400, N 37800–37900 in fictional meter frame); never pixel-distance grading | **Later** |
| **E. Mixed Hunt** — combine learned verbs | 2–4 short independent or actually linked objective cards | Switch appropriately between find, read, interpret, and plot | **Typed** per-objective validators; success if required subtasks pass | **Later / Custom** |

**Caution on B vs C:** If “Landmark Hunt” only shows a cabin and asks “What are its coordinates?”, it is just **Reverse Hunt** with a cabin picture. To be distinct, Landmark Hunt must involve **identification using a map legend or meaningful spatial evidence** and select a feature rather than return a number.

**Caution on A vs D:** If six digits only shrink a tap target, it is **Coordinate Hunt at higher difficulty**, not a new compelling mode. D earns its own name only if choosing a subcell is readable and enjoyable with a proper expanded plotting instrument.

**Caution on E:** Displaying Coordinate, Reverse and Landmark cards sequentially is a **playlist**; dependent clues that change available information are a genuinely different dynamic, but also risk length and fragility. Don't force dependency in all Mixed hunts.

## 5. Tiny authored examples (fictional training grid)

All examples below assume a clearly stated local fictional meter frame, grid lines, geometry and answer IDs. These are *test cases*, **not complete MGRS addresses**.

**A — “Find the Lost Parcel.”** Four-digit clue **2437**. The successful selection is the 1 km square E [24000,25000), N [37000,38000), independent of where inside it the finger lands. Three visible candidate squares should look equally plausible; no treasure graphic marks the correct one before solving. The answer is a **cell**.

**B — “Where's the Lookout?”** The lookout's canonical point is E=24,375 m, N=37,860 m; the response is **2437**, optionally typed via two two-digit fields. The six-digit extension after introduction is **243378**. An enlarged lookout drawing may overlap another cell but must not change its reference. The answer is a **numeric reference**, not a map tap.

**C — “Which Crossing?”** The clue: “Find the bridge where the footpath crosses the stream **east of the bend**.” The map contains two bridges; both look like bridges, but only one fits the spatial relationship. The player taps the correct **feature**. The game checks a feature ID, not a coordinate string. If more than one feature meets the clue, **reject the puzzle before publishing**.

**D — “Look Closer.”** Six-digit clue **243378**. Step 1 chooses four-digit parent cell **2437**. Step 2 opens a clear 10×10 subgrid, selecting the cell between easting tenth 3–4 and northing tenth 8–9. The player may preview/correct before confirmation. This is *not* an exact point or a real-world ten-meter claim.

**E — “The Trail Note.”** Three cards: (1) find **2437**, (2) identify which of two crossings meets a clue at the marked area, (3) read a marked lookout's four-digit reference. Independent variant: all available, any order. Linked variant: only the first provides an **essential** clue for the second; if it doesn't, do not call the order a puzzle mechanic.

## 6. State/answer model proposal (not code)

**Task kind:** COORDINATE_CELL4 / REVERSE_REF4 / FEATURE_IDENTIFY / COORDINATE_CELL6 / COMPOSITE.  
**Shared fields:** taskId, mapId, mapRevision, taskKind, clueId/text, eligibility/unlocks, acceptedAnswer, hintTiers, validationVersion.  
**Outputs:** cell1kmId, normalizedFourDigitString, featureId, cell100mId, or individual typed subtask outcomes.  
**Optional:** sourceFeatureId; canonicalAnchorId; relationConstraints; precisionM; chosenObjectives; seed and generatorVersion when generation eventually exists.

Rules:
1. The **expected answer is derived at authoring / generation time** from canonical map data and validated again when loading.
2. Correct answers compare **semantic IDs / grid cells**, not center-pixel proximity.
3. Input syntax checks remain separate from spatial correctness (e.g., wrong number of digits vs correct format but wrong square).
4. A Mixed objective delegates to the original task validator; it must not silently redefine correctness.
5. The compiler/validator rejects zero-valid-answer and multiple-valid-answer clues, especially relational Landmark hunts.
6. Repeated attempts do not change answer truth; camera state is not part of validation.
7. Intro tasks have the **minimum visible operations** required for genuine reasoning; optional hint layers reveal one helpful step at a time.

## 7. Puzzle-design pitfalls and how to catch them

| Failure mode | Why it weakens the game | Gate |
|---|---|---|
| Coordinates merely hide under shiny objects | Players can guess visually without grid reading | Remove or neutralize giveaway artwork until after success |
| Multiple landmarks satisfy one description | Puzzle becomes trial and error | Automated relational uniqueness check + human inspection |
| Correct reference truncated (e.g. 0417 → 417) | Teaches wrong habits | Round-trip formatting and leading-zero test |
| 100 m target tiny on phone | Tests motor precision, not mapping | Magnifier, 10×10 selector, alternate input path |
| Reverse Hunt presented as ordinary Find & Mark | Mode distinction is superficial | Enforce a different answer type and new reasoning step |
| “Mixed” only means more tasks or an arbitrary story delay | More work but no more insight | Test whether the sequence produces a new logical decision |
| Wrong response says only “Nope” | Poor information for recovery | Distinguish wrong line / wrong precision / wrong feature with optional hint |
| New rules arrive in a giant tutorial | Loss of casual-puzzle appeal | One changed task operation at a time, contextual guidance |
| Long compulsory discovery animation | Interrupts core puzzle loop | Nonblocking payoff and immediate next-hunt control |

## 8. Controlled playtest plan — **not run**

**Build only 12 authored test puzzles using the same small woodland map** to hold art and geometry constant: 3 Coordinate, 3 Reverse, 3 Landmark, 2 Precision and 1 short Mixed. Use 4-digit references before 6-digit, no timers/lives, no procedural landscape generation. Pilot with beginners and people with land-nav experience; observe behavior, do not assume veterans will enjoy it automatically.

Observe for each task:
- Can a first-time player **say what is being asked** before acting?
- Did the player use eastings/northings, a symbol/legend, spatial relationships or subdivision logic as intended?
- How many taps/edits and wrong committed answers occurred? Was confusion conceptual or touch-related?
- What help was requested? Could the player recover and explain the correction?
- Does the player notice a real difference between A, B and C **without reading the mode description**?
- Is the Precision tool enjoyable or just a fiddly duplicate of Coordinate Hunt?
- Does the player choose another round voluntarily? Which variants feel satisfying rather than workbook-like?

Use a neutral interview: “What information did you use?”, “Which task felt different?”, “What made this confusing?”, “Which one would you play again?” Compare authored tasks first; do not use guessed difficulty scores or fabricated success rates.

**Acceptance is provisional:** baseline A/B/C each has a clearly distinct verb and answer representation, every task is solvable with available information, semantic validation works, failed attempts are recoverable, and players understand the goal. If a supposed mode feels indistinguishable, **merge it as a variant** rather than maintaining a fake extra game mode.

## 9. Research recommendation and next question

- **Keep:** a small common map interface and a limited family of well-defined task types.
- **Prototype first:** A Coordinate Hunt and B Reverse Hunt; then C Landmark Hunt with a genuine spatial deduction.
- **Prototype as a variant:** D Precision once grid subdivision and touch controls are introduced.
- **Package later:** E Mixed Hunt as a short custom/chapter playlist; investigate dependency-based clues only if they add reasoning.
- **Avoid now:** fully simulated navigation routes, dynamic real-world maps, competitive penalties, exhaustive map symbol encyclopedias, and generic procedural level generation.

**Next focused research: GM03 — What makes a good puzzle?** Test clue sufficiency, uniqueness, distractor selection, interesting deductions, anti-guesswork and difficulty dimensions before creating a generator. Research is not implementation; no APK, game mode, validated player results or signed-off features are asserted here.
