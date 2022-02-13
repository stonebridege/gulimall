package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stonebridge.mallproduct.entity.BrandEntity;
import com.stonebridge.mallproduct.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import com.stonebridge.mallproduct.entity.CategoryBrandRelationEntity;
import com.stonebridge.mallproduct.service.CategoryBrandRelationService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 品牌分类关联
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:31
 */
@RestController
@RequestMapping("mallproduct/categorybrandrelation")
public class CategoryBrandRelationController {
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    @Qualifier(value = "categoryBrandRelationService")
    public void setCategoryBrandRelationService(CategoryBrandRelationService categoryBrandRelationService) {
        this.categoryBrandRelationService = categoryBrandRelationService;
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return Result.ok().put("page", page);
    }

    /**
     * 列表
     */
    @GetMapping("/catelog/list")
    public Result catelogList(@RequestParam("brandId") Long brandId) {
        QueryWrapper<CategoryBrandRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("brand_id", brandId);
        List<CategoryBrandRelationEntity> list = categoryBrandRelationService.list(queryWrapper);
        return Result.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return Result.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

    /**
     * mallproduct/categorybrandrelation/brands/list
     * 1.Controller:处理请求、接受和校验数据
     * 2.Service接受Controller传来的数据，进行业务处理
     * 3.Controller接受Service处理的数据，页面进行封装
     *
     * @param catelog_Id :分类id
     * @return :品牌数据集
     */
    @GetMapping("/brands/list")
    public Result relationBrandsList(@RequestParam(value = "catId") Long catelog_Id) {
        List<BrandEntity> list = categoryBrandRelationService.getBrandsByCatId(catelog_Id);
        List<BrandVo> rtnList = list.stream().map(item -> {
            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(item.getBrandId());
            brandVo.setBrandName(item.getName());
            return brandVo;
        }).collect(Collectors.toList());
        return Result.ok().put("data", rtnList);
    }

}
