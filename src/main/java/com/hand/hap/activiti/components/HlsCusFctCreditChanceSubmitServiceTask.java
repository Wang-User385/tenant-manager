package com.hand.hap.activiti.components;


import com.alibaba.fastjson.JSON;
import com.hand.hap.activiti.custom.IActivitiBean;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fct.dto.*;
import com.hand.hls.fct.mapper.*;
import com.hand.hls.fct.service.HlsCreditLineService;
import com.hand.hls.fct.service.HlsCusHlsCreditLineChanceService;
import com.hand.hls.fct.service.IHlsCreditChanceLeaseItemService;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.interfacePlatform.utils.FinanceBaseUtils;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.*;
import com.hand.hls.sys.dto.FndOrgUnit;
import com.hand.hls.sys.dto.SysUser;
import com.hand.hls.sys.dto.SysUserAuthorityTrx;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.sys.mapper.FndOrgUnitMapper;
import com.hand.hls.sys.mapper.SysUserAuthorityTrxMapper;
import com.hand.hls.sys.mapper.SysUserMapper;
import com.hand.hls.utils.MathUtil;
import hls.core.sys.event.service.SysEventService;
import lombok.SneakyThrows;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Date: Created in 16:42 2018/10/12
 * @Description:授信立项结束流程
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctCreditChanceSubmitServiceTask implements JavaDelegate, IActivitiBean {

    @Autowired
    private HlsCusHlsCreditLineChanceService hlsCusHlsCreditLineChanceService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private HlsCreditLineService hlsCreditLineService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper hlsCusHlsCreditLineChanceBpMapper;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusPrjProjectBpService hlsCusPrjProjectBpService;

    @Autowired
    FndCompanyMapper fndCompanyMapper;

    @Autowired
    FndOrgUnitMapper fndOrgUnitMapper;

    @Autowired
    SysUserMapper sysUserMapper;
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
    HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;

    @Autowired
    HlsCreditLineAttachMapper hlsCreditLineAttachMapper;

    @Autowired
    IFndAttachmentMultiService fndAttachmentMultiService;

    @Autowired
    HlsCreditPlanMapper hlsCreditPlanMapper;

    @Autowired
    HlsCreditPlanService hlsCreditPlanService;

    @Autowired
    private FinanceBaseUtils financeBaseUtils;

    @Autowired
    SysUserAuthorityTrxMapper sysUserAuthorityTrxMapper;
    @Autowired
    private SysEventService sysEventService;

    public HlsCusFctCreditChanceSubmitServiceTask(){}

    //private static final String TENANT_SEC
    //private static final String TENANT_SEC
    //private static final String TENANT_SEC
    @SneakyThrows
    @Override
    public void execute(DelegateExecution delegateExecution) {
        String flag = null;
        IRequest requestCtx = (IRequest) delegateExecution.getVariable("iRequest");
        //获取前台的判断信息，通过或是不通过等
        String result = (String) delegateExecution.getVariable("approveResult");
        long unitid=Long.valueOf(requestCtx.getAttribute("unitId"));
        String employeeCode = (String) delegateExecution.getVariable("startUserName");
        //SysUser sysUser = sysUserService.queryUserByCode(employeeCode);
        //获取开始时传入的头表对象
        String hlsCusHlsCreditLineChancePrams = (String) delegateExecution.getVariable("hlsCusHlsCreditLineChance");
        //反编译，将JSON格式转换成对象
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = JSON.parseObject(hlsCusHlsCreditLineChancePrams, HlsCusHlsCreditLineChance.class);
        HlsCusHlsCreditLineChance resultHlsCusFctCreditChance = new HlsCusHlsCreditLineChance();
        //根据状态修改立项信息
        resultHlsCusFctCreditChance.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
        //通过查询找出表中的最新数据
        resultHlsCusFctCreditChance = hlsCusHlsCreditLineChanceService.selectByPrimaryKey(requestCtx, resultHlsCusFctCreditChance);
        if(resultHlsCusFctCreditChance.getCreatedBy()!=null){
            requestCtx.setUserId(resultHlsCusFctCreditChance.getCreatedBy());
        }
        //为表上锁，防止操作中对数据进行修改
        //databaseLockProvider.lock(resultHlsCusFctCreditChance);

        //针对立项审批通过 多次生成项目尽调  判断下状态
        if("APPROVING".equalsIgnoreCase(resultHlsCusFctCreditChance.getCreditLineStatus())){
            //当是通过时复制表信息
            if ("APPROVED".equalsIgnoreCase(result) ) {
                //修改流程的状态
                flag = "APPROVED";
                //修改审批通过时间
                resultHlsCusFctCreditChance.setApprovedDate(new Date());

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

                //立项 的“增信措施” 带到尽调的“担保方式
                cusPrjProject.setGuaranteeMethodNote(resultHlsCusFctCreditChance.getCreditMeasures());
                //立项新增字段复制
                cusPrjProject.setIndustry(resultHlsCusFctCreditChance.getIndustry());
                cusPrjProject.setIndustryType(resultHlsCusFctCreditChance.getIndustryType());
                cusPrjProject.setCity(resultHlsCusFctCreditChance.getCity());

                //是否授信
                cusPrjProject.setCreditFlag(resultHlsCusFctCreditChance.getCreditFlag());

                //流程状态
                cusPrjProject.setReviewStatus(String.valueOf(100));

                //权限赋值
                FndCompany company = fndCompanyMapper.selectByPrimaryKey(requestCtx.getCompanyId());
                FndOrgUnit orgUnit = fndOrgUnitMapper.selectByPrimaryKey(resultHlsCusFctCreditChance.getUnitId());
                if(orgUnit==null){
                    orgUnit = fndOrgUnitMapper.selectByPrimaryKey(unitid);
                }
                SysUser sysUser = sysUserMapper.selectUserNameById(resultHlsCusFctCreditChance.getProposerEmployeeId());

                String companyCode = company.getCompanyCode();
                SysUserAuthorityTrx record = new SysUserAuthorityTrx();
                record.setUserId(resultHlsCusFctCreditChance.getProposerEmployeeId());
                List<Map> maps = sysUserAuthorityTrxMapper.selectUsers(record);
                if (maps !=null && maps.size() > 0){
                    Map userMap = maps.get(0);
                    companyCode = userMap.get("COMPANY_CODE") == null ? companyCode : userMap.get("COMPANY_CODE").toString();
                }

                String bussinessType = "PRJ_PROJECT";
                String industryType = "null";
                String userCode = sysUser.getUserName().toUpperCase();
                String authorityString = '"' + companyCode  + '"' + "." + '"'+ orgUnit.getUnitCode()  + '"' + "." + '"'  + '"' + "." + '"'  +  bussinessType  +  '"' + "." + '"' +  industryType  + '"' + "." + '"'  +   industryType  +  '"' + "." + '"' + userCode   + '"' ;
                cusPrjProject.setAuthorityRuleString(authorityString);

                HlsCusHlsCreditLineChanceBp creditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
                creditLineChanceBp.setChanceId(hlsCusHlsCreditLineChance.getChanceId());

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

                //授信方案查询复制
                HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
                hlsCreditPlan.setSourceDocumentId(resultHlsCusFctCreditChance.getChanceId());
                hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
                List<HlsCreditPlan> hlsCreditPlanList = hlsCreditPlanMapper.queryCreditPlanInfo(hlsCreditPlan);

                String functionCode="PRJ001F1";
                String linkUrl="PRJ/PRJ_CHANCE/PRJ001/prj_chance_modify_detail.lview";
                //授信项目 融资金额从授信中取
                if("Y".equals(resultHlsCusFctCreditChance.getCreditFlag())){
                    cusPrjProject.setFinanceAmount(hlsCreditPlanList.get(0).getCreditAmt());
                    cusPrjProject.setLeaseItemAmount(hlsCreditPlanList.get(0).getCreditAmt());
                    cusPrjProject.setOrgSignState("NEW");
                    functionCode="PRJ003F1";
                    linkUrl = "PRJ/PRJ_CHANCE/PRJ001/prj_credit_modify_detail.lview";
                }else{
                    cusPrjProject.setFinanceAmount(hlsCusPrjQuotationList.get(0).getFinanceAmount());
                    //LEASE_ITEM_AMOUNT
                    cusPrjProject.setLeaseItemAmount(hlsCusPrjQuotationList.get(0).getLeaseItemAmount());
                }

                cusPrjProject.setCreatedBy(resultHlsCusFctCreditChance.getCreatedBy());
                cusPrjProject = hlsCusPrjProjectService.insertSelective(requestCtx , cusPrjProject);

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
                    hlsCusPrjProjectBpService.insertSelective(requestCtx , hlsCusPrjProjectBp);
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
                hlsCusPrjProjectService.updateByPrimaryKey(requestCtx,cusPrjProject);

                //租赁物复制  将 hls_credit_chance_lease_item 复制到 prj_project_lease_item
                /*HlsCusPrjProjectLeaseItem hlsCusPrjProjectLeaseItem = new HlsCusPrjProjectLeaseItem();
                HlsCreditChanceLeaseItem hlsCreditChanceLeaseItem = new HlsCreditChanceLeaseItem();
                hlsCreditChanceLeaseItem.setChanceId(resultHlsCusFctCreditChance.getChanceId());
                List<HlsCreditChanceLeaseItem> hlsCreditChanceLeaseItemList = hlsCreditChanceLeaseItemMapper.queryCreditChanceLeaseItem(hlsCreditChanceLeaseItem);
                if(CollectionUtils.isNotEmpty(hlsCreditChanceLeaseItemList)){
                    for(HlsCreditChanceLeaseItem hlsCreditChanceLeaseItem1:hlsCreditChanceLeaseItemList){
                        BeanRefUtils.beanToBean(hlsCreditChanceLeaseItem1,hlsCusPrjProjectLeaseItem,hlsBeanRefUtilService);
                        hlsCusPrjProjectLeaseItem.setProjectId(cusPrjProject.getProjectId());
                        hlsCusPrjProjectLeaseItemService.insertSelective(requestCtx,hlsCusPrjProjectLeaseItem);
                    }
                }*/
                //抵质押复制 hls_credit_line_chance_mp 复制到 prj_project_chance_mp
                /*HlsCusPrjProjectChanceMp hlsCusPrjProjectChanceMp = new HlsCusPrjProjectChanceMp();
                HlsCreditLineChanceMp hlsCreditLineChanceMp = new HlsCreditLineChanceMp();
                hlsCreditLineChanceMp.setChanceId(resultHlsCusFctCreditChance.getChanceId());
                List<HlsCreditLineChanceMp> hlsCreditLineChanceMpList = hlsCreditLineChanceMpMapper.queryCredLineChance(hlsCreditLineChanceMp);
                if(CollectionUtils.isNotEmpty(hlsCreditChanceLeaseItemList)){
                    for(HlsCreditLineChanceMp hlsCreditLineChanceMp1:hlsCreditLineChanceMpList){
                        BeanRefUtils.beanToBean(hlsCreditLineChanceMp1,hlsCusPrjProjectChanceMp,hlsBeanRefUtilService);
                        hlsCusPrjProjectChanceMp.setProjectId(cusPrjProject.getProjectId());
                        hlsCusPrjProjectChanceMpService.insertSelective(requestCtx,hlsCusPrjProjectChanceMp);
                    }
                }*/

                //报价复制
                if(CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)){
                    for(HlsCusPrjQuotation hlsCusPrjQuotation1 : hlsCusPrjQuotationList){
                        //立项报价
                        HlsCusPrjQuotation hlsCusPrjQuotationCredit = new HlsCusPrjQuotation();
                        //项目立项报价复制
                        BeanRefUtils.beanToBean(hlsCusPrjQuotation1,hlsCusPrjQuotationCredit,hlsBeanRefUtilService);
                        hlsCusPrjQuotationCredit.setDocumentId(cusPrjProject.getProjectId());
                        hlsCusPrjQuotationCredit.setSourceDocumentId(cusPrjProject.getProjectId());
                        //项目评审流程 项目报价存在三个，尽调，一个业审会的  一个经营班子 改DataClass工作量太大，改SourceDocumentCategory
                        hlsCusPrjQuotationCredit.setSourceDocumentCategory("PRJ_PROJECT");
                        hlsCusPrjQuotationCredit.setDataClass("PRJ_PROJECT_INVEST");
                        //hlsCusPrjQuotationCredit.setDeposit(MathUtil.mul(hlsCusPrjQuotation1.getLeaseItemAmount(),0.06D));
//                        hlsCusPrjQuotationCredit.setDeposit(0D);
                        hlsCusPrjQuotationCredit = hlsCusPrjQuotationService.insertSelective(requestCtx ,hlsCusPrjQuotationCredit);

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
                                hlsCusPrjQuotationDetailsService.insertSelective(requestCtx ,hlsCusPrjQuotationDetailsCredit);
                            }
                        }

                        //查询立项现金流
                        /*HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                        hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.queryPrjQuotationCashflowByQuotationId(hlsCusPrjQuotationCashflow);
                        if(CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)){
                            for(HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1:hlsCusPrjQuotationCashflowList){
                                //项目现金流
                                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflowCredit = new HlsCusPrjQuotationCashflow();
                                BeanRefUtils.beanToBean(hlsCusPrjQuotationCashflow1,hlsCusPrjQuotationCashflowCredit,hlsBeanRefUtilService);
                                //设置项目现金流外键（报价ID）
                                hlsCusPrjQuotationCashflowCredit.setQuotationId(hlsCusPrjQuotationCredit.getQuotationId());
                                hlsCusPrjQuotationCashflowService.insertSelective(requestCtx ,hlsCusPrjQuotationCashflowCredit);
                            }
                        }*/
                    }
                }

                //授信方案复制
                for(HlsCreditPlan hlsCreditPlan1 : hlsCreditPlanList){
                    //立项授信方案
                    HlsCreditPlan hlsCreditPlanCredit = new HlsCreditPlan();
                    //项目立项授信方案复制
                    BeanRefUtils.beanToBean(hlsCreditPlan1,hlsCreditPlanCredit,hlsBeanRefUtilService);
                    hlsCreditPlanCredit.setSourceDocumentId(cusPrjProject.getProjectId());
                    hlsCreditPlanCredit.setSourceDocumentCategory("PRJ_PROJECT");
                    hlsCreditPlanCredit = hlsCreditPlanService.insertSelective(requestCtx ,hlsCreditPlanCredit);
                }

                //资料清单生成
                hlsCusPrjProjectService.generatePrjBaseAttachment(requestCtx,cusPrjProject);

               if( "N".equals(resultHlsCusFctCreditChance.getCreditFlag())){
                   financeBaseUtils.createProjectPlanItfc(requestCtx,cusPrjProject.getProjectId());
               }

                String msg= "【"+resultHlsCusFctCreditChance.getCreditLineName()+"】立项审批已通过，请知悉！" ;
                String url = linkUrl+"?chance_id="+resultHlsCusFctCreditChance.getChanceId()+"&credit_flag="+resultHlsCusFctCreditChance.getCreditFlag()
                        +"&maintain_type=READONLY&function_usage=QUERY&function_code="+functionCode;
                //立项审批通过发送通知给 财务部-余洋、李思佳。法务部-肖珍珍、林晓光。
                String[] sendUsers = {"yu.yang", "lisijia","xiaozhenzhen","linxiaoguang"};
                //保留原始的userId
                Long originalUserId = requestCtx.getUserId();
                for (String userName : sendUsers) {
                    Map<String, Object> paramsEvent = new HashMap<>();
                    SysUser user = new SysUser();
                    user = sysUserMapper.queryUserByUserName(userName);
                    if(user != null) {
                        requestCtx.setUserId(user.getUserId());
                        paramsEvent.put("message", msg);
                        paramsEvent.put("noticeTitle", "项目立项审批流程\n");
                        paramsEvent.put("noticeType", "NOTICE");
                        paramsEvent.put("url", url);
                        paramsEvent.put("level", 1L);
                        sysEventService.eventSave(requestCtx, cusPrjProject.getProjectId(), cusPrjProject.getDocumentCategory(), cusPrjProject.getDocumentType(), "BAC", "FCT_PROJECTCREATE_WFL", "P2D", paramsEvent);
                    }
                }
                requestCtx.setUserId(originalUserId);

            }else if ("APPROVED_RETURN".equalsIgnoreCase(result)) {
                flag = "APPROVED_RETURN";
                resultHlsCusFctCreditChance.setApprovedDate(new Date());
            }
            else if ("REJECTED".equalsIgnoreCase(result)) {
                flag = "REJECTED";
            }
            resultHlsCusFctCreditChance.setCreditLineStatus(flag);
            //hlsCusHlsCreditLineChanceService.updateByPrimaryKeySelective(requestCtx, resultHlsCusFctCreditChance);
            hlsCusHlsCreditLineChanceMapper.updateByPrimaryKeySelective(resultHlsCusFctCreditChance);
        }

    }
}
