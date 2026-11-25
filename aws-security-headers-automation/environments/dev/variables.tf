variable "aws_region" {
  description = "AWS region for the CloudFront infrastructure."
  type        = string
  default     = "eu-north-1"
}

variable "environment" {
  description = "Environment name used to identify resources, such as dev or prod."
  type        = string
}

variable "content_security_policy" {
  description = "Content Security Policy directives for the application."
  type        = string
}

variable "project_name" {
  type = string
}

variable "distributions" {
  description = "CloudFront distributions to create, keyed by a stable identifier."
  type = map(object({
    comment                = string
    origin_domain_name     = string
    origin_id              = optional(string)
    allowed_methods        = optional(list(string), ["GET", "HEAD", "OPTIONS"])
    cached_methods         = optional(list(string), ["GET", "HEAD"])
    cache_policy_id        = optional(string, "4135ea2d-6df8-44a3-9df3-4b5a84be39ad")
    viewer_protocol_policy = optional(string, "redirect-to-https")
    origin_protocol_policy = optional(string, "https-only")
    compress               = optional(bool, true)
    tags                   = optional(map(string), {})
  }))
  default = {}
}