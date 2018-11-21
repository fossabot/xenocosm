module vpc {
  source         = "./modules/vpc"

  project        = "${var.project}"
  aws_region     = "${var.aws_region}"
  azs            = "${var.azs}"
  public_subnets = "${var.public_subnets}"
}

module ec2 {
  source     = "./modules/ec2"

  project    = "${var.project}"
  aws_region = "${var.aws_region}"
}

module ecs {
  source     = "./modules/ecs"

  project    = "${var.project}"
  aws_region = "${var.aws_region}"
}

module route53 {
  source     = "./modules/route53"

  project    = "${var.project}"
  aws_region = "${var.aws_region}"
}
