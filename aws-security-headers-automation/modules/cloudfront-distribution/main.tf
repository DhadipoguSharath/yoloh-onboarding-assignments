resource "aws_cloudfront_distribution" "this" {
  enabled         = true
  is_ipv6_enabled = true
  comment         = var.distribution.comment

  origin {
    domain_name = var.distribution.origin_domain_name
    origin_id   = coalesce(var.distribution.origin_id, var.distribution.origin_domain_name)

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = var.distribution.origin_protocol_policy
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    target_origin_id       = coalesce(var.distribution.origin_id, var.distribution.origin_domain_name)
    viewer_protocol_policy = var.distribution.viewer_protocol_policy
    allowed_methods        = var.distribution.allowed_methods
    cached_methods         = var.distribution.cached_methods
    cache_policy_id        = var.distribution.cache_policy_id
    response_headers_policy_id = var.response_headers_policy_id
    compress                   = var.distribution.compress
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
    minimum_protocol_version       = "TLSv1.2_2021"
  }

  tags = var.distribution.tags
}