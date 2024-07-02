package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.mapper.HlsCusFundTransferListMapper;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.utils.HlsCusConstant;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * description
 *
 * @author yuanyuan 2019/07/25 9:14 PM
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusEftChangeTransferSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusFundTransferService fundTransferService;

    @Autowired
    private HlsCusFundTransferListMapper fundTransferListMapper;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long transferId = Long.parseLong(delegateExecution.getProcessInstanceBusinessKey());
        HlsCusFundTransfer fundTransfer = new HlsCusFundTransfer();
        fundTransfer.setTransferId(transferId);
        fundTransfer.setApplyStatus(result);
        fundTransferService.updateByPrimaryKeySelective(requestCtx, fundTransfer);
        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equals(result)) {
            //审批通过更新原始头状态
            //fundTransfer=fundTransferService.selectByPrimaryKey(requestCtx,fundTransfer);

            HlsCusFundTransferList fundTransferList = new HlsCusFundTransferList();
            fundTransferList.setTransferId(transferId);
            List<HlsCusFundTransferList> transferLists = fundTransferListMapper.selectFundTransferList(fundTransferList);
            //更新原始行数据申请金额
            for (HlsCusFundTransferList transferList : transferLists) {
                fundTransferList.setTransferId(transferList.getRefTransferListId());
                fundTransferList.setApplyPayAmount(transferList.getApplyPayAmount());
                fundTransferListMapper.updateByPrimaryKeySelective(fundTransferList);
            }

        }
    }
}
