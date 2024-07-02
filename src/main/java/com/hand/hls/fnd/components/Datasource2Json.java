//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.fnd.components;

import com.hand.hls.fnd.dto.HlsDbDataSource;
import com.hand.hls.fnd.dto.HlsDbDataSourceColumn;
import com.hand.hls.fnd.dto.HlsDbDataSourcePara;
import com.hand.hls.fnd.dto.HlsDbDataSourceTb;
import com.hand.hls.fnd.dto.HlsDbDataSourceTbWh;
import com.hand.hls.fnd.mapper.HlsDbDataSourceColumnMapper;
import com.hand.hls.fnd.mapper.HlsDbDataSourceMapper;
import com.hand.hls.fnd.mapper.HlsDbDataSourceParaMapper;
import com.hand.hls.fnd.mapper.HlsDbDataSourceTbMapper;
import com.hand.hls.fnd.mapper.HlsDbDataSourceTbWhMapper;
import com.hand.hls.utils.SqlUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.validation.constraints.NotNull;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.util.StringUtils;

@Component
public class Datasource2Json {
    @Autowired
    private HlsDbDataSourceMapper dsMapper;
    @Autowired
    private HlsDbDataSourceParaMapper paraMapper;
    @Autowired
    private HlsDbDataSourceTbMapper tableMapper;
    @Autowired
    private HlsDbDataSourceTbWhMapper whereMapper;
    @Autowired
    private HlsDbDataSourceColumnMapper columnMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    private String dbName;

    public Datasource2Json() {
    }

    public String getDbName() {
        return this.dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String createSQL(long dataSourceId) {
        HlsDbDataSourcePara param = new HlsDbDataSourcePara();
        param.setDataSourceId(dataSourceId);
        param.setEnabledFlag("Y");
        List<HlsDbDataSourcePara> paramList = this.paraMapper.select(param);
        HlsDbDataSourceTb table = new HlsDbDataSourceTb();
        table.setDataSourceId(dataSourceId);
        table.setEnabledFlag("Y");
        List<HlsDbDataSourceTb> tableList = this.tableMapper.select(table);
        return this.generateSQL(dataSourceId, paramList, tableList);
    }

    public String executeSQLs4Json(List<Long> dsIds, Map<String, Object> params) throws IOException {
        if (dsIds == null) {
            return "";
        } else {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = new HashMap(dsIds.size());
            Iterator var5 = dsIds.iterator();

            while(var5.hasNext()) {
                Long dsId = (Long)var5.next();
                Map<String, Object> param = null;
                if (params != null && params.containsKey(dsId.toString())) {
                    param = (Map)params.get(dsId.toString());
                }

                String s = this.executeSQL4Json(dsId, param);
                HashMap hashMap = (HashMap)mapper.readValue(s, HashMap.class);
                if (hashMap.containsKey(dsId.toString())) {
                    map.put(dsId.toString(), hashMap.get(dsId.toString()));
                }
            }

            return mapper.writeValueAsString(map);
        }
    }

    public String executeSQLs4Json(List<Long> dsIds) throws IOException {
        return this.executeSQLs4Json(dsIds, (Map)null);
    }

    public String executeSQL4Json(Long dataSourceId) throws IOException {
        return this.executeSQL4Json(dataSourceId, (Map)null);
    }

    public String executeSQL4Json(Long dataSourceId, Map<String, Object> params) throws IOException {
        HlsDbDataSource t = new HlsDbDataSource();
        t.setDataSourceId(dataSourceId);
        List<HlsDbDataSource> query = this.dsMapper.query(t);
        if (CollectionUtils.isNotEmpty(query)) {
            HlsDbDataSource ds = (HlsDbDataSource)query.get(0);
            if (ds != null) {
                String type = ds.getUsageType();
                return this.executeSQL4Json(dataSourceId, type, SqlUtils.autoTranslate(ds.getSqlContext()), params);
            }
        }

        return "";
    }

    public List<Map<String, Object>> executeSQL4List(Long dataSourceId, Map<String, Object> params) {
        HlsDbDataSource ds = (HlsDbDataSource)this.dsMapper.selectByPrimaryKey(dataSourceId);
        return ds != null ? this.executeSQL(dataSourceId, ds.getSqlContext(), params) : null;
    }

    private String executeSQL4Json(Long dsId, String type, String sql, Map<String, Object> params) throws IOException {
        List<Map<String, Object>> maps = this.executeSQL(dsId, sql, params);
        HashMap<String, Object> map = new HashMap();
        HashMap table;
        if ("BOOKMARK".equalsIgnoreCase(type)) {
            table = this.getBookmarkMap(dsId);
            table.put("table", maps);
            map.put(dsId.toString(), table);
        } else {
            table = new HashMap();
            table.put("default", maps.stream().findFirst().orElse(new HashMap()));
            map.put(dsId.toString(), table);
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }

    private List<Map<String, Object>> executeSQL(Long dataSourceId, String sql, Map<String, Object> params) {
        if (StringUtils.isEmpty(sql)) {
            return new ArrayList(0);
        } else {
            if (params != null) {
                Set<Entry<String, Object>> entries = params.entrySet();

                String key;
                for(Iterator var5 = entries.iterator(); var5.hasNext(); sql = sql.replace("${" + key + "}", ":" + key)) {
                    Entry<String, Object> entry = (Entry)var5.next();
                    key = (String)entry.getKey();
                }
            }

            HlsDbDataSourceColumn hlsDbDataSourceColumn = new HlsDbDataSourceColumn();
            hlsDbDataSourceColumn.setDataSourceId(dataSourceId);
            hlsDbDataSourceColumn.setEnabledFlag("Y");
            List<HlsDbDataSourceColumn> columns = this.columnMapper.select(hlsDbDataSourceColumn);
            List<Map<String, Object>> maps = this.namedJdbcTemplate.queryForList(SqlUtils.autoTranslate(sql), params);
            if (maps != null && maps.size() >= 1) {
                if (columns != null && columns.size() > 0) {
                    maps = this.trans(maps, columns);
                }

                return maps;
            } else {
                return new ArrayList(0);
            }
        }
    }

    private List<Map<String, Object>> trans(@NotNull List<Map<String, Object>> maps, @NotNull List<HlsDbDataSourceColumn> columns) {
        ArrayList<Map<String, Object>> result = new ArrayList(maps.size());
        Iterator var4 = maps.iterator();

        while(true) {
            Map map;
            do {
                if (!var4.hasNext()) {
                    return result;
                }

                map = (Map)var4.next();
            } while(!(map instanceof LinkedCaseInsensitiveMap));

            HashMap<String, Object> resultMap = new HashMap(map.size());
            Iterator var7 = columns.iterator();

            while(var7.hasNext()) {
                HlsDbDataSourceColumn column = (HlsDbDataSourceColumn)var7.next();
                String aliasName = column.getAliasName();
                String columnName = column.getColumnName();
                resultMap.put(aliasName, map.get(columnName));
            }

            result.add(resultMap);
        }
    }

    private HashMap<String, Object> getBookmarkMap(Long dsId) {
        return new HashMap();
    }

    public List<String> getAllTables() {
        if (this.dbName != null && !"".equals(this.dbName.trim())) {
            String sql = "select TABLE_NAME from INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA='" + this.dbName + "'";
            List<String> tableNames = this.jdbcTemplate.queryForList(sql, String.class);
            return tableNames;
        } else {
            return new ArrayList(0);
        }
    }

    public List<String> getAllColumns(String tableName) {
        if (this.dbName != null && !"".equals(this.dbName.trim()) && tableName != null && !"".equals(tableName.trim())) {
            String sql = "select COLUMN_NAME from INFORMATION_SCHEMA.Columns where table_name='" + tableName + "' and table_schema=" + this.dbName;
            List<String> columns = this.jdbcTemplate.queryForList(sql, String.class);
            return columns;
        } else {
            return new ArrayList(0);
        }
    }

    private String generateSQL(long dataSourceId, List<HlsDbDataSourcePara> paramList, List<HlsDbDataSourceTb> tableList) {
        String sql = "";
        if (tableList != null && paramList != null && tableList.size() >= 1) {
            StringBuilder select = new StringBuilder("select ");
            StringBuilder on = new StringBuilder();
            StringBuilder from = new StringBuilder();
            StringBuilder where = new StringBuilder();

            for(int i = 0; i < tableList.size(); ++i) {
                HlsDbDataSourceTb table = (HlsDbDataSourceTb)tableList.get(i);
                String tableName = table.getDbObjectName();
                String aliasName = table.getAliasName();
                String baseTableFlag = table.getBaseTableFlag();
                String sp = this.generateSelectPartByTable(dataSourceId, table);
                if (sp != null) {
                    select.append(sp);
                    if (i < tableList.size() - 1) {
                        select.append(",");
                    }
                }

                if ("Y".equalsIgnoreCase(baseTableFlag)) {
                    from.append(" from ").append(tableName).append(" as ").append(aliasName);
                    where.append(" where ");
                } else {
                    String joinType = table.getJoinType();
                    on.append(joinType).append(" ").append(tableName).append(" as ").append(aliasName);
                    on.append(" on ");
                }
            }

            return "";
        } else {
            return sql;
        }
    }

    private String generateSelectPartByTable(@NotNull Long dsId, @NotNull HlsDbDataSourceTb table) {
        if (table != null && dsId != null) {
            List<HlsDbDataSourceColumn> columns = this.getColumnByDsTable(dsId, table.getDataSourceTbId());
            StringBuilder sb = new StringBuilder();
            if (columns != null && columns.size() >= 1) {
                String aliasName = table.getAliasName();

                for(int i = 0; i < columns.size(); ++i) {
                    HlsDbDataSourceColumn column = (HlsDbDataSourceColumn)columns.get(i);
                    sb.append(aliasName).append(".").append(column.getColumnName()).append(" as ").append(column.getAliasName());
                    if (i < columns.size() - 1) {
                        sb.append(",");
                    }
                }

                return sb.toString();
            } else {
                return null;
            }
        } else {
            throw new IllegalArgumentException("arguements cannot be null!");
        }
    }

    private String generateFromOrOnPartByTable(@NotNull HlsDbDataSourceTb table) {
        if (table == null) {
            throw new IllegalArgumentException("arguements cannot be null!");
        } else {
            StringBuilder sb = new StringBuilder(" from ");
            sb.append(table.getDbObjectName());
            if (table.getAliasName() == null || "".equals(table.getAliasName().trim())) {
                sb.append(" as ").append(table.getAliasName());
            }

            return sb.toString();
        }
    }

    private List<HlsDbDataSourceTbWh> getWhereByTable(long tableId) {
        HlsDbDataSourceTbWh where = new HlsDbDataSourceTbWh();
        where.setDataSourceTbId(tableId);
        where.setEnabledFlag("Y");
        return this.whereMapper.select(where);
    }

    private List<HlsDbDataSourceColumn> getColumnByDsTable(long dsId, long tableId) {
        HlsDbDataSourceColumn column = new HlsDbDataSourceColumn();
        column.setDataSourceId(dsId);
        column.setDataSourceTbId(tableId);
        column.setEnabledFlag("Y");
        return this.columnMapper.select(column);
    }

    private Map<Long, HlsDbDataSourcePara> convertPara(List<HlsDbDataSourcePara> paramList) {
        HashMap<Long, HlsDbDataSourcePara> paraMap = new HashMap();
        if (paramList != null) {
            Iterator var3 = paramList.iterator();

            while(var3.hasNext()) {
                HlsDbDataSourcePara param = (HlsDbDataSourcePara)var3.next();
                if (param != null && param.getDataSourceId() != null) {
                    paraMap.put(param.getDataSourceParaId(), param);
                }
            }
        }

        return paraMap;
    }
}
