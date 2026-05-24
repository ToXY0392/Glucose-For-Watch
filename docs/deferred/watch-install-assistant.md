# Assistant installation montre (retiré de l’UI)

> **Statut :** fonctionnalité conservée dans le code, **masquée dans l’app** depuis 2026-05-23.  
> **Raison :** pas utile pour le moment ; l’installation se fait via ADB / scripts QA.

## Ce qui a été retiré

| Emplacement | Élément | Action |
|-------------|---------|--------|
| Accueil (`activity_main.xml`) | Carte **« Aide installation montre »** (`wearAssistantCard`) | Supprimée |
| Accueil | Grille 2 colonnes Dexcom / Montre + assistant | Carte **Montre** seule, pleine largeur |
| Montre (`activity_watch_setup.xml`) | Bouton **« Aide installation montre »** (`openWearInstallerButton`) | Supprimé |
| `MainActivity.kt` | Navigation vers `WearInstallerActivity` | Supprimée |
| `WatchSetupActivity.kt` | Idem | Supprimée |

Libellés retirés de `strings.xml` :

- Titre : **Aide installation montre**
- Sous-titre accueil : **Guide pas à pas pour installer l’app sur la montre**

## Ce qui reste (code intact)

Le module et l’écran existent toujours ; ils ne sont plus accessibles depuis l’UI.

| Composant | Chemin |
|-----------|--------|
| Activity | `mobile/.../ui/WearInstallerActivity.kt` |
| Module | `feature/watch-install/` (`WearDirectAdbInstaller`, OCR pairing, APK embarqué) |
| Manifest | `WearInstallerActivity` déclarée dans `mobile/src/main/AndroidManifest.xml` |
| Layout | `mobile/src/main/res/layout/activity_wear_installer.xml` |
| Strings install | `wear_install_*` dans `strings.xml` |

Fonctionnalités de l’assistant :

- Pairing ADB Wi‑Fi (IP, ports, code)
- Installation directe de l’APK wear embarqué via Kadb
- OCR sur capture / photo pour pré-remplir IP et ports depuis l’écran de pairing Wear OS

## Installation actuelle (recommandée)

- Script : `scripts/qa/install-and-verify.ps1`
- Manuel : [User manual — Installation](../user/manual.md)
- APK : `mobile-debug.apk` (téléphone) + `wear-debug.apk` (montre)

## Réactiver dans l’app

1. **Accueil** — dans `activity_main.xml`, réintroduire `wearAssistantCard` à côté de `installCard` (guideline `tileMidGuide` à 50 %) ou ajouter un lien depuis la carte Montre.
2. **Montre** — remettre `openWearInstallerButton` dans `activity_watch_setup.xml` et le listener dans `WatchSetupActivity.kt`.
3. **Navigation** — dans `MainActivity.kt` :
   ```kotlin
   findViewById<View>(R.id.wearAssistantCard).setOnClickListener {
       startActivity(Intent(this, WearInstallerActivity::class.java))
   }
   ```
4. **Strings** — restaurer `wear_setup_install_assistant` et `home_wear_assistant_subtitle`.
5. **Test debug** — lancer `WearInstallerActivity` :
   ```bash
   adb shell am start -n com.widgetg7.mobile/.ui.WearInstallerActivity
   ```

## Références

- [Dual IDE setup](../development/dual-ide-setup.md) — chemins WSL / Windows pour Gradle et adb
- [Getting started](../development/getting-started.md)
