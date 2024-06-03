# Subnet 생성
## Load Balancer Subnet 생성
resource "ncloud_subnet" "lb_a" {
  vpc_no         = ncloud_vpc.vpc.id
  subnet         = "10.0.0.0/24"
  zone           = "KR-1"
  network_acl_no = ncloud_vpc.vpc.default_network_acl_no
  subnet_type    = "PUBLIC"
  name           = "${var.prefix}-lb-a"
  usage_type     = "LOADB"
}

## Public Subnet A 생성
resource "ncloud_subnet" "public_a" {
  vpc_no         = ncloud_vpc.vpc.id
  subnet         = "10.0.10.0/24"
  zone           = "KR-1"
  network_acl_no = ncloud_vpc.vpc.default_network_acl_no
  subnet_type    = "PUBLIC"
  name           = "${var.prefix}-public-a"
}

## Database Subnet A 생성
resource "ncloud_subnet" "db_a" {
  vpc_no         = ncloud_vpc.vpc.id
  subnet         = "10.0.100.0/24"
  zone           = "KR-1"
  network_acl_no = ncloud_vpc.vpc.default_network_acl_no
  subnet_type    = "PUBLIC"
  name           = "${var.prefix}-db-a"
}
