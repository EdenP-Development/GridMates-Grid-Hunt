# Coastal artwork collection — GH-V40-D

Draft, not approved runtime art. Companion to coordinate-composition-v0.1.svg.

The original vector studies in landmark-studies-v0.1.svg establish six distinct silhouettes, subdued palette and readable shapes. They are hand-authored SVG geometry, not externally sourced images. The sheet is a review asset, not a runtime sprite atlas. No baked labels in future sprite exports.

| ID | Visual silhouette | Grid | Anchor |
|---|---|---|---|
| beacon-point | striped lighthouse | 4656 | tower base |
| driftwood-cove | diagonal branched log on shoreline | 4352 | log center |
| shell-beach | scallop fan | 4551 | shell center |
| old-jetty | diagonal timber pier | 4753 | seaward tip (terrain pier and symbol must agree) |
| gull-rock | angular rock with gull | 4455 | rock center |
| tidepool-bend | oval rock ring with blue water and starfish | 4654 | pool center |

Palette: ocean #98C0C7, grass #B4C39A, sand #E8D6A6, ink #315747, paper #F4EDDA. Keep gameplay markers and grid above scenery. Distinguish decorative rocks from Gull Rock, driftwood from generic beach accents, and tidepool from water textures.

## Current source inventory
- coordinate-composition-v0.1.svg: coordinate schematic.
- landmark-studies-v0.1.svg: six labeled silhouette studies.
- masters/: six individual landmark SVGs and four scenery SVGs (dune-grass, coastal-shrub, coastal-rocks, wave-accent), each 128×128, no baked labels.
- terrain-study-v0.1.svg: 600×600 ungridded composite, six proposed coordinate centers, reused master geometry and decorative scenery. Placement is preliminary: final symbol anchors and shoreline agreement are not yet verified.
- postcard-study-v0.1.svg: 900×480 label-free presentation composition, reused master geometry. Not puzzle geometry.

Next: review at 24/32/48 px on field and survey backgrounds; deterministic transparent raster exports, visual comparison to Woodland, correct exact anchor alignment and scene/map binding. The two composite SVGs embed copies of master geometry; future changes require regeneration or explicit reconciliation. No registry or runtime changes, no release approval.
