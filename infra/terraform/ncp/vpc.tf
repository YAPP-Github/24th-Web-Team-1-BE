# VPC 생성
resource "ncloud_vpc" "vpc" {
  name            = "${var.prefix}-vpc"
  ipv4_cidr_block = "10.0.0.0/16"
}
