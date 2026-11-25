#!/usr/bin/env python3
"""Actual QA APK replacement on a disposable emulator; never clears app data."""
import pathlib
import subprocess
import sys
import time
import xml.etree.ElementTree as ET

PACKAGE = "com.playereden.gridmates.gridhunt.qa"
FIXTURE = {
    "gridhunt_mixed_woodland_v1": {"step": 4},
    "gridhunt_woodland_supplies_v1": {"step": 4},
    "gridhunt_woodland_return_v1": {"step": 2},
    "gridhunt_first_bearings_v1": {"hunt_index": 0, "objective_index": 1},
    "gridhunt_reverse_v1": {"step": 2, "answer": "24"},
    "gridhunt_identify_v1": {"step": 2},
    "gridhunt_treasure_checklist_v1": {"completed_ids": "mushroom-nook,buttercup-glade"},
    "gridhunt_academy_exploration_v1": {
        "earned": {"north", "south", "grid-order", "grid-practice", "grid-field", "woodland-field", "woodland-symbol", "woodland-route", "woodland-deduction"},
        "graduation": {"grad-start", "grad-route", "grad-symbol", "grad-deduction"},
        "qualifications": {"junior-explorer-v1"},
    },
    "gridhunt_custom_v1": {"mode": "grid", "count": 3, "codes": "2437,2538,2639", "kinds": "", "step": 1},
    "gridhunt_visual_lab_v1": {"haptics": False, "map_look": "FIELD_GUIDE"},
    "gridhunt_ui_preferences": {"precise_selection": True},
}

def adb(*args, data=None):
    return subprocess.run(["adb", *args], input=data, capture_output=True, check=True).stdout

def xml_bytes(values):
    root = ET.Element("map")
    for key, value in values.items():
        attrs = {"name": key}
        if isinstance(value, bool):
            ET.SubElement(root, "boolean", attrs | {"value": str(value).lower()})
        elif isinstance(value, int):
            ET.SubElement(root, "int", attrs | {"value": str(value)})
        elif isinstance(value, set):
            node = ET.SubElement(root, "set", attrs)
            for item in sorted(value):
                ET.SubElement(node, "string").text = item
        else:
            ET.SubElement(root, "string", attrs).text = value
    return ET.tostring(root, encoding="utf-8", xml_declaration=True)

def read_store(name):
    root = ET.fromstring(adb("exec-out", "run-as", PACKAGE, "cat", "shared_prefs/" + name + ".xml"))
    result = {}
    for node in root:
        key = node.attrib["name"]
        if node.tag == "int":
            result[key] = int(node.attrib["value"])
        elif node.tag == "boolean":
            result[key] = node.attrib["value"] == "true"
        elif node.tag == "set":
            result[key] = {child.text or "" for child in node}
        elif node.tag == "string":
            result[key] = node.text or ""
        else:
            raise AssertionError("Unexpected XML type: " + node.tag)
    return result

def verify(stage):
    for name, expected in FIXTURE.items():
        actual = read_store(name)
        assert actual == expected, (stage, name, expected, actual)
    print("PASS:", stage, len(FIXTURE), "exact preference maps", flush=True)

def version():
    output = adb("shell", "dumpsys", "package", PACKAGE).decode()
    import re
    return int(re.search(r"versionCode=(\d+)", output).group(1))

def launch_home():
    adb("shell", "am", "start", "-W", "-n", PACKAGE + "/com.playereden.gridmates.gridhunt.MainActivity")
    for _ in range(12):
        time.sleep(2)
        adb("shell", "uiautomator", "dump", "/sdcard/gridhunt-upgrade-ui.xml")
        screen = adb("shell", "cat", "/sdcard/gridhunt-upgrade-ui.xml").decode()
        if "2 of 3 chapters complete" in screen and "2 of 4 objectives solved" in screen:
            print("PASS: cold launch shows restored Adventure progress", flush=True)
            return
    raise AssertionError("Restored Home progress not visible")

def main():
    old, new = map(pathlib.Path, sys.argv[1:3])
    assert old.is_file() and new.is_file()
    assert PACKAGE not in adb("shell", "pm", "list", "packages", PACKAGE).decode(), "Expected fresh disposable emulator"
    print(adb("install", str(old)).decode(), flush=True)
    old_version = version()
    assert old_version == 10345, old_version
    adb("shell", "am", "force-stop", PACKAGE)
    adb("shell", "run-as", PACKAGE, "mkdir", "-p", "shared_prefs")
    for name, values in FIXTURE.items():
        adb("shell", "-T", "run-as " + PACKAGE + " sh -c 'cat > shared_prefs/" + name + ".xml'", data=xml_bytes(values))
    verify("baseline installed and fixture seeded")
    launch_home()
    adb("shell", "am", "force-stop", PACKAGE)
    verify("baseline cold launch")
    print(adb("install", "-r", str(new)).decode(), flush=True)
    assert version() > old_version, "Candidate version must increase"
    verify("after actual APK replacement, before launch")
    launch_home()
    adb("shell", "am", "force-stop", PACKAGE)
    verify("candidate cold launch")
    print("PASS: signed APK install-over and cold launch, no data clear or uninstall", flush=True)

if __name__ == "__main__":
    main()
