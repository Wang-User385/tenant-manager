package com.hand.hls.prj.dto;

/**
 * @Author Robert8900
 * @Date: 2019/8/20 10:42
 * @Description:计算pmt
 * @Purpose: 等额租金
 **/


/**
 * PMT公式
 * @param  pRate  利率
 * @param  pNper  还款期数
 * @param  pPv 融资额
 * @param  pFv 终值
 * @param  pType 先付后付,先付为1，后付为0
 * @return
 */

public class HlsCusCalcPmt {
    public static Double Pmt(Double pRate,Double pNper,Double pPv,Double pFv, Long pType) {
        Double pmt = 0D;
        Double r = pRate;
        Double R1 = 1D+r;
        Double n = pNper;
        Double pv = pPv;
        Double fv = 0D;
        Long ct = 0L;

        if ("".equals(pFv) || pFv == null) {
            fv = 0D;
        }else{
            fv = pFv;
        }
        if ("".equals(pType) || pType == null) {
            ct = 0L;
        }else{
            ct = pType;
        }

        pmt = - (pv * Math.pow(R1, n) + fv) * r /
                ((1 + r * ct) * (Math.pow(R1, n) - 1));

        return pmt;
    }
}
