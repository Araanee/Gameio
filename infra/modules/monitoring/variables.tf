variable "project" {
  type        = string
  description = "Nom du projet (préfixe nommage/tags)."
}

variable "environment" {
  type        = string
  description = "Environnement logique (dev/prod)."
}

variable "vpc_id" {
  type        = string
  description = "ID du VPC."
}

variable "public_subnet_id" {
  type        = string
  description = "Subnet public où placer l'EC2 monitoring."
}

variable "instance_type" {
  type        = string
  description = "Type d'instance EC2."
  default     = "t3.micro"
}

variable "key_name" {
  type        = string
  description = "Key pair SSH (Learner Lab fournit 'vockey')."
  default     = "vockey"
}

variable "instance_profile_name" {
  type        = string
  description = "Instance profile IAM (Learner Lab : LabInstanceProfile, porte LabRole)."
  default     = "LabInstanceProfile"
}

variable "ssh_cidr" {
  type        = string
  description = "CIDR autorisé en SSH. Mets ton IP/32 idéalement ; 0.0.0.0/0 = ouvert (démo)."
  default     = "0.0.0.0/0"
}
