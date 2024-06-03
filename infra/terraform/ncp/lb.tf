# Load Balancer 생성
resource "ncloud_lb" "be_lb" {
  name           = "${var.prefix}-lb"
  network_type   = "PUBLIC"
  type           = "APPLICATION"
  subnet_no_list = [ncloud_subnet.lb_a.subnet_no]
}

# Load Balancer Target Group 설정
resource "ncloud_lb_listener" "be_lb_listener" {
  load_balancer_no = ncloud_lb.be_lb.load_balancer_no
  protocol         = "HTTP"
  port             = 80
  target_group_no  = ncloud_lb_target_group.be_tg.target_group_no
}
