package hls.layout.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.annotation.DocumentHistory;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseCompositeMap;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.fnd.dto.HlsLayoutCodeRuleEngine;
import com.hand.hls.fnd.mapper.HlsLayoutCodeRuleEngineMapper;
import com.hand.hls.history.dto.HlsStandardHistory;
import com.hand.hls.history.mapper.HlsStandardHistoryMapper;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import hls.layout.mapper.ServerLayoutSaveMapper;
import hls.layout.service.IServerLayoutSaveService;
import hls.layout.service.IServerLayoutService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.DocumentChecker;
import leaf.bm.components.RecordHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/8/24
 * Time: 10:13
 */
@Service
@Transactional
public class ServerLayoutSaveServiceImpl implements IServerLayoutSaveService {
    public static final String DYNAMIC_SOURCE_TABLE = "dynamic_source_table";
    public static final String DYNAMIC_SOURCE_TABLE_PK_VALUE = "dynamic_source_table_pk_value";


    @Autowired
    private ServerLayoutSaveMapper serverLayoutSaveMapper;
    @Autowired
    private HlsStandardHistoryMapper hlsStandardHistoryMapper;
    @Autowired
    private IServerLayoutService serverLayoutService;
    @Autowired
    private DocumentChecker documentChecker;
    @Autowired
    private IHLSRuleEngineInitService ihlsRuleEngineInitService;
    @Autowired
    private HlsLayoutCodeRuleEngineMapper hlsLayoutCodeRuleEngineMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    private ThreadLocal<CompositeMap> localParameter = new ThreadLocal<>();
    private ThreadLocal<CompositeMap> localMap = new ThreadLocal<>();
    private ThreadLocal<String> localDynamicBaseTable = new ThreadLocal<>();
    private ThreadLocal<Object> localDynamicBaseTablePkValue = new ThreadLocal<>();

    @Override
    public void fieldValueBackup(Map<String, Object> record) {
        IRequest currentRequest = RequestHelper.getCurrentRequest(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", currentRequest.getUserId());
        params.put("table_name", record.get("table_name"));
        params.put("table_pk_value", record.get("table_pk_value"));
        params.put("column_name", record.get("column_name"));
        params.put("now", new Date(System.currentTimeMillis()));
        serverLayoutSaveMapper.updateHistory(params);
        HlsStandardHistory history = copyHistory(record, currentRequest.getUserId());
        hlsStandardHistoryMapper.insertSelective(history);
    }

    public ResponseData commonSave(CompositeMap map, String baseTable, String queryOnly, String parentTable, String parentBaseTablePk) {
        ResponseData responseData = new ResponseData(true);
        localParameter.set(map);
        localMap.set(new CompositeMap());
        try {
            responseData = recursiveSave(map, baseTable, queryOnly, parentTable, parentBaseTablePk,map);
        } catch (Exception e) {
            responseData.setSuccess(false);
            responseData.setMessage(e.getMessage());
            logger.error("failed to common save" + e);
        } finally {
            localParameter.remove();
        }
        return responseData;
    }

    @Override
    @DocumentHistory(tableName = "base_table", pkField = "base_table", paraIndex = 0, forQuery = false)
    public ResponseCompositeMap commonSave(LeafRequestData data, IRequest requestContext) {
        ResponseCompositeMap responseCompositeMap = new ResponseCompositeMap();
        CompositeMap paramMap = data.toCompositeMap();
        String baseTable = paramMap.getString("base_table");
        String parentTable = paramMap.getString("parent_table");
        String parentBaseTablePk = paramMap.getString("parent_base_table_pk");
        String queryOnly = paramMap.getString("query_only");

        CompositeMap map = new CompositeMap();
        localParameter.set(paramMap);
        localMap.set(map);
        try {
            recursiveSave(paramMap, baseTable, queryOnly, parentTable, parentBaseTablePk,paramMap);
            responseCompositeMap.setSuccess(true);
        } catch (Exception e) {
            logger.debug("Common save error.", e);
            responseCompositeMap.setSuccess(false);
            responseCompositeMap.setMessage(e.getMessage());
        } finally {
            responseCompositeMap.setCompositeMap(paramMap);
            localParameter.remove();
        }
        return responseCompositeMap;
    }

    @Override
    public String commonGet(LeafRequestData data, IRequest requestContext) {
        Map parameter = data.getParameter();
        String layoutCode = null;
        if (parameter == null) {
            return layoutCode;
        } else {
            JSONObject jsonObject = new JSONObject(parameter);
            jsonObject.put("getLayoutCodeFlag","Y");
            HlsLayoutCodeRuleEngine hlsLayoutCodeRuleEngine = new HlsLayoutCodeRuleEngine();
            hlsLayoutCodeRuleEngine.setEnabledFlag("Y");
            List<HlsLayoutCodeRuleEngine> queryList = hlsLayoutCodeRuleEngineMapper.query(hlsLayoutCodeRuleEngine);
            if (queryList != null) {
                for (HlsLayoutCodeRuleEngine item : queryList) {
                    jsonObject.put("ruleEngineId", item.getRuleEngineId());
                    String[] strings = ihlsRuleEngineInitService.ruleEngineInit(requestContext, jsonObject,null);
                    if (strings.length > 0) {
                        layoutCode = strings[0];
                        return layoutCode;
                    }
                }
            }
        }
        return layoutCode;
    }

    @Override
    public String functionGetLayout(String functionCode, IRequest requestContext) {
        String layoutCode = null;
        if (functionCode == null) {
            return layoutCode;
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("functionCode", functionCode);
            HlsLayoutCodeRuleEngine hlsLayoutCodeRuleEngine = new HlsLayoutCodeRuleEngine();
            hlsLayoutCodeRuleEngine.setEnabledFlag("Y");
            hlsLayoutCodeRuleEngine.setLayoutCodeEngineCode("FUNCTION_LAYOUT");
            List<HlsLayoutCodeRuleEngine> queryList = hlsLayoutCodeRuleEngineMapper.query(hlsLayoutCodeRuleEngine);
            if (queryList != null && queryList.size() > 0) {
                HlsLayoutCodeRuleEngine item = queryList.get(0);
                jsonObject.put("ruleEngineId", item.getRuleEngineId());
//                String[] strings = ihlsRuleEngineInitService.ruleEngineInit(requestContext, jsonObject,null);
                HashMap<String, Object> objMapper = new HashMap<>();
                objMapper.put("ruleEngineId",item.getRuleEngineId());
                objMapper.put("functionCode",functionCode);
                List<Long> longs = hlsLayoutCodeRuleEngineMapper.queryFunctionRoute(objMapper);
                if(longs.size()>0){
                    List<String> strings = hlsLayoutCodeRuleEngineMapper.queryFunctionResult(longs.get(0));
                    if (strings.size() > 0) {
                        layoutCode = strings.get(0);
                        return layoutCode;
                    }
                }
            }
        }
        return layoutCode;
    }

    private ResponseData recursiveSave(CompositeMap map, String baseTable, String queryOnly, String parentTable, String parentBaseTablePk,CompositeMap root) {
        return recursiveSave(map, baseTable, queryOnly, parentTable, parentBaseTablePk, null, null, null,root);
    }

    private ResponseData recursiveSave(CompositeMap map, String baseTable, String queryOnly, String parentTable, String parentBaseTablePk, String checkBeforeDelete, String checkAfterSave, String tabCode,CompositeMap root) {
        if (map == null) {
            logger.debug("parameter map is null");
            return new ResponseData(false, "请求参数有误：parameter 参数有误");
        }
        List<CompositeMap> childs = map.getChilds();
        if (CollectionUtils.isEmpty(childs)) {
            logger.debug("parameter children is empty");
            return new ResponseData(false, "请求参数有误：parameter children 为空");
        }
        CompositeMap subTabMap;
        String pkField = null;
        if (StringUtils.isNotBlank(baseTable)) {
            subTabMap = selectTabOrTree(
                    createMap("parent_table", baseTable.toUpperCase(Locale.CHINA), "layout_code", getLayoutCode(), "enabled_flag", "Y"));
            localDynamicBaseTable.set(baseTable);
            List<String> pkFields = RecordHelper.getPkFields(baseTable);
            if (pkFields == null || pkFields.size() != 1) {
                logger.debug("baseTable: {}, pkFields: {}", baseTable, pkFields);
                throw new RuntimeException("pk field error in baseTable: " + baseTable);
            }
            pkField = pkFields.get(0);
        } else {
            subTabMap = selectTabOrTree(
                    createMap("virtual_parent_flag", "Y", "layout_code", getLayoutCode(), "enabled_flag", "Y"));
        }
        List<CompositeMap> subTabs = subTabMap.getChilds();
        if ("Y".equals(queryOnly) && CollectionUtils.isEmpty(subTabs)) {
            logger.debug("return. queryOnly: {}, subTabs: {}", queryOnly, subTabs);
            return new ResponseData(map.getChilds());
        }
        for (int i = 0; i < childs.size(); i++) {
            CompositeMap child = childs.get(i);
            // child代表一条记录
            String status = child.getString("_status");
            if (StringUtils.isNotBlank(baseTable)) {
//                String errorCode = null;
                if (StringUtils.isBlank(parentTable) && StringUtils.isBlank(localDynamicBaseTable.get())) {
                    localDynamicBaseTable.set(baseTable);
                    localDynamicBaseTablePkValue.set(child.get(pkField));
                }
                if ("delete".equals(status)) {
                    if (StringUtils.isBlank(checkBeforeDelete)) {
                        // 删除前需要执行checker
                        CompositeMap result = selectTabOrTree(createMap("tab_code", getTabCode(), "layout_code", getLayoutCode(), "enabled_flag", "Y"));
                        List<CompositeMap> resultChilds = result.getChilds();
                        if (CollectionUtils.isNotEmpty(resultChilds)) {
                            CompositeMap compositeMap = resultChilds.get(0);
                            String checker = compositeMap.getString("check_before_delete_bm");
                            if (StringUtils.isNotBlank(checker)) {
                                documentCheckBeforeDelete(child, checker);
                            }
                        }
                    } else {
                        documentCheckBeforeDelete(child, checkBeforeDelete);
                    }
                }
                try {
                    if (StringUtils.isNoneBlank(parentTable, parentBaseTablePk) && StringUtils.isBlank(child.getString(parentBaseTablePk))) {
                        throw new RuntimeException("HLS_TABLE_UNIQUE.SAVE_PARENT_FIRST");
                    }
                    if (!"Y".equals(queryOnly)) {
                        if (!"insert".equals(status)) {
                            backupField(status, child, baseTable, pkField, parentTable, parentBaseTablePk, getLayoutCode(), tabCode);
                        }

//                      调用对应的 insert / update / delete
                        dealData(status, child, baseTable);
                        if ("insert".equals(status)) {
                            backupField(status, child, baseTable, pkField, parentTable, parentBaseTablePk, getLayoutCode(), tabCode);
                        }
                        if (StringUtils.isNotBlank(checkAfterSave) && i == childs.size() - 1) {
                            documentCheckAfterSave(child, checkAfterSave,root);
                        }
                    }
                } catch (Exception e) {
                    logger.error("在调用{} 语句的时候报错", status, e);
                }

            }

            for (int j = 0; CollectionUtils.isNotEmpty(subTabs) && j < subTabs.size(); j++) {
                recursiveDynamicRepeat(child, subTabs.get(j), pkField, null,root);
            }

        }
        return new ResponseData(map.getChilds());

    }


    private void recursiveDynamicRepeat(CompositeMap child, CompositeMap subTable, String pkField, String bindPrefix,CompositeMap root) {
        if (subTable == null || !subTable.containsKey("base_table")) {
            logger.debug("recursiveDynamicRepeat cannot get base_table");
            return;
        }
        String baseTable = subTable.getString("base_table");
        String tabCode = subTable.getString("tab_code");
        StringBuilder sb = new StringBuilder(StringUtils.isBlank(bindPrefix) ? "" : bindPrefix);
        sb.append(getLayoutCode()).append('_').append(tabCode).append('_').append(baseTable.toLowerCase(Locale.CHINA));
        CompositeMap bind = child.getChild(sb.toString());
        if (bind == null) {
            return;
        }
        List<CompositeMap> childs = bind.getChilds();
        if (CollectionUtils.isEmpty(childs)) {
            logger.debug("recursiveDynamicRepeat childs is empty");
            return;
        }
        for (CompositeMap map : childs) {
            if (StringUtils.isNotBlank(pkField)) {
                map.put(pkField, child.get(pkField));
            }
        }

        recursiveSave(bind,
                baseTable,
                subTable.getString("query_only"),
                subTable.getString("parent_table"),
                subTable.getString("parent_base_table_pk"),
                subTable.getString("check_before_delete_bm"),
                subTable.getString("check_after_save_bm"),
                subTable.getString("tab_code"),
                root);

    }


    private void dealData(String status, CompositeMap data, String tableName) {
        if (status == null || data == null) {
            throw new IllegalArgumentException();
        }
        switch (status) {
            case "insert":
                RecordHelper.insert(tableName, data);
                break;
            case "delete":
                RecordHelper.delete(tableName, data);
                break;
            case "update":
                RecordHelper.update(tableName, data);
            case "select":
            default:
                logger.debug("[{}] operation not support", status);
        }

    }

    private void backupField(String status, CompositeMap child, String baseTable, String pkField, String parentTable, String parentBaseTablePk, String layoutCode, String tabCode) {
        if (child == null || child.size() < 1) {
            logger.debug("backupField skip.");
            return;
        }
        Set<Map.Entry> set = child.entrySet();
        Iterator<Map.Entry> iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry next = iterator.next();
            String key = String.valueOf(next.getKey());
            Object value = next.getValue();
            String bakKey = key + "_bak";
            if ("_status".equals(key) || "_id".equals(key) || !child.containsKey(bakKey)) {
                continue;
            }
            Object oldValue = child.get(bakKey);
            Object parentPkValue = null;
            if (StringUtils.isNotBlank(parentTable)) {
                parentPkValue = child.get(parentBaseTablePk);
            }
            if (value != oldValue && oldValue != null) {
                if ("LAYOUT_FIELD_VALUE_INIT_NULL".equals(oldValue)) {
                    oldValue = "";
                }
                self().fieldValueBackup(
                        createMap(
                                "layout_code", layoutCode,
                                "tab_code", tabCode,
                                "table_name", baseTable,
                                "table_pk_value", child.get(pkField),
                                "parent_table", parentTable,
                                "parent_table_pk_value", parentPkValue,
                                "source_table", localDynamicBaseTable.get(),
                                "source_table_pk_value", localDynamicBaseTablePkValue.get(),
                                "column_name", key,
                                "from_column_value", oldValue,
                                "to_column_value", value,
                                "confirm_flag", "N"
                        ));
            }
        }
    }

    private void documentCheckBeforeDelete(CompositeMap map, String checker) {
        if (documentChecker.isJavaChecker(checker)) {
            documentChecker.check(map, checker,null);
        }
    }

    private void documentCheckAfterSave(CompositeMap map, String checker,CompositeMap root) {
        if (documentChecker.isJavaChecker(checker)) {
            documentChecker.check(map, checker,root);
        }
    }

    private CompositeMap selectTabOrTree(Map<String, Object> params) {
        CompositeMap result;
        String treeCode = getTreeCode();
        if (StringUtils.isNotBlank(treeCode)) {
            result = serverLayoutService.selectLayoutTreeTab(params);
        } else {
            result = serverLayoutService.selectLayoutTab(params);
        }
        if(logger.isTraceEnabled()) {
            logger.trace("query tab layout, treeCode: {}, param: {}", treeCode, params);
            logger.trace("result: {}", result);
        }
        return result;
    }

    private String getLayoutCode() {
        return (String) getLocalValue("layout_code");
    }

    private String getTreeCode() {
        return (String) getLocalValue("tree_code");
    }

    private String getTabCode() {
        return (String) getLocalValue("tab_code");
    }

    private Object getLocalValue(String key) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("key is blank");
        }
        CompositeMap compositeMap = localParameter.get();
        if (compositeMap == null) {
            logger.warn("Cannot get local value for key [{}], because of null localParameter.", key);
            return null;
        }
        return compositeMap.get(key);
    }

    private Map<String, Object> createMap(Object... objs) {
        if (objs == null || objs.length < 1) {
            return new HashMap<>(0);
        }
        if (objs.length % 2 != 0) {
            throw new RuntimeException("parameter should show in pair");
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < objs.length; i++) {
            String key = String.valueOf(objs[i]);
            map.put(key, objs[i + 1]);
            i++;
        }
        return map;
    }

    private HlsStandardHistory copyHistory(Map<String, Object> record, Long userId) {
        HlsStandardHistory history = new HlsStandardHistory();
        Date now = new Date(System.currentTimeMillis());

        history.setTableName(getStringValue(record, "table_name").toUpperCase());
        history.setTablePkValue(getStringValue(record, "table_pk_value"));
        history.setParentTable(getStringValue(record, "parent_table").toUpperCase());
        history.setParentTablePkValue(getStringValue(record, "parent_table_pk_value"));
        history.setSourceTable(getStringValue(record, "source_table").toUpperCase());
        history.setSourceTablePkValue(getStringValue(record, "source_table_pk_value"));
        history.setLayoutCode(getStringValue(record, "layout_code"));
        history.setTabCode(getStringValue(record, "tab_code"));
        history.setColumnName(getStringValue(record, "column_name").toUpperCase());
        history.setFromColumnValue(getStringValue(record, "from_column_value"));
        history.setToColumnValue(getStringValue(record, "to_column_value"));
        history.setOperationDate((Date) record.getOrDefault("operation_date", now));
        history.setConfirmFlag(getStringValue(record, "confirm_flag", "N"));
        history.setCreatedBy(userId);
        history.setCreationDate(now);
        history.setLastUpdateDate(now);
        history.setLastUpdatedBy(userId);

        return history;
    }

    private String getStringValue(Map<String, Object> record, String key) {
        return getStringValue(record, key, null);
    }

    private String getStringValue(Map<String, Object> record, String key, String defaultValue) {
        Object o = record.get(key);
        if (o instanceof String) {
            return (String) o;
        }
        if (o != null) {
            return o.toString();
        }
        return defaultValue;
    }
}
