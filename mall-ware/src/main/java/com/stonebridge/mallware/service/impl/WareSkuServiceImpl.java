package com.stonebridge.mallware.service.impl;

import com.common.utils.Result;
import com.common.utils.StrUtil;
import com.stonebridge.mallware.feign.ProductFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;
import com.stonebridge.mallware.dao.WareSkuDao;
import com.stonebridge.mallware.entity.WareSkuEntity;
import com.stonebridge.mallware.service.WareSkuService;

@Slf4j
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    ProductFeignService productFeignService;

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

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        //1.如果还没有这个库存记录则新增
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("sku_id", skuId).eq("ware_id", wareId);
        List<WareSkuEntity> list = wareSkuDao.selectList(queryWrapper);
        if (list.isEmpty()) {
            WareSkuEntity wareSkuEntity = new WareSkuEntity();
            wareSkuEntity.setSkuId(skuId);
            wareSkuEntity.setStock(skuNum);
            wareSkuEntity.setWareId(wareId);
            wareSkuEntity.setStockLocked(0);
            try {
                //TODO 远程查询sku的名字,如果失败不需要回滚
                Result result = productFeignService.info(skuId);
                if (result.getCode() == 0) {

                    Map<String, Object> map = (Map<String, Object>) result.get("skuInfo");
                    wareSkuEntity.setSkuName(StrUtil.trim(map.get("skuName")));
                }
            } catch (Exception e) {
                log.error("远程获取商品信息失败", e);
            }
            wareSkuDao.insert(wareSkuEntity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
    }
}