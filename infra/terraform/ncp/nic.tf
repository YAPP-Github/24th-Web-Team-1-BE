# Network Interface 생성
resource "ncloud_network_interface" "be_server_nic" {
  name                  = "${var.prefix}-be-server-nic"
  description           = "Backend NIC"
  subnet_no             = ncloud_subnet.public_a.subnet_no
  access_control_groups = [ncloud_access_control_group.be_server.id]
}
