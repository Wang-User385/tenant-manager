package com.hand.hls.vat.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.service.IConContractCashflowService;
import com.hand.hls.cont.utils.BeanRefUtils;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.gld.components.AbstractJeTrxService;
import com.hand.hls.gld.components.JeTrxCommonService;
import com.hand.hls.interfacePlatform.utils.InvoiceBaseUtils;
import com.hand.hls.utils.MathUtil;
import com.hand.hls.vat.dto.*;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.mapper.*;
import com.hand.hls.vat.service.AcrInvoiceHdMidService;
import com.hand.hls.vat.service.AcrInvoiceLnMidService;
import com.hand.hls.vat.service.IAcrInvoiceHdService;
import com.hand.hls.vat.service.IAcrInvoiceLnService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;

@Service
@Transactional(rollbackFor = Exception.class)
public class AcrInvoiceHdServiceImpl extends BaseServiceImpl<HlsCusAcrInvoiceHd> implements IAcrInvoiceHdService {

    //billingType
    private static final String BILLING_TYPE_PRINCIPAL = "PRINCIPAL";
    private static final String BILLING_TYPE_INTEREST = "INTEREST";

    //invoiceStatus
    private static final String INVOICE_STATUS_NEW= "NEW";  //发票已创建
    private static final String INVOICE_STATUS_RED= "RED";  //正票已红冲
    private static final String INVOICE_STATUS_IMPORTED = "IMPORTED"; //正常开票
    private static final String INVOICE_STATUS_NEGATIVE = "NEGATIVE";  //已开具的负数发票
    private static final String INVOICE_STATUS_CANCEL = "CANCEL";  //已开发票作废
    //confirmStatus
    private static final String CONFIRM = "CONFIRM";
    private static final String NEW = "NEW";

    //documentType
    private static final String DOCUMENT_CATEGORY = "AR_INVOICE";
    private static final String DOCUMENT_TYPE = "ACR";
    private static final String BUSINESS_TYPE = "ACR";

    //combineRule
    private static final String GROUP_RULE_CONTRACT = "CN";    //合同
    private static final String GROUP_RULE_BP = "BP";          //对象
    private static final String GROUP_RULE_DEFAULT = "N";      //不合并

    //reverseType
    private static final String REVERSE_TYPE_CANCEL = "CANCEL";
    private static final String REVERSE_TYPE_RED = "RED";

    //invoiceKind
    private static final String INVOICE_KIND_SPECIAL = "0";
    private static final String INVOICE_KIND_NORMAL = "2";

    private static final String DEFAULT_CURRENCY = "CNY";

    private static final String DATE_PATTERN_YM = "yyyy-MM";

    private static final String LEFT_BRACKET = "[";
    private static final String RIGHT_BRACKET = "]";

    //billingStatus
    public static final String BILLING_STATUS_FULL = "FULL";       //全部开票
    public static final String BILLING_STATUS_NOT = "NOT";         //未开票
    public static final String BILLING_STATUS_PARTIAL = "PARTIAL"; //部分开票

    @Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;

    @Autowired
    private HlsCusAcrInvoiceLnMapper acrInvoiceLnMapper;

    @Autowired
    private AcrInvoiceLnDtMapper acrInvoiceLnDtMapper;


    @Autowired
    private HlsCusInvoiceAttributeMapper invoiceAttributeMapper;

    @Autowired
    private IAcrInvoiceLnService acrInvoiceLnService;

    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;

    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    @Autowired
    private IConContractCashflowService conContractCashflowService;

    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;

    @Autowired
    private JeTrxCommonService jeTrxCommonService;

    @Autowired
    private AcrInvoiceHdMidService acrInvoiceHdMidService;

    @Autowired
    private AcrInvoiceLnMidService acrInvoiceLnMidService;
    @Autowired
    private AcrInvoiceHdMidMapper acrInvoiceHdMidMapper;
    @Autowired
    private InvoiceBaseUtils invoiceBaseUtils;

    @Override
    public List<HlsCusAcrInvoiceHd> selectForCreate(String tempId, List<Long> cashflowIds, String groupRule) throws AcrInvoiceException {

        List<HlsCusAcrInvoiceHd> list = acrInvoiceHdMapper.selectForCreateAcrInvoice(cashflowIds, tempId);

        return group(list, groupRule);

    }


    //合并
    private List<HlsCusAcrInvoiceHd> group(List<HlsCusAcrInvoiceHd> list, String groupRule) {
        //不合并直接返回
        if (GROUP_RULE_DEFAULT.equals(groupRule)) {
            return list;
        }

        List<HlsCusAcrInvoiceHd> groupingList = new ArrayList<>();
        Map map = new HashMap();
        //按合同号合并
        if (GROUP_RULE_CONTRACT.equals(groupRule)) {
            map = list.stream().collect(groupingBy(item -> item.getProjectId()));
        }
        //按开票对象合并
        if (GROUP_RULE_BP.equals(groupRule)) {
            map = list.stream().collect(groupingBy(item -> (item.getBpId() + ";" + item.getTaxpayerType())));
        }

        map.forEach((k, v) -> {

            List<HlsCusAcrInvoiceHd> afterGroupList = (List<HlsCusAcrInvoiceHd>) v;

            HlsCusAcrInvoiceHd item = afterGroupList.get(0);
            for (int i = 1; i < afterGroupList.size(); i++) {
                //记录相关联的cashflowId
                item.setCashflowIds(item.getCashflowIds() + "," + afterGroupList.get(i).getCashflowIds());
            }
            groupingList.add(item);

        });

        return groupingList;
    }


    @Override
    public void create(IRequest requestCtx, List<HlsCusAcrInvoiceHd> invoiceHdList) throws AcrInvoiceException {

        //校验
        Long companyId = requestCtx.getCompanyId();
        if (companyId == null) {
            throw new AcrInvoiceException("登录信息过期,请重新登录!");
        }

        //公司税务属性
        HlsCusInvoiceAttribute condition = new HlsCusInvoiceAttribute();
        condition.setCompanyId(companyId);
        InvoiceAttribute attribute = invoiceAttributeMapper.selectByPrimaryKey(condition);
        if (attribute == null) {
            throw new AcrInvoiceException(AcrInvoiceException.WITH_OUT_INVOICE_ATTRIBUTE);
        }

        for (HlsCusAcrInvoiceHd item : invoiceHdList) {
            if (org.apache.commons.lang3.StringUtils.isAnyBlank(item.getInvoiceKind(), item.getBpName(), item.getBpTaxRegistryNum(), item.getBpAddressPhoneNum(), item.getBpBankAccount(), item.getTaxpayerType())) {
                throw new AcrInvoiceException("请维护合开票对象:" + item.getBpIdN() + "的开票信息!");
            }

            if (item.getAcrInvoiceLns() == null) {
                throw new AcrInvoiceException("未维护发票行信息!");
            }

            if (attribute.getLineLimit() == null || (item.getAcrInvoiceLns() != null && (item.getAcrInvoiceLns().size() > attribute.getLineLimit())))
                throw new AcrInvoiceException(AcrInvoiceException.LINE_LIMIT_ERROR);

            long countOfNotHavingTotalAmount = item.getAcrInvoiceLns().stream().filter(o -> (o.getTotalAmount() == null || o.getTotalAmount() == 0D)).count();
            if (countOfNotHavingTotalAmount > 0) {
                throw new AcrInvoiceException("请维护开票金额!");
            }
        }

        //计算头表总开票金额
        calculateTotalAmount(invoiceHdList);

        //根据税务属性拆分
        invoiceHdList = split(invoiceHdList, attribute);

        //计算税额等
        calculateTaxAmount(invoiceHdList);

        invoiceHdList.forEach(invoiceHd -> {
            //存头表相关信息
            invoiceHd.setDocumentCategory(DOCUMENT_CATEGORY);
            invoiceHd.setDocumentType(DOCUMENT_TYPE);
            invoiceHd.setBusinessType(BUSINESS_TYPE);
            invoiceHd.setCompanyId(companyId);
            invoiceHd.setCurrency(DEFAULT_CURRENCY);
            invoiceHd.setInvoiceStatus(INVOICE_STATUS_NEW);
            invoiceHd.setConfirmStatus(NEW);
            invoiceHd.setInvoicePeriod(new SimpleDateFormat(DATE_PATTERN_YM).format(new Date()));

            String documentNumber = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, new HashMap<>());
            invoiceHd.setDocumentNumber(documentNumber);
            self().insertSelective(requestCtx, invoiceHd);


            //遍历头下的所有行
            List<HlsCusAcrInvoiceLn> acrInvoiceLns = invoiceHd.getAcrInvoiceLns();

            for (Integer lineNumber = 1; lineNumber <= acrInvoiceLns.size(); lineNumber++) {
                HlsCusAcrInvoiceLn invoiceLn = invoiceHd.getAcrInvoiceLns().get((lineNumber - 1));
                //存行表
                invoiceLn.setInvoiceHdId(invoiceHd.getInvoiceHdId());
                invoiceLn.setProductName(invoiceLn.getTaxableServiceName());
                invoiceLn.setDescription("法务合同编号:" + invoiceLn.getLegalContractNumber());
                invoiceLn.setQuantity(1L);

                Double totalAmount = invoiceLn.getTotalAmount();
                Double netAmount = CalculateUtil.div(invoiceLn.getTotalAmount(), CalculateUtil.add(1D, invoiceLn.getTaxTypeRate()));
                Double taxAmount = CalculateUtil.sub(totalAmount, netAmount);

                invoiceLn.setTaxAmount(taxAmount);
                invoiceLn.setNetAmount(netAmount);

                invoiceLn.setLineNumber(Long.parseLong(lineNumber.toString()));
                acrInvoiceLnService.insertSelective(requestCtx, invoiceLn);

                if (StringUtils.isBlank(invoiceLn.getCashflowIds())) {
                    invoiceLn.setCashflowIds(invoiceLn.getCashflowId().toString());
                }

                String cashflowIdsInfo = LEFT_BRACKET + invoiceLn.getCashflowIds() + RIGHT_BRACKET;
                //本条发票所对应的现金流
                List<Long> cashflowIds = JSON.parseArray(cashflowIdsInfo, Long.class);
                List<HlsCusConContractCashflow> cashflowList = conContractCashflowMapper.selectByCashflowIds(cashflowIds);

                int cashflowIndex = 0;
                while (invoiceLn.getTotalAmount() > 0 && cashflowIndex < cashflowList.size()) {

                    HlsCusConContractCashflow currentCashFlow = cashflowList.get(cashflowIndex);

                    if (BILLING_TYPE_PRINCIPAL.equals(invoiceLn.getBillingType())) {
                        //本金开票:本次开票金额大于这一条现金流的未开金额,sql中使用了ifnull故此处金额字段不需要判空
                        if (invoiceLn.getTotalAmount() > (currentCashFlow.getPrincipal() - currentCashFlow.getBillingPrincipal())) {
                            //本条记录的开票金额
                            Double billingAmount = CalculateUtil.sub(currentCashFlow.getPrincipal(), currentCashFlow.getBillingPrincipal());

                            //更新现金流开票金额信息
                            currentCashFlow.setBillingPrincipal(currentCashFlow.getPrincipal());
                            currentCashFlow.setBillingAmount(CalculateUtil.add(currentCashFlow.getBillingAmount(), billingAmount));

                            updateConContractCashflow(requestCtx, currentCashFlow);

                            //存关联表
                            saveLineDetail(currentCashFlow.getCashflowId(), invoiceLn.getInvoiceLnId(), invoiceLn.getBillingType(), billingAmount);

                            //剩余本次开票金额
                            invoiceLn.setTotalAmount(CalculateUtil.sub(invoiceLn.getTotalAmount(), billingAmount));

                            cashflowIndex++;

                        } else {

                            currentCashFlow.setBillingPrincipal(CalculateUtil.add(currentCashFlow.getBillingPrincipal(), invoiceLn.getTotalAmount()));
                            currentCashFlow.setBillingAmount(CalculateUtil.add(currentCashFlow.getBillingAmount(), invoiceLn.getTotalAmount()));

                            updateConContractCashflow(requestCtx, currentCashFlow);

                            //存关联表
                            saveLineDetail(currentCashFlow.getCashflowId(), invoiceLn.getInvoiceLnId(), invoiceLn.getBillingType(), invoiceLn.getTotalAmount());

                            invoiceLn.setTotalAmount(0.0);

                            cashflowIndex++;

                        }
                    } else if (BILLING_TYPE_INTEREST.equals(invoiceLn.getBillingType())) {
                        //利息开票:本次开票金额大于这一条现金流的未开金额
                        if (invoiceLn.getTotalAmount() > (currentCashFlow.getInterest() - currentCashFlow.getBillingInterest())) {

                            Double billingAmount = CalculateUtil.sub(currentCashFlow.getInterest(), currentCashFlow.getBillingInterest());

                            currentCashFlow.setBillingInterest(currentCashFlow.getInterest());
                            currentCashFlow.setBillingAmount(CalculateUtil.add(currentCashFlow.getBillingAmount(), billingAmount));

                            updateConContractCashflow(requestCtx, currentCashFlow);

                            //存关联表
                            saveLineDetail(currentCashFlow.getCashflowId(), invoiceLn.getInvoiceLnId(), invoiceLn.getBillingType(), billingAmount);

                            invoiceLn.setTotalAmount(CalculateUtil.sub(invoiceLn.getTotalAmount(), billingAmount));

                            cashflowIndex++;

                        } else {
                            currentCashFlow.setBillingInterest(currentCashFlow.getBillingInterest() + invoiceLn.getTotalAmount());
                            currentCashFlow.setBillingAmount(currentCashFlow.getBillingAmount() + invoiceLn.getTotalAmount());

                            updateConContractCashflow(requestCtx, currentCashFlow);

                            //存关联表
                            saveLineDetail(currentCashFlow.getCashflowId(), invoiceLn.getInvoiceLnId(), invoiceLn.getBillingType(), invoiceLn.getTotalAmount());

                            invoiceLn.setTotalAmount(0.0D);
                            cashflowIndex++;
                        }
                    } else {

                        //普通开票:本次开票金额大于这一条现金流的未开金额(不存在本金利息的区别)
                        if (invoiceLn.getTotalAmount() > (currentCashFlow.getDueAmount() - currentCashFlow.getBillingAmount())) {

                            Double billingAmount = CalculateUtil.sub(currentCashFlow.getDueAmount(), currentCashFlow.getBillingAmount());

                            currentCashFlow.setBillingAmount(currentCashFlow.getDueAmount());

                            updateConContractCashflow(requestCtx, currentCashFlow);

                            //存关联表
                            saveLineDetail(currentCashFlow.getCashflowId(), invoiceLn.getInvoiceLnId(), invoiceLn.getBillingType(), billingAmount);

                            invoiceLn.setTotalAmount(CalculateUtil.sub(invoiceLn.getTotalAmount(), billingAmount));

                            cashflowIndex++;

                        } else {

                            currentCashFlow.setBillingAmount(currentCashFlow.getBillingAmount() + invoiceLn.getTotalAmount());

                            updateConContractCashflow(requestCtx, currentCashFlow);

                            //存关联表
                            saveLineDetail(currentCashFlow.getCashflowId(), invoiceLn.getInvoiceLnId(), "", invoiceLn.getTotalAmount());

                            invoiceLn.setTotalAmount(0.0);

                            cashflowIndex++;
                        }
                    }
                }

            }

            //发票接口生产环境未采购
            /*invoiceBaseUtils.createInvoiceItfc(requestCtx, invoiceHd);*/
        });
    }


    //统计头表开票总额，用于判定是否拆分
    private void calculateTotalAmount(List<HlsCusAcrInvoiceHd> invoiceHdList) {

        invoiceHdList.forEach(invoiceHd -> {

            invoiceHd.setTotalAmount(0.0D);

            //累加行的开票金额
            invoiceHd.getAcrInvoiceLns().forEach(invoiceLn -> {
                Double totalAmount = invoiceLn.getTotalAmount();
                invoiceHd.setTotalAmount(CalculateUtil.add(invoiceHd.getTotalAmount(), totalAmount));
            });

        });

    }

    //统计头表不含税、税额、价税合计
    private void calculateTaxAmount(List<HlsCusAcrInvoiceHd> invoiceHdList) {

        invoiceHdList.forEach(invoiceHd -> {

            invoiceHd.setNetAmount(0.0);
            invoiceHd.setTaxAmount(0.0);
            invoiceHd.setTotalAmount(0.0);

            //累加行的开票金额
            invoiceHd.getAcrInvoiceLns().forEach(invoiceLn -> {

                Double totalAmount = invoiceLn.getTotalAmount();
                Double taxAmount = CalculateUtil.mul(CalculateUtil.div(invoiceLn.getTotalAmount(), CalculateUtil.add(1D, invoiceLn.getTaxTypeRate())), invoiceLn.getTaxTypeRate());
                Double netAmount = CalculateUtil.sub(totalAmount, taxAmount);

                invoiceHd.setTotalAmount(CalculateUtil.add(invoiceHd.getTotalAmount(), totalAmount));
                invoiceHd.setTaxAmount(CalculateUtil.add(invoiceHd.getTaxAmount(), taxAmount));
                invoiceHd.setNetAmount(CalculateUtil.add(invoiceHd.getNetAmount(), netAmount));

            });
        });

    }

    //拆分
    private List<HlsCusAcrInvoiceHd> split(List<HlsCusAcrInvoiceHd> invoiceHdList, InvoiceAttribute attribute) throws AcrInvoiceException {

        //承载拆分后的发票头表list
        List<HlsCusAcrInvoiceHd> spiltInvoiceHds = new ArrayList<>();

        for (HlsCusAcrInvoiceHd acrInvoiceHd : invoiceHdList) {

            //没有发票类型时报错
            if (!INVOICE_KIND_SPECIAL.equals(acrInvoiceHd.getInvoiceKind()) && !INVOICE_KIND_NORMAL.equals(acrInvoiceHd.getInvoiceKind())) {
                throw new AcrInvoiceException(AcrInvoiceException.ERROR_INVOICE_KIND);
            }

            //发票限额  开票系统限额增对不含税金额
            Double limit;
            List<HlsCusAcrInvoiceLn> originLns = acrInvoiceHd.getAcrInvoiceLns();
            if (INVOICE_KIND_SPECIAL.equals(acrInvoiceHd.getInvoiceKind())) {
                limit = MathUtil.round(attribute.getSpecialInvoiceLimit()*(1+originLns.get(0).getTaxTypeRate()),2);
            } else {
                limit = MathUtil.round(attribute.getNormalInvoiceLimit()*(1+originLns.get(0).getTaxTypeRate()),2);
            }

            //未定义限额或头表total_amount未超过限额,不拆分
            if (limit == null || acrInvoiceHd.getTotalAmount() < limit) {
                spiltInvoiceHds.add(acrInvoiceHd);
            } else {
                //拆分de情况
                //原来所有行表

                //拆分后de发票张数
                int count = ((Double) Math.ceil(acrInvoiceHd.getTotalAmount() / limit)).intValue();
                //是否有余数，有余数则为最后一张发票的金额
                Double left = BigDecimal.valueOf(acrInvoiceHd.getTotalAmount()).remainder(BigDecimal.valueOf(limit)).doubleValue();

                List<HlsCusAcrInvoiceHd> nowHds = new ArrayList<>();

                for (int m = 1; m <= count; m++) {

                    HlsCusAcrInvoiceHd invoiceHd = new HlsCusAcrInvoiceHd();
                    BeanRefUtils.beanToBean(acrInvoiceHd, invoiceHd, hlsBeanRefUtilService);
                    invoiceHd.setTotalAmount(limit);

                    //本次额度
                    Double credit = limit;
                    if (m == count && new BigDecimal(left).compareTo(new BigDecimal(0.0D)) > 0) {
                        credit = left;
                        invoiceHd.setTotalAmount(left);
                    }

                    //本条拆分头表的所有行
                    List<HlsCusAcrInvoiceLn> nowLns = new ArrayList<>();

                    while (new BigDecimal(credit).compareTo(new BigDecimal(0.0D)) > 0 && originLns.stream().filter(o -> !"FULL".equals(o.getSplitStatus())).count() != 0) {
                        for (HlsCusAcrInvoiceLn invoiceLn : originLns) {
                            //行表小于限额
                            if (!"FULL".equals(invoiceLn.getSplitStatus()) && new BigDecimal(invoiceLn.getTotalAmount()).compareTo(new BigDecimal(credit)) <= 0) {
                                HlsCusAcrInvoiceLn nowLn = new HlsCusAcrInvoiceLn();
                                BeanRefUtils.beanToBean(invoiceLn, nowLn, hlsBeanRefUtilService);
                                nowLn.setTotalAmount(invoiceLn.getTotalAmount());
                                invoiceLn.setTotalAmount(0.0D);
                                invoiceLn.setSplitStatus("FULL");
                                nowLns.add(nowLn);
                                credit = CalculateUtil.sub(credit, nowLn.getTotalAmount());
                            } else if (!"FULL".equals(invoiceLn.getSplitStatus()) && new BigDecimal(invoiceLn.getTotalAmount()).compareTo(new BigDecimal(credit)) > 0) {
                                //行表大于剩余限额
                                HlsCusAcrInvoiceLn nowLn = new HlsCusAcrInvoiceLn();
                                BeanRefUtils.beanToBean(invoiceLn, nowLn, hlsBeanRefUtilService);
                                nowLn.setTotalAmount(credit);
                                invoiceLn.setTotalAmount(CalculateUtil.sub(invoiceLn.getTotalAmount(), credit));
                                nowLns.add(nowLn);
                                credit = 0.0D;
                            }
                        }
                    }
                    invoiceHd.setAcrInvoiceLns(nowLns);
                    nowHds.add(invoiceHd);
                }
                spiltInvoiceHds.addAll(nowHds);
            }
        }
        return spiltInvoiceHds;
    }

    // 存关联表,用于关联发票行与开票现金流
    private void saveLineDetail(Long cashflowId, Long invoiceLnId, String billingType, Double billingAmount) {

        AcrInvoiceLnDt dt = new AcrInvoiceLnDt();
        dt.setCashflowId(cashflowId);
        dt.setInvoiceLnId(invoiceLnId);
        dt.setBillingType(billingType);
        dt.setBillingAmount(billingAmount);

        acrInvoiceLnDtMapper.insertSelective(dt);

    }


    //发票删除
    @Override
    public List<HlsCusAcrInvoiceHd> delete(IRequest requestCtx, List<HlsCusAcrInvoiceHd> invoiceHdList) throws AcrInvoiceException {
        //发票确认--删除校验
        long newCount = invoiceHdList.stream().filter(o -> INVOICE_STATUS_NEW.equals(o.getInvoiceStatus())).count();
        if (newCount != invoiceHdList.size()) {
            throw new AcrInvoiceException("只能删除新建的发票!");
        }
        return this.deleteAcrInvoiceHd(requestCtx, invoiceHdList, false,"DELETE");
    }

    //发票反冲
    @Override
    public void reverse(IRequest requestCtx, List<HlsCusAcrInvoiceHd> invoiceHdList, String reverseType) throws AcrInvoiceException {
        //发票反冲校验
        for (HlsCusAcrInvoiceHd invoiceHd : invoiceHdList) {
            if (INVOICE_STATUS_RED.equals(invoiceHd.getInvoiceStatus()) || INVOICE_STATUS_CANCEL.equals(invoiceHd.getInvoiceStatus())) {
                throw new AcrInvoiceException("该发票已反冲!");
            }
            if (INVOICE_STATUS_NEGATIVE.equals(invoiceHd.getInvoiceStatus())) {
                throw new AcrInvoiceException("红字发票不能反冲!");
            }
            if (REVERSE_TYPE_CANCEL.equals(reverseType)) {
                if (!new SimpleDateFormat(DATE_PATTERN_YM).format(new Date()).equals(invoiceHd.getInvoicePeriod())) {
                    throw new AcrInvoiceException("该发票已超期，请选择反冲操作!");
                }
                if("1".equals(invoiceHd.getBillingWay())){
                    throw new AcrInvoiceException("电子发票不支持作废，请选择反冲操作!");
                }
            }
//            if (REVERSE_TYPE_RED.equals(reverseType)) {
//                if (new SimpleDateFormat(DATE_PATTERN_YM).format(new Date()).equals(invoiceHd.getInvoicePeriod())) {
//                    throw new AcrInvoiceException("该发票尚未超期，请选择作废操作!");
//                }
//            }
        }

        deleteAcrInvoiceHd(requestCtx, invoiceHdList, true, reverseType);

    }

    /**
     * 发票删除--同时updateCashFlow
     *
     * @param invoiceHdList
     * @return
     * @condition 1.reverseFlag->true->反冲 2.reverseFlag->false->删除
     */
    private List<HlsCusAcrInvoiceHd> deleteAcrInvoiceHd(IRequest requestCtx, List<HlsCusAcrInvoiceHd> invoiceHdList, boolean reverseFlag,String reverseType) throws AcrInvoiceException {
        for (HlsCusAcrInvoiceHd hd : invoiceHdList) {
            HlsCusAcrInvoiceLn condition = new HlsCusAcrInvoiceLn();
            condition.setInvoiceHdId(hd.getInvoiceHdId());
            List<HlsCusAcrInvoiceLn> lnList = acrInvoiceLnMapper.select(condition);
            //遍历行表
            for (HlsCusAcrInvoiceLn ln : lnList) {
                AcrInvoiceLnDt dtCondition = new AcrInvoiceLnDt();
                dtCondition.setInvoiceLnId(ln.getInvoiceLnId());
                List<AcrInvoiceLnDt> lnDtList = acrInvoiceLnDtMapper.select(dtCondition);
                //遍历ln明细表
                for (AcrInvoiceLnDt lnDt : lnDtList) {
                    HlsCusConContractCashflow cashflow = conContractCashflowMapper.selectByPrimaryKey(lnDt.getCashflowId());
                    if (BILLING_TYPE_PRINCIPAL.equals(lnDt.getBillingType())) {
                        cashflow.setBillingPrincipal(CalculateUtil.sub(cashflow.getBillingPrincipal(), lnDt.getBillingAmount()));
                    } else if (BILLING_TYPE_INTEREST.equals(lnDt.getBillingType())) {
                        cashflow.setBillingInterest(CalculateUtil.sub(cashflow.getBillingInterest(), lnDt.getBillingAmount()));
                    }

                    cashflow.setBillingAmount(CalculateUtil.sub(cashflow.getBillingAmount(), lnDt.getBillingAmount()));
                    updateConContractCashflow(requestCtx, cashflow);
                    if (!reverseFlag) {
                        acrInvoiceLnDtMapper.deleteByPrimaryKey(lnDt);
                    }
                }
                if (!reverseFlag) {
                    acrInvoiceLnMapper.deleteByPrimaryKey(ln);
                }
            }
            if (!reverseFlag) {
                acrInvoiceHdMapper.deleteByPrimaryKey(hd);
            } else {
                hd.setReversedFlag(BaseConstants.YES);
                hd.setReverseDate(new Date());
                hd.setInvoiceStatus(reverseType);
                acrInvoiceHdMapper.updateByPrimaryKeySelective(hd);

                if(REVERSE_TYPE_RED.equals(reverseType)){
                    insertReverseData(requestCtx,hd);
                }
                if(REVERSE_TYPE_CANCEL.equals(reverseType)){
                    ////发票接口生产环境未采购
                    /*invoiceBaseUtils.cancelInvoiceItfc(requestCtx,hd);*/
                }
            }

        }
        return invoiceHdList;
    }

    private void insertReverseData(IRequest requestCtx, HlsCusAcrInvoiceHd acrInvoiceHd) {
        HlsCusAcrInvoiceHd acrInvoiceHdReverse = acrInvoiceHdMapper.selectByPrimaryKey(acrInvoiceHd.getInvoiceHdId());
        acrInvoiceHdReverse.setTotalAmount(-acrInvoiceHdReverse.getTotalAmount());
        acrInvoiceHdReverse.setTaxAmount(-acrInvoiceHdReverse.getTaxAmount());
        acrInvoiceHdReverse.setNetAmount(-acrInvoiceHdReverse.getNetAmount());
        acrInvoiceHdReverse.setInvoiceStatus(INVOICE_STATUS_NEW);
        acrInvoiceHdReverse.setConfirmStatus(NEW);
        acrInvoiceHdReverse.setInvoiceHdId(null);
        acrInvoiceHdReverse.setInvoiceCode(null);
        acrInvoiceHdReverse.setInvoiceNumber(null);
        acrInvoiceHdReverse.setSourceInvoiceHeaderId(acrInvoiceHd.getInvoiceHdId());
        acrInvoiceHdReverse.setReversedFlag("W");
        String documentNumber = fndCodingRuleValuesService.getCodeRuleValue(requestCtx, DOCUMENT_CATEGORY, DOCUMENT_TYPE, BUSINESS_TYPE, new HashMap<>());
        acrInvoiceHdReverse.setDocumentNumber(documentNumber);
        self().insert(requestCtx, acrInvoiceHdReverse);

        HlsCusAcrInvoiceLn condition = new HlsCusAcrInvoiceLn();
        condition.setInvoiceHdId(acrInvoiceHd.getInvoiceHdId());
        List<HlsCusAcrInvoiceLn> lnList = acrInvoiceLnMapper.select(condition);
        //遍历行表
        for (HlsCusAcrInvoiceLn ln : lnList) {
            HlsCusAcrInvoiceLn acrInvoiceLnReverse = new HlsCusAcrInvoiceLn();
            acrInvoiceLnReverse.setInvoiceLnId(ln.getInvoiceLnId());
            acrInvoiceLnReverse = acrInvoiceLnService.selectByPrimaryKey(requestCtx, acrInvoiceLnReverse);
            acrInvoiceLnReverse.setTotalAmount(-acrInvoiceLnReverse.getTotalAmount());
            acrInvoiceLnReverse.setTaxAmount(-acrInvoiceLnReverse.getTaxAmount());
            acrInvoiceLnReverse.setNetAmount(-acrInvoiceLnReverse.getNetAmount());
            //acrInvoiceLnReverse.setPrice(-acrInvoiceLnReverse.getPrice());
            acrInvoiceLnReverse.setInvoiceHdId(acrInvoiceHdReverse.getInvoiceHdId());
            acrInvoiceLnReverse.setInvoiceLnId(null);
            acrInvoiceLnService.insert(requestCtx, acrInvoiceLnReverse);

            AcrInvoiceLnDt dtCondition = new AcrInvoiceLnDt();
            dtCondition.setInvoiceLnId(ln.getInvoiceLnId());
            List<AcrInvoiceLnDt> lnDtList = acrInvoiceLnDtMapper.select(dtCondition);
            for (AcrInvoiceLnDt lnDt : lnDtList) {
                AcrInvoiceLnDt acrInvoiceLnDtReverse =lnDt;
                acrInvoiceLnDtReverse.setInvoiceLnId(acrInvoiceLnReverse.getInvoiceLnId());
                acrInvoiceLnDtReverse.setInvoiceLnDtId(null);
                acrInvoiceLnDtReverse.setBillingAmount(-lnDt.getBillingAmount());
                acrInvoiceLnDtMapper.insert(acrInvoiceLnDtReverse);
            }
        }
        //发票接口生产环境未采购
        /*invoiceBaseUtils.reverseInvoiceItfc(requestCtx,acrInvoiceHdReverse);*/
    }

    @Override
    public void confirm(IRequest requestCtx, List<HlsCusAcrInvoiceHd> invoiceHdList) throws AcrInvoiceException {
        long confirmedCount = invoiceHdList.stream().filter(o -> CONFIRM.equals(o.getConfirmStatus())).count();
        if (confirmedCount > 0) {
            throw new AcrInvoiceException("发票已确认,请重新选择!");
        }
        long newCount = invoiceHdList.stream().filter(o -> INVOICE_STATUS_NEW.equals(o.getInvoiceStatus())).count();
        if (newCount > 0) {
            throw new AcrInvoiceException("发票未开具,请重新选择!");
        }
        invoiceHdList.forEach(hd -> {
            hd.setConfirmStatus(CONFIRM);
            hd.setConfirmedBy(requestCtx.getUserId());
            hd.setConfirmedDate(new Date());
            hd.setReceiptFlag("Y");
            acrInvoiceHdMapper.updateByPrimaryKeySelective(hd);

            //生成凭证 --销项发票确认
//            AbstractJeTrxService jeTrxService = jeTrxCommonService.map.get("ACR_INVOICE_CONFIRM");
//            Map params = new HashMap<>();
//            params.put("jeTrxId", hd.getInvoiceHdId());
//            params.put("companyId", requestCtx.getCompanyId());
//            params.put("contractId", hd.getContractId());
//            params.put("jeSourceId", hd.getInvoiceHdId());
//            params.put("sourceDoc", "ACR_INVOICE_HD");
//            jeTrxService.process(requestCtx, params);
        });
    }

    //根据billing_amount与due_amount更新合同现金流开票状态
    public void updateConContractCashflow(IRequest requestCtx, HlsCusConContractCashflow conContractCashflow) {

        //应收金额
        Double dueAmount = conContractCashflow.getDueAmount();
        if (dueAmount == null) {
            dueAmount = 0.0D;
        }

        //开票金额
        Double billingAmount = conContractCashflow.getBillingAmount();
        if (billingAmount == null) {
            billingAmount = 0.0D;
        }

        if (dueAmount.compareTo(billingAmount) == 0) {
            conContractCashflow.setBillingStatus(BILLING_STATUS_FULL);
        } else if (dueAmount.compareTo(billingAmount) > 0 && billingAmount.compareTo(0.0D) != 0) {
            conContractCashflow.setBillingStatus(BILLING_STATUS_PARTIAL);
        } else if (dueAmount.compareTo(billingAmount) > 0 && billingAmount.compareTo(0.0D) == 0) {
            conContractCashflow.setBillingStatus(BILLING_STATUS_NOT);
        }

        conContractCashflowService.updateByPrimaryKeySelective(requestCtx, conContractCashflow);

    }


    @Override
    public List<HlsCusAcrInvoiceHd> queryAcrInvoiceHdDetail(HlsCusAcrInvoiceHd condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return acrInvoiceHdMapper.queryAcrInvoiceHdDetail(condition);
    }

    @Override
    public List<HlsCusAcrInvoiceHd> searchInvoicHdHomeQuery(HlsCusAcrInvoiceHd condition, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return acrInvoiceHdMapper.searchInvoicHdHomeQuery(condition);
    }

    @Override
    public HlsCusAcrInvoiceHd queryAcrInvoiceHdDetailById(Long invoiceHdId) {
        return acrInvoiceHdMapper.queryAcrInvoiceHdDetailById(invoiceHdId);
    }
}