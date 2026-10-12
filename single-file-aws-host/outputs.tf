
output "ec2_public_ip" {
  description = "Public IP address of the EC2 web server"
  value       = aws_instance.web_server.public_ip
}

output "website_url" {
  description = "URL to access the web server"
  value       = "http://${aws_instance.web_server.public_ip}"
}