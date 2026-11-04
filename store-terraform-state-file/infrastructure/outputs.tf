output "aws_region" {
  description = "Configured AWS region"
  value       = var.aws_region
}

output "terraform_state_location" {
  description = "Remote Terraform state file location"
  value       = "s3://sharath-terraform-state-359290219388-eu-north-1/infrastructure/terraform.tfstate"
}
