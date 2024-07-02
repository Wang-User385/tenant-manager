package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;

import java.util.Map;

/**
 * @Author: xuju
 * @Description:合同文本生成
 * @Date: Created in 2017/11/15 14:50
 * @Modified By:
 */
public interface HlsCusPrjContractDocxService {

    void process(IRequest requestContext, Map<String, Object> params) ;

    String processNotice(IRequest requestContext, Map<String, Object> params);
}
