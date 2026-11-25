module "security_headers" {
  source = "../../modules/cloudfront-security-headers"

  policy_name = "${var.environment}-${var.project_name}-security-headers-policy"
  content_security_policy = var.content_security_policy
}

module "distribution" {
  for_each = var.distributions
  source   = "../../modules/cloudfront-distribution"

  distribution               = each.value
  response_headers_policy_id = module.security_headers.security_headers_policy_id
}
