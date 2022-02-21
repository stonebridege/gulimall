package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.stonebridge.mallproduct.entity.ProductAttrValueEntity;
import com.stonebridge.mallproduct.service.ProductAttrValueService;
import com.stonebridge.mallproduct.vo.AttrRespVo;
import com.stonebridge.mallproduct.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    ProductAttrValueService productAttrValueService;

    //mallproduct/attr/base/listforspu/7
    @GetMapping("base/listforspu/{spuId}")
    public Result baseAttrList(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForspu(spuId);
        return Result.ok().put("data", entities);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);
        return Result.ok().put("page", page);
    }

    /**
     * 查询查询条件查询属性表（pms_attr）的数据
     * mallproduct/attr/base/list/225
     *
     * @param params    ：参数
     * @param catelogId ：分类的id（pms_category.id）
     * @param attrType  : 当为sale的时候查询<销售属性>attr_type=0，当为base的时候查询<所有属性>
     * @return :结果集
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public Result baseList(@RequestParam Map<String, Object> params,
                           @PathVariable("catelogId") Long catelogId,
                           @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);
        return Result.ok().put("page", page);
    }

    /**
     * 修改参数信息时，回显要修改的数据
     *
     * @param attrId 属性id
     * @return 数据集
     */
    @RequestMapping("/info/{attrId}")
    public Result info(@PathVariable("attrId") Long attrId) {
        AttrRespVo attrRespVo = attrService.getAttrInfo(attrId);
        return Result.ok().put("attr", attrRespVo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody AttrVo attr) {
        attrService.saveAttr(attr);
        return Result.ok();
    }

    /**
     * 修改保存属性数据
     */
    @RequestMapping("/update")
    public Result update(@RequestBody AttrVo attrVo) {
        attrService.updateAttr(attrVo);
        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));
        return Result.ok();
    }
}
