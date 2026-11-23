# GH-V05 — A Real Puzzle Ending and Inspectable Emulator Screenshots

**Owner request:** Continue Grid Hunt while owner cannot test on their Android phone. Build actual game-feel and quality evidence without overstating unverified visuals.

## What changed

### Finish cards
- **Trail Treasures:** At the end of each authored three-clue trail, replace the busy input/arrow panel with a compact illustrated-paper finish card offering **Next trail** or **Home**.
- **Final chapter:** After the third trail, show "Woodland adventure complete!", correctly displaying 9/9 found and offering **Explore again** or **Home**.
- **Golden Spots:** After the fifth correct code, show a matching 5/5 card with **Find them again** or **Home**.
- **No automatic reset:** navigating Home must preserve completed state; only an explicit replay/continue action advances or restarts the authored sequence. The answer/validation core and stored preference names remain unchanged.
- These cards make no claim of virtual coins, stars, rankings, or invented unlocks. The visible counts derive from the existing level state, not from fabricated rewards.
- Replacing the normal control panel when finished preserves the actual map in both orientations.

### Real screenshot pipeline
- `VisualCaptureTest` uses Android's `UiAutomation.takeScreenshot()` during an **executed instrumented test**, not a renderer mock or aspirational image.
- Six screenshots from the same running Android 35 emulator:
  1. `01_home.png` — cozy woodland menu
  2. `02_trail_treasures.png` — first map puzzle
  3. `03_trail_other_map_style.png` — same puzzle, alternate palette
  4. `04_golden_spots.png` — reverse puzzle
  5. `05_treasure_finish.png` — last authored Trail Treasures completion state
  6. `06_golden_finish.png` — Golden Spots 5/5 completion state
- The finish images are generated from **test-only seeded preferences on the emulator**, identified as simulated completion state and never represented as an owner's real accomplishment.
- Screenshots are saved under the emulator app's own external files directory, collected through `adb pull`, checked for nonzero PNG size, and uploaded alongside executed Android instrumentation results.
- These image files allow a developer to inspect layout, contrast, overlaps, density and visual coherence independently of whether the owner is available. They do **not** automatically establish actual phone quality.

### Regression gate
- JVM/core test + compilation + Android QA package and test-only signer/version preflight
- Executed emulator UI navigation, real coordinate submissions, and resume tests
- Explicit completion card Home and replay tests (no auto-reset)
- Six nonempty screenshot captures from the running app
- Retain prior accepted QA22/QA23 install-over evidence; do not claim success on the owner's specific phone (issue #11)

## Review checklist after CI
- Verify the newest run is PASS with both jobs complete before describing V05 as checked.
- Inspect all six images, looking for clipped map grid labels, crowded headers, hidden buttons, unreadable large text and lack of map dominance.
- A portrait-only Pixel 5 run is limited evidence. Future changes should test landscape, larger font and an additional narrow portrait emulator.
- Preserve the preexisting installed QA package/signing identity; new APK can be attempted as an **update**, not an uninstall/reinstall, when the owner is ready.
- The picture-book hero art is illustration only; all numeric solutions still come from the canonical core.
