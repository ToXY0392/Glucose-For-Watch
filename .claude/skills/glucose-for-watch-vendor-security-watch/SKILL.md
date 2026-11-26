---
name: glucose-for-watch-vendor-security-watch
description: Monitors Android, Wear OS, AGP, Gradle, Kotlin and Dexcom Share updates plus security advisories (CVEs) for Glucose For Watch, classifies real impact (do now / plan / ignore) and can pre-fill the CHANGELOG "Upstream changes" section. Use when running the weekly vendor or security watch, before a release tag, or when the user asks what changed upstream or which CVEs affect the repo.
disable-model-invocation: true
---

# Glucose For Watch Vendor & Security Watch

Replaces `vendor-watch`, `security-bulletin` and `release-notes-curator` (one inventory, one bulletin).

## Modes

| Mode | Output | Edits |
|------|--------|-------|
| `vendor` (default) | Impact bulletin: Do now / Plan / Ignore | none |
| `security` | CVE priority table | none |
| `changelog` | `Upstream changes` section for `CHANGELOG.md` | only if the user authorises editing |
| `delta` | Same as above, only items newer than the last run | none |

## Step 1 — Inventory (shared by all modes)

Read effective versions — never guess them:

- `build.gradle.kts`, `settings.gradle.kts`, `**/build.gradle.kts`
- `gradle/wrapper/gradle-wrapper.properties`
- `docs/dev/setup.md`, `README.md`

## Step 2 — Collect

| Source | Vendor | Security |
|--------|--------|----------|
| Android Developers / Jetpack release notes | ✓ | ✓ (Android security bulletins) |
| Wear OS documentation | ✓ | |
| AGP, Gradle, Kotlin release notes | ✓ | ✓ (advisories for build tooling) |
| Dexcom Share documentation | ✓ | |
| GitHub advisories for declared dependencies | | ✓ |

Out of scope while Phase 0 is blocked: Dexcom G7 / direct BLE sensor protocol items.

## Step 3 — Classify (real impact only)

- **Vendor:** breaking change, deprecation, min SDK / tooling compatibility, API behaviour change.
- **Security:** severity, exploitability *in this app's context*, exposure (build-time vs runtime) → `Critical` / `Important` / `Minor`.
- Drop anything with no concrete effect on this repo.

## Output

**vendor**
- Executive summary (3–5 lines)
- **Do now** (impact · action · urgency) · **Plan** (window) · **Ignore** (reason)
- Sources consulted (links)

**security**

| CVE / advisory | Component | Severity | Exposure | Fix path |
|----------------|-----------|----------|----------|----------|

Then: immediate actions · planned actions · temporarily accepted risks.

**changelog** — under `## Upstream changes`, one bullet per item: source · change · Glucose For Watch impact · action. Preserve existing history.

## Rules

- Cite a source for every important point; do not invent versions.
- Prefer minimal, reversible actions; upgrades go through `glucose-for-watch-dependency-advisor`.
- Never paste credentials, tokens or real glucose values into bulletins.
