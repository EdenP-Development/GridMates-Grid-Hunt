# GM03 — What Makes a Good Grid Hunt Puzzle? Fairness, Deduction and Difficulty

**Researched:** 2026-11-21  
**Status:** RESEARCH DOCUMENTED / QUALITY GATES PROPOSED. **No game code, generator, real puzzle pack, solver, APK, difficulty calibration, or human playtest was built/run.**  
**Scope:** information sufficiency, intended solution, anti-guesswork, meaningful distractors, challenge pacing and puzzle-quality assurance. **Not** rewards/scoring (GM04), retention sequence (GM05), map art (R04), or full seed-driven generation (R07).  
**Follows:** [GM01 core gameplay](GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md) and [GM02 distinct objectives](GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md). [Mechanics track](GAMEPLAY_MECHANICS_RESEARCH_TRACK.md).

## 1. Executive finding: Correct is not enough

A grid-puzzle generator may produce coordinates with provably correct answers while generating **boring, unfair or misleading** puzzles. We should gate authored/seeded objectives with **hard validity checks** and review their **soft quality** (interesting reasoning, clarity, novelty and aesthetics) independently.

**A good Grid Hunt puzzle** has a clearly communicated goal, all needed information on the map or in accessible hints/legend, an answer rule whose correct result can be justified, enough choice to feel like a puzzle rather than a disguised click test, and feedback that supports another attempt. It must genuinely practice the claimed map skill, not motor precision or icon guessing.

**Important distinction:** a pure 4-digit `2437` lookup can be an excellent **early practice task** without requiring a complicated logical inference. Don't artificially add riddles to every novice map square. We need engaging **round composition**, skill rotation and small discoveries too.

## 2. External sources — evidence and caveats

| Source | Actual claim supported | What we can adapt | Limitation |
|---|---|---|---|
| [De Kegel & Haahr (2020), *Procedural Puzzle Generation: A Survey*, IEEE Transactions on Games 12(1), 21–40](https://www.scss.tcd.ie/mads.haahr/papers/de-kegel-2020-transactions.pdf) | Surveys procedural puzzle approaches; highlights challenges of gradual concept introduction, assessing difficulty, generating varied/nonrepetitive output and aesthetics; puzzle quality metrics depend on type. | Treat quality as **multiple criteria**, not solely "has one answer." Use reject/regenerate and human review of candidate levels; scope an initial narrow generator. | Survey is not specifically about map-grid games; no automatic formula for "fun." |
| [O'Sullivan & Horan (2007), *Generating and Solving Logic Puzzles Through Constraint Satisfaction*, AAAI](https://mlanthology.org/aaai/2007/oapossullivan2007aaai-generating/) | Describes a logic-puzzle generator whose outputs are unique, graded and solvable by inference. | For **deduction-mode** objectives, programmatically test the clue against every eligible feature and require a single admissible answer. Use a human-tractable reasoning path. | Their Jidoku constraints are not our map/landmark grammar; a unique answer alone doesn't prove enjoyment. |
| [Fernández-Vara, summarized by Leigh Alexander (2010), *Making Puzzles and Writing Work Together*, Game Developer](https://www.gamedeveloper.com/design/gdc-online-making-puzzles-and-writing-work-together) | Emphasizes the player-designer fairness contract, accessible clue information, puzzles connected to player insight and story, and avoiding hidden one-off clues or "pixel hunting." | Keep all necessary clues reviewable on the map/mission card; let success reveal a relevant discovery rather than repetitive ceremony. | A reported design talk, **practitioner guidance**, not an experiment with Grid Hunt. |
| [Alice May Lynch (2024), *Effective Player Guidance in Logic Puzzles*, St Andrews PhD](https://research-repository.st-andrews.ac.uk/handle/10023/29069) | In studied progressive pen-and-paper puzzles, hints that guide toward **easier deductions** rather than directly filling the next cell were reported comparably helpful and more positively experienced. | Offer staged hints for mistaken easting/northing, relevant map features and possible reasoning next steps; don't reveal the answer immediately. | Sudoku/other logic-puzzle research, not proven optimal for spatial maps; thesis results should not be generalized into military proficiency claims. |
| [Kristensen et al. (2024), *Difficulty Modelling in Mobile Puzzle Games*](https://onlinelibrary.wiley.com/doi/full/10.1155/2024/5592373) | Investigates predicting perceived difficulty using puzzle characteristics and player/simulation data; difficulty varies across players/cohorts. | Use several explicit difficulty dimensions and actual novice/expert playtest results; resist a single pseudo-scientific "difficulty score." | Commercial puzzle game context, not Grid Hunt. |
| [Shyne, Facey & Cooper (2024), *Procedurally Puzzling: On Algorithmic Difficulty and Player Experience in QD-Generated Logic Grid Puzzles*, AIIDE](https://ojs.aaai.org/index.php/AIIDE/article/download/31873/34040/35942) | Studies links between algorithmic solver difficulty and players' experience; shows measuring human challenge is itself a research problem. | A logical solver is a **validity gate** and one difficulty signal, not a substitute for observing humans. | Different logic grid domain, method and sample; do not claim their results directly calibrate grid-reference games. |
| [FGDC — How to Read a USNG Spatial Address](https://www.fgdc.gov/usng/how-to-read-usng) and our [accuracy contract](../MAP_READING_ACCURACY.md) | Right/easting first, up/northing second; local four digits define 1 km square and six digits 100 m square in appropriate grid context. | The core numeric lookup puzzle must check **areas** using genuine map lines and leading zeroes. | Our fictional local training grid is **not a complete globally georeferenced MGRS location**. |

**Do not copy** copyrighted game-design publications, source PDF diagrams or licensed map imagery into the public game. Cite concepts, implement original puzzles and assets.

## 3. Two levels of quality checking

### Tier A — HARD gates: automatic reject if violated

1. **Correct answer exists** in the selected map extent at required precision.
2. **Answer cardinality matches question contract.** A "Which bridge?" puzzle expects **exactly one** qualifying bridge; "Select all ponds" is explicitly multi-answer and must state its completion rule. Single-reference numeric hunts have one intended cell.
3. **All clue predicates are grounded** in authoritative map geometry, tags, symbol legend or stated conditions. No guessable hidden property like "the safest bridge" without a rule for safety.
4. **Accessible information:** relevant map layer, labels, clue and legend are available on screen or through a discoverable action; no missing assets/locked help required.
5. **Grid semantics:** E first, N second, [SW inclusive, NE exclusive) area bins, exact leading zeroes, proper 1 km/100 m ranges. A reskinned asset never changes truth.
6. **No paradox or impossible combination:** a landmark isn't both required to be east and west of the same strict boundary; a bridge relation aligns with authored stream/path geometry.
7. **No answer leak that defeats intended skill:** a huge pre-existing treasure icon doesn't mark the requested numeric grid before decoding.
8. **No duplicate objective/near-identical answer without deliberate author approval:** a seeded 5-task set shouldn't ask for the exact same 1 km cell five times under nearly identical wording.
9. **Valid prerequisite chain:** every needed earlier objective can be completed without access to information only available later; no circular deadlock.
10. **Deterministic replay and content revision:** planned seeded output must yield the same objective IDs/answers for the same map+generator version+seed; validate stored instances.
11. **Readable interaction:** the actual mobile input can express the required answer; a 100 m cell cannot rely on a 2px touch target.
12. **Original/cleared assets and terminology:** no unlawful published map reproductions; no false claim of modern official MGRS/military map accuracy.

**Implementation philosophy:** not every gate belongs in the first JVM library. Start with coordinate and static content validators; later add geometry-predicate and generator-level checks as those modes ship.

### Tier B — SOFT quality traits: reviewed, improved, not claimed objectively solved

- **Clarity:** the player can explain the requested action before tapping.
- **Meaningful reasoning:** success depends on the intended grid, legend, terrain or relationship skill.
- **Distraction quality:** multiple believable alternatives matter, but don't make the solution subjective.
- **Novelty:** successive puzzles change objective type, relation, location pattern, or map context, not only the numeral.
- **Pacing:** a short hunt balances easy wins with one slightly thoughtful decision, with no forced long animation.
- **Feedback:** the player sees why a submitted answer is wrong and can recover independently.
- **Visual readability:** important map clues are not buried under decoration, poor contrast or overlapping labels.
- **Story payoff:** discoveries reinforce the objective rather than taking over the game.
- **Player fit:** novice and experienced navigation readers may prefer different levels of explanation and pace.

**Do not sum these into a fake universal 0–100 fun score.** Use an explicit qualitative checklist, objective-specific tests, and human feedback.

## 4. Difference between a meaningful distractor and an unfair trick

A **good distractor** matches some of the clue but fails one clearly interpretable constraint: another bridge crossing a trail **west** of the lake, or another road bridge **east** of the lake when the clue specifies a *footpath*. The player must inspect map relationships to decide.

An **unfair distractor** relies on visual coincidence, an unlabeled symbol category, near-invisible art, or an arbitrary subjective distinction that players cannot determine from map information. Making the icon smaller is not making the puzzle deeper.

**Worked example (illustrative, not a generated/verified world geometry file):**

> "Find the **footbridge east of the lake where a trail crosses the stream**."

Assume the lake's eastmost boundary is E=26,700m:
- **B1** point E=27,400 N=39,200, a **footbridge**, registered as crossing trail+stream. **Qualifies**.
- **B2** point E=24,900 N=39,400, also a **footbridge** crossing trail+stream, but west of lake boundary. **Fails**.
- **B3** point E=27,600 N=37,600, registered as a road bridge crossing road+stream but no trail. **Fails**.

**Predicate contract:** `kind == footbridge AND E > max(lake.polygon.easting) AND crosses(trail) AND crosses(stream)`. Exactly **one** eligible ID: B1. The map should visually corroborate these declared relationships. If an additional footbridge also satisfies this condition, the puzzle is **rejected or its clue is revised**. "East of the lake" here is explicitly strict east of its entire mapped extent, not a vague centroid.

For a relaxed beginner Landmark Hunt, start with two easy-to-distinguish choices and one clear constraint; add second constraint when players already know map symbols.

## 5. Difficulty is multidimensional

| Dimension | Early | Intermediate | Later |
|---|---|---|---|
| **Task rule** | Given 4-digit → select 1 km square | Reverse-read a visible point / legend identify | Multi-constraint Landmark or Mixed chain |
| **Precision** | 4 digits (1 km area) | 4 digits with less help | 6 digits (100 m area), magnifier |
| **Candidate count** | 2–4 meaningful candidates | 4–8 | Several plausible feature matches |
| **Relations** | No spatial deduction or one obvious cue | One geometric relation | Two independent relations or short clue chain |
| **Assistance** | Visible right/up guide | Optional contextual hint | Optional hint after independent attempt |
| **Map density** | Sparse readable map | More scenery without occlusion | More symbols/contours, all legible |
| **Target count** | 3 | 4–6 | Flexible 3–10 without compulsory long sessions |
| **Novelty** | One known skill at a time | Old skill + one twist | Mixed tasks plus periodic calm familiar hunts |

**Do not** advance every row at the same time. If six-digit plotting is new, keep the map uncluttered and the objective count short. A harder puzzle may be 4-digit with meaningful landmark relationships rather than a microscopic 6-digit finger challenge. Map theme aesthetics shouldn't affect canonical answer difficulty.

## 6. Quality templates for individual task families

### Coordinate Hunt (given numeric reference → cell)
**Unique semantic answer:** one cell at requested precision. **Reasoning skill:** eastings then northings. **Quality comes mostly from** a gentle progression, visible axes, occasional leading zero, nonrepetitive target distribution and satisfying multi-objective mission flow. Do not invent artificial logic for the simplest square hunt.

### Reverse Hunt (feature → numeric reference)
**Unique semantic answer:** the cell containing a **declared point**. **Reasoning skill:** read southwest grid boundaries and format digits accurately. **Risks:** a sprawling lake or forest does not automatically have one reference, a sprite spilling over a grid boundary, leading zeroes stripped by number inputs. Specify "marked entrance" or another point.

### Landmark Hunt (spatial description → feature)
**Unique semantic answer:** one feature ID for a single-choice clue. **Reasoning:** recognize symbols and apply spatial relations to a map. **Risks:** two valid candidates, subjective labels ("most sheltered"), decorations disguising symbol facts. Use explicit relation-predicate checking plus a human readability pass.

### Precision Hunt (6 digits → 100 m cell)
**Unique semantic answer:** 100 m cell. **Reasoning:** first locate parent kilometre square then interpolate tenths. **Risks:** wrong E/N half order; incorrectly rounded coordinates; microscopic touch; missing magnifier. A six-digit test should not require one pixel.

### Mixed Hunt (family of prior skills)
**Unique or explicitly multi-answer by each subtask's contract.** **Reasoning:** apply learned verbs with a genuine new dependency when appropriate. **Risks:** arbitrary story gating or a chain whose first target doesn't provide essential information. Every subtask has to be valid on its own and dependency graph acyclic.

## 7. A human-friendly authoring and generation quality pipeline (PROPOSED)

```text
Select map and unlocked skill(s)
   ↓
Choose an authored puzzle template / eligible objective type
   ↓
Bind canonical points, cells and feature relations
   ↓
Compute accepted semantic answers from authoritative map data
   ↓
Run hard validity/uniqueness/accessibility/chain checks
   ├─ invalid → revise candidate or reject and choose another
   ↓
Evaluate soft quality and novelty against current mission
   ↓
Publish a human-reviewed chapter puzzle OR cache valid seeded Free Hunt instance
   ↓
Playtest with players and revise difficulty labels/wording
```

**No general-purpose SAT solver is required for the first version.** One tiny complete scan of eligible features can prove uniqueness for the early relation grammar. Only introduce a more sophisticated constraint solver if real mission structure requires it.

## 8. Example of authored quality assertions (test IDs, NONE EXECUTED)

| Test | Scenario | Required behavior |
|---|---|---|
| Q01 | `2437` on a 6×6 map containing that complete cell | Exactly one correct 1 km area |
| Q02 | `2437` outside map bounds or partially clipped impossible target | Reject or redesign map viewport/task |
| Q03 | Two bridges satisfy same "footbridge east of lake" clue | Reject ambiguous single-choice task |
| Q04 | No footbridges satisfy relation | Reject impossible clue |
| Q05 | B1/B2/B3 example matches exactly one candidate | B1 accepted; B2/B3 distractors |
| Q06 | Sprite reskin changes size/alpha | Same point, target answer and relation truth |
| Q07 | Reverse Hunt landmark anchor lies exactly on grid boundary | Deterministic containing-area rule |
| Q08 | Six-digit `243378` | Correct parent `2437` and subcell easting tenth 3/northing tenth 8 |
| Q09 | Numeric `0417` supplied as string | Leading 0 retained |
| Q10 | One of two clue terms refers to hidden, unavailable map layer | Reject or make information available |
| Q11 | First stage depends on later locked clue | Reject circular dependency |
| Q12 | A seed draws duplicate references / same feature objective | Reject or resample per declared replay constraints |
| Q13 | Huge treasure icon indicates numeric answer before decoding | Flag answer-leak visual QA |
| Q14 | Grid labels obscured by generated background | Flag readability visual QA |
| Q15 | Correct answer requires sub-48dp precision tap when at default zoom | Offer tested magnifier/alternate path |
| Q16 | New puzzle differs only in random seed but identical reasoning pattern repeatedly | Flag for novelty/pacing review, not automatic invalidation |
| Q17 | Multi-answer task explicitly asks "find all" | Accept exactly stated set; do not force unique singleton |
| Q18 | Undo, wrong submission, hint request | No stale/changed answer; player can recover |
| Q19 | Same map+generator revision+seed | Same valid objective sequence once R07 implemented |
| Q20 | Experienced map reader and novice disagree on which clue is sufficient | Capture observation and revise wording/visible legend; not proof the data is invalid |

**Evidence discipline:** tests are not green until actually implemented and executed. Performance, success rates and player enjoyment must not be fabricated.

## 9. Playtesting focused on *why* puzzles work (NOT RUN)

Create a small authored set **before** the random generator:
- 3 simple coordinate lookups on sparse map,
- 2 Reverse Hunt exercises (one with meaningful leading zero),
- 3 Landmark Hunt tasks: one easy single relation, one controlled two-relation puzzle, one **intentionally ambiguous** task for evaluator rejection,
- 2 Precision tasks with an accessible 10×10 picker,
- 1 short Mixed task combining two previously introduced operations.

Ask testers: "What made you choose that?" and "Was there another feature that also fit?" Observe whether they *use* the grid/reference/legend or identify targets by decorative giveaways, whether hints preserve a sense of discovery, and how they recover from errors. Contrast with authors' expected reasoning paths.

Record time/hints/taps as secondary information only; **interpret with error type and explanation**. Someone slow because labels are unreadable isn't necessarily having a harder intellectually interesting puzzle.

Suggested outcome gates to revise after actual play: every published task has unambiguous rule and solver-backed answer; independent player can explain what the clue means; most novice players can complete early tasks using built-in help without external instruction; the higher challenge comes from one additional valid reasoning operation rather than cramped controls.

## 10. Outcome and next focused research

**GM03 recommendation:** write a **quality-lint checklist** into the eventual mission authoring system, with hard semantic correctness gates and softer player-experience checks. Use small handcrafted levels to define and playtest what "a good Grid Hunt puzzle" means **before** R07 generates hundreds of mathematically valid but uninteresting instances.

**No mode or engine is approved or built.** First-puzzle interaction and custom generator options remain experiments.

**Next study:** GM04 — mistakes, feedback, hint costs, rewards, optional mastery and calm completion. R07 seed generation remains postponed until the mechanics and quality grammar are agreed/tested, not cancelled.