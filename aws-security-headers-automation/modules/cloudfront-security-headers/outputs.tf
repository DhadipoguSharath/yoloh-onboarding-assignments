output "security_headers_policy_id" {
  description = "ID of the CloudFront security headers policy"
  value       = aws_cloudfront_response_headers_policy.security_headers.id
}

output "security_headers_policy_etag" {
  description = "ETag of the CloudFront security headers policy"
  value       = aws_cloudfront_response_headers_policy.security_headers.etag
}