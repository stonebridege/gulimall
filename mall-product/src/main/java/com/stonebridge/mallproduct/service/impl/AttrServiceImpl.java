package com.stonebridge.mallproduct.service.impl;

import com.stonebridge.mallproduct.dao.AttrAttrgroupRelationDao;
import com.stonebridge.mallproduct.entity.AttrAttrgroupRelationEntity;
import com.stonebridge.mallproduct.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallproduct.dao.AttrDao;
import com.stonebridge.mallproduct.entity.AttrEntity;
import com.stonebridge.mallproduct.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存属性值时保存到属性表（pms_attr）和保存到和属性分组表（pms_attr_group）到关联关系表中pms_attr_attrgroup_relation
     * @param attr
     */
    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        // 1.保存基本数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //2.保存关联关系
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationDao.insert(relationEntity);
    }
}