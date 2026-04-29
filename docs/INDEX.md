# Index documentation

Date de mise a jour : 30 avril 2026

## Documents produit

- [README projet](../README.md) : presentation globale, installation, strategie sync.
- [MODE_D_EMPLOI.md](MODE_D_EMPLOI.md) : parcours utilisateur detaille.
- [NOTICE_UTILISATEUR.md](NOTICE_UTILISATEUR.md) : notice courte.
- [RELEASE_NOTES.md](RELEASE_NOTES.md) : historique des versions et decisions recentes.
- [REPRISE_PROJET.md](REPRISE_PROJET.md) : point de reprise pour continuer le travail.

## Documents sync

- [SYNC_G7_WEAR_RECHERCHE.md](SYNC_G7_WEAR_RECHERCHE.md) : recherche sur Dexcom G7, nombre d'appareils, Wear OS et Direct to Watch.
- [DIRECT_PATCH_WEAR_SOLUTION.md](DIRECT_PATCH_WEAR_SOLUTION.md) : meilleure solution envisageable pour brancher directement le capteur sur Wear OS.
- [PLAN_WEAR_COLLECTOR_AVANCE.md](PLAN_WEAR_COLLECTOR_AVANCE.md) : plan de construction du mode avance Wear Collector.

## Documents design

- [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) : direction visuelle, couleurs, composants, regles d'ecran.
- [design-widget-g7-cockpit.png](design-widget-g7-cockpit.png) : visuel de reference recent.
- [reference_design_configurer_montre.png](reference_design_configurer_montre.png) : ancienne reference de configuration montre.

## Documents juridiques

- [CGU.md](CGU.md)
- [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md)
- [AVERTISSEMENT_MEDICAL.md](AVERTISSEMENT_MEDICAL.md)

Ces textes restent a relire avant diffusion publique.

## Decision actuelle

La direction technique validee est :

1. stabiliser le mode standard `Dexcom/telephone -> Wear OS` ;
2. documenter le mode direct `capteur G7 -> Wear OS` comme experimental ;
3. ne pas coder le Wear Collector sans spike BLE prealable sur Pixel Watch 2 ;
4. conserver un affichage montre simple, lisible, avec age de la donnee visible quand necessaire.
