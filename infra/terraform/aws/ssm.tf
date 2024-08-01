# System Manager Parameter Store
## TIMEOUT_CONNECT
resource "aws_ssm_parameter" "timeout_connect" {
  name        = "/timeout/connect"
  description = "Timeout connect"
  type        = "SecureString"
  value       = 5000
}

## TIMEOUT_READ
resource "aws_ssm_parameter" "timeout_read" {
  name        = "/timeout/read"
  description = "Timeout read"
  type        = "SecureString"
  value       = 5000
}

## WEBHOOK_DISCORD
resource "aws_ssm_parameter" "webhook_discord" {
  name        = "/webhook/discord"
  description = "Webhook discord"
  type        = "SecureString"
    value       = var.webhook_discord
}

## DISCORD_THREAD_POOL_CORE_POOL_SIZE
resource "aws_ssm_parameter" "discord_thread_pool_core_pool_size" {
  name        = "/discord/thread-pool/core-pool-size"
  description = "Discord thread pool core pool size"
  type        = "SecureString"
  value       = 5
}

## DISCORD_THREAD_POOL_MAX_POOL_SIZE
resource "aws_ssm_parameter" "discord_thread_pool_max_pool_size" {
  name        = "/discord/thread-pool/max-pool-size"
  description = "Discord thread pool max pool size"
  type        = "SecureString"
  value       = 15
}

## DISCORD_THREAD_POOL_QUEUE_CAPACITY
resource "aws_ssm_parameter" "discord_thread_pool_queue_capacity" {
  name        = "/discord/thread-pool/queue-capacity"
  description = "Discord thread pool queue capacity"
  type        = "SecureString"
  value       = 30
}

## DISCORD_THREAD_POOL_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN
resource "aws_ssm_parameter" "discord_thread_pool_wait_for_tasks_to_complete_on_shutdown" {
  name        = "/discord/thread-pool/wait-for-tasks-to-complete-on-shutdown"
  description = "Discord thread pool wait for tasks to complete on shutdown"
  type        = "SecureString"
  value       = "true"
}

## DISCORD_THREAD_POOL_AWAIT_TERMINATION_SECONDS
resource "aws_ssm_parameter" "discord_thread_pool_await_termination_seconds" {
  name        = "/discord/thread-pool/await-termination-seconds"
  description = "Discord thread pool await termination seconds"
  type        = "SecureString"
  value       = 60
}

## TOKEN_SECRETKEY
resource "aws_ssm_parameter" "token_secret_key" {
  name        = "/tokenSecretKey"
  description = "token secret key"
  type        = "SecureString"
  value       = "jwtsecretKeyhastolonghowlongidontknow"
}

## ACCESS_TOKEN_VALIDTIME
resource "aws_ssm_parameter" "access_token_validtime" {
  name        = "/access-token/validtime"
  description = "Access token validtime"
  type        = "SecureString"
  value       = 31557600000
}

## REFRESH_TOKEN_VALIDTIME
resource "aws_ssm_parameter" "refresh_token_validtime" {
  name        = "/refresh-token/validtime"
  description = "Refresh token validtime"
  type        = "SecureString"
  value       = 31557600000
}

## CORS_PATH_PATTERNS
resource "aws_ssm_parameter" "cors_path_patterns" {
  name        = "/cors/path-patterns"
  description = "CORS path patterns"
  type        = "SecureString"
  value       = "/**"
}

## CORS_ORIGIN_PATTERNS
resource "aws_ssm_parameter" "cors_origin_patterns" {
  name        = "/cors/origin-patterns"
  description = "CORS origin patterns"
  type        = "SecureString"
  value       = var.fe_origin
}

## CORS_ALLOWED_METHODS
resource "aws_ssm_parameter" "cors_allowed_methods" {
  name        = "/cors/allowed-methods"
  description = "CORS allowed methods"
  type        = "SecureString"
  value       = "*"
}

## CORS_ALLOWED_HEADERS
resource "aws_ssm_parameter" "cors_allowed_headers" {
  name        = "/cors/allowed-headers"
  description = "CORS allowed methods"
  type        = "SecureString"
  value       = "*"
}

## CORS_EXPOSED_HEADERS
resource "aws_ssm_parameter" "cors_exposed_headers" {
  name        = "/cors/exposed-headers"
  description = "CORS exposed headers"
  type        = "SecureString"
  value       = "Set-Cookie, Authorization, Content-Type, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers, Access-Control-Allow-Origin, Access-Control-Allow-Credentials"
}

## CORS_ALLOW_CREDENTIALS
resource "aws_ssm_parameter" "cors_allow_credentials" {
  name        = "/cors/allow-credentials"
  description = "CORS allow credentials"
  type        = "SecureString"
  value       = "true"
}

## CORS_MAX_AGE
resource "aws_ssm_parameter" "cors_max_age" {
  name        = "/cors/max-age"
  description = "CORS max age"
  type        = "SecureString"
  value       = "1800"
}

## DB_HOSTNAME
resource "aws_ssm_parameter" "db_hostname" {
  name        = "/db/hostname"
  description = "DB hostname"
  type        = "SecureString"
  value       = "jdbc:mysql://${aws_db_instance.rds.address}:${aws_db_instance.rds.port}"
}

## DB_USERNAME
resource "aws_ssm_parameter" "db_username" {
  name        = "/db/username"
  description = "DB username"
  type        = "SecureString"
  value       = aws_db_instance.rds.username
}

## DB_PASSWORD
resource "aws_ssm_parameter" "db_password" {
  name        = "/db/password"
  description = "DB password"
  type        = "SecureString"
  value       = aws_db_instance.rds.password
}

## EMAIL_USERNAME
resource "aws_ssm_parameter" "email_username" {
  name        = "/email/username"
  description = "Email username"
  type        = "SecureString"
  value       = var.email_username
}

## EMAIL_PASSWORD
resource "aws_ssm_parameter" "email_password" {
  name        = "/email/password"
  description = "Email password"
  type        = "SecureString"
    value       = var.email_password
}

## STORAGE_URL
resource "aws_ssm_parameter" "storage_url" {
  name        = "/s3/url"
  description = "S3 URL"
  type        = "SecureString"
  value       = "s3://${aws_s3_bucket.bucket.id}"
}

## STORAGE_ACCESS_KEY
resource "aws_ssm_parameter" "storage_access_key" {
  name        = "/s3/access-key"
  description = "S3 access key"
  type        = "SecureString"
  value       = var.access_key
}

## STORAGE_SECRET_KEY
resource "aws_ssm_parameter" "storage_secret_key" {
  name        = "/s3/secret-key"
  description = "S3 secret key"
  type        = "SecureString"
  value       = var.secret_key
}

## IMAGE_STORE_BUCKET_NAME
resource "aws_ssm_parameter" "image_store_bucket_name" {
  name        = "/s3/bucket-name"
  description = "S3 bucket name"
  type        = "SecureString"
  value       = aws_s3_bucket.bucket.bucket
}

## STORAGE_REGION
resource "aws_ssm_parameter" "storage_region" {
  name        = "/s3/region"
  description = "S3 region"
  type        = "SecureString"
  value       = var.region
}

## DOCUMENT_STORE_BUCKET_NAME
resource "aws_ssm_parameter" "document_store_bucket_name" {
  name        = "/s3/document-bucket-name"
  description = "S3 document bucket name"
  type        = "SecureString"
  value       = aws_s3_bucket.bucket.bucket
}

## CDN_URL
resource "aws_ssm_parameter" "cdn_url" {
  name        = "/cdn/url"
  description = "CDN URL"
  type        = "SecureString"
  value       = "https://${aws_cloudfront_distribution.s3_distribution.domain_name}"
}