package com.hand.hls.req.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.*;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.req.service.HlsCusChangeReqInfoService;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.sys.utils.SysDocumentHistoryUtils;
import com.hand.hls.utils.ResMessageException;
import hls.core.utils.exception.HlsCusException;
import leaf.service.validation.ParameterNullException;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusChangeReqInfoServiceImpl extends BaseServiceImpl<HlsCusChangeReqInfo> implements HlsCusChangeReqInfoService {

    @Autowired
    private HlsCusChangeReqInfoMapper mapper;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private DatabaseLockProvider databaseLockProvider;

    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;

    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Autowired
    private HlsCusPrjProjectBpMapper hlsCusPrjProjectBpMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final String APPROVING = "APPROVING";
    private static final String NEW = "NEW";
    private static final String REJECTED = "REJECTED";

    private static final String DOCUMENT_CATEGORY = "PRJ_PROJECT";

    @Override
    public List<HlsCusChangeReqInfo> queryChangeInfo(IRequest request, HlsCusChangeReqInfo hlsCusChangeReqInfo) {
        return mapper.queryChangeInfo(hlsCusChangeReqInfo);
    }

    @Override
    public List<HlsCusChangeReqInfo> queryContractChangeInfo(IRequest request, HlsCusChangeReqInfo hlsCusChangeReqInfo, int page, int pageSize) {
        hlsCusChangeReqInfo.setCompanyId(request.getCompanyId());
        PageHelper.startPage(page, pageSize);
        return mapper.queryContractChangeInfo(hlsCusChangeReqInfo);
    }

    @Override
    public List<HlsCusChangeReqInfo> queryHistory(HlsCusChangeReqInfo hlsCusChangeReqInfo) {
        return mapper.queryHistory(hlsCusChangeReqInfo);
    }

    @Override
    public List<HlsCusChangeReqInfo> queryStatus(HlsCusChangeReqInfo hlsCusChangeReqInfo) {
        return mapper.queryStatus(hlsCusChangeReqInfo);
    }

    @Override
    public Map queryConChangeType(HlsCusChangeReqInfo hlsCusChangeReqInfo) {
        Map map = new HashMap();
        map = mapper.queryConChangeType(hlsCusChangeReqInfo);
        return map;
    }

    @Override
    public Map queryPrcContractChangeType() {
        return mapper.queryPrcContractChangeType();
    }

    @Override
    public List<HlsCusChangeReqInfo> selectConChangeType(IRequest requestContext, HlsCusChangeReqInfo hlsCusChangeReqInfo, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        HashedMap map = new HashedMap();
        String[] paramBusStatus = null;
        if (hlsCusChangeReqInfo.getBusTypeStatusList() != null) {
            paramBusStatus = hlsCusChangeReqInfo.getBusTypeStatusList().split("、");
        }
        map.put("busTypeStatusList", paramBusStatus);
        if (hlsCusChangeReqInfo.getChangeReqDate() != null) {
            map.put("changeReqDate", hlsCusChangeReqInfo.getChangeReqDate());
        }
        if (hlsCusChangeReqInfo.getBusinessType() != null) {
            map.put("businessType", hlsCusChangeReqInfo.getBusinessType());
        }
        if (hlsCusChangeReqInfo.getParams() != null) {
            map.put("params", hlsCusChangeReqInfo.getParams());
        }
        return mapper.selectConChangeType(map);
    }

    @Override
    public List<HlsCusChangeReqInfo> projectChangeReq(IRequest iRequest, HlsCusChangeReqInfo hlsCusChangeReqInfo) throws HlsCusException {


        //校验参数及单据状态是否正确
        if(hlsCusChangeReqInfo.getDocumentId() == null || hlsCusChangeReqInfo.getDocumentCategory() == null){
            throw new HlsCusException("单据信息获取失败!");
        }
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusChangeReqInfo.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest,hlsCusPrjProject);

        //审批中,新建，拒绝跟正在变更的单据无法提交变更
        if (APPROVING.equals(hlsCusPrjProject.getProjectStatus()) || NEW.equals(hlsCusPrjProject.getProjectStatus()) || REJECTED.equals(hlsCusPrjProject.getProjectStatus())) {
            throw new HlsCusException("当前单据状态无法创建变更申请!");
        }

        List<HlsCusChangeReqInfo> hlsCusChangeReqInfoList = mapper.selectDocuemntSubmitChangeInfo(hlsCusChangeReqInfo);
        if(hlsCusChangeReqInfoList.size() > 0){
            throw new HlsCusException("当前单据已经在变更申请中!");
        }

        //锁表
        databaseLockProvider.lock(hlsCusPrjProject);

        //更改项目状态

        //新建变更记录
        hlsCusChangeReqInfo.setChangeReqId(iRequest.getUserId());
        hlsCusChangeReqInfo.setStatus(NEW);
        self().insert(iRequest,hlsCusChangeReqInfo);

        //备份项目信息，存储到大字段中


        return null;
    }

    @Override
    public void backupProject(IRequest iRequest, HlsCusPrjProject hlsCusPrjProject) throws ParameterNullException, HlsCusException {
        List<Map<String, Object>> datas = getProjectDatas(hlsCusPrjProject);


        //通用创建历史留痕接口
        sysDocumentHistoryService.createHistory(DOCUMENT_CATEGORY, hlsCusPrjProject.getProjectId(), datas);
    }

    private List<Map<String,Object>> getProjectDatas(HlsCusPrjProject hlsCusPrjProject) throws ParameterNullException, HlsCusException {
        List<Map<String, Object>> list = new ArrayList<>();

        //项目信息
        List<Map> hlsCusPrjProjectList = hlsCusPrjProjectMapper.queryPrjDetail(hlsCusPrjProject);
        try {
            List<Map<String, Object>> projectMapList = SysDocumentHistoryUtils.initRecords("prj_project", "project_id", hlsCusPrjProjectList);
            list.addAll(projectMapList);
        } catch (ResMessageException e) {
            logger.warn("projectMapList {}", e.getMessage());
            throw new HlsCusException("备份项目数据异常");
        }

        //附件表信息
        HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
        List<Map> attachmentList = hlsCusPrjProjectAttachmentMapper.selectPrjProjectAllAttachmentInfo(hlsCusPrjProjectAttachment);
        try {
            List<Map<String, Object>> attachmentMapList = SysDocumentHistoryUtils.initRecords("prj_project_attachment", "project_attachment_id","prj_project",hlsCusPrjProject.getProjectId().toString(), attachmentList);
            list.addAll(attachmentMapList);
        } catch (ResMessageException e) {
            logger.warn("attachmentList {}", e.getMessage());
            throw new HlsCusException("备份项目附件数据异常");
        }

        //承租人
        Map map = new HashMap<>();
        map.put("projectId", hlsCusPrjProject.getProjectId());
        map.put("bpType", "TENANT");
        List<Map> tenantList = hlsCusPrjProjectBpMapper.selectProjectBpInfo(map);
        try {
            List<Map<String, Object>> tenantMapList = SysDocumentHistoryUtils.initRecords("prj_project_bp", "prj_bp_id","prj_project",hlsCusPrjProject.getProjectId().toString(), tenantList);
            list.addAll(tenantMapList);
        } catch (ResMessageException e) {
            logger.warn("tenantMapList {}", e.getMessage());
            throw new HlsCusException("备份项目承租人数据异常");
        }

        //联合承租人
        map.put("bpType", "TENANT_SEC");
        List<Map> tenantSecList = hlsCusPrjProjectBpMapper.selectProjectBpInfo(map);
        try {
            List<Map<String, Object>> tenantSecMapList = SysDocumentHistoryUtils.initRecords("prj_project_bp", "prj_bp_id","prj_project",hlsCusPrjProject.getProjectId().toString(), tenantSecList);
            list.addAll(tenantSecMapList);
        } catch (ResMessageException e) {
            logger.warn("tenantSecMapList {}", e.getMessage());
            throw new HlsCusException("备份项目联合承租人数据异常");
        }

        //租赁物
        HlsCusPrjProjectLeaseItem leaseItem = new HlsCusPrjProjectLeaseItem();
        leaseItem.setProjectId(hlsCusPrjProject.getProjectId());
        List<HlsCusPrjProjectLeaseItem> itemList = hlsCusPrjProjectLeaseItemMapper.queryPrjProjectLeaseItem(leaseItem);
        try {
            List<Map<String, Object>> itemMapList = SysDocumentHistoryUtils.initRecords("prj_project_lease_item", "project_lease_item_id","prj_project",hlsCusPrjProject.getProjectId().toString(), itemList);
            list.addAll(itemMapList);
        } catch (ResMessageException e) {
            logger.warn("itemMapList {}", e.getMessage());
            throw new HlsCusException("备份项目租赁物数据异常");
        }

        //抵质押物
        map.put("bpRoleTypeFlag", "Y");
        List<Map> mortgagorList = hlsCusPrjProjectBpMapper.selectPledgeAndMortgagor(map);
        try {
            List<Map<String, Object>> mortgagorMapList = SysDocumentHistoryUtils.initRecords("prj_project_bp", "prj_bp_id","prj_project",hlsCusPrjProject.getProjectId().toString(), mortgagorList);
            list.addAll(mortgagorMapList);
        } catch (ResMessageException e) {
            logger.warn("mortgagorMapList {}", e.getMessage());
            throw new HlsCusException("备份项目抵质押物数据异常");
        }

        //报价信息
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation.setSourceDocumentId(hlsCusPrjProject.getProjectId());
        quotation.setSourceDocumentCategory("PRJ_PROJECT");
        List<HlsCusPrjQuotation> quotationList = hlsCusPrjQuotationMapper.queryPrjQuotationInfo(quotation);
        try {
            List<Map<String, Object>> mortgagorMapList = SysDocumentHistoryUtils.initRecords("prj_quotation", "quotation_id","prj_project",hlsCusPrjProject.getProjectId().toString(), quotationList);
            list.addAll(mortgagorMapList);
        } catch (ResMessageException e) {
            logger.warn("mortgagorMapList {}", e.getMessage());
            throw new HlsCusException("备份项目报价信息数据异常");
        }

        //报价现金流

        return list;
    }
}