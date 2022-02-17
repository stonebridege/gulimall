package com.stonebridge.mallcoupon.service.impl;

import com.common.to.MemberPrice;
import com.common.to.SkuReductionTo;
import com.stonebridge.mallcoupon.entity.MemberPriceEntity;
import com.stonebridge.mallcoupon.entity.SkuLadderEntity;
import com.stonebridge.mallcoupon.service.MemberPriceService;
import com.stonebridge.mallcoupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
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

import com.stonebridge.mallcoupon.dao.SkuFullReductionDao;
import com.stonebridge.mallcoupon.entity.SkuFullReductionEntity;
import com.stonebridge.mallcoupon.service.SkuFullReductionService;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<>()
        );
        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        //保存满减打折信息、会员价
        //1.sms_sku_ladder(sku打折表)
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setSkuId(skuReductionTo.getSkuId());
        skuLadderEntity.setFullCount(skuReductionTo.getFullCount());
        skuLadderEntity.setDiscount(skuReductionTo.getDiscount());
        skuLadderEntity.setAddOther(skuReductionTo.getCountStatus());
        skuLadderService.save(skuLadderEntity);
        //2.sms_sku_full_reduction(满减表)
        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReduction);
        this.save(skuFullReduction);
        //3.sms_member_price(会员价格表)
        List<MemberPrice> memberPrices = skuReductionTo.getMemberPrice();
        List<MemberPriceEntity> list = memberPrices.stream().map(item -> {
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
            memberPriceEntity.setMemberLevelId(item.getId());
            memberPriceEntity.setMemberLevelName(item.getName());
            memberPriceEntity.setMemberPrice(item.getPrice());
            memberPriceEntity.setAddOther(1);
            return memberPriceEntity;
        }).collect(Collectors.toList());
        memberPriceService.saveBatch(list);
    }
}