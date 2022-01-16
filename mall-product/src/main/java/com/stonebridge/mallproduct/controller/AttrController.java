package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.Map;

import com.stonebridge.mallproduct.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallproduct.entity.AttrEntity;
import com.stonebridge.mallproduct.service.AttrService;
import com.common.utils.PageUtils;
import com.common.utils.Result;



/**
 * 商品属性
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:31
 */
@RestController
@RequestMapping("mallproduct/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public Result info(@PathVariable("attrId") Long attrId){
		AttrEntity attr = attrService.getById(attrId);

        return Result.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody AttrVo attr){
		attrService.saveAttr(attr);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody AttrEntity attr){
		attrService.updateById(attr);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return Result.ok();
    }

}
