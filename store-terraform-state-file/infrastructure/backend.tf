terraform {
  backend "s3" {
    bucket       = "sharath-terraform-state-359290219388-eu-north-1"
    key          = "infrastructure/terraform.tfstate"
    region       = "eu-north-1"
    encrypt      = true
    use_lockfile = true
  }
}

data "aws_caller_identity" "current" {}