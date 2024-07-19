package com.hand.hls.credit.service;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * description
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/12 11:30
 */
public interface TongDunService {

    //预审
    //Accept：通过
    //Reject：拒绝
    //Repeat: 重复审批订单
    //Error：参数异常
    String preliminaryValid(Long projectId, HttpServletRequest request);


    //正审
    //Accept：通过
    //Reject：拒绝
    //Review：人工审核
    //Repeat: 重复审批订单
    //Error：参数异常
    String interlocutoryValid(Long projectId, HttpServletRequest request);

}
