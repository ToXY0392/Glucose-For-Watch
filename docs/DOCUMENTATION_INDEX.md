# Documentation Widget G7

Plan central des documents Markdown du dépôt (présentation produit : [README racine](../README.md)). Utilise la **table des matières** pour sauter à une section, ou les **raccourcis** ci-dessous.

## Table des matières

- [Carte rapide](#carte-rapide) — une ligne par rôle (fichier + usage)
- [Parcours de lecture](#parcours-de-lecture) — ordre conseillé pour lire la doc
- [Produit et usage](#produit-et-usage) — parcours install, plans, historique
- [Sync et architecture](#sync-et-architecture) — technique, opérations, compatibilité
- [Direct capteur](#direct-capteur) — expérimental, hors flux principal
- [Juridique et publication](#juridique-et-publication) — checklist, CGU, confidentialité
- [Décisions à garder](#decisions-a-garder) — règles produit / diffusion

### Raccourcis par besoin

- **Utiliser l’app** — [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) · [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md)
- **Carte du code** — [STRUCTURE_REPO.md](STRUCTURE_REPO.md)
- **Reprendre le développement** — [REPRISE_PROJET.md](REPRISE_PROJET.md)
- **Sync, montre, Dexcom** — [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md)
- **Installation montre sans Store** — [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md)
- **Retours APK mobile** — [MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md)
- **Préparer une diffusion** — [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md)

## Carte rapide

| Zone | Document | À quoi ça sert |
| --- | --- | --- |
| Arborescence code / doc | [STRUCTURE_REPO.md](STRUCTURE_REPO.md) | Référence rapide des dossiers |
| Produit | [README projet](../README.md) | Présentation |
| Utilisateur | [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) | Court |
| Installation | [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) | Opérationnel |
| Installation montre | [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) | Sans Store |
| Reprise dev | [REPRISE_PROJET.md](REPRISE_PROJET.md) | Prioritaire |
| Retours APK mobile (cases à cocher) | [MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md) | Feedback rapide |
| Technique Wear OS | [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Sync, assistant install (ADB + OCR photo à valider en conditions réelles), expérimental = direct capteur |
| Publication | [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) | Relecture finale recommandée |

## Parcours de lecture

| Besoin | Lire |
| --- | --- |
| Voir où est quoi dans le repo | [STRUCTURE_REPO.md](STRUCTURE_REPO.md) |
| Comprendre le projet | [README projet](../README.md) |
| Installer et tester | [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) |
| Parcours montre sans Store | [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) |
| Résumé pour un utilisateur | [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) |
| Reprendre le développement | [REPRISE_PROJET.md](REPRISE_PROJET.md) |
| Retours APK mobile | [MOBILE_APK_RETOURS.md](MOBILE_APK_RETOURS.md) |
| Sync, Dexcom, assistant install montre, direct capteur | [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) |
| Préparer une diffusion | [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) |

## Produit et usage

| Document | Rôle |
| --- | --- |
| [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) | Résumé : installer, connecter, tester |
| [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) | Parcours complet mobile, Wear OS, Dexcom, statuts |
| [INSTALLATION_MONTRE_GUIDEE.md](INSTALLATION_MONTRE_GUIDEE.md) | APK mobile vers Wear natif |
| [PLAN_INSTALLATION_DISTANTE_WEAR.md](PLAN_INSTALLATION_DISTANTE_WEAR.md) | Plan lots installation distante |
| [PLAN_MIGRATION_NOUVELLE_VERSION_APK.md](PLAN_MIGRATION_NOUVELLE_VERSION_APK.md) | Migration UI / produit |
| [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) | Limites médicales |
| [RELEASE_NOTES.md](RELEASE_NOTES.md) | Historique des changements |

## Sync et architecture

| Document | Rôle |
| --- | --- |
| [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Sync, assistant install, direct capteur, spike BLE |
| [REPRISE_PROJET.md](REPRISE_PROJET.md) | État actuel, fichiers clés |
| [architecture/DECISIONS_SYNC_WEAR_RELIABILITY_2026-05-06.md](architecture/DECISIONS_SYNC_WEAR_RELIABILITY_2026-05-06.md) | Décisions fiabilité sync / watch |
| [operations/MATRICE_TEST_BATTERIE_MONTRE_SYNC.md](operations/MATRICE_TEST_BATTERIE_MONTRE_SYNC.md) | Matrice batterie montre |
| [operations/BILAN_EXTRACTION_MODULAIRE_2026-05-06.md](operations/BILAN_EXTRACTION_MODULAIRE_2026-05-06.md) | Bilan extraction modulaire |
| [operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md](operations/BILAN_FINAL_PLAN_REFONTE_2026-05-06.md) | Bilan plan refonte |
| [operations/RUNBOOK_RELEASE_2026-05-06.md](operations/RUNBOOK_RELEASE_2026-05-06.md) | Runbook release |
| [operations/MATRICE_OBSERVABILITE_SUPPORT_SYNC_2026-05-06.md](operations/MATRICE_OBSERVABILITE_SUPPORT_SYNC_2026-05-06.md) | Observabilité / support sync |
| [operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md](operations/CAMPAGNE_E2E_PHASE5_2026-05-06.md) | Campagne E2E |
| [../COMPATIBILITY.md](../COMPATIBILITY.md) | Compatibilité appareils |

Le téléphone reste la source principale. La montre affiche, confirme et peut demander un refresh.

## Direct capteur

| Document | Rôle |
| --- | --- |
| [TECHNIQUE_WEAR_OS.md](TECHNIQUE_WEAR_OS.md) | Décision produit, architecture possible, protocole test BLE |

Ne pas coder le direct capteur dans l'app principale tant que le spike BLE Pixel Watch 2 n'est pas concluant.

## Juridique et publication

| Document | Rôle |
| --- | --- |
| [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md) | Complétée, relecture finale recommandée |
| [CGU.md](CGU.md) | Version remplie |
| [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md) | Version remplie |
| [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md) | Avertissement utilisateur |

<a id="decisions-a-garder"></a>

## Décisions à garder

1. Garder `Dexcom Share -> téléphone -> Wear OS` comme mode principal.
2. Documenter Widget G7 comme app compagnon, pas comme remplacement Dexcom.
3. Valider la sync active en veille longue avant d'élargir les tests.
4. Garder le direct capteur comme expérimental et séparé.
5. Ne jamais publier de secrets, serials, valeurs réelles ou logs sensibles.
6. Ne diffuser publiquement qu'après validation terrain complète + relecture juridique finale.
