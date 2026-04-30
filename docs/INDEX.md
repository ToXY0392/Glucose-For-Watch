<h1 align="center">🗂️ Documentation Widget G7</h1>

<p align="center">
  Une doc courte pour utiliser · une doc claire pour reprendre · une doc stricte pour ne pas déraper
</p>

<p align="center">
  <a href="../README.md">🏠 README</a>
  ·
  <a href="NOTICE_UTILISATEUR.md">⚡ Notice</a>
  ·
  <a href="MODE_D_EMPLOI.md">📘 Mode d'emploi</a>
  ·
  <a href="REPRISE_PROJET.md">🧭 Reprise</a>
  ·
  <a href="RELEASE_NOTES.md">🧾 Versions</a>
</p>

---

## 🟢 Vue Rapide

```text
╭─ Widget G7 ────────────────────────────╮
│ Mode fiable : téléphone -> Wear OS     │
│ Mode direct : expérimental, hors app   │
│ Priorité   : sync active stable        │
│ Risque     : données médicales         │
╰────────────────────────────────────────╯
```

| Panneau | Signal |
| --- | --- |
| 📱 **Produit** | Installation, usage, reprise |
| 🔁 **Sync** | Dexcom Share, téléphone, Wear OS |
| 🧪 **Direct capteur** | Recherche, décision, spike BLE |
| 🎨 **Design** | UI claire, médicale, peu bavarde |
| 🔐 **Juridique** | Textes à compléter avant diffusion |

---

## ⚡ À lire en premier

| Document | Pourquoi |
| --- | --- |
| [README projet](../README.md) | Présentation, architecture, démarrage |
| [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) | Version courte pour l'utilisateur |
| [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) | Parcours complet d'installation |
| [REPRISE_PROJET.md](REPRISE_PROJET.md) | État actuel et prochaines priorités |

---

## 🔁 Sync et mode direct

| Document | Contenu |
| --- | --- |
| [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md) | Pourquoi le téléphone reste la source principale |
| [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md) | Options et décision pour le direct capteur |
| [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md) | Plan si le direct devient viable |
| [SPIKE_BLE_WEAR_COLLECTOR.md](SPIKE_BLE_WEAR_COLLECTOR.md) | Protocole du premier test BLE |

```text
Ne pas coder le direct capteur dans l'app principale
tant que le spike BLE Pixel Watch 2 n'est pas concluant.
```

---

## 🎨 Produit et design

| Document | Contenu |
| --- | --- |
| [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) | Direction visuelle et composants |
| [RELEASE_NOTES.md](RELEASE_NOTES.md) | Historique des changements |
| [design-widget-g7-cockpit.png](design-widget-g7-cockpit.png) | Référence visuelle récente |
| [reference_design_configurer_montre.png](reference_design_configurer_montre.png) | Ancienne référence montre |

---

## 🔐 Juridique

| Document | Statut |
| --- | --- |
| [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) | À terminer avant diffusion |
| [CGU.md](CGU.md) | Modèle à compléter |
| [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md) | Modèle à compléter |
| [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) | Avertissement utilisateur |

> ⚠️ Les textes juridiques ne sont pas prêts pour une diffusion publique tant que les champs `[À compléter]` ne sont pas renseignés et relus.

---

## ✅ Décision actuelle

1. Garder `Dexcom Share -> téléphone -> Wear OS` comme mode principal.
2. Valider la sync active en veille longue.
3. Garder le direct capteur comme expérimental.
4. Ne jamais publier de secrets, serials, valeurs réelles ou logs sensibles.

```text
╭────────────────────────────────────────╮
│  > docs are mapped                     │
│  > sync remains the center             │
│  > direct stays behind a gate          │
╰────────────────────────────────────────╯
```
