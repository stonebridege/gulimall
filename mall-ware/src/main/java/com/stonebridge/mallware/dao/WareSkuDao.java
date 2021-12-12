package com.stonebridge.mallware.dao;

import com.stonebridge.mallware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品库存
 * 
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:20
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {
	
}
