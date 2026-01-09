variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "ecr_repository_name" {
  type    = string
  default = "testpyramid-app"
}

variable "db_username" {
  type    = string
  default = "anagram"
}

variable "db_password" {
  type    = string
  default = "anagram"
  sensitive = true
}

variable "db_instance_class" {
  type    = string
  default = "db.t3.micro"
}

