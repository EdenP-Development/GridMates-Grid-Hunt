# GM04 — Hints, Feedback, Scoring and Rewards

**Researched:** 2026-11-21  
**Status:** Source-backed research + TESTABLE DESIGN HYPOTHESES. **No game code, hint system, APK, rewards, QA or player tests implemented.**  
**Purpose:** Make GridMates: Grid Hunt a friendly puzzle game where players can recover from mistakes while optionally recognizing unassisted practice of real grid-navigation concepts.  
**Scope:** Hints, mistakes, feedback, completion, optional mastery, celebration and rewards. Monetization, chapter scheduling (GM05), actual puzzle generator (R07) and art production are separate.

## 1. Central conclusion

A good practice puzzle should provide **useful information after a mistake**, not only a red X. A hint should preferably help players take the next reasoning step rather than instantly place the answer. **Completion and independent practice are different facts:** people can complete a hunt after hints and deserve normal progress; separate optional stats can recognize a correct first attempt without assistance on a genuinely new problem. These are recommendations to test, not confirmed player preferences.

Grid Hunt's job is a **casual puzzle first**, with navigation skill rehearsal underneath. It is not an aptitude test, military certification, GPS product or competitive mobile economy.

## 2. External evidence and limitations

| Source | Direct support | Grid Hunt interpretation | Limitation |
|---|---|---|---|
| **Alice May Lynch (2024), PhD, University of St Andrews — [Effective player guidance in logic puzzles](https://research-portal.st-andrews.ac.uk/en/studentTheses/effective-player-guidance-in-logic-puzzles/)** | Across four studies on two progressive pen-and-paper puzzle types, guiding players toward easier deductions was similarly helpful to answer-filling hints, felt less like cheating, and was chosen significantly more often. | Try an **on-demand, graduated reasoning hint** before revealing the correct square or feature. | Not tested on spatial map-reading puzzles. |
| **[Digital instructional feedback meta-analysis (2024)](https://link.springer.com/article/10.1007/s10984-024-09501-4)** | Synthesis of 116 interventions across 46 articles showed moderate overall learning benefits; context/content/task interact, process-focused feedback can help, and simply supplying feedback does not ensure it is processed. | Offer precise, short correction that lets the player try again immediately; do not assume feedback itself establishes learning. | Educational digital learning, not specifically games or military skill retention. |
| **[Learning from errors and failure in educational contexts (2025)](https://pmc.ncbi.nlm.nih.gov/articles/PMC11803059/)** | Synthesis discusses interactive error feedback and reports research comparing supportive vs discouraging wording with more favorable reactions to supportive feedback. | Frame wrong guesses as normal attempts with constructive guidance, not shameful failure. | Covers differing learner ages and contexts; outcomes may not transfer directly. |
| **[Gamification motivation meta-analysis (2024)](https://link.springer.com/article/10.1007/s11423-023-10337-7)** | Gamification yielded gains in intrinsic motivation, autonomy and relatedness in studied settings, but competence gains were small; controlling rewards and competitive ranking can sometimes undermine needs. | Use badges/points sparingly and allow player choice rather than forcing competition or artificial pressure. | Education/gamification research, not a casual-puzzle preference survey. |
| **[2026 mathematics gamification review/meta-analysis](https://link.springer.com/article/10.1007/s10648-025-10108-1)** | Review identified 45 studies; 11 were meta-analyzed, finding a small-to-moderate motivation benefit; some studies linked low leaderboard placement, hard badges and competition to reduced engagement. | Do not make perfect runs, rank, streaks or clocks mandatory for ordinary chapters. | Mathematics education, substantial heterogeneity, not direct evidence about Grid Hunt. |
| **[Room2Educ8 (2022), escape room hint guidance](https://www.mdpi.com/2227-7102/12/11/768)** | Describes real design choices including limited hints, cooldowns, time penalties and free initial hints, and encourages transparent and incremental rules. | Such economies are **alternatives to compare**, not defaults. For relaxed solo play, test freely available help first. | Escape-room assessment goals differ from a replayable offline phone game. |

**Evidence distinction:** these sources substantiate design risks and options, NOT a universal scoring formula, ideal hint-tier count, proven military skill retention, or any Grid Hunt playtest results.

## 3. Separating four different events

1. **Finger slip while previewing:** no recorded mistake; player reselects freely.
2. **Wrong committed area or reference:** the validator reports the actual selected answer and gives optional meaningful correction; player may retry without losing access.
3. **Using help:** puzzle remains valid and completable, but optional independent-practice indicator is not awarded for that attempt.
4. **Finishing a hunt:** objectives solved, discoverable progress recorded regardless of assistance. Never mistake “completed” for “independently mastered.”

Correct answers always derive from world grid cells, point reference locations and feature IDs. A visually near-correct tap is not secretly awarded correct; fair screen controls and a clear preview solve finger imprecision.

## 4. Proposed free graduated hints

Hint stages are **design hypotheses**. Keep Help visible, readable, skippable and nonblocking. Offer **Reveal & Explain** as the final voluntary escape hatch; make it clear when it will show the answer.

| Hunt type | Method reminder | Narrow useful attention | Partial worked reasoning | Optional full reveal |
|---|---|---|---|---|
| **4-digit Coordinate** | “Read across/right, then up.” | Emphasize easting 24 line | Emphasize northing 37 line | Show square `2437`, explain SW origin |
| **Reverse Hunt** | “Use the lines just west and south of the marker.” | Highlight containing kilometre cell | Reveal first half of reference | Show full four digits and why |
| **Landmark Hunt** | “Check legend and spatial clue.” | Highlight applicable symbol category | Narrow choices using one verified relation | Show correct feature and compare alternatives |
| **6-digit Precision** | “First find the 4-digit parent.” | Show split `243 | 378` and parent `2437` | Reveal one axis tenth in 10×10 inset | Show correct 100 m cell and explain both parts |
| **Contour task (future)** | “Contour lines connect equal heights.” | Point to height labels or interval | Trace relevant contour relationship | Explain identifiable hill/valley/saddle |

Unlike a worksheet, the first hint is **not** a long lecture. Only show what helps the current mistake and preserve the player's ability to make the final reasoning move.

**Important:** An always-free help button is the default proposal. Don't require earning consumable hints, viewing ads, paying, losing lives or accepting a countdown to learn the mechanic. If designing paid optional content someday, research separately with user trust in mind.

## 5. Error feedback: task-specific and calm

| Failure | Better response candidate |
|---|---|
| Wrong easting, northing correct | “You found the right row; check the bottom easting number.” |
| Easting correct, northing wrong | “Right easting! Now check the northing going up.” |
| Wrong side of grid boundary | “That number labels the line. Choose the square up and right from its intersection.” |
| Leading zero omitted in Reverse | “Four-digit references need all four digits, including zeroes.” |
| Wrong bridge/feature | “This bridge crosses the road; the clue asks for the footpath crossing.” |
| Unclear/multiple-valid landmark clue | **Content bug**, reject task; don't blame player |

Make the first feedback useful without immediately disclosing the actual cell. Additional hints are voluntary. Don't use punitive sounds, forced “failure” popups or misleading precision.

## 6. Three candidate progression/reward approaches

**A — Calm completion (preferred first prototype)**  
No score as the focus. Each correct square/feature receives a small stamp or journal progress mark. All objectives solved → normal completion. Used hints? Still complete. No timer, lives, public rankings or streak penalties.

**B — Calm completion + optional independent-practice recognition**  
Same as A. Optional private “First attempt without hint” stamp on a **new and unassisted** reference, clearly described as a session statistic, not proof of outdoor navigation skill. Never gate chapters or punish help.

**C — Points and optional challenge performance**  
After studying player preference, try a distinct optional challenge mode with transparent scoring/time rules. Keep the normal game fully playable without the scoring pressure; don't mix assisted and unassisted leaderboard results deceptively.

**Research recommendation:** test A vs B first. C is later **only if** users actually want competition. More reward layers are not automatically more fun.

## 7. Rewards without noise

**Start minimal:** clear solved-square animation, progress counter (e.g. 3/5), and a brief end-of-hunt completion illustration. Offer optional muted sound/haptic.  
**Compare later:** tiny map discoveries or a collection journal, but avoid modal cards that interrupt every correct tap.  
**Defer:** currency stores, dozens of badges, punitive daily streaks, mandatory timers, competitive rankings, pay-for-hints, reward ads after every grid cell, and cosmetic farming loops.  
**Preserve meaningful goal:** the player should want the next puzzle for its new search/deduction, not because a badge meter demands daily attendance.

## 8. A/B playtests to conduct (NONE RUN)

Use the **same map, same map data and same objectives**, varying only the researched mechanic:

- **H-A (hints):** unlimited staged help vs visible limited-help count. Observe actual usage, hesitation to ask, mood, ability to solve a fresh puzzle afterward.
- **H-B (feedback):** “Not quite” vs specific actionable easting/northing correction. Observe repeated error type, whether correction gives away too much, and player preference.
- **H-C (reward):** simple completed-cell stamp vs stamp + short optional discovery reveal. Observe whether player voluntarily starts another round and whether reveal gets skipped.
- **H-D (mastery UI):** completion only vs completion plus *optional* independent-first-attempt mark. Observe whether users avoid hints due to perceived pressure.
- Include novices and map-literate players, counterbalance order, record actions + quotes, don't invent preferred variant or completion rates.

Ask: “Did that help you figure it out?”, “Did you feel comfortable requesting help?”, “What felt rewarding?”, “Would you play another one?”, “Did any score make mistakes feel worse?”

## 9. Proposed QA cases — UNEXECUTED

| ID | Scenario | Expected |
|---|---|---|
| H01 | Preview incorrect cell, then change | No wrong attempt or hidden penalty |
| H02 | Commit wrong easting | Task-appropriate correction and retry |
| H03 | Commit wrong northing | Correct diagnosis; retry |
| H04 | Correct semantic cell despite not center tapping | Correct; no pixel bullseye |
| H05 | Mixed up axes | Axis-order explanation, not automatic answer |
| H06 | Request increasing hints | Deterministic stage; final reveal explicit |
| H07 | Reveal then solve | Normal completion, not independent first attempt |
| H08 | First unassisted correct submission on new task | Eligible for optional private indicator |
| H09 | Multiple wrong submissions then success | Completed normally, no “mastered” claim |
| H10 | Leading-zero Reverse answer | Four/six-digit strings preserved |
| H11 | Restart/resume after backgrounding | Help usage, selection and progress retained |
| H12 | Changing artwork/zoom/density | Same canonical answer and clue |
| H13 | Landmark clue has two valid answers | Reject puzzle as authoring bug |
| H14 | Accessible input + screen reader | Hints and feedback announced without requiring pinch |
| H15 | Optional sounds/animations disabled | Never blocks gameplay |
| H16 | Relaxed campaign no timer | No time expiration or fail-state pressure |
| H17 | Full help used on later chapter | Still permits normal completion and next chapter |
| H18 | Independent indicator on previously revealed task | Not misrepresented as new unassisted evidence |

Do not report these as passed until a real test suite runs on source-matched APK/core logic and physical Android input is verified.

## 10. What is decided vs what is open

**Recommended for comparison, NOT approved:** free on-demand reasoning hints, preview-before-commit, descriptive wrong-answer feedback, normal completion with help, subtle celebrations and optional private independent-first-attempt recognition.

**Open:** exact number of hints, auto-check vs Check button (R06), whether score is visible at all, wording/timing of feedback, journal/theme rewards, badges, optional timed challenges and multiplayer competition.

**Next focused subject: GM05 — How to layer and revisit skills over chapters to build enjoyable practice and investigate possible retention.** GM04 research is *documentation only*, not an implemented hint/reward feature.
