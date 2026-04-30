<h1 align="center">🔐 Checklist juridique avant diffusion</h1>

<p align="center">
  Tant que ce n'est pas complet, l'APK reste en test privé
</p>

---

## 🛑 Décision

```text
╭─ Publication ──────────────────────────╮
│  APK public : non                      │
│  Test privé : oui                      │
│  Condition  : champs juridiques relus  │
╰────────────────────────────────────────╯
```

Les documents juridiques existent, mais ils ne sont pas prêts pour une diffusion publique.

---

## 📜 CGU

Document : [CGU.md](CGU.md)

| Champ | Statut |
| --- | --- |
| Nom ou raison sociale | `[À compléter]` |
| Forme juridique | `[À compléter]` |
| Adresse | `[À compléter]` |
| E-mail de contact | `[À compléter]` |
| Responsable de publication | `[À compléter]` |
| Droit applicable | `[À compléter]` |
| Juridictions compétentes | `[À compléter]` |
| Contact final | `[À compléter]` |

---

## 🔒 Confidentialité

Document : [POLITIQUE_CONFIDENTIALITE.md](POLITIQUE_CONFIDENTIALITE.md)

| Champ | Statut |
| --- | --- |
| Responsable de traitement | `[À compléter]` |
| Adresse | `[À compléter]` |
| E-mail vie privée | `[À compléter]` |
| Prestataires techniques | `[À compléter]` |
| Autorité de contrôle | `[À compléter]` |

---

## 📱 Textes embarqués

Les mêmes informations doivent être reportées dans l'app :

| Fichier | Rôle |
| --- | --- |
| [cgu.txt](../mobile/src/main/res/raw/cgu.txt) | CGU affichées dans l'app |
| [politique_confidentialite.txt](../mobile/src/main/res/raw/politique_confidentialite.txt) | Confidentialité affichée dans l'app |

---

## ✅ Sortie de checklist

```text
tous les champs remplis
textes relus
textes embarqués alignés
aucune donnée personnelle non voulue
validation juridique si diffusion publique
```
