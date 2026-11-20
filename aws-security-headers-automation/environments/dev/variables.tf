variable "aws_region" {
  description = "AWS region for the CloudFront infrastructure"
  type        = string
  default     = "eu-north-1"
}

variable "lambda_origin_domain_name" {
  description = "Domain name of the existing Lambda Function URL"
  type        = string
}

variable "content_security_policy" {
  description = "CSP configuration for the application"
  type        = string
}