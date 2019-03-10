resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  tags {
    Name    = "Xenocosm VPC"
    Project = "Xenocosm"
  }
}

resource "aws_default_security_group" "default" {
  vpc_id = "${aws_vpc.main.id}"

  ingress {
    protocol  = -1
    self      = true
    from_port = 0
    to_port   = 0
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags {
    Name    = "Xenocosm SG"
    Project = "Xenocosm"
  }
}

resource "aws_default_route_table" "default" {
  default_route_table_id = "${aws_vpc.main.default_route_table_id}"

  tags {
    Name    = "Xenocosm VPC Route Table"
    Project = "Xenocosm"
  }
}

resource "aws_default_network_acl" "default" {
  default_network_acl_id = "${aws_vpc.main.default_network_acl_id}"
  subnet_ids = ["${aws_subnet.public.*.id}"]

  egress {
    rule_no    = 100
    protocol   = "-1"
    from_port  = 0
    to_port    = 0
    cidr_block = "0.0.0.0/0"
    action     = "allow"
  }

  ingress {
    rule_no    = 100
    protocol   = "-1"
    from_port  = 0
    to_port    = 0
    cidr_block = "0.0.0.0/0"
    action     = "allow"
  }

  tags {
    Name    = "Xenocosm Network ACL"
    Project = "Xenocosm"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = "${aws_vpc.main.id}"

  tags {
    Name    = "Xenocosm IGW"
    Project = "Xenocosm"
  }
}
