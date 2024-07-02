package com.hand.hls.meta.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.core.web.view.IDGenerator;
import com.hand.hap.dataset.annotation.Dataset;
import com.hand.hap.mybatis.util.StringUtil;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.meta.ModelingException;
import com.hand.hls.meta.dto.Metadata;
import com.hand.hls.meta.dto.MetadataChange;
import com.hand.hls.meta.dto.MetadataItem;
import com.hand.hls.meta.dto.MetadataTable;
import com.hand.hls.meta.mapper.MetadataItemMapper;
import com.hand.hls.meta.mapper.MetadataMapper;
import com.hand.hls.meta.service.*;
import com.hand.hls.utils.SqlUtils;
import leaf.bean.LeafRequestData;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dom4j.dom.DOMElement;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.util.*;

@Service
@Dataset("Metadata")
public class MetadataServiceImpl extends BaseServiceImpl<Metadata> implements IMetadataService {

    public static final String KEY_MAIN_TABLE = "mainTable";
    public static final String KEY_TABLES = "tables";
    public static final String KEY_FIELDS = "fields";
    public static final String KEY_QUERY_FIELDS = "queryFields";
    private static final String FIELD_MODEL_TYPE = "modelType";
    private static final String MODEL_TYPE_SQL = "sql";
    private static final String FIELD_ENCRYPTED_SQL = "encryptedSql";
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Value("${db.type}")
    private String dbType;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IMetadataDriver metadataService;
    @Autowired
    private IMetadataItemService metadataItemService;
    @Autowired
    private MetadataTableService metadataTableService;
    @Autowired
    private MetadataColumnService metadataColumnService;
    @Autowired
    private MetadataRelationService metadataRelationService;
    @Autowired
    private MetadataMapper metadataMapper;
    @Autowired
    private MetadataItemMapper metadataItemMapper;

    @Autowired
    private SqlSessionTemplate template;
    @Autowired
    private SqlSessionFactory factory;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private XMLLanguageDriver xmlLanguageDriver = new XMLLanguageDriver();


    @Override
    public List<?> queries(Map<String, Object> body, int page, int pageSize, String sortname, boolean isDesc) {
        PageHelper.startPage(page, pageSize);
        Metadata metadata = new Metadata();
        metadata.setName((String) body.get("name"));
        metadata.setStatus((String) body.get("status"));
        return select(null, metadata, page, pageSize);
    }

    @Override
    public List<Metadata> mutations(List<Metadata> objs) {
        return null;
    }

    public void apply(List<String> ids, boolean commit) {
        try {
            Metadata metadata = new Metadata();
            for (String id : ids) {
                metadata.setMetaId(id);
                metadata = selectByPrimaryKey(null, metadata);
                if (Metadata.STATUS_COMMITTED.equals(metadata.getStatus())) {
                    continue;
                }
                MetadataItem item = new MetadataItem();
                item.setItemId(metadata.getChangeId());
                item = metadataItemService.selectByPrimaryKey(null, item);
                if (item == null) {
                    continue;
                }
                if (commit) {
                    applyTableChanges(item.getData());
                    metadata.setDataId(metadata.getChangeId());
                    metadata.setLockedBy(null);
                    metadata.setChangeId(null);
                    metadata.setStatus(Metadata.STATUS_COMMITTED);
                    updateByPrimaryKey(null, metadata);
                } else {
                    rollbackTableChanges(item.getData());
                    metadataItemService.deleteByPrimaryKey(item);
                    metadata.setLockedBy(null);
                    metadata.setChangeId(null);
                    metadata.setStatus(Metadata.STATUS_COMMITTED);
                    if (metadata.getDataId() == null) {
                        deleteByPrimaryKey(metadata);
                    } else {
                        updateByPrimaryKey(null, metadata);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void rollbackTableChanges(String data) throws IOException, SQLException {
        List<MetadataChange> changes = objectMapper.readValue(data,
                objectMapper.getTypeFactory().constructCollectionType(List.class, MetadataChange.class));
        Collections.reverse(changes);
        for (MetadataChange change : changes) {
            switch (change.getType()) {
                case CREATE_TABLE:
                    metadataTableService.rollbackCreateTable(change.getTable());
                    break;
                case DELETE_TABLE:
                    metadataTableService.rollbackRemoveTable(change.getTable());
                    break;
                case CREATE_COLUMN:
                    metadataColumnService.rollbackCreateColumn(change.getColumn());
                    break;
                case DELETE_COLUMN:
                    metadataColumnService.rollbackRemoveColumn(change.getColumn());
                    break;
                case CREATE_RELATION:
                    metadataRelationService.rollbackCreateRelation(change.getRelation());
                    break;
                case DELETE_RELATION:
                    metadataRelationService.rollbackRemoveRelation(change.getRelation());
                    break;
            }
        }
    }

    private void applyTableChanges(String data) throws IOException, SQLException {
        List<MetadataChange> changes = objectMapper.readValue(data,
                objectMapper.getTypeFactory().constructCollectionType(List.class, MetadataChange.class));
        for (MetadataChange change : changes) {
            switch (change.getType()) {
                case DELETE_TABLE:
                    metadataTableService.applyRemoveTable(change.getTable());
                    break;
                case DELETE_COLUMN:
                    metadataColumnService.applyRemoveColumn(change.getColumn());
                    break;
                case CREATE_COLUMN:
                    metadataColumnService.applyCreateColumn(change.getColumn());
                    break;
                case CREATE_TABLE:
                    metadataTableService.applyCreateTable(change.getTable());
                    break;
                case DELETE_RELATION:
                    metadataRelationService.applyRemoveRelation(change.getRelation());
                    break;
                case CREATE_RELATION:
                    metadataRelationService.applyCreateRelation(change.getRelation());
                    break;
            }
        }
    }

    @Override
    public Metadata checkLock(MetadataChange change) {
        Metadata metadata = new Metadata();
        switch (change.getType()) {
            case CREATE_RELATION:
            case DELETE_RELATION:
                metadata.setDataType(Metadata.DATA_TYPE_RELATION);
                metadata.setName(change.getRelation().getUniqueName());
                break;
            case DELETE_TABLE:
            case CREATE_COLUMN:
            case CREATE_TABLE:
            case DELETE_COLUMN:
                metadata.setDataType(Metadata.DATA_TYPE_TABLE);
                metadata.setName(change.getTableName());
                break;
        }
        List<Metadata> result = select(null, metadata, 1, 10);
        metadata = result.isEmpty() ? null : result.get(0);
        if (metadata != null && metadata.getLockedBy() != null &&
                !metadata.getLockedBy().equals(RequestHelper.getCurrentRequest().getUserName())) {
            throw new ModelingException("lock check exception", change.getTableName(), metadata.getLockedBy());
        }
        return metadata;
    }

    public void importHistory(List<MetadataChange> changes) throws SQLException {
        for (MetadataChange change : changes) {
            switch (change.getType()) {
                case DELETE_COLUMN:
                    metadataColumnService.removeColumn(change.getColumn());
                    break;
                case CREATE_TABLE:
                    metadataTableService.createTable(change.getTable());
                    break;
                case CREATE_COLUMN:
                    metadataColumnService.createColumn(change.getColumn());
                    break;
                case DELETE_TABLE:
                    metadataTableService.removeTable(change.getTable());
                    break;
                case CREATE_RELATION:
                    metadataRelationService.createRelation(change.getRelation());
                    break;
                case DELETE_RELATION:
                    metadataRelationService.removeRelation(change.getRelation());
                    break;
            }
        }
    }

    public List<MetadataChange> export(String[] ids) throws IOException {
        List<MetadataChange> result = new LinkedList<>();
        for (String id : ids) {
            Metadata metadata = new Metadata();
            metadata.setMetaId(id);
            metadata = selectByPrimaryKey(null, metadata);
            if (metadata == null) {
                continue;
            }
            if (Metadata.STATUS_COMMITTED.equals(metadata.getStatus())) {
                continue;
            }
            MetadataItem metadataItem = new MetadataItem();
            if (metadata.getChangeId() == null) {
                continue;
            }
            metadataItem.setItemId(metadata.getChangeId());
            metadataItem = metadataItemService.selectByPrimaryKey(null, metadataItem);
            if (metadataItem == null) {
                continue;
            }
            metadata.setChange(metadataItem);
            List<MetadataChange> changes = objectMapper.readValue(metadataItem.getData(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MetadataChange.class));
            result.addAll(changes);
        }
        return result;
    }

    /**
     * 添加数据表修改记录
     *
     * @param change
     * @param metadata
     * @return 当删除对象的时候如果前面有添加对象会进行合并返回True，其他情况返回False
     * @throws IOException
     */
    @Override
    public boolean addChange(MetadataChange change, Metadata metadata) throws IOException {
        boolean merged = false;
        MetadataItem metadataItem = null;
        if (metadata != null) {
            if (Metadata.STATUS_CHECKOUT.equals(metadata.getStatus())) {
                metadataItem = new MetadataItem();
                metadataItem.setItemId(metadata.getChangeId());
                metadataItem = metadataItemService.selectByPrimaryKey(null, metadataItem);
            }
        } else {
            metadata = new Metadata();
            switch (change.getType()) {
                case CREATE_RELATION:
                case DELETE_RELATION:
                    metadata.setDataType(Metadata.DATA_TYPE_RELATION);
                    metadata.setName(change.getRelation().getUniqueName());
                    break;
                case DELETE_TABLE:
                case CREATE_COLUMN:
                case CREATE_TABLE:
                case DELETE_COLUMN:
                    metadata.setDataType(Metadata.DATA_TYPE_TABLE);
                    metadata.setName(change.getTableName());
                    break;
            }
            metadata.setStatus(Metadata.STATUS_CHECKOUT);
        }
        if (metadataItem == null) {
            metadataItem = new MetadataItem();
            metadataItem.setData(objectMapper.writeValueAsString(Collections.singleton(change)));
            metadataItem.setDataVersion(1L);
        } else {
            List<MetadataChange> changes = objectMapper.readValue(metadataItem.getData(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, MetadataChange.class));
            Iterator<MetadataChange> changeIterator;
            switch (change.getType()) {
                case DELETE_COLUMN:
                    changeIterator = changes.iterator();
                    while (changeIterator.hasNext()) {
                        MetadataChange current = changeIterator.next();
                        if (current.getType().equals(MetadataChange.Type.CREATE_COLUMN) &&
                                current.getColumn().getColumnName().equals(change.getColumn().getColumnName())) {
                            changeIterator.remove();
                            merged = true;
                            break;
                        }
                    }
                    break;
                case DELETE_TABLE:
                    changeIterator = changes.iterator();
                    while (changeIterator.hasNext()) {
                        MetadataChange current = changeIterator.next();
                        if (current.getType().equals(MetadataChange.Type.CREATE_TABLE)) {
                            changeIterator.remove();
                            merged = true;
                            break;
                        }
                    }
                    break;
                case DELETE_RELATION:
                    changeIterator = changes.iterator();
                    while (changeIterator.hasNext()) {
                        MetadataChange current = changeIterator.next();
                        if (current.getType().equals(MetadataChange.Type.CREATE_RELATION)) {
                            changeIterator.remove();
                            merged = true;
                            break;
                        }
                    }
                    break;
            }
            if (!merged) {
                changes.add(change);
            }
            metadataItem.setData(objectMapper.writeValueAsString(changes));
            metadataItem.setDataVersion(metadataItem.getDataVersion() + 1);
        }
        String changeId = metadataItem.getItemId();
        if (changeId == null) {
            changeId = IDGenerator.getInstance().generate();
        }
        if (metadata.getMetaId() == null) {
            metadata.setMetaId(IDGenerator.getInstance().generate());
            metadata.setDataId(null);
            metadata.setChangeId(changeId);
            metadata.setLockedBy(RequestHelper.getCurrentRequest().getUserName());
            insertSelective(null, metadata);
        } else {
            metadata.setChangeId(changeId);
            metadata.setStatus(Metadata.STATUS_CHECKOUT);
            metadata.setLockedBy(RequestHelper.getCurrentRequest().getUserName());
            updateByPrimaryKeySelective(null, metadata);
        }
        if (metadataItem.getItemId() == null) {
            metadataItem.setItemId(changeId);
            metadataItem.setMetadataId(metadata.getMetaId());
            metadataItemService.insertSelective(null, metadataItem);
        } else {
            metadataItemService.updateByPrimaryKey(null, metadataItem);
        }
        return merged;
    }

    @Override
    public Object removeSelectAction(String metadataId) {
        return DocumentSelectAction.removeStatement(metadataId, template.getConfiguration());
    }

    @Override
    public List queryDataByMetadataId(String metadataId, Map<String, Object> resultMap, Map map, int pageNum, int pageSize) throws IOException {
        if (StringUtils.isEmpty(metadataId)) {
            return Collections.emptyList();
        }
        Metadata metadata = metadataMapper.selectByPrimaryKey(metadataId);
        if (metadata == null) {
            return Collections.emptyList();
        }
        MetadataItem metadataItem = new MetadataItem();
        metadataItem.setMetadataId(metadataId);
        List<MetadataItem> items = metadataItemMapper.select(metadataItem);
        if (items == null || items.size() != 1) {
            return Collections.emptyList();
        }
        metadataItem = items.get(0);
        String data = metadataItem.getData();
        if (StringUtils.isEmpty(data)) {
            return Collections.emptyList();
        }
        resultMap.put("metadata", data);
        JSONObject jsonObject = JSONObject.fromObject(data);
        DocumentSelectAction action = null;
        if (MODEL_TYPE_SQL.equals(jsonObject.get(FIELD_MODEL_TYPE))) {
//            自定义SQL数据模型
            String sql = jsonObject.getString(FIELD_ENCRYPTED_SQL);
            Configuration configuration = template.getConfiguration();
            action = DocumentSelectAction.getInstance(metadataItem.getItemId(), sql, jsonObject.getJSONArray("sqlFields"), configuration);

        } else {
            DOMElement domElement = processDataset(jsonObject);
            String tableName = domElement.getAttribute("table");
            MetadataTable table = metadataTableService.queryTable(tableName);
            Optional<String> key = table.getPrimaryColumns().stream().findFirst();
            if (!key.isPresent()) {
                throw new ModelingException("dataset key notFound: " + tableName);
            }
            Configuration configuration = template.getConfiguration();
            action = DocumentSelectAction.getInstance(metadataItem.getItemId(), domElement, StringUtil.underlineToCamelhump(key.get().toLowerCase()), tableName, null, configuration, metadataTableService);
        }
        SqlSession sqlSession = factory.openSession();
        if (pageNum > 0 && pageSize > 0) {
            PageHelper.startPage(pageNum, pageSize);
        }
        Object invoke = action.invoke(sqlSession, map);
        if (invoke instanceof List) {
            return (List) invoke;
        }
        return Collections.emptyList();

    }

    @Override
    public ResponseData resolveFields(IRequest iRequest, LeafRequestData requestData) {
        ResponseData responseData = new ResponseData(false);
        if (requestData == null || requestData.getParameter() == null) {
            responseData.setMessage("Empty parameter.");
            return responseData;
        }
        Map parameter = requestData.getParameter();
        Object sql = SqlUtils.autoTranslate(parameter.get("sql").toString());//兼容oracle mysql
        if (sql == null) {
            responseData.setMessage("Empty sql content.");
            return responseData;
        }
        PageHelper.startPage(1, 1);
        SqlSession sqlSession = factory.openSession();
        Configuration configuration = sqlSession.getConfiguration();
        String finalSql = new MappedStatement.Builder(configuration,
                "tmp",
                xmlLanguageDriver.createSqlSource(sqlSession.getConfiguration(), "<script>" + sql.toString() + "</script>", null),
                SqlCommandType.SELECT).build().getBoundSql(null).getSql();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(finalSql);
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < columnCount; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("columnName", metaData.getColumnLabel(i + 1));
            String columnTypeName = metaData.getColumnTypeName(i + 1);
            if (columnTypeName == null) {
                columnTypeName = JDBCType.valueOf(metaData.getColumnType(i + 1)).getName();
            }
            map.put("typeName", columnTypeName.toUpperCase(Locale.CHINA));
            map.put("tableName", metaData.getTableName(i + 1));
            map.put("columnSize", metaData.getScale(i + 1));
//            map.put("nullable", "");
            list.add(map);
        }
        responseData.setSuccess(true);
        responseData.setRows(list);
        return responseData;
    }

    @Override
    public List<Metadata> selectFilterRole(IRequest iRequest, Metadata dto) {
        if (iRequest.getRoleId() != null) {
            return metadataMapper.selectFilterRole(dto, iRequest.getRoleId());
        } else {
            return new LinkedList<>();
        }
    }

    public DOMElement processDataset(JSONObject json) throws IOException {
        Set<String> updateColumns = new TreeSet<>();
        String masterTable = null;
        DOMElement datasetXml = new DOMElement("dataset");
        if (json.containsKey("datasetId")) {
            datasetXml.addAttribute("id", json.getString("datasetId"));
        }
        JSONObject backend = json;
        DOMElement selectXml = new DOMElement("select");
        DOMElement tablesXml = new DOMElement("tables");
        for (Object table : backend.getJSONArray("tables").toArray()) {
            if (table instanceof JSONObject) {
                JSONObject tableObject = (JSONObject) table;
                String parentKey = (String) tableObject.get("parentKey");
                JSONObject filter = tableObject.getJSONObject("filter");
                String filterLogic = filter.isNullObject() ? null : (String) filter.get("logic");

                if (parentKey == null) {
                    masterTable = tableObject.getString("key");
                    datasetXml.setAttribute("table", masterTable);
                    datasetXml.setAttribute("filterLogic", filterLogic);
                    if (filter.containsKey("rows")) {
                        for (Object row : filter.getJSONArray("rows")) {
                            if (row instanceof JSONObject) {
                                DOMElement filterXml = new DOMElement("filter");
                                JSONObject object = (JSONObject) row;
                                filterXml.setAttribute("columnName", object.getString("field1"));
                                filterXml.setAttribute("operation", object.getString("operator"));
                                filterXml.setAttribute("type", object.getString("field2_type"));
                                filterXml.setAttribute("value", object.getString("field2"));
//                            FIXME: valueTable取值问题
                                filterXml.setAttribute("valueTable", masterTable);
//                            filterXml.setAttribute("valueTable", (String) object.get("valueTable"));
                                selectXml.add(filterXml);
                            }
                        }
                    }
                } else {
                    DOMElement tableXml = new DOMElement("table");
                    tableXml.setAttribute("parentTable", parentKey);
                    tableXml.setAttribute("filterLogic", filterLogic);
                    tableXml.setAttribute("table", tableObject.getString("key"));
                    tableXml.setAttribute("join", tableObject.getString("joinType"));
                    tableXml.setAttribute("masterColumnName", tableObject.getString("masterColumnName"));
                    tableXml.setAttribute("relationColumnName", tableObject.getString("relationColumnName"));
                    if ((!filter.isNullObject()) && filter.containsKey("rows")) {
                        for (Object row : filter.getJSONArray("rows")) {
                            if (row instanceof JSONObject) {
                                DOMElement filterXml = new DOMElement("filter");
                                JSONObject object = (JSONObject) row;
                                filterXml.setAttribute("columnName", object.getString("field"));
                                filterXml.setAttribute("operation", object.getString("operation"));
                                filterXml.setAttribute("type", object.getString("type"));
                                filterXml.setAttribute("value", object.getString("value"));
                                filterXml.setAttribute("valueTable", (String) object.get("valueTable"));
                                filterXml.setAttribute("table", object.getString("table"));
                                tableXml.add(filterXml);
                            }
                        }
                    }
                    tablesXml.add(tableXml);
                }
            }
        }
        DOMElement fieldsXml = new DOMElement("fields");
        for (Object field : backend.getJSONArray("fields").toArray()) {
            if (field instanceof JSONObject) {
                DOMElement fieldXml = new DOMElement("field");
                String name = ((JSONObject) field).getString("name");
                String tableName = ((JSONObject) field).getString("tableName");
                String columnName = ((JSONObject) field).getString("columnName");
                if (tableName.equals(masterTable)) {
                    updateColumns.add(name);
                }
                fieldXml.setAttribute("name", name);
                fieldXml.setAttribute("columnName", columnName);
                fieldXml.setAttribute("table", tableName);

                fieldsXml.add(fieldXml);
            }
        }
        DOMElement queryFieldsXml = new DOMElement("queryFields");
        for (Object field : backend.getJSONArray("queryFields").toArray()) {
            if (field instanceof JSONObject) {
                DOMElement fieldXml = new DOMElement("field");
                JSONObject object = (JSONObject) field;
                fieldXml.setAttribute("name", object.getString("name"));
                fieldXml.setAttribute("columnName", object.getString("columnName"));
                fieldXml.setAttribute("table", object.getString("tableName"));
                fieldXml.setAttribute("operation", object.getString("sqlOperator"));
                queryFieldsXml.add(fieldXml);
            }
        }
        selectXml.add(tablesXml);
        selectXml.add(fieldsXml);
        selectXml.add(queryFieldsXml);
        DOMElement updateXml = new DOMElement("update");
        updateXml.setAttribute("columns", StringUtils.join(updateColumns, ','));
        DOMElement insertXml = new DOMElement("insert");
        insertXml.setAttribute("columns", StringUtils.join(updateColumns, ','));
        DOMElement deleteXml = new DOMElement("delete");
        datasetXml.add(selectXml);
        datasetXml.add(updateXml);
        datasetXml.add(insertXml);
        datasetXml.add(deleteXml);
        return datasetXml;
//        datasetRepositoryService.updateDataset(datasetXml);
    }

}
