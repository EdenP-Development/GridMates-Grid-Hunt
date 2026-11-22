# GM08 — First Physical-Android Playtest Session Sheet

**Status:** UNEXECUTED TEMPLATE (2026-11-22). Use one copy per real player/session.  
**Full methodology:** [GM08](GM08_PLAYTEST_EVIDENCE_AND_PROTOTYPE_DECISIONS.md); [QA source/build requirements](../QA_AND_PLAYTEST.md).

## Before you test
- [ ] **Real signed QA APK exists** and installs in airplane mode, with exact source SHA/version/APK hash/signer recorded.
- [ ] One **6×6 fictional training map**, three distinct valid 4-digit references, world-space whole-cell validator.
- [ ] Tap/preview/Change/Check/Undo and help work; coordinate/gesture tests **executed**, not only written.
- [ ] Participant consent for any screen/audio recording; use pseudonymous session ID, not a name.

## Neutral opening
“I'm testing the game, not you. Try this short hunt without me telling you how the puzzles work. Please say if something confuses you; I won't give hints unless you request help. You can stop any time.”

**Don't give away eastings/northings.** Let the game teach that; observe what happens.

## In-session tasks (10–15 min is only a planning target)
- [ ] Launch and find the goal without external instruction.
- [ ] Select, preview, and confirm **three** different 4-digit areas.
- [ ] Change an uncommitted selection before Check.
- [ ] Fix a wrong submitted square, using hint if wanted.
- [ ] Pan/pinch or visible zoom, without moving canonical marker.
- [ ] Try one **fresh** reference; if possible test a new art/map fixture.
- [ ] Pause after completion: observe spontaneous **Next Hunt** use, don't prompt it.

**Afterward ask:** “What information did you use?” · “What was confusing?” · “Which moment felt satisfying?” · “What would you remove?” · “Would you play another hunt?”

## Observation record
```text
Session ID / date:
Source SHA / app version / APK hash / signer:
Android device / OS / orientation / font scale:
Map revision, objective ID, variant (A/B), familiar or fresh:
Experience category: novice / some map reading / experienced
Recording permission: yes/no  (default: no)
Action observed:
Expected vs actual:
Hint/attempt/undo:
Error category: MATH CONTENT GESTURE VISUAL TEACHING
                PUZZLE FLOW MOTIVATION ACCESS TECH
Player quote (if offered):
Severity and proposed fix:
Issue ID / build to retest:
```

## Decision
**FIX** canonical/critical mobile failure · **REVISE** confusing or uninteresting play · **CONTINUE CAUTIOUSLY** only with verified math, playable Android interaction and observations that justify next iteration.

A small qualitative round finds design problems; it is not a statistical proof of preference, transfer, or military land-navigation ability. No results exist until this sheet is filled after actual testing.
