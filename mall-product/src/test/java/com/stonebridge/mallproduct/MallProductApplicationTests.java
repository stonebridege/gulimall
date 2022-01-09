package com.stonebridge.mallproduct;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stonebridge.mallproduct.entity.BrandEntity;
import com.stonebridge.mallproduct.service.BrandService;
import com.stonebridge.mallproduct.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

//    @Test
//    public void testUpload() throws FileNotFoundException {
//        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";
//        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建RAM账号。
//        String accessKeyId = "LTAI5tABipxw153Ucj1R6Hds";
//        String accessKeySecret = "BXbJ3TDjMfEW6mNlzECh5LctgMmdoE";
//
//        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
//
//        //上传文件流
//        InputStream inputStream = new FileInputStream("C:\\Users\\augt2\\Pictures\\CameraRoll\\Test\\banff-national-park-herbert-lake-canada-kanada-ozero-dymka-u.jpg");
//        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
//        ossClient.putObject("gulimall-ciel", "banff-national-park-herbert-lake-canada-kanada-ozero-dymka-u.jpg", inputStream);
//        // 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传完成");
//    }
//
//    @Test
//    public void testUpload1() throws FileNotFoundException {
//        //上传文件
//        InputStream inputStream = new FileInputStream("C:\\Users\\augt2\\OneDrive\\日常纪录\\Test\\alpy-shveitsariia-alps-gory-switzerland-mountains-derevia-tr.jpg");
//        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
//        ossClient.putObject("gulimall-ciel", "alpy-shveitsariia-alps-gory-switzerland-mountains-derevia-tr.jpg", inputStream);
//        // 关闭OSSClient。
//        ossClient.shutdown();
//        System.out.println("上传完成");
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
