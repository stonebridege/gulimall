package com.stonebridge.mallproduct.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.stonebridge.mallproduct.dao.BrandDao;
import com.stonebridge.mallproduct.dao.CategoryDao;
import com.stonebridge.mallproduct.entity.BrandEntity;
import com.stonebridge.mallproduct.entity.CategoryEntity;
import com.stonebridge.mallproduct.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallproduct.dao.CategoryBrandRelationDao;
import com.stonebridge.mallproduct.entity.CategoryBrandRelationEntity;
import com.stonebridge.mallproduct.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    BrandDao brandDao;

    @Autowired
    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationDao relationDao;

    @Autowired
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        //查询品牌名和分类名
        BrandEntity brand = brandDao.selectById(brandId);
        CategoryEntity category = categoryDao.selectById(catelogId);
        categoryBrandRelation.setBrandName(brand.getName());
        categoryBrandRelation.setCatelogName(category.getName());
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelation = new CategoryBrandRelationEntity();
        categoryBrandRelation.setBrandId(brandId);
        categoryBrandRelation.setBrandName(name);
        this.update(categoryBrandRelation, new UpdateWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
    }

    /**
     * 更新分类
     *
     * @param catId CategoryEntity对象的id
     * @param name
     */
    @Override
    public void updateCategory(Long catId, String name) {
        baseMapper.updateCategory(catId, name);
    }

    /**
     * 根据分类id查询对应的商品品牌
     *
     * @param catelog_Id :分类id
     * @return :数据集
     */
    @Override
    public List<BrandEntity> getBrandsByCatId(Long catelog_Id) {
        List<CategoryBrandRelationEntity> catelogId = relationDao.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catelog_Id));
        List<BrandEntity> collect = catelogId.stream().map(item -> {
            Long brandId = item.getBrandId();
            return brandService.getById(brandId);
        }).collect(Collectors.toList());
        return collect;
    }
}