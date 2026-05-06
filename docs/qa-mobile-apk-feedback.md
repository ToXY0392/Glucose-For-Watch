# APK mobile — retours rapides

Fichier prévu pour **cocher en un clic** ce qui ne va pas (dans l’éditeur : place le curseur sur `[ ]` et utilise la commande **Toggle Task** si tu l’as liée au clavier, ou remplace manuellement `[ ]` par `[x]`).

---

## Où sont les APK

| Variant | Chemin (relatif au dépôt) |
| --- | --- |
| **Mobile debug** | `mobile/build/outputs/apk/debug/mobile-debug.apk` |
| **Mobile release** | `mobile/build/outputs/apk/release/` (nom selon config) |
| **Wear debug** (source avant embarquement) | `wear/build/outputs/apk/debug/wear-debug.apk` |
| **Wear embarqué dans le mobile (debug)** | `mobile/build/embeddedWearApk/wear/widget-g7-wear.apk` (généré au build) |

Tâche Gradle utile : `:mobile:assembleDebug` (enchaîne aussi la copie Wear → assets via `prepareWearApkForDebugAssets`). Voir `mobile/build.gradle.kts`.

---

## Ce qui ne me va pas — coche tout ce qui s’applique

### Taille, build, installation

- [ ] APK mobile trop lourd
- [ ] Temps de build trop long
- [ ] `install` / `adb` ne marche pas comme prévu
- [ ] Problème avec la tâche `installWidgetG7Debug` ou `local.properties` (serials)

### APK Wear embarqué dans le mobile

- [ ] Je ne veux pas embarquer le Wear dans le debug / la taille me gêne
- [ ] Le fichier `widget-g7-wear.apk` n’est pas à jour après build
- [ ] L’assistant install montre ne trouve pas / n’utilise pas le bon APK

### Assistant installation montre (`WearInstallerActivity`)

- [ ] Étape ADB Wi‑Fi peu claire ou instable
- [ ] Pairing / Kadb ne fonctionne pas chez moi
- [ ] OCR photo : inutile ou gênant (même en option)
- [ ] Saisie manuelle des infos montre : trop longue ou confuse

### Sync / service (lié à l’usage de l’APK)

- [ ] Service foreground gênant (notif, batterie)
- [ ] Sync après install ne démarre pas correctement

---

## Notes libres (une ligne ou plus)

Écris ici ce que les cases ne couvrent pas :

-
-
-
