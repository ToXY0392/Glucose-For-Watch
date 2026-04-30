<h1 align="center">📘 Mode D'emploi Widget G7</h1>

<p align="center">
  Du téléphone à la montre, sans lecture directe du capteur
</p>

---

## 🟢 Parcours

```text
╭────────────────────────────────────────╮
│  > installer mobile                    │
│  > installer wear                      │
│  > connecter Dexcom Share              │
│  > choisir la montre                   │
│  > tester l'envoi                      │
│  > ajouter tile ou complication        │
╰────────────────────────────────────────╯
```

---

## 1. Installer

| APK | Appareil |
| --- | --- |
| `mobile` | Téléphone Android |
| `wear` | Montre Wear OS |

Ouvrir ensuite Widget G7 sur le téléphone.

---

## 2. Connecter Dexcom

1. Ouvrir l'écran Dexcom.
2. Accepter les textes requis.
3. Saisir les identifiants Dexcom Share.
4. Choisir la région Dexcom.
5. Enregistrer.

> Une fois Dexcom configuré, Widget G7 lance une sync active côté téléphone avec notification permanente.

---

## 3. Configurer La Montre

| Étape | Action |
| --- | --- |
| 1 | Ouvrir les paramètres montre |
| 2 | Vérifier que la montre est détectée |
| 3 | Choisir la montre principale si plusieurs montres sont connectées |
| 4 | Appuyer sur `Tester l'envoi` |
| 5 | Autoriser la sync en veille si Android le propose |
| 6 | Ajouter la tile ou la complication depuis Wear OS |

---

## 4. Comprendre Les Statuts

| Statut | Sens |
| --- | --- |
| `Connectée` | La montre est visible par le téléphone |
| `Sync active` | Le service foreground maintient la surveillance |
| `Montre vérifiée` | La dernière livraison a été confirmée par ack |
| `Aucune nouvelle mesure` | Dexcom Share n'a pas encore publié de valeur plus récente |
| `Erreur` | Vérifier Dexcom, réseau, Bluetooth ou montre |

---

## 🔋 Sync Stable

```text
Téléphone en ligne
Montre connectée
Notification Widget G7 active
Optimisation batterie désactivée
Batterie montre suffisante
```

---

## 🧪 Mode Direct Capteur

Le mode `capteur G7 -> Wear OS` n'est pas disponible dans l'app actuelle.

Il reste expérimental et ne doit pas être intégré sans validation BLE, autonomie et sécurité.
