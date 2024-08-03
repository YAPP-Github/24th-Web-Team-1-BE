# Launch Configuration
resource "aws_launch_configuration" "lc" {
  name_prefix                 = "${var.prefix}-lc-"
  image_id                    = var.lc_image_id
  instance_type               = var.instance_type
  iam_instance_profile        = aws_iam_instance_profile.ecs_instance_profile.name
  security_groups             = [aws_security_group.application_sg.id]
  associate_public_ip_address = true
  key_name                    = aws_key_pair.kp.key_name
  root_block_device {
    volume_size = 30
    volume_type = "gp2"
  }
  lifecycle {
    create_before_destroy = true
  }
  user_data = <<EOF
#!/bin/bash
echo ECS_CLUSTER=${aws_ecs_cluster.ecs_cluster.name} >> /etc/ecs/ecs.config; echo ECS_BACKEND_HOST= >> /etc/ecs/ecs.config;
export PATH=/usr/local/bin:$PATH
yum -y install jq
yum install -y awscli
EOF
}

# Autoscaling Group
resource "aws_autoscaling_group" "asg" {
  name = "${var.prefix}-asg"
  vpc_zone_identifier = [
    aws_subnet.public_a.id,
    aws_subnet.public_c.id
  ]
  launch_configuration = aws_launch_configuration.lc.name
  min_size             = 1
  max_size             = 1
  tag {
    key                 = "Name"
    value               = "${var.prefix}-asg"
    propagate_at_launch = true
  }
  target_group_arns = [aws_alb_target_group.alb_target_group.arn]
}
