locals {
  name = "${var.project}-${var.environment}"
}

# Récupère dynamiquement les AZ disponibles dans la région -> on prend les 2 premières.
# (Évite de coder en dur "us-east-1a/b" : plus robuste si une AZ est indisponible.)
data "aws_availability_zones" "available" {
  state = "available"
}

# ─── VPC : le réseau privé isolé ───────────────────────────────
resource "aws_vpc" "this" {
  cidr_block           = var.vpc_cidr
  enable_dns_support   = true # résolution DNS interne
  enable_dns_hostnames = true # noms DNS pour les instances (requis par RDS, ALB...)

  tags = { Name = "${local.name}-vpc" }
}

# ─── Internet Gateway : la porte vers Internet ─────────────────
resource "aws_internet_gateway" "this" {
  vpc_id = aws_vpc.this.id
  tags   = { Name = "${local.name}-igw" }
}

# ─── Subnets PUBLICS (1 par AZ) : pour l'ALB ───────────────────
resource "aws_subnet" "public" {
  count                   = length(var.public_subnet_cidrs)
  vpc_id                  = aws_vpc.this.id
  cidr_block              = var.public_subnet_cidrs[count.index]
  availability_zone       = data.aws_availability_zones.available.names[count.index]
  map_public_ip_on_launch = true # IP publique auto -> rend le subnet "public"

  tags = {
    Name = "${local.name}-public-${count.index + 1}"
    Tier = "public"
  }
}

# ─── Subnets PRIVÉS (1 par AZ) : pour ECS Fargate + RDS ────────
resource "aws_subnet" "private" {
  count             = length(var.private_subnet_cidrs)
  vpc_id            = aws_vpc.this.id
  cidr_block        = var.private_subnet_cidrs[count.index]
  availability_zone = data.aws_availability_zones.available.names[count.index]

  tags = {
    Name = "${local.name}-private-${count.index + 1}"
    Tier = "private"
  }
}

# ─── Route table PUBLIQUE : tout le trafic sortant -> IGW ──────
resource "aws_route_table" "public" {
  vpc_id = aws_vpc.this.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.this.id
  }

  tags = { Name = "${local.name}-rt-public" }
}

resource "aws_route_table_association" "public" {
  count          = length(aws_subnet.public)
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}

# ─── NAT Gateway : sortie Internet pour les subnets privés ─────
# Permet à Fargate (subnets privés) de pull son image depuis ECR.
# Un seul NAT (dans 1 AZ) partagé par les 2 subnets privés = moins cher.
resource "aws_eip" "nat" {
  domain = "vpc"
  tags   = { Name = "${local.name}-nat-eip" }
}

resource "aws_nat_gateway" "this" {
  allocation_id = aws_eip.nat.id
  subnet_id     = aws_subnet.public[0].id # le NAT vit dans un subnet PUBLIC
  tags          = { Name = "${local.name}-nat" }
  depends_on    = [aws_internet_gateway.this]
}

# ─── Route table PRIVÉE : sortie 0.0.0.0/0 -> NAT ──────────────
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.this.id
  tags   = { Name = "${local.name}-rt-private" }
}

resource "aws_route" "private_nat" {
  route_table_id         = aws_route_table.private.id
  destination_cidr_block = "0.0.0.0/0"
  nat_gateway_id         = aws_nat_gateway.this.id
}

resource "aws_route_table_association" "private" {
  count          = length(aws_subnet.private)
  subnet_id      = aws_subnet.private[count.index].id
  route_table_id = aws_route_table.private.id
}
