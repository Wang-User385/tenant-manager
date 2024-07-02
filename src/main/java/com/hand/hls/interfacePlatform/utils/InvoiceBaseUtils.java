package com.hand.hls.interfacePlatform.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.BaseConstants;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.bp.components.CalculateUtil;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.fct.mapper.HlsCusFctProjectAttachmentMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.vat.dto.AcrInvoiceLnDt;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceLn;
import com.hand.hls.vat.dto.HlsInvoiceInterfaceInfo;
import com.hand.hls.vat.exception.AcrInvoiceException;
import com.hand.hls.vat.mapper.AcrInvoiceLnDtMapper;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceLnMapper;
import com.hand.hls.vat.mapper.HlsInvoiceInterfaceInfoMapper;
import com.hand.hls.vat.service.HlsCusAcrInvoiceHdService;
import com.hand.hls.vat.service.IAcrInvoiceHdService;
import com.hand.hls.vat.service.IAcrInvoiceLnService;
import com.hand.hls.vat.service.IHlsInvoiceInterfaceInfoService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.management.OperationsException;
import java.io.File;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangdan
 * @date 2022/10
 */
@Component
public class InvoiceBaseUtils {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HlsCusAcrInvoiceHdService acrInvoiceHdService;
    @Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;
    @Autowired
    private IAcrInvoiceLnService acrInvoiceLnService;
    @Autowired
    private HlsCusBpMasterService bpMasterService;
    @Autowired
    private IHlsInvoiceInterfaceInfoService invoiceInterfaceInfoService;
    @Autowired
    private HlsInvoiceInterfaceInfoMapper invoiceInterfaceInfoMapper;
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private IFndAttachmentMultiService fndAttachmentMultiService;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private IAcrInvoiceHdService hdService;
    @Autowired
    private HlsCusAcrInvoiceLnMapper acrInvoiceLnMapper;
    @Autowired
    private AcrInvoiceLnDtMapper acrInvoiceLnDtMapper;
    @Autowired
    private HlsCusConContractCashflowMapper conContractCashflowMapper;

    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;

    private static String downLoadPath = "/u01/hls_attachment/invoice/";
    private static String PDF = ".pdf";
    //附件表名
    public static final String FND_ATM_ATTACHMENT_MULTI = "fnd_atm_attachment_multi";
    //billingType
    private static final String BILLING_TYPE_PRINCIPAL = "PRINCIPAL";
    private static final String BILLING_TYPE_INTEREST = "INTEREST";
    //开票终端

    String terminalCode;
    //销方信息
    String sellerTaxCode,sellerName,sellerAddressTel,sellerBankAccount;
    private void init() {
        terminalCode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("INVOICE_INTERFACE", "terminalCode");
        sellerTaxCode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("INVOICE_INTERFACE", "sellerTaxCode");
        sellerName = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("INVOICE_INTERFACE", "sellerName");
        sellerAddressTel = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("INVOICE_INTERFACE", "sellerAddressTel");
        sellerBankAccount = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("INVOICE_INTERFACE", "sellerBankAccount");
    }

    private static String createInvoiceUrl = "/hitf/v2p/rest/invoke/R0RIQ0c6QkFJV0FOR0pTOkpTMDAy?access_token=";
    private static String reverseInvoiceUrl = "/hitf/v2p/rest/invoke/R0RIQ0c6QkFJV0FOR0pTOkpTMDAy?access_token=";
    private static String queryInvoiceUrl = "/hitf/v2p/rest/invoke/R0RIQ0c6QkFJV0FOR0pTOkpTMDA1?access_token=";
    private static String queryUrlUrl = "/hitf/v2p/rest/invoke/R0RIQ0c6QkFJV0FOR0pTOkpTMDA2?access_token=";
    private static String cancelInvoiceUrl = "/hitf/v2p/rest/invoke/R0RIQ0c6QkFJV0FOR0pTOkpTMDA3?access_token=";


    static Map<String, String> invoiceKindCode = new HashMap<String, String>(200) {
    };

    /* 发票类型代码:004-专票;007-普通发票;026-普通电子发票;028-电子专用发票;005-机动车发票;006-二手车发票;025-卷式发票;*/
    static {
        invoiceKindCode.put("0", "004");
        invoiceKindCode.put("2", "007");
        invoiceKindCode.put("VEHICLE_INVOICE", "005");
        invoiceKindCode.put("SECOND_HAND_INVOICE", "006");
        invoiceKindCode.put("ROLL_INVOICE", "025");
    }

    static Map<String, String> eleInvoiceKindCode = new HashMap<String, String>(200) {
    };

    static {
        eleInvoiceKindCode.put("2", "026");
        eleInvoiceKindCode.put("0", "028");
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmm");
    SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM");

    private Object dealValue(Object object) {
        Object res;
        if (null == object) {
            res = "";
        } else {
            res = object;
        }
        return res;
    }

    private BigDecimal dealDoubleAmount(Double amount) {
        BigDecimal bAmount = new BigDecimal(amount);
        bAmount = bAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bAmount;
    }


    /**
     * description
     * 发票开具 蓝字发票
     */
    public void createInvoiceItfc(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        JSONObject result = new JSONObject(new LinkedHashMap());
        JSONObject content = createInvoiceInfo(requestCtx, invoiceHd);
        result = interfacePlatformUtils.getInterfaceRequest(content, createInvoiceUrl, "发票开具");

        String msg = result.getString("message");
        String code = result.getString("code");
        boolean success = result.getBoolean("success");

        HlsInvoiceInterfaceInfo info = new HlsInvoiceInterfaceInfo();
        info.setDocumentId(invoiceHd.getInvoiceHdId());
        info.setDocumentType("ACR_INVOICE");
        invoiceInterfaceInfoMapper.deleteByDocument(info);
        HlsCusAcrInvoiceHd acrInvoiceHd = new HlsCusAcrInvoiceHd();
        acrInvoiceHd.setInvoiceHdId(invoiceHd.getInvoiceHdId());
        if (true == success) {
            JSONArray fpIssueResponses = result.getJSONArray("fpIssueResponses");
            JSONObject fpIssueItem = fpIssueResponses.getJSONObject(0);

            acrInvoiceHd.setInvoiceCode(fpIssueItem.getString("invoiceCode"));
            acrInvoiceHd.setInvoiceNumber(fpIssueItem.getString("invoiceNo"));
            acrInvoiceHd.setInvoiceStatus("IMPORTED");//正常发票
            acrInvoiceHdService.updateByPrimaryKeySelective(requestCtx, acrInvoiceHd);

            info.setReqserialNo(fpIssueItem.getString("reqSerialNo"));
            info.setInvoiceCode(fpIssueItem.getString("invoiceCode"));
            info.setInvoiceNo(fpIssueItem.getString("invoiceNo"));
            info.setInvoiceKindCode(fpIssueItem.getString("invoiceKindCode"));
            info.setInvoiceStatus(fpIssueItem.getString("invoiceStatus"));
            info.setDzfpUrl(fpIssueItem.getString("dzfpUrl"));
            info.setInvoiceType("0");
            info.setResponseJson(result.toJSONString());
            info.setMessage(msg);
            info.setStatus("success");
            invoiceInterfaceInfoService.insertSelective(requestCtx, info);
        } else {
            acrInvoiceHd.setInvoiceStatus("IMPORTED_FAILURE");//发票开具失败
            acrInvoiceHdService.updateByPrimaryKeySelective(requestCtx, acrInvoiceHd);

            info.setResponseJson(result.toJSONString());
            info.setMessage(msg);
            info.setStatus("failure");
            invoiceInterfaceInfoService.insertSelective(requestCtx, info);
        }
    }

    private JSONObject createInvoiceInfo(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        init();
        JSONObject content = new JSONObject(new LinkedHashMap());

        invoiceHd = acrInvoiceHdMapper.queryAcrInvoiceHdDetailNew(invoiceHd).get(0);

        content.put("reqSerialNo", invoiceHd.getInvoiceHdId());//开票序列号，一张发票唯一标识,税控服务器校验重复开票的依据
        content.put("terminalCode", terminalCode);//开票终端唯一性标识

        String invoiceKind = "";
        if ("1".equals(invoiceHd.getBillingWay())) {
            invoiceKind = eleInvoiceKindCode.get(invoiceHd.getInvoiceKind());
        } else {
            invoiceKind = invoiceKindCode.get(invoiceHd.getInvoiceKind());
        }
        content.put("invoiceKindCode", invoiceKind);//发票类型代码
        content.put("invoiceType", "0");//开票类型 0-蓝字发票、正数票开具，1-红字发票，负数票开具
        content.put("buyerTaxCode", invoiceHd.getBpTaxRegistryNum());//购货单位识别号
        content.put("buyerName", invoiceHd.getBpName());//购货单位名称
        content.put("buyerAddressTel", invoiceHd.getBpAddressPhoneNum());//购货单位地址电话
        content.put("buyerBankAccount", invoiceHd.getBpBankAccount());//购货单位银行帐号
        content.put("buyerEmail ", "");//购方邮箱
        content.put("buyerMobile", "");//购方手机号
        content.put("sellerTaxCode", sellerTaxCode);//销货单位识别号
        content.put("sellerName", sellerName);//销货单位名称
        content.put("sellerAddressTel", sellerAddressTel);//销货单位地址电话
        content.put("sellerBankAccount", sellerBankAccount);//销货单位银行帐号
        content.put("sellerContact", "");//销货方联系方式
        content.put("specialFlag", "00");// 特殊票种标识 “00”不是 “01”农产品销售 “02”农产品收购 “06”抵扣通行费 “07”其它通行费 “08”成品油销售 “12”机动车
        content.put("taxationMode", "0");//征税方式  0：普通征税  1：减按计征  2：差额征税
        content.put("minusAmount", 0.0);//差额征税扣除额 小数点后2位，当zsfs为2时扣除额为必填项。
        content.put("amount", invoiceHd.getNetAmount());//合计金额,小数点后2位，不含税
        content.put("taxAmount", invoiceHd.getTaxAmount());//计税额,小数点后2位
        content.put("totalAmount", invoiceHd.getTotalAmount());//价税合计
        content.put("payee", "");//收款人
        content.put("checker", "");//复核人
        content.put("drawer", invoiceHd.getCreatedByN());//开票人
        content.put("invoiceRemark", invoiceHd.getDescription());//备注

        content.put("billSource", null);//开票订单信息来源 0.在线开票订单 1.易开票订单 2.云开票订单 3.ERP开票订单 4.扫码开票订单
        content.put("billCode", null);//开票订单编号
        content.put("actType", null);//开票行为类型， NUll或者0.为立即开具，1.表示不立即开具，2.表示返回自助二维码，3.进开票前预处理后自动开票
        content.put("invoiceFormat", "");//卷式发票票样
        content.put("expand1", "");//扩展字段

        JSONArray goodsList = new JSONArray();
        List<HlsCusAcrInvoiceLn> acrInvoiceLns = new ArrayList<>();
        HlsCusAcrInvoiceLn ln = new HlsCusAcrInvoiceLn();
        ln.setInvoiceHdId(invoiceHd.getInvoiceHdId());
        acrInvoiceLns = acrInvoiceLnService.select(requestCtx, ln, 1, 999);
        for (HlsCusAcrInvoiceLn item : acrInvoiceLns) {
            JSONObject goodItem = new JSONObject(new LinkedHashMap());
            goodItem.put("lineKind", "0");//发票行性质 0.正常行,默认值 1.折扣行 2.被折扣行 3.混合行，需开票接口处理折扣
            goodItem.put("goodsName", item.getProductName());//商品名称
            goodItem.put("taxKindCode", "3040502020200000000");//税收分类编码
            goodItem.put("goodsCode", "");//纳税人自行编码，如果税收分类编码为空，会根据该值查自该企业开票商
            goodItem.put("taxFlag", "0");//0 不含税   1 含税, 电票必须是不含税
            goodItem.put("qty", item.getQuantity());//商品数量
            goodItem.put("price", item.getTotalAmount());//单价
            goodItem.put("nonTaxPrice", item.getNetAmount());//不含税单价
            goodItem.put("amount", item.getNetAmount());//不含税金额
            goodItem.put("totalAmount", item.getTotalAmount());//含税金额
            goodItem.put("taxRate", item.getTaxTypeRate());//税率
            goodItem.put("taxAmount", item.getTaxAmount());//税额
            goodItem.put("dutyFree", "");//免税类型，空代表无 0.正常税率 1.免税和 2.不征增值税 3.普通零税率
            goodItem.put("couponFlag", "0");//是否使用优惠政策标识，0未使用，1使用
            goodItem.put("couponPolicy", "");//优惠政策名称，例如：免税,先征后退,简易征收,按5%简易征收等,如果couponFlag为1时，此项必填
            goodItem.put("outId", "");//外部流水ID，用于回传时业务系统确定那行发票商品
            goodItem.put("taxItem", "");//商品税目，保留字段，目前为空
            goodItem.put("specName", "");//规格型号
            goodItem.put("saleUnit", "");//单位,元/米，元/顿，元/件
            goodsList.add(goodItem);
        }
        content.put("goodsList", goodsList);
        return content;
    }

    /**
     * description
     * 发票开具 红字发票
     */
    public void reverseInvoiceItfc(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        JSONObject result = new JSONObject(new LinkedHashMap());
        JSONObject content = reverseInvoiceInfo(requestCtx, invoiceHd);
        //System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, reverseInvoiceUrl, "发票红冲");

        String msg = result.getString("message");
        String code = result.getString("code");
        boolean success = result.getBoolean("success");

        HlsInvoiceInterfaceInfo info = new HlsInvoiceInterfaceInfo();
        info.setDocumentId(invoiceHd.getInvoiceHdId());
        info.setDocumentType("ACR_INVOICE");
        invoiceInterfaceInfoMapper.deleteByDocument(info);
        if (true == success) {
            JSONArray fpIssueResponses = result.getJSONArray("fpIssueResponses");
            JSONObject fpIssueItem = fpIssueResponses.getJSONObject(0);
            HlsCusAcrInvoiceHd acrInvoiceHd = new HlsCusAcrInvoiceHd();
            acrInvoiceHd.setInvoiceHdId(invoiceHd.getInvoiceHdId());
            acrInvoiceHd.setInvoiceCode(fpIssueItem.getString("invoiceCode"));
            acrInvoiceHd.setInvoiceNumber(fpIssueItem.getString("invoiceNo"));
            acrInvoiceHd.setInvoiceStatus("NEGATIVE");//已开具的负数发票
            acrInvoiceHdService.updateByPrimaryKeySelective(requestCtx, acrInvoiceHd);

            info.setBizId(fpIssueItem.getString("bizId"));
            info.setReqserialNo(fpIssueItem.getString("reqSerialNo"));
            info.setInvoiceCode(fpIssueItem.getString("invoiceCode"));
            info.setInvoiceNo(fpIssueItem.getString("invoiceNo"));
            info.setInvoiceDate(fpIssueItem.getString("invoiceDate"));
            info.setInvoiceCheckCode(fpIssueItem.getString("invoiceCheckCode"));
            info.setInvoiceQrCode(fpIssueItem.getString("invoiceQrCode"));
            info.setInvoiceKindCode(fpIssueItem.getString("invoiceKindCode"));
            info.setInvoiceStatus(fpIssueItem.getString("invoiceStatus"));
            info.setDzfpUrl(fpIssueItem.getString("dzfpUrl"));
            info.setInvoiceType("1");
            info.setResponseJson(result.toJSONString());
            info.setMessage(msg);
            info.setStatus("success");
            invoiceInterfaceInfoService.insertSelective(requestCtx, info);
        } else {
            HlsCusAcrInvoiceHd acrInvoiceHd = new HlsCusAcrInvoiceHd();
            acrInvoiceHd = acrInvoiceHdMapper.selectByPrimaryKey(invoiceHd.getInvoiceHdId());
            List<HlsCusAcrInvoiceHd> invoiceHdList =new ArrayList<>();
            invoiceHdList.add(acrInvoiceHd);
            try {
                hdService.delete(requestCtx,invoiceHdList);

                //原始发票还原
                HlsCusAcrInvoiceHd sourceInvoiceHd = new HlsCusAcrInvoiceHd();
                sourceInvoiceHd.setInvoiceHdId(invoiceHd.getSourceInvoiceHeaderId());
                sourceInvoiceHd.setReversedFlag(BaseConstants.NO);
                sourceInvoiceHd.setReverseDate(null);
                sourceInvoiceHd.setInvoiceStatus("IMPORTED");
                acrInvoiceHdMapper.updateByPrimaryKeySelective(sourceInvoiceHd);

                HlsCusAcrInvoiceLn condition = new HlsCusAcrInvoiceLn();
                condition.setInvoiceHdId(sourceInvoiceHd.getInvoiceHdId());
                List<HlsCusAcrInvoiceLn> lnList = acrInvoiceLnMapper.select(condition);
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
                        hdService.updateConContractCashflow(requestCtx, cashflow);
                    }
                }
            } catch (AcrInvoiceException e) {
                e.printStackTrace();
            }

            info.setResponseJson(result.toJSONString());
            info.setMessage(msg);
            info.setStatus("failure");
            invoiceInterfaceInfoService.insertSelective(requestCtx, info);
        }
    }

    private JSONObject reverseInvoiceInfo(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        init();
        JSONObject content = new JSONObject(new LinkedHashMap());

        invoiceHd = acrInvoiceHdMapper.queryAcrInvoiceHdDetailNew(invoiceHd).get(0);
        //原发票
        HlsCusAcrInvoiceHd invoiceHdOrig = acrInvoiceHdMapper.selectByPrimaryKey(invoiceHd.getSourceInvoiceHeaderId());

        content.put("reqSerialNo", invoiceHd.getInvoiceHdId());//开票序列号，一张发票唯一标识,税控服务器校验重复开票的依据
        content.put("terminalCode", terminalCode);//开票终端唯一性标识

        String invoiceKind = "";
        if ("1".equals(invoiceHd.getBillingWay())) {
            invoiceKind = eleInvoiceKindCode.get(invoiceHd.getInvoiceKind());
        } else {
            invoiceKind = invoiceKindCode.get(invoiceHd.getInvoiceKind());
        }
        content.put("invoiceKindCode", invoiceKind);//发票类型代码
        content.put("invoiceType", "1");//开票类型 0-蓝字发票、正数票开具，1-红字发票，负数票开具
        content.put("buyerTaxCode", invoiceHd.getBpTaxRegistryNum());//购货单位识别号
        content.put("buyerName", invoiceHd.getBpName());//购货单位名称
        content.put("buyerAddressTel", invoiceHd.getBpAddressPhoneNum());//购货单位地址电话
        content.put("buyerBankAccount", invoiceHd.getBpBankAccount());//购货单位银行帐号
        content.put("buyerEmail ", "");//购方邮箱
        content.put("buyerMobile", "");//购方手机号
        content.put("sellerTaxCode", sellerTaxCode);//销货单位识别号
        content.put("sellerName", sellerName);//销货单位名称
        content.put("sellerAddressTel", sellerAddressTel);//销货单位地址电话
        content.put("sellerBankAccount", sellerBankAccount);//销货单位银行帐号
        content.put("sellerContact", "");//销货方联系方式
        content.put("specialFlag", "00");// 特殊票种标识 “00”不是 “01”农产品销售 “02”农产品收购 “06”抵扣通行费 “07”其它通行费 “08”成品油销售 “12”机动车
        content.put("taxationMode", "0");//征税方式  0：普通征税  1：减按计征  2：差额征税
        content.put("minusAmount", 0.0);//差额征税扣除额 小数点后2位，当zsfs为2时扣除额为必填项。
        content.put("amount", invoiceHd.getNetAmount());//合计金额,小数点后2位，不含税
        content.put("taxAmount", invoiceHd.getTaxAmount());//计税额,小数点后2位
        content.put("totalAmount", invoiceHd.getTotalAmount());//价税合计
        content.put("payee", "");//收款人
        content.put("checker", "");//复核人
        content.put("drawer", invoiceHd.getCreatedByN());//开票人
        content.put("invoiceRemark", invoiceHd.getDescription());//备注
        content.put("originalInvoiceCode", invoiceHdOrig.getInvoiceCode());//原发票代码,红字发票时必须
        content.put("originalInvoiceNo", invoiceHdOrig.getInvoiceNumber());//原发票号码,红字发票时必须
        String invoiceKindOrg = "";
        if ("1".equals(invoiceHd.getBillingWay())) {
            invoiceKindOrg = eleInvoiceKindCode.get(invoiceHdOrig.getInvoiceKind());
        } else {
            invoiceKindOrg = invoiceKindCode.get(invoiceHdOrig.getInvoiceKind());
        }
        content.put("originalInvoiceKindCode", invoiceKindOrg);//原发票类型,红字发票时必须
        content.put("originalInvoiceDate", sdf.format(invoiceHdOrig.getInvoiceDate()));//原开票日期,红字发票时必须，yyyyMMdd格式
        content.put("sslkjly", "");//小规模、转登记纳税人2021年12月31日前3%税率开具发票理由
       /* 冲红原因代码,红字发票时必须
        1.销货退回；
        2.开票有误；
        3.服务中止；
        4.销售折让；*/
        content.put("chyydm", "2");//冲红原因代码

        content.put("billSource", null);//开票订单信息来源 0.在线开票订单 1.易开票订单 2.云开票订单 3.ERP开票订单 4.扫码开票订单
        content.put("billCode", null);//开票订单编号
        content.put("actType", null);//开票行为类型， NUll或者0.为立即开具，1.表示不立即开具，2.表示返回自助二维码，3.进开票前预处理后自动开票
        content.put("invoiceFormat", "");//卷式发票票样
        content.put("redInfoNo", "");//信息表编号 红票通知单
        content.put("expand1", "");//扩展字段

        JSONArray goodsList = new JSONArray();
        List<HlsCusAcrInvoiceLn> acrInvoiceLns = new ArrayList<>();
        HlsCusAcrInvoiceLn ln = new HlsCusAcrInvoiceLn();
        ln.setInvoiceHdId(invoiceHd.getInvoiceHdId());
        acrInvoiceLns = acrInvoiceLnService.select(requestCtx, ln, 1, 999);
        for (HlsCusAcrInvoiceLn item : acrInvoiceLns) {
            JSONObject goodItem = new JSONObject(new LinkedHashMap());
            goodItem.put("lineKind", "0");//发票行性质 0.正常行,默认值 1.折扣行 2.被折扣行 3.混合行，需开票接口处理折扣
            goodItem.put("goodsName", item.getProductName());//商品名称
            goodItem.put("taxKindCode", "3040502020200000000");//税收分类编码
            goodItem.put("goodsCode", "");//纳税人自行编码，如果税收分类编码为空，会根据该值查自该企业开票商
            goodItem.put("taxFlag", "0");//0 不含税   1 含税, 电票必须是不含税
            goodItem.put("qty", item.getQuantity());//商品数量
            goodItem.put("price", item.getTotalAmount());//单价
            goodItem.put("nonTaxPrice", item.getNetAmount());//不含税单价
            goodItem.put("amount", item.getNetAmount());//不含税金额
            goodItem.put("totalAmount", item.getTotalAmount());//含税金额
            goodItem.put("taxRate", item.getTaxTypeRate());//税率
            goodItem.put("taxAmount", item.getTaxAmount());//税额
            goodItem.put("dutyFree", "");//免税类型，空代表无 0.正常税率 1.免税和 2.不征增值税 3.普通零税率
            goodItem.put("couponFlag", "0");//是否使用优惠政策标识，0未使用，1使用
            goodItem.put("couponPolicy", "");//优惠政策名称，例如：免税,先征后退,简易征收,按5%简易征收等,如果couponFlag为1时，此项必填
            goodItem.put("outId", "");//外部流水ID，用于回传时业务系统确定那行发票商品
            goodItem.put("taxItem", "");//商品税目，保留字段，目前为空
            goodItem.put("specName", "");//规格型号
            goodItem.put("saleUnit", "");//单位,元/米，元/顿，元/件
            goodsList.add(goodItem);
        }
        content.put("goodsList", goodsList);
        return content;
    }

    /**
     * description
     * 已开发票查询 未启用
     */
    public void queryInvoiceItfc(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd, String queryKind) {
        JSONObject result = new JSONObject(new LinkedHashMap());
        JSONObject content = queryInvoiceInfo(requestCtx, invoiceHd, queryKind);
        //System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, queryInvoiceUrl, "已开发票查询");

        String msg = result.getString("message");
        String code = result.getString("code");

    }

    private JSONObject queryInvoiceInfo(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd, String queryKind) {
        init();
        JSONObject content = new JSONObject(new LinkedHashMap());

        invoiceHd = acrInvoiceHdService.selectByPrimaryKey(requestCtx, invoiceHd);
        HlsCusBpMaster master = new HlsCusBpMaster();
        master.setBpId(invoiceHd.getBpId());
        master = bpMasterService.selectByPrimaryKey(requestCtx, master);

        content.put("sellerTaxCode", sellerTaxCode);//销货单位识别号
        content.put("queryKind", queryKind);//查询方式 默认值2, 2：按输入请求流水号查询 0：按发票号码查询  3：按输入按开票申请bizId查询
        if ("2".equals(queryKind)) {
            content.put("reqSerialNo", invoiceHd.getInvoiceHdId());//开票请求流水号 queryKind为2时：必须
        }
        if ("0".equals(queryKind)) {
            content.put("invoiceCode", invoiceHd.getInvoiceCode());//发票代码 queryKind为0时：必须
            content.put("invoiceNo", invoiceHd.getInvoiceNumber());//发票号码 queryKind为0时：必须
        }
        if ("3".equals(queryKind)) {
            content.put("bizId", null);//业务ID  queryKind为3时：必须
        }
        content.put("fullInfo", "1");//返回票面信息  默认值0,0：返回发票主要信息1：返回全票面信息
        return content;
    }

    /**
     * description
     * 电子发票URL查询
     */
    public void queryInvoiceUrlItfc(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        init();
        JSONObject result = new JSONObject(new LinkedHashMap());
        List<HlsCusAcrInvoiceHd> invoiceHdList = acrInvoiceHdMapper.querydzfpUrl(invoiceHd);

        for (HlsCusAcrInvoiceHd item : invoiceHdList) {
            JSONObject content = new JSONObject(new LinkedHashMap());
            content.put("sellerTaxCode", sellerTaxCode);
            content.put("invoiceCode", item.getInvoiceCode());
            content.put("invoiceNo", item.getInvoiceNumber());
            result = interfacePlatformUtils.getInterfaceRequest(content, queryUrlUrl, "电子发票URL查询");

            String msg = result.getString("message");
            String code = result.getString("code");
            boolean success = result.getBoolean("success");
            if (true == success) {
                try {
                    downloadFileFromURL(requestCtx, result.getString("dzfpUrl"), item);

                    HlsCusAcrInvoiceHd acrInvoiceHd = new HlsCusAcrInvoiceHd();
                    acrInvoiceHd.setInvoiceHdId(item.getInvoiceHdId());
                    acrInvoiceHd.setDzfpUrl(result.getString("dzfpUrl"));
                    acrInvoiceHdService.updateByPrimaryKeySelective(requestCtx, acrInvoiceHd);

                    HlsInvoiceInterfaceInfo info = new HlsInvoiceInterfaceInfo();
                    info.setDocumentId(item.getInvoiceHdId());
                    info.setDocumentType("ACR_INVOICE");
                    info = invoiceInterfaceInfoService.select(requestCtx, info, 1, 999).get(0);
                    info.setDzfpUrl(result.getString("dzfpURL"));
                    invoiceInterfaceInfoService.updateByPrimaryKeySelective(requestCtx, info);
                } catch (OperationsException e) {
                    HlsCusAcrInvoiceHd acrInvoiceHd = new HlsCusAcrInvoiceHd();
                    acrInvoiceHd.setInvoiceHdId(item.getInvoiceHdId());
                    acrInvoiceHd.setDzfpUrl("");
                    acrInvoiceHdService.updateByPrimaryKeySelective(requestCtx, acrInvoiceHd);
                }
            }
        }
    }

    /**
     * 从URL下载文件
     *
     * @param url 下载文件的路径
     */
    public String downloadFileFromURL(IRequest requestCtx, String url, HlsCusAcrInvoiceHd hd) throws OperationsException {
        try {
            URL httpUrl = new URL(url);
            String fileName = hd.getDocumentNumber() + "_DZFP" + PDF;
            //System.out.println("-----------------------downloadFileFromURL-----" + downLoadPath.concat(fileName));
            File file = new File(downLoadPath.concat(fileName));
            file.createNewFile();
            //System.out.println("-----------------------createNewFile-----" + url);
            FileUtils.copyURLToFile(httpUrl, file, 30000, 30000);
            //System.out.println("-----------------------copyURLToFile-----");
            Long size = file.length();

            FndAttachment conDocFile = new FndAttachment();
            conDocFile.setFileName(fileName);
            FndAttachmentMulti conDocFileMulti = new FndAttachmentMulti();
            conDocFileMulti.setTableName("ACR_INVOICE_HD");
            conDocFileMulti.setTablePkValue(hd.getInvoiceHdId().toString());
            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            conDocFileMulti.setCreatedBy(requestCtx.getUserId());
            conDocFileMulti.setCreationDate(new Date());
            conDocFileMulti.setLastUpdateDate(new Date());
            conDocFileMulti.setLastUpdatedBy(requestCtx.getUserId());
            fndAttachmentMultiService.insertSelective(requestCtx, conDocFileMulti);

            conDocFile.setSourceTypeCode(FND_ATM_ATTACHMENT_MULTI);
            conDocFile.setSourcePkValue(conDocFileMulti.getRecordId().toString());
            conDocFile.setFilePath(file.getPath());
            conDocFile.setFileTypeCode(PDF);
            conDocFile.setFileSize(size);
            conDocFile.setCreationDate(new Date());
            conDocFile.setCreatedBy(requestCtx.getUserId());
            conDocFile.setLastUpdateDate(new Date());
            conDocFile.setLastUpdatedBy(requestCtx.getUserId());
            conDocFile = fndAttachmentService.insertSelective(requestCtx, conDocFile);

            conDocFileMulti.setAttachmentId(conDocFile.getAttachmentId());
            fndAttachmentMultiService.updateByPrimaryKey(requestCtx, conDocFileMulti);

            return file.getPath();
        } catch (Exception e) {
            e.printStackTrace();
            throw new OperationsException("电子发票下载失败");
        }
    }

    /**
     * description
     * 已开发票作废(电票使用发票开具红冲 ; 纸票当月可作废，跨越只能红冲)
     */
    public void cancelInvoiceItfc(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        JSONObject result = new JSONObject(new LinkedHashMap());
        invoiceHd = acrInvoiceHdService.selectByPrimaryKey(requestCtx, invoiceHd);
        JSONObject content = cancelInvoiceInfo(requestCtx, invoiceHd);
        //System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, cancelInvoiceUrl, "已开发票作废");

        String msg = result.getString("message");
        String code = result.getString("code");
        boolean success = result.getBoolean("success");
        if (true == success) {
            invoiceHd.setReversedFlag(BaseConstants.YES);
            invoiceHd.setReverseDate(new Date());
            invoiceHd.setInvoiceStatus("CANCEL");
            acrInvoiceHdMapper.updateByPrimaryKeySelective(invoiceHd);
        } else {
            invoiceHd.setReversedFlag(BaseConstants.NO);
            invoiceHd.setReverseDate(null);
            invoiceHd.setInvoiceStatus("IMPORTED");
            acrInvoiceHdMapper.updateByPrimaryKeySelective(invoiceHd);
        }
    }

    private JSONObject cancelInvoiceInfo(IRequest requestCtx, HlsCusAcrInvoiceHd invoiceHd) {
        init();
        JSONObject content = new JSONObject(new LinkedHashMap());

        HlsCusBpMaster master = new HlsCusBpMaster();
        master.setBpId(invoiceHd.getBpId());
        master = bpMasterService.selectByPrimaryKey(requestCtx, master);

        content.put("sellerTaxCode", sellerTaxCode);//销货单位识别号
        content.put("terminalCode", terminalCode);//
        String invoiceKind = "";
        if ("1".equals(invoiceHd.getBillingWay())) {
            invoiceKind = eleInvoiceKindCode.get(invoiceHd.getInvoiceKind());
        } else {
            invoiceKind = invoiceKindCode.get(invoiceHd.getInvoiceKind());
        }
        content.put("invoiceKindCode", invoiceKind);//发票类型代码
        content.put("cancelType", "1");//作废类型 0：空白票作废  1：已开票作废
        content.put("invoiceCode", invoiceHd.getInvoiceCode());//发票代码
        content.put("invoiceNo", invoiceHd.getInvoiceNumber());//发票号码
        content.put("cancelUserName", requestCtx.getUserName());//作废人
        return content;
    }
}
