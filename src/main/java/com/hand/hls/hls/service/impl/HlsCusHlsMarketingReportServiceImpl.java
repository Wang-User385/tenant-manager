package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.cont.dto.DocFileTempletRule;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.fnd.dto.FndCompany;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsCusEmployeeMapper;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.hls.dto.*;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportBpMapper;
import com.hand.hls.hls.mapper.HlsCusHlsMarketingReportMapper;
import com.hand.hls.hls.mapper.HlsMarketingReportBpMapper;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.hls.service.HlsCusHlsReportAttachmentService;
import com.hand.hls.hls.service.HlsMarketingReportDocxService;
import com.hand.hls.cont.service.IDocFileTempletRuleService;
import com.hand.hls.ruleengine.dto.RuleEngineType;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.ruleengine.service.IRuleEngineTypeService;
import com.hand.hls.sys.mapper.FndCompanyMapper;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;
import leaf.utils.ConfigUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusHlsMarketingReportServiceImpl extends BaseServiceImpl<HlsCusHlsMarketingReport> implements HlsCusHlsMarketingReportService {
    public static final String TENANT = "TENANT";
    public static final String ACCOUNTS_RECEIVABLE = "ACCOUNTS_RECEIVABLE";
    public static final String ASSET_TRANSFEROR = "ASSET_TRANSFEROR";
    @Autowired
    HlsCusHlsMarketingReportMapper hlsCusHlsMarketingReportMapper;
    @Autowired
    HlsCusHlsMarketingReportBpMapper hlsCusHlsMarketingReportBpMapper;
    @Autowired
    HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    FndCompanyMapper fndCompanyMapper;

    /**
     * 工作流状态 新建
     */
    private static final String NEW = "NEW";
    /**
     * 工作流状态 驳回
     */
    private static final String REJECTED = "REJECTED";

    /**
     * 工作流状态 审批中
     */
    private static final String APPROVING = "APPROVING";


    /**
     * 工作流状态 审批通过
     */
    private static final String APPROVED = "APPROVED";
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusEmployeeMapper employeeMapper;

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
    private HlsCusHlsReportAttachmentService reportAttachmentService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
    private static final String MARKETING_REPORT_ID = "marketingReportId";
    private static final String BP_ID = "marketingAttachmentId";
    private static final String TEMPLET_ID = "templetId";
    private static final String MARKETING_ATTACHMENT_ID = "marketingAttachmentId";
    @Autowired
    private HlsMarketingReportDocxService reportDocxService;
    //合同文本生成end

    @Override
    public List<HlsCusHlsMarketingReport> selectDetail(IRequest request, HlsCusHlsMarketingReport hlsCusHlsMarketingReport, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return this.hlsCusHlsMarketingReportMapper.queryDetail(hlsCusHlsMarketingReport);
    }

    @Override
    public ResponseData queryMarketingReport(CompositeMap var1, String var2) {
        List answer;
        if (ConfigUtils.isMySQL()) {
            answer = this.hlsCusHlsMarketingReportMapper.queryMarketingReportId(var1, var2);
        } else {
            answer = this.hlsCusHlsMarketingReportMapper.queryMarketingReportId(var1, var2);
        }
        return new ResponseData(answer);
    }

    public void dateCheck(HlsCusHlsMarketingReport hlsCusHlsMarketingReport) throws ResMessageException {
        /**
         * 新建时校验
         */
        if (HlsCusCheckNull.isNull(hlsCusHlsMarketingReport.getMarketingReportId())) {
            HlsCusHlsMarketingReport hmr = new HlsCusHlsMarketingReport();
            hmr.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
            List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReportList = hlsCusHlsMarketingReportMapper.select(hmr);
            if (!hlsCusHlsMarketingReportList.isEmpty()) {
                Long count = hlsCusHlsMarketingReportList.stream().filter(item -> NEW.equals(item.getStatus()) || APPROVING.equals(item.getStatus())).count();
                if (count > 0) {
                    throw new ResMessageException("已经创建了申请,无需重复创建!");
                }
            }
        }
        /**
         * 提交时校验
         */
        else {
            HlsCusHlsMarketingReport hmr = new HlsCusHlsMarketingReport();
            hmr.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
            HlsCusHlsMarketingReport hlsMarketingReport = hlsCusHlsMarketingReportMapper.selectByPrimaryKey(hmr);
            if (APPROVED.equals(hlsMarketingReport.getStatus()) || APPROVING.equals(hlsMarketingReport.getStatus())) {
                throw new ResMessageException("已经提交了申请,无需重复提交!");
            }
            HlsCusHlsMarketingReportBp hmrb = new HlsCusHlsMarketingReportBp();
            hmrb.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
            List<HlsCusHlsMarketingReportBp> hlsCusHlsMarketingReportBps = hlsCusHlsMarketingReportBpMapper.select(hmrb);
            int k = 0;
            for (int i = 0; i < hlsCusHlsMarketingReportBps.size(); i++) {
                HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
                hlsCusBpMaster.setBpId(hlsCusHlsMarketingReportBps.get(i).getBpId());
                hlsCusBpMaster.setBpType(hlsCusHlsMarketingReportBps.get(i).getBpType());
                List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.queryForMarketing(hlsCusBpMaster);
//                for (int j = 0; j < hlsCusBpMasters.size(); j++) {
//
//
//                    if(null==hlsCusBpMasters.get(j).getCountryN()){
//                        throw new ResMessageException("请维护客户信息中的国家信息!");
//                    }
//                    if(null==hlsCusBpMasters.get(j).getProvinceN()){
//                        throw new ResMessageException("请维护客户信息中的省份信息!");
//                    }
//                    if(null==hlsCusBpMasters.get(j).getCityN()){
//                        throw new ResMessageException("请维护客户信息中的城市信息!");
//                    }
//                    if(null==hlsCusBpMasters.get(j).getDistrictN()){
//                        throw new ResMessageException("请维护客户信息中的区/县信息!");
//                    }
//                    if("ORG".equals(hlsCusBpMasters.get(j).getBpClass())){
//                        if(null==hlsCusBpMasters.get(j).getListedCompanyN()){
//                            throw new ResMessageException("请维护客户信息中的是否上市公司信息!");
//                        }
//                    }
//                }
                if (TENANT.equals(hlsCusHlsMarketingReportBps.get(i).getBpType()) || ACCOUNTS_RECEIVABLE.equals(hlsCusHlsMarketingReportBps.get(i).getBpType()) || ASSET_TRANSFEROR.equals(hlsCusHlsMarketingReportBps.get(i).getBpType())) {
                    k++;
                }
            }
            if (k != 1) {
                throw new ResMessageException("承租人或应收账款债权人或资产转让方数量不为1，无法提交!");
            }
        }
    }

    private void approveWfl(IRequest iRequest, HlsCusHlsMarketingReport hlsCusHlsMarketingReport) throws ResMessageException {

        List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReports = hlsCusHlsMarketingReportMapper.queryMarketingReportDetail(hlsCusHlsMarketingReport);
        hlsCusHlsMarketingReport = hlsCusHlsMarketingReports.get(0);
        if (APPROVED.equals(hlsCusHlsMarketingReport.getStatus()) || APPROVING.equals(hlsCusHlsMarketingReport.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        databaseLockProvider.lock(hlsCusHlsMarketingReport);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new ResMessageException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        List<HlsCusHlsMarketingReport> cs = new ArrayList<>();
        cs.add(hlsCusHlsMarketingReport);
        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", "PRJ_MARKETING_REPORT_WFL");
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "PRJ_MARKETING_REPORT_WFL");
        params.put(IActivitiCommonService.DEMO_NAME, "PRJ_MARKETING_REPORT");
        params.put(IActivitiCommonService.BUSINESS_KEY, hlsCusHlsMarketingReport.getMarketingReportId());
        params.put("documentCategory", "HLS_MARKETING");
        params.put("hlsCusInvFinancePurchase", JSON.toJSONString(hlsCusHlsMarketingReport));
        params.put("documentName", hlsCusHlsMarketingReport.getMarketingReportNumber()+"项目方案");
        params.put("documentNumber", hlsCusHlsMarketingReport.getMarketingReportNumber());
        params.put("startUserName", iRequest.getUserName());

        params.put("unitId", hlsCusHlsMarketingReport.getUnitId());
        params.put("companyId", iRequest.getCompanyId());
        params.put("assistUnitId", hlsCusHlsMarketingReport.getAssistUnitId());
        activitiStartService.start(iRequest, cs, params);

        HlsCusHlsMarketingReport hlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsMarketingReport.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
        hlsMarketingReport.setStatus(APPROVING);
        this.updateByPrimaryKeySelective(iRequest, hlsMarketingReport);

    }

    @Override
    public List<HlsCusHlsMarketingReport> marketingReportSubmit(IRequest iRequest, HlsCusHlsMarketingReport hlsCusHlsMarketingReport) throws ResMessageException, ParameterNullException {
        //状态检查
        dateCheck(hlsCusHlsMarketingReport);
        //启动工作流
        approveWfl(iRequest, hlsCusHlsMarketingReport);

        List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReports = new ArrayList<>();
        hlsCusHlsMarketingReports.add(hlsCusHlsMarketingReport);
        return hlsCusHlsMarketingReports;
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
        String authorityString = '"' + company.getCompanyCode() + '"' + "." + '"' + unitCode + '"' + "." + '"' +  positionCode + '"' + "." + '"' + '"' + "." + '"' + '"' + "." + '"'  + positionCode + '"' + "." + '"'+  empCode + '"';
        return authorityString;
    }

    @Override
    public List<HlsCusHlsMarketingReport> selectMarketingHomeQuery(IRequest iRequest,HlsCusHlsMarketingReport hlsCusHlsMarketingReport,int page,int pageSize){
        PageHelper.startPage(page,pageSize);
        return hlsCusHlsMarketingReportMapper.queryMarketingReport(hlsCusHlsMarketingReport);
    }

    //项目方案合同文本生成入口
    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<FndAttachment> reportCreateDocx(IRequest iRequest, HlsCusHlsMarketingReport hlsCusHlsMarketingReport) throws Exception, ResMessageException, ParameterNullException  {
        return self().reportCreateDocx(iRequest, hlsCusHlsMarketingReport.getMarketingReportId(), "HLS_MARKETING_REPORT");
    }
    //合同文本生成过程
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachment> reportCreateDocx(IRequest iRequest, Long marketingReprotId, String templateType) throws Exception, ResMessageException, ParameterNullException  {
        List<FndAttachment> list = new ArrayList<>();

        if (marketingReprotId == null) {
            throw new ResMessageException("未找到项目方案，请保存后在生成!");
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
            pMap.put("marketingReprotId", marketingReprotId);

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
        if(templetIds.length == 0){
//            throw new ResMessageException("未找到项目尽调模板，请核查后再生成!");
            throw new ResMessageException("未找到项目尽调模板，请核查后再生成!");
        }
        HlsDocFileTemplet hlsDocFileTemplet = null;

        //每次生成合同文本区删除原来的合同文本记录
        HlsCusHlsReportAttachment hlsCusHlsReportAttachment = new HlsCusHlsReportAttachment();

        hlsCusHlsReportAttachment.setMarketingReportId(marketingReprotId);
        hlsCusHlsReportAttachment.setAttachmentCategory(REPORT_DOCX);
        List<HlsCusHlsReportAttachment> atmLists = reportAttachmentService.selectSelective(iRequest, hlsCusHlsReportAttachment);

        atmLists.forEach(item -> {
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTableName(REPORT_DOCX);
            fndAttachmentMulti.setTablePkValue(item.getMarketingAttachmentId().toString());
            List<FndAttachmentMulti> multis = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);
            //删除已有附件
            if (multis != null && multis.size() > 0) {
                fndAttachmentMulti = multis.get(0);
                String sourceTypeCode = fndAttachmentMulti.getRecordId().toString();
                fndAttachmentService.deleteAtmMultiByTypeCodeAndPkValue(REPORT_DOCX, item.getMarketingAttachmentId().toString());
                fndAttachmentService.deleteByTypeCodeAndPkValue(FND_ATM_ATTACHMENT_MULTI, sourceTypeCode);
            }
            reportAttachmentService.deleteByPrimaryKey(item);
        });

        HlsCusHlsReportAttachment ppa = null;
        Map<String, Object> params = null;

        HlsCusHlsMarketingReportBp hlsMarketingReportBp = new HlsCusHlsMarketingReportBp();
        hlsMarketingReportBp.setMarketingReportId(marketingReprotId);
        List<HlsCusHlsMarketingReportBp> reportBps
                = hlsCusHlsMarketingReportBpMapper.select(hlsMarketingReportBp);

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
                    HlsCusHlsMarketingReport hmr = new HlsCusHlsMarketingReport();
                    hmr.setMarketingReportId(marketingReprotId);
                    hmr = this.selectByPrimaryKey(iRequest, hmr);
                    StringBuilder attachmentName = new StringBuilder();
                    attachmentName.append(hmr.getMarketingReportNumber()).append("-").append(hlsDocFileTemplet.getTempletName());
                    ppa = new HlsCusHlsReportAttachment();
                    ppa.setMarketingReportId(marketingReprotId);
                    ppa.setAttachmentCategory(REPORT_DOCX);
                    ppa.setDocumentName(attachmentName.toString());
                    ppa.setDescription(CONTRACT_DOCX_DESCRIPTION);
                    ppa.setSourceId(hlsDocFileTemplet.getTempletId());
                    ppa.setUploadPerson(iRequest.getUserId().toString());
                    ppa.setUploadDate(new Date());
//                    ppa.setBpCategory(reportBp.getBpCategory());
//                    ppa.setBpId(reportBp.getBpId());
                    ppa = reportAttachmentService.insertSelective(iRequest, ppa);

                    //取对应参数生成合同文本文件
                    params = new HashMap<String, Object>();
                    params.put(MARKETING_REPORT_ID, marketingReprotId);
                    params.put(TEMPLET_ID, hlsDocFileTemplet.getTempletId());
//                    params.put(BP_ID, ppa.getBpId());
                    params.put(MARKETING_ATTACHMENT_ID, ppa.getMarketingAttachmentId());
                    params.put(TABLE_NAME, REPORT_DOCX);
                    list.addAll(reportDocxService.process(iRequest, params, jsonObject));
//                }
            }
//        }
        return list;

    }
}