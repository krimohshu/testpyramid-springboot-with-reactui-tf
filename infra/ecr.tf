data "aws_ecr_repository" "app" {
  repository_name = var.ecr_repository_name
}
