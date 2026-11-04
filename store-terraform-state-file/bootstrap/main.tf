terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "= 6.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Retrieve AWS account ID
data "aws_caller_identity" "aws_identity" {}

# Retrieve current AWS region
data "aws_region" "current" {}

# Create S3 bucket for Terraform state storage
resource "aws_s3_bucket" "state_storage_bucket" {
  bucket = "${var.s3_bucket_prefix_name}-${data.aws_caller_identity.aws_identity.account_id}-${data.aws_region.current.name}"
}

# Enable versioning
resource "aws_s3_bucket_versioning" "recovery" {
  bucket = aws_s3_bucket.state_storage_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

# Block public access
resource "aws_s3_bucket_public_access_block" "bucket_access_manage" {
  bucket = aws_s3_bucket.state_storage_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# Enable server-side encryption
resource "aws_s3_bucket_server_side_encryption_configuration" "object_protection_configuration" {
  bucket = aws_s3_bucket.state_storage_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}
