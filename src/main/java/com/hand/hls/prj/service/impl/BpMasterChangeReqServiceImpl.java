package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.account.mapper.UserMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.*;
import com.hand.hls.bp.mapper.*;
import com.hand.hls.fin.dto.BpMasterAttachment;
import com.hand.hls.fin.mapper.BpMasterAttachmentMapper;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsBpMasterRole;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.IBpMasterChangeReqService;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.sys.utils.SysDocumentHistoryUtils;
import com.hand.hls.utils.ResMessageException;
import com.hand.hls.wfl.service.IActivitiCommonService;
import com.hand.hls.wfl.service.IActivitiStartService;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class BpMasterChangeReqServiceImpl extends BaseServiceImpl<BpMasterChangeReq> implements IBpMasterChangeReqService {

    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;

    @Autowired
    private BpMasterChangeReqMapper bpMasterChangeReqMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private HlsCusBpMasterMapper hlsBpMasterMapper;
    @Autowired
    private HlsCusBpMasterAddressMapper hlsBpMasterAddressMapper;
    @Autowired
    private HlsCusBpMasterContactInfoMapper contactInfoMapper;
    @Autowired
    private HlsBpShareholderInfoMapper bpMasterShareholderMapper;
    @Autowired
    private HlsCusBpMasterRoleMapper bpMasterRoleMapper;
    @Autowired
    private HlsCusBpMasterRelationMapper relationMapper;
    @Autowired
    private HlsCusBpMasterBankAccountMapper bankAccountMapper;
    @Autowired
    private ZxBpOrgbaseMapper orgbaseMapper;
    @Autowired
    private ZxBpOrgattrMapper orgattrMapper;
    @Autowired
    private ZxBpOrgstatusMapper orgstatusMapper;
    @Autowired
    private ZxBpOrgaffiliatedMapper orgaffiliatedMapper;
    @Autowired
    private ZxBpOrgparentbodyMapper orgparentbodyMapper;
    @Autowired
    private ZxBpCustlawsuitMapper custlawsuitMapper;
    @Autowired
    private ZxBpCusteventMapper custeventMapper;
    @Autowired
    private HlsCusBpAttachmentMapper attachmentMapper;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private com.hand.hls.prj.mapper.BpMasterChangeReqMapper BpMasterChangeReqMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HlsBpMasterMainMembersMapper hlsBpMasterMainMembersMapper;
    @Autowired
    private HlsSysDocumentHistoryDetailMapper documentHistoryDetailMapper;
    @Autowired
    private HlsSysDocumentHistoryBlobMapper documentHistoryBlobMapper;
    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;
    @Autowired
    private BpMasterAgreementMapper bpMasterAgreementMapper;
    @Autowired
    private BpMasterEmployeeMapper bpMasterEmployeeMapper;
    @Autowired
    private HlsBpMasterRoleMapper hlsBpMasterRoleMapper;
    @Autowired
    private HlsBpMasterInceptRuleMapper hlsBpMasterInceptRuleMapper;
    @Autowired
    private HlsCusBpSeniorPersionMapper hlsCusBpSeniorPersionMapper;
    @Autowired
    private HlsBpAssetsListMapper hlsBpAssetsListMapper;


    public static final String DOCUMENT_CATEGORY_BP_CHANGE = "HLS_BP_MASTER_CHANGE";
    public static final String HLS_BP_MASTER = "HLS_BP_MASTER";
    public static final String APPROVED = "APPROVED";
    public static final String APPROVING = "APPROVING";
    public static final String BP_CHANGE_WORK_FLOW = "BP_CHANGE_WORK_FLOW";
    public static final String VENDER_WORKFLOW_TYPE = "BP_ADMIT_WORK_FLOW";
    public static final String DISTRIBUTOR_WORKFLOW_TYPE = "BP_DISTRU_ADMIT_WORK_FLOW";
    public static final String VENDER_WFL_TYPE = "VENDER_WFL_TYPE";
    public static final String DISTRIBUTOR_WFL_TYPE = "DISTRIBUTOR_WFL_TYPE";
    public static final String CHANGE_WFL_TYPE = "CHANGE_WFL_TYPE";
    public static final String VENDER = "VENDER";
    public static final String DEALER = "DEALER";
    public static final String DISTRIBUTOR = "DISTRIBUTOR";

    private Logger logger = LoggerFactory.getLogger(getClass());


    public String getBpMasterChangeWflType(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws Exception {

        List<String> changeRoleList = new ArrayList<>();
        List<String> bpRoleList = new ArrayList<>();

        Map map = new HashMap<>(1);
        map.put("bpId", bpChangeReq.getBpId());
        List<HlsBpMasterRole> hlsBpMasterRoles = bpMasterRoleMapper.query2(map);
        if (hlsBpMasterRoles.size() > 0) {
            for (HlsBpMasterRole role :
                    hlsBpMasterRoles) {
                bpRoleList.add(role.getBpType());
            }
        }

        List<JSONObject> roleJsonList = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, bpChangeReq.getChangeReqId(), DOCUMENT_CATEGORY_BP_CHANGE, "hls_bp_master_role");
        if (roleJsonList.size() > 0) {
            for (int i = 0; i < roleJsonList.size(); i++) {
                JSONObject bpJson = (JSONObject) roleJsonList.get(i).get("data");
                changeRoleList.add(bpJson.toJavaObject(HlsBpMasterRole.class).getBpType());
            }
        }

        List<String> changeDiffList = new ArrayList<>(changeRoleList);

        //变更角色与正常版本的差异
        changeRoleList.removeAll(bpRoleList);


        if (changeRoleList.size() > 0) {
            //变更角色与上面差异的差异
            changeDiffList.removeAll(changeRoleList);
            if (changeDiffList.size() > 0) {
                if (changeDiffList.contains(VENDER)||changeDiffList.contains(DEALER)) {
                    return CHANGE_WFL_TYPE;
                } else if (changeDiffList.contains(DISTRIBUTOR)) {
                    if (changeRoleList.contains(VENDER)||changeRoleList.contains(DEALER)) {
                        return VENDER_WFL_TYPE;
                    } else {
                        return CHANGE_WFL_TYPE;
                    }
                } else {
                    if (changeRoleList.contains(VENDER)||changeRoleList.contains(DEALER)) {
                        return VENDER_WFL_TYPE;
                    } else if (changeRoleList.contains(DISTRIBUTOR)) {
                        return DISTRIBUTOR_WFL_TYPE;
                    } else {
                        return CHANGE_WFL_TYPE;
                    }
                }
            }
        }


        return CHANGE_WFL_TYPE;

    }


    public Boolean bpChangeValidate(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws Exception {
        Long bpId = bpChangeReq.getBpId();
        if (bpId == null) {
            throw new ResMessageException("未找到商业伙伴变更对应的商业伙伴信息，请核查数据!");
        }
        Long changeNewStatusNum = BpMasterChangeReqMapper.queryChangeNewStatusNum(bpChangeReq).get(0).getChangeNewStatusNum();
        if (changeNewStatusNum > 0) {
            throw new ResMessageException("存在商业伙伴变更数据正在维护中，请核查数据!");
        }
        return true;
    }

    public void approveWfl(IRequest iRequest, BpMasterChangeReq bpChangeReq, Long allocationId, String workflowType) throws Exception {
        bpChangeReq = self().selectByPrimaryKey(iRequest, bpChangeReq);
        if (APPROVED.equals(bpChangeReq.getStatus()) || APPROVING.equals(bpChangeReq.getStatus())) {
            throw new ResMessageException("当前单据状态不能提交申请！");
        }

        HlsBpMaster bpMaster = new HlsBpMaster();
        bpMaster.setBpId(bpChangeReq.getBpId());
        List<JSONObject> bpMasterJsonList = sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, bpChangeReq.getChangeReqId(), DOCUMENT_CATEGORY_BP_CHANGE, "hls_bp_master");
        if (bpMasterJsonList.size() > 0) {
            JSONObject bpJson = (JSONObject) bpMasterJsonList.get(0).get("data");
            bpMaster = bpJson.toJavaObject(HlsBpMaster.class);
        } else {
            throw new ResMessageException("未找到商业伙伴变更历史，请核查数据!");
        }

        List<BpMasterChangeReq> cs = new ArrayList<>();
        cs.add(bpChangeReq);

        if (StringUtils.equals(workflowType, VENDER_WFL_TYPE)) {
            workflowType = VENDER_WORKFLOW_TYPE;
        } else if (StringUtils.equals(workflowType, DISTRIBUTOR_WFL_TYPE)) {
            workflowType = DISTRIBUTOR_WORKFLOW_TYPE;
        } else if (StringUtils.equals(workflowType, CHANGE_WFL_TYPE)) {
            workflowType = BP_CHANGE_WORK_FLOW;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("workFlowType", workflowType);
        params.put(IActivitiCommonService.WORK_FLOW_NAME, workflowType);
        params.put(IActivitiCommonService.DEMO_NAME, "BP");
        params.put(IActivitiCommonService.BUSINESS_KEY, bpChangeReq.getChangeReqId());
        params.put("documentCategory", DOCUMENT_CATEGORY_BP_CHANGE);
        params.put("documentName", bpMaster.getBpName());
        params.put("documentNumber", bpMaster.getBpCode());
        params.put("documentId", bpChangeReq.getChangeReqId());
        params.put("bpId", bpChangeReq.getBpId());
        params.put("bpCode", bpMaster.getBpCode());
        params.put("bpName", bpMaster.getBpName());
        params.put("bpClass", bpMaster.getBpClass());
        params.put("bpChangeReq", JSON.toJSONString(bpChangeReq));
        params.put("hlsBpMaster", JSON.toJSONString(bpMaster));
        params.put("allocationId", allocationId);

        bpChangeReq.setStatus("APPROVING");
        bpChangeReq.setChangeReqUserId(iRequest.getUserId());
        self().updateByPrimaryKeySelective(iRequest, bpChangeReq);

        activitiStartService.start(iRequest, cs, params);


    }

    public void leaveHistory(IRequest iRequest, BpMasterChangeReq changeReq, Long processInstanceId) throws Exception {
        List<Map<String, Object>> list = self().getDatas(changeReq.getBpId());
        Long lastVersion = sysDocumentHistoryService.leaveHistoryWithData(DOCUMENT_CATEGORY_BP_CHANGE, changeReq.getChangeReqId(), list);
        sysDocumentHistoryService.leaveHistory(DOCUMENT_CATEGORY_BP_CHANGE, changeReq.getChangeReqId(), lastVersion);
    }

    public boolean submit(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws ParameterNullException {
        //锁表
        Long bpId = bpChangeReq.getBpId();
        HlsBpMaster bpMaster = new HlsBpMaster();
        bpMaster.setBpId(bpId);
//        databaseLockProvider.lock(bpMaster);

        //保存req
        bpChangeReq.setDocumentVersion(BpMasterChangeReqMapper.queryVersionNum(bpChangeReq).get(0).getVersionNum());
        bpChangeReq.setDocumentCategory(DOCUMENT_CATEGORY_BP_CHANGE);
        bpChangeReq.setDocumentType(HLS_BP_MASTER);
        bpChangeReq.setStatus("NEW");

        bpChangeReq = self().insert(iRequest, bpChangeReq);

        Long bpChangeReqId = bpChangeReq.getChangeReqId();
        self().createHistory(bpChangeReqId, DOCUMENT_CATEGORY_BP_CHANGE, bpId);

        return true;
    }

    /**
     * 经销商同步接口初始化商业伙伴变更信息
     * @param iRequest
     * @param bpChangeReq
     */

    public void initBpChangeInfo(IRequest iRequest, BpMasterChangeReq bpChangeReq) throws ParameterNullException {
        //锁表
        HlsBpMaster bpMaster = new HlsBpMaster();
        bpMaster.setBpId(bpChangeReq.getBpId());
        databaseLockProvider.lock(bpMaster);

        //保存req
        bpChangeReq.setDocumentVersion(BpMasterChangeReqMapper.queryVersionNum(bpChangeReq).get(0).getVersionNum());
        bpChangeReq.setDocumentCategory(DOCUMENT_CATEGORY_BP_CHANGE);
        bpChangeReq.setDocumentType(HLS_BP_MASTER);
        bpChangeReq.setStatus(APPROVED);

        bpChangeReq = self().insert(iRequest, bpChangeReq);

        Long bpChangeReqId = bpChangeReq.getChangeReqId();

        self().createHistory(bpChangeReqId, DOCUMENT_CATEGORY_BP_CHANGE, bpChangeReq.getBpId());
        //迁移变更完成数据
        documentHistoryDetailMapper.bpDataTransfer(bpChangeReqId);
        //删除已迁移数据
        documentHistoryDetailMapper.bpDeleteTransferData(bpChangeReqId);
        //迁移 sys_document_history_blob 数据
        documentHistoryBlobMapper.bpDataTransfer(bpChangeReqId);
        //删除已迁移数据
        documentHistoryBlobMapper.bpDeleteTransferData(bpChangeReqId);
    }

    public void createHistory(Long documentId, String documentCategory, Long bpId) throws ParameterNullException {
        List<Map<String, Object>> datas = getDatas(bpId);

        datas.forEach(item -> {
            Map<String, String> meta = (Map<String, String>) item.get("meta");
            meta.put("parentBaseTable", "hls_bp_master");
            meta.put("parentPkValue", bpId.toString());
        });
        //通用创建历史留痕接口
        sysDocumentHistoryService.createHistory(documentCategory, documentId, datas);
    }


    @Override
    public List<String> getMasterHistory(Long changeReqId) {
        Long historyId =  bpMasterChangeReqMapper.getHistoryIdByChangeReqId(changeReqId);
        Example example = new Example(SysDocumentHistoryDetail.class);
        example.createCriteria().andEqualTo("historyId", historyId);
        return sysDocumentHistoryDetailMapper.selectByExample(example).stream().map(SysDocumentHistoryDetail::getHistoryData).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDatas(Long bpId) throws ParameterNullException {
        //开始数据组装
        List<Map<String, Object>> datas = new ArrayList<>();
        //商业伙伴信息
        datas.addAll(getBpInfo(bpId));
        //地址信息
        datas.addAll(getBpAddress(bpId));
        //联系人信息
        datas.addAll(getBpContactInfo(bpId));
        //股东信息
        datas.addAll(getBpShareholder(bpId));
        //伙伴关系
        datas.addAll(getBpRelation(bpId));
        //银行账号
        datas.addAll(getBpBank(bpId));
        //框架协议
        datas.addAll(getAgreement(bpId));
        //关联业务经理
        datas.addAll(getEmployee(bpId));
        //开票信息
//        datas.addAll(getBilling(bpId));
        //角色信息
        datas.addAll(getBpRole(bpId));
        //起租规则
        datas.addAll(getRule(bpId));
        //附件
        datas.addAll(getBpAttachments(bpId));
        //主要组成人员
        datas.addAll(getBpMainMembers(bpId));
        //资产信息
        datas.addAll(getAssets(bpId));
        //基础数据项补充
//        datas.addAll(getBpOrgbase(bpId));
        //基本属性段数据项补充
//        datas.addAll(getBpOrgattr(bpId));
        //机构状态段数据项补充
//        datas.addAll(getBpOrgstatus(bpId));
        //主要关联企业
//        datas.addAll(getBpOrgaffiliated(bpId));
        //上级(主管)单位
//        datas.addAll(getBpOrgparentbody(bpId));
        //诉讼信息
//        datas.addAll(getBpCustlawsuit(bpId));
        //其他重大信息
//        datas.addAll(getBpCustevent(bpId));
        return datas;
    }

    private List<Map<String, Object>> getBpInfo(Long bpId) throws ParameterNullException {

        HlsCusBpMaster hlsCusBpMaster=new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(bpId);
        List<HlsCusBpMaster> bpMasters = hlsCusBpMasterMapper.queryCusBpMasterDetails(hlsCusBpMaster);
//        if (StringUtils.equals(bpMasters.get(0).getBpClass(), "NP")) {
//            bpMasters = hlsCusBpMasterMapper.queryForNp(map);
//        }
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master", "bp_id", bpMasters);
        } catch (ResMessageException e) {
            logger.warn("getHlsBpMaster {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpAddress(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        HlsCusBpMasterAddress hlsCusBpMasterAddress=new HlsCusBpMasterAddress();
        hlsCusBpMasterAddress.setBpId(bpId);
        List<HlsCusBpMasterAddress> bpAddresses = hlsBpMasterAddressMapper.queryAll(hlsCusBpMasterAddress);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_address", "address_id", "hls_bp_master", bpId.toString(), bpAddresses);
        } catch (ResMessageException e) {
            logger.warn("getBpAddresses {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpContactInfo(Long bpId) throws ParameterNullException {
        HlsCusBpMasterContactInfo hlsCusBpMasterContactInfo=new HlsCusBpMasterContactInfo();
        hlsCusBpMasterContactInfo.setBpId(bpId);
        List<HlsCusBpMasterContactInfo> bpContactInfos = contactInfoMapper.queryAll(hlsCusBpMasterContactInfo);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_contact_info", "contact_info_id", "hls_bp_master", bpId.toString(), bpContactInfos);
        } catch (ResMessageException e) {
            logger.warn("getBpContract {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpShareholder(Long bpId) throws ParameterNullException {

        HlsBpShareholderInfo hlsBpShareholderInfo=new HlsBpShareholderInfo();
        hlsBpShareholderInfo.setBpId(bpId);
        List<HlsBpShareholderInfo> bpShareholders = bpMasterShareholderMapper.queryHlsBpShareholderInfo(hlsBpShareholderInfo);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_shareholder", "bp_shareholder_id", "hls_bp_master", bpId.toString(), bpShareholders);
        } catch (ResMessageException e) {
            logger.warn("getBpShareholder {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getAgreement(Long bpId) throws ParameterNullException {

        List<BpMasterAgreement> bpMasterAgreements = bpMasterAgreementMapper.queryBpMasterAgreementDetail(bpId);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_agreement", "agreement_id", "hls_bp_master", bpId.toString(), bpMasterAgreements);
        } catch (ResMessageException e) {
            logger.warn("getBpRole {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getEmployee(Long bpId) throws ParameterNullException {
        List<BpMasterEmployee> bpMasterEmployees = bpMasterEmployeeMapper.queryBpMasterEmployeeDetail(bpId);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_employee", "bp_employee_id", "hls_bp_master", bpId.toString(), bpMasterEmployees);
        } catch (ResMessageException e) {
            logger.warn("getBpRole {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBilling(Long bpId) throws ParameterNullException {
        HlsCusBpMaster hlsCusBpMaster =new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(bpId);
        List<HlsCusBpMaster> hlsCusBpMasters = hlsBpMasterMapper.queryCusBpMasterDetails(hlsCusBpMaster);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master", "bp_id",   hlsCusBpMasters);
        } catch (ResMessageException e) {
            logger.warn("getBpRole {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getRule(Long bpId) throws ParameterNullException {
        List<HlsBpMasterInceptRule> bpMasterInceptRules = hlsBpMasterInceptRuleMapper.query(bpId);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_incept_rule", "incept_rule_id", "hls_bp_master", bpId.toString(), bpMasterInceptRules);
        } catch (ResMessageException e) {
            logger.warn("getBpRole {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getAssets(Long bpId) throws ParameterNullException {
        HlsBpAssetsList hlsBpAssetsList = new HlsBpAssetsList();
        hlsBpAssetsList.setBpId(bpId);
        List<HlsBpAssetsList> hlsBpAssetsLists = hlsBpAssetsListMapper.queryAll(hlsBpAssetsList);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_assets_list", "assets_id", "hls_bp_master", bpId.toString(), hlsBpAssetsLists);
        } catch (ResMessageException e) {
            logger.warn("getBpRole {}", e.getMessage());
        }
        return list;
    }


    private List<Map<String, Object>> getBpRole(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<HlsBpMasterRole> bpRoles = bpMasterRoleMapper.query2(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_role", "bp_role_id", "hls_bp_master", bpId.toString(), bpRoles);
        } catch (ResMessageException e) {
            logger.warn("getBpRole {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpRelation(Long bpId) throws ParameterNullException {
        HashMap<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<HlsCusBpMasterRelation> hlsBpMasterRelations = relationMapper.queryRelationDetails(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_relation", "bp_relation_id", "hls_bp_master", bpId.toString(), hlsBpMasterRelations);
        } catch (ResMessageException e) {
            logger.warn("getBpMasterRelation {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpBank(Long bpId) throws ParameterNullException {
        HlsCusBpMasterBankAccount hlsCusBpMasterBankAccount=new HlsCusBpMasterBankAccount();
        hlsCusBpMasterBankAccount.setBpId(bpId);
        List<HlsCusBpMasterBankAccount> bpBanks = bankAccountMapper.queryCusBpMasterBankByBpId(hlsCusBpMasterBankAccount);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_master_bank_account", "bank_account_id", "hls_bp_master", bpId.toString(), bpBanks);
        } catch (ResMessageException e) {
            logger.warn("getBpBank {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpOrgbase(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpOrgbase> bpOrgbases = orgbaseMapper.queryZxBpOrgbase2(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_orgbase", "record_id", "hls_bp_master", bpId.toString(), bpOrgbases);
        } catch (ResMessageException e) {
            logger.warn("getBpOrgbase {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpOrgattr(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpOrgattr> bpOrgattrs = orgattrMapper.query(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_orgattr", "record_id", "hls_bp_master", bpId.toString(), bpOrgattrs);
        } catch (ResMessageException e) {
            logger.warn("getBpOrgattr {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpOrgstatus(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpOrgstatus> bpOrgstatus = orgstatusMapper.query(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_orgstatus", "record_id", "hls_bp_master", bpId.toString(), bpOrgstatus);
        } catch (ResMessageException e) {
            logger.warn("getBpOrgstatus {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpOrgaffiliated(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpOrgaffiliated> bpOrgaffiliated = orgaffiliatedMapper.query(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_orgaffiliated", "record_id", "hls_bp_master", bpId.toString(), bpOrgaffiliated);
        } catch (ResMessageException e) {
            logger.warn("getBpOrgaffiliated {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpOrgparentbody(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpOrgparentbody> bpOrgparentbody = orgparentbodyMapper.query(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_orgparentbody", "record_id", "hls_bp_master", bpId.toString(), bpOrgparentbody);
        } catch (ResMessageException e) {
            logger.warn("getBpOrgparentbody {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpCustlawsuit(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpCustlawsuit> bpCustlawsuit = custlawsuitMapper.query(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_custlawsuit", "record_id", "hls_bp_master", bpId.toString(), bpCustlawsuit);
        } catch (ResMessageException e) {
            logger.warn("getBpCustlawsuit {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpCustevent(Long bpId) throws ParameterNullException {
        Map<String, Long> map = new HashMap<>();
        map.put("bpId", bpId);
        List<ZxBpCustevent> bpCustevent = custeventMapper.query(map);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("zx_bp_custevent", "record_id", "hls_bp_master", bpId.toString(), bpCustevent);
        } catch (ResMessageException e) {
            logger.warn("getBpCustevent {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpAttachments(Long bpId) throws ParameterNullException {
        HlsCusBpAttachment hlsCusBpAttachment=new HlsCusBpAttachment();
        hlsCusBpAttachment.setBpId(bpId);
        List<HlsCusBpAttachment> hlsCusBpAttachments = attachmentMapper.queryAllBpFileDocumentListName(hlsCusBpAttachment);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("hls_bp_attachment", "bp_attachment_id", "hls_bp_master", bpId.toString(), hlsCusBpAttachments);
        } catch (ResMessageException e) {
            logger.warn("getBpAttachments {}", e.getMessage());
        }
        return list;
    }

    private List<Map<String, Object>> getBpMainMembers(Long bpId) throws ParameterNullException {
        HlsCusBpSeniorPersion hlsCusBpSeniorPersion=new HlsCusBpSeniorPersion();
        hlsCusBpSeniorPersion.setBpId(bpId);
        List<HlsCusBpSeniorPersion> hlsCusBpSeniorPersions = hlsCusBpSeniorPersionMapper.querySeniorByBpId(hlsCusBpSeniorPersion);
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = SysDocumentHistoryUtils.initRecords("bp_senior_persion", "main_members_id", "hls_bp_master", bpId.toString(), hlsCusBpSeniorPersions);
        } catch (ResMessageException e) {
            logger.warn("getBpAddresses {}", e.getMessage());
        }
        return list;
    }


}