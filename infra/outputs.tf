output "ecr_repository_url" {
  # data.aws_ecr_repository exports `repository_url` (not repository_uri)
  value = data.aws_ecr_repository.app.repository_url
}

output "db_endpoint" {
  value = aws_db_instance.postgres.endpoint
}
