package com.stonebridge.mallware.service.impl;

import com.common.utils.StrUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;
import com.stonebridge.mallware.dao.WareInfoDao;
import com.stonebridge.mallware.entity.WareInfoEntity;
import com.stonebridge.mallware.service.WareInfoService;

@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();
        String key = StrUtil.trim(params.get("key"));
        if (StrUtil.isNotEmpty(key)) {
            queryWrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(new Query<WareInfoEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }
}