output "vpc_id" {
  description = "ID du VPC."
  value       = aws_vpc.this.id
}

output "vpc_cidr_block" {
  description = "CIDR du VPC (utilisé pour les règles de Security Group)."
  value       = aws_vpc.this.cidr_block
}

output "public_subnet_ids" {
  description = "IDs des subnets publics (pour l'ALB)."
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "IDs des subnets privés (pour ECS et RDS)."
  value       = aws_subnet.private[*].id
}

output "igw_id" {
  description = "ID de l'Internet Gateway."
  value       = aws_internet_gateway.this.id
}
