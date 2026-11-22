#!/usr/bin/env bash
# First actual Android emulator install-over proof for the exact archived APKs.
# NO adb uninstall, NO pm clear. Saves must survive package manager upgrade.
set -euo pipefail

PKG=com.playereden.gridmates.gridhunt.qa
ACTIVITY=com.playereden.gridmates.gridhunt.MainActivity
OLD=prior-qa22/GridHunt-VisionLab-QA.apk
NEW=candidate-qa23/GridHunt-VisionLab-QA.apk
OUT=update-evidence
mkdir -p "$OUT"
test -s "$OLD" && test -s "$NEW"

adb wait-for-device
boot="$(adb shell getprop sys.boot_completed | tr -d '\r')"
test "$boot" = 1

adb install "$OLD" | tee "$OUT/qa22-install.txt"
grep -qx 'Success' "$OUT/qa22-install.txt"
adb shell am start -W -n "$PKG/$ACTIVITY" | tee "$OUT/qa22-launch.txt"
grep -Fq 'Status: ok' "$OUT/qa22-launch.txt"
adb shell pidof "$PKG" >/dev/null
adb shell am force-stop "$PKG"

# Seed a simple app-private data canary and the REAL Grid Hunt chapter
# SharedPreferences path. Do this with app stopped, before any upgrade.
adb shell "run-as $PKG sh -c 'mkdir -p files shared_prefs; printf GRIDHUNT_QA22_CANARY > files/update-canary.txt'"
PREF_XML='<?xml version="1.0" encoding="utf-8" standalone="yes" ?><map><int name="hunt_index" value="1" /><int name="objective_index" value="2" /><int name="hint_stage" value="1" /></map>'
printf '%s\n' "$PREF_XML" | adb shell "run-as $PKG sh -c 'cat > shared_prefs/gridhunt_first_bearings_v1.xml'"
before="$(adb shell "run-as $PKG cat files/update-canary.txt" | tr -d '\r')"
test "$before" = GRIDHUNT_QA22_CANARY
adb exec-out run-as "$PKG" cat shared_prefs/gridhunt_first_bearings_v1.xml > "$OUT/preferences-before.xml"
grep -Fq 'name="hunt_index" value="1"' "$OUT/preferences-before.xml"
grep -Fq 'name="objective_index" value="2"' "$OUT/preferences-before.xml"

echo 'Installing QA23 OVER existing QA22, without uninstall or clearing data'
adb install -r "$NEW" | tee "$OUT/qa23-install-over.txt"
grep -qx 'Success' "$OUT/qa23-install-over.txt"
after="$(adb shell "run-as $PKG cat files/update-canary.txt" | tr -d '\r')"
test "$after" = "$before"
adb exec-out run-as "$PKG" cat shared_prefs/gridhunt_first_bearings_v1.xml > "$OUT/preferences-after-upgrade.xml"
cmp "$OUT/preferences-before.xml" "$OUT/preferences-after-upgrade.xml"

adb shell am start -W -n "$PKG/$ACTIVITY" | tee "$OUT/qa23-launch.txt"
grep -Fq 'Status: ok' "$OUT/qa23-launch.txt"
adb shell pidof "$PKG" >/dev/null
adb shell dumpsys package "$PKG" > "$OUT/package-after.txt"
grep -m 1 -F 'versionCode=10023' "$OUT/package-after.txt" > "$OUT/installed-version.txt"
# Avoid an arbitrary sleep for Async SharedPreferences migration;
# the chapter state should remain nonzero after QA23 actually starts.
sleep 3
adb exec-out run-as "$PKG" cat shared_prefs/gridhunt_first_bearings_v1.xml > "$OUT/preferences-after-launch.xml"
grep -Fq 'name="hunt_index" value="1"' "$OUT/preferences-after-launch.xml"
grep -Fq 'name="objective_index" value="2"' "$OUT/preferences-after-launch.xml"
adb shell screencap -p /sdcard/gridhunt-qa23.png
adb pull /sdcard/gridhunt-qa23.png "$OUT/after-update.png" >/dev/null

cat > "$OUT/RESULT.txt" <<EOF
ANDROID_EMULATOR_INSTALL_OVER_PASS
ACTUAL ARCHIVED APKs: QA22 -> QA23
PACKAGE: $PKG
VERSION CODE: 10022 -> 10023
adb install -r returned Success; no uninstall or pm clear
APP PRIVATE CANARY: survived
FIRST BEARINGS REAL SHARED PREFS: survived upgrade and app launch (hunt 1 / step 2, zero-based)
QA23 MAIN ACTIVITY: launched and remained running
SCHEME: Public disposable QA test key; never for release signing
SCOPE: Android 35 emulator only. This does NOT confirm the owner's phone installer,
its earlier installed APK identity or live player-created saves.
EOF
cat "$OUT/RESULT.txt"
