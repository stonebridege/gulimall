package com.stonebridge.mallware.dao;

import com.stonebridge.mallware.entity.PurchaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 采购信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@Mapper
public interface PurchaseDao extends BaseMapper<PurchaseEntity> {

}
