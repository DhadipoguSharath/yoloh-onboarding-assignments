
variable "aws_region" {
  description = "AWS region where the infrastructure will be deployed"
  type        = string
}

variable "ec2_instance_type" {
  description = "Instance type for the EC2 web server"
  type        = string
  default     = "t3.micro"
}

variable "s3_bucket_name_prefix" {
  description = "Prefix used to generate a globally unique S3 bucket name"
  type        = string
  default     = "sharath-terraform-web"
}
