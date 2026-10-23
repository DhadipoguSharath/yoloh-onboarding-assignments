terraform {
  required_version = ">= 1.0.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

data "aws_caller_identity" "current_aws_account" {}

resource "aws_s3_bucket" "static_website_bucket" {
  bucket = "${var.s3_bucket_name_prefix}-${data.aws_caller_identity.current_aws_account.account_id}-${var.aws_region}"

  tags = {
    Name        = "Terraform Static Website Bucket"
    Environment = "Learning"
  }
}

resource "aws_s3_bucket_public_access_block" "static_website_public_access" {
  bucket = aws_s3_bucket.static_website_bucket.id

  block_public_acls       = true
  block_public_policy     = false
  ignore_public_acls      = true
  restrict_public_buckets = false
}

resource "aws_s3_bucket_website_configuration" "static_website_configuration" {
  bucket = aws_s3_bucket.static_website_bucket.id

  index_document {
    suffix = "index.html"
  }
}

resource "aws_s3_object" "website_index_document" {
  bucket       = aws_s3_bucket.static_website_bucket.id
  key          = "index.html"
  source       = "${path.module}/index.html"
  content_type = "text/html"

  etag = filemd5("${path.module}/index.html")
}

resource "aws_s3_bucket_policy" "static_website_read_access" {
  bucket = aws_s3_bucket.static_website_bucket.id

  depends_on = [
    aws_s3_bucket_public_access_block.static_website_public_access
  ]

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "AllowPublicWebsiteRead"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.static_website_bucket.arn}/*"
      }
    ]
  })
}

resource "aws_iam_role" "ec2_s3_read_role" {
  name = "terraform-static-website-ec2-s3-read-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role_policy" "ec2_s3_object_read_policy" {
  name = "terraform-static-website-object-read-policy"
  role = aws_iam_role.ec2_s3_read_role.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:GetObject"
        ]
        Resource = "${aws_s3_bucket.static_website_bucket.arn}/index.html"
      }
    ]
  })
}

resource "aws_iam_instance_profile" "ec2_s3_read_instance_profile" {
  name = "terraform-static-website-ec2-instance-profile"
  role = aws_iam_role.ec2_s3_read_role.name
}

data "aws_ssm_parameter" "amazon_linux_2023_ami" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}

resource "aws_security_group" "static_website_server_security_group" {
  name        = "terraform-static-website-server-sg"
  description = "Allow HTTP access to the EC2 static website server"

  ingress {
    description = "Allow HTTP traffic"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Allow outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Terraform Static Website Server Security Group"
  }
}

resource "aws_instance" "static_website_server" {
  ami                  = data.aws_ssm_parameter.amazon_linux_2023_ami.value
  instance_type        = var.ec2_instance_type
  iam_instance_profile = aws_iam_instance_profile.ec2_s3_read_instance_profile.name

  vpc_security_group_ids = [
    aws_security_group.static_website_server_security_group.id
  ]

  user_data = <<-EOF
    #!/bin/bash
    dnf install -y httpd awscli
    systemctl enable --now httpd

    aws s3 cp \
      s3://${aws_s3_bucket.static_website_bucket.bucket}/index.html \
      /var/www/html/index.html \
      --region ${var.aws_region}

    chown apache:apache /var/www/html/index.html
    chmod 644 /var/www/html/index.html
  EOF

  tags = {
    Name = "Terraform Static Website EC2 Server"
  }
}