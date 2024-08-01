# RDS
resource "aws_db_instance" "rds" {
  identifier             = "${var.prefix}-rds"
  db_name                = "${var.prefix}_${var.rds_db_name}"
  allocated_storage      = var.rds_allocated_storage
  engine                 = var.rds_engine
  engine_version         = var.rds_engine_version
  instance_class         = var.rds_instance_class
  username               = var.rds_username
  password               = var.rds_password
  skip_final_snapshot    = true
  db_subnet_group_name   = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [aws_security_group.rds_sg.id]
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name       = "${var.prefix}-rds-subnet-group"
  subnet_ids = [aws_subnet.private_a.id, aws_subnet.private_c.id]
}
