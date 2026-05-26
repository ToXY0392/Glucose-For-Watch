---
name: glucose-for-watch-security-bulletin
description: Tracks CVEs and security advisories for Android dependencies and build tools used by Widget G7, then prioritizes fixes by severity and exploitability.
disable-model-invocation: true
---

# Widget G7 Security Bulletin

## Objective
Provide action-oriented security monitoring for mobile + wear.

## Targets
- Android/Wear runtime dependencies
- Build dependencies (AGP, Gradle, plugins)
- Critical tools and build chains

## Workflow
1. Inventory components used by the repo.
2. Collect relevant security advisories and CVEs.
3. Evaluate each item by:
   - severity
   - exploitability in the Widget G7 context
   - real exposure (build-time / runtime)
4. Classify:
   - `Critical`
   - `Important`
   - `Minor`
5. Propose minimal fix + release window.

## Expected output
- **Priority table** (CVE, component, score/severity, impact)
- **Immediate actions**
- **Planned actions**
- **Temporarily accepted risks**

## Rules
- Do not report CVEs outside the repo's technical context.
- Cite advisory sources.
- Always indicate a realistic fix path.
