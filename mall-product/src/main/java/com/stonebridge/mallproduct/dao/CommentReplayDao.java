package com.stonebridge.mallproduct.dao;

import com.stonebridge.mallproduct.entity.CommentReplayEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品评价回复关系
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:59
 */
@Mapper
public interface CommentReplayDao extends BaseMapper<CommentReplayEntity> {

}
