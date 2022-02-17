package com.stonebridge.mallproduct.service.impl;

import com.common.to.SkuReductionTo;
import com.common.to.SpuBoundTo;
import com.common.utils.Result;
import com.common.utils.StrUtil;
import com.stonebridge.mallproduct.entity.*;
import com.stonebridge.mallproduct.feign.CouponFeignService;
import com.stonebridge.mallproduct.service.*;
import com.stonebridge.mallproduct.vo.*;
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
import org.springframework.transaction.annotation.Transactional;

@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    SpuInfoDescService spuInfoDescService;

    SpuImagesService spuImagesService;

    AttrService attrService;

    ProductAttrValueService valueService;

    SkuInfoService skuInfoService;

    SkuImagesService skuImagesService;

    SkuSaleAttrValueService skuSaleAttrValueService;

    CouponFeignService couponFeignService;

    @Autowired
    public void setCouponFeignService(CouponFeignService couponFeignService) {
        this.couponFeignService = couponFeignService;
    }

    @Autowired
    public void setSkuInfoService(SkuInfoService skuInfoService) {
        this.skuInfoService = skuInfoService;
    }

    @Autowired
    public void setSkuImagesService(SkuImagesService skuImagesService) {
        this.skuImagesService = skuImagesService;
    }

    @Autowired
    public void setSkuSaleAttrValueService(SkuSaleAttrValueService skuSaleAttrValueService) {
        this.skuSaleAttrValueService = skuSaleAttrValueService;
    }

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
        //5.保存spu的积分信息；gulimall_product通过feign调用sms_spu_bounds完成保存
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(infoEntity.getId());
        Result result = couponFeignService.saveSpuBounds(spuBoundTo);
        if (result.getCode() != 0) {
            log.error("远程保存spu积分信息失败");
        }
        //6.保存当前SPU对应的SKU信息
        List<Skus> skusList = spuSaveVo.getSkus();
        if (!skusList.isEmpty()) {
            skusList.forEach(item -> {
                String defaultImg = "";
                for (Images image : item.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                //skuName、price、skuTitle、skuSubtitle
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(item, skuInfoEntity);
                skuInfoEntity.setBrandId(infoEntity.getBrandId());
                skuInfoEntity.setCatalogId(infoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(infoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(defaultImg);
                //6.1.sku的基本信息pms_sku_info
                skuInfoService.saveSkuInfo(skuInfoEntity);
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntities = item.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    return skuImagesEntity;
                }).filter(entity -> {
                    //返回true就是需要，false就是剔除
                    return !StrUtil.isEmpty(entity.getImgUrl());
                }).collect(Collectors.toList());
                //6.2.sku的图片信息pms_sku_images
                skuImagesService.saveBatch(imagesEntities);
                //TODO 没有图片路径的无需保存
                List<Attr> attr = item.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity attrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, attrValueEntity);
                    attrValueEntity.setSkuId(skuId);
                    return attrValueEntity;
                }).collect(Collectors.toList());
                //6.3.sku的销售属性信息pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);
                //6.4.sku的优惠、满减等信息；gulimall_sms->sms_sku_ladder(sku打折表)\sms_sku_full_reduction(满减表)\sms_member_price(会员价格表)
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(item, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                Result result1 = couponFeignService.saveSkuReduction(skuReductionTo);
                if (result1.getCode() != 0) {
                    log.error("远程保存sku优惠信息失败");
                }
            });
        }
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