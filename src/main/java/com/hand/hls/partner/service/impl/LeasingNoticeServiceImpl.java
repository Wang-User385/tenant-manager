package com.hand.hls.partner.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.partner.dto.AssetNeedBuybackDto;
import com.hand.hls.partner.dto.AssetNeedSubstituteDto;
import com.hand.hls.partner.dto.LeasingNotice;
import com.hand.hls.partner.service.ILeasingNoticeService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;


@Service
@Transactional(rollbackFor = Exception.class)
public class LeasingNoticeServiceImpl extends BaseServiceImpl<LeasingNotice> implements ILeasingNoticeService {

    @Autowired
    private IYLMessageNoticeService messageNoticeService;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void insertNoticeMsg(IRequest iRequest, LeasingNotice leasingNotice) {
        leasingNotice.setNoticeId(null);
        self().insertSelective(iRequest, leasingNotice);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = Exception.class)
    public void updateLog(IRequest iRequest, LeasingNotice leasingNotice) {
        self().updateByPrimaryKey(iRequest, leasingNotice);
    }


    @Override
    public void noticeRePush(LeasingNotice leasingNotice, IRequest iRequest) {
        //通过主键ID查询需要重新推送消息的数据
        LeasingNotice leasingNoticeNew = this.selectByPrimaryKey(iRequest, leasingNotice);
        leasingNoticeNew.setResendFlag("Y");
        self().updateLog(iRequest, leasingNoticeNew);
        if (!ObjectUtils.isEmpty(leasingNoticeNew)) {
            if (leasingNoticeNew.getSourceType().indexOf("n001") != -1) {
                messageNoticeService.orderAuditResult(leasingNoticeNew.getSourceId(), leasingNoticeNew.getScene(), iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n002") != -1) {
                messageNoticeService.orderLoanResult(leasingNoticeNew.getSourceId(), iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n003") != -1) {
                messageNoticeService.orderClosedNotify(leasingNoticeNew.getSourceId(), iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n004") != -1) {
                messageNoticeService.repayPlanCreatedNotify(leasingNoticeNew.getSourceId(), iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n005") != -1) {
                messageNoticeService.overdueCalculateFinishedNotify(iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n006") != -1) {
                AssetNeedSubstituteDto assetNeedSubstituteDto = new AssetNeedSubstituteDto();
                assetNeedSubstituteDto.setCashflowId(leasingNoticeNew.getSourceId());
                messageNoticeService.assetNeedSubstitute(assetNeedSubstituteDto, iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n007") != -1) {
                AssetNeedBuybackDto assetNeedBuyback = new AssetNeedBuybackDto();
                assetNeedBuyback.setContractId(leasingNoticeNew.getSourceId());
                messageNoticeService.assetNeedBuyback(assetNeedBuyback, iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n008") != -1) {
                messageNoticeService.withholdContractResult(leasingNoticeNew.getSourceId(), iRequest);
            } else if (leasingNoticeNew.getSourceType().indexOf("n009") != -1) {
                messageNoticeService.repayPlanRepaidNotify(leasingNoticeNew.getSourceId(), iRequest);
            }
        }
    }
}
