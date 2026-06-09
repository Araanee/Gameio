# infra/ — Infrastructure as Code (Terraform)

Provisionnement AWS du projet GameBoard, contraint par **AWS Academy Learner Lab**.

## Contraintes Learner Lab (à garder en tête)
- **Région imposée** : `us-east-1` (variable `aws_region`, ne pas changer sans raison).
- **Session 4h** : les credentials expirent → relancer le lab et réexporter les creds avant chaque session.
- **Pas d'IAM custom** : impossible de créer rôles/users. On **utilise `LabRole`** comme rôle d'exécution.
  → On écrit quand même les policies *least-privilege* en Terraform (data sources / documents)
    pour la **traçabilité et la note**, même si on les attache à `LabRole`.

## Cible (paliers J2+)
```
VPC (2 AZ)
 ├── 2 subnets publics  → ALB
 └── 2 subnets privés   → ECS Fargate (backend) + RDS PostgreSQL (single-AZ)
Frontend → S3 statique (build Angular)
```
Kafka : **retiré du cloud** (restait en local docker-compose uniquement).

## Layout prévu
```
infra/
├── versions.tf      # versions Terraform + provider AWS (pinned)
├── providers.tf     # provider aws + default_tags
├── variables.tf     # aws_region, project, environment, ...
├── main.tf          # (J2) appels des modules
├── outputs.tf       # (J2) sorties (alb_dns, rds_endpoint, ...)
└── modules/
    ├── vpc/         # J2 : VPC + 4 subnets + IGW + routes
    ├── ecs/         # backend Fargate + ALB
    ├── rds/         # PostgreSQL single-AZ
    └── frontend/    # bucket S3 statique
```

## État Terraform (state)
Démarrage en **state local** (`terraform.tfstate`, git-ignoré). Migration possible vers
un backend S3 plus tard si le temps le permet — attention : les creds Learner Lab tournent,
donc le backend S3 doit utiliser un profil/creds valides au moment du `apply`.

## Workflow
```bash
# Réexporter les creds Learner Lab dans l'environnement, puis :
cd infra
terraform init
terraform plan
terraform apply
```
