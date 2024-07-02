package com.hand.hls.fp.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fp.dto.JcFundPlanSchedule;

import java.sql.SQLException;
import java.text.ParseException;

public interface JcFundPlanScheduleService extends IBaseService<JcFundPlanSchedule>, ProxySelf<JcFundPlanScheduleService>{
    void receiptImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException;
}