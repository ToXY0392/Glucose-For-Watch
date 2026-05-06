# Documentation Widget G7

Documentation de travail et d'exploitation du projet.

## Carte rapide

- **Arborescence code / doc** · [STRUCTURE_REPO.md](STRUCTURE_REPO.md) · référence rapide
- **Produit** · [README projet](../README.md) · présentation
- **Utilisateur** · [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) · court
- **Installation** · [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) · opérationnel
- **Installation montre** · [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) · sans Store
- **Reprise dev** · [REPRISE_PROJET.md](REPRISE_PROJET.md) · prioritaire
- **Retours APK mobile (cases à cocher)** · [MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md) · feedback rapide
- **Technique Wear OS** · [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) · sync, assistant install (ADB + OCR photo à valider en conditions réelles), expérimental = direct capteur
- **Publication** · [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) · relecture finale recommandée

## Parcours de lecture

- **[STRUCTURE_REPO.md](STRUCTURE_REPO.md)** — voir où est quoi dans le repo
- **[README projet](../README.md)** — comprendre le projet
- **[MODE_D_EMPLOI.md](MODE_D_EMPLOI.md)** — installer et tester
- **[INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md)** — parcours montre sans Store
- **[NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md)** — résumé pour un utilisateur
- **[REPRISE_PROJET.md](REPRISE_PROJET.md)** — reprendre le développement
- **[MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md)** — retours APK mobile
- **[TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md)** — sync, Dexcom, assistant install montre, direct capteur
- **[LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md)** — préparer une diffusion

## Produit et usage

- [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) — résumé : installer, connecter, tester
- [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) — parcours complet mobile, Wear OS, Dexcom, statuts
- [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) — APK mobile vers Wear natif
- [PLAN_INSTALLATION_DISTANTE_WEAR.md](PLAN_INSTALLATION_DISTANTE_WEAR.md) — plan lots installation distante
- [PLAN_MIGRATION_NOUVELLE_VERSION_APK.md](PLAN_MIGRATION_NOUVELLE_VERSION_APK.md) — migration UI / produit
- [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) — limites médicales
- [RELEASE_NOTES.md](RELEASE_NOTES.md) — historique des changements

## Sync et architecture

- [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) — sync, assistant install, direct capteur, spike BLE
- [REPRISE_PROJET.md](REPRISE_PROJET.md) — état actuel, fichiers clés
- [architecture/DECISIONS_SYNC_WEAR_RELIABILITY_2026-05-06.md](architecture/DECISIONS_SYNC_WEAR_RELIABILITY_2026-05-06.md) — décisions fiabilité sync / watch
- [operations/MATRICE_TEST_BATTERIE_MONTRE_SYNC.md](operations/MATRICE_TEST_BATTERIE_MONTRE_SYNC.md) — matrice batterie montre
- [operations/BILAN_EXTRACTION_MODULAIRE_2026-05-06.md](operations/BILAN_EXTRACTION_MODULAIRE_2026-05-06.md) — bilan extraction modulaire
- [operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md](operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md) — bilan plan refonte
- [operations/RUNBOOK_RELEASE_2026-05-06.md](operations/RUNBOOK_RELEASE_2026-05-06.md) — runbook release
- [operations/MATRICE_OBSERVABILITE_SUPPORT_SYNC_2026-05-06.md](operations/MATRICE_OBSERVABILITE_SUPPORT_SYNC_2026-05-06.md) — observabilité / support sync
- [operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md](operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md) — campagne E2E
- [../COMPATIBILITY.md](../COMPATIBILITY.md) — compatibilité appareils

Le téléphone reste la source principale. La montre affiche, confirme et peut demander un refresh.

## Direct capteur

- [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) — décision produit, architecture possible, protocole test BLE

Ne pas coder le direct capteur dans l'app principale tant que le spike BLE Pixel Watch 2 n'est pas concluant.

## Juridique et publication

- [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) — complétée, relecture finale recommandée
- [CGU.md](CGU.md) — version remplie
- [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md) — version remplie
- [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) — avertissement utilisateur

## Décisions à garder

1. Garder `Dexcom Share -> téléphone -> Wear OS` comme mode principal.
2. Documenter Widget G7 comme app compagnon, pas comme remplacement Dexcom.
3. Valider la sync active en veille longue avant d'élargir les tests.
4. Garder le direct capteur comme expérimental et séparé.
5. Ne jamais publier de secrets, serials, valeurs réelles ou logs sensibles.
6. Ne diffuser publiquement qu'après validation terrain complète + relecture juridique finale.
