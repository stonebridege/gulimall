package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallproduct.entity.SpuInfoDescEntity;
import com.stonebridge.mallproduct.service.SpuInfoDescService;
import com.common.utils.PageUtils;
import com.common.utils.Result;



/**
 * spu信息介绍
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:30
 */
@RestController
@RequestMapping("mallproduct/spuinfodesc")
public class SpuInfoDescController {
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoDescService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{spuId}")
    public Result info(@PathVariable("spuId") Long spuId){
		SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(spuId);

        return Result.ok().put("spuInfoDesc", spuInfoDesc);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody SpuInfoDescEntity spuInfoDesc){
		spuInfoDescService.save(spuInfoDesc);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SpuInfoDescEntity spuInfoDesc){
		spuInfoDescService.updateById(spuInfoDesc);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] spuIds){
		spuInfoDescService.removeByIds(Arrays.asList(spuIds));

        return Result.ok();
    }

}
