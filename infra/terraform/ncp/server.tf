# SSH Key 생성
resource "ncloud_login_key" "be_key" {
  key_name = "${var.prefix}-be-key"
}

# SSH Key 파일 생성
resource "local_file" "be_key" {
  filename = "${var.prefix}_be.pem"
  content  = ncloud_login_key.be_key.private_key
}

# Server 생성
resource "ncloud_server" "be_server" {
  subnet_no                 = ncloud_subnet.public_a.id
  name                      = "${var.prefix}-be-server"
  server_image_product_code = data.ncloud_server_image.image.product_code
  server_product_code       = data.ncloud_server_product.product.product_code
  login_key_name            = ncloud_login_key.be_key.key_name
  network_interface {
    order                = 0
    network_interface_no = ncloud_network_interface.be_server_nic.id
  }
}

# Server에 public ip 할당
resource "ncloud_public_ip" "be_public_ip" {
  server_instance_no = ncloud_server.be_server.id
}

# ubuntu 20.04 이미지 정보
data "ncloud_server_image" "image" {
  product_code = "SW.VSVR.OS.LNX64.UBNTU.SVR2004.B050"
}

# Server 스펙 정보
data "ncloud_server_product" "product" {
  server_image_product_code = data.ncloud_server_image.image.product_code
  filter {
    name   = "product_code"
    values = ["SSD"]
    regex  = true
  }

  filter {
    name   = "cpu_count"
    values = ["2"]
  }

  filter {
    name   = "memory_size"
    values = ["8GB"]
  }

  filter {
    name   = "base_block_storage_size"
    values = ["50GB"]
  }

  filter {
    name   = "product_type"
    values = ["STAND"]
  }
}

# Server root password 정보
data "ncloud_root_password" "be_root_password" {
  server_instance_no = ncloud_server.be_server.id
  private_key        = ncloud_login_key.be_key.private_key
}

resource "local_file" "be_root_password" {
  filename = "${var.prefix}_be_root_password.txt"
  content  = data.ncloud_root_password.be_root_password.root_password
}

# Server init script
## 임시 init script / nginx 설치 및 실행
resource "ncloud_init_script" "be_init_script" {
  name    = "${var.prefix}-be-init-script"
  content = <<EOF
#!/bin/bash
apt update -y
apt install docker.io -y
systemctl enable docker
systemctl start docker
docker run -d -p 8080:80 --name nginx nginx
EOF
}
