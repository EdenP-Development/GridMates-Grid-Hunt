# GridMates: Grid Hunt — Campaign, Puzzle Modes & Product Blueprint

**Owner direction captured:** 2026-11-23  
**Status:** APPROVED DIRECTION; four standalone woodland puzzle families, symbol Map Guide, GH-V11 clue fairness core, and the first four-stage **Mixed Expedition** chapter are now IMPLEMENTED and emulator-verified. Full multi-chapter campaign navigation, Custom Play, new worlds and a cohesive UI/art audit remain planned.  
**Purpose:** Durable product vision, practical build order, and chat-to-chat handoff. Read this before starting any new gameplay/content/art phase.

> **Design promise:** *Grid Hunt is a welcoming, replayable, native Android map-puzzle adventure. Coordinates are the underlying rule system; discovery, recognition, treasure hunting, and mixed challenges are what make it a game.*

## Read this first — current handoff (2026-11-23)

- **Repository:** [PlayerEden/GridMates-Grid-Hunt](https://github.com/PlayerEden/GridMates-Grid-Hunt).
- **Active development:** draft [PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12), branch `feature/gh-v02-in-puzzle-map-look` → `main`. **Not merged.**
- **Last fully green game build:** [QA146 Android CI](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810), game source SHA `351ca9bb138998218fb758c63e33c1c4e86e03c6`, passed core/APK/signature checks and executed emulator games plus **15** screenshot gates. [QA146 APK ZIP](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810/artifacts/14877122779). Public **test-only** QA signer; documentation-only commits may follow.
- **Actually implemented:** Kotlin/Jetpack Compose Android app, pure-Kotlin four-digit training grid and validator; touch map/camera; one authored Little Woodland map; 3 Trail Treasures mini-trails = **9 square-finding objectives**; **5 Golden Spots** reverse-reading objectives; **first authored 4-stage Mixed Expedition** combining square taps, golden marker reading, object coordinates and picture identification; **four-question What's at This Grid?** identification mode with four illustrated choices per question, wrong-answer prevention, separate saved progress, earned finish/replay; **four-item Treasure Checklist** using canonically anchored Mushroom Nook, Buttercup Glade, Acorn Nook and Old Waystone; **optional illustrated Map Guide and matching active-target preview**, both drawn with the same reusable artwork as the map; Cozy Woods/Cool Sketch appearances; hints, isolated local progress, golden discovery stamps, finish/replay cards; automated Android UI/visual tests.
- **Actually tested on a real phone:** owner-provided QA72 and QA98 screenshots reviewed (including Treasure Checklist). The newer QA113 Map Guide, QA132 Identify and **QA146 Mixed Expedition** have **not** received physical-phone acceptance; check game feel, map size, dialogs, field entry, orientation and install-over. See [GH-V13 QA checkpoint](vision/GH_V13_MIXED_EXPEDITION_VERTICAL_SLICE.md).
- **What is NOT built yet:** a larger object/building catalogue (four woodland landmarks only), any three-church map, geometry-verified creek/road or elevation clues, distinct world maps, multi-chapter campaign navigation/mission registry, additional authored hybrid chapters, Quick/Custom Play, seeded content generation, meaningful rewards, polished unified game hub and production signing.
- **Next action:** physically test **QA146 Mixed Expedition** and prior QA113/QA132 UI: map selection, numeric input, picture choices, guide, zoom, completion/replay, resuming, portrait/landscape and installer behavior. After fixes, organize **campaign navigation** (Chapters / Quick Play, later Custom) so Home doesn't become an endless stack of mode cards. [GH-V13 checkpoint](vision/GH_V13_MIXED_EXPEDITION_VERTICAL_SLICE.md) records the exact green CI and future constraints.
- **Continuity rule:** after each milestone update this handoff, the mode/phase status table, source SHA, CI run, APK link, owner findings, and next blocking action. A chat summary is not a substitute for repository status.

**Map-symbol and clue fairness contract:** [Map Guide, Symbol Meanings & Fair Clue Design](PUZZLE_SYMBOLS_CLUE_FAIRNESS.md). Written after the owner's QA98 screenshot review: players need a guide for tiny landmark symbols, and future multiple same-category objects require genuinely unique, data-backed relationship clues. Read this before building a second object-based mode.\n\nThis blueprint expands [Master Game Design](MASTER_GAME_DESIGN.md), [Puzzle & Content System](PUZZLE_AND_CONTENT_SYSTEM.md), [Roadmap](ROADMAP.md), [Map Accuracy](MAP_READING_ACCURACY.md), and the [GM01–GM08 gameplay research](research/GAMEPLAY_MECHANICS_RESEARCH_TRACK.md). The **new owner vision here takes precedence for future mode/chapter and product-experience direction**; older prototype status text elsewhere may describe historical states.

## 1. Locked product principles

1. **A game, not a military or classroom app.** Keep authentic grid-coordinate logic, but present it as a curious, cozy puzzle adventure. No orders, examinations, tactical/operational claims, or forced lessons.
2. **Start simple, then vary the thinking.** The first four-digit puzzles should stay approachable. Later play changes *what the player does* and combines skills, instead of merely repeating harder numbers.
3. **Word Search–like satisfaction.** A visible checklist of several discoveries, immediate marking-off, and a satisfying complete-board moment are central inspirations—not a literal copy of Word Search's UI.
4. **The map is the hero.** Map-first play, clear coordinates/landmarks, forgiving pan/zoom, high legibility, and compact objective controls. Avoid burying the actual game under giant forms or endless scrolling.
5. **Premium-feeling native Android interface.** Compose is already native, but future UI should feel designed as a polished Play Store puzzle game, not a browser page wrapped into an APK.
6. **Distinct within GridMates.** Recognizably friendly/offline-first/cohesive, without reusing the same generic cards, navigation surfaces, or visual treatment from Sudoku, Word Search, or Crossroads.
7. **Assets when needed, not in bulk.** First organize mechanics, content contracts, and flows. Produce a specific sprite, marker, map component, icon, or animation only when it solves a demonstrated design/legibility problem; later conduct a cohesive asset-library pass.
8. **Practical scope.** Offline solo first; no required account, network, GPS, timers, monetization, multiplayer, or enormous procedural world in this phase.
9. **Truthful QA.** Separate DESIGNED, IMPLEMENTED, CI-TESTED, PHONE-TESTED and RELEASED. Never claim a feature is complete because it has a mockup or a GitHub commit.

## 2. Mode families — design target, not finished feature list

| Family / working name | Player sees | Player action and win condition | Proposed priority |
| --- | --- | --- | --- |
| **Trail Treasures** (existing) | A four-digit grid-square reference | Tap/adjust the matching square, check and progress | IMPLEMENTED, improve presentation |
| **Golden Spots** (existing) | A clearly highlighted point on the map | Read/type its containing four-digit square | IMPLEMENTED, improve presentation |
| **Treasure Checklist** (first pilot BUILT) | Four existing woodland illustrations with a named 4-item list | Enter each distinct object's four-digit cell; correct answers cross items off and reveal a 4/4 finish | **IMPLEMENTED + QA98 CI PASS; PHONE TEST PENDING** |
| **What's at This Grid?** (FIRST PILOT BUILT) | A 4-digit coordinate reference and four existing woodland features | Choose the matching picture/name tile, confirm, advance through 4 authored questions; replay at 4/4 | **IMPLEMENTED / QA132 EMULATOR PASS; PHONE QA PENDING** |
| **Clue Chain** (NEW) | A location-based clue whose answer reveals the next clue | Mix find-square, identify-object and reverse-read objectives in a fixed order; reveal only the next valid stage | After standalone checklist/identify modes |
| **Route / Trailblazer** (CANDIDATE) | Start/end/landmark clues and possible paths | Trace or choose valid waypoints/route; optional obstacles with clearly authored rules | Evaluate later; don't promise pathfinding yet |
| **Mixed Expedition** (FIRST PILOT BUILT) | Four ordered authored tasks in one woodland story | Tap square → read marker → find landmark coordinates → identify object; finish/replay | **GH-V13 QA146 GREEN; one chapter only, phone QA pending** |
| **Precision Hunt** (CANDIDATE) | A properly zoomable six-digit/100 m task | Correctly locate or read the finer precision reference with a deliberate precision control | Later, only after usability/accuracy testing |

**Not every family needs a permanent top-level menu.** Chapter missions may combine puzzle verbs; Quick Play can offer sensible presets; Custom Play can choose from compatible families. Avoid a cluttered mode picker.

### 2.1 Treasure Checklist: example play contract

- **Scenario:** 'The woodland camp is missing supplies.' Task list: Old Cabin, Footbridge, Water Well, and Ranger Hut.
- **On screen:** large map plus a compact scrollable/expandable **4-object discovery list**, e.g. `□ Old Cabin  □ Footbridge  □ Water Well  □ Ranger Hut`. Tapping an item chooses the active target.
- **Action:** locate that object's visual symbol, then enter or choose its four-digit coordinate. An accepted answer marks **only that item** complete, pins/stamps it on the map and advances or lets the player select another outstanding target.
- **Feedback:** immediate, pleasant and useful; no harsh penalty. Wrong square = retry with optional hint. Already-solved item = show its discovered coordinate without changing score or duplicating completion.
- **Win:** every required object is correctly identified, not simply every visible decoration tapped.
- **Quality rules:** the object must be visually recognizable, have one authoritative stable world position, and be distinguishable from distractors. If two objects share a four-digit square, either (a) use a higher-precision reference or (b) explicitly allow the same square for both *while still identifying them by distinct IDs*. Do not secretly grade by a single pixel or claim 1 km precision pinpoints a building.

### 2.2 What's at This Grid?: example play contract

- **Scenario:** 'At 2741, what do you find?' Show three or four recognizable candidate object tiles: Footbridge, Cabin, Orchard, or Lookout.
- **Action:** follow the grid to a square, inspect its features, choose the matching object (or a clearly stated 'None of these' option when explicitly authored).
- **Win:** select the verified feature that matches the reference and clue, then complete a short batch.
- **Ambiguity rule:** if multiple authored landmarks lie in the same four-digit square, the prompt must say **'Which of these is in the square?'** and accept all supported matches, or provide a disambiguating clue and a uniquely correct answer; otherwise reject the puzzle as unpublishable.
- **Presentation:** game-like answer tiles with concise art/labels, not a bland dropdown/standard input form. Provide color+shape+text rather than color alone.

### 2.3 Mixed/chain contract

Each authored stage declares `objectiveType`, `targetId`, `answerKind`, `precision`, `validatorVersion`, `hintSet` and optional `unlockAfter`. The mission controller owns **current stage, completed IDs and reveal order**; the single map/renderer and coordinate engine remain shared. A later stage's target must not be accidentally exposed by earlier visual markers. Completing a stage can legitimately reveal a new clue, icon or mini story beat.

## 3. Campaign structure and difficulty

**Core principle:** chapters introduce new objective verbs and then *revisit* old ones in new combinations. Difficulty never means unreadably tiny art, time pressure by default, or arbitrary trick answers.

### Illustrative six-chapter arc (names/themes are editable)

| Chapter | Story/world | New skill and mix | Design intent |
| --- | --- | --- | --- |
| **1 — The Little Woodland** | Friendly forest; existing board | Four-digit find-square + Golden Spots | Beginner entry, easy wins, understandable map conventions; preserve existing progress |
| **2 — The Missing Supplies** | Woodland camp with cabins and a bridge | Treasure Checklist and several clear landmark symbols | First Word Search–style discovery list; a satisfying set to cross off |
| **3 — Around the Village** | Small village, mill, market and paths | 'What's at This Grid?' + reverse reading | Different question type: identify **what**, not only **where** |
| **4 — Across the Brook** | River, paths, footbridges | Mixed clue sequences; optional simple route reasoning | Learn to combine observations without excessive instruction |
| **5 — The Explorer's Journal** | Multiple locations or a richer map | Longer mixed objectives; less mandatory guidance; precision only if validated | Interesting deductions and replayable choices |
| **6 — The Lost Expedition** | Finale joining earlier environments | Carefully authored hybrid/chain puzzles | A memorable game-like chapter conclusion, not an endless coordinates exam |

**Chapter pacing:** start with 3–5 objectives for a new mechanic, grow lists/batches only after playtests. Examples of 6–12 items can be later options, not a forced count. A longer chapter should alternate task types and feature discoveries that matter.

**Difficulty axes (independent):** number of objectives; map complexity; amount of clue guidance; feature similarities; mixed-stage reasoning; precision level *only when controls allow it*. Calibrate three friendly presets to begin: **Relaxed**, **Explorer**, **Expert**. Keep hints available in all and do not reduce contrast or target size to simulate difficulty.

**Replay:** completed chapter can be revisited without erasing earlier achievements. Preserve prior chapter/content IDs when expanding the system. Do not add a punitive three-star gate before players may continue.

## 4. Quick Play and Custom Play

**Quick Play** should be a one-tap entrance with safe defaults. **Custom Play** is a choice for players who enjoy one particular mechanic or want a longer session; it should be available without campaign unlock requirements.

**Proposed Custom options, introduced progressively:**
- Mode: Treasure Checklist, Identify the Feature, Classic Coordinate, Reverse Reading, Mixed; route/precision only when built and compatible.
- Map/environment (only verified eligible maps), difficulty preset, 3–10 (and later more) target count, optional hints/guide, and optional mixed-mode ratios.
- Default preset works with no configuration. Collapse advanced settings; show a concise 'Your expedition' summary and a clear Start action.
- Invalid combinations are **disabled or explained** rather than silently changed (e.g. asking for 10 unique wells on a map with 2).
- One active session stores a stable **seed + generator version + map revision + objective IDs**. Resuming must reproduce **identical** answers/layout after app restarts; content migrations may not reshuffle an unfinished puzzle.
- Seeded objectives use curated eligible landmarks and bounded retries/fallbacks. Randomness alone is not quality: no duplicate/ambiguous targets, unrecognizable objects, impossible size or hidden answers. Prototype with a small verified pool before scaling.

**Later, optional:** Daily Hunt, user-authored map pack or nearby collaboration. None should block offline solo.

## 5. Professional game UI: GridMates DNA without the WebView feeling

**Current reality:** Grid Hunt is already **native Android/Jetpack Compose**, not a WebView. The owner is describing its *visual treatment and interaction feel* as too much like a website/form. Solve the design problem, not by rewriting the stack.

- **Home:** distinctive game cover / woodland hub, prominent Continue, visible chapter route, small persistent progress; minimal scrolling and no repetitive generic buttons.
- **Chapter select:** illustrated regions or a simple connected journey, with readable titles and completion marks. Discoveries unlock visibility as chapters progress; don't make navigation unnecessarily complex.
- **Gameplay HUD:** map takes priority; compact checklist/journal panel **folds out** on demand, clue bar or answer tray near reachable thumb zone, unobtrusive north/zoom/help, obvious status and selected target. Avoid six separate web-style buttons in tall stacks.
- **Checklist UX:** checkmarks or physically satisfying stamps, current target visually highlighted, items can be revisited, zero accidental auto-completion, a pleasant whole-list finish.
- **Identification UX:** select among tactile picture/name pieces, clear selected state, obvious Submit/Confirm, non-blocking result. Never require tiny object taps without an alternate accessible interaction.
- **Results:** authored 'You found them all' expedition/journal page with true discovered items and optional replay/next route, not a spreadsheet-like score summary.
- **Style:** coherent type hierarchy, spacing, shapes, custom iconography, layered illustrated game materials, quiet motion/optional sound/haptics. Avoid copying another GridMates title's exact layout or dressing every control as a material form field.
- **Accessibility:** touch targets around 48dp or larger, large-text and contrast checks, TalkBack labels, color-blind-safe markings, portrait/landscape, readable 4-/6-digit grid ticks and zoom/focus fallback.
- **Tone:** curiosity, discovery, kindness, no faux military ranks or 'survey complete' bureaucracy.

**GridMates family resemblance:** the friendly tone, crisp readable typography, practical accessibility/offline behavior, light achievement/journal language, and a modest shared logo/color accent—not identical menu architecture.

## 6. Asset policy — intentionally just-in-time

The owner explicitly wants the **game and content architecture organized first**, then a cohesive art pass. Do **not** generate hundreds of sprites or redesign every button in advance.

**Create an asset early ONLY when necessary to test a mechanic/legibility:** e.g. visibly different Cabin vs Hut answer tiles, selected-object badge, gold target versus solved stamp, a list-discovery check animation, or a landmark whose icon must be recognized to play fairly.

1. Define the gameplay purpose, exact map-space anchor/bounds, target sizes and contrast requirements.
2. Try a reusable Compose vector/shape or clear placeholder in a single vertical slice.
3. Create one small original final-quality asset if the placeholder makes the mechanic impossible to judge fairly.
4. Register it in a versioned asset/landmark manifest with license/source and responsive variants; test on a real device.
5. Once mission architecture and a few modes are stable, do a separate **cohesion/style/library audit** and produce reusable category sets, icons, transitions and chapter world maps.

**Never:** derive coordinates from an illustration, turn scenery into unanchored answer data, silently replace existing identifiers, copy unlicensed map images, or let map decoration obscure grid labels.

## 7. Engine and content contracts before expansion

Existing engine: pure-Kotlin grid math and canonical meter-world positions, plus Compose renderer/camera and local progress. Preserve it.

Build **one shared mode-neutral objective/mission layer** instead of writing a second game engine for each mode. Conceptual models (names are proposals; do not interpret as existing classes):

- **MapPack:** `mapId`, `revision`, `bounds`, `gridSpec`, `landmarkIds`, `artLayers`, `licensing`.
- **Landmark:** stable `id`, localized name, category, authoritative world point/area, sprite/symbol key, legibility tier, optional clue tags.
- **Objective:** stable `id`, `objectiveType`, `answerKind` (grid area / feature ID / waypoint / sequence), `targetIds`, `precision`, `hintIds`, `validatorVersion`, ambiguity/accepted-answer policy.
- **Mission:** stable `missionId`, map pack/revision, ordered or unordered objective IDs, required count, reveal dependencies, optional story beats, content version.
- **Session:** mission ID + version, validated completions, current selection, attempts/hints if desired, stable seed (for Custom), and last-known viewport where safe.
- **UI state:** renders objective list or answer tray based on objective type; **never owns canonical answer logic**.

**Critical validation tests:** correct grid boundaries/leading zeroes and 4-digit area semantics; world↔pixel stable under zoom/pan; exact feature-anchor mapping; no ambiguous same-square object prompts; no future clue leak; checklist mark-off only after accepted answer; no duplicate list items unless explicit; replay/resume deterministic; old saves survive upgrades; art version cannot change coordinates.

**Release identity:** preserve QA package/signature/versionCode continuity during testing. The current public QA key is disposable/test-only. Production signing/privacy/store approval are separate future gates.

## 8. Build sequence — small vertical slices with exit tests

| Phase | Deliverable | Exit gate |
| --- | --- | --- |
| **P0 — Hold a stable base** | Accept/fix QA85 real-phone usability; document observed issues and prevent regressions | Genuine phone feedback and green build/emulator evidence; keep PR reviewable |
| **P1 — Objective & landmark foundation** | Stable data contracts, one small verified landmark set, standalone checklist progression validator, migration/resume plan | Unit tests for IDs, cells/features, unique answers, ambiguity and persisted state |
| **P2 — First Treasure Checklist** | One fully playable 3–5-object hunt on the existing map, in a collapsible objective list, enter/select coordinate, genuine check-off/finish | Owner can find, mark, undo selection and finish; no false matches or inaccessible targets; emulator+phone tests |
| **P3 — What's at This Grid?** | One curated 3–5-question coordinate→object identification puzzle with answer tiles | Correct & ambiguous case tests, visual recognition/large-text and real-device checks |
| **P4 — Story chapters + mixed controller** | Two or three deliberate new chapters, one fixed clue-chain pilot, chapter/journal flow | Same engine supports several objective verbs; resume/replay/old-save compatibility; measurable chapter fun |
| **P5 — Quick Play / Custom** | One-tap preset and small compatible-mode custom settings; versioned deterministic curated objective sampling | Same seed = same puzzle; invalid options handled; no duplicate/unfair objectives; save-resume stable |
| **P6 — Presentation integration** | Chapter-select polish, map HUD/list/answer trays, focused effects, accessible native-feeling navigation, cohesion audit, *justified* assets | Screenshots from multiple device shapes/text scales; owner approves actual game feel |
| **P7 — Beta/release gates** | Content breadth, offline performance, launch/resume/install-over, legal/art audit, production signing and store readiness | Source-matched CI + physical QA + signed release/privacy/compliance checks; explicit owner approval |

**P6 visual standards are applied throughout P1–P5** rather than postponed until the end; the late phase is a unified polish pass, not permission to ship ugly/unusable mechanics.

**Stop rule:** if a phase cannot pass honest game rules/legibility/resume tests, fix that slice before adding another mode or 50 more objects.

## 9. Decisions, open questions, and deliberate deferrals

### Owner-established decisions (carry forward)
- Easy intro stays easy; difficulty grows through **mixed task types and chapters**.
- Word Search–like **discoverable-object checklist** is a major target.
- **Reverse identification** (given coordinates, determine which feature belongs there) is another major target.
- Chapters should progress across difficulty and mix mechanics; Custom Play lets players repeatedly play favorite puzzles.
- Play Store-quality professional presentation and a stronger **distinct Grid Hunt identity** are goals, while keeping recognizable GridMates friendliness.
- **No blanket asset generation now**; make essential one-offs only when the game requires them.
- Keep existing app progress, mechanics, and QA integrity intact while transitioning.

### Not decided / prototype choices (do not silently lock)
- Final names for the new modes/chapters and exact chapter count.
- Whether a checklist target is solved by typing a grid number, selecting the square, or both options; test the fastest phone flow.
- How to handle more than one landmark within a four-digit square (unique map design, explicit multi-answers, or precision upgrades).
- Whether route plotting is fun/feasible enough for v1.
- Default Custom target counts, degree of randomization, difficulty thresholds and scoring/reward economy.
- Final game-art style, sprite atlas, animation/sound set, icon and market positioning.

### Out of scope until separately approved
Real-world GPS/MGRS outdoor use; combat/military simulation; mandatory online account; LAN multiplayer; live weather or map tiles; 3D terrain; user-generated map marketplace; intrusive monetization; huge launch-day map catalogue; unvalidated 8/10-digit precision.

## 10. How to resume work in the next chat

**Tell the assistant:** 'Continue GridMates: Grid Hunt using `docs/GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md` in `PlayerEden/GridMates-Grid-Hunt`. Read the latest PR, START_HERE and roadmap, confirm CI, then implement the next approved phase in a tested slice.'

**Start-of-session checklist:**
1. Fetch the **actual current** GitHub docs and [PR #12](https://github.com/PlayerEden/GridMates-Grid-Hunt/pull/12); confirm branch/merge state and last successful source-matched run. Never assume QA146 stays latest; inspect current source, jobs and exact APK artifacts.
2. Read [this blueprint](GAMEPLAY_CAMPAIGN_AND_UI_BLUEPRINT.md), [master design](MASTER_GAME_DESIGN.md), [accuracy rules](MAP_READING_ACCURACY.md), [content contract](PUZZLE_AND_CONTENT_SYSTEM.md), and the relevant prior [phone audit](vision/GH_V08_PHONE_QA72_SCREEN_AUDIT.md).
3. Reconfirm the current phase, what code is actually built, what is merely proposed, and what still needs the owner's physical-device test.
4. For any new puzzle mode, first write answer/ambiguity/fairness tests, then add minimal native UI, then run Android CI and review screenshots.
5. Maintain the **stable QA Android signing identity** and monotonic update version; don't tell owner to uninstall a working QA app to test.
6. Update this file's current handoff and status once the milestone is done, with exact PR, commit, Android run, owner remarks, and next step.

**Latest implementation status (2026-11-23):** Four standalone gameplay families are playable on Little Woodland—Trail Treasures, Golden Spots, Treasure Checklist, and What's at This Grid?—plus **the first authored Mixed Expedition**, a single continuous 4-stage woodland chapter with different kinds of map questions. [QA146](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45298114810) passed core/APK and executed emulator UI, including **15** screenshot gates; real phone feel and update-over remain **unapproved**. GH-V11's typed clue validator remains core-tested against synthetic multi-church cases only. [GH-V13 handoff](vision/GH_V13_MIXED_EXPEDITION_VERTICAL_SLICE.md). Next: owner QA and a distinct Chapters/Quick Play home structure—not a mass art pass.
