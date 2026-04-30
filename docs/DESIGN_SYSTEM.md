# Design system

## Direction

Widget G7 doit paraitre :

- medical sans etre froid ;
- simple ;
- fiable ;
- lisible vite ;
- centre sur la montre et la derniere valeur.

L'interface ne doit pas ressembler a une landing page. Elle doit aider l'utilisateur a verifier la sync et a lire l'etat courant.

## Palette

Fond :

- blanc clinique ;
- gris tres clair pour les zones secondaires.

Accent :

- vert pour la sync active et les etats corrects ;
- orange pour attention ;
- rouge pour erreur ou risque ;
- gris pour indisponible ou ancien.

Regle : le vert doit rester un accent, pas envahir tout l'ecran.

## Typographie

- titres courts ;
- peu de texte ;
- valeur glucose tres lisible ;
- statuts courts et explicites ;
- pas de paragraphes longs dans l'app.

## Composants

### Cards

Utiliser les cards pour :

- statut principal ;
- choix de montre ;
- bloc Dexcom ;
- actions utilisateur.

Eviter les cards imbriquees.

### Boutons

- action principale : bouton plein ;
- action secondaire : bouton contour ;
- icones pour navigation et outils simples ;
- libelles courts.

### Statuts

Les statuts doivent dire ce qui se passe, pas seulement afficher une couleur.

Exemples :

- `Sync active`
- `Montre verifiee`
- `Aucune nouvelle mesure`
- `Verifier Dexcom`
- `Donnee ancienne`

## Ecrans

### Accueil

Objectif : voir l'etat montre et relancer une sync.

Contenu :

- grande presence montre ;
- statut de sync ;
- bouton `Synchroniser` ;
- acces parametres.

### Connexion Dexcom

Objectif : configurer Dexcom Share.

Contenu :

- identifiants ;
- region ;
- acceptation juridique ;
- messages d'erreur simples.

### Montre

Objectif : verifier la liaison.

Contenu :

- montre detectee ;
- choix de montre principale si besoin ;
- bouton `Tester l'envoi` ;
- bouton `Autoriser la sync en veille` si necessaire ;
- etat batterie/sync si disponible.

### Wear

Objectif : lire vite.

Contenu :

- valeur ;
- tendance ;
- fraicheur ;
- refresh manuel.

Le texte doit rester minimal sur la montre.

### Mode direct capteur

Si ce mode existe un jour :

- le marquer `experimental` ;
- afficher les risques ;
- montrer l'age de la donnee ;
- offrir un retour simple vers `Sync telephone`.

## A eviter

- ecrans trop bavards ;
- decoration gratuite ;
- gradients dominants ;
- texte qui repete les docs ;
- cacher l'age d'une donnee ;
- presenter le mode direct comme officiel.

## References visuelles

- [design-widget-g7-cockpit.png](design-widget-g7-cockpit.png)
- [reference_design_configurer_montre.png](reference_design_configurer_montre.png)
