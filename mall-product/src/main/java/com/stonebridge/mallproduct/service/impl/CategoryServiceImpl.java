package com.stonebridge.mallproduct.service.impl;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 查出所有分类，将其组装父子的树形结构
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
                categoryEntity.getParentCid() == 0
        ).map((meau) -> {
            meau.setChildren(getChildren(meau, list));
            return meau;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return leve1Menus;
    }

    /**
     * 递归查找当前菜单以及所有子菜单
     *
     * @param category
     * @param alllist
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

}