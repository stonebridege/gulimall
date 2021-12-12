package com.stonebridge.mallproduct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stonebridge.mallproduct.entity.BrandEntity;
import com.stonebridge.mallproduct.service.BrandService;
import com.stonebridge.mallproduct.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    BrandService brandService;


    @Autowired
    CategoryService categoryService;

//    @Test
//    public void testFindPath(){
//        Long[] catelogPath = categoryService.findCatelogPath(225L);
//        log.info("完整路径：{}", Arrays.asList(catelogPath));
//    }


    @Test
    public void contextLoads() {

//        BrandEntity brandEntity = new BrandEntity();
//        brandEntity.setBrandId(1L);
//        brandEntity.setDescript("华为");
//
////
//        brandEntity.setName("华为");
//        brandService.save(brandEntity);
//        System.out.println("保存成功....");

//        brandService.updateById(brandEntity);


        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach((item) -> {
            System.out.println(item);
        });

    }


}
