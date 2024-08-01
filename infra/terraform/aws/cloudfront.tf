locals {
  s3_domain_name = "${aws_s3_bucket.bucket.bucket}.s3.${var.region}.amazonaws.com"
}

resource "aws_cloudfront_origin_access_control" "s3_origin_access_control" {
  name                              = "s3-origin-access-control"
  description                       = "s3-origin-access-control"
  origin_access_control_origin_type = "s3"
  signing_behavior                  = "always"
  signing_protocol                  = "sigv4"
}

resource "aws_cloudfront_distribution" "s3_distribution" {
  origin {
    domain_name              = local.s3_domain_name
    origin_id                = local.s3_domain_name
    origin_access_control_id = aws_cloudfront_origin_access_control.s3_origin_access_control.id
  }

  enabled         = true
  is_ipv6_enabled = true

  default_cache_behavior {
    allowed_methods  = ["GET", "HEAD"]
    cached_methods   = ["GET", "HEAD"]
    target_origin_id = local.s3_domain_name
    cache_policy_id  = "4135ea2d-6df8-44a3-9df3-4b5a84be39ad"

    viewer_protocol_policy = "redirect-to-https"
    min_ttl                = 0
    default_ttl            = 3600
    max_ttl                = 86400
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }
}
