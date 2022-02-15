package com.stonebridge.mallproduct.service.impl;

import com.stonebridge.mallproduct.entity.ProductAttrValueEntity;
import com.stonebridge.mallproduct.entity.SpuInfoDescEntity;
import com.stonebridge.mallproduct.service.*;
import com.stonebridge.mallproduct.vo.BaseAttrs;
import com.stonebridge.mallproduct.vo.SpuSaveVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;
import com.stonebridge.mallproduct.dao.SpuInfoDao;
import com.stonebridge.mallproduct.entity.SpuInfoEntity;
import org.springframework.transaction.annotation.Transactional;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    SpuInfoDescService spuInfoDescService;

    SpuImagesService spuImagesService;

    AttrService attrService;

    ProductAttrValueService valueService;

    @Autowired
    public void setValueService(ProductAttrValueService valueService) {
        this.valueService = valueService;
    }

    @Autowired
    public void setSpuInfoDescService(SpuInfoDescService spuInfoDescService) {
        this.spuInfoDescService = spuInfoDescService;
    }

    @Autowired
    public void setSpuImagesService(SpuImagesService spuImagesService) {
        this.spuImagesService = spuImagesService;
    }

    @Autowired
    public void setAttrService(AttrService attrService) {
        this.attrService = attrService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), new QueryWrapper<>());
        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        //1.保存spu的基本信息 pms_spu_info
        SpuInfoEntity infoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, infoEntity);
        infoEntity.setCreateTime(new Date());
        infoEntity.setUpdateTime(new Date());
        this.saveBaseSpuInfo(infoEntity);
        //2.保存spu的描述图片 pms_spu_info_desc
        List<String> decripts = spuSaveVo.getDecript();
        SpuInfoDescEntity descEntity = new SpuInfoDescEntity();
        descEntity.setSpuId(infoEntity.getId());
        descEntity.setDecript(String.join(",", decripts));
        spuInfoDescService.saveSpuInfoDesc(descEntity);
        //3.保存spu的图片集 pms_spu_images
        List<String> images = spuSaveVo.getImages();
        spuImagesService.saveImages(infoEntity.getId(), images);
        //4.保存spu的规格参数pms_product_attr_value
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        List<ProductAttrValueEntity> list = baseAttrs.stream().map(item -> {
            ProductAttrValueEntity valueEntity = new ProductAttrValueEntity();
            valueEntity.setAttrId(item.getAttrId());
            valueEntity.setAttrName(attrService.getById(item.getAttrId()).getAttrName());
            valueEntity.setAttrValue(item.getAttrValues());
            valueEntity.setQuickShow(item.getShowDesc());
            valueEntity.setSpuId(infoEntity.getId());
            return valueEntity;
        }).collect(Collectors.toList());
        valueService.saveProductAttr(list);
        //5.保存spu的积分信息；gulimall_sms->sms_spu_bounds
        //6.保存当前SPU对应的SKU信息；
        //6.1.sku的基本信息pms_sku_info
        //6.2.sku的图片信息pms_sku_images
        //6.3.sku的销售属性信息pms_sku_sale_attr_value
        //6.4.sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder(sku打折表)\sms_sku_full_reduction(满减表)\sms_member_price(会员价格表)
    }

    /**
     * 保存spu的基本信息 pms_spu_info
     *
     * @param infoEntity SpuInfoEntity对象
     */
    @Override
    public void saveBaseSpuInfo(SpuInfoEntity infoEntity) {
        this.baseMapper.insert(infoEntity);
    }
}