package com.stonebridge.mallware.service.impl;

import com.common.utils.StrUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallware.dao.PurchaseDetailDao;
import com.stonebridge.mallware.entity.PurchaseDetailEntity;
import com.stonebridge.mallware.service.PurchaseDetailService;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<PurchaseDetailEntity> queryWrapper = new QueryWrapper<>();
        String key = StrUtil.trim(params.get("key"));
        if (StrUtil.isNotEmpty(key)) {
            queryWrapper.and(w -> {
                w.eq("purchase_id", key).or().eq("sku_id", key);
            });
        }
        String status = StrUtil.trim(params.get("status"));
        if (StrUtil.isNotEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        String wareId = StrUtil.trim(params.get("wareId"));
        if (StrUtil.isNotEmpty(wareId)) {
            queryWrapper.eq("ware_id", wareId);
        }
        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<PurchaseDetailEntity> listDetailByPurchaseId(Long id) {
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("purchase_id", id);
        List<PurchaseDetailEntity> list = this.list(wrapper);
        return list;
    }
}