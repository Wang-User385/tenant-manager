package com.hand.hls.fct.service.impl;


import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.common.components.HlsWordToPdfComponent;
import com.hand.hls.cont.dto.HlsDocFileTemplet;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.csh.service.IProjectCreditConditionService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusFctProjectAttachment;
import com.hand.hls.fct.mapper.HlsCusFctProjectAttachmentMapper;
import com.hand.hls.fct.service.HlsCusFctProjectAttachmentService;
import com.hand.hls.fct.service.HlsCusPrjContractDocxService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjProjectAttachment;
import com.hand.hls.prj.mapper.HlsCusPrjProjectAttachmentMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.prj.service.HlsCusPrjSurviveRequireService;
import com.hand.hls.prj.service.IHlsCusQuatationElementsService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusFctProjectAttachmentServiceImpl extends BaseServiceImpl<HlsCusFctProjectAttachment> implements HlsCusFctProjectAttachmentService {

    public static final String PRJ_REPORT_CONTENT = "PRJ_REPORT_CONTENT";

    @Autowired
    private HlsCusFctProjectAttachmentMapper hlsCusFctProjectAttachmentMapper;
    @Autowired
    private HlsCusFctProjectAttachmentService hlsCusFctProjectAttachmentService;

    @Autowired
    private HlsCusPrjProjectAttachmentMapper hlsCusPrjProjectAttachmentMapper;

    @Autowired
    private HlsWordToPdfComponent hlsWordToPdfComponent;

    @Autowired
    private IProjectCreditConditionService iProjectCreditConditionService;
    @Autowired
    private HlsCusPrjSurviveRequireService hlsCusPrjSurviveRequireService;

    @Autowired
    private IHlsCusQuatationElementsService iHlsCusQuatationElementsService;

    @Override
    public List<HlsCusFctProjectAttachment> fctProjectNoticeAttachmentDetailQuery(IRequest iRequest, HlsCusFctProjectAttachment hlsCusFctProjectAttachment, int page, int pageSize) {
        //PageHelper.startPage(page, pageSize);
        return hlsCusFctProjectAttachmentMapper.fctProjectNoticeAttachmentDetailQuery(hlsCusFctProjectAttachment);
    }


    @Override
    public List<HlsCusFctProjectAttachment> queryContentFileInfo(IRequest iRequest, HlsCusFctProjectAttachment hlsCusFctProjectAttachment, int page, int pageSize, int mode) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusFctProjectAttachment> list;
        if (mode == 1) {
            list = hlsCusFctProjectAttachmentMapper.queryContentFileInfo(hlsCusFctProjectAttachment);
        } else {
            list = hlsCusFctProjectAttachmentMapper.queryContentFileInfo2(hlsCusFctProjectAttachment);
        }

        //查询的是签约信息
        if ("SIGN".equalsIgnoreCase(hlsCusFctProjectAttachment.getSignInfoFlag())) {
            HlsCusPrjProjectAttachment hlsCusPrjProjectAttachment = new HlsCusPrjProjectAttachment();
            hlsCusPrjProjectAttachment.setProjectId(hlsCusFctProjectAttachment.getProjectId());
            hlsCusPrjProjectAttachment.setProjectAttachmentCategory("FCT_CONTRACT");
            List<HlsCusPrjProjectAttachment> hlsCusPrjProjectAttachmentList = hlsCusPrjProjectAttachmentMapper.select(hlsCusPrjProjectAttachment);

            for (HlsCusPrjProjectAttachment dt : hlsCusPrjProjectAttachmentList) {
                HlsCusFctProjectAttachment fctProjectAttachment = new HlsCusFctProjectAttachment();
                fctProjectAttachment.setProjectId(hlsCusFctProjectAttachment.getProjectId());
                fctProjectAttachment.setBpCategory("FCT_CONTRACT_CONTENT_ATT");
                fctProjectAttachment.setSourceType("FCT_CONTRACT_CONTENT");
                fctProjectAttachment.setSourceId(dt.getProjectAttachmentId() * (-2L));//把附件表主键作为sourceId
                List<HlsCusFctProjectAttachment> hlsCusFctProjectAttachmentList = hlsCusFctProjectAttachmentMapper.select(fctProjectAttachment);
                if (CollectionUtils.isEmpty(hlsCusFctProjectAttachmentList)) {
                    fctProjectAttachment.setDocumentName(dt.getDocumentName());
                    fctProjectAttachment.setDocumentNumber(dt.getDocumentName());
                    fctProjectAttachment.setStatus("GENERATED");
                    fctProjectAttachment = self().insertSelective(iRequest, fctProjectAttachment);
                    list.add(fctProjectAttachment);
                }
            }
            //}

        }
        return list;
    }


    @Override
    public List<HlsCusFctProjectAttachment> prjConContentSave(IRequest iRequest, Long projectId) throws HlsCusException {
        HlsCusFctProjectAttachment t = new HlsCusFctProjectAttachment();
        t.setProjectId(projectId);
        List<HlsCusFctProjectAttachment> hlsCusFctProjectAttachmentList = hlsCusFctProjectAttachmentMapper.queryContentFileInfo(t);
        if (CollectionUtils.isEmpty(hlsCusFctProjectAttachmentList)) {
            throw new HlsCusException("没有合同模板");
        }

        String companyName = "";
        String categoryType = "";
        String contrateMainNum = "";

        //集合排序-正序
//        Collections.sort(hlsCusFctProjectAttachmentList, (o1, o2) -> {
//            int i = o1.getSourceId().intValue() - o2.getSourceId().intValue();
//            return i;
//        });
//
//        for (int i = 0; i < hlsCusFctProjectAttachmentList.size(); i++) {
//            hlsCusFctProjectAttachmentList.get(i).setSourceId((long) (i + 1));
//        }

        //生成合同附件
        for (int i = 0; i < hlsCusFctProjectAttachmentList.size(); i++) {
            HlsCusFctProjectAttachment attachment = hlsCusFctProjectAttachmentList.get(i);
            FndAttachmentMulti query = new FndAttachmentMulti();
            query.setTablePkValue(attachment.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> files = fndAttachmentMultiService.selectSelective(iRequest, query);
//            if (CollectionUtils.isEmpty(files)) {
            Map<String, Object> params = new HashMap<String, Object>();
            //前台传入的参数需要这里接收
            params.put("projectId", attachment.getProjectId());
            params.put("templetId", attachment.getTemplateId());
            params.put("sourceType", attachment.getSourceType());
            params.put("projectAttachmentId", attachment.getProjectAttachmentId());
            params.put("companyId", iRequest.getCompanyId());
            params.put("bpId", attachment.getBpId());
            hlsCusPrjContractDocxService.process(iRequest, params);
//            }
        }
        return hlsCusFctProjectAttachmentList;
    }

    @Override
    public List<HlsCusFctProjectAttachment> prjApprovalNoticeTextSave(IRequest iRequest, Long projectId, Long prjNoticeId, Long quotationId) throws HlsCusException {
        HlsCusFctProjectAttachment t = new HlsCusFctProjectAttachment();
        t.setProjectId(projectId);
        List<HlsCusFctProjectAttachment> hlsCusFctProjectAttachmentList = hlsCusFctProjectAttachmentMapper.queryContentFileInfo(t);
        if (CollectionUtils.isEmpty(hlsCusFctProjectAttachmentList)) {
            throw new HlsCusException("没有合同模板");
        }

        String companyName = "";
        String categoryType = "";
        String contrateMainNum = "";

        //集合排序-正序
//        Collections.sort(hlsCusFctProjectAttachmentList, (o1, o2) -> {
//            int i = o1.getSourceId().intValue() - o2.getSourceId().intValue();
//            return i;
//        });
//
//        for (int i = 0; i < hlsCusFctProjectAttachmentList.size(); i++) {
//            hlsCusFctProjectAttachmentList.get(i).setSourceId((long) (i + 1));
//        }

        //生成合同附件
        for (int i = 0; i < hlsCusFctProjectAttachmentList.size(); i++) {
            HlsCusFctProjectAttachment attachment = hlsCusFctProjectAttachmentList.get(i);
            FndAttachmentMulti query = new FndAttachmentMulti();
            query.setTablePkValue(attachment.getProjectAttachmentId().toString());
            List<FndAttachmentMulti> files = fndAttachmentMultiService.selectSelective(iRequest, query);
//            if (CollectionUtils.isEmpty(files)) {
            Map<String, Object> params = new HashMap<String, Object>();
            //前台传入的参数需要这里接收
            params.put("projectId", attachment.getProjectId());
            params.put("templetId", attachment.getTemplateId());
            params.put("sourceType", attachment.getSourceType());
            params.put("projectAttachmentId", attachment.getProjectAttachmentId());
            params.put("companyId", iRequest.getCompanyId());
            params.put("bpId", attachment.getBpId());
            params.put("prjNoticeId", prjNoticeId);
            params.put("quotationId", quotationId);
            hlsCusPrjContractDocxService.process(iRequest, params);
//            }
        }
        return hlsCusFctProjectAttachmentList;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachmentMulti> prjContentSave(IRequest iRequest, Long projectId, String templateCode) throws HlsCusException {
        List<FndAttachmentMulti> fndAttachmentMultiList = new ArrayList<FndAttachmentMulti>();

        if (templateCode == null || templateCode.trim().length() == 0) {
            return fndAttachmentMultiList;
        }

        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletCode(templateCode);
        List<HlsDocFileTemplet> list = hlsDocFileTempletService.selectSelective(iRequest, hlsDocFileTemplet);
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
        if (list.size() == 1) {
            Long templeteId = list.get(0).getTempletId();
            hlsCusFctProjectAttachment.setProjectId(projectId);
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            hlsCusFctProjectAttachment.setBpId(hlsCusPrjProject.getTenantId());
            hlsCusFctProjectAttachment.setTemplateId(templeteId);
            hlsCusFctProjectAttachment.setSourceType(templateCode);
            List<HlsCusFctProjectAttachment> attachmentList = hlsCusFctProjectAttachmentService.selectSelective(iRequest, hlsCusFctProjectAttachment);

            if (attachmentList.size() == 0) {
                hlsCusFctProjectAttachment = hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
            } else {
                for (HlsCusFctProjectAttachment at : attachmentList) {
                    hlsCusFctProjectAttachmentMapper.deleteHistoryPrjReport(at);
                }
                hlsCusFctProjectAttachment = hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
            }
        }
        //生成合同附件
        FndAttachmentMulti query = new FndAttachmentMulti();
        query.setTablePkValue(hlsCusFctProjectAttachment.getProjectAttachmentId().toString());
        List<FndAttachmentMulti> files = fndAttachmentMultiService.selectSelective(iRequest, query);

        Map<String, Object> params = new HashMap<String, Object>();
        //前台传入的参数需要这里接收
        params.put("projectId", projectId);
        params.put("templetId", hlsCusFctProjectAttachment.getTemplateId());
        params.put("sourceType", hlsCusFctProjectAttachment.getSourceType());
        params.put("projectAttachmentId", hlsCusFctProjectAttachment.getProjectAttachmentId());
        params.put("companyId", iRequest.getCompanyId());
        params.put("bpId", hlsCusFctProjectAttachment.getBpId());
        hlsCusPrjContractDocxService.process(iRequest, params);


        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTablePkValue(String.valueOf(hlsCusFctProjectAttachment.getProjectAttachmentId()));
        fndAttachmentMulti.setTableName("FCT_PROJECT_ATTACHMENT");
        fndAttachmentMultiList = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);
        return fndAttachmentMultiList;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    @Override
    public List<FndAttachmentMulti> prjApprovalNoticeSave(IRequest iRequest, Long projectId, String templateCode, Long prjNoticeId, Long quotationId, Long oldProjectId) throws HlsCusException {

        //如果是变更，则实时更新项目要素等信息
        if (oldProjectId != null) {

            //获取付款前提条件的变更文本信息
            String creditConditionContent = iProjectCreditConditionService.getProjectHistoryInfo(iRequest, projectId, oldProjectId);

            //获取存续管理要求的变更文本信息
            String surviveRequireContent = hlsCusPrjSurviveRequireService.getPrjSurviveRequireChangeInfo(iRequest, projectId, oldProjectId);

            //获取报价要素变更文本信息
            String quotationElementsContent = iHlsCusQuatationElementsService.getQuatationElementChangeInfo(iRequest, projectId, oldProjectId);

            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject.setCreditConditionContent(creditConditionContent);
            hlsCusPrjProject.setSurviveRequireContent(surviveRequireContent);
            hlsCusPrjProject.setQuotationElementContent(quotationElementsContent);
            hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, hlsCusPrjProject);
        }

        List<FndAttachmentMulti> fndAttachmentMultiList = new ArrayList<FndAttachmentMulti>();

        if (templateCode == null || templateCode.trim().length() == 0) {
            return fndAttachmentMultiList;
        }

        HlsDocFileTemplet hlsDocFileTemplet = new HlsDocFileTemplet();
        hlsDocFileTemplet.setTempletCode(templateCode);
        List<HlsDocFileTemplet> list = hlsDocFileTempletService.selectSelective(iRequest, hlsDocFileTemplet);
        HlsCusFctProjectAttachment hlsCusFctProjectAttachment = new HlsCusFctProjectAttachment();
        if (list.size() == 1) {
            Long templeteId = list.get(0).getTempletId();
            hlsCusFctProjectAttachment.setProjectId(projectId);
            HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
            hlsCusPrjProject.setProjectId(projectId);
            hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
            hlsCusFctProjectAttachment.setBpId(hlsCusPrjProject.getTenantId());
            List<HlsCusFctProjectAttachment> attachmentList = hlsCusFctProjectAttachmentService.selectSelective(iRequest, hlsCusFctProjectAttachment);
            hlsCusFctProjectAttachment.setTemplateId(templeteId);
            hlsCusFctProjectAttachment.setSourceType(templateCode);
            hlsCusFctProjectAttachment.setPrjNoticeId(prjNoticeId);
            if (attachmentList.size() == 0) {
                hlsCusFctProjectAttachment = hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
            } else {
                for (HlsCusFctProjectAttachment at : attachmentList) {
                    hlsCusFctProjectAttachmentMapper.deleteHistoryPrjReport(at);
                }
                hlsCusFctProjectAttachment = hlsCusFctProjectAttachmentService.insertSelective(iRequest, hlsCusFctProjectAttachment);
            }
        }
        this.prjApprovalNoticeTextSave(iRequest, projectId, prjNoticeId, quotationId);
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setTablePkValue(String.valueOf(hlsCusFctProjectAttachment.getProjectAttachmentId()));
        fndAttachmentMulti.setTableName("FCT_PROJECT_ATTACHMENT");
        fndAttachmentMultiList = fndAttachmentMultiService.selectSelective(iRequest, fndAttachmentMulti);

        return fndAttachmentMultiList;
    }


    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    /**
     * 查询合同文本的数量
     *
     * @param templetCategory
     * @param projectId
     * @return 返回数量的两位字符串
     */
    public String getTwoNum(String templetCategory, Long projectId, Long projectAttachmentId) {
        String twoNum = "";
        HlsCusFctProjectAttachment fctProjectAttachment = new HlsCusFctProjectAttachment();
        fctProjectAttachment.setBpCategory(templetCategory);
        fctProjectAttachment.setProjectId(projectId);
        List<HlsCusFctProjectAttachment> list = hlsCusFctProjectAttachmentMapper.selectByCategory(fctProjectAttachment);
        if (projectAttachmentId != null && projectAttachmentId != 0) {
            twoNum = String.valueOf(list.size() + 101).substring(1, 3);
        } else {
            twoNum = String.valueOf(list.size() + 101).substring(1, 3);
        }
        return twoNum;
    }

    /**
     * 重新刷新编号
     *
     * @param iRequest
     * @param templetCategory
     * @param projectId
     * @return
     */
    public void getCount(IRequest iRequest, String templetCategory, Long projectId) {
        HlsCusFctProjectAttachment fctProjectAttachment = new HlsCusFctProjectAttachment();
        fctProjectAttachment.setBpCategory(templetCategory);
        fctProjectAttachment.setProjectId(projectId);
        List<HlsCusFctProjectAttachment> list = hlsCusFctProjectAttachmentMapper.selectByCategory(fctProjectAttachment);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String oldNumber = list.get(i).getDocumentNumber().substring(0, list.get(i).getDocumentNumber().length() - 2);
                String newNumber = oldNumber + String.valueOf(i + 101).substring(1, 3);
                list.get(i).setDocumentNumber(newNumber);
                self().updateByPrimaryKeySelective(iRequest, list.get(i));
            }
        }
    }

    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;

    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;

    @Autowired
    private HlsCusPrjContractDocxService hlsCusPrjContractDocxService;

    @Override
    public void contentFinalized(IRequest iRequest, HlsCusFctProjectAttachment hlsCusFctProjectAttachment) {
        HlsCusFctProjectAttachment projectAttachment = new HlsCusFctProjectAttachment();
        projectAttachment.setProjectId(hlsCusFctProjectAttachment.getProjectId());
        projectAttachment.setStatus("FINALIZED");
        List<HlsCusFctProjectAttachment> list2 = hlsCusFctProjectAttachmentMapper.select(projectAttachment);
        this.batchDelete(list2);


        HlsCusFctProjectAttachment fctProjectAttachment = new HlsCusFctProjectAttachment();
        fctProjectAttachment.setProjectId(hlsCusFctProjectAttachment.getProjectId());
        fctProjectAttachment.setStatus("FIRST_DRAFT");
        List<HlsCusFctProjectAttachment> list = hlsCusFctProjectAttachmentMapper.select(fctProjectAttachment);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setStatus("FINALIZED");
            list.get(i).set__status("insert");
        }
        this.batchUpdate(iRequest, list);
    }


    @Override
    public List<HlsCusFctProjectAttachment> selectAttachmentByProjectId(HlsCusFctProjectAttachment hlscusfctprojectattachment){
        List<HlsCusFctProjectAttachment> hlsCusPrjProjectAttachmentList=new ArrayList<>();
        hlsCusPrjProjectAttachmentList=hlsCusFctProjectAttachmentMapper.selecttAttachmentByProjectId(hlscusfctprojectattachment);
        return hlsCusPrjProjectAttachmentList;
    }

    @Override
    public List<HlsCusFctProjectAttachment> selecttAttachmentByTemplateId(HlsCusFctProjectAttachment hlscusfctprojectattachment){
        List<HlsCusFctProjectAttachment> hlsCusPrjProjectAttachmentList=new ArrayList<>();
        hlsCusPrjProjectAttachmentList=hlsCusFctProjectAttachmentMapper.selecttAttachmentByTemplateId(hlscusfctprojectattachment);
        return hlsCusPrjProjectAttachmentList;
    }

}
