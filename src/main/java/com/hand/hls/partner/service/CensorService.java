package com.hand.hls.partner.service;

import com.hand.hls.exception.HlsCusException;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 审查接口
 * </p>
 *
 * @author JINGZHOU.LI@hand-china.com 2024/7/11 16:17
 */
public interface CensorService {
    //预审
    boolean preliminaryValid(Long projectId, HttpServletRequest request) throws HlsCusException;

    //正审
    boolean interlocutoryValid(Long projectId, HttpServletRequest request) throws HlsCusException;

}
