variable "project" {
  description = "Nom du projet (préfixe de nommage/tags)."
  type        = string
}

variable "environment" {
  description = "Environnement logique (dev/prod)."
  type        = string
}

variable "vpc_cidr" {
  description = "Plage d'adresses du VPC (/16 = 65 536 IPs)."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "CIDR des subnets publics (1 par AZ). Doit avoir la même longueur que private_subnet_cidrs."
  type        = list(string)
  default     = ["10.0.0.0/24", "10.0.1.0/24"]
}

variable "private_subnet_cidrs" {
  description = "CIDR des subnets privés (1 par AZ)."
  type        = list(string)
  default     = ["10.0.10.0/24", "10.0.11.0/24"]
}
