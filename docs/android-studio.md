# Widget G7 — Guide Android Studio

Ce document résume comment travailler sur **Widget G7** depuis **Android Studio** (Windows ou ouverture d’un dossier WSL via `\\wsl$\…`).  
Pour **Cursor + même dépôt WSL**, voir `developpement-double-ide-cursor-studio.md`.  
Pour **IDE instable / droits écriture / Defender**, voir `COMPATIBILITY.md` (racine du dépôt).

---

## Références officielles (manuel complet)

Documentation Google (à garder sous la main pour l’usage générique de Studio) :

| Sujet | URL |
|--------|-----|
| Présentation & téléchargement | https://developer.android.com/studio |
| Guide utilisateur | https://developer.android.com/studio/intro |
| Build / Gradle / AGP | https://developer.android.com/build |
| Lancer et déboguer | https://developer.android.com/studio/run |
| Profiler | https://developer.android.com/studio/profile |
| Versions AGP ↔ Gradle | https://developer.android.com/build/releases/gradle-plugin |

Plate-forme IntelliJ (options IDE communes, VCS…) : https://www.jetbrains.com/help/idea/

---

## Ce dépôt : versions outils attendues

Définies dans le repo (ne pas changer sans raison forte) :

- **Android Gradle Plugin** **8.13.2**
- **Gradle** wrapper **8.13** (`gradle/wrapper/gradle-wrapper.properties`)
- **Kotlin Android** plugin **2.3.20** (`build.gradle.kts` racine)
- **`compileSdk`** / **`targetSdk`** **36** (modules `mobile` et `wear`)

Installe ou mets à jour le **SDK Platform 36** et les **Android SDK Build-Tools** compatibles depuis le **SDK Manager** dans Studio.

---

## Où ouvrir le projet

### Option A — Dossier sur disque Windows (classique USB / ADB Windows)

Copie ou clone : exemple `C:\Dev\Widget-G7` (**éviter espaces dans le nom du dossier**).

### Option B — Même fichier que sous WSL (Cursor sur Linux)

- **Android Studio** (sur Windows) : **File → Open**, chemin UNC du type :  
  `\\wsl.localhost\Ubuntu\home\<linux_user>\...\PROJECTS\Widget-G7`  
  ou variant `\\wsl$\Ubuntu\...` (voir `wsl -l -v`).
- Ou depuis une console WSL à la racine du repo :  
  `./scripts/dev/open-android-studio-wsl-project.sh`

Ne pas garder **deux** copies différentes (Desktop + WSL) comme « vérités » : une seule arborescence de travail par machine si possible.

---

## Première ouverture : SDK et `local.properties`

`local.properties` est **non versionné** (`.gitignore`). Il doit contenir au minimum **`sdk.dir`** pointant vers le **SDK Android utilisé par le même système qui lance Gradle** pour cette séance.

Exemple **Windows** :

```properties
sdk.dir=C\:\\Users\\Utilisateur\\AppData\\Local\\Android\\Sdk
```

(adapte le chemin : **File → Settings → Languages & Frameworks → Android SDK** affiche « Android SDK location ».)

Exemple **Linux / Gradle lancé dans WSL** :

```properties
sdk.dir=/home/linux_user/Android/Sdk
```

Quand Studio **Windows** fait la sync Gradle sur un projet **`\\wsl$\...`**, le daemon Gradle utilise en pratique l’outil **Windows** : utilise en général un **`sdk.dir` Windows**. Si tu compilés surtout avec **`./gradlew` dans WSL**, garde **`sdk.dir` Linux** et accepte une resynchro différente côté Studio selon tes besoins (voir doc double‑IDE).

---

## JDK Gradle (JBR Studio)

Pour que Gradle dans Studio soit cohérent avec le bundle Android Studio :

- **File → Settings → Build, Execution, Deployment → Build Tools → Gradle** → **Gradle JDK** : choisir le **JDK embarqué** (étiquette du type « jbr-xx » livré avec Studio).

En complément sous **Windows**, on peut aussi poser :

`org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr`  

 dans **`%USERPROFILE%\.gradle\gradle.properties`** (fichier **utilisateur**, pas le dépôt). Le fichier **`gradle.properties` du projet** évite une `org.gradle.java.home` figée par machine ; sous **Linux**, on s’appuie sur **`JAVA_HOME`** (voir commentaires dans `gradle.properties` à la racine).

---

## Sync Gradle et modules

- Après ouverture : **Sync Project with Gradle Files** si besoin (**File → Sync** ou bouton éléphant).
- Modules **`mobile`** (application téléphone) et **`wear`** (montre).

Créations de **Run/Debug configurations** typiques :

1. **`mobile`** → sélection du module `mobile`, type **Android App**.
2. **`wear`** → module `wear`, type **Wear OS**.

Le **same `applicationId`** sur mobile et wear en debug reflète une contrainte du projet ; utilise **deux exécutions** (téléphone puis montre) ou une tâche Gradle dédiée (ci‑dessous).

---

## Installation debug sur téléphone + montre (ADB)

Une tâche racine existe dans `build.gradle.kts` : **`installWidgetG7Debug`**.

**Prérequis** dans **`local.properties`** (ou équivalent **`WIDGETG7_*`** dans l’environnement selon implémentation) :

```properties
widgetg7.adb.phone.serial=<série adb du téléphone>
widgetg7.adb.watch.serial=<série adb de la montre>
```

Séries listées avec **`adb devices -l`** (SDK **Windows** ou **Linux** suivant où tourne Gradle).

Depuis Studio : fenêtre **Gradle** (triangle) → tâche **`widget g7` → `installWidgetG7Debug`**  
ou ligne de commande dans le dossier projet :

- Windows : `.\gradlew.bat installWidgetG7Debug`
- Linux / WSL : `./gradlew installWidgetG7Debug`

Cela enchaîne **`assembleDebug`** mobile + wear puis **`adb install`** sur les deux séries configurées.

Détails : fichier **`.cursor/rules/widget-g7-reinstall-apks.mdc`** et `README.md` (section ADB).

---

## Déboguer et USB

Pour **ADB et USB physique confortables** avec Wear : faire tourner **Android Studio sur Windows** et brancher téléphone et montre (ou ADB pairing sans fil pour la montre selon votre setup Google).

Le **SDK Manager** installe **`platform-tools`** (`adb`). Vérifie que **USB debugging** et autorisations appareils sont OK.

Si le projet est **uniquement** sous Linux pur (pas `\\wsl$` depuis Windows Studio), prévoir **`usbipd`** ou **`adb`** Wi‑Fi pour attacher USB depuis l’hôte Windows — hors scope de ce fichier court ; préférez alors le flux Studio **Windows**.

---

## Dépannage rapide projet

| Symptôme | Piste |
|----------|--------|
| « Write Permissions » ou import bloqué (Windows) | `COMPATIBILITY.md`, scripts **`scripts/windows/*.ps1`**, dossier projet hors Desktop/OneDrive si possible |
| Sync OK en terminal mais pas dans l’IDE | Gradle JDK ≠ JBR, ou cache daemon : `./gradlew --stop` puis resync |
| SDK introuvable | `sdk.dir` dans `local.properties` + même OS que Gradle |
| Build OK, pas de device | Câbles, mode USB, liste `adb devices` |

Pour **Gradle / Kotlin daemon** (« Could not connect to Kotlin compile daemon »), le projet active **`kotlin.compiler.execution.strategy=in-process`** dans `gradle.properties` à la racine.

---

## Liens docs internes

- `COMPATIBILITY.md` — environnement, Windows Defender, chemins doubles
- `developpement-double-ide-cursor-studio.md` — Cursor + Studio, `\\wsl$`
- `structure-repository.md` — arborescence modules
- `README.md` — build ADB synthétique
