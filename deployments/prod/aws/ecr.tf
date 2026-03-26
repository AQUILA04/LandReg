resource "aws_ecr_repository" "landreg-backend-api" {
  name                 = "landreg-backend-api"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "landreg-afis-master" {
  name                 = "landreg-afis-master"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "landreg-afis-service" {
  name                 = "landreg-afis-service"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}
