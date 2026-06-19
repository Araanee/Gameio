# Architecture Cloud — GameBoard

Document technique du déploiement AWS : architecture, choix de conception et contraintes.

---

## 1. Contexte & contraintes

Déploiement sur **AWS Academy Learner Lab**, qui impose des limites structurantes :

| Contrainte | Conséquence sur l'architecture |
|---|---|
| Région imposée `us-east-1` | Variable `aws_region` verrouillée |
| Session de 4 h, credentials rotatifs | Creds réexportés à chaque session ; state Terraform local |
| Création de rôles IAM custom bloquée | On réutilise **`LabRole`** (et `LabInstanceProfile`) comme rôle d'exécution/tâche/instance |
| Budget crédits limité | NAT unique partagé, instances `*.micro`, RDS single-AZ, pas de CloudFront |

> **Note IAM** : faute de pouvoir créer des rôles, on attache `LabRole` aux ressources. Les intentions *least-privilege* sont documentées (Security Groups stricts, accès réseau cloisonné) même si le rôle IAM lui-même reste large.

---

## 2. Vue d'ensemble

```
                                Internet
       ┌───────────────────────────┼────────────────────────────┐
       │                           │                             │
┌──────▼───────┐         ┌─────────▼──────────┐        ┌─────────▼─────────┐
│ S3 (statique)│         │   ALB :80 (public) │        │ EC2 monitoring    │
│  Frontend    │         │   subnets publics  │        │ :3000 Grafana     │
│  Angular     │         └─────────┬──────────┘        │ + agent CloudWatch│
└──────────────┘                   │ :8080             └─────────┬─────────┘
   appels API ───────────────────► │                             │ métriques
   (CORS)                ┌─────────▼──────────┐                   │
                         │  ECS Fargate       │           ┌───────▼────────┐
                         │  backend (privé)   │──────────►│   CloudWatch   │
                         │  pull image ◄─ NAT │  logs      └────────────────┘
                         └─────────┬──────────┘
                                   │ :5432
                         ┌─────────▼──────────┐
                         │ RDS PostgreSQL     │
                         │ (privé, single-AZ) │
                         └────────────────────┘
```

---

## 3. Réseau (module `vpc`)

- **VPC `10.0.0.0/16`**, DNS activé.
- **2 AZ** (résolues dynamiquement via `aws_availability_zones`) pour la disponibilité.
- **2 subnets publics** (`10.0.0.0/24`, `10.0.1.0/24`) → ALB, EC2 monitoring, NAT.
- **2 subnets privés** (`10.0.10.0/24`, `10.0.11.0/24`) → ECS, RDS.
- **Internet Gateway** : sortie/entrée des subnets publics.
- **NAT Gateway unique** (dans un subnet public) : permet aux subnets privés de **sortir** (pull image ECR, API BGG) sans être joignables de l'extérieur.

**Choix** : un seul NAT (au lieu d'un par AZ) → compromis coût/HA assumé pour un environnement de démo.

---

## 4. Base de données (module `rds`)

- **PostgreSQL 15**, `db.t3.micro`, **single-AZ**, **stockage chiffré**, `publicly_accessible = false`.
- Placée dans un **DB subnet group** sur les 2 subnets privés → injoignable depuis Internet.
- Security Group : entrée `5432` depuis le VPC (à resserrer vers le SG ECS pour un least-privilege strict — documenté en TODO).
- Mot de passe fourni via `TF_VAR_db_password`, **jamais versionné**.

---

## 5. Backend conteneurisé (module `ecs`)

- **ECR** : registre privé de l'image backend (scan de vulnérabilités on-push).
- **ECS Fargate** : cluster + task definition + service dans les **subnets privés**. **2 tâches réparties sur les 2 AZ** (résilience) avec **autoscaling** (target tracking CPU 60 %, 2→4 tâches) → scalabilité automatique sous charge.
- **ALB** (public) → **target group :8080** → tâches Fargate. Health check sur `/actuator/health`.
- Rôles d'exécution/tâche = **`LabRole`** (contrainte Learner Lab).
- Logs conteneur → **CloudWatch** (`/ecs/gameboard-dev-backend`).
- Variables d'env de la tâche : profil `aws`, `SPRING_DATASOURCE_*` (→ RDS), `APP_CORS_ALLOWED_ORIGINS` (→ URL S3).

### Chaîne de Security Groups (least-privilege)
```
Internet ──:80──► [SG ALB] ──:8080──► [SG ECS] ──:5432──► [SG RDS]
            (0.0.0.0/0)      (depuis SG ALB        (depuis le VPC)
                              uniquement)
```
Chaque couche n'accepte que le trafic de la couche précédente.

### Kafka retiré du cloud
L'app utilise Kafka en local (events `game-imported`). En cloud, le profil `aws` désactive Kafka (`spring.kafka.enabled=false` + exclusion de l'auto-config). Le code le supporte nativement : les beans `GameEventProducer`/`Consumer` sont `@ConditionalOnProperty` et `BoardGameService` injecte un `Optional<>`. **Aucune dégradation fonctionnelle** hors messaging.

---

## 6. Frontend statique (module `s3-frontend`)

- **Bucket S3** en *static website hosting* (lecture publique), fallback SPA `error_document → index.html`.
- L'app Angular est buildée en prod avec `apiBase` = DNS de l'ALB (fichiers `environment`).
- **CORS** : le backend autorise l'origine du site S3 via `APP_CORS_ALLOWED_ORIGINS` (injectée par Terraform dans la tâche ECS) → pas de recompilation du backend si l'origine change.

**Choix** : S3 + ALB en **HTTP** (pas de TLS). Le HTTPS propre nécessiterait **CloudFront + certificat ACM** — noté comme évolution, hors périmètre (contrainte temps/coût).

---

## 7. Monitoring (module `monitoring` + Ansible)

- **EC2** (Amazon Linux 2023, `t3.micro`) en subnet public, key `vockey`, instance profile `LabInstanceProfile`.
- **Séparation des responsabilités** :
  - *Terraform* crée la machine, le réseau, le SG, le rôle.
  - *Ansible* configure ce qui tourne dessus (idempotent) : **agent CloudWatch** (métriques système) + **Grafana** (datasource CloudWatch via le rôle EC2).
- `pre_task` Ansible : 2 Go de **swap** (sinon `dnf` est tué par l'OOM killer sur 1 Go de RAM).

---

## 8. CI/CD & sécurité

- **GitHub Actions** : build/tests (back+front), **Trivy** ×3 (image back, image front, filesystem deps+secrets+IaC) en *report-only* (SARIF), **CodeQL** (SAST Java + JS/TS).
- **Mirroring GitLab** : push automatique vers le dépôt de rendu (token Maintainer + `write_repository`).
- **Gestion des secrets** : aucun secret versionné ; un incident initial (mot de passe Postgres commité) a été traité par rotation + réécriture de l'historique git + scan de secrets en CI.

---

## 9. Récapitulatif des décisions

| Décision | Raison |
|---|---|
| `LabRole` partout | IAM custom interdit en Learner Lab |
| NAT Gateway unique | Coût (vs 1 NAT/AZ) |
| ECS : 2 tâches + autoscaling CPU | Résilience (2 AZ) + scalabilité automatique |
| RDS single-AZ | Coût + périmètre démo (évolution possible : multi-AZ) |
| Kafka retiré du cloud | Pas de valeur en prod démo, simplifie l'infra |
| Frontend S3 + HTTP | Architecture imposée ; HTTPS = bonus CloudFront |
| Trivy en report-only | Ne pas bloquer le rendu ; visibilité via SARIF |
| State Terraform local | Creds Learner Lab rotatifs (backend S3 fragile) |
| CORS via variable d'env | Découpler l'origine front du build backend |
