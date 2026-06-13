# Sorties remontées au niveau racine (utiles pour les paliers suivants et le debug).

output "vpc_id" {
  description = "ID du VPC créé."
  value       = module.vpc.vpc_id
}

output "public_subnet_ids" {
  description = "Subnets publics (ALB)."
  value       = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  description = "Subnets privés (ECS, RDS)."
  value       = module.vpc.private_subnet_ids
}

output "rds_endpoint" {
  description = "Endpoint de la base RDS (host:port)."
  value       = module.rds.endpoint
}

output "rds_address" {
  description = "Hostname RDS (pour SPRING_DATASOURCE_URL)."
  value       = module.rds.address
}
