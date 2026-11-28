# Explorer's Academy — GH-V18 learning design

Status: proposed design, not implemented. Owner identified zero prior map-reading knowledge as a core accessibility requirement. Integrate learning before publishing harder spatial puzzles.

## Experience
- First launch offers a friendly optional guided introduction; Skip and replay are always available. Never gate Quick Play or existing campaign progress on tutorial completion.
- A permanent Explorer's Academy entry on Home provides short interactive lessons; in-puzzle Map Guide remains a context-sensitive reference, not a replacement.
- Teach via demonstration, guided practice, then independent practice with a different example. No timed pressure, penalties, or mandatory reading walls. Incorrect answers explain the relevant concept, allow unlimited retry, and offer progressive visual guidance.
- Reuse native map camera, coordinate selection, legend and canonical validation. Keep existing chapter and puzzle saves intact. Academy uses separate versioned progress by stable lesson IDs.
- Preserve woodland parchment/forest identity, large touch targets, portrait and landscape usability, readable text and non-color-only cues. No new mass asset production.

## Lesson progression
1. Your First Map: pan, pinch, zoom controls, See all, tap/clear a square.
2. Finding Your Bearings: north/up, east/right on north-up maps; south/west, compass.
3. Cracking the Grid: read easting first and northing second; highlight the square's west and south boundaries; distinguish grid square from exact point; guided and independent examples.
4. Reading the Landscape: match map symbols and legend to visible features; avoid unverified scenery relationships.
5. Following the Trail: count adjacent squares in cardinal directions from a known start, reject out-of-bounds paths.
6. Explorer's Deduction: compare actual canonical landmarks, combine conditions, reject ambiguous clues.
7. Future optional precision, distance/scale, contours/elevation only when corresponding map data and mechanics exist.

## Integration and QA
- GH-V18-B should prioritize a small playable Academy vertical slice (map controls, bearings, four-digit grid) before harder campaign content. Keep later lesson titles out of playable registry until implemented.
- Provide first-use coaching per mechanic, dismissible and available again; use concise language before introducing technical terminology. Do not reveal campaign answers.
- Test novice error patterns (reversed digits, off-by-one boundaries, wrong direction), retries, skip/revisit, state restore, Academy/campaign isolation, gesture alternatives, font scaling and landscape. Add genuine Android screenshots and human first-time playtest.
- Existing GH-V17 campaign and Quick Play remain accessible. No claims of implemented lessons or verified builds until actual code and CI exist.

## Academy mini-campaigns and graduation (owner direction)
Academy is a parallel optional progression pillar, not merely onboarding. Each school is a small authored campaign with lesson stages, independent practice, and a graduation expedition using unfamiliar examples. The main foundational School of Exploration covers the six initial lessons; optional School of Navigation covers routes and distance, School of Cartography covers precision/scale/terrain, and School of Discovery covers multi-condition deductions and mysteries. Advanced schools are future proposals, published only when their underlying mechanics and map data exist.

Graduation requires earned completion of the school's lessons and final expedition; no paid shortcuts, timer requirements, or punitive retries. Offer coaching during practice and after incorrect graduation attempts. Graduation awards durable cosmetic entitlements such as a school insignia, profile title, journal cover or profile decoration, subject to actual cosmetic system availability. Do not promise external accreditation. Rewards are earned once per stable school ID, retained on replay, independent of current lesson completion and campaign progress. Optional advanced school prerequisites govern Academy only, never block Adventure or Quick Play. Begin with one functional school and one graduation reward before building additional schools or artwork.

Use stable versioned school/lesson/challenge IDs, a separate Academy save namespace, and idempotent graduation awards. Test skipped onboarding, old saves, repeated graduation, no duplicate rewards, replay, wrong answers, and no impact on campaign or standalone modes. No fake playable course entries or reward claims before implementation.
