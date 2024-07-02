package com.hand.hls.utils;

import com.hand.hap.generator.service.impl.DBUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uncertain.composite.CompositeMap;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/8/20
 * Time: 11:26
 */
public class DbUtils {
    private static SqlSessionFactory sqlSessionFactory;
    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private static SqlSessionFactory getSqlSessionFactory() {
        if (sqlSessionFactory == null) {
            synchronized (DbUtils.class) {
                if (sqlSessionFactory == null) {
                    sqlSessionFactory = SpringContextHolder.getBean(SqlSessionFactory.class);
                }
            }
        }
        return sqlSessionFactory;
    }


    public static List queryDataForList(String sqlId, Map<String, Object> params) {
        LinkedList<Object> list = new LinkedList<>();
        queryData(sqlId, params, (context -> {
            list.add(context.getResultObject());

        }));
        return list;
    }

    public static CompositeMap queryDataForCompositeMap(String sqlId, Map<String, Object> params) {
        CompositeMap compositeMap = new CompositeMap();
        queryData(sqlId, params, (context -> {
            Object resultObject = context.getResultObject();
            if (resultObject instanceof Map) {
                CompositeMap map = new CompositeMap();
                map.putAll((Map) resultObject);
                compositeMap.addChild(map);
            }
        }));
        return compositeMap;
    }

    public static void queryData(String sqlId, Map<String, Object> params, RowHandler rh) {
        SqlSessionFactory sessionFactory = getSqlSessionFactory();
        try (SqlSession sqlSession = sessionFactory.openSession()) {
            sqlSession.select(sqlId, params, rh::rowHandle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getPkFieldByTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) return "";
        String pkField = "";
        DataSource dataSource = SpringContextHolder.getBean(DataSource.class);
        try(Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            pkField = DBUtil.getPrimaryKey(tableName, metaData);
            if(pkField == null){
                pkField = DBUtil.getPrimaryKey(tableName.toUpperCase(), metaData);
            }
        } catch (SQLException e) {
            logger.error("can not find COLUMN_NAME by tableName [{}]", tableName);
        }
        return pkField;
    }

}
