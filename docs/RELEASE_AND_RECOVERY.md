# Grid Hunt release, signing and recovery policy

Updated 2026-11-24. Adopt [Eden Development Standards](https://github.com/PlayerEden/Eden-Development-Standards) and [central registry](https://github.com/PlayerEden/Eden-Development-Standards/blob/main/APP_REGISTRY.json). This project-specific supplement preserves existing identities.

## Verified inventory

- Persistent QA: com.playereden.gridmates.gridhunt.qa; public certificate SHA-256 0ff2e763e5667bdfda45aab662639ea6c1c7efa813f82cd4a5cdba8225febd52. Existing public test-only PKCS12 is tracked in the repository by explicit legacy design. It is compromised by design and unsuitable for trusted or production distribution. Do not rotate this lineage automatically or copy it into production. Only install trusted project test artifacts.
- Production: configured applicationId com.playereden.gridmates.gridhunt, versionName 0.3.0-gh02c, no release signer configured in inspected Gradle source. No accepted production APK, fingerprint or owner-controlled key verified. Do not generate a key until original distribution history and custody are established with owner.
- Legacy ephemeral preview QA (QA10 and earlier): same QA package, differing temporary certificates. QA10 fingerprint bf54f0d3b2603353f6b3aa9d1ecb2ab1485151c7f53e7d27952d3df05b032231. Cannot install fixed QA as update over incompatible historical signer. Preserve old installation; no uninstall/data-clear workaround. No independent preview package verified.
- QA292 verified source 63f0fc271eb731a1d9b6109ddd71164f4afdb19a, code 10292, name 0.3.0-gh02c-qa.292, APK SHA-256 8bf801d3df2a0b6e771b70b603f45f506426dee9ac839ee0c37cdf2bed6e3db8. [Full passing workflow](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45460697797): 32 Android tests, JVM tests, package/signature preflight and screenshot verification. [Artifact](https://github.com/PlayerEden/GridMates-Grid-Hunt/actions/runs/45460697797/artifacts/14951748911) expires 2026-12-08. Owner acceptance pending; not production.
- CI uses versionCode 10000 + workflow run number; local build defaults to 10000 and is not a distributed update. Current preflight compares QA22 pinned baseline, not necessarily last installed APK. Compare actual installed/last accepted APK before distribution; never claim the baseline alone proves all upgrade paths.

## Custody and recovery

Existing QA public test material is recoverable from source history; this is not confidential custody. No private production backup, private QA backup, repository secret availability or owner's local vault has been verified. No new keys or secrets created. Owner must inspect prior trusted computers/archives/password manager privately, and GitHub Settings > Secrets and variables > Actions for names only. Do not paste keys/passwords into chat or commit them. No access to owner's local files or vault is assumed.

1. Owner stores original existing private signing material (if located) in encrypted owner-controlled storage outside repositories; password manager stores password and private alias/recovery notes.
2. Make a second independently recoverable encrypted offline copy. For the legacy public QA lineage, preserve original test key and exact accepted APK separately for reproducibility, not as a security guarantee.
3. Privately verify both copies open and extract only public certificate SHA-256 using keytool, matching the known original fingerprint or original signed APK. Record date and verified/unverified custody status only. GitHub secrets and expiring CI artifacts are not backups.
4. For future private channels configure original signing material via Actions secrets with owner authorization. Fail closed on missing/mismatching key; restrict signing to trusted reviewed code. Never run private signing on untrusted PR code. No private signing workflow activated here.
5. Keep exact accepted APKs and verification receipts in owner-approved durable archive; download QA292 before artifact expiration if wanted. Do not publish Releases without owner approval.

## Save preservation and release gates

[QA22 to QA23 exact APK emulator rehearsal](qa/GH_QA22_TO_QA23_REAL_UPDATE_REHEARSAL.md) passed adb install -r, retained private canary and First Bearings synthetic SharedPreferences, and launched new APK. This is historical QA evidence only. QA292 has gameplay/rotation tests, not a fresh exact-binary upgrade rehearsal. Portable export/import not verified or implemented here. QA to production uses different package and cannot transfer private data automatically.

Preserve existing package, signer, version monotonicity, preference names, canonical coordinates and stable objective/qualification IDs. Next upgrade test must use exact previously accepted APK and new APK, seeded noncritical campaign, Academy qualification, Quick Play, custom session and settings; verify after launch, document hashes. Back up first where supported; do not clear/uninstall on rejection. Record exact installer error. Do not promise recovery of data without a verified export/import route.

Before each accepted distribution follow central release checklist, compare actual prior package/certificate/version and APK hashes, review CI and physical testing, obtain owner acceptance, archive exact binary and receipt, update registry and handoff. Production publication blocked pending custody, original identity determination, safe release signing and migration evidence.

## Next development

Continue current GH-V23 stabilization, physical QA292, generated artwork reference assets and approved asset batches. No gameplay or identity change required for standards adoption. Current draft PR #12 remains unmerged.
