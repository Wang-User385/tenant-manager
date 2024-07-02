package com.hand.hls.pam.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.pam.dto.HlsCusHlsLeaseItemWarrant;

import java.sql.SQLException;
import java.text.ParseException;

public interface HlsCusHlsLeaseItemWarrantService extends IBaseService<HlsCusHlsLeaseItemWarrant>, ProxySelf<HlsCusHlsLeaseItemWarrantService>{

    void warrantImport(IRequest iRequest, Long hdId , Long projectId) throws ExcelException, Exception, ParseException;
}