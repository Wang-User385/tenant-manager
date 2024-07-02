package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.pam.dto.HlsCusLeaseItem;
import com.hand.hls.pam.dto.HlsCusLeaseItemList;

import java.sql.SQLException;
import java.text.ParseException;

public interface IHlsCusLeaseItemListService extends IBaseService<HlsCusLeaseItemList>, ProxySelf<IHlsCusLeaseItemListService> {

    void receiptImport(IRequest iRequest, Long hdId,Long leaseItemId) throws ExcelException, SQLException, ParseException;





}
