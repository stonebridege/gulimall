package com.stonebridge.mallware.dao;

import com.stonebridge.mallware.entity.UndoLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@Mapper
public interface UndoLogDao extends BaseMapper<UndoLogEntity> {

}
