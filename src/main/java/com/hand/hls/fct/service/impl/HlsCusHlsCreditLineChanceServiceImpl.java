package com.hand.hls.fct.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;

import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.DocFileTempletRule;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.cont.service.IDocFileTempletRuleService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.csh.dto.ProjectCreditCondition;
import com.hand.hls.fct.dto.*;
import com.hand.hls.fct.mapper.HlsChanceBusinessAccessCompareMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceAttachMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceBpMapper;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.*;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.dto.HlsBusinessAccessCompare;
import com.hand.hls.fnd.dto.HlsEmployee;


import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsBusinessAccessCompareMapper;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReport;
import com.hand.hls.hls.dto.HlsCusHlsMarketingReportBp;
import com.hand.hls.hls.dto.HlsCusHlsReportAttachment;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportBpMapper;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import com.hand.hls.hls.service.HlsMarketingReportDocxService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.*;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.dto.SysDocumentList;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.ruleengine.dto.RuleEngineType;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.ruleengine.service.IRuleEngineTypeService;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import hls.core.utils.exception.HlsCusException;
import leaf.utils.ConfigUtils;
import leaf.service.validation.ParameterNullException;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uncertain.composite.CompositeMap;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.jws.Oneway;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsCreditLineChanceServiceImpl extends BaseServiceImpl<HlsCusHlsCreditLineChance> implements HlsCusHlsCreditLineChanceService {

    @Autowired
    private HlsCusHlsCreditLineChanceMapper CreditLineChanceMapper;

    @Autowired
    private HlsEmployeeMapper employeeMapper;

    @Autowired
    private HlsCusHlsCreditLineChanceMapper hlsCusHlsCreditLineChanceMapper;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusHlsCreditChanceGuarantorService chanceGuarantorService;
    @Autowired
    private HlsCusHlsCreditChanceMortgageService chanceMortgageService;
    @Autowired
    private HlsCusHlsCreditChancePledgeService chancePledgeService;
    @Autowired
    private HlsCusCreditChanceFinStatementService chanceFinStatementService;
    @Autowired
    private HlsCusHlsCreditLineChanceBpService creditLineChanceBpService;

    @Autowired
    private HlsCusHlsCreditLineChanceAttachService cusHlsCreditLineChanceAttachService;

    @Autowired
    private HlsCusHlsCreditLineChanceBpMapper creditLineChanceBpMapper;
    @Autowired
    private HlsCusHlsMarketingReportMapper marketingReportMapper;
    @Autowired
    private HlsCusHlsMarketingReportBpMapper marketingReportBpMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceBpService chanceBpService;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

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
    FndCompanyMapper fndCompanyMapper;
    private final String sourceDocumentCategoryF = "HLS_MARKETING_REPORT";
    private final String sourceDocumentCategoryD = "HLS_CREDIT_LINE_CHANCE";
    //合同文本生成 start
    @Autowired
    private IDocFileTempletRuleService docFileTempletRuleService;
    @Autowired
    private IRuleEngineTypeService ruleEngineTypeService;
    @Autowired
    private Datasource2Json datasource2Json;
    @Autowired
    private IHLSRuleEngineInitService hlsRuleEngineInitService;
    @Autowired
    private IHlsCreditLineAttachService creditLineAttachService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    private static final String CHANCE_ID = "chanceId";
    private static final String BP_ID = "chanceAttachmentId";
    private static final String TEMPLET_ID = "templetId";
    private static final String CHANCE_ATTACHMENT_ID = "chanceAttachmentId";
    private static final String DOCUMENT_CATEGORY = "HLS_CREDIT_LINE_CHANCE";
    @Autowired
    private HlsCreditChanceDocxService chanceDocxService;
    //合同文本生成end

    @Autowired
    HlsBusinessAccessCompareMapper hlsBusinessAccessCompareMapper;
    @Autowired
    HlsChanceBusinessAccessCompareServiceImpl chanceAccessCompareService;
    @Autowired
    HlsChanceBusinessAccessCompareMapper chanceCompareMapper;
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private HlsCusChangeReqInfoService hlsCusChangeReqInfoService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private IFndAttachmentMultiService iFndAttachmentMultiService;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private IFndAttachmentService iFndAttachmentService;

    @Autowired
    HlsCusHlsCreditLineChanceAttachMapper hlsCusHlsCreditLineChanceAttachMapper;

    @Autowired
    HlsCreditPlanService hlsCreditPlanService;

    @Autowired
    HlsCreditPlanMapper hlsCreditPlanMapper;

    /*项目立项创建*/
    @Override
    public HlsCusHlsCreditLineChance hlsCreditCreate(IRequest iRequest, HlsCusHlsCreditLineChance dto) throws HlsCusException {
        HlsCusHlsCreditLineChance hlsCreditChance = new HlsCusHlsCreditLineChance();
        //
        hlsCreditChance.setCreditLineName(dto.getCreditLineName());
        hlsCreditChance.setCreditFlag(dto.getCreditFlag());
        hlsCreditChance.setMarketingReportNumber(dto.getMarketingReportNumber());
        hlsCreditChance.setCompanyId(dto.getCompanyId());
        hlsCreditChance.setProjectAssistant(dto.getProjectAssistant());
        hlsCreditChance.setProposerEmployeeId(dto.getProposerEmployeeId());
        hlsCreditChance.setUnitId(dto.getUnitId());
        hlsCreditChance.setBusinessType(dto.getBusinessType());
        hlsCreditChance.setDocumentCategory(DOCUMENT_CATEGORY);
        hlsCreditChance.setValidFrom(new Date());
        hlsCreditChance.setValidTo(getAfterMonth(hlsCreditChance.getValidFrom(), 3));
        Long marketintReportId = dto.getMarketingReportId();
        //校验项目方案主键是否存在
        checkQuotation(marketintReportId);
        //查询项目方案信息
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(marketintReportId);
        List<HlsCusHlsMarketingReport> marketingReport = marketingReportMapper.queryMarketingReportDetail(hlsCusHlsMarketingReport);
        //遍历TENANT
        for (HlsCusHlsMarketingReport hlsCusHlsMarketing : marketingReport) {
            //赋值
            hlsCreditChance.setMarketingReportId(hlsCusHlsMarketing.getMarketingReportId());

            hlsCreditChance.setCreditLineStatus("NEW");

            hlsCreditChance.setCreditApply(hlsCusHlsMarketing.getCreditApply());

            hlsCreditChance.setCreditNote(hlsCusHlsMarketing.getCreditNote());
            hlsCreditChance.setLeaseMatterNote(hlsCusHlsMarketing.getLeaseMatterNote());
            hlsCreditChance.setGuaranteeMethodNote(hlsCusHlsMarketing.getGuaranteeMethodNote());
            hlsCreditChance.setFinanceNote(hlsCusHlsMarketing.getFinanceNote());
            hlsCreditChance.setInsurancePlanNote(hlsCusHlsMarketing.getInsurancePlanNote());
            hlsCreditChance.setInsureFlag(hlsCusHlsMarketing.getInsureFlag());
            hlsCreditChance.setGuaranteeMethod(hlsCusHlsMarketing.getGuaranteeMethod());
            hlsCreditChance.setPayMethod(hlsCusHlsMarketing.getPayMethod());
            hlsCreditChance.setInsuranceAmount(hlsCusHlsMarketing.getInsuranceAmount());
            hlsCreditChance.setBondAmount(hlsCusHlsMarketing.getBondAmount());
            hlsCreditChance.setInsuredAmount(hlsCusHlsMarketing.getInsuredAmount());
            hlsCreditChance.setBeneficiary(hlsCusHlsMarketing.getBeneficiary());
            hlsCreditChance.setInsurancePeriod(hlsCusHlsMarketing.getInsurancePeriod());
            hlsCreditChance.setComRiskFlag(hlsCusHlsMarketing.getComRiskFlag());
            hlsCreditChance.setMachineDamageFlag(hlsCusHlsMarketing.getMachineDamageFlag());
            hlsCreditChance.setEnginMachFlag(hlsCusHlsMarketing.getEnginMachFlag());
            hlsCreditChance.setEnginAllFlag(hlsCusHlsMarketing.getEnginAllFlag());
            hlsCreditChance.setCompulsoryInsFlag(hlsCusHlsMarketing.getCompulsoryInsFlag());
            hlsCreditChance.setOthersFlag(hlsCusHlsMarketing.getOthersFlag());
            hlsCreditChance.setDescription(hlsCusHlsMarketing.getNote());
            //CREDIT_LINE_AMT ESTIMATED_FINANCING_AMOUNT
            hlsCreditChance.setCreditLineAmt(hlsCusHlsMarketing.getEstimatedFinancingAmount());
            //


            //立项权限字符串处理 AUTHORITY_RULE_STRING
            hlsCreditChance.setAuthorityRuleString(generateAuthorityString(iRequest));

            //获取项目立项编号
            Map<String, String> params = new HashMap<>();
            String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_PROJECT",
                    "PRJ_PROJECT", "LEASE", params);
            hlsCreditChance.setCreditLineNumber(ruleCode);
            hlsCreditChance.setCreatedBy(iRequest.getUserId());
            hlsCreditChance = this.insertSelective(iRequest, hlsCreditChance);
        }
        //商业伙伴信息复制 marketingReportBpMapper
        HlsCusHlsMarketingReportBp reportBp = new HlsCusHlsMarketingReportBp();
        reportBp.setMarketingReportId(marketintReportId);
        List<HlsCusHlsMarketingReportBp> mrbs = marketingReportBpMapper.queryMarketingReportBp(reportBp);
        //遍历
        if (CollectionUtils.isNotEmpty(mrbs)) {
            for (HlsCusHlsMarketingReportBp repBp : mrbs) {
                HlsCusHlsCreditLineChanceBp lineChanceBp = new HlsCusHlsCreditLineChanceBp();
                BeanRefUtils.beanToBean(repBp, lineChanceBp, hlsBeanRefUtilService);
                lineChanceBp.setChanceId(hlsCreditChance.getChanceId());

                chanceBpService.insertSelective(iRequest, lineChanceBp);
            }
        }
        //项目立项报价复制 prj_quotation document_id(ChanceId) source_document_id(ChanceId) source_document_ca(HLS_CREDIT_LINE_CHANCE)
        //项目立项报价明细复制 prj_quotation_details
        //项目立项现金流表 prj_quotation_cashflow

        //queryPrjQuotationInfo
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setDocumentId(marketintReportId);
        hlsCusPrjQuotation.setSourceDocumentId(marketintReportId);
        hlsCusPrjQuotation.setSourceDocumentCategory(sourceDocumentCategoryF);
        //查询方案报价
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = hlsCusPrjQuotationMapper.queryQuotationInfo(hlsCusPrjQuotation);
        if (CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)) {
            for (HlsCusPrjQuotation hlsCusPrjQuotation1 : hlsCusPrjQuotationList) {
                //立项报价
                HlsCusPrjQuotation hlsCusPrjQuotationCredit = new HlsCusPrjQuotation();
                //项目立项报价复制
                //hlsCusPrjQuotation1.setBusinessType(dto.getBusinessType());
                BeanRefUtils.beanToBean(hlsCusPrjQuotation1, hlsCusPrjQuotationCredit, hlsBeanRefUtilService);
                hlsCusPrjQuotationCredit.setDocumentId(hlsCreditChance.getChanceId());
                hlsCusPrjQuotationCredit.setBusinessType(dto.getBusinessType());
                hlsCusPrjQuotationCredit.setSourceDocumentId(hlsCreditChance.getChanceId());
                hlsCusPrjQuotationCredit.setSourceDocumentCategory(sourceDocumentCategoryD);
                hlsCusPrjQuotationCredit = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotationCredit);

                //查询方案报价明细
                HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
                //QUOTATION_ID
                hlsCusPrjQuotationDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = prjQuotationDetailsMapper.queryDetailsById(hlsCusPrjQuotationDetails);
                if (CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)) {
                    for (HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails1 : hlsCusPrjQuotationDetailsList) {
                        //立项报价明细
                        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetailsCredit = new HlsCusPrjQuotationDetails();
                        BeanRefUtils.beanToBean(hlsCusPrjQuotationDetails1, hlsCusPrjQuotationDetailsCredit, hlsBeanRefUtilService);
                        //设置立项报价明细外键（报价ID）
                        hlsCusPrjQuotationDetailsCredit.setQuotationId(hlsCusPrjQuotationCredit.getQuotationId());
                        hlsCusPrjQuotationDetailsService.insertSelective(iRequest, hlsCusPrjQuotationDetailsCredit);
                    }
                }

                //查询方案现金流
                HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
                hlsCusPrjQuotationCashflow.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.queryPrjQuotationCashflowByQuotationId(hlsCusPrjQuotationCashflow);
                if (CollectionUtils.isNotEmpty(hlsCusPrjQuotationList)) {
                    for (HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow1 : hlsCusPrjQuotationCashflowList) {
                        //立项现金流
                        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflowCredit = new HlsCusPrjQuotationCashflow();
                        BeanRefUtils.beanToBean(hlsCusPrjQuotationCashflow1, hlsCusPrjQuotationCashflowCredit, hlsBeanRefUtilService);
                        //设置立项现金流外键（报价ID）
                        hlsCusPrjQuotationCashflowCredit.setQuotationId(hlsCusPrjQuotationCredit.getQuotationId());
                        hlsCusPrjQuotationCashflowService.insertSelective(iRequest, hlsCusPrjQuotationCashflowCredit);
                    }
                }
            }
        }
        return hlsCreditChance;
    }

    public static Date getAfterMonth(Date inputDate, int number) {
        Calendar c = Calendar.getInstance();//获得一个日历的实例
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        c.setTime(inputDate);//设置日历时间
        c.add(Calendar.MONTH, number);//在日历的月份上增加6个月
        return c.getTime();
    }

    @Override
    public String generateAuthorityString(IRequest iRequest) {
        Long companyId = iRequest.getCompanyId();
        HttpSession session = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest().getSession();
        FndCompany company = fndCompanyMapper.selectByPrimaryKey(companyId);
        String unitCode = (String) session.getAttribute("unitCode");
        String positionCode = (String) session.getAttribute("positionCode");
        String empCode = iRequest.getEmployeeCode();
//        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' + positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"' + positionCode + '"' + "." + '"' + empCode + '"';
        return authorityString;
    }

    @Override
    public ResponseData queryChanceId(CompositeMap var1, String var2) {
        List answer;
        if (ConfigUtils.isMySQL()) {
            answer = this.CreditLineChanceMapper.queryChanceId(var1, var2);
        } else {
            answer = this.CreditLineChanceMapper.queryChanceId(var1, var2);
        }
        return new ResponseData(answer);
    }

    @Override
    public List<HlsCusHlsCreditLineChance> selectCreditLineChanceByCreditLineStatus() {
        return this.CreditLineChanceMapper.selectCreditLineChanceByCreditLineStatus();
    }

    @Override
    public List<HlsCusHlsCreditLineChance> selectCreditLineChanceById1(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) {
        return this.CreditLineChanceMapper.selectCreditLineChanceById1(hlsCusHlsCreditLineChance);
    }


    void checkQuotation(Long marketintReportId) throws HlsCusException {
        if (marketintReportId == null) {
            throw new HlsCusException("项目方案为空，不可创建立项！");
        }
        //marketintReportId  判断该项目方案是否已被立项选中过
        List<HlsCusHlsCreditLineChance> lineChancelist = CreditLineChanceMapper.selectCreditLineChanceByMarketintReportId(marketintReportId);
        if (lineChancelist.size() > 0) {
            throw new HlsCusException("该项目方案已被立项选中过，不可创建立项！");
        }
    }

    /**
     * 综合授信项目查询（根据状态）
     */
    @Override
    public List<HlsCusHlsCreditLineChance> selectCreditLineChanceByStatus(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, Integer page, Integer pageSize,String sortName,String sortOrder) {
        String orderBy = null;
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }

        if (page != null && pageSize != null) {
            PageHelper.startPage(page, pageSize);
            if(net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isNotEmpty(orderBy)){
                PageHelper.orderBy(orderBy);
            }
        }
        return CreditLineChanceMapper.selectCreditLineChanceByStatus(hlsCusHlsCreditLineChance);
    }

    /**
     * 授信立项扇形图查询
     */
    @Override
    public List<HlsCusHlsCreditLineChance> selectCreditLineChanceStatusInfo(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, IRequest iRequest) {
        return CreditLineChanceMapper.selectCreditLineChanceStatusInfo(hlsCusHlsCreditLineChance);
    }

    /**
     * 授信立项首页条件查询
     */
    @Override
    public List<HlsCusHlsCreditLineChance> selectCreditLineChanceByCondition(HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, IRequest iRequest) {
        return CreditLineChanceMapper.selectCreditLineChanceByCondition(hlsCusHlsCreditLineChance);
    }

    /**
     * 授项立项保存
     *
     * @param iRequest
     * @param creditProject
     * @return
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HlsCusCreditProject cascadeSubmit(IRequest iRequest, HlsCusCreditProject creditProject) {
        HlsCusCreditProject hlsCusCreditProject = new HlsCusCreditProject();
        //获取授项立项信息->保存授项立项信息
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
        hlsCusHlsCreditLineChance = creditProject.getHlsCusHlsCreditLineChance();
        if (!ObjectUtils.isEmpty(hlsCusHlsCreditLineChance)) {
            //如果主键为空或者为0，那么就是新建，新建的时候使用编码规则获取授信编号
            if (hlsCusHlsCreditLineChance.getChanceId() == null || hlsCusHlsCreditLineChance.getChanceId() == 0) {
                hlsCusHlsCreditLineChance.setDocumentCategory(HlsCusConstant.CREDIT_LINE_CHANCE.DOCUMENT_CATEGORY);
//                hlsCusHlsCreditLineChance.setBusinessType(HlsCusConstant.CREDIT_LINE_CHANCE.BUSINESS_TYPE);
                hlsCusHlsCreditLineChance.setCreditLineStatus(HlsCusConstant.WORKFLOW_STATUS.NEW);
                //编码规则连注释都没有，经过分析大致是这个意思 iRequest+单据类别+单据类型+业务类型+编码参数
                Map<String, String> params = new HashMap<>();
                String ruleCode = fndCodingRuleValuesService.getCodeRuleValue(iRequest, "PRJ_PROJECT",
                        "PRJ_PROJECT", "LEASE", params);
                hlsCusHlsCreditLineChance.setCreditLineNumber(ruleCode);
                hlsCusHlsCreditLineChance.setCreditLineName(hlsCusHlsCreditLineChance.getCreditLineName());
                if (iRequest.getUserId() != null) {
                    hlsCusHlsCreditLineChance.setCreatedBy(iRequest.getUserId());
                }
//                Long employeeId = Long.valueOf(iRequest.getAttribute("employeeId").toString());
                Long employeeAssignId = Long.valueOf(iRequest.getAttribute("employeeAssignId").toString());
//                hlsCusHlsCreditLineChance.setProposerEmployeeId(employeeId);
                hlsCusHlsCreditLineChance.setProposerEmployeeAssignId(employeeAssignId);
                hlsCusHlsCreditLineChance = this.insertSelective(iRequest, hlsCusHlsCreditLineChance);
            } else {
                hlsCusHlsCreditLineChance.setCreditLineName(hlsCusHlsCreditLineChance.getCreditLineName());
                hlsCusHlsCreditLineChance = this.updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLineChance);
            }
        }


      /*  //保证信息
        List<HlsCusHlsCreditChanceGuarantor> hlsCusHlsCreditChanceGuarantors = creditProject.getHlsCusHlsCreditChanceGuarantors();
        if(!CollectionUtils.isEmpty(hlsCusHlsCreditChanceGuarantors)){
            for (HlsCusHlsCreditChanceGuarantor chanceGuarantor : hlsCusHlsCreditChanceGuarantors) {
                //判断是新增还是更新
                if (chanceGuarantor.getCreditChanceGuarantorId() == null || chanceGuarantor.getCreditChanceGuarantorId() == 0) {
                    //设置头ID外键
                    chanceGuarantor.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
                    chanceGuarantorService.insertSelective(iRequest, chanceGuarantor);
                } else {
                    chanceGuarantorService.updateByPrimaryKeySelective(iRequest, chanceGuarantor);
                }
            }
        }
        //抵押信息
        List<HlsCusHlsCreditChanceMortgage> hlsCusHlsCreditChanceMortgages = creditProject.getHlsCusHlsCreditChanceMortgages();
        if(!CollectionUtils.isEmpty(hlsCusHlsCreditChanceMortgages)){
            for (HlsCusHlsCreditChanceMortgage chanceMortgage : hlsCusHlsCreditChanceMortgages) {
                //判断是新增还是更新
                if (chanceMortgage.getCreditChanceMortgageId() == null || chanceMortgage.getCreditChanceMortgageId() == 0) {
                    //设置ID外键
                    chanceMortgage.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
                    chanceMortgageService.insertSelective(iRequest, chanceMortgage);
                } else {
                    chanceMortgageService.updateByPrimaryKeySelective(iRequest, chanceMortgage);
                }
            }
        }

        //质押信息
        List<HlsCusHlsCreditChancePledge> hlsCusHlsCreditChancePledges = creditProject.getHlsCusHlsCreditChancePledges();
        if(!CollectionUtils.isEmpty(hlsCusHlsCreditChancePledges)){
            for (HlsCusHlsCreditChancePledge chancePledge : hlsCusHlsCreditChancePledges) {
                //判断是新增还是更新
                if (chancePledge.getCreditChancePledgeId() == null || chancePledge.getCreditChancePledgeId() == 0) {
                    chancePledge.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
                    chancePledgeService.insertSelective(iRequest, chancePledge);
                } else {
                    chancePledgeService.updateByPrimaryKeySelective(iRequest, chancePledge);
                }
            }
        }*/
        //财报信息
       /* HlsCusCreditChanceFinStatement chanceFinStatement = creditProject.getHlsCusCreditChanceFinStatement();
        if (!ObjectUtils.isEmpty(chanceFinStatement)) {
            if (chanceFinStatement.getFinStatementId() == null || chanceFinStatement.getFinStatementId() == 0) {
                //设置外键
                chanceFinStatement.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
                chanceFinStatementService.insertSelective(iRequest, chanceFinStatement);
            } else {
                chanceFinStatementService.updateByPrimaryKeySelective(iRequest, chanceFinStatement);
            }
        }*/
        //客户信息
        List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBps = creditProject.getHlsCusHlsCreditLineChanceBps();
        if (CollectionUtils.isNotEmpty(hlsCusHlsCreditLineChanceBps)) {
            for (HlsCusHlsCreditLineChanceBp creditLineChanceBp : hlsCusHlsCreditLineChanceBps) {
                //判断是新增还是更新
                creditLineChanceBp.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
                List<HlsCusHlsCreditLineChanceBp> creditLineChanceBps = creditLineChanceBpMapper.selectDistinctByBp(creditLineChanceBp);
                if (creditLineChanceBps.size() == 0) {
                    creditLineChanceBpService.insertSelective(iRequest, creditLineChanceBp);
                } else {
                    creditLineChanceBpService.updateByPrimaryKeySelective(iRequest, creditLineChanceBp);
                }
            }

        }

        //附件信息
        List<HlsCusHlsCreditLineChanceAttach> hlsCusHlsCreditLineChanceAttaches = creditProject.getHlsCusHlsCreditLineChanceAttaches();
        if (CollectionUtils.isNotEmpty(hlsCusHlsCreditLineChanceAttaches)) {
            for (HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach : hlsCusHlsCreditLineChanceAttaches) {
                //判断是新增还是更新
                if (hlsCusHlsCreditLineChanceAttach.getChanceAttachmentId() == null || hlsCusHlsCreditLineChanceAttach.getChanceAttachmentId() == 0) {
                    hlsCusHlsCreditLineChanceAttach.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
                    cusHlsCreditLineChanceAttachService.insertSelective(iRequest, hlsCusHlsCreditLineChanceAttach);
                } else {
                    cusHlsCreditLineChanceAttachService.updateByPrimaryKeySelective(iRequest, hlsCusHlsCreditLineChanceAttach);
                }
            }

        }

        return creditProject;
    }

    @Autowired
    private HlsCusEmployeeMapper hlsCusEmployeeMapper;

    /**
     * 启动授信立项的提交审批
     *
     * @param requestCtx
     * @param dto
     * @return
     */
    @Override
    public HlsCusHlsCreditLineChance submitApproval(IRequest requestCtx, HlsCusCreditProject dto) {
        //保存页面信息
//        HlsCusCreditProject hlsCusCreditProject = this.cascadeSubmit(requestCtx, dto);
        //获取申请人
//        HlsEmployee employee = hlsCusEmployeeMapper.getEmployeeCode(requestCtx.getUserId());
//        String employeeCode = employee.getEmployeeCode();
//        requestCtx.setEmployeeCode(requestCtx.getEmployeeCode());
        //获取需要的参数
        Long chanceId = dto.getHlsCusHlsCreditLineChance().getChanceId();
        HlsCusHlsCreditLineChance creditLineChance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(chanceId);
        List<HlsCusHlsCreditLineChance> creditChanceListParam = new ArrayList<>();
        creditChanceListParam.add(creditLineChance);

//        if ("APPROVING".equalsIgnoreCase(creditLineChance.getCreditLineStatus()) || "APPROVED".equalsIgnoreCase(creditLineChance.getCreditLineStatus())
//                || "CLOSED".equalsIgnoreCase(creditLineChance.getCreditLineStatus())) {
//
//
//
//        }

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        //此次启动的工作流的唯一标识
        params.put("workFlowType", "CREDIT_CHANCE_CREATE_WFL");

        //工作流状态
        params.put("creditLineStatus", creditLineChance.getCreditLineStatus());
        //表单的主键
        params.put("chanceId", creditLineChance.getChanceId());
        params.put("creditFlag", creditLineChance.getCreditFlag());
        params.put("businessKey", creditLineChance.getChanceId());
        Map<String, Object> evenParams = new HashMap<>();
        activitiStartService.start(requestCtx, creditChanceListParam, params);

        //插入事件
        String userName = "";
        if (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(requestCtx.getEmployeeCode())).get(0).getUserName();
        }
        //消息用于动态
        String msg = userName + "提交了一条授信立项:" + creditLineChance.getCreditLineName() + creditLineChance.getCreditLineNumber();
        evenParams.put("message", msg);
        evenParams.put("noticeTitle", "授信立项创建");
        evenParams.put("url", "");
        evenParams.put("level", 2L);
        evenParams.put("noticeType", "NOTICE");
        evenParams.put("sourceUserId", requestCtx.getUserId());
        //sysEventService.eventSave(requestCtx, creditLineChance.getChanceId(), creditLineChance.getDocumentCategory(), creditLineChance.getDocumentType(), "HLS_CREDIT_LINE_CHANCE", "HLS_CREDIT_LINE_CHANCE", "P2D", evenParams);

        //修改合同状态

        HlsCusHlsCreditLineChance chance = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(chanceId);
        chance.setCreditLineStatus("APPROVING");
        chance = self().updateByPrimaryKeySelective(requestCtx, chance);
        return chance;
    }

    /**
     * 按照条件查询授信立项信息
     *
     * @param iRequest
     * @param hlsCusHlsCreditLineChance
     * @return 返回对象的集合
     */
    @Override
    public List<HlsCusHlsCreditLineChance> selectModelByCondition(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance, Integer page, Integer pageSize) {
        if (page != null && pageSize != 0) {
            PageHelper.startPage(page, pageSize);
        }
        return CreditLineChanceMapper.selectModelByCondition(hlsCusHlsCreditLineChance);
    }

    private static final String STATUS_NEW = "NEW";
    private static final String STATUS_APPROVED_RETURN = "NEW";

    @Override
    public List<HlsCusHlsCreditLineChance> closeCreditChance(IRequest requestCtx, List<HlsCusHlsCreditLineChance> hlsCusHlsCreditLineChances) {

        if (CollectionUtils.isNotEmpty(hlsCusHlsCreditLineChances)) {
            for (HlsCusHlsCreditLineChance creditLineChance : hlsCusHlsCreditLineChances) {
                //判断状态为 NEW || APPROVED_RETURN
//                if(creditLineChance.getCreditLineStatus() == STATUS_NEW ||creditLineChance.getCreditLineStatus() == STATUS_APPROVED_RETURN){
                CreditLineChanceMapper.updateCreditChanceStatus(creditLineChance.getChanceId());
//                }
            }

        }
        return null;


    }

    //项目立项合同文本生成入口
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<FndAttachment> reportCreateDocx(IRequest iRequest, HlsCusHlsCreditLineChance hlsCreditLineChance) throws Exception, ResMessageException, ParameterNullException {
        return self().reportCreateDocx(iRequest, hlsCreditLineChance.getChanceId(), "PRJ_CHANCE_REPORT"); // HLS_MARKETING_REPORT
    }

    //合同文本生成过程
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachment> reportCreateDocx(IRequest iRequest, Long chanceId, String templateType) throws Exception, ResMessageException, ParameterNullException {
        List<FndAttachment> list = new ArrayList<>();

        if (chanceId == null) {
            throw new ResMessageException("未找到项目立项单据，请保存后在生成!");
        }

        Long companyId = iRequest.getCompanyId();
        if (companyId == null || companyId == -1L) {
            companyId = 3L;
        }
        DocFileTempletRule docFileTempletRule = new DocFileTempletRule();
        docFileTempletRule.setCompanyId(companyId);
        docFileTempletRule.setTempletType(templateType);
        List<DocFileTempletRule> rules = docFileTempletRuleService.selectSelective(iRequest, docFileTempletRule);
        if (!rules.isEmpty()) {
            docFileTempletRule = rules.get(0);
        } else {
            throw new ResMessageException("未找到合同文本模板，请核查后再生成!");
        }
        //根据规则引擎得到模板集合
        String json = null;
        RuleEngineType ruleEngineType = new RuleEngineType();
        try {
            Map<String, Object> pMap = new HashMap<String, Object>();
            pMap.put("chanceId", chanceId);

            ruleEngineType.setRuleEngineType(docFileTempletRule.getRuleEngineType());
            List<RuleEngineType> ruleEngineTypes = ruleEngineTypeService.selectSelective(iRequest, ruleEngineType);
            if (ruleEngineTypes.size() > 0) {
                ruleEngineType = ruleEngineTypes.get(0);
            }
            json = datasource2Json.executeSQL4Json(ruleEngineType.getDataSourceId(), pMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject0 = JSON.parseObject(json);
        JSONObject jsonObject1 = JSON.parseObject(JSON.toJSONString(jsonObject0.get(ruleEngineType.getDataSourceId().toString())));
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(jsonObject1.get("default")));
        jsonObject.put("ruleEngineId", docFileTempletRule.getRuleEngineId());

        //拿到匹配的所有模板Id
        String[] templetIds = hlsRuleEngineInitService.ruleEngineInit(iRequest, jsonObject);
        if (templetIds.length == 0) {
//            throw new ResMessageException("未找到项目尽调模板，请核查后再生成!");
            throw new ResMessageException("未找到项目尽调模板，请核查后再生成!");
        }
        HlsDocFileTemplet hlsDocFileTemplet = null;

        //每次生成合同文本区删除原来的合同文本记录
        HlsCreditLineAttach hlsCreditLineAttach = new HlsCreditLineAttach();

        hlsCreditLineAttach.setChanceId(chanceId);
        hlsCreditLineAttach.setAttachmentCategory(REPORT_DOCX);
        List<HlsCreditLineAttach> atmLists = creditLineAttachService.selectSelective(iRequest, hlsCreditLineAttach);

        atmLists.forEach(item -> {
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName(REPORT_DOCX);
            fndAttachmentMulti.setTablePkValue(item.getChanceAttachmentId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourceTypeCode = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue(REPORT_DOCX, item.getChanceAttachmentId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue(FND_ATM_ATTACHMENT_MULTI, sourceTypeCode);
            }
            creditLineAttachService.deleteByPrimaryKey(item);
        });

        HlsCreditLineAttach ppa = null;
        Map<String, Object> params = null;

        HlsCusHlsCreditLineChanceBp hlsCreditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
        hlsCreditLineChanceBp.setChanceId(chanceId);
        List<HlsCusHlsCreditLineChanceBp> chanceBps
                = creditLineChanceBpMapper.select(hlsCreditLineChanceBp);

//        for (HlsCusHlsMarketingReportBp reportBp : reportBps) {

        for (int i = 0; i < templetIds.length; i++) {
            hlsDocFileTemplet = new HlsDocFileTemplet();
            hlsDocFileTemplet.setTempletId(Long.parseLong(templetIds[i]));
            hlsDocFileTemplet = hlsDocFileTempletService.selectByPrimaryKey(iRequest, hlsDocFileTemplet);
            if (hlsDocFileTemplet == null) {
                throw new HlsCusException(NOT_FOUND_CONTRACT_TEMPLATE);
            }
//                if (hlsDocFileTemplet.getUsageCategory().equals(reportBp.getBpCategory())) {  单个合同文本生成不做匹配 直接生成
            //重新插入合同文本记录
            HlsCusHlsCreditLineChance hmr = new HlsCusHlsCreditLineChance();
            hmr.setChanceId(chanceId);
            hmr = this.selectByPrimaryKey(iRequest, hmr);
            StringBuilder attachmentName = new StringBuilder();
            attachmentName.append(hmr.getCreditLineNumber()).append("-").append(hlsDocFileTemplet.getTempletName());
            ppa = new HlsCreditLineAttach();
            ppa.setChanceId(chanceId);
            ppa.setAttachmentCategory(REPORT_DOCX);
            ppa.setDocumentName(attachmentName.toString());
            ppa.setDescription(CONTRACT_DOCX_DESCRIPTION);
            ppa.setSourceId(hlsDocFileTemplet.getTempletId());
            ppa.setUploadPerson(iRequest.getUserId().toString());
            ppa.setUploadDate(new Date());
//                    ppa.setBpCategory(reportBp.getBpCategory());
//                    ppa.setBpId(reportBp.getBpId());
            ppa = creditLineAttachService.insertSelective(iRequest, ppa);

            //取对应参数生成合同文本文件
            params = new HashMap<String, Object>();
            params.put(CHANCE_ID, chanceId);
            params.put(TEMPLET_ID, hlsDocFileTemplet.getTempletId());
//                    params.put(BP_ID, ppa.getBpId());
            params.put(CHANCE_ATTACHMENT_ID, ppa.getChanceAttachmentId());
            params.put(TABLE_NAME, REPORT_DOCX);
            list.addAll(chanceDocxService.process(iRequest, params, jsonObject));
//                }
        }
//        }
        return list;

    }

    @Override
    public void generateChanceCompare(IRequest requestCtx, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) {
        List<HlsBusinessAccessCompare> businessAccessCompareAll = this.hlsBusinessAccessCompareMapper.selectAll();
        HlsChanceBusinessAccessCompare chanceCompare = new HlsChanceBusinessAccessCompare();
        Long documentId = hlsCusHlsCreditLineChance.getChanceId();
        chanceCompare.setDocumentId(hlsCusHlsCreditLineChance.getChanceId());
        List<HlsChanceBusinessAccessCompare> chanceCompares = this.chanceAccessCompareService.selectSelective(requestCtx, chanceCompare);
        List<String> chanceAccessCompares = (List) chanceCompares.stream().map(HlsChanceBusinessAccessCompare::getBusinessAccess).collect(Collectors.toList());
        List<HlsBusinessAccessCompare> filterAccess = (List) businessAccessCompareAll.stream().filter((o) -> {
            return chanceAccessCompares.indexOf(o.getBusinessAccess()) < 0;
        }).collect(Collectors.toList());
        Iterator var13 = filterAccess.iterator();

        HlsChanceBusinessAccessCompare item;
        while (var13.hasNext()) {
            HlsBusinessAccessCompare item1 = (HlsBusinessAccessCompare) var13.next();
            item = new HlsChanceBusinessAccessCompare();
            item.setDocumentId(documentId);
            item.setBusinessAccess(item1.getBusinessAccess());
            item.setProjectAccessItems(item1.getProjectAccessItems());
            //Long chanceCompareId = chanceCompareMapper.selectSeqId();
            //item.setChanceCompareId(chanceCompareId);
            this.chanceCompareMapper.addChanceCompare(item);
        }
        ;
    }

    //获取接口表数据
    public List<FndInterfaceLines> getInterfaceData(Long hdId, Long readLine) {
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hdId);
        fndInterfaceLines.setReadLine(readLine);
        List<FndInterfaceLines> fndInterfaceLinesList = fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);
        return fndInterfaceLinesList;
    }

    @Override
    public void chanceAccessCompareImport(IRequest iRequest, Long hdId, Long chanceId) throws ExcelException, Exception, ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        List<FndInterfaceLines> fndInterfaceLinesList = getInterfaceData(hdId, 4L);
        List<HlsChanceBusinessAccessCompare> resultList = new ArrayList<>();

        if (chanceId == null ) {
            throw new com.hand.hls.exception.HlsCusException("未正确获取chanceId！");
        }

        HlsChanceBusinessAccessCompare chanceCompare = new HlsChanceBusinessAccessCompare();
        chanceCompare.setDocumentId(chanceId);
        List<HlsChanceBusinessAccessCompare> baseChanceCompares = this.chanceAccessCompareService.selectSelective(iRequest, chanceCompare);

        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(chanceId);
        chance = self().selectByPrimaryKey(iRequest, chance);

        for (FndInterfaceLines fndInterfaceLine : fndInterfaceLinesList) {
            HlsChanceBusinessAccessCompare record = new HlsChanceBusinessAccessCompare();

            record.setDocumentId(chanceId);
            record.setBusinessAccess(fndInterfaceLine.getAttributes_1());
            record.setProjectAccessItems(fndInterfaceLine.getAttributes_2());
            record.setItemsDetail(fndInterfaceLine.getAttributes_3());

            List<HlsChanceBusinessAccessCompare> filterAccess = (List) baseChanceCompares.stream().filter((o) -> {
                return fndInterfaceLine.getAttributes_1().equals(o.getBusinessAccess());
            }).collect(Collectors.toList());
            if (filterAccess.size()>0) {
                throw new com.hand.hls.exception.HlsCusException("业务准入对照：'"+fndInterfaceLine.getAttributes_1()+"'已存在！");
            }

            String checkResult=fndInterfaceLine.getAttributes_4();
            if("符合".equals(checkResult)){
                record.setCheckResult("MATCH");
            }else{
                record.setCheckResult("NOT_MATCH");
            }
            this.chanceCompareMapper.addChanceCompare(record);
            resultList.add(record);
        }
    }



    //保存chang_req
    @Override
    public HlsCusHlsCreditLineChance changeCreate(IRequest request, HlsCusHlsCreditLineChance hlsCusChance) throws com.hand.hls.exception.HlsCusException {
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(hlsCusChance.getChanceId());

        //校验项目状态
        chance = self().selectByPrimaryKey(request, chance);
        if ("NEW".equals(chance.getCreditLineStatus()) || "REJECTED".equals(chance.getCreditLineStatus())) {
            throw new com.hand.hls.exception.HlsCusException("当前项目无需进行变更,可直接维护信息!");
        }
        if ("APPROVING".equals(chance.getCreditLineStatus())) {
            throw new com.hand.hls.exception.HlsCusException("当前项目正在审批中，无法进行变更!");
        }

        /*单据状态置为挂起, 立项变更方案修改,不改为暂挂*/
        /*if(!"CHANCE_ALL_CHANGE".equals(hlsCusChance.getChangeType())){
            chance.setCreditLineStatus("PENDING");
            chance.set__status("update");
            self().updateByPrimaryKeySelective(request, chance);
        }*/

        /*插入审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setDocumentId(chance.getChanceId());
        hlsCusChangeReqInfo.setDocumentCategory("PRJ_CHANCE");
        List<HlsCusChangeReqInfo> hlsCusChangeReqInfoList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);

        hlsCusChangeReqInfo.setStatus("NEW");
        /*List<HlsCusChangeReqInfo> newChangeReqList = hlsCusChangeReqInfoService.select(request, hlsCusChangeReqInfo, 1, 99999);
        if (newChangeReqList != null && newChangeReqList.size() > 0) {
            throw new com.hand.hls.exception.HlsCusException("该项目正在变更中!");
        }*/

        hlsCusChangeReqInfo.setDocumentVersionId(hlsCusChangeReqInfoList.size() + 1L);
        hlsCusChangeReqInfo.setChangeReqUserId(request.getUserId());
        hlsCusChangeReqInfo.setChangeDescription(chance.getChangeDescription());
        hlsCusChangeReqInfo.setChangeReqDate(new Date());
        hlsCusChangeReqInfo.setChangeType(hlsCusChance.getChangeType());
        hlsCusChangeReqInfo.setApproveNumber(chance.getApproveNumber());
        hlsCusChangeReqInfo.setChangeInfoDesc(chance.getChangeInfoDesc());
        hlsCusChangeReqInfo.setChangeEffctDesc(chance.getChangeEffctDesc());
        //创建时设置为N，流程结束后再修改为Y
        hlsCusChangeReqInfo.setInstanceEndFlag("N");
        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.insertSelective(request, hlsCusChangeReqInfo);

        /*复制当前项目，创建变更数据*/
        //复制一份用作变更的数据, dataType:CHANGE_REQ
        hlsCusChance = selectByPrimaryKey(request, hlsCusChance);
        HlsCusHlsCreditLineChance prjChance = new HlsCusHlsCreditLineChance();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusChance);
        hlsBeanRefUtilService.setFieldValue(prjChance, map1);
        prjChance.setRefChanceId(chance.getChanceId());
        prjChance.setChanceId(null);
        prjChance.setDataType("CHANGE_REQ");
        prjChance.setCreditLineStatus("NEW");
        prjChance.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        prjChance = self().insertSelective(request, prjChance);

        //复制一份用作历史的数据, dataType:CHANGE_REQ_HISTORY, 在变更结束后改为HISTORY
        HlsCusHlsCreditLineChance prjChanceH = new HlsCusHlsCreditLineChance();
        hlsBeanRefUtilService.setFieldValue(prjChanceH, map1);
        prjChanceH.setRefChanceId(prjChanceH.getChanceId());
        prjChanceH.setChanceId(null);
        prjChanceH.setDataType("CHANGE_REQ_HISTORY");
        prjChanceH.setCreditLineStatus("NEW");
        prjChanceH.setChangeReqId(hlsCusChangeReqInfo.getChangeReqId());
        prjChanceH = self().insertSelective(request, prjChanceH);

        //插入从表
        chanceBackUp(request, prjChance);
        chanceBackUp(request, prjChanceH);

        //查询打开链接需要的参数
        HlsCusHlsCreditLineChance cusPrjChance = new HlsCusHlsCreditLineChance();
        cusPrjChance.setChanceId(prjChance.getChanceId());
        cusPrjChance = self().selectByPrimaryKey(request, cusPrjChance);
        /*List<HlsCusHlsCreditLineChance> list = hlsCusPrjProjectMapper.selectProjectChangeReqInfo(cusPrjChance);

        if (CollectionUtils.isNotEmpty(list) && list.size() == 1) {
            cusPrjChance = list.get(0);
        }*/
        cusPrjChance.setChangeType(hlsCusChance.getChangeType());
        return cusPrjChance;
    }

    //保存chang_req
    @SneakyThrows
    @Override
    public HlsCusHlsCreditLineChance reCreate(IRequest request, HlsCusHlsCreditLineChance hlsCusChance) {
        HlsCusHlsCreditLineChance chance = new HlsCusHlsCreditLineChance();
        chance.setChanceId(hlsCusChance.getChanceId());

        //校验项目状态
        chance = self().selectByPrimaryKey(request, chance);
        String codeRuleValue;
        Map<String, String> params = new HashMap<String, String>();
        if("Y".equals(hlsCusChance.getCreditFlag())){
            codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(request, "CREDIT_PRJ_PROJECT", "CREDIT_PRJ_PROJECT", "LEASE", params);
        }else{
            codeRuleValue = fndCodingRuleValuesService.getCodeRuleValue(request, "PRJ_PROJECT", "PRJ_PROJECT", "LEASE", params);
        }

        /*复制当前项目，创建变更数据*/
        //复制一份用作变更的数据, dataType:CHANGE_REQ
        hlsCusChance = selectByPrimaryKey(request, hlsCusChance);
        HlsCusHlsCreditLineChance prjChance = new HlsCusHlsCreditLineChance();
        Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusChance);
        hlsBeanRefUtilService.setFieldValue(prjChance, map1);
        prjChance.setRefChanceId(chance.getChanceId());
        prjChance.setChanceId(null);
        prjChance.setDataType("NORMAL");
        prjChance.setCreditLineStatus("NEW");
        prjChance.setCreditLineNumber(codeRuleValue);
        prjChance = self().insertSelective(request, prjChance);


        //插入从表
        chanceBackUp(request, prjChance);

        //查询打开链接需要的参数
        HlsCusHlsCreditLineChance cusPrjChance = new HlsCusHlsCreditLineChance();
        cusPrjChance.setChanceId(prjChance.getChanceId());
        cusPrjChance = self().selectByPrimaryKey(request, cusPrjChance);
        cusPrjChance.setChangeType(hlsCusChance.getChangeType());
        return cusPrjChance;
    }


    //项目变更复制从表信息
    @Override
    public void chanceBackUp(IRequest iRequest, HlsCusHlsCreditLineChance prjChance) throws com.hand.hls.exception.HlsCusException {

        //对外报送方案  prj_quotation      pq.source_document_id = #{chanceId}
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(prjChance.getRefChanceId());
        hlsCusPrjQuotation.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 100000);
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            HlsCusPrjQuotation hlsCusPrjQuotation1 = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotation1, map);
            hlsCusPrjQuotation1.setSourceDocumentId(prjChance.getChanceId());
            hlsCusPrjQuotation1.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
            hlsCusPrjQuotation1.setPaymentNumber(prjChance.getCreditLineNumber());
            hlsCusPrjQuotation1.setPaymentStatus("NEW");
            hlsCusPrjQuotation1.setChangeRefQuotationId(dt.getQuotationId());
            hlsCusPrjQuotation1 = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation1);

            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
            List<HlsCusPrjQuotationDetails> detailList = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000);
            if (CollectionUtils.isNotEmpty(detailList)) {
                hlsCusPrjQuotationDetails = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000).get(0);
                HlsCusPrjQuotationDetails copyDetails = new HlsCusPrjQuotationDetails();
                Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationDetails);
                hlsBeanRefUtilService.setFieldValue(copyDetails, map1);
                copyDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationDetailsService.insertSelective(iRequest, copyDetails);
            }

        }

        //客户信息  hls_credit_line_chance_bp    t.CHANCE_ID=#{chanceId}
        List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBpList = new ArrayList<>();
        HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
        hlsCusHlsCreditLineChanceBp.setChanceId(prjChance.getRefChanceId());
        hlsCusHlsCreditLineChanceBpList = chanceBpService.select(iRequest, hlsCusHlsCreditLineChanceBp, 1, 100000);
        for (HlsCusHlsCreditLineChanceBp dt : hlsCusHlsCreditLineChanceBpList) {
            HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp1 = new HlsCusHlsCreditLineChanceBp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusHlsCreditLineChanceBp1, map);
            hlsCusHlsCreditLineChanceBp1.setChanceId(prjChance.getChanceId());
            chanceBpService.insertSelective(iRequest, hlsCusHlsCreditLineChanceBp1);
        }


        //业务准入对照  chance_business_access_compare    cc.DOCUMENT_ID=#{documentId} chanceId?
        List<HlsChanceBusinessAccessCompare> hlsChanceBusinessAccessCompareList = new ArrayList<>();
        HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare = new HlsChanceBusinessAccessCompare();
        hlsChanceBusinessAccessCompare.setDocumentId(prjChance.getRefChanceId());
        hlsChanceBusinessAccessCompareList = chanceAccessCompareService.select(iRequest, hlsChanceBusinessAccessCompare, 1, 100000);
        for (HlsChanceBusinessAccessCompare dt : hlsChanceBusinessAccessCompareList) {
            HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare1 = new HlsChanceBusinessAccessCompare();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsChanceBusinessAccessCompare1, map);
            hlsChanceBusinessAccessCompare1.setDocumentId(prjChance.getChanceId());
            chanceCompareMapper.addChanceCompare(hlsChanceBusinessAccessCompare1);
            //chanceAccessCompareService.insertSelective(iRequest, hlsChanceBusinessAccessCompare1);
        }


        //附件信息  hls_credit_line_attach   hra.attachment_category in ('CHANCE_ATT','HLS_CREDIT_CHANCE_DOCX') and hra.CHANCE_ID = nvl(#{chanceId},-99)
        List<HlsCusHlsCreditLineChanceAttach> hlsCusPrjChanceAttachList = new ArrayList<>();
        HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach = new HlsCusHlsCreditLineChanceAttach();
        hlsCusHlsCreditLineChanceAttach.setChanceId(prjChance.getRefChanceId());
        hlsCusPrjChanceAttachList = cusHlsCreditLineChanceAttachService.select(iRequest, hlsCusHlsCreditLineChanceAttach, 1, 100000);
        for (HlsCusHlsCreditLineChanceAttach dt : hlsCusPrjChanceAttachList) {

            //项目附件表
            HlsCusHlsCreditLineChanceAttach hpa = new HlsCusHlsCreditLineChanceAttach();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hpa, map);
            hpa.setChanceId(prjChance.getChanceId());
            cusHlsCreditLineChanceAttachService.insertSelective(iRequest, hpa);
            //复制系统附件表
            copyFndAtmFile(iRequest, String.valueOf(dt.getChanceAttachmentId()), String.valueOf(hpa.getChanceAttachmentId()), "HLS_CREDIT_LINE_CHANCE");
        }

        //授信方案
        List<HlsCreditPlan> hlsCreditPlanList = new ArrayList<>();
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(prjChance.getRefChanceId());
        hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlanList = hlsCreditPlanService.select(iRequest, hlsCreditPlan, 1, 100000);
        for (HlsCreditPlan dt : hlsCreditPlanList) {
            HlsCreditPlan hlsCreditPlan1 = new HlsCreditPlan();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCreditPlan1, map);
            hlsCreditPlan1.setSourceDocumentId(prjChance.getChanceId());
            hlsCreditPlan1.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
            hlsCreditPlan1.setChangeBeforeCreditAmt(dt.getCreditAmt());
            hlsCreditPlan1.setDateToBefore(dt.getDateTo());
            hlsCreditPlan1 = hlsCreditPlanService.insertSelective(iRequest, hlsCreditPlan1);
        }
    }

    @Override
    public HlsCusHlsCreditLineChance prjChangeSubmitWfl(IRequest iRequest, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) {

        HlsCusHlsCreditLineChance prjChance = new HlsCusHlsCreditLineChance();
        prjChance = self().selectByPrimaryKey(iRequest, hlsCusHlsCreditLineChance);
        List<HlsCusHlsCreditLineChance> hlsCusPrjProjectList = new ArrayList<>();
        hlsCusPrjProjectList.add(prjChance);

        /*修改审批信息表*/
//        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
//        hlsCusChangeReqInfo.setChangeReqId(prjChance.getChangeReqId());
//        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(iRequest, hlsCusChangeReqInfo);
//        hlsCusChangeReqInfo.setStatus("APPROVING");
//        hlsCusChangeReqInfo.setWflNodeStatus(null);
//        hlsCusChangeReqInfo = hlsCusChangeReqInfoService.updateByPrimaryKey(iRequest, hlsCusChangeReqInfo);
//        HlsCusHlsCreditLineChance prjChanceOld = new HlsCusHlsCreditLineChance();
//        prjChanceOld.setChanceId(hlsCusChangeReqInfo.getDocumentId());
//        prjChanceOld = self().selectByPrimaryKey(iRequest, prjChanceOld);
//        prjChanceOld.setCreditLineStatus("PENDING");
//        self().updateByPrimaryKeySelective(iRequest, prjChanceOld);

        //获取申请人
//        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
//        String employeeCode = employee.getEmployeeCode();
//        iRequest.setEmployeeCode(employeeCode);

        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();

        // 如果立项变更与授信立项变更流程不同,则判断发起对应的流程
        /*if("CHANCE_ALL_CHANGE".equals(hlsCusChangeReqInfo.getChangeType())){
            params.put("workFlowType", "PRJ_CHANCE_CHANGE_WFL");
            params.put("wflKey", "PRJ_CHANCE_CHANGE_WFL");
        }else{
            params.put("workFlowType", "PRJ_CREDIT_CHANCE_CHANGE_WFL");
            params.put("wflKey", "PRJ_CREDIT_CHANCE_CHANGE_WFL");
        }*/

        params.put("workFlowType", "PRJ_CHANCE_CHANGE_WFL");
        params.put("wflKey", "PRJ_CHANCE_CHANGE_WFL");// PRJ_PROJECT_CHANGE_WFL

        activitiStartService.start(iRequest, hlsCusPrjProjectList, params);

        prjChance.setCreditLineStatus("APPROVING");
        HlsCusPrjProject getPrj = new HlsCusPrjProject();
        getPrj.setProjectId(prjChance.getChanceId());
        //prjChance.setObjectVersionNumber(self().selectByPrimaryKey(iRequest, getPrj).getObjectVersionNumber());
        prjChance = self().updateByPrimaryKeySelective(iRequest, prjChance);

        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + prjChance.getCreditLineName() + "项目的立项变更" + prjChance.getCreditLineNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "项目变更");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        //todo 后面几个参数还不确定 "BAC", "PRJ_PROJECT_WFL", "P2D"
        sysEventService.eventSave(iRequest, prjChance.getChanceId(), prjChance.getDocumentCategory(), prjChance.getDocumentType(), "BAC", "PRJ_PROJECT_WFL", "P2D", paramsEvent);

        return prjChance;
    }

    @Override
    public boolean changeCancel(IRequest requestCt, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) {
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChanceOld = self().selectByPrimaryKey(requestCt, hlsCusHlsCreditLineChance);
        hlsCusHlsCreditLineChanceOld.setCreditLineStatus("APPROVED");
        self().updateByPrimaryKeySelective(requestCt, hlsCusHlsCreditLineChanceOld);

        /*更新审批信息表*/
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(hlsCusHlsCreditLineChance.getChangeReqId());
        HlsCusChangeReqInfo changeReqInfo = hlsCusChangeReqInfoService.selectByPrimaryKey(requestCt, hlsCusChangeReqInfo);

        changeReqInfo.setStatus("CANCEL");
        changeReqInfo.set__status("update");
        hlsCusChangeReqInfoService.updateByPrimaryKeySelective(requestCt, changeReqInfo);
        return true;
    }

    @Override
    public boolean creditCancel(IRequest requestCt, HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance) {
        hlsCusHlsCreditLineChance =hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(hlsCusHlsCreditLineChance.getChanceId());

        //关闭授信尽调
        HlsCusPrjProject project=new HlsCusPrjProject();
        project.setProjectId(hlsCusHlsCreditLineChance.getJdProjectId());
        project.setProjectStatus("CLOSED");
        hlsCusPrjProjectMapper.updateByPrimaryKeySelective(project);

        //关闭额度占用创建的项目
        HlsCusPrjProject projectN=new HlsCusPrjProject();
        projectN.setChanceId(hlsCusHlsCreditLineChance.getChanceId());
        projectN.setDataType("CREDIT_NORMAL");
        projectN.setDataClass("NORMAL");
        List<HlsCusPrjProject> prjList=new ArrayList<>();
        prjList=hlsCusPrjProjectMapper.select(projectN);
        for(HlsCusPrjProject item:prjList){
            HlsCusPrjProject projectU=new HlsCusPrjProject();
            projectU.setProjectId(item.getProjectId());
            projectU.setProjectStatus("CLOSED");
            hlsCusPrjProjectMapper.updateByPrimaryKeySelective(projectU);
        }

        hlsCusHlsCreditLineChance.setCreditLineStatus("CLOSED");
        self().updateByPrimaryKeySelective(requestCt, hlsCusHlsCreditLineChance);
        return true;
    }

    @Override
    public void deleteOld(Long projectIdOld, IRequest request) {
        //对外报送方案  prj_quotation      pq.source_document_id = #{chanceId}
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(projectIdOld);
        hlsCusPrjQuotation.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCusPrjQuotationMapper.delete(hlsCusPrjQuotation);

        //客户信息  hls_credit_line_chance_bp    t.CHANCE_ID=#{chanceId}
        HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
        hlsCusHlsCreditLineChanceBp.setChanceId(projectIdOld);
        creditLineChanceBpMapper.delete(hlsCusHlsCreditLineChanceBp);

        //业务准入对照  chance_business_access_compare    cc.DOCUMENT_ID=#{documentId} chanceId?
        HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare = new HlsChanceBusinessAccessCompare();
        hlsChanceBusinessAccessCompare.setDocumentId(projectIdOld);
        chanceCompareMapper.delete(hlsChanceBusinessAccessCompare);

        //附件信息  hls_credit_line_attach   hra.attachment_category in ('CHANCE_ATT','HLS_CREDIT_CHANCE_DOCX') and hra.CHANCE_ID = nvl(#{chanceId},-99)
        HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach = new HlsCusHlsCreditLineChanceAttach();
        hlsCusHlsCreditLineChanceAttach.setChanceId(projectIdOld);
        hlsCusHlsCreditLineChanceAttachMapper.delete(hlsCusHlsCreditLineChanceAttach);

        //授信方案
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(projectIdOld);
        hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlanMapper.delete(hlsCreditPlan);
    }

    @Override
    public void updateOld(IRequest iRequest, Long projectIdOld, Long projectIdNew, IRequest request) throws com.hand.hls.exception.HlsCusException {
        //对外报送方案  prj_quotation      pq.source_document_id = #{chanceId}
        List<HlsCusPrjQuotation> hlsCusPrjQuotationList = new ArrayList<>();
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setSourceDocumentId(projectIdNew);
        hlsCusPrjQuotation.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCusPrjQuotationList = hlsCusPrjQuotationService.select(iRequest, hlsCusPrjQuotation, 1, 100000);
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            HlsCusPrjQuotation hlsCusPrjQuotation1 = new HlsCusPrjQuotation();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusPrjQuotation1, map);
            hlsCusPrjQuotation1.setSourceDocumentId(projectIdOld);
            hlsCusPrjQuotation1.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
            hlsCusPrjQuotation1 = hlsCusPrjQuotationService.insertSelective(iRequest, hlsCusPrjQuotation1);

            HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
            hlsCusPrjQuotationDetails.setQuotationId(dt.getQuotationId());
            List<HlsCusPrjQuotationDetails> detailList = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000);
            if (CollectionUtils.isNotEmpty(detailList)) {
                hlsCusPrjQuotationDetails = hlsCusPrjQuotationDetailsService.select(iRequest, hlsCusPrjQuotationDetails, 1, 1000).get(0);
                HlsCusPrjQuotationDetails copyDetails = new HlsCusPrjQuotationDetails();
                Map<String, String> map1 = hlsBeanRefUtilService.getFieldValueMap(hlsCusPrjQuotationDetails);
                hlsBeanRefUtilService.setFieldValue(copyDetails, map1);
                copyDetails.setQuotationId(hlsCusPrjQuotation1.getQuotationId());
                hlsCusPrjQuotationDetailsService.insertSelective(iRequest, copyDetails);
            }

        }



        //客户信息  hls_credit_line_chance_bp    t.CHANCE_ID=#{chanceId}
        List<HlsCusHlsCreditLineChanceBp> hlsCusHlsCreditLineChanceBpList = new ArrayList<>();
        HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp = new HlsCusHlsCreditLineChanceBp();
        hlsCusHlsCreditLineChanceBp.setChanceId(projectIdNew);
        hlsCusHlsCreditLineChanceBpList = chanceBpService.select(iRequest, hlsCusHlsCreditLineChanceBp, 1, 100000);
        for (HlsCusHlsCreditLineChanceBp dt : hlsCusHlsCreditLineChanceBpList) {
            HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp1 = new HlsCusHlsCreditLineChanceBp();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCusHlsCreditLineChanceBp1, map);
            hlsCusHlsCreditLineChanceBp1.setChanceId(projectIdOld);
            chanceBpService.insertSelective(iRequest, hlsCusHlsCreditLineChanceBp1);
        }


        //业务准入对照  chance_business_access_compare    cc.DOCUMENT_ID=#{documentId} chanceId?
        List<HlsChanceBusinessAccessCompare> hlsChanceBusinessAccessCompareList = new ArrayList<>();
        HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare = new HlsChanceBusinessAccessCompare();
        hlsChanceBusinessAccessCompare.setDocumentId(projectIdNew);
        hlsChanceBusinessAccessCompareList = chanceAccessCompareService.select(iRequest, hlsChanceBusinessAccessCompare, 1, 100000);
        for (HlsChanceBusinessAccessCompare dt : hlsChanceBusinessAccessCompareList) {
            HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare1 = new HlsChanceBusinessAccessCompare();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsChanceBusinessAccessCompare1, map);
            hlsChanceBusinessAccessCompare1.setDocumentId(projectIdOld);
            chanceCompareMapper.addChanceCompare(hlsChanceBusinessAccessCompare1);
        }


        //附件信息  hls_credit_line_attach   hra.attachment_category in ('CHANCE_ATT','HLS_CREDIT_CHANCE_DOCX') and hra.CHANCE_ID = nvl(#{chanceId},-99)
        List<HlsCusHlsCreditLineChanceAttach> hlsCusPrjChanceAttachList = new ArrayList<>();
        HlsCusHlsCreditLineChanceAttach hlsCusHlsCreditLineChanceAttach = new HlsCusHlsCreditLineChanceAttach();
        hlsCusHlsCreditLineChanceAttach.setChanceId(projectIdNew);
        hlsCusPrjChanceAttachList = cusHlsCreditLineChanceAttachService.select(iRequest, hlsCusHlsCreditLineChanceAttach, 1, 100000);
        for (HlsCusHlsCreditLineChanceAttach dt : hlsCusPrjChanceAttachList) {

            //项目附件表
            HlsCusHlsCreditLineChanceAttach hpa = new HlsCusHlsCreditLineChanceAttach();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hpa, map);
            hpa.setChanceId(projectIdOld);
            cusHlsCreditLineChanceAttachService.insertSelective(iRequest, hpa);
            //复制系统附件表
            copyFndAtmFile(iRequest, String.valueOf(dt.getChanceAttachmentId()), String.valueOf(hpa.getChanceAttachmentId()), "CHANCE_ATT");
        }

        //授信方案
        List<HlsCreditPlan> hlsCreditPlanList = new ArrayList<>();
        HlsCreditPlan hlsCreditPlan = new HlsCreditPlan();
        hlsCreditPlan.setSourceDocumentId(projectIdNew);
        hlsCreditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
        hlsCreditPlanList = hlsCreditPlanService.select(iRequest, hlsCreditPlan, 1, 100000);
        for (HlsCreditPlan dt : hlsCreditPlanList) {
            HlsCreditPlan hlsCreditPlan1 = new HlsCreditPlan();
            Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(dt);
            hlsBeanRefUtilService.setFieldValue(hlsCreditPlan1, map);
            hlsCreditPlan1.setSourceDocumentId(projectIdOld);
            hlsCreditPlan1.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
            hlsCreditPlan1 = hlsCreditPlanService.insertSelective(iRequest, hlsCreditPlan1);
        }
    }

    @Override
    public void deleteRelaProject(IRequest requestCtx, List<HlsCusHlsCreditLineChanceBp> bpList) {
        for (HlsCusHlsCreditLineChanceBp hlsCusHlsCreditLineChanceBp : bpList) {
            if(hlsCusHlsCreditLineChanceBp.getCreditProjectId() == null){
                continue;
            }
            HlsCusPrjProject record = new HlsCusPrjProject();
            record.setProjectId(hlsCusHlsCreditLineChanceBp.getCreditProjectId());
            hlsCusPrjProjectMapper.deleteByPrimaryKey(record);
        }
    }


    //复制系统附件表
    void copyFndAtmFile(IRequest iRequest, String oldPkValue, String newPkValue, String tableName) throws com.hand.hls.exception.HlsCusException {

        if (StringUtils.isEmpty(oldPkValue) || StringUtils.isEmpty(tableName) || StringUtils.isEmpty(newPkValue)) {
            throw new com.hand.hls.exception.HlsCusException("数据异常，请联系管理员!");
        }

        //复制fnd_atm_attachment_multi
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTableName(tableName);
        fndAttachmentMulti.setTablePkValue(oldPkValue);

        List<FndAttachmentMulti> multiList = fndAttachmentMultiMapper.select(fndAttachmentMulti);
        if (CollectionUtils.isNotEmpty(multiList)) {
            for (FndAttachmentMulti multi : multiList) {

                FndAttachmentMulti attachmentMulti = new FndAttachmentMulti();
                Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(multi);

                hlsBeanRefUtilService.setFieldValue(attachmentMulti, map);
                attachmentMulti.setTablePkValue(newPkValue);
                iFndAttachmentMultiService.insertSelective(iRequest, attachmentMulti);

                //复制fnd_atm_attachment
                FndAttachment fndAttachment = new FndAttachment();
                fndAttachment.setAttachmentId(multi.getAttachmentId());
                fndAttachment = fndAttachmentMapper.selectByPrimaryKey(fndAttachment);

                fndAttachment.setAttachmentId(attachmentMulti.getAttachmentId());
                iFndAttachmentService.insert(iRequest, fndAttachment);

                attachmentMulti.setAttachmentId(fndAttachment.getAttachmentId());
                iFndAttachmentMultiService.updateByPrimaryKeySelective(iRequest, attachmentMulti);
            }
        }

    }

    @Override
    public void checkCooperativeOrganization(IRequest requestCtx, HlsCusHlsCreditLineChance chance) throws HlsCusException{
        //获取这个合作机构所有启用的立项
        List<HlsCusHlsCreditLineChance> lists = hlsCusHlsCreditLineChanceMapper.queryChanceIdByCooperativeOrganization(chance);
        for (HlsCusHlsCreditLineChance item : lists) {
            HlsCreditPlan creditPlan = new HlsCreditPlan();
            creditPlan.setSourceDocumentId(item.getChanceId());
            creditPlan.setSourceDocumentCategory("HLS_CREDIT_LINE_CHANCE");
            creditPlan = hlsCreditPlanService.select(requestCtx,creditPlan,1,999).get(0);
            if((chance.getChanceId() == null) || (chance.getChanceId() != null && !chance.getChanceId().equals(item.getChanceId()))){
                if(!(chance.getValidTo().before(creditPlan.getDateFrom()) ||chance.getValidFrom().after(creditPlan.getDateTo()))){
                    throw new HlsCusException("在同一个授信期限范围内，一个合作方有且只能有一条有效的立项信息！");
                }
            }
        }
    }

    @Override
    public List<HlsCusHlsCreditLineChance> createInfo(Long userId) {
        HlsCusHlsCreditLineChance hlsCusHlsCreditLineChance = new HlsCusHlsCreditLineChance();
        hlsCusHlsCreditLineChance.setProposerUserId(userId);
        return hlsCusHlsCreditLineChanceMapper.createInfo(hlsCusHlsCreditLineChance);
    }
    public static final String WORK_FLOW = "FACTORING_PROJRCT_PROPOSAL";
    public static final String DEMO_NAME = "FACTORING_PROJRCT_PROPOSAL";
    public static final String DOCUMENT_TYPE = "FACTORING";
    public static final String DOCUMENT_NAME = "保理立项工作流";
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Override
    public List<HlsCusHlsCreditLineChance> submit(HlsCusHlsCreditLineChance dto,IRequest requestCtx) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("workFlowType", WORK_FLOW);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, WORK_FLOW);
        params.put(IActivitiCommonService.DEMO_NAME, DEMO_NAME);
        params.put(IActivitiCommonService.BUSINESS_KEY, dto.getChanceId());
        params.put("chanceId", dto.getChanceId());
        dto = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(dto);
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(dto.getBpId());
        hlsCusBpMaster =  hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        //单据类别
        params.put("documentCategory",DOCUMENT_CATEGORY);
        //单据类型
        params.put("documentType", DOCUMENT_TYPE);
        //单据名称
        params.put("documentName", dto.getCreditLineName()+"-"+hlsCusBpMaster.getBpName()+"-"+DOCUMENT_NAME);
        //单据编号
        params.put("documentNumber", dto.getCreditLineName());
        //是否授信
        params.put("credit_flag", dto.getCreditFlag());
        //保存业务准入信息
        saveBusinessInfo(dto.getChanceId());
        //查询
        List<HlsCusHlsCreditLineChance> res = new ArrayList<>();
        res.add(dto);
        activitiStartService.start(requestCtx, res, params);
        return  res;
    }

    private void saveBusinessInfo( Long chanceId) {
        HlsChanceBusinessAccessCompare hlsChanceBusinessAccessCompare = new HlsChanceBusinessAccessCompare();
        hlsChanceBusinessAccessCompare.setChanceId(chanceId);
        List<HlsChanceBusinessAccessCompare> factoringInfo = chanceCompareMapper.findFactoringInfo(hlsChanceBusinessAccessCompare);
        if (!CollectionUtils.isEmpty(factoringInfo)){
            return;
        }
        List<HlsChanceBusinessAccessCompare> compareList = chanceCompareMapper.findFactoringBusinessCompare();
        compareList.forEach(v->{
            HlsChanceBusinessAccessCompare compare = new HlsChanceBusinessAccessCompare();
            BeanUtils.copyProperties(v,compare);
            compare.setDocumentId(chanceId);
            compare.setChanceCompareId(null);
            chanceCompareMapper.insertSelective(compare);
        });

    }

    public static final String CREDIT_WORK_FLOW = "CREDIT_CHANCE_CREATE_WFL";
    public static final String CREDIT_DEMO_NAME = "CREDIT_CHANCE_CREATE_WFL";
    public static final String CREDIT_DOCUMENT_TYPE = "CREDIT";
    public static final String CREDIT_DOCUMENT_CATEGORY = "HLS_CREDIT_LINE_CHANCE";
    public static final String CREDIT_DOCUMENT_NAME = "授信立项工作流";
    @Override
    public List<HlsCusHlsCreditLineChance> submitCredit(HlsCusHlsCreditLineChance dto,IRequest requestCtx) throws HlsCusException {
        if ("APPROVING".equalsIgnoreCase(dto.getCreditLineStatus()) || "APPROVED".equalsIgnoreCase(dto.getCreditLineStatus())
                || "CLOSED".equalsIgnoreCase(dto.getCreditLineStatus())) {

           throw new HlsCusException("当前单据状态不能提交申请");

        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("workFlowType", CREDIT_WORK_FLOW);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, CREDIT_WORK_FLOW);
        params.put(IActivitiCommonService.DEMO_NAME, CREDIT_DEMO_NAME);
        params.put(IActivitiCommonService.BUSINESS_KEY, dto.getChanceId());
        params.put("chanceId", dto.getChanceId());
        dto = hlsCusHlsCreditLineChanceMapper.selectByPrimaryKey(dto);
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(dto.getBpId());
        hlsCusBpMaster =  hlsCusBpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);
        //单据类别
        params.put("documentCategory",CREDIT_DOCUMENT_CATEGORY);
        //单据类型
        params.put("documentType", CREDIT_DOCUMENT_TYPE);
        //单据名称
        params.put("documentName", dto.getCreditLineName()+"-"+hlsCusBpMaster.getBpName()+"-"+CREDIT_DOCUMENT_NAME);
        //单据编号
        params.put("documentNumber", dto.getCreditLineName());
        //是否授信
        params.put("creditFlag", dto.getCreditFlag());
        //查询
        List<HlsCusHlsCreditLineChance> res = new ArrayList<>();
        res.add(dto);
        activitiStartService.start(requestCtx, res, params);
        dto.setCreditLineStatus("APPROVING");
        hlsCusHlsCreditLineChanceMapper.updateByPrimaryKeySelective(dto);
        return  res;
    }

    @Override
    public void transfer(IRequest requestCtx,List<HlsCusHlsCreditLineChance> chanceList, String flag,Long userId) {
        if ("HOST".equals(flag)){
            chanceList.forEach(v-> v.setProposerEmployeeId(userId));
        }else if ("SLAVE".equals(flag)){
            chanceList.forEach(v-> v.setProjectAssistant(userId));
        }
        if (!CollectionUtils.isEmpty(chanceList)){
            chanceList.forEach(v->{
                hlsCusHlsCreditLineChanceMapper.updateByPrimaryKeySelective(v);
            });
        }

    }

}
