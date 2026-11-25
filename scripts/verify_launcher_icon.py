"""Verify exact original/derived launcher bytes and compiled Android icon resources."""
import hashlib
import json
import pathlib
import re
import struct
import subprocess
import sys
import xml.etree.ElementTree as ET
import zipfile

ROOT = pathlib.Path(__file__).resolve().parents[1]
PNG = b"\x89PNG\r\n\x1a\n"
NS = "{http://schemas.android.com/apk/res/android}"

def require(condition, message):
    if not condition:
        raise SystemExit("Launcher verification failed: " + str(message))

def source_check():
    receipt = json.loads((ROOT / "artwork/launcher/v2/manifest.json").read_text())
    for item in receipt["files"]:
        data = (ROOT / item["path"]).read_bytes()
        require(len(data) == item["bytes"], (item["path"], "byte count"))
        require(hashlib.sha256(data).hexdigest() == item["sha256"], (item["path"], "SHA256"))
        require(data[:8] == PNG and data[12:16] == b"IHDR", item["path"])
        width, height, depth, color_type = struct.unpack(">IIBB", data[16:26])
        require((width, height, depth, color_type) == (item["width"], item["height"], 8, item["png_color_type"]), (item["path"], "PNG header"))
        print("PASS source:", item["path"], item["sha256"])
    app = ET.parse(ROOT / "app/src/main/AndroidManifest.xml").getroot().find("application")
    require(app is not None, "no application element")
    for attr in ("icon", "roundIcon"):
        require(app.get(NS + attr) == "@mipmap/gh_launcher", attr)
    adaptive = ET.parse(ROOT / "app/src/main/res/mipmap-anydpi-v26/gh_launcher.xml").getroot()
    require(adaptive.tag == "adaptive-icon", "adaptive root")
    for tag, expected in (("background", "@color/gh_launcher_background"), ("foreground", "@drawable/gh_launcher_foreground")):
        child = adaptive.find(tag)
        require(child is not None and child.get(NS + "drawable") == expected, tag)
    print("PASS source manifest and adaptive XML")

def resource(table, name):
    match = re.search(
        r"(?m)^\s*resource\s+(0x[0-9a-fA-F]+)\s+(?:[\w.]+:)?" + re.escape(name)
        + r"(?=\s|$)[^\n]*\n(.*?)(?=^\s*resource\s+0x|^\s*type\s+|^\s*Package\s+|\Z)",
        table, re.S | re.M,
    )
    require(match is not None, "missing compiled resource " + name)
    return match.group(1).lower(), re.findall(r"\b(res/[^\s()]+)", match.group(2))

def apk_check(tools, apk):
    aapt = str(tools / "aapt")
    table = subprocess.check_output([str(tools / "aapt2"), "dump", "resources", str(apk)], text=True)
    icon_id, icon_paths = resource(table, "mipmap/gh_launcher")
    fg_id, fg_paths = resource(table, "drawable/gh_launcher_foreground")
    bg_id, _ = resource(table, "color/gh_launcher_background")
    manifest = subprocess.check_output([aapt, "dump", "xmltree", str(apk), "AndroidManifest.xml"], text=True)
    for attr in ("icon", "roundIcon"):
        match = re.search(r"A:\s+android:" + attr + r"\([^)]*\)\s*=\s*@(0x[0-9a-fA-F]+)", manifest)
        require(match is not None and match.group(1).lower() == icon_id, (apk, attr, icon_id))
    with zipfile.ZipFile(apk) as z:
        require(bool(icon_paths) and bool(fg_paths), (apk, "missing file mapping"))
        for path in fg_paths:
            require(path in z.namelist() and z.read(path).startswith(PNG), (apk, path, "foreground PNG"))
        # Use the file content rather than guessing AAPT's archive filenames.
        png_paths, xml_paths = [], []
        for path in icon_paths:
            require(path in z.namelist(), (apk, path))
            data = z.read(path)
            if data.startswith(PNG):
                png_paths.append(path)
            elif data.startswith(b"\x03\x00\x08\x00"):
                xml_paths.append(path)
        require(bool(png_paths) and bool(xml_paths), (apk, "legacy PNG or adaptive XML missing", icon_paths))
        for path in xml_paths:
            compiled = subprocess.check_output([aapt, "dump", "xmltree", str(apk), path], text=True)
            require("E: adaptive-icon" in compiled and "E: background" in compiled and "E: foreground" in compiled, (apk, path, "adaptive structure"))
            require("@" + fg_id in compiled.lower() and "@" + bg_id in compiled.lower(), (apk, path, "adaptive layer references"))
    print("PASS compiled launcher:", apk, icon_paths, fg_paths)

if __name__ == "__main__":
    source_check()
    if len(sys.argv) > 1:
        require(len(sys.argv) >= 3, "usage: verify_launcher_icon.py <build-tools-dir> <apk> ...")
        for apk in sys.argv[2:]:
            apk_check(pathlib.Path(sys.argv[1]), pathlib.Path(apk))
