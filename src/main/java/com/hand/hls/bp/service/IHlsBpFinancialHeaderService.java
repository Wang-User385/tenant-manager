package com.hand.hls.bp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;

import java.util.List;

public interface IHlsBpFinancialHeaderService extends IBaseService<HlsBpFinancialHeader>, ProxySelf<IHlsBpFinancialHeaderService>{

    List<HlsBpFinancialHeader> selectBpExportInfo(IRequest iRequest,HlsBpFinancialHeader hlsBpFinancialHeader,int pagenum, int pagesize);

}