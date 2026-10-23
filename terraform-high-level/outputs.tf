output "s3_website_endpoint" {
  description = "Static website endpoint hosted on Amazon S3"
  value       = "http://${aws_s3_bucket.static_website_bucket.bucket}.s3-website-${var.aws_region}.amazonaws.com"
}

output "ec2_public_ip_address" {
  description = "Public IP address of the EC2 static website server"
  value       = aws_instance.static_website_server.public_ip
}
