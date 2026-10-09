
resource "aws_security_group" "web_sg" {
  name        = "terraform-med-level-web-sg1"
  description = "Allow HTTP traffic to web server"

  ingress {
    description = "Allow HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name        = "${var.project_name}-${var.environment}-web-server-sg"
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}

data "aws_ssm_parameter" "al2023_ami" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}

resource "aws_instance" "web_server" {
  ami           = data.aws_ssm_parameter.al2023_ami.value
  instance_type = var.instance_type

  vpc_security_group_ids = [aws_security_group.web_sg.id]

  user_data = <<-EOF
              #!/bin/bash
              dnf update -y
              dnf install -y httpd
              systemctl enable httpd
              systemctl start httpd

              echo '<html>
              <head>
                <title>Terraform Web Server</title>
              </head>
              <body>
                <h1>Welcome to AWS via Terraform!</h1>
              </body>
              </html>' > /var/www/html/index.html
              EOF
  tags = {
    Name        = "${var.project_name}-${var.environment}-web-server"
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "Terraform"
  }
}