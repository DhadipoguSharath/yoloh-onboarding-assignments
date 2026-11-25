output "cloudfront_distributions" {
  description = "IDs and domain names for the configured CloudFront distributions."
  value = {
    for name, distribution in module.distribution : name => {
      id          = distribution.distribution_id
      domain_name = distribution.domain_name
    }
  }
}

output "security_headers_policy_id" {
  description = "ID of the shared CloudFront security headers policy."
  value       = module.security_headers.security_headers_policy_id
}
