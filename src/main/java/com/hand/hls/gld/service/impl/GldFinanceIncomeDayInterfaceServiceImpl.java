package com.hand.hls.gld.service.impl;

import com.hand.hap.core.AppContextInitListener;
import com.hand.hap.core.IRequest;
import com.hand.hls.gld.service.IGldFinanceIncomeDayCommonService;
import com.hand.hls.gld.service.IGldFinanceIncomeDayInterfaceService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
@Service
public class GldFinanceIncomeDayInterfaceServiceImpl implements AppContextInitListener, IGldFinanceIncomeDayInterfaceService {

    private Map<String, IGldFinanceIncomeDayCommonService> commonService = new HashMap<String, IGldFinanceIncomeDayCommonService>();

    @Override
    public void contextInitialized(ApplicationContext applicationContext) {
        Map<String, IGldFinanceIncomeDayCommonService> map = applicationContext.getBeansOfType(IGldFinanceIncomeDayCommonService.class);
        map.forEach((k, v) -> {
            commonService.put(v.getShareType(), v);
        });
    }

    @Override
    public void start(IRequest iRequest, List list, Map params) throws Exception {
        String type = (String) params.get("shareType");
        IGldFinanceIncomeDayCommonService service = commonService.get(type);
        if (service != null) {
            try {
                service.process(iRequest, list, params);
            } catch (Exception e) {
                throw e;
            }
        }

    }
}
