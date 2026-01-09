output "ecr_repository_url" {
  value = data.aws_ecr_repository.app.repository_uri
}

output "db_endpoint" {
  value = aws_db_instance.postgres.endpoint
}
