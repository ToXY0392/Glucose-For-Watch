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

## 🟢 Carte Rapide

```text
╭─ Widget G7 ───────────────────────────╮
│ Mode fiable : téléphone -> Wear OS     │
│ Mode direct : expérimental, hors app   │
│ Priorité   : sync active stable        │
│ Diffusion  : test privé uniquement     │
│ Risque     : données médicales         │
╰────────────────────────────────────────╯
```

| Zone | Source de vérité | Statut |
| --- | --- | --- |
| Arborescence code / doc | [STRUCTURE_REPO.md](STRUCTURE_REPO.md) | Référence rapide |
| Produit | [README projet](../README.md) | Présentation |
| Utilisateur | [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) | Court |
| Installation | [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) | Opérationnel |
| Installation montre | [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) | Sans Store |
| Reprise dev | [REPRISE_PROJET.md](REPRISE_PROJET.md) | Prioritaire |
| Retours APK mobile (cases à cocher) | [MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md) | Feedback rapide |
| Technique Wear OS | [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Sync, assistant install (ADB + OCR photo à valider en conditions réelles), expérimental = direct capteur |
| Publication | [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) | Bloquant |

---

## ⚡ Parcours De Lecture

| Besoin | Lire |
| --- | --- |
| Voir où est quoi dans le repo | [STRUCTURE_REPO.md](STRUCTURE_REPO.md) |
| Comprendre le projet | [README projet](../README.md) |
| Installer et tester | [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) |
| Concevoir le parcours montre sans Store | [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) |
| Donner une version courte à un utilisateur | [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) |
| Reprendre le développement | [REPRISE_PROJET.md](REPRISE_PROJET.md) |
| Signaler ce qui ne va pas sur l’APK mobile | [MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md) |
| Comprendre la sync, Dexcom, l'assistant install montre (ADB + OCR), le direct capteur | [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) |
| Préparer une diffusion | [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) |

---

## 📱 Produit Et Usage

| Document | Rôle |
| --- | --- |
| [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) | Résumé utilisateur : installer, connecter, tester |
| [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) | Parcours complet : mobile, Wear OS, Dexcom, statuts |
| [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) | APK mobile unique vers Wear natif par installation distante |
| [PLAN_INSTALLATION_DISTANTE_WEAR.md](PLAN_INSTALLATION_DISTANTE_WEAR.md) | Plan de lots pour rendre l'installation distante viable |
| [PLAN_MIGRATION_NOUVELLE_VERSION_APK.md](PLAN_MIGRATION_NOUVELLE_VERSION_APK.md) | Plan de migration UI / produit sans casser CGU, Dexcom et sync |
| [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) | Limites médicales à afficher et conserver |
| [RELEASE_NOTES.md](RELEASE_NOTES.md) | Historique court des changements et validations |

---

## 🔁 Sync Et Architecture

| Document | Rôle |
| --- | --- |
| [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Sync principale, assistant installation montre (ADB ; OCR photo + parseur ligne par ligne — toujours vérifier les champs), direct capteur et spike BLE |
| [REPRISE_PROJET.md](REPRISE_PROJET.md) | État actuel, fichiers clés, prochaines vérifications |
| [../COMPATIBILITY.md](../COMPATIBILITY.md) | Compatibilité Android et montres Wear OS |

```text
Le téléphone reste la source principale.
La montre affiche, confirme et peut demander un refresh.
```

---

## 🧪 Direct Capteur

| Document | Rôle |
| --- | --- |
| [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Décision produit, architecture possible et protocole de test BLE |

```text
Ne pas coder le direct capteur dans l'app principale
tant que le spike BLE Pixel Watch 2 n'est pas concluant.
```

---

## 🔐 Juridique Et Publication

| Document | Statut |
| --- | --- |
| [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) | À terminer avant diffusion |
| [CGU.md](CGU.md) | Modèle à compléter |
| [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md) | Modèle à compléter |
| [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) | Avertissement utilisateur |

> ⚠️ Les textes juridiques ne sont pas prêts pour une diffusion publique tant que les champs `[À compléter]` ne sont pas renseignés et relus.

---

## ✅ Décisions À Garder

1. Garder `Dexcom Share -> téléphone -> Wear OS` comme mode principal.
2. Documenter Widget G7 comme app compagnon, pas comme remplacement Dexcom.
3. Valider la sync active en veille longue avant d'élargir les tests.
4. Garder le direct capteur comme expérimental et séparé.
5. Ne jamais publier de secrets, serials, valeurs réelles ou logs sensibles.
6. Ne pas diffuser publiquement tant que la checklist juridique n'est pas complète.

```text
╭────────────────────────────────────────╮
│  > docs are mapped                     │
│  > sync remains the center             │
│  > direct stays behind a gate          │
╰────────────────────────────────────────╯
```
