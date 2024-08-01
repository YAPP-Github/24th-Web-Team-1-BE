# IAM User
resource "aws_iam_user" "user" {
  name = "${var.prefix}-user"
  path = "/"
}

data "aws_iam_policy_document" "policy_docs" {
  version = "2012-10-17"
  statement {
    sid    = "VisualEditor0"
    effect = "Allow"
    actions = [
      "ecs:SubmitTaskStateChange",
      "ecs:UpdateCluster",
      "ecs:UpdateClusterSettings",
      "ecs:ExecuteCommand",
      "ecs:StartTask",
      "ecs:UpdateCapacityProvider",
      "ecs:DeleteCluster",
      "ecs:RegisterContainerInstance",
      "ecs:DescribeClusters",
      "ecs:DeleteCapacityProvider",
      "ecs:SubmitAttachmentStateChanges",
      "ecs:DescribeCapacityProviders",
      "ecs:ListTagsForResource",
      "ecs:RunTask",
      "ecs:SubmitContainerStateChange",
      "ecs:StopTask",
      "ecs:DeregisterContainerInstance",
      "ecs:DescribeTasks",
      "ecs:PutClusterCapacityProviders"
    ]
    resources = [
      "arn:aws:ecs:*:629229556301:capacity-provider/*",
      "arn:aws:ecs:*:629229556301:task/*/*",
      "arn:aws:ecs:*:629229556301:task-definition/*:*",
      "arn:aws:ecs:*:629229556301:cluster/*"
    ]
  }

  statement {
    sid    = "VisualEditor1"
    effect = "Allow"
    actions = [
      "ecs:DeregisterTaskDefinition",
      "ecs:CreateCapacityProvider",
      "ecs:DiscoverPollEndpoint",
      "ecs:ListAccountSettings",
      "ecs:PutAccountSettingDefault",
      "ecs:DeleteAccountSetting",
      "ecs:CreateCluster",
      "ecs:RegisterTaskDefinition",
      "ecs:DescribeTaskDefinition",
      "ecs:CreateTaskSet",
      "ecs:PutAccountSetting",
      "iam:GetRole",
      "iam:PassRole"
    ]
    resources = ["*"]

  }
}

resource "aws_iam_user_policy" "user_policy" {
  name   = "${var.prefix}-user-policy"
  user   = aws_iam_user.user.name
  policy = data.aws_iam_policy_document.policy_docs.json
}

# ECS Instance Role
resource "aws_iam_role" "ecs_instance_role" {
  name = "${var.prefix}-ecs-instance-role"
  assume_role_policy = jsonencode({
    "Version" : "2008-10-17",
    "Statement" : [
      {
        "Sid" : "",
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "ec2.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

variable "ecs_instance_role_attach_targets" {
  type = list(string)
  default = [
    "arn:aws:iam::aws:policy/service-role/AmazonEC2ContainerServiceforEC2Role",
    "arn:aws:iam::aws:policy/AmazonEC2FullAccess",
    "arn:aws:iam::aws:policy/AmazonECS_FullAccess",
    "arn:aws:iam::aws:policy/AmazonSSMFullAccess",
    "arn:aws:iam::aws:policy/ElasticLoadBalancingFullAccess"
  ]
}

resource "aws_iam_role_policy_attachment" "ecs_instance_role_attach" {
  count      = length(var.ecs_instance_role_attach_targets)
  policy_arn = var.ecs_instance_role_attach_targets[count.index]
  role       = aws_iam_role.ecs_instance_role.name
}

resource "aws_iam_instance_profile" "ecs_instance_profile" {
  name = "${var.prefix}-ecs-instance-profile"
  role = aws_iam_role.ecs_instance_role.name
}


# ECS Service Role
resource "aws_iam_role" "ecs_task_execution_role" {
  name = "${var.prefix}-ecs-task-execution-role"
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "",
        "Effect" : "Allow",
        "Principal" : {
          "Service" : "ecs-tasks.amazonaws.com"
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })
}

variable "ecs_task_execution_role_attach_targets" {
  type = list(string)
  default = [
    "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy",
    "arn:aws:iam::aws:policy/CloudWatchEventsFullAccess",
    "arn:aws:iam::aws:policy/AmazonECS_FullAccess",
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
    "arn:aws:iam::aws:policy/AmazonSSMFullAccess",
    "arn:aws:iam::aws:policy/AWSCodeDeployRoleForECS",
    "arn:aws:iam::aws:policy/CloudWatchFullAccess",
    "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess",
    "arn:aws:iam::aws:policy/SecretsManagerReadWrite"
  ]
}

resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_attach" {
  count      = length(var.ecs_task_execution_role_attach_targets)
  policy_arn = var.ecs_task_execution_role_attach_targets[count.index]
  role       = aws_iam_role.ecs_task_execution_role.name
}

resource "aws_iam_role_policy" "ecs_task_execution_role_ssm" {
  name = "${var.prefix}-ecs-task-execution-role-ssm"
  role = aws_iam_role.ecs_task_execution_role.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "ssm:GetParameters",
          "ssm:GetParameter",
          "ssm:GetParametersByPath",
          "ssm:DescribeParameters"
        ],
        "Resource" : "*"
      }
    ]
  })
}

resource "aws_iam_role_policy" "ecs_task_execution_role_policy" {
  name = "${var.prefix}-ecs-task-execution-role-policy"
  role = aws_iam_role.ecs_task_execution_role.name
  policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Effect" : "Allow",
        "Action" : [
          "iam:PassRole"
        ],
        "Resource" : "${var.iam_root_arn}"
      }
    ]
  })
}
