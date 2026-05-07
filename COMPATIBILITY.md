# Compatibilité

## Environnement recommandé

- Android Studio : récent
- Gradle wrapper : `8.13`
- JDK Gradle : JBR Android Studio (`jbr-21`)
- Android : mobile + Wear OS

## Vérification rapide

```powershell
.\gradlew.bat help
.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug
```

Si les commandes finissent en `BUILD SUCCESSFUL`, l'environnement est compatible.

## Windows : dialogue « Write Permissions Issue » dans Android Studio

Si `attrib` / `icacls` ont été appliqués mais que Studio affiche encore *restricted write permissions*,
la cause **la plus fréquente** est **Windows Defender** (accès aux dossiers contrôlés / analyse temps réel), pas NTFS.

1. Ouvre une **invite PowerShell en administrateur** (clic droit → Exécuter en tant qu’administrateur).
2. Exécute (adapte le chemin projet si besoin) :

```powershell
cd "\\wsl$\Ubuntu\home\toxy\dossierlinux\PROJECTS\Widget-G7\scripts\windows"
.\fix-windows-studio-defender-admin.ps1 -ProjectPath "C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"
```

Ce script :

- autorise **`studio64.exe`** si la protection « Accès aux dossiers contrôlés » est active ;
- ajoute aux **exclusions antivirus** le dépôt, `%USERPROFILE%\.gradle`, et les dossiers de config **Google**/Android Studio.

Ensuite **ferme tout Android Studio**, relance‑le, puis **File → Open** sur le même dossier.  
À défaut : utilise le dossier **`\\wsl$\Ubuntu\...\Widget-G7`** ou **`C:\Dev\...`** (hors Bureau/OneDrive) pour éviter ce blocage.

### Ça échoue encore ? Étapes suivantes

1. **Diagnostic** (PowerShell **sans** admin), pour distinguer ACL / Defender / OneDrive :

```powershell
cd "\\wsl$\Ubuntu\home\toxy\dossierlinux\PROJECTS\Widget-G7\scripts\windows"
.\diagnose-windows-studio-project-path.ps1 -ProjectPath "C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"
```

2. **Contourner Desktop / espaces dans le nom** : copier ou recloner le dépôt vers  
   `C:\Dev\Widget-G7` (**sans espace**), puis n’ouvrir **que** ce dossier dans Studio.

3. **Android Studio « Exécuter en tant qu’administrateur »** (double-clic) : uniquement pour **tester**.  
   Si le message disparaît, le problème vient du **niveau d’intégrité** / ACL héritées que l’EXE normal ne passe pas.

4. **Désactiver complètement** *Accès aux dossiers contrôlés* **le temps d’un essai** (PowerShell **admin**) : script `scripts/windows/disable-controlled-folder-access-temp-admin.ps1`.  
   Si Android Studio fonctionne alors → le problème était **uniquement** CFA ; rerun ensuite **`fix-windows-studio-defender-admin.ps1`** (mis à jour : autorise aussi **`java.exe`** du JBR **et** exclusions process `java.exe`), puis **réactive** le CFA depuis Sécurité Windows.

5. Lancer Studio **avec le dossier projet en argument** (évite parfois l’erreur à l’ouverture depuis l’explorateur) :  
   `scripts/windows/launch-android-studio-with-project.ps1 -ProjectPath "C:\Dev\Widget-G7"`

6. **Ouvrir depuis WSL** (recommandé à terme) :  
   `\\wsl$\Ubuntu\home\toxy\dossierlinux\PROJECTS\Widget-G7` dans **File → Open** — même dépôt que sous Linux.

7. **`subst`** (chemins très longs / NTFS bizarre) :

```powershell
subst W: "C:\Users\Utilisateur\Desktop\THP\Projects\Widget G7"
```

puis dans Studio ouvrir **`W:\`**. Démontage : `subst W: /d`.
