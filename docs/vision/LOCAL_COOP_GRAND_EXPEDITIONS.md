# Local cooperative Grand Expeditions — long-term vision
2026-11-27. Owner wants to work on very large shared puzzle hunts with spouse on separate devices over days/weeks, using local Wi-Fi. Future direction, not current release scope.

## Player experience
- Offline-first solo or shared expedition; no mandatory accounts, cloud, permanent host, timer, daily reset, or simultaneous attendance.
- Pair devices on LAN with explicit invitation/confirmation. Each device retains durable project replica and independent personal viewport, settings and current clue.
- Either participant can continue while the other is offline, then reconnect and reconcile. Both may close the app and resume later.
- Shared discoveries, regional milestones, optional personal contribution counts, no competitive pressure. Optional live presence later.

## Proposed persistence contract
- Immutable expedition ID, map/content revision, objective IDs, generation algorithm version and exact objective manifest (seed alone insufficient across versions).
- Monotonic validated solved-objective set; merge by union only when identity and manifest match. A stable objective ID is counted once even if both solve it offline. Use immutable event IDs and deterministic duplicate attribution policy; personal totals must not inflate shared totals. No clock-based last-write-wins for completion.
- Atomic durable local write before acknowledging a discovery; idempotent sync, checksums, schema version and transactional import. Exchange summary and missing events, then acknowledge committed revisions. Retain pending unsent changes; interruptions and repeated reconnects safe. No automatic replacement of entire save.
- Different project or incompatible revision: reject merge, explain, preserve both copies. Reset/replay creates new expedition identity; cannot erase another replica silently. Explicit backup/export/import and device replacement recovery; lost unsynced progress cannot be recovered without surviving copy.
- Pairing authentication, encrypted transport, bounded validated messages, no trust in remote arbitrary save files, no unnecessary personal data. Android LAN permissions and device discovery compatibility audit before implementation. Neither device is permanent authority; temporary session host may coordinate live presence only.
- Shared rewards derived deterministically or deduplicated by stable ID. Nonmonotonic edits (notes, choices, deletion) require separate conflict policy; defer.

## Example
Both devices have 347 discoveries. A solves 20, B solves 15 offline, 5 overlap. On reconnect union gives 377 unique discoveries, not 382; both converge. Atomic commits and retries preserve state across disconnect, process death and device restart.

## Sequencing and acceptance
1. Build stable objective IDs, versioned expansion progress and durable single-device large expedition save first.
2. Pure core replica merge tests: commutative, associative, idempotent; duplicate objectives, overlapping offline progress, invalid IDs, incompatible manifest/revision, repeated delivery, crash/partial transfer, deterministic attribution.
3. Local persistence/backup and migration tests including signed install-over and process death.
4. LAN pairing and authenticated sync prototype; two actual devices, disconnect/reconnect, both offline and later convergence, app termination, network changes, no internet, device replacement.
5. Optional live collaboration and presence only after durable sync is verified.
No network permissions or production code changed by this design. No commitment to a specific protocol/library or publication schedule. See ../LARGE_SCALE_EXPLORATION_VISION.md and ../ADVENTURE_CHAPTER_LEVEL_STANDARD.md.
