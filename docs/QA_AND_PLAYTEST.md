# QA, Educational Accuracy and Phone Playtests

**Status:** planned test matrix; no test runs yet.

## A. Pure Kotlin coordinate invariants (P0)

- E first, N second; north-up y-axis inversion handled exactly once.
- Square containment uses **floor/truncation**, not rounding.
- Four-digit result = 1 km cell; six-digit = 100 m; reject malformed/odd digit counts and out-of-range coordinates.
- Leading zeroes preserved; 4->6->8 digit containment consistent.
- Southwest origin, right/top boundaries and just-inside/just-outside cases pass.
- Verify numeric worked example in [accuracy reference](MAP_READING_ACCURACY.md).
- Camera pan/zoom conversion roundtrip does not alter world points or selection.
- Reversing a feature coordinate at stated precision yields expected reference.

## B. Generator integrity (P0 before Free Hunt)

- Same mapId/revision/generatorVersion/seed -> same targets and ordering.
- Required objectives are unique and in-bounds with valid resolution.
- Landmark clues have **one** supported answer or explicitly multiple answers.
- Landmark/marker density stays comfortable; no invisible answer-only art.
- Bounded generation failures degrade to known good fixtures.
- Old saved seeds remain stable across content updates; no silent reroll.

## C. Actual UI/device behavior (P0/P1)

- Pinch zoom, single-finger pan, recenter, inspect and marker placement are distinct and predictable.
- Map grid remains readable; no tiny mandatory hit boxes; zoom/reticle works for 100 m cells.
- Test compact Android widths, tall devices, landscape, 200% system font, TalkBack/focus, reduced motion, color distinguishability.
- Airplane mode gameplay; resume after force-close/background/rotation; markers and current objective persist.
- Hints don't unintentionally spoil the solution; results accurately explain errors without false precision.
- Verify no phantom taps during pan and no unintentionally lost progress on reset.

## D. Build and signing evidence

For every candidate: source SHA, branch, Android package ID/version code, build variant, APK SHA-256, signer fingerprint, tested emulator API/device profile, automated results, physical owner results, known limitations and rollback ref.

**Never conflate:** compiled UI tests vs executed UI tests; emulator vs physical phone; test signer install-over vs production signer; a source merge vs a published APK.

## E. First owner phone playtest (GH-01)

1. Open in airplane mode; locate three four-digit references without guessing the grid reading order.
2. Zoom and pan before selecting; check same map cell remains selected.
3. Intentionally choose a wrong square, correct it and resubmit.
4. Try tapping a boundary and a distant corner; confirm behavior is fair.
5. Rotate screen if supported; return from background; confirm points remain placed.
6. Note font/grid readability, finger occlusion, UI clutter, map aesthetics and anything frustrating.

**Required release statement:** only claim the game is functional after a real device verifies the same build. Record failures as issues with repro, expected/actual and screenshot/video when available.