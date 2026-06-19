variable "project" {
  type        = string
  description = "Nom du projet (préfixe nommage/tags)."
}

variable "environment" {
  type        = string
  description = "Environnement logique (dev/prod)."
}

variable "aws_region" {
  type        = string
  description = "Région AWS (pour la config des logs CloudWatch)."
}

variable "vpc_id" {
  type        = string
  description = "ID du VPC."
}

variable "public_subnet_ids" {
  type        = list(string)
  description = "Subnets publics (pour l'ALB)."
}

variable "private_subnet_ids" {
  type        = list(string)
  description = "Subnets privés (pour les tâches Fargate)."
}

variable "db_address" {
  type        = string
  description = "Hostname RDS."
}

variable "db_name" {
  type        = string
  description = "Nom de la base."
}

variable "db_username" {
  type        = string
  description = "Utilisateur de la base."
}

variable "db_password" {
  type        = string
  description = "Mot de passe de la base."
  sensitive   = true
}

variable "image_tag" {
  type        = string
  description = "Tag de l'image backend dans ECR."
  default     = "latest"
}

variable "cors_allowed_origins" {
  type        = string
  description = "Origines CORS autorisées par le backend (ex. URL du site S3)."
  default     = "http://localhost:4200"
}

variable "desired_count" {
  type        = number
  description = "Nombre de tâches backend au démarrage (baseline)."
  default     = 2
}

variable "min_capacity" {
  type        = number
  description = "Nombre minimum de tâches (autoscaling)."
  default     = 2
}

variable "max_capacity" {
  type        = number
  description = "Nombre maximum de tâches (autoscaling)."
  default     = 4
}

variable "cpu_target_value" {
  type        = number
  description = "Cible d'utilisation CPU moyenne (%) pour l'autoscaling."
  default     = 60
}

variable "container_port" {
  type        = number
  description = "Port exposé par le backend."
  default     = 8080
}

variable "cpu" {
  type        = number
  description = "CPU Fargate (unités, 512 = 0.5 vCPU)."
  default     = 512
}

variable "memory" {
  type        = number
  description = "Mémoire Fargate (Mo)."
  default     = 1024
}
