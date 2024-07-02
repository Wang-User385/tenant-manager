package com.hand.hap.activiti.service.impl;

import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.activiti.components.IHlsCusActivitiBean;
import com.hand.hls.activiti.dto.HlsCusProcess;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fct.dto.HlsCreditChanceLeaseItem;
import com.hand.hls.fct.dto.HlsCreditLineChanceMp;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChanceBp;
import com.hand.hls.fct.mapper.HlsCreditChanceLeaseItemMapper;
import com.hand.hls.fct.mapper.HlsCreditLineChanceMpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import org.activiti.engine.delegate.DelegateExecution;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.bstek.ureport.definition.datasource.DataType.Date;


/**
 * Copyright (C) Hand Business Consulting Services
 * AllRights Reserved
 *
 * @author: quzongkai
 * @date: 2020/6/12
 * @description: 项目审查工作流一键操作
 */
@Component
public class HlsCreditLineChancePassActiviti implements IHlsCusActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceService chanceService;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper chanceMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    private final static String PASS = "PASS";
    private final static String REJECT = "REJECT";
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;

    @Autowired
    private HlsCusPrjQuotationDetailsMapper prjQuotationDetailsMapper;

    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private HlsCreditChanceLeaseItemMapper hlsCreditChanceLeaseItemMapper;
    @Autowired
    private HlsCusPrjProjectLeaseItemService hlsCusPrjProjectLeaseItemService;

    @Autowired
    private HlsCreditLineChanceMpMapper hlsCreditLineChanceMpMapper;

    @Autowired
    private IHlsCusPrjProjectChanceMpService hlsCusPrjProjectChanceMpService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    FndCompanyMapper fndCompanyMapper;
    @Autowired
    FndOrgUnitMapper fndOrgUnitMapper;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;
    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;


    @Override
    public void excute(IRequest iRequest, HlsCusProcess hlsCusProcess) {

        HlsCusHlsCreditLineChance chance = chanceMapper.selectByPrimaryKey(hlsCusProcess.getBussinessKey());
        databaseLockProvider.lock(chance);
        if (PASS.equals(hlsCusProcess.getType())) {
            if("APPROVING".equalsIgnoreCase(chance.getCreditLineStatus())){
                //项目立项结束 生成尽调
                HlsCusHlsCreditLineChance resultHlsCusFctCreditChance = new HlsCusHlsCreditLineChance();
                //根据状态修改立项信息
                resultHlsCusFctCreditChance.setChanceId(Long.valueOf(hlsCusProcess.getBussinessKey()));
                resultHlsCusFctCreditChance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(iRequest, resultHlsCusFctCreditChance);
                if(resultHlsCusFctCreditChance.getCreatedBy()!=null){
                    iRequest.setUserId(resultHlsCusFctCreditChance.getCreatedBy());
                }
                //复制数据到prj_project 表 项目融资额 项目经理AB，项目主承租人
                HlsCusPrjProject cusPrjProject = new HlsCusPrjProject();
                Map<String, String> map2 = hlsBeanRefUtilService.getFieldValueMap(resultHlsCusFctCreditChance);
                hlsBeanRefUtilService.setFieldValue(cusPrjProject , map2);
                //将复制的对象的状态设置为new
                cusPrjProject.setProjectStatus("NEW");
                cusPrjProject.setProjectName(resultHlsCusFctCreditChance.getCreditLineName());
                cusPrjProject.setProjectNumber(resultHlsCusFctCreditChance.getCreditLineNumber());
                cusPrjProject.setMarketingReportId(resultHlsCusFctCreditChance.getMarketingReportId());
                cusPrjProject.setMarketingReportNumber(resultHlsCusFctCreditChance.getMarketingReportNumber());
                cusPrjProject.setHostProjectManager(resultHlsCusFctCreditChance.getProposerEmployeeId());
                cusPrjProject.setAssistProjectManager(resultHlsCusFctCreditChance.getProjectAssistant());
                cusPrjProject.setDocumentType("PRJ_PROJECT");
                cusPrjProject.setDocumentCategory("PRJ_PROJECT");
                cusPrjProject.setBusinessType(resultHlsCusFctCreditChance.getBusinessType());
                cusPrjProject.setDataClass("NORMAL");
                cusPrjProject.setDataType("NORMAL");
                cusPrjProject.setCurrency(resultHlsCusFctCreditChance.getCurrencyCode());
                cusPrjProject.setCompanySpv(resultHlsCusFctCreditChance.getCompanyId());
                cusPrjProject.setHostUnitId(resultHlsCusFctCreditChance.getUnitId());
                cusPrjProject.setAssistUnitId(resultHlsCusFctCreditChance.getAssitUnitId());
                //流程状态
                cusPrjProject.setReviewStatus(String.valueOf(100));

                //权限赋值
                FndCompany company = fndCompanyMapper.selectByPrimaryKey(resultHlsCusFctCreditChance.getCompanyId());
                FndOrgUnit orgUnit = fndOrgUnitMapper.selectByPrimaryKey(resultHlsCusFctCreditChance.getUnitId());
                SysUser sysUser = sysUserMapper.selectUserNameById(resultHlsCusFctCreditChance.getProposerEmployeeId());
                String bussinessType = "PRJ_PROJECT";
                String industryType = "null";
                String userCode = sysUser.getUserName().toUpperCase();
                String authorityString = '"' + company.getCompanyCode()  + '"' + "." + '"'+ orgUnit.getUnitCode()  + '"' + "." + '"'  + '"' + "." + '"'  +  bussinessType  +  '"' + "." + '"' +  industryType  + '"' + "." + '"'  +   industryType  +  '"' + "." + '"' + userCode   + '"' ;
                cusPrjProject.setAuthorityRuleString(authorityString);

                HlsCusHlsCreditLineChanceBp creditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
                creditLineChanceBp.setChanceId(Long.valueOf(hlsCusProcess.getBussinessKey()));

                //主承租人
                List<HlsCusHlsCreditLineChanceBp> creditLineChanceBps  = hlsCusHlsCreditLineChanceBpMapper.selectTenantBpByChance(creditLineChanceBp);
                if(creditLineChanceBps.size() != 0){
                    if(creditLineChanceBps.get(0) != null){
                        if(creditLineChanceBps.get(0).getBpId() != null){
                            cusPrjProject.setTenantId(creditLineChanceBps.get(0).getBpId());
                        }

                    }

                }
                //项目融资额
                HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
                hlsCusPrjQuotation.setDocumentId(resultHlsCusFctCreditChance.getChanceId());
                hlsCusPrjQuotation.setSourceDocumentId(resultHlsCusFctCreditChance.getChanceId());
                hlsCusPrjQuotation.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
                //报价查询
                List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationMapper.queryQuotationInfo(hlsCusPrjQuotation);
                cusPrjProject.setFinanceAmount(hlsCusPrjQuotationList.get(0).getFinanceAmount());
                //LEASE_ITEM_AMOUNT
                cusPrjProject.setLeaseItemAmount(hlsCusPrjQuotationList.get(0).getLeaseItemAmount());
                cusPrjProject.setCreatedBy(resultHlsCusFctCreditChance.getCreatedBy());
                cusPrjProject = hlsCusPrjProjectService.insertSelective(iRequest , cusPrjProject);

                //将credit_line_chance_bp 表的数据 复制到 prj_project_bp 表里
                HlsCusPrjProjectBp hlsCusPrjProjectBp = new HlsCusPrjProjectBp();
                List<HlsCusHlsCreditLineChanceBp> cusHlsCreditLineChanceBpList = hlsCusHlsCreditLineChanceBpMapper.selectBpInfoByChance(creditLineChanceBp);
                StringBuffer tenantSec = new StringBuffer();
                StringBuffer orgWarrantor = new StringBuffer();
                StringBuffer npWarrantor = new StringBuffer();
                for(HlsCusHlsCreditLineChanceBp lineChanceBp : cusHlsCreditLineChanceBpList ){
                    BeanRefUtils.beanToBean(lineChanceBp,hlsCusPrjProjectBp,hlsBeanRefUtilService);
                    hlsCusPrjProjectBp.setProjectId(cusPrjProject.getProjectId());
                    hlsCusPrjProjectBp.setBpCategroy("PRJ_PROJECT");
                    hlsCusPrjProjectBpService.insertSelective(iRequest , hlsCusPrjProjectBp);
                    //共同承租人 TENANT_SEC  法人担保人 bp_class --->ORG  WARRANTOR   自然人担保人 np WARRANTOR

                    if(("TENANT_SEC").equals(lineChanceBp.getBpType())){
                        tenantSec.append(lineChanceBp.getBpId());
                    }else if(("WARRANTOR").equals(lineChanceBp.getBpType())&&("ORG").equals(lineChanceBp.getBpClass())){
                        orgWarrantor.append(lineChanceBp.getBpId());
                    }else if(("WARRANTOR").equals(lineChanceBp.getBpType())&&("NP").equals(lineChanceBp.getBpClass())){
                        npWarrantor.append(lineChanceBp.getBpId());
                    }
                }
                cusPrjProject.setTenantSecId(String.valueOf(tenantSec));
                cusPrjProject.setGuarantorId(String.valueOf(orgWarrantor));
                cusPrjProject.setGuarantorNpId(String.valueOf(npWarrantor));
                hlsCusPrjProjectService.updateByPrimaryKey(iRequest,cusPrjProject);

                //租赁物复制  将 hls_credit_chance_lease_item 复制到 prj_project_lease_item
                HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
                HlsCreditChanceLeaseItem hlsCreditChanceLeaseItem = new HlsCreditChanceLeaseItem();
                hlsCreditChanceLeaseItem.setChanceId(resultHlsCusFctCreditChance.getChanceId());
                List<HlsCreditChanceLeaseItem> hlsCreditChanceLeaseItemList = hlsCreditChanceLeaseItemMapper.queryCreditChanceLeaseItem(hlsCreditChanceLeaseItem);
                if(CollectionUtils.isNotEmpty(hlsCreditChanceLeaseItemList)){
                    for(HlsCreditChanceLeaseItem hlsCreditChanceLeaseItem1:hlsCreditChanceLeaseItemList){
                        BeanRefUtils.beanToBean(hlsCreditChanceLeaseItem1,hlsCusPrjProjectLeaseItem,hlsBeanRefUtilService);
                        hlsCusPrjProjectLeaseItem.setProjectId(cusPrjProject.getProjectId());
                        hlsCusPrjProjectLeaseItemService.insertSelective(iRequest,hlsCusPrjProjectLeaseItem);
                    }
                }
                //抵质押复制 hls_credit_line_chance_mp 复制到 prj_project_chance_mp
                HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp = new HlsCusPrjProjectChanceMp();
                HlsCreditLineChanceMp hlsCreditLineChanceMp = new HlsCreditLineChanceMp();
                hlsCreditLineChanceMp.setChanceId(resultHlsCusFctCreditChance.getChanceId());
                List<HlsCreditLineChanceMp> hlsCreditLineChanceMpList = hlsCreditLineChanceMpMapper.queryCredLineChance(hlsCreditLineChanceMp);
                if(CollectionUtils.isNotEmpty(hlsCreditChanceLeaseItemList)){
                    for(HlsCreditLineChanceMp hlsCreditLineChanceMp1:hlsCreditLineChanceMpList){
                        BeanRefUtils.beanToBean(hlsCreditLineChanceMp1,hlsCusPrjProjectChanceMp,hlsBeanRefUtilService);
                        hlsCusPrjProjectChanceMp.setProjectId(cusPrjProject.getProjectId());
                        hlsCusPrjProjectChanceMpService.insertSelective(iRequest,hlsCusPrjProjectChanceMp);
                    }
                }

                //报价复制
                if(CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)){
                    for(HlsCusPrjQuotation hlsCusPrjQuotation1 : hlsCusPrjQuotationList){
                        //立项报价
                        HlsCusPrjQuotation hlsCusPrjQuotationCredit = new HlsCusPrjQuotation();
                        //项目立项报价复制
                        BeanRefUtils.beanToBean(hlsCusPrjQuotation1,hlsCusPrjQuotationCredit,hlsBeanRefUtilService);
                        hlsCusPrjQuotationCredit.setDocumentId(cusPrjProject.getProjectId());
                        hlsCusPrjQuotationCredit.setSourceDocumentId(cusPrjProject.getProjectId());
                        hlsCusPrjQuotationCredit.setSourceDocumentCategory("PRJ_PROJECT");
                        hlsCusPrjQuotationCredit = hlsCusPrjQuotationService.insertSelective(iRequest ,hlsCusPrjQuotationCredit);

                        //查询立项报价明细
                        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
                        //QUOTATION_ID
                        hlsCusPrjQuotationDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                        List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = prjQuotationDetailsMapper.queryDetailsById(hlsCusPrjQuotationDetails);
                        if(CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)){
                            for(HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails1:hlsCusPrjQuotationDetailsList){
                                //项目报价明细
                                HlsCusPrjQuotationDetails hlsCusPrjQuotationDetailsCredit = new HlsCusPrjQuotationDetails();
                                BeanRefUtils.beanToBean(hlsCusPrjQuotationDetails1,hlsCusPrjQuotationDetailsCredit,hlsBeanRefUtilService);
                                //设置项目报价明细外键（报价ID）
                                hlsCusPrjQuotationDetailsCredit.setQuotationId(hlsCusPrjQuotationCredit.getQuotationId());
                                hlsCusPrjQuotationDetailsService.insertSelective(iRequest ,hlsCusPrjQuotationDetailsCredit);
                            }
                        }

                        //查询立项现金流
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                        hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.queryPrjQuotationCashflowByQuotationId(hlsCusPrjQuotationCashflow);
                        if(CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)){
                            for(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1:hlsCusPrjQuotationCashflowList){
                                //项目现金流
                                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflowCredit = new HlsCusPrjQuotationCashflow();
                                BeanRefUtils.beanToBean(hlsCusPrjQuotationCashflow1,hlsCusPrjQuotationCashflowCredit,hlsBeanRefUtilService);
                                //设置项目现金流外键（报价ID）
                                hlsCusPrjQuotationCashflowCredit.setQuotationId(hlsCusPrjQuotationCredit.getQuotationId());
                                hlsCusPrjQuotationCashflowService.insertSelective(iRequest ,hlsCusPrjQuotationCashflowCredit);
                            }
                        }
                    }
                }
            }
            chance.setCreditLineStatus("APPROVED");
            chance.setApprovedDate(new Date());
        } else if (REJECT.equals(hlsCusProcess.getType())) {
            chance.setCreditLineStatus("REJECTED");
        }
        chanceService.updateByPrimaryKeySelective(iRequest, chance);
    }
}
