output "endpoint" {
  description = "Endpoint de connexion (host:port) de la base."
  value       = aws_db_instance.this.endpoint
}

output "address" {
  description = "Hostname de la base (sans le port)."
  value       = aws_db_instance.this.address
}

output "port" {
  description = "Port de la base."
  value       = aws_db_instance.this.port
}

output "db_name" {
  description = "Nom de la base."
  value       = aws_db_instance.this.db_name
}

output "security_group_id" {
  description = "ID du SG de la base (pour autoriser le SG ECS plus tard)."
  value       = aws_security_group.rds.id
}
