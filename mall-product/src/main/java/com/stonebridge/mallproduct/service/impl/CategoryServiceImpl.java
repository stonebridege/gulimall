package com.stonebridge.mallproduct.service.impl;

import com.common.utils.StrUtil;
import com.stonebridge.mallproduct.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.utils.PageUtils;
import com.common.utils.Query;

import com.stonebridge.mallproduct.dao.CategoryDao;
import com.stonebridge.mallproduct.entity.CategoryEntity;
import com.stonebridge.mallproduct.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有商品分类，根据父子节点关系将其组装父子的树形结构
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        //1，查出所有的数据
        List<CategoryEntity> list = baseMapper.selectList(null);
        //2.组装成父子的树形结构
        //2.1.找到所有的以及分类
        List<CategoryEntity> leve1Menus = list.stream().filter((categoryEntity) ->
                //2.2.获取所有的根节点，即parentCid为0
                categoryEntity.getParentCid() == 0
        ).map((meau) -> {
            //2.3.设置当前根节点的子节点
            meau.setChildren(getChildren(meau, list));
            return meau;
        }).sorted((menu1, menu2) -> {
            //2.4.排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return leve1Menus;
    }

    /**
     * 递归查找当前菜单以及所有子菜单
     *
     * @param rootCategory :根节点
     * @param alllist      ：所有的节点
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity rootCategory, List<CategoryEntity> alllist) {
        List<CategoryEntity> list = alllist.stream().filter(category -> {
            return Objects.equals(category.getParentCid(), rootCategory.getCatId());
        }).map(category -> {
            category.setChildren(getChildren(category, alllist));
            return category;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return list;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPaths = this.findParentPath(catelogId, paths);
        Collections.reverse(parentPaths);
        return parentPaths.toArray(new Long[paths.size()]);
    }

    /**
     * 商品分类数据（CategoryEntity）进行更新，除了pms_category表进行更新；
     * 使用冗余存储的pms_category_brand_relation表中的catelog_name和cateLog字段进行更新
     *
     * @param category CategoryEntity对象
     */
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        if (!StrUtil.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    private List<Long> findParentPath(long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity categoryId = this.getById(catelogId);
        if (categoryId.getParentCid() != 0) {
            findParentPath(categoryId.getParentCid(), paths);
        }
        return paths;
    }

}