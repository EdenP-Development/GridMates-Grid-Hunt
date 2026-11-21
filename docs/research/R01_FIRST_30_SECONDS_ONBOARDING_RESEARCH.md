# R01 — First 30 Seconds: Beginner Onboarding Research

**Date:** 2026-11-21  
**Status:** RESEARCHED / DESIGN HYPOTHESIS — **not implemented, playtested, or approved**.  
**Research question:** Can a first-time player start an authentic four-digit grid hunt within roughly 30 seconds and find their first correct grid square without encountering an intimidating military-style interface?

**Scope:** the opening experience **only**. Map rendering techniques, contour lines, full MGRS conversion, procedural world generation and unlock economy are separately tracked in [the research backlog](OPEN_DESIGN_RESEARCH_BACKLOG.md).

## 1. Owner-established intent

- Grid Hunt should feel like a casual puzzle **game**, not Google Maps, a military simulator or a land-navigation qualification.
- Gradual difficulty begins with selecting the correct **grid square**, not precise pin placement.
- Authentic map reading is the underlying skill: easting first, northing second, later 100 m plotting, terrain, contour interpretation and mixed missions.
- Art can be friendly and simplified. The map math and feedback must be correct.
- Campaign and Custom Hunt are both desirable, but an intimidating setup screen is not the opening experience.

## 2. What external sources actually support

| Evidence | Source | Design implication | Limitation |
|---|---|---|---|
| Android design guidance favors contextual/just-in-time onboarding where a product can show its value immediately, and advises considering whether a full walkthrough is needed at all. | [Android Developers — Authentication & Onboarding](https://developer.android.com/design/ui/mobile/guides/patterns/onboarding), last updated May 19, 2026. | Launch straight into an understandable demonstration/puzzle, with short help near the action. | General app UX, **not** a trial of Grid Hunt or evidence for a precise 30-second completion threshold. |
| Progressive disclosure presents the few important controls initially and makes advanced options available when requested. | [Nielsen Norman Group — Progressive Disclosure](https://www.nngroup.com/articles/progressive-disclosure/) | Avoid showing maps settings, grid precision selector, MGRS glossaries or Custom Hunt on the first playable screen. | General usability design guidance; still requires app-specific usability tests. |
| Contextual help should be easy to dismiss/return to, avoid requiring remembered instructions and appear where/when needed. | [Nielsen Norman Group — Onboarding Tutorials vs. Contextual Help](https://www.nngroup.com/articles/onboarding-tutorials/) | Keep "right then up" and a small demo adjacent to the map; let users replay hints. | Assumes good knowledge of current player intent; avoid intrusive unsolicited overlays. |
| Worked examples and progressively faded guidance can help novices transition to independent problem solving. | [PLOS Computational Biology — Ten quick tips for creating an effective lesson](https://journals.plos.org/ploscompbiol/article?id=10.1371/journal.pcbi.1006915), section 7; also section 8 on diagnostic feedback. | Show first easting/northing line, then remove one guide, then ask for an unaided square; explain errors constructively. | A cross-domain educational synthesis, **not** proof of permanent military skill retention from games. |
| Game user research should define concrete tasks and distinguish observations from designer opinion. | [Steve Bromley, Games User Research — Tips for new games user researchers](https://gamesuserresearch.com/tips-for-new-games-user-researchers/) | Observe whether players know what to do, find a square, and explain their reasoning, rather than asking only whether they "had fun." | Practical games research guidance, not a published result for this prototype. |

**Technical accuracy reference:** UK Royal School of Military Survey *Military Map Reading* (2010) printed pp. 3–5, included in [annotated source notes](MILITARY_MAP_READING_2010_SOURCE_NOTES.md), describes easting-before-northing, four-figure grid squares and progressive references. As a historical **British** reference, it does not replace modern NGA/USGS/FGDC checks for any actual MGRS conversion. See [accuracy specification](../MAP_READING_ACCURACY.md).

## 3. Proposed default: first contact should be a puzzle, not a setup form

**HYPOTHESIS A — "Find the Lost Camp"** (working prompt; content title not locked)

### Before the first tap
- Home screen: clear **Play** or **Start Hunting** action. The player may revisit a visible **How to Play**, but should not need account, profile, registration, permissions, map download or difficulty selection.
- First screen: small north-up fictional woodland map showing ~4×4 **1 km squares**, visible easting/northing labels and a few carefully placed feature/distractor symbols.
- Short prompt: **"Find grid 2437"**. Tiny contextual line: **"Across to 24. Up to 37."**
- An optional animated trace may briefly follow easting 24 then northing 37, ending at the **southwest corner of the requested square**. Do not highlight the final cell continuously if we want the player to practice finding it.
- Only essential controls: map, objective, accessible tap/selection, **Check**, and **Hint** / **Undo**.

### First successful selection
- Accept **any point inside the correct 1 km cell**; do not penalize for not tapping the center or a specific landmark graphic.
- Reveal a short, calm confirmation and highlight the selected square.
- If incorrect, allow correction immediately with a specific hint (e.g. "You found 24 across. Now check 37 up.") instead of punitive grading.
- Keep the optional explanation ("That was a four-digit grid reference") after the win, not as a wall of text before the first move.

### Second and third puzzles
- Second: show both axis labels but reduce the animated trace or show only the first hint.
- Third: player gets a new grid square with no compulsory hint; "?" remains available on demand.
- A **different** target coordinate and distractor layout make memorizing the single square insufficient.

### Avoid a false positive
If the player merely taps the only visible tent and wins, they have not demonstrated grid reading. The puzzle must make the **grid reference**, rather than recognition of a unique oversized icon, the reliable way to choose the answer. Use several feature categories or non-target landmarks, and test with targets on cells containing no unique icon as needed.

## 4. Alternative approaches worth comparing (not yet tested)

| Candidate | Player sees | Benefit | Risk |
|---|---|---|---|
| **A. Embedded microtutorial** — proposed default | Play screen + short right/up cue, one action | Fast first puzzle; instructions located where useful | Could be unclear without one explicit worked demonstration |
| **B. Two-step guided trace** | Select easting line, then northing line, then square | Makes correct order unmistakable and catches specific errors | May feel slow/over-scripted to anyone already familiar with coordinates |
| **C. Tutorial slides** | Separate explanation screens before first puzzle | Easy to describe all controls | Creates reading burden, delays fun; least aligned with Android contextual-onboarding guidance |

**Recommendation:** prototype **A and B** as small variants. Do not create a complex tutorial framework or a full art series to run this comparison.

## 5. A proposed 30-second journey — DESIGN TARGET, not an established user metric

| Approximate moment | Intended action | What we learn |
|---|---|---|
| 0–5 s | Launch/Start; see a friendly map with a single objective | Is it immediately recognizable as a puzzle? |
| 5–10 s | Notice "Find grid 2437" and contextual right/up cue | Can the person identify the task without knowing the word MGRS? |
| 10–25 s | Examine lines and choose a square | Can the user interpret easting/northing, rather than guessing at an icon? |
| 25–30+ s | Confirm result / adjust and try again | Is feedback constructive? Do they want another puzzle? |

**The 30-second target is a design hypothesis.** Do not call it a researched usability standard; if actual novice results show more time is needed without confusion, optimize clarity over an arbitrary stopwatch.

## 6. Four-digit correctness guardrails

- Labels increase east/right and north/up; grid line labels denote each cell's **southwest** origin.
- Numeric 4-digit training reference `2437` identifies the area E [24,000,25,000), N [37,000,38,000) in the fictional 100 km training square.
- The map must be **correctly labeled**, not merely drawn as a 4×4 letter game.
- Correct area selection ignores exact pixel tap position inside the square.
- A gentle illustrated map is fine; do not call these numeric-only local references complete real-world MGRS.

## 7. Concrete test plan after low-fidelity prototype exists

**Sample:** ideally five or more first-time adults unfamiliar with map grid reading, plus at least one player with navigation experience as an expert-friction check. This is a *formative usability sample*, not a statistically representative learning study.

**Instructions to tester:** "Try this new puzzle game." Don't pre-explain grid semantics, introduce military terminology, or reveal the intended path. Observe without coaching unless the app's own hint system handles it.

**Record per tester:**
- Time to first meaningful map action and first **correct** selected cell; where any pause occurred.
- Whether they choose from the displayed numeric reference or only chase a landmark icon.
- Mistakes: easting/northing swapped, wrong corner of square, map pan mistaken for tap, or lack of visible controls.
- Number/type of hints used; whether Help is discoverable and dismissible.
- Whether they can explain **"across/easting first, then up/northing"** after the third map.
- Whether they elect to do a fourth puzzle unprompted; brief reported confusion/frustration.

**Provisional prototype gate** (go/no-go design targets to revise after first tests): at least 4/5 novices understand the objective without verbal coaching; at least 4/5 correctly find a new square by the end of their third exercise with app-provided hints as needed; no accidental tap/zoom blockers; no player is forced to read multi-page instructions.

**Do not claim real-life or long-term proficiency** on the basis of this test. Longitudinal skill retention and paper-map transfer are separate research questions (R18).

## 8. Design and engineering handoff

- Keep a single simple training-grid renderer and objective validator; this R01 report does **not** approve a map asset style (that is R04).
- The first screen needs the same canonical square logic whether the map is code-drawn, illustrated, or hybrid.
- Until user testing, report **"recommended" / "hypothesis"**, not **"validated" / "player-approved."**
- Maintain a dedicated playable **first-puzzle path**, then later allow experienced players to skip tutorials or access custom choices.

## Outcome and next research item

**R01 finding:** contextual in-map guidance + gradually reduced hints is a credible **first prototype hypothesis** with support from general UI/learning research. No Grid Hunt-specific evidence yet.

**Next (not researched here):** **R02 — What makes Coordinate Hunt satisfying and repeatable?** Compare moment-to-moment objective design, feedback, mission variation and player motivation separately, rather than mixing that topic into this onboarding report.