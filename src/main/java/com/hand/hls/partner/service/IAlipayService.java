package com.hand.hls.partner.service;

import hls.core.utils.exception.HlsCusException;

public interface IAlipayService {

    String getPenetrateId(Long projectId) throws HlsCusException;
    String sign(Long projectId) throws HlsCusException;
    void signQuery(Long projectId) throws HlsCusException;
    void signCancel(Long projectId) throws HlsCusException;
    void withhold(Long orderId) throws HlsCusException;
    void withholdQuery(Long orderId) throws HlsCusException;
    void withholdCancel(Long orderId) throws HlsCusException;
}
