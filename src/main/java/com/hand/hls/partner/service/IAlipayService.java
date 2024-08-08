package com.hand.hls.partner.service;

import hls.core.utils.exception.HlsCusException;

public interface IAlipayService {

    String getPenetrateId(Long projectId);
    String sign(Long projectId);
    void signQuery(Long projectId);
    void signCancel(Long projectId) throws HlsCusException;
    void withhold(Long orderId) throws HlsCusException;
    void withholdQuery(Long orderId) throws HlsCusException;
    void withholdCancel(Long orderId) throws HlsCusException;
}
