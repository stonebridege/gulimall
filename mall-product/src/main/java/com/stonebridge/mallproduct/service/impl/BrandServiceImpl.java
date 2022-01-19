package com.stonebridge.mallproduct.service.impl;

import com.common.utils.StrUtil;
import com.stonebridge.mallproduct.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallproduct.dao.BrandDao;
import com.stonebridge.mallproduct.entity.BrandEntity;
import com.stonebridge.mallproduct.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = StrUtil.trim(params.get("key"));
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if (!StrUtil.isEmpty(key)) {
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(new Query<BrandEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    /**
     * 当对品牌信息进行修改的时候,如果修改了品牌名称（pms_brand.name）,则要更新pms_category_brand_relation中冗余的pms_brand.name字段
     *
     * @param brand BrandEntity对象
     */
    @Override
    public void updateDetail(BrandEntity brand) {
        //保证冗余字段的数据进行更新
        this.updateById(brand);
        if (!StrUtil.isEmpty(brand.getName())) {
            //同步更新其他关联表中的数据
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
            //TODO
//            更新其他关联
        }
    }
}