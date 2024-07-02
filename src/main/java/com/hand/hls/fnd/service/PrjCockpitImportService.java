package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.PrjCockpitImport;

import java.sql.SQLException;
import java.text.ParseException;

public interface PrjCockpitImportService extends IBaseService<PrjCockpitImport>, ProxySelf<PrjCockpitImportService> {
    void receiptImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException;
}