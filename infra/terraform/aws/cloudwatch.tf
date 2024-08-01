# KMS Key
resource "aws_kms_key" "kms_key" {
  description             = "${var.prefix}-kms-key"
  deletion_window_in_days = 7
}

# Cloudwatch Log Group
resource "aws_cloudwatch_log_group" "cloudwatch_log_group" {
  name = "${var.prefix}-cloudwatch-log-group"
}
