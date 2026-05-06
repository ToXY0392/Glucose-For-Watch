# Audit Technique Complet - 2026-05-06

## Objectif

Faire le point sur ce qui ne fonctionne pas ou fonctionne mal dans l'app (mobile + wear), puis proposer un plan strategique pour stabiliser, fiabiliser et preparer les prochaines releases.

## Resume Executif

L'application compile et s'installe, mais la fiabilite produit reste fragile sur les flux critiques:

- sync telephone <-> montre sensible aux contraintes Android background/foreground;
- parcours tile/complication encore fragile en conditions reelles;
- securite/robustesse perfectible sur certains services exportes;
- packaging/deploiement wear pas assez industrialise pour release;
- absence de tests automatiques sur les zones les plus risquées.

Le principal risque utilisateur est une experience incoherente ("refresh lance mais pas de mise a jour visible", statut de sync ambigu, tuile absente apres reinstall) combinee a des regressions silencieuses.

## Perimetre Analyse

- mobile (`mobile/`)
- wear (`wear/`)
- data layer et sync (`mobile/.../sync`, `wear/.../services`, `wear/.../tile`)
- build/deploiement (`build.gradle.kts`, `gradle/wrapper`, tasks d'installation)

## Constat Detaille: Ce Qui Ne Fonctionne Pas Ou Mal

### Critique

1. **Demarrage sync depuis requete montre potentiellement bloque en background**
   - Symptome: action refresh cote montre peut ne pas declencher une sync effective selon contexte systeme.
   - Source: `mobile/src/main/java/com/widgetg7/mobile/sync/PhoneWearRefreshRequestService.kt`, `mobile/src/main/java/com/widgetg7/mobile/sync/ActiveGlucoseSyncController.kt`.
   - Cause probable: lancement FGS depuis contexte non privilegie sur Android recent.

2. **Services Data Layer exportes a durcir**
   - Symptome: surface IPC plus large que necessaire.
   - Source: `mobile/src/main/AndroidManifest.xml`, `wear/src/main/AndroidManifest.xml`.
   - Risque: interactions non souhaitees, bruit, comportements parasites.

3. **Aucun filet de securite test (unit/instrumentation)**
   - Symptome: toute evolution peut casser sync/tile sans alerte prealable.
   - Source: absence de suites dans `src/test` et `src/androidTest`.

### Haute Priorite

1. **Packaging wear principalement pense debug**
   - Symptome: installation wearable pas deterministe hors flux dev local.
   - Source: logique de copie APK wear surtout branchee sur debug dans `mobile/build.gradle.kts`.
   - Impact: echec d'installation montre dans certains parcours release.

2. **Listener Wear avec portions bloquantes**
   - Symptome: latence sur updates et refresh dans certains cas.
   - Source: `wear/src/main/java/com/widgetg7/wear/services/WearDataLayerListenerService.kt`.

3. **Scheduler de sync agressif**
   - Symptome: consommation batterie / comportement inconstant en Doze.
   - Source: `mobile/src/main/java/com/widgetg7/mobile/sync/PhoneAutoSyncScheduler.kt` (intervalle court).

4. **Flux "tuile absente apres reinstall" non suffisamment guide**
   - Symptome: l'app est installee, mais l'utilisateur pense que "rien ne marche" car la tuile n'est pas re-ajoutee automatiquement.
   - Source: comportement Wear OS + manque de garde-fou UX dans le produit.

### Priorite Moyenne

1. **Etat sync montre pas assez contextualise (multi-montres)**
   - Symptome: statut potentiellement ambigu selon le node.
   - Source: repository/statut cote mobile.

2. **Timeouts et retries a recalibrer**
   - Symptome: faux negatifs "non reponse" ou retries inefficaces.
   - Source: verification statut montre + engine sync.

3. **Gestion d'erreurs trop generique a certains endroits**
   - Symptome: diagnostic difficile, fallback pas toujours explicite.
   - Source: catches larges dans des flows critiques.

### Priorite Basse

1. **Accessibilite et i18n incomplètes**
   - Symptome: experience perfectible pour certains utilisateurs.
   - Source: labels/strings/hardcoded text.

2. **Dette de documentation technique**
   - Symptome: confusion possible entre comportements debug/release.
   - Source: docs build/deploiement a consolider.

## Plan Strategique De Resolution

## Phase 0 (Immediate - 24/48h): Stabilisation operationnelle

Objectif: stop aux incidents de surface et rendre le comportement previsible.

- durcir composants exportes (permissions/visibilite minimales);
- fiabiliser le parcours installation montre (APK correct sur device correct);
- ajouter une verification explicite dans l'app wear ("service tile actif", "complication disponible");
- clarifier UX post-reinstall: message in-app pour re-ajouter la tuile/complication;
- stabiliser configuration build/sync IDE (AGP/Gradle compatibles, flux logs lisible).

**Critere d'acceptation:**
- 0 echec d'installation sur 5 cycles consecutifs phone+watch;
- tile visible et mise a jour apres reinstall guidee;
- sync IDE reproductible sans manipulation manuelle complexe.

## Phase 1 (Semaine 1): Fiabilite sync et data layer

Objectif: rendre les flux refresh/sync robustes aux contraintes Android.

- remplacer les chemins sensibles FGS par orchestration plus resilience (fallback worker + etats explicites);
- rendre listeners non bloquants (pas d'attente lourde dans callbacks);
- revoir cadence scheduler + politique retry/backoff;
- tracer les etats clefs: demande refresh, start sync, push montre, ack, echec.

**Critere d'acceptation:**
- taux de refresh "demande -> donnee visible" >= 95% sur scenarios terrain;
- pas d'ANR ni freeze service observes en test de charge evenementielle;
- batterie stable sur 24h d'usage normal.

## Phase 2 (Semaine 2): Industrialisation packaging/release

Objectif: supprimer les surprises entre debug et release.

- pipeline explicite mobile/wear pour debug et release (assets, signatures, install scripts);
- checks CI sur presence des APK attendus et cohérence versioning;
- gates de release: checklist install phone/watch + validation tile/complication.

**Critere d'acceptation:**
- build release deterministic;
- aucun "mauvais APK sur mauvais device" en procedure standard;
- checklist release executee a 100% avant distribution.

## Phase 3 (Semaines 3-4): Qualite durable et dette technique

Objectif: prevenir les regressions futures.

- creer socle de tests unitaires sur moteurs sync/data mapping;
- ajouter tests instrumentation critiques (installation, lifecycle services, tile update path);
- renforcer observabilite (logs structurees, codes d'etat, erreurs actionnables);
- corriger accessibilite et finaliser i18n des ecrans cles.

**Critere d'acceptation:**
- couverture minimale des modules critiques definie et atteinte;
- regression critique bloquee en CI avant merge;
- diagnostics clairs en support utilisateur.

## Backlog Priorise (Top 10)

1. Durcir services exportes dans les manifests (mobile/wear).
2. Stabiliser refresh montre -> sync sans dependance fragile au contexte background.
3. Supprimer chemins bloquants du listener Wear.
4. Revoir intervalle/strategie scheduler + retries.
5. Rendre explicite le parcours "re-ajout tuile/complication" post-reinstall.
6. Verrouiller pipeline install phone/wear et packaging release.
7. Ajouter telemetry sync end-to-end (demande, push, ack, erreurs).
8. Ajouter tests unitaires du moteur sync et mapping etats.
9. Ajouter tests instrumentation de parcours critique montre.
10. Nettoyer i18n/accessibilite des ecrans principaux.

## Risques Si Rien N'est Fait

- incidents repetes "ca ne marche pas" mal diagnosticables;
- perte de confiance utilisateur (tuile/complication perçues instables);
- cout support eleve (reinstall manuelle, confusion debug/release);
- regressions frequentes a chaque evolution.

## Recommendation Finale

Traiter en priorite **Phase 0 + Phase 1** avant toute nouvelle fonctionnalite UI.  
Le meilleur retour sur investissement est de securiser le flux sync/watch et le packaging deploiement, puis d'ajouter le socle de tests pour sortir de la boucle de regressions.

