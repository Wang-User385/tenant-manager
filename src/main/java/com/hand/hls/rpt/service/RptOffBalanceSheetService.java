package com.hand.hls.rpt.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.rpt.dto.RptOffBalanceSheet;

import java.text.ParseException;
import java.util.List;

public interface RptOffBalanceSheetService extends IBaseService<RptOffBalanceSheet>, ProxySelf<RptOffBalanceSheetService>{
    List<RptOffBalanceSheet> createRptOffBalanceSheet(IRequest iRequest, Long contractId) throws ParseException;
}