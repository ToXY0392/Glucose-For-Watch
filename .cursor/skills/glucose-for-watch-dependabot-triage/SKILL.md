---
name: glucose-for-watch-dependabot-triage
description: Triages Dependabot PRs for Glucose For Watch — small scoped merges, no feature bloc mixing, coordinates with glucose-for-watch-dependency-advisor. Use when Dependabot opens PRs or when reviewing dependency upgrades.
disable-model-invocation: true
---

# Glucose For Watch Dependabot Triage

## Config

[.github/dependabot.yml](../../../.github/dependabot.yml) — Gradle + GitHub Actions, weekly, max 2 open PRs, labels `area:infra` + `bloc-s`.

## Rules

1. **One concern per PR** — never merge Dependabot with feature bloc work.
2. **AGP / Gradle / Kotlin major bumps** — invoke `glucose-for-watch-dependency-advisor` first; may need compat matrix update (`glucose-for-watch-compat-matrix-maintainer`).
3. **GitHub Actions bumps** — run `verify_ci.sh` after merge; check workflow breaking changes.
4. **Transitive only** — prefer merge if CI green and no AGP conflict.
5. During **soak C.7** — merge low-risk PRs only if CI green; no local install required for CI-only deps.

## Triage workflow

1. Read Dependabot PR title and release notes link.
2. Classify: **patch** / **minor** / **major** / **actions-only**.
3. Run:
   ```bash
   bash scripts/dev/verify_ci.sh
   ```
4. For major AGP/Gradle: defer until dedicated `chore/deps-*` branch window.
5. Label PR `bloc-s`, milestone none or current patch release.

## Output

| PR | Risk | Recommendation |
|----|------|----------------|
| #N | low/medium/high | merge / defer / needs advisor |
