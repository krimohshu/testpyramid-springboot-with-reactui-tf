# Main Terraform configuration for existing AWS resources
# This references existing ECR and RDS resources that were created manually or in previous runs
# ECR Repository (existing)
data "aws_ecr_repository" "app" {
  name = var.ecr_repository_name
}
# DB Subnet Group (existing)
data "aws_db_subnet_group" "default" {
  name = "${var.ecr_repository_name}-subnets"
}
# DB Instance (existing) - optional, comment out if not created yet
# data "aws_db_instance" "postgres" {
#   db_instance_identifier = "testpyramid-db"
# }
# VPC and Subnets
data "aws_vpc" "default" {
  default = true
}
data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}
# Outputs
output "ecr_repository_url" {
  value = data.aws_ecr_repository.app.repository_url
}
output "db_subnet_group_name" {
  value = data.aws_db_subnet_group.default.name
}
# Uncomment if using data source for DB instance
# output "db_endpoint" {
#   value = data.aws_db_instance.postgres.endpoint
# }
