package com.stonebridge.mallcoupon.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallcoupon.entity.CouponEntity;
import com.stonebridge.mallcoupon.service.CouponService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 优惠券信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 10:50:52
 */
@RestController
@RefreshScope
@RequestMapping("mallcoupon/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @Value("${coupon.user.name}")
    private String name;

    @Value("${coupon.user.age}")
    private String age;


    @RequestMapping("/test")
    public Result test() {
        return Result.ok().put("name", name).put("age", age);
    }

    @RequestMapping("/member/list")
    public Result membercoupons() {
        CouponEntity couponEntity = new CouponEntity();
        couponEntity.setCouponName("满100减10");
        return Result.ok().put("coupons", Arrays.asList(couponEntity));
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = couponService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        CouponEntity coupon = couponService.getById(id);

        return Result.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody CouponEntity coupon) {
        couponService.save(coupon);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody CouponEntity coupon) {
        couponService.updateById(coupon);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        couponService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
