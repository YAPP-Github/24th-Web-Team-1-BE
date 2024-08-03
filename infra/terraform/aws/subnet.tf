# Subnet
resource "aws_subnet" "public_a" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.10.0/24"
  availability_zone = var.availability_zones[0]
  tags = {
    "Name" = "${var.prefix}-public-a"
  }
}

resource "aws_subnet" "private_a" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.100.0/24"
  availability_zone = var.availability_zones[0]
  tags = {
    "Name" = "${var.prefix}-private-a"
  }
}

resource "aws_subnet" "public_c" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.20.0/24"
  availability_zone = var.availability_zones[1]
  tags = {
    "Name" = "${var.prefix}-public-c"
  }
}

resource "aws_subnet" "private_c" {
  vpc_id            = aws_vpc.vpc.id
  cidr_block        = "10.0.110.0/24"
  availability_zone = var.availability_zones[1]
  tags = {
    "Name" = "${var.prefix}-private-c"
  }
}
