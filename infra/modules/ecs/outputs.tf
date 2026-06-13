output "alb_dns_name" {
  description = "DNS public de l'ALB (point d'entrée de l'API)."
  value       = aws_lb.this.dns_name
}

output "ecr_repository_url" {
  description = "URL du dépôt ECR où pousser l'image backend."
  value       = aws_ecr_repository.backend.repository_url
}

output "cluster_name" {
  description = "Nom du cluster ECS."
  value       = aws_ecs_cluster.this.name
}

output "service_name" {
  description = "Nom du service ECS."
  value       = aws_ecs_service.backend.name
}

output "ecs_security_group_id" {
  description = "SG des tâches (pour resserrer le SG RDS plus tard)."
  value       = aws_security_group.ecs.id
}
