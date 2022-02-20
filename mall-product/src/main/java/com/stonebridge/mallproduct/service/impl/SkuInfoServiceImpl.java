package com.stonebridge.mallproduct.service.impl;

import com.common.utils.StrUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;
import com.stonebridge.mallproduct.dao.SkuInfoDao;
import com.stonebridge.mallproduct.entity.SkuInfoEntity;
import com.stonebridge.mallproduct.service.SkuInfoService;

@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = StrUtil.trim(params.get("key"));
        if (StrUtil.isNotEmpty(key)) {
            queryWrapper.and(obj -> {
                queryWrapper.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catelogId = StrUtil.trim(params.get("catelogId"));
        if (StrUtil.isEmpty(catelogId) && !"0".equals(catelogId)) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        String brandId = StrUtil.trim(params.get("brandId"));
        if (StrUtil.isEmpty(brandId) && !"0".equals(catelogId)) {
            queryWrapper.eq("brand_id", brandId);
        }
        String min = StrUtil.trim(params.get("min"));
        if (StrUtil.isNotEmpty(min)) {
            queryWrapper.ge("price", min);
        }
        String max = StrUtil.trim(params.get("max"));
        if (StrUtil.isNotEmpty(max) && "0".equalsIgnoreCase(max)) {
            queryWrapper.le("price", max);
        }
        IPage<SkuInfoEntity> page = this.page(new Query<SkuInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }
}