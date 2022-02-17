package com.stonebridge.mallcoupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.common.to.SkuReductionTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.stonebridge.mallcoupon.entity.SkuFullReductionEntity;
import com.stonebridge.mallcoupon.service.SkuFullReductionService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 商品满减信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 10:50:50
 */
@RestController
@RequestMapping("mallcoupon/skufullreduction")
public class SkuFullReductionController {
    @Autowired
    private SkuFullReductionService skuFullReductionService;

    @PostMapping("/saveinfo")
    public Result saveInfo(@RequestBody SkuReductionTo skuReductionTo) {
        skuFullReductionService.saveSkuReduction(skuReductionTo);
        return Result.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = skuFullReductionService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        SkuFullReductionEntity skuFullReduction = skuFullReductionService.getById(id);

        return Result.ok().put("skuFullReduction", skuFullReduction);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody SkuFullReductionEntity skuFullReduction) {
        skuFullReductionService.save(skuFullReduction);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody SkuFullReductionEntity skuFullReduction) {
        skuFullReductionService.updateById(skuFullReduction);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        skuFullReductionService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
