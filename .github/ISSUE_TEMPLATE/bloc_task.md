---
name: Bloc task (plan)
about: Tâche atomique du ACTION-PLAN (X.5a, C.7, B.1…)
title: "[bloc-] "
labels:
  - enhancement
body:
  - type: markdown
    attributes:
      value: |
        Tâche alignée sur [ACTION-PLAN](docs/plan/ACTION-PLAN.md) · checklist merge : [PR-CHECKLIST](docs/plan/PR-CHECKLIST.md).
        **Ne pas inclure** credentials Dexcom ni glucose réel.

  - type: dropdown
    id: bloc
    attributes:
      label: Bloc
      options:
        - S — Stabilité transverse
        - X — Crash FGS
        - A — P0 fiabilité
        - M — Mock user / HomeViewModel
        - B — Sync / wear
        - C — QA hardware
        - D — Qualité / tests
        - F0 — Compose Gradle
        - F1 — Écrans simples Compose
        - F2 — Dexcom / WatchSetup Compose
        - F3 — Home Compose
        - F4 — Installer
        - F5 — Cleanup XML
    validations:
      required: true

  - type: input
    id: task_id
    attributes:
      label: Task ID
      description: "Ex. X.5a, C.7, B.1"
      placeholder: "X.5a"
    validations:
      required: true

  - type: dropdown
    id: gate
    attributes:
      label: Gate cible
      options:
        - G-X
        - G-A
        - G-M
        - G-B
        - G-C
        - G-D
        - G-M7 (v0.5.0)
        - G-F0
        - G-F1
        - G-F2
        - G-F3
        - G-M8 (v0.6.0)
        - —
    validations:
      required: true

  - type: dropdown
    id: milestone
    attributes:
      label: Milestone
      options:
        - v0.5.0 — Stable sideload
        - v0.6.0 — Compose phone
        - —
    validations:
      required: true

  - type: checkboxes
    id: sync_touch
    attributes:
      label: Sync
      options:
        - label: "Touch sync (mobile/sync, wear/, feature/sync) — S1–S3 retest required"
          required: false

  - type: checkboxes
    id: hardware_qa
    attributes:
      label: Hardware QA
      options:
        - label: "Requires phone + watch adb session"
          required: false

  - type: textarea
    id: definition_of_done
    attributes:
      label: Definition of Done
      description: Copier depuis ACTION-PLAN ou préciser critères
      placeholder: |
        - [ ] Critère 1
        - [ ] stability-gate.ps1 PASS
        - [ ] PROGRESS.md updated after merge
    validations:
      required: true

  - type: input
    id: branch
    attributes:
      label: Branch name
      placeholder: "fix/bloc-x-fgs-crash"

  - type: input
    id: effort
    attributes:
      label: Effort (hours)
      placeholder: "4"

  - type: textarea
    id: notes
    attributes:
      label: Notes / dependencies
      placeholder: "Blocked by G-X · parallel with M after G-A"
