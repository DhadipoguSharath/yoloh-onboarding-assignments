module "security_headers" {
  source = "../../modules/cloudfront-security-headers"

  policy_name             = "dev-security-headers-policy"
  content_security_policy = var.content_security_policy
}

resource "aws_cloudfront_distribution" "app_distribution" {
  enabled         = true
  is_ipv6_enabled = true
  comment         = "Development distribution with security headers"

  origin {
    domain_name = var.lambda_origin_domain_name
    origin_id   = "lambda-function-url-origin"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    target_origin_id       = "lambda-function-url-origin"
    viewer_protocol_policy = "redirect-to-https"

    allowed_methods = ["GET", "HEAD", "OPTIONS"]
    cached_methods  = ["GET", "HEAD"]

    cache_policy_id = "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"

    response_headers_policy_id = module.security_headers.security_headers_policy_id
    compress                   = true
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

  tags = {
    Environment = "dev"
    Project     = "aws-security-headers-automation"
  }
}