package com.stonebridge.mallproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.stonebridge.mallproduct.entity.AttrAttrgroupRelationEntity;
import com.stonebridge.mallproduct.entity.AttrEntity;
import com.stonebridge.mallproduct.service.AttrAttrgroupRelationService;
import com.stonebridge.mallproduct.service.AttrService;
import com.stonebridge.mallproduct.service.CategoryService;
import com.stonebridge.mallproduct.vo.AttrGroupRelationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.stonebridge.mallproduct.entity.AttrGroupEntity;
import com.stonebridge.mallproduct.service.AttrGroupService;
import com.common.utils.PageUtils;
import com.common.utils.Result;

/**
 * 属性分组
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 23:49:31
 */
@RestController
@RequestMapping("mallproduct/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService relationService;

    /**
     * 列表
     */
    @RequestMapping("/list/{cateLogId}")
    public Result list(@RequestParam Map<String, Object> params, @PathVariable("cateLogId") Long cateLogId) {
        PageUtils page = attrGroupService.queryPage(params, cateLogId);
        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public Result info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        Long catelogId = attrGroup.getCatelogId();
        Long[] path = categoryService.findCatelogPath(catelogId);
        attrGroup.setCatelogPath(path);
        return Result.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));
        return Result.ok();
    }

    /**
     * 根据属性分组id查询对应的所有的属性
     *
     * @param attrgroupId 属性分组id
     * @return 查询数据
     */
    @GetMapping("/{attrgroupId}/attr/relation")
    public Result attrRelation(@PathVariable("attrgroupId") Long attrgroupId) {
        List<AttrEntity> list = attrService.getRelationAttr(attrgroupId);
        return Result.ok().put("data", list);
    }

    /**
     * 删除《属性分组id》与《属性的id》的关联关系
     *
     * @param vos ：属性分组id与属性的id
     * @return 删除结果
     */
    @PostMapping("/attr/relation/delete")
    public Result deleteRelation(@RequestBody AttrGroupRelationVo[] vos) {
        attrService.deleteRelation(vos);
        return Result.ok();
    }

    /**
     * mallproduct/attrgroup/2/noattr/relation
     * 获取本分类下没有关联其他分组关联的属性
     *
     * @param attrgroupId 属性分组的id
     * @param params      所在分类信息
     * @return :查询结果
     */
    @GetMapping("/{attrgroupId}/noattr/relation")
    public Result attrNoRelation(@PathVariable("attrgroupId") Long attrgroupId, @RequestParam Map<String, Object> params) {
        PageUtils page = attrService.getNoRelationAttr(attrgroupId, params);
        return Result.ok().put("page", page);
    }

    /**
     * 属性和属性分组保存关联关系
     *
     * @param list: 参数集合
     * @return :处理结果
     */
    @PostMapping("/attr/relation")
    public Result addRelation(@RequestBody List<AttrGroupRelationVo> list) {
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = list.stream().map((item) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        relationService.saveBatch(attrgroupRelationEntityList);
        return Result.ok();
    }
}
