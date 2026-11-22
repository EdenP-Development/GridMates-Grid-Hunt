# GM07 — Quick Hunt and Custom Hunt: Meaningful Choice Without Setup Friction

**Research date:** 2026-11-21  
**State:** RESEARCH DOCUMENTED / DESIGN HYPOTHESES; **no interface, preset engine, random generator, Android build or human playtest implemented or executed**.  
**Focus:** distinguish a one-action Quick Hunt from an expressive but reliable Custom Hunt while retaining access to chapters and a no-story free-play path.  
**Related:** [GM01 core gameplay](GM01_CORE_GAMEPLAY_MECHANICS_RESEARCH.md), [GM02 task validators](GM02_DISTINCT_PUZZLE_MECHANICS_RESEARCH.md), [GM03 quality gates](GM03_PUZZLE_QUALITY_AND_FAIRNESS_RESEARCH.md), [GM04 hints](GM04_HINTS_FEEDBACK_SCORING_REWARDS_RESEARCH.md), [GM05 chapter progression](GM05_CHAPTER_PROGRESSION_SPACED_PRACTICE_AND_TRANSFER.md), [GM06 story-light missions](GM06_MISSION_THEMES_AND_ENVIRONMENTAL_STORYTELLING.md). Seed generation remains R07, not implemented here.

## 1. Main conclusion

**Quick Hunt and Custom Hunt should run the exact same validated puzzle engine.** They differ in who chooses the setup:
- **Quick Hunt**: safe, short, immediately playable configuration selected by the game from suitable authored and later seed-based objectives.
- **Custom Hunt**: players deliberately select meaningful constraints like task type, length, map/theme, and challenge level; secondary controls only appear on request.
- **Chapters**: curated skill introduction and story-lite progression, not a third ad-hoc generator.

**Proposed initial menu:** Continue Chapter / Quick Hunt / Custom Hunt. Avoid putting an intimidating menu of numerical map parameters in front of the first puzzle. Do not quietly override an explicit Custom Hunt choice to make generation easier.

An important choice-overload caveat: **more choices aren't inherently harmful**. What matters is how complex, unfamiliar, conflicting, and burdensome the choices are. Limit *unnecessary upfront decisions*, while keeping meaningful player control.

## 2. Source research: supported design guidance and boundaries

| Source | What the evidence actually supports | Applied design hypothesis | Limits |
|---|---|---|---|
| [Nielsen Norman Group, *Progressive Disclosure*](https://www.nngroup.com/articles/progressive-disclosure/) | Show primary features first; specialized options available explicitly; the boundary between levels must follow users' actual needs. Secondary navigation should be obvious and labeled. | One-tap Quick; Custom gets compact common settings with an obvious "More options" section. | UX guidance, not a tested design for Grid Hunt. If players continually open secondary controls, promote them. |
| [Android Developers, *Settings*](https://developer.android.com/design/ui/mobile/guides/patterns/settings) | Brief precise labels, contextual rather than buried frequently used actions, polite defaults, saved preferences, and respect system accessibility settings. | Put per-hunt settings **with Custom Hunt**, reserve general app preferences for Settings, and use low-risk offline defaults. | Android platform guidance, not a rule for optimal hunt length. |
| [Scheibehenne, Greifeneder & Todd (2010), *Can There Ever Be Too Many Options?*, Journal of Consumer Research](https://doi.org/10.1086/651235) | Meta-analysis of **63 conditions in 50 experiments (N=5,036)** yielded near-zero overall mean effect of greater choice, with substantial variance. | Reject simplistic "three options is scientifically best" claims; evaluate player task complexity and expertise. | Consumer-choice studies, not an Android game-mode experiment. |
| [Chernev, Böckenholt & Goodman (2015), *Choice Overload: A Conceptual Review and Meta-Analysis*](https://www.sciencedirect.com/science/article/pii/S1057740814000916) | Across 99 observations / 7,202 participants, identifies moderators including choice-set complexity, decision-task difficulty, preference uncertainty and decision goal. | A one-tap default helps users uncertain about what to play; meaningful customizable presets help those who know their preferences. | Consumers/assortments, not proof that these exact settings work in Grid Hunt. |
| [Joshi et al. (2025), *Exploring Player Agency in Educational Video Games*, Entertainment Computing](https://web.ics.purdue.edu/~cmousas/papers/jour25-EntCom-PlayerAgency.pdf) | In a particular programming-game experiment, giving agency over use of learning mechanics improved enjoyment/intrinsic motivation without measured learning disadvantage in that setting. | Let users voluntarily choose Coordinate/Reverse/Landmark practice or a mixed session rather than forcing only chapter order. | Does **not** validate arbitrary settings, random generation or direct navigation transfer. |
| [NN/g, *Recognition Rather than Recall*](https://www.nngroup.com/articles/recognition-and-recall/) | Visible meaningful options reduce user recall demands when appropriate. | Use descriptive mode labels and a short plain-language preview instead of unexplained `P4`, `MGRS`, `E/N` switches. | UX heuristic, not controlled proof of this menu. |

The user already established a preference for simple GridMates apps and minimal steps. This informs the product hypothesis but is **not external scientific evidence**.

## 3. Proposed information architecture

```text
Home
  Continue Chapter                    [curated story-lite practice]
  Quick Hunt                          [STARTS a short suitable puzzle]
  Custom Hunt...                      [opens compact configuration]
  How to Play / optional accessibility settings
```

**Quick Hunt first-time behavior:** if there is no known experience yet, serve **three four-digit Coordinate tasks** with clear map labels and optional contextual help. A learning example may be offered **inside** the round. Avoid a mandatory 12-step setup wizard. Later Quick Hunt may rotate already encountered task types on a valid map if a player wants variation.

**Quick Hunt after experience:** use eligible, known skills to select a short validated mission. Avoid unexpectedly introducing six-digit plotting, symbols or complex Clue Chain because the next chapter is theoretically available. Keep a *small readable pre-start or in-round summary* describing which tasks will occur; the one-tap action must not turn into surprise mechanics.

**Custom Hunt:** a single scrollable, clearly grouped setup view with descriptive defaults, a live text summary and Start. More technical options may expand. Keep keyboard entry unnecessary for routine setup.

**Chapters:** fixed curated missions with purpose and progression. Custom is a valid independent free-play experience; experienced users should not have to grind through chapters just to request a familiar skill. A new-to-user task can offer optional “Show me how” guidance rather than silently disabling the feature forever.

## 4. Which choices belong where?

| Control | Quick (auto) | Custom prominent | Custom optional/advanced | Dependencies |
|---|---|---|---|---|
| **Hunt mode** | Suitable familiar tasks | Coordinate, Reverse, Landmark, Mixed | Precision as available or explicit advanced type | Task must have validator and eligible clues; Mixed requires compatible tasks |
| **Length / targets** | Small **3** as first prototype default | Short (3), Standard (5), Long (8) | Fine-tune count 3–10 if meaningful | Enough distinct valid objectives exist; max is not a promise of content |
| **Challenge** | Relaxed/familiar | Relaxed / Thoughtful / Expert (descriptive) | Per-skill difficulty facets after testing | Must change reasoning/assistance/distractors, **not secretly change coordinate rules** |
| **Map / theme** | Suitable clean map | Woodland, Countryside, Lakeside etc. when authored | Fixed vs shuffled from compatible approved packs | Theme cannot change underlying grid math, clues or feature IDs |
| **Precision** | 4-digit until 6-digit skill is known | Normally derived from chosen mode | 4-digit (1km), 6-digit (100m) once supported | Requires subcell data and fair zoom/magnifier; no false precision |
| **Hints** | Always available | Visible note: Help is available | Presentation style / offer to turn off prompts, **not paid solution gate** | Disabling hint reminders must not hide core accessibility controls |
| **Story/Discovery** | Off or one-line flavor according to tested default | Optional small “Mission flavor” choice if used frequently | Journal detail/animation preference | No optional prose may contain essential clue; required clues always visible |
| **Seed / share code** | New valid seed under future R07 | Not necessary at v1 | Repeat/share seed after generator and versioning exist | Same revision+seed must reproduce validated answer set |
| **Time/score** | Never compulsory | Not prominent for initial build | Optional challenge trial later | Must not gate casual completion |

**Settings to avoid initially:** arbitrary map kilometer origin, raw integer seed entry, count of every individual landmark type, percentage of contours, independently randomize bridges/water/roads, MGRS zone/datum switches, satellite layer, real-world GPS, or a deep difficulty spreadsheet. These choices introduce complexity without validated puzzle value.

**Prototype numbers are not research-derived optima.** “3, 5, 8” and the difficulty labels are candidate presets; playtests should determine whether a shorter/longer session is preferable.

## 5. Show what the choice really produces

Example Custom screen microcopy (PROPOSED):

```text
CUSTOM HUNT

Mode          Mixed Hunt        [Choose]
Length        Short · 3 finds   [Choose]
Map           Woodland         [Choose]
Challenge     Relaxed          [Choose]
More options                      [Expand]

Your hunt: 3 short tasks on a woodland grid,
including Coordinate and Reverse puzzles.
Hints available · No timer

[Start Hunt]
```

If no suitable map supports a mode/count combination, **don't pretend it will work**. Show a near-field explanation:
- “This map has only 2 distinct qualifying crossings. Choose Shorter Hunt or another map.”
- “Six-digit plotting needs a map with a precision grid. Choose a supported map or use four digits.”
- “Mixed Hunt needs at least two compatible task types. Add Reverse Hunt or change to Coordinate Hunt.”

**No silent fallback from a deliberately selected six-digit challenge to four digits**, from a requested landmark puzzle to random coordinates, or from eight targets to three. Suggestions may offer **explicit one-tap fixes** but require player choice.

## 6. Mission validity / compatibility matrix

| Setting combination | Status / action | Why |
|---|---|---|
| First-time Quick, 3 four-digit targets, approved woodland map | **Allow** if at least 3 valid distinct references exist | A predictable entry-level task |
| Quick asks for Reverse but skill has not been introduced | **Choose familiar skill instead**, or provide an explicit optional learning preview; never surprise | Protects simple first-play |
| Custom 6-digit Precision on map with no validated 100 m selection tool | **Block and explain** | Prevents motor/accuracy failure |
| Custom Landmark on map with zero uniquely distinguishable candidate sets | **Block and explain** | Prevents zero/ambiguous solutions |
| Custom 8 distinct bridge relations with only 2 authored unique bridge clues | **Block and suggest smaller count/map** | No filler or duplicates |
| Custom Mixed with only Coordinate task selected | **Treat as Coordinate**, but require explicit user confirmation to switch; or make Mixed unavailable until second selected type | Mode label must be truthful |
| Custom 4-digit and 6-digit with relevant validated fixtures | **Allow**, if user knowingly selected both and controls are supported | Legitimate variation |
| Invalid saved preset after map/content revision | **Mark needs attention**, explain and offer one-tap repair | No hidden downgrade |
| Custom asks for unlocked/expert skill without completing chapter | **Allow when feature exists**, offer “How it works” | Preserve agency, keep guidance |
| Custom story turned off for a true clue-chain mission | **Allow**, still show required puzzle clue independently | Story flavor never gates knowledge |
| Offline / after process restart | **Allow**, persist draft/resolved options locally | No account/network required |

**Implementation concept:** For every user change, compute **eligible candidates** from map data and validator rules. If impossible, disable Start and explain the exact conflicting settings **without altering the user's selections**. If still possible but fewer candidates than requested, offer alternatives. A map/asset revision triggers revalidation.

## 7. Resolved configuration contract (proposal, not code)

```json
{
  "configurationVersion": 1,
  "entry": "CUSTOM",
  "mapId": "woodland-training-01",
  "mapRevision": 1,
  "mode": "MIXED",
  "eligibleTaskTypes": ["COORDINATE_CELL4", "REVERSE_REF4"],
  "targetCount": 3,
  "challengePreset": "RELAXED",
  "hintsAvailable": true,
  "narrativeFlavor": false,
  "seed": "local-001",
  "generatorVersion": 1
}
```

Example only, **not a real app schema or executed generation**. It separates deliberate user constraints from the normalized resolved mission and future seed/revision metadata.

**Preference persistence:** save last Custom draft (including settings), optionally a user-selected default preset, and play accessibility preferences locally. **Do not let experimental Custom settings silently redefine the one-tap Quick experience.** Persist exact task IDs/map revision for active saves to avoid surprise changes after updating content.

**Story vs gameplay:** a Mini Expedition narrative wrapper (GM06) can apply to Custom or chapters; Quick should work without it. All real clue dependencies remain in mandatory objective metadata.

## 8. Compare three UX alternatives (UNRUN)

**Variant A — One-tap Quick and separate Custom (preferred candidate)**
- Home Quick Hunt begins suitable puzzle immediately.
- Custom Hunt opens 4 grouped common controls + explicit More Options.
- Most direct; may disappoint regular users wanting a two-tap favorite preset.

**Variant B — One small preset chooser**
- Home selects “Quick: Relaxed / Thoughtful / Expert” in one brief step before Start.
- Increased awareness and control, but costs another decision every session.
- Could be a compact long-press/preset shortcut *after* usability data, not required to test first.

**Variant C — All Custom options on Home**
- Maximum immediate flexibility, most clutter on first view.
- Useful comparative **control**, not preferred for novices.

Keep exact underlying maps, task distribution, actual difficulty, help, and outcomes fixed for fair comparison. Test **startup friction** independently from in-game enjoyment.

**Observe:** time/taps until first map, misconfiguration, first-time confusion, custom control discoverability, player awareness of which mode ran, ability to recover from incompatible choices, percentage who actually change each option, repeat-use desire, accessibility at larger text scales, and whether “quick” puzzles feel unexpectedly advanced. Do not fabricate results.

## 9. Planned QA cases — NONE EXECUTED

| ID | Fixture | Proposed pass condition |
|---|---|---|
| C01 | Fresh install Quick | Starts beginner-appropriate 4-digit play without config form |
| C02 | Experienced user Quick | Chooses only supported familiar tasks, preview is truthful |
| C03 | Quick vs Custom same resolved config | **Identical** mission validation and completion rules |
| C04 | User chooses 6-digit on unsupported map | Start disabled, exact reason shown, explicit fixes |
| C05 | User chooses 8 landmark tasks with only 2 unique valid clues | Reject/offer count/map correction; no padding with repeats |
| C06 | User selects unintroduced but implemented skill in Custom | Allow with opt-in tutorial, not impossible permanent lock |
| C07 | No suitable objectives for chosen map/mode | Clear no-valid-puzzle state and meaningful adjustment options |
| C08 | Mixed mode only one enabled task family | Truthful classification or explicit require second family |
| C09 | Switch map after setting precision/length | Revalidate, preserve stated constraints or explain conflict |
| C10 | Device offline / no saved progress | Core selections and game continue |
| C11 | Leave/reenter Custom | Draft preferences retained; Quick doesn't inherit unsafe choices |
| C12 | Retry invalid saved preset after content update | Mark needs attention, no silent mode change |
| C13 | Starting/resuming same generated seed+revision | Deterministic same answers once generator built |
| C14 | Turn off story flavor | All actual clue dependencies still visible, puzzle solvable |
| C15 | Large text, TalkBack, portrait/landscape | All controls and error explanations reachable, no inaccessible scroll |
| C16 | Tap 3/5/8 preset and adjust to 10 custom | Labels/count consistent; generator doesn't exceed supported pool |
| C17 | Custom “Expert” includes unsupported precision | Explain need for precision tools, never silently misgrade |
| C18 | Quick mode if zero valid local candidates | Noncrashing fallback to an approved built-in practice round **only if valid**; otherwise transparent unavailable state |
| C19 | Press Back/cancel after changing settings | No accidental launch and no state corruption |
| C20 | Compare user choice and generated result summary | Exact map, objective type, count and precision correspond to selections |

**Acceptance:** hard map/puzzle constraints from GM03 always pass, none of Custom's explicit user choices silently changes, and Quick remains immediately understandable. Test suites and real Android trials must actually execute before any row becomes passed.

## 10. What we should not claim

- There is **no evidence that exactly four controls, 3/5/8 lengths, or these three difficulty names are optimal**. They are prototype candidates.
- Consumer studies show **context-dependent** choice overload, not an invariant rule that 3 settings outperform 8.
- Agency in a programming game **does not** prove unlimited Custom Hunt settings increase fun or navigation retention.
- No current source demonstrates that our proposed Quick selection produces a fun, correctly calibrated random mission. We haven't built a generator or surveyed users.

## 11. GM07 decision proposal and next work

**Recommendation for first playable prototype:** quick one-action 3-task 4-digit hunt; Custom 4 prominent settings (Mode, Length, Map, Challenge), explicit **More Options** for precision and advanced controls later, and a visible one-sentence generated-hunt summary. Keep **one canonical answer engine**, validate configuration at every change, preserve user choices, and offer understandable fixes rather than secret downgrades. No network accounts or forced daily play.

**Not adopted/implemented:** final settings, difficulty score, generator, presets, game UI, validation code, Android QA, measured user choice.

**NEXT GM08:** formal gameplay playtest research: how to run small objective-specific experiments, collect error types/real preferences, evaluate enjoyment separately from grid-reading success, and define evidence-based go/no-go decisions. Then return to R07 seed-based generation design, and move into small code prototypes only when the team/user chooses that direction.
