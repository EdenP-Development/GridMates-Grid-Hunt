# GM08 — Playtest Evidence, Interpretation and Prototype Gates

**Research date:** 2026-11-22  
**Status:** RESEARCH AND PLAYTEST PLANNING ONLY — **NO GAMEPLAY IMPLEMENTATION, APK, QA RUN, HUMAN STUDY, OR MEASURED RESULTS.**  
**Scope:** Evaluate whether Grid Hunt is correct, understandable, enjoyable and genuinely exercises map-reading reasoning. A follow-up [first phone session sheet](GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md) provides a ready-to-use protocol.  
**Context:** [GM01–GM07 mechanics research](GAMEPLAY_MECHANICS_RESEARCH_TRACK.md), [existing QA baseline](../QA_AND_PLAYTEST.md), [roadmap](../ROADMAP.md).

## 1. Research conclusion

**An accurate coordinate validator is necessary, not sufficient, for a good game.** Similarly, a player saying a round was fun does not prove the map math is correct, and a high rate of right answers does not prove map-reading skill if the artwork gives away locations.

Evaluate four distinct evidence domains:

| Domain | Core question | Evidence to collect | What it cannot prove |
|---|---|---|---|
| **Technical correctness** | Are 4-digit 1km and 6-digit 100m **areas**, eastings/northings, map bounds, feature IDs and gesture transforms correct? | Pure coordinate tests, deterministic fixtures, executed Compose UI tests, build identity | Enjoyment or learning |
| **Usability** | Can unfamiliar people navigate the UI, locate clues, select and confirm areas, and correct mistakes without developer instructions? | Observations of first action, pauses, pan vs tap confusion, recovery, accessible controls | True long-term skill |
| **Enjoyment** | Does a short puzzle feel engaging, fairly challenging, and worth replaying? | Immediate feedback, voluntary next hunt, observed frustration/delight, neutral follow-up questions | Statistical population appeal from a few testers |
| **Skill reasoning** | Does success depend on reading grid references rather than copying a hint, guessing, or tapping an icon? | Explanation of method and **fresh** reference/map test, error types, hints used | Operational outdoor land navigation competence |

**Data interpretation rule:** a problem must be categorized before prescribing a fix. Don't “make the game easier” because a pinch gesture moved a pin, and don't “fix the art” if the clue is logically ambiguous.

## 2. External methodological evidence — sources and limits

| Source | Supported lesson | Grid Hunt application | Limit |
|---|---|---|---|
| [Nielsen Norman Group — Usability Testing 101](https://www.nngroup.com/articles/usability-testing-101/) | Realistic tasks, observation, neutral facilitator behavior and distinction between qualitative insight and quantitative benchmarking. Around five representative participants in a group often surface many common interface problems. | Small iterative novice sessions, with exact build/task recording. | **Five do not prove statistical superiority, retention or general enjoyment rates**. |
| [NN/g — Thinking Aloud](https://www.nngroup.com/articles/thinking-aloud-the-1-usability-tool/) | Participants verbalizing what they expect/attempt can reveal misunderstandings; prompting can bias behavior. | Let them speak, but don't teach “easting first” unless the game itself offers it. | Speaking during gameplay can change pace or attention; also observe unprompted play. |
| [Games User Research — Choose the Right Playtest Method](https://gamesuserresearch.com/choose-the-right-playtest-method/) | Observation reveals behaviors; interviews reveal interpreted motivations; surveys summarize perception with less explanation. | Combine watched play with post-hunt nonleading questions, not just an enjoyment number. | Practitioner methods guide, not Grid Hunt trial results. |
| [UCSC Games User Research — Testing Methods](https://gur.ucsc.edu/testing-methods) | Behavioral telemetry has limits for understanding player feelings and intentions. | Local optional event logs can support analysis, never replace interviews. | Doesn't require installing a cloud analytics service. |
| [MEEGA+ — Educational Games Evaluation Method (SBGames 2019)](https://www.sbgames.org/sbgames2019/files/papers/CTDDoutorado/195720.pdf) | Separates usability (learnability, operability, accessibility, aesthetics) from player experience (fun, challenge, satisfaction, focus, confidence, relevance and **perceived** learning). | Ask about game feel and comprehension separately; avoid claiming perceived learning proves transfer. | Developed for computing-education games, not validated as an exact Grid Hunt scoring instrument. |
| [Android Developers — Test Compose](https://developer.android.com/develop/ui/compose/testing) / [Gestures](https://developer.android.com/develop/ui/compose/touch-input/pointer-input/understand-gestures) | Compose semantics and automated touch-input testing can verify UI state/gestures, including `performTouchInput`. | Test tap/preview/check, pan/pinch cancellation, undo, accessibility semantics. | Emulator/automated successes don't establish real-phone finger feel. |
| [NN/g — How Many Test Users?](https://www.nngroup.com/articles/how-many-test-users/) | Iterative small qualitative studies help identify recurring problems and focus rework. | Repeat test after fixing defects; don't wait for a giant beta. | Participant count must account for distinct groups and study purpose. |

**No research above establishes a universal first-hunt length, 90% acceptance threshold, best button layout or Grid Hunt's real learning outcomes.** All counts, targets and gates below are planning proposals.

## 3. Core playtest design: the smallest worthwhile slice

**First GH-01 vertical slice** (already on the [roadmap](../ROADMAP.md)): one fictional north-up **6×6** 1km grid, 3 distinct valid four-digit target squares, readable grid-line labels, correct E-first/N-second coordinates, select/preview/Check/undo, contextual help, simple correct/wrong feedback, and an offline Android APK whose source and signature are known.

Begin **without** giant map art packages, reward economies, an adaptive trainer, a 100-map seed generator, 6-digit plotting, story branches, multiplayer or a real GPS/map SDK. Hold the math and initial map constant while testing the loop.

### Session phases

1. **Author technical audit:** automatic math/format fixtures, one deliberately wrong answer, geometry, pan/zoom round trips; creator play is debugging, **not** unbiased beginner evidence.
2. **Moderated novice round:** aim for ~4–6 people unfamiliar with project if feasible; capture observational defects. A knowledgeable map reader should independently review map-reading accuracy. Keep group insights distinct rather than merging them in a meaningless percentage.
3. **Owner actual-phone session:** installed source-matched signed QA APK; phone portrait/landscape if supported, airplane mode, map readability, one hand, unintended zoom/taps, resume. Note owner has design foreknowledge and is not a naive participant.
4. **Targeted variants:** compare one feature at a time, using comparable **fresh** references and counterbalanced order when practical.
5. **Follow-up retention/transfer:** later, optional new in-game map and distinct paper-style grid test after a documented interval. This is exploratory; no military field competence claim.

People with motor, vision or other accessibility needs should be invited when practicable; TalkBack and controls need real checks beyond a single informal test. Obtain permission for recording and use participant pseudonyms. No mandatory analytics upload/cloud account.

## 4. Actual first-session protocol

Use [the short GM08 session script](GM08_FIRST_PHONE_PLAYTEST_SCRIPT.md).

**Neutral opening:** “We're testing the puzzle game's design, not your ability. Try a short hunt. I won't walk you through the answers. Tell me anything confusing and you can stop any time.”

**Watch the following with minimum coaching:**
- Start the hunt, identify the current objective from screen alone.
- Attempt three different 4-digit cells, preview and confirm **whole areas**.
- Replace an uncommitted selection; correct a wrong submitted square.
- Pan/zoom and verify the marker's world cell does not change.
- Request a hint if stuck (do not encourage or discourage).
- Try a previously unseen reference and, when available, an unfamiliar map to see whether the player uses grid lines rather than remembered placement.
- After completion, observe whether the person **chooses** another hunt without being told to.

**Post-hunt questions:** “What helped you decide?”; “What was confusing?”; “When did it feel like a puzzle?”; “What would you remove?”; “Would you start another hunt, and why?” Avoid “Wasn't the discovery card fun?” and other leading wording.

**Think-aloud caveat:** some players struggle to narrate while concentrating; let them simply play, then ask nonleading questions after.

## 5. Error taxonomy: failures require different remedies

| Code | Example | Required interpretation / fix |
|---|---|---|
| **MATH** | `2437` resolves to the wrong 1km area; leading `0` lost | **Hard stop.** Fix canonical parser/validator and regression tests |
| **CONTENT** | Both bridges satisfy an alleged unique landmark clue | Reject/rewrite mission; add candidate cardinality check |
| **GESTURE** | Pinch creates phantom answer, drag selects when panning | Fix pointer arbitration and UI tests, **not** knowledge scoring |
| **VISUAL** | Northing line label hidden by artwork or ribbon | Fix contrast, anchoring, layout and map scale |
| **TEACHING** | Player uses E/N in reverse order repeatedly | Revise right/up cues, staged feedback; later test fresh transfer |
| **PUZZLE** | All players solve by tapping obvious prepainted cache icons | Remove answer leakage; require actual coordinate reasoning |
| **FLOW** | Repeated Check/reward modal is tedious | Compare auto-check vs preview and nonblocking rewards |
| **MOTIVATION** | No one wants another hunt; it feels like homework | Trial meaningful discovery or varied objective type, not merely badges |
| **ACCESS** | TalkBack has no semantic route to choose square | Implement accessible input/step alternative; retest |
| **TECH** | App freezes, install fails, save resets | Fix/reproduce, record device, APK signature, source SHA |

**Important:** a wrong cell is not automatically a conceptual failure. Record whether it arose from misunderstanding, inability to read a label, finger slip, faulty data or wrong question.

## 6. Proposed focused comparisons (NOT RUN)

| Question | A vs B | Controlled constants | What to observe |
|---|---|---|---|
| **Selection** (R06) | Tap→preview→Check vs direct auto-check | Same references/map/help | Accidental submits vs action friction |
| **Game feel** (GM01/GM06) | Classic Find vs one-sentence Mini Expedition | Same grid/math/difficulty | Enjoyment, task comprehension, one-more-hunt choice |
| **Feedback** (GM04) | Bare “Try again” vs actionable E/N suggestion | Same errors/tasks | Ability to correct independently, annoyance/spoilers |
| **Modes** (GM02) | Coordinate vs Reverse vs Landmark | Same authored map and comparable task count | Distinct thought and actions, confusion |
| **Precision** (R06) | Zoomed 10×10 subcell vs fixed reticle | Same valid 100m answer and zoom range | Finger accuracy, legibility, perceived fairness |
| **Menu** (GM07) | One-tap Quick vs optional preset picker | Same actual generated objectives | Time to first map, understanding, perceived control |
| **Generation** (R07 later) | Curated set vs seeded validated set | Same mechanic/difficulty labels | Repetition, invalid clues, replay interest |

**One independent factor per comparison**, test on source-matched playable artifacts; avoid reading an A/B preference from four people as a statistical win.

## 7. Capture useful evidence, not superficial numbers

Suggested per-session observation schema:

```text
sessionCode / date
sourceSHA / buildVariant / Android package+version
apkSHA256 / signing fingerprint / Android OS+model / display scale
mapId / mapRevision / objective IDs / variant / task familiarity
player category (novice, casual map reader, experienced), consent for recording
task and time stamp
observed action or quote (verbatim, nonidentifying)
submitted cell/ref vs expected semantic answer
hints requested / changed preview / committed attempts
error category / impact (blocking, severe, minor) / hypothesis
finish? / retry? / started optional extra round?
suggested fix and next source revision to retest
```

Use observational records to report meaningful patterns and contrasting incidents, not unsupported percentages. Optional **time-to-first-action**, task duration, first-correct on *fresh* tasks, errors, hints and voluntary replay observations may aid diagnosis. Do not collapse these into a made-up “fun score” or compare map veterans and beginners as equivalent samples.

## 8. Technical evidence and acceptance gates

### Hard stop before adding more content
- Any incorrect E/N order, floor/truncation, four-digit **1km area** or six-digit **100m area** math, leading zero, map-boundary semantics or feature-anchor truth.
- A mandatory puzzle with no supported answer or multiple valid answers under an allegedly unique clue.
- A crash/offline failure, irrecoverable progress loss, phantom marker after pinch/pan or inaccessible essential input (no suitable alternative).
- Inability to link installed APK to exact source, version, hash, signer and executed test results.

### Observe/revise before moving forward
- First-time players need a developer to interpret the objective despite built-in teaching.
- Players confuse panning with marking or repeatedly misread essential grid labels.
- Feedback is useless or gives away all answers before a second attempt.
- Players find the loop repetitive or tedious and do not voluntarily replay.
- Surprise or forced unlocks turn Custom/Quick into a confusing experience.

### Proposed decision rubric (not a validated numerical threshold)
- **FIX:** hard correctness/critical interaction errors exist.
- **REVISE AND RETEST:** repeated confusion or shallow/unsatisfying gameplay appears, even if math is correct.
- **EXPAND CAUTIOUSLY:** canonical math and mobile input are verified; fresh puzzles can be completed independently with optional support; participants find some joy in playing again. Use additional testers before generalizing appeal.
- **DO NOT SCALE GENERATION:** core loop still feels like a repetitive worksheet. Better puzzles first, more puzzles later.

A passing Kotlin unit test is **not** an Android phone playtest. A GitHub commit is **not** a compiled APK. Source-only animation/UX assertions aren't demonstrated human enjoyment.

## 9. Proposed test inventory (NONE EXECUTED)

| ID | Scenario | Intended evidence |
|---|---|---|
| T01 | Four-digit `2437`, including extreme boundary positions | One correct 1km area |
| T02 | Leading-zero reference, swapped E/N halves | Accurate formatting and error feedback |
| T03 | Preview then change selected cell | No committed wrong attempt |
| T04 | Wrong submitted cell then correct | Recoverable, truthful feedback |
| T05 | Two-finger pinch lifting unevenly | No phantom tap |
| T06 | Camera pan/zoom and rotation | Canonical selected area unchanged |
| T07 | Airplane mode cold start | No internet dependency |
| T08 | App background/resume | Same objective, answer and hint state |
| T09 | No external teaching on first hunt | Observe genuine independent comprehension |
| T10 | Visible hint / full reveal | Player can continue, no softlock |
| T11 | Very small/large font or TalkBack | Essential tasks accessible |
| T12 | Same map/targets, narrative A/B | Actual behavioral difference vs cosmetic effect |
| T13 | Same map, preview vs auto-check | Fewer input mistakes vs friction |
| T14 | Reverse task returns a four-digit string | Distinct reasoning vs selecting a cell |
| T15 | Landmark with two plausible candidates | Only one valid per semantic clue |
| T16 | Six-digit magnifier vs reticle | Correct 100m cell selected without pixel precision |
| T17 | Fresh map/coordinate after tutorial | Observe real axis reasoning rather than memorization |
| T18 | Player spontaneously selects another hunt | Evidence of desire to replay, no fabricated rate |
| T19 | Source SHA, APK signature/hash, device and actual test execution | Reproducible test result |
| T20 | Bug fixed and exact new build retested | Evidenced resolution, no “planned = passed” |

## 10. Handoff after the research

**GM01–GM08: research documented, not validated or built.** The existing [GH-01 roadmap](../ROADMAP.md) is a sensible initial *vertical-slice* target: offline Android app, one fictional 6×6 map, **three 4-digit Coordinate tasks**, actual world-to-screen grid math, forgiving selection, Check/Undo and clear feedback/hints. It should be small enough to test, rather than launching a vast procedural generator and collection economy first.

Next decision after GM08: either **build and verify GH-01** so research can be tested on the owner's Android phone, or complete the separately queued **R07 seeded objective generation design** first. We recommend testing GH-01 before extensive generator implementation. **Neither follow-up has been performed by GM08.**
