package hls.layout.utils;

import com.hand.hls.exception.HlsCusException;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * @Author: Eric Chen
 * @Email : qiang.chen04@hand-china.com
 * @Date: 2019/3/14 13:55
 */
public class ServerSqlUtils {

    /**
     * 将 LeafRequestData.parameter的除了randomString 的属性拼成SQL ,
     *
     * @param data
     * @return
     */
    public static String generateWhereSql(LeafRequestData data) {
        String whereSql = "";
        if (data == null || data.getParameter() == null) {
            return null;
        }
        if (data.getParameter() != null) {
            Map<String, Object> parameter = data.getParameter();
            Object valueName = parameter.remove("value_name");
            String valueWhere = where("value_name", valueName);
            Iterator<Map.Entry<String, Object>> iterator = parameter.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                if (next.getKey().equals("_column_config_") || next.getKey().equals("randomString") || next.getKey().equals("sort_name") || next.getKey().equals("sort_order")) {
                    continue;
                }
                if (StringUtils.isNotBlank(whereSql)) {
                    whereSql += andWhere(next.getKey(), next.getValue());
                } else {
                    whereSql += where(next.getKey(), next.getValue());
                }
            }
            if (StringUtils.isEmpty(whereSql) && valueName != null) {
                whereSql = valueWhere;
            }
        }
        return whereSql;
    }

    /**
     * 将 LeafRequestData.parameter的sort_name/sort_order属性拼成SQL ,
     *
     * @param data
     * @return
     */
    public static String generateSortSql(LeafRequestData data) {
        String sortSql = "";
        if (data == null || data.getParameter() == null) {
            return null;
        }
        if (data.getParameter() != null) {
            Map<String, Object> parameter = data.getParameter();
            Iterator<Map.Entry<String, Object>> iterator = parameter.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = iterator.next();
                if (next.getKey().equals("sort_name")) {
                    String _colName = com.hand.hap.mybatis.util.OGNL.unCamel(next.getValue().toString());
                    sortSql += "order by " + _colName;
                }
                if (next.getKey().equals("sort_order")) {
                    sortSql += " " + next.getValue();
                }
            }
        }
        return sortSql;
    }

    public static String andWhere(Object key, Object value) {
        String whereSql = "";
//        if (StringUtils.isNumeric((String) value)) {
//            whereSql += " and " + key + " = " + value;
//        } else {
        if(value != null && StringUtils.isNotEmpty(value.toString())){
            boolean symbol = value.toString().contains("'");
            if(symbol){
                throw new IllegalArgumentException("输入包含非法字符!");
            }
        }
        whereSql += " and " + key + " like " + "'%" + value + "%'";
//        }
        return whereSql;
    }


    public static String where(Object key, Object value) {
        String whereSql = "";
//        if (StringUtils.isNumeric((String) value)) {
//            whereSql += key + " = " + value;
//        } else {
        if(value != null && StringUtils.isNotEmpty(value.toString())){
            boolean symbol = value.toString().contains("'");
            if(symbol){
                throw new IllegalArgumentException("输入包含非法字符!");
            }
        }
        whereSql += key + " like " + "'%" + value + "%'";
//        }
        return whereSql;
    }
}
