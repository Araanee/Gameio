# Point d'entrée Terraform : appelle les modules d'infra.
# J2 = réseau (VPC). Les modules ecs/rds/frontend viendront aux paliers suivants.

module "vpc" {
  source = "./modules/vpc"

  project     = var.project
  environment = var.environment
  # Les CIDR utilisent les défauts du module (10.0.0.0/16, /24 par subnet).
}
