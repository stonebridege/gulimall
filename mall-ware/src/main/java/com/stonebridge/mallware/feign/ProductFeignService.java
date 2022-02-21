package com.stonebridge.mallware.feign;

import com.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {
    /**
     * 请求路径1：/mallproduct/skuinfo/info/{skuId}
     * 请求路径2：/api/mallproduct/skuinfo/info/{skuId}
     * <p>
     * 1)、让所有请求过网关；
     * 1、@FeignClient("gulimall-gateway")：给gulimall-gateway所在的机器发请求
     * 2、/api/product/skuinfo/info/{skuId}
     * 2）、直接让后台指定服务处理
     * 1、@FeignClient("gulimall-gateway")
     * 2、/product/skuinfo/info/{skuId}
     *
     * @return
     */
    @RequestMapping("/mallproduct/skuinfo/info/{skuId}")
    public Result info(@PathVariable("skuId") Long skuId);
}
