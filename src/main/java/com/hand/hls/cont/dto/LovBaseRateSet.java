package com.hand.hls.cont.dto;

import leaf.annotation.LovField;

/**
 * @author : qzk
 * @date : 2020/3/17
 */
public class LovBaseRateSet {
    @LovField(prompt = "利率有效期从",field = "valid_from", forQuery = true, forDisplay = true, displayAlign ="left", displayWidth = 120)
    private String valid_from;
    @LovField(prompt = "调息使用利率",field = "base_rate_set", forQuery = true, forDisplay = true,displayAlign ="left", displayWidth = 120)
    private String base_rate_set;

}
