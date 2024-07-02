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
 * @description:
 */
public interface IGldFinanceIncomeDayInterfaceService<T> {

    void start(IRequest iRequest, List<T> list, Map params) throws Exception;

}
