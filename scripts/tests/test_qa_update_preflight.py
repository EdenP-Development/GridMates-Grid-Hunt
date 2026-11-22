import unittest
from scripts.qa_update_preflight import assess, parse_badging, parse_cert, validate_policy, UpdateNoGo

SIG = "a" * 64
BASE = {"application_id": "com.playereden.gridmates.gridhunt.qa",
        "version_code": 10022, "version_name": "v22",
        "signer_sha256": SIG, "android_min_sdk": 26}
CAND = dict(BASE, version_code=10023, version_name="v23")

class UpdateGateTests(unittest.TestCase):
    def test_same_signer_newer_code(self):
        self.assertEqual(assess(BASE, CAND), "IDENTITY_MATCH_AND_NEWER_CODE")

    def test_original_ephemeral_qa10_is_incompatible(self):
        old = dict(BASE, version_code=2, signer_sha256="b" * 64)
        self.assertEqual(assess(old, CAND), "SIGNER_MISMATCH")

    def test_signer_change_is_blocked(self):
        self.assertEqual(assess(BASE, dict(CAND, signer_sha256="b" * 64)), "SIGNER_MISMATCH")

    def test_downgrade_and_equal_code_blocked(self):
        self.assertEqual(assess(BASE, dict(CAND, version_code=10021)), "VERSION_CODE_NOT_HIGHER")
        self.assertEqual(assess(BASE, dict(CAND, version_code=10022)), "VERSION_CODE_NOT_HIGHER")

    def test_wrong_package_is_blocked(self):
        self.assertEqual(assess(BASE, dict(CAND, application_id="com.fake.qa")), "PACKAGE_MISMATCH")

    def test_sdk_drift_is_blocked(self):
        self.assertEqual(assess(BASE, dict(CAND, android_min_sdk=29)), "MIN_SDK_CHANGED")

    def test_cert_parser_requires_one_signer(self):
        line = "Signer #1 certificate SHA-256 digest: " + SIG + "\n"
        self.assertEqual(parse_cert(line), SIG)
        with self.assertRaises(UpdateNoGo):
            parse_cert("")
        with self.assertRaises(UpdateNoGo):
            parse_cert(line + line.replace("#1", "#2"))

    def test_badging_parse(self):
        text = ("package: name='com.playereden.gridmates.gridhunt.qa' versionCode='10023' "
                "versionName='vision23'\nsdkVersion:'26'\n")
        data = parse_badging(text)
        self.assertEqual(data["version_code"], 10023)
        self.assertEqual(data["android_min_sdk"], 26)

    def test_baseline_policy_rejects_missing_cert(self):
        with self.assertRaises(UpdateNoGo):
            validate_policy({"schema_version": 1, "channel": "qa",
                "application_id": "com.playereden.gridmates.gridhunt.qa",
                "android_min_sdk": 26, "version_code": 10022,
                "signer_sha256": "", "apk_sha256": "a"*64,
                "source_commit": "b"*40})

if __name__ == "__main__":
    unittest.main()
