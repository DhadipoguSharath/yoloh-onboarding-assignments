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