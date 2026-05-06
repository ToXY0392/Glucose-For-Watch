# Documentation Widget G7

Convention de fichiers : **anglais**, **kebab-case** (`technical-wear-os-sync.md`). Le contenu des guides peut rester en français.

Index des documents **maintenus** à jour dans ce dépôt.

## Par besoin

| Besoin | Document |
| --- | --- |
| Cartographie code et chemins Gradle | [structure-repository.md](structure-repository.md) |
| Vue produit (racine du dépôt) | [README](../README.md) |
| Utilisateur — court | [user-quick-notice.md](user-quick-notice.md) |
| Utilisateur — manuel | [user-manual.md](user-manual.md) |
| Installation Wear (assistant ADB depuis le mobile, pas de Store) | [technical-wear-os-sync.md](technical-wear-os-sync.md) (section assistant), [user-manual.md](user-manual.md) |
| Développeur — reprise du travail | [developer-handoff.md](developer-handoff.md) |
| QA — retours APK mobile | [qa-mobile-apk-feedback.md](qa-mobile-apk-feedback.md) |
| Technique — sync, Data Layer, assistant install Wear | [technical-wear-os-sync.md](technical-wear-os-sync.md) |
| Compatibilité appareils | [COMPATIBILITY.md](../COMPATIBILITY.md) |
| Juridique / publication | [legal-publication-checklist.md](legal-publication-checklist.md), [legal-terms.md](legal-terms.md), [legal-privacy-policy.md](legal-privacy-policy.md), [legal-medical-disclaimer.md](legal-medical-disclaimer.md) |
| Versions | [release-notes.md](release-notes.md) |

## Plans produit / migration

| Document | Rôle |
| --- | --- |
| [plan-apk-upgrade-migration.md](plan-apk-upgrade-migration.md) | Migration UI / APK sans régression Dexcom ni sync |

## Références fournisseurs

| Ressource | Rôle |
| --- | --- |
| [ref/README.md](ref/README.md) | Point d’entrée des liens officiels Dexcom / Google / build |
| [ref/dependency-catalog.yaml](ref/dependency-catalog.yaml) | Registre structuré (toolchain, dépendances, URLs) |
| [ref/dependency-registry.md](ref/dependency-registry.md) | Table Gradle ↔ documentation |

## Principes à conserver

1. Chaîne nominale : **Dexcom Share → téléphone → Wear OS** ; la montre n’interroge pas le capteur en production.
2. Application **compagnon d’affichage**, pas substitut officiel Dexcom pour les décisions thérapeutiques.
3. Mode **capteur → montre (BLE)** : expérimental, hors branche produit tant que non validé ([technical-wear-os-sync.md](technical-wear-os-sync.md)).
4. Ne pas versionner secrets, données de santé réelles, serials ou keystores.
5. Pas de diffusion publique avant finalisation juridique (checklist + modèles légaux).
