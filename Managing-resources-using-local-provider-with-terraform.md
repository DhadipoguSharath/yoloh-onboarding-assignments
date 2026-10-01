
# Managing Resources Using Terraform with the Local Provider

## 1. Overview

This assignment demonstrates the fundamentals of Terraform by managing local resources using the **HashiCorp Local Provider**.

Terraform is an Infrastructure as Code (IaC) tool that allows us to define, create, update, and delete resources using configuration files instead of performing these operations manually.

In this assignment, Terraform is used to create and manage a local text file. Through this hands-on exercise, we understand how Terraform providers, resources, variables, outputs, state management, and essential Terraform commands work.

## 2. Objectives

- Understand the fundamentals of Terraform and Infrastructure as Code.
- Understand the role of Terraform providers.
- Configure and initialize the Local Provider.
- Create and manage a local file using Terraform resources.
- Use variables to make configurations reusable and flexible.
- Use outputs to display resource information.
- Understand Terraform state and how it tracks managed resources.
- Identify changes made outside Terraform (configuration drift).
- Practice essential Terraform commands.
- Safely destroy resources using Terraform.

## 3. Prerequisites

- **Terraform CLI:** Installed and configured on the system.
- **Visual Studio Code:** Used to create and edit Terraform configuration files.
- **PowerShell:** Used to execute Terraform commands.
- **Git:** Used for version control.

**Note:** This assignment uses the Local Provider, so an AWS account or other cloud infrastructure is not required.

## 4. Understanding Terraform Providers

A provider is a plugin that allows Terraform to interact with a particular platform or service.

Examples:

- AWS Provider – Manages AWS resources.
- Azure Provider – Manages Azure resources.
- Google Cloud Provider – Manages Google Cloud resources.
- Local Provider – Manages local resources such as files.

For this assignment, the HashiCorp Local Provider is used to create and manage a text file on the local system.

## 5. Project Structure

```text
Terraform Fundamentals/
│
├── main.tf
├── variables.tf
├── outputs.tf
└── README.md
```

**File descriptions:**

- `main.tf` – Defines the Terraform provider configuration and resource.
- `variables.tf` – Declares input variables used by the resource.
- `outputs.tf` – Defines output values to display resource information.
- `README.md` – Documents the assignment, implementation, and execution steps.

Terraform automatically loads all `.tf` files in the current working directory. Separating the configuration into multiple files helps maintain readability and organization.

## 6. Provider Configuration

The Local Provider is configured in `main.tf`.

```hcl
terraform {
  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
  }
}

provider "local" {}
```

**Explanation:**

- `terraform`: Defines Terraform configuration settings.
- `required_providers`: Specifies the providers required by the project.
- `local`: Defines the local provider name.
- `source`: Identifies the provider source as `hashicorp/local`.
- `version`: Specifies the acceptable provider version range (`>= 2.5` and `< 3.0`).
- `provider "local"`: Configures the Local Provider. No additional configuration is required for this exercise.

## 7. Initializing Terraform

After configuring the provider, initialize the Terraform working directory.

```powershell
terraform init
```

**Purpose:**

- Initializes the Terraform project.
- Downloads the required provider plugin.
- Creates the `.terraform` directory.
- Generates or updates the `.terraform.lock.hcl` dependency lock file.

The initialization process prepares the project for planning and applying infrastructure changes. It does not create the actual resources.

## 8. Creating a Resource

A resource is an object that Terraform manages.

For this assignment, the `local_file` resource is used to create a text file.

Add the following resource configuration to `main.tf`:

```hcl
resource "local_file" "hi" {
  filename = "${path.module}/hi.txt"
  content  = "Hi From Terraform!"
}
```

**Explanation:**

- `resource`: Declares a resource that Terraform should manage.
- `local_file`: Specifies the resource type provided by the Local Provider.
- `hi`: Resource name used by Terraform to identify this resource.
- `filename`: Specifies the path and name of the file to create.
- `content`: Specifies the content to write into the file.
- `path.module`: Refers to the directory containing the current Terraform module.

The resource address is:

```text
local_file.hi
```

## 9. Formatting and Validating Configuration

Before creating the resource, format and validate the Terraform configuration.

**Format the configuration:**

```powershell
terraform fmt
```

Formats Terraform configuration files according to standard formatting conventions.

**Validate the configuration:**

```powershell
terraform validate
```

Checks whether the Terraform configuration is syntactically valid and internally consistent.

## 10. Previewing Changes Using Terraform Plan

Execute:

```powershell
terraform plan
```

**Purpose:**

- Compares the desired configuration with the current infrastructure and state.
- Identifies resources that need to be created, updated, or deleted.
- Displays the proposed changes before execution.

For the initial resource creation, the expected summary is:

```text
Plan: 1 to add, 0 to change, 0 to destroy.
```

The `+` symbol indicates that Terraform plans to create a resource.

At this stage, Terraform only displays the proposed changes. The resource has not yet been created.

## 11. Creating the Resource Using Terraform Apply

Execute:

```powershell
terraform apply
```

Terraform displays the proposed changes and requests confirmation.

Enter:

```text
yes
```

After confirmation, Terraform creates the local file.

**Expected result:**

```text
Apply complete! Resources: 1 added, 0 changed, 0 destroyed.
```

A new file named `hi.txt` is created in the Terraform project directory.

To verify the file:

```powershell
Test-Path .\hi.txt
```

Expected output:

```text
True
```

To view its contents:

```powershell
Get-Content .\hi.txt
```

Expected output:

```text
Hi From Terraform!
```

## 12. Understanding Terraform Variables

Variables allow us to define reusable input values instead of hardcoding them directly inside resource configurations.

Create a file named `variables.tf` and add:

```hcl
variable "file_name" {
  description = "Name of the file to create"
  type        = string
  default     = "hi.txt"
}

variable "file_content" {
  description = "Content to write into the file"
  type        = string
  default     = "Hi From Terraform!"
}
```

**Explanation:**

- `variable`: Declares an input variable.
- `file_name`: Variable used to specify the filename.
- `file_content`: Variable used to specify the file content.
- `description`: Explains the purpose of the variable.
- `type`: Defines the expected data type.
- `default`: Specifies the value used when no other input is provided.

### Connecting Variables to the Resource

Update the resource configuration in `main.tf`:

```hcl
resource "local_file" "hi" {
  filename = "${path.module}/${var.file_name}"
  content  = var.file_content
}
```

**Explanation:**

- `var.file_name`: References the filename variable.
- `var.file_content`: References the content variable.

Now, the resource uses values defined in `variables.tf` instead of hardcoded values.

Execute:

```powershell
terraform fmt
terraform validate
terraform plan
```

Since the variable defaults match the existing resource configuration, Terraform should report that no infrastructure changes are required.

## 13. Understanding Terraform Outputs

Outputs allow us to retrieve and display useful information about resources managed by Terraform.

Create a file named `outputs.tf` and add:

```hcl
output "created_file_path" {
  description = "Path of the created file"
  value       = local_file.hi.filename
}

output "file_content" {
  description = "Content written to the file"
  value       = local_file.hi.content
}
```

**Explanation:**

- `output`: Declares an output block.
- `created_file_path`: Name of the output that displays the file path.
- `file_content`: Name of the output that displays the file content.
- `value`: Specifies the resource attribute to retrieve.
- `local_file.hi.filename`: References the filename attribute.
- `local_file.hi.content`: References the content attribute.

Execute:

```powershell
terraform plan
terraform apply
```

Confirm with `yes` if prompted.

Then execute:

```powershell
terraform output
```

**Expected output:**

```text
created_file_path = "./hi.txt"
file_content = "Hi From Terraform!"
```

The outputs make it easier to retrieve important resource information without manually inspecting the resource configuration.

## 14. Understanding Terraform State

Terraform State is a record maintained by Terraform to keep track of the resources it manages.

By default, Terraform stores this information in a local file named:

```text
terraform.tfstate
```

The state file helps Terraform understand the relationship between the configuration and the actual managed resources.

### Important State Commands

**List managed resources:**

```powershell
terraform state list
```

Expected output:

```text
local_file.hi
```

**View details of a specific resource:**

```powershell
terraform state show local_file.hi
```

Displays the attributes recorded for the specified resource.

**View the current state in a readable format:**

```powershell
terraform show
```

Displays information about the current Terraform-managed infrastructure.

### Important State File Information

The state file contains information such as:

- Terraform state format version.
- Terraform version.
- Resource type and resource name.
- Provider information.
- Resource attributes.
- Resource identifiers and content hashes.
- Output values, when configured.

**Important:** The state file should not be manually modified. It may contain sensitive information and should generally be excluded from Git version control.

## 15. Understanding Configuration Drift

Configuration drift occurs when the actual resource is modified outside Terraform and no longer matches the desired configuration.

In this exercise, we manually modify the `hi.txt` file to understand how Terraform detects such changes.

### Step 1: Modify the File Manually

Execute:

```powershell
Set-Content .\hi.txt "I changed this file manually."
```

This command changes the actual file content without modifying the Terraform configuration.

### Step 2: Review the Changes

Execute:

```powershell
terraform plan
```

Terraform refreshes the managed resource information and compares the actual file with the declared configuration.

It identifies the difference and proposes the required action to restore the desired configuration.

### Step 3: Restore the Original Configuration

Execute:

```powershell
terraform apply
```

Enter `yes` when prompted.

### Step 4: Verify the File

Execute:

```powershell
Get-Content .\hi.txt
```

Expected output:

```text
Hi From Terraform!
```

This exercise demonstrates how Terraform identifies changes made outside its configuration and proposes corrective actions.

The exact action depends on the resource and the type of change. For this `local_file` example, a content change can require replacing the file.

## 16. Important Terraform Commands

| Command | Description |
|---|---|
| `terraform init` | Initializes the working directory and installs required providers. |
| `terraform fmt` | Formats Terraform configuration files. |
| `terraform validate` | Validates the configuration. |
| `terraform plan` | Previews the proposed infrastructure changes. |
| `terraform apply` | Creates or updates resources. |
| `terraform output` | Displays configured output values. |
| `terraform show` | Displays the current state in readable form. |
| `terraform state list` | Lists resources managed by Terraform. |
| `terraform state show` | Displays details of a specific resource. |
| `terraform plan -destroy` | Previews resource deletion. |
| `terraform destroy` | Deletes resources managed by Terraform. |

### Basic Terraform Workflow

```text
terraform init
      |
      v
terraform fmt
      |
      v
terraform validate
      |
      v
terraform plan
      |
      v
terraform apply
      |
      v
terraform output
```

This workflow covers the basic lifecycle of managing resources using Terraform.

## 17. Destroying Resources

After completing the practical exercise, remove the Terraform-managed resource using the destroy command.

First, preview the deletion:

```powershell
terraform plan -destroy
```

Then execute:

```powershell
terraform destroy
```

Terraform displays the resources that will be deleted and requests confirmation.

Enter:

```text
yes
```

**Expected result:**

```text
Destroy complete! Resources: 1 destroyed.
```

### Verify Resource Deletion

Check whether the file exists:

```powershell
Test-Path .\hi.txt
```

Expected output:

```text
False
```

Check the Terraform state:

```powershell
terraform state list
```

No managed resources should be listed.

The configuration files remain in the project, while the managed local file has been deleted.

## 18. Git Version Control and Project Files

The Terraform configuration files are maintained in Git for version control.

Recommended project structure:

```text
Terraform Fundamentals/
│
├── main.tf
├── variables.tf
├── outputs.tf
├── README.md
└── .terraform.lock.hcl
```

The dependency lock file is generated by `terraform init` and should generally be committed to ensure consistent provider selections.

The following local working files and directories should generally be excluded from version control:

```gitignore
.terraform/
*.tfstate
*.tfstate.*
```

The Terraform state file and generated resources are local working data for this exercise and are not required as Git deliverables.

## 19. Learning Outcomes

By completing this assignment, the following concepts were understood and practiced:

- Understanding Infrastructure as Code and Terraform fundamentals.
- Understanding the purpose of Terraform providers.
- Configuring and initializing the Local Provider.
- Creating and managing local resources using Terraform.
- Using input variables to make configurations reusable.
- Defining outputs to retrieve resource information.
- Understanding how Terraform maintains resource state.
- Identifying and correcting configuration drift.
- Executing the Terraform resource lifecycle.
- Using Git to maintain Terraform configuration files.

## 20. References

- [Terraform Official Documentation](https://developer.hashicorp.com/terraform/docs)
- [Terraform Local Provider Documentation](https://registry.terraform.io/providers/hashicorp/local/latest/docs)
- [Local File Resource Documentation](https://registry.terraform.io/providers/hashicorp/local/latest/docs/resources/file)
- [Terraform Provider Requirements](https://developer.hashicorp.com/terraform/language/providers/requirements)
- [Terraform Resource Block Documentation](https://developer.hashicorp.com/terraform/language/block/resource)

---

**Assignment:** Managing Resources Using Terraform with the Local Provider

**Tools Used:** Terraform CLI, HashiCorp Local Provider, Visual Studio Code, PowerShell, and Git.