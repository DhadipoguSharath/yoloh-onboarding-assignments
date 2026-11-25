output "distribution_id" {
  description = "ID of the CloudFront distribution."
  value       = aws_cloudfront_distribution.this.id
}

output "domain_name" {
  description = "CloudFront domain name."
  value       = aws_cloudfront_distribution.this.domain_name
}
