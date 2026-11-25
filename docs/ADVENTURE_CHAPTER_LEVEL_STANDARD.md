# Adventure chapter level standard — GH-V41-B

Owner direction 2026-11-25: eventually each chapter should contain at least 10 or 12 levels. Working target: 12 independently meaningful puzzle levels per chapter; minimum 10 for a mature chapter. Three chapters per world gives a target of 36 levels per world, 72 across Woodland and Coast. Current four-objective chapters are development scope, not the final content target.

## Design
- A level is one authored puzzle with a stable objective ID, an explicit answer contract, and a journal discovery or useful feedback. Do not pad with repeated answers or artificial waits.
- Proposed 12-level arc: 1–3 orientation and recall; 4–6 landmark and grid interpretation; 7–9 directional reasoning and combined clues; 10–12 synthesis and chapter conclusion. Adapt to each chapter's story, map and accessibility needs.
- Preserve existing four objectives and journal discoveries. New content should feel coherent with the chapter, not like a disconnected appendix; author and review all clues before publication.
- Use varied modes appropriate to the map, no invented marker or feature dependencies. Golden marker data must be authored and validated per map. Avoid mandatory time pressure.

## Save compatibility gate
Existing saves are integer objective steps in chapter-specific namespaces. Simply changing a four-objective chapter to twelve changes the meaning of completion and may relock downstream chapters. Never ship that change directly.
- Preserve stable chapter and objective identities. Never reorder or insert before existing progress without a tested explicit migration.
- Recommended approach: preserve legacy completion as a durable earned entitlement and track expansion progress separately, with a versioned migration plan. A completed legacy chapter stays completed for unlock purposes; additional levels remain optional until a new-player policy is approved. An incomplete legacy chapter retains its earned objectives, and no downstream access already earned is removed.
- Do not implement or write migration data yet; first design exact semantics, UI labels, schema and rollback behavior. Read existing saves without mutation until approved. Test every old step 0–4, malformed/missing data, completed downstream chapters, restart and signed install-over, including all eleven baseline preference maps.
- No automatic expansion of Beta 1 scope, no public release or merge without owner approval.

## Sequence
Finish map/scene binding and coastal runtime foundations, then build a level-selection and progress model and expand content in reviewed batches. Do not publish a twelve-level count until all twelve actual puzzles exist and pass answer and UI tests.
