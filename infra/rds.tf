resource "aws_db_subnet_group" "default" {
  name       = "${var.ecr_repository_name}-subnets"
  subnet_ids = data.aws_subnets.default.ids
  tags = { Name = "${var.ecr_repository_name}-subnets" }
}

resource "aws_db_instance" "postgres" {
  allocated_storage    = 20
  engine               = "postgres"
  engine_version       = "15.3"
  instance_class       = var.db_instance_class
  name                 = "anagramdb"
  username             = var.db_username
  password             = var.db_password
  skip_final_snapshot  = true
  publicly_accessible  = false
  db_subnet_group_name = aws_db_subnet_group.default.name
  tags = { Environment = var.environment }
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

