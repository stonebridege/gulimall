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
     * ???????????????????????????????????????pms_attr????????????????????????????????????pms_attr_group????????????????????????pms_attr_attrgroup_relation
     *
     * @param attr
     */
    @Transactional
    @Override
    public void saveAttr(AttrVo attr) {
        // 1.??????????????????
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        this.save(attrEntity);
        //2.??????????????????,AttrType???1?????????????????????????????????1????????????0???????????????????????????????????????
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && (attr.getAttrGroupId() != null)) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    /**
     * ??????????????????pms_attr????????????
     *
     * @param params    ?????????
     * @param catelogId ????????????id???pms_category.id???
     * @return ????????????
     */
    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        //1.????????????id???0,???????????????id????????????????????????????????????
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        //2.??????????????????????????????????????????????????????????????????????????????????????????attr_id???attr_name
        String key = StrUtil.trim(params.get("key"));
        if (!StrUtil.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //3.???pms_attr????????????????????????????????????pms_attr???????????????????????????
        // - ??????pms_attr???pms_category?????????????????????????????????pms_attr_attrgroup_relation??????
        // - ??????pms_category???????????????pms_attr.catelog_id?????????????????????
        // 3.1.?????????pms_attr?????????
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);
        List<AttrEntity> attrEntityList = page.getRecords();
        // 3.2.???????????????pms_attr??????
        List<AttrRespVo> attrRespVoList = attrEntityList.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 3.3.???????????????pms_attr???pms_category?????????????????????????????????pms_attr_attrgroup_relation??????
            // ?????????????????????base???????????????????????????
            if ("base".equalsIgnoreCase(attrType)) {
                QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("attr_id", attrEntity.getAttrId());
                AttrAttrgroupRelationEntity attrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(queryWrapper1);
                if (attrgroupRelationEntity != null && attrgroupRelationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(attrgroupRelationEntity.getAttrGroupId()));
                    attrRespVo.setGroupName(attrGroup.getAttrGroupName());
                }
            }
            // 3.4.??????pms_category????????????????????????pms_attr.catelog_id??????????????????
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
     * ?????????????????????????????????
     *
     * @param attrId ????????????id
     * @return ????????????
     */
    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        AttrRespVo attrRespVo = new AttrRespVo();
        //1.????????????Id??????????????????????????????????????????????????????AttrRespVo??????
        AttrEntity attrEntity = this.getById(attrId);
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        //2.???????????????base??????????????????????????????????????????????????????????????????
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //3.????????????Id?????????<??????&?????????????????????>????????????
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
            if (relationEntity != null) {
                //4.???????????????&??????????????????????????????????????????id???????????????????????????????????????????????????
                attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
                AttrGroupEntity attrGroup = attrGroupDao.selectById(StrUtil.trim(relationEntity.getAttrGroupId()));
                attrRespVo.setGroupName(attrGroup.getAttrGroupName());
            }
        }
        //5.??????pms_attr.catelog_id?????????????????????id?????????????????????????????????????????????
        Long catelogId = attrEntity.getCatelogId();
        //6.???????????????????????????????????????
        Long[] catelogPath = categoryService.findCatelogPath(catelogId);
        attrRespVo.setCatelogPath(catelogPath);
        //7.???????????????????????????????????????
        CategoryEntity categoryEntity = categoryService.getById(catelogId);
        if (categoryEntity != null) {
            attrRespVo.setCatelogName(categoryEntity.getName());
        }
        return attrRespVo;
    }

    /**
     * ??????<????????????>???????????????<??????&?????????????????????>pms_attr_attrgroup_relation
     *
     * @param attrVo???
     */
    @Override
    public void updateAttr(AttrVo attrVo) {
        //1.???????????????pms_attr??????
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.updateById(attrEntity);
        // 2.??????????????????,AttrType???1?????????????????????????????????1????????????0???????????????????????????????????????
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //3.?????????????????????pms_attr_attrgroup_relation??????
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            relationEntity.setAttrId(attrVo.getAttrId());
            //4.??????????????????????????????????????????????????????????????????????????????????????????????????????
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationDao.update(relationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
            } else {
                attrAttrgroupRelationDao.insert(relationEntity);
            }
        }
    }

    /**
     * ??????<????????????id>????????????????????????????????????
     *
     * @param attrgroupId ????????????id
     * @return ?????????
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        /*
         * 1.??????<????????????id>??????<??????&?????????????????????>????????????
         */
        QueryWrapper<AttrAttrgroupRelationEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("attr_group_id", attrgroupId);
        List<AttrAttrgroupRelationEntity> list = attrAttrgroupRelationDao.selectList(queryWrapper);
        /**
         * 2.??????<??????&?????????????????????>????????????????????????????????????
         */
        List<Long> attrIds = list.stream().map((sttr) -> sttr.getAttrId()).collect(Collectors.toList());
        if (attrIds.isEmpty()) {
            return null;
        } else {
            /**
             * 3.????????????????????????????????????
             */
            return this.listByIds(attrIds);
        }
    }

    /**
     * ?????????????????????id??????????????????id????????????????????????<????????????&??????????????????>?????????
     *
     * @param relationVos ???????????????????????????
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] relationVos) {
        /**
         * ???<????????????id>???<?????????id>?????????AttrAttrgroupRelationEntity???????????????????????????
         */
        List<AttrAttrgroupRelationEntity> entityList = Arrays.asList(relationVos).stream().map((item) -> {
            AttrAttrgroupRelationEntity attrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrgroupRelationEntity);
            return attrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelation(entityList);
    }

    /**
     * ?????????????????????????????????????????????????????????
     *
     * @param attrgroupId ??????????????????id
     * @param params      ?????????????????????????????????
     * @return ????????????
     */
    @Override
    public PageUtils getNoRelationAttr(Long attrgroupId, Map<String, Object> params) {
        //1.??????attrgroupId????????????<????????????pms_attr_group>???????????????????????????????????????category_id???pms_category????????????
        //1.1.??????attrgroupId??????<????????????pms_attr_group>????????????
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        //1.2.??????????????????<????????????pms_attr_group>??????????????????????????????category_id???pms_category????????????
        Long catelogId = attrGroupEntity.getCatelogId();

        //2.????????????<????????????>??????????????????<????????????>?????????????????????
        //2.1.??????<????????????pms_attr_group>?????????id???category_id???????????????????????????????????????
        QueryWrapper<AttrGroupEntity> groupEntityQueryWrapper = new QueryWrapper<>();
        groupEntityQueryWrapper.eq("catelog_id", catelogId);
        List<AttrGroupEntity> groupEntityList = attrGroupDao.selectList(groupEntityQueryWrapper);
        //2.2.??????????????????????????????<???????????????>?????????????????????id??????????????????????????????pms_attr_attrgroup_relation?????????attr_group_id??????
        List<Long> attrGroupIdList = groupEntityList.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //2.3.????????????????????????pms_attr_attrgroup_relation?????????attr_group_id?????????attrGroupIdList???????????????
        QueryWrapper<AttrAttrgroupRelationEntity> attrgroupRelationQueryWrapper = new QueryWrapper<>();
        attrgroupRelationQueryWrapper.in("attr_group_id", attrGroupIdList);
        List<AttrAttrgroupRelationEntity> attrgroupRelationEntityList = attrAttrgroupRelationDao.selectList(attrgroupRelationQueryWrapper);
        //2.4.??????<??????&?????????????????????>?????????????????????????????????????????????id????????????
        List<Long> attrIds = attrgroupRelationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        //2.5.??????<????????????catelog_id> && <????????????????????????????????????????????????> ????????????
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("catelog_id", catelogId);
        queryWrapper.eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if (attrIds.size() > 0) {
            queryWrapper.notIn("attr_id", attrIds);
        }
        //2.6.??????????????????????????????????????????????????????
        String key = StrUtil.trim(params.get("key"));
        if (!StrUtil.isEmpty(key)) {
            queryWrapper.and((w) -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        //3.?????????????????????
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }
}