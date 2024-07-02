package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.cap.service.HlsCusCapFinancingPlanLnService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fin.dto.HlsCusLonContract;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.service.HlsCusICreditContractService;
import com.hand.hls.fin.service.HlsCusLonContractService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.gld.service.IHlsCusLonContractRepaymentMergeService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangyu on 2018/5/24.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusLonContractWithdrawSubmitServiceTask implements JavaDelegate, IActivitiBean,IHlsCusActivitiBean {
    @Autowired
    private HlsCusLonContractService lonContractService;
    @Autowired
    private HlsCusLonContractWithdrawService hlsCusLonContractWithdrawService;
    @Autowired
    private HlsCreditLineService hlsCreditLineService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private JeTrxCommonService commonService;
    @Autowired
    private HlsCusICreditContractService creditContractService;

    @Autowired
    private HlsCusCapFinancingPlanLnService capFinancingPlanLnService;
    @Autowired
    private IHlsCusLonContractRepaymentMergeService lonContractRepaymentMergeService;
    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";


    public HlsCusLonContractWithdrawSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = RequestHelper.getCurrentRequest();
        String result = (String) delegateExecution.getVariable("approveResult");
        String endTaskFlag = "N";
        if (delegateExecution.getVariable("endTaskFlag") != null) {
            endTaskFlag = delegateExecution.getVariable("endTaskFlag").toString();
        }

        if (endTaskFlag.equals("Y")) {
            //监听器执行了两遍，很奇怪，用标志来使其执行一遍
            return;
        }
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        String lonContractWithdrawPrams = (String) delegateExecution.getVariable("lonContractWithdraw");
        HlsCusLonContractWithdraw lonContractWithdraw = JSON.parseObject(lonContractWithdrawPrams, HlsCusLonContractWithdraw.class);
        HlsCusLonContractWithdraw resultLonContractWithdraw = new HlsCusLonContractWithdraw();
        //根据状态修改合同信息
        resultLonContractWithdraw.setWithdrawId(lonContractWithdraw.getWithdrawId());
        resultLonContractWithdraw = hlsCusLonContractWithdrawService.selectByPrimaryKey(requestCtx, resultLonContractWithdraw);
        if(resultLonContractWithdraw.getExchangeRate()==null){
            resultLonContractWithdraw.setExchangeRate(1D);
        }
        databaseLockProvider.lock(resultLonContractWithdraw);

        if ("APPROVED".equalsIgnoreCase(result)) {
            flag = "APPROVED";
        } else if ("CANCEL".equalsIgnoreCase(result)) {
            flag = "CANCEL";
        } else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
            flag = "APPROVED_RETURN";
        }else if ("REJECTED".equalsIgnoreCase(result)) {
            flag = "REJECTED";
        }

        resultLonContractWithdraw.setWithdrawStatus(result);
        resultLonContractWithdraw.setChangeStatus(result);
        HlsCusLonContract hlsCusLonContract = new HlsCusLonContract();
        hlsCusLonContract.setContractId(resultLonContractWithdraw.getContractId());
        hlsCusLonContract = lonContractService.selectByPrimaryKey(requestCtx, hlsCusLonContract);
        if ("APPROVED".equalsIgnoreCase(result)) {
            if ("NORMAL".equalsIgnoreCase(resultLonContractWithdraw.getDataClass())) {
                resultLonContractWithdraw.setApprovalDate(new Date());
//                resultLonContractWithdraw.setWriteOffFlag("FULL");
//                resultLonContractWithdraw.setWriteOffAmount(resultLonContractWithdraw.getDueAmount());

                //插入凭证事物流水表
                AbstractJeTrxService withdrawService = commonService.map.get("CSH_TRANSACTION_LOAN");
                Map params = new HashMap<>();
                params.put("jeTrxId", resultLonContractWithdraw.getWithdrawId());
                params.put("companyId", resultLonContractWithdraw.getCompanyId());
                params.put("contractId", resultLonContractWithdraw.getWithdrawId());
                params.put("sourceDoc", "LON_CONTRACT_WITHDRAW");
//                withdrawService.process(requestCtx, params);
            } else {
                try {
                    hlsCusLonContractWithdrawService.lonConWithdrawChangeReqConfirm(requestCtx, resultLonContractWithdraw);
                } catch (HlsCusException e) {
                    e.printStackTrace();
                }
                resultLonContractWithdraw.setApprovalDate(new Date());
            }
            //融资提款计提初始化
            if ("NORMAL".equalsIgnoreCase(resultLonContractWithdraw.getDataClass())) {
                lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(requestCtx, resultLonContractWithdraw,null);
            }else{
                HlsCusLonContractWithdraw lonContractWithdrawNormal = new HlsCusLonContractWithdraw();
                lonContractWithdrawNormal.setWithdrawId(resultLonContractWithdraw.getChangeReqId());
                lonContractWithdrawNormal = hlsCusLonContractWithdrawService.selectByPrimaryKey(requestCtx, lonContractWithdrawNormal);
                lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(requestCtx, lonContractWithdrawNormal,null);
            }
        }
        if("CHANGE_REQ".equalsIgnoreCase(resultLonContractWithdraw.getDataClass())){
            //变更状态
            resultLonContractWithdraw.setChangeStatus(flag);
        }
        hlsCusLonContractWithdrawService.updateByPrimaryKeySelective(requestCtx, resultLonContractWithdraw);

        delegateExecution.setVariable("endTaskFlag", "Y");
    }

    /**
     * 一键通过/一键拒绝
     * @param requestCtx
     * @param hlsCusProcess
     */
    @Override
    public void excute(IRequest requestCtx, HlsCusProcess hlsCusProcess) {
        String flag = null;
        String withdrawId = hlsCusProcess.getBussinessKey();
        HlsCusLonContractWithdraw resultLonContractWithdraw = new HlsCusLonContractWithdraw();
        //根据状态修改合同信息
        resultLonContractWithdraw.setWithdrawId(Long.parseLong(withdrawId));
        resultLonContractWithdraw = hlsCusLonContractWithdrawService.selectByPrimaryKey(requestCtx, resultLonContractWithdraw);
        if(resultLonContractWithdraw.getExchangeRate()==null){
            resultLonContractWithdraw.setExchangeRate(1D);
        }
        databaseLockProvider.lock(resultLonContractWithdraw);

        if (PASS.equals(hlsCusProcess.getType())) {
            flag = "APPROVED";
            resultLonContractWithdraw.setWithdrawStatus("APPROVED");
            resultLonContractWithdraw.setChangeStatus("APPROVED");

            if ("NORMAL".equalsIgnoreCase(resultLonContractWithdraw.getDataClass())) {
                resultLonContractWithdraw.setApprovalDate(new Date());
            } else {
                try {
                    hlsCusLonContractWithdrawService.lonConWithdrawChangeReqConfirm(requestCtx, resultLonContractWithdraw);
                } catch (HlsCusException e) {
                    e.printStackTrace();
                }
                resultLonContractWithdraw.setApprovalDate(new Date());
            }
            //融资提款计提初始化
            if ("NORMAL".equalsIgnoreCase(resultLonContractWithdraw.getDataClass())) {
                lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(requestCtx, resultLonContractWithdraw,null);
            }else{
                HlsCusLonContractWithdraw lonContractWithdrawNormal = new HlsCusLonContractWithdraw();
                lonContractWithdrawNormal.setWithdrawId(resultLonContractWithdraw.getChangeReqId());
                lonContractWithdrawNormal = hlsCusLonContractWithdrawService.selectByPrimaryKey(requestCtx, lonContractWithdrawNormal);
                lonContractRepaymentMergeService.calcLonConWithdrawFinCostNew(requestCtx, lonContractWithdrawNormal,null);
            }
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            flag = "REJECTED";
            resultLonContractWithdraw.setWithdrawStatus("REJECTED");
            resultLonContractWithdraw.setChangeStatus("REJECTED");
        }

        if("CHANGE_REQ".equalsIgnoreCase(resultLonContractWithdraw.getDataClass())){
            //变更状态
            resultLonContractWithdraw.setChangeStatus(flag);
        }
        hlsCusLonContractWithdrawService.updateByPrimaryKeySelective(requestCtx, resultLonContractWithdraw);
    }
}
