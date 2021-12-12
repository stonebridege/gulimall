package com.stonebridge.mallmember.controller;

import java.util.Arrays;
import java.util.Map;

import com.stonebridge.mallmember.feign.CouponFeiginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallmember.entity.MemberEntity;
import com.stonebridge.mallmember.service.MemberService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 会员
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:02:25
 */
@RestController
@RequestMapping("mallmember/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    CouponFeiginService couponFeiginService;


    @RequestMapping("coupons")
    public Result test() {
        MemberEntity entity = new MemberEntity();
        entity.setNickname("stonebridge");
        Result result = couponFeiginService.membercoupons();
        return result.ok().put("member", entity).put("coupons", result.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return Result.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
