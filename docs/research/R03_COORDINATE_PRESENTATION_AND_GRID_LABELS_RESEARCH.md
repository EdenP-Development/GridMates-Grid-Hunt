# R03 — Teaching Grid References Without Intimidating New Players

**Research date:** 2026-11-21  
**Question:** How should Grid Hunt present numeric coordinate clues, grid-line labels, feedback and early precision so a casual player understands the puzzle **and** learns correct military-style grid reading?  
**Status:** RESEARCH COMPLETE AS A DOCUMENT; UI HYPOTHESES, PROTOTYPE AND PLAYER VERIFICATION OPEN. No app, screens, user trial or instructional outcome is claimed.  
**Scope:** grid-reference communication and beginner visual grammar only; artistic map pipeline is **R04**, object placement accuracy is **R05**, touch interaction is **R06**, and procedural missions are **R07**.

## 1. Decision context

The owner explicitly wants **a welcoming puzzle game first**, starting with easy grid-square hunts and then gradually adding reverse plotting, greater precision, map symbols and contour reasoning. A first-time player should not be faced with an intimidating military map, unexplained acronyms or a complex settings screen.

This study asks which **information should be visible, when, where and in what representation**, *without changing the authentic coordinate rules*.

## 2. Grounded technical references

| Source | What it directly supports | Application in Grid Hunt |
|---|---|---|
| [FGDC — How to Read a USNG Spatial Address](https://www.fgdc.gov/usng/how-to-read-usng) | Numeric local coordinates use **right/easting then up/northing**, equal-length component halves; four digits identify 1 km areas, six digits 100 m, eight digits 10 m; full address has zone + 100 km square + local digits. | Use real digit order and keep **numeric-only fictional training references** explicitly map-contextual rather than calling them worldwide positions. |
| [USGS — How UTM coordinates are measured on topographic maps](https://www.usgs.gov/faqs/how-are-utm-coordinates-measured-usgs-topographic-maps) | Map-side 1,000 m ticks/grid lines, measuring subdivisions into tenths (100 m) or hundredths (10 m), abbreviated map-margin labels and 100 km square conventions. | Render grid numbers on actual **lines**, interpolate correctly, and use a precision tool only when needed. |
| [NGA — Coordinate Systems / MGRS](https://earth-info.nga.mil/?action=coordsys&dir=coordsys) | Official reference context for fully qualified MGRS and grid-zone/100 km identifiers; no invented real-world locations. | Later standards validation. Our first training grid is *MGRS-style numeric practice*, not real georeferenced MGRS. |
| [NN/g — Onboarding Tutorials vs. Contextual Help](https://www.nngroup.com/articles/onboarding-tutorials/) | Instructions shown out of context are hard to recall; user-controlled, dismissible, in-context help is often preferable; reveal complexity progressively. | Show a short right/up prompt **on the map** when needed; don't force a lengthy slideshow. |
| [NN/g — Recognition and Recall](https://www.nngroup.com/articles/recognition-and-recall/) | Visible cues and labels reduce the need to retrieve unfamiliar operations from memory. | Leave coordinate and axis cues visible in early puzzles, with assistance later fading to a help button. |
| [NN/g — Error-Message Guidelines](https://www.nngroup.com/articles/error-message-guidelines/) | Corrections should be specific, constructive and respect user effort. | Differentiate wrong easting, wrong northing and a mistap, rather than flashing only "Wrong!". |
| [Android — Make apps more accessible](https://developer.android.com/guide/topics/ui/accessibility/apps) | Interactive touch targets should be at least **48 dp** when applicable; strong text contrast and meaningful accessibility descriptions. | Plotting must not require microscopic tap accuracy. Keep UI controls accessible and grid labels legible. |
| [Android — Compose Semantics](https://developer.android.com/develop/ui/compose/accessibility/semantics) | Custom Canvas UI needs explicit semantics/accessibility strategy for interactive elements. | Map cells need meaningful focus/selection alternatives and accessible descriptions; visual Canvas alone isn't enough. |

**Source distinction:** FGDC/USGS/NGA describe technical geography; NN/g and Android inform interface guidance. **None** demonstrates that a particular Grid Hunt clue design is intuitively understandable or teaches durable skill — this is the experiment to run.

### Historical source alignment
The owner-provided **UK Royal School of Military Survey, *Military Map Reading* (2010), printed pp. 3–7**, treats eastings before northings and illustrates 4- and 6-figure grid references. See [annotated source notes](MILITARY_MAP_READING_2010_SOURCE_NOTES.md). Its historical British examples do not override official present-day references; it is not labeled a US Army manual.

## 3. Essential map grammar — what MUST remain technically correct

**Reference:** Within one fictional training 100 km square, numeric reference `2437` identifies the **whole 1 km square** whose southwest corner is the intersection of **easting 24**, **northing 37**. With local meters, the area is E [24,000,25,000), N [37,000,38,000).

To find it:
1. **Across/right:** follow the bottom (or an accessible floating axis) to the vertical **easting grid line 24**.
2. **Up:** find the horizontal **northing grid line 37**.
3. **Select:** the **square immediately northeast (above and right) of that intersection**, not the line intersection alone and not an arbitrary center point.

This must work whether a cell is empty, wooded or decorated with a cabin icon. A landmark image does NOT replace the whole-square target.

**Six-digit later:** `243378` = easting `243` plus northing `378` = E [24,300,24,400), N [37,800,37,900), within the four-digit `2437` cell. Interpret the third easting/northing digits as **tenths within each 1 km cell**, measured from its southwest origin. Do not scatter a 6-digit target anywhere inside `2437` or round a feature's location to its nearest cell.

**Leading-zero fixture:** `0000` and `004008` are legitimate local numeric training components, corresponding respectively to an initial 1 km square and E [400,500), N [800,900) for 6 digits. An input field must not strip or omit zeroes.

**World context:** these examples use an abstract **training** square and are *not complete GPS-ready MGRS/USNG references*. If we ever display a genuine real-world map, include verified zone, 100 km identifier and datum/projection.

## 4. Player-facing clue progression: friendly display, unchanged rules

| Stage | Actual player-facing clue (hypothesis) | Guidance at that stage | Technical truth |
|---|---|---|---|
| First round | **Find square 24 • 37** with little **→ then ↑** labels | Animated arrow traces to **easting 24**, then **northing 37**; finally highlights the correct *type of area* without continually revealing the target | Two components are still easting 24/northing 37 |
| Early independent rounds | **24 | 37** and one small reusable "**Right, then up**" hint chip | Axis labels are always visible; no trace unless player asks | Same four-digit `2437` |
| Normal 4-digit play | **2437** | Optional hint splits as `24 | 37`; the map itself still has line labels | Standard compact numeric reference |
| First 6-digit lesson | **243378**, with optional `243 | 378` view | Select parent `2437` cell and then `3` tenths east/`8` tenths north on a zoomed 10×10 inset | One 100 m **cell**, not an exact pixel |
| Later mixed missions | `243378` or target landmark to reverse-read | Help/legend available, not always front and center | Original correct representation preserved |

**Key nuance:** showing `24 • 37` is a **temporary display teaching aid**, **not** a new coordinate format. Show the standard `2437` alongside or soon after the assisted variant, so the player's skill transfers to ordinary map references. Don't label a cell with "24" at its center; label the corresponding **vertical boundary line**. Same for northing.

## 5. Visual layout hypotheses to test in R04/R06 later

### First small training map
- 4×4 or 5×5 visible **1 km squares** with a bounded but nontrivial set of eastings (e.g. lines `22,23,24,25,26`) and northings (e.g. `35,36,37,38,39`). All target cells must be fully visible.
- Major grid lines remain clearly distinct from background roads, streams and decorations. No arbitrary row letters or top-down row numbering.
- Easting labels align to **vertical grid lines** at bottom edge; northing labels align to **horizontal grid lines** on left edge. A north indicator is present but unobtrusive.
- When the grid pans/zooms, axis labels should remain associated with correct **world** grid lines; "floating edge labels" must follow their corresponding lines on screen, never be raw viewport row numbers.
- The objective card is always readable without covering the target; labels need sufficient text size/contrast; no tap target should be occluded by decorative assets.
- When a player selects a square, highlight **its whole area** with a visible border/fill and provide an action to correct it. The landmark symbol need not be at center.

### Clues and hints
- **Plain-language default:** "Find square 2437" and optional "Go right to 24. Go up to 37. Choose the square above-right."
- A two-step help tool can highlight **easting line** and then **northing line**, and show the square relative to the intersection if requested.
- **No big MGRS slide** or protractor on first entry. The formal vocabulary is optional and introduced progressively, e.g. after a success: "Those are called eastings and northings."
- A wrong-square response should help locate the error without immediately exposing the next correct square: e.g. "Check the northing (up/down) value." Stronger hints available intentionally.
- Clues should remain short; lengthier explanations are accessible from Help at any time.

## 6. Design alternatives still open

**A. Labels at map edges (preferred initial test):** traditional numbered easting/northing boundaries; easiest to align with real map reading.

**B. "Guided intersection" variant:** first identify the requested vertical grid line, then horizontal, then choose the NE square; clearer causal learning but may add too many taps and feel like a worksheet.

**C. Adjacent coordinate decomposition ribbon:** show a short `2437 = 24 / 37` mini-strip above map; lets players see components without prolonged overlays; may create extra interface clutter.

Compare A and C on the *same* map, same reference and same tap mechanics. B can be a **hint**, rather than every-round required interaction. This preserves puzzle flow.

## 7. Mistake taxonomy and response policy

| Error | Likely learner difficulty | Support without giving away answer instantly |
|---|---|---|
| Swapped eastings and northings | Reading `37` right and `24` up | "Start with the two digits for right/across." |
| Selected square **west/south** of correct intersection | Confusing line number with neighboring cell | "The square begins at the grid lines you found and extends up and right." |
| Off by one line | Not reading nearest **southwest** grid boundary | Highlight the numbered boundary line after hint request. |
| Leading zero omitted | Treating numeric reference as a number rather than digit string | Show zeros as significant characters; use text-like digit slots. |
| Missed tap near boundary | Touch rather than knowledge problem | Offer zoom, undo, large selection aids; do not award punitive wrong-answer grade for finger slip. |
| 6-digit digits grouped as `24|3378` | Not understanding equal easting/northing halves | On request, show `243 | 378` and the corresponding 10×10 inset. |

In tests distinguish **conceptual misunderstanding** from **interaction errors**. A test participant who understands `2437` but cannot tap a 10 px target has not failed map-reading.

## 8. Accessibility and touch rules

- Follow Android's **48 dp target** guidance for standalone touch controls. Grid cells smaller than this need another method: zoom, accessible coordinate-entry option or reticle; do **not** change canonical cell geometry just to enlarge touch-hit areas across boundaries.
- Noncolor-only selected state: border/shape plus optional check, not just green fill.
- Strong text contrast; retain labels as text or provide an accessible equivalent. Canvas-drawn grid lines/labels won't automatically become screen-reader-interactive objects.
- Provide sensible semantic actions/labels: "Grid square easting 24, northing 37, selected", plus change/confirm; avoid forcing screen readers to traverse thousands of decorative cells.
- First mode ideally supports tapping the whole 1 km square. Six-digit mode needs precision picker or magnification; don't confuse increased precision with making tap targets smaller and smaller.

## 9. Test plan before adopting a permanent design

**Participants:** 5 novice players (illustrative formative target, not a statistically representative sample) and one experienced navigation reader.

**Use a mock map with exact, independent answer key.** Compare:
- Variant A: `2437` plus optional "right then up" help.
- Variant C: `24 | 37` together with standard `2437`, same map and help access.

**Observe:** correct/incorrect easting/northing decoding; which side of crossing players select; first-action and puzzle time; number of hints; clutter reports; leading-zero handling; ability to solve a new square unassisted; whether the cue makes the hunt too easy or interrupts play. Counterbalance variant order.

**Preliminary pass criteria — proposed, not measured:**
1. The learner can explain why `2437` means the NE square from 24/37 and uses the same method on an **unseen** labeled map.
2. The first hint is discoverable without hiding map content; player can remove it and try again.
3. The selected cell matches the engine answer at all zooms; errors near lines are handled fairly.
4. A beginner can play without knowing military acronyms, but an experienced player does not spot incorrect grid conventions.
5. At least 4 of 5 first-time players complete the third independent 4-digit square using in-app guidance only, as an *experiment goal*.

Add future tests for six-digit 100 m reticle, leading-zero digit slots, large text and accessible equivalent selection.

## 10. Outcome and next research

**Recommended prototype hypothesis:** Use authentic numbered grid **lines** from the beginning, paired with a very small, optional clue decomposition. Teach "right then up" through the player's first real square selection. Move gradually from split `24 | 37` to `2437`, then introduce six-digit `243 | 378` using a zoomable 10×10 subdivision. The game continues to judge **areas**, not points.

**R03 state:** source-backed design research completed, **unvalidated in Grid Hunt**. Do not mark clue UI, tutorial, map renderer or accessibility as built.

**Next focused research:** **R04 — map production comparison** (code-drawn vs illustrated vs hybrid), with specific production costs, accurate asset anchors and lightweight prototyping before art direction is approved. Related **R05** (map symbol/feature placement) remains a separate deeper investigation.