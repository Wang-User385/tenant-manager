package com.hand.hap.core.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.hand.hap.cache.Cache;
import com.hand.hap.cache.CacheDelete;
import com.hand.hap.cache.CacheManager;
import com.hand.hap.cache.CacheSet;
import com.hand.hap.cache.impl.HashStringRedisCache;
import com.hand.hap.cache.impl.HashStringRedisCacheGroup;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.IRequestAware;
import com.hand.hap.core.annotation.DocumentHistory;
import com.hand.hap.core.annotation.StdWho;
import com.hand.hap.mybatis.entity.EntityField;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hap.system.dto.DTOClassInfo;
import com.hand.hap.system.dto.ResponseCompositeMap;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.mapper.HlsCusBpAttachmentMapper;
import com.hand.hls.cont.dto.HlsCusConContractChangeReq;
import com.hand.hls.cont.mapper.HlsCusConContractChangeReqMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.sys.dto.*;
import com.hand.hls.sys.mapper.*;
import com.hand.hls.sys.service.ISysDocumentHistoryService;
import com.hand.hls.utils.JsonUtils;
import hls.layout.service.IServerLayoutService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * service 方法执行通知.
 * <p>
 * 拦截所有 Service 方法执行,处理与 IRequest 相关的参数.
 * <p>
 * service 方法执行时间统计.
 * <p>
 * 从 request 中 copy MDC property 到 真正的 MDC
 *
 * @author shengyang.zhou@hand-china.com
 */
public class ServiceExecutionAdvice implements MethodInterceptor {

    private Logger logger = LoggerFactory.getLogger(ServiceExecutionAdvice.class);

    private static final String DOCUMENT_HISTORY_FLAG_KEY = "document_history_flag";
    private static final String CHANGE_REQ_STATUS = "APPROVED";
    private static final String DOCUMENT_CATEGORY_BP = "HLS_BP_MASTER_CHANGE";
    private static final String DOCUMENT_CATEGORY_CON = "CONTRACT_CHANGE";
    private static final String TABLE_NAME_HLS_BP_ATTACHMENT = "hls_bp_attachment";
    private static final String TABLE_NAME_CON_CONTRACT_ATTACHMENT = "con_contract_attachment";

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private ISysDocumentHistoryService sysDocumentHistoryService;
    @Autowired
    private SysDocumentHistoryMapper documentHistoryMapper;
    @Autowired
    private SysDocumentHistoryDetailMapper documentHistoryDetailMapper;
    @Autowired
    private SysDocumentHistoryBlobMapper documentHistoryBlobMapper;
    @Autowired
    private IServerLayoutService serverLayoutService;
    @Autowired
    private SysDocumentHistoryMapper historyMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsCusConContractChangeReqMapper contractChangeReqMapper;
    @Autowired
    private DocumentHistoryDetailDMapper documentHistoryDetailDMapper;
    @Autowired
    private DocumentHistoryBlobDMapper documentHistoryBlobDMapper;

    @Autowired
    private HlsCusBpAttachmentMapper hlsCusBpAttachmentMapper;
    @Autowired
    private IFndAttachmentService fndAttachmentService;

    private ThreadLocal<CompositeMap> localParameter = new ThreadLocal<>();
    private ThreadLocal<CompositeMap> localMap = new ThreadLocal<>();
    private ThreadLocal<String> localDynamicBaseTable = new ThreadLocal<>();
    private ThreadLocal<Object> localDynamicBaseTablePkValue = new ThreadLocal<>();

    void initMDC(IRequest request) {
        if (request != null) {
            request.getAttributeMap().forEach((key, v) -> {
                if (key.startsWith(IRequest.MDC_PREFIX)) {
                    String mdcProperty = key.substring(IRequest.MDC_PREFIX.length());
                    if (v instanceof Long) {
                        MDC.put(mdcProperty, v.toString());
                    } else {
                        MDC.put(mdcProperty, (String) v);
                    }
                }
            });
        }
    }

    public void before(Method method, Object[] args, Object target) throws Throwable {
        Class<?>[] types = method.getParameterTypes();
        int idx = -1;

        for (int i = 0; i < types.length; ++i) {
            if (IRequest.class.isAssignableFrom(types[i])) {
                idx = i;
                break;
            }
        }
        if (idx == -1) {
            // method has no argument of type IRequest
            return;
        }

        IRequest requestContext = (IRequest) args[idx];
        if (requestContext != null) {
            IRequest preRequestContext = RequestHelper.getCurrentRequest();
            if (preRequestContext == null) {
                RequestHelper.setCurrentRequest(requestContext);
            }
            initMDC(requestContext);
        } else {
            if (logger.isWarnEnabled()) {
                logger.warn("{}'s IRequest argument is null.", method);
            }
            return;
        }
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < types.length; ++i) {
            StdWho who = parameters[i].getAnnotation(StdWho.class);
            if (who != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("enable StdWho for argument {}, type: {}", i, types[i].getName());
                }
            }
            if (args[i] instanceof IRequestAware) {
                ((IRequestAware) args[i]).setRequest(requestContext);
            }
            if (args[i] instanceof BaseDTO) {
                BaseDTO baseDTO = (BaseDTO) args[i];
                autoAssignStdProperty(logger, requestContext, baseDTO, who != null);
            } else if (args[i] instanceof Collection) {
                for (Object o : (Collection) args[i]) {
                    if (args[i] instanceof IRequestAware) {
                        ((IRequestAware) args[i]).setRequest(requestContext);
                    }
                    if (o instanceof BaseDTO) {
                        autoAssignStdProperty(logger, requestContext, (BaseDTO) o, who != null);
                    }
                }
            }
        }
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        Logger serviceLogger = LoggerFactory.getLogger(target.getClass());
        String methodStamp = target + "#" + method.getName();
        Object ret;
        if (logger.isTraceEnabled()) {
            serviceLogger.trace("{}, parameters: {}", methodStamp, Arrays.toString(invocation.getArguments()));
        }
        long ts = System.currentTimeMillis();
        try {
            before(method, invocation.getArguments(), target);
            if (checkDocumentHistory(invocation)) {
                ret = proceedDocumentHistory(method, invocation.getArguments(), target);
            } else {
                ret = invocation.proceed();
            }
            if (logger.isTraceEnabled()) {
                serviceLogger.trace("{}, return value: {}", methodStamp, ret);
            }
            proceedAutoCacheOperation(invocation, ret);
            return ret;
        } finally {
            long timeUsed = System.currentTimeMillis() - ts;
            if (logger.isTraceEnabled()) {
                logger.trace("{}, execution time: {}ms.", methodStamp, timeUsed);
            }
        }
    }

    /**
     * 判断是否是@checkDocumentHistory 修饰
     */
    protected boolean checkDocumentHistory(MethodInvocation invocation) {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        try {
            method = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            return false;
        }
        DocumentHistory annotation = AnnotationUtils.findAnnotation(method, DocumentHistory.class);
        if (annotation == null) return false;
        IRequest currentRequest = RequestHelper.getCurrentRequest();
        if (currentRequest == null) {
            logger.error("execute @documentHistory method failed ,RequestHelper.getCurrentRequest() can not be null when use @DocumentHistory annotation");
            return false;
        }

        Object documentHistoryFlag = currentRequest.getAttribute(DOCUMENT_HISTORY_FLAG_KEY);
        if (documentHistoryFlag == null || !documentHistoryFlag.equals("Y")) {
            logger.error("execute @documentHistory method failed ,document_history_flag can not be null when use @DocumentHistory annotation");
            return false;
        }
        return true;
    }

    /**
     * 历史版本搜索
     */
    private Object proceedDocumentHistory(Method method, Object[] arguments, Object target) {
        DocumentHistory annotation;
        try {
            method = target.getClass().getMethod(method.getName(), method.getParameterTypes());
            annotation = AnnotationUtils.findAnnotation(method, DocumentHistory.class);
        } catch (NoSuchMethodException e) {
            logger.error("execute @documentHistory method failed ,cannot find DocumentHistory annotation");
            return null;
        }
        String tableName = annotation.tableName();
        String pkField = annotation.pkField();
        boolean forQuery = annotation.forQuery();
        int index = annotation.paraIndex();
        if (index >= arguments.length) {
            logger.error("execute @documentHistory method failed ,cannot find paraIndex");
            return null;
        }
        Object argument = arguments[index];
        if (argument == null) {
            logger.error("execute @documentHistory method failed ,cannot find paraIndex");
            return null;
        }
        if (pkField.equals("base_table") && tableName.equals("base_table")) {
            LeafRequestData leafRequestData = (LeafRequestData) argument;
            if (forQuery) {
                return documentHistoryCommonQuery(annotation, leafRequestData);
            } else {
                //说明是动态页面的通用保存，这时应该使用表名去获取//执行通用的保存或者修改
                return documentHistoryCommonSave(annotation, leafRequestData);
            }
        }
        Object pkValue = parsePrimaryKey(argument, pkField);
        if (pkValue == null) {
            logger.error("execute @documentHistory method failed ,cannot get pkValue");
            return null;
        }
        DocumentHistoryData data = sysDocumentHistoryService.selectDocumentHistory(tableName, pkValue);
        if (data == null) {
            return null;
        }
        Class<?> returnType = method.getReturnType();
        return parseDocumentHistory(returnType, data);
    }

    private Object documentHistoryCommonQuery(DocumentHistory annotation, LeafRequestData leafRequestData) {
        boolean isBackup = false;
        List<SysDocumentHistoryDetail> details = new ArrayList<>();
        List<DocumentHistoryDetailD> detailDS = new ArrayList<>();
        ResponseData responseData = new ResponseData(false);

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        Long historyId = getHistoryId(request, leafRequestData);
        Map parameter = leafRequestData.getParameter();
        if (historyId == null) {
            responseData.setMessage("Can not get target historyId.");
            return responseData;
        }
        String documentId = request.getParameter("document_id");
        String documentCategory = request.getParameter("document_category");
        if (DOCUMENT_CATEGORY_CON.equals(documentCategory)) {
            //获取变更申请状态
            HlsCusConContractChangeReq contractChangeReq = contractChangeReqMapper.selectByPrimaryKey(documentId);
            if (CHANGE_REQ_STATUS.equals(contractChangeReq.getStatus())) {
                isBackup = true;
            }
        }
        String layoutCode = request.getParameter("layout_code");
        String tabCode = request.getParameter("tab_code");
        if (StringUtils.isAnyEmpty(layoutCode, tabCode)) {
            responseData.setMessage("Can not get layoutCode or tabCode.");
            return responseData;
        }
        Map<String, Object> map = createMap("layout_code", layoutCode, "tab_code", tabCode);
        CompositeMap tabMap = serverLayoutService.selectLayoutTab(map);
        List<CompositeMap> maps = tabMap.getChilds();
        if (maps == null || maps.size() != 1) {
            responseData.setMessage("Can not get target layout tab.");
            return responseData;
        }
        Map<String, Object> tab = maps.get(0);
        String baseTable = (String) tab.get("base_table");
        String parentTable = (String) tab.get("parent_table");
        String parentTablePkField = (String) tab.get("parent_base_table_pk");

        if (StringUtils.equals("con_lease_item_insurance", baseTable)) {
            parentTable = "con_contract_lease_item";
            parentTablePkField = "con_lease_item_id";
        }

        String pkField = RecordHelper.getPkFields(baseTable).get(0);

        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        if (StringUtils.isNoneEmpty(parentTable, parentTablePkField)) {
//            history_id,parent_table_name,parent_pk_value
            Object pkValue = parameter.get(parentTablePkField);
            if (pkValue == null) {
                throw new RuntimeException("Can not query child data without parent pk value. Try baseTable");
            } else {
                detail.setTableName(baseTable);
                detail.setParentTableName(parentTable);
                detail.setParentPkValue(pkValue.toString());
            }
        } else {
//            history_id,baseTable
            if (StringUtils.isEmpty(baseTable)) {
                responseData.setMessage("Can not query data without base table.");
                return responseData;
            }
            detail.setTableName(baseTable);
        }
        String pageNum = request.getParameter("pagenum");
        String pageSize = request.getParameter("pagesize");
//        if (StringUtils.isNoneEmpty(pageNum, pageSize)) {
//            PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
//        }
        boolean isPreVersion = leafRequestData != null && leafRequestData.getParameter() != null && "Y".equals(leafRequestData.getParameter().get("__preVersion"));
        if (isBackup&&!isPreVersion) {
            DocumentHistoryDetailD detailD = new DocumentHistoryDetailD();
            BeanUtils.copyProperties(detail, detailD);
            detailDS = documentHistoryDetailDMapper.select(detailD);
            for (DocumentHistoryDetailD d : detailDS) {
                SysDocumentHistoryDetail historyDetail = new SysDocumentHistoryDetail();
                BeanUtils.copyProperties(d, historyDetail);
                details.add(historyDetail);
            }
        } else {
            details = documentHistoryDetailMapper.select(detail);
        }

        //List<SysDocumentHistoryDetail> details = documentHistoryDetailMapper.select(detail);
        if (StringUtils.isNoneEmpty(parentTable, parentTablePkField) && CollectionUtils.isEmpty(details) && !StringUtils.equals("con_lease_item_insurance", baseTable)) {
            if (isBackup) {
                DocumentHistoryDetailD detailD = new DocumentHistoryDetailD();
                detailD.setHistoryId(historyId);
                detailD.setTableName(baseTable);
                detailDS = documentHistoryDetailDMapper.select(detailD);
                for (DocumentHistoryDetailD d : detailDS) {
                    SysDocumentHistoryDetail historyDetail = new SysDocumentHistoryDetail();
                    BeanUtils.copyProperties(d, historyDetail);
                    details.add(historyDetail);
                }
            } else {
                detail = new SysDocumentHistoryDetail();
                detail.setHistoryId(historyId);
                detail.setTableName(baseTable);
//            if (StringUtils.isNoneEmpty(pageNum, pageSize)) {
//                PageHelper.startPage(Integer.valueOf(pageNum), Integer.valueOf(pageSize));
//            }
                details = documentHistoryDetailMapper.select(detail);
            }
        }
        boolean finalIsBackup = isBackup;
        List<JSONObject> result = details.stream().filter(item -> {
            String historyData = item.getHistoryData();
            JSONObject json = JSON.parseObject(historyData);
            return !"delete".equals(json.getString("_status"));
        }).map(t -> {
            String historyData = t.getHistoryData();
            JSONObject json = JSON.parseObject(historyData);
            if (pkField != null && !json.containsKey(pkField)) {
                json.put(pkField, t.getTablePkValue());
            }
            json.remove("_status");
            json.remove("_id");
            if ("Y".equals(t.getHaveClob())) {
                if (finalIsBackup) {
                    DocumentHistoryBlobD blobD = new DocumentHistoryBlobD();
                    blobD.setHistoryDetailId(t.getHistoryDetailId());
                    List<DocumentHistoryBlobD> blobDS = documentHistoryBlobDMapper.select(blobD);
                    for (int i = 0; blobDS != null && i < blobDS.size(); i++) {
                        json.put(blobD.getFieldName(), blobD.getFieldValue());
                    }
                } else {
                    SysDocumentHistoryBlob blob = new SysDocumentHistoryBlob();
                    blob.setHistoryDetailId(t.getHistoryDetailId());
                    List<SysDocumentHistoryBlob> blobs = documentHistoryBlobMapper.select(blob);
                    for (int i = 0; blobs != null && i < blobs.size(); i++) {
                        json.put(blob.getFieldName(), blob.getFieldValue());
                    }
                }
            }
            if ("hls_report_attachment".equals(t.getTableName())) {

                SysDocumentHistoryDetail detail2 = new SysDocumentHistoryDetail();
                detail2.setHistoryId(t.getHistoryId());
                detail2.setTableName("hls_report_attachment");

                List<SysDocumentHistoryDetail> detailList = documentHistoryDetailMapper.select(detail2);

                detailList.stream().map(t2 -> {
                    String historyData2 = t2.getHistoryData();
                    JSONObject json2 = JSON.parseObject(historyData2);
                    if (t2.getTablePkValue().equals(t.getTablePkValue())) {
                        List<String> createList = new ArrayList<>();
                        json2.forEach((k, v) -> {
                            try {
                                if (k == "upload_person") {
                                    createList.add(String.valueOf(v));
                                }
                            } catch (Exception e) {
                                logger.error("Key: [{}], value: [{}] transfer to date error.", k, v, e);
                            }
                        });
                        if (createList.size() > 0) {
                            String creationBy = createList.stream().sorted(Comparator.reverseOrder()).findFirst().get();
                            SysUser sysUser = new SysUser();
                            sysUser.setUserId(Long.valueOf(creationBy));
                            sysUser = sysUserMapper.selectUserById(sysUser.getUserId());
                            json.put("upload_person_n", sysUser.getDescription());
                        }
                    }

                    return json2;
                }).collect(Collectors.toList());

            }

            if (TABLE_NAME_CON_CONTRACT_ATTACHMENT.equals(t.getTableName())) {
                CompositeMap parameterMap = new CompositeMap("parameter",parameter);
                // 存在表里的是大写，这里转为大写
                parameterMap.put("table_name",t.getTableName().toUpperCase());
                parameterMap.put("header_id",t.getTablePkValue());
                CompositeMap root = new CompositeMap();
                root.addChild(parameterMap);
                List<Map> attachmentMapList = fndAttachmentService.queryAttachment(root, null);
                List<String> fileNameList = new ArrayList<>();
                List<String>  attachmentIdList = new ArrayList<>();
                attachmentMapList.stream().forEach(e->{
                    fileNameList.add(e.get("file_name").toString());
                    attachmentIdList.add(e.get("attachment_id").toString());
                });
                String fileNames = String.join(",",fileNameList);
                String attachmentId = String.join(",",attachmentIdList);
                json.put("file_names",fileNames);
                json.put("attachment_id",attachmentId);
            }
            // 商业伙伴变更附件查询回显问题
            if(TABLE_NAME_HLS_BP_ATTACHMENT.equals(t.getTableName())){
                // 查询关联文件信息,返回file_name、upload_date、upload_person信息
                List<Map<String, Object>> fileInfoMapList = hlsCusBpAttachmentMapper.queryFileInfoByTablePkValue(t.getTablePkValue());
                if (null != fileInfoMapList && 0 < fileInfoMapList.size()) {
                    // 放入map当中
                    Map<String, Object> fileInfoMap = fileInfoMapList.get(0);
                    for (String key : fileInfoMap.keySet()) {
                        json.put(key,fileInfoMap.get(key));
                    }
                }
            }
            //有date 才会认为是日期
            json.forEach((k, v) -> {
                try {
                    if (k != null && k.contains("date") && StringUtils.isNumeric(v.toString())) {
                        json.put(k, new Date(Long.valueOf(v.toString())));
                    }
                } catch (Exception e) {
                    logger.error("Key: [{}], value: [{}] transfer to date error.", k, v, e);
                }
            });
            return json;
        }).collect(Collectors.toList());
        String queryConditionKey1 = (String) leafRequestData.getParameter().get("query_condition_key1");
        String queryConditionValue1 = (String) leafRequestData.getParameter().get("query_condition_value1");
        String queryConditionKey2 = (String) leafRequestData.getParameter().get("query_condition_key2");
        String queryConditionValue2 = (String) leafRequestData.getParameter().get("query_condition_value2");
        String queryConditionKey3 = (String) leafRequestData.getParameter().get("query_condition_key3");
        String queryConditionValue3 = (String) leafRequestData.getParameter().get("query_condition_value3");
        List<JSONObject> detailArrayList = new ArrayList<>();
        if (queryConditionKey1 != null || queryConditionKey2 != null || queryConditionKey3 != null) {
            for (JSONObject item : result) {
                if (queryConditionKey1 != null) {
                    if (queryConditionValue1 == null && item.getString(queryConditionKey1) == null) {
                        detailArrayList.add(item);
                    } else if (queryConditionValue1.equals(item.getString(queryConditionKey1))) {
                        detailArrayList.add(item);
                    }
                }
                if (queryConditionKey2 != null) {
                    if (queryConditionValue2 == null && item.getString(queryConditionKey2) == null) {
                        detailArrayList.add(item);
                    } else if (queryConditionValue2.equals(item.getString(queryConditionKey2))) {
                        detailArrayList.add(item);
                    }
                }
                if (queryConditionKey3 != null) {
                    if (queryConditionValue3 == null && item.getString(queryConditionKey3) == null) {
                        detailArrayList.add(item);
                    } else if (queryConditionValue3.equals(item.getString(queryConditionKey3))) {
                        detailArrayList.add(item);
                    }
                }
            }
        } else {
            detailArrayList = result;
        }
        responseData.setRows(detailArrayList);
        if (details instanceof Page) {
            responseData.setTotal(((Page) details).getTotal());
        }
        responseData.setSuccess(true);
        return responseData;
    }

    private Long getHistoryId(HttpServletRequest request, LeafRequestData leafRequestData) {
        String documentCategory = request.getParameter("document_category");
        String documentId = request.getParameter("document_id");
        if (StringUtils.isAnyEmpty(documentCategory, documentId)) {
            return null;
        }

        SysDocumentHistory history = new SysDocumentHistory();
        history.setDocumentId(Long.valueOf(documentId));
        history.setDocumentCategory(documentCategory);

        boolean isPreVersion = leafRequestData != null && leafRequestData.getParameter() != null && "Y".equals(leafRequestData.getParameter().get("__preVersion"));
        SysDocumentHistory sysDocumentHistory = null;

        if (isPreVersion) {
            List<SysDocumentHistory> histories = historyMapper.selectHistoryCount(history);
            if (CollectionUtils.isEmpty(histories)) {
                return null;
            }
            SysDocumentHistory max = histories.get(0);
            if (histories.size() == 1) {
                return max.getHistoryId();
            }
            SysDocumentHistory second = max;
            Long maxVersion = max.getVersion();
            for (int i = 1; i < histories.size(); i++) {
                SysDocumentHistory item = histories.get(i);
                if (item.getVersion() > max.getVersion()) {
                    second = max;
                    max = item;
                }
            }
            if (maxVersion.equals(second.getVersion())) {
                for (int i = 1; i < histories.size(); i++) {
                    SysDocumentHistory item = histories.get(i);
                    if (item.getVersion().equals(maxVersion - 1)) {
                        second = item;
                    }
                }
            }
            return second.getHistoryId();
        } else {
            sysDocumentHistory = historyMapper.selectMaxVersion(history);
            if (sysDocumentHistory == null || sysDocumentHistory.getHistoryId() == null) {
                return null;
            }
            return sysDocumentHistory.getHistoryId();
        }


    }

    /**
     * 动态页面，对DocumentHistory通用的保存
     */
    private Object documentHistoryCommonSave(DocumentHistory annotation, LeafRequestData leafRequestData) {
        ResponseCompositeMap resultMap = new ResponseCompositeMap();
        resultMap.setSuccess(false);
        String status = leafRequestData.getStatus();
        if (StringUtils.isEmpty(status)) {
            return resultMap;
        }
        CompositeMap map = leafRequestData.toCompositeMap();
        String baseTable = map.getString("base_table");
        String queryOnly = map.getString("query_only");
        String tabCode = map.getString("tab_code");
        localParameter.set(map);
        localMap.set(new CompositeMap());

        try {
            recursiveSave(map, baseTable, queryOnly, null, null, tabCode);
        } catch (Exception e) {
            resultMap.setMessage(e.getMessage());
            return resultMap;
        } finally {
            localParameter.remove();
            localMap.remove();
            localDynamicBaseTable.remove();
            localDynamicBaseTablePkValue.remove();
        }

//        String tableName = annotation.tableName();
//
//        List<String> pkFields = RecordHelper.getPkFields(tableName);
//        if (pkFields == null || pkFields.size() == 0) {
//            return compositeMap;
//        }
        resultMap.setSuccess(true);
        resultMap.setCompositeMap(map);
        return resultMap;
    }

    private boolean recursiveSave(CompositeMap map, String baseTable, String queryOnly, String parentTable, String parentBaseTablePk, String tabCode) {
        List<CompositeMap> children = map.getChilds();
        if (CollectionUtils.isEmpty(children)) {
            logger.debug("parameter children is empty");
            return true;
        }

        String layoutCode = getLayoutCode();
        CompositeMap subTabMap;
        String pkField = null;
        if (StringUtils.isNotBlank(baseTable)) {
            subTabMap = selectTabOrTree(
                    createMap("parent_table", baseTable.toUpperCase(Locale.CHINA), "layout_code", layoutCode, "enabled_flag", "Y"));
            localDynamicBaseTable.set(baseTable);
            List<String> pkFields = RecordHelper.getPkFields(baseTable);
            if (pkFields == null || pkFields.size() != 1) {
                throw new RuntimeException("pk field error in baseTable: " + baseTable);
            }
            pkField = pkFields.get(0);
        } else {
            subTabMap = selectTabOrTree(
                    createMap("virtual_parent_flag", "Y", "layout_code", layoutCode, "enabled_flag", "Y"));
        }
        List<CompositeMap> subTabs = subTabMap.getChilds();
        if ("Y".equals(queryOnly) && CollectionUtils.isEmpty(subTabs)) {
            logger.debug("return. queryOnly: {}, subTabs: {}", queryOnly, subTabs);
            return true;
        }

        String documentCategory = getDocumentCategory();
        Long documentId = getDocumentId();
        Long version = getVersion();
        SysDocumentHistory sysDocumentHistory = documentHistoryMapper.selectDocumentHistory(documentCategory, documentId, version);
        if (sysDocumentHistory == null || sysDocumentHistory.getHistoryId() == null) {
            throw new RuntimeException("Can not find target history data.");
        }
        Long historyId = sysDocumentHistory.getHistoryId();

        for (CompositeMap child : children) {
            String status = child.getString("_status");
            if (StringUtils.isNotBlank(baseTable)) {
                if (StringUtils.isBlank(parentTable) && StringUtils.isBlank(localDynamicBaseTable.get())) {
                    localDynamicBaseTable.set(baseTable);
                    localDynamicBaseTablePkValue.set(child.get(pkField));
                }
                if (StringUtils.isNoneBlank(parentTable, parentBaseTablePk) && StringUtils.isBlank(child.getString(parentBaseTablePk))) {
                    throw new RuntimeException("HLS_TABLE_UNIQUE.SAVE_PARENT_FIRST");
                }
                if ("Y".equals(queryOnly)) {
                    continue;
                }
                switch (status) {
                    case "insert":
                        createDetail(child, historyId, baseTable, pkField, parentTable, parentBaseTablePk);
                        break;
                    case "delete":
                        deleteDetail(child, historyId, baseTable, pkField, parentTable, parentBaseTablePk);
                        break;
                    case "update":
                        updateDetail(child, historyId, baseTable, pkField, parentTable, parentBaseTablePk);
                        break;
                    default:
                        logger.warn("Operation [{}] not support", status);
                }

            }

            for (int j = 0; CollectionUtils.isNotEmpty(subTabs) && j < subTabs.size(); j++) {
                recursiveDynamicRepeat(child, subTabs.get(j), pkField);

            }
        }
        return false;
    }

    private int updateDetail(CompositeMap child, Long historyId, String baseTable, String pkField, String parentTable, String parentBaseTablePk) {
        if (StringUtils.isAnyEmpty(baseTable, pkField) || historyId == null) {
            throw new IllegalArgumentException();
        }
        Object pk;
        pk = child.get(pkField);
        if (pk == null) {
//            logger.warn("Can not update record without pk value");
//            pk = child.get("id");
            throw new RuntimeException("Can not update record without pk value");
        }
        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        detail.setTableName(baseTable);
        detail.setTablePkValue(pk.toString());
        detail = documentHistoryDetailMapper.selectOne(detail);
        String historyData = detail.getHistoryData();
        boolean isNew = false;
        if (StringUtils.isNotEmpty(historyData)) {
            JSONObject jsonObject = JSON.parseObject(historyData);
            if ("insert".equals(jsonObject.getString("_status"))) {
                isNew = true;
            }
        }
        documentHistoryDetailMapper.deleteByPrimaryKey(detail.getHistoryDetailId());
        if ("Y".equals(detail.getHaveClob())) {
            SysDocumentHistoryBlob blob = new SysDocumentHistoryBlob();
            blob.setHistoryDetailId(detail.getHistoryDetailId());
            documentHistoryBlobMapper.delete(blob);
        }
        createDetail(child, historyId, baseTable, pkField, parentTable, parentBaseTablePk, isNew ? RecordHelper.STATUS_INSERT : RecordHelper.STATUS_UPDATE);
        return 0;
    }

    private void createDetail(CompositeMap child, Long historyId, String baseTable, String pkField, String parentTable, String parentBaseTablePk) {
        if (StringUtils.isAnyEmpty(baseTable, pkField) || historyId == null) {
            throw new IllegalArgumentException();
        }
        if(StringUtils.equals("con_lease_item_insurance", baseTable) && StringUtils.equals("CONTRACT_CHANGE", getDocumentCategory())){
            parentTable = "con_contract_lease_item";
            parentBaseTablePk = "con_lease_item_id";
        }

        DocumentHistoryData data = new DocumentHistoryData();
        data.setDocumentCategory(getDocumentCategory());
        data.setDocumentId(getDocumentId());
        data.setHistoryId(historyId);
        data.setTableName(baseTable);
        if (StringUtils.isNoneEmpty(parentBaseTablePk, parentTable)) {
            data.setParentTableName(parentTable);
            data.setParentTablePkValue(child.getString(parentBaseTablePk));
        }
        Object pk = child.get(pkField);
        String tablePkValue;
        if(StringUtils.equals("CONT302F3", getLayoutCode()) && StringUtils.equals("con_contract_lease_item", baseTable)){
            tablePkValue = pk == null ? conContractMapper.queryNextId().toString() : pk.toString();
            child.put("contract_lease_item_id", tablePkValue);
        }else{
            tablePkValue = pk == null ? UUID.randomUUID().toString() : pk.toString();
        }
        data.setTablePkValue(tablePkValue);
        child.put(RecordHelper.STATUS_FIELD, RecordHelper.STATUS_INSERT);
        data.setRecord(JSON.toJSONString(child));
        ResponseData result = sysDocumentHistoryService.save(data, false, RequestHelper.getCurrentRequest(true));
        child.remove(RecordHelper.STATUS_FIELD);
        child.put(pkField, tablePkValue);
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
    }

    private void createDetail(CompositeMap child, Long historyId, String baseTable, String pkField, String parentTable, String parentBaseTablePk, String status) {
        if (StringUtils.isAnyEmpty(baseTable, pkField) || historyId == null) {
            throw new IllegalArgumentException();
        }

        if(StringUtils.equals("con_lease_item_insurance", baseTable) && StringUtils.equals("CONTRACT_CHANGE", getDocumentCategory())){
            parentTable = "con_contract_lease_item";
            parentBaseTablePk = "con_lease_item_id";
        }

        DocumentHistoryData data = new DocumentHistoryData();
        data.setDocumentCategory(getDocumentCategory());
        data.setDocumentId(getDocumentId());
        data.setHistoryId(historyId);
        data.setTableName(baseTable);
        if (StringUtils.isNoneEmpty(parentBaseTablePk, parentTable)) {
            data.setParentTableName(parentTable);
            data.setParentTablePkValue(child.getString(parentBaseTablePk));
        }
        Object pk = child.get(pkField);
        String tablePkValue = pk == null ? UUID.randomUUID().toString() : pk.toString();
        data.setTablePkValue(tablePkValue);
        child.put(RecordHelper.STATUS_FIELD, status);
        if (Objects.equals(status, RecordHelper.STATUS_INSERT)) {
            child.remove(pkField);
        }
        data.setRecord(JSON.toJSONString(child));
        ResponseData result = sysDocumentHistoryService.save(data, false, RequestHelper.getCurrentRequest(true));
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getMessage());
        }
    }

    private int deleteDetail(CompositeMap child, Long historyId, String baseTable, String pkField, String parentTable, String parentBaseTablePk) {
        if (StringUtils.isAnyEmpty(baseTable, pkField) || historyId == null) {
            throw new IllegalArgumentException();
        }
        Object pk;
        pk = child.get(pkField);
        if (pk == null) {
//            logger.warn("Can not delete record without pk value");
            throw new RuntimeException("Can not delete record without pk value");
//            pk = child.get("id");
        }
        SysDocumentHistoryDetail detail = new SysDocumentHistoryDetail();
        detail.setHistoryId(historyId);
        detail.setTableName(baseTable);
        detail.setTablePkValue(pk.toString());
        detail = documentHistoryDetailMapper.selectOne(detail);
        String historyData = detail.getHistoryData();
        JSONObject json = JSON.parseObject(historyData);
        if (RecordHelper.STATUS_INSERT.equals(json.getString(RecordHelper.STATUS_FIELD))) {
            documentHistoryDetailMapper.deleteByPrimaryKey(detail.getHistoryDetailId());
        }
        json.put(RecordHelper.STATUS_FIELD, RecordHelper.STATUS_DELETE);
        detail.setHistoryData(json.toJSONString());
        return documentHistoryDetailMapper.updateByPrimaryKey(detail);
    }

    private void recursiveDynamicRepeat(CompositeMap child, CompositeMap subTable, String pkField) {
        if (subTable == null || !subTable.containsKey("base_table")) {
            logger.debug("RecursiveDynamicRepeat cannot get base_table");
            return;
        }
        String baseTable = subTable.getString("base_table");
        String tabCode = subTable.getString("tab_code");
        CompositeMap bind = child.getChild(getLayoutCode() + '_' + tabCode + '_' + baseTable.toLowerCase(Locale.CHINA));
        if (bind == null) {
            return;
        }
        List<CompositeMap> childs = bind.getChilds();
        if (CollectionUtils.isEmpty(childs)) {
            logger.debug("RecursiveDynamicRepeat childs is empty");
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
                subTable.getString("tab_code"));

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

    private String getDocumentCategory() {
        return (String) getLocalValue("document_category");
    }

    private Long getDocumentId() {
        Object document_id = getLocalValue("document_id");
        if (document_id != null) {
            return Long.valueOf(document_id.toString());
        }
        return null;
    }

    private Long getVersion() {
        return (Long) getLocalValue("version");
    }

    private Object getLocalValue(String key) {
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("key is blank");
        }
        CompositeMap compositeMap = localParameter.get();
        if (compositeMap == null) {
            logger.warn("Can not get local value for key [{}], because of null localParameter.", key);
            return null;
        }
        return compositeMap.get(key);
    }

    private CompositeMap selectTabOrTree(Map<String, Object> params) {
        CompositeMap result;
        String treeCode = getTreeCode();
        if (StringUtils.isNotBlank(treeCode)) {
            result = serverLayoutService.selectLayoutTreeTab(params);
        } else {
            result = serverLayoutService.selectLayoutTab(params);
        }
        return result;
    }

    private Map<String, Object> createMap(Object... objs) {
        if (objs == null || objs.length < 1) {
            return new HashMap<>(0);
        }
        if (objs.length % 2 != 0) {
            throw new RuntimeException("Parameter should show in pair");
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < objs.length; i++) {
            String key = String.valueOf(objs[i]);
            map.put(key, objs[i + 1]);
            i++;
        }
        return map;
    }


    private Object parsePrimaryKey(Object argument, String pkField) {
        if (argument == null) return null;
        Object answer;
        //获取PkValue
        if (argument instanceof Map) {
            Map a = (Map) argument;
            answer = a.get(pkField);
        } else if (argument instanceof String) {
            String str = (String) argument;
            // id 或者JSON
            if (JsonUtils.isJson(str)) {
                JSONObject pkValue1 = JSONObject.parseObject(str);
                answer = pkValue1.getString(pkField);
            } else {
                answer = str;
            }
        } else {
            String propertiesName = DTOClassInfo.underLineToCamel(pkField);
            try {
                answer = PropertyUtils.getProperty(argument, propertiesName);
            } catch (Exception e) {
                logger.error("execute @documentHistory method failed ,cannot get id by method " + propertiesName);
                return null;
            }
        }
        return answer;
    }

    /**
     * 将DocumentHistoryData转化为指定的Object对象
     *
     * @param clazz 转化前的对象
     * @param data  数据
     * @return 转化后的对象
     */
    private Object parseDocumentHistory(Class<?> clazz, DocumentHistoryData data) {
        Object answer;
        try {
            answer = clazz.newInstance();
        } catch (Exception e) {
            logger.error("error when creating new instance of returnType", e);
            return null;
        }
        String record = data.getRecord();
        if (!StringUtils.isEmpty(record)) {
            JSONObject json = JSONObject.parseObject(record);
            answer = JSON.toJavaObject(json, answer.getClass());
        }
        List<DocumentHistoryData.LongTextField> fields = data.getFields();
        for (DocumentHistoryData.LongTextField field : fields) {
            try {
                String fieldName = field.getFieldName();
                String fieldValue = field.getFieldValue();
                PropertyUtils.setProperty(answer, DTOClassInfo.underLineToCamel(fieldName), DTOClassInfo.underLineToCamel(fieldValue));
            } catch (Exception e) {
                logger.error("error when setting blob text properties , {}", e);
            }
        }
        return answer;
    }


    /**
     * 检查一个被@Children 标注的属性的类型,是否被支持.
     *
     * @param type class type
     * @return the type is supported or not
     */
    protected boolean checkChildrenType(Class<?> type) {
        if (BaseDTO.class.isAssignableFrom(type)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    private void autoAssignStdProperty(Logger logger, IRequest request, BaseDTO dto, boolean stdWhoSupport) {
        if (stdWhoSupport) {
            for (EntityField f : DTOClassInfo.getIdFields(dto.getClass())) {
                try {
                    if (PropertyUtils.getProperty(dto, f.getName()) == null) {
                        dto.setCreatedBy(request.getUserId());
                        dto.setCreationDate(new Date());
                        break;
                    }
                } catch (Exception e) {
                }
            }
            dto.setLastUpdateDate(new Date());
            dto.setLastUpdatedBy(request.getUserId());
            dto.setLastUpdateLogin(request.getUserId());
        }
        for (EntityField field : DTOClassInfo.getChildrenFields(dto.getClass())) {
            if (!checkChildrenType(field.getJavaType())) {
                if (logger.isWarnEnabled()) {
                    logger.warn("property '{}' is annotated by @Children, incorrect usage.", field.getName());
                }
                return;
            }
            try {
                Object p = PropertyUtils.getProperty(dto, field.getName());
                if (p instanceof BaseDTO) {
                    autoAssignStdProperty(logger, request, (BaseDTO) p, stdWhoSupport);
                } else if (p instanceof Collection) {
                    for (Object o : (Collection) p) {
                        if (o instanceof BaseDTO) {
                            autoAssignStdProperty(logger, request, (BaseDTO) o, stdWhoSupport);
                        }
                    }
                }
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void proceedAutoCacheOperation(MethodInvocation invocation, Object ret)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object target = invocation.getThis();
        Method method = invocation.getMethod();
        method = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        CacheSet cacheSet = AnnotationUtils.findAnnotation(method, CacheSet.class);
        if (cacheSet != null) {
            proceedCacheSet(method, cacheSet, ret);
        }
        CacheDelete cacheDelete = AnnotationUtils.findAnnotation(method, CacheDelete.class);
        if (cacheDelete != null) {
            proceedCacheDelete(invocation, cacheDelete);
        }
    }

    @SuppressWarnings("unchecked")
    private void proceedCacheSet(Method method, CacheSet cacheSet, Object ret)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String cacheName = cacheSet.cache();
        if (!StringUtils.isNotEmpty(cacheName)) {
            throw new IllegalArgumentException("cache: " + cacheName + " is not valid.");
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("cache: {} not found.", cacheName);
            }
            return;
        }
        String key = cache.getCacheKey(ret);
        if (cache instanceof HashStringRedisCache) {
            String valueField = ((HashStringRedisCache) cache).getValueField();
            if (valueField != null) {
                ret = PropertyUtils.getProperty(ret, valueField);
            }
        }
        cache.setValue(key, ret);
        if (logger.isDebugEnabled()) {
            logger.debug("{} cache auto set. key={}", method.getName(), key);
        }
    }

    @SuppressWarnings("unchecked")
    private void proceedCacheDelete(MethodInvocation invocation, CacheDelete cacheDelete) throws NoSuchMethodException {
        String cacheName = cacheDelete.cache();
        if (!StringUtils.isNoneEmpty(cacheName)) {
            throw new IllegalArgumentException("cache: " + cacheName + " is not valid.");
        }
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("cache: {} not found.", cacheName);
            }
            return;
        }
        Object dto = invocation.getArguments()[0];
        String key = cache.getCacheKey(dto);
        if (cache instanceof HashStringRedisCacheGroup) {
            HashStringRedisCacheGroup hashStringRedisCacheGroup = (HashStringRedisCacheGroup) cache;
            String group = hashStringRedisCacheGroup.getGroupValue(dto);
            hashStringRedisCacheGroup.remove(group, key);
        } else {
            cache.remove(key);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("{} cache auto remove. key={}", invocation.getMethod().getName(), key);
        }
    }
}
