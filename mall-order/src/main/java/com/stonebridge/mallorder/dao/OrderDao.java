package com.stonebridge.mallorder.dao;

import com.stonebridge.mallorder.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:25:01
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
