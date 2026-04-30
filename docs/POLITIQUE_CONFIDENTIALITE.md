<h1 align="center">🔒 Politique De Confidentialité</h1>

<p align="center">
  Modèle de travail · données sensibles · à compléter avant diffusion
</p>

---

## 🛑 Statut

```text
╭─ Confidentialité ──────────────────────╮
│  publication publique : non prête      │
│  données de santé    : possibles       │
│  validation RGPD     : recommandée     │
╰────────────────────────────────────────╯
```

Voir : [LEGAL_PUBLICATION_CHECKLIST.md](LEGAL_PUBLICATION_CHECKLIST.md)

---

## 1. Responsable Du Traitement

| Champ | Valeur |
| --- | --- |
| Nom / raison sociale | `[A completer]` |
| Adresse | `[A completer]` |
| E-mail vie privée | `[A completer]` |

---

## 2. Données Traitées

| Donnée | Exemple |
| --- | --- |
| Identifiants Dexcom Share | Saisie utilisateur |
| Paramètres app | Région, préférences |
| Glycémie | Valeur affichée ou synchronisée |
| État de sync | Dernier push, ack, statut |
| Infos techniques | Téléphone, montre, connectivité |
| Journaux techniques | Limités au fonctionnement |

---

## 3. Données De Santé

Les valeurs de glycémie peuvent être considérées comme des données de santé.

Elles doivent être protégées avec une attention particulière.

---

## 4. Finalités

| Finalité | Rôle |
| --- | --- |
| Connexion | Accéder à Dexcom Share |
| Affichage | Montrer la glycémie |
| Transmission | Envoyer vers Wear OS |
| Sync active | Maintenir la donnée disponible |
| Diagnostic | Comprendre une erreur technique |
| Fiabilité | Améliorer le fonctionnement local |

---

## 5. Base Légale

La base légale dépend du mode de diffusion et du contexte d'utilisation.

Elle doit être confirmée avant diffusion publique.

---

## 6. Destinataires

| Destinataire | Condition |
| --- | --- |
| Utilisateur | Toujours |
| Application locale | Fonctionnement |
| Dexcom Share | Si configuré |
| Prestataires techniques | `[A completer]` |

---

## 7. Stockage

Peuvent être stockés localement :

- configuration Dexcom ;
- dernier état de sync ;
- dernière valeur connue ;
- préférence de montre principale.

> Les secrets doivent rester sur le téléphone et ne pas être stockés dans le module Wear.

---

## 8. Conservation Et Sécurité

| Sujet | Règle |
| --- | --- |
| Conservation | Temps nécessaire au fonctionnement |
| Suppression | Désinstallation ou action utilisateur |
| Logs | Limiter les données sensibles |
| Secrets | Ne pas exposer les identifiants Dexcom |
| Sécurité | Aucun système n'est absolu |

---

## 9. Droits Et Contact

Selon le droit applicable, l'utilisateur peut disposer de droits sur ses données : accès, rectification, suppression, limitation, opposition ou portabilité.

| Champ | Valeur |
| --- | --- |
| Modalités de contact | `[A completer]` |
| Autorité compétente | `[A completer]` |
