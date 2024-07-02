package com.hand.hls.hls.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.hls.dto.*;
import com.hand.hls.hls.mapper.*;
import com.hand.hls.hls.service.HlsCusHlsMarketingReportService;
import com.hand.hls.office.mapper.FndAtmAttachmentMapper;
import com.hand.hls.office.mapper.FndAtmAttachmentMultiMapper;
import com.hand.hls.sys.dto.SysDocumentHistory;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.sys.utils.SysDocumentHistoryUtils;
import com.hand.hls.utils.HlsCusCheckNull;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.bean.LeafRequestData;
import leaf.service.validation.ParameterNullException;

import org.activiti.editor.language.json.converter.util.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hand.hls.hls.service.IMarketingReportChangeService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.rmi.NoSuchObjectException;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class MarketingReportChangeServiceImpl extends BaseServiceImpl<MarketingReportChange> implements IMarketingReportChangeService{
    /**
     * 单据类型
     */
    public static final String DOCUMENT_TYPE = "MARKETING_REPORT_CHANGE";
    /**
     * 单据类别
     */
    private static final String DOCUMENT_CATEGORY = "MARKETING_REPORT_CHANGE";

    /**
     * 业务类型
     */
    private static final String BUSINESS_TYPE = "MARKETING_REPORT_CHANGE";
    /**
     * 工作流状态 新建
     */
    private static final String NEW = "NEW";
    private static final String REJECTED = "REJECTED";


    /**
     * 工作流状态 审批中
     */
    private static final String APPROVING = "APPROVING";


    /**
     * 工作流状态 审批通过
     */
    private static final String APPROVED = "APPROVED";
    public static final String TENANT = "TENANT";
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private HlsCusHlsMarketingReportBpMapper hlsCusHlsMarketingReportBpMapper;
    @Autowired
    private HlsCusHlsReportAttachmentMapper hlsCusHlsReportAttachmentMapper;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private MarketingReportChangeMapper marketingReportChangeMapper;
    @Autowired
    private HlsCusHlsMarketingReportMapper hlsCusHlsMarketingReportMapper;
    @Autowired
    private HlsCusHlsMarketingReportService hlsCusHlsMarketingReportService;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private MarketingReportChannelMapper marketingReportChannelMapper;

    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private SysDocumentHistoryMapper historyMapper;

    @Autowired
    private SysDocumentHistoryDetailMapper documentHistoryDetailMapper;
    /**
     * @Title: contractChangeInit
     * @Discription: 变更表信息初始化
     * @Param: [iRequest, contractChangeReq]
     * @Return: void
     */
    public void marketingReportChangeInit(IRequest iRequest, MarketingReportChange marketingReportChange) {
        marketingReportChange.setDocumentType(DOCUMENT_TYPE);
        marketingReportChange.setDocumentCategory(DOCUMENT_CATEGORY);
        marketingReportChange.setBusinessType(BUSINESS_TYPE);
        marketingReportChange.setStatus(NEW);
        marketingReportChange.setChangeReqUserId(iRequest.getUserId());
        Date date = new Date();
        marketingReportChange.setChangeReqDate(date);
        if (StringUtils.isEmpty(marketingReportChange.getChangeReqNumber())) {
            Map<String, String> params = new HashMap<>();
            marketingReportChange.setChangeReqNumber(fndCodingRuleValuesService.getCodeRuleValue(iRequest,
                    marketingReportChange.getDocumentCategory(),
                    marketingReportChange.getDocumentType(),
                    marketingReportChange.getBusinessType(),
                    params));
        }

    }
    private List<Map<String, Object>> getMarketingReportBasic(Long marketingReportId) throws ParameterNullException {
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(marketingReportId);
        List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReports = hlsCusHlsMarketingReportMapper.queryMarketingReportDetail(hlsCusHlsMarketingReport);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_marketing_report", "marketing_report_id", hlsCusHlsMarketingReports);
        } catch (ResMessageException e) {
            logger.warn("getBp: {}", e.getMessage());
        }
        return list;
    }
    private List<Map<String, Object>> getBp(Long marketingReportId) throws ParameterNullException {
        HlsCusHlsMarketingReportBp hlsCusHlsMarketingReportBp = new HlsCusHlsMarketingReportBp();
        hlsCusHlsMarketingReportBp.setMarketingReportId(marketingReportId);
        List<HlsCusHlsMarketingReportBp> hlsCusHlsMarketingReportBps = hlsCusHlsMarketingReportBpMapper.queryMarketingReportBp(hlsCusHlsMarketingReportBp);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_marketing_report_bp", "marketing_report_bp_id","hls_marketing_report",marketingReportId.toString(), hlsCusHlsMarketingReportBps);
        } catch (ResMessageException e) {
            logger.warn("getBp: {}", e.getMessage());
        }
        return list;
    }
    private List<Map<String, Object>> getChannel(Long marketingReportId) throws ParameterNullException {
        MarketingReportChannel marketingReportChannel = new MarketingReportChannel();
        marketingReportChannel.setMarketingReportId(marketingReportId);
        List<MarketingReportChannel> marketingReportChannels = marketingReportChannelMapper.queryReportChannel(marketingReportChannel);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_marketing_report_channel", "marketing_channel_id","hls_marketing_report",marketingReportId.toString(), marketingReportChannels);
        } catch (ResMessageException e) {
            logger.warn("getChannel: {}", e.getMessage());
        }
        return list;
    }
    /**
     * @Title: getAttachment
     * @Discription: 附件信息
     * @Param: [contractId]
     * @Return:
     */
    private List<Map<String, Object>> getAttachment(Long marketingReportId) throws ParameterNullException {
        HlsCusHlsReportAttachment hlsCusHlsReportAttachment = new HlsCusHlsReportAttachment();
        hlsCusHlsReportAttachment.setMarketingReportId(marketingReportId);
        List<HlsCusHlsReportAttachment> hlsCusHlsReportAttachments = hlsCusHlsReportAttachmentMapper.queryReportAttachment(hlsCusHlsReportAttachment);
        List<FndAttachmentMulti> fndAttachmentMultis = new ArrayList<>();
        List<FndAttachment> fndAttachments = new ArrayList<>();
        //fnd_attachment_multi fnd_attachment
        for (HlsCusHlsReportAttachment reportAttachment : hlsCusHlsReportAttachments) {
            Long marketingAttachmentId = reportAttachment.getMarketingAttachmentId();
            String tableName = "hls_report_attachment";
            FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
            fndAttachmentMulti.setTablePkValue(marketingAttachmentId.toString());
            fndAttachmentMulti.setTableName(tableName);
            fndAttachmentMultis.addAll(fndAttachmentMultiMapper.select(fndAttachmentMulti));

            for (FndAttachmentMulti attachmentMulti : fndAttachmentMultis) {
                Long attachmentId = attachmentMulti.getAttachmentId();
                if (attachmentId != null) {
                    FndAttachment fndAttachment = new FndAttachment();
                    fndAttachment.setAttachmentId(attachmentId);
                    fndAttachments.addAll(fndAttachmentMapper.select(fndAttachment));
                }
            }

        }
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list.addAll(SysDocumentHistoryUtils.initRecords("hls_report_attachment", "marketing_attachment_id", "hls_marketing_report", marketingReportId.toString(), hlsCusHlsReportAttachments));
            list.addAll(SysDocumentHistoryUtils.initRecords("fnd_atm_attachment_multi", "record_id", fndAttachmentMultis));
            list.addAll(SysDocumentHistoryUtils.initRecords("fnd_atm_attachment", "attachment_id", fndAttachments));
        } catch (ResMessageException e) {
            logger.warn("getLonContractContent {}", e.getMessage());
        }

        return list;
    }

    /**
     * @Title: getDatas
     * @Discription: 组装需要备份的数据
     * @Param: [changeReqId, contractId]
     * @Return: java.util.List<java.util.Map                                                               <                                                               java.lang.String                                                               ,                                                               java.lang.Object>>
     */
    public List<Map<String, Object>> getDatas(Long changeReqId, Long marketingReportId) throws ParameterNullException, ResMessageException {
        //开始数据组装
        List<Map<String, Object>> datas = new ArrayList<>();
        //营销基本信息
        datas.addAll(getMarketingReportBasic(marketingReportId));
        datas.addAll(getChannel(marketingReportId));
        //客户信息
        datas.addAll(getBp(marketingReportId));
        //附件信息
        datas.addAll(getAttachment(marketingReportId));

        return datas;
    }
    /**
     * @Title: saveHistory
     * @Discription: 保存历史数据
     * @Param: [changeReqId, contractId]
     * @Return: void
     */
    private void saveHistory(Long changeReqId, Long marketingReportId) throws ParameterNullException, ResMessageException {
        List<Map<String, Object>> datas = getDatas(changeReqId, marketingReportId);
        sysDocumentHistoryService.createHistory(DOCUMENT_CATEGORY, changeReqId, datas);


    }
    public void dateCheck(IRequest iRequest, MarketingReportChange marketingReportChange) throws ResMessageException, NoSuchObjectException {
        /**
         * 新建时校验
         */
        if (HlsCusCheckNull.isNull(marketingReportChange.getChangeReqId())) {
            MarketingReportChange req = new MarketingReportChange();
            req.setMarketingReportId(marketingReportChange.getMarketingReportId());
            req.setDocumentCategory(DOCUMENT_CATEGORY);
            List<MarketingReportChange> marketingReportChangeList = marketingReportChangeMapper.select(req);
            if (!marketingReportChangeList.isEmpty()) {
                Long count = marketingReportChangeList.stream().filter(item -> NEW.equals(item.getStatus()) || APPROVING.equals(item.getStatus())).count();
                if (count > 0) {
                    throw new ResMessageException("已经创建了变更申请,无需重复创建!");
                }
            }
        }
        /**
         * 提交时校验
         */
        else {
            marketingReportChange = marketingReportChangeMapper.selectByPrimaryKey(marketingReportChange);
            if (APPROVED.equals(marketingReportChange.getStatus()) || APPROVING.equals(marketingReportChange.getStatus())) {
                throw new ResMessageException("已经提交了变更申请,无需重复提交!");
            }


            List<HlsCusHlsMarketingReportBp> hlsCusHlsMarketingReportBps = new ArrayList<>();


            SysDocumentHistory history = new SysDocumentHistory();
            history.setDocumentCategory("MARKETING_REPORT_CHANGE");
            history.setDocumentId(marketingReportChange.getChangeReqId());
            SysDocumentHistory sysDocumentHistory = historyMapper.selectMaxVersion(history);
            Long historyId;
            if (sysDocumentHistory == null || sysDocumentHistory.getHistoryId() == null) {
                historyId = null;
            }
            historyId = sysDocumentHistory.getHistoryId();
            SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
            detail.setHistoryId(historyId);
            detail.setTableName("hls_marketing_report_bp");
            List<SysDocumentHistoryDetail> details = documentHistoryDetailMapper.select(detail);
            if (details.size() > 0) {
                for (SysDocumentHistoryDetail sysDocumentHistoryDetail : details) {
                    JSONObject historyDate = JSON.parseObject(sysDocumentHistoryDetail.getHistoryData());
                    HlsCusHlsMarketingReportBp hlsCusHlsMarketingReportBp = new HlsCusHlsMarketingReportBp();
                    hlsCusHlsMarketingReportBp.setBpId(Long.valueOf(String.valueOf(historyDate.get("bp_id"))));
                    hlsCusHlsMarketingReportBp.setBpType(String.valueOf(historyDate.get("bp_type")));
                    hlsCusHlsMarketingReportBps.add(hlsCusHlsMarketingReportBp);
                }
            }
            int k = 0;
            for (int i = 0; i < hlsCusHlsMarketingReportBps.size(); i++) {
                HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
                hlsCusBpMaster.setBpId(hlsCusHlsMarketingReportBps.get(i).getBpId());
                hlsCusBpMaster.setBpType(hlsCusHlsMarketingReportBps.get(i).getBpType());
                List<HlsCusBpMaster> hlsCusBpMasters = hlsCusBpMasterMapper.queryForMarketing(hlsCusBpMaster);
                for (int j = 0; j < hlsCusBpMasters.size(); j++) {


                    if (null == hlsCusBpMasters.get(j).getCountryN()) {
                        throw new ResMessageException("请维护客户信息中的国家信息!");
                    }
                    if (null == hlsCusBpMasters.get(j).getProvinceN()) {
                        throw new ResMessageException("请维护客户信息中的省份信息!");
                    }
                    if (null == hlsCusBpMasters.get(j).getCityN()) {
                        throw new ResMessageException("请维护客户信息中的城市信息!");
                    }
                    if (null == hlsCusBpMasters.get(j).getDistrictN()) {
                        throw new ResMessageException("请维护客户信息中的区/县信息!");
                    }
                    if("ORG".equals(hlsCusBpMasters.get(j).getBpClass())){
                        if(null==hlsCusBpMasters.get(j).getListedCompanyN()){
                            throw new ResMessageException("请维护客户信息中的是否上市公司信息!");
                        }
                    }

                }
                if (TENANT.equals(hlsCusHlsMarketingReportBps.get(i).getBpType())) {
                    k++;
                }
            }
            if (k != 1) {
                throw new ResMessageException("承租人数量不为1，无法提交!");
            }
        }

    }

    /**
     * @Title: changeCreate
     * @Discription:营销报备变更申请创建
     * @Param: [iRequest, contractChangeReq]
     * @Return: com.hand.hls.cont.dto.HlsCusConContractChangeReq
     */
    @Override
    public List<MarketingReportChange> changeCreate(IRequest iRequest, MarketingReportChange marketingReportChange) throws ResMessageException, ParameterNullException, NoSuchObjectException {
        //状态检查
        dateCheck(iRequest, marketingReportChange);
        //创建变更申请
        marketingReportChangeInit(iRequest, marketingReportChange);
        MarketingReportChange marketingReportChangeReq = new MarketingReportChange();
        if (HlsCusCheckNull.isNull(marketingReportChange.getChangeReqId())) {
            marketingReportChangeReq = this.insertSelective(iRequest, marketingReportChange);

            HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
            hlsCusHlsMarketingReport.setMarketingReportId(marketingReportChangeReq.getMarketingReportId());
            hlsCusHlsMarketingReport.setChangeReqId(marketingReportChangeReq.getChangeReqId());
            hlsCusHlsMarketingReport.setStatus("PENDING");
            hlsCusHlsMarketingReportService.updateByPrimaryKeySelective(iRequest, hlsCusHlsMarketingReport);
        } else {
            this.updateByPrimaryKeySelective(iRequest, marketingReportChange);
        }
        //插入版本
        saveHistory(marketingReportChange.getChangeReqId(), marketingReportChange.getMarketingReportId());

        List<MarketingReportChange> marketingReportChangeList = new ArrayList<>();
        marketingReportChangeList.add(marketingReportChangeReq);
        return marketingReportChangeList;
    }

    private void approveWfl(IRequest iRequest, MarketingReportChange marketingReportChange) throws ResMessageException {

        marketingReportChange = marketingReportChangeMapper.selectByPrimaryKey(marketingReportChange);
        if (APPROVED.equals(marketingReportChange.getStatus()) || APPROVING.equals(marketingReportChange.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(marketingReportChange.getMarketingReportId());
        List<HlsCusHlsMarketingReport> hlsCusHlsMarketingReports= hlsCusHlsMarketingReportMapper.queryMarketingReportDetail(hlsCusHlsMarketingReport) ;
        hlsCusHlsMarketingReport = hlsCusHlsMarketingReports.get(0);

        List<MarketingReportChange> cs = new ArrayList<>();
        cs.add(marketingReportChange);

        databaseLockProvider.lock(marketingReportChange);
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        if (ObjectUtils.isEmpty(employee)) {
            throw new ResMessageException("获取提交人失败");
        }
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", DOCUMENT_CATEGORY);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, "MARKETING_REPORT_CHANGE");
        params.put(IActivitiCommonService.DEMO_NAME, "PRJ_MARKETING_REPORT");
        params.put(IActivitiCommonService.BUSINESS_KEY, marketingReportChange.getChangeReqId());
        params.put("documentCategory", DOCUMENT_CATEGORY);
        params.put("documentName", hlsCusHlsMarketingReport.getMarketingReportNumber()+"营销补充报备");
        params.put("documentNumber", hlsCusHlsMarketingReport.getMarketingReportNumber());
        params.put("documentId", marketingReportChange.getChangeReqId());
        params.put("marketingReportId", marketingReportChange.getMarketingReportId());
        params.put("marketingReportChange", JSON.toJSONString(marketingReportChange));
        params.put("hlsCusHlsMarketingReport", JSON.toJSONString(hlsCusHlsMarketingReport));

        params.put("unitId", hlsCusHlsMarketingReport.getUnitId());
        params.put("companyId", iRequest.getCompanyId());
        params.put("assistUnitId", hlsCusHlsMarketingReport.getAssistUnitId());


        activitiStartService.start(iRequest, cs, params);
        marketingReportChange.setStatus(APPROVING);
        this.updateByPrimaryKeySelective(iRequest, marketingReportChange);

        HlsCusHlsMarketingReport hlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsMarketingReport.setMarketingReportId(hlsCusHlsMarketingReport.getMarketingReportId());
        hlsMarketingReport.setStatus("CHANGE_APPROVING");
        hlsCusHlsMarketingReportService.updateByPrimaryKeySelective(iRequest,hlsMarketingReport);


    }
    @Override
    public List<MarketingReportChange> changeSubmit(IRequest iRequest, MarketingReportChange marketingReportChange) throws ResMessageException, ParameterNullException, NoSuchObjectException {

        dateCheck(iRequest, marketingReportChange);

        //启动工作流
        approveWfl(iRequest, marketingReportChange);

        List<MarketingReportChange> marketingReportChangeList = new ArrayList<>();
        marketingReportChangeList.add(marketingReportChange);
        return marketingReportChangeList;
    }
    @Override
    public void leaveHistory(IRequest iRequest, MarketingReportChange marketingReportChange, Long processInstanceId) throws Exception {
        List<Map<String, Object>> list = getDatas(marketingReportChange.getChangeReqId(), marketingReportChange.getMarketingReportId());
        Long lastVersion = sysDocumentHistoryService.leaveHistoryWithData(DOCUMENT_CATEGORY, marketingReportChange.getChangeReqId(), list);
        sysDocumentHistoryService.leaveHistory(DOCUMENT_CATEGORY, marketingReportChange.getChangeReqId(), lastVersion);
    }

    @Override
    public void cancelChangeReq(IRequest iRequest, Long changeReqId) throws NoSuchObjectException {
        MarketingReportChange changeReq = new MarketingReportChange();
        changeReq.setChangeReqId(changeReqId);
        changeReq = marketingReportChangeMapper.selectByPrimaryKey(changeReq);
        if (!NEW.equals(changeReq.getStatus())|| REJECTED.equals(changeReq.getStatus())) {
            throw new NoSuchObjectException("只有新建或者审批拒绝状态的变更申请才可以取消!");
        }
        MarketingReportChange marketingReportChange = new MarketingReportChange();
        marketingReportChange.setChangeReqId(changeReqId);
        marketingReportChange.setStatus("CANCEL");
        self().updateByPrimaryKeySelective(iRequest, marketingReportChange);
        marketingReportChange = self().selectByPrimaryKey(iRequest, marketingReportChange);

        Long marketingReportId = changeReq.getMarketingReportId();
        HlsCusHlsMarketingReport hlsCusHlsMarketingReport = new HlsCusHlsMarketingReport();
        hlsCusHlsMarketingReport.setMarketingReportId(marketingReportId);
        hlsCusHlsMarketingReport.setStatus(APPROVED);
        hlsCusHlsMarketingReportService.updateByPrimaryKeySelective(iRequest, hlsCusHlsMarketingReport);
    }
    @Override
    public List<HlsCusHlsMarketingReportBp> deleteBpChangeReq(IRequest iRequest, Long marketingReportId, Long marketingReportBpId) throws Exception {
        HlsCusHlsMarketingReportBp hlsCusHlsMarketingReportBp = new HlsCusHlsMarketingReportBp();
        hlsCusHlsMarketingReportBp.setMarketingReportBpId(marketingReportBpId);
        hlsCusHlsMarketingReportBp.setMarketingReportId(marketingReportId);
        List<HlsCusHlsMarketingReportBp> marketingReportBps = hlsCusHlsMarketingReportBpMapper.select(hlsCusHlsMarketingReportBp);
        return marketingReportBps;

    }
    @Override
    public int deleteAttachmentChangeReq(IRequest iRequest, List<HlsCusHlsReportAttachment> marketingAttachmentIds) throws Exception {
        int k = 0;
        for (int i = 0; i < marketingAttachmentIds.size(); i++) {
            HlsCusHlsReportAttachment hlsCusHlsReportAttachment = new HlsCusHlsReportAttachment();
            hlsCusHlsReportAttachment.setMarketingAttachmentId(marketingAttachmentIds.get(i).getMarketingAttachmentId());
            hlsCusHlsReportAttachment.setMarketingReportId(marketingAttachmentIds.get(i).getMarketingReportId());
            List<HlsCusHlsReportAttachment> hlsCusHlsReportAttachments = hlsCusHlsReportAttachmentMapper.select(hlsCusHlsReportAttachment);
            if(hlsCusHlsReportAttachments.size() != 0){
                k++;
            }
        }
        return k;

    }
}