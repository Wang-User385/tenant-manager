package com.hand.hap.activiti.components;

import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.ConChangeRepaymentInfo;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.ConChangeRepaymentInfoService;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.HlsCusCshPaymentReqLn;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.gld.service.IGldContractCashflowService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.dto.HlsCusPrjQuotationCashflow;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjQuotationCashflowService;
import com.hand.hls.prj.service.HlsCusPrjQuotationService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by wangyan on 2017/11/13.
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusConContractChangeSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusConContractService hlsCusConContractService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper prjQuotationCashflowMapper;
    @Autowired
    private IConContractCashflowService conContractCashflowService;
    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    ConChangeRepaymentInfoService conChangeRepaymentInfoService;
    @Autowired
    HlsCusConContractCashflowMapper conCashflowMapper;
    @Autowired
    HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    HlsCusCshPaymentReqLnMapper cshPaymentReqLnMapper;
    @Autowired
    private IGldContractCashflowService gldContractCashflowService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public HlsCusConContractChangeSubmitServiceTask() {
    }

    @Override
    public void execute(DelegateExecution delegateExecution) {

        //解决跳转时结束监听器多次被调用问题
        if(((ExecutionEntityImpl) delegateExecution).getStartTime()==null){
            return;
        }

        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        String result = (String) delegateExecution.getVariable("approveResult");
        String processDefinitionId =  delegateExecution.getProcessDefinitionId().split(":")[0];
        String hlsCusPrjProjectPrams = (String) delegateExecution.getVariable("hlsCusPrjProjectOld");
        HlsCusPrjProject hlsCusPrjProjectOld = JSON.parseObject(hlsCusPrjProjectPrams, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectOld = new HlsCusPrjProject();

        String hlsCusPrjProjectPramsNew = (String) delegateExecution.getVariable("hlsCusPrjProjectNew");
        HlsCusPrjProject hlsCusPrjProjectNew = JSON.parseObject(hlsCusPrjProjectPramsNew, HlsCusPrjProject.class);
        HlsCusPrjProject resultHlsCusPrjProjectNew = new HlsCusPrjProject();

        //根据状态修改项目信息
        resultHlsCusPrjProjectOld.setProjectId(hlsCusPrjProjectOld.getProjectId());
        resultHlsCusPrjProjectOld = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProjectOld);

        resultHlsCusPrjProjectNew.setProjectId(hlsCusPrjProjectNew.getProjectId());
        resultHlsCusPrjProjectNew = hlsCusPrjProjectService.selectByPrimaryKey(requestCtx, resultHlsCusPrjProjectNew);

        Long projectIdOld = hlsCusPrjProjectOld.getProjectId();
        Long projectIdNew = hlsCusPrjProjectNew.getProjectId();

        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(resultHlsCusPrjProjectNew.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCtx, hlsCusChangeReqInfo);

        //退回还是会偶尔重复调用监听器成功,再加一步处理
        if("Y".equals(hlsCusChangeReqInfo.getInstanceEndFlag())){
            return;
        }

        Boolean swapParojectFlag = false;

        if("APPROVED".equals(result)){
            hlsCusChangeReqInfo.setStatus(result);
            //swapParojectFlag = true;
            hlsCusChangeReqInfo.setInstanceEndFlag("Y");

            //业务变更(租后)与业务变更(租前)
            if("BUSINESS_CHANGE_AFTER".equals(hlsCusChangeReqInfo.getChangeType()) || "BUSINESS_CHANGE_BEFORE".equals(hlsCusChangeReqInfo.getChangeType())){
                /*删除旧数据，将新数据回写*/
                requestCtx.setAttribute("processDefinitionId", processDefinitionId);

                hlsCusPrjProjectService.deleteOld(projectIdOld, requestCtx);
                try {
                    hlsCusPrjProjectService.updateOld(requestCtx,projectIdOld, projectIdNew, requestCtx);
                } catch (HlsCusException e) {
                    logger.info("项目变更更新数据失败!");
                    logger.info(e.getMessage());
                }

                /*更新normal*/
                HlsCusPrjProject prjProjectnNew = new HlsCusPrjProject();
                Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(resultHlsCusPrjProjectNew);
                hlsBeanRefUtilService.setFieldValue(prjProjectnNew, map1);
                prjProjectnNew.setProjectStatus("APPROVED");
                prjProjectnNew.setDataClass("VIRTUAL_CON");
                prjProjectnNew.setDataType("NORMAL");
                prjProjectnNew.setRefProjectId(null);
                prjProjectnNew.setChangeReqId(null);

                //更新原始项目
                prjProjectnNew.setProjectId(projectIdOld);
                prjProjectnNew.setMeetingStatus("APPROVED");
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjProjectnNew);

                //变更历史单据状态从CHANGE_REQ_HISTORY改为HISTORY
                /*HlsCusPrjProject prjChanceH = new HlsCusPrjProject();
                prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
                prjChanceH.setDataType("CHANGE_REQ_HISTORY");
                prjChanceH = hlsCusPrjProjectService.select(requestCtx, prjChanceH, 1, 999).get(0);
                prjChanceH.setDataType("HISTORY");
                hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prjChanceH);*/

                //更新合同与合同现金流仅租前需要执行
                if("BUSINESS_CHANGE_BEFORE".equals(hlsCusChangeReqInfo.getChangeType())){
                    // 更新合同表数据
                    //1.更新CON_CONTRACT
                    HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
                    //quotation.setQuotationId(prjProjectnNew.getQuotationId());
                    quotation.setSourceDocumentId(prjProjectnNew.getProjectId());
                    quotation.setSourceDocumentCategory("PRJ_PROJECT");
                    quotation.setDataClass("VIRTUAL_CON");
                    quotation = hlsCusPrjQuotationService.select(requestCtx, quotation, 1, 999).get(0);

                    HlsCusConContract hlsCusConContract = new HlsCusConContract();
                    hlsCusConContract.setProjectId(prjProjectnNew.getProjectId());
                    hlsCusConContract = hlsCusConContractService.select(requestCtx, hlsCusConContract, 1 ,10).get(0);
                    //更新合同
                    //Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(prjProjectnNew);
                    //hlsBeanRefUtilService.setFieldValue(hlsCusConContract, map);
                    hlsCusConContract.setLeaseTerm(quotation.getLeaseTerm());
                    //hlsCusConContract.setQuotationId(quotation.getQuotationId());
                    hlsCusConContract.setIntRate(quotation.getIntRate());
                    hlsCusConContract.setIntRateType(quotation.getIntRateType());
                    hlsCusConContract.setFloatingWayRate(quotation.getFloatingWayRate());
                    hlsCusConContract.setRentingFrequency(quotation.getRentingFrequency());
                    hlsCusConContract.setBaseRate(quotation.getBaseRate());
                    hlsCusConContract.setBaseRateType(quotation.getBaseRateType());
                    hlsCusConContract.setIrr(quotation.getIrr());
                    hlsCusConContract.setXirr(quotation.getXirr());
                    hlsCusConContract.setTotalRental(quotation.getTotalRental());
                    hlsCusConContract.setTotalInterest(quotation.getTotalInterest());
                    hlsCusConContract.setFinanceAmount(quotation.getFinanceAmount());
                    hlsCusConContract.setLeaseStartDate(quotation.getLeaseStartDate());
                    hlsCusConContract.setLeaseEndDate(quotation.getLeaseStartDate());
                    hlsCusConContract.setPmt(quotation.getPmt());
                    hlsCusConContract.setIrrAfterTax(quotation.getIrrAfterTax());
                    hlsCusConContract.setLeaseItemAmount(quotation.getLeaseItemAmount());
                    hlsCusConContract.setResidualValue(quotation.getResidualValue());
                    hlsCusConContract.setPayType(quotation.getPayType());
                    hlsCusConContract.setDownPayment(quotation.getDownPayment());
                    hlsCusConContract.setDownPaymentRatio(quotation.getDownPaymentRatio());
                    hlsCusConContract.setLeaseCharge(quotation.getLeaseCharge());
                    hlsCusConContract.setLeaseChargeRatio(quotation.getLeaseChargeRatio());
                    hlsCusConContract.setDeposit(quotation.getDeposit());
                    hlsCusConContract.setDepositRatio(quotation.getDepositRatio());
                    hlsCusConContract.setDepositDeduction(quotation.getDepositDeduction());
                    hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContract);
                    //2.PRJ_QUOTATION  VIRTUAL_CON覆盖到CONTRACT_CONFIRM和CONTRACT_PLAN


                    //3.更新CON_CONTRACT_CASHFLOW, 租前变更,合同现金流直接覆盖(删除后新增)
                    Long contractId = hlsCusConContract.getContractId();
                    Long quotationId = quotation.getQuotationId();
                    HlsCusConContract c = new HlsCusConContract();
                    c.setContractId(contractId);
                    c = hlsCusConContractService.selectByPrimaryKey(requestCtx, c);
                    //先删除合同现金流
                    HlsCusConContractCashflow conFlow = new HlsCusConContractCashflow();
                    conFlow.setContractId(contractId);
                    List<HlsCusConContractCashflow> oldConFlowList = conContractCashflowService.select(requestCtx, conFlow, 1, 99999);
                    conContractCashflowService.batchDelete(oldConFlowList);
                    List<HlsCusConContractCashflow> conContractCashflowList = new ArrayList<>();
                    HlsCusPrjQuotationCashflow prjQuotationCashflowParameter = new HlsCusPrjQuotationCashflow();
                    prjQuotationCashflowParameter.setQuotationId(quotationId);
                    List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = prjQuotationCashflowMapper.select(prjQuotationCashflowParameter);
                    if (prjQuotationCashflowList.size() > 0) {
                        c.setFirstPayDate(prjQuotationCashflowList.get(0).getDueDate());
                        c.setLeaseEndDate(prjQuotationCashflowList.get(0).getDueDate());
                    }
                    for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
                        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                        if (prjQuotationCashflowList.get(i).getDueDate() != null) {
                            //新加判断 报价现金流的dueDate可能为空
                            if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getFirstPayDate().getTime()) < 0) {
                                c.setFirstPayDate(prjQuotationCashflowList.get(i).getDueDate());
                            }
                            if (prjQuotationCashflowList.get(i).getCfItem() == 1 && (prjQuotationCashflowList.get(i).getDueDate().getTime() - c.getLeaseEndDate().getTime()) > 0) {
                                c.setLeaseEndDate(prjQuotationCashflowList.get(i).getDueDate());
                            }
                        }
                        BeanRefUtils.beanToBean(prjQuotationCashflowList.get(i), conContractCashflow, hlsBeanRefUtilService);
                        conContractCashflow.setTimes(prjQuotationCashflowList.get(i).getTimes().longValue());
                        conContractCashflow.setContractId(contractId);
                        conContractCashflow.setCfStatus("RELEASE");
                        conContractCashflow.setWriteOffFlag("NOT");
                        conContractCashflow.setBillingStatus("NOT");
                        conContractCashflow.setOverdueStatus("N");
                        conContractCashflow.setPenaltyProcessStatus("N");
                        conContractCashflow.setCalcDate(conContractCashflow.getDueDate());
                        conContractCashflow.setGeneratedSource("PRJ_QUOTATION");
                        conContractCashflow.setGeneratedSourceDocId(quotationId);
                        conContractCashflow.setGeneratedSourceDocLineId(prjQuotationCashflowList.get(i).getQuotationCashflowId());
                        if(conContractCashflow.getCfItem() == 1L){
                            conContractCashflow.setAmortizationMethod(quotation.getInterestAmortizationMethod());
                        }
                        conContractCashflowService.insertSelective(requestCtx, conContractCashflow);
                        conContractCashflowList.add(conContractCashflow);
                    }
                    //更新放款支付第0期现金流id update csh_payment_req_ln set Source_Doc_Line_Id = 3287 where Source_Doc_Line_Id = 3275;
                    //csh_payment_req_ln.source_doc_id = con
                    HlsCusCshPaymentReqLn reqLn = new HlsCusCshPaymentReqLn();
                    reqLn.setSourceDocId(c.getContractId());
                    List<HlsCusCshPaymentReqLn> lns = cshPaymentReqLnMapper.select(reqLn);
                    if(lns.size() > 0){
                        reqLn = lns.get(0);
                        Optional<HlsCusConContractCashflow> first = conContractCashflowList.stream().filter(
                                f -> f.getCfItem() == 0L && f.getCfType() == 0L && f.getTimes() == 0L
                        ).findFirst();
                        reqLn.setSourceDocLineId(first.get().getCashflowId());
                        cshPaymentReqLnMapper.updateByPrimaryKeySelective(reqLn);
                    }
                    hlsCusConContractService.updateByPrimaryKey(requestCtx, c);
                }

                //更新项目状态
                hlsCusPrjProjectOld.setProjectStatus("APPROVED");
                hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProjectOld);
            }else if("ET".equals(hlsCusChangeReqInfo.getChangeType())){
                //提前结清

                //获取合同
                HlsCusConContract contract = new HlsCusConContract();
                contract.setProjectId(resultHlsCusPrjProjectOld.getProjectId());
                contract = hlsCusConContractService.select(requestCtx, contract, 1, 999).get(0);

                //变更单据中报价
                HlsCusPrjQuotation cQuotation = new HlsCusPrjQuotation();
                cQuotation.setSourceDocumentId(hlsCusPrjProjectNew.getProjectId());
                cQuotation.setSourceDocumentCategory("PRJ_PROJECT");
                cQuotation.setDataClass("VIRTUAL_CON");
                cQuotation = hlsCusPrjQuotationService.select(requestCtx, cQuotation, 1, 999).get(0);

                //原单据中报价
//                HlsCusPrjQuotation oQuotation = new HlsCusPrjQuotation();
//                oQuotation.setSourceDocumentId(hlsCusPrjProjectOld.getProjectId());
//                oQuotation.setSourceDocumentCategory("PRJ_PROJECT");
//                oQuotation.setDataClass("VIRTUAL_CON");
//                oQuotation = hlsCusPrjQuotationService.select(requestCtx, oQuotation, 1, 999).get(0);

                //更新原虚拟合同现金流(留存最初版本虚拟合同报价，只更新当前contract_cashflow数据)
//                prjQuotationCashflowMapper.deleteByQuotationId(oQuotation);
//                List<HlsCusPrjQuotationCashflow> cFlows = new ArrayList<>();
//                HlsCusPrjQuotationCashflow record = new HlsCusPrjQuotationCashflow();
//                record.setQuotationId(cQuotation.getQuotationId());
//                cFlows = hlsCusPrjQuotationCashflowService.select(requestCtx, record, 1, 100000);
//                for (HlsCusPrjQuotationCashflow dto : cFlows) {
//                    HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
//                    Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
//                    hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
//                    hlsCusPrjQuotationCashflow1.setQuotationId(oQuotation.getQuotationId());
//                    hlsCusPrjQuotationCashflowService.insertSelective(requestCtx, hlsCusPrjQuotationCashflow1);
//                }

                //获取虚拟合同提前结清现金流
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                hlsCusPrjQuotationCashflow.setQuotationId(cQuotation.getQuotationId());
                hlsCusPrjQuotationCashflow.setCfItem(11L);
                hlsCusPrjQuotationCashflow = hlsCusPrjQuotationCashflowService.select(requestCtx, hlsCusPrjQuotationCashflow, 1, 999).get(0);

                //将结清大于等于结清期次的租金合同现金流删除
                HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
                hlsCusConContractCashflow.setContractId(contract.getContractId());
                hlsCusConContractCashflow.setCfItem(1L);
                List<HlsCusConContractCashflow> deleteCashFlowList = conContractCashflowService.select(requestCtx, hlsCusConContractCashflow, 1, 999);
                Long etTimes = hlsCusPrjQuotationCashflow.getTimes();
                deleteCashFlowList = deleteCashFlowList.stream().filter(f -> f.getTimes() >= etTimes && f.getCfItem() == 1L).collect(Collectors.toList());
                conContractCashflowService.batchDelete(deleteCashFlowList);

                //生成提前结清现金流
                HlsCusConContractCashflow etConFlow = new HlsCusConContractCashflow();
                BeanRefUtils.beanToBean(hlsCusPrjQuotationCashflow, etConFlow, hlsBeanRefUtilService);
                etConFlow.setContractId(contract.getContractId());
                etConFlow.setCfStatus("RELEASE");
                etConFlow.setWriteOffFlag("NOT");
                etConFlow.setBillingStatus("NOT");
                etConFlow.setOverdueStatus("N");
                etConFlow.setPenaltyProcessStatus("N");
                etConFlow.setCalcDate(etConFlow.getDueDate());
                etConFlow.setGeneratedSource("PRJ_QUOTATION");
                etConFlow.setGeneratedSourceDocId(cQuotation.getQuotationId());
                etConFlow.setGeneratedSourceDocLineId(hlsCusPrjQuotationCashflow.getQuotationCashflowId());
                conContractCashflowService.insertSelective(requestCtx, etConFlow);

                //更新留购价现金流日期和期次
                HlsCusConContractCashflow lgConFlow = new HlsCusConContractCashflow();
                lgConFlow.setCfItem(8L);
                lgConFlow.setCfType(8L);
                lgConFlow.setContractId(contract.getContractId());
                lgConFlow = conCashflowMapper.select(lgConFlow).get(0);
                lgConFlow.setDueDate(etConFlow.getDueDate());
                lgConFlow.setTimes(etConFlow.getTimes());
                conCashflowMapper.updateByPrimaryKeySelective(lgConFlow);

                //合同状态修改
                HlsCusConContract hlsCusConContract = new HlsCusConContract();
                hlsCusConContract.setContractId(contract.getContractId());
                hlsCusConContract.setContractStatus("ET");
                hlsCusConContract.setDataClass("NORMAL");
                hlsCusConContractService.updateByPrimaryKeySelective(requestCtx, hlsCusConContract);

                //更新项目状态
                hlsCusPrjProjectOld.setProjectStatus("APPROVED");
                hlsCusPrjProjectMapper.updateByPrimaryKeySelective( hlsCusPrjProjectOld);
            }else if("REPAYMENT_SCHEDULE".equals(hlsCusChangeReqInfo.getChangeType())){
                //还款计划变更

                //获取合同
                HlsCusConContract contract = new HlsCusConContract();
                contract.setProjectId(resultHlsCusPrjProjectOld.getProjectId());
                contract = hlsCusConContractService.select(requestCtx, contract, 1, 999).get(0);

                //变更单据中报价
                HlsCusPrjQuotation cQuotation = new HlsCusPrjQuotation();
                cQuotation.setSourceDocumentId(hlsCusPrjProjectNew.getProjectId());
                cQuotation.setSourceDocumentCategory("PRJ_PROJECT");
                cQuotation.setDataClass("VIRTUAL_CON");
                cQuotation = hlsCusPrjQuotationService.select(requestCtx, cQuotation, 1, 999).get(0);

                //原单据中报价
                HlsCusPrjQuotation oQuotation = new HlsCusPrjQuotation();
                oQuotation.setSourceDocumentId(hlsCusPrjProjectOld.getProjectId());
                oQuotation.setSourceDocumentCategory("PRJ_PROJECT");
                oQuotation.setDataClass("VIRTUAL_CON");
                oQuotation = hlsCusPrjQuotationService.select(requestCtx, oQuotation, 1, 999).get(0);

                //更新原虚拟合同现金流
                prjQuotationCashflowMapper.deleteByQuotationId(oQuotation);
                List<HlsCusPrjQuotationCashflow> cFlows = new ArrayList<>();
                HlsCusPrjQuotationCashflow record = new HlsCusPrjQuotationCashflow();
                record.setQuotationId(cQuotation.getQuotationId());
                cFlows = hlsCusPrjQuotationCashflowService.select(requestCtx, record, 1, 100000);
                for (HlsCusPrjQuotationCashflow dto : cFlows) {
                    HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 = new HlsCusPrjQuotationCashflow();
                    Map<String, String> mapCsh = hlsBeanRefUtilService.getFieldValueMap(dto);
                    hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotationCashflow1, mapCsh);
                    hlsCusPrjQuotationCashflow1.setQuotationId(oQuotation.getQuotationId());
                    hlsCusPrjQuotationCashflowService.insertSelective(requestCtx, hlsCusPrjQuotationCashflow1);
                }

                //获取还款计划变更信息
                ConChangeRepaymentInfo conChangeRepaymentInFo = new ConChangeRepaymentInfo();
                conChangeRepaymentInFo.setProjectId(hlsCusPrjProjectNew.getProjectId());
                conChangeRepaymentInFo = conChangeRepaymentInfoService.select(requestCtx, conChangeRepaymentInFo, 1, 999).get(0);
                Long changeStartTimes = conChangeRepaymentInFo.getChangeStartTimes();

                //删除合同现金流开始变更期次(包括开始变更期次)后的现金流
                conCashflowMapper.deleteChengeTimeAfterCashflow(contract.getContractId(), changeStartTimes);

                //将变更后现金流存入合同现金流表
                /*record.setQuotationId(oQuotation.getQuotationId());
                record.setCfItem(1L);*/

                Example example = new Example(HlsCusPrjQuotationCashflow.class);
                Example.Criteria criteria = example.createCriteria();
                List<Long> cfitems = new ArrayList<>();
                cfitems.add(1L);
                cfitems.add(3L);
                criteria.andIn("cfItem",cfitems);
                criteria.andEqualTo("quotationId", oQuotation.getQuotationId());
                cFlows = prjQuotationCashflowMapper.selectByExample(example);
                //cFlows = hlsCusPrjQuotationCashflowService.select(requestCtx, record, 1, 100000);
                for (HlsCusPrjQuotationCashflow cFlow : cFlows) {
                    if(cFlow.getTimes() >= changeStartTimes){
                        HlsCusConContractCashflow etConFlow = new HlsCusConContractCashflow();
                        BeanRefUtils.beanToBean(cFlow, etConFlow, hlsBeanRefUtilService);
                        etConFlow.setContractId(contract.getContractId());
                        etConFlow.setCfStatus("RELEASE");
                        etConFlow.setWriteOffFlag("NOT");
                        etConFlow.setBillingStatus("NOT");
                        etConFlow.setOverdueStatus("N");
                        etConFlow.setPenaltyProcessStatus("N");
                        etConFlow.setCalcDate(etConFlow.getDueDate());
                        etConFlow.setGeneratedSource("PRJ_QUOTATION");
                        etConFlow.setGeneratedSourceDocId(oQuotation.getQuotationId());
                        etConFlow.setGeneratedSourceDocLineId(cFlow.getQuotationCashflowId());
                        conContractCashflowService.insertSelective(requestCtx, etConFlow);
                    }
                }

                //更新留购价现金流日期和期次
                record.setQuotationId(oQuotation.getQuotationId());
                record.setCfItem(8L);
                record = hlsCusPrjQuotationCashflowService.select(requestCtx, record, 1, 10).get(0);
                HlsCusConContractCashflow lgConFlow = new HlsCusConContractCashflow();
                lgConFlow.setCfItem(8L);
                lgConFlow.setCfType(8L);
                lgConFlow.setContractId(contract.getContractId());
                lgConFlow = conCashflowMapper.select(lgConFlow).get(0);
                lgConFlow.setDueDate(record.getDueDate());
                lgConFlow.setTimes(record.getTimes());
                conCashflowMapper.updateByPrimaryKeySelective(lgConFlow);

                //查询最新的合同数据, 调用分摊
                HlsCusConContract ct = new HlsCusConContract();
                ct.setContractId(contract.getContractId());
                ct = hlsCusConContractService.selectByPrimaryKey(requestCtx,ct);
                gldContractCashflowService.clacFinanceIncome(requestCtx, ct.getContractId(), ct.getVatRate(), conChangeRepaymentInFo.getAfterIrr());
            }

            //更新项目状态
            hlsCusPrjProjectOld.setProjectStatus("APPROVED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective( hlsCusPrjProjectOld);
        }else if("REJECTED".equals(result)){
            //更新项目状态
            hlsCusPrjProjectOld.setProjectStatus("APPROVED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(hlsCusPrjProjectOld);
            /*resultHlsCusPrjProjectNew.setDataType("HISTORY");
            resultHlsCusPrjProjectNew.setDataClass("NORMAL");
            hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx,resultHlsCusPrjProjectNew);*/
            hlsCusChangeReqInfo.setStatus("REJECTED");
        }
        hlsCusChangeReqInfoService.updateByPrimaryKey(requestCtx, hlsCusChangeReqInfo);
    }
}
