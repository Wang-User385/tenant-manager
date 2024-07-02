package com.hand.hls.sys.service.impl;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.mapper.HlsSysDocumentHistoryBlobMapper;
import com.hand.hls.bp.mapper.HlsSysDocumentHistoryDetailMapper;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.cont.mapper.HlsCusConContractChangeReqMapper;
import com.hand.hls.cont.mapper.HlsCusContractAttachmentMapper;
import com.hand.hls.cont.service.ILeaveHistoryService;
import com.hand.hls.db.database.*;
import com.hand.hls.db.utils.DBMetadataUtils;
import com.hand.hls.prj.dto.BpMasterChangeReq;
import com.hand.hls.prj.mapper.BpMasterChangeReqMapper;
import com.hand.hls.sys.dto.*;
import com.hand.hls.sys.mapper.*;
import com.hand.hls.sys.service.ISysDocumentHistoryBlobService;
import com.hand.hls.sys.service.ISysDocumentHistoryDetailService;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import leaf.utils.ConfigUtils;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.rmi.NoSuchObjectException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/2/18 11:11
 */
@Service
@Transactional
public class SysDocumentHistoryServiceImpl extends BaseServiceImpl<SysDocumentHistory> implements ISysDocumentHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(SysDocumentHistoryServiceImpl.class);
    private static final String DS_NULL_ERROR = "datasourceId is null.";
    private static final String QUERY_PARAMETER_NULL = "the required parameter for query is null.";
    private static final String HISTORY_ID_NULL_ERROR = "the required parameter historyId for query is null.";
    private static final String HISTORY_ID_INCORRECT_ERROR = "the given historyId is incorrect.";
    private static final String EXT_SQL4JSON_ERROR = "execute sql for json error.";
    public static final String DOCUMENT_VERSION_KEY = "document_version";
    private static final String APPROVED = "APPROVED";
    private static final String[] CHANGE_REQ_STATUS = {"APPROVED"};
    private static final String DOCUMENT_CATEGORY_BP = "HLS_BP_MASTER_CHANGE";
    private static final String DOCUMENT_CATEGORY_CON = "CONTRACT_CHANGE";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("dataSource")
    private DataSource dataSource;

    @Autowired
    private SysDocumentHistoryMapper historyMapper;

    @Autowired
    private ISysDocumentHistoryBlobService sysDocumentHistoryBlobService;
    @Autowired
    private SysDocumentHistoryDetailMapper sysDocumentHistoryDetailMapper;

    @Autowired
    private ISysDocumentHistoryDetailService detailService;

    @Autowired
    private HlsSysDocumentHistoryDetailMapper historyDetailMapper;

    @Autowired
    private HlsSysDocumentHistoryBlobMapper sysDocumentHistoryBlobMapper;

    @Autowired
    private HlsCusConContractChangeReqMapper hlsCusConContractChangeReqMapper;

    @Autowired
    private SqlSessionFactory sessionFactory;

    @Autowired
    private ILeaveHistoryService leaveHistoryService;
    @Autowired
    private HlsCusContractAttachmentMapper conContractAttachmentMapper;

    @Autowired
    private DocumentHistoryDetailDMapper documentHistoryDetailDMapper;
    @Autowired
    private DocumentHistoryBlobDMapper documentHistoryBlobDMapper;
    @Autowired
    private BpMasterChangeReqMapper bpMasterChangeReqMapper;

    @Override
    public List getLastVersionChangeReq(IRequest iRequest, String documentCategory, Long documentId, String tableName, String querySource, Map map) {
        List result = new ArrayList<>();
        if (documentId != null && documentCategory != null && tableName != null) {
            HlsCusConContractChangeReq contractChangeReq = new HlsCusConContractChangeReq();
            //documentId为变更id
            contractChangeReq.setChangeReqId(documentId);
            contractChangeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(contractChangeReq);
            map.put("changeReqId", documentId);
            map.put("contractId", contractChangeReq.getContractId());
            if (contractChangeReq.getStatus() != null) {
                List<SysDocumentHistory> histories = new ArrayList<>();
                SysDocumentHistory history = new SysDocumentHistory();
                history.setDocumentId(documentId);
                if (!APPROVED.equals(contractChangeReq.getStatus())) {
                    //如果当前的状态不是审批通过，找存在的最近的一次变更记录，状态为审批通过的V2版本，如果找不到，则取当前合同信息
                    histories = historyMapper.selectCloseDocumentHistorySecondVersion(history);
                } else {
                    //如果当前状态为审批通过，找存在的最近的一次变更记录，状态为审批通过的V1版本，一定存在
                    histories = historyMapper.selectCloseDocumentHistoryFirstVersion(history);
                }
                if (histories != null && histories.size() > 0) {
                    history = histories.get(0);
                    try {
                        List<JSONObject> list = getHistoryDetails(tableName, history.getHistoryId());
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).get("data") != null) {
                                result.add(list.get(i).get("data"));
                            }
                        }
                    } catch (NoSuchObjectException e) {
                        e.printStackTrace();
                    }
                } else {
                    SqlSession sqlSession = sessionFactory.openSession();
                    result = sqlSession.selectList(querySource, map);
                }
            }
        }
        return result;
    }

    @Override
    public ResponseData save(DocumentHistoryData data, IRequest requestContext, LeafRequestData requestData) {
        ResponseData responseData = new ResponseData(false);
        if (data == null || data.getDocumentId() == null || StringUtils.isEmpty(data.getDocumentCategory())) {
            responseData.setMessage("请求参数有误");
            return responseData;
        }

        String tableName = data.getTableName();
        String tablePkValue = data.getTablePkValue();
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(tablePkValue)) {
            responseData.setMessage("请求参数有误");
            return responseData;
        }
        data.setRecord(JSON.toJSONString(requestData.get("parameter")));
        return getResponseData(data, requestContext, responseData);
    }

    @Override
    public ResponseData save(DocumentHistoryData data, IRequest requestContext) {
        return save(data, true, requestContext);
    }

    @Override
    public ResponseData save(DocumentHistoryData data, boolean insertHistory, IRequest requestContext) {
        ResponseData responseData = new ResponseData(false);
        if (data == null || data.getDocumentId() == null || StringUtils.isEmpty(data.getDocumentCategory())) {
            responseData.setMessage("请求参数有误");
            return responseData;
        }

        String tableName = data.getTableName();
        String tablePkValue = data.getTablePkValue();
        if (StringUtils.isEmpty(tableName) || StringUtils.isEmpty(tablePkValue)) {
            responseData.setMessage("请求参数有误");
            return responseData;
        }
        try {
            //data = getHistoryData(data, insertHistory);
            data = extractLongTextField(data,insertHistory);
        } catch (Exception e) {
            responseData.setMessage("查询数据失败");
            logger.error("query getHistoryData failed,", e);
            return responseData;
        }
        return getResponseData(data, requestContext, responseData, insertHistory);
    }

    @Override
    public List<SysDocumentHistory> selectAllHistory(DocumentHistoryData data, IRequest requestContext) {
        if (data == null || data.getDocumentId() == null || data.getDocumentCategory() == null) {
            return Collections.emptyList();
        }
        Example example = new Example(SysDocumentHistory.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo(SysDocumentHistory.FIELD_DOCUMENT_ID, data.getDocumentId())
                .andEqualTo(SysDocumentHistory.FIELD_DOCUMENT_CATEGORY, data.getDocumentCategory());
        return historyMapper.selectByExample(example);
    }

    @Override
    public DocumentHistoryData selectDocumentHistory(String tableName, Object pkValue) {
        IRequest currentRequest = RequestHelper.getCurrentRequest();
        DocumentHistoryData answer;
        Object version = currentRequest.getAttribute(DOCUMENT_VERSION_KEY);
        if (version == null) {
            answer = historyMapper.selectDocumentHistoryLatest(tableName, pkValue);
        } else {
            answer = historyMapper.selectDocumentHistoryByVersion(tableName, pkValue, version);
        }
        if (answer != null) {
            Example example = new Example(SysDocumentHistoryBlob.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo(SysDocumentHistoryBlob.FIELD_TABLE_NAME, answer.getTableName())
                    .andEqualTo(SysDocumentHistoryBlob.FIELD_TABLE_PK_VALUE, answer.getTablePkValue())
                    .andEqualTo(SysDocumentHistoryBlob.FIELD_HISTORY_DETAIL_ID, answer.getHistoryDetailId());
            List<SysDocumentHistoryBlob> sysDocumentHistoryBlobs = sysDocumentHistoryBlobMapper.selectByExample(example);
            answer.setFieldsByBlobs(sysDocumentHistoryBlobs);
        }
        return answer;
    }

    /**
     * 获取字段数据信息
     */
    private DocumentHistoryData getHistoryData(DocumentHistoryData data) throws Exception {
        return getHistoryData(data, true);
    }

    private DocumentHistoryData getHistoryData(DocumentHistoryData data, boolean fetchDataFromTable) throws Exception {
        Set<String> longTextField = getLongTextField(data);
        Map map;
        if (fetchDataFromTable) {
            List<Map> select = getTableDataByPk(data);
            if (select == null || select.size() < 1) {
                return null;
            }
            map = select.get(0);
        } else {
            map = JSON.parseObject(data.getRecord());
        }
        List<DocumentHistoryData.LongTextField> list = new ArrayList<>();
        if (map != null && longTextField.size() > 0) {
            longTextField.forEach(set -> {
                if (map.containsKey(set)) {
                    Object o = map.get(set);
                    if (o != null) {
                        DocumentHistoryData.LongTextField field = data.new LongTextField(set, o.toString());
                        list.add(field);
                    }
                    map.remove(set);
                }
            });
        }
        data.setRecord(JSON.toJSONString(map));
        data.setFields(list);
        return data;
    }

    private DocumentHistoryData extractLongTextField(DocumentHistoryData data, boolean fetchDataFromTable) throws Exception {
        List<DocumentHistoryData.LongTextField> list = new ArrayList<>();
        Map map = JSON.parseObject(data.getRecord());
        switch (data.getTableName()){
            case "con_contract":
                if (map.containsKey("sap_message")) {
                    Object o = map.get("sap_message");
                    if (o != null) {
                        DocumentHistoryData.LongTextField field = data.new LongTextField("sap_message", o.toString());
                        list.add(field);
                    }
                    map.remove("sap_message");
                }
                break;
            case "prj_quotation":
                if (map.containsKey("sheets")) {
                    Object o = map.get("sheets");
                    if (o != null) {
                        DocumentHistoryData.LongTextField field = data.new LongTextField("sheets", o.toString());
                        list.add(field);
                    }
                    map.remove("sheets");
                }
                break;
        }
        data.setRecord(JSON.toJSONString(map));
        data.setFields(list);
        return data;
    }

    private ResponseData getResponseData(DocumentHistoryData data, IRequest requestContext, ResponseData responseData) {
        return getResponseData(data, requestContext, responseData, true);
    }

    private ResponseData getResponseData(DocumentHistoryData data, IRequest requestContext, ResponseData responseData, boolean insertHistory) {
        Long userId = requestContext.getUserId();
        // insert sys_document_history
        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(data.getDocumentId());
        history.setDocumentCategory(data.getDocumentCategory());
        history.setLastUpdateDate(new Date());
        history.setCreationDate(new Date());
        history.setLastUpdatedBy(userId);
        history.setCreatedBy(userId);
        SysDocumentHistory sysDocumentHistory = historyMapper.selectMaxVersion(history);
        if (sysDocumentHistory == null) {
            history.setVersion(1L);
        } else {
            history.setVersion(sysDocumentHistory.getVersion() + 1);
        }
        if (insertHistory) {
            historyMapper.insertSelective(history);
        } else {
            if (sysDocumentHistory == null) {
                historyMapper.insertSelective(history);
            } else {
                history = sysDocumentHistory;
            }
        }
        //insert sys_document_history_detail
        SysDocumentHistoryDetail documentHistoryDetail = new SysDocumentHistoryDetail();
        List<DocumentHistoryData.LongTextField> fields = data.getFields();

        documentHistoryDetail.setHistoryData(data.getRecord());
        documentHistoryDetail.setTableName(data.getTableName());
        documentHistoryDetail.setTablePkValue(data.getTablePkValue());
        documentHistoryDetail.setHistoryId(history.getHistoryId());
        documentHistoryDetail.setCreatedBy(userId);
        documentHistoryDetail.setLastUpdatedBy(userId);
        documentHistoryDetail.setCreationDate(new Date());
        documentHistoryDetail.setCreatedBy(userId);
        documentHistoryDetail.setParentTableName(data.getParentTableName());
        documentHistoryDetail.setParentPkValue(data.getParentTablePkValue());
        if (CollectionUtils.isNotEmpty(fields)) {
            documentHistoryDetail.setHaveClob("Y");
        }
        detailService.insertSelective(requestContext, documentHistoryDetail);
        for (DocumentHistoryData.LongTextField field : fields) {
            SysDocumentHistoryBlob historyBlob = new SysDocumentHistoryBlob();
            historyBlob.setHistoryDetailId(documentHistoryDetail.getHistoryDetailId());
            historyBlob.setTableName(data.getTableName());
            historyBlob.setTablePkValue(data.getTablePkValue());
            historyBlob.setFieldName(field.getFieldName());
            if("sheets".equals(field.getFieldName())){
                String stringSheets = GzipUtil.atob(field.getFieldValue());
                String unzipSheets = null;
                try {
                    unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
                    String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");
                    historyBlob.setFieldValue(jsonSheets);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                historyBlob.setFieldValue(field.getFieldValue());
            }
            historyBlob.setLastUpdateDate(new Date());
            historyBlob.setCreationDate(new Date());
            historyBlob.setLastUpdatedBy(userId);
            historyBlob.setCreatedBy(userId);
            sysDocumentHistoryBlobService.insertSelective(requestContext, historyBlob);
        }

        List<SysDocumentHistory> histories = new ArrayList<>();
        histories.add(history);
        responseData.setSuccess(true);
        responseData.setRows(histories);
        return responseData;
    }

    private List<Map> getTableDataByPk(DocumentHistoryData data) {
        if (StringUtils.isEmpty(data.getTablePkValue())) return null;
        IntrospectedTable introspectedTable = getIntrospectedTable(data);
        if (introspectedTable == null) return null;
        List<IntrospectedColumn> primaryKeyColumns = introspectedTable.getPrimaryKeyColumns();
        Map param = new HashMap();

        if (data.isMutiPk()) {
            String tablePkValue = data.getTablePkValue();
            Map map = JSON.parseObject(tablePkValue, Map.class);
            param.putAll(map);
        } else {
            //获取主键
            IntrospectedColumn introspectedColumn = primaryKeyColumns.get(0);
            String name = introspectedColumn.getName();
            param.put(name, data.getTablePkValue());
        }
        return RecordHelper.select(data.getTableName(), param);
    }


    private IntrospectedTable getIntrospectedTable(DocumentHistoryData data) {
        SimpleDataSource simpleDataSource;
        if (ConfigUtils.isMySQL()) {
            simpleDataSource = new SimpleDataSource(Dialect.MYSQL, dataSource);
        } else {
            simpleDataSource = new SimpleDataSource(Dialect.ORACLE, dataSource);
        }
        //查询给定表名的元数据
        DBMetadataUtils dbMetadataUtils = new DBMetadataUtils(simpleDataSource);

        List<IntrospectedTable> list = null;
        try (Connection connection = dataSource.getConnection()) {
            String schema = null;
            if (ConfigUtils.isOracle() && connection instanceof DruidPooledConnection) {
                schema = ((DruidPooledConnection) connection).getConnection().getSchema();
            }
            DatabaseConfig config = new DatabaseConfig(null, schema, data.getTableName());
            list = dbMetadataUtils.introspectTables(config);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("failed to get database Metadata", e);
        }
        if (list == null || list.size() < 1) {
            return null;
        }
        return list.get(0);
    }

    private Set<String> getLongTextField(DocumentHistoryData data) {
        Set<String> answer = new HashSet<>();
        IntrospectedTable introspectedTable = getIntrospectedTable(data);
        if (introspectedTable == null) return Collections.emptySet();
        List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
        allColumns.forEach(column -> {
            if (column.isBLOBColumn()) {
                answer.add(column.getName().toLowerCase());
            }
        });
        return answer;
    }

    @Override
    public void createHistory(String documentCategory, Long documentId, List<Map<String, Object>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        String metaKey = "meta";
        for (Map<String, Object> data : datas) {
            Object metaMap = data.get(metaKey);
            if (!(metaMap instanceof Map)) {
                logger.warn("No meta info in data [{}] ", data);
                continue;
            }
            data.remove(metaKey);
            Map<String, String> meta = (Map<String, String>) metaMap;
            String baseTable = meta.get("baseTable");
            String pkField = meta.get("pkField");
            String parentBaseTable = meta.get("parentBaseTable");
            String parentPkValue = meta.get("parentPkValue");
            DocumentHistoryData historyData = new DocumentHistoryData();
            // historyData.setHistoryId(historyId);
            historyData.setRecord(JSON.toJSONString(data));
            historyData.setTableName(baseTable);
            historyData.setTablePkValue(String.valueOf(data.get(pkField)));
            historyData.setDocumentCategory(documentCategory);
            historyData.setDocumentId(documentId);
            historyData.setParentTableName(parentBaseTable);
            historyData.setParentTablePkValue(parentPkValue);
            save(historyData, false, RequestHelper.getCurrentRequest(true));

        }
    }

    @Override
    public Long leaveHistoryWithData(String documentCategory, Long documentId, List<Map<String, Object>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return null;
        }
        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(documentCategory);
        SysDocumentHistory sysDocumentHistory = historyMapper.selectMaxVersion(history);
        if (sysDocumentHistory == null) {
            throw new RuntimeException("Can not get history.");
        }
        Long version = sysDocumentHistory.getVersion();
        sysDocumentHistory.setVersion(sysDocumentHistory.getVersion() + 1);
        sysDocumentHistory.setHistoryId(null);
        historyMapper.insertSelective(sysDocumentHistory);

        String metaKey = "meta";
        for (Map<String, Object> data : datas) {
            Object metaMap = data.get(metaKey);
            if (!(metaMap instanceof Map)) {
                logger.warn("No meta info in data [{}] ", data);
                continue;
            }
            data.remove(metaKey);
            Map<String, String> meta = (Map<String, String>) metaMap;
            String baseTable = meta.get("baseTable");
            String pkField = meta.get("pkField");
            String parentBaseTable = meta.get("parentBaseTable");
            String parentPkValue = meta.get("parentPkValue");
            DocumentHistoryData historyData = new DocumentHistoryData();
            //historyData.setHistoryId(historyId);
            historyData.setRecord(JSON.toJSONString(data));
            historyData.setTableName(baseTable);
            historyData.setTablePkValue(String.valueOf(data.get(pkField)));
            historyData.setDocumentCategory(documentCategory);
            historyData.setDocumentId(documentId);
            historyData.setParentTableName(parentBaseTable);
            historyData.setParentTablePkValue(parentPkValue);
            save(historyData, false, RequestHelper.getCurrentRequest(true));

        }
        return version;
    }

    @Override
    public void leaveHistory(String documentCategory, Long documentId, Long version) {
        boolean isBackup = false;
        if (StringUtils.isEmpty(documentCategory) || documentId == null || version == null) {
            throw new IllegalArgumentException();
        }
        SysDocumentHistory sysDocumentHistory = historyMapper.selectDocumentHistory(documentCategory, documentId, version);
        if (sysDocumentHistory == null) {
            throw new RuntimeException("Can not find target DocumentHistory");
        }
        if (DOCUMENT_CATEGORY_CON.equals(documentCategory)) {
            //获取变更申请状态
            HlsCusConContractChangeReq contractChangeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(documentId);
            if (ArrayUtils.contains(CHANGE_REQ_STATUS,contractChangeReq.getStatus())) {
                isBackup = true;
            }
        } else if (DOCUMENT_CATEGORY_BP.equals(documentCategory)) {
            BpMasterChangeReq bpMasterChangeReq = bpMasterChangeReqMapper.selectByPrimaryKey(documentId);
            if (ArrayUtils.contains(CHANGE_REQ_STATUS,bpMasterChangeReq.getStatus())) {
                isBackup = true;
            }
        }

        SysDocumentHistoryDetail condition = new SysDocumentHistoryDetail();
        condition.setHistoryId(sysDocumentHistory.getHistoryId());
        List<SysDocumentHistoryDetail> details = sysDocumentHistoryDetailMapper.select(condition);
        if(StringUtils.equals("CONTRACT_CHANGE", documentCategory)){
            details.removeIf(item -> StringUtils.equals("hls_bp_master", item.getTableName()));
        }
        for (SysDocumentHistoryDetail detail : details) {
            //保存前勾子
            if (!leaveHistoryService.beforeLeaveHistory(documentCategory, detail.getTableName(), detail)) {
                continue;
            }
            String historyData = detail.getHistoryData();
            String tableName = detail.getTableName();
            JSONObject jsonObject = JSON.parseObject(historyData);
            if(StringUtils.equals("CONTRACT_CHANGE", documentCategory) && StringUtils.equals("con_contract", tableName)){
                Double lease_item_amount = jsonObject.getDouble("lease_item_amount");
                Double sum_lease_item_amount = jsonObject.getDouble("sum_lease_item_amount");
                if(lease_item_amount != null && sum_lease_item_amount != null){
                    jsonObject.put("lease_item_amount", sum_lease_item_amount);
                }
            }
            if ("Y".equals(detail.getHaveClob())) {
                wrapBlobData(jsonObject, detail.getHistoryDetailId(),isBackup);
            }
            String status = jsonObject.getString(RecordHelper.STATUS_FIELD);
            if (status == null) {
                status = "OTHER";
            }
            switch (status) {
                case RecordHelper.STATUS_INSERT:
                    RecordHelper.insert(tableName, jsonObject);
                    break;
                case RecordHelper.STATUS_DELETE:
                    RecordHelper.delete(tableName, jsonObject);
                    break;
                case RecordHelper.STATUS_UPDATE:
                    RecordHelper.update(tableName, jsonObject);
                    break;
                default:
                    logger.warn("No proper method to execute for status [{}]", jsonObject.getString(RecordHelper.STATUS_FIELD));
            }
            //保存后勾子
            leaveHistoryService.afterLeaveHistory(documentCategory, detail.getTableName(), jsonObject, details, detail);
            if(StringUtils.equals("CONTRACT_CHANGE", documentCategory) && StringUtils.equals("con_contract_attachment", detail.getTableName())){
                List<Map<String, Object>> attachmentList = conContractAttachmentMapper.queryAttachmentForContractChange(detail.getTableName().toUpperCase(), detail.getTablePkValue());
                if(CollectionUtils.isNotEmpty(attachmentList)){
                    for (Map<String, Object> attachment : attachmentList) {
                        attachment = toLowerCase(attachment);
                        Map<String, Object> attachmentFile = conContractAttachmentMapper.queryAttachmentFile(Long.valueOf(attachment.get("attachment_id").toString()));
                        attachmentFile = toLowerCase(attachmentFile);
                        attachmentFile.remove("attachment_id");
                        RecordHelper.insert("fnd_atm_attachment", attachmentFile);

                        attachment.remove("record_id");
                        attachment.put("attachment_id", attachmentFile.get("attachment_id"));
                        attachment.put("table_pk_value", jsonObject.get("contract_attachment_id").toString());
                        RecordHelper.insert("fnd_atm_attachment_multi", attachment);
                    }
                }

            }
        }
        //创建好新版本后交换两个历史的版本
        SysDocumentHistory newestVersion = new SysDocumentHistory();
        newestVersion.setDocumentCategory(documentCategory);
        newestVersion.setDocumentId(documentId);
        newestVersion = historyMapper.selectMaxVersion(newestVersion);
        if (Objects.equals(newestVersion.getVersion(), version)) {
            //最新版本和上一版本相同。
            throw new RuntimeException("最新版本和上一版本相同");
        }
        historyMapper.updateHistoryVersion(sysDocumentHistory.getHistoryId(), newestVersion.getVersion());

        historyMapper.updateHistoryVersion(newestVersion.getHistoryId(), version);


    }

    private Map<String, Object> toLowerCase(Map<String, Object> map){
        Map<String, Object> m = new HashMap<>(map.size());
        for(Map.Entry<String, Object> entry : map.entrySet()){
            m.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return m;
    }

    private void wrapBlobData(JSONObject jsonObject, Long historyDetailId,boolean isBackup) {
        if (isBackup) {
            DocumentHistoryBlobD blobD = new DocumentHistoryBlobD();
            blobD.setHistoryDetailId(historyDetailId);
            List<DocumentHistoryBlobD> blobDS = documentHistoryBlobDMapper.select(blobD);
            for (int i = 0; blobDS != null && i < blobDS.size(); i++) {
                jsonObject.put(blobD.getFieldName(), blobD.getFieldValue());
            }
        } else {
            SysDocumentHistoryBlob condition = new SysDocumentHistoryBlob();
            condition.setHistoryDetailId(historyDetailId);
            List<SysDocumentHistoryBlob> blobs = sysDocumentHistoryBlobMapper.select(condition);
            for (SysDocumentHistoryBlob blob : blobs) {
                jsonObject.put(blob.getFieldName(), blob.getFieldValue());
            }
        }
    }

    @Override
    public List<JSONObject> selectDocumentHistoryByTableName(IRequest iRequest, Long documentId, String documentCategory, String tableName, Long version) throws NoSuchObjectException {
        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentCategory(documentCategory);
        history.setDocumentId(documentId);
        history.setVersion(version);
        history = historyMapper.selectOne(history);
        if (history == null) {
            throw new NoSuchObjectException("没有该历史记录");
        }
        Long historyId = history.getHistoryId();

        List<JSONObject> result = getHistoryDetails(tableName, historyId);

        return result;
    }

    private List<JSONObject> getHistoryDetails(String tableName, Long historyId) throws NoSuchObjectException {
        boolean isBackup = false;
        List<SysDocumentHistoryDetail> details = new ArrayList<>();
        List<DocumentHistoryDetailD> detailDS = new ArrayList<>();

        //获取变更历史记录
        SysDocumentHistory history = historyMapper.selectByPrimaryKey(historyId);
        if (DOCUMENT_CATEGORY_CON.equals(history.getDocumentCategory())) {
            //获取变更申请状态
            HlsCusConContractChangeReq contractChangeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(history.getDocumentId());
            if (ArrayUtils.contains(CHANGE_REQ_STATUS,contractChangeReq.getStatus())) {
                isBackup = true;
            }
        } else if (DOCUMENT_CATEGORY_BP.equals(history.getDocumentCategory())) {
            BpMasterChangeReq bpMasterChangeReq = bpMasterChangeReqMapper.selectByPrimaryKey(history.getDocumentId());
            if (ArrayUtils.contains(CHANGE_REQ_STATUS,bpMasterChangeReq.getStatus())) {
                isBackup = true;
            }
        }
        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        detail.setTableName(tableName);
        if (isBackup) {
            DocumentHistoryDetailD detailD = new DocumentHistoryDetailD();
            BeanUtils.copyProperties(detail,detailD);
            detailDS = documentHistoryDetailDMapper.select(detailD);
            for (DocumentHistoryDetailD d : detailDS) {
                SysDocumentHistoryDetail historyDetail = new SysDocumentHistoryDetail();
                BeanUtils.copyProperties(d,historyDetail);
                details.add(historyDetail);
            }
        }else {
            details = historyDetailMapper.select(detail);
        }

        if (CollectionUtils.isEmpty(details)) {
            throw new NoSuchObjectException("没有该历史记录");
        }
        boolean finalIsBackup = isBackup;
        return details.stream().map(t -> {
            String historyData = t.getHistoryData();
            JSONObject json = JSON.parseObject(historyData);
            if ("Y".equals(t.getHaveClob())) {
                wrapBlobData(json, t.getHistoryDetailId(), finalIsBackup);
            }
            JSONObject object = new JSONObject();
            object.put("data", json);
            object.put("id", t.getHistoryDetailId());
            return object;
        }).collect(Collectors.toList());
    }


    @Override
    public List<JSONObject> selectDocumentHistoryByTableName(IRequest iRequest, Long documentId, String documentCategory, String tableName) throws NoSuchObjectException {
        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(documentCategory);
        history = historyMapper.selectMaxVersion(history);
        Long historyId = history.getHistoryId();

        List<JSONObject> result = getHistoryDetails(tableName, historyId);

        return result;
    }

    private List<JSONObject> getHistoryDetails(String tableName, Long historyId, Boolean blobFlag) throws NoSuchObjectException {
        boolean isBackup = false;
        List<SysDocumentHistoryDetail> details = new ArrayList<>();
        List<DocumentHistoryDetailD> detailDS = new ArrayList<>();

        //获取变更历史记录
        SysDocumentHistory history = historyMapper.selectByPrimaryKey(historyId);
        if (DOCUMENT_CATEGORY_CON.equals(history.getDocumentCategory())) {
            //获取变更申请状态
            HlsCusConContractChangeReq contractChangeReq = hlsCusConContractChangeReqMapper.selectByPrimaryKey(history.getDocumentId());
            if (ArrayUtils.contains(CHANGE_REQ_STATUS,contractChangeReq.getStatus())) {
                isBackup = true;
            }
        } else if (DOCUMENT_CATEGORY_BP.equals(history.getDocumentCategory())) {
            BpMasterChangeReq bpMasterChangeReq = bpMasterChangeReqMapper.selectByPrimaryKey(history.getDocumentId());
            if (ArrayUtils.contains(CHANGE_REQ_STATUS,bpMasterChangeReq.getStatus())) {
                isBackup = true;
            }
        }


        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        detail.setTableName(tableName);
        if (isBackup) {
            DocumentHistoryDetailD detailD = new DocumentHistoryDetailD();
            BeanUtils.copyProperties(detail,detailD);
            detailDS = documentHistoryDetailDMapper.select(detailD);
            for (DocumentHistoryDetailD d : detailDS) {
                SysDocumentHistoryDetail historyDetail = new SysDocumentHistoryDetail();
                BeanUtils.copyProperties(d,historyDetail);
                details.add(historyDetail);
            }
        }else {
            details = historyDetailMapper.select(detail);
        }

        if (CollectionUtils.isEmpty(details)) {
            throw new NoSuchObjectException("没有该历史记录");
        }
        boolean finalIsBackup = isBackup;
        return details.stream().map(t -> {
            String historyData = t.getHistoryData();
            JSONObject json = JSON.parseObject(historyData);
            if (blobFlag) {
                wrapBlobData(json, t.getHistoryDetailId(), finalIsBackup);
            }
            JSONObject object = new JSONObject();
            object.put("data", json);
            object.put("id", t.getHistoryDetailId());
            return object;
        }).collect(Collectors.toList());
    }


    @Override
    public List<JSONObject> selectDocumentHistoryByTableName(IRequest iRequest, Long documentId, String documentCategory, String tableName, Boolean blobFlag) throws NoSuchObjectException {
        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(documentId);
        history.setDocumentCategory(documentCategory);
        history = historyMapper.selectMaxVersion(history);
        Long historyId = history.getHistoryId();

        List<JSONObject> result = getHistoryDetails(tableName, historyId, blobFlag);

        return result;
    }


    @Override
    public void updateHistoryDetail(IRequest iRequest, Long documentId, String documentCategory, Long detailId, String record) {
        logger.info("updateHistoryDetail");
        SysDocumentHistoryDetail detail = (SysDocumentHistoryDetail) historyDetailMapper.selectByPrimaryKey(detailId);
        String tableName = detail.getTableName();
        String tablePkValue = detail.getTablePkValue();
        String parentTableName = detail.getParentTableName();
        String parentPkValue = detail.getParentPkValue();
        Long histroyId = detail.getHistoryId();

        historyDetailMapper.deleteByPrimaryKey(detailId);

        if ("Y".equals(detail.getHaveClob())) {
            SysDocumentHistoryBlob blob = new SysDocumentHistoryBlob();
            blob.setHistoryDetailId(detailId);
            sysDocumentHistoryBlobMapper.delete(blob);
        }
        DocumentHistoryData data = new DocumentHistoryData();
        data.setTableName(tableName);
        data.setTablePkValue(tablePkValue);
        data.setParentTableName(parentTableName);
        data.setParentTableName(parentTableName);
        data.setParentTablePkValue(parentPkValue);
        data.setHistoryId(histroyId);
        data.setDocumentId(documentId);
        data.setDocumentCategory(documentCategory);
        data.setRecord(record);
        save(data, false, iRequest);
    }

    @Override
    public void updateHistoryDetail(IRequest iRequest, Long documentId, String documentCategory, Long detailId, String record, boolean blobFlag) {
        logger.info("updateHistoryDetail");
        SysDocumentHistoryDetail detail = (SysDocumentHistoryDetail) historyDetailMapper.selectByPrimaryKey(detailId);
        String tableName = detail.getTableName();
        String tablePkValue = detail.getTablePkValue();
        String parentTableName = detail.getParentTableName();
        String parentPkValue = detail.getParentPkValue();
        Long histroyId = detail.getHistoryId();

        historyDetailMapper.deleteByPrimaryKey(detailId);

        if (blobFlag) {
            SysDocumentHistoryBlob blob = new SysDocumentHistoryBlob();
            blob.setHistoryDetailId(detailId);
            sysDocumentHistoryBlobMapper.delete(blob);
        }
        DocumentHistoryData data = new DocumentHistoryData();
        data.setTableName(tableName);
        data.setTablePkValue(tablePkValue);
        data.setParentTableName(parentTableName);
        data.setParentTableName(parentTableName);
        data.setParentTablePkValue(parentPkValue);
        data.setHistoryId(histroyId);
        data.setDocumentId(documentId);
        data.setDocumentCategory(documentCategory);
        data.setRecord(record);
        save(data, false, iRequest);
    }
}
