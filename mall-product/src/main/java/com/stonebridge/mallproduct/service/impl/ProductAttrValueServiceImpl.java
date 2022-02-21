package com.stonebridge.mallproduct.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallproduct.dao.ProductAttrValueDao;
import com.stonebridge.mallproduct.entity.ProductAttrValueEntity;
import com.stonebridge.mallproduct.service.ProductAttrValueService;

@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(new Query<ProductAttrValueEntity>().getPage(params), new QueryWrapper<>());
        return new PageUtils(page);
    }

    /**
     * 保存spu的规格参数pms_product_attr_value
     *
     * @param list
     */
    @Override
    public void saveProductAttr(List<ProductAttrValueEntity> list) {
        this.saveBatch(list);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrListForspu(Long spuId) {
        QueryWrapper<ProductAttrValueEntity> queryWrapper = new QueryWrapper<>();
        this.baseMapper.selectList(queryWrapper.eq("spu_id", spuId));
        return null;
    }
}