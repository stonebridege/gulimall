package com.stonebridge.mallmember.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallmember.entity.MemberCollectSubjectEntity;
import com.stonebridge.mallmember.service.MemberCollectSubjectService;
import com.common.utils.PageUtils;
import com.common.utils.Result;



/**
 * 会员收藏的专题活动
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:02:25
 */
@RestController
@RequestMapping("mallmember/membercollectsubject")
public class MemberCollectSubjectController {
    @Autowired
    private MemberCollectSubjectService memberCollectSubjectService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = memberCollectSubjectService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
		MemberCollectSubjectEntity memberCollectSubject = memberCollectSubjectService.getById(id);

        return Result.ok().put("memberCollectSubject", memberCollectSubject);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody MemberCollectSubjectEntity memberCollectSubject){
		memberCollectSubjectService.save(memberCollectSubject);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody MemberCollectSubjectEntity memberCollectSubject){
		memberCollectSubjectService.updateById(memberCollectSubject);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids){
		memberCollectSubjectService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
