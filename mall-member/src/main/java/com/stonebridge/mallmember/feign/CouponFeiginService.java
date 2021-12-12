package com.stonebridge.mallmember.feign;

import com.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient("mall-coupon")
public interface CouponFeiginService {
    @RequestMapping("mallcoupon/coupon/member/list")
    public Result membercoupons();
}
