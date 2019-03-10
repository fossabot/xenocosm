terraform {
  backend "s3" {
    bucket = "robotsnowfall.tf"
    key    = "xenocosm"
    region = "us-east-2"
  }
}

provider "aws" {
  region = "us-east-2"
}

data "aws_region" "current" {}

variable "version" {
  description = "The Xenocosm version to deploy."
  type        = "string"
}

variable "key-name" {
  description = "The key pair name used to launch ECS instances"
  type        = "string"
}

variable "availability-zones" {
  description = "A list of availability zones in the region"
  type        = "list"
  default     = ["us-east-2a", "us-east-2b", "us-east-2c"]
}

variable "public-subnets" {
  description = "A list of public subnets inside the VPC."
  type        = "list"
  default     = ["10.0.1.0/24", "10.0.2.0/24", "10.0.3.0/24"]
}

locals {
  ecs-ami      = "ami-0aa9ee1fc70e57450"
  ecs-instance = "t3.nano"
}
