resource "aws_subnet" "public" {
  count                   = "${length(var.availability-zones)}"
  vpc_id                  = "${aws_vpc.main.id}"
  cidr_block              = "${var.public-subnets[count.index]}"
  availability_zone       = "${var.availability-zones[count.index]}"
  map_public_ip_on_launch = true

  tags {
    Name    = "Xenocosm Public ${count.index}"
    Project = "Xenocosm"
  }

  lifecycle {
    ignore_changes = "tags.Created"
  }
}

resource "aws_route_table" "public" {
  count  = "${length(var.availability-zones)}"
  vpc_id = "${aws_vpc.main.id}"

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = "${aws_internet_gateway.main.id}"
  }

  tags {
    Name    = "Xenocosm RT ${count.index}"
    Project = "Xenocosm"
  }
}

resource "aws_route_table_association" "public" {
  count          = "${length(var.availability-zones)}"
  subnet_id      = "${element(aws_subnet.public.*.id, count.index)}"
  route_table_id = "${element(aws_route_table.public.*.id, count.index)}"
}
