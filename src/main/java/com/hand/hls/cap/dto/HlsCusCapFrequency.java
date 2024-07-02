package com.hand.hls.cap.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * description
 *
 * @author yuanyuan 2019/08/23 10:17 AM
 */
@Getter
@Setter
public class HlsCusCapFrequency implements Serializable {


    private String dateDesc;


    private BigDecimal dueAmount;

}
