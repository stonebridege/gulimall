package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallproduct.entity.CategoryEntity;
import com.stonebridge.mallproduct.service.CategoryService;
import com.common.utils.Result;


/**
 * 商品三级分类
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:31
 */
@RestController
@RequestMapping("mallproduct/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list/tree")
    public Result list() {
        List<CategoryEntity> list = categoryService.listWithTree();
        return Result.ok().put("data", list);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public Result info(@PathVariable("catId") Long catId) {
        CategoryEntity category = categoryService.getById(catId);

        return Result.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody CategoryEntity category) {
        categoryService.save(category);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody CategoryEntity category) {
        categoryService.updateById(category);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] catIds) {
//        categoryService.removeByIds(Arrays.asList(catIds));
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return Result.ok();
    }

}
