# Grid Hunt — Dedicated Gameplay Mechanics Research Track

**Opened:** 2026-11-21.  
**Status:** questions and evidence plans, **not approved features**.  
**Reason:** real puzzle rules and player experiences should lead map/art/GPS engineering and automated content generation. The owner wants a **fun GridMates puzzle game** with authentic land-navigation habits practiced naturally.

**Existing research** [R01–R24](OPEN_DESIGN_RESEARCH_BACKLOG.md) remains valid. The GM track is focused on *mechanics*, not technical map implementation. Work on **one question at a time**.

| ID | Priority | Focused game-design research question | Outcome wanted | State |
|---|---|---|---|---|
| **GM01** | P0 | What can players do, what changes, and how do they win/undo/retry? | Formal core loop, Find & Mark vs Discovery vs later linked clues | **Research documented; untested** |
| **GM02** | P0 | How do Coordinate, Reverse, Landmark, Precision and short Mixed challenges **actually play differently**? | A small family of objective verbs, validators, meaningful examples and state rules | **Research documented; untested** |
| **GM03** | P0 | How do we create **good puzzles** rather than rote lookups or unfair guesswork? | Clue constraints, difficulty dimensions, distractor logic, novelty and solution uniqueness | **Research documented; untested** |
| **GM04** | P1 | What scoring, hints, mistakes, rewards and completion mechanics are worth having? | Low-pressure, fair optional mastery and nonintrusive game feedback | **Research documented; untested** |
| **GM05** | P1 | How do levels build, revisit and combine skills for **continued practice**? | Skill layering, beginner-to-expert learning curve, retention and transfer test concepts | **Research documented; untested** |
| **GM06** | P1 | What gives a mission a personality: theme, small story, discoveries or a collection? | Narrative wrappers with minimal reading and low authoring burden | **Research documented; untested** |
| **GM07** | P1 | How much choice belongs in Quick Hunt and Custom Hunt? | Presets, valid options, compatible constraints, easy defaults | **Research documented; untested** |
| **GM08** | P1 | How do actual playtests tell us if mechanics are enjoyable and fair? | Playtest protocol, observations, objective and experience acceptance criteria | **Research documented; untested** |

**Sources to consult:** [MDA](https://aaai.org/papers/ws04-04-001-mda-a-formal-approach-to-game-design-and-game-research/), [Sicart mechanics](https://www.gamestudies.org/0802/articles/sicart/), [GameFlow](https://doi.org/10.1145/1077246.1077253), [2024 gameplay loop method](https://intellectdiscover.com/content/journals/10.1386/jgvw_00094_1), [Patrick's Parabox GDC design talk](https://www.gdcvault.com/play/1034415/System-Centric-Puzzle-Design-in), [player agency study](https://doi.org/10.1016/j.entcom.2024.100754).

**Links to other work:** [R02 — game feel](R02_GAME_FEEL_AND_REPLAYABILITY_RESEARCH.md) studies motivation, not rules. [R06 — map interaction](R06_MOBILE_MAP_INTERACTION_RESEARCH.md) is physical touch/gesture behavior. [R07 — seeded objective generator](OPEN_DESIGN_RESEARCH_BACKLOG.md) remains **OPEN but postponed until GM02–GM03** define good objective types and puzzle constraints.

No implemented code, player tests or app behavior are implied by this research track.

**GM02 report:** [Distinct puzzle mechanics — five hunts, different thinking](GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md) (research documented 2026-11-21; not implemented or tested). GM03 documented; GM04 now next.

**GM03 report:** [Fair clues, valid puzzles, and quality gates](GM03_PUZZLE_QUALITY_AND_CLUE_DESIGN_RESEARCH.md), researched 2026-11-21. Distinguishes strict semantic/solvability checks from untested human enjoyment; no generator or playtest is built. **GM04 now next.**


**GM03 report:** [Fairness, deduction and difficulty study](GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md) documents hard quality gates vs soft enjoyment criteria, candidate-uniqueness/distractor rules, difficulty dimensions and 20 **unexecuted** QA cases. **GM04 next.**

**GM04 report:** [Hints, feedback, scoring and rewards](GM04_HINTS_FEEDBACK_SCORING_REWARDS_RESEARCH.md) — literature-backed alternatives, explicit unrun A/B plans and QA; **GM05 next**.

**GM05 report:** [Chapter progression, skill revisit, retention/transfer](GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md). Literature supports examining spaced/retrieval/interleaved practice **in other learning settings**, not guaranteed transfer to navigation. Spiral chapter arc and delayed tests are hypothetical. **GM06 next.**

**GM06 report:** [Mission themes and environmental storytelling](GM06_MISSION_THEMES_AND_ENVIRONMENTAL_STORYTELLING.md) source-backed narrative research, six reusable motifs and **unrun** validation/A-B tests. GM07 is next.

**GM07 report:** [Quick vs Custom player choice](GM07_QUICK_AND_CUSTOM_HUNT_PLAYER_CHOICE_RESEARCH.md) documents one-tap Quick, progressive Custom settings, mandatory compatibility checks and **20 unexecuted tests**. **GM08 next.**

## Research complete / prototype gate (2026-11-22)

[GM08 — Player research and prototype decisions](GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md) plus the [first-phone session sheet](GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md) document how to distinguish correctness, usability, enjoyment and learning observations; **all GM01–GM08 are research-only, untested**. Recommended next evidence: build a source-matched **GH-01 one-map three-reference Android prototype** and watch real players. [R07 seeded generation](OPEN_DESIGN_RESEARCH_BACKLOG.md) remains open as a separate research topic.
