# Reference or create DB subnet group
resource "aws_db_subnet_group" "default" {
  name       = "${var.ecr_repository_name}-subnets"
  subnet_ids = data.aws_subnets.default.ids
  tags = { Name = "${var.ecr_repository_name}-subnets" }
  lifecycle {
    ignore_changes = [subnet_ids]
  }
}
resource "random_password" "db" {
  length  = 16
  special = true
  override_special = "_@"
  keepers = {
    env = var.environment
  }
  lifecycle {
    prevent_destroy = false
  }
}
locals {
  final_db_password = length(trimspace(var.db_password)) > 0 ? var.db_password : random_password.db.result
}
resource "aws_db_instance" "postgres" {
  allocated_storage    = 20
  engine               = "postgres"
  engine_version       = "15"
  instance_class       = var.db_instance_class
  db_name              = "anagramdb"
  username             = var.db_username
  password             = local.final_db_password
  skip_final_snapshot  = true
  publicly_accessible  = false
  db_subnet_group_name = aws_db_subnet_group.default.name
  tags = { Environment = var.environment }
  lifecycle {
    ignore_changes = [password]
  }
}
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}
data "aws_vpc" "default" {
  default = true
}
output "db_endpoint" {
  value = aws_db_instance.postgres.endpoint
}
output "db_username" {
  value = var.db_username
}
output "db_name" {
  value = aws_db_instance.postgres.db_name
}
