variable "policy_name" {
  description = "Name of the CloudFront security headers policy"
  type        = string
}

variable "content_security_policy" {
  description = "Content Security Policy directives for the application"
  type        = string
}

variable "x_frame_options" {
  description = "X-Frame-Options security header value"
  type        = string
  default     = "DENY"
}

variable "hsts_max_age" {
  description = "HSTS max age in seconds"
  type        = number
  default     = 31536000
}

variable "hsts_include_subdomains" {
  description = "Apply HSTS to all subdomains"
  type        = bool
  default     = false
}

variable "hsts_preload" {
  description = "Enable HSTS preload"
  type        = bool
  default     = false
}