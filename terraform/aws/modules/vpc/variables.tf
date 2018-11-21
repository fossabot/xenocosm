variable "project" {
  description = "The Xenocosm project name. Will be used as a tag."
  type        = "string"
}

variable "aws_region" {
  description = "The region to build in"
  type        = "string"
}

variable "azs" {
  description = "A list of Availability zones in the region"
  type        = "list"
}

variable "public_subnets" {
  description = "A list of public subnets inside the VPC."
  type        = "list"
}
