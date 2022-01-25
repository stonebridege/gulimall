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
    AttrAttrgroupRelationDao attrAttrgroupRelationDao;

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
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && (attr.getAttrGroupId() != null)) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(relationEntity);
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
                AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrgroupRelationEntity != null && attrgroupRelationEntity.getAttrGroupId() != null) {
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
        AttrRespVo attrRespVo = new AttrRespVo();
        //1.根据属性Id查询当前属性的详细信息，并将其赋值到AttrRespVo对象
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        //2.规格属性为base时（即该属性为基础属性的值）查询关联属性组表
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //3.根据属性Id查询出<属性&属性分组关联表>的数据；
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                //4.再根据属性&属性分组关联表查询属性分组的id，再查询出属性分组表，获取分组名称
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(relationEntity.getAttrGroupId()));
                attrRespVo.setGroupName(attrGroup.getAttrGroupName());
            }
        }
        //5.根据pms_attr.catelog_id获取分类的主键id。根据主键查询出所有的分类信息
        Long catelogId = attrEntity.getCatelogId();
        //6.获取分类信息的父级节点关系
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        //7.获取分类信息并返回分类名称
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    /**
     * 修改<属性数据>，以及更新<属性&属性分组关联表>pms_attr_attrgroup_relation
     *
     * @param attrVo：
     */
    @Override
    public void updateAttr(AttrVo attrVo) {
        //1.更新属性表pms_attr数据
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        // 2.更新关联关系,AttrType为1，即为基本属性。当不为1时，即为0。销售属性时不保存关联关系
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //3.更新保存关系表pms_attr_attrgroup_relation数据
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrVo.getAttrId());
            //4.查询该属性是否已经与属性分组关联，如果已关联则更新数据，否则新增数据
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            } else {
                attrAttrgroupRelationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 根据<属性分组id>查找其关联的所有基本属性
     *
     * @param attrgroupId 属性分组id
     * @return ：数据
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        /**
         * 1.根据<属性分组id>查找<属性&属性分组关联表>对应数据
         */
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        /**
         * 2.遍历<属性&属性分组关联表>的数据获取所有是属性主键
         */
        List<Long> attrIds = list.stream().map((sttr) -> sttr.getAttrId()).collect(Collectors.toList());
        if (attrIds.isEmpty()) {
            return null;
        } else {
            /**
             * 3.根据属性主键查询所有数据
             */
            return this.listByIds(attrIds);
        }
    }

    /**
     * 删除《属性分组id》与《属性的id》的关联关系，即<删除属性&属性分组关联>的数据
     *
     * @param relationVos 删除的关联关系数据
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] relationVos) {
        /**
         * 将<属性分组id>和<属性的id>封装成AttrAttrgroupRelationEntity对象再执行删除操作
         */
        List<AttrAttrgroupRelationEntity> entityList = Arrays.asList(relationVos).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrgroupRelationEntity);
            return attrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(entityList);
    }

    /**
     * 获取本分类下没有其他属性分组关联的属性
     *
     * @param attrgroupId ：当前分组的id
     * @param params      分页参数、模糊匹配参数
     * @return ：数据集
     */
    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //1.获取当前<属性分组>中对应的pms_category的主键
        //1.1.根据<属性&属性分组关联表的主键id>查询pms_attr_group对应的数据
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //1.2.获取<属性&属性分组关联表>中的分类主键id（pms_category的主键）
        Long catelogId = attrGroupEntity.getCatelogId();


        //2.获取当前<属性分组>只能关联别的<属性分组>没有引用的属性
        //2.1.获取当前分类下的所有的<属性&属性分组关联表>数据
        List<AttrGroupEntity> groupEntityList = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //2.2.获取当前分类下的所有<属性&属性分组关联表>数据的属性分组id（主键），即已经关联当前分类所有属性分组
        List<Long> attrGroupIdList = groupEntityList.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //2.3.根据<当前分类所有属性分组的id>的数据从<属性&属性分组关联表>查询所有<属性&属性分组关联表>的关联数据
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIdList));
        //2.4.根据<属性&属性分组关联表>的数据获取当前分类下所有的属性id（主键）
        List<Long> attrIds = attrgroupRelationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        //2.5.获取<当前分类catelog_id> && <移除已经绑定了关联属性分组的属性> 所有属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }


        //3.查询对应的数据
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