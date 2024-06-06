variable "prefix" {
  type        = string
  default     = "few"
  description = "The prefix to use for all resources"
}


variable "ncp_access_key" {
  type        = string
  sensitive   = true
  description = "The access key for the IAM root user"
}

variable "ncp_secret_key" {
  type        = string
  sensitive   = true
  description = "The secret key for the IAM root user"
}

variable "ncp_region" {
  type        = string
  description = "The region where the resources will be created"
  default     = "KR"
}

variable "ncp_rds_username" {
  type        = string
  default     = "thisisrdsroot"
  description = "The username for the RDS instance"
}

variable "ncp_rds_password" {
  type        = string
  default     = "thisisrdspassword@1"
  description = "The password for the RDS instance"
}
