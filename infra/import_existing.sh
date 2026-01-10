#!/bin/bash
# Script to import existing AWS resources into Terraform state
set -e
cd "$(dirname "$0")"
echo "=== Importing existing AWS resources into Terraform state ==="
# Import ECR repository (if exists)
echo "Checking ECR repository..."
if aws ecr describe-repositories --repository-names testpyramid-app --region eu-west-2 2>/dev/null; then
  echo "ECR repository exists, importing..."
  terraform import aws_ecr_repository.app testpyramid-app 2>/dev/null || echo "Already imported or failed"
else
  echo "ECR repository doesn't exist, will be created"
fi
# Import DB Subnet Group (if exists)
echo "Checking DB Subnet Group..."
if aws rds describe-db-subnet-groups --db-subnet-group-name testpyramid-app-subnets --region eu-west-2 2>/dev/null; then
  echo "DB Subnet Group exists, importing..."
  terraform import aws_db_subnet_group.default testpyramid-app-subnets 2>/dev/null || echo "Already imported or failed"
else
  echo "DB Subnet Group doesn't exist, will be created"
fi
# Note: DB instance import requires the instance identifier
# If you have a DB instance, uncomment and update the identifier:
# terraform import aws_db_instance.postgres <db-instance-identifier>
echo "=== Import complete ==="
echo "Run 'terraform plan' to see if there are any differences"
