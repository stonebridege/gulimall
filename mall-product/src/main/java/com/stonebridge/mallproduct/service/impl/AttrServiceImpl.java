package com.stonebridge.mallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.common.constant.ProductConstant;
import com.common.utils.StrUtil;
import com.stonebridge.mallproduct.dao.AttrAttrgroupRelationDao;
import com.stonebridge.mallproduct.dao.AttrGroupDao;
import com.stonebridge.mallproduct.dao.CategoryDao;
import com.stonebridge.mallproduct.entity.AttrAttrgroupRelationEntity;
import com.stonebridge.mallproduct.entity.AttrGroupEntity;
import com.stonebridge.mallproduct.entity.CategoryEntity;
import com.stonebridge.mallproduct.service.CategoryService;
import com.stonebridge.mallproduct.vo.AttrGroupRelationVo;
import com.stonebridge.mallproduct.vo.AttrRespVo;
import com.stonebridge.mallproduct.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
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
        //2.保存关联关系,AttrType为1，即为基本属性。当不为1时，即为0。销售属性时不保存关联关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationDao.insert(relationEntity);
        }
    }

    /**
     * 根据条件查询pms_attr表中数据
     *
     * @param params    ：参数
     * @param catelogId ：分类的id（pms_category.id）
     * @return ：数据集
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
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
            // 仅为规格属性为base时查询关联属性组表
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity attrgroupRelationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(attrgroupRelationEntity.getAttrGroupId()));
                    attrRespVo.setGroupName(attrGroup.getAttrGroupName());
                }
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
        // 仅为规格属性为base时查询关联属性组表
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //2.查询出关联pms_attr的pms_category的名字，他们通过中间表pms_attr_attrgroup_relation关联。再查询pms_category.groupName数据
            AttrAttrgroupRelationEntity relationEntity = relationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(relationEntity.getAttrGroupId()));
                attrRespVo.setGroupName(attrGroup.getAttrGroupName());
            }
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
     *
     * @param attrVo：
     */
    @Override
    public void updateAttr(AttrVo attrVo) {
        //1.更新属性表pms_attr数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        // 更新关联关系,AttrType为1，即为基本属性。当不为1时，即为0。销售属性时不保存关联关系
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
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

    /**
     * 根据分组id查找关联的所有基本属性
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> list = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = list.stream().map((sttr) -> sttr.getAttrId()).collect(Collectors.toList());
        if (attrIds.size() == 0) {
            return null;
        } else {
            List<AttrEntity> attrEntities = this.listByIds(attrIds);
            return attrEntities;
        }
    }

    /**
     * 删除《属性分组id》与《属性的id》的关联关系
     *
     * @param relationVos
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] relationVos) {
        List<AttrAttrgroupRelationEntity> entityList = Arrays.asList(relationVos).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrgroupRelationEntity);
            return attrgroupRelationEntity;
        }).collect(Collectors.toList());
        relationDao.deleteBatchRelation(entityList);
    }

    /**
     *
     *
     * @param attrgroupId ：当前分组的id
     * @param params
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //1.1.当前的分组只能只能关联自己所属的分类的里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //1.2.获取分组的id
        Long catelogId = attrGroupEntity.getCatelogId();
        //2.1.当前分组只能关联别的分组没有引入的属性
        List<AttrGroupEntity> groupEntityList = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> collect = groupEntityList.stream().map(item -> {
            return item.getAttrGroupId();
        }).collect(Collectors.toList());

        //2.2.当前分类下的其他分组
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = relationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        List<Long> attrIds = attrgroupRelationEntityList.stream().map(item -> {
            return item.getAttrId();
        }).collect(Collectors.toList());
        //2.3.从当前分类的所有属性移除属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() == 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        String key = StrUtil.trim(params.get("key"));
        if (!StrUtil.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }
}