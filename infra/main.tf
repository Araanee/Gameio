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
  db_name             = var.db_name
  db_username         = var.db_username
  db_password         = var.db_password
}

module "s3_frontend" {
  source = "./modules/s3-frontend"

  project     = var.project
  environment = var.environment
}

module "ecs" {
  source = "./modules/ecs"

  project            = var.project
  environment        = var.environment
  aws_region         = var.aws_region
  vpc_id             = module.vpc.vpc_id
  public_subnet_ids  = module.vpc.public_subnet_ids
  private_subnet_ids = module.vpc.private_subnet_ids

  db_address  = module.rds.address
  db_name     = var.db_name
  db_username = var.db_username
  db_password = var.db_password

  # Autorise le site S3 à appeler l'API (CORS)
  cors_allowed_origins = module.s3_frontend.website_url
}

module "monitoring" {
  source = "./modules/monitoring"

  project          = var.project
  environment      = var.environment
  vpc_id           = module.vpc.vpc_id
  public_subnet_id = module.vpc.public_subnet_ids[0]
}
