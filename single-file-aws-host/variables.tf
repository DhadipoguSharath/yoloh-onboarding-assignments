
variable "aws_region" {
  description = "AWS region where resources will be created"
  type        = string
  default     = "eu-north-1"
}

variable "instance_type" {
  description = "EC2 instance type for the web server"
  type        = string
  default     = "t3.micro"
}

variable "project_name" {
  description = "Name of this Terraform project"
  type        = string
  default     = "single-file-aws-host"
}

variable "environment" {
  description = "Deployment environment"
  type        = string
  default     = "dev"
}