resource "aws_cloudwatch_log_group" "http" {
  name = "xenocosm/http"

  tags {
    Name    = "Xenocosm HTTP Logs"
    Project = "Xenocosm"
  }
}

resource "aws_iam_role" "http" {
  name = "xenocosm-http"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF

  tags {
    Name    = "Xenocosm HTTP"
    Project = "Xenocosm"
  }
}


resource "aws_iam_policy" "http" {
  name = "xenocosm-http"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Effect": "Allow",
      "Resource": "${aws_cloudwatch_log_group.http.arn}"
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "http" {
  role       = "${aws_iam_role.http.name}"
  policy_arn = "${aws_iam_policy.http.arn}"
}

resource "aws_ecs_task_definition" "http" {
  family                   = "xenocosm-http"
  network_mode             = "host"
  requires_compatibilities = ["EC2"]
  execution_role_arn       = "${aws_iam_role.http.arn}"
  task_role_arn            = "${aws_iam_role.http.arn}"

  container_definitions = <<EOF
    [
      {
        "image": "robotsnowfall/xenocosm-http:${var.version}",
        "name": "xenocosm-http",
        "memoryReservation": 128,
        "networkMode": "host",
        "portMappings": [
          {
            "containerPort": 8080,
            "hostPort": 8080
          }
        ],
        "logConfiguration": {
          "logDriver": "awslogs",
          "options": {
            "awslogs-group": "${aws_cloudwatch_log_group.http.name}",
            "awslogs-region": "${data.aws_region.current.name}"
          }
        }
      }
    ]
EOF

  tags {
    Name    = "Xenocosm HTTP Task"
    Project = "Xenocosm"
  }
}

resource "aws_ecs_service" "http" {
  name            = "xenocosm-http"
  cluster         = "${aws_ecs_cluster.main.id}"
  task_definition = "${aws_ecs_task_definition.http.arn}"
  desired_count   = 1
  launch_type     = "EC2"
  depends_on      = ["aws_iam_policy.http"]

  lifecycle {
    ignore_changes = "desired_count"
  }

  tags {
    Name    = "Xenocosm HTTP Service"
    Project = "Xenocosm"
  }
}
