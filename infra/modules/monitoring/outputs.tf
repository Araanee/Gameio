output "public_ip" {
  description = "IP publique de l'EC2 monitoring (pour Ansible + Grafana)."
  value       = aws_instance.monitoring.public_ip
}

output "grafana_url" {
  description = "URL de Grafana."
  value       = "http://${aws_instance.monitoring.public_ip}:3000"
}
