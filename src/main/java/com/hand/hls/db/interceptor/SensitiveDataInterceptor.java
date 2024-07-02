package com.hand.hls.db.interceptor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.sys.dto.SysUserAllocation;
import com.hand.hls.sys.dto.SysUserAuthorityRule;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2017-10-12
 * Time: 10:04
 * Modify by Eugene Song 2020-5-21 添加营销报备权限 && 租赁物抵质押物权限
 */
@Intercepts({
        @Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class})})
public class SensitiveDataInterceptor implements Interceptor {
    private String dbType;
    private Connection connection;
    private JdbcTemplate jdbcTemplate;
    private static final String DOC_PRJ_PREFIX = "PRJ_PROJECT";
    private static final String DOC_CON_PREFIX = "CON_CONTRACT";
    private static final String DOC_BP_PREFIX = "HLS_BP_MASTER";
    private static final String DOC_PRJ_CHANCE = "PRJ_CHANCE";
    private static final String DOC_HLS_CREDIT_LINE = "HLS_CREDIT_LINE";
    private static final String DOC_FCT_CHANCE = "FCT_CHANCE";
    private static final String DOC_FCT_PROJECT = "FCT_PROJECT";
    private static final String DOC_FCT_CONTRACT = "FCT_CONTRACT";
    private static final String DOC_HLS_MARKETING_REPORT = "HLS_MARKETING";
    private static final String DOC_HLS_LEASE_ITEM = "HLS_LEASE_ITEM";
    private static final String DOC_PRJ_PROJECT_MEETING_APPROVER = "PRJ_PROJECT_MEETING_APPROVER";
    private static final String DOC_HLS_CREDIT_LINE_CHANCE = "HLS_CREDIT_LINE_CHANCE";
    private static final String DOC_HLS_GENERAL_ISSUE = "HLS_GENERAL_ISSUE";
    private static final String DOC_JC_TRANSFER_APPLICATION = "JC_TRANSFER_APPLICATION";
    private static final String DOC_CSH_TRANSACTION = "CSH_TRANSACTION";
    private static final String DOC_CSH_PAYMENT_REQ_HD = "CSH_PAYMENT_REQ_HD";
    private static final String DOC_JC_ARCHIVE_BORROW = "JC_ARCHIVE_BORROW";


    private Logger logger = LoggerFactory.getLogger(getClass());

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

//    public static Map<String, List<String>> getRules() {
//        return rules;
//    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        IRequest currentRequest = RequestHelper.getCurrentRequest();
        if (currentRequest == null || "Y".equals(currentRequest.getAttribute("wflRuleControlFlag")) || isUserNeedFilter(currentRequest) || isCompanyNeedFilter(currentRequest)) {
            return invocation.proceed();
        }
        connection = (Connection) invocation.getArgs()[0];
        RoutingStatementHandler handler = getRoutingStatementHandler(invocation);
        StatementHandler delegate = (StatementHandler) getObject(handler, "delegate");
        BoundSql boundSql = delegate.getBoundSql();
        String sql = boundSql.getSql();
        String newSql = filter(sql);
        setObject(boundSql, "sql", newSql);
        return invocation.proceed();
    }

    /**
     * 按照公司id来查询，若是需要过滤，返回false
     *
     * @return false : 需要过滤
     */
    private boolean isCompanyNeedFilter(IRequest currentRequest) {
        if (currentRequest == null || currentRequest.getCompanyId() == null || currentRequest.getCompanyId() < 0) {
            return true;
        }
        Map<String, Object> attributeMap = currentRequest.getAttributeMap();
        String needFilter = String.valueOf(attributeMap.getOrDefault("authorityRuleFlag", "N"));
        return needFilter.length() < 1 || "N".equalsIgnoreCase(needFilter);
    }

    private boolean isUserNeedFilter(IRequest currentRequest) {
        return currentRequest == null || currentRequest.getUserId() == null || currentRequest.getUserId() < 0;
    }

    public RoutingStatementHandler getRoutingStatementHandler(Object invocation) {
        Object target = invocation;
        if (target instanceof Invocation) {
            return getRoutingStatementHandler(((Invocation) target).getTarget());
        } else if (target instanceof RoutingStatementHandler) {
            return (RoutingStatementHandler) target;
        } else if (target instanceof Proxy) {
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(target);
            Object targetObj = getObject(invocationHandler, "target");
            return getRoutingStatementHandler(targetObj);
        }
        return null;
    }

    private String filter(String sql) {
        IRequest currentRequest = RequestHelper.getCurrentRequest();
        if (currentRequest == null || isUserNeedFilter(currentRequest)) {
            return sql;
        }
        Object attribute = currentRequest.getAttribute(IRequest.MDC_PREFIX.concat(SysUserAllocation.FIELD_ALLOCATION_ID));
        if (attribute == null) {
            return sql;
        }
        // 从session中获取的allocationId
        Long allocationId = Long.valueOf(String.valueOf(attribute));
        SQLStatementParser parser = null;

        // 开始解析sql
        switch (dbType) {
            case "mysql":
                parser = new MySqlStatementParser(sql);
                break;
            case "oracle":
                parser = new OracleStatementParser(sql);
                break;
            default:
                throw new RuntimeException("could not found proper statement parser. dbType:" + String.valueOf(dbType));
        }
        List<SQLStatement> statementList = parser.parseStatementList();
        if (statementList == null || statementList.size() < 1) {
            return sql;
        }

        SQLStatement sqlStatement = statementList.get(0);
        if (!(sqlStatement instanceof SQLSelectStatement)) {
            return sql;
        }
        SQLSelect select = ((SQLSelectStatement) sqlStatement).getSelect();
        dealQuery(allocationId, select);
        return SQLUtils.toSQLString(statementList, dbType);

    }

    private void dealQuery(Long allocationId, SQLSelect select) {
        if (select == null || allocationId == null) {
            return;
        }
        SQLSelectQuery queryPart = select.getQuery();
        dealQueryBlock(allocationId, queryPart);
    }

    private void dealQueryBlock(Long allocationId, SQLSelectQuery selectQuery) {
        if (selectQuery == null || allocationId == null) {
            return;
        }
        if (selectQuery instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock query = (SQLSelectQueryBlock) selectQuery;
            SQLTableSource from = query.getFrom();

            // select part
            List<SQLSelectItem> selectList = query.getSelectList();
            selectList.forEach(selectItem -> {
                if (selectItem.getExpr() instanceof SQLQueryExpr) {
                    SQLQueryExpr expr = (SQLQueryExpr) selectItem.getExpr();
                    SQLSelect subQuery = expr.getSubQuery();
                    dealQuery(allocationId, subQuery);
                }
            });

            // from part
            dealSQLTableSource(allocationId, from, query);

        } else if (selectQuery instanceof SQLUnionQuery) {
            SQLSelectQuery left = ((SQLUnionQuery) selectQuery).getLeft();
            SQLSelectQuery right = ((SQLUnionQuery) selectQuery).getRight();
            dealQueryBlock(allocationId, left);
            dealQueryBlock(allocationId, right);
        }
    }

    private void dealSQLTableSource(Long allocationId, SQLTableSource tableSource, SQLSelectQueryBlock query) {
        if (tableSource == null || allocationId == null) {
            return;
        }
        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinFrom = (SQLJoinTableSource) tableSource;
            SQLTableSource left = joinFrom.getLeft();
            SQLTableSource right = joinFrom.getRight();
            dealSQLTableSource(allocationId, left, query);
            dealSQLTableSource(allocationId, right, query);
        } else if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) tableSource;
            SQLSelect select = subqueryTableSource.getSelect();
            dealQuery(allocationId, select);
        } else {
            SQLTableSource conContract = tableSource.findTableSource("con_contract");
            SQLTableSource prjProject = tableSource.findTableSource("prj_project");
            SQLTableSource bpMaster = tableSource.findTableSource("hls_bp_master");
            SQLTableSource prjChance = tableSource.findTableSource("prj_chance");
            SQLTableSource hlsCreditLine = tableSource.findTableSource("hls_credit_line");
            SQLTableSource fctChance = tableSource.findTableSource("fct_chance");
            SQLTableSource fctProject = tableSource.findTableSource("fct_project");
            SQLTableSource fctContract = tableSource.findTableSource("fct_contract");
            SQLTableSource hlsMarketingReport = tableSource.findTableSource("hls_marketing_report");
            SQLTableSource hlsLeaseItem = tableSource.findTableSource("hls_lease_item");
            SQLTableSource prjProjectMeetingApprover = tableSource.findTableSource("prj_project_meeting_approver");
            SQLTableSource hlsCreditLineChance = tableSource.findTableSource("hls_credit_line_chance");
            SQLTableSource hlsgeneralissue = tableSource.findTableSource("hls_general_issue");
            SQLTableSource jcTransferApplication = tableSource.findTableSource("jc_transfer_application");
            SQLTableSource jcArchiveBorrow = tableSource.findTableSource("jc_archive_borrow");

            SQLTableSource cshtransaction = tableSource.findTableSource("csh_transaction");

            SQLTableSource cshpaymentreqhd = tableSource.findTableSource("csh_payment_req_hd");

            if (conContract != null) {
                // sql的最外层查询包含的con_contract表
                String alias = conContract.getAlias();
                getConSQLPart(alias, allocationId, query);
            }

            if (prjProject != null) {
                // sql的最外层查询包含的prj_project表
                String alias = prjProject.getAlias();
                getPrjSQLPart(alias, allocationId, query);
            }

            if (prjChance != null) {
                // sql的最外层查询包含的prj_project表
                String alias = prjChance.getAlias();
                getPrjChanceSQLPart(alias, allocationId, query);
            }

            if (bpMaster != null) {
                String alias = bpMaster.getAlias();
                getBpSQLPart(alias, allocationId, query);
            }

            if (hlsCreditLine != null) {
                String alias = hlsCreditLine.getAlias();
                getCrdtSQLPart(alias, allocationId, query);
            }

            if (fctChance != null) {
                String alias = fctChance.getAlias();
                getFctChanceSQLPart(alias, allocationId, query);
            }

            if (fctProject != null) {
                String alias = fctProject.getAlias();
                getFctPrjSQLPart(alias, allocationId, query);
            }

            if (fctContract != null) {
                String alias = fctContract.getAlias();
                getFctConSQLPart(alias, allocationId, query);
            }

            if (hlsMarketingReport != null) {
                String alias = hlsMarketingReport.getAlias();
                getHlsMarketingSQLPart(alias, allocationId, query);
            }

            if (hlsLeaseItem != null) {
                String alias = hlsLeaseItem.getAlias();
                getHlsLeaseItemSQLPart(alias, allocationId, query);
            }

            if (prjProjectMeetingApprover != null) {
                String alias = prjProjectMeetingApprover.getAlias();
                getPrjProjectMeetingApproverSQLPart(alias, allocationId, query);
            }

            if (hlsCreditLineChance != null) {
                String alias = hlsCreditLineChance.getAlias();
                getHlsCreditLineChanceSQLPart(alias, allocationId, query);
            }

            if (hlsgeneralissue != null) {
                String alias = hlsgeneralissue.getAlias();
                getHlsgeneralissueSQLPart(alias, allocationId, query);
            }

            if (jcTransferApplication != null) {
                String alias = jcTransferApplication.getAlias();
                getJcTransferApplicationSQLPart(alias, allocationId, query);
            }
            if (cshtransaction != null) {
                String alias = cshtransaction.getAlias();
                getCshtransactionSQLPart(alias, allocationId, query);
            }
            if (cshpaymentreqhd != null) {
                String alias = cshpaymentreqhd.getAlias();
                getcshpaymentreqidSQLPart(alias, allocationId, query);
            }
            if (jcArchiveBorrow != null) {
                String alias = jcArchiveBorrow.getAlias();
                getJcArchiveSQLPart(alias, allocationId, query);
            }
        }
    }

    private void getBpSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "hls_bp_master";
        }
        getSQLPart(alias, allocationId, query, DOC_BP_PREFIX);
    }

    private void getPrjSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "prj_project";
        }
        getSQLPart(alias, allocationId, query, DOC_PRJ_PREFIX);
    }

    private void getPrjChanceSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "prj_chance";
        }
        getSQLPart(alias, allocationId, query, DOC_PRJ_CHANCE);
    }

    private void getConSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "con_contract";
        }
        getSQLPart(alias, allocationId, query, DOC_CON_PREFIX);
    }

    private void getCrdtSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "hls_credit_line";
        }
        getSQLPart(alias, allocationId, query, DOC_HLS_CREDIT_LINE);
    }

    private void getFctChanceSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "fct_chance";
        }
        getSQLPart(alias, allocationId, query, DOC_FCT_CHANCE);
    }

    private void getFctPrjSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "fct_project";
        }
        getSQLPart(alias, allocationId, query, DOC_FCT_PROJECT);
    }

    private void getFctConSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "fct_contract";
        }
        getSQLPart(alias, allocationId, query, DOC_FCT_CONTRACT);
    }


    private void getHlsMarketingSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "hls_marketing_report";
        }
        getSQLPart(alias, allocationId, query, DOC_HLS_MARKETING_REPORT);
    }

    private void getHlsLeaseItemSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "hls_lease_item";
        }
        getSQLPart(alias, allocationId, query, DOC_HLS_LEASE_ITEM);
    }

    private void getPrjProjectMeetingApproverSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "prj_project_meeting_approver";
        }
        getSQLPart(alias, allocationId, query, DOC_PRJ_PROJECT_MEETING_APPROVER);
    }

    private void getHlsCreditLineChanceSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "hls_credit_line_chance";
        }
        getSQLPart(alias, allocationId, query, DOC_HLS_CREDIT_LINE_CHANCE);
    }

    private void getHlsgeneralissueSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "hls_general_issue";
        }
        getSQLPart(alias, allocationId, query, DOC_HLS_GENERAL_ISSUE);
    }

    private void getCshtransactionSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "csh_transaction";
        }
        getSQLPart(alias, allocationId, query, DOC_HLS_GENERAL_ISSUE);
    }
    private void getcshpaymentreqidSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "csh_payment_req_hd";
        }
        getSQLPart(alias, allocationId, query, DOC_CSH_PAYMENT_REQ_HD);
    }
    private void getJcTransferApplicationSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "jc_transfer_application";
        }
        getSQLPart(alias, allocationId, query, DOC_JC_TRANSFER_APPLICATION);
    }
    private void getJcArchiveSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query) {
        if (StringUtils.isEmpty(alias)) {
            alias = "jc_archive_borrow";
        }
        getSQLPart(alias, allocationId, query, DOC_JC_ARCHIVE_BORROW);
    }


    private void getSQLPart(String alias, Long allocationId, SQLSelectQueryBlock query, String doc) {
        IRequest currentRequest = RequestHelper.getCurrentRequest();
        if (currentRequest == null) {
            return;
        }
        Object userRules = currentRequest.getAttribute("userRules");
        if (userRules instanceof List) {
            List<SysUserAuthorityRule> rules = (List<SysUserAuthorityRule>) userRules;
            List<String> ruleStrings = rules.stream().filter(rule -> doc.equals(rule.getDocumentCategory())).map(SysUserAuthorityRule::getAuthorityRuleString).collect(Collectors.toList());
            dealWhere(query, alias, doc, ruleStrings);
        }
    }

    private List<SysUserAuthorityRule> querySysUserAuthorityRules(Long allocationId, String doc) throws SQLException {
        String sql = "select * from sys_user_authority_rule where allocation_id = " + allocationId + " and document_category = '" + doc + "'";
        if (jdbcTemplate != null) {
            return jdbcTemplate.queryForList(sql, SysUserAuthorityRule.class);
        }
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        ArrayList<SysUserAuthorityRule> rules = new ArrayList<>(5);
        while (resultSet.next()) {
            SysUserAuthorityRule sysUserAuthorityRule = new SysUserAuthorityRule();
            sysUserAuthorityRule.setAllocationId(allocationId);
            sysUserAuthorityRule.setDocumentCategory(doc);
            String authority_rule_string = resultSet.getString("authority_rule_string");
            sysUserAuthorityRule.setAuthorityRuleString(authority_rule_string);
            rules.add(sysUserAuthorityRule);
        }
        resultSet.close();
        statement.close();
        return rules;
    }

    private void dealWhere(SQLSelectQueryBlock query, String alias, String documentCatagory, List<String> rules) {
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }
        String columnPrefix = " " + alias + ".authority_rule_string like '";
        String columnSuffix = "' ";
        ArrayList<String> params = new ArrayList<>();
        for (int i = 0; i < rules.size(); i++) {
            params.add(columnPrefix + rules.get(i) + columnSuffix);
        }

        String id_field = null;
        Long userId = RequestHelper.getCurrentRequest().getUserId();
        switch (documentCatagory) {
            case DOC_BP_PREFIX:
                id_field = alias + ".bp_id";
                break;
            case DOC_CON_PREFIX:
            case DOC_FCT_CONTRACT:
                id_field = alias + ".contract_id";
                break;
            case DOC_PRJ_PREFIX:
            case DOC_FCT_PROJECT:
            case DOC_FCT_CHANCE:
                id_field = alias + ".project_id";
                break;
            case DOC_PRJ_CHANCE:
                id_field = alias + ".chance_id";
                break;
            case DOC_HLS_CREDIT_LINE:
                id_field = alias + ".credit_line_id";
                break;
            case DOC_HLS_MARKETING_REPORT:
                id_field = alias + ".marketing_report_id";
                break;
            case DOC_HLS_LEASE_ITEM:
                id_field = alias + ".lease_item_id";
                break;
            case DOC_PRJ_PROJECT_MEETING_APPROVER:
                id_field = alias + ".approver_record_id";
                break;
            case DOC_HLS_CREDIT_LINE_CHANCE:
                id_field = alias + ".chance_id";
                break;
            case DOC_HLS_GENERAL_ISSUE:
                id_field = alias + ".general_id";
                break;
            case DOC_JC_TRANSFER_APPLICATION:
                id_field = alias + ".transfer_application_id";
                break;
            case DOC_CSH_TRANSACTION:
                id_field = alias + ".transaction_id";
                break;
            case DOC_CSH_PAYMENT_REQ_HD:
                id_field = alias + ".payment_req_id";
                break;
            case DOC_JC_ARCHIVE_BORROW:
                id_field = alias + ".borrow_id";
                break;
            default:
                break;
        }
        if (id_field != null) {
            String whiteListSql = "  " + id_field + " in " +
                    "(select document_id " +
                    " from   SYS_USER_AUTHORITY_TRX " +
                    " where " +
                    "        document_category =  '" + documentCatagory + "' " +
                    "    and document_id =  " + id_field +
                    "    and user_id = " + userId + ") ";
            logger.debug("add white list sql: ({})", whiteListSql);
            params.add(whiteListSql);
        }
        String or = StringUtils.join(params, "or");
        SQLExprParser sqlExprParser = new SQLExprParser("(" + or + ")");
        query.addCondition(sqlExprParser.expr());
    }


    private String getPrjKey(String id) {
        return getKey(DOC_PRJ_PREFIX, id);
    }

    private String getConKey(String id) {
        return getKey(DOC_CON_PREFIX, id);
    }

    private String getKey(String id, String doc) {
        return doc + "|" + id;
    }

    public Object getObject(Object object, String name) {
        Field delegate = null;
        try {
            delegate = object.getClass().getDeclaredField(name);
            if (delegate != null) {
                delegate.setAccessible(true);
                Object o = delegate.get(object);
                delegate.setAccessible(false);
                return o;
            }
        } catch (NoSuchFieldException e) {
            //pass
        } catch (IllegalAccessException e) {
            //pass
        }
        return null;
    }

    public void setObject(Object object, String name, Object value) {
        try {
            Field declaredField = object.getClass().getDeclaredField(name);
            declaredField.setAccessible(true);
            declaredField.set(object, value);
            declaredField.setAccessible(false);
        } catch (Exception e) {
            //pass
        }
        return;
    }

    @Override
    public Object plugin(Object o) {
        return Plugin.wrap(o, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

}
