package com.stonebridge.mallcoupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.to.SkuReductionTo;
import com.common.utils.PageUtils;
import com.stonebridge.mallcoupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 10:50:50
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);
}

