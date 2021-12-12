package com.stonebridge.mallcoupon.dao;

import com.stonebridge.mallcoupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 10:50:52
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
