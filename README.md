# 🎲 GameBoard — Déploiement Cloud AWS (DevOps)

Plateforme de notation de jeux de société (Spring Boot + Angular), **déployée sur AWS** via une infrastructure entièrement décrite en code (**Terraform + Ansible**), avec **CI/CD GitHub Actions** (scan de sécurité Trivy + CodeQL) et **mirroring GitLab**.

> L'application (backend/frontend) provient du projet Fullstack S8 (voir [crédits](#crédits)). **Ce dépôt couvre le volet DevOps / Cloud** : conteneurisation, Infrastructure as Code, déploiement AWS, CI/CD, monitoring et sécurité.

📐 Architecture détaillée et justification des choix : **[docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)**

---

## Architecture cloud (vue d'ensemble)

```
                          Internet
        ┌────────────────────┼─────────────────────┐
        │                    │                      │
   S3 (statique)        ALB :80 (public)       EC2 monitoring :3000
   Frontend Angular          │                  Grafana + agent CW
                             │ :8080
                    ECS Fargate (privé)
                     backend Spring Boot
                             │ :5432
                     RDS PostgreSQL (privé)
```

AWS Academy **Learner Lab** · région `us-east-1` · tout en IaC dans [`infra/`](infra/) (Terraform) et [`ansible/`](ansible/).

---

## Stack technique

| Couche | Technologies |
|--------|-------------|
| Backend | Java 21 · Spring Boot 3.5 · Spring Data JPA · Spring Security |
| Frontend | Angular · TypeScript (build statique sur S3) |
| Base de données | **RDS PostgreSQL** (cloud) · PostgreSQL local (dev) · H2 (tests) |
| Messaging | Apache Kafka — **dev local uniquement, retiré du cloud** |
| Conteneurs | Docker (multi-stage) · ECR · ECS Fargate |
| IaC | **Terraform** (VPC, RDS, ECS, ALB, S3, EC2) · **Ansible** (monitoring) |
| CI/CD | GitHub Actions · Trivy · CodeQL · mirroring GitLab |
| Monitoring | CloudWatch (logs + métriques) · Grafana |
| Tests | JUnit 5 · Mockito · Cypress (e2e) |

---

## Déploiement AWS

> Pré-requis : creds AWS Academy actifs, `terraform`, `aws` CLI, `docker`, `ansible`.

### 1. Infrastructure (réseau, base, backend)
```bash
export AWS_DEFAULT_REGION=us-east-1
export TF_VAR_db_password="<mot_de_passe_RDS>"   # jamais commité

cd infra
terraform init
terraform apply
```

### 2. Image backend → ECR
```bash
ECR_URL=$(terraform output -raw ecr_repository_url)
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin "${ECR_URL%/*}"
docker build --platform linux/amd64 -t gameboard-backend ../backend
docker tag gameboard-backend:latest "$ECR_URL:latest"
docker push "$ECR_URL:latest"
aws ecs update-service --cluster gameboard-dev-cluster --service gameboard-dev-backend --force-new-deployment --region us-east-1
```

### 3. Frontend → S3
```bash
# Renseigner l'URL de l'ALB dans frontend/src/environments/environment.ts (apiBase)
#   terraform output -raw alb_dns_name
cd ../frontend && npx ng build --configuration production
BUCKET=$(cd ../infra && terraform output -raw frontend_bucket)
aws s3 sync dist/frontend/browser "s3://$BUCKET" --delete
```

### 4. Monitoring → EC2 (Ansible)
```bash
cd ../ansible
cp inventory.ini.example inventory.ini   # y mettre : terraform output -raw monitoring_public_ip
ansible-playbook -i inventory.ini playbook.yml
```

URLs finales : `terraform output` (`frontend_url`, `alb_dns_name`, `grafana_url`).

---

## Développement local

> Pré-requis : Docker, Java 21, Node.js 20+, Angular CLI.

```bash
# Base PostgreSQL + Kafka en local
docker compose up -d postgres kafka

# Backend (profil local → PostgreSQL local)
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# Frontend (autre terminal)
cd frontend && npm install && ng serve
```

| Service | URL locale |
|---------|-----|
| Frontend | http://localhost:4200 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Actuator Health | http://localhost:8080/actuator/health |

### Profils Spring
- **`local`** — PostgreSQL local (défauts du `docker-compose`), surchargeable par variables d'env.
- **`aws`** — cloud : datasource injectée par ECS (`SPRING_DATASOURCE_*`), **Kafka désactivé**.
- **`test`** — H2 en mémoire, sans Docker (utilisé par la CI).

> ⚠️ Aucun secret n'est versionné : les identifiants de base sont fournis par variables d'environnement (env ECS en cloud, défauts non sensibles en local).

---

## CI/CD & Sécurité

Pipeline GitHub Actions (push/PR sur `main`), mirrorée automatiquement vers GitLab :

- **[`ci.yml`](.github/workflows/ci.yml)** — 3 jobs : `backend` (build + tests H2 + scan image Trivy), `frontend` (build + scan image Trivy), `trivy-repo` (scan filesystem : deps + secrets + IaC). Scans Trivy en *report-only* → rapports SARIF dans l'onglet Security.
- **[`codeql.yml`](.github/workflows/codeql.yml)** — SAST (Java + JS/TS).
- **[`mirror-gitlab.yml`](.github/workflows/mirror-gitlab.yml)** — push vers le dépôt GitLab de rendu.

Mesures de sécurité : creds externalisés (jamais en dur), Security Groups *least-privilege* (ALB → ECS → RDS), RDS et ECS en subnets privés, chiffrement RDS au repos, scan de secrets en CI.

---

## Application

### Rôles & comptes (créés au démarrage si la base est vide)
| Identifiant | Mot de passe | Rôle | Accès |
|-------------|-------------|------|-------|
| `user` | `user` | Utilisateur | Consulter, noter les jeux |
| `editor` | `editor` | Éditeur | + Proposer des jeux |
| `admin` | `admin` | Webmaster | + Administrer tout |

### Fonctionnalités par profil
- **Utilisateur** : catalogue paginé (recherche titre/genre/année/joueurs), tri, fiche détaillée, notation (1–5 ⭐ + commentaire).
- **Éditeur** : proposer un jeu (enrichissement BGG), suivre/modifier ses propositions.
- **Webmaster** : valider/refuser les jeux, gérer le catalogue et les comptes.

Guide détaillé : [docs/USER_GUIDE.md](docs/USER_GUIDE.md).

### Principaux endpoints API
| Méthode | Endpoint | Accès |
|---------|----------|-------|
| GET | `/api/games`, `/api/games/{id}` | Public |
| POST / PUT / DELETE | `/api/games/**` | EDITOR+ |
| PATCH | `/api/games/{id}/status` | WEBMASTER |
| POST | `/api/games/{id}/ratings` | Authentifié |
| `**` | `/api/users/**` | WEBMASTER |
| GET | `/actuator/health` | Public |

### Modèle de données
`BoardGame (1) ──< Rating (N)` — un jeu a plusieurs notes ; une note appartient à un jeu.
Statuts d'un jeu : `PENDING` → `APPROVED` / `REJECTED`.

---

## Tests
```bash
cd backend && ./mvnw test            # JUnit + Mockito, profil test (H2)
cd frontend && npm run e2e:ci        # Cypress headless
```

---

## Structure du dépôt
```
.
├── backend/                Spring Boot (profils local / aws / test)
├── frontend/               Angular (environments dev / prod)
├── infra/                  Terraform
│   └── modules/            vpc · rds · ecs · s3-frontend · monitoring
├── ansible/                provisionnement EC2 monitoring (Grafana + CloudWatch)
│   └── roles/              cloudwatch_agent · grafana
├── docker-compose.yml      stack locale (postgres + kafka + backend)
├── docs/                   ARCHITECTURE.md · USER_GUIDE.md
└── .github/workflows/      ci.yml · codeql.yml · mirror-gitlab.yml
```

---

## Crédits

Application Fullstack S8 — EPITA APC : Amina SERRANO, Christina LOPES, Hajar KHETTOU, Lyna MEDJEDOUB, Rawane OUFFA. Encadrant : David THIBAU.
**Volet DevOps / Cloud (ce dépôt) : Rawane OUFFA.**
