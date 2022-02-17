package com.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundTo {
    public Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
