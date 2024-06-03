# Cloud
terraform {
  cloud {
    organization = "few-org"
    hostname     = "app.terraform.io"
    workspaces {
      name = "few-org-work"
    }
  }
}

# NCP Provider
module "ncp" {
  source       = "./ncp"
  prefix       = var.prefix
  region       = var.ncp_region
  access_key   = var.ncp_access_key
  secret_key   = var.ncp_secret_key
  rds_username = var.ncp_rds_username
  rds_password = var.ncp_rds_password
}
