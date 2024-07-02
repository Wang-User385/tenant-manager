package com.hand.hls.fct.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fct.dto.HlsCusFctProjectFinStatement;
import com.hand.hls.fct.dto.HlsCusHlsCreditLine;
import com.hand.hls.prj.dto.HlsCusCreditInfo;

import java.text.ParseException;
import java.util.List;

public interface HlsCreditLineService extends IBaseService<HlsCusHlsCreditLine>, ProxySelf<HlsCreditLineService> {

    List<HlsCusFctProjectFinStatement> queryFinStatementByBpId(HlsCusHlsCreditLine cusHlsCreditLine);

    List<HlsCusHlsCreditLine> creditLineDetailQuery(HlsCusHlsCreditLine hlsCusHlsCreditLine);

    /*保存授信评审信息*/
    HlsCusHlsCreditLine hlscreditLineSave(IRequest iRequest, HlsCusCreditInfo hlsCusCreditInfo);

    /*提交授信评审信息*/
    HlsCusHlsCreditLine hlscreditLineSubmit(IRequest iRequest, HlsCusCreditInfo hlsCusCreditInfo);

    List<HlsCusHlsCreditLine> selectCreditLineInfo(HlsCusHlsCreditLine hlsCusHlsCreditLine, int page, int pageSize) throws ParseException;

    /**
     * 更新提款余额
     * @param creditLineId
     * @return
     */
    int updateWithdrawBalance(Long creditLineId);

    /**
     * 更新行已用金额
     * @param creditLineId
     * @return
     */
    int updateCreditExposureAmt(Long creditLineId);





}
