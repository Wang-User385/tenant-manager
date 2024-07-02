//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.atm.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.activiti.util.ActivitiSysEventUtils;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.sys.dto.SysDocumentHistory;
import com.hand.hls.sys.dto.SysDocumentHistoryDetail;
import com.hand.hls.sys.mapper.SysDocumentHistoryDetailMapper;
import com.hand.hls.sys.mapper.SysDocumentHistoryMapper;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.utils.JsonUtils;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

@Service
@Transactional
public class FndAttachmentServiceImpl extends BaseServiceImpl<FndAttachment> implements IFndAttachmentService {
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;
    @Autowired
    private SysDocumentHistoryMapper sysDocumentHistoryMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public FndAttachmentServiceImpl() {
    }

    public List<Map> queryAttachment(CompositeMap params, String defaultWhere) {
        return this.fndAttachmentMapper.queryAttachment(params.getChild("parameter"), defaultWhere);
    }

    public Long uploadAttachment(String fileName, String filePath, String sourceType, String pkValue, Long fileSize) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        Date now = new Date(System.currentTimeMillis());
        int index = fileName.lastIndexOf(".");
        String typeCode = null;
        String mineType = null;
        if (index > 0) {
            String ext = fileName.substring(index + 1);
            Map map = this.fndAttachmentMapper.queryExt(ext);
            if (map != null) {
                typeCode = map.getOrDefault("file_type_code", "").toString();
                mineType = map.getOrDefault("mine_type", "").toString();
            }
        }

        this.fndAttachmentMapper.deleteAttachmentMulti(sourceType, pkValue);
        FndAttachment fndAttachment = new FndAttachment();
        fndAttachment.setSourceTypeCode("fnd_atm_attachment_multi");
        fndAttachment.setFileName(fileName);
        fndAttachment.setFilePath(filePath);
        fndAttachment.setMimeType(mineType);
        fndAttachment.setFileSize(fileSize);
        fndAttachment.setFileTypeCode(typeCode);
        fndAttachment.setCreationDate(now);
        fndAttachment.setLastUpdateDate(now);
        fndAttachment.setLastUpdatedBy(iRequest.getUserId());
        fndAttachment.setCreatedBy(iRequest.getUserId());
        this.fndAttachmentMapper.insertSelective(fndAttachment);
        FndAttachmentMulti multi = new FndAttachmentMulti();
        multi.setTableName(sourceType);
        multi.setTablePkValue(pkValue);
            multi.setAttachmentId(fndAttachment.getAttachmentId());
        multi.setCreationDate(now);
        multi.setLastUpdateDate(now);
        multi.setLastUpdatedBy(iRequest.getUserId());
        multi.setCreatedBy(iRequest.getUserId());
        this.fndAttachmentMultiMapper.insertSelective(multi);
        FndAttachment condition = new FndAttachment();
        condition.setAttachmentId(fndAttachment.getAttachmentId());
        condition.setSourcePkValue(multi.getRecordId().toString());
        this.fndAttachmentMapper.updateByPrimaryKeySelective(condition);
        //activitiSysEventUtils.upLoadFileToPlat1(fileName,filePath, String.valueOf(fndAttachment.getAttachmentId()));
        return fndAttachment.getAttachmentId();
    }

    public Long uploadAttachment(CompositeMap parameter) {
        String fileName = parameter.getString("file_name");
        String sourceType = parameter.getString("source_type");
        String pkValue = parameter.getString("pkvalue");
        String fileSizeString = parameter.getString("file_size");
        Long fileSize = fileSizeString == null ? null : Long.valueOf(fileSizeString);
        return this.uploadAttachment(fileName, (String)null, sourceType, pkValue, fileSize);
    }

    public String uploadHistoryAttachment(String fileName, String filePath, String sourceType, String pkValue, Long fileSize, Long documentId, String documentCategory) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
        Object fndAtmAttachmentMultis = new ArrayList();

        try {
            fndAtmAttachmentMultis = this.sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "fnd_atm_attachment_multi");
        } catch (NoSuchObjectException var24) {
            this.logger.warn("list is empty");
        }

        if (CollectionUtils.isNotEmpty((Collection)fndAtmAttachmentMultis)) {
            Iterator var10 = ((List)fndAtmAttachmentMultis).iterator();

            label48:
            while(true) {
                Long id;
                JSONObject data;
                do {
                    do {
                        do {
                            if (!var10.hasNext()) {
                                break label48;
                            }

                            JSONObject fndAtmAttachmentMulti = (JSONObject)var10.next();
                            id = fndAtmAttachmentMulti.getLong("id");
                            data = fndAtmAttachmentMulti.getJSONObject("data");
                        } while(!sourceType.equals(data.getString("source_type")));
                    } while(!pkValue.equals(data.getString("pk_value")));
                } while(data.getString("attachment_id") != null && !data.getLong("attachment_id").equals(0L));

                if (data.getString("_status").equals("insert")) {
                    SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
                    detail.setHistoryDetailId(id);
                    this.sysDocumentHistoryDetailMapper.deleteByPrimaryKey(detail);
                } else {
                    data.put("_status", "delete");
                    this.sysDocumentHistoryService.updateHistoryDetail(iRequest, documentId, documentCategory, id, data.toJSONString());
                }
            }
        }

        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(documentCategory);
        history = this.sysDocumentHistoryMapper.selectMaxVersion(history);
        Long historyId = history.getHistoryId();
        Date now = new Date(System.currentTimeMillis());
        int index = fileName.lastIndexOf(".");
        String typeCode = null;
        String mineType = null;
        if (index > 0) {
            String ext = fileName.substring(index + 1);
            Map map = this.fndAttachmentMapper.queryExt(ext);
            if (map != null) {
                typeCode = map.getOrDefault("file_type_code", "").toString();
                mineType = map.getOrDefault("mine_type", "").toString();
            }
        }

        FndAttachment fndAttachment = new FndAttachment();
        String tablePkValue = UUID.randomUUID().toString();
        fndAttachment.setSourcePkValue(tablePkValue);
        fndAttachment.setSourceTypeCode("fnd_atm_attachment_multi");
        fndAttachment.setFileName(fileName);
        fndAttachment.setFilePath(filePath);
        fndAttachment.setMimeType(mineType);
        fndAttachment.setFileSize(fileSize);
        fndAttachment.setFileTypeCode(typeCode);
        fndAttachment.setCreationDate(now);
        fndAttachment.setLastUpdateDate(now);
        fndAttachment.setLastUpdatedBy(iRequest.getUserId());
        fndAttachment.setCreatedBy(iRequest.getUserId());
        JSONObject jsonObject = JSON.parseObject(JsonUtils.toSnakeJsonString(fndAttachment));
        jsonObject.put("_status", "insert");
        String uuid = UUID.randomUUID().toString();
        SysDocumentHistoryDetail attachment = new SysDocumentHistoryDetail();
        attachment.setTablePkValue(uuid);
        attachment.setTableName("fnd_atm_attachment");
        attachment.setHistoryData(jsonObject.toJSONString());
        attachment.setHaveClob("N");
        attachment.setHistoryId(historyId);
        this.sysDocumentHistoryDetailMapper.insertSelective(attachment);
        FndAttachmentMulti multi = new FndAttachmentMulti();
        multi.setTableName(sourceType);
        multi.setTablePkValue(pkValue);
        multi.setCreationDate(now);
        multi.setLastUpdateDate(now);
        multi.setLastUpdatedBy(iRequest.getUserId());
        multi.setCreatedBy(iRequest.getUserId());
        JSONObject object = JSON.parseObject(JsonUtils.toSnakeJsonString(multi));
        object.put("attachment_id", uuid);
        object.put("_status", "insert");
        SysDocumentHistoryDetail attachmentMulti = new SysDocumentHistoryDetail();
        attachmentMulti.setHistoryId(historyId);
        attachmentMulti.setTablePkValue(tablePkValue);
        attachmentMulti.setTableName("fnd_atm_attachment_multi");
        attachmentMulti.setHistoryData(object.toJSONString());
        attachmentMulti.setHaveClob("N");
        this.sysDocumentHistoryDetailMapper.insertSelective(attachmentMulti);
        return uuid;
    }

    public boolean deleteAttachment(Long attachmentId) {
        if (attachmentId == null) {
            return false;
        } else {
            FndAttachmentMulti multi = new FndAttachmentMulti();
            multi.setAttachmentId(attachmentId);
            this.fndAttachmentMultiMapper.delete(multi);
            this.fndAttachmentMapper.deleteByPrimaryKey(attachmentId);
            return true;
        }
    }

    public boolean deleteHistoryAttachment(Long documentId, String documentCategory, String attachmentId) {
        if (documentId != null && attachmentId != null && !StringUtils.isEmpty(documentCategory)) {
            SysDocumentHistory history = new SysDocumentHistory();
            history.setDocumentCategory(documentCategory);
            history.setDocumentId(documentId);
            history = this.sysDocumentHistoryMapper.selectMaxVersion(history);
            Long historyId = history.getHistoryId();
            SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
            detail.setHistoryId(historyId);
            List<SysDocumentHistoryDetail> sysDocumentHistoryDetails = this.sysDocumentHistoryDetailMapper.select(detail);

            for(int i = 0; i < sysDocumentHistoryDetails.size(); ++i) {
                SysDocumentHistoryDetail sysDocumentHistoryDetail = (SysDocumentHistoryDetail)sysDocumentHistoryDetails.get(i);
                JSONObject jsonObject = JSON.parseObject(sysDocumentHistoryDetail.getHistoryData());
                if ("fnd_atm_attachment_multi".equals(sysDocumentHistoryDetail.getTableName()) && jsonObject.getString("attachment_id").equals(attachmentId)) {
                    this.deleteDetail(sysDocumentHistoryDetail, jsonObject);
                } else if ("fnd_atm_attachment".equals(sysDocumentHistoryDetail.getTableName()) && attachmentId.equals(sysDocumentHistoryDetail.getTablePkValue())) {
                    this.deleteDetail(sysDocumentHistoryDetail, jsonObject);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    private void deleteDetail(SysDocumentHistoryDetail detail, JSONObject jsonObject) {
        if ("insert".equals(jsonObject.getString("_status"))) {
            this.sysDocumentHistoryDetailMapper.deleteByPrimaryKey(detail);
        } else {
            jsonObject.put("_status", "delete");
            detail.setHistoryData(jsonObject.toJSONString());
            this.sysDocumentHistoryDetailMapper.updateByPrimaryKey(detail);
        }

    }

    public void deleteByTypeCodeAndPkValue(String sourceTypeCode, String pkValue) {
        this.fndAttachmentMapper.deleteByTypeCodeAndPkValue(sourceTypeCode, pkValue);
    }

    public void deleteAtmMultiByTypeCodeAndPkValue(String tableName, String tablePkValue) {
        this.fndAttachmentMapper.deleteAtmMultiByTypeCodeAndPkValue(tableName, tablePkValue);
    }

    public List<JSONObject> queryHistoryAttachment(IRequest iRequest, Long documentId, String documentCategory, String tablePkValue, String tableName) {
        List<JSONObject> fndAtmAttachments = null;
        SysDocumentHistory documentHistory = new SysDocumentHistory();
        documentHistory.setDocumentId(documentId);
        documentHistory.setDocumentCategory(documentCategory);
        documentHistory = this.sysDocumentHistoryMapper.selectMaxVersion(documentHistory);
        Long historyId = documentHistory.getHistoryId();
        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        detail.setTableName("fnd_atm_attachment_multi");
        List fndAtmAttachmentMultis = this.sysDocumentHistoryDetailMapper.select(detail);

        try {
            fndAtmAttachments = this.sysDocumentHistoryService.selectDocumentHistoryByTableName(iRequest, documentId, documentCategory, "fnd_atm_attachment");
        } catch (NoSuchObjectException var19) {
            this.logger.debug("list is empty");
        }

        List<JSONObject> results = new ArrayList();
        if (CollectionUtils.isEmpty(fndAtmAttachmentMultis)) {
            return results;
        } else {
            Iterator var12 = fndAtmAttachmentMultis.iterator();

            while(true) {
                String recordId;
                JSONObject jsonObject;
                do {
                    do {
                        do {
                            if (!var12.hasNext()) {
                                return results;
                            }

                            SysDocumentHistoryDetail fndAtmAttachmentMulti = (SysDocumentHistoryDetail)var12.next();
                            recordId = fndAtmAttachmentMulti.getTablePkValue();
                            jsonObject = JSON.parseObject(fndAtmAttachmentMulti.getHistoryData());
                        } while("delete".equals(jsonObject.getString("_status")));
                    } while(!tableName.equals(jsonObject.getString("table_name")));
                } while(!tablePkValue.equals(jsonObject.getString("table_pk_value")));

                jsonObject.put("record_id", recordId);
                Iterator var16 = fndAtmAttachments.iterator();

                while(var16.hasNext()) {
                    JSONObject fndAtmAttachment = (JSONObject)var16.next();
                    JSONObject data = fndAtmAttachment.getJSONObject("data");
                    if (!"delete".equals(fndAtmAttachment.getString("_status")) && recordId.equals(data.getString("source_pk_value"))) {
                        jsonObject.putAll(data);
                    }
                }

                jsonObject.remove("_id");
                jsonObject.remove("_status");
                jsonObject.put("document_id", documentId);
                jsonObject.put("document_category", documentCategory);
                jsonObject.put("status", 1);
                results.add(jsonObject);
            }
        }
    }

    public JSONObject downloadHistoryAttachment(IRequest iRequest, Long documentId, String documentCategory, String attachmentId) {
        if (documentId != null && !StringUtils.isEmpty(documentCategory) && attachmentId != null) {
            SysDocumentHistory history = new SysDocumentHistory();
            history.setDocumentId(documentId);
            history.setDocumentCategory(documentCategory);
            history = this.sysDocumentHistoryMapper.selectMaxVersion(history);
            Long historyId = history.getHistoryId();
            SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
            detail.setHistoryId(historyId);
            detail.setTableName("fnd_atm_attachment");
            List<SysDocumentHistoryDetail> attachments = this.sysDocumentHistoryDetailMapper.select(detail);
            JSONObject jsonObject = null;
            Iterator var10 = attachments.iterator();

            while(var10.hasNext()) {
                SysDocumentHistoryDetail attachment = (SysDocumentHistoryDetail)var10.next();
                if (String.valueOf(attachmentId).equals(attachment.getTablePkValue())) {
                    jsonObject = JSON.parseObject(attachment.getHistoryData());
                    jsonObject.remove("_status");
                    jsonObject.remove("_id");
                    jsonObject.put("attachment_id", String.valueOf(attachmentId));
                }
            }

            return jsonObject;
        } else {
            throw new RuntimeException("参数不全!");
        }
    }
}
