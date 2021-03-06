package com.stonebridge.mallproduct.service.impl;

import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;
import com.stonebridge.mallproduct.dao.SpuInfoDescDao;
import com.stonebridge.mallproduct.entity.SpuInfoDescEntity;
import com.stonebridge.mallproduct.service.SpuInfoDescService;

@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoDescEntity> page = this.page(
                new Query<SpuInfoDescEntity>().getPage(params),
                new QueryWrapper<SpuInfoDescEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存spu的描述图片 pms_spu_info_desc
     *
     * @param descEntity SpuInfoDescEntity对象
     */
    @Override
    public void saveSpuInfoDesc(SpuInfoDescEntity descEntity) {
        this.baseMapper.insert(descEntity);
    }
}