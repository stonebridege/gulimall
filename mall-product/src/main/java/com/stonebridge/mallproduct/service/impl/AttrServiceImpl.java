package com.stonebridge.mallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.common.utils.StrUtil;
import com.stonebridge.mallproduct.dao.AttrAttrgroupRelationDao;
import com.stonebridge.mallproduct.dao.AttrGroupDao;
import com.stonebridge.mallproduct.dao.CategoryDao;
import com.stonebridge.mallproduct.entity.AttrAttrgroupRelationEntity;
import com.stonebridge.mallproduct.entity.AttrGroupEntity;
import com.stonebridge.mallproduct.entity.CategoryEntity;
import com.stonebridge.mallproduct.service.CategoryService;
import com.stonebridge.mallproduct.vo.AttrRespVo;
import com.stonebridge.mallproduct.vo.AttrVo;
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
import com.stonebridge.mallproduct.dao.AttrDao;
import com.stonebridge.mallproduct.entity.AttrEntity;
import com.stonebridge.mallproduct.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    AttrAttrgroupRelationDao relationDao;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存属性值时保存到属性表（pms_attr）和保存到和属性分组表（pms_attr_group）到关联关系表中pms_attr_attrgroup_relation
     *
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

    /**
     * 根据条件查询pms_attr表中数据
     *
     * @param params    ：参数
     * @param catelogId ：分类的id（pms_category.id）
     * @return ：数据集
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        //1.如果分组id为0,则表示分组id不作为条件查询所有的属性
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        //2.如果用户输入关键字作为查询条件，则进行模糊匹配查询。同时匹配attr_id和attr_name
        String key = StrUtil.trim(params.get("key"));
        if (!StrUtil.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //3.从pms_attr查询数据后，前台除了显示pms_attr数据外还需要显示；
        // - 关联pms_attr的pms_category的名字，他们通过中间表pms_attr_attrgroup_relation关联
        // - 关联pms_category的分类名称pms_attr.catelog_id。关联查询即可
        // 3.1.查询出pms_attr的数据
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> attrEntityList = page.getRecords();
        // 3.2.遍历查询出pms_attr的数
        List<AttrRespVo> attrRespVoList = attrEntityList.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 3.3.查询出关联pms_attr的pms_category的名字，他们通过中间表pms_attr_attrgroup_relation关联
            AttrAttrgroupRelationEntity attrgroupRelationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (attrgroupRelationEntity != null) {
                AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(attrgroupRelationEntity.getAttrGroupId()));
                attrRespVo.setGroupName(attrGroup.getAttrGroupName());
            }
            // 3.4.关联pms_category的分类名称，根据pms_attr.catelog_id关联查询即可
            CategoryEntity category = categoryDao.selectById(attrEntity.getCatelogId());
            if (category != null) {
                attrRespVo.setCatelogName(category.getName());
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        pageUtils.setList(attrRespVoList);
        return pageUtils;
    }

    /**
     * 修改参数时回显所有数据
     *
     * @param attrId ：参数的id
     * @return ：数据集
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //1.查询当前属性的详细信息
        AttrRespVo attrRespVo = new AttrRespVo();
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        //2.查询出关联pms_attr的pms_category的名字，他们通过中间表pms_attr_attrgroup_relation关联。再查询pms_category.groupName数据
        AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
        if (relationEntity != null) {
            AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(relationEntity.getAttrGroupId()));
            attrRespVo.setGroupName(attrGroup.getAttrGroupName());
        }
        //3.关联pms_category的分类名称，根据pms_attr.catelog_id关联查询即可
        Long catelogId = attrEntity.getCatelogId();
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    /**
     * 修改保存属性数据，以及更新与属性分组表的关系表pms_attr_attrgroup_relation
     * @param attrVo：
     */
    @Override
    public void updateAttr(AttrVo attrVo) {
        //1.更新属性表pms_attr数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        //2.更新保存关系表pms_attr_attrgroup_relation数据
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        relationEntity.setAttrId(attrVo.getAttrId());
        Integer count = relationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
        if (count > 0) {
            relationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
        } else {
            relationDao.insert(relationEntity);
        }
    }
}