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
    boolean preliminaryValid(Long projectId, HttpServletRequest request);

    //正审
    //Accept：通过
    //Reject：拒绝
    //Review：人工审核
    String interlocutoryValid(Long projectId, HttpServletRequest request);

}
