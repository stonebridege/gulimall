package com.stonebridge.mallmember.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallmember.entity.MemberLoginLogEntity;
import com.stonebridge.mallmember.service.MemberLoginLogService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 会员登录记录
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:02:24
 */
@RestController
@RequestMapping("mallmember/memberloginlog")
public class MemberLoginLogController {
    @Autowired
    private MemberLoginLogService memberLoginLogService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberLoginLogService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        MemberLoginLogEntity memberLoginLog = memberLoginLogService.getById(id);

        return Result.ok().put("memberLoginLog", memberLoginLog);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody MemberLoginLogEntity memberLoginLog) {
        memberLoginLogService.save(memberLoginLog);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody MemberLoginLogEntity memberLoginLog) {
        memberLoginLogService.updateById(memberLoginLog);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        memberLoginLogService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
