package com.hand.hls.cont.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.cont.service.ILeaveHistoryService;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import leaf.bm.components.RecordHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * program: leaf-parent
 * description:
 * author: huangtianyang
 * create: 2019-07-26 14:36
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class LeaveHistoryServiceImpl implements ILeaveHistoryService {
    private static final String[] CONTRACT_CHANGE_LN_TABLE_NAMES = {"fnd_atm_attachment", "fnd_atm_attachment_multi"};
    private static final String[] CONTRACT_CHANGE_HD_TABLE_NAMES = {"lon_contract_attachment"};
    private static final String[] MARKETING_REPORT_CHANGE_HD_TABLE_NAMES = {"hls_report_attachment"};
    private static final String LOAN_CONTRACT_CHANGE = "LOAN_CONTRACT_CHANGE";
    private static final String MARKETING_REPORT_CHANGE = "MARKETING_REPORT_CHANGE";
    private static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    private static final String FND_ATM_ATTACHMENT = "fnd_atm_attachment";


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private SysDocumentHistoryDetailMapper documentHistoryDetailMapper;


    @Override
    public boolean beforeLeaveHistory(String documentCategory, String tableName, SysDocumentHistoryDetail detail) {
        if (LOAN_CONTRACT_CHANGE.equals(documentCategory)) {
            if (FND_ATM_ATTACHMENT_MULTI.equals(tableName)) {
                //先删除uuid
                JSONObject jsonObject = JSON.parseObject(detail.getHistoryData());
                jsonObject.remove("attachment_id");
                detail.setHistoryData(jsonObject.toJSONString());
                //documentHistoryDetailMapper.updateByPrimaryKey(detail);
            }
            return !ArrayUtils.contains(CONTRACT_CHANGE_LN_TABLE_NAMES, tableName);
        } else if (MARKETING_REPORT_CHANGE.equals(documentCategory)) {
            if (FND_ATM_ATTACHMENT_MULTI.equals(tableName)) {
                //先删除uuid
                JSONObject jsonObject = JSON.parseObject(detail.getHistoryData());
                jsonObject.remove("attachment_id");
                detail.setHistoryData(jsonObject.toJSONString());
                //documentHistoryDetailMapper.updateByPrimaryKey(detail);
            }
            return !ArrayUtils.contains(CONTRACT_CHANGE_LN_TABLE_NAMES, tableName);
        } else {
            return true;
        }
    }

    @Override
    public void afterLeaveHistory(String documentCategory, String tableName, JSONObject record, List<SysDocumentHistoryDetail> details, SysDocumentHistoryDetail detail) {
        if (LOAN_CONTRACT_CHANGE.equals(documentCategory)
                && ArrayUtils.contains(CONTRACT_CHANGE_HD_TABLE_NAMES, tableName)) {
            switch (tableName) {
                case "lon_contract_attachment":
                    batchUpdateAttachment(record, details, detail);
                    break;
                default:
                    break;
            }
        }else if (MARKETING_REPORT_CHANGE.equals(documentCategory)
                && ArrayUtils.contains(MARKETING_REPORT_CHANGE_HD_TABLE_NAMES, tableName)) {
            switch (tableName) {
                case "hls_report_attachment":
                    batchUpdateReportAttachment(record, details, detail);
                    break;
                default:
                    break;
            }
        }
    }


    private void batchUpdateAttachment(JSONObject record, List<SysDocumentHistoryDetail> details, SysDocumentHistoryDetail detail) {
        Long contractAttachmentId = record.getLong("contract_attachment_id");
        String tablePkValue = detail.getTablePkValue();
        for (SysDocumentHistoryDetail documentHistoryDetail : details) {

            JSONObject attachmentMulti = JSON.parseObject(documentHistoryDetail.getHistoryData());
            if (FND_ATM_ATTACHMENT_MULTI.equals(documentHistoryDetail.getTableName())
                    && detail.getTablePkValue().equals(attachmentMulti.getString("table_pk_value"))) {
                //新增的附件修改tablepkvalue
                if (!NumberUtils.isNumber(detail.getTablePkValue())) {
                    attachmentMulti.remove("attachment_id");
                    attachmentMulti.put("table_pk_value", contractAttachmentId);
                    attachmentMulti = saveHistory(attachmentMulti, documentHistoryDetail);
                }

                Long recordId = attachmentMulti.getLong("record_id");

                JSONObject attachmentObject = null;
                SysDocumentHistoryDetail attachmentDetail = null;
                for (int i = 0; i < details.size(); i++) {
                    SysDocumentHistoryDetail historyDetail = details.get(i);
                    if (FND_ATM_ATTACHMENT.equals(historyDetail.getTableName())) {
                        JSONObject jsonObject = JSON.parseObject(historyDetail.getHistoryData());
                        //修改source_pk_value 为刚刚插入的值
                        if (FND_ATM_ATTACHMENT_MULTI.equals(jsonObject.getString("source_type_code"))
                                && documentHistoryDetail.getTablePkValue().equals(jsonObject.get("source_pk_value"))) {
                            jsonObject.put("source_pk_value", recordId);
                            attachmentObject = jsonObject;
                            attachmentDetail = historyDetail;
                        }
                    }
                }

                if (attachmentObject != null) {
                    attachmentObject = saveHistory(attachmentObject, attachmentDetail);
                    FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
                    fndAttachmentMulti.setRecordId(recordId);
                    fndAttachmentMulti.setAttachmentId(attachmentObject.getLong("attachment_id"));
                    fndAttachmentMultiMapper.updateByPrimaryKeySelective(fndAttachmentMulti);
                }

            }
        }

    }
    private void batchUpdateReportAttachment(JSONObject record, List<SysDocumentHistoryDetail> details, SysDocumentHistoryDetail detail) {
        Long marketingAttachmentId = record.getLong("marketing_attachment_id");
        String tablePkValue = detail.getTablePkValue();
        for (SysDocumentHistoryDetail documentHistoryDetail : details) {

            JSONObject attachmentMulti = JSON.parseObject(documentHistoryDetail.getHistoryData());
            if (FND_ATM_ATTACHMENT_MULTI.equals(documentHistoryDetail.getTableName())
                    && detail.getTablePkValue().equals(attachmentMulti.getString("table_pk_value"))) {
                //新增的附件修改tablepkvalue
                if (!NumberUtils.isNumber(detail.getTablePkValue())) {
                    attachmentMulti.remove("attachment_id");
                    attachmentMulti.put("table_pk_value", marketingAttachmentId);
                    attachmentMulti = saveHistory(attachmentMulti, documentHistoryDetail);
                }

                Long recordId = attachmentMulti.getLong("record_id");

                JSONObject attachmentObject = null;
                SysDocumentHistoryDetail attachmentDetail = null;
                for (int i = 0; i < details.size(); i++) {
                    SysDocumentHistoryDetail historyDetail = details.get(i);
                    if (FND_ATM_ATTACHMENT.equals(historyDetail.getTableName())) {
                        JSONObject jsonObject = JSON.parseObject(historyDetail.getHistoryData());
                        //修改source_pk_value 为刚刚插入的值
                        if (FND_ATM_ATTACHMENT_MULTI.equals(jsonObject.getString("source_type_code"))
                                && documentHistoryDetail.getTablePkValue().equals(jsonObject.get("source_pk_value"))) {
                            jsonObject.put("source_pk_value", recordId);
                            attachmentObject = jsonObject;
                            attachmentDetail = historyDetail;
                        }
                    }
                }

                if (attachmentObject != null) {
                    attachmentObject = saveHistory(attachmentObject, attachmentDetail);
                    FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
                    fndAttachmentMulti.setRecordId(recordId);
                    fndAttachmentMulti.setAttachmentId(attachmentObject.getLong("attachment_id"));
                    fndAttachmentMultiMapper.updateByPrimaryKeySelective(fndAttachmentMulti);
                }

            }
        }

    }

    private JSONObject saveHistory(JSONObject attachmentObject, SysDocumentHistoryDetail attachmentDetail) {
        String tableName = attachmentDetail.getTableName();
        String status = attachmentObject.getString(RecordHelper.STATUS_FIELD);
        if (status == null) {
            status = "OTHER";
        }
        switch (status) {
            case RecordHelper.STATUS_INSERT:
                RecordHelper.insert(tableName, attachmentObject);
                break;
            case RecordHelper.STATUS_DELETE:
                RecordHelper.delete(tableName, attachmentObject);
                break;
            case RecordHelper.STATUS_UPDATE:
                RecordHelper.update(tableName, attachmentObject);
                break;
            default:
                logger.warn("No proper method to execute for status [{}]", attachmentObject.getString(RecordHelper.STATUS_FIELD));
        }
        return attachmentObject;
    }
}
