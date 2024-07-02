package com.hand.hls.layout.service;

import com.hand.hap.system.dto.ResponseData;
import leaf.bean.LeafRequestData;

import javax.servlet.http.HttpServletRequest;

public interface IDocLayoutValidationSqlService {
    ResponseData queryLov(LeafRequestData requestData, String sqlId, HttpServletRequest request);

    ResponseData queryBox(LeafRequestData requestData, HttpServletRequest request);
}
