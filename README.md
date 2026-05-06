<span class="wg7-skip-markdown-theme" hidden aria-hidden="true"></span>

<h3 align="center">🟢 Widget G7 garde la glycémie Dexcom G7 visible sur Wear OS.</h3>

<p align="center">
  Le téléphone récupère · il synchronise · la montre affiche · l'utilisateur garde le contrôle
</p>

<p align="center">
  <img alt="Android" src="https://img.shields.io/badge/Android-mobile-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Wear OS" src="https://img.shields.io/badge/Wear%20OS-watch-4285F4?style=for-the-badge&logo=wearos&logoColor=white">
  <img alt="Dexcom" src="https://img.shields.io/badge/Dexcom%20Share-source-00A86B?style=for-the-badge">
  <img alt="Status" src="https://img.shields.io/badge/sync-active-22C55E?style=for-the-badge">
</p>

<p align="center">
  <a href="docs/user-quick-notice.md">⚡ Notice rapide</a>
  ·
  <a href="docs/user-manual.md">📘 Mode d'emploi</a>
  ·
  <a href="docs/developer-handoff.md">🧭 Reprise projet</a>
  ·
  <a href="docs/technical-wear-os-sync.md">🔁 Technique Wear OS</a>
  ·
  <a href="docs/index.md">🗂️ Documentation</a>
</p>

---

## 🟢 Console

```text
╭────────────────────────────────────────╮
│              WIDGET G7                 │
├────────────────────────────────────────┤
│  > connect Dexcom Share                │
│  > read latest glucose                 │
│  > push to Wear OS                     │
│  > wait for watch ack                  │
│  > repush if needed                    │
│  > keep tile and complication fresh    │
╰────────────────────────────────────────╯
```

> 💚 **Widget G7 n'est pas un dispositif médical officiel.**
>
> Il relaie une donnée Dexcom vers Wear OS. Les décisions de traitement restent à confirmer dans l'application Dexcom G7, le récepteur Dexcom ou une solution officielle adaptée.

---

## ✨ Ce que Widget G7 apporte

| 🧩 Surface | 🎯 Rôle |
| --- | --- |
| 📱 **App mobile** | Connexion Dexcom, état de sync, test montre |
| ⌚ **App Wear** | Affichage rapide de la dernière valeur reçue |
| 🧱 **Tile Wear OS** | Glycémie accessible depuis le carrousel Wear |
| 🕘 **Complication** | Valeur visible sur les cadrans compatibles |
| 🔁 **Sync active** | Service foreground, push urgent, ack et repush borné |
| 🔋 **Veille** | Demande d'exemption batterie pour stabiliser la sync |
| 🎯 **Multi-montres** | Choix d'une montre principale et ciblage défensif |

```text
Dexcom Share
le téléphone récupère
Wear OS reçoit
la montre affiche
```

---

## 🚀 Démarrage

> ⚙️ **Installation APK**

```text
1. Installer l'APK mobile sur le téléphone
2. Installer l'APK wear sur la montre
3. Ouvrir Widget G7 sur le téléphone
4. Accepter les textes requis
5. Se connecter à Dexcom Share
6. Tester l'envoi vers la montre
7. Ajouter la tile ou la complication
```

> 🛠️ **Depuis le code source** (Linux/Unix)

```bash
./gradlew :mobile:assembleDebug :wear:assembleDebug
```

Sous Windows: `.\gradlew.bat :mobile:assembleDebug :wear:assembleDebug`

Voir aussi : [COMPATIBILITY.md](COMPATIBILITY.md) · **[Structure du dépôt](docs/structure-repository.md)** (où vit le code, la doc et les modules).

---

## 🔁 Synchronisation

| Étape | Action |
| --- | --- |
| 1 | Le téléphone interroge Dexcom Share |
| 2 | Il garde la dernière valeur connue |
| 3 | Il pousse la valeur vers Wear OS avec un `sequenceId` |
| 4 | La montre met à jour son cache, sa tile et sa complication |
| 5 | La montre renvoie un ack au téléphone |
| 6 | Le téléphone repousse la valeur si l'ack attendu manque |

```text
╭─ Mode principal ───────────────────────╮
│ Dexcom Share -> téléphone -> Wear OS   │
╰────────────────────────────────────────╯
```

La montre peut demander un refresh, mais elle ne lit pas directement le capteur G7 dans la version actuelle.

---

## 🧭 Direction technique

| Sujet | Décision |
| --- | --- |
| Mode principal | `Dexcom Share -> téléphone -> Wear OS` |
| Sync | Service foreground, polling rapproché, ack montre |
| Batterie | Exemption recommandée pour la veille longue |
| Multi-montres | Montre principale ciblée via `targetNodeId` |
| Direct capteur | Piste expérimentale, non implémentée |

> 🧪 **Mode direct capteur**
>
> Le mode `capteur G7 -> Wear OS` ne doit pas entrer dans l'app principale avant un spike BLE concluant, une validation batterie et un audit sécurité.

---

## 📚 Documentation

| Document | Usage |
| --- | --- |
| [Structure du dépôt](docs/structure-repository.md) | Arborescence `mobile/` / `wear/` / sync / doc |
| [Index documentation](docs/index.md) | Choisir le bon document selon le besoin |
| [Notice rapide](docs/user-quick-notice.md) | Lire l'essentiel côté utilisateur |
| [Mode d'emploi](docs/user-manual.md) | Installer, connecter Dexcom, configurer la montre |
| [Wear — assistant installation (ADB)](docs/technical-wear-os-sync.md) | Sync, installer l’APK Wear depuis le téléphone sans Store |
| [Reprise projet](docs/developer-handoff.md) | Reprendre le développement sans relire tout l'historique |
| [Technique Wear OS](docs/technical-wear-os-sync.md) | Sync, audit Dexcom, direct capteur et spike BLE |

---

## 🔐 Données sensibles

```text
Ne jamais publier :
identifiants Dexcom
tokens ou sessions
données de glycémie réelles
serials d'appareils
codes capteur
keystores et fichiers locaux privés
```

Les exemples doivent rester fictifs, anonymisés ou génériques.

---

```text
╭────────────────────────────────────────╮
│  > sync stays active                   │
│  > watch stays readable                │
│  > medical decisions stay official     │
╰────────────────────────────────────────╯
```
