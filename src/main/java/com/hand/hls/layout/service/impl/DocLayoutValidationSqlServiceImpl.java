package com.hand.hls.layout.service.impl;

import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.layout.dto.DocLayoutConfig;
import com.hand.hls.layout.mapper.DocLayoutConfigMapper;
import com.hand.hls.layout.service.IDocLayoutConfigService;
import com.hand.hls.layout.service.IDocLayoutValidationSqlService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class DocLayoutValidationSqlServiceImpl implements IDocLayoutValidationSqlService {
    @Autowired
    private IDocLayoutConfigService configService;
    @Autowired
    private SqlSessionFactory sessionFactory;
    @Autowired
    private DocLayoutConfigMapper configMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ResponseData queryLov(LeafRequestData requestData, String sqlId, HttpServletRequest request) {
        IRequest iRequest = RequestHelper.createServiceRequest(request);
        RequestHelper.setCurrentRequest(iRequest);
        if (StringUtils.isBlank(sqlId)) {
            throw new RuntimeException("empty validationSQL");
        }
        if (sqlId.startsWith("$")) {
//            sqlId
            String pagesize = request.getParameter("pagesize");
            String pagenum = request.getParameter("pagenum");
            MappedStatement mappedStatement = sessionFactory.getConfiguration().getMappedStatement(sqlId.substring(1));
            SqlSession sqlSession = sessionFactory.openSession();
            if (StringUtils.isNumeric(pagenum) && StringUtils.isNumeric(pagesize)) {
                PageHelper.startPage(Integer.valueOf(pagenum), Integer.valueOf(pagesize));
            }
            List<Object> objects = sqlSession.selectList(sqlId.substring(1), requestData.getParameter());
            return new ResponseData(objects);
        } else {
//            normal validationSQL
            logger.warn("SqlId : {} is not started with $", sqlId);
        }
        return new ResponseData(false);
    }

    @Override
    public ResponseData queryBox(LeafRequestData requestData, HttpServletRequest request) {
        IRequest iRequest = RequestHelper.createServiceRequest(request);
        RequestHelper.setCurrentRequest(iRequest);
        Map parameter = requestData.getParameter();
        Object tabCode = parameter.get("tab_code");
        Object layoutCode = parameter.get("layout_code");
        Object columnName = parameter.get("column_name");

        if(tabCode == null || layoutCode == null || columnName == null){
            return new ResponseData(false);
        }
        DocLayoutConfig config = new DocLayoutConfig();
        config.setTabCode(tabCode.toString());
        config.setLayoutCode(layoutCode.toString());
        config.setColumnName(columnName.toString());
        String validationSql = configMapper.selectValidationSql(config);
        if (validationSql!= null && validationSql.startsWith("$")) {
//            sqlId
            SqlSession sqlSession = sessionFactory.openSession();
            List<Object> objects = sqlSession.selectList(validationSql.substring(1), requestData.getParameter());
            return new ResponseData(objects);
        } else {
//            normal validationSQL
            logger.warn("SqlId : {} is not started with $", validationSql);
        }
        return new ResponseData(false);
    }
}
