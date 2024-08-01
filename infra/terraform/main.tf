# Cloud
terraform {
  cloud {
    organization = "few-org"
    hostname     = "app.terraform.io"
    workspaces {
      #      name = "few-org-work"
      name = "few-aws-work"
    }
  }
}

# AWS
module "aws" {
  source          = "./aws"
  iam_root_arn    = var.aws_root_arn
  access_key      = var.aws_access_key
  secret_key      = var.aws_secret_key
  rds_username    = var.aws_rds_username
  rds_password    = var.aws_rds_password
  fe_origin       = var.fe_origin
  domain_name     = var.domain_name
  webhook_discord = var.webhook_discord
  email_username  = var.email_username
  email_password  = var.email_password
}
