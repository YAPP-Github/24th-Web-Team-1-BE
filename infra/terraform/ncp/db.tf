# MySQL 생성
resource "ncloud_mysql" "mysql" {
  user_name          = var.rds_username
  user_password      = var.rds_password
  host_ip            = ncloud_public_ip.be_public_ip.public_ip
  database_name      = "${var.prefix}-db"
  service_name       = "mysql"
  server_name_prefix = "${var.prefix}-db"
  subnet_no          = ncloud_subnet.db_a.id
  data_storage_type  = "SSD"
  is_ha              = false
  is_backup          = false
  port               = 3306
}

