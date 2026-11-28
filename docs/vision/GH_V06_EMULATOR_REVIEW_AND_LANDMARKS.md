# GH-V06 — Emulator Visual Review and Friendly Landmark Pass

**Date:** 2026-11-23. **Status:** review candidate, subject to the next complete CI run and owner device acceptance.

## Evidence inspected
The Android 35 Pixel 5 emulator successfully **ran all 9 instrumentation tests** in [QA66](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45206547388). The new visual test solved all nine authored Trail Treasures squares and all five Golden Spots through the actual UI. The MediaStore capture mechanism saved and adb retrieved six actual PNGs (1080×2340) from the same emulator.

QA66 itself finished red because the final six-file validation loop was spread across shell commands in the emulator runner's script. The APK/core/signer and emulator gameplay passed, and all six PNGs were present in the published `gridhunt-android-ui-test-results` artifact. This CI shell problem has been corrected with six standalone `test -s` commands.

## Actual screenshot observations

1. **Home:** Original illustrated hills and winding path are welcoming, and the two clearly distinguished puzzle cards are easy to find. Woodland art still feels fairly flat, and the second mode sits below the fold on shorter devices — acceptable scroll behavior but worth better first-use discovery.
2. **Trail Treasures:** The puzzle map dominates the screen much more than in the owner's original screenshots. Golden number token is legible, and the controls and status do not visibly overlap on Pixel 5 portrait at default text scale.
3. **Cool Sketch palette:** Readable, but currently less lively and lower in color contrast/character than Cozy Woods. Future polish should not just recolor an identical map and call it a substantially new theme.
4. **Golden Spots:** The dot and four-digit input are clear, with the map central. The control area feels utilitarian but smaller than the original reverse-survey form.
5. **Trail finale:** 9/9 checkmarks, golden congratulation and explicit Explore again / Home are clearly present without hiding the entire solved map.
6. **Golden finale:** 5/5 discovered spots and a clear replay / Home choice, with the map still visible.

**Limits:** One emulator profile and default font sizing cannot prove phone comfort, text accessibility or landscape quality. The captures are native emulator screenshots, not proposed concept art or owner-device proof.

## New original landmark art (GH-V06)

- Add a tiny two-mushroom nook, flower glade, acorn nook and smooth waystone to the actual interactive map.
- Their positions are deterministic world-meter coordinates and they appear in both palettes. They are **not** hidden targets, collectibles, prompts or taps, and they never affect answer checking.
- Icons use moss, ivory and terracotta, intentionally distinct from gold target markers and completed stamp rings.
- Original core coordinates, target locations, camera and saving remain untouched.

## Next review gates
- Verify an Android build and executed emulator gameplay and screenshot-export pass on the exact V06 commit.
- Inspect the six exported PNGs again to confirm the four small decorative landmarks remain subordinate to grid lines and the answer marker.
- Record any clipping at map edges or overlap with stamps when zoomed.
- Owner phone update-over and large-font/landscape playtest remain deferred, not waived.
