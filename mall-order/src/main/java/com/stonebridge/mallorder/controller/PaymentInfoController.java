package com.stonebridge.mallorder.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stonebridge.mallorder.entity.PaymentInfoEntity;
import com.stonebridge.mallorder.service.PaymentInfoService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 支付信息表
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:25:00
 */
@RestController
@RequestMapping("mallorder/paymentinfo")
public class PaymentInfoController {
    @Autowired
    private PaymentInfoService paymentInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = paymentInfoService.queryPage(params);

        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        PaymentInfoEntity paymentInfo = paymentInfoService.getById(id);

        return Result.ok().put("paymentInfo", paymentInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody PaymentInfoEntity paymentInfo) {
        paymentInfoService.save(paymentInfo);

        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody PaymentInfoEntity paymentInfo) {
        paymentInfoService.updateById(paymentInfo);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        paymentInfoService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
