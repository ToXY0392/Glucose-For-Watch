<h1 align="center">🧾 Release Notes</h1>

<p align="center">
  Historique court des décisions et validations importantes
</p>

---

## 🟢 Console

```text
╭─ Timeline ─────────────────────────────╮
│  V1                  base publiable    │
│  Dexcom sessions     auth plus claire  │
│  Avril 2026          UX + sync         │
│  30 avril            sync active       │
│  30 avril            batterie + docs   │
╰────────────────────────────────────────╯
```

---

## V1

| Champ | Détail |
| --- | --- |
| Commit | `3b1b53b` |
| Scope | Première version publiable |

Inclus :

- app Android téléphone ;
- app Wear OS ;
- sync Dexcom Share vers Wear OS ;
- tile glucose ;
- complication selon cadran compatible ;
- configuration Dexcom ;
- aide de configuration montre.

---

## Dexcom Session Handling

| Champ | Détail |
| --- | --- |
| Commit | `bc434ef` |
| Objectif | Session Dexcom plus robuste |

Changements :

- distinction erreur réseau / erreur identifiants ;
- messages utilisateur plus clairs ;
- bouton de déconnexion ;
- persistance de session améliorée.

---

## Mise À Jour Avril 2026

| Sujet | Changement |
| --- | --- |
| UX | Splash simplifié, accueil centré sur la montre |
| Dexcom | Écran `Connexion Dexcom` |
| Juridique | Textes acceptés avant connexion |
| Sync | Rythme rapproché du G7 |
| Montre | Test de liaison, multi-montres |
| Wear | Refresh visuellement corrigé |

Validation connue :

- APK mobile debug installé sur Pixel 8a ;
- APK Wear debug installé sur Pixel Watch 2 ;
- tile glucose validée avec `mg/dL`.

---

## Documentation Sync - 30 Avril 2026

| Document | Rôle |
| --- | --- |
| [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md) | Décision téléphone -> Wear OS |
| [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md) | Cadre du mode direct |
| [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md) | Plan si le direct devient viable |
| [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md) | Protocole BLE |

Décision :

```text
mode fiable = téléphone -> Wear OS
mode direct = expérimental
```

---

## Sync Solide - 30 Avril 2026

| Changement | Effet |
| --- | --- |
| `sequenceId` | Trace chaque push |
| Ack montre | Confirme la livraison |
| Repush borné | Répare un ack manquant |
| Service foreground | Maintient la sync active |
| Fallback WorkManager / AlarmManager | Filet de secours |
| Logs nettoyés | Moins de données sensibles |

Limites :

- Dexcom Share peut publier avec retard ;
- Android peut limiter l'app sans exemption batterie ;
- la veille longue reste à valider.

---

## Reprise 1-5 - 30 Avril 2026

| Sujet | Résultat |
| --- | --- |
| Batterie | Permission + bouton `Autoriser la sync en veille` |
| Multi-montres | Ciblage `targetNodeId` |
| Wear | Filtrage des paquets non destinés à la montre locale |
| Juridique | Checklist avant diffusion |
| Docs | Simplification et nouvelle structure |

Validation :

- build debug mobile + wear OK ;
- APK installés sur appareils de test ;
- `ActiveGlucoseSyncService` confirmé en foreground ;
- dernier push et ack confirmés.

Restes à faire :

```text
test veille longue
test avec deux montres
champs juridiques à compléter
```
