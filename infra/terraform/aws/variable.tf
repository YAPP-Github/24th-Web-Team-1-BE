// set at root
variable "domain_name" {
  type        = string
  description = "The domain name for the Route 53 hosted zone"
}

// set at root
variable "access_key" {
  type        = string
  sensitive   = true
  description = "The access key for the IAM root user"
}

// set at root
variable "secret_key" {
  type        = string
  sensitive   = true
  description = "The secret key for the IAM root user"
}

// set at root
variable "iam_root_arn" {
  type        = string
  description = "The IAM root user"
}

variable "prefix" {
  type        = string
  default     = "few"
  description = "The prefix to use for all resources"
}



variable "region" {
  type        = string
  default     = "ap-northeast-2"
  description = "The region to deploy to"
}

variable "availability_zones" {
  type        = list(string)
  default     = ["ap-northeast-2a", "ap-northeast-2c"]
  description = "The availability zones to use for the subnets"
}


variable "instance_type" {
  type        = string
  default     = "t2.micro"
  description = "The instance type to use for the EC2 instances"
}

variable "lc_image_id" {
  type        = string
  default     = "ami-04607756254222deb"
  description = "The ID of the AMI to use for the launch configuration"
}

variable "rds_db_name" {
  type        = string
  default     = "mydb"
  description = "The name of the database to create in the RDS instance"
}

variable "rds_username" {
  type        = string
  default     = "root"
  description = "The username for the RDS instance"
}

variable "rds_password" {
  type        = string
  default     = "thisisrdspassword"
  description = "The password for the RDS instance"
}

variable "rds_instance_class" {
  type        = string
  default     = "db.t3.micro"
  description = "The instance class for the RDS instance"
}

variable "rds_engine" {
  type        = string
  default     = "MySQL"
  description = "The engine for the RDS instance"
}

variable "rds_engine_version" {
  type        = string
  default     = "8.0.35"
  description = "The engine version for the RDS instance"
}

variable "rds_allocated_storage" {
  type        = number
  default     = 20
  description = "The allocated storage for the RDS instance"
}

variable "fe_origin" {
    type        = string
    default     = "https://www.fewletter.site"
    description = "The origin for the web front-end"
}

variable "webhook_discord" {
    type        = string
    description = "The Discord webhook URL to use for notifications"
}

variable "email_username" {
    type        = string
    description = "The username for the email service"
}

variable "email_password" {
    type        = string
    description = "The password for the email service"
}
