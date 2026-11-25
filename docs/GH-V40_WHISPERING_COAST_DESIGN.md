# GH-V40 — Whispering Coast design proposal

Status: proposed, not playable, not approved for release. Little Woodland remains canonical and unchanged.

## Design intent
A cozy coastal field expedition, not a recolored Woodland. Keep north-up fictional training maps, accessible grid labels, original journal presentation, offline play and existing interaction patterns. Avoid time pressure or real navigation claims.

## Candidate map
Proposed ID whispering-coast-01, revision 1; world ID whispering-coast-v1. Proposed 6 x 6 km bounds west 42000, south 51000, east 48000, north 57000. These are fictional coordinates, not geographic locations. Thirty-six four-digit squares; ocean squares remain valid coordinate objectives. No implied traversability or route constraints until explicitly authored. Map artwork must be aligned to answer data.

Candidate landmarks (not published):
| ID | Name | Easting | Northing | Grid |
|---|---|---:|---:|---|
| beacon-point | Beacon Point | 46600 | 56100 | 4656 |
| driftwood-cove | Driftwood Cove | 43800 | 52200 | 4352 |
| shell-beach | Shell Beach | 45200 | 51300 | 4551 |
| old-jetty | Old Jetty | 47600 | 53300 | 4753 |
| gull-rock | Gull Rock | 44500 | 55300 | 4455 |
| tidepool-bend | Tidepool Bend | 46100 | 54500 | 4654 |

All positions are illustrative design commitments only after terrain review. Six distinct landmark cells allow two consecutive three-landmark hunts to be disjoint. Draw shoreline first, verify each feature sits in a sensible location, then freeze IDs/points and publish.

## Adventure draft
1. The Beacon's Message: introductory grid selection and landmark recognition; find a missing coastal chart.
2. Along the Tide Trail: grid reading and directional/landmark deductions; reconnect supplies and field notes.
3. The Last Light: mixed expedition combining learned skills; complete the coastal survey.
Four objectives per chapter initially; no real-time tides, timed hazards, or six-digit mechanic promises. Explicit prerequisites proposed: finish Woodland Chapter 3 to unlock Coast Adventure; Quick/Custom world availability policy requires owner review.

## Art deliverables
Original illustrated coastal postcard, six readable landmark sprites (distinct silhouettes, transparent source), environmental scenery (rocks, dune grass, coastal shrubs, water accents), terrain composition and survey-style equivalents. Reuse shared journal UI, compass, overlays, startup and launcher identity. No mascot regeneration. No text baked into puzzle art. Source master provenance and generated export hashes required. Grid and canonical landmark positions remain code-owned, never inferred from illustration.

## Publication gates
- Fully green combined GH-V38/39 QA, visual comparison against QA388.
- Verify actual map ID/revision and scene binding; no silent fallback to Woodland for another map.
- Authored map and chapter content validated, complete artwork and map guide, all answers verified.
- Connect isolated WorldHuntStore off UI thread with explicit replacement confirmation and retain legacy Woodland namespace. Test cross-world progress, process restart, wrong revision/corrupt saves.
- Integrate world selection only when playable; preserve single-world navigation and existing progress.
- Android tests for all five supported Custom modes on both maps, camera/labels/selection at fit and zoom, chapter unlocks and completion, accessibility and save upgrade.
- Owner physical review and release approval. Beta 1 scope is not automatically expanded by this proposal.

No new published map, artwork, save key, package or release is authorized by this document.
