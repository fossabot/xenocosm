resource "aws_ecs_cluster" "main" {
  name = "xenocosm"

  tags {
    Name    = "Xenocosm ECS"
    Project = "Xenocosm"
  }
}

resource "aws_iam_role" "ecs-fleet" {
  name = "xenocosm-fleet"

  assume_role_policy = <<EOF
{
 "Version": "2012-10-17",
 "Statement": [
   {
     "Action": "sts:AssumeRole",
     "Principal": {
       "Service": "spotfleet.amazonaws.com"
     },
     "Effect": "Allow",
     "Sid": ""
   }
 ]
}
EOF

  tags {
    Name    = "Xenocosm Fleet Role"
    Project = "Xenocosm"
  }
}

resource "aws_iam_policy" "ecs-fleet" {
  name = "xenocosm-fleet"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ec2:Describe*",
                "ec2:RequestSpotInstances",
                "ec2:TerminateInstances",
                "iam:PassRole"
            ],
            "Resource": "*"
        }
    ]
}
EOF
}

resource "aws_iam_policy_attachment" "ecs-fleet" {
  name       = "xenocosm-fleet"
  policy_arn = "${aws_iam_policy.ecs-fleet.arn}"
  roles      = ["${aws_iam_role.ecs-fleet.name}"]
}

resource "aws_iam_role" "ecs-instance" {
  name = "xenocosm-instance"
  path = "/"

  assume_role_policy = <<EOF
{
    "Version": "2008-10-17",
    "Statement": [
      {
        "Action": "sts:AssumeRole",
        "Principal": {
          "Service": "ec2.amazonaws.com"
        },
        "Effect": "Allow",
        "Sid": ""
      }
    ]
}
EOF

  tags {
    Name    = "Xenocosm Instance Role"
    Project = "Xenocosm"
  }
}

resource "aws_iam_policy" "ecs-instance" {
  name = "xenocosm-instance"

  policy = <<EOF
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ecs:CreateCluster",
                "ecs:DiscoverPollEndpoint",
                "ecs:Poll",
                "ecs:StartTelemetrySession",
                "ecs:UpdateContainerInstancesState",
                "ecr:GetAuthorizationToken",
                "ecr:BatchCheckLayerAvailability",
                "ecr:GetDownloadUrlForLayer",
                "ecr:BatchGetImage",
                "logs:CreateLogStream",
                "logs:PutLogEvents"
            ],
            "Resource": "*"
        },
        {
            "Effect": "Allow",
            "Action": [
                "ecs:DeregisterContainerInstance",
                "ecs:RegisterContainerInstance",
                "ecs:Submit*"
            ],
            "Resource": "${aws_ecs_cluster.main.arn}"
        }
    ]
}
EOF
}

resource "aws_iam_policy_attachment" "ecs-instance" {
  name       = "xenocosm-instance"
  roles      = ["${aws_iam_role.ecs-instance.name}"]
  policy_arn = "${aws_iam_policy.ecs-instance.arn}"
}

resource "aws_iam_instance_profile" "ecs-instance" {
  name  = "xenocosm-instance"
  role = "${aws_iam_role.ecs-instance.name}"
}

resource "aws_security_group" "ecs-instance" {
  name   = "xenocosm-instance"
  vpc_id = "${aws_vpc.main.id}"

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Name    = "Xenocosm Instance SG"
    Project = "Xenocosm"
  }
}

resource "aws_spot_fleet_request" "ecs-fleet" {
  iam_fleet_role                      = "${aws_iam_role.ecs-fleet.arn}"
  allocation_strategy                 = "diversified"
  target_capacity                     = 1
  wait_for_fulfillment                = false
  replace_unhealthy_instances         = true
  terminate_instances_with_expiration = true
  valid_until                         = "2030-01-01T00:00:00Z"

  launch_specification {
    instance_type          = "${local.ecs-instance}"
    ami                    = "${local.ecs-ami}"
    key_name               = "${var.key-name}"
    availability_zone      = "${element(var.availability-zones, 0)}"
    subnet_id              = "${element(aws_subnet.public.*.id, 0)}"
    iam_instance_profile   = "${aws_iam_instance_profile.ecs-instance.name}"
    vpc_security_group_ids = ["${aws_security_group.ecs-instance.id}"]

    user_data = <<USER_DATA
#!/bin/bash
echo ECS_CLUSTER=${aws_ecs_cluster.main.name} >> /etc/ecs/ecs.config
USER_DATA

    tags {
      Name    = "Xenocosm Instance"
      Project = "Xenocosm"
    }
  }

  launch_specification {
    instance_type          = "${local.ecs-instance}"
    ami                    = "${local.ecs-ami}"
    key_name               = "${var.key-name}"
    availability_zone      = "${element(var.availability-zones, 1)}"
    subnet_id              = "${element(aws_subnet.public.*.id, 1)}"
    iam_instance_profile   = "${aws_iam_instance_profile.ecs-instance.name}"
    vpc_security_group_ids = ["${aws_security_group.ecs-instance.id}"]

    user_data = <<USER_DATA
#!/bin/bash
echo ECS_CLUSTER=${aws_ecs_cluster.main.name} >> /etc/ecs/ecs.config
USER_DATA

    tags {
      Name    = "Xenocosm Instance"
      Project = "Xenocosm"
    }
  }

  launch_specification {
    instance_type          = "${local.ecs-instance}"
    ami                    = "${local.ecs-ami}"
    key_name               = "${var.key-name}"
    availability_zone      = "${element(var.availability-zones, 2)}"
    subnet_id              = "${element(aws_subnet.public.*.id, 2)}"
    iam_instance_profile   = "${aws_iam_instance_profile.ecs-instance.name}"
    vpc_security_group_ids = ["${aws_security_group.ecs-instance.id}"]

    user_data = <<USER_DATA
#!/bin/bash
echo ECS_CLUSTER=${aws_ecs_cluster.main.name} >> /etc/ecs/ecs.config
USER_DATA

    tags {
      Name    = "Xenocosm Instance"
      Project = "Xenocosm"
    }
  }
}
