# Assignment 1 — Single-File AWS Web Host with Terraform

## 1. Objective

Provision a basic web server on AWS using Terraform from a single `main.tf` file.

The assignment focuses on learning how to:

- Configure the AWS provider
- Retrieve an Amazon Linux 2023 AMI dynamically
- Configure a Security Group
- Provision an EC2 instance
- Install Apache automatically using `user_data`
- Create a web page dynamically on the EC2 instance
- Output and verify the EC2 public IP
- Follow the Terraform `init → fmt → validate → plan → apply → destroy` workflow

---

## 2. Project Structure

The assignment was developed as a single-file Terraform project.

```text
single-file-aws-host/
├── main.tf
└── .terraform.lock.hcl
```

Terraform-generated files such as `.terraform/`, `terraform.tfstate`, and `terraform.tfstate.backup` were excluded from Git.

---

## 3. AWS Configuration

### AWS Region

The deployment uses:

```hcl
region = "eu-north-1"
```

This is the Europe (Stockholm) AWS region.

### Provider Configuration

```hcl
provider "aws" {
  region = "eu-north-1"
}
```

The AWS provider allows Terraform to communicate with AWS and create the required infrastructure.

---

## 4. Dynamic Amazon Linux 2023 AMI

Instead of hardcoding an AMI ID, the configuration retrieves the Amazon Linux 2023 AMI dynamically through AWS Systems Manager (SSM) Parameter Store.

```hcl
data "aws_ssm_parameter" "al2023_ami" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}
```

The EC2 instance then uses:

```hcl
ami = data.aws_ssm_parameter.al2023_ami.value
```

### Why use SSM?

The AMI ID is dynamically retrieved using AWS SSM Parameter Store, ensuring that the latest Amazon Linux 2023 AMI for the selected AWS region is used instead of manually maintaining a fixed AMI ID.

---

## 5. Security Group

A Security Group was created to control network access to the web server.

```hcl
resource "aws_security_group" "web_sg" {
  name        = "terraform-med-level-web-sg"
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
}
```

### Inbound Rule

Port `80` is opened for HTTP traffic:

```text
Protocol: TCP
Port: 80
Source: 0.0.0.0/0
```

This allows the web page to be accessed through the EC2 instance's public IP.

### Outbound Rule

All outbound traffic is allowed so that the EC2 instance can communicate externally, including downloading packages during initialization.

---

## 6. EC2 Instance

The EC2 instance is configured as follows:

```hcl
resource "aws_instance" "web_server" {
  ami           = data.aws_ssm_parameter.al2023_ami.value
  instance_type = "t3.micro"

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
    Name = "terraform-web-server"
  }
}
```

### Instance Type

The original assignment specified `t2.micro`.

However, AWS reported that `t2.micro` was not eligible for the account's Free Tier configuration in the selected region. The available Free-Tier-eligible instance types were checked using the AWS CLI, and `t3.micro` was selected as the closest suitable alternative.

```hcl
instance_type = "t3.micro"
```

---

## 7. Apache Web Server Configuration

Apache is installed automatically when the EC2 instance starts.

The `user_data` script performs:

```bash
dnf update -y
dnf install -y httpd
systemctl enable httpd
systemctl start httpd
```

This means no manual installation is required after connecting to the server.

---

## 8. Dynamic `index.html` Creation

The web page is created by the EC2 `user_data` script:

```bash
echo '<html>
<head>
  <title>Terraform Web Server</title>
</head>
<body>
  <h1>Welcome to AWS via Terraform!</h1>
</body>
</html>' > /var/www/html/index.html
```

The file is created **inside the EC2 instance** at:

```text
/var/www/html/index.html
```

It is not a local Windows file.

The resulting page displays:

```text
Welcome to AWS via Terraform!
```

---

## 9. EC2 Public IP Output

The public IP address is exposed through a Terraform output:

```hcl
output "ec2_public_ip" {
  description = "Public IP address of the EC2 web server"
  value       = aws_instance.web_server.public_ip
}
```

This makes it easy to obtain the address after deployment.

---

## 10. Terraform Workflow

The assignment followed the standard Terraform workflow.

### Step 1 — Initialize

```powershell
terraform init
```

This initializes the Terraform working directory and downloads the required AWS provider.

### Step 2 — Format

```powershell
terraform fmt
```

This formats the Terraform configuration according to Terraform's standard formatting.

### Step 3 — Validate

```powershell
terraform validate
```

This checks whether the Terraform configuration is syntactically valid and internally consistent.

### Step 4 — Plan

```powershell
terraform plan
```

This previews the infrastructure Terraform intends to create or change.

### Step 5 — Apply

```powershell
terraform apply
```

This creates the AWS resources defined in the configuration.

### Step 6 — Destroy

```powershell
terraform destroy
```

This removes the infrastructure created by Terraform.

---

## 11. Deployment Verification

After deployment, the EC2 instance was successfully created and reached:

```text
Instance state: running
System status: ok
Instance status: ok
```

The EC2 public IP was:

```text
13.51.70.254
```

The HTTP connection was tested successfully on port `80`.

The web server returned:

```text
HTTP/1.1 200 OK
```

The browser displayed:

```text
Welcome to AWS via Terraform!
```

This confirmed that:

1. The EC2 instance was running.
2. The Security Group allowed HTTP traffic.
3. Apache was installed successfully.
4. Apache was running.
5. `index.html` was created correctly.
6. The web server was accessible through the EC2 public IP.

---

## 12. Troubleshooting Performed

### Issue 1 — Security Group Name Conflict

A duplicate Security Group name caused an AWS error.

The Security Group name was changed to:

```text
terraform-med-level-web-sg
```

This allowed Terraform to create the required Security Group successfully.

### Issue 2 — `t2.micro` Free Tier Eligibility

The original instance type was:

```text
t2.micro
```

AWS reported that it was not eligible for the account's Free Tier configuration in the selected region.

The AWS CLI was used to check Free-Tier-eligible instance types:

```powershell
aws ec2 describe-instance-types --region eu-north-1 --filters "Name=free-tier-eligible,Values=true" --query "InstanceTypes[].InstanceType" --output text --no-cli-pager
```

`t3.micro` was selected as the available alternative.

### Issue 3 — Web Page Not Initially Loading

The EC2 instance was running, but the page was initially not responding.

The following checks were performed:

- EC2 instance status
- Security Group port 80
- Network connectivity to port 80
- Apache installation
- Apache service status
- HTTP response from the public IP

A connectivity test confirmed:

```text
TcpTestSucceeded : True
```

A direct HTTP request returned:

```text
HTTP 200 OK
```

The browser then successfully displayed the expected web page.

---

## 13. Final Terraform Architecture

```text
                    Terraform
                        |
                        v
                 AWS Provider
                 eu-north-1
                        |
            +-----------+-----------+
            |                       |
            v                       v
     Security Group              EC2 Instance
       HTTP : 80                  t3.micro
            |                       |
            |                       v
            |                 Amazon Linux 2023
            |                       |
            |                       v
            |                    user_data
            |                       |
            |              +--------+--------+
            |              |                 |
            |              v                 v
            |           Install           Create
            |           Apache          index.html
            |                                |
            +--------------------------------+
                             |
                             v
                     Public IP :80
                             |
                             v
                          Browser
```

---

## 14. Git Structure

The assignment was placed in the repository under the task branch:

```text
feature/terraform-fundamentals
        |
        └── task/single-file-aws-host
```

The project folder is:

```text
single-file-aws-host/
├── .terraform.lock.hcl
└── main.tf
```

Terraform-generated state and provider directories were excluded using `.gitignore`.

---

## 15. Key Terraform Concepts Learned

### Provider

The provider tells Terraform which cloud platform to communicate with.

```hcl
provider "aws" {
  region = "eu-north-1"
}
```

### Data Source

The SSM data source retrieves an existing value from AWS rather than creating a resource.

```hcl
data "aws_ssm_parameter" "al2023_ami" {
  ...
}
```

### Resource

Resources represent infrastructure that Terraform creates and manages.

Examples:

```text
aws_security_group
aws_instance
```

### User Data

`user_data` allows initialization commands to run automatically when the EC2 instance starts.

### Output

Outputs expose useful values after Terraform creates infrastructure.

```text
EC2 public IP
```

### State

Terraform state tracks the infrastructure Terraform manages. The local state files were intentionally excluded from Git.

---

## 16. Final Outcome

The assignment successfully demonstrated how to use Terraform to provision a complete basic AWS web server from a single Terraform configuration.

The final implementation:

- Uses AWS region `eu-north-1`
- Dynamically retrieves the Amazon Linux 2023 AMI through SSM Parameter Store
- Creates a Security Group with HTTP access on port 80
- Provisions a `t3.micro` EC2 instance
- Installs Apache automatically using `user_data`
- Dynamically creates `/var/www/html/index.html`
- Outputs the EC2 public IP
- Successfully serves the web page through the public IP
- Uses the Terraform `init`, `fmt`, `validate`, `plan`, `apply`, and `destroy` workflow
