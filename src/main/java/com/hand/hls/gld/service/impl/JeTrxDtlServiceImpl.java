package com.hand.hls.gld.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.csh.dto.HlsCusCshTransaction;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshTransactionMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.dto.*;
import com.hand.hls.gld.mapper.*;
import com.hand.hls.gld.service.*;
import com.hand.hls.ruleengine.dto.HlsCusGldJeRuleEngineExtend;
import com.hand.hls.ruleengine.mapper.HlsCusGldJeRuleEngineExtendMapper;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.utils.MathUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.hand.hls.sys.utils.OracleUtils.nvl;

@Service
@Transactional
public class JeTrxDtlServiceImpl extends BaseServiceImpl<JeTrxDtl> implements IJeTrxDtlService {

    protected static final int DEFAULT_PAGE = 1;
    protected static final int DEFAULT_PAGE_SIZE = 10;
    protected static final String CSH_WRITE_OFF = "CSH_WRITE_OFF";
    protected static final String CSH_TRANSACTION = "CSH_TRANSACTION";
    protected static final String CONTRACT_INCEPT = "CONTRACT_INCEPT";
    protected static final String FIN_INCOME_RECOGNITION = "FIN_INCOME_RECOGNITION";
    protected static final String ACR_INVOICE_CONFIRM = "ACR_INVOICE_CONFIRM";
    protected static final String JE_TRX_DTL_ID = "FIX-JE_TRX_DTL_ID";
    protected static final String COMPANY_ID = "FIX-COMPANY_ID";
    protected static final String JE_TRX = "FIX-JE_TRX";
    protected static final String PERIOD_NAME = "FIX-PERIOD_NAME";
    protected static final String JE_DATE = "FIX-JE_DATE";
    protected static final String JE_POST_FLAG = "POST";//凭证确认标志
    protected static final String REVERSE_FLAG_N = "N";//未反冲标志
    protected static final String REVERSE_FLAG_W = "W";//被反冲标志
    protected static final String REVERSE_FLAG_R = "R";//反冲标志
    protected static final String OPERATE_TYPE_CREATE = "CREATE";//凭证流水方式-制证
    protected static final String OPERATE_TYPE_REVERSE = "REVERSE";//凭证流水方式-反冲
    protected static final String RULE_HEAD = "HEAD";//凭证模板反冲规则-头
    protected static final String RULE_LINE = "LINE";//凭证模板反冲规则-行
    protected static final String RULE_HEAD_RETURN = "HEAD_RETURN";//凭证模板反冲规则-头+回退
    protected static final String NEGATIVE_RULE = "NEGATIVE";//负借负贷
    protected static final String OPPOSITE_RULE = "OPPOSITE";//借贷相反
    protected static final String CONFIRM = "CONFIRM";
    protected static final String CR = "CR";
    protected static final String DR = "DR";
    public static final String NEW = "NEW";

    protected static final String PAY_EQIPMENT = "PAY_EQIPMENT";
    protected static final String CSH_TRANSACTION_PAYMENT = "CSH_TRANSACTION_PAYMENT";
    protected static final String CSH_DEDUCTION = "CSH_DEDUCTION";
    protected static final String TRE_ACCRUED_INTEREST = "TRE_ACCRUED_INTEREST";

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private IJeTemplateService jeTemplateService;
    @Autowired
    private IJeLineService lineService;
    @Autowired
    private FinancialAttributeMapper financialAttributeMapper;
    @Autowired
    private SetOfBooksMapper setOfBooksMapper;
    @Autowired
    private PeriodMapper periodMapper;
    @Autowired
    private JeTrxDtlMapper jeTrxDtlMapper;
    @Autowired
    private HlsCusCshWriteOffMapper cshWriteOffMapper;
    @Autowired
    private HlsCusCshTransactionMapper cshTransactionMapper;
    /*@Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;*/
    @Autowired
    private IJeTrxJsonDtlService jeTrxJsonDtlService;
    @Autowired
    private JeRuleEngineMapper jeRuleEngineMapper;
    @Autowired
    private Datasource2Json datasource2Json;
    @Autowired
    private IHLSRuleEngineInitService hLSRuleEngineInitService;
    @Autowired
    private HlsCusJeHeadMapper jeHeadMapper;
    @Autowired
    private IJeHeadService jeHeadService;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    /*    @Autowired
        private HlsCusConContractInceptMapper hlsCusConContractInceptMapper;*/
    @Autowired
    private HlsCusContractFinanceIncomeMapper contractFinanceIncomeMapper;
    @Autowired
    private JeLineMapper lineMapper;
    @Autowired
    private HlsCusGldJeRuleEngineExtendMapper ruleEngineExtendMapper;
    @Autowired
    private HlsCusJeHdTemplateMapper jeHdTemplateMapper;

    private Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 生成凭证行
     *
     * @param iRequest
     * @param ids
     */
    @Override
    public ResponseData createJeLine(IRequest iRequest, List<Long> ids) {
        ResponseData answer = new ResponseData(true);
        String msg;
        final List<JeTrxDtl> jeTrxDtls = new ArrayList<>();
        ids.forEach(item -> {
            JeTrxDtl jeTrxDtl = jeTrxDtlMapper.selectByPrimaryKey(item);
            //先判断流水是否被确认
            if (JE_POST_FLAG.equalsIgnoreCase(jeTrxDtl.getStatus())) {
                return;
            } else {
                jeTrxDtls.add(jeTrxDtl);
            }
        });
        for (JeTrxDtl item : jeTrxDtls) {
            JeTrxJsonDtl jeTrxJsonDtl;
            String json;
            Long jeTrxId = item.getJeTrxId();
            Long companyId = iRequest.getCompanyId();
            Long jeTrxDtlId = item.getJeTrxDtlId();
            String jeTrx = item.getJeTrx();
            JeRuleEngine jeRuleEngine = new JeRuleEngine();
            jeRuleEngine.setCompanyId(companyId);
            jeRuleEngine.setJeTrx(jeTrx);
            jeRuleEngine = jeRuleEngineMapper.selectOne(jeRuleEngine);
            if (jeRuleEngine == null) {
                item.setStatus("ERROR");
                item.setMsgLog("无法在凭证规则树中找到对应的JeTrx: " + jeTrx);
                self().updateByPrimaryKeySelective(iRequest, item);
                return new ResponseData(false, "无法在凭证规则树中找到对应的JeTrx" + jeTrx);
            }
            Long dataSourceId = jeRuleEngine.getDataSourceId();//数据源ID
            Long ruleEngineId = jeRuleEngine.getRuleEngineId();//规则引擎ID
            try {
                Map param = new HashMap<>();
                param.put("jeTrxId", jeTrxId);
                json = datasource2Json.executeSQL4Json(dataSourceId, param);
            } catch (IOException e) {
                item.setStatus("ERROR");
                item.setMsgLog("获取数据源json值出错");
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("获取数据源json值出错",e);
                return new ResponseData(false, "获取数据源json值出错");
            }
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject defaultJson = JSON.parseObject(JSON.toJSONString(jsonObject.get(dataSourceId.toString())));
            JSONObject jsonValue;
            try {
                jsonValue = JSON.parseObject(JSON.toJSONString(defaultJson.getJSONObject("default")));//获取数据源的json
            } catch (Exception e) {
                item.setStatus("ERROR");
                item.setMsgLog("从数据源取得的json数据有误,请确认json的格式与数据的完好性");
                logger.error("从数据源取得的json数据有误,请确认json的格式与数据的完好性",e);
                self().updateByPrimaryKeySelective(iRequest, item);
                return new ResponseData(false, "从数据源取得的json数据有误,请确认json的格式与数据的完好性");
            }
            jsonValue.put("ruleEngineId", ruleEngineId);

            jeTrxJsonDtl = new JeTrxJsonDtl();
            jeTrxJsonDtl.setJeTrxDtlId(jeTrxDtlId);
            jeTrxJsonDtl.setJsonData(JSON.toJSONString(jsonValue));

            JeTrxJsonDtl boolCondition = hasJson(iRequest, jeTrxDtlId);
            if (boolCondition != null) {
                boolCondition.setJsonData(JSON.toJSONString(jsonValue));
                jeTrxJsonDtlService.updateByPrimaryKeySelective(iRequest, boolCondition);
            } else {
                jeTrxJsonDtlService.insertSelective(iRequest, jeTrxJsonDtl);//插入凭证事务流水数据表
            }

            json = JSON.toJSONString(jsonValue);
            Map<String, Object> stringObjectMap = hLSRuleEngineInitService.gldRuleEngineInit(iRequest, JSON.parseObject(json));
            String[] routeResult = (String[]) stringObjectMap.get("routeResult");//对应的模板ID
            Map result = (Map) stringObjectMap.get("gldRouteResult");

            List<String> routeResultList = new ArrayList();//凭证行模板ID
            List<String> gldRouteResult = new ArrayList();//凭证规则树节点扩展ID
            Map<Long, Long> hdLnMap = new HashMap();
            //处理凭证行和汇总规则关系，每个凭证模板必须配置凭证汇总规则，否则将不会生成该行凭证
            for (int i = 0; i < routeResult.length; i++) {
                if (!"".equalsIgnoreCase(routeResult[i])) {
                    String extendIds = (String) result.get(routeResult[i]);
                    String[] extendId = extendIds.split("\\$");
                    for (int j = 0; j < extendId.length; j++) {
                        hdLnMap.put(Long.valueOf(routeResult[i]), Long.valueOf(extendId[j]));
                        gldRouteResult.add(extendId[j]);
                        routeResultList.add(routeResult[i]);
                    }
                }
            }

            if (routeResult.length == 0) {
                item.setStatus("IGNORE");
                item.setMsgLog("未匹配到模板");
                self().updateByPrimaryKeySelective(iRequest, item);
                return new ResponseData(false, "未匹配到模板");
            }

            try {
                if (OPERATE_TYPE_REVERSE.equalsIgnoreCase(item.getOperateType())) {
                    self().reverseLine(iRequest, item, JSON.parseObject(json));
                } else {
                    self().updateGldJeLineData(iRequest, routeResultList, gldRouteResult, item.getJeTrxDtlId(), JSON.parseObject(json));

                }
            } catch (ParseException e) {
                msg = "请核对日期的格式";
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("请核对日期的格式",e);
                return new ResponseData(false, msg);
            } catch (JSONException e) {
                msg = "数据源获取的json数据格式无法与凭证匹配";
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("数据源获取的json数据格式无法与凭证匹配",e);
                return new ResponseData(false, msg);
            } catch (IllegalArgumentException e) {
                msg = e.getMessage();
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("不合法的参数",e);
                return new ResponseData(false, msg);
            } catch (NullPointerException e) {
                msg = "数据源配置有误,请检查字段";
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("数据源配置有误",e);
                return new ResponseData(false, msg);
            }
            item.setStatus("POST");
            item.setMsgLog("凭证行创建成功");
            self().updateByPrimaryKeySelective(iRequest, item);
        }
        answer.setMessage("凭证行创建成功");
        return answer;
    }

    /**
     * 生成凭证行
     *
     * @param iRequest
     * @param ids
     */
    @Override
    public ResponseData createJeLine(IRequest iRequest, List<Long> ids, Map param) {
        ResponseData answer = new ResponseData(true);
        String msg;
        final List<JeTrxDtl> jeTrxDtls = new ArrayList<>();
        ids.forEach(item -> {
            JeTrxDtl jeTrxDtl = jeTrxDtlMapper.selectByPrimaryKey(item);
            //先判断流水是否被确认
            if (JE_POST_FLAG.equalsIgnoreCase(jeTrxDtl.getStatus())) {
                return;
            } else {
                jeTrxDtls.add(jeTrxDtl);
            }
        });
        for (JeTrxDtl item : jeTrxDtls) {
            JeTrxJsonDtl jeTrxJsonDtl;
            String json;
            Long jeTrxId = item.getJeTrxId();
            Long companyId = item.getCompanyId();
            Long jeTrxDtlId = item.getJeTrxDtlId();
            String jeTrx = item.getJeTrx();
            JeRuleEngine jeRuleEngine = new JeRuleEngine();
            jeRuleEngine.setCompanyId(companyId);
            jeRuleEngine.setJeTrx(jeTrx);
            jeRuleEngine = jeRuleEngineMapper.selectOne(jeRuleEngine);
            if (jeRuleEngine == null) {
                item.setStatus("ERROR");
                item.setMsgLog("无法在凭证规则树中找到对应的JeTrx: " + jeTrx);
                self().updateByPrimaryKeySelective(iRequest, item);
                return new ResponseData(false, "无法在凭证规则树中找到对应的JeTrx" + jeTrx);
            }
            Long dataSourceId = jeRuleEngine.getDataSourceId();//数据源ID
            Long ruleEngineId = jeRuleEngine.getRuleEngineId();//规则引擎ID
            try {
                //Map param = new HashMap<>();
                param.put("jeTrxId", jeTrxId);
                json = datasource2Json.executeSQL4Json(dataSourceId, param);
            } catch (IOException e) {
                item.setStatus("ERROR");
                item.setMsgLog("获取数据源json值出错");
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("获取数据源json值出错", e);
                return new ResponseData(false, "获取数据源json值出错");
            }
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject defaultJson = JSON.parseObject(JSON.toJSONString(jsonObject.get(dataSourceId.toString())));
            JSONObject jsonValue;
            try {
                jsonValue = JSON.parseObject(JSON.toJSONString(defaultJson.getJSONObject("default")));//获取数据源的json
            } catch (Exception e) {
                item.setStatus("ERROR");
                item.setMsgLog("从数据源取得的json数据有误,请确认json的格式与数据的完好性");
                logger.error("从数据源取得的json数据有误,请确认json的格式与数据的完好性", e);
                self().updateByPrimaryKeySelective(iRequest, item);
                return new ResponseData(false, "从数据源取得的json数据有误,请确认json的格式与数据的完好性");
            }
            jsonValue.put("ruleEngineId", ruleEngineId);

            jeTrxJsonDtl = new JeTrxJsonDtl();
            jeTrxJsonDtl.setJeTrxDtlId(jeTrxDtlId);
            jeTrxJsonDtl.setJsonData(JSON.toJSONString(jsonValue));

            JeTrxJsonDtl boolCondition = hasJson(iRequest, jeTrxDtlId);
            if (boolCondition != null) {
                boolCondition.setJsonData(JSON.toJSONString(jsonValue));
                jeTrxJsonDtlService.updateByPrimaryKeySelective(iRequest, boolCondition);
            } else {
                jeTrxJsonDtlService.insertSelective(iRequest, jeTrxJsonDtl);//插入凭证事务流水数据表
            }

            json = JSON.toJSONString(jsonValue);
            Map<String, Object> stringObjectMap = hLSRuleEngineInitService.gldRuleEngineInit(iRequest, JSON.parseObject(json));
            String[] routeResult = (String[]) stringObjectMap.get("routeResult");//对应的模板ID
            Map result = (Map) stringObjectMap.get("gldRouteResult");

            List<String> routeResultList = new ArrayList();//凭证行模板ID
            List<String> gldRouteResult = new ArrayList();//凭证规则树节点扩展ID
            Map<Long, Long> hdLnMap = new HashMap();
            //处理凭证行和汇总规则关系，每个凭证模板必须配置凭证汇总规则，否则将不会生成该行凭证
            for (int i = 0; i < routeResult.length; i++) {
                if (!"".equalsIgnoreCase(routeResult[i])) {
                    String extendIds = (String) result.get(routeResult[i]);
                    String[] extendId = extendIds.split("\\$");
                    for (int j = 0; j < extendId.length; j++) {
                        hdLnMap.put(Long.valueOf(routeResult[i]), Long.valueOf(extendId[j]));
                        gldRouteResult.add(extendId[j]);
                        routeResultList.add(routeResult[i]);
                    }
                }
            }

            if (routeResult.length == 0) {
                item.setStatus("IGNORE");
                item.setMsgLog("未匹配到模板");
                self().updateByPrimaryKeySelective(iRequest, item);
                return new ResponseData(false, "未匹配到模板");
            }

            try {
                if (OPERATE_TYPE_REVERSE.equalsIgnoreCase(item.getOperateType())) {
                    self().reverseLine(iRequest, item, JSON.parseObject(json));
                } else {
                    self().updateGldJeLineData(iRequest, routeResultList, gldRouteResult, item.getJeTrxDtlId(), JSON.parseObject(json));
                }
            } catch (ParseException e) {
                msg = "请核对日期的格式";
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("请核对日期的格式", e);
                return new ResponseData(false, msg);
            } catch (JSONException e) {
                msg = "数据源获取的json数据格式无法与凭证匹配";
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("数据源获取的json数据格式无法与凭证匹配", e);
                return new ResponseData(false, msg);
            } catch (IllegalArgumentException e) {
                msg = e.getMessage();
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("不合法的参数", e);
                return new ResponseData(false, msg);
            } catch (NullPointerException e) {
                msg = "数据源配置有误,请检查字段";
                item.setStatus("ERROR");
                item.setMsgLog(msg);
                self().updateByPrimaryKeySelective(iRequest, item);
                logger.error("数据源配置有误", e);
                return new ResponseData(false, msg);
            }
            item.setStatus("POST");
            item.setMsgLog("凭证行创建成功");
            self().updateByPrimaryKeySelective(iRequest, item);
        }
        answer.setMessage("凭证行创建成功");
        return answer;
    }

    /**
     * 新增模板头
     *
     * @param request
     * @param jeTemplateIds
     * @param gldRouteResult
     * @param sourceId
     * @param json
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateGldJeLineData(IRequest request, List<String> jeTemplateIds, List<String> gldRouteResult, Long sourceId, JSONObject json) throws ParseException, JSONException, NullPointerException, IllegalArgumentException {

        List<JeLine> jeLineList = new ArrayList<>();

        for (int i = 0; i < jeTemplateIds.size(); i++) {
            String jeTemplateId = jeTemplateIds.get(i);//凭证模板ID
            String extendId = gldRouteResult.get(i);//拓展ID
            JeTemplate je = new JeTemplate();
            JeLine jeLine;
            //当取出的集合中某个数据为空时,直接进入下一次循环
            if (StringUtils.isEmpty(jeTemplateId))
                continue;
            je.setJeTemplateId(Long.valueOf(jeTemplateId));
            List<JeTemplate> jeTemplateList = jeTemplateService.selectWithDesc(request, je, DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
            if (jeTemplateList.size() > 0) {
                je = jeTemplateList.get(0);//获取将描述和字段值分隔后的对象
                if ("N".equalsIgnoreCase(je.getEnabledFlag()) || je.getEnabledFlag() == null)//启用标志不为Y时直接跳过这次循环
                    continue;
            }

            this.setPro(je, json);//将字段上的值根据前缀重新获取

            String jsonStr = JSON.toJSONString(je);//先把JeTemplate转为json字符串
            JeTemplate template = new JeTemplate();
            try {
                template = JSON.parseObject(jsonStr, JeTemplate.class);
                jeLine = JSON.parseObject(jsonStr, JeLine.class); //再把json转为JeLine对象(两个类中字段大部分一致,但是类型有区别)
                jeLine.setCompanyId(Long.valueOf(template.getJeCompanyId()));
            } catch (JSONException e) {
                throw e;
            }

            jeLine.setSourceType("JE_TRX_DTL");
            jeLine.setSourceId(sourceId);
            if (!StringUtils.isEmpty(je.getJeAccountId()))
                jeLine.setAccountId(Long.valueOf(je.getJeAccountId()));
            if (!StringUtils.isEmpty(je.getJeCostCenterId()))
                jeLine.setCostCenterId(Long.valueOf(je.getJeCostCenterId()));
            if (!StringUtils.isEmpty(je.getJeCompanyId())) {
                Long jeCompanyId = Long.valueOf(je.getJeCompanyId());
                FinancialAttribute attr = financialAttributeMapper.selectByPrimaryKey(jeCompanyId);
                if (attr != null) {
                    jeLine.setSetOfBooksId(attr.getSetOfBooksId());
                    SetOfBooks setOfBook = setOfBooksMapper.selectByPrimaryKey(attr.getSetOfBooksId());
                    Long periodSetId = setOfBook.getPeriodSetId();
                    Period p = new Period();
                    String date = je.getJeDate();
                    if (("").equalsIgnoreCase(date) || date == null) {
                        throw new IllegalArgumentException("记账日期jeDate为空");
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date jeDate = null;
                        try {
                            //有些json中存的时间为Long类型
                            try {
                                Long dateTime = Long.valueOf(date);
                                jeDate = new Date(dateTime);
                            } catch (Exception e) {
                                jeDate = sdf.parse(date);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw e;
                        }
                        Calendar now = Calendar.getInstance();
                        now.setTime(jeDate);
                        p.setPeriodSetId(periodSetId);
                        p.setPeriodYear((long) now.get(Calendar.YEAR));
                        p.setPeriodMonth((long) (now.get(Calendar.MONTH) + 1));
                        Period period = periodMapper.selectOne(p);
                        if (period != null) {
                            jeLine.setPeriodName(period.getPeriodName());
                        } else {
                            throw new IllegalArgumentException("凭证日期" + jeDate + "未在期间表上找到对应期间");
                        }
                    }
                }
            }

            jeLine.setDrCr(je.getDrCr());
            jeLine.setCurrency(je.getJeCurrency());
            jeLine.setJeStatus("NEW");
            jeLine.setJeCreatedBy(request.getUserId());
            jeLine.setJeCreationDate(new Date());
            jeLine.setJeDescription(replaceDescription(je.getJeDescription(), json));

            HlsCusGldJeRuleEngineExtend gldJeRuleEngineExtend = ruleEngineExtendMapper.selectByPrimaryKey(extendId);
            Long jeHdTemplateId = gldJeRuleEngineExtend.getJeHdTemplateId();
            JeHdTemplate jeHdTemplate = jeHdTemplateMapper.selectByPrimaryKey(jeHdTemplateId);
            if (jeHdTemplate == null) {
                throw new IllegalArgumentException("找不到对应的凭证模板 " + jeTemplateId + "," + extendId);
            }
            /*处理凭证头*/
            createJeHead(request, jeHdTemplate, gldJeRuleEngineExtend, jeLine, json);
            //add by Eugene Song 只生成金额不为0 的凭证行
            if (nvl(jeLine.getCrAmount(), 0.0) != 0.0 || nvl(jeLine.getDrAmount(), 0.0) != 0.0) {

                JeLine jeLine1 = lineService.self().insertSelective(request, jeLine);
                jeLineList.add(jeLine1);
            }
        }


        //付款相关凭证合并
        jeLineList = jeLineList.stream().filter(jeLine -> PAY_EQIPMENT.equalsIgnoreCase(jeLine.getJeTrx()) ||  CSH_DEDUCTION.equalsIgnoreCase(jeLine.getJeTrx()) ).collect(Collectors.toList());

        if(jeLineList.size() > 0) {

            for(int j = 0 ; j < jeLineList.size() ; j ++ ){
                JeLine jeLineOld = jeLineList.get(j);
                List<JeLine> jeLineHeadList = lineMapper.selectJeLineByHead(jeLineOld);
                while ( jeLineHeadList.size()>0) {
                    //和jeLineList.get(0)比较
                    List<JeLine> finalJeLineList = jeLineHeadList;
                    List<JeLine> listOrder = new ArrayList<>();

                    for(JeLine dtHead : jeLineHeadList){
                        JeLine jeLineOne = jeLineHeadList.get(0);
                        if(dtHead.getAccountId().equals(jeLineOne.getAccountId()) && dtHead.getJeHeadId().equals(jeLineOne.getJeHeadId()) && dtHead.getDrCr().equalsIgnoreCase(jeLineOne.getDrCr())){
                            listOrder.add(dtHead);
                        }
                    }

                    //把在listorder里的合并
                    if(listOrder.size() > 0 ){
                        Double DrAmountSum = 0D;
                        Double DrFunctionalAmountSum= 0D;
                        Double CrAmountSum = 0D;
                        Double CrFunctionalAmountSum = 0D;

                        for(JeLine lineOrder: listOrder){
                            DrAmountSum = MathUtil.add(DrAmountSum, nvl(lineOrder.getDrAmount(),  0D));
                            DrFunctionalAmountSum = MathUtil.add(DrFunctionalAmountSum, nvl(lineOrder.getDrFunctionalAmount(),  0D));
                            CrAmountSum = MathUtil.add(CrAmountSum, nvl(lineOrder.getCrAmount(),  0D));
                            CrFunctionalAmountSum = MathUtil.add(CrFunctionalAmountSum, nvl(lineOrder.getCrFunctionalAmount(),  0D));
                        }

                        JeLine jeLinePast = listOrder.get(0);
                        JeLine jeLineNew = new JeLine();
                        Map<String, String> map = hlsBeanRefUtilService.getFieldValueMap(jeLinePast);
                        hlsBeanRefUtilService.setFieldValue(jeLineNew , map);
                        jeLineNew.setDrAmount(DrAmountSum);
                        jeLineNew.setDrFunctionalAmount(DrFunctionalAmountSum);
                        jeLineNew.setCrAmount(CrAmountSum);
                        jeLineNew.setCrFunctionalAmount(CrFunctionalAmountSum);
                        jeLineNew.setJeLineId(null);
                        jeLineNew.set__status("add");

                        lineService.insertSelective(request, jeLineNew);

                    }

                    //jeLineHeadList 删除原来的
                    for(JeLine lineOrderDelete: listOrder){
                        for(int i = 0 ; i< jeLineHeadList.size() ; i ++){
                            JeLine dt  = jeLineHeadList.get(i);
                            if(dt.getJeLineId().equals(lineOrderDelete.getJeLineId())){
                                lineService.deleteByPrimaryKey( dt);
                                jeLineHeadList.remove(dt);
                            }
                        }
                    }

                }
            }
        }

    }


    /**
     * 凭证反冲
     *
     * @param iRequest
     * @param jeTrxDtl
     * @param jsonObject
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void reverseLine(IRequest iRequest, JeTrxDtl jeTrxDtl, JSONObject jsonObject) {
        Date reverseJeTrxDate = jeTrxDtl.getReverseJeDate();
        Boolean confirmFlag = false;//确认标志

        String jeTrx = jeTrxDtl.getJeTrx();
        Long jeTrxId = jeTrxDtl.getReverseJeTrxId();
        JeTrxDtl dtl = new JeTrxDtl();
        dtl.setJeTrx(jeTrx);
        dtl.setJeTrxId(jeTrxId);
        dtl.setOperateType(OPERATE_TYPE_CREATE);
        List<JeTrxDtl> jeTrxDtls = jeTrxDtlMapper.select(dtl);//找出目标单据对应的所有凭证事物流水
        if (jeTrxDtls == null || jeTrxDtls.isEmpty()) {
            throw new IllegalArgumentException("未找到原始流水");
        }
        JeLine jeLine = new JeLine();
        jeLine.setJeTrx(jeTrxDtl.getJeTrx());
        jeLine.setSourceId(jeTrxDtls.get(jeTrxDtls.size() - 1).getJeTrxDtlId());//取最后一条流水的id

        Long jeHeadId = null;
        HlsCusJeHead jeHead = null;
        List<JeLine> jeLines = lineMapper.select(jeLine);//查找到该流水对应的所有的凭证行(反冲)
        jeLines = jeLines.stream().filter(rule -> !REVERSE_FLAG_R.equalsIgnoreCase(rule.getReverseFlag())).collect(Collectors.toList());

        if (jeLines != null && jeLines.size() > 0) {
            jeHeadId = jeLines.get(jeLines.size() - 1).getJeHeadId();
            jeHead = jeHeadMapper.selectByPrimaryKey(jeHeadId);
            //if (CONFIRM.equalsIgnoreCase(jeHead.getJeStatus()))
                confirmFlag = true;
        } else {
            return;
        }
        JeLine obj = new JeLine();
        obj.setJeHeadId(jeHeadId);
        Long jeHdTemplateId = jeHeadMapper.selectByPrimaryKey(jeHead.getJeHeadId()).getJeHdTemplateId();
        JeHdTemplate result = jeHdTemplateMapper.selectByPrimaryKey(jeHdTemplateId);
        String jeReverseRule = result.getJeReverseRule();
        Long companyId = iRequest.getCompanyId();
        List<JeLine> select = lineMapper.select(obj);//查找该流水对应凭证下的最后一个凭证头里所有的凭证行
        final HlsCusJeHead confirmHead = jeHead;
        HlsCusJeHead paramHead = null;

        if (confirmFlag) {
            for (JeLine item : jeLines) {//先遍历反冲的凭证行
                if (REVERSE_FLAG_N.equalsIgnoreCase(item.getReverseFlag()) || "".equalsIgnoreCase(item.getReverseFlag()) || item.getReverseFlag() == null) { //只有当反冲的flag为N或者为空的时候才进行操作
                    final String autoReverseRule = financialAttributeMapper.selectByPrimaryKey(companyId).getJeAutoReverseRule();//反冲规则
                    if (RULE_HEAD.equalsIgnoreCase(jeReverseRule)) { //当规则为头类型
                        //modify 添加凭证头反冲日期
                        jeHead.setJeDate(reverseJeTrxDate);
                        final HlsCusJeHead finalJeHead = createReverseJeHead(iRequest, select, jeHead, false);
                        select.forEach(k -> {
                            if (REVERSE_FLAG_N.equalsIgnoreCase(k.getReverseFlag()) || StringUtils.isEmpty(k.getReverseFlag())) { //只有当反冲的flag为N或者为空的时候才进行操作
                                JeLine clone = null;
                                if (OPPOSITE_RULE.equalsIgnoreCase(autoReverseRule)) {
                                    clone = reverseOpposite(k);//获得反冲的对象,借贷相反
                                } else {
                                    clone = reverseNegative(k);//获得反冲的对象,负借负贷
                                }
                                clone.setJeDate(reverseJeTrxDate);//设置反冲日期
                                clone.setJeHeadId(finalJeHead.getJeHeadId());
                                clone = lineService.insertSelective(iRequest, clone);//插入反冲的数据
                                confirmHead.setJeHeadId(clone.getJeHeadId());
                                k.setReverseFlag(REVERSE_FLAG_W);
                                k.setReverseJeLineId(clone.getJeLineId());
                                lineService.self().updateByPrimaryKeySelective(iRequest, k);
                            }
                        });
                        break;//反冲后直接中断循环
                    } else if (RULE_LINE.equalsIgnoreCase(jeReverseRule)) { //当规则为行类型
                        if (paramHead == null)
                            //modify 添加凭证头反冲日期
                            jeHead.setJeDate(reverseJeTrxDate);
                            paramHead = createReverseJeHead(iRequest, select, jeHead, false);
                        JeLine clone = null;
                        if (OPPOSITE_RULE.equalsIgnoreCase(autoReverseRule)) {
                            clone = reverseOpposite(item);//获得反冲的对象,借贷相反
                        } else {
                            clone = reverseNegative(item);//获得反冲的对象,负借负贷
                        }
                        clone.setJeDate(reverseJeTrxDate);//设置反冲日期
                        clone.setJeHeadId(paramHead.getJeHeadId());
                        clone = lineService.insertSelective(iRequest, clone);//插入反冲的数据
                        item.setReverseFlag(REVERSE_FLAG_W);
                        item.setReverseJeLineId(clone.getJeLineId());
                        lineService.self().updateByPrimaryKeySelective(iRequest, item);
                    } else if (RULE_HEAD_RETURN.equalsIgnoreCase(jeReverseRule)) { //当规则为头返回类型
                        //modify 添加凭证头反冲日期
                        jeHead.setJeDate(reverseJeTrxDate);
                        final JeHead finalJeHead = createReverseJeHead(iRequest, select, jeHead, false);//创建凭证反冲凭证头
                        select.forEach(k -> {
                            JeLine clone = null;
                            if (OPPOSITE_RULE.equalsIgnoreCase(autoReverseRule)) {
                                clone = reverseOpposite(k);//获得反冲的对象,借贷相反
                            } else {
                                clone = reverseNegative(k);//获得反冲的对象,负借负贷
                            }
                            clone.setJeDate(reverseJeTrxDate);//设置反冲日期
                            clone.setJeHeadId(finalJeHead.getJeHeadId());
                            clone = lineService.insertSelective(iRequest, clone);//插入反冲的数据
                            confirmHead.setJeHeadId(clone.getJeHeadId());
                            k.setReverseFlag(REVERSE_FLAG_W);
                            k.setReverseJeLineId(clone.getJeLineId());
                            lineService.self().updateByPrimaryKeySelective(iRequest, k);
                        });
                        if (select.size() == 1)//如果原始凭证下只有一个凭证行,那么不生成新的头,直接退出循环
                            break;
                        //JeHead reverseJeHead = createReverseJeHead(iRequest, select, jeHead, true);//创建新的凭证头
                       /* select.forEach(k -> {
                            if (k.getJeLineId().compareTo(item.getJeLineId()) == 0)//除了选中的记录之外,都进行生成处理
                                return;
                            k.setJeLineId(null);
                            k.setJeHeadId(null);
                            k.setJeStatus(NEW);
                            k.setJeHeadId(reverseJeHead.getJeHeadId());
                            lineService.self().insertSelective(iRequest, k);
                        });*/
                        break;//反冲后直接中断循环
                    } else {
                        return;
                    }
                }
            }
        } else {
            /*lineService.batchDelete(select);
            JeLine line = new JeLine();
            line.setJeHeadId(jeHeadId);
            List<JeLine> jeLineList = lineMapper.select(line);
            //当凭证头下没有凭证行时,删除该凭证
            if (jeLineList == null || jeLineList.isEmpty()) {
                HlsCusJeHead head = new HlsCusJeHead();
                head.setJeHeadId(jeHeadId);
                jeHeadService.deleteByPrimaryKey(head);
            }*/
        }

    }


    /**
     * 生成凭证头(有凭证头模板)
     *
     * @param request
     * @param hdTemplate
     * @param gldJeRuleEngineExtend
     * @param jeLine
     * @param json
     */
    private void createJeHead(IRequest request, JeHdTemplate hdTemplate, HlsCusGldJeRuleEngineExtend gldJeRuleEngineExtend, JeLine jeLine, JSONObject json) {

        String jeHdRule = hdTemplate.getJeHdRule();
        String jeHdRuleFlag1 = hdTemplate.getJeHdRuleUser1Flag();
        String jeHdRuleFlag2 = hdTemplate.getJeHdRuleUser2Flag();
        String jeHdRuleFlag3 = hdTemplate.getJeHdRuleUser3Flag();

        if (gldJeRuleEngineExtend != null) {
            if ("Y".equalsIgnoreCase(jeHdRuleFlag1))
                jeHdRule = jeHdRule + "|||TRX---" + gldJeRuleEngineExtend.getJeHdRuleUserColumn1() + "$$$" + hdTemplate.getJeHdRuleUser1();
            if ("Y".equalsIgnoreCase(jeHdRuleFlag2))
                jeHdRule = jeHdRule + "|||TRX---" + gldJeRuleEngineExtend.getJeHdRuleUserColumn2() + "$$$" + hdTemplate.getJeHdRuleUser2();
            if ("Y".equalsIgnoreCase(jeHdRuleFlag3))
                jeHdRule = jeHdRule + "|||TRX---" + gldJeRuleEngineExtend.getJeHdRuleUserColumn3() + "$$$" + hdTemplate.getJeHdRuleUser3();
        }

        if (StringUtils.isEmpty(jeHdRule))
            jeHdRule = "FIX-JE_TRX_DTL_ID";

        String result = this.replacePosition(jeHdRule, jeLine, json);//替换值

        HlsCusJeHead jeHead = new HlsCusJeHead();
        jeHead.setJeHeadStr(result);
        List<HlsCusJeHead> jeHeads = jeHeadMapper.select(jeHead);
        if (jeHeads != null && jeHeads.size() > 0) { //存在这个凭证头,则直接把凭证头的id赋值给jeline的字段
            jeHeads.forEach(item -> {
                if ("CONFIRM".equalsIgnoreCase(item.getJeStatus())) {
                    return;
                } else if ((REVERSE_FLAG_N.equalsIgnoreCase(item.getReverseFlag()) || "".equalsIgnoreCase(item.getReverseFlag()) || item.getReverseFlag() == null)) {
                    jeLine.setJeHeadId(jeHeads.get(jeHeads.size() - 1).getJeHeadId());
                    String description = item.getDescription();
                    item.setDescription(replaceDescription(description, json));
                }
            });
        }
        if (jeLine.getJeHeadId() == null) {
            HlsCusJeHead head;
            String jsonHeadValue = JSON.toJSONString(jeLine);
            String jeHdDescription = hdTemplate.getJeHdDescriptiton();
            try {
                head = JSON.parseObject(jsonHeadValue, HlsCusJeHead.class); //使用json数据转换复制属性
            } catch (JSONException e) {
                throw e;
            }
            head.setJeHeadStr(result);
            head.setJeHdTemplateId(hdTemplate.getJeHdTemplateId());
            //获取编码规则
            Map<String, String> params = new HashMap<String, String>();
            head.setJeNumber(fndCodingRuleValuesService.getCodeRuleValue(request, jeLine.getDocumentCategory(), jeLine.getDocumentType(), jeLine.getBusinessType(), params));
            head.setDescription(replaceDescription(jeHdDescription, json));
            head.setOperateId(request.getUserId());
            jeHeadService.self().insertSelective(request, head);
            jeLine.setJeHeadId(head.getJeHeadId());
        }
    }

    /**
     * 凭证头反冲
     *
     * @param request
     * @param jeLines
     * @param jeHead
     * @param flag    当flag为false时,表示反冲时,生R,W的记录,为true时,生成的为N
     * @return
     */
    private HlsCusJeHead createReverseJeHead(IRequest request, List<JeLine> jeLines, HlsCusJeHead jeHead, boolean flag) {
        String jeHeadStr = jeHead.getJeHeadStr();
        String jsonString = JSON.toJSONString(jeHead);
        HlsCusJeHead head = JSON.parseObject(jsonString, HlsCusJeHead.class);
        head.setJeConfirmDate(null);
        head.setJeConfirmedBy(null);
        head.setGlStatus(null);
        head.setGlMsg(null);
        if (flag == false) {
            head.setJeHeadStr(jeHeadStr);

            head.setReverseJeHeadId(jeHead.getJeHeadId());
            head.setReverseFlag(REVERSE_FLAG_R);
            head.setJeStatus(NEW);
            head.setJeHeadId(null);
            head.setRetrialedBy(null);
            head.setTrialedBy(null);
            //获取编码规则
            Map<String, String> params = new HashMap<String, String>();
            head.setJeNumber(fndCodingRuleValuesService.getCodeRuleValue(request, jeLines.get(0).getDocumentCategory(), jeLines.get(0).getDocumentType(), jeLines.get(0).getBusinessType(), params));
            jeHeadService.insertSelective(request, head);

            jeHead.setReverseJeHeadId(head.getJeHeadId());
            jeHead.setReverseFlag(REVERSE_FLAG_W);
            jeHeadService.updateByPrimaryKeySelective(request, jeHead);
        } else {
            head.setJeHeadStr(jeHeadStr);
            head.setReverseFlag(REVERSE_FLAG_N);
            head.setReverseJeHeadId(null);
            head.setJeStatus(NEW);
            head.setRetrialedBy(null);
            head.setTrialedBy(null);
            //获取编码规则
            Map<String, String> params = new HashMap<String, String>();
            head.setJeNumber(fndCodingRuleValuesService.getCodeRuleValue(request, jeLines.get(0).getDocumentCategory(), jeLines.get(0).getDocumentType(), jeLines.get(0).getBusinessType(), params));
            jeHeadService.insertSelective(request, head);

        }

        return head;
    }

    /**
     * 凭证头规则替换
     *
     * @param jeHdRule
     * @param jeLine
     * @param json
     * @return
     */
    private String replacePosition(String jeHdRule, JeLine jeLine, JSONObject json) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date jeDate = jeLine.getJeDate();
        String dateString = formatter.format(jeDate);
        final Optional<String> opt = Optional.ofNullable(jeHdRule);
        final String[] results = opt.map(item -> item.split("\\|\\|\\|")).orElse(new String[]{});//以|||为分隔符号
        final Function<String, Predicate<String>> resolve = param -> name -> name.startsWith(param);//定义函数resolve,String为参数,返回为lamdba
        final Function<String, Predicate<String>> resolveEqual = param -> name -> name.equalsIgnoreCase(param);//定义函数resolve,String为参数,返回为lamdba
        List<String> strings = Arrays.asList(results);
        String resultJeTrxDtlId = strings.stream().filter(resolveEqual.apply(JE_TRX_DTL_ID)).findFirst().map(item -> item + jeLine.getSourceId()).orElse("");
        String resultCompanyId = strings.stream().filter(resolveEqual.apply(COMPANY_ID)).findFirst().map(item -> "|||" + item + jeLine.getCompanyId()).orElse("");
        String resultJeTrx = strings.stream().filter(resolveEqual.apply(JE_TRX)).findFirst().map(item -> "|||" + item + jeLine.getJeTrx()).orElse("");
        String resultPeriodName = strings.stream().filter(resolveEqual.apply(PERIOD_NAME)).map(item -> "|||" + item + jeLine.getPeriodName()).findFirst().orElse("");
        String resultJeDate = strings.stream().filter(resolveEqual.apply(JE_DATE)).map(item -> "|||" + item + dateString).findFirst().orElse("");
        List<String> resultTrxs = strings.stream().filter(resolve.apply("TRX-")).map(item -> {
            String[] split = item.split("\\$\\$\\$");
            return "TRX-" + replaceDescription(split[0].replace("TRX---", ""), json) + split[1];
        }).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        resultTrxs.forEach(item -> sb.append(item));
        String resultTrx = sb.toString();
        String finalResult = resultJeTrxDtlId + resultCompanyId + resultJeTrx + resultPeriodName + resultJeDate + "|||" + resultTrx;
        //去除头和尾的|||标志
        int index1 = 0;
        if (finalResult.startsWith("|||")) {
            index1 = 3;
        }
        int index2 = finalResult.length();
        if (finalResult.endsWith("|||")) {
            index2 = index2 - 3;
        }
        finalResult = finalResult.substring(index1, index2);
        return finalResult;
    }

    /**
     * 按规则替换jdDescription  {$xxx$}
     *
     * @param jeDescription
     * @param json
     * @return
     */
    private String replaceDescription(String jeDescription, JSONObject json) {
        StringBuilder sb = new StringBuilder(jeDescription);
        String paramName; //定义参数名
        String paramValue; //定义参数值
        while (sb.lastIndexOf("{$") >= 0) {    //循环遍历字符串中的{$$}标示
            paramName = sb.subSequence(sb.lastIndexOf("{$") + 2, sb.lastIndexOf("$}")).toString();//获取标示中的参数名
            if (json.containsKey(paramName)) {
                paramValue = json.getString(paramName);//从json中将结果取出
            } else if (json.containsKey(paramName.toUpperCase())) {
                paramValue = json.getString(paramName.toUpperCase());
            } else {
                paramValue = new String();
            }
            sb.replace(sb.lastIndexOf("{$"), sb.lastIndexOf("$}") + 2, paramValue);
        }
        return sb.toString();
    }

    @Override
    public List<JeTrxDtl> queryJeTrxDtlStatus() {
        return jeTrxDtlMapper.queryJeTrxDtlStatus();
    }


    /**
     * 处理JeTemplate中的字段值,将实际值取出附在原字段上
     */
    private void setPro(JeTemplate jeTemplate, JSONObject json) {
        Class clazz = jeTemplate.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field :
                fields) {
            String filedName = field.getName();
            if (filedName.endsWith("Desc")) {
                String filedRealName = filedName.split("Desc")[0];
                String valueBefore = null;
                try {
                    valueBefore = BeanUtils.getProperty(jeTemplate, filedRealName);
                    if (StringUtils.isEmpty(valueBefore))
                        continue;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                String valueAfter = split(valueBefore, json);
                if (StringUtils.isEmpty(valueAfter)) {
                    throw new IllegalArgumentException("模板中字段 " + filedRealName + " 在数据源中取到的值为空");
                }
                try {
                    BeanUtils.setProperty(jeTemplate, filedRealName, valueAfter);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 根据规则,截取出对应的数据
     *
     * @param param
     * @param jsonObject
     * @return
     */
    private String split(String param, JSONObject jsonObject) {
        if ("".equalsIgnoreCase(param) || param == null)//当传入的参数为空值或者为空字符串时,直接返回""
            return "";
        String[] result = param.split("---");
        String finalValue = null;
        if (result.length == 2) {  //SEGMENT---XXX & TRX---XXX
            if ("SEGMENT".equals(result[0])) {
                finalValue = result[1];
            } else if ("TRX".equals(result[0])) {
                if (jsonObject.get(result[1]) != null) {
                    finalValue = jsonObject.get(result[1]).toString();
                } else if (jsonObject.get(result[1].toUpperCase()) != null) {
                    finalValue = jsonObject.get(result[1].toUpperCase()).toString();
                } else
                    finalValue = "";
            } else {
                return "";
            }
            return finalValue;
        } else {
            return "";
        }
    }


    @Override
    @Deprecated
    public List<JeTrxDtl> waitGenerateList(JeTrxDtl dto) {
        List<JeTrxDtl> jeTrxDtls = jeTrxDtlMapper.waitGenerateList(dto);
        jeTrxDtls.forEach(item -> {
            String documentNumber = null;
            switch (item.getJeTrx()) {
                case CSH_WRITE_OFF:
                    HlsCusCshWriteOff cshWriteOff = cshWriteOffMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (cshWriteOff == null)
                        break;
                    HlsCusCshTransaction cshTransaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOff.getCshTransactionId());
                    if (cshTransaction == null)
                        break;
                    String format = format(cshTransaction.getTransactionDate());
                    String transactionNum = cshTransaction.getTransactionNum() != null ? cshTransaction.getTransactionNum() : "";
                    String bankSlipNum = cshTransaction.getBankSlipNum() != null ? cshTransaction.getBankSlipNum() : "";
                    String writeOffType = cshWriteOff.getWriteOffType() != null ? item.getDescription() : "";
                    String s = cshWriteOff.getCshWriteOffAmount() != null ? BigDecimal.valueOf(cshWriteOff.getCshWriteOffAmount()).toPlainString() : "";
                    documentNumber = format.concat("-").concat(transactionNum).concat("-").concat(bankSlipNum).concat("-").concat(writeOffType).concat("-").concat(s);
                    break;
                case CSH_TRANSACTION:
                    HlsCusCshTransaction transaction = cshTransactionMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (transaction == null)
                        break;
                    String format1 = format(transaction.getTransactionDate());
                    String s1 = transaction.getTransactionNum() != null ? transaction.getTransactionNum() : "";
                    String s2 = transaction.getBankSlipNum() != null ? transaction.getBankSlipNum() : "";
                    String s3 = transaction.getTransactionAmount() != null ? BigDecimal.valueOf(transaction.getTransactionAmount()).toPlainString() : "";
                    documentNumber = format1.concat("-").concat(s1).concat("-").concat(s2).concat("-").concat(s3);
                    break;
                case CONTRACT_INCEPT:
                    /*HlsCusConContractIncept conContractIncept = hlsCusConContractInceptMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (conContractIncept == null)
                        break;
                    String format2 = format(conContractIncept.getInceptionOfLease());
                    documentNumber = format2.concat("-").concat("合同起租");*/
                    break;
                case FIN_INCOME_RECOGNITION:
                    HlsCusContractFinanceIncome income = contractFinanceIncomeMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (income == null)
                        break;
                    String format3 = format(income.getStartDate());
                    String format4 = format(income.getEndDate());
                    String s4 = income.getFinanceIncome() != null ? BigDecimal.valueOf(income.getFinanceIncome()).toPlainString() : "";
                    documentNumber = format3.concat("-").concat(format4).concat("-").concat(s4);
                    break;
                case ACR_INVOICE_CONFIRM:
                   /* HlsCusAcrInvoiceHd acrInvoiceHd = acrInvoiceHdMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (acrInvoiceHd == null)
                        break;
                    String format5 = format(acrInvoiceHd.getInvoiceDate());
                    String s5 = acrInvoiceHd.getDocumentNumber() != null ? acrInvoiceHd.getDocumentNumber() : "";
                    String s6 = acrInvoiceHd.getTotalAmount() != null ? BigDecimal.valueOf(acrInvoiceHd.getTotalAmount()).toPlainString() : "";
                    documentNumber = format5.concat("-").concat(s5).concat("-").concat(s6);*/
                    break;
                default:
                    break;
            }
            item.setDocumentNumber(documentNumber);
        });
        return jeTrxDtls;
    }

    @Override
    public List<JeTrxDtl> waitGenerateList(JeTrxDtl dto, int page, int pageSize) {
        /*PageHelper.startPage(page, pageSize);
        List<JeTrxDtl> jeTrxDtls = jeTrxDtlMapper.waitGenerateList(dto);
        Set<String> collect = jeTrxDtls.stream().map(item -> item.getJeTrx()).collect(Collectors.toSet());
        List<HlsCusCshWriteOff> cshWriteOffList = null;
        List<HlsCusCshTransaction> cshTransactionList = null;
        List<HlsCusConContractIncept> conContractInceptList = null;
        List<ContractFinanceIncome> contractFinanceIncomeList = null;
        List<HlsCusAcrInvoiceHd> acrInvoiceHdList = null;
        for (String s : collect) {
            if (CSH_WRITE_OFF.equalsIgnoreCase(s)) {
                cshWriteOffList = cshWriteOffMapper.selectAll();
                cshTransactionList = cshTransactionMapper.selectAll();
            } else if (CSH_TRANSACTION.equalsIgnoreCase(s)) {
                if (cshTransactionList == null)
                    cshTransactionList = cshTransactionMapper.selectAll();
            } else if (CONTRACT_INCEPT.equalsIgnoreCase(s)) {
                conContractInceptList = hlsCusConContractInceptMapper.selectAll();
            } else if (FIN_INCOME_RECOGNITION.equalsIgnoreCase(s)) {
                contractFinanceIncomeList = contractFinanceIncomeMapper.selectAll();
            } else if (ACR_INVOICE_CONFIRM.equalsIgnoreCase(s)) {
                acrInvoiceHdList = acrInvoiceHdMapper.selectAll();
            }
        }
        for (JeTrxDtl item : jeTrxDtls) {
            String documentNumber = null;
            Long jeTrxId = item.getJeTrxId();
            switch (item.getJeTrx()) {
                case CSH_WRITE_OFF:
                    out:
                    for (HlsCusCshWriteOff cshWriteOff : cshWriteOffList) {
                        if (cshWriteOff.getWriteOffId().compareTo(jeTrxId) == 0) {
                            inner:
                            for (HlsCusCshTransaction cshTransaction : cshTransactionList) {
                                if (cshTransaction == null || cshWriteOff == null || cshTransaction.getTransactionId() == null || cshWriteOff.getCshTransactionId() == null)
                                    continue;
                                if (cshTransaction.getTransactionId().compareTo(cshWriteOff.getCshTransactionId()) == 0) {
                                    String format = format(cshTransaction.getTransactionDate());
                                    String transactionNum = cshTransaction.getTransactionNum() != null ? cshTransaction.getTransactionNum() : "";
                                    String bankSlipNum = cshTransaction.getBankSlipNum() != null ? cshTransaction.getBankSlipNum() : "";
                                    String writeOffType = cshWriteOff.getWriteOffType() != null ? item.getDescription() : "";
                                    String s = cshWriteOff.getCshWriteOffAmount() != null ? BigDecimal.valueOf(cshWriteOff.getCshWriteOffAmount()).toPlainString() : "";
                                    documentNumber = format.concat("-").concat(transactionNum).concat("-").concat(bankSlipNum).concat("-").concat(writeOffType).concat("-").concat(s);
                                    break out;
                                }
                            }
                        }
                    }
                    break;
                case CSH_TRANSACTION:
                    for (HlsCusCshTransaction transaction : cshTransactionList) {
                        if (transaction.getTransactionId().compareTo(jeTrxId) == 0) {
                            String format1 = format(transaction.getTransactionDate());
                            String s1 = transaction.getTransactionNum() != null ? transaction.getTransactionNum() : "";
                            String s2 = transaction.getBankSlipNum() != null ? transaction.getBankSlipNum() : "";
                            String s3 = transaction.getTransactionAmount() != null ? BigDecimal.valueOf(transaction.getTransactionAmount()).toPlainString() : "";
                            documentNumber = format1.concat("-").concat(s1).concat("-").concat(s2).concat("-").concat(s3);
                            break;
                        }
                    }
                    break;
                case CONTRACT_INCEPT:
                    for (HlsCusConContractIncept conContractIncept : conContractInceptList) {
                        if (conContractIncept.getContractInceptId().compareTo(jeTrxId) == 0) {
                            String format2 = format(conContractIncept.getInceptionOfLease());
                            documentNumber = format2.concat("-").concat("合同起租");
                            break;
                        }
                    }
                    break;
                case FIN_INCOME_RECOGNITION:
                    for (ContractFinanceIncome income : contractFinanceIncomeList) {
                        if (income.getFinanceIncomeId().compareTo(jeTrxId) == 0) {
                            String format3 = format(income.getStartDate());
                            String format4 = format(income.getEndDate());
                            String s4 = income.getFinanceIncome() != null ? BigDecimal.valueOf(income.getFinanceIncome()).toPlainString() : "";
                            documentNumber = format3.concat("-").concat(format4).concat("-").concat(s4);
                            break;
                        }
                    }
                    break;
                case ACR_INVOICE_CONFIRM:
                    for (HlsCusAcrInvoiceHd acrInvoiceHd : acrInvoiceHdList) {
                        if (acrInvoiceHd.getInvoiceHdId().compareTo(jeTrxId) == 0) {
                            String format5 = format(acrInvoiceHd.getInvoiceDate());
                            String s5 = acrInvoiceHd.getDocumentNumber() != null ? acrInvoiceHd.getDocumentNumber() : "";
                            String s6 = acrInvoiceHd.getTotalAmount() != null ? BigDecimal.valueOf(acrInvoiceHd.getTotalAmount()).toPlainString() : "";
                            documentNumber = format5.concat("-").concat(s5).concat("-").concat(s6);
                            break;
                        }
                    }
                    break;
                default:
                    break;
            }
            item.setDocumentNumber(documentNumber);
        }
        ;*/
        return null;
    }

    /**
     * 负借负贷 --- 反冲
     *
     * @param item
     * @return
     */
    private JeLine reverseNegative(JeLine item) {

        JeLine clone = null;
        try {
            clone = (JeLine) BeanUtils.cloneBean(item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        clone.setDrAmount(CalculateUtil.mul(-1D, item.getDrAmount()));
        clone.setCrAmount(CalculateUtil.mul(-1D, item.getCrAmount()));
        clone.setCrFunctionalAmount(CalculateUtil.mul(-1D, item.getCrFunctionalAmount()));
        clone.setDrFunctionalAmount(CalculateUtil.mul(-1D, item.getDrFunctionalAmount()));
        clone.setReverseFlag("R");
        clone.setReverseJeLineId(item.getJeLineId());
        clone.setJeHeadId(null);
        clone.setJeLineId(null);
        clone.setJeConfirmDate(null);
        clone.setJeConfirmedBy(null);
        clone.setGlMsg(null);
        clone.setGlStatus(null);

        return clone;
    }


    /**
     * 借贷相反 --- 反冲
     *
     * @param item
     * @return
     */
    private JeLine reverseOpposite(JeLine item) {
        JeLine clone = null;
        try {
            clone = (JeLine) BeanUtils.cloneBean(item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        clone.setDrAmount(item.getCrAmount());
        clone.setCrAmount(item.getDrAmount());
        clone.setDrFunctionalAmount(item.getCrFunctionalAmount());
        clone.setCrFunctionalAmount(item.getDrFunctionalAmount());
        clone.setJeConfirmDate(null);
        clone.setJeConfirmedBy(null);
        if (CR.equalsIgnoreCase(clone.getDrCr()))
            clone.setDrCr(DR);
        else if (DR.equalsIgnoreCase(clone.getDrCr()))
            clone.setDrCr(CR);
        clone.setReverseFlag("R");
        clone.setReverseJeLineId(item.getJeLineId());
        clone.setJeHeadId(null);
        clone.setJeLineId(null);
        clone.setGlMsg(null);
        clone.setGlStatus(null);

        return clone;
    }

    /**
     * 是否包含json数据
     *
     * @param request
     * @param id
     * @return
     */
    private JeTrxJsonDtl hasJson(IRequest request, Long id) {
        JeTrxJsonDtl jsonDtl = new JeTrxJsonDtl();
        jsonDtl.setJeTrxDtlId(id);
        jsonDtl = jeTrxJsonDtlService.selectByPrimaryKey(request, jsonDtl);
        return jsonDtl;
    }

    /**
     * 时间转换
     *
     * @param date
     * @return
     */
    private static String format(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        return dateString;
    }


    /**
     * 凭证事物调用api,map中包含必要的参数,均以驼峰式命名
     * 数据格式
     * map.put("jeTrxId", 68L);
     * map.put("jeTrx", "CONTRACT_INCEPT");
     * map.put("companyId", 1L);
     * map.put("jeSourceDoc","CON_CONTRACT");
     * map.put("jeSourceId", 68L);
     *
     * @param request
     * @param map
     */
    @Deprecated
    public void jeTrxMade(IRequest request, HashMap map) {
        JeTrxDtl jeTrxDtl = new JeTrxDtl();

        //取出map中的值
        Long jeTrxId = map.get("jeTrxId") != null ? (Long) map.get("jeTrxId") : null;
        String jeTrx = map.get("jeTrx") != null ? (String) map.get("jeTrx") : null;
        Long companyId = map.get("companyId") != null ? (Long) map.get("companyId") : null;
        String jeSourceDoc = map.get("jeSourceDoc") != null ? (String) map.get("jeSourceDoc") : null;
        Long jeSourceId = map.get("jeSourceId") != null ? (Long) map.get("jeSourceId") : null;

        jeTrxDtl.setJeTrxId(jeTrxId);
        jeTrxDtl.setJeTrx(jeTrx);
        jeTrxDtl.setCompanyId(companyId);
        jeTrxDtl.setJeSourceDoc(jeSourceDoc);
        jeTrxDtl.setJeSourceId(jeSourceId);
        jeTrxDtl.setStatus("NEW");

        self().insertSelective(request, jeTrxDtl);
    }

    /**
     * 旧版生成凭证(没有凭证头模板)
     *
     * @param request
     * @param jeTemplateIds
     * @param sourceId
     * @param json
     * @throws ParseException
     * @throws JSONException
     * @throws NullPointerException
     */
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateGldJeLineData(IRequest request, String[] jeTemplateIds, Long sourceId, JSONObject json) throws ParseException, JSONException, NullPointerException {
        for (String jeTemplateId : jeTemplateIds) {
            JeTemplate je = new JeTemplate();
            JeLine jeLine;
            //当取出的集合中某个数据为空时,直接进入下一次循环
            if (("").equalsIgnoreCase(jeTemplateId) || jeTemplateId == null)
                continue;
            je.setJeTemplateId(Long.valueOf(jeTemplateId));
            if (jeTemplateService.selectWithDesc(request, je, DEFAULT_PAGE, DEFAULT_PAGE_SIZE).size() > 0) {
                je = jeTemplateService.selectWithDesc(request, je, DEFAULT_PAGE, DEFAULT_PAGE_SIZE).get(0);//获取将描述和字段值分隔后的对象
                if ("N".equalsIgnoreCase(je.getEnabledFlag()) || je.getEnabledFlag() == null)//启用标志不为Y时直接跳过这次循环
                    continue;
            }

            this.setPro(je, json);//将字段上的值根据前缀重新获取

            String jsonStr = JSON.toJSONString(je);//先把JeTemplate转为json字符串
            try {
                jeLine = JSON.parseObject(jsonStr, JeLine.class); //再把json转为JeLine对象(两个类中字段大部分一致,但是类型有区别)
            } catch (JSONException e) {
                throw e;
            }

            jeLine.setCompanyId(request.getCompanyId());
            jeLine.setSourceType("JE_TRX_DTL");
            jeLine.setSourceId(sourceId);
            if ((!"".equalsIgnoreCase(je.getJeAccountId())) && je.getJeAccountId() != null)
                jeLine.setAccountId(Long.valueOf(je.getJeAccountId()));
            if ((!"".equalsIgnoreCase(je.getJeCostCenterId())) && je.getJeCostCenterId() != null)
                jeLine.setCostCenterId(Long.valueOf(je.getJeCostCenterId()));
            if ((!"".equalsIgnoreCase(je.getJeCompanyId())) && je.getJeCompanyId() != null) {
                Long jeCompanyId = Long.valueOf(je.getJeCompanyId());
                FinancialAttribute attr = financialAttributeMapper.selectByPrimaryKey(jeCompanyId);
                if (attr != null) {
                    jeLine.setSetOfBooksId(attr.getSetOfBooksId());
                    SetOfBooks setOfBook = setOfBooksMapper.selectByPrimaryKey(attr.getSetOfBooksId());
                    Long periodSetId = setOfBook.getPeriodSetId();
                    Period p = new Period();
                    String date = je.getJeDate();
                    if (("").equalsIgnoreCase(date) || date == null) {
                        throw new IllegalArgumentException("记账日期jeDate为空");
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date jeDate = null;
                        try {
                            //有些json中存的时间为Long类型
                            try {
                                Long dateTime = Long.valueOf(date);
                                jeDate = new Date(dateTime);
                            } catch (Exception e) {
                                jeDate = sdf.parse(date);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                            throw e;
                        }
                        Calendar now = Calendar.getInstance();
                        now.setTime(jeDate);
                        p.setPeriodSetId(periodSetId);
                        p.setPeriodYear((long) now.get(Calendar.YEAR));
                        p.setPeriodMonth((long) (now.get(Calendar.MONTH) + 1));
                        Period period = periodMapper.selectOne(p);
                        if (period != null)
                            jeLine.setPeriodName(period.getPeriodName());
                        else
                            throw new IllegalArgumentException("凭证期间取值不合法");
                    }
                }
            }

            jeLine.setDrCr(je.getDrCr());
            jeLine.setCurrency(je.getJeCurrency());
            jeLine.setJeStatus("NEW");
            jeLine.setJeCreatedBy(request.getUserId());
            jeLine.setJeCreationDate(new Date());
            jeLine.setJeDescription(replaceDescription(je.getJeDescription(), json));

            /*处理凭证头*/
            createJeHead(request, je, jeLine, json, null);

            lineService.self().insertSelective(request, jeLine);
        }
    }

    /**
     * 凭证行生成方法,一次生成一个凭证行,每个凭证行事物独立,不推荐使用
     *
     * @param request
     * @param jeTemplateId
     * @param SourceId
     * @param json
     * @throws ParseException
     * @throws JSONException
     * @throws NullPointerException
     */
    @Deprecated
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateGldJeLineData(IRequest request, String jeTemplateId, Long SourceId, JSONObject json) throws ParseException, JSONException, NullPointerException {
        JeTemplate je = new JeTemplate();
        //当取出的集合中某个数据为空时,直接进入下一次循环
        if (("").equalsIgnoreCase(jeTemplateId) || jeTemplateId == null)
            return;
        je.setJeTemplateId(Long.valueOf(jeTemplateId));
        if (jeTemplateService.selectWithDesc(request, je, DEFAULT_PAGE, DEFAULT_PAGE_SIZE).size() > 0) {
            je = jeTemplateService.selectWithDesc(request, je, DEFAULT_PAGE, DEFAULT_PAGE_SIZE).get(0);//获取将描述和字段值分隔后的对象
            if ("N".equalsIgnoreCase(je.getEnabledFlag()) || je.getEnabledFlag() == null)//启用标志不为Y时直接跳过这次循环
                return;
        }

        this.setPro(je, json);//将字段上的值根据前缀重新获取

        String jsonStr = JSON.toJSONString(je);//先把JeTemplate转为json字符串
        JeLine jeLine;
        try {
            jeLine = JSON.parseObject(jsonStr, JeLine.class); //再把json转为JeLine对象(两个类中字段大部分一致,但是类型有区别)
        } catch (JSONException e) {
            throw e;
        }

        jeLine.setCompanyId(request.getCompanyId());
        jeLine.setSourceType("JE_TRX_DTL");
        jeLine.setSourceId(SourceId);
        if ((!"".equalsIgnoreCase(je.getJeAccountId())) && je.getJeAccountId() != null)
            jeLine.setAccountId(Long.valueOf(je.getJeAccountId()));
        if ((!"".equalsIgnoreCase(je.getJeCostCenterId())) && je.getJeCostCenterId() != null)
            jeLine.setCostCenterId(Long.valueOf(je.getJeCostCenterId()));
        if ((!"".equalsIgnoreCase(je.getJeCompanyId())) && je.getJeCompanyId() != null) {
            Long jeCompanyId = Long.valueOf(je.getJeCompanyId());
            FinancialAttribute attr = financialAttributeMapper.selectByPrimaryKey(jeCompanyId);
            if (attr != null) {
                jeLine.setSetOfBooksId(attr.getSetOfBooksId());
                SetOfBooks setOfBook = setOfBooksMapper.selectByPrimaryKey(attr.getSetOfBooksId());
                Long periodSetId = setOfBook.getPeriodSetId();
                Period p = new Period();
                String date = je.getJeDate();
                if (("").equalsIgnoreCase(date) || date == null) {
                    throw new IllegalArgumentException("记账日期jeDate为空");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date jeDate = null;
                    try {
                        //有些json中存的时间为Long类型
                        try {
                            Long dateTime = Long.valueOf(date);
                            jeDate = new Date(dateTime);
                        } catch (Exception e) {
                            jeDate = sdf.parse(date);
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        throw e;
                    }
                    Calendar now = Calendar.getInstance();
                    now.setTime(jeDate);
                    p.setPeriodSetId(periodSetId);
                    p.setPeriodYear((long) now.get(Calendar.YEAR));
                    p.setPeriodMonth((long) (now.get(Calendar.MONTH) + 1));
                    Period period = periodMapper.selectOne(p);
                    if (period != null)
                        jeLine.setPeriodName(period.getPeriodName());
                    else
                        throw new IllegalArgumentException("凭证期间取值不合法");
                }
            }
        }

        jeLine.setDrCr(je.getDrCr());
        jeLine.setCurrency(je.getJeCurrency());
        jeLine.setJeStatus("NEW");
        jeLine.setJeCreatedBy(request.getUserId());
        jeLine.setJeCreationDate(new Date());

        /*处理凭证头*/
        String jeHdRule = je.getJeHdRule();
        if (jeHdRule == null || "".equalsIgnoreCase(jeHdRule))
            jeHdRule = "FIX-JE_TRX_DTL_ID";
        String result = this.replacePosition(jeHdRule, jeLine, json);//替换值
        HlsCusJeHead jeHead = new HlsCusJeHead();
        jeHead.setJeHeadStr(result);
        List<HlsCusJeHead> jeHeads = jeHeadMapper.select(jeHead);
        if (jeHeads != null && jeHeads.size() > 0) { //存在这个凭证头,则直接把凭证头的id赋值给jeline的字段
            jeHeads.forEach(item -> {
                if ("CONFIRM".equalsIgnoreCase(item.getJeStatus()))
                    return;
                jeLine.setJeHeadId(jeHeads.get(0).getJeHeadId());
            });
        }
        if (jeLine.getJeHeadId() == null) {
            HlsCusJeHead head;
            String jsonHeadValue = JSON.toJSONString(jeLine);
            try {
                head = JSON.parseObject(jsonHeadValue, HlsCusJeHead.class); //使用json数据转换复制属性
            } catch (JSONException e) {
                throw e;
            }
            head.setDescription(jeLine.getJeDescription());
            head.setJeHeadStr(result);
            //获取编码规则
            Map<String, String> params = new HashMap<String, String>();
            head.setJeNumber(fndCodingRuleValuesService.getCodeRuleValue(request, jeLine.getDocumentCategory(), jeLine.getDocumentType(), jeLine.getBusinessType(), params));
            jeHeadService.self().insertSelective(request, head);
            jeLine.setJeHeadId(head.getJeHeadId());
        }
        /**/
        lineService.self().insertSelective(request, jeLine);
    }


    /**
     * 生成凭证头
     *
     * @param request
     * @param je
     * @param jeLine
     * @param json
     */
    @Deprecated
    private void createJeHead(IRequest request, JeTemplate je, JeLine jeLine, JSONObject json, Long reverseJeHeadId) {

        String jeHdRule = je.getJeHdRule();
        if (jeHdRule == null || "".equalsIgnoreCase(jeHdRule))
            jeHdRule = "FIX-JE_TRX_DTL_ID";
        String result = this.replacePosition(jeHdRule, jeLine, json);//替换值
        HlsCusJeHead jeHead = new HlsCusJeHead();
        jeHead.setJeHeadStr(result);
        List<HlsCusJeHead> jeHeads = jeHeadMapper.select(jeHead);
        if (jeHeads != null && jeHeads.size() > 0) { //存在这个凭证头,则直接把凭证头的id赋值给jeline的字段
            jeHeads.forEach(item -> {
                if ("CONFIRM".equalsIgnoreCase(item.getJeStatus()))
                    return;
                jeLine.setJeHeadId(jeHeads.get(jeHeads.size() - 1).getJeHeadId());
            });
        }
        if (jeLine.getJeHeadId() == null) {
            HlsCusJeHead head;
            String jsonHeadValue = JSON.toJSONString(jeLine);
            try {
                head = JSON.parseObject(jsonHeadValue, HlsCusJeHead.class); //使用json数据转换复制属性
            } catch (JSONException e) {
                throw e;
            }
            head.setDescription(jeLine.getJeDescription());
            head.setJeHeadStr(result);
            //获取编码规则
            Map<String, String> params = new HashMap<String, String>();
            head.setJeNumber(fndCodingRuleValuesService.getCodeRuleValue(request, jeLine.getDocumentCategory(), jeLine.getDocumentType(), jeLine.getBusinessType(), params));

            if (reverseJeHeadId != null) { //当反冲头ID不为空时,set对应的值
                head.setReverseJeHeadId(reverseJeHeadId);
                head.setReverseFlag(REVERSE_FLAG_R);
                head.setJeStatus(NEW);
            }

            jeHeadService.self().insertSelective(request, head);
            jeLine.setJeHeadId(head.getJeHeadId());

            if (reverseJeHeadId != null) { //当反冲头ID不为空时,原纪录要进行互记
                HlsCusJeHead origin = jeHeadMapper.selectByPrimaryKey(reverseJeHeadId);
                origin.setReverseFlag(REVERSE_FLAG_W);
                origin.setReverseJeHeadId(head.getJeHeadId());
                jeHeadService.self().updateByPrimaryKeySelective(request, origin);
            }
        }
    }


    @Override
    public List<JeTrxDtl> gld320aJeTrxDtlQuery(JeTrxDtl jeTrxDtl, IRequest iRequest, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<String> descriptionList = new ArrayList<String>();
        List<String> statusList = new ArrayList<String>();
        if (jeTrxDtl.getDescription() != null) {
            String[] descriptionArray = jeTrxDtl.getDescription().split(",");
            for (int i = 0; i < descriptionArray.length; i++) {
                descriptionList.add(descriptionArray[i]);
            }
            jeTrxDtl.setQueryList(descriptionList);
        }
        if (jeTrxDtl.getStatus() != null) {
            String[] statusArray = jeTrxDtl.getStatus().split(",");
            for (int i = 0; i < statusArray.length; i++) {
                statusList.add(statusArray[i]);
            }
            jeTrxDtl.setQueryList1(statusList);
        }
        List<JeTrxDtl> maps = jeTrxDtlMapper.gld320aJeTrxDtlQuery(jeTrxDtl);

        maps.forEach(item -> {
            String documentNumber = null;
            switch (item.getJeTrx()) {
                case CSH_WRITE_OFF:
                    HlsCusCshWriteOff cshWriteOff = cshWriteOffMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (cshWriteOff == null)
                        break;
                    HlsCusCshTransaction cshTransaction = cshTransactionMapper.selectByPrimaryKey(cshWriteOff.getCshTransactionId());
                    if (cshTransaction == null)
                        break;
                    String format = format(cshTransaction.getTransactionDate());
                    String transactionNum = cshTransaction.getTransactionNum() != null ? cshTransaction.getTransactionNum() : "";
                    String bankSlipNum = cshTransaction.getBankSlipNum() != null ? cshTransaction.getBankSlipNum() : "";
                    String writeOffType = cshWriteOff.getWriteOffType() != null ? item.getDescription() : "";
                    String s = cshWriteOff.getCshWriteOffAmount() != null ? BigDecimal.valueOf(cshWriteOff.getCshWriteOffAmount()).toPlainString() : "";
                    documentNumber = format.concat("-").concat(transactionNum).concat("-").concat(bankSlipNum).concat("-").concat(writeOffType).concat("-").concat(s);
                    break;
                case CSH_TRANSACTION:
                    HlsCusCshTransaction transaction = cshTransactionMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (transaction == null)
                        break;
                    String format1 = format(transaction.getTransactionDate());
                    String s1 = transaction.getTransactionNum() != null ? transaction.getTransactionNum() : "";
                    String s2 = transaction.getBankSlipNum() != null ? transaction.getBankSlipNum() : "";
                    String s3 = transaction.getTransactionAmount() != null ? BigDecimal.valueOf(transaction.getTransactionAmount()).toPlainString() : "";
                    documentNumber = format1.concat("-").concat(s1).concat("-").concat(s2).concat("-").concat(s3);
                    break;
                case CONTRACT_INCEPT:
                    /*HlsCusConContractIncept conContractIncept = hlsCusConContractInceptMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (conContractIncept == null)
                        break;
                    String format2 = format(conContractIncept.getInceptionOfLease());
                    documentNumber = format2.concat("-").concat("合同起租");
                    break;*/
                case FIN_INCOME_RECOGNITION:
                    HlsCusContractFinanceIncome income = contractFinanceIncomeMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (income == null)
                        break;
                    String format3 = format(income.getStartDate());
                    String format4 = format(income.getEndDate());
                    String s4 = income.getFinanceIncome() != null ? BigDecimal.valueOf(income.getFinanceIncome()).toPlainString() : "";
                    documentNumber = format3.concat("-").concat(format4).concat("-").concat(s4);
                    break;
                case ACR_INVOICE_CONFIRM:
                   /* HlsCusAcrInvoiceHd acrInvoiceHd = acrInvoiceHdMapper.selectByPrimaryKey(item.getJeTrxId());
                    if (acrInvoiceHd == null)
                        break;
                    String format5 = format(acrInvoiceHd.getInvoiceDate());
                    String s5 = acrInvoiceHd.getDocumentNumber() != null ? acrInvoiceHd.getDocumentNumber() : "";
                    String s6 = acrInvoiceHd.getTotalAmount() != null ? BigDecimal.valueOf(acrInvoiceHd.getTotalAmount()).toPlainString() : "";
                    documentNumber = format5.concat("-").concat(s5).concat("-").concat(s6);
                    break;*/
                default:
                    break;
            }
            item.setDocumentNumber(documentNumber);
        });
        return maps;
    }
}