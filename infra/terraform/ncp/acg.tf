# ACG 생성
resource "ncloud_access_control_group" "be_server" {
  name        = "${var.prefix}-be-server-acg"
  description = "Backend Server Access Control Group"
  vpc_no      = ncloud_vpc.vpc.id
}

# ACG Rule 생성
## Backend Server ACG Rule
resource "ncloud_access_control_group_rule" "be_server_rule" {
  access_control_group_no = ncloud_access_control_group.be_server.id

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "22"
    description = "accept 22 port"
  }

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
    description = "accept 1-65535 port"
  }

  outbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
    description = "accept 1-65535 port"
  }
}

## Database Server ACG Rule
resource "ncloud_access_control_group_rule" "db_server_rule" {
  access_control_group_no = ncloud_mysql.mysql.access_control_group_no_list[0]

  inbound {
    protocol                       = "TCP"
    port_range                     = "3306"
    source_access_control_group_no = ncloud_access_control_group.be_server.id
    description                    = "accept 3306 port"
  }

  inbound {
    protocol    = "TCP"
    ip_block    = ncloud_subnet.public_a.subnet
    port_range  = "3306"
    description = "accept 3306 port"
  }

  inbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "3306"
    description = "accept 3306 port"
  }

  outbound {
    protocol    = "TCP"
    ip_block    = "0.0.0.0/0"
    port_range  = "1-65535"
    description = "accept 1-65535 port"
  }
}
