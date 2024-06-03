# Route Table 생성
## Public RT 생성
resource "ncloud_route_table" "public_rt" {
  vpc_no                = ncloud_vpc.vpc.id
  supported_subnet_type = "PUBLIC"
  name                  = "${var.prefix}-public-rt"
}

## Private RT 생성
resource "ncloud_route_table" "private_rt" {
  vpc_no                = ncloud_vpc.vpc.id
  supported_subnet_type = "PRIVATE"
  name                  = "${var.prefix}-private-rt"
}

# Route Table Association 생성
## Public RT & Public Subnet A 연결
resource "ncloud_route_table_association" "public_a" {
  route_table_no = ncloud_route_table.public_rt.id
  subnet_no      = ncloud_subnet.public_a.id
}
