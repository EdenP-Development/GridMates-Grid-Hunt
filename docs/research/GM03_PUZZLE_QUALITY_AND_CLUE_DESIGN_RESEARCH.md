# GM03 — Good Puzzle Design: Fair Clues, Real Deduction, and Quality Gates

**Research date:** November 21, 2026  
**Status:** SOURCE-BASED RESEARCH AND PROPOSED DESIGN RULES; **NO PLAYABLE PUZZLE, AUTOMATED VALIDATOR, APK, OR PLAYER STUDY IMPLEMENTED OR RUN**.  
**Question:** How can Grid Hunt produce puzzles that are **fair, interesting, unambiguous and skill-relevant**, without becoming a worksheet or making the map unnecessarily complicated?  
**Scope:** clue sufficiency, deduction, valid alternatives, distractors, difficulty dimensions, hints and pre-publication quality. **Outside this study:** exact reward economy (GM04), chapter learning cadence (GM05), code for full generator (R07), map art pipeline (R04), touch (R06).  
**Prerequisite:** [GM01 core rules](GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md), [GM02 objective families](GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md).

## 1. Conclusion

**A good beginner hunt may be a straightforward grid-location exercise**. The first objective is to establish a comprehensible, satisfying loop. It need not force elaborate deduction immediately. As modes develop, better puzzles require **connecting meaningful clues or switching reasoning operations**, not just increasing the number of targets.

For authored and generated puzzles, use two *different* gates:
- **Hard correctness gate (automatic, eventually):** answer exists, clue is sufficient under the map's known rules, accepted answer is well-defined, valid inputs are parseable, assets/feature locations correspond to data, and no unintended alternative answer also qualifies. A numeric 1 km cell contains many physical points but just **one semantic cell answer**; do not confuse this with solution uniqueness.
- **Soft experience gate (human playtest):** comprehensible goal, satisfying reasoning, little accidental giveaway, useful progression, appropriate perceived challenge, pleasing feedback and willingness to try another. Being mechanically valid does **not** prove the puzzle is fun.

A generator should **choose from verified puzzles/geometry** and reject invalid instances. Do not substitute random terrain and AI-written clues for solvability verification.

## 2. Research references: what they support, and what they don't

| Source | Direct evidence / argument | Practical Grid Hunt implication | Limit |
|---|---|---|---|
| [Clara Fernández-Vara, GDC — Puzzle Writing: Best Practices](https://www.gdcvault.com/play/1013851/puzzle-writing-best) (GDC Online 2010 abstract) | Frames each puzzle as a *designer-player contract*: provide all necessary information while maintaining engagement; integrate it into game world. | Every objective needs sufficient, accessible information, not hidden developer assumptions. | The accessible session overview is **not a full transcript** or controlled experiment. |
| [Andrejs Klavins on The Case of the Golden Idol (Game Developer, 2022)](https://www.gamedeveloper.com/design/case-of-the-golden-idol) | Designer describes cases being either too obvious or too confusing; specific partial feedback, reducing unnecessary information, and many external playtests improved the design. | Provide **progressive, actionable feedback** and test clues with people who don't know the intended solution. | Retrospective developer interview, not a randomized experiment; a murder mystery is not a four-digit coordinate puzzle. |
| [De Kegel & Haahr (2020), Procedural Puzzle Generation: A Survey](https://www.tara.tcd.ie/items/58270d70-f98a-462e-bdf3-5a21672dd3ba) | Peer-reviewed survey of **32 methods** across eleven categories and their generation characteristics; repetition and validity remain design concerns. | Reuse a small, quality-controlled puzzle grammar rather than generating arbitrary text and objects. | Broad procedural-generation survey, not a proof any simple Grid Hunt sampler is interesting. |
| [Smith, Andersen, Mateas & Popović, A Case Study of Expressively Constrainable Level Design Automation Tools for a Puzzle Game](https://grail.cs.washington.edu/wp-content/uploads/2015/08/smith2012acs.pdf) | Constraint-driven Refraction generation can ensure hard requirements such as solvability, appropriate skill requirements and stylistic constraints. | Define machine-checkable predicates and eligibility before releasing a generated hunt. | Refraction is another puzzle system; our rules and quality tests must be designed specifically. |
| [Kristensen et al. (2024), Difficulty Modelling in Mobile Puzzle Games](https://onlinelibrary.wiley.com/doi/full/10.1155/2024/5592373) | Perceived difficulty depends on individual/cohort response; level features alone are incomplete. Gameplay attempts and simulated agents can help estimate it. | Track qualitative novice difficulty before labeling "easy/medium/hard"; separate cognitive challenge from touch friction. | No need for expensive player telemetry, RL agents or personalized AI for a lightweight offline MVP. |
| [Lomas et al. (2017), Is Difficulty Overrated?](https://doi.org/10.1145/3025453.3025638) | Educational-game studies involving >20,000 sessions examined choice, novelty and suspense; easy and moderately novel play can be motivating. | Keep relaxing hunts and modest objective variety in late chapters too. | Different game/task; don't over-generalize exact level settings. |

**Practical judgment, not measured conclusion:** Hard validation plus a small number of human-authored puzzle patterns is a reasonable first approach for our team and offline Android scope.

## 3. Define what the player must *infer*

| Family (GM02) | Given | Needed inference / operation | Valid response | Failure pattern |
|---|---|---|---|---|
| **Coordinate** | `2437` + labeled north-up grid | Read **east first, north second**, identify NE square of boundary crossing | One cell ID at 1 km resolution | Target physically obvious from decoration instead of reading |
| **Reverse** | Clearly designated canonical feature point | Determine enclosing SW gridline coordinates and encode fixed digits | Exact 4-char grid reference, including zeros | Sprite overlaps adjacent cell; point not marked or ambiguous |
| **Landmark** | Legend and one or more spatial predicates | Recognize a symbol, combine relation with other features, choose uniquely qualifying object | Feature ID | Multiple bridges match / clue vocabulary undefined |
| **Precision** | `243378` + zoom/inset | Find parent `2437`, then tenth east=3 and tenth north=8 | One 100 m subcell ID | Tap target tiny; game rounds to closest visual icon |
| **Mixed** | Familiar operations plus actual needed intermediate evidence | Select suitable operation, use found information to constrain next answer | Typed subtask outcomes | Random playlist mislabeled deep deduction; later clue doesn't depend on prior one |

**Caution:** a basic Coordinate Hunt is a valid *starter game action*, even if it has little deduction depth. Our quality problem is to prevent it from being the **only** experience indefinitely.

## 4. Puzzle examples: invalid → repaired

### Case A — The obvious icon (mechanically valid but weak skill exercise)
- Weak: "Find grid 2437" with a bright parcel icon **already sitting in 2437**, while other cells are empty.
- Why weak: the player can succeed without reading `24` or `37`.
- Repair: keep the map decorated similarly across potential cells; reveal the parcel **after** the correct reference is plotted. The grid should be sufficient to identify the cell regardless of symbols.
- Automatic check possibility: tag "reward" art as hidden-until-solved; human visual inspection still needed.

### Case B — The ambiguous bridge (unfair)
- Weak: "Tap the bridge east of the lake" when **two bridges** lie east of it.
- Invalid because: two feature IDs satisfy the stated predicate (assuming "east of lake" has an explicitly defined reference point or boundary).
- Repair: add a learnable and *true* distinguishing clue: "the bridge east of the lake **where the footpath crosses the stream**." **Publish only if exactly one bridge satisfies both rules.**
- Automated check: enumerate all visible/eligible bridge features and evaluate specified relations. Ambiguous, zero-match or contradictory tasks are rejected before reaching the player.
- Important: "east of a large lake" can itself be ambiguous unless "east of" is defined (e.g. east of the polygon's eastmost bound, or east of a marked lake reference dot). Specify which meaning a level uses; don't silently rely on a sprite's art center.

### Case C — An invented six-digit reference (incorrect skill)
- Weak: assign `243378` by approximating where a cabin image appears on a zoomed screenshot.
- Invalid because: the picture may be offset/resized independently of the feature's geographic anchor.
- Repair: read canonical feature **E=24375, N=37860**; compute 1 km cell `2437` and 100 m cell `243378` with floor/truncation; draw icon at the world position.
- Automated check: compute expected reference from source-of-truth coordinates; compare after theme change, zoom and device density.

### Case D — The vague terrain clue (unfair or unteachable)
- Weak: "Go to the steep hill" on a flat cartoon landscape where contour-line spacing and heights aren't defined.
- Repair: use a curated valid contour model and state a clear question, e.g. "Which slope is steepest? Look where contours are closest." Ensure the candidates have distinguishable spacing and known contour interval.
- Do NOT draw arbitrary decorative contour squiggles and declare them geographic answers. Detailed terrain authoring/verification remains R12.

### Case E — The needless chain (busywork)
- Weak: Find square A, then receive an unrelated instruction for B, then an unrelated instruction for C, calling it "three-step detective work."
- Repair: at A reveal a clue that **actually narrows B** (e.g., the landmark type), then B provides the unique relation needed for C. If every task is independently solvable from the start, simply present it as a **Mixed Hunt**, not a chain.
- Human quality gate: the chain produces an intelligible "oh, now I know" moment, not just an artificial unlock timer.

## 5. Define HARD constraints before thinking about randomness

Every `PuzzleInstance` should declare:
```text
mapId + mapRevision + taskId + taskKind + ruleVersion
requiredPlayerSkills[]
clueTemplateId + typedParameters[]
visible/eligibleFeatureIds[] OR eligibleGridCells[]
responseType (gridCell4, gridRef4, featureId, gridCell6, composite)
expectedSemanticAnswer(s) and cardinalityPolicy
requiredRelations[] (typed, e.g. CROSSING(streamId,trailId))
presentationRequirements (grid/legend/contours/zoom)
puzzleConstraints (no giveaway symbol, valid data, disambiguation)
hintTiers[] + feedbackPolicy
generatorVersion + seed (when randomized)
```

**Preflight validation pseudocode (illustrative only):**
```text
candidateAnswers = enumerateEligibleAnswers(map, taskKind, context)
satisfying = candidateAnswers.filter(x => everyTypedConstraint(x, clueData))

if rule is SINGLE_ANSWER:
    require satisfying.size == 1
else:
    require satisfying matches declared multi-answer policy

require all required skills/tools are introduced or supplied
require all coordinate references roundtrip & remain inside map bounds
require all feature positions/relations are valid & label/art aren't misleading
require no answer leaks through hidden-before-solve assets
require saved revision & seed reproduce same answer
publish only if hard constraints pass; otherwise resample or fall back
```

**Important exception:** "Select any of the three connected shoreline cells" can have three *intended* accepted answers; it's fair only if the clue clearly announces that multiple answers are allowed. In a four-digit single-cell task, many physical tap points within one cell are still **one answer**.

**Avoid an overbuilt logic solver:** on a 6×6 map and 5–30 landmarks, enumerate eligible cell/feature IDs and test authored rules. Sophisticated SAT/constraint solvers and GIS databases are not required at first.

## 6. What to measure as difficulty (not a fake universal number)

Separate **reasoning variables** from UI/art burden:

| Axis | Earlier / lower load | Later / higher load | Key protection |
|---|---|---|---|
| Precision | 4-digit km cell | 6-digit 100 m cell | Magnifier/plotting aid; no tiny taps |
| Decision operation | Given reference→cell | Reverse or combine different learned operations | Explain one new verb at a time |
| Relationship complexity | One explicit clue | Two or more *independent, necessary* predicates | Must remain unique and visually legible |
| Distractor relevance | Few, obviously distinct features | Multiple plausible same-category features | Distinguish with meaningful clues, not pixel hunts |
| Map density | Sparse with clean lines | More landmarks, paths, contours | Do not conceal labels or vital geometry |
| Mission length | 3 objectives | 5–8 or linked two/three-step chain | More taps does **not** necessarily mean more intellectual difficulty |
| Help level | In-map across/up cue | Optional user-requested hints | Assistance doesn't redefine answer |
| Familiarity | New verb with worked example | Mixed skills on a new map | Don't assume success on memorized board means transfer |

**No fixed numeric weightings or calibrated difficulties yet.** Player performance, errors, time, and enjoyment must be observed. Distinguish **cognitive difficulty** from **visibility, terminology, or control friction**.

## 7. Soft quality screen (proposed for human reviewers)

Before marking a hand-authored puzzle or new template "ready," answer:
1. Can a person state the objective in one short sentence?
2. Is the required answer inferable from information actually visible, without knowing developer intentions?
3. Is there only one intended semantic answer, or is multi-answer acceptance disclosed?
4. Would solving require the intended map-reading/terrain skill, not only tapping the most obvious image?
5. Does at least one clue *change what the player knows or can decide*?
6. Is the map itself readable at default zoom without intense visual scanning?
7. Can mistakes be corrected, and are hints specific to the failed inference?
8. Does victory visibly advance the game without interrupting it?
9. Would a novice understand the task type after a small example, without a lecture?
10. Is there a reason to play another puzzle of this family besides merely repeating more cells?

**Gate:** #1–4 and #7 are nonnegotiable design review concerns; #5, #8–10 guide how satisfying the level feels. A basic early Coordinate Hunt may deliberately be simple (#5), and that's okay.

## 8. Hint ladder: teach the reasoning, not just reveal the answer

Hints are proposed, not implemented and not yet costed:
- **Tier 0:** no help except visible legend/map lines.
- **Tier 1:** remind the *operation*: "Right across the eastings, then up the northings."
- **Tier 2:** highlight only the relevant component/axis; for a Landmark clue, restate the decisive spatial relation.
- **Tier 3:** demonstrate the method step-by-step with the correct line(s), optionally making the cell obvious as explicit help. Player still completes and can learn.
- Avoid insulting or generic messages ("Wrong!") and avoid random hint selection disconnected from the task the player is solving.

**Test whether strong hints remove all thinking**, rather than declaring any tier inherently optimal. An assisted completion can still count as game completion but should not be misrepresented as independent mastery.

## 9. Test matrix (planned, none executed)

| ID | Fixture | Required outcome |
|---|---|---|
| Q01 | 4-digit `2437` on bounded map | Exactly one **cell ID** qualifies; any coordinate within its area accepted |
| Q02 | 4-digit `0417` | Leading zeroes kept; exact target cell |
| Q03 | Two bridges both east of lake | Landmark objective rejected as ambiguous |
| Q04 | Add footpath-crosses-stream constraint, only one meets it | Exactly one eligible feature ID |
| Q05 | No candidate satisfies all relations | Reject instead of exposing impossible puzzle |
| Q06 | Feature point near a grid boundary, sprite visually crosses it | Correct reference based on point, not sprite |
| Q07 | Random seed chooses 3 duplicate 4-digit references | Reject or resample for no duplicates (unless explicitly designed) |
| Q08 | Six-digit target at E24375,N37860 | `243378` -> one 100 m cell, parent `2437` |
| Q09 | Same instance reloaded after app resume | Identical clue, accepted IDs, attempts and hints |
| Q10 | Generated clue references an unavailable/unintroduced legend symbol | Reject or schedule tutorial before task |
| Q11 | "Find steepest hill" without coherent contour model | Reject as invalid terrain puzzle |
| Q12 | One clue clearly allows several answers | Multi-answer policy matches declared allowed set |
| Q13 | Change map theme and redraw assets | Answer remains identical |
| Q14 | All selected candidates obvious from giveaway art | Human review flags weak skill exercise |
| Q15 | Chain task B solvable without clue from A | Treat as independent Mixed Hunt, not a dependent chain |
| Q16 | Wrong attempt easting correct/northing wrong | Feedback correctly identifies operation (not a random wrong-message) |
| Q17 | 100 m subcell physically too small to tap | Alternate selection tool or zoom required |
| Q18 | Two clue templates differ only in flavor text | Count as same mechanic/logic structure for diversity accounting |
| Q19 | Feature-ID pool contains stale map revision | Reject cross-revision puzzle references |
| Q20 | Batch of many seeded puzzle candidates | Hard constraints always satisfied for published output; rejected seeds logged, no fake "quality scores" |

**Future experiment:** produce small batches from 3 authored templates and evaluate actual player behavior. Compare:
- A simple 4-digit location hunt;
- A landmark hunt with **one** valid spatial relationship;
- A landmark hunt with **two** necessary relationships.
Keep map, control, feedback and art comparable. Observe time spent reasoning, accidental taps, first-clue understanding, actual approach to answers, need for hints, perceived fairness, and "one more puzzle" willingness.

If a new puzzle is hard **only** because the map/controls are confusing, that is a UX defect, not a desired hard level. No research in this document has run or proved that players enjoy these puzzles.

## 10. Decision and next focus

**GM03 finding:** adopt a **two-gate authoring policy**: strict automatic correctness (including semantic answer cardinality and geometry) plus small human puzzle-quality/playtest review. Build first around authored templates and a handful of verified maps. Use constraints to enable future random objective generation, not as a replacement for designing good puzzles.

**Status:** RESEARCHED, NOT IMPLEMENTED. No puzzle QA, creator editor, generator, reward system, human results or Android game is claimed complete.

**Next focused research:** **GM04 — hints, scoring, attempts and rewards**: support enjoyable low-pressure play without punishing learners, while letting enthusiasts measure mastery if they want.
