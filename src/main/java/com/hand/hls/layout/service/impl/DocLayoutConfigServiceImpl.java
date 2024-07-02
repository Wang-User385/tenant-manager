package com.hand.hls.layout.service.impl;

import com.hand.hap.mybatis.entity.Example;
import com.hand.hap.security.EncryptUtils;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.layout.dto.DocLayoutConfig;
import com.hand.hls.layout.mapper.DocLayoutConfigMapper;
import com.hand.hls.layout.service.IDocLayoutConfigService;
import leaf.bean.LeafRequestData;
import leaf.bm.components.RecordHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class DocLayoutConfigServiceImpl extends BaseServiceImpl<DocLayoutConfig> implements IDocLayoutConfigService {

    @Autowired
    private DocLayoutConfigMapper docLayoutConfigMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Override
    public List<DocLayoutConfig> selectDocLayoutConfig(Map<String, Object> map, int page, int pageSize) {
//        PageHelper.startPage(page,pageSize);
        return docLayoutConfigMapper.selectDocLayoutConfig(map);
    }

    @Override
    public ResponseData selectDocLayoutConfigInit(CompositeMap map, String whereStr) {
        DocLayoutConfig docLayoutConfig = new DocLayoutConfig();
        CompositeMap parameter = (CompositeMap) map.get("parameter");
        docLayoutConfig.setLayoutCode(parameter.getString("layout_code"));
        docLayoutConfig.setTabCode(parameter.getString("tab_code"));
        docLayoutConfig.setColumnName(parameter.getString("column_name").toUpperCase());
        List<CompositeMap> list = docLayoutConfigMapper.selectDocLayoutConfigInit(docLayoutConfig);
        return new ResponseData(list);
    }

    @Override
    public List<DocLayoutConfig> selectDocLayoutConfigWithoutPrecision(DocLayoutConfig config) {
//        PageHelper.startPage(page,pageSize);
        return docLayoutConfigMapper.selectDocLayout(config);
    }

    @Override
    public boolean configReload(DocLayoutConfig config) {
        Example example = new Example(DocLayoutConfig.class);
        example.createCriteria().andEqualTo(DocLayoutConfig.FIELD_CONFIG_ID, config.getConfigId());
        docLayoutConfigMapper.deleteByExample(example);


        return false;
    }

    @Override
    public ResponseData resolveConfigField(LeafRequestData data) {
        Map parameter = data.getParameter();
        if (parameter == null || !parameter.containsKey("layout_code") || !parameter.containsKey("column_name") || !parameter.containsKey("tab_code")) {
            return new ResponseData(true);
        }
        List<DocLayoutConfig> list = docLayoutConfigMapper.selectDocLayoutConfig(parameter);
        if (CollectionUtils.isEmpty(list) || list.size() != 1) {
            return new ResponseData(true);
        }
        DocLayoutConfig config = list.get(0);
        String sql = config.getClobValidationSql() == null ? config.getValidationSql() : config.getClobValidationSql();
        if (StringUtils.isEmpty(sql)) {
            return new ResponseData(true);
        }
        if (sql.startsWith("$")) {
            String sqlId = sql.substring(1);
            BoundSql boundSql = sqlSessionFactory.getConfiguration().getMappedStatement(sqlId).getBoundSql(Collections.EMPTY_MAP);
            sql = boundSql.getSql();
        }
        sql = "select * from (" + sql + ") tmp_tbl_x where 1 = 0";
        SqlRowSet sqlRowSet;
        try {
            sqlRowSet = jdbcTemplate.queryForRowSet(sql);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(true);
            responseData.setMessage(e.getMessage());
            return responseData;
        }
        SqlRowSetMetaData metaData = sqlRowSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List results = new ArrayList();
        for (int i = 0; i < columnCount; i++) {
            Map map = new HashMap();
            map.put("name", metaData.getColumnLabel(i + 1));
            results.add(map);
        }
        ResponseData responseData = new ResponseData();
        responseData.setSuccess(true);
        responseData.setRows(results);
        return responseData;
    }

    @Override
    public ResponseData updateConfig(LeafRequestData data) {
        if (data.get("parameter") != null && ((List) data.get("parameter")).size() > 0) {
            List<Map> list = ((List) data.get("parameter"));
            for (Map value : list) {

                CompositeMap map = new CompositeMap("", value);
                String clobValidationSql = map.getString("clob_validation_sql");
                String queryValidationSql = map.getString("query_validation_sql");
                if (StringUtils.isNotEmpty(clobValidationSql)) {
                    map.put("clob_validation_sql", EncryptUtils.aesDecrypt(clobValidationSql));
                }
                if (StringUtils.isNotEmpty(queryValidationSql)) {
                    map.put("query_validation_sql", EncryptUtils.aesDecrypt(queryValidationSql));
                }

                if ("insert".equals(value.get("_status"))) {
                    RecordHelper.insert("hls_doc_layout_config", map);
                } else if ("update".equals(value.get("_status"))) {
                    RecordHelper.update("hls_doc_layout_config", map);
                } else if ("delete".equals(value.get("_status"))) {
                    RecordHelper.delete("hls_doc_layout_config", map);
                }
            }
        }
        return new ResponseData(((List) data.get("parameter")));
    }
}