# Fixed update signing for Grid Hunt QA APKs

**Implemented:** 2026-11-22. **QA only; explicitly NOT production.**

## Why this exists
Earlier debug APKs were signed with new auto-generated key material on each GitHub hosted runner. Android requires the **same application ID**, **matching signing certificate** and a **nondecreasing/usually increasing versionCode** to install an update without uninstalling. QA10 and earlier therefore cannot update in place.

## Our practical QA-only approach
This repository intentionally contains a **publicly known, disposable QA keystore** at `.github/qa-signing/gridhunt-public-qa.p12`. It is **not confidential** and MUST NEVER sign production packages, personal data apps, client releases or anything distributed to people who need a trustworthy app publisher identity.

- Debug package: `com.playereden.gridmates.gridhunt.qa`
- New disposable QA signing certificate SHA-256: `0ff2e763e5667bdfda45aab662639ea6c1c7efa813f82cd4a5cdba8225febd52`
- Key alias/password are public by design; no real release passwords or secrets are in the repository.
- Android QA version code: `10000 + GITHUB_RUN_NUMBER`, so Actions builds in this workflow increase monotonically (as long as its run-number continuity is preserved).
- APK version string also identifies the build run. CI checks APK signature fingerprint before artifact upload and captures actual package/version from `aapt`.
- **One final uninstall is necessary** to move from QA10's ephemeral key to the fixed public QA signing key. Subsequent builds using this **exact key, package ID and higher version code** should install over the prior QA build while retaining local app data.

## Security warning and later release plan
**A public signing key is deliberately insecure for distribution.** Anyone can copy this known private key and sign APKs that Android could accept as updates to the QA app. Never install QA files from untrusted sources. This is acceptable only as a temporary, local, clearly labeled test-app arrangement. Use a **private, secret-managed production keystore** for the actual release package `com.playereden.gridmates.gridhunt`. Do not make release signing depend on this public key.

If secure GitHub Actions repository secrets become accessible, migrate to a **private stable QA key** under a *new QA application ID* to avoid ambiguous trust and require one final migration; otherwise retain this explicitly insecure QA test key only during development.

Do not rename/recreate the workflow or reset its run numbering without reviewing versionCode monotonicity. See [GitHub workflow](../../.github/workflows/gh01-android-qa.yml).

**Not yet proven:** this change requires a successful Android build and two successive stable-signed installs to confirm update-in-place on a real phone. Source change is not acceptance evidence.
