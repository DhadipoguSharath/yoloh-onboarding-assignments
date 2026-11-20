resource "aws_cloudfront_response_headers_policy" "security_headers" {
  name = var.policy_name

  security_headers_config {
    content_type_options {
      override = true
    }

    frame_options {
      frame_option = var.x_frame_options
      override     = true
    }

    strict_transport_security {
      access_control_max_age_sec = var.hsts_max_age
      include_subdomains         = var.hsts_include_subdomains
      preload                    = var.hsts_preload
      override                   = true
    }

    content_security_policy {
      content_security_policy = var.content_security_policy
      override                = true
    }
  }
}