output "bucket_name" {
  description = "Nom du bucket (pour aws s3 sync)."
  value       = aws_s3_bucket.frontend.id
}

output "website_endpoint" {
  description = "Endpoint du site statique (sans schéma)."
  value       = aws_s3_bucket_website_configuration.frontend.website_endpoint
}

output "website_url" {
  description = "URL complète du site (http://...). Sert d'origine CORS pour le backend."
  value       = "http://${aws_s3_bucket_website_configuration.frontend.website_endpoint}"
}
