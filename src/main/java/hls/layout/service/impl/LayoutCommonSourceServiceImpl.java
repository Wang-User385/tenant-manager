package hls.layout.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.annotation.DocumentHistory;
import com.hand.hap.system.dto.BaseDTO;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutTab;
import com.hand.hls.layout.mapper.DocLayoutTabMapper;
import com.hand.hls.utils.JsonUtils;
import hls.layout.service.ILayoutCommonSourceService;
import hls.layout.service.IServerLayoutService;
import leaf.bean.LeafRequestData;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/9/10
 * Time: 15:56
 */
@Service
@Transactional
public class LayoutCommonSourceServiceImpl implements ILayoutCommonSourceService {
    @Autowired
    private SqlSessionFactory sessionFactory;
    @Autowired
    private BeanFactory beanFactory;
    @Autowired
    private DocLayoutTabMapper docLayoutTabMapper;
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    @Autowired
    private IServerLayoutService serverLayoutService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    @DocumentHistory(tableName = "base_table", pkField = "base_table", paraIndex = 0)
    public ResponseData selectData(LeafRequestData requestData, Map<String, Object> params) {
        if (hasSqlId(params)) {
            return selectDataBySqlId(params);
        }
        return selectDataBySql(requestData, params);
    }

    private ResponseData selectDataBySql(LeafRequestData requestData, Map<String, Object> params) {
        if (params == null || !params.containsKey("tab_code") || !params.containsKey("layout_code")) {
            return new ResponseData(false);
        }
        List<DocLayoutTab> docLayoutTabs = docLayoutTabMapper.selectDocLayoutTab(params);
        if (CollectionUtils.isEmpty(docLayoutTabs) && docLayoutTabs.size() != 1) {
            return new ResponseData(false);
        }
        DocLayoutTab docLayoutTab = docLayoutTabs.get(0);
        if (docLayoutTab == null) {
            return new ResponseData(false);
        }
        String querySource = docLayoutTab.getQuerySource();
        if (requestData != null && requestData.getParameter() != null) {
            params.putAll(requestData.getParameter());
        }
        String parentBaseTablePk = docLayoutTab.getParentBaseTablePk();
        String finalSql = "select tmp_tbl_x.* from (" + querySource + ") tmp_tbl_x where tmp_tbl_x." + parentBaseTablePk + " = :" + parentBaseTablePk;
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(finalSql, params);
        return new ResponseData(maps);
    }

    private ResponseData selectDataBySqlId(Map<String, Object> params) {
        String sqlId = getSqlId(params);
        Object pagesizeO = params.get("pagesize");
        Object pagenumO = params.get("pagenum");

        Map requestData = JSON.parseObject(params.get("_request_data").toString());
        if (requestData != null) {
            Map qparam = (Map) requestData.get("parameter");
            if (qparam != null) {
                Map param = JSON.parseObject(JsonUtils.toCamelJsonString(qparam));
                params.putAll(param);
            }
        }
        HashMap<String, Object> layoutParams = new HashMap<>();
        layoutParams.put("tab_code", params.get("tab_code"));
        layoutParams.put("layout_code", params.get("layout_code"));
        CompositeMap compositeMap = serverLayoutService.selectLayoutOrderField(layoutParams);
        List<CompositeMap> childs = compositeMap.getChilds();
        String orderBy = null;
        if(CollectionUtils.isNotEmpty(childs)){
            orderBy = childs.stream().map(child->{
                String orderType = child.getString("grid_order_type");
                String columnName = child.getString("column_name");
                orderType = "ASCENDING".equals(orderType) ? "ASC" : "DESC";
                return columnName + " " + orderType;
            }).collect(Collectors.joining(","));

        }
        String sortName =(String) params.get("sortName");
        String sortOrder =(String) params.get("sortOrder");
        if(sortName!=null){
            if(orderBy==null){
                orderBy=sortName+" "+sortOrder;
            }else {
                orderBy = orderBy + " " + sortName + " " + sortOrder;
            }
        }
        if (pagenumO != null && pagesizeO != null && !"true".equals(params.get("_fetchall"))) {
            PageHelper.startPage(Integer.valueOf(pagenumO.toString()), Integer.valueOf(pagesizeO.toString()));
        }
        if(StringUtils.isNotEmpty(orderBy)){
            PageHelper.orderBy(orderBy);
        }
        SqlSession sqlSession = sessionFactory.openSession();
        List<Object> objects = sqlSession.selectList(sqlId, params);
        return new ResponseData(objects);
    }

    public ResponseData selectDataFields(Map<String, Object> params) {
        String sqlId = getSqlId(params);
        Configuration configuration = sessionFactory.getConfiguration();
        MappedStatement mappedStatement = configuration.getMappedStatement(sqlId, true);
        ResultSetType resultSetType = mappedStatement.getResultSetType();
        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
        if (!sqlCommandType.equals(SqlCommandType.SELECT)) {
            throw new RuntimeException("target sql is not query method");
        }
        LinkedList<Map<String, Object>> fields = new LinkedList<>();
        if (resultSetType == null) {
//            使用resultMap
            if (CollectionUtils.isEmpty(resultMaps)) {
                throw new RuntimeException("target sqlId is not configured properly");
            }
            for (ResultMap resultMap : resultMaps) {
                Class<?> type = resultMap.getType();
                if (type.isAssignableFrom(BaseDTO.class)) {
                    List<Map<String, Object>> dtoFields = getFieldFromClass(type);
                    fields.addAll(dtoFields);
                } else {
                    logger.trace("sqlId: {} resultMap not extend from BaseDTO", sqlId);
                }
                List<ResultMapping> resultMappings = resultMap.getResultMappings();
                for (ResultMapping resultMapping : resultMappings) {
                    String property = resultMapping.getProperty();
                    Class<?> javaType = resultMapping.getJavaType();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("fieldName", property);
                    map.put("fieldType", javaType != null ? javaType.getName() : "java.lang.Object");
                    fields.add(map);
                }
            }
        } else {
//            使用resultType
            fields.addAll(getFieldFromClass(resultSetType.getDeclaringClass()));
        }
        return new ResponseData(fields);
    }

    private List<Map<String, Object>> getFieldFromClass(Class<?> type) {
        PropertyDescriptor[] propertyDescriptors = PropertyUtils.getPropertyDescriptors(type);
        LinkedList<Map<String, Object>> maps = new LinkedList<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("fieldName", propertyDescriptor.getDisplayName());
            map.put("fieldType", propertyDescriptor.getPropertyType().getName());
        }
        return maps;
    }


    private boolean hasSqlId(Map<String, Object> params) {
        return params != null && params.containsKey("sqlId");
    }

    private String getSqlId(Map<String, Object> params) {
        if (!hasSqlId(params))
            throw new RuntimeException("sqlId not found");
        return String.valueOf(params.get("sqlId"));
    }


}
