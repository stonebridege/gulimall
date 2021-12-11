package com.stonebridge.mallproduct.dao;

import com.stonebridge.mallproduct.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:59
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
