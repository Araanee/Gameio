locals {
  name = "${var.project}-${var.environment}-monitoring"
}

# AMI Amazon Linux 2023 la plus récente (via SSM Parameter Store public).
data "aws_ssm_parameter" "al2023" {
  name = "/aws/service/ami-amazon-linux-latest/al2023-ami-kernel-default-x86_64"
}

# ─── Security Group : SSH + Grafana ────────────────────────────
resource "aws_security_group" "monitoring" {
  name        = "${local.name}-sg"
  description = "EC2 monitoring : SSH + Grafana"
  vpc_id      = var.vpc_id

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = [var.ssh_cidr]
  }

  ingress {
    description = "Grafana UI"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name}-sg" }
}

# ─── EC2 monitoring ────────────────────────────────────────────
resource "aws_instance" "monitoring" {
  ami                         = data.aws_ssm_parameter.al2023.value
  instance_type               = var.instance_type
  subnet_id                   = var.public_subnet_id
  associate_public_ip_address = true
  key_name                    = var.key_name
  iam_instance_profile        = var.instance_profile_name
  vpc_security_group_ids      = [aws_security_group.monitoring.id]

  tags = { Name = local.name }
}
