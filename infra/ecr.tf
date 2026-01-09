resource "aws_ecr_repository" "app" {
  name                 = var.ecr_repository_name
  image_tag_mutability = "MUTABLE"
  tags = {
    Environment = var.environment
  }
}

output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}

