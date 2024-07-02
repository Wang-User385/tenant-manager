package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.FndInterfaceLines;

import java.util.List;

/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: Eugene Song
 * @date: 2020/4/22
 * @description:
 */
public interface FndInterfaceLinesService extends IBaseService<FndInterfaceLines>, ProxySelf<FndInterfaceLinesService> {

    List<FndInterfaceLines> fndInterfaceLinesDetailQuery(IRequest iRequest, FndInterfaceLines fndInterfaceLines,int page,int pageSize);
}
