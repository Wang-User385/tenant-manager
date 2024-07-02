package com.hand.hls.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsScoreCalculation;
import com.hand.hls.bp.mapper.HlsScoreCalculationMapper;
import com.hand.hls.bp.service.IHlsScoreCalculationService;
import com.hand.hls.pam.dto.AssetsDisposal;
import com.hand.hls.pam.dto.AssetsDisposalDetail;
import com.hand.hls.pam.mapper.AssetsDisposalDetailMapper;
import com.hand.hls.pam.mapper.AssetsDisposalMapper;
import com.hand.hls.pam.service.IAssetsDisposalDetailService;
import com.hand.hls.pam.service.IAssetsDisposalService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
@Transactional(rollbackFor = Exception.class)
public class AssetsDisposalSubmitServiceTask implements JavaDelegate, IActivitiBean {
    @Autowired
    private AssetsDisposalMapper assetsDisposalMapper;
    @Autowired
    private AssetsDisposalDetailMapper assetsDisposalDetailMapper;
    @Autowired
    private IAssetsDisposalService assetsDisposalService;
    @Autowired
    private IAssetsDisposalDetailService assetsDisposalDetailService;
    public AssetsDisposalSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String scoreId = delegateExecution.getProcessInstanceBusinessKey();

        AssetsDisposal assetsDisposal = assetsDisposalMapper.selectByPrimaryKey(scoreId);

        if ("APPROVED".equalsIgnoreCase(result)) {
            assetsDisposal.setApprovalStatus("APPROVED");
            AssetsDisposalDetail assetsDisposalDetail = new AssetsDisposalDetail();
            assetsDisposalDetail.setAssetsDisposalId(assetsDisposal.getAssetsDisposalId());
            List<AssetsDisposalDetail> assetsDisposalDetailList = assetsDisposalDetailMapper.select(assetsDisposalDetail);
            for(AssetsDisposalDetail assetsDisposalDetail1:assetsDisposalDetailList){
                //ASSETS_DISPOSAL_STAUTS
                assetsDisposalDetail1.setAssetsDisposalStauts("DISPOSED");
                assetsDisposalDetailService.updateByPrimaryKeySelective(requestCtx,assetsDisposalDetail1);
            }
        } else if ("REJECTED".equalsIgnoreCase(result)) {
            assetsDisposal.setApprovalStatus("REJECTED");
        }
        assetsDisposalService.updateByPrimaryKeySelective(requestCtx, assetsDisposal);
    }

}
