package com.hand.hls.prj.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.utils.ResMessageException;

import java.util.Date;
import java.util.List;
import java.util.Map;


public interface IPrjQuotationService extends IBaseService<HlsCusPrjQuotation>, ProxySelf<IPrjQuotationService> {

    HlsCusPrjQuotation calcPrjQuotation(IRequest iRequest, HlsCusPrjQuotation HlsCusPrjQuotation) throws Exception;

    //复制一份新的报价(prj_quotaion,prj_quotation_cashflow,prj_quotation_cash_flow)
    HlsCusPrjQuotation copyQuotationRelated(IRequest iRequest, Long quotationId, Long newSourceDocumentId, String newSourceDocumentCategory);

    List<Map> getApproveInfo(Long projectId, int pageNum, int pageSize);

    List<HlsCusPrjQuotation> queryPrjQuotationDetail(Map map);

    void spinOff(IRequest iRequest, HlsCusConContract contract) throws ResMessageException;

    List<HlsCusPrjQuotation> querySpinQuotationDetail(Map map);

    List<HlsCusPrjQuotation> querySpinQuotationApproved(Map map);

    //查询审批提交的合同拆分报价
    List<HlsCusPrjQuotation> queryApprovingSpinQuotation(Long contractId, Long processInstanceId);

    //寄送管理查询合同报价
    List<HlsCusPrjQuotation> queryContractQuotationList(HlsCusPrjQuotation hlsCusPrjQuotation, int pageNum, int pageSize);

    //起租时更新报价日期
    void updateSheetWhenIncept(IRequest iRequest, Long quotationId, Date inceptionOfLease);

}
