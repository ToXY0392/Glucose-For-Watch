# Cursor + Android Studio sur le même dépôt (WSL)

## Principe

**Une seule copie Git** sous Linux : tous les fichiers sources vivent sous WSL.  
Tu édites le même dossier depuis :

| Outil           | Ouverture |
|-----------------|-----------|
| **Cursor**      | dossier **`/home/.../Widget-G7`** (remote WSL) |
| **Android Studio** (Windows) | **`\\wsl.localhost\Ubuntu\home\<user>\<…>\PROJECTS\Widget-G7`** (ou **`\\wsl$\Ubuntu\...`**) |

Ne pas garder une **deuxième** copie sous `C:\...\Desktop` pour éviter divergence et problèmes de droits ; tout synchro passe par cette arborescence.

## Chemins exemple (adaptation `Ubuntu` et ton user si besoin)

- Linux : `/home/toxy/dossierlinux/PROJECTS/Widget-G7`
- Windows UNC :  
  **`\\wsl.localhost\Ubuntu\home\toxy\dossierlinux\PROJECTS\Widget-G7`**

Découverte WSL : `wsl -l -v`

## Gradle / SDK (`local.properties`)

Fichier **local** non versionné. Selon qui lance Gradle :

| Contexte | `sdk.dir` typique |
|----------|-------------------|
| **`./gradlew` dans un terminal WSL** | Unix, ex. `/home/toxy/Android/Sdk` |
| **Gradle lancé par Android Studio Windows** sur **`\\wsl$\…`** | Souvent **`C:\Users\…\AppData\Local\Android\Sdk`** (échapper les `\`, voir exemple ci‑dessous) |

Une seule ligne `sdk.dir` par fichier : après sync dans Studio sous Windows tu peux devoir passer temporairement au SDK Windows, puis retrouver celui Linux pour du build WSL ; soit tu normalises celui utilisé pour **la sync Studio** dominante sur ta machine.

Exemple ligne Windows :

```properties
sdk.dir=C\:\\Users\\Utilisateur\\AppData\\Local\\Android\\Sdk
```

Voir aussi `COMPATIBILITY.md` (double IDE / Write permissions).

## Lancer Studio sur ce dossier depuis WSL

```bash
./scripts/dev/open-android-studio-wsl-project.sh
```

Ou sous Windows : `scripts/windows/launch-android-studio-with-project.ps1` avec le chemin UNC.

## Conseils sync

- Évite deux **Gradle sync massives en parallele** depuis Cursor + Studio ; après des changements de scripts Gradle/Kotlin volumineux, refaire **Sync Gradle** dans Studio ou **`./gradlew`** dans un seul environnement principal.
- Métadonnées : `.idea` (Studio), `.cursor` (Cursor), `.vscode` cohabitent en général sans conflit.
- Tirets de fin de ligne : même convention (**LF**) dans les deux outils évite les diffs parasites.
