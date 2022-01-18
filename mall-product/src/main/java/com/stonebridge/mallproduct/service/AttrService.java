package com.stonebridge.mallproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.common.utils.PageUtils;
import com.stonebridge.mallproduct.entity.AttrEntity;
import com.stonebridge.mallproduct.vo.AttrGroupRelationVo;
import com.stonebridge.mallproduct.vo.AttrRespVo;
import com.stonebridge.mallproduct.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-11 10:16:59
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttr(AttrVo attr);

    PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId,String attrType);

    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attrVo);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);
}

