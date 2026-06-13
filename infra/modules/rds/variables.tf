variable "project" {
  description = "Nom du projet (préfixe de nommage/tags)."
  type        = string
}

variable "environment" {
  description = "Environnement logique (dev/prod)."
  type        = string
}

variable "vpc_id" {
  description = "ID du VPC où vit la base."
  type        = string
}

variable "private_subnet_ids" {
  description = "Subnets privés pour le DB subnet group."
  type        = list(string)
}

variable "allowed_cidr_blocks" {
  description = "CIDR autorisés à joindre le port 5432 (par défaut : le VPC entier). À resserrer vers le SG ECS plus tard."
  type        = list(string)
}

variable "db_name" {
  description = "Nom de la base à créer."
  type        = string
  default     = "gameboard"
}

variable "db_username" {
  description = "Utilisateur maître de la base."
  type        = string
  default     = "gameboard"
}

variable "db_password" {
  description = "Mot de passe maître. Fourni via TF_VAR_db_password, JAMAIS commité."
  type        = string
  sensitive   = true
}

variable "instance_class" {
  description = "Classe d'instance RDS."
  type        = string
  default     = "db.t3.micro"
}

variable "allocated_storage" {
  description = "Stockage alloué en Go."
  type        = number
  default     = 20
}

variable "engine_version" {
  description = "Version majeure de PostgreSQL."
  type        = string
  default     = "15"
}
