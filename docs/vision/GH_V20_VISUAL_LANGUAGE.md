# GH-V20 — Visual language and map-first interface

Owner direction, 2026-11-24: use meaningful UI artwork and icons to reduce repeated words and wasted space; teach their meanings in the Academy. Pictures are broadly useful across languages but not automatically understood.

## Principles
- Map first. Keep current clue, selection, essential progress and confirmation visible; move explanations, previous feedback, settings and reference material into contextual panels/dialogs.
- One icon, one meaning throughout Adventure, Academy, Quick Play and future Custom Play. Distinguish map symbols (world features), UI actions, statuses and earned rewards.
- Reuse native artwork with consistent stroke, shape, palette and optical size. Prefer stable named assets over platform-dependent glyphs. Do not alter canonical landmarks or puzzle answers.
- Preserve at least 48dp interactive targets, accessible localized content descriptions, contrast, non-color cues, large-text and narrow-screen fallbacks. Do not make icon-only actions dependent on memorization.
- Keep text for actual clue wording, unfamiliar or consequential actions, coordinate values, ambiguous concepts, and optional help. No universal comprehension assumption.

## Proposed semantic inventory
home, journal/overview, compass/orientation, map legend, hint, fit map, zoom in/out, clear selection, confirm, qualifications, settings, four cardinal move actions, lock, completion. Use distinct zoom-fit and clear-selection symbols. A compass rose indicates directions, not an unexplained reset control.

## Academy integration
Introduce map tools in context before independent field practice; demonstrate meaning with icon + short label and a real interaction. Optional help remains available outside Academy; never force graduation to use normal gameplay. Provide replayable tool guide. Keep stable earned lesson IDs and qualification storage; if new required exercises are introduced, explicitly version curriculum and preserve previous graduation entitlement.

## Implementation order
1. Audit shared HuntUi, map controls, Academy and home destination actions; establish reusable vector asset registry.
2. Introduce icon+label controls, accessible descriptions and test tags; compact repetitive navigation and selection actions, with labels for unfamiliar controls.
3. Teach controls during Academy and provide on-demand legend; verify at phone scale, high font scale and landscape.
4. Reuse in all modes, then consider icon-only familiar actions where usability evidence supports it.
5. Audit visual consistency, accessibility, saves and emulator/owner phone QA.

No artwork registry or new tutorial mechanic is implemented by this design note alone. QA238 is the last confirmed fully verified baseline at authoring; newer map-first commits pending verification.
