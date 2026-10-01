terraform {
  required_providers {
    local = {
      source  = "hashicorp/local"
      version = "~> 2.5"
    }
  }
}

provider "local" {}

resource "local_file" "hi" {
  filename = "${path.module}/${var.file_name}"
  content  = var.file_content
}