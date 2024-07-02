package leaf.bm.components;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import jodd.util.ArraysUtil;
import leaf.utils.ConfigUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import uncertain.composite.CompositeUtil;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.Reader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RecordHelper implements ApplicationContextAware {
    public static final String SEQ_FIELD = "__seq_field__";
    public static final String STATUS_FIELD = "_status";
    public static final String STATUS_INSERT = "insert";
    public static final String STATUS_DELETE = "delete";
    public static final String STATUS_UPDATE = "update";
    private static NamedParameterJdbcTemplate jdbcTemplate;
    private static DataSource dataSource;
    private static Logger logger = LoggerFactory.getLogger(RecordHelper.class);

    private static final String RESULTSET_COLUMN_NAME = "COLUMN_NAME";
    private static final String RESULTSET_DATA_TYPE = "DATA_TYPE";

    private static final String HLS_V3_WHO_CREATE_DATE_FIELD = "creation_date";
    private static final String HLS_V3_WHO_CREATE_BY_FIELD = "created_by";
    private static final String HLS_V3_WHO_UPDATE_DATE_FIELD = "last_update_date";
    private static final String HLS_V3_WHO_UPDATE_BY_FIELD = "last_updated_by";

    private static final String LAYOUT_CODE = "layout_code";
    private static final String LOV_CONFIG_ID = "lov_config_id";
    private static final String FUNCTION_CODE = "function_code";

    public static final String HLS_DOC_LAYOUT_BUTTON = "hls_doc_layout_button";
    public static final String HLS_DOC_LAYOUT_CONFIG_LOV = "hls_doc_layout_config_lov";

    private static final int[] DATE_DATA_TYPES = {Types.DATE, Types.TIMESTAMP, Types.TIME};


    public static List<Map> select(String tableName, Map record) {
        return select(tableName, record, null);
    }

    public static List<Map> select(String tableName, Map record, List<String> paramFields) {
        String sql = genSelectStatement(tableName, record, paramFields);
        logger.debug("prepare query sql: {}, params: [{}]", sql, record);
        return jdbcTemplate.query(sql, record, new RowMapper<Map>() {
            @Override
            public Map mapRow(ResultSet resultSet, int rows) throws SQLException {
                Map map = new HashMap();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnLabel = metaData.getColumnLabel(i).toLowerCase(Locale.CHINA);
                    int columnType = metaData.getColumnType(i);
                    Object value = null;
                    if (columnType == Types.CLOB) {
                        Clob clob = resultSet.getClob(i);
                        if (Objects.nonNull(clob)) {
                            value = clobToString(clob);
                        }
                    } else {
                        value = resultSet.getObject(i);
                    }
                    map.put(columnLabel, value);
                }
                return map;
            }
        });
    }

    private static String genSelectStatement(String tableName, Map record, List<String> paramFields) {
        StringBuilder st = new StringBuilder();
        st.append(" select * ").append("from ").append(tableName);
        String wherePart = genQueryWherePart(tableName, record, paramFields);
        if (wherePart != null) {
            st.append(" where ").append(wherePart);
        }
        return st.toString();
    }

    public static int insert(String tableName, Map record) {
        IRequest currentRequest = RequestHelper.getCurrentRequest(true);
        Map map = prepareInsertWho(currentRequest, record);
        InsertStatement insertStatement = genInsertStatement(tableName, map);
        String sql = insertStatement.getSql();
        String pkField = insertStatement.getPkField();
        String seqName = insertStatement.getSeqName();

        int count = 0;
        if (ConfigUtils.isOracle()) {
            //oracle
            Object seqNextValue = null;
            if (pkField != null && seqName != null) {
                // oracle需要先获取seq的值
                logger.debug("prepare get sequence:[{}] next value", seqName);
                seqNextValue = getSeqNextValue(seqName);
                map.put(pkField, seqNextValue);
            }
            try {
                count = jdbcTemplate.update(sql, map);
                record.put(pkField, map.get(pkField));
            } catch (Exception e) {
                if (pkField != null) {
                    record.remove(pkField);
                }
                throw e;
            }
        } else if (ConfigUtils.isMySQL()) {
            //MySQL
            logger.debug("prepare insert sql: {}, params: [{}]", sql, map);
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            MapSqlParameterSource params = new MapSqlParameterSource(map);
            count = jdbcTemplate.update(sql, params, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.size() > 0) {
                keys.forEach((k, v) -> {
                    record.put(pkField, v);
                });
            }
        }
        return count;
    }

    private static InsertStatement genInsertStatement(String tableName, Map record) {
        if (StringUtils.isEmpty(tableName) || record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record or tableName");
        }
        AtomicBoolean first = new AtomicBoolean(true);
        StringBuilder st = new StringBuilder();
        StringBuilder insert = new StringBuilder();
        StringBuilder value = new StringBuilder();
        List<String> pkFields = getPkFields(tableName);
        Map<String, Integer> tableColumnsType = getTableColumnsType(tableName);
        Set<String> tableColumns = tableColumnsType.keySet();
        record.forEach((k, v) -> {
            if (!tableColumns.contains(k)) {
                // 跳过表中不存在的
                return;
            }
            if (ArraysUtil.contains(DATE_DATA_TYPES, tableColumnsType.get(k)) && v != null) {
                if (StringUtils.isEmpty(v.toString())) {
//                    do nothing
                }
                if(v instanceof Date){
                    record.put(k, v);
                }else  if (StringUtils.isNumeric(v.toString())) {
                    record.put(k, new Date((Long) v));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = sdf.parse(v.toString());
                        record.put(k, date);
                    } catch (Exception e) {
                        try {
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(v.toString());
                            record.put(k, date);
                        } catch (Exception ee) {
                            logger.warn("Can not parse date type value of {}, value: {}", k, v);
                        }
                    }

                }
            }
            if (!first.getAndSet(false)) {
                insert.append(",");
                value.append(",");
            }
            insert.append(k);
            value.append(":").append(k);
        });
        String pkField = null;
        String seqName = null;
        if (pkFields != null && pkFields.size() == 1) {
            pkField = pkFields.get(0);
        }
        if (ConfigUtils.isOracle() && pkField != null) {
            // 数据库为Oracle、主键只有一列时
            // 尝试使用序列
            if (!record.containsKey(pkField)) {
                // record中不包含主键的值时，才使用序列
                seqName = getSeqName(tableName, record);
                if (!first.getAndSet(false)) {
                    insert.append(",");
                    value.append(",");
                }
                insert.append(pkField);
                value.append(":").append(pkField);
            }
        }
        st.append("insert into ").append(tableName)
                .append(" (").append(insert).append(") ")
                .append(" values(").append(value).append(")");

        InsertStatement insertStatement = new InsertStatement(st.toString(), seqName, pkField, ConfigUtils.isOracle());
        return insertStatement;
    }

    private static Object getSeqNextValue(String seqName) {
        if (StringUtils.isBlank(seqName)) {
            return null;
        }
        String sql = "select " + seqName + ".nextval as VAL from dual";
        Map map = jdbcTemplate.queryForMap(sql, Collections.EMPTY_MAP);
        if (map != null) {
            return map.get("VAL");
        }
        return null;
    }

    private static String getSeqName(String tableName, Map record) {
        if (record != null && record.containsKey(SEQ_FIELD)) {
            return String.valueOf(record.get(SEQ_FIELD));
        }
        return tableName + "_s";
    }

    public static int batchUpdate(String tableName, List<Map> records) {
        if (CollectionUtils.isEmpty(records)) {
            return 0;
        }

        int count = 0;
        for (Map record : records) {
            // 默认插入状态
            String status = String.valueOf(record.getOrDefault(STATUS_FIELD, STATUS_INSERT));
            switch (status) {
                case STATUS_INSERT:
                    int insert = insert(tableName, record);
                    count += insert;
                    break;
                case STATUS_UPDATE:
                    int update = update(tableName, record);
                    count += update;
                    break;
                case STATUS_DELETE:
                    int delete = delete(tableName, record);
                    count += delete;
                    break;
                default:
                    throw new RuntimeException("unknown status: " + status);
            }
        }
        return count;
    }

    public static int batchUpdate(String tableName, List<Map> records, List<String> paramField) {
        if (CollectionUtils.isEmpty(records)) {
            return 0;
        }

        int count = 0;
        for (Map record : records) {
            // 默认插入状态
            String status = String.valueOf(record.getOrDefault(STATUS_FIELD, STATUS_INSERT));
            switch (status) {
                case STATUS_INSERT:
                    int insert = insert(tableName, record);
                    count += insert;
                    break;
                case STATUS_UPDATE:
                    int update = update(tableName, record, paramField);
                    count += update;
                    break;
                case STATUS_DELETE:
                    int delete = delete(tableName, record, paramField);
                    count += delete;
                    break;
                default:
                    throw new RuntimeException("unknown status: " + status);
            }
        }
        return count;
    }

    public static int update(String tableName, Map record) {
        return update(tableName, record, null);
    }

    public static int update(String tableName, Map record, List<String> paramFields) {
        IRequest currentRequest = RequestHelper.getCurrentRequest(true);
        Map map = prepareUpdateWho(currentRequest, record);
        String sql = genUpdateStatement(tableName, map, paramFields);
        logger.debug("prepare update sql: {}, params: [{}]", sql, map);
        return jdbcTemplate.update(sql, map);
    }

    private static String genUpdateStatement(String tableName, Map record, List<String> paramFields) {
        if (StringUtils.isEmpty(tableName) || record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record or tableName");
        }
        StringBuilder st = new StringBuilder();
        st.append("update ").append(tableName)
                .append(" set ").append(genSetPart(tableName, record))
                .append(" where ").append(genWherePart(tableName, record, paramFields));
        return st.toString();
    }

    public static int delete(String tableName, Map record) {
        return delete(tableName, record, null);
    }

    public static int delete(String tableName, Map record, List<String> paramFields) {
        String sql = genDeleteStatement(tableName, record, paramFields);
        logger.debug("prepare delete sql: {}, params: [{}]", sql, record);
        return jdbcTemplate.update(sql, record);
    }
    public static int delete4ImportLayout(String tableName, Map<String,String> record) {
        String sql = genDeleteStatement4ImportLayout(tableName, record);
        logger.debug("prepare delete sql: {}, params: [{}]", sql, record);
        return jdbcTemplate.update(sql, record);
    }
    private static String genDeleteStatement4ImportLayout(String tableName, Map<String,String> record) {
        if (StringUtils.isEmpty(tableName) || record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record or tableName");
        }
        StringBuilder st = new StringBuilder();
        st.append("delete from ").append(tableName)
                .append(" where ").append(genWherePart4ImportLayout(tableName, record));
        return st.toString();
    }
    private static String genWherePart4ImportLayout(String tableName, Map<String,String> record) {
        if (record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record");
        }
        List<String> fields = new ArrayList<>();
        // 导入前先删除数据，特例值列表配置
        if (HLS_DOC_LAYOUT_CONFIG_LOV.equals(tableName)){
            fields.add(LOV_CONFIG_ID);
        // 导入前先删除数据，功能按钮配置使用包含条件删除button&proc
        } else if (tableName.contains(HLS_DOC_LAYOUT_BUTTON)){
            fields.add(FUNCTION_CODE);
        } else {
            // 导入前先删除数据，其他都使用layout_code
            if (Objects.nonNull(record.get(LAYOUT_CODE))){
                fields.add(LAYOUT_CODE);
            }
        }
        return genWherePartByFields(tableName, fields);
    }

    private static String genDeleteStatement(String tableName, Map record, List<String> paramFields) {
        if (StringUtils.isEmpty(tableName) || record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record or tableName");
        }
        StringBuilder st = new StringBuilder();
        st.append("delete from ").append(tableName)
                .append(" where ").append(genWherePart(tableName, record, paramFields));
        return st.toString();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        jdbcTemplate = applicationContext.getBean(NamedParameterJdbcTemplate.class);
        dataSource = applicationContext.getBean(DataSource.class);
    }


    private static String genSetPart(String tableName, Map record) {
        if (record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record");
        }
        AtomicBoolean first = new AtomicBoolean(true);
        StringBuilder sb = new StringBuilder();
        Map<String, Integer> tableColumnsType = getTableColumnsType(tableName);
        Set<String> tableColumns = tableColumnsType.keySet();
        record.forEach((k, v) -> {
            if (!tableColumns.contains(k)) {
                // 跳过表中不存在的
                return;
            }
            if (ArraysUtil.contains(DATE_DATA_TYPES, tableColumnsType.get(k)) && v != null) {
                if (StringUtils.isEmpty(v.toString())) {
//                    do nothing
                }
                if(v instanceof Date){
                    record.put(k, v);
                }else  if (StringUtils.isNumeric(v.toString())) {
                    record.put(k, new Date((Long) v));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date date = sdf.parse(v.toString());
                        record.put(k, date);
                    } catch (Exception e) {
                        try {
                            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = sdf.parse(v.toString());
                            record.put(k, date);
                        } catch (Exception ee) {
                            logger.warn("Can not parse date type value of {}, value: {}", k, v);
                        }
                    }

                }
            }
            if (!first.getAndSet(false)) {
                sb.append(",");
            }
            sb.append(k).append(" = :").append(k);
        });
        return sb.toString();
    }

    private static String genWherePart(String tableName, Map record) {
        return genWherePart(tableName, record, null);
    }

    private static String genWherePart(String tableName, Map record, List<String> params) {
        if (record == null || record.size() < 1) {
            throw new IllegalArgumentException("empty record");
        }

        if (params != null) {
            return genWherePartByFields(tableName, params);
        }
        List<String> fields = getPkFields(tableName);
        return genWherePartByFields(tableName, fields);
    }

    public static List<String> getPkFields(String tableName) {
        Set<String> pks = new HashSet<>();
        try (Connection connection = dataSource.getConnection()) {
            String schema = null;
            if (ConfigUtils.isOracle() && connection instanceof DruidPooledConnection) {
                schema = ((DruidPooledConnection) connection).getConnection().getSchema();
            }
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet pkResultSet = metaData.getPrimaryKeys(catalog, schema, tableName.toUpperCase(Locale.CHINA));
            while (pkResultSet.next()) {
                String columnName = pkResultSet.getString(RESULTSET_COLUMN_NAME);
                pks.add(columnName.toLowerCase(Locale.CHINA));
            }
        } catch (SQLException e) {
            throw new RuntimeException("get Pk fields failed", e);
        }
        return new ArrayList<>(pks);
    }

    public static Set<String> getTableColumns(String tableName) {
        Set<String> columns = new HashSet<>();
        try (Connection connection = dataSource.getConnection()) {
            String schema = null;
            if (ConfigUtils.isOracle() && connection instanceof DruidPooledConnection) {
                schema = ((DruidPooledConnection) connection).getConnection().getSchema();
            }
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(catalog, schema, tableName.toUpperCase(Locale.CHINA), "%");
            while (resultSet.next()) {
                String columnName = resultSet.getString(RESULTSET_COLUMN_NAME);
                columns.add(columnName.toLowerCase(Locale.CHINA));
            }
        } catch (SQLException e) {
            throw new RuntimeException("get table columns failed", e);
        }
        return columns;
    }

    public static Map<String, Integer> getTableColumnsType(String tableName) {
        Map<String, Integer> columns = new HashMap<>();
        try (Connection connection = dataSource.getConnection()) {
            String schema = null;
            if (ConfigUtils.isOracle() && connection instanceof DruidPooledConnection) {
                schema = ((DruidPooledConnection) connection).getConnection().getSchema();
            }
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(catalog, schema, tableName.toUpperCase(Locale.CHINA), "%");
            while (resultSet.next()) {
                String columnName = resultSet.getString(RESULTSET_COLUMN_NAME);
                columns.put(columnName.toLowerCase(Locale.CHINA), resultSet.getInt(RESULTSET_DATA_TYPE));
            }
        } catch (SQLException e) {
            throw new RuntimeException("get table columns failed", e);
        }
        return columns;
    }

    private static String genWherePartByFields(String tableName, List<String> fields) {
        StringBuilder sb = new StringBuilder();
        AtomicBoolean first = new AtomicBoolean(true);
        Set<String> tableColumns = getTableColumns(tableName);
        fields.forEach(p -> {
            if (!tableColumns.contains(p)) {
                // 跳过表中不存在的
                return;
            }
            if (!first.getAndSet(false)) {
                sb.append(" and ");
            }
            sb.append(p).append(" = :").append(p);
        });
        return sb.toString();
    }

    private static String genQueryWherePart(String tableName, Map record, List<String> fields) {

        StringBuilder sb = new StringBuilder();
        AtomicBoolean first = new AtomicBoolean(true);
        if (fields == null) {
            if (record == null) {
                throw new IllegalArgumentException("fields,record empty");
            }
            Set<String> tableColumns = getTableColumns(tableName);
            // 用record里value不为空的值且表中存在的
            record.forEach((k, v) -> {
                if (v == null) {
                    // 跳过值为null的
                    return;
                }
                if (!tableColumns.contains(k.toString().toLowerCase())) {
                    // 跳过表中不存在的
                    return;
                }
                if (!first.getAndSet(false)) {
                    sb.append(" and ");
                }
                sb.append(k).append(" = :").append(k);
            });
        } else if (fields.size() > 0) {
            // 只用fields部分
            Set<String> tableColumns = getTableColumns(tableName);
            fields.forEach(p -> {
                if (!tableColumns.contains(p)) {
                    // 跳过表中不存在的
                    return;
                }
                if (!first.getAndSet(false)) {
                    sb.append(" and ");
                }
                sb.append(p).append(" = :").append(p);
            });
        } else {
            // 不加条件部分
            return null;
        }
        // 防止过滤后的字段为空
        if (sb.length() < 1) {
            throw new RuntimeException("no proper field in record or fields");
        }
        return sb.toString();
    }

    private static Map prepareInsertWho(IRequest iRequest, Map map) {
        if (iRequest == null || iRequest.getUserId() == null) {
            logger.warn("currentRequest is empty");
        }
        Map dest = prepareUpdateWho(iRequest, map);
        if (map == null || dest == null) {
            return null;
        }
        dest.put(HLS_V3_WHO_CREATE_BY_FIELD, iRequest.getUserId());
        dest.put(HLS_V3_WHO_CREATE_DATE_FIELD, new Date(System.currentTimeMillis()));
        return dest;
    }

    private static Map prepareUpdateWho(IRequest iRequest, Map map) {
        if (iRequest == null || iRequest.getUserId() == null) {
            logger.warn("currentRequest is empty");
        }
        if (map == null) {
            return null;
        }
        Map dest = new HashMap();
        CompositeUtil.copyAttributes(map, dest);
        dest.put(HLS_V3_WHO_UPDATE_BY_FIELD, iRequest.getUserId());
        dest.put(HLS_V3_WHO_UPDATE_DATE_FIELD, new Date(System.currentTimeMillis()));
        dest.remove(HLS_V3_WHO_CREATE_DATE_FIELD);
        return dest;
    }

    /**
     * transfer Clob to String.
     *
     * @param clob
     * @return String value of clob if trans success. otherwise null will be returned.
     */
    private static String clobToString(Clob clob) {
        try {
            Reader is = clob.getCharacterStream();// 得到流
            BufferedReader br = new BufferedReader(is);
            String s = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (s != null) {
                sb.append(s);
                //add by Eugene Song 修复sql 去除回行之后 ，连接在一起的bug
                sb.append(" ");
                s = br.readLine();
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error("clob to String error", e);
        }
        return null;
    }
}

class InsertStatement {
    private String sql;
    private String seqName;
    private String pkField;
    private Boolean isOracle;

    public InsertStatement(String sql, String seqName, String pkField, Boolean isOracle) {
        this.sql = sql;
        this.seqName = seqName;
        this.pkField = pkField;
        this.isOracle = isOracle;
    }

    public String getSql() {
        return sql;
    }

    public String getSeqName() {
        return seqName;
    }

    public String getPkField() {
        return pkField;
    }

    public Boolean getOracle() {
        return isOracle;
    }
}
