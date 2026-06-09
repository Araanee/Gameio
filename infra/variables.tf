variable "aws_region" {
  description = "Région AWS (imposée par le Learner Lab)."
  type        = string
  default     = "us-east-1"
}

variable "project" {
  description = "Nom du projet, utilisé pour nommer/taguer les ressources."
  type        = string
  default     = "gameboard"
}

variable "environment" {
  description = "Environnement logique (dev/prod). Un seul pour ce projet."
  type        = string
  default     = "dev"
}
