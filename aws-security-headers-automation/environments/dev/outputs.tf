output "cloudfront_distribution_id" {
  description = "ID of the CloudFront distribution"
  value       = aws_cloudfront_distribution.app_distribution.id
}

output "cloudfront_domain_name" {
  description = "CloudFront distribution domain name"
  value       = aws_cloudfront_distribution.app_distribution.domain_name
}

output "security_headers_policy_id" {
  description = "ID of the reusable security headers policy"
  value       = module.security_headers.security_headers_policy_id
}