provider "aws" {
  region = "${var.aws_region}"
}

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  tags {
    Name    = "${var.project} VPC"
    Project = "${var.project}"
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
    Name    = "${var.project} SG"
    Project = "${var.project}"
  }
}

resource "aws_default_route_table" "default" {
  default_route_table_id = "${aws_vpc.main.default_route_table_id}"

  tags {
    Name    = "${var.project} Route Table"
    Project = "${var.project}"
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
    Name    = "${var.project} Network ACL"
    Project = "${var.project}"
  }
}

resource "aws_internet_gateway" "main" {
  vpc_id = "${aws_vpc.main.id}"

  tags {
    Name    = "${var.project} IGW"
    Project = "${var.project}"
  }
}

resource "aws_vpc_dhcp_options" "main" {
  domain_name         = "com.xenocosm.ec2"
  domain_name_servers = ["AmazonProvidedDNS"]

  tags {
    Name    = "${var.project} DHCP Options"
    Project = "${var.project}"
  }
}

resource "aws_vpc_dhcp_options_association" "main" {
  vpc_id          = "${aws_vpc.main.id}"
  dhcp_options_id = "${aws_vpc_dhcp_options.main.id}"
}

resource "aws_subnet" "public" {
  vpc_id                  = "${aws_vpc.main.id}"

  count                   = "${length(var.azs)}"
  cidr_block              = "${var.public_subnets[count.index]}"
  availability_zone       = "${var.azs[count.index]}"

  tags {
    Name    = "${var.project} Public ${count.index}"
    Project = "${var.project}"
  }

  lifecycle {
    ignore_changes = "tags.Created"
  }
}

resource "aws_eip" "nat_gateway_eip" {
  vpc   = "true"
  count = "${length(var.azs)}"

  tags {
    Name    = "${var.project} EIP ${count.index}"
    Project = "${var.project}"
  }
}

resource "aws_nat_gateway" "nat_gateway" {
  count         = "${length(var.azs)}"

  allocation_id = "${element(aws_eip.nat_gateway_eip.*.id, count.index)}"
  subnet_id     = "${element(aws_subnet.public.*.id, count.index)}"
  depends_on    = ["aws_internet_gateway.main"]

  tags {
    Name    = "${var.project} NAT GW ${count.index}"
    Project = "${var.project}"
  }
}
