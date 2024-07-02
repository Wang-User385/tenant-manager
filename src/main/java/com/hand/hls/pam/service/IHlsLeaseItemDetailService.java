package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.pam.dto.HlsLeaseItemDetail;

import java.sql.SQLException;
import java.text.ParseException;

public interface IHlsLeaseItemDetailService extends IBaseService<HlsLeaseItemDetail>, ProxySelf<IHlsLeaseItemDetailService>{
    void receiptImportPledgeDc(IRequest iRequest, Long hdId, Long leaseItemId, String patternDet) throws ExcelException, SQLException, ParseException;
}