package com.hand.hls.sys.components;

import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.fct.dto.HlsCreditLineAttach;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.fct.mapper.HlsCusHlsCreditLineChanceMapper;
import com.hand.hls.fct.service.IHlsCreditLineAttachService;
import com.hand.hls.fnd.exception.NecessaryFileNotUploadException;
import com.hand.hls.sys.dto.SysDocumentList;
import com.hand.hls.sys.mapper.SysDocumentListMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional(
        rollbackFor = {Exception.class}
)
public class ChanceGenerateStrategy implements GenerateStrategy {
    private static final String DOCUMENT_CATEGORY = "PRJ_CHANCE";
    private static final String ATM_LINK_TABLE_NAME = "hls_credit_line_attach";
    @Autowired
    private SysDocumentListMapper sysDocumentListMapper;
    @Autowired
    private HlsCusHlsCreditLineChanceMapper creditLineChanceMapper;
    @Autowired
    private IHlsCreditLineAttachService creditLineAttachService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;

    public ChanceGenerateStrategy() {
    }

    public void generateDocumentList(IRequest iRequest, Long documentId) throws IllegalArgumentException {
        HlsCusHlsCreditLineChance chance = (HlsCusHlsCreditLineChance) this.creditLineChanceMapper.selectByPrimaryKey(documentId);
        if (chance == null) {
            throw new IllegalArgumentException("立项不存在!");
        } else {
            List<SysDocumentList> documentLists = new ArrayList();
            SysDocumentList condition = new SysDocumentList();
            condition.setDocumentCategory(DOCUMENT_CATEGORY);
            condition.setDocumentType("NORMAL");
            condition.setEnabledFlag("Y");
            List<SysDocumentList> normalDocumentList = this.sysDocumentListMapper.select(condition);
            documentLists.addAll(normalDocumentList);
            HlsCreditLineAttach attachment = new HlsCreditLineAttach();
            attachment.setChanceId(documentId);
            attachment.setSourceType("SYS_DOCUMENT_LIST");
            attachment.setAttachmentCategory("CHANCE_ATT");
            List<HlsCreditLineAttach> chanceAttachs = this.creditLineAttachService.selectSelective(iRequest, attachment);
            List<Long> recentDocumentIds = (List) chanceAttachs.stream().map(HlsCreditLineAttach::getSourceId).collect(Collectors.toList());
            List<SysDocumentList> filterDocumentList = (List) documentLists.stream().filter((o) -> {
                return recentDocumentIds.indexOf(o.getDocumentId()) < 0;
            }).collect(Collectors.toList());
            Iterator var13 = filterDocumentList.iterator();

            HlsCreditLineAttach item;
            while (var13.hasNext()) {
                SysDocumentList item1 = (SysDocumentList) var13.next();
                item = new HlsCreditLineAttach();
                item.setChanceId(documentId);
                item.setAttachmentCategory("CHANCE_ATT");
                item.setSourceType("SYS_DOCUMENT_LIST");
                item.setSourceId(item1.getDocumentId());
                item.setDocumentName(item1.getDocumentListName());
                item.setUploadDate(new Date());
                item.setUploadPerson(Long.toString(iRequest.getUserId()));
                this.creditLineAttachService.insertSelective(iRequest, item);
            }

            if (this.deleteHistoryDocumentListDynamic()) {
                List<Long> sysDocumentListIds = (List) documentLists.stream().map(SysDocumentList::getDocumentId).collect(Collectors.toList());
                Iterator var17 = chanceAttachs.iterator();

                while (var17.hasNext()) {
                    item = (HlsCreditLineAttach) var17.next();
                    if (sysDocumentListIds.indexOf(item.getSourceId()) < 0) {
                        this.creditLineAttachService.deleteByPrimaryKey(item);
                    }
                }
            }


        }
    }

    public void validate(IRequest iRequest, Long documentId) throws NecessaryFileNotUploadException {
        HlsCreditLineAttach attachment = new HlsCreditLineAttach();
        attachment.setSourceType("SYS_DOCUMENT_LIST");
        attachment.setChanceId(documentId);
        List<HlsCreditLineAttach> creditLineAttachs = this.creditLineAttachService.selectSelective(iRequest, attachment);
        Iterator var5 = creditLineAttachs.iterator();

        while (var5.hasNext()) {
            HlsCreditLineAttach item = (HlsCreditLineAttach) var5.next();
            SysDocumentList sysDocumentList = (SysDocumentList) this.sysDocumentListMapper.selectByPrimaryKey(item.getSourceId());
            if ("Y".equals(sysDocumentList.getNecessaryUploadFlag())) {
                FndAttachmentMulti condition = new FndAttachmentMulti();
                condition.setTableName(this.getAttachmentLinkTableName());
                condition.setTablePkValue(String.valueOf(item.getChanceAttachmentId()));
                List<FndAttachmentMulti> fndAttachmentMultis = this.fndAttachmentMultiService.selectSelective(iRequest, condition);
                if (fndAttachmentMultis.size() < 1) {
                    throw new NecessaryFileNotUploadException("缺少" + item.getDocumentName() + "的附件,请上传后再提交审批!");
                }
            }
        }

    }

    public String getDocumentCategory() {
        return "PRJ_CHANCE";
    }

    public String getAttachmentLinkTableName() {
        return "hls_credit_line_attach";
    }
}
