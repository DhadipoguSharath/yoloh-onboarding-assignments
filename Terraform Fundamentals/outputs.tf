output "created_file_path" {
  description = "Path of the created file"
  value       = local_file.hi.filename
}

output "file_content" {
  description = "Content written to the file"
  value       = local_file.hi.content
}