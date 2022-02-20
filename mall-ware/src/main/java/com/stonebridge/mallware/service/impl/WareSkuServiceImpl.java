package com.stonebridge.mallware.service.impl;

import com.common.utils.StrUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;
import com.stonebridge.mallware.dao.WareSkuDao;
import com.stonebridge.mallware.entity.WareSkuEntity;
import com.stonebridge.mallware.service.WareSkuService;

@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        String skuId = StrUtil.trim(params.get("skuId"));
        if (StrUtil.isNotEmpty(skuId)) {
            queryWrapper.eq("sku_id", skuId);
        }
        String wareId = StrUtil.trim(params.get("wareId"));
        if (StrUtil.isNotEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<WareSkuEntity> page = this.page(new Query<WareSkuEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }
}