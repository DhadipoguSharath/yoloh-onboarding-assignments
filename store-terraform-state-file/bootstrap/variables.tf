variable "aws_region" {
  description = "AWS region where Terraform state bucket will be created"
  type        = string
  default     = "eu-north-1"
}

variable "s3_bucket_prefix_name" {
  description = "Prefix for the Terraform state S3 bucket"
  type        = string
}
