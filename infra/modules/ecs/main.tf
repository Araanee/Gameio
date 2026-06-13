locals {
  name           = "${var.project}-${var.environment}"
  container_name = "backend"
}

# LabRole : on ne peut pas créer de rôle IAM custom en Learner Lab.
# On récupère LabRole et on l'utilise comme execution + task role.
data "aws_iam_role" "lab" {
  name = "LabRole"
}

# ─── ECR : registre de l'image backend ────────────────────────
resource "aws_ecr_repository" "backend" {
  name                 = "${local.name}-backend"
  image_tag_mutability = "MUTABLE"
  force_delete         = true # facilite le terraform destroy en dev

  image_scanning_configuration {
    scan_on_push = true # scan de vulnérabilités à chaque push (bonus sécu)
  }

  tags = { Name = "${local.name}-backend" }
}

# ─── Logs CloudWatch pour le conteneur ─────────────────────────
resource "aws_cloudwatch_log_group" "backend" {
  name              = "/ecs/${local.name}-backend"
  retention_in_days = 7
  tags              = { Name = "${local.name}-backend-logs" }
}

# ─── Security Group de l'ALB : 80 ouvert depuis Internet ───────
resource "aws_security_group" "alb" {
  name        = "${local.name}-alb-sg"
  description = "ALB : HTTP entrant depuis Internet"
  vpc_id      = var.vpc_id

  ingress {
    description = "HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${local.name}-alb-sg" }
}

# ─── Security Group des tâches : 8080 depuis l'ALB UNIQUEMENT ──
resource "aws_security_group" "ecs" {
  name        = "${local.name}-ecs-sg"
  description = "Taches Fargate : trafic applicatif depuis l'ALB"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Depuis l'ALB"
    from_port       = var.container_port
    to_port         = var.container_port
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"] # sortie vers RDS + NAT (pull image, BGG API...)
  }

  tags = { Name = "${local.name}-ecs-sg" }
}

# ─── Application Load Balancer (subnets publics) ───────────────
resource "aws_lb" "this" {
  name               = "${local.name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.alb.id]
  subnets            = var.public_subnet_ids

  tags = { Name = "${local.name}-alb" }
}

resource "aws_lb_target_group" "backend" {
  name        = "${local.name}-backend-tg"
  port        = var.container_port
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip" # Fargate (awsvpc) = enregistrement par IP

  health_check {
    path                = "/actuator/health"
    matcher             = "200"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 2
    unhealthy_threshold = 3
  }

  tags = { Name = "${local.name}-backend-tg" }
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.this.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.backend.arn
  }
}

# ─── ECS Cluster + Task Definition + Service ───────────────────
resource "aws_ecs_cluster" "this" {
  name = "${local.name}-cluster"
  tags = { Name = "${local.name}-cluster" }
}

resource "aws_ecs_task_definition" "backend" {
  family                   = "${local.name}-backend"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = var.cpu
  memory                   = var.memory
  execution_role_arn       = data.aws_iam_role.lab.arn
  task_role_arn            = data.aws_iam_role.lab.arn

  container_definitions = jsonencode([
    {
      name      = local.container_name
      image     = "${aws_ecr_repository.backend.repository_url}:${var.image_tag}"
      essential = true
      portMappings = [
        { containerPort = var.container_port, protocol = "tcp" }
      ]
      environment = [
        { name = "SPRING_PROFILES_ACTIVE", value = "aws" },
        { name = "SPRING_DATASOURCE_URL", value = "jdbc:postgresql://${var.db_address}:5432/${var.db_name}" },
        { name = "SPRING_DATASOURCE_USERNAME", value = var.db_username },
        { name = "SPRING_DATASOURCE_PASSWORD", value = var.db_password }
      ]
      logConfiguration = {
        logDriver = "awslogs"
        options = {
          "awslogs-group"         = aws_cloudwatch_log_group.backend.name
          "awslogs-region"        = var.aws_region
          "awslogs-stream-prefix" = "backend"
        }
      }
    }
  ])

  tags = { Name = "${local.name}-backend" }
}

resource "aws_ecs_service" "backend" {
  name            = "${local.name}-backend"
  cluster         = aws_ecs_cluster.this.id
  task_definition = aws_ecs_task_definition.backend.arn
  desired_count   = var.desired_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = var.private_subnet_ids
    security_groups  = [aws_security_group.ecs.id]
    assign_public_ip = false # tâches privées, sortie via NAT
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.backend.arn
    container_name   = local.container_name
    container_port   = var.container_port
  }

  # Laisse le temps à Spring Boot de démarrer avant les health checks
  health_check_grace_period_seconds = 120

  depends_on = [aws_lb_listener.http]

  tags = { Name = "${local.name}-backend" }
}
