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

output "alb_dns_name" {
  description = "DNS public de l'ALB — point d'entrée de l'API."
  value       = module.ecs.alb_dns_name
}

output "ecr_repository_url" {
  description = "URL ECR où pousser l'image backend."
  value       = module.ecs.ecr_repository_url
}

output "frontend_bucket" {
  description = "Bucket S3 du frontend (pour aws s3 sync)."
  value       = module.s3_frontend.bucket_name
}

output "frontend_url" {
  description = "URL publique du frontend (site statique S3)."
  value       = module.s3_frontend.website_url
}

output "monitoring_public_ip" {
  description = "IP publique de l'EC2 monitoring (pour l'inventaire Ansible)."
  value       = module.monitoring.public_ip
}

output "grafana_url" {
  description = "URL de Grafana."
  value       = module.monitoring.grafana_url
}
