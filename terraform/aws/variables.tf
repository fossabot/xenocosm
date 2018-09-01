variable "project" {
  description = "The Xenocosm project name. Will be used as a tag."
  type        = "string"
  default     = "Xenocosm"
}

variable "aws_region" {
  description = "The region to build in"
  type        = "string"
  default     = "us-east-2"
}

variable "azs" {
  description = "A list of Availability zones in the region"
  type        = "list"
  default     = ["us-east-2a"]
}

variable "public_subnets" {
  description = "A list of public subnets inside the VPC."
  type        = "list"
  default     = ["10.0.1.0/24"]
}
