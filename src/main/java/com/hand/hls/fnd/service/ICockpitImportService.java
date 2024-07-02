package com.hand.hls.fnd.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fnd.dto.CockpitImport;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public interface ICockpitImportService extends IBaseService<CockpitImport>, ProxySelf<ICockpitImportService> {
    void receiptImport(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException;

    List<CockpitImport> queryAdmin(IRequest iRequest, CockpitImport cockpitImport, int pagenum, int pagesize);

    List<CockpitImport> queryProject(IRequest iRequest, CockpitImport cockpitImport, int pagenum, int pagesize);
}