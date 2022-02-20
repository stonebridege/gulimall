package com.stonebridge.mallware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import com.stonebridge.mallware.Vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.stonebridge.mallware.entity.PurchaseEntity;
import com.stonebridge.mallware.service.PurchaseService;
import com.common.utils.PageUtils;
import com.common.utils.Result;


/**
 * 采购信息
 *
 * @author stonebridge
 * @email stonebridge@njfu.edu.com
 * @date 2021-12-12 11:36:21
 */
@RestController
@RequestMapping("mallware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @PostMapping("merge")
    private Result merge(@RequestBody MergeVo mergeVo) {
        purchaseService.mergePurchase(mergeVo);
        return Result.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/unreceive/list")
    public Result unreceiveList(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPageUnreceive(params);
        return Result.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public Result list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);
        return Result.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return Result.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public Result save(@RequestBody PurchaseEntity purchase) {
        purchase.setUpdateTime(new Date());
        purchase.setCreateTime(new Date());
        purchaseService.save(purchase);
        return Result.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public Result update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return Result.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return Result.ok();
    }

}
