package com.hand.hls.partner.service;

public interface IAlipayService {

    String getPenetrateId(Long projectId);
    String sign(Long projectId);

}
