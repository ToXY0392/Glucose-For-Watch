---
name: widget-g7-usb-detach-handoff-writer
description: Detecte les deconnexions USB du telephone ou de la montre via ADB et ajoute automatiquement une entree horodatee dans docs/developer-handoff.md pour faciliter la reprise.
disable-model-invocation: true
---

# Widget G7 USB Detach Handoff Writer

## Objectif
Tracer automatiquement les deconnexions USB des appareils de test dans le handoff developpeur.

## Cibles
- `docs/developer-handoff.md`
- `.cursor/state/usb-state.json`

## Preconditions
- `WIDGETG7_PHONE_SERIAL` et `WIDGETG7_WATCH_SERIAL` definis.
- `adb` disponible dans le PATH.

## Workflow
1. Lire l'etat USB courant avec `adb devices -l`.
2. Comparer au dernier etat connu (fichier `.cursor/state/usb-state.json`).
3. Detecter les transitions `connected -> disconnected`.
4. Ajouter un incident dans `docs/developer-handoff.md` (table `Incidents recents`).
5. Appliquer une deduplication de 30 minutes par appareil.
6. Mettre a jour l'etat local.

## Format incident
`| YYYY-MM-DD | USB detach detecte (phone/watch) | Ouvert (rebrancher + verifier sync) |`

## Regles
- Ne pas ecrire d'incident si les serials ne sont pas configures.
- Ne pas dupliquer un incident identique dans la fenetre de deduplication.
- Preserver la structure markdown du handoff.
