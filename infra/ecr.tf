resource "aws_ecr_repository" "app" {
  name                 = var.ecr_repository_name
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
  lifecycle {
    # Prevent destruction if repository already exists
    prevent_destroy = false
  }
}
output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}
