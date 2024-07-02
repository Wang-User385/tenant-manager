package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.dto.MarketingReportChange;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;

import java.rmi.NoSuchObjectException;
import java.util.List;

public interface IMarketingReportChangeService extends IBaseService<MarketingReportChange>, ProxySelf<IMarketingReportChangeService>{

    List<MarketingReportChange> changeCreate(IRequest iRequest, MarketingReportChange marketingReportChange) throws ResMessageException, ParameterNullException, NoSuchObjectException;
    void leaveHistory(IRequest iRequest, MarketingReportChange marketingReportChange, Long processInstanceId) throws Exception;
    List<MarketingReportChange> changeSubmit(IRequest iRequest, MarketingReportChange marketingReportChange) throws ResMessageException, ParameterNullException, NoSuchObjectException;
    void cancelChangeReq(IRequest iRequest, Long changeReqId) throws NoSuchObjectException;
    List<HlsCusHlsMarketingReportBp> deleteBpChangeReq(IRequest iRequest, Long marketingReportId, Long marketingReportBpId) throws Exception;
    int deleteAttachmentChangeReq(IRequest iRequest,List<HlsCusHlsReportAttachment> marketingAttachmentIds) throws Exception;
}