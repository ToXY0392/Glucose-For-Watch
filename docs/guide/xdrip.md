# xDrip+ — guide de configuration et grille d’audit

**Type de document :** guide technique de configuration et d’audit  
**Public :** utilisateurs avancés, aidants et intégrateurs Android  
**Révision :** 28 septembre 2026  
**Référence de version amont consultée :** release xDrip+ `2026.03.01` (vérifier la version installée et les notes de release avant application).  
**Références consultées :** documentation xDrip+ (Nightscout Foundation), documentation Android et documentation AndroidAPS.  
**Important :** xDrip+ est un logiciel indépendant. Les options, noms de menus et compatibilités peuvent varier selon la version de l’application, le modèle de capteur, la région et Android.

> **Sécurité clinique :** ce guide ne fournit ni réglage thérapeutique ni conseil de calibration. xDrip+ et les données affichées ne remplacent pas le système officiel du fabricant, un lecteur de glycémie, ni les consignes d’un professionnel de santé. Ne prenez pas de décision de traitement sur la seule base d’une donnée dont la fraîcheur ou la source n’est pas confirmée. Pour une utilisation avec AndroidAPS, suivez en plus les consignes de sécurité et la liste de sources compatibles de la version d’AAPS installée.

## 1. Périmètre et architecture d’intégration

### 1.1 Principes de conception

xDrip+ peut servir de collecteur, de processeur, de visualiseur ou de relais vers d’autres applications. Avant toute configuration :

1. Identifiez l’application ou l’appareil qui reçoit réellement les données du capteur.
2. Choisissez un seul chemin principal de collecte pour une session capteur donnée.
3. Ajoutez ensuite les sorties (Nightscout, diffusion locale, montre) indépendamment.
4. Vérifiez la source et l’heure de la dernière lecture dans xDrip+, puis dans chaque destination.
5. Évitez les boucles : une application ne doit pas renvoyer à xDrip+ les mêmes valeurs que xDrip+ lui a transmises.

Une valeur affichée n’est pas nécessairement une valeur reçue directement du capteur. Elle peut provenir d’un follower, d’un serveur ou d’une application compagnon, avec des délais et des transformations différents.

### 1.2 Sources de données

Les noms exacts des sources dépendent du build xDrip+ et de la compatibilité de l’équipement. Le tableau décrit des architectures possibles, pas une garantie de compatibilité universelle.

| Architecture | Configuration de principe | Points d’audit |
|---|---|---|
| **Dexcom G6/G7 collecté par xDrip+** | Sélectionner la source correspondant au modèle et au mode pris en charge par la version installée. Pour les modes Bluetooth directs, suivre les réglages de collecteur indiqués par la documentation xDrip+ et le modèle de téléphone. | Vérifier l’émetteur autorisé, l’état Bluetooth, les autorisations, le numéro de série si demandé et la compatibilité exacte de la version. Ne pas faire fonctionner plusieurs collecteurs concurrents pour le même capteur. |
| **Dexcom reçu par une application compagnon** | L’application compagnon est le collecteur principal ; xDrip+ consomme uniquement le flux d’intégration officiellement pris en charge par cette application/version. | Le nom « Companion App » ne désigne pas une interface universelle. Confirmer l’action/format de diffusion dans la documentation du fournisseur, puis vérifier fraîcheur, unités et doublons. |
| **Nightscout Follower** | Configurer la source follower et l’URL du site Nightscout selon l’écran de configuration de la version installée. L’accès réseau est nécessaire pour récupérer les données. | Vérifier l’URL/API, l’authentification en lecture, la connectivité Internet et le délai de polling. Ne pas choisir simultanément un collecteur local et un follower pour le même flux sans besoin explicite. |
| **Bluetooth direct** | Sélectionner une source xDrip+ compatible avec le transmetteur ou le lecteur Bluetooth concerné ; appairer et autoriser les permissions requises. | « Bluetooth direct » n’est pas un protocole unique. Confirmer modèle, firmware, région et méthode d’appairage dans la documentation xDrip+ du matériel. |
| **Libre avec transmetteur** | Le transmetteur compatible lit le capteur et transmet les données à xDrip+ ; sélectionner la source correspondant au transmetteur et au modèle de Libre. | Vérifier que le transmetteur, sa génération, le capteur et la région sont explicitement pris en charge. Contrôler les pertes Bluetooth et les lectures historiques au démarrage d’une session. |
| **Libre et OOP2** | Traiter OOP2 comme une intégration/implémentation spécifique, souvent communautaire, et non comme une compatibilité garantie par le dépôt officiel xDrip+. N’utiliser que la combinaison capteur/région/version explicitement documentée par le projet qui fournit ce flux et, le cas échéant, par AAPS. | La compatibilité OOP2 n’est pas universelle. Ne pas extrapoler entre Libre 2, Libre 2+, Libre 3 et variantes régionales. Confirmer si le flux fournit des données brutes et si la calibration est autorisée ou interdite par cette configuration. |
| **Montre ou autre appareil comme source** | Dans les architectures explicitement prises en charge, un appareil peut recevoir/relayer des valeurs. | Les fonctions de collecte directe par certaines montres sont spécifiques au matériel/firmware et distinctes de la simple visualisation Wear OS. Tester séparément hors de portée du téléphone. |

**Règle de mise en service :** après sélection ou modification de la source, attendre plusieurs périodes de lecture attendues et vérifier la fraîcheur ainsi que la cohérence du capteur dans xDrip+. Ne pas diagnostiquer une intégration en se basant uniquement sur un cadran ou un serveur distant.

### 1.3 Destinations et flux de données

| Destination | Flux usuel | Configuration et limites |
|---|---|---|
| **Nightscout — envoi** | xDrip+ publie des entrées vers l’API Nightscout via le réglage de synchronisation REST/API disponible dans l’application. | Utiliser l’URL de base attendue par l’écran xDrip+ et l’authentification correspondant au serveur. Les versions xDrip+ peuvent ajouter elles-mêmes le chemin d’entrée ; ne pas ajouter un chemin supplémentaire sans vérifier la valeur attendue. HTTPS est requis hors environnement de test isolé. |
| **Nightscout — réception/follower** | xDrip+ récupère des valeurs du site configuré comme source follower. | C’est un chemin de lecture distinct de l’upload. Il dépend d’Internet et de l’API du site ; une absence de réseau peut rendre les données anciennes. |
| **REST et WebSocket Nightscout** | La documentation xDrip+ décrit notamment l’upload REST/API. D’autres composants de l’écosystème Nightscout peuvent utiliser REST, des flux temps réel ou des WebSockets. | Ne supposez pas que le réglage « Nightscout Sync (REST-API) » configure un WebSocket. Vérifiez le protocole de la version xDrip+ et du consommateur concernés. Si aucun réglage WebSocket explicite n’existe, traitez le flux comme une intégration REST/polling et mesurez son délai. |
| **AndroidAPS (AAPS)** | xDrip+ émet les valeurs via une diffusion Android locale ; AAPS peut sélectionner xDrip+ comme source de glycémie. | Activer l’émission locale dans xDrip+ et sélectionner la source xDrip+ dans le Config Builder AAPS. Confirmer que la dernière valeur et son horodatage apparaissent dans AAPS. AAPS distingue les sources fiables des modes follower/companion ; suivre sa documentation courante et ne pas supposer qu’un flux follower est une source de confiance. |
| **Glucodata et autres applications** | Diffusion locale ou interface spécifique au consommateur. | Les deux applications doivent prendre en charge le même mécanisme et le même format. Les restrictions Android sur les diffusions, les permissions et l’exécution en arrière-plan peuvent varier. Configurer séparément chaque consommateur. |
| **Wear OS et watchfaces** | Intégration Wear xDrip+, watchface compatible, ou interface de données fournie par une autre application. | Vérifier l’option Wear xDrip+ et le guide correspondant à la montre/version. Une complication ou un cadran tiers n’est pas automatiquement alimenté par la diffusion locale Android. Les fonctions de collecte directe depuis la montre ne s’appliquent qu’à des combinaisons explicitement prises en charge. |
| **Master/Follower xDrip+** | Un téléphone « master » collecte ; un ou plusieurs followers reçoivent et affichent/synchronisent les données. | Définir un seul master pour une source donnée, documenter quel appareil collecte et quel appareil relaie, vérifier le délai et l’authentification. Éviter qu’un follower renvoie le même flux au master ou à lui-même. |

### 1.4 Schémas de flux recommandés pour l’audit

```text
Capteur/transmetteur ──Bluetooth/NFC──> xDrip+ (collecteur principal)
                                          ├──diffusion locale──> AAPS / Glucodata / application compatible
                                          ├──REST/API──────────> Nightscout
                                          └──Wear──────────────> montre / watchface compatible
```

```text
Application compagnon ──interface documentée──> xDrip+ (relais ou affichage)
                                                  ├──diffusion locale──> consommateurs
                                                  └──REST/API──────────> Nightscout
```

Le second schéma n’est valable que si l’application compagnon et le build xDrip+ fournissent une interface compatible et activée.

## 2. Audit des paramètres système Android

Les libellés des réglages varient selon Android et le fabricant du téléphone. Autorisez uniquement les accès nécessaires au mode de collecte choisi.

### 2.1 Batterie, Doze et activité en arrière-plan

- Dans les informations de l’application xDrip+, vérifier l’utilisation de la batterie et, lorsque l’option existe, choisir **Sans restriction** ou exclure xDrip+ de l’optimisation de batterie pour un usage de collecte continue.
- Vérifier séparément les politiques du fabricant : démarrage automatique, lancement en arrière-plan, verrouillage dans l’écran des applications récentes ou gestion propriétaire de la veille.
- Ne pas confondre l’exemption d’optimisation avec une garantie d’exécution : Doze, la veille du fabricant, une fermeture forcée, un redémarrage ou la révocation des permissions peuvent interrompre l’acquisition.
- Vérifier qu’aucun économiseur, outil de nettoyage, profil professionnel ou règle MDM ne force l’arrêt de l’application.
- Après redémarrage du téléphone, confirmer que le collecteur reprend et que les nouvelles lectures arrivent sans ouvrir manuellement l’application.

L’exemption est souvent nécessaire sur les téléphones agressifs avec les applications en arrière-plan, mais son caractère obligatoire dépend de l’appareil, du mode de collecte et de la version Android. N’utilisez pas une exemption pour contourner une restriction de sécurité de l’organisation.

### 2.2 Bluetooth, localisation et appareils à proximité

- **Android 11 et antérieur :** la recherche BLE peut nécessiter l’autorisation de localisation et, selon l’appareil/version, l’activation du service de localisation pendant le scan.
- **Android 12 et ultérieur :** Android introduit les permissions **Appareils à proximité** (`BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT`) pour le scan et la connexion. Certaines versions de xDrip+ ou certains chemins de scan peuvent également demander la localisation.
- Accorder les permissions que le build xDrip+ installé explique comme nécessaires, sans activer un partage de localisation permanent si le système et le fonctionnement n’en ont pas besoin.
- Vérifier le Bluetooth activé, le statut du transmetteur, les appareils associés et l’absence d’une connexion concurrente incompatible.
- Pour un appareil oublié ou un changement de téléphone, suivre la procédure de reset/unpair propre au matériel ; ne pas multiplier les suppressions d’appairage à l’aveugle, car certains capteurs/transmetteurs ont des limites ou procédures spécifiques.

La localisation n’est pas intrinsèquement requise par tout usage Bluetooth LE ; elle a historiquement été demandée par Android pour certaines recherches BLE. L’exigence exacte dépend du niveau d’API et de l’implémentation xDrip+.

### 2.3 Notifications, alarmes prioritaires et Ne pas déranger

- Accorder la permission de notification à partir d’Android 13 lorsque demandée.
- Dans **Paramètres Android > Applications > xDrip+ > Notifications**, vérifier les canaux concernés, leur importance, leur son, leur vibration et l’affichage sur l’écran verrouillé.
- Vérifier volume des notifications, volume d’alarme, mode silencieux et paramètres audio du téléphone. Certains appareils règlent ces volumes séparément.
- Pour contourner **Ne pas déranger**, l’utilisateur doit généralement accorder l’accès spécial « accès aux règles/paramètres Ne pas déranger » à l’application ou au canal. Cette dérogation est optionnelle, explicite, et dépend du système ; elle n’est jamais garantie par xDrip+ seul.
- Tester une alerte en environnement sûr après configuration. Vérifier également l’état du canal après une mise à jour ou une migration de téléphone.
- Les alertes de glycémie ne remplacent pas les alarmes certifiées du système officiel du capteur.

## 3. Configuration des paramètres xDrip+

### 3.1 Repérage des réglages

Les pages et intitulés changent entre versions, traductions et builds. Utiliser la recherche de réglages de xDrip+ si disponible et vérifier les résumés d’option dans l’application avant toute modification. Les catégories ci-dessous indiquent ce qu’il faut auditer, pas une arborescence universelle.

### 3.2 Hardware Data Source et collecteur

1. Ouvrir les réglages xDrip+ et identifier **Hardware Data Source** ou son équivalent.
2. Sélectionner précisément la source correspondant au matériel réellement utilisé.
3. Pour Dexcom G5/G6 ou matériel compatible, vérifier si le build propose **OB1 Collector** et/ou un mode natif. Utiliser le collecteur indiqué comme compatible par la documentation de la version et du transmetteur.
4. Ne pas activer des options de fallback, de code natif ou de reconnexion Bluetooth au hasard : elles peuvent changer le chemin de collecte ou la gestion d’appairage. Consulter le résumé xDrip+ et conserver une note de la valeur initiale avant essai.
5. Vérifier les options de réactivation/récupération du Bluetooth si elles existent. Le téléphone doit avoir l’autorisation système correspondante et la procédure doit être compatible avec son constructeur.
6. Après toute modification, observer les journaux/état système xDrip+ et vérifier plusieurs lectures consécutives, l’horodatage, la tendance et l’absence de doublons.

| Paramètre ou libellé rencontré | Rôle et consigne d’audit |
|---|---|
| **Hardware Data Source** | Choisit la famille de source. La sélectionner d’après le matériel réellement utilisé ; elle n’active pas à elle seule un protocole compatible. |
| **Use the OB1 Collector** | Option présente dans les ressources xDrip+ pour certains chemins de collecte. N’activer que si le matériel/version l’exige ou si la documentation de cette combinaison le recommande. |
| **Use the transmitter’s internal algorithm** | Peut demander l’utilisation de l’algorithme interne du transmetteur lorsque possible. Son effet dépend du matériel ; ne pas le confondre avec un choix universel de calibration. |
| **Allow OB1 unbonding** | Option spécifique au collecteur OB1 et à la gestion de l’appairage. La documentation indique qu’elle peut permettre au collecteur de supprimer l’association dans certains scénarios de chiffrement. Ne la modifier qu’en réponse à un symptôme reproductible et selon les consignes xDrip+ du matériel. |
| **Fallback / options de reconnexion** | Noms et disponibilité variables. Noter la valeur initiale, utiliser un seul mécanisme à la fois et revenir en arrière si la collecte se dégrade. |

Les descriptions d’options de collecteur peuvent dater d’anciennes versions Android ou d’anciens matériels. La présence d’un libellé dans xDrip+ n’est pas une garantie de compatibilité actuelle.

| Élément | Vérification |
|---|---|
| Source matérielle | Correspond au capteur et à son chemin de collecte réel |
| Collecteur OB1/natif | Mode documenté pour ce matériel et cette version ; pas de collecteurs parallèles |
| Bluetooth | Permissions, état radio, statut du transmetteur et association cohérents |
| Fallback | Activé uniquement si documenté et nécessaire ; comportement après perte/retour radio vérifié |
| Redémarrage | Collecte reprise après redémarrage sans action manuelle non prévue |

### 3.3 Raw, valeurs filtrées, calibration et lissage

Les termes **raw**, **filtered**, **calibrated**, **smoothed** et **displayed glucose** ne sont pas interchangeables :

- Les données brutes ne sont disponibles que si le capteur, le transmetteur, le collecteur et le relais les fournissent.
- Les valeurs filtrées ou calculées peuvent inclure un traitement du fabricant, du collecteur ou d’un serveur.
- Une option de lissage peut modifier l’affichage ou le flux remis à une application consommatrice ; auditer son effet dans chaque application.
- Une valeur de calibration n’est pas une simple correction graphique : selon la source, elle peut modifier la conversion entre signal capteur et estimation de glucose.

**Règles de configuration sûres :**

- Ne pas imposer une fréquence de calibration universelle. Suivre les instructions du fabricant et la documentation de la source exacte.
- Ne pas ajouter de calibration manuelle à un capteur conçu pour l’étalonnage usine, sauf si la source/documentation et le professionnel de santé l’autorisent explicitement.
- Pour les flux OOP2/Libre, vérifier la documentation du capteur, de la région et du bridge ; certaines combinaisons interdisent ou ne prennent pas en charge la calibration manuelle.
- Ne pas changer simultanément source, calibration et lissage. Modifier un seul paramètre à la fois, consigner sa valeur précédente et comparer les horodatages/données avec l’application officielle.
- Si AAPS est connecté, vérifier sa documentation de **Data Smoothing** et la qualification de la source. Ne pas activer plusieurs étages de lissage par supposition.
- Ne jamais utiliser les données raw comme mesure clinique indépendante sans comprendre l’algorithme et sa validation pour le matériel utilisé.

### 3.4 Alertes et alarmes

Configurer les alertes dans xDrip+ uniquement après avoir confirmé que les notifications Android fonctionnent. L’emplacement et les capacités dépendent de la version.

| Fonction | À vérifier | Conseil de sûreté |
|---|---|---|
| Seuil bas / urgence hypo | Valeur, unité, répétition, son et canal Android | Les seuils doivent suivre le plan de soins individuel, pas une valeur générique issue de ce guide. |
| Seuil haut / hyper | Valeur, unité, répétition, son et canal Android | Vérifier la conversion mg/dL ↔ mmol/L et l’unité affichée par chaque application. |
| Prévision de baisse/hausse | Fonction activée, fenêtre de prédiction et sensibilité disponibles dans le build | Une prévision est une estimation ; elle peut produire des faux positifs ou manquer une évolution rapide. |
| Rate of rise/fall | Seuils de tendance proposés par l’application, si présents | Ne pas confondre tendance calculée et valeur de glucose confirmée. |
| Réitération/snooze | Durée de répétition, silence temporaire et comportement après redémarrage | Tester qu’une alarme urgente n’est pas silencée durablement par une règle inattendue. |
| Profil sonore/vocal | Canal, son, volume, vibration et synthèse vocale | Tester avec écran éteint, écran verrouillé, casque et mode silencieux si utilisés. |
| DND | Permission spéciale, exceptions système et canal autorisé | Le contournement n’est effectif que si Android et le constructeur le permettent. |

Ne recopiez pas automatiquement les seuils de l’application officielle dans xDrip+ sans vérifier l’unité, l’alarme active et le comportement des répétitions : deux applications peuvent émettre des alertes redondantes.

### 3.5 Diffusion locale et communication inter-applications

- **Broadcast Locally :** active une diffusion locale de nouvelles valeurs pour les applications compatibles ; ce n’est pas un upload Internet.
- Pour **AAPS**, activer la diffusion locale dans xDrip+ puis choisir **xDrip+** comme source de glycémie dans le Config Builder d’AAPS. Contrôler la réception et l’horodatage dans AAPS.
- Pour **Glucodata** ou un autre consommateur, vérifier son propre réglage de source, son format attendu, ses autorisations Android et la version du protocole.
- Les options d’envoi et de réception sont distinctes. Une app peut recevoir une diffusion sans que xDrip+ reçoive ses données.
- Ne pas activer en même temps plusieurs sources AAPS ou chemins Nightscout comme solution de secours sans savoir lequel est prioritaire.
- Après une mise à jour Android, vérifier l’association des applications, les permissions et la livraison des diffusions en arrière-plan.
- Pour un watchface, choisir une intégration documentée par ce cadran : diffusion xDrip+, application Wear, complication, ou service Nightscout local. Ne pas supposer que le cadran lit automatiquement `Broadcast Locally`.
- Si une montre ou un cadran utilise le serveur Web local xDrip+, vérifier les options **Open Web Service** et authentification. Le service est normalement local (`127.0.0.1`, port `17580`) ; l’ouvrir sur les interfaces réseau peut exposer des endpoints de données et de contrôle. Le laisser fermé sauf besoin explicite, et ne jamais le publier sur Internet ou un réseau non fiable.

### 3.6 Nightscout REST/API et follower

**Pour l’upload :**

1. Confirmer que le serveur Nightscout est joignable en HTTPS depuis le téléphone.
2. Utiliser le réglage xDrip+ de synchronisation Nightscout REST/API.
3. Entrer l’URL de base et le secret exactement comme l’écran de la version installée le demande. La documentation/les libellés xDrip+ indiquent que l’application peut compléter le chemin d’entrée automatiquement.
4. Activer l’envoi, vérifier l’état et comparer une nouvelle entrée côté Nightscout à l’heure affichée dans xDrip+.
5. Si la configuration est un follower, valider séparément l’accès en lecture et la fraîcheur ; un upload réussi ne prouve pas que le follower reçoit les données.

**Sécurité de l’URL et du secret :** ne copiez jamais une URL contenant un secret dans un ticket, un journal, un navigateur partagé, une capture d’écran ou une commande. Si l’interface place le secret dans l’URL, traitez l’URL comme un credential ; utilisez uniquement HTTPS, limitez son exposition et faites tourner le secret si celui-ci a été divulgué. Ne réutilisez pas un secret Nightscout comme clé xDrip Sync.

La documentation xDrip+ de référence décrit un réglage REST/API. N’attribuez pas un flux WebSocket à ce réglage sans vérification explicite de la version concernée.

## 4. Grille d’audit et dépannage

### 4.1 Checklist d’audit rapide

| Contrôle | Résultat attendu | Statut / preuve |
|---|---|---|
| Version xDrip+ identifiée | Version/build relevé ; source de téléchargement reconnue | ☐ |
| Source primaire documentée | Capteur, transmetteur, application source et région connus | ☐ |
| Collecteur unique | Pas de collecte concurrente non intentionnelle pour la même session | ☐ |
| Dernière lecture locale | Horodatage récent et évolution cohérente dans xDrip+ | ☐ |
| Permissions Android | Bluetooth/à proximité, localisation si demandée, notifications accordées | ☐ |
| Batterie/arrière-plan | Restrictions système et fabricant vérifiées ; reprise après redémarrage testée | ☐ |
| Alarmes | Valeurs et unité vérifiées ; canal et répétition testés sans modifier le plan clinique | ☐ |
| Calibration/lissage | Paramètres conformes à la source et documentés ; aucun réglage empirique non tracé | ☐ |
| Diffusion locale | AAPS/Glucodata reçoit une valeur fraîche et un horodatage correct | ☐ |
| Nightscout | Upload ou follower testé séparément ; URL/permissions confirmées | ☐ |
| Montre | Cadran/intégration pris en charge ; fraîcheur vérifiée indépendamment | ☐ |
| Doublons et décalage horaire | Pas de valeurs répétées, futures ou mal ordonnées | ☐ |
| Secrets | Non présents dans captures, tickets, journaux ou sauvegardes non protégées | ☐ |
| Sauvegarde | Export protégé et import testé uniquement sur une installation contrôlée | ☐ |

### 4.2 Procédure de diagnostic des pertes Bluetooth

Procéder du diagnostic non destructif au plus intrusif et conserver les journaux avant de réinitialiser un appairage.

1. **Établir le périmètre**
   - Noter modèle du téléphone, version Android, build xDrip+, capteur/transmetteur, région et heure de la dernière lecture.
   - Comparer l’application officielle/compagnon à xDrip+ : la donnée manque-t-elle à la source, dans xDrip+ ou seulement dans la destination ?
2. **Contrôler les prérequis**
   - Bluetooth activé ; permission Appareils à proximité accordée sur Android récent.
   - Accorder la localisation si le build xDrip+ la demande pour son scan ; sur les anciens Android, vérifier aussi que le service de localisation est activé.
   - Écarter mode avion, économie extrême, restriction fabricant, fermeture forcée et conflit avec une autre application connectée au transmetteur.
3. **Vérifier le collecteur**
   - Confirmer la bonne Hardware Data Source et le bon mode OB1/natif pour la combinaison réelle.
   - Vérifier l’état système xDrip+ et les événements du collecteur ; relever les erreurs exactes et leurs heures.
   - Ne pas basculer plusieurs options Bluetooth simultanément. Modifier une option à la fois, puis attendre assez longtemps pour constater l’effet.
4. **Vérifier la veille et le redémarrage**
   - Tester écran éteint et après une période de veille.
   - Contrôler l’exemption de batterie et les paramètres de démarrage automatique du constructeur.
   - Redémarrer le téléphone uniquement après avoir relevé l’état et les journaux ; confirmer la reprise du collecteur.
5. **Isoler le matériel**
   - Comparer à l’application officielle et examiner les instructions de reconnexion du fabricant/transmetteur.
   - Ne pas réinitialiser ou désappairer le capteur sans suivre la procédure officielle applicable.
6. **Collecter des éléments utiles**
   - Activer temporairement les journaux supplémentaires uniquement si nécessaire.
   - Reproduire le problème, exporter les journaux depuis les fonctions intégrées et expurger identifiants, URL secrètes, numéros de série et données de santé avant tout partage.
   - Utiliser les conseils xDrip+ de dépannage Bluetooth pour le mode/transmetteur concerné. Les conseils tels que « Trust Auto Connect », « close GATT on disconnect », « Use scanning » ou « Always discover services » sont issus de documentation de dépannage historique et concernent certains collecteurs ; ils ne constituent pas des réglages universels ni une garantie pour la version actuelle.

### 4.3 Intégrité des données, horodatages et synchronisation

- **Lecture ancienne :** comparer l’heure de la mesure, l’heure du téléphone et l’état du collecteur. Une valeur répétée ne signifie pas nécessairement une nouvelle mesure.
- **Lecture future ou désordonnée :** vérifier date, fuseau horaire automatique, synchronisation réseau de l’heure, changement de fuseau et origine de la mesure. Ne corriger ni supprimer des entrées avant d’avoir identifié leur source.
- **Doublons :** rechercher deux collecteurs actifs, un master renvoyant vers lui-même, plusieurs sources de glycémie sélectionnées ou un même enregistrement importé par deux chemins.
- **Écart entre xDrip+ et Nightscout :** valider d’abord xDrip+ localement, puis le réseau, l’URL, l’authentification et le dernier enregistrement côté serveur. Vérifier que l’envoi et le follower ne sont pas confondus.
- **Écart entre xDrip+ et AAPS :** contrôler la diffusion locale, la source sélectionnée dans AAPS, l’unité, le timestamp et la qualification de source attendue par la version AAPS.
- **Montre en retard :** déterminer si l’application téléphone, le Data Layer, la synchronisation Internet ou le cadran constitue le maillon en retard. Vérifier la dernière mise à jour affichée plutôt que la seule présence d’une valeur.
- **Ne pas “réparer” par un lissage ou une calibration empirique :** une correction d’affichage peut masquer une interruption de données sans la résoudre.

Pour les usages AAPS, la documentation AAPS consultée distingue explicitement les sources dites fiables : elle cite certains chemins xDrip+ **Direct/Native** pour Dexcom et un cas Libre 2 EU/OOP2 sans calibration, tout en indiquant que les modes follower/companion ne sont pas des sources fiables. Cette liste est spécifique à AAPS et peut évoluer ; vérifier la version actuelle, le capteur et le mode précis avant toute intégration. Ne déduire aucune recommandation thérapeutique de cette classification logicielle.

## 5. Sauvegarde et sécurité

### 5.1 Clés d’accès et données sensibles

- Considérer le secret Nightscout, toute clé xDrip Sync, les URL d’API et les données exportées comme sensibles.
- Utiliser HTTPS pour les communications hors appareil et éviter les serveurs ou points d’accès non fiables.
- Ne pas publier de capture d’écran des réglages, de journal brut, de QR code, d’export ou d’URL contenant des identifiants.
- Ne pas inscrire les secrets dans un dépôt Git, un document partagé, une commande d’assistance ou un fichier non chiffré.
- Distinguer le secret d’accès Nightscout de la clé de synchronisation xDrip et de tout token émis par une application compagnon.
- Si la synchronisation xDrip entre appareils est utilisée, configurer et partager la clé uniquement entre les appareils prévus, selon les instructions de la version installée. Ne pas supposer que cette clé est interchangeable avec un secret Nightscout ; la traiter comme un credential et la protéger dans les sauvegardes.
- En cas d’exposition, révoquer/renouveler le secret depuis le service concerné et mettre à jour les clients autorisés.
- Pour le mode follower, préférer un accès en lecture minimale lorsque l’infrastructure le permet. Pour l’upload, n’accorder que les droits nécessaires au fonctionnement attendu.
- Protéger physiquement le téléphone et le stockage des exports ; une sauvegarde xDrip+ peut inclure des données de santé et/ou des credentials.

### 5.2 Export et import des paramètres

Les intitulés exacts des écrans d’export/import peuvent changer. xDrip+ possède des fonctions distinctes d’import/export local et de sauvegarde/restauration ; ne supposez pas qu’un export de paramètres équivaut à une sauvegarde complète de la base de données. Le code amont consulté comporte un mécanisme de sauvegarde chiffrée, mais cela ne signifie pas que chaque export ou fichier de réglages est chiffré.

**Avant un export :**

1. Ouvrir la fonction d’export des paramètres xDrip+.
2. Vérifier si l’export contient des secrets, des données de capteur ou uniquement des préférences.
3. Enregistrer le fichier dans un emplacement privé et protégé ; chiffrer le support si l’export contient des données ou credentials.
4. Garder une copie de la version xDrip+ et de la date d’export séparément du fichier.
5. Ne pas envoyer l’export dans un outil cloud ou de support sans en comprendre le contenu et le niveau de protection.

**Avant un import :**

1. Confirmer l’origine du fichier, la version source et sa compatibilité avec le build cible.
2. Effectuer une sauvegarde séparée de l’installation cible avant restauration.
3. Importer uniquement dans xDrip+ et lire le résultat/les éventuels avertissements.
4. Contrôler la source matérielle, les identifiants, les permissions Android, les alertes, l’unité, la diffusion locale et les destinations après import.
5. Ne pas considérer la restauration de préférences comme un transfert des associations Bluetooth, des autorisations Android ou d’une session active.
6. Tester les nouvelles lectures et les sorties une par une avant de supprimer l’ancien téléphone ou son export.

La portée exacte des fichiers exportés, des clés restaurées et de la compatibilité entre versions n’est pas garantie par une procédure publique unique pour tous les builds. Examiner le fichier et tester l’import sur un appareil contrôlé ; ne pas utiliser un export de test comme seule sauvegarde.

## Références

- [xDrip+ — dépôt officiel Nightscout Foundation](https://github.com/NightscoutFoundation/xDrip)
- [xDrip+ — release amont consultée `2026.03.01`](https://github.com/NightscoutFoundation/xDrip/releases/tag/2026.03.01)
- [xDrip+ — guide Wear et dépannage](https://github.com/NightscoutFoundation/xDrip/blob/master/Documentation/WatchGuide.md)
- [xDrip+ — intégration par diffusion vers xDrip+](https://github.com/NightscoutFoundation/xDrip/blob/master/Documentation/technical/Incoming_Glucose_Broadcast.md)
- [xDrip+ — services Web locaux](https://github.com/NightscoutFoundation/xDrip/blob/master/Documentation/technical/Local_Web_Services.md)
- [xDrip+ — dépannage Bluetooth Libre](https://github.com/NightscoutFoundation/xDrip/blob/master/Documentation/libre-fix-bt-connection-issues.md)
- [xDrip+ — implémentation amont de la sauvegarde](https://github.com/NightscoutFoundation/xDrip/blob/master/app/src/main/java/com/eveningoutpost/dexdrip/cloud/backup/Backup.java)
- [xDrip+ — import/export local](https://github.com/NightscoutFoundation/xDrip/blob/master/app/src/main/java/com/eveningoutpost/dexdrip/utils/SdcardImportExport.java)
- [xDrip+ — page d’information et téléchargements](https://jamorham.github.io/#xdrip-plus)
- [Android — optimisation de batterie et tâches en arrière-plan](https://developer.android.com/develop/background-work/background-tasks/optimize-battery)
- [Android — permissions Bluetooth](https://developer.android.com/develop/connectivity/bluetooth/bt-permissions)
- [Android — permission de notification](https://developer.android.com/develop/ui/views/notifications/notification-permission)
- [AndroidAPS — sources CGM compatibles](https://androidaps.readthedocs.io/en/latest/Getting-Started/CompatiblesCgms.html)
- [AndroidAPS — configuration xDrip+](https://androidaps.readthedocs.io/en/latest/CompatibleCgms/xDrip.html)

Les documentations xDrip+ et AndroidAPS évoluent. En cas de divergence, la documentation à jour du matériel, du build et de la version réellement installés prévaut sur ce guide.
