package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.dto.HlsCusDepositDeduction;
import com.hand.hls.csh.dto.HlsCusDepositDeductionHd;
import com.hand.hls.csh.mapper.HlsCusDepositDeductionHdMapper;
import com.hand.hls.csh.mapper.HlsCusDepositDeductionMapper;
import com.hand.hls.csh.service.CshWriteOffService;
import com.hand.hls.csh.service.HlsCusDepositDeductionService;
import com.hand.hls.gld.components.JeTrxCommonService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName HlsCusDepositDeductionSubmitServiceTask
 * @Description //TODO
 * @Author yuan.yuan01@hand-china.com
 * @Date 2019/3/7 10:27 PM
 * @Version 1.0
 **/
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusDepositDeductionSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private HlsCusDepositDeductionHdMapper depositDeductionHdMapper;

    @Autowired
    private HlsCusDepositDeductionMapper depositDeductionMapper;

    @Autowired
    private HlsCusDepositDeductionService depositDeductionService;


    @Autowired
    private CshWriteOffService cshWriteOffService;

    @Override
    public void execute(DelegateExecution delegateExecution) {
        IRequest iRequest = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long depositDeductionHdId = (Long) delegateExecution.getVariable("documentId");
        String documentType = (String) delegateExecution.getVariable("documentType");
        HlsCusDepositDeductionHd depositDeductionHd=new HlsCusDepositDeductionHd();
        depositDeductionHd.setDepositDeductionHdId(depositDeductionHdId);

        if("APPROVED".equalsIgnoreCase(result)){
            //头数据
            depositDeductionHd=depositDeductionHdMapper.selectHlsCusDepositDeductionData(depositDeductionHdId);

            //行数据
            HlsCusDepositDeduction depositDeduction=new HlsCusDepositDeduction();
            depositDeduction.setDepositDeductionHdId(depositDeductionHdId);
            List<HlsCusDepositDeduction> depositDeductions = depositDeductionMapper.selectDepositDedctionData(depositDeduction);

            //保证金抵扣
            if ("DEPOSIT_DEDUCT_CREDIT".equalsIgnoreCase(documentType)) {
                List<HlsCusCshWriteOff> cshwriteoffs=new ArrayList<>();
                for(HlsCusDepositDeduction deduction:depositDeductions) {
                    HlsCusCshWriteOff cshWriteOff=new HlsCusCshWriteOff();
                    cshWriteOff.setWriteOffType("DEPOSIT_CREDIT");
                    cshWriteOff.setCshTransactionId(deduction.getTransactionId());
                    cshWriteOff.setTransactionId(deduction.getTransactionId());
                    cshWriteOff.setContractId(deduction.getContractId());
                    cshWriteOff.setWriteOffDocCategory(deduction.getDeductionDocCategory());
                    cshWriteOff.setCashflowId(deduction.getCashflowId());
                    cshWriteOff.setCfType(deduction.getCfType());
                    cshWriteOff.setCfItem(deduction.getCfItem());
                    cshWriteOff.setTimes(deduction.getTimes());
                    cshWriteOff.setBpId(deduction.getBpId());
                    cshWriteOff.setWriteOffDueAmount(deduction.getDeductionAmount());
                    cshWriteOff.setCshWriteOffAmount(deduction.getDeductionAmount());
                    cshWriteOff.setWriteOffInterest(deduction.getDeductionInterest());
                    cshWriteOff.setWriteOffPrincipal(deduction.getDeductionPrincipal());
                    cshWriteOff.setWriteOffDate(depositDeductionHd.getDeductionDate());
                    cshWriteOff.setImportFlag("N");
                    cshwriteoffs.add(cshWriteOff);
                }
                try {
                    cshWriteOffService.updateWriteOff(iRequest,cshwriteoffs,iRequest.getCompanyId());
                }catch (Exception e){
                    e.printStackTrace();
                    throw new RuntimeException(e.getMessage());
                }


            } else if("DEPOSIT_ADD_CREDIT".equalsIgnoreCase(documentType)){
                //保证金垫付
                for(HlsCusDepositDeduction deduction:depositDeductions) {
                    try {
                        depositDeductionService.depositDeductionAdd(iRequest, deduction);
                    }catch (Exception e){
                        e.printStackTrace();
                        throw new RuntimeException(e.getMessage());
                    }

                }

            }
            depositDeductionHd.setDepositStatus("APPROVED");
        }else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            depositDeductionHd.setDepositStatus("APPROVED_RETURN");
        }else{
            depositDeductionHd.setDepositStatus("REJECTED");
        }

        depositDeductionHdMapper.updateByPrimaryKeySelective(depositDeductionHd);
    }
}
