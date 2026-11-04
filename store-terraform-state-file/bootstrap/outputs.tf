output "state_bucket_name" {
  description = "Name of the S3 bucket used for Terraform state storage"
  value       = aws_s3_bucket.state_storage_bucket.id
}

output "state_bucket_region" {
  description = "AWS region of the Terraform state bucket"
  value       = data.aws_region.current.name
}
