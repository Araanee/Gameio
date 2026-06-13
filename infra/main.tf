# Point d'entrée Terraform : appelle les modules d'infra.
# J2 = réseau (VPC). Les modules ecs/rds/frontend viendront aux paliers suivants.

module "vpc" {
  source = "./modules/vpc"

  project     = var.project
  environment = var.environment
  # Les CIDR utilisent les défauts du module (10.0.0.0/16, /24 par subnet).
}

module "rds" {
  source = "./modules/rds"

  project            = var.project
  environment        = var.environment
  vpc_id             = module.vpc.vpc_id
  private_subnet_ids = module.vpc.private_subnet_ids
  # Pour l'instant : tout le VPC peut joindre la base. À resserrer vers le SG ECS.
  allowed_cidr_blocks = [module.vpc.vpc_cidr_block]
  db_password         = var.db_password
}
