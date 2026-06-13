locals {
  name = "${var.project}-${var.environment}"
}

# ─── DB Subnet Group : RDS doit vivre dans ces subnets privés ──
# (au moins 2 subnets sur 2 AZ, même si l'instance est single-AZ)
resource "aws_db_subnet_group" "this" {
  name       = "${local.name}-db-subnet-group"
  subnet_ids = var.private_subnet_ids
  tags       = { Name = "${local.name}-db-subnet-group" }
}

# ─── Security Group : pare-feu de la base ──────────────────────
# Entrée 5432 depuis le VPC uniquement (la base n'est pas publique).
# TODO (palier ECS) : remplacer cidr_blocks par security_groups = [sg_ecs]
# pour n'autoriser QUE les tâches Fargate (least-privilege).
resource "aws_security_group" "rds" {
  name        = "${local.name}-rds-sg"
  description = "Acces PostgreSQL depuis le VPC"
  vpc_id      = var.vpc_id

  ingress {
    description = "PostgreSQL depuis le VPC"
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = var.allowed_cidr_blocks
  }

  egress {
    description = "Sortie autorisee"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name}-rds-sg" }
}

# ─── Instance RDS PostgreSQL ───────────────────────────────────
resource "aws_db_instance" "this" {
  identifier     = "${local.name}-postgres"
  engine         = "postgres"
  engine_version = var.engine_version
  instance_class = var.instance_class

  allocated_storage = var.allocated_storage
  storage_type      = "gp3"
  storage_encrypted = true # chiffrement au repos (KMS par défaut)

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  db_subnet_group_name   = aws_db_subnet_group.this.name
  vpc_security_group_ids = [aws_security_group.rds.id]

  multi_az            = false # single-AZ (archi imposée + coût)
  publicly_accessible = false # jamais exposée à Internet

  # Réglages dev/Learner Lab : pas de backups, suppression facile
  backup_retention_period = 0
  skip_final_snapshot     = true
  deletion_protection     = false
  apply_immediately       = true

  tags = { Name = "${local.name}-postgres" }
}
