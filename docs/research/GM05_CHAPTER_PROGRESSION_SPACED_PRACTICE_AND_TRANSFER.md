# GM05 — Chapters, Skill Revisit, and Honest Retention Testing

**Research date:** 2026-11-21  
**Status:** SOURCE-BACKED RESEARCH + **UNTESTED CHAPTER DESIGN PROPOSAL**. No levels, scheduling engine, adaptive practice, trial, or APK implemented.  
**Scope:** when/how Grid Hunt introduces and revisits mechanics, chapter vs repeatable play, player autonomy, and how to test whether practice lasts. Not rewards (GM04), map rendering (R04–R06), or seed generator implementation (R07).  
**Parent:** [Game mechanics research track](GAMEPLAY_MECHANICS_RESEARCH_TRACK.md) · [GM04 hints and feedback](GM04_HINTS_FEEDBACK_SCORING_REWARDS_RESEARCH.md) · [Master design](../MASTER_GAME_DESIGN.md) · [Skill-to-puzzle matrix](SKILL_TO_PUZZLE_MATRIX.md).

## 1. Main finding

**Research-supported learning strategies are useful candidates, but they aren't automatically good game chapters.** A game can feel delightful without producing transferable navigation improvement, and a practice schedule can produce better retention in a classroom while feeling annoying in a game.

**Proposed game-first compromise:** introduce one genuinely new player action at a time, demonstrate it briefly, let the player practice independently, then **bring it back on fresh maps during later expeditions**. Rotate short familiar puzzles with harder ones. Add a mixed finale only when its component skills have been learned. Always allow accessible help and relaxed play.

The aim is repeated, authentic reasoning (eastings → northings; point → formatted reference; symbol/terrain interpretation; 100 m subdivision), **not** a long escalating lecture, a forced daily attendance schedule, or an implied military qualification.

## 2. External evidence: what it supports and what it does not

| Source | Direct supported finding | Game-design implication | Important boundary |
|---|---|---|---|
| **2026 authentic-classroom meta-analysis**: [*A Meta-Analysis of the Effectiveness and Boundary Conditions of Retrieval Practice, Distributed Practice, and Interleaved Practice in Authentic School Classrooms*](https://link.springer.com/article/10.1007/s10648-026-10225-5) | Three-level analysis of **53 studies, 233 effects, 7,303 pupils**, with average **testing g=0.37**, **spacing g=0.20**, **interleaving g=0.40**, but sizable variation and moderators including feedback timing and practice/test match; authors caution against one-size-fits-all use. | Revisit recently learned mechanics using changed coordinates, maps and tasks. Vary exercise order *after* a skill is understandable. Keep feedback meaningful and compare with simpler blocked chapter sequences. | Primary/secondary **school** contexts; these are not predicted Grid Hunt gains and effect sizes must not be applied to a mobile game. |
| **Rohrer, Dedrick & Stershic (2015), [*Interleaved Practice Improves Mathematics Learning*](https://eric.ed.gov/?id=EJ1071568)** and [WWC review](https://ies.ed.gov/ncee/wwc/Study/89950) | 126 seventh graders practiced mathematical skills in different orders; interleaving improved later assessment in the studied classroom condition. | Once players know Coordinate and Reverse, occasional mixed hunts may support remembering **which** operation to use. | Maths is not spatial navigation; interleaving too early may confuse a novice and reduce fun. |
| **2019 cognitive-load review**: [*Cognitive Architecture and Instructional Design: 20 Years Later*](https://link.springer.com/article/10.1007/s10648-019-09465-5) | Worked examples help many novices; fading redundant support as expertise grows can avoid unnecessary cognitive load; temporary instructions that disappear may burden memory. | First show a right/up guide beside the map; later make it optional, with an accessible Help return path. Avoid fading help *forever* for struggling or infrequent players. | Explanatory synthesis, not a controlled test of this game's tutorial. |
| **2023 game tutorial experiment**: [Benvenuti et al., *An Approach to Assess the Impact of Tutorials in Video Games*](https://www.mdpi.com/2227-9709/10/1/6) | Investigated tutorial variants in an FPS prototype using 46 participants and performance/questionnaires; demonstrates tutorial format can be studied empirically. | Measure the effects of instructional presentation inside the game, not just player preference for a pretty tutorial. | Different game genre, controls and sample; no direct map-game design winner. |
| **2022 implicit tutorial pilot**: [*Learning to Play: Understanding In-Game Tutorials with a Pilot Study on Implicit Tutorials*](https://pmc.ncbi.nlm.nih.gov/articles/PMC9676530/) | Implicit instruction was explored in a mobile game; pilot reported more favorable tutorial perceptions in the tested context, notably among experienced gamers. | Offer a brief discoverable in-map instruction rather than a mandatory text wall. | Pilot evidence and player-background effects; do not generalize to all beginners. |
| **2023 video-game training meta-analysis**: [Smith & Basak, *A Game-Factors Approach to Cognitive Benefits from Video-Game Training*](https://pmc.ncbi.nlm.nih.gov/articles/PMC10395941/) | Across 63 studies (N=2,079), overall cognitive gains were moderate/small, with outcomes associated with specific gameplay features more than broad game genre labels; transfer varied. | Target the exact navigation reasoning mechanisms and separately measure whether skills generalize beyond familiar screens. | Generic cognitive training, not outdoor navigation. It does **not** establish transferable land-navigation skill from Grid Hunt. |
| **2025 learning-and-transfer review**: [*Learning and Transfer: A Perspective From Action Video Game Play*](https://pmc.ncbi.nlm.nih.gov/articles/PMC11813694/) | Task-specific learning does not guarantee generalized performance on new tasks; careful transfer measurement matters. | Distinguish same-map fluency, fresh in-game maps, paper-map reading and outdoor field navigation. | Action video game/cognition context, not proof of a map-training game effect. |

**Interpretation:** Spacing, retrieval and mixed practice have supporting educational evidence, while gameplay learning and skill transfer require independent observation. There is **no validated optimal interval, level count, chapter length or player success threshold for Grid Hunt**.

## 3. Keep the owner-approved direction distinct from chapter suggestions

**Existing project direction (from [master design](../MASTER_GAME_DESIGN.md)):**
1. First Bearings — Woodland: 4-digit eastings/northings.
2. Look Again — Countryside: reverse reading.
3. Clues on the Map — Lakeside: legends and landmarks.
4. Finer Details — Highlands: 6-digit precision and zoom.
5. Across the Map — Mixed Landscapes: combine objectives.
6. The Expedition — Mixed Challenges: multi-stage puzzles.

These are **working chapter names/illustrative arc**, not published content or an owner-approved final chapter count. Preserve familiar story/theme direction, but revise individual mission **mix** so old mechanics recur after new ones begin.

**Pilot remains deliberately small:** the earlier project proposed **8–12 curated puzzles across the first two skills** before committing to all six chapters. Keep that lightweight pilot instead of building dozens of maps and assuming the progression works.

## 4. The five-step chapter pattern (prototype hypothesis)

| Phase | Meaning | Example for Reverse Hunt | UX policy |
|---|---|---|---|
| **Encounter** | One short clear reason to use a new verb | A marked cabin's location must go into a survey notebook | No compulsory vocabulary wall |
| **Guide** | Partial worked example near the actual map | Show southwest easting/northing of one marked point | Keep the relevant hint beside the task; user can revisit |
| **Try** | A fresh but similar problem | Return the four-digit reference for a **different** lookout | Wrong answer gives step-specific feedback |
| **Vary** | Change relevant details, not unrelated demands | Marked feature near a line; another map style; leading zero | Don't introduce 6-digit precision at the same time |
| **Revisit** | Later independent opportunity | In a new chapter, find grid `2437` then reverse-read a different campsite | Help still on demand, relaxed completion always possible |

The player should encounter a **real puzzle decision** before a new explanatory page. Full “worked examples” can be optional contextual help rather than forced classroom instruction.

## 5. Proposed spiral chapter sequence (illustrative, NOT authored levels)

| Themed chapter | Main new skill | Brief return to previously learned skills | Suggested finale | New cognitive load limit |
|---|---|---|---|---|
| **1. First Bearings — Woodland** | Find 1 km squares using four-digit references | Repeat the same east-first/north-second rule on new coordinates | Three independent squares with optional hints | One task type; keep clean labels |
| **2. Look Again — Countryside** | Reverse-read reference from a marked **point**, preserve zeroes | A few fresh **Coordinate** hunts | Locate one square, then report a different marked feature's grid | No new six-digit precision |
| **3. Clues on the Map — Lakeside** | Identify **unique** symbol/landmark using legend and relationships | Coordinate + Reverse as short familiar tasks | Identify one of two bridges from a spatial clue and report its grid | Add one spatial relation at a time |
| **4. Finer Details — Highlands** | Split six digits; select a 100 m **sub-square** via magnifier | Calm four-digit/reverse/landmark activities | Find a parent kilometre cell, then one correct tenth-by-tenth cell | Reduce decorative noise and target count while precision is new |
| **5. Across the Map — Mixed Landscapes** | Choose the right known verb based on the clue; short linked tasks | All four established verbs rotated across **fresh** map fixtures | Linked two- or three-step search with justified clue dependencies | No new bearing/contour/scale system all at once |
| **6. The Expedition — Mixed Challenges** | Short satisfying multi-objective journey, *optional* inference/route twist | Old skills return without forced review slides | A mixed expedition with discovery payoff and free help | Complexity from combination, not a giant checklist |

**Map accuracy and content caveat:** when contours/scale/distance/bearings arrive in later expansions, they need their own grounded authored content, original geometry and tested math. Merely placing brown curves is not valid contour instruction. These aren't required for the initial six-chapter sketch.

## 6. Balance the short hunt and the longer chapter

**A hunt:** one map, typically **3–5 tasks** as a prototype target; a map challenge that can be played and finished independently, without story interruption after each correct mark. A relaxed player's five-minute break should be viable, but **no playtime target is validated**.

**A chapter:** a loose thematic progression of multiple short hunts, not one giant timed examination. Prefer chapter completion through regular solves and a pleasing illustrated map-page or journal marker; all assisted solves count. An optional independent-practice indicator may be separate (see GM04).

**The chapter needs personality, not only difficulty:** new woodland/river/lake/highland visual language, a few recurring feature types, discoveries and clear mission purposes. The level sequence should not read like a textbook contents page. Mission storytelling is researched separately at GM06.

**Avoid quota assumptions:** do not assume each chapter needs exactly 10 levels, every mixed hunt needs six objectives, or a final requires more taps. More goals ≠ more reasoning.

## 7. Revisit logic for chapters, Quick Hunt and Custom Hunt

### Rule prototype: practice rotation by **unlocked skill**
- On entering new chapter content, **prioritize its single new mechanic** with one guided example and a fresh independent puzzle.
- When familiar, return earlier task types as short, optional—or naturally embedded—objectives.
- A **new mission** should choose fresh target points/references, not simply display a memorized coordinate from the prior puzzle.
- Avoid revisiting **every** known skill in **every** hunt. Rotate: players should still feel the chapter's distinctive theme.
- Do **not** schedule a mandatory daily reminder, wait period, “learn again” lock, or loss of progress due to inactivity.
- Optional Quick Hunt may default to a mix of familiar skills once unlocked; Free/Custom Hunt can always allow an easy practice session at four digits.
- Let people choose older chapters and old techniques even after unlocking harder ones. A beginner shouldn't be pressured to master everything to keep enjoying play.

### Candidate local-only data contract (NOT IMPLEMENTED)

```text
skillId
introducedAtChapterId
unlocked: boolean
guidedExamplesSeen: integer
attemptsInFreshPuzzles: integer
correctFirstCommitsWithoutHint: integer
lastPracticeLocalDate: optional
lastEncounteredPuzzleIds: bounded list
currentAssistancePreference: optional
```

**Privacy/design:** no cloud account or server is necessary to rotate locally stored skill content. The data is optional and should not trigger shameful streak notices. Dates alone don't establish that a skill was truly retrieved; success on fresh tasks matters more.

**Danger of premature adaptivity:** do not introduce an opaque AI learner score that claims to know someone's outdoor navigation competence. A rule-based prototype is more explainable and testable.

## 8. How to test learning without overstating results

Measure **three separate levels**, with consent and purpose clear:

**Level 1 — Game comprehension / immediate performance**
- A player can finish a new four-digit puzzle on the same map **without additional verbal coaching**.
- Observe easting/northing swaps, line-side mistakes, leading zero omissions, help usage, reticle slip.
- This measures *game performance at the test*, not durable skill.

**Level 2 — Retention and near transfer**
- After a proposed **one-week interval**, test fresh in-game map, target coordinates, and visual skin, without a compulsory tutorial; permit a separate helped-play path afterward.
- In another optional comparison condition, test at roughly **four weeks**, with no reminder required.
- Measure *first unassisted attempt* and error type; distinguish players who repeatedly practiced meanwhile from those who did not.
- The 1- and 4-week intervals are **study logistics proposals**, not literature-derived optimum intervals.

**Level 3 — Transfer to non-game material**
- Provide an **original, accurately drawn paper-style grid map** with equivalent coordinate rules, different graphics and no familiar game marker cues.
- Ask for one four-digit plot and one reverse read; report game-to-paper performance **separately**.
- Optional real-map test requires properly contextualized grid data, legible licensed imagery and clear instructions; only later consider observed supervised outdoor navigation tasks, not app self-reports.
- This is a transfer experiment idea, **not** evidence that the game already trains operational navigation.

### Study design (small formative pilot first)
1. Ask a few unfamiliar novice players to solve fresh tasks after guided onboarding; have at least one experienced map reader catch authenticity defects.
2. Use matched groups if possible: **blocked practice** (many coordinates in a row) vs **spiral-mixed practice** (introduce each verb first, then revisit on new maps). Keep total practice amount and target difficulty similar.
3. At end of play, measure **enjoyment and ability separately**; replay choice, clarity and friction are not identical to learning.
4. Retest later on **novel** items; don't show the same `2437` square/art and call correct memory skill retention.
5. Record source revision, exact map/content seeds, hint used, attempts, duration, errors by type, test timing, consent, and any missing follow-up data.
6. Do not claim significant differences from a 3–5-person usability pass; a larger controlled study would be needed for reliable efficacy conclusions.

## 9. Proposed QA matrix (NONE EXECUTED)

| ID | Check | Expected behavior |
|---|---|---|
| P01 | Intro to 4 digits | Correct real 1 km selection and optional guidance |
| P02 | First reverse task | Clear response type, same 4-digit rule, leading zeros preserved |
| P03 | New landmark task | At least one unique candidate verified by canonical geometry |
| P04 | First 6-digit task | Correct parent 4-digit and 100 m cell, accessible controls |
| P05 | A former skill appears in later chapter | Fresh reference/map, original answer rule unchanged |
| P06 | Fading guidance | Help returns on demand and never permanently blocks struggling players |
| P07 | Wrong answer on review task | Recoverable; no chapter lock or lost progress |
| P08 | Assisted solved chapter | Normal completion and access to next chapter |
| P09 | A chapter introduces several new verbs at once | Flags cognitive overload/authoring review |
| P10 | A mixed level requires unlearned verb | Reject until prerequisites explicitly taught |
| P11 | Linked tasks depend on a locked future clue | Reject content dependency cycle |
| P12 | An old puzzle is replayed | Clearly practice/replay, not falsely “fresh independent mastery” |
| P13 | Practice log absent, cleared or offline | Game still works and player can choose a chapter |
| P14 | No daily play | No penalty, lock, shame message or streak loss in relaxed mode |
| P15 | One-week novel in-game retention check | Track observed accuracy separately from session play |
| P16 | Novel paper-style test | Measure separately from game and note limits |
| P17 | New chapter map is prettier/different | Numeric/landmark answer correctness unaffected |
| P18 | Hard task uses tiny hit targets | Accessibility QA failure; add tool/zoom |
| P19 | Player returns after long absence | Optional recap; no forced restart |
| P20 | Equal-practice A/B chapter schedules | Compare enjoyment, confusion, new-task performance separately |

Tests above are design acceptance **ideas**, not green checks. In-game learning analytics are optional local test instrumentation, not an automatic requirement for user tracking.

## 10. Proposed decision, blockers, next question

**Proposed:** six themed chapters remain a useful narrative skeleton, but **interleave old skills through later chapters**, introduce just one significant new operation at a time, fade compulsory guidance while keeping optional Help accessible, and let Quick Hunt rotate familiar tasks for casual practice. Curated small pilot first.

**Not settled:** exact levels per chapter, difficulty tuning, interleaving cadence, number of objectives, mandatory vs optional review, retention interval, badge/stat policy and whether this increases player enjoyment or real-world skill.

**Next focused research:** **GM06 — Mission character and discovery narratives without writing-heavy story walls**. R07 generator research stays separate until puzzle-quality grammar and human feedback can validate generated objectives.
