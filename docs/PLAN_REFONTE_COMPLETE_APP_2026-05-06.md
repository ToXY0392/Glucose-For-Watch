# Plan de Refonte Complete - Widget G7

Date: 2026-05-06  
Sources: `AUDIT_STRUCTURE_GLOBALE_2026-05-06.md`, `AUDIT_TECHNIQUE_2026-05-06.md`

---

## 1) Objectif de la refonte

Transformer Widget G7 en application fiable en conditions reelles (telephone + Wear OS), maintenable dans le temps, et livrable sans surprises entre debug et release.

Cette refonte vise 3 resultats:

1. **Fiabilite produit**: refresh montre -> donnee visible de maniere previsible.
2. **Robustesse technique**: architecture modulaire, couplage reduit, parcours critiques testes.
3. **Delivery industriel**: pipeline build/install/release deterministic et documente.

---

## 2) Principes de cadrage (non negociables)

1. **Stabiliser avant d'ajouter des features UI.**
2. **Migration progressive, sans big bang** (petites PR atomiques, build vert a chaque etape).
3. **Un seul responsable par couche**:
   - `mobile` et `wear`: composition UI + wiring,
   - `core`: contrats/modeles partages,
   - `feature`: logique metier.
4. **Une seule source de verite documentaire** (y compris legal).
5. **Pas de release sans gates qualite** (tests critiques + checklist).
6. **Continuite outillage IA**: le projet a demarre sur Codex puis a ete repris sous Cursor; toute decision technique doit etre tracee dans la doc projet pour eviter la perte de contexte entre assistants.
7. **Se documenter sur les Sources officielles** avant toute decision structurante (Android, Wear OS, Gradle/AGP, Dexcom quand pertinent).

---

## 3) Contexte de reprise IA (Codex -> Cursor)

Le projet a connu une transition d'assistant principal:

1. Demarrage des travaux et premiers choix techniques avec Codex.
2. Reprise et continuation du projet avec Cursor.

Regles operationnelles associees:

1. Conserver un historique clair des decisions d'architecture et de sync dans `docs/`.
2. Formaliser les hypotheses implicites (trade-offs, contraintes Android/Wear, choix de packaging).
3. Exiger qu'une PR de refonte mentionne son impact sur les conventions etablies avant/apres reprise.
4. Privilegier des changements atomiques et documentes pour faciliter la relecture inter-outils.

---

## 4) Cibles de succes (KPI)

1. Taux de succes refresh montre -> donnee visible >= **95%** en scenarios terrain.
2. **0 echec d'installation** sur 5 cycles consecutifs phone + watch (procedure standard).
3. **0 ANR/freeze** observe sur les services critiques lors des tests de charge evenementielle.
4. Build release **reproductible** (pas d'ambiguite debug/release, pas de mauvais APK sur mauvais device).
5. Couverture de tests minimale atteinte sur modules critiques (`sync`, `datalayer-contract`, `mapping etats`).
6. **0 crash fatal phone** sur parcours sync en campagne terrain (7 jours minimum).
7. Comportement sync explicite en mode degrade batterie montre faible (<20%).

---

## 5) Retours terrain integres (2 semaines)

Constats observes en test APK:

1. La sync fonctionne bien en usage normal.
2. Degradation notable lorsque la montre passe sous **20%** de batterie.
3. Deux occurrences d'alerte Android cote telephone: "Widget G7 a cesse de fonctionner".
4. Pas d'usure batterie montre anormale constatee a ce stade.

Impacts sur la feuille de route:

1. Priorite immediate a l'investigation crash phone (stabilite runtime).
2. Ajout d'un mode degrade explicite pour batterie montre faible.
3. Ajout d'une campagne de tests ciblee par paliers de batterie montre.

---

## 6) Architecture cible

```text
WidgetG7/
  mobile/                      # app phone: UI + orchestration
  wear/                        # app watch: UI + listeners + tile/complication wiring
  core/
    datalayer-contract/        # paths/keys/protocoles partages phone/watch
    model/                     # modeles metier partages
    testing/                   # utilitaires de tests communs
  feature/
    sync/                      # moteur de sync + etats + retries/backoff
    dexcom-share/              # integration Dexcom
    watch-install/             # flux pair/install watch
  docs/
    architecture/
    operations/
    product/
    legal/
  scripts/
    dev/
    release/
    assets/
```

---

## 7) Plan d'execution complet (8 semaines)

## Phase 0 - Stabilisation immediate (J0 -> J2)

### Objectif
Couper les incidents les plus visibles et rendre le systeme previsible.

### Actions
1. Durcir composants exportes dans les manifests mobile/wear (visibilite minimale).
2. Verrouiller le parcours d'installation phone/watch (bon APK sur bon appareil).
3. Ajouter garde-fous UX post-reinstall (re-ajout tuile/complication guide).
4. Stabiliser environnement build local (AGP/Gradle/versioning, logs exploitables).

### Livrables
- Manifests durcis.
- Procedure d'installation debug fiable.
- Message guide post-reinstall dans l'app.
- Runbook de build local a jour.

### Definition of Done
- 5 cycles d'installation consecutifs sans echec.
- Tuile/complication visibles apres parcours guide.

---

## Phase 1 - Fiabilite sync et data layer (Semaine 1)

### Objectif
Rendre refresh/sync robuste aux contraintes Android modernes (background/Doze/FGS).

### Actions
1. Revoir le chemin refresh montre -> sync pour eviter les demarrages fragiles.
2. Introduire orchestration resiliente (fallback worker + etats explicites).
3. Rendre les listeners Wear non bloquants.
4. Recalibrer scheduler, retries, timeouts, backoff.
5. Instrumenter les etats cle: demande refresh, debut sync, push, ack, echec.
6. Instrumenter les crashs fatals phone autour des parcours sync (contexte batterie, etape sync, etat background/Doze).
7. Definir le comportement en batterie montre faible (<20%): cadence reduite, messaging explicite, retries adaptes.

### Livrables
- Flux sync end-to-end durci.
- Journalisation structuree exploitable support/QA.
- Politique retry/backoff documentee.

### Definition of Done
- Taux refresh -> donnee visible >= 95% en test terrain.
- Pas d'ANR/freeze en test charge evenementielle.
- Batterie stable sur 24h d'usage normal.
- Aucun crash fatal phone observe sur 7 jours de test cible sync.
- Cas batterie montre <20% couvert et comportement degrade valide.

---

## Phase 2 - Refonte structurelle modulaire (Semaines 2-4)

### Objectif
Sortir du monolithe mobile et clarifier les responsabilites.

### Actions
1. Creer `core:datalayer-contract` (paths/keys/protocoles).
2. Creer `core:model` (modeles sync/glucose/etat).
3. Extraire `feature:sync`.
4. Extraire `feature:dexcom-share`.
5. Extraire `feature:watch-install`.
6. Recentrer `mobile` sur UI + orchestration.
7. Aligner conventions de structure et dependances autorisees.

### Livrables
- Modules `core/*` et `feature/*` en production.
- Reduction de duplication phone/watch.
- Regles de dependances formalisees.

### Definition of Done
- Responsabilites des modules explicites et respectees.
- Plus de logique metier critique enfouie directement dans `mobile`.
- Build vert apres chaque extraction atomique.

---

## Phase 3 - Industrialisation build, packaging et release (Semaine 5)

### Objectif
Supprimer les surprises entre debug et release.

### Actions
1. Unifier les pipelines debug/release mobile/wear.
2. Clarifier packaging wear (copie APK, signatures, versioning coherent).
3. Deplacer l'automatisation dans `scripts/` et/ou convention plugins Gradle dedies.
4. Ajouter checks CI: presence APK attendus, coherence versioning, smoke install.
5. Introduire checklist release obligatoire.

### Livrables
- Pipeline deterministic.
- Procede release documente.
- Gates CI actives.

### Definition of Done
- Aucune installation "mauvais APK/mauvais device" en procedure standard.
- Build release reproductible en environnement propre.

---

## Phase 4 - Qualite durable, observabilite et produit (Semaines 6-7)

### Objectif
Sortir de la boucle des regressions silencieuses.

### Actions
1. Mettre en place socle de tests unitaires sur `core` et `feature`.
2. Ajouter instrumentation tests parcours critiques (sync, install, tile, complication).
3. Standardiser logs/erreurs actionnables (codes d'etat, diagnostics support).
4. Completer i18n/accessibilite sur ecrans critiques.
5. Consolider doc technique et operations (fin de divergence debug/release).
6. Ajouter matrice de test "batterie montre" (25%, 20%, 15%, 10%, chargeur oui/non) sur refresh, ack, tile et complication.

### Livrables
- Suite tests critique en CI.
- Observabilite exploitable en support.
- Base produit plus inclusive et robuste.

### Definition of Done
- Regressions critiques bloquees en CI avant merge.
- Couverture minimale modules critiques atteinte (objectif a fixer en equipe).
- Diagnostics support clairs et repetables.

---

## Phase 5 - Validation finale et pre-release (Semaine 8)

### Objectif
Certifier que l'application "fonctionne vraiment" avant diffusion plus large.

### Actions
1. Campagne E2E complete: installation, sync longue veille, multi-montres, reinstall.
2. Verification batterie/reseau sur scenarios reels.
3. Dry-run release complet (artefacts + checklist + rollback plan).
4. Go/No-Go final sur criteres objectifs.

### Definition of Done
- Tous les KPI de section 4 passent.
- Aucune anomalie bloquante ouverte.
- Decision Go release tracee.
- Livrable final produit: schema visuel de reference (architecture + flux sync phone/wear + branches d'etat critiques + observabilite), maintenu dans `docs/architecture`.

---

## 8) Backlog priorise (ordre d'implementation)

1. Durcir services exportes (mobile/wear manifests).
2. Stabiliser refresh montre -> sync (sans dependance fragile au contexte background).
3. Retirer les chemins bloquants du listener Wear.
4. Instrumenter et corriger les crashs fatals phone sur flux sync.
5. Definir et implementer le mode degrade montre <20% batterie.
6. Revoir scheduler/retries/timeouts/backoff.
7. Ajouter garde-fou UX "re-ajout tuile/complication" post-reinstall.
8. Verrouiller pipeline install phone/watch et packaging release.
9. Instrumenter telemetry sync end-to-end.
10. Ajouter matrice de tests batterie montre + tests critiques sync.
11. Extraire `core:datalayer-contract` puis `core:model`.
12. Extraire `feature:sync`, `feature:dexcom-share`, `feature:watch-install`.

---

## 9) Gouvernance d'execution

1. **Rythme**: lot hebdomadaire avec demo et mesure KPI.
2. **Regle merge**: pas de merge si build rouge ou tests critiques KO.
3. **Granularite**: PR petites, atomiques, faciles a reviewer.
4. **Pilotage**: suivi risques + decisions architecture dans `docs/architecture`.
5. **Arret de dette**: suppression immediate du legacy des qu'un nouveau chemin est stabilise.
6. **Memoire inter-assistants**: chaque lot doit laisser une trace de contexte (decision, rationale, impacts) pour assurer la continuite Codex -> Cursor -> sessions futures.

---

## 10) Risques majeurs et mitigations

1. **Risque**: casse pendant extraction modulaire.  
   **Mitigation**: migrations incrementales + checks CI sur chaque etape.

2. **Risque**: derive de planning (trop de chantiers paralleles).  
   **Mitigation**: priorisation stricte Phase 0/1 avant reste.

3. **Risque**: ambiguite debug/release persistante.  
   **Mitigation**: pipeline unique, scripts centralises, checklist obligatoire.

4. **Risque**: observabilite insuffisante pour diagnostiquer incidents terrain.  
   **Mitigation**: telemetry standardisee des etats sync/watch des Phase 1.

5. **Risque**: perte de contexte due a la transition Codex -> Cursor et au passage de sessions.  
   **Mitigation**: journal de decisions maintenu dans `docs/architecture` + references explicites dans chaque PR de refonte.

---

## 11) Decision produit immediate

Jusqu'a la fin de la Phase 1:

- **Gel des nouvelles features non critiques**.
- Priorite absolue a la fiabilite sync/watch et a la stabilite packaging.

Cette decision maximise le retour utilisateur et reduit le cout support a court terme.

---

## 12) Prompt design du schema final (premium clair)

Ce prompt est pret a l'emploi pour produire le schema visuel de reference en fin de refonte:

```text
Creer un design premium, moderne et elegant pour le schema final de refonte de Widget G7.

Contexte:
- Application sante Android: mobile + Wear OS.
- Le schema doit refleter une architecture production-ready apres refonte complete.
- Public cible: equipe produit/dev, stakeholders non techniques, QA.

Direction artistique:
- Style premium minimaliste, lumineux, inspire des interfaces haut de gamme.
- Vision d'un jeune developpeur qui aime le moderne et l'elegance: design frais, propre, raffine.
- Fond clair (blanc casse / gris tres clair), cartes blanches avec ombres tres legeres.
- Palette d'accent: bleu cobalt, cyan doux, violet subtil, vert succes discret.
- Couleurs d'etat:
  - normal: bleu,
  - avertissement: ambre,
  - erreur: rouge doux,
  - mode degrade: violet.
- Typographie: sans-serif contemporaine, lisible, hierarchie nette.
- Icones fines, style outline, coherentes sur tout le schema.
- Espacement genereux, grille stricte, alignements impeccables.

Contenu fonctionnel obligatoire du schema:
1) Flux principal sync:
   Dexcom Share -> Mobile App -> Data Layer -> Wear App -> Tile/Complication.
2) Boucle de fiabilite:
   refresh request -> start sync -> push -> ack -> status update.
3) Branches critiques:
   - montre <20% batterie -> mode degrade explicite,
   - echec ack -> retry/backoff borne,
   - crash/erreur phone -> observabilite + fallback.
4) Parcours install/deploy:
   - debug: mobile-debug sur telephone, wear-debug sur montre,
   - release: pipeline controle + checklist.
5) Observabilite:
   points de logs structures et etats (request/start/push/ack/fail).

Contraintes UX du schema:
- Comprehensible en moins de 30 secondes.
- Mettre en evidence happy path vs failure path.
- Utiliser labels et couleurs coherents pour tous les etats.
- Prevoir une version exportable pour docs/architecture et presentation.

Livrable attendu:
- 1 diagramme principal (vue systeme end-to-end),
- 1 zoom "gestion d'incidents sync" (erreurs, retries, mode degrade),
- 1 legende visuelle claire (codes couleur + signification des fleches).
```

