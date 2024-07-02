package com.hand.hls.layout.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayout;
import com.hand.hls.layout.dto.DocLayoutTab;
import com.hand.hls.layout.dto.DocLayoutTree;
import com.hand.hls.layout.dto.LovDocLayoutDto;
import com.hand.hls.layout.mapper.DocLayoutMapper;
import com.hand.hls.layout.mapper.DocLayoutTabMapper;
import com.hand.hls.layout.mapper.DocLayoutTreeMapper;
import com.hand.hls.layout.service.IDocLayoutService;
import com.hand.hls.layout.service.IDocLayoutTabService;
import leaf.bm.components.RecordHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutServiceImpl extends BaseServiceImpl<DocLayout> implements IDocLayoutService {

    public static final String KEY_TABLE = "_table";
    public static final String KEY_DATA = "_data";
    public static final String KEY_SUB = "_sub";

    public static final String HLS_DOC_LAYOUT = "hls_doc_layout";
    public static final String HLS_DOC_LAYOUT_CONFIG_LOV = "hls_doc_layout_config_lov";
    public static final String HLS_DOC_LAYOUT_TREE = "hls_doc_layout_tree";
    public static final String HLS_DOC_LAYOUT_TAB = "hls_doc_layout_tab";
    public static final String HLS_DOC_LAYOUT_CONFIG = "hls_doc_layout_config";
    public static final String HLS_DOC_LAYOUT_SCREEN = "hls_doc_layout_screen";
    public static final String HLS_DOC_LAYOUT_TAB_BUTTON = "hls_doc_layout_tab_button";
    public static final String HLS_DOC_LAYOUT_BUTTON = "hls_doc_layout_button";
    public static final String HLS_DOC_LAYOUT_BUTTON_PROC = "hls_doc_layout_button_proc";

    private static final String LAYOUT_CODE = "layout_code";
    private static final String LOV_CONFIG_ID = "lov_config_id";
    private static final String FUNCTION_CODE = "function_code";

    @Autowired
    private DocLayoutMapper docLayoutMapper;
    @Autowired
    private IDocLayoutTabService docLayoutTabService;
    @Autowired
    private DocLayoutTreeMapper docLayoutTreeMapper;
    @Autowired
    private DocLayoutTabMapper docLayoutTabMapper;

    private Map<String, String[]> mapping;
    private Set<String> removeKey;

    {
        /*
        hls_doc_layout
        hls_doc_layout_button
        hls_doc_layout_button_proc
        hls_doc_layout_config*
        hls_doc_layout_config_lov*
        hls_doc_layout_screen
        hls_doc_layout_tab
        hls_doc_layout_tab_but_proc
        hls_doc_layout_tab_button
        * */
        mapping = new HashMap<>();
        mapping.put("hls_doc_layout", new String[]{"hls_doc_layout_tab", "hls_doc_layout_config"});
        mapping.put("hls_doc_layout_button", new String[]{"hls_doc_layout_button_proc"});
        mapping.put("hls_doc_layout_button_proc", new String[]{});
        mapping.put("hls_doc_layout_config", new String[]{"hls_doc_layout_config_lov"});
        mapping.put("hls_doc_layout_config_lov", new String[]{});
        mapping.put("hls_doc_layout_screen", new String[]{});
        mapping.put("hls_doc_layout_tab", new String[]{"hls_doc_layout_screen", "hls_doc_layout_tab_button"});
        mapping.put("hls_doc_layout_tab_but_proc", new String[]{"hls_doc_layout_button"});
        mapping.put("hls_doc_layout_tab_button", new String[]{"hls_doc_layout_tab_but_proc"});

        removeKey = new HashSet<>();
        removeKey.add("hls_doc_layout_config");
        removeKey.add("hls_doc_layout_config_lov");
    }


    @Override
    public List<DocLayout> selectDocLayout(Map<String, Object> paramMap, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return docLayoutMapper.selectDocLayout(paramMap);
    }


    @Override
    public ResponseData layoutCopy(IRequest requestContext, String fromLayoutCode, String toLayoutCode, String toDesc) {
        //若存在需复制的布局先删除
        DocLayout to = new DocLayout();
        to.setLayoutCode(toLayoutCode);
        this.deleteByPrimaryKey(to);
        DocLayout from = new DocLayout();
        from.setLayoutCode(fromLayoutCode);
        to = this.selectByPrimaryKey(requestContext, from);
        if (to == null) {
            return new ResponseData(false, "您输入的源布局代码不正确");
        }
        to.setLayoutCode(toLayoutCode);
        to.setDescription(toDesc == null ? from.getDescription() : toDesc);
        //插入hls_doc_layout表
        this.insertSelective(requestContext, to);
        //复制hls_doc_layout_tab
        DocLayoutTab docLayoutTab = new DocLayoutTab();
        docLayoutTab.setLayoutCode(fromLayoutCode);
        List<DocLayoutTab> tabs = docLayoutTabMapper.select(docLayoutTab);
        for (DocLayoutTab tab : tabs) {
            docLayoutTabService.layoutTabCopy(requestContext, fromLayoutCode, toLayoutCode, tab.getTabCode(), tab.getTabCode(), tab.getTabDesc(), "N", null);
        }
        //删除hls_doc_layout_tree
        DocLayoutTree docLayoutTree = new DocLayoutTree();
        docLayoutTree.setLayoutCode(toLayoutCode);
        docLayoutTreeMapper.delete(docLayoutTree);
        //复制hls_doc_layout_tree
        docLayoutTree.setLayoutCode(fromLayoutCode);
        List<DocLayoutTree> trees = docLayoutTreeMapper.select(docLayoutTree);
        for (DocLayoutTree tree : trees) {
            tree.setLayoutCode(toLayoutCode);
            docLayoutTreeMapper.insert(tree);
        }
        return new ResponseData(true);
    }

    @Override
    public ResponseData selectDocLayoutSequence(CompositeMap map, String whereStr) {
        HttpServletRequest request = (HttpServletRequest) map.get("_instance.javax.servlet.http.HttpServletRequest");
        Object screen_config_record_count = request.getAttribute("screen_config_record_count");
        map.put("total_count", screen_config_record_count);
        List<CompositeMap> list = docLayoutMapper.selectDocLayoutSequence(map);
        return new ResponseData(list);
    }

    @Override
    public List<LovDocLayoutDto> selectForLov(Map param, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return docLayoutMapper.selectForLov(param);
    }

    @Override
    public ResponseEntity<Resource> exportLayout(DocLayout layout) {

        Map<String, Object> params = new HashMap<>();
        String tableName = "hls_doc_layout";
        params.put("layout_code", layout.getLayoutCode());
        Map<String, Object> data = getTableDataByParams(tableName, params);
        byte[] bytes = JSON.toJSONString(data).getBytes();
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", new String((layout.getLayoutCode() + ".layout.leaf").getBytes(), StandardCharsets.ISO_8859_1));
        headers.setContentLength(bytes.length);
        return ResponseEntity.ok().headers(headers).body(byteArrayResource);
    }

    @Override
    public void importLayout(String content) {
        if(StringUtils.isEmpty(content)){
            throw new RuntimeException("Empty content");
        }
        JSONObject jsonObject = JSON.parseObject(content);
        saveData(jsonObject, null);
    }

    @Override
    public ResponseEntity<Resource> exportLayoutButton(@NotNull final String functionCode) {
        Map<String, Object> params = new HashMap<>();
        params.put(FUNCTION_CODE, functionCode);
        Map<String, Object> data = getTableDataByParams(HLS_DOC_LAYOUT_BUTTON, params);
        byte[] bytes = JSON.toJSONString(data).getBytes();
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", new String((functionCode + ".layout_button.leaf").getBytes(), StandardCharsets.ISO_8859_1));
        headers.setContentLength(bytes.length);
        return ResponseEntity.ok().headers(headers).body(byteArrayResource);
    }

    @Override
    public void importLayoutButton(@NotNull final String content) {
        if(StringUtils.isEmpty(content)){
            throw new RuntimeException("Import content must not be empty");
        }
        JSONObject jsonObject = JSON.parseObject(content);
        saveButtonData(jsonObject, null);
    }

    private void saveData(JSONObject jsonObject, Map<String, Object> pkValues) {
        String tableName = jsonObject.getString(KEY_TABLE);
        JSONArray jsonArray = jsonObject.getJSONArray(KEY_DATA);
        if(jsonArray == null || jsonArray.isEmpty()){
            return;
        }
        List<String> pkFields = RecordHelper.getPkFields(tableName);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject record = jsonArray.getJSONObject(i);
            // 先删除layout
            if(HLS_DOC_LAYOUT.equals(tableName)){
                String layoutCode = record.getString(LAYOUT_CODE);
                Map<String,String> dr = new HashMap<>();
                dr.put(LAYOUT_CODE,layoutCode);
                deleteLayout(dr);
            }else if (HLS_DOC_LAYOUT_CONFIG_LOV.equals(tableName)){
                // 因为导出的config_lov是带主键的，预防唯一索引错误使用lov_config_id删除。而不使用config_id(因为导入成功后再次导入也会报唯一索引错误)
                String lovConfigId = record.getString(LOV_CONFIG_ID);
                if (Objects.nonNull(lovConfigId)){
                    Map<String,String> dclr = new HashMap<>();
                    dclr.put(LOV_CONFIG_ID,lovConfigId);
                    RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_CONFIG_LOV,dclr);
                }
            }
            if(pkValues != null && pkValues.size() > 0){
                record.putAll(pkValues);
            }
            RecordHelper.insert(tableName, record);
            Map<String, Object> map = extractPkMap(record, pkFields);
            JSONArray subData = record.getJSONArray(KEY_SUB);
            if(subData != null && !subData.isEmpty()){
                // 如果不循环处理，hls_doc_layout_config_lov的数据会处理不到
                for (int j = 0; j < subData.size(); j++) {
                    JSONObject subObject = subData.getJSONObject(j);
                    saveData(subObject, map);
                }
            }
        }
    }
    private void saveButtonData(JSONObject jsonObject, Map<String, Object> pkValues) {
        String tableName = jsonObject.getString(KEY_TABLE);
        JSONArray jsonArray = jsonObject.getJSONArray(KEY_DATA);
        if(jsonArray == null || jsonArray.isEmpty()){
            return;
        }
        // 导入的功能按钮都是一个功能的数据，故从数组中取其中一个找到function_code，先删除对应的源button数据
        String functionCode = jsonArray.getJSONObject(0).getString(FUNCTION_CODE);
        Map<String,String> ft = new HashMap<>();
        ft.put(FUNCTION_CODE,functionCode);
        deleteLayoutButton(ft);
        List<String> pkFields = RecordHelper.getPkFields(tableName);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject record = jsonArray.getJSONObject(i);
            if(pkValues != null && pkValues.size() > 0){
                record.putAll(pkValues);
            }
            RecordHelper.insert(tableName, record);
            Map<String, Object> map = extractPkMap(record, pkFields);
            JSONArray subData = record.getJSONArray(KEY_SUB);
            if(subData != null && !subData.isEmpty()){
                for (int j = 0; j < subData.size(); j++) {
                    JSONObject subObject = subData.getJSONObject(j);
                    saveData(subObject, map);
                }
            }
        }
    }
    // 导入前先删除已存在的布局数据
    private void deleteLayout(Map<String,String> record){
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT,record);
        // 没有树的测试数据，未经过测试。暂时注释
        // RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_TREE,record);
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_TAB,record);
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_CONFIG,record);
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_SCREEN,record);
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_TAB_BUTTON,record);
    }
    // 导入前先删除已存在的布局按钮数据
    private void deleteLayoutButton(Map<String,String> record){
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_BUTTON,record);
        RecordHelper.delete4ImportLayout(HLS_DOC_LAYOUT_BUTTON_PROC,record);
    }
    private Map<String, Object> getTableDataByParams(String tableName, Map params) {
        List<Map> list = RecordHelper.select(tableName, params);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        result.put(KEY_TABLE, tableName);
        result.put(KEY_DATA, list);
        String[] subs = mapping.get(tableName);
        if (subs != null && subs.length > 0) {
            List<String> pkFields = RecordHelper.getPkFields(tableName);
            for (Map map : list) {
                Map<String, Object> pkValues = extractPkMap(map, pkFields);
                if (pkValues == null) {
                    continue;
                }
                List<Map> subData = new ArrayList<>();
                for (String subTableName : subs) {
                    Map<String, Object> subDataMap = getTableDataByParams(subTableName, pkValues);
                    if (subDataMap != null) {
                        subData.add(subDataMap);
                    }
                }
                map.put(KEY_SUB, subData);
                if(removeKey.contains(tableName)){
                    for (String pkField : pkFields) {
                        map.remove(pkField);
                    }
                }
            }
        }
        return result;
    }

    private Map<String, Object> extractPkMap(Map map, List<String> pkFields) {
        if (map == null || map.size() == 0 || pkFields == null || pkFields.size() == 0) {
            return null;
        }
        Map<String, Object> target = new HashMap<>();
        for (String pkField : pkFields) {
            target.put(pkField, map.get(pkField));
        }
        return target;
    }
}