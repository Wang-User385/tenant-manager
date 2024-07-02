package com.hand.hls.gld.service;

import com.hand.hap.core.IRequest;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/5/7
 * @description: 收益分摊 每日摊销
 */
public interface IGldFinanceIncomeDayCommonService<T> {
    String getShareType();

    void process(IRequest iRequest, List<T> list, Map params) throws Exception;

}