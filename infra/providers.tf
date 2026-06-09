provider "aws" {
  region = var.aws_region

  # Tags appliqués automatiquement à toutes les ressources (traçabilité + nettoyage facile).
  default_tags {
    tags = {
      Project     = var.project
      Environment = var.environment
      ManagedBy   = "terraform"
    }
  }
}
