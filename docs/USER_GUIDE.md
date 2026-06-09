# 📖 Guide Utilisateur — GameYo

Pour le lancement et la configuration du projet, consulter le [README principal](../README.md).

---

## Sommaire

- [👤 Utilisateur public](#-utilisateur-public)
- [✏️ Éditeur](#️-éditeur)
- [🔧 Webmaster](#-webmaster)
- [🔐 Authentification](#-authentification)

---

## 👤 Utilisateur public

> Aucune connexion requise pour consulter le catalogue.

### Consulter le catalogue

1. Ouvrir l'application sur **http://localhost:4200**
2. La page d'accueil affiche la liste de tous les jeux approuvés
3. Chaque carte jeu affiche : l'image, le titre, l'année, le nombre de joueurs et la note moyenne

### Rechercher un jeu

1. Saisir un mot-clé dans la barre de recherche en haut de la page
2. Appuyer sur **Entrée** ou cliquer sur **Rechercher**
3. Les résultats se filtrent automatiquement par titre

### Voir la fiche détaillée d'un jeu

1. Cliquer sur **Voir la fiche** sur n'importe quelle carte jeu
2. La fiche affiche : image, description complète, année, nombre de joueurs, note moyenne
3. Les avis de la communauté sont visibles en bas de page

### Noter un jeu

> ⚠️ La connexion est requise pour noter un jeu.

1. Aller sur la fiche détaillée d'un jeu
2. Se connecter si ce n'est pas déjà fait (voir [Authentification](#-authentification))
3. Dans la section **Noter ce jeu**, choisir une note de 1 à 5 ⭐
4. Ajouter un commentaire (optionnel)
5. Cliquer sur **Envoyer ma note**
6. Un message de confirmation apparaît

---

## ✏️ Éditeur

> L'éditeur doit être connecté avec un compte de rôle `editor`.

**Identifiants par défaut :**
| Identifiant | Mot de passe |
|-------------|-------------|
| `editor` | `editor` |

### Proposer un nouveau jeu

1. Se connecter avec le compte éditeur
2. Cliquer sur **+ Proposer un jeu** depuis la page d'accueil
3. Remplir le formulaire :
   - **Titre** — le titre du jeu
   - **Description** — une présentation du jeu
   - **URL de l'image** — lien vers une image de couverture
   - **Joueurs min / max** — nombre de joueurs
   - **Année** — année de publication
4. Cliquer sur **Envoyer la proposition**
5. Le jeu passe en statut **En attente** (PENDING) jusqu'à validation par un Webmaster

### Suivre le statut de ses propositions

1. Aller dans **Mes propositions** (menu utilisateur)
2. Chaque proposition affiche son statut :
   - 🕐 **En attente** — en cours d'examen par un modérateur
   - ✅ **Approuvé** — le jeu est visible dans le catalogue
   - ❌ **Refusé** — la proposition a été rejetée

### Modifier ou supprimer une proposition en attente

1. Aller dans **Mes propositions**
2. Cliquer sur **Modifier** ou **Supprimer** sur une proposition en statut **En attente**
3. Les propositions déjà approuvées ne peuvent plus être modifiées par l'éditeur

---

## 🔧 Webmaster

> Le Webmaster a accès à toutes les fonctionnalités d'administration.

**Identifiants par défaut :**
| Identifiant | Mot de passe |
|-------------|-------------|
| `admin` | `admin` |

### Valider ou refuser un jeu proposé

1. Se connecter avec le compte admin
2. Aller dans **Administration → Jeux en attente**
3. La liste affiche tous les jeux avec le statut PENDING
4. Pour chaque jeu, cliquer sur :
   - ✅ **Valider** — le jeu devient visible dans le catalogue public
   - ❌ **Refuser** — la proposition est rejetée et l'éditeur en est informé

### Modifier un jeu existant

1. Aller sur la fiche d'un jeu ou dans **Administration → Catalogue**
2. Cliquer sur **Modifier**
3. Mettre à jour les champs souhaités
4. Cliquer sur **Enregistrer**

### Supprimer un jeu

1. Aller sur la fiche du jeu ou dans **Administration → Catalogue**
2. Cliquer sur **Supprimer**
3. Confirmer la suppression

> ⚠️ La suppression est irréversible.


### Gérer les utilisateurs

1. Aller dans **Administration → Utilisateurs**
2. Actions disponibles :
   - **Créer** un nouveau compte (utilisateur, éditeur ou admin)
   - **Modifier** le rôle ou les informations d'un compte
   - **Supprimer** un compte

---

## 🔐 Authentification

### Se connecter

1. Cliquer sur **Se connecter** en haut à droite
2. Saisir votre identifiant et mot de passe
3. Cliquer sur **Connexion**

**Comptes disponibles par défaut :**

| Identifiant | Mot de passe | Rôle | Accès |
|-------------|-------------|------|-------|
| `user` | `user` | Utilisateur | Consulter, noter les jeux |
| `editor` | `editor` | Éditeur | + Proposer des jeux |
| `admin` | `admin` | Webmaster | + Administrer tout |

> Ces comptes sont créés automatiquement au premier démarrage si la base est vide.

### Se déconnecter

1. Cliquer sur votre nom en haut à droite
2. Cliquer sur **Se déconnecter**

---

*GameYo — Projet Fullstack S8 | EPITA APC — 2026*
