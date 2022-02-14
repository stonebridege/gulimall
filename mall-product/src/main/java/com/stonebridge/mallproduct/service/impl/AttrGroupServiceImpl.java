package com.stonebridge.mallproduct.service.impl;

import com.common.utils.StrUtil;
import com.stonebridge.mallproduct.entity.AttrEntity;
import com.stonebridge.mallproduct.service.AttrService;
import com.stonebridge.mallproduct.vo.AttrGroupWithAttrsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallproduct.dao.AttrGroupDao;
import com.stonebridge.mallproduct.entity.AttrGroupEntity;
import com.stonebridge.mallproduct.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    AttrService attrService;

    @Autowired
    public void setAttrService(AttrService attrService) {
        this.attrService = attrService;
    }

    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 查询属性分组表时，增加模糊匹配
     *
     * @param params    :参数集
     * @param catelogId ：分类id
     * @return ：数据集
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        //SELECT attr_group_id,attr_group_name,sort,descript,icon,catelog_id FROM pms_attr_group WHERE (catelog_id = ? AND (attr_group_id = ? OR attr_group_name LIKE ?))
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if (!StrUtil.isEmpty(key)) {
            wrapper.and((obj) -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id查询出所有的分组以及这些分组里面的属性
     *
     * @param catelogId 分类id
     * @return :结果集
     */
    @Override
    public List<AttrGroupWithAttrsVo> getAttrGroupWithAttrsByCatelogId(String catelogId) {
        //1.查询出所有的分组信息
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<>();
        List<AttrGroupEntity> attrGroupEntities = this.list(queryWrapper.eq("catelog_id", catelogId));
        //2.查询出所有属性
        List<AttrGroupWithAttrsVo> list = attrGroupEntities.stream().map(group -> {
            AttrGroupWithAttrsVo attrsVo = new AttrGroupWithAttrsVo();
            BeanUtils.copyProperties(group, attrsVo);
            List<AttrEntity> attrEntities = attrService.getRelationAttr(attrsVo.getAttrGroupId());
            attrsVo.setAttrs(attrEntities);
            return attrsVo;
        }).collect(Collectors.toList());
        return list;
    }
}