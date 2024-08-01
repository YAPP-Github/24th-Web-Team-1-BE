variable "prefix" {
  type        = string
  default     = "few"
  description = "The prefix to use for all resources"
}

variable "domain_name" {
  type        = string
  description = "The domain name for the Route 53 hosted zone"
  default     = "fewletter.site"
}

variable "ncp_access_key" {
  type        = string
  sensitive   = true
  description = "The access key for the IAM root user"
  default     = ""
}

variable "ncp_secret_key" {
  type        = string
  sensitive   = true
  description = "The secret key for the IAM root user"
  default     = ""
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

variable "aws_root_arn" {
  type        = string
  sensitive   = true
  description = "The ARN of the root account"
}

variable "aws_access_key" {
  type        = string
  sensitive   = true
  description = "The access key for the IAM root user"
}

variable "aws_secret_key" {
  type        = string
  sensitive   = true
  description = "The secret key for the IAM root user"
}

variable "aws_rds_username" {
  type        = string
  sensitive   = true
  description = "The username for the RDS instance"
  default     = "root"
}

variable "aws_rds_password" {
  type        = string
  sensitive   = true
  description = "The password for the RDS instance"
}

variable "fe_origin" {
  type        = string
  description = "Frontend origin"
}

variable "email_username" {
  type        = string
  description = "Email username"
}

variable "email_password" {
  type        = string
  sensitive   = true
  description = "Email password"
}

variable "webhook_discord" {
  type        = string
  description = "Discord webhook"
}