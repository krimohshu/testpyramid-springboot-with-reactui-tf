# Reference existing ECR repository (created manually or in previous run)
data "aws_ecr_repository" "app" {
  name = var.ecr_repository_name
}
output "ecr_repository_url" {
  value = data.aws_ecr_repository.app.repository_url
}
