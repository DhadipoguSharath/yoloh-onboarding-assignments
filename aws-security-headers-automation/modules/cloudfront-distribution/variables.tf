variable "distribution" {
  description = "Configuration for a CloudFront distribution."
  type = object({
    comment                 = string
    origin_domain_name      = string
    origin_id               = optional(string)
    allowed_methods         = optional(list(string), ["GET", "HEAD", "OPTIONS"])
    cached_methods          = optional(list(string), ["GET", "HEAD"])
    cache_policy_id         = optional(string, "4135ea2d-6df8-44a3-9df3-4b5a84be39ad")
    viewer_protocol_policy  = optional(string, "redirect-to-https")
    origin_protocol_policy  = optional(string, "https-only")
    compress                = optional(bool, true)
    tags                    = optional(map(string), {})
  })
}

variable "response_headers_policy_id" {
  description = "CloudFront response headers policy to attach to the distribution."
  type        = string
}
