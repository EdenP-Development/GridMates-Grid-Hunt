#!/usr/bin/env python3
"""Render draft coastal SVG masters into a review-only output directory.
Requires cairosvg and Pillow. Never modifies Android resources or source masters.
"""
import argparse
import hashlib
import json
from pathlib import Path
import xml.etree.ElementTree as ET
import cairosvg
from PIL import Image, ImageDraw
from io import BytesIO

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "artwork/worlds/whispering-coast"
IDS = ("beacon-point", "driftwood-cove", "shell-beach", "old-jetty",
       "gull-rock", "tidepool-bend", "dune-grass", "coastal-shrub",
       "coastal-rocks", "wave-accent")
SIZES = (24, 32, 48, 128, 256)
BACKGROUNDS = ("#E8D6A6", "#98C0C7", "#DCEBE7")
NS = "{http://www.w3.org/2000/svg}"

def digest(data):
    return hashlib.sha256(data).hexdigest()

def render(data, size):
    png = cairosvg.svg2png(bytestring=data, output_width=size, output_height=size)
    image = Image.open(BytesIO(png)).convert("RGBA")
    assert image.size == (size, size)
    alpha = image.getchannel("A")
    box = alpha.getbbox()
    assert box and alpha.getextrema()[0] == 0, "missing art or transparency"
    assert box[0] > 0 and box[1] > 0 and box[2] < size and box[3] < size, (size, box)
    return png, image, box

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--output", required=True, type=Path)
    args = parser.parse_args()
    output = args.output.resolve()
    assert not output.is_relative_to(ROOT / "app"), "review exports must stay outside app"
    output.mkdir(parents=True, exist_ok=True)
    assert {p.stem for p in (SOURCE / "masters").glob("*.svg")} == set(IDS)
    manifest = {}
    sheet = Image.new("RGB", (760, 80 + len(IDS) * 70), "#F4EDDA")
    draw = ImageDraw.Draw(sheet)
    draw.text((12, 12), "Whispering Coast - draft sprite review", fill="#315747")
    draw.text((12, 32), "Each background: 24 / 32 / 48 px at native size", fill="#315747")
    for row, name in enumerate(IDS):
        data = (SOURCE / "masters" / (name + ".svg")).read_bytes()
        root = ET.fromstring(data)
        assert root.tag == NS + "svg" and root.attrib.get("viewBox") == "0 0 128 128"
        assert not list(root.iter(NS + "text")), name
        for node in root.iter():
            assert node.tag not in {NS + "script", NS + "image", NS + "foreignObject", NS + "use"}, name
        record = {"source_sha256": digest(data), "exports": {}}
        previews = {}
        for size in SIZES:
            png, image, box = render(data, size)
            repeat, _, _ = render(data, size)
            assert png == repeat, "nondeterministic render"
            target = output / f"{name}-{size}.png"
            target.write_bytes(png)
            record["exports"][str(size)] = {"sha256": digest(png), "alpha_bbox": box}
            previews[size] = image
        y = 70 + row * 70
        draw.text((12, y + 24), name, fill="#315747")
        for i, color in enumerate(BACKGROUNDS):
            x = 150 + i * 200
            draw.rectangle((x, y, x + 185, y + 60), fill=color)
            for size, offset in zip(SIZES[:3], (12, 64, 122)):
                sheet.paste(previews[size], (x + offset, y + (60-size)//2), previews[size])
        manifest[name] = record
    sheet.save(output / "sprite-review.png")
    (output / "manifest.json").write_text(json.dumps(manifest, indent=2, sort_keys=True) + "\n")
    print(f"PASS: {len(IDS)} masters, {len(IDS)*len(SIZES)} transparent exports; visual approval still required")

if __name__ == "__main__":
    main()
