package com.hand.hap.activiti.components;

import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hls.abs.service.HlsCusAbsProFeeInfoService;
import com.hand.hls.abs.service.HlsCusAbsProductBuybackService;
import com.hand.hls.abs.service.HlsCusAbsProductCollectionService;
import com.hand.hls.abs.service.HlsCusAbsProductRepaymentService;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.eft.dto.HlsCusFundTransfer;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import com.hand.hls.eft.mapper.HlsCusFundTransferListMapper;
import com.hand.hls.eft.mapper.HlsCusFundTransferMapper;
import com.hand.hls.eft.service.HlsCusFundTransferService;
import com.hand.hls.fin.dto.HlsCusContractRepaymentLn;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.exception.HlsCusException;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description
 *
 * @author yuanyuan 2019/07/25 5:27 PM
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusEftFinanceTransferSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusFundTransferService fundTransferService;

    @Autowired
    private HlsCusFundTransferListMapper fundTransferListMapper;

    @Autowired
    private HlsCusLonContractRepaymentService lonContractRepaymentService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCusAbsProductRepaymentService absProductRepaymentService;

    @Autowired
    private HlsCusAbsProFeeInfoService absProFeeInfoService;

    @Autowired
    private HlsCusAbsProductCollectionService absProductCollectionService;

    @Autowired
    private HlsCusAbsProductBuybackService absProductBuybackService;

    @Autowired
    private JeTrxCommonService commonService;

    @Autowired
    private HlsCusFundTransferMapper fundTransferMapper;

    @Override
    public void execute(DelegateExecution delegateExecution){
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        Long companyId = Long.parseLong(delegateExecution.getVariable("companyId").toString());
        Long transferId = Long.parseLong(delegateExecution.getVariable("transferId").toString());
        HlsCusFundTransfer fundTransfer = new HlsCusFundTransfer();
        fundTransfer.setTransferId(transferId);
        fundTransfer.setApplyStatus(result);
        fundTransferService.updateByPrimaryKeySelective(requestCtx, fundTransfer);
        if (HlsCusConstant.WORKFLOW_STATUS.APPROVED.equals(result)) {
            HlsCusFundTransferList fundTransferList = new HlsCusFundTransferList();
            fundTransferList.setFinTransferId(transferId);
            fundTransferList.setDataClass(HlsCusConstant.DATA_CLASS.NORMAL);
            List<HlsCusFundTransferList> transferLists = fundTransferListMapper.selectFundTransferList(fundTransferList);
            for (HlsCusFundTransferList transferList : transferLists) {
                transferList.setTransferStatus(HlsCusConstant.WORKFLOW_STATUS.CONFIRM);
                fundTransferListMapper.updateByPrimaryKeySelective(transferList);
                if(transferList.getTransferType()!=null) {
                    if (HlsCusConstant.TRANSFER_TYPE.PAY.equals(transferList.getTransferType())) {

                        //插入变更数据
                        if (transferList.getApplyPayAmount().compareTo(transferList.getActualPayAmount()) != 0) {

                            HlsCusFundTransfer transferNewChange = fundTransferService.selectFundTransferNewChange(transferList.getTransferId());

                            if (transferNewChange == null) {
                                //资金部发起的头信息
                                HlsCusFundTransfer transfer = new HlsCusFundTransfer();
                                transfer.setTransferId(transferList.getTransferId());
                                transfer = fundTransferService.selectByPrimaryKey(requestCtx, transfer);
                                HlsCusFundTransfer changeTransfer = new HlsCusFundTransfer();
                                Map<String, String> transferMap = hlsBeanRefUtilService.getFieldValueMap(transfer);
                                hlsBeanRefUtilService.setFieldValue(changeTransfer, transferMap);
                                changeTransfer.setRefTransferId(transfer.getTransferId());
                                changeTransfer.setTransferId(null);
                                changeTransfer.setDataClass(HlsCusConstant.DATA_CLASS.CHANGE);
                                changeTransfer.setBusinessType(HlsCusConstant.TRANSFER_BUSINESS_TYPE.FUND_TYPE);
                                changeTransfer.setApplyStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
                                changeTransfer.setCreatedBy(transfer.getCreatedBy());
                                changeTransfer.setLastUpdatedBy(transfer.getLastUpdatedBy());
                                fundTransferMapper.insertSelective(changeTransfer);
                                transferNewChange = new HlsCusFundTransfer();
                                transferNewChange.setTransferId(changeTransfer.getTransferId());
                            }

                            HlsCusFundTransferList changeTransferList = new HlsCusFundTransferList();
                            Map<String, String> transferListMap = hlsBeanRefUtilService.getFieldValueMap(transferList);
                            hlsBeanRefUtilService.setFieldValue(changeTransferList, transferListMap);
                            changeTransferList.setFinTransferId(null);
                            changeTransferList.setExchangeRate(transferList.getExchangeRate());
                            changeTransferList.setPlannedDueAmount(transferList.getPlannedDueAmount());
                            changeTransferList.setApplyPayAmount(transferList.getApplyPayAmount());
                            changeTransferList.setActualPayAmount(transferList.getActualPayAmount());
                            changeTransferList.setRefTransferListId(transferList.getTransferListId());
                            changeTransferList.setTransferListId(null);
                            changeTransferList.setTransferId(transferNewChange.getTransferId());
                            changeTransferList.setDataClass(HlsCusConstant.DATA_CLASS.CHANGE);
                            fundTransferListMapper.insertSelective(changeTransferList);
                        }


                        //融资还款
                        if (HlsCusConstant.TRANSFER_LIST_CATEGORY.LON_CONTRACT_REPAYMENT.equals(transferList.getSourceDocCategory())) {

                            HlsCusContractRepaymentLn repaymentLn = new HlsCusContractRepaymentLn();
                            repaymentLn.setWithdrawId(transferList.getSourceDocId());
                            repaymentLn.setRepaymentId(transferList.getSourceDocLineId());
                            repaymentLn.setBankAccountId(transferList.getOutBankAccountId());
                            repaymentLn.setBankAccountName(transferList.getOutBankAccountName());
                            repaymentLn.setBankBranchName(transferList.getOutBankBranchName());
                            repaymentLn.setBankAccountNum(transferList.getOutBankAccountNum());
                            repaymentLn.setDueDate(transferList.getActualPayDate());
                            repaymentLn.setDueAmount(transferList.getActualPayAmount().doubleValue());
                            if (transferList.getExchangeRate() != null) {
                                repaymentLn.setCnyDueAmount(transferList.getActualPayAmount()
                                        .multiply(transferList.getExchangeRate()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                            }
                            try {
                                lonContractRepaymentService.confirmFundRepayment(requestCtx, repaymentLn);
                            } catch (HlsCusException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }

                            //其他发债本息
                        } else if (HlsCusConstant.TRANSFER_LIST_CATEGORY.CT_ABS_PRODUCT_REPAYMENT.equals(transferList.getSourceDocCategory())) {

                            try {
                                absProductRepaymentService.confirmProductRepayment(requestCtx, transferList);
                            } catch (HlsCusException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }
                            //费用
                        } else if (HlsCusConstant.TRANSFER_LIST_CATEGORY.CT_ABS_PRODUCT_FEE.equals(transferList.getSourceDocCategory())
                                || HlsCusConstant.TRANSFER_LIST_CATEGORY.CT_ABS_PROJECT_FEE.equals(transferList.getSourceDocCategory())) {

                            try {
                                absProFeeInfoService.confirmAbsProFee(requestCtx, transferList);
                            } catch (HlsCusException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }

                            //归集
                        } else if (HlsCusConstant.TRANSFER_LIST_CATEGORY.CT_ABS_PRODUCT_COLLECTION.equals(transferList.getSourceDocCategory())) {

                            try {
                                absProductCollectionService.confirmProductCollection(requestCtx, transferList);
                            } catch (HlsCusException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }

                            //转付
                        } else if (HlsCusConstant.TRANSFER_LIST_CATEGORY.CT_ABS_PRODUCT_REMITTANCE.equals(transferList.getSourceDocCategory())) {

                            try {
                                absProductCollectionService.confirmProductRemittance(requestCtx, transferList);
                            } catch (HlsCusException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }

                            //回购
                        } else if (HlsCusConstant.TRANSFER_LIST_CATEGORY.CT_ABS_PRODUCT_BUYBACK.equals(transferList.getSourceDocCategory())) {

                            try {
                                absProductBuybackService.confirmReceiverProductBuyback(requestCtx, transferList);
                            } catch (HlsCusException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e.getMessage());
                            }

                        }

                        //插入凭证事物流水表
                        if (!HlsCusConstant.TRANSFER_LIST_CATEGORY.LON_CONTRACT_REPAYMENT.equals(transferList.getSourceDocCategory())) {
                            AbstractJeTrxService transferService = JeTrxCommonService.map.get("ABS_PAYMENT");
                            Map params = new HashMap<>();
                            params.put("jeTrxId", transferList.getTransferListId());
                            params.put("companyId", companyId);
                            params.put("contractId", transferList.getTransferListId());
                            params.put("sourceDoc", "EFT_FUND_TRANSFER_LIST");
                            transferService.process(requestCtx, params);
                        }
                    } else if(HlsCusConstant.TRANSFER_TYPE.TRANSFER.equals(transferList.getTransferType())){
                        //插入凭证事物流水表
                        AbstractJeTrxService transferService = JeTrxCommonService.map.get("TRANSFER_OF_FINANCIAL_RESOURCES");
                        Map params = new HashMap<>();
                        params.put("jeTrxId", transferList.getTransferListId());
                        params.put("companyId", companyId);
                        params.put("contractId", transferList.getTransferListId());
                        params.put("sourceDoc", "EFT_FUND_TRANSFER_LIST");
                        transferService.process(requestCtx, params);
                    }
                }
            }
        }
    }


}
