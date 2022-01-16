package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.Map;

import com.common.vaild.AddGroup;
import com.common.vaild.UpdateGroup;
import com.common.vaild.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallproduct.entity.BrandEntity;
import com.stonebridge.mallproduct.service.BrandService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 品牌
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:31
 */
@RestController
@RequestMapping("mallproduct/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = brandService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public Result info(@PathVariable("brandId") Long brandId) {
        BrandEntity brand = brandService.getById(brandId);

        return Result.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand) {
        brandService.save(brand);
        return Result.ok();
    }


    /**
     * 当对品牌信息进行修改的时候
     * @param brand ：BrandEntity对象
     * @return
     */
    @RequestMapping("/update")
    public Result update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateDetail(brand);
        return Result.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public Result updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand) {
        brandService.updateById(brand);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] brandIds) {
        brandService.removeByIds(Arrays.asList(brandIds));

        return Result.ok();
    }

}
