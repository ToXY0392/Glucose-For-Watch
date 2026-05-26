---
name: Incident P0 (crash / fatal)
about: Fatal phone crash or blocking sync — minimum gate G-X
title: "[incident] "
labels:
  - bug
  - incident-p0
  - gate-blocker
body:
  - type: markdown
    attributes:
      value: |
        **Stability-blocking incident (K1).** Link to a plan bloc · close only after gate validated.
        Do not attach real glucose values · credentials · unredacted full logcat.

  - type: input
    id: incident_date
    attributes:
      label: Incident date
      placeholder: "2026-05-25"
    validations:
      required: true

  - type: dropdown
    id: bloc
    attributes:
      label: Related bloc
      options:
        - X — FGS crash
        - A — P0 reliability
        - B — Sync / wear
        - C — QA
        - S — Other
    validations:
      required: true

  - type: textarea
    id: summary
    attributes:
      label: Summary
      description: One sentence — typical stack trace without PII
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
      description: Link to docs/qa/incidents/ or soak-runs/
      placeholder: "docs/qa/incidents/2026-05-25-app-crash.md"

  - type: dropdown
    id: gate_to_retest
    attributes:
      label: Minimum gate before close
      options:
        - G-X
        - G-A
        - G-C
        - G-M7
    validations:
      required: true
