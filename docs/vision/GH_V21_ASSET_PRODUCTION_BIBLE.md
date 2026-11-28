# GH-V21 — Grid Hunt asset production bible
Owner-approved direction, 2026-11-24. Establish functional layouts first, then replace provisional programmatic illustrations with a cohesive original art library. Companion: GH_V20_VISUAL_LANGUAGE.md.

## Art direction
Cozy woodland field journal meets illustrated cartography. Warm parchment, muted forest and moss greens, bronze, ochre and cream. Soft hand-painted texture, gently imperfect ink outlines, restrained shading, clear silhouettes, friendly but not childish. Avoid military styling, glossy 3D, noisy textures, mismatched clip art and generic emoji. Existing QA238 artwork is a layout reference, not a final art-quality reference.

## Production inventory (ordered batches)
| Batch | Assets | Acceptance |
|---|---|---|
| A core UI | parchment texture, subtle wood/paper frames, button states, plaques, tabs, dividers | scalable borders, quiet surfaces, readable text, no baked labels |
| B tools | zoom in/out, fit, guide, clear, hint, more, home, journal, settings, confirm, lock, optional cardinal arrows | recognizable at 24dp; shared meaning and accessible labels |
| C woodland map | evergreen, broadleaf, shrub, mushroom nook, buttercup glade, acorn nook, old waystone, trail details | distinct silhouette, exact canonical anchors, readable at map fit and zoom |
| D Adventure | woodland postcard, chapter destination badges, field journal pages, supply illustrations | consistent scenery, no baked route geometry or chapter text |
| E Quick Play | Trail Treasures grid, Golden Spots marker, Treasure Checklist, identification magnifier | distinguish modes at small card size |
| F Academy | school crest, Bronze Compass, compass diagram, grid-reading diagram, lesson illustrations | accurate teaching, qualification remains tied to earned state |
| G rewards | discovery stamp, completion seal, future qualification family | no invented unlocks; same palette and line treatment |
| H expansion | new biome kits and school families | reuse approved reference sheet and asset rules |

## Asset contract
- Native drawable/vector or transparent PNG/WebP as appropriate. Suggested source masters: icons 256px square, landmark sprites 512px, badges 512px, illustrations 1536px or higher, textures seamless 512–1024px. Export device-appropriate sizes after inspection; don't ship source masters automatically.
- Each sprite isolated with consistent padding, clear alpha edges, no sheet borders, accidental neighboring objects, watermarks or text. UI panels designed for stretch/tile or nine-slice; keep typography live.
- Maintain registry with asset ID, file, category, role, dimensions, version, anchor, provenance, approval and fallback. Use stable semantic filenames such as tool_zoom_in, landmark_buttercup_glade, academy_bronze_compass. Map world coordinates remain in canonical model; sprite anchor cannot change answer geometry.
- Preserve live grid lines, coordinates, dynamic progress, selection, camera, hit targets, accessibility semantics and puzzle state in code. Simple vector utility icons may remain if cleaner than painted equivalents.
- Record generation prompts and approved reference images in repository alongside asset manifest. Generate contact sheets in small coherent batches; inspect individual assets at actual scale, reject inconsistent or malformed outputs, regenerate as needed. No full pack generated until the layout gate is satisfied.

## Gates
1. Touchscreen-first gameplay and short contextual training integrated, stable CI, physical acceptance.
2. Inventory actual required slots and dimensions; approve a small reference sheet before producing the full pack.
3. Produce batches A–G, reviewing cohesion and legibility each time; do not blindly replace good assets.
4. Integrate incrementally with fallback, compare screenshots, check alpha/cropping, contrast, performance, APK size, offline behavior and unchanged saves/landmark positions.
5. Final phone review and owner approval before release or PR merge.

This document is the production plan, not a claim that generated assets exist. Existing native visuals remain functional until approved replacement artwork is integrated.
