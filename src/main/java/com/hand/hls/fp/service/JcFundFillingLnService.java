package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundFillingLn;
import uncertain.composite.CompositeMap;

import java.util.List;

public interface JcFundFillingLnService extends IBaseService<JcFundFillingLn>, ProxySelf<JcFundFillingLnService>{
    List<JcFundFillingLn> selectDetailAll(IRequest iRequest,JcFundFillingLn jcFundFillingLn,int page,int pageSize);
    /**
     * 页面字段描述查询
     * var1
     * var2
    */
    ResponseData queryFiledPrompt(CompositeMap var1, String var2);
}