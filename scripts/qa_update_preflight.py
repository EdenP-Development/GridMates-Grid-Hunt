#!/usr/bin/env python3
"""Grid Hunt read-only Android QA update preflight.

Compares actual signed candidate APK metadata with a pinned known-build baseline,
and optionally with the EXACT archived baseline APK. Does not install, sign, or
modify installed data. Compatibility does not prove the user's installed identity.
"""
import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess
import sys

CERT = re.compile(r"^Signer #\d+ certificate SHA-256 digest: ([a-fA-F0-9:]+)\s*$", re.M)
PACKAGE = re.compile(r"^package: (.+)$", re.M)
FIELDS = re.compile(r"([A-Za-z][A-Za-z0-9]*)='([^']*)'")
SDK = re.compile(r"^sdkVersion:'(\d+)'\s*$", re.M)

class UpdateNoGo(ValueError):
    pass

def digest(path):
    h = hashlib.sha256()
    with open(path, "rb") as stream:
        for part in iter(lambda: stream.read(1024 * 1024), b""):
            h.update(part)
    return h.hexdigest()

def parse_cert(output):
    found = CERT.findall(output)
    if len(found) != 1:
        raise UpdateNoGo("Cannot establish a single Android signing certificate")
    value = found[0].replace(":", "").lower()
    if not re.fullmatch(r"[a-f0-9]{64}", value):
        raise UpdateNoGo("Invalid signing certificate fingerprint")
    return value

def parse_badging(output):
    packages = PACKAGE.findall(output)
    sdks = SDK.findall(output)
    if len(packages) != 1 or len(sdks) != 1:
        raise UpdateNoGo("Package identity or minimum SDK missing")
    fields = dict(FIELDS.findall(packages[0]))
    if not all(k in fields for k in ("name", "versionCode", "versionName")):
        raise UpdateNoGo("Android package metadata incomplete")
    if not re.fullmatch(r"[1-9][0-9]*", fields["versionCode"]):
        raise UpdateNoGo("Invalid Android versionCode")
    return dict(
        application_id=fields["name"],
        version_code=int(fields["versionCode"]),
        version_name=fields["versionName"],
        android_min_sdk=int(sdks[0]),
    )

def run(command):
    try:
        return subprocess.run(command, check=True, text=True,
                              capture_output=True, timeout=90).stdout
    except (OSError, subprocess.SubprocessError):
        raise UpdateNoGo("Android metadata or signing tool failed") from None

def inspect_apk(path, aapt, apksigner):
    if not path.is_file() or not (0 < path.stat().st_size < 512 * 1024 * 1024):
        raise UpdateNoGo("Signed APK missing, empty or too large")
    badging = run([str(aapt), "dump", "badging", str(path)])
    cert = run([str(apksigner), "verify", "--min-sdk-version", "26",
                "--print-certs", str(path)])
    return dict(parse_badging(badging), signer_sha256=parse_cert(cert),
                apk_sha256=digest(path))

def validate_policy(policy):
    required = {
        "schema_version": 1, "channel": "qa",
        "application_id": "com.playereden.gridmates.gridhunt.qa",
        "android_min_sdk": 26,
    }
    for key, expected in required.items():
        if policy.get(key) != expected:
            raise UpdateNoGo("Pinned baseline policy is invalid: " + key)
    for key in ("signer_sha256", "apk_sha256", "source_commit"):
        length = 40 if key == "source_commit" else 64
        if not re.fullmatch(r"[a-f0-9]{" + str(length) + r"}", str(policy.get(key))):
            raise UpdateNoGo("Pinned baseline hash malformed: " + key)
    if type(policy.get("version_code")) is not int or policy["version_code"] < 1:
        raise UpdateNoGo("Pinned version code invalid")
    return policy

def assess(previous, candidate):
    if previous["application_id"] != "com.playereden.gridmates.gridhunt.qa":
        return "PREVIOUS_PACKAGE_MISMATCH"
    if candidate["application_id"] != previous["application_id"]:
        return "PACKAGE_MISMATCH"
    if previous["signer_sha256"] != candidate["signer_sha256"]:
        return "SIGNER_MISMATCH"
    if candidate["version_code"] <= previous["version_code"]:
        return "VERSION_CODE_NOT_HIGHER"
    if candidate["android_min_sdk"] != previous["android_min_sdk"]:
        return "MIN_SDK_CHANGED"
    return "IDENTITY_MATCH_AND_NEWER_CODE"

def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--baseline", type=Path, required=True)
    parser.add_argument("--candidate", type=Path, required=True)
    parser.add_argument("--previous", type=Path)
    parser.add_argument("--aapt", type=Path, required=True)
    parser.add_argument("--apksigner", type=Path, required=True)
    parser.add_argument("--expected-version-code", type=int, required=True)
    parser.add_argument("--source-sha", required=True)
    parser.add_argument("--output", type=Path)
    args = parser.parse_args(argv)
    try:
        policy = validate_policy(json.loads(args.baseline.read_text()))
        if not re.fullmatch(r"[a-f0-9]{40}", args.source_sha):
            raise UpdateNoGo("Exact source SHA required")
        previous = {k: policy[k] for k in ("application_id", "version_code",
                   "version_name", "signer_sha256", "android_min_sdk", "apk_sha256")}
        if args.previous is not None:
            inspected = inspect_apk(args.previous, args.aapt, args.apksigner)
            if inspected != previous:
                raise UpdateNoGo("Archived previous APK DOES NOT match pinned QA22 baseline")
        candidate = inspect_apk(args.candidate, args.aapt, args.apksigner)
        if candidate["version_code"] != args.expected_version_code:
            raise UpdateNoGo("Candidate version differs from CI expected code")
        result = assess(previous, candidate)
        receipt = {
            "status": "PREFLIGHT_PASS" if result == "IDENTITY_MATCH_AND_NEWER_CODE" else "PREFLIGHT_NO_GO",
            "reason": result,
            "baseline": previous,
            "candidate": candidate,
            "source_sha": args.source_sha,
            "archived_previous_apk_checked": args.previous is not None,
            "phone_install_over_tested": False,
            "save_retention_tested": False,
            "security_note": "Signing key is public and suitable for test-only QA, not release.",
        }
        if args.output:
            args.output.write_text(json.dumps(receipt, indent=2, sort_keys=True) + "\n")
        print(json.dumps(receipt, indent=2, sort_keys=True))
        return 0 if receipt["status"] == "PREFLIGHT_PASS" else 2
    except (UpdateNoGo, OSError, ValueError, KeyError, TypeError) as err:
        print("PREFLIGHT_NO_GO: " + str(err), file=sys.stderr)
        return 2

if __name__ == "__main__":
    sys.exit(main())
