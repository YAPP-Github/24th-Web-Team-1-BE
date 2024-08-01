# ECS Cluster
resource "aws_ecs_cluster" "ecs_cluster" {
  name = "${var.prefix}-ecs-cluster"
  configuration {
    execute_command_configuration {
      kms_key_id = aws_kms_key.kms_key.id
      logging    = "OVERRIDE"

      log_configuration {
        cloud_watch_encryption_enabled = true
        cloud_watch_log_group_name     = aws_cloudwatch_log_group.cloudwatch_log_group.name
      }

    }
  }
}

# Task Definition
resource "aws_ecs_task_definition" "ecs_task" {
  family                   = "${var.prefix}-ecs-task"
  network_mode             = "bridge"
  requires_compatibilities = ["EC2"]
  task_role_arn            = aws_iam_role.ecs_task_execution_role.arn
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn
  container_definitions = jsonencode(
    [
      {
        "name" : "${var.prefix}-container",
        "image" : "${aws_ecr_repository.ecr.repository_url}:latest",
        "cpu" : 0,
        "memory" : 512,
        "memoryReservation" : 256,
        "environment": [
          {
            "name": "spring.profiles.active",
            "value": "prd"
          }
        ],
        "essential" : true,
        "portMappings" : [
          {
            "containerPort" : 8080,
            "hostPort" : 0,
            "protocol" : "tcp"
          }
        ],
        "mountPoints" : [],
        "volumesFrom" : [],
        "secrets" : [
          {
            "name" : "TOKEN_SECRETKEY",
            "valueFrom" : "${aws_ssm_parameter.token_secret_key.name}"
          },
          {
            "name" : "ACCESS_TOKEN_VALIDTIME"
            "valueFrom" : "${aws_ssm_parameter.access_token_validtime.name}"
          },
          {
            "name" : "REFRESH_TOKEN_VALIDTIME",
            "valueFrom" : "${aws_ssm_parameter.refresh_token_validtime.name}"
          },
          {
            "name" : "CORS_PATH_PATTERNS",
            "valueFrom" : "${aws_ssm_parameter.cors_path_patterns.name}"
          },
          {
            "name" : "CORS_ORIGIN_PATTERNS",
            "valueFrom" : "${aws_ssm_parameter.cors_origin_patterns.name}"
          },
          {
            "name" : "CORS_ALLOWED_METHODS",
            "valueFrom" : "${aws_ssm_parameter.cors_allowed_methods.name}"
          },
          {
            "name" : "CORS_ALLOWED_HEADERS",
            "valueFrom" : "${aws_ssm_parameter.cors_allowed_headers.name}"
          },
          {
            "name" : "CORS_EXPOSED_HEADERS",
            "valueFrom" : "${aws_ssm_parameter.cors_exposed_headers.name}"
          },
          {
            "name" : "CORS_ALLOW_CREDENTIALS",
            "valueFrom" : "${aws_ssm_parameter.cors_allow_credentials.name}"
          },
          {
            "name" : "CORS_MAX_AGE",
            "valueFrom" : "${aws_ssm_parameter.cors_max_age.name}"
          },
          {
            "name" : "DB_HOSTNAME",
            "valueFrom" : "${aws_ssm_parameter.db_hostname.name}"
          },
          {
            "name" : "DB_USERNAME",
            "valueFrom" : "${aws_ssm_parameter.db_username.name}"
          },
          {
            "name" : "DB_PASSWORD",
            "valueFrom" : "${aws_ssm_parameter.db_password.name}"
          },
          {
            "name" : "STORAGE_URL",
            "valueFrom" : "${aws_ssm_parameter.storage_url.name}"
          },
          {
            "name" : "STORAGE_ACCESS_KEY",
            "valueFrom" : "${aws_ssm_parameter.storage_access_key.name}"
          },
          {
            "name" : "STORAGE_SECRET_KEY",
            "valueFrom" : "${aws_ssm_parameter.storage_secret_key.name}"
          },
          {
            "name" : "IMAGE_STORE_BUCKET_NAME",
            "valueFrom" : "${aws_ssm_parameter.image_store_bucket_name.name}"
          },
          {
            "name": "DOCUMENT_STORE_BUCKET_NAME",
            "valueFrom": "${aws_ssm_parameter.document_store_bucket_name.name}"
          },
          {
            "name" : "STORAGE_REGION",
            "valueFrom" : "${aws_ssm_parameter.storage_region.name}"
          },
          {
              "name" : "EMAIL_USERNAME",
              "valueFrom" : "${aws_ssm_parameter.email_username.name}"
          },
          {
              "name" : "EMAIL_PASSWORD",
              "valueFrom" : "${aws_ssm_parameter.email_password.name}"
          },
          {
            "name" : "TIMEOUT_CONNECT",
            "valueFrom" : "${aws_ssm_parameter.timeout_connect.name}"
          },
          {
            "name" : "TIMEOUT_READ",
            "valueFrom" : "${aws_ssm_parameter.timeout_read.name}"
          },
          {
            "name": "WEBHOOK_DISCORD",
            "valueFrom": "${aws_ssm_parameter.webhook_discord.name}"
          },
          {
            "name" : "DISCORD_THREAD_POOL_CORE_POOL_SIZE",
            "valueFrom" : "${aws_ssm_parameter.discord_thread_pool_core_pool_size.name}"
          },
          {
            "name": "DISCORD_THREAD_POOL_MAX_POOL_SIZE",
            "valueFrom": "${aws_ssm_parameter.discord_thread_pool_max_pool_size.name}"
          },
          {
            "name": "DISCORD_THREAD_POOL_QUEUE_CAPACITY",
            "valueFrom": "${aws_ssm_parameter.discord_thread_pool_queue_capacity.name}"
          },
          {
            "name" : "DISCORD_THREAD_POOL_WAIT_FOR_TASKS_TO_COMPLETE_ON_SHUTDOWN",
            "valueFrom" : "${aws_ssm_parameter.discord_thread_pool_wait_for_tasks_to_complete_on_shutdown.name}"
          },
          {
            "name" : "DISCORD_THREAD_POOL_AWAIT_TERMINATION_SECONDS",
            "valueFrom" : "${aws_ssm_parameter.discord_thread_pool_await_termination_seconds.name}"
          },
          {
            "name": "CDN_URL",
            "valueFrom": "${aws_ssm_parameter.cdn_url.name}"
          }
        ],
        "logConfiguration" : {
          "logDriver" : "awslogs",
          "options" : {
            "awslogs-group" : "${aws_cloudwatch_log_group.cloudwatch_log_group.name}",
            "awslogs-region" : "${var.region}",
            "awslogs-stream-prefix" : "ecs"
          }
        }
      }
    ]
  )
}

# ALB
resource "aws_alb" "alb" {
  name                       = "${var.prefix}-alb"
  internal                   = false
  load_balancer_type         = "application"
  security_groups            = [aws_security_group.alb_sg.id]
  subnets                    = [aws_subnet.public_a.id, aws_subnet.public_c.id]
  enable_deletion_protection = false
}

# ALB Target Group
resource "aws_alb_target_group" "alb_target_group" {
  name        = "${var.prefix}-alb-tg"
  port        = 80
  protocol    = "HTTP"
  vpc_id      = aws_vpc.vpc.id
  target_type = "instance"
  health_check {
    path                = "/actuator/health"
    protocol            = "HTTP"
    port                = "traffic-port"
    interval            = 300
    timeout             = 120
    healthy_threshold   = 2
    unhealthy_threshold = 2
  }
}

# ALB Listener
resource "aws_alb_listener" "http_alb_listener" {
  load_balancer_arn = aws_alb.alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.alb_target_group.arn
  }
}

resource "aws_alb_listener" "https_alb_listenser" {
  load_balancer_arn = aws_alb.alb.arn
  port              = 443
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate.cert.arn

  default_action {
    type             = "forward"
    target_group_arn = aws_alb_target_group.alb_target_group.arn
  }
}

resource "aws_lb_listener_certificate" "https_listener_cert" {
  listener_arn    = aws_alb_listener.https_alb_listenser.arn
  certificate_arn = aws_acm_certificate.cert.arn
}

# ECS Service
resource "aws_ecs_service" "ecs_service" {
  name            = "${var.prefix}-ecs-service"
  cluster         = aws_ecs_cluster.ecs_cluster.id
  task_definition = aws_ecs_task_definition.ecs_task.arn
  desired_count   = 1
  launch_type     = "EC2"
  load_balancer {
    target_group_arn = aws_alb_target_group.alb_target_group.arn
    container_name   = "${var.prefix}-container"
    container_port   = 8080
  }
}
