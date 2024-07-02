package com.hand.hls.hls.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.dto.HlsMarketingReport;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.sys.dto.SysTemplate;
import com.hand.hls.utils.ResMessageException;
import leaf.service.validation.ParameterNullException;
import uncertain.composite.CompositeMap;

import java.util.List;
import java.util.Map;

public interface HlsCusHlsMarketingReportService extends IBaseService<HlsCusHlsMarketingReport>, ProxySelf<HlsCusHlsMarketingReportService>{
    //合同文本
    public static final String REPORT_DOCX = "HLS_MARKETING_REPORT_DOCX";
    public static final String DATA_ECXEPTION = "数据异常";
    //附件表名
    public static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    public static final String NOT_FOUND_CONTRACT_TEMPLATE  = "找不到对应的合同文本模板";
    public static final String TABLE_NAME = "table_name";
    public static final String CONTRACT_DOCX_DESCRIPTION = "业务意向书";

    List<HlsCusHlsMarketingReport> marketingReportSubmit(IRequest iRequest, HlsCusHlsMarketingReport hlsCusHlsMarketingReport) throws ResMessageException, ParameterNullException;
    List<HlsCusHlsMarketingReport> selectMarketingHomeQuery(IRequest iRequest,HlsCusHlsMarketingReport hlsCusHlsMarketingReport,int page,int pageSize);
    String generateAuthorityString(IRequest iRequest);
    ResponseData queryMarketingReport(CompositeMap var1, String var2);
    List<HlsCusHlsMarketingReport> selectDetail(IRequest var1, HlsCusHlsMarketingReport hlsCusHlsMarketingReport, int var3, int var4);

    List<FndAttachment> reportCreateDocx(IRequest iRequest, Long marketingReprotId, String templateType) throws Exception;
    List<FndAttachment> reportCreateDocx(IRequest iRequest, HlsCusHlsMarketingReport hlsCusHlsMarketingReport) throws Exception;

}