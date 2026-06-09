# 🎲 GameBoard — Plateforme de notation de jeux de société

Projet Fullstack S8 — EPITA APC

**GitHub** : https://github.com/hajar-khettou/Fullstack-project

---

## Équipe

- Amina SERRANO
- Christina LOPES
- Hajar KHETTOU
- Lyna MEDJEDOUB
- Rawane OUFFA

**Encadrant** : David THIBAU — david.thibau@gmail.com

---

## Stack technique

| Couche | Technologies |
|--------|-------------|
| Backend | Java 21 · Spring Boot 3.5 · Spring Data JPA · Spring Security |
| Messaging | Apache Kafka · Topic `game-imported` |
| Base de données | Neon PostgreSQL (cloud partagé) · H2 (tests) |
| API externe | BoardGameGeek XML API v2 |
| Frontend | Angular 21 · TypeScript |
| Tests | JUnit 5 · Mockito · Cypress (e2e) |
| DevOps | Docker · docker-compose · GitHub Actions (CI/CD) |
| Documentation | Swagger / OpenAPI 3 |

---

## Base de données partagée (Neon)

La base est hébergée sur **[Neon](https://neon.tech)** (cloud PostgreSQL). Elle est **partagée entre tous les membres** — pas besoin d'installer PostgreSQL localement.

- Connexion configurée dans `application-local.properties`
- Au premier démarrage, 15 jeux et 3 comptes sont insérés automatiquement
- Tout ajout approuvé est visible par tous en temps réel

---

## Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) — pour Kafka uniquement
- [Node.js 20+](https://nodejs.org/) + Angular CLI (`npm install -g @angular/cli`)
- Java 21

---

## Lancement from scratch

### Option 1 — Backend local + Kafka Docker (recommandé)

```bash
git clone https://github.com/hajar-khettou/Fullstack-project.git
cd Fullstack-project

# Démarrer Kafka (base sur Neon, pas besoin de postgres local)
docker-compose up -d zookeeper kafka

# Lancer le backend (profil local → Neon)
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Lancer le frontend (dans un autre terminal)
cd frontend
npm install
ng serve
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Actuator Health | http://localhost:8080/actuator/health |

---

### Option 2 — Stack complète Docker

```bash
docker-compose up --build -d
cd frontend && npm install && ng serve
```

---

## Comptes disponibles

| Identifiant | Mot de passe | Rôle | Accès |
|-------------|-------------|------|-------|
| `user` | `user` | Utilisateur | Consulter, noter les jeux |
| `editor` | `editor` | Éditeur | + Proposer des jeux |
| `admin` | `admin` | Webmaster | + Administrer tout |

> Créés automatiquement au démarrage si la base est vide.

---

## Documentation utilisateur
(Pour le guide d'utilisation détaillé par profil, 
consulter [docs/USER_GUIDE.md](docs/USER_GUIDE.md))

### Utilisateur
- Catalogue paginé avec recherche (titre, genre, année, nombre de joueurs)
- Tri par note, année ou titre
- Fiche détaillée avec statistiques de notation par étoile
- Noter un jeu (1–5 ⭐ + commentaire), modifier ou supprimer sa note

### Éditeur
- Proposer un nouveau jeu (enrichissement automatique via BGG)
- Suivre le statut de ses propositions (En attente / Approuvé / Refusé)
- Modifier ou supprimer ses propositions en attente

### Webmaster
- Valider ou refuser les jeux proposés
- Modifier ou supprimer les jeux du catalogue
- Importer un jeu depuis BoardGameGeek via son ID
- Gérer les comptes utilisateurs (créer / modifier / supprimer)

---

## Architecture

```
┌─────────────────────────────────────┐
│         FRONTEND (Angular 21)        │
│  Catalogue · Fiche · Proposer · Admin │
└──────────────┬──────────────────────┘
               │ HTTP REST + Basic Auth
┌──────────────▼──────────────────────┐
│        BACKEND (Spring Boot 3.5)     │
│  Controller → Service → Repository  │
│  BGG Client + Kafka Producer         │
└──────┬───────────────────┬──────────┘
       │ JPA               │ Topic: game-imported
┌──────▼──────┐    ┌───────▼──────────┐
│   Neon DB   │    │  Kafka Cluster   │
│ (PostgreSQL)│    │    (Docker)      │
└─────────────┘    └──────────────────┘
```

### Kafka

À chaque création ou import de jeu, un événement est publié sur `game-imported` :

```json
{
  "gameId": 1,
  "title": "Catan",
  "source": "editor",
  "importedAt": "2026-04-21T12:00:00Z"
}
```

---

## Modèle de données

### BoardGame
| Champ | Type | Description |
|-------|------|-------------|
| `id` | Long (PK) | Identifiant auto-généré |
| `title` | String | Titre du jeu — obligatoire (`@NotBlank`) |
| `description` | Text | Description longue (source BGG) |
| `imageUrl` | String | URL image de couverture (source BGG) |
| `minPlayers` | Integer | Nombre minimum de joueurs |
| `maxPlayers` | Integer | Nombre maximum de joueurs |
| `year` | Integer | Année de publication |
| `averageRating` | Double | Moyenne des notes, calculée dynamiquement |
| `status` | Enum | `PENDING` (défaut) / `APPROVED` / `REJECTED` |

### Rating
| Champ | Type | Description |
|-------|------|-------------|
| `id` | Long (PK) | Identifiant auto-généré |
| `boardGameId` | Long (FK) | Référence vers `BoardGame` |
| `userId` | String | Identifiant utilisateur (nullable si anonyme) |
| `score` | Integer | Note de 1 à 5 |
| `comment` | String | Commentaire libre (optionnel) |
| `createdAt` | LocalDateTime | Date/heure automatique à la création |

### Relation
BoardGame (1) ──────< Rating (N)
Un jeu peut avoir plusieurs notes.
Une note appartient à un seul jeu.

---

## Choix techniques

### Intégration BoardGameGeek
L'intégration BGG permet l'auto-complétion des fiches jeux (titre, description, image, joueurs, année). 

> ⚠️ **Note** : Depuis avril 2026, BGG a restreint l'accès public à son API XML et requiert désormais une authentification. Donc cette option est désactivée.

### Pourquoi Spring Security Basic Auth ?
Auth0 aurait été plus complet mais plus complexe à mettre en place dans le temps imparti. Basic Auth avec Spring Security permet de gérer les 3 rôles (`user`, `editor`, `admin`) simplement, avec une sécurisation des endpoints par annotation (`@PreAuthorize`).

### Pourquoi Neon (PostgreSQL cloud) ?
Neon permet à toute l'équipe de partager la même base de données sans installer PostgreSQL localement. Seul Kafka reste en Docker. Cela simplifie l'onboarding d'un nouveau développeur.

### Pourquoi H2 pour les tests ?
H2 est une base de données en mémoire — elle démarre et s'arrête avec les tests, sans Docker, sans configuration. Cela permet de lancer `./mvnw test` n'importe où, y compris dans la CI GitHub Actions.

### Flux d'une requête (exemple : liste des jeux)
```
Navigateur Angular
│  GET http://localhost:8080/api/games
▼
BoardGameController.java      → reçoit la requête HTTP
│  appelle
▼
BoardGameService.java         → applique la logique métier
│  appelle                  (filtre status = APPROVED)
▼
BoardGameRepository.java      → requête SQL via JPA
│
▼
Neon PostgreSQL               → retourne les lignes
│
▼
Spring                        → convertit en JSON automatiquement
│
▼
Navigateur Angular            → affiche les cartes jeux
```

### Profils Spring
Le projet utilise deux profils :
- **`local`** — connecté à Neon (base partagée). Lancer avec `-Dspring-boot.run.profiles=local`
- **`test`** — connecté à H2 en mémoire. Activé automatiquement par `./mvnw test`

---

## API — Documentation Swagger

Disponible sur **http://localhost:8080/swagger-ui/index.html**

| Méthode | Endpoint | Accès |
|---------|----------|-------|
| GET | `/api/games` | Public |
| GET | `/api/games/{id}` | Public |
| POST | `/api/games` | EDITOR+ |
| PUT | `/api/games/{id}` | EDITOR+ |
| DELETE | `/api/games/{id}` | EDITOR+ |
| PATCH | `/api/games/{id}/status` | WEBMASTER |
| GET | `/api/games/pending` | WEBMASTER |
| POST | `/api/games/bgg/{bggId}` | WEBMASTER |
| GET | `/api/games/my-proposals` | EDITOR+ |
| POST | `/api/games/{id}/ratings` | Authentifié |
| PUT | `/api/ratings/{id}` | Propriétaire |
| DELETE | `/api/ratings/{id}` | Propriétaire |
| GET | `/api/users` | WEBMASTER |
| POST | `/api/users` | WEBMASTER |
| PUT | `/api/users/{id}` | WEBMASTER |
| DELETE | `/api/users/{id}` | WEBMASTER |
| GET | `/api/auth/me` | Authentifié |
| GET | `/actuator/health` | Public |

---

## Tests

### Backend (JUnit + Mockito)

```bash
cd backend
./mvnw test
```

22 tests — profil `test` (H2 en mémoire, sans Docker, sans Kafka).

### Frontend e2e (Cypress)

```bash
cd frontend
npm run e2e       # interface graphique (backend doit tourner)
npm run e2e:ci    # mode headless
```

16 tests e2e couvrant : liste des jeux, fiche détaillée, authentification, navigation.

---

## CI/CD — GitHub Actions

Pipeline déclenchée à chaque push sur `main` — deux jobs en parallèle :

**Backend**
1. Java 21 + Maven
2. `./mvnw clean verify` (tests + build)
3. Build image Docker

**Frontend**
1. Node 22 + `npm ci`
2. `ng build`
3. Tests unitaires headless

Voir [.github/workflows/ci.yml](.github/workflows/ci.yml)

---

## Structure du projet

```
Fullstack-project/
├── backend/
│   ├── src/main/java/com/gameboard/
│   │   ├── game/          → CRUD jeux + import BGG
│   │   ├── rating/        → Notes et avis
│   │   ├── user/          → Gestion utilisateurs
│   │   ├── kafka/         → Producer + Consumer
│   │   ├── bgg/           → Client BoardGameGeek
│   │   ├── security/      → Spring Security (Basic Auth)
│   │   └── config/        → Swagger, DataInitializer
│   └── src/test/          → Tests unitaires (22 tests)
├── frontend/
│   └── src/app/
│       ├── pages/         → game-list, game-detail, admin, login...
│       ├── core/          → Services, guards, interceptors
│       └── cypress/e2e/   → Tests e2e (16 tests)
├── docker-compose.yml     → Kafka + Backend
└── .github/workflows/     → CI/CD GitHub Actions
```
