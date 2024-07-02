package com.hand.hls.sys.components;

import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.fin.dto.BpMasterAttachment;
import com.hand.hls.fin.service.IBpMasterAttachmentService;
import com.hand.hls.fnd.exception.NecessaryFileNotUploadException;
import com.hand.hls.sys.dto.SysDocumentList;
import com.hand.hls.sys.mapper.SysDocumentListMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @auth: Marshal
 * @date: 2019/6/1
 * @desc:
 */
@Component
@Transactional(rollbackFor = Exception.class)
public class BpGenerateStrategy implements GenerateStrategy {

    //单据类别
    private static final String DOCUMENT_CATEGORY = "HLS_BP_MASTER";

    //附件关联表
    private static final String ATM_LINK_TABLE_NAME = "bp_master_attachment";

    @Autowired
    private SysDocumentListMapper sysDocumentListMapper;

    @Autowired
    private HlsCusBpMasterMapper hlsCusBpMasterMapper;

    @Autowired
    private IBpMasterAttachmentService bpMasterAttachmentService;

    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    @Override
    public void generateDocumentList(IRequest iRequest, Long documentId) throws IllegalArgumentException {
        HlsCusBpMaster bpMaster = hlsCusBpMasterMapper.selectByPrimaryKey(documentId);
        if (bpMaster == null) {
            throw new IllegalArgumentException("商业伙伴不存在!");
        }
        String bpCategory = bpMaster.getBpCategory();
        if (StringUtils.isBlank(bpCategory)) {
            throw new IllegalArgumentException("商业伙伴类别信息异常!");
        }

        List<SysDocumentList> documentLists = new ArrayList<>();
        SysDocumentList condition = new SysDocumentList();
        condition.setDocumentCategory(DOCUMENT_CATEGORY);
        condition.setDocumentType(DOCUMENT_TYPE_NORMAL);
        condition.setEnabledFlag(BaseConstants.YES);

        //通用资料清单
        List<SysDocumentList> normalDocumentList = sysDocumentListMapper.select(condition);
        documentLists.addAll(normalDocumentList);

        condition.setDocumentType(bpCategory);

        //根据bpCategory得到的资料清单
        List<SysDocumentList> specialDocumentList = sysDocumentListMapper.select(condition);
        documentLists.addAll(specialDocumentList);

        BpMasterAttachment attachment = new BpMasterAttachment();
        attachment.setBpId(documentId);
        attachment.setBpAttachmentCategory(ATTACHMENT_CATEGORY);
        //已存在的自动生成的资料清单记录
        List<BpMasterAttachment> bpMasterAttachments = bpMasterAttachmentService.selectSelective(iRequest, attachment);

        List<Long> recentDocumentIds = bpMasterAttachments.stream().map(BpMasterAttachment::getListDocumentId).collect(Collectors.toList());

        //尚未添加的资料清单
        List<SysDocumentList> filterDocumentList = documentLists.stream().filter(o -> {
            return recentDocumentIds.indexOf(o.getDocumentId()) < 0;
        }).collect(Collectors.toList());

        for (SysDocumentList item : filterDocumentList) {
            BpMasterAttachment bpMasterAttachment = new BpMasterAttachment();
            bpMasterAttachment.setBpId(documentId);
            bpMasterAttachment.setBpAttachmentCategory(ATTACHMENT_CATEGORY);
            bpMasterAttachment.setDocumentName(item.getDocumentListName());
            bpMasterAttachment.setListDocumentId(item.getDocumentId());
            bpMasterAttachmentService.insertSelective(iRequest, bpMasterAttachment);
        }

        if (deleteHistoryDocumentListDynamic()) {
            //在已生成的附件关联表中存在，但是在匹配到的所有资料清单中不存在，则删除
            List<Long> sysDocumentListIds = documentLists.stream().map(SysDocumentList::getDocumentId).collect(Collectors.toList());
            for (BpMasterAttachment item : bpMasterAttachments) {
                if (sysDocumentListIds.indexOf(item.getListDocumentId()) < 0) {
                    bpMasterAttachmentService.deleteByPrimaryKey(item);
                }
            }
        }

    }

    @Override
    public void validate(IRequest iRequest, Long documentId) throws NecessaryFileNotUploadException {

        BpMasterAttachment attachment = new BpMasterAttachment();
        attachment.setBpAttachmentCategory(ATTACHMENT_CATEGORY);
        attachment.setBpId(documentId);
        //资料清单里的记录
        List<BpMasterAttachment> bpMasterAttachments = bpMasterAttachmentService.selectSelective(iRequest, attachment);

        for (BpMasterAttachment item : bpMasterAttachments) {
            //对应资料清单记录的信息
            SysDocumentList sysDocumentList = sysDocumentListMapper.selectByPrimaryKey(item.getListDocumentId());

            //如果是必传，则需要校验
            if (BaseConstants.YES.equals(sysDocumentList.getNecessaryUploadFlag())) {
                FndAttachmentMulti condition = new FndAttachmentMulti();
                condition.setTableName(getAttachmentLinkTableName());
                condition.setTablePkValue(String.valueOf(item.getBpAttachmentId()));
                List<FndAttachmentMulti> fndAttachmentMultis = fndAttachmentMultiService.selectSelective(iRequest, condition);
                if (fndAttachmentMultis.size() < 1) {
                    throw new NecessaryFileNotUploadException("缺少" + item.getDocumentName() + "的附件,请上传后再提交审批!");
                }
            }
        }

    }

    @Override
    public String getDocumentCategory() {
        return DOCUMENT_CATEGORY;
    }

    @Override
    public String getAttachmentLinkTableName() {
        return ATM_LINK_TABLE_NAME;
    }

}
