# GM01 — Core Gameplay Mechanics: Make It a Game, Not a Worksheet

**Researched:** 2026-11-21  
**Status:** DOCUMENTED RESEARCH + TESTABLE DESIGN HYPOTHESES. **NO PROTOTYPE, APK OR PLAYER TESTING.**  
**Scope:** rules, available actions, state changes, win/retry conditions, player choices and meaningful puzzle payoffs. Game art, map SDKs, input gestures, full scoring systems and world generation are separate questions.  
**Research track:** [GM01–GM08 game mechanics](GAMEPLAY_MECHANICS_RESEARCH_TRACK.md). Previous [R02 study](R02_GAME_FEEL_AND_REPLAYABILITY_RESEARCH.md) considered motivation and replayability; this report defines actual playable rules.

## 1. Why we need this track

We have substantial research on coordinate systems, map rendering, sprite anchors and Android gestures. Those are necessary **tools**, but they do not automatically make an enjoyable puzzle. Grid Hunt needs clear **verbs**, **constraints**, **decisions**, **feedback**, and **completion conditions** before we automate thousands of content instances.

The owner wants a friendly, offline GridMates puzzle that subtly rehearses authentic land-navigation habits, beginning with 4-digit squares and eventually adding reverse reading, 6-digit interpolation, landmarks, contours and combinations. It must **not** feel like Google Maps, military operational software or classroom training.

## 2. Source-backed design references

| Source | Grounded contribution | Application to Grid Hunt | Limitation |
|---|---|---|---|
| [Hunicke, LeBlanc & Zubek (2004), MDA](https://aaai.org/papers/ws04-04-001-mda-a-formal-approach-to-game-design-and-game-research/) | Distinguishes *Mechanics*, emergent *Dynamics* and intended player *Aesthetics*. | State both the exact rule and what searching/discovering should feel like. | Framework, **not proof** that a specific mechanic will be fun. |
| [Sicart (2008), Defining Game Mechanics](https://www.gamestudies.org/0802/articles/sicart/) | Game mechanics describe agent actions in constrained game states. | A meaningful choice or rule interaction is a mechanic; an animated popup alone isn't. | Other definitions coexist. |
| [Guardiola & Czauderna (2024), Teaching and learning game design through gameplay loops](https://intellectdiscover.com/content/journals/10.1386/jgvw_00094_1) | Gameplay-loop methodology helps analyze dynamic play and was assessed in game-design education. | Draw a repeatable goal → action → outcome → next decision loop. | Study on **design education**, not proof of Grid Hunt player enjoyment. |
| [Sweetser & Wyeth (2005), GameFlow](https://doi.org/10.1145/1077246.1077253) | Heuristic dimensions: clear goals, player skills, control, challenge, feedback, concentration, immersion and social interaction. | Evaluate whether each short mission has a clear goal, forgiving controls and noticeable progress. | Developed/evaluated originally in real-time strategy context; not a universal formula. |
| [Traynor (2024), GDC session on system-centric puzzle design](https://www.gdcvault.com/play/1034415/System-Centric-Puzzle-Design-in) | Public talk description identifies iteration on core mechanics, level-design strategies and playtesting. | Develop a few small scenarios exploring **one stable rule system** before adding unrelated systems. | Talk description is not a full transcript; no unsupported detailed claims. |
| [Joshi et al. (2025), Player agency in educational games](https://doi.org/10.1016/j.entcom.2024.100754) | In a programming-game prototype, freedom over using learning mechanics improved enjoyment and intrinsic motivation without measurable learning disadvantage in that context. | Test player-selectable order of independent objectives and optional help, not a fully forced path. | Does **not** show arbitrary freedom always improves map puzzles. |
| [2026 meta-analysis of practice testing, spacing and interleaving](https://link.springer.com/article/10.1007/s10648-026-10225-5) | Observed average benefits in authentic school classroom studies but contextual and cognitive-process limitations. | Later chapters can revisit already learned grid operations on **new** maps after gaps. | Not a study of military skill retention through mobile games; do not claim validated transfer. |

## 3. Mechanic grammar: verb → rule → state → player experience

| Player verb | Constraint or rule | Actual state change | Intended outcome |
|---|---|---|---|
| **Inspect** | View numbered fictional map; pan/zoom don't alter truth | Camera state only | Freedom to look and reason |
| **Choose** | Pick any **available independent** target | Active objective changes | Small sense of agency |
| **Mark** | Select a canonical world-derived 1 km **cell** for 4 digits | Pending selection becomes that cell ID | Practice east/right, north/up |
| **Adjust** | Replace or clear *unsubmitted* selection | Pending cell changes | Recovery; no motor-error punishment |
| **Check** | Validate chosen area, not sprite center or exact pixel | On success objective solved; on error attempt recorded and correction possible | Legible cause/effect |
| **Discover** | Successful clue awards a small optional discovery or later reveals dependent task | Discovery or stage-unlock state | Curiosity / meaningful progress |
| **Hint** | Player requests graduated contextual assistance | Assistance recorded; clue remains solvable | Learn without compulsory instruction |
| **Finish** | Every mandatory objective solved | Session becomes Completed | Clear accomplishment and Next Hunt |
| **Replay** | New instance or authored puzzle, no save corruption | New objective list or session | Repetition with meaningful variation |

Important distinction: **a drawn star or particle effect is polish, not a decision mechanic**. Mechanics must change what the player can choose or understand.

## 4. Three practical core-loop candidates

### M1 — Find & Mark (necessary baseline)
- **Setup:** one small map; three clearly stated four-digit numeric targets.
- **Goal:** find and correctly confirm every target **whole 1 km square**.
- **Action loop:** read clue → inspect axes → tap square → preview/adjust → Check → see accurate feedback → next chosen target.
- **Choice:** independent targets may be solved **in any order**.
- **Win:** all targets solved; no compulsory timer, lives or fail state.
- **Risk:** could become a dry lookup task after first few rounds.

### M2 — Discovery Hunt (recommended comparison)
- Identical **coordinates, map, controls, number of targets and correctness rules** as M1.
- Correct locations produce **short nonblocking discoveries**: tiny postcard, campsite note, bridge sketch, journal scrap. Progress accumulates toward a mission completion.
- **Win:** same required correctly confirmed grid areas; story wrapper must not substitute for actual map reasoning.
- **Risk:** interruptions, clutter, or obvious icons that let players guess answers instead of reading the grid.

### M3 — Clue Chain (later, after multiple core skills exist)
- Correct first square reveals information required to solve a *different* next step (e.g. identify a bridge on the map or read its reference).
- Success unlocks next task; **order is restricted only by real information dependency**.
- **Win:** all linked tasks completed with each task's own validator, no ambiguous or impossible clue.
- **Risk:** tutorial overload, weaker agency, brittle authored/generated chains. **Do not lead with M3.**

**Initial comparison:** M1 vs M2; only then build M3 if linked clues add genuine thinking rather than a forced sequence.

## 5. Example: three-objective proof game

Use one 6×6 north-up fictional training map with target references `2437`, `2539`, `2741`. Those are the **same data in both M1 and M2**.

- `2437` is E [24000,25000), N [37000,38000).
- `2539` is E [25000,26000), N [39000,40000).
- `2741` is E [27000,28000), N [41000,42000).

**Rules:**
1. Select the active target in the mission strip, or accept an initially chosen target.
2. A tap inside one square previews **the whole square**, regardless of pixel location or a cabin's art footprint.
3. Preview can be changed or cleared. Check commits it.
4. Correct: mark target solved and allow progress. Wrong: show selected square, retain recoverable mission and offer contextual hints.
5. When all three are solved, finish the hunt. M2 reveals its discovery items unobtrusively as goals are reached.
6. New hunt uses a fresh valid target set; replay may always be available.

**Example session state (CONCEPTUAL, not Kotlin implementation):**
```text
session: NEW | PLAYING | COMPLETED
objectives: [{id, gridRef, status: AVAILABLE | ACTIVE | SOLVED}]
activeObjectiveId
pendingCellId: nullable
committedAttemptsByObjective
hintsUsedByObjective
discoveriesRevealed[] (M2 only)
mapId + mapRevision + generatorVersion + seed (when generation exists)
```

No full screen reward modal on every tap; the map is still the focal play surface.

## 6. Where actual decisions and challenges come from

A real puzzle can introduce variation through:
- **Target selection:** different *valid* grid cells, rather than always the same three.
- **Target order:** pick which of several independent clues to solve first.
- **Task transformation:** later alternate "find grid" and "what grid is this feature?" after each verb is learned.
- **Clue constraints:** later use a legend, terrain pattern, direction or spatial relationship that narrows a uniquely valid solution.
- **Optional assistance:** player chooses whether to request a right/up reminder or stronger reveal.

A difficulty increase should require **better reasoning**, not simply a smaller tap target, illegible labels, hidden gridlines, arbitrary extra steps or forced clocks.

## 7. Correctness, mistakes, completion, rewards

**Default relaxation hypothesis:** no life counter; an incorrect *submitted* choice gives specific feedback and allows correction. Uncommitted finger slips are never scored. Players may use hints and still finish. Assisted solving can be noted separately without shame or artificial paywall.

**A completion is not necessarily mastery.** Finite grids permit guessing. To assess actual learning, have a player solve a **new** cell, ideally on a different map and without help, and observe whether they use the axes. Deliberate random-tapping patterns can be logged in playtests, not punished automatically with a lives mechanic.

**No scoring decision yet:** optional stars, perfect runs, speed, streaks and cosmetic economies belong to **GM04**. Avoid accidentally locking a scoring system via R02 feedback prose.

## 8. Anti-worksheet checklist

Every level pitch must answer:
- Can the player explain **what they want to find** in a single sentence?
- What *actions* change game state? Are any pointless extra taps?
- Is the solution logically determined by clues and grid geometry, not icon size or guessing?
- Does each success produce visible progress?
- Can players recover from both conceptual mistakes and interaction slips?
- Is the win condition unambiguous? Can the player play another hunt immediately?
- Does the next stage create **a different decision** rather than just another identical square?
- Does solving a level actually use the map-reading habit we claim is being practiced?
- Can all this work from a tiny offline map with no GPS, network or huge engine?

## 9. Controlled prototype experiment (UNEXECUTED)

**Test A — M1 vs M2:** same map, same three target areas, same hint, labels, controls and difficulty. Counterbalance presentation order among novice testers. Observe actual grid reading, time spent waiting on discoveries, accidental interactions, voluntarily choosing another mission, objective-order decisions, and what participants describe as satisfying/confusing.

Neutral questions: "What did you do to solve it?", "What happened when a marker was correct?", "Which would you play again?", "Which parts seemed unnecessary?"

**Technical gate:** coordinate validator and answer areas must be identical between variants. **Experience gate (proposal):** extra discovery content must not obstruct reading or require irritating taps to dismiss. Do not assign fake playtest percentages.

**Test B — M3 later:** only after Reverse/Landmark mechanics are proven understandable. Compare two independent clues with a two-step real information chain; reject busywork chains.

## 10. Finding and next game design question

**Recommended first prototype mechanic:** a **small, recoverable, player-ordered Find & Mark loop**, then compare the same system with a **Discovery Hunt payoff**. Reserve dependency-based Clue Chains for later mixed missions. This is an evidence-informed hypothesis **awaiting prototype and human validation**, not a final gameplay decision.

**NEXT GM02:** explicit objective and validator rule grammar for Coordinate Hunt, Reverse Hunt, Landmark Hunt, Precision Hunt and short Mixed Hunt. After GM02/GM03, return to **R07 seeded generation**, so we generate meaningful missions rather than large numbers of repetitive worksheets.
