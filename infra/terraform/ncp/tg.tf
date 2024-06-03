# Target Group 생성
resource "ncloud_lb_target_group" "be_tg" {
  vpc_no      = ncloud_vpc.vpc.id
  protocol    = "HTTP"
  target_type = "VSVR"
  port        = 8080
  description = "target group for ncp"
  health_check {
    protocol       = "HTTP"
    http_method    = "GET"
    port           = 8080
    url_path       = "/" // todo fix
    cycle          = 30
    up_threshold   = 2
    down_threshold = 2
  }

  algorithm_type = "RR"
}

# Target Group Attachment 설정
resource "ncloud_lb_target_group_attachment" "be_tg_attachment" {
  target_group_no = ncloud_lb_target_group.be_tg.target_group_no
  target_no_list  = [ncloud_server.be_server.id]
}
