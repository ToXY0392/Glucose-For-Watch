---
name: Incident P0 (crash / fatal)
about: Crash fatal phone ou sync bloquante — gate G-X minimum
title: "[incident] "
labels:
  - bug
  - incident-p0
  - gate-blocker
body:
  - type: markdown
    attributes:
      value: |
        **Incident bloquant stabilité (K1).** Lier à un bloc du plan · fermer seulement après gate validée.
        Ne pas joindre glucose réel · credentials · logcat complet non redigé.

  - type: input
    id: incident_date
    attributes:
      label: Date incident
      placeholder: "2026-05-25"
    validations:
      required: true

  - type: dropdown
    id: bloc
    attributes:
      label: Bloc lié
      options:
        - X — Crash FGS
        - A — P0 fiabilité
        - B — Sync / wear
        - C — QA
        - S — Autre
    validations:
      required: true

  - type: textarea
    id: summary
    attributes:
      label: Summary
      description: Une phrase — stack trace type sans PII
      placeholder: "ForegroundServiceStartNotAllowedException in ActiveGlucoseSyncService.onCreate"
    validations:
      required: true

  - type: textarea
    id: environment
    attributes:
      label: Environment
      value: |
        - App version:
        - Phone / Android:
        - Watch / Wear OS:
        - Dexcom: G6 / G7 · region US/OUS
        - Trigger: boot / alarm / manual sync / overnight soak
    validations:
      required: true

  - type: textarea
    id: reproduction
    attributes:
      label: Reproduction steps
      placeholder: |
        1.
        2.
        3.
    validations:
      required: true

  - type: checkboxes
    id: mitigation
    attributes:
      label: Mitigation status
      options:
        - label: "Mitigation merged (PR #)"
          required: false
        - label: "X.6 — 30 min sync post-fix OK"
          required: false
        - label: "C.7 soak required before close"
          required: false

  - type: input
    id: evidence
    attributes:
      label: Evidence doc
      description: Lien vers docs/qa/incidents/ ou soak-runs/
      placeholder: "docs/qa/incidents/2026-05-25-app-crash.md"

  - type: dropdown
    id: gate_to_retest
    attributes:
      label: Gate minimum before close
      options:
        - G-X
        - G-A
        - G-C
        - G-M7
    validations:
      required: true
