package com.hand.hls.interfacePlatform.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentMultiService;
import com.hand.hls.bp.dto.HlsCusBpMaster;
import com.hand.hls.bp.dto.HlsCusBpMasterBankAccount;
import com.hand.hls.bp.mapper.HlsCusBpMasterBankAccountMapper;
import com.hand.hls.bp.mapper.HlsCusBpMasterMapper;
import com.hand.hls.bp.service.HlsCusBpMasterService;
import com.hand.hls.common.dto.HlsCusItfcBankFlow;
import com.hand.hls.common.service.HlsCusItfcBankFlowService;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.csh.dto.*;
import com.hand.hls.csh.mapper.*;
import com.hand.hls.csh.service.*;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fct.dto.HlsCusHlsCreditLineChance;
import com.hand.hls.gld.dto.HlsCusJeHead;
import com.hand.hls.gld.service.HlsCusConContractService;
import com.hand.hls.prj.dto.HlsBpMaster;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.dto.HlsCusPrjQuotation;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.HlsCusPrjProjectService;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.HlsCusMathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhangdan
 * @date 2022/9
 */
@Component
public class FinanceBaseUtils {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;

    @Autowired
    private InterfacePlatformUtils interfacePlatformUtils;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private ICshPaymentReqHdService cshPaymentReqHdService;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    private CshPaymentAttachmentMapper cshPaymentAttachmentMapper;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;
    @Autowired
    private HlsCusPrjQuotationMapper hlsCusPrjQuotationMapper;
    @Autowired
    private HlsCusItfcBankFlowService hlsItfcBankFlowService;
    @Autowired
    private CshTransactionService transactionService;
    @Autowired
    private HlsCusCshBankAccountService cshBankAccountService;
    @Autowired
    private HlsCusBpMasterBankAccountMapper bpBankAccountMapper;
    @Autowired
    private CshWriteOffService cshWriteOffService;
    @Autowired
    private CshPaymentReqLnBankAccountService paymentReqLnBankAccountService;
    @Autowired
    private CshPaymentReqLnBankAccountMapper cshPaymentReqLnBankAccountMapper;
    @Autowired
    private HlsCusCshTransactionMapper cshTransactionMapper;
    @Autowired
    private HlsCusConContractCashflowMapper cashflowMapper;
    @Autowired
    private HlsCusConContractMapper conContractMapper;
    @Autowired
    private HlsCusConContractService conContractService;
    @Autowired
    private HlsCusBpMasterMapper bpMasterMapper;
    @Autowired
    HlsCusCshWriteOffMapper cshWriteOffMapper;

    private static String appId;
    private static String compCode;
    private static String DC51_USER;
    private static String DC51_USER_PWD;
    //YH006 流水查询
    private static String queryFlowUrl;
    //DC51 应付报账
    private static String DC51ASendUrl;
    //DC41 收款数据报账
    private static String DC41ASendUrl;
    //CW002 计划项目上报
    private static String createPorjectPlanUrl;
    //YH007 流水补录 AL001 创建已投放的项目，收到了投放流水 AL002 核销后匹配流水
    private static String flowMatchUrl;
    //YH009 流水合并接口
    private static String mergeFlowUrl;
    //共享系统查询报表
    private static String shareVendorQueryUrl;

    //数据所属部门代码
    private static String unitCode = "CC970000";
    //通用推送人员工号
    private static String employeeCode = "11800098";
    //推送人员OA系统账号
    //生产 GDH_LBS_VIR_USER 测试 GDH_FAS_VIR_USER
    private static String postOAEmployeeCode = "GDH_FAS_VIR_USER";

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdfYM = new SimpleDateFormat("yyyy-MM");

    static Map<String, String> projectType = new HashMap<String, String>(200) {

    };

    /* 项目类别 经营性租赁-26 直租-27 售后回租-28 转租赁-29*/
    static {
        projectType.put("GENERAL_OPERATING_LEASE", "26");
        projectType.put("LEASE", "27");
        projectType.put("LEASEBACK", "28");
        projectType.put("SUBLEASE", "29");
        projectType.put("WITHIN", "23");
        projectType.put("OUTSIDE", "24");
    }

    private void init() {
        appId = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "appId");
        compCode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "compCode");
        DC51_USER = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "DC51_USER");
        DC51_USER_PWD = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "DC51_USER_PWD");
        queryFlowUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "queryFlowUrl");
        DC51ASendUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "DC51ASendUrl");
        DC41ASendUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "DC41ASendUrl");
        createPorjectPlanUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "createPorjectPlanUrl");
        flowMatchUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "flowMatchUrl");
        shareVendorQueryUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "shareVendorQueryUrl");
        mergeFlowUrl = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("FINANCE_INTERFACE", "mergeFlowUrl");
    }

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
     * 共享查询报表 供应商信息查询 MD23
     */
    public void shareVendorQueryItfc(IRequest requestCtx, HlsCusBpMaster bpMaster) {
        init();
        List<HlsCusBpMaster> list = bpMasterMapper.selectEbsCodeBp(bpMaster);
        for (HlsCusBpMaster item : list) {
            JSONObject result = new JSONObject(new LinkedHashMap());
            JSONObject content = queryVendorContentInfo(requestCtx, item);
            result = interfacePlatformUtils.getInterfaceRequest(content, shareVendorQueryUrl, "共享系统供应商查询");

            String msg = result.getString("msg");
            String code = result.getString("code");
            JSONArray data = result.getJSONArray("data");

            HlsCusBpMasterBankAccount cusBpMasterBankAccount = new HlsCusBpMasterBankAccount();
            cusBpMasterBankAccount.setBpId(item.getBpId());
            cusBpMasterBankAccount.setBankAccountNum(item.getBankAccountNum());

            if (HlsCusConstant.DC51_RETURN_CODE.OK_VAL.equals(code)) {
                for (int i = 0; i < data.size(); i++) {
                    JSONObject newObject = (JSONObject) data.get(i);
                    String ebsCode = String.valueOf(newObject.get("v19"));
                    if (!("EBS往来段".equals(ebsCode))) {
                        cusBpMasterBankAccount.setEbsBusinessCode(ebsCode);
                        bpBankAccountMapper.updateEbsCodeforBp(cusBpMasterBankAccount);
                    }
                }
            }
        }
    }

    private JSONObject queryVendorContentInfo(IRequest requestCtx, HlsCusBpMaster bpMaster) {
        JSONObject content = new JSONObject(new LinkedHashMap());

        //基本信息
        JSONObject header = new JSONObject(new LinkedHashMap());
        //查询报表
        header.put("rec_type", "RP11");
        header.put("p1", 1);
        header.put("p2", DC51_USER);
        header.put("p3", DC51_USER_PWD);
        header.put("p4", compCode);

        JSONArray lines = new JSONArray();
        JSONObject line1 = new JSONObject(new LinkedHashMap());
        HlsCusBpMaster hlsCusBpMaster = new HlsCusBpMaster();
        hlsCusBpMaster.setBpId(bpMaster.getBpId());
        hlsCusBpMaster = bpMasterMapper.selectByPrimaryKey(hlsCusBpMaster);

        line1.put("rec_type", "RP11A");
        line1.put("p1", 1);//序号
        line1.put("p2", "MD23");//供应商查询
        line1.put("p3", compCode);//公司
        line1.put("p4", hlsCusBpMaster.getRegisterCertNum() + "");//供应商信用代码
        line1.put("p5", hlsCusBpMaster.getBpName() + "");//供应商名称
        line1.put("p6", bpMaster.getBankAccountNum() + "");//银行账号
        lines.add(line1);

        content.put("head", header);
        content.put("line", lines);
        return content;
    }


    /**
     * description
     * CW002 项目立项的项目信息通过计划项目新增接口传到财务中台
     */
    public void createProjectPlanItfc(IRequest requestCtx, Long projectId) {
        init();
        JSONObject result = new JSONObject(new LinkedHashMap());
        JSONObject content = createProjectInfo(projectId);
        //System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, createPorjectPlanUrl, "CW002计划项目新增");

        String msg = result.getString("msg");
        String code = result.getString("code");
        //成功返回接口方项目ID，不成功返回NULL
        String data = result.getString("data");

        HlsCusPrjProject prject = new HlsCusPrjProject();
        prject.setProjectId(projectId);

        if (HlsCusConstant.YH007_RETURN_CODE.success.equals(code)) {
            prject.setPostItfcFlag("Y");
            prject.setPostItfcCode(code);
            prject.setPostItfcMsg(msg);
            prject.setItfcDocument(data);
            prject.setPostApprovedDate(new Date());
        } else {
            prject.setPostItfcFlag("N");
            prject.setPostItfcCode(code);
            prject.setPostItfcMsg(msg);
        }
        hlsCusPrjProjectService.updateByPrimaryKeySelective(requestCtx, prject);
    }

    private JSONObject createProjectInfo(Long projectId) {
        JSONObject content = new JSONObject(new LinkedHashMap());
        JSONObject data = new JSONObject(new LinkedHashMap());

        HlsCusPrjProject prjProject = new HlsCusPrjProject();
        prjProject.setProjectId(projectId);
        List<Map> prjProjects = hlsCusPrjProjectMapper.queryPrjDetailSecond(prjProject);
        HlsCusPrjQuotation quotation = new HlsCusPrjQuotation();
        quotation = hlsCusPrjQuotationMapper.selectQuotationByProjectId(prjProject).get(0);

        String startDateStr = (String) prjProjects.get(0).get("createDateStr");
        Date date = null;
        String endDateStr = "";
        if (null == quotation.getLeaseStartDate()) {
            try {
                date = sdf.parse(startDateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.YEAR, 1);
                endDateStr = sdf.format(calendar.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            startDateStr = sdf.format(quotation.getLeaseStartDate());
            endDateStr = sdf.format(quotation.getLeaseEndDate());
        }

        data.put("bizProjId", projectId);//项目ID唯一标识
        data.put("projCode", prjProjects.get(0).get("projectNumber"));//项目编号
        data.put("compCode", compCode);//公司编码
        data.put("projName", prjProjects.get(0).get("projectName"));//项目名称
        data.put("projType", projectType.get(prjProjects.get(0).get("businessType")));//项目类别
        data.put("putinDay", dealValue(startDateStr));//投放日
        data.put("creditorUser", prjProjects.get(0).get("tenantIdN"));//债权人
        data.put("debtorUser", prjProjects.get(0).get("tenantIdN"));//债务人
        data.put("expectDay", dealValue(endDateStr));//预期到期日
        data.put("investAmt", dealValue(dealDoubleAmount(Double.valueOf(prjProjects.get(0).get("financeAmount").toString()))));//投放金额
        data.put("investRate", dealValue(prjProjects.get(0).get("chanceIrr")));//预期收益率
        data.put("lrrRate", dealValue(prjProjects.get(0).get("chanceIrr")));//iRR收益率
        data.put("cashAmt", dealValue(prjProjects.get(0).get("deposit")));//保证金
        data.put("firstAmt", dealValue(prjProjects.get(0).get("downPayment")));//首期租金
        data.put("cashRate", dealValue(prjProjects.get(0).get("depositRatio")));//保证金比率
        data.put("firstRate", dealValue(prjProjects.get(0).get("downPaymentRatio")));//首期费率
        data.put("intervestDate", "");//计息日
        data.put("fisrtPayDate", "");//首个信托支付日
        data.put("channelRate", "");//通道费率
        data.put("releaseRate", "");//发行利率
        data.put("remark", "");//备注

        content.put("data", data);
        content.put("appId", appId);
        content.put("prnd", System.currentTimeMillis() + "");
        return content;
    }

    /**
     * description
     * DC51 应付报账
     */
    public void postPaymentItfc(IRequest iRequest, Long paymentId) {
        init();
        JSONObject result = new JSONObject(new LinkedHashMap());

        JSONObject content = getPaymentInfo(iRequest, paymentId);
        System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, DC51ASendUrl, "DC51应付报账");

        String msg = result.getString("msg");
        String code = result.getString("code");
        JSONArray data = result.getJSONArray("data");

        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentId);

        if (HlsCusConstant.DC51_RETURN_CODE.OK_IMPORT.equals(code)) {
            for (int i = 0; i < data.size(); i++) {
                JSONObject newObject = (JSONObject) data.get(i);
                String document = String.valueOf(newObject.get("v5"));
                Long bankAccountId = Long.valueOf(newObject.get("v2").toString());
                CshPaymentReqLnBankAccount payAccount = new CshPaymentReqLnBankAccount();
                payAccount.setCshBankId(bankAccountId);
                payAccount.setApyNumber(document);
                paymentReqLnBankAccountService.updateByPrimaryKeySelective(iRequest, payAccount);
            }
            cshPaymentReqHd.setPostItfcFlag("Y");
            cshPaymentReqHd.setPostItfcCode(code);
            cshPaymentReqHd.setPostItfcMsg(msg);
            //cshPaymentReqHd.setItfcDocument(document);
            cshPaymentReqHd.setPostApprovedDate(new Date());
        } else {
            cshPaymentReqHd.setPostItfcFlag("N");
            cshPaymentReqHd.setPostItfcCode(code);
            cshPaymentReqHd.setPostItfcMsg(msg + data.toString());
        }
        cshPaymentReqHdService.updateByPrimaryKeySelective(iRequest, cshPaymentReqHd);
    }

    private JSONObject getPaymentInfo(IRequest iRequest, Long paymentId) {
        HlsCusCshPaymentReqHd cshPaymentReqHd = new HlsCusCshPaymentReqHd();
        cshPaymentReqHd.setPaymentReqId(paymentId);
        List<HlsCusCshPaymentReqHd> hlsCusCshPaymentReqHds = hlsCusCshPaymentReqHdMapper.conContractCshReqDetail(cshPaymentReqHd);
        cshPaymentReqHd = hlsCusCshPaymentReqHds.get(0);

        HlsCusCshPaymentReqLn paymentReqLn = new HlsCusCshPaymentReqLn();
        paymentReqLn.setPaymentReqId(paymentId);
        paymentReqLn = hlsCusCshPaymentReqLnMapper.select(paymentReqLn).get(0);

        List<CshPaymentReqLnBankAccount> paymentReqLnBankAccountList = new ArrayList<>();
        CshPaymentReqLnBankAccount payAccount = new CshPaymentReqLnBankAccount();
        payAccount.setPaymentReqLnId(paymentReqLn.getPaymentReqLnId());
        paymentReqLnBankAccountList = paymentReqLnBankAccountService.selectSelective(iRequest, payAccount);

        HlsCusPrjProject project = new HlsCusPrjProject();
        project.setProjectId(cshPaymentReqHd.getProjectId());
        List<Map> projectListMap = hlsCusPrjProjectMapper.queryPrjDetailNew(project);

        HlsCusBpMaster tenant = bpMasterMapper.selectByPrimaryKey(cshPaymentReqHd.getBpId());
        String group_customers = tenant.getGroupCustomers();

        int sequence = 0;

        JSONObject content = new JSONObject(new LinkedHashMap());
        //基本信息
        JSONObject header = new JSONObject(new LinkedHashMap());
        //DC51 应付报账
        header.put("rec_type", "DC51");
        //导入ID
        header.put("p1", paymentId);
        //用户名
        header.put("p2", DC51_USER);
        //密码
        header.put("p3", DC51_USER_PWD);
        //公司
        header.put("p4", compCode);
        JSONArray lines = new JSONArray();

        for (CshPaymentReqLnBankAccount actualPayAccount : paymentReqLnBankAccountList) {
            sequence = sequence + 1;
            JSONObject line1 = new JSONObject(new LinkedHashMap());

            HlsCusBpMaster bpMaster = new HlsCusBpMaster();
            bpMaster = bpMasterMapper.selectByPrimaryKey(paymentReqLn.getBpId());

            BigDecimal amount = dealDoubleAmount(actualPayAccount.getPaymentAmount());
            line1.put("rec_type", "DC51A");//行类型 DC51A 应付报账单头信息
            line1.put("p1", cshPaymentReqHd.getContractNumber() + "-" + sequence);//序号
            line1.put("p2", actualPayAccount.getCshBankId());//头ID
            line1.put("p3", "");//备用
            line1.put("p4", sequence);//行号
            line1.put("p5", compCode);//核算主体 应付报账数据所属公司代码
            line1.put("p6", unitCode);//应付报账数据所属部门代码
            line1.put("p7", employeeCode);//应付报账通用推送人员工号
            line1.put("p8", postOAEmployeeCode);//应付报账通用推送人员OA系统账号
            line1.put("p9", amount);//总金额
            line1.put("p10", "VENDER");//收款对象，区分供应商与个人，供应商为“VENDER”，个人为“EMPLOYEE”
            line1.put("p11", bpMaster.getRegisterCertNum());//收款方 "914403007892348906"
            line1.put("p12", actualPayAccount.getCshBankAccountNum());//付款银行账户
            line1.put("p13", "CNY");//应付报账数据记录币种
            line1.put("p14", "CORPORATE");//汇率类型  默认类型为：CORPORATE
            line1.put("p15", "1");//汇率值  币种为人民币，则默认为1

            String p16;
            if ("REFUND_DEPOSIT".equals(cshPaymentReqHd.getPaymentType())) {
                p16 = cshPaymentReqHd.getContractName() + "保证金";
            } else {
                p16 = cshPaymentReqHd.getContractName() + "设备款";
            }
            line1.put("p16", p16);//请款概述
            line1.put("p17", "无");//汇款银行附言
            line1.put("p18", amount);//实际支付金额,如无需对外支付则传0
            line1.put("p19", cshPaymentReqHd.getAttachmentCount());//附件张数
            line1.put("p20", "GDHlecse");//来源系统
            line1.put("p21", "N");//预算内 Y/N
            line1.put("p22", "APY_05");//单据类型
            line1.put("p23", "N");//是否自审批
            line1.put("p24", "");//请款类型
            line1.put("p25", "");//关联建
            line1.put("p26", "");//属性
            line1.put("p27", "N");//紧急标识 Y/N
            lines.add(line1);

            JSONObject line2 = new JSONObject(new LinkedHashMap());
            line2.put("rec_type", "DC51B");
            sequence = sequence + 1;
            line2.put("p1", sequence);
            line2.put("p2", actualPayAccount.getCshBankId());//头ID
            line2.put("p3", "");//备用
            line2.put("p4", "1");//行号

            String p5;
            if ("REFUND_DEPOSIT".equals(cshPaymentReqHd.getPaymentType())) {
                p5 = "支付" + bpMaster.getBpName() + "保证金";
            } else {
                p5 = "支付" + bpMaster.getBpName() + "设备款";
            }
            line2.put("p5", p5);//描述
            line2.put("p6", "EPT008311");//报销类型

            //支付设备款	EXP00831101	 所有
            //退保证金	EXP00831102	 外部
            //退保证金	EXP00831103	 内部
            String p7;
            if ("REFUND_DEPOSIT".equals(cshPaymentReqHd.getPaymentType())) {
                if ("Y".equals(group_customers)) {
                    p7 = "EXP00831103";
                } else {
                    p7 = "EXP00831102";
                }
            } else {
                p7 = "EXP00831101";
            }
            line2.put("p7", p7);//费用项目
            line2.put("p8", "CNY");//业务币种
            line2.put("p9", amount);//业务单价
            line2.put("p10", "1");//数量
            line2.put("p11", unitCode);//部门

            line2.put("p12", "0");//核算项目段，如无则传0
            String p13;
            if ("PAYMENT".equals(cshPaymentReqHd.getPaymentType())) {
                p13 = "CC9718010001";
            } else {
                p13 = "0";
            }
            line2.put("p13", p13);//子科目段，如无则传0
            line2.put("p14", "01");//发票种类

            String vatRrate;
            Double vatAmount;
            if (projectType.get("LEASE").equals(projectListMap.get(0).get("businessType"))) {
                vatRrate = "VAT INPUT 13";
                vatAmount = HlsCusMathUtil.mul(0.13D, Double.valueOf(amount.toString()));
            } else {
                vatRrate = "VAT INPUT 6";
                vatAmount = HlsCusMathUtil.mul(0.06D, Double.valueOf(amount.toString()));
            }
            line2.put("p15", vatRrate);//税率
            line2.put("p16", vatAmount);//税额
            line2.put("p17", "0");//车牌号，如无则传0

            lines.add(line2);

            //系统目前没有差额放款的情形
            /*JSONObject line3 = new JSONObject(new LinkedHashMap());
            line3.put("rec_type", "DC51C");
            sequence = sequence + 1;
            line3.put("p1", sequence);
            line3.put("p2", actualPayAccount.getCshBankId());//头ID =DC51的p1
            line3.put("p3", "");//备用
            line3.put("p4", "1");//行号
            line3.put("p5", "PERSONAL_INCOME_TAX");//扣款类型
            line3.put("p6", "10000");//扣款金额
            lines.add(line3);*/
            CshPaymentAttachment cshPaymentAttachment = new CshPaymentAttachment();
            cshPaymentAttachment.setPaymentReqId(paymentId);
            List<CshPaymentAttachment> cshPaymentAttachments = cshPaymentAttachmentMapper.queryPaymentAttaMutliInfo(cshPaymentAttachment);
            for (CshPaymentAttachment paymentAttachment : cshPaymentAttachments) {
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = requestAttributes.getRequest();
                String localAddr = request.getLocalAddr();
                int serverPort = request.getServerPort();

                JSONObject line4 = new JSONObject(new LinkedHashMap());
                line4.put("rec_type", "DC51D");
                sequence = sequence + 1;
                line4.put("p1", sequence);
                line4.put("p2", actualPayAccount.getCshBankId());//头ID
                line4.put("p3", "");//备用
                line4.put("p4", paymentAttachment.getFileName());//附件名称
                //附扫描件附件url
                line4.put("p5", request.getScheme() + "://" + localAddr + ":" + serverPort + request.getContextPath() + "/wx/api/fnd/attachment/download?attachment_id=" + paymentAttachment.getAttachmentId());
                lines.add(line4);
            }

            //DC51F 如果传，合同号必须存在
            if (!(null == projectListMap.get(0).get("contractNum"))) {
                JSONObject line5 = new JSONObject(new LinkedHashMap());
                line5.put("rec_type", "DC51F");
                sequence = sequence + 1;
                line5.put("p1", sequence);
                line5.put("p2", actualPayAccount.getCshBankId());//头ID
                line5.put("p3", "");//备用
                line5.put("p4", "1");//行号
                line5.put("p5", projectListMap.get(0).get("contractNum"));//合同号 法务系统合同号
                line5.put("p6", amount);//本次申请付款金额
                lines.add(line5);
            }
        }

        content.put("head", header);
        content.put("line", lines);
        return content;
    }

    /**
     * description
     * YH006 获取流水
     */
    public void queryFlowItfc(IRequest iRequest, List<String> ioCodeList, String queryDateStr) throws Exception {
        init();
        //AL001 项目投放 AL002 项目回款 AL003 银行借款 AL004 银行还本付息(003\004融资业务不对接)
        JSONObject result = new JSONObject(new LinkedHashMap());

        String startDateStr;
        String endDateStr;
        if (null == queryDateStr || "" == queryDateStr) {
            startDateStr = sdf.format(new Date());
            endDateStr = sdf2.format(new Date());
        } else {
            startDateStr = queryDateStr;
            Date endDate = sdf.parse(startDateStr);
            endDateStr = sdf.format(new Date(endDate.getTime() + 3 * 24 * 60 * 60 * 1000));
        }
        JSONObject content = getFlowInfo(ioCodeList, startDateStr, endDateStr);
        result = interfacePlatformUtils.getInterfaceRequest(content, queryFlowUrl, "流水查询");

        String msg = result.getString("msg");
        String code = result.getString("code");
        JSONArray data = result.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject newObject = (JSONObject) data.get(i);
            HlsCusItfcBankFlow bankFlow = newObject.toJavaObject(HlsCusItfcBankFlow.class);
            List<HlsCusItfcBankFlow> bankFlowAllList = hlsItfcBankFlowService.selectAll(iRequest);

            List<HlsCusItfcBankFlow> filterList = (List) bankFlowAllList.stream().filter((o) -> {
                return bankFlow.getFlowId().indexOf(o.getFlowId()) >= 0;
            }).collect(Collectors.toList());

            if (filterList.size() <= 0) {
                bankFlow.setDealStatus("N");
                hlsItfcBankFlowService.insertSelective(iRequest, bankFlow);
            }
        }
        dealFlowItfc(iRequest, null);
    }

    /**
     * description
     * 流水处理
     */
    public void dealFlowItfc(IRequest iRequest, HlsCusItfcBankFlow bankFlowQuery) throws Exception {
        HlsCusItfcBankFlow bankFlow = new HlsCusItfcBankFlow();
        bankFlow.setDealStatus("N");
        if (bankFlowQuery != null) {
            bankFlow.setFlowId(bankFlowQuery.getFlowId());
        }
        List<HlsCusItfcBankFlow> bankFlowList = hlsItfcBankFlowService.select(iRequest, bankFlow, 1, 999);

        for (int i = 0; i < bankFlowList.size(); i++) {
            HlsCusItfcBankFlow dealbankFlow = bankFlowList.get(i);
            HlsCusCshBankAccount bankAccount = new HlsCusCshBankAccount();
            if (dealbankFlow.getOwnAccountNo() == null) {
                dealbankFlow.setDealMsg("本方银行为空，请核对！");
                hlsItfcBankFlowService.updateByPrimaryKeySelective(iRequest, dealbankFlow);
                continue;
            }
            bankAccount.setBankAccountNum(dealbankFlow.getOwnAccountNo());
            List<HlsCusCshBankAccount> bankAccountList = new ArrayList<>();
            bankAccountList = cshBankAccountService.selectSelective(iRequest, bankAccount);
            if (bankAccountList.size() > 0) {
                bankAccount = bankAccountList.get(0);
                dealbankFlow.setOwnAccountId(bankAccountList.get(0).getBankAccountId());
                dealbankFlow.setDealMsg("");
            } else {
                dealbankFlow.setDealMsg("本方银行" + dealbankFlow.getOwnAccountNo() + "不存在，请核对！");
                hlsItfcBankFlowService.updateByPrimaryKeySelective(iRequest, dealbankFlow);
                continue;
            }

            HlsCusBpMasterBankAccount bpbankAccount = new HlsCusBpMasterBankAccount();
            bpbankAccount.setBankAccountNum(dealbankFlow.getRivalAccountNo());
            List<HlsCusBpMasterBankAccount> bpBankAccountList = bpBankAccountMapper.select(bpbankAccount);
            if (dealbankFlow.getRivalAccountNo() == null || dealbankFlow.getRivalAccountNo() == "") {
                bpbankAccount.setBankAccountNum("9999999999");//虚拟账号
                bpbankAccount = bpBankAccountMapper.select(bpbankAccount).get(0);
                dealbankFlow.setRivalAccountId(bpbankAccount.getBankAccountId());
                dealbankFlow.setDealMsg("");
            } else if (bpBankAccountList.size() > 0) {
                bpbankAccount = bpBankAccountList.get(0);
                dealbankFlow.setRivalAccountId(bpbankAccount.getBankAccountId());
                dealbankFlow.setDealMsg("");
            } else {
                dealbankFlow.setDealMsg("对方银行" + dealbankFlow.getRivalAccountNo() + "不存在，请核对！");
                hlsItfcBankFlowService.updateByPrimaryKeySelective(iRequest, dealbankFlow);
                continue;
            }

            //AL001 项目投放 AL002 项目回款 AL003 银行借款 AL004 银行还本付息(003\004融资业务不对接)
            if ("AL001".equals(dealbankFlow.getIoCode())) {
                dealbankFlow.setDealStatus("Y");
                dealbankFlow.setDealDate(new Date());
            }
            if ("AL002".equals(dealbankFlow.getIoCode())) {
                List<HlsCusCshTransaction> list = new ArrayList<>();
                HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
                hlsCusCshTransaction.setFlowNo(dealbankFlow.getFlowId());
                hlsCusCshTransaction.setPkEbankDzd(dealbankFlow.getPkEbankDzd());
                hlsCusCshTransaction.setTransactionDate(dealbankFlow.getFlowTime());
                hlsCusCshTransaction.setTransactionAmount(dealbankFlow.getTransAmount());
                hlsCusCshTransaction.setBankAccountId(bankAccount.getBankAccountId());
                hlsCusCshTransaction.setCurrencyCode(dealbankFlow.getCurrencyType());
                hlsCusCshTransaction.setBankSlipNum(dealbankFlow.getTradeNo());
                hlsCusCshTransaction.setDescription(dealbankFlow.getRemark());
                hlsCusCshTransaction.setComments(dealbankFlow.getPurpose());
                hlsCusCshTransaction.setBpId(bpbankAccount.getBpId());
                hlsCusCshTransaction.setBpName(bpbankAccount.getBpName());
                hlsCusCshTransaction.setBpBankAccountId(bpbankAccount.getBankAccountId());
                hlsCusCshTransaction.setBpBankAccountName(bpbankAccount.getBankAccountName());
                hlsCusCshTransaction.setBpBankAccountNum(bpbankAccount.getBankAccountNum());
                hlsCusCshTransaction.setBpBankName(bpbankAccount.getBankFullName());
                hlsCusCshTransaction.setBpBankBranchName(bpbankAccount.getBankBranchName());
                hlsCusCshTransaction.setCompanyId(bankAccount.getCompanyId());
                hlsCusCshTransaction.setPaymentMethod("TT");
                hlsCusCshTransaction.setPostedFlag("Y");
                hlsCusCshTransaction.setImportFlag("Y");
                hlsCusCshTransaction.setImportDate(new Date());

                hlsCusCshTransaction = transactionService.createCshTransaction(iRequest, hlsCusCshTransaction);
                dealbankFlow.setDealStatus("Y");
                dealbankFlow.setDealDate(new Date());
                dealbankFlow.setDocumentTable("CSH_TRANSACTION");
                dealbankFlow.setDocumentNumber(hlsCusCshTransaction.getTransactionId());
            }
            hlsItfcBankFlowService.updateByPrimaryKeySelective(iRequest, dealbankFlow);
        }
    }

    private JSONObject getFlowInfo(List<String> ioCodeList, String startDate, String endDate) {
        JSONObject content = new JSONObject(new LinkedHashMap());
        JSONObject data = new JSONObject(new LinkedHashMap());

        data.put("companyCode", compCode);
        data.put("ioCodeList", ioCodeList);
        data.put("startDate", startDate);
        data.put("endDate", endDate);

        content.put("appId", appId);
        content.put("prnd", System.currentTimeMillis() + "");
        content.put("data", data);
        return content;
    }

    /**
     * description
     * YH007 AL001 创建已投放的项目 我们这里传合同支付表
     */
    public void paymentFlowItfc(IRequest iRequest, Long contractId) throws Exception {

        List<CshPaymentReqLnBankAccount> paymentReqLnBankAccountLists = cshPaymentReqLnBankAccountMapper.queryContractCshBank(contractId);
        List<CshPaymentReqLnBankAccount> filterList = paymentReqLnBankAccountLists.stream().filter(
                item -> null == item.getFlowNo() || item.getFlowNo().isEmpty()
        ).collect(Collectors.toList());

        if (filterList.size() <= 0) {
            init();
            JSONObject result = new JSONObject(new LinkedHashMap());
            JSONObject content = createContractInfo(iRequest, contractId);
            System.out.println(content);
            result = interfacePlatformUtils.getInterfaceRequest(content, flowMatchUrl, "创建已投放的项目");

            String msg = result.getString("msg");
            String code = result.getString("code");
            String data = result.getString("data");

            HlsCusConContract contract = new HlsCusConContract();
            contract = conContractMapper.selectByPrimaryKey(contractId);
            HlsCusPrjProject project = new HlsCusPrjProject();
            project.setProjectId(contract.getProjectId());

            if (HlsCusConstant.YH007_RETURN_CODE.success.equals(code)) {
                project.setPostItfcFlag("Y");
                project.setItfcDocument(data);
                project.setPostItfcCode(code);
                project.setPostItfcMsg(msg);
                project.setPostApprovedDate(new Date());
            } else {
                project.setPostItfcFlag("N");
                project.setPostItfcCode(code);
                project.setPostItfcMsg(msg);
            }
            hlsCusPrjProjectService.updateByPrimaryKeySelective(iRequest, project);
        }
    }

    private JSONObject createContractInfo(IRequest iRequest, Long contractId) {
        JSONObject content = new JSONObject(new LinkedHashMap());
        JSONObject data = new JSONObject(new LinkedHashMap());

        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(contractId);
        contract = conContractMapper.conHomePageContractInfoGrid(contract).get(0);

        HlsCusCshPaymentReqHd paymentReqHd = new HlsCusCshPaymentReqHd();
        paymentReqHd.setPaymentReqId(contract.getPaymentReqId());
        paymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(iRequest, paymentReqHd);

        HlsCusPrjQuotation quotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(contract.getQuotationId());
        HlsCusConContractCashflow cashflow = new HlsCusConContractCashflow();
        cashflow.setContractId(contractId);
        //合同全部还款计划
        List<HlsCusConContractCashflow> allCashflowList = cashflowMapper.select(cashflow);

        List<CshPaymentReqLnBankAccount> paymentReqLnBankAccountLists = cshPaymentReqLnBankAccountMapper.queryContractCshBank(contractId);
        //付款现金流
        HlsCusConContractCashflow payCashflow = allCashflowList.stream().filter(a ->
                (paymentReqLnBankAccountLists.get(0).getSourceDocLineId().equals(a.getCashflowId()) && 0L == a.getCfItem())
        ).collect(Collectors.toList()).get(0);

        data.put("compCode", compCode);//公司编码
        data.put("bizProjId", contract.getContractId());//项目ID唯一标识  支付表ID
        data.put("projCode", contract.getProjectNumber());//项目编号
        data.put("contractCode", contract.getLegalContractNumber());//法务合同编号
        data.put("projName", contract.getContractName());//合同名称
        String projType = projectType.get(contract.getIsLowRisk());
        if ("CONLB".equals(contract.getDocumentType()) || "CONL".equals(contract.getDocumentType())) {
            projType = "24";
        }
        data.put("projType", projType);//项目类别
        data.put("putinDay", sdf2.format(paymentReqHd.getActualPayDate()));//投放日
        data.put("intervestDate", sdf2.format(paymentReqHd.getActualPayDate()));//计息日
        data.put("expectDay", sdf2.format(contract.getLeaseEndDate()));//预期到期日
        data.put("investAmt", dealDoubleAmount(contract.getLeaseItemAmount()));//投放金额
        data.put("investRate", contract.getXirr());//预期收益率
        data.put("lrrRate", contract.getIrr());//LRR收益率
        data.put("ioCode", "AL001");//回传类型 AL001项目投放 AL002项目回款

        data.put("creditorUser", "广东粤海融资租赁有限公司");//债权人
        data.put("debtorUser", contract.getBpName());//债务人
        data.put("cashAmt", quotation.getDeposit());//保证金
        data.put("firstAmt", dealDoubleAmount(quotation.getDownPayment()));//首期租金
        data.put("cashRate", quotation.getDepositRatio());//保证金比率
        data.put("firstRate", quotation.getDownPaymentRatio());//首期费率
        data.put("remark", "");//备注
        //放款支付的时候这三个不填
        //data.put("period", payCashflow.getTimes());//回款期数
        //data.put("prcAmt", dealDoubleAmount(payCashflow.getDueAmount()));//回款总本金
        //data.put("intAmt", dealDoubleAmount(payCashflow.getInterest()));//回款总利息
        //data.put("fisrtPayDate", "");//首个信托支付日
        //data.put("channelRate", "");//通道费率
        //data.put("releaseRate", "");//发行利率

        //补录流水
        JSONArray flowList = new JSONArray();
        for (CshPaymentReqLnBankAccount paymentReqLnBankAccount : paymentReqLnBankAccountLists) {
            JSONObject flow = new JSONObject(new LinkedHashMap());
            flow.put("flowId", paymentReqLnBankAccount.getFlowNo());//流水ID
            /*flow.put("prcAmt", dealDoubleAmount(paymentReqLnBankAccount.getActualPaymentAmount()));//补录流水本金
            flow.put("intAmt", 0);//补录流水利息
            flow.put("flowBizId", paymentReqLnBankAccount.getCshBankId());//业务流水ID
            //还款计划状态 {01还款中02已逾期03还款完毕04已提还05逾期完成}
            flow.put("status", "03");
            flow.put("periodAmount", dealDoubleAmount(payCashflow.getDueAmount()));//计划本金
            flow.put("periodInterestAmount", dealDoubleAmount(payCashflow.getInterest()));//计划利息
            flow.put("periodFineAmount", "0");//应还罚息
            flow.put("period", payCashflow.getTimes());//还款期数
            */
            flowList.add(flow);
        }

        List<HlsCusConContractCashflow> dueCashList = cashflowMapper.selectCashflowTimes(cashflow);
        //还款计划列表 传输全部期次
        JSONArray lineList = new JSONArray();
        for (HlsCusConContractCashflow cash : dueCashList) {
            JSONObject line = new JSONObject(new LinkedHashMap());

            String paytime;
            if (null != cash.getTransactionDate()) {
                paytime = sdf2.format(cash.getTransactionDate());
            } else {
                paytime = "";
            }

            line.put("period", cash.getTimes());//期数
            line.put("periodAmount", dealDoubleAmount(cash.getPrincipal()));//计划本金
            line.put("periodInterestAmount", dealDoubleAmount(cash.getInterest()));//计划利息
            line.put("repayAmount", dealDoubleAmount(cash.getReceivedPrincipal()));//已还本金
            line.put("repayInterestAmount", dealDoubleAmount(cash.getReceivedInterest()));//已还利息
            line.put("periodEndDate", sdf2.format(cash.getDueDate()));//计划还款日期
            line.put("periodFineAmount", dealDoubleAmount(cash.getDuePenalty()));//应收罚息
            line.put("repayFineAmount", dealDoubleAmount(cash.getReceivedPenalty()));//已还罚息
            line.put("payTime", paytime);//实际还款日期
            line.put("overdueDays", cash.getOverdueMaxDays());//逾期天数
            lineList.add(line);
        }

        data.put("flowList", flowList);
        data.put("lineList", lineList);
        content.put("data", data);
        content.put("appId", appId);
        content.put("prnd", System.currentTimeMillis() + "");
        return content;
    }

    /**
     * description
     * YH007 AL002 核销后匹配流水
     */
    public void writeOffFlowItfc(IRequest iRequest, HlsCusConContractCashflow cashflow) {
        init();
        JSONObject result = new JSONObject(new LinkedHashMap());
        JSONObject content = writeOffFlowInfo(iRequest, cashflow);
        System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, flowMatchUrl, "核销后匹配流水");

        String msg = result.getString("msg");
        String code = result.getString("code");
        String data = result.getString("data");

        HlsCusConContractCashflow cashflowUpdate = new HlsCusConContractCashflow();
        cashflowUpdate.setCashflowId(cashflow.getCashflowId());

        if (HlsCusConstant.YH007_RETURN_CODE.success.equals(code)) {
            cashflowUpdate.setPostItfcFlag("Y");
            cashflowUpdate.setPostItfcMsg(msg);
            cashflowUpdate.setPostApprovedDate(new Date());
        } else {
            cashflowUpdate.setPostItfcFlag("N");
            cashflowUpdate.setPostItfcMsg(msg);
        }
        cashflowMapper.updateByPrimaryKeySelective(cashflowUpdate);
    }

    private JSONObject writeOffFlowInfo(IRequest iRequest, HlsCusConContractCashflow writeOffCashflow) {
        JSONObject content = new JSONObject(new LinkedHashMap());
        JSONObject data = new JSONObject(new LinkedHashMap());

        //还款计划
        HlsCusConContractCashflow queryCash = new HlsCusConContractCashflow();
        queryCash.setContractId(writeOffCashflow.getContractId());
        List<HlsCusConContractCashflow> dueCashList = cashflowMapper.selectCashflowTimes(queryCash);

        HlsCusConContractCashflow dueCashflow = dueCashList.stream().filter(a ->
                (writeOffCashflow.getCashflowId().equals(a.getCashflowId()))
        ).collect(Collectors.toList()).get(0);

        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(writeOffCashflow.getContractId());
        contract = conContractMapper.conHomePageContractInfoGrid(contract).get(0);

        HlsCusCshPaymentReqHd paymentReqHd = new HlsCusCshPaymentReqHd();
        paymentReqHd.setPaymentReqId(contract.getPaymentReqId());
        paymentReqHd = cshPaymentReqHdService.selectByPrimaryKey(iRequest, paymentReqHd);

        HlsCusPrjQuotation quotation = hlsCusPrjQuotationMapper.selectByPrimaryKey(contract.getQuotationId());

        data.put("compCode", compCode);//公司编码
        data.put("bizProjId", contract.getContractId());//项目ID唯一标识  支付表ID
        data.put("projCode", contract.getProjectNumber());//项目编号
        data.put("contractCode", contract.getLegalContractNumber());//合同编号
        data.put("projName", contract.getContractName());//项目名称
        String projType = projectType.get(contract.getIsLowRisk());
        if ("CONLB".equals(contract.getDocumentType()) || "CONL".equals(contract.getDocumentType())) {
            projType = "24";
        }
        data.put("projType", projType);//项目类别

        data.put("putinDay", sdf2.format(paymentReqHd.getActualPayDate()));//投放日
        data.put("intervestDate", sdf2.format(paymentReqHd.getActualPayDate()));//计息日
        data.put("expectDay", sdf2.format(contract.getLeaseEndDate()));//预期到期日
        data.put("investAmt", dealDoubleAmount(contract.getLeaseItemAmount()));//投放金额
        data.put("investRate", contract.getXirr());//预期收益率
        data.put("lrrRate", contract.getIrr());//LRR收益率
        data.put("ioCode", "AL002");//回传类型 AL001项目投放 AL002项目回款

        data.put("creditorUser", "广东粤海融资租赁有限公司");//债权人
        data.put("debtorUser", contract.getBpName());//债务人
        data.put("cashAmt", dealDoubleAmount(quotation.getDeposit()));//保证金
        data.put("firstAmt", dealDoubleAmount(quotation.getDownPayment()));//首期租金
        data.put("cashRate", quotation.getDepositRatio());//保证金比率
        data.put("firstRate", quotation.getDownPaymentRatio());//首期费率
        data.put("remark", "");//备注
        data.put("period", writeOffCashflow.getTimes());//回款期数
        data.put("prcAmt", dealDoubleAmount(contract.getReceivedPrincipal()));//回款总本金
        data.put("intAmt", dealDoubleAmount(contract.getReceivedInterest()));//回款总利息
        //data.put("fisrtPayDate", "");//首个信托支付日
        //data.put("channelRate", "");//通道费率
        //data.put("releaseRate", "");//发行利率

        //补录流水
        HlsCusConContractCashflow cashflow = cashflowMapper.selectConContractCashflow(writeOffCashflow.getCashflowId());
        JSONArray flowList = new JSONArray();
        JSONObject flow = new JSONObject(new LinkedHashMap());
        flow.put("prcAmt", dealDoubleAmount(cashflow.getReceivedPrincipal()).setScale(2, BigDecimal.ROUND_HALF_UP));//补录流水本金
        flow.put("intAmt", dealDoubleAmount(cashflow.getReceivedInterest()));//补录流水利息
        flow.put("flowId", writeOffCashflow.getMergeFlowId());//流水ID
        flow.put("flowBizId", writeOffCashflow.getCashflowId());//业务流水ID
        //还款计划状态 {01还款中02已逾期03还款完毕04已提还05逾期完成}
        flow.put("status", cashflow.getWriteOffFlagDesc());
        flow.put("periodAmount", dealDoubleAmount(writeOffCashflow.getPrincipal()));//计划本金
        flow.put("periodInterestAmount", dealDoubleAmount(writeOffCashflow.getInterest()));//计划利息
        flow.put("periodFineAmount", dealDoubleAmount(dueCashflow.getDuePenalty()));//应还罚息
        flow.put("period", writeOffCashflow.getTimes());//还款期数
        flowList.add(flow);

        //还款计划列表 传输全部期次
        JSONArray lineList = new JSONArray();
        for (HlsCusConContractCashflow cash : dueCashList) {
            JSONObject line = new JSONObject(new LinkedHashMap());

            String paytime;
            if (null != cash.getTransactionDate()) {
                paytime = sdf2.format(cash.getTransactionDate());
            } else {
                paytime = "";
            }

            line.put("period", cash.getTimes());//期数
            line.put("periodAmount", dealDoubleAmount(cash.getPrincipal()));//计划本金
            line.put("periodInterestAmount", dealDoubleAmount(cash.getInterest()));//计划利息
            line.put("repayAmount", dealDoubleAmount(cash.getReceivedPrincipal()));//已还本金
            line.put("repayInterestAmount", dealDoubleAmount(cash.getReceivedInterest()));//已还利息
            line.put("periodEndDate", sdf2.format(cash.getDueDate()));//计划还款日期
            line.put("payTime", paytime);//实际还款日期
            line.put("overdueDays", cash.getOverdueMaxDays());//逾期天数
            line.put("periodFineAmount", dealDoubleAmount(cash.getDuePenalty()));//应收罚息
            line.put("repayFineAmount", dealDoubleAmount(cash.getReceivedPenalty()));//已还罚息
            lineList.add(line);
        }

        data.put("flowList", flowList);
        data.put("lineList", lineList);
        content.put("data", data);
        content.put("appId", appId);
        content.put("prnd", System.currentTimeMillis() + "");
        return content;
    }

    /**
     * description
     * YH009 流水合并接口
     */
    public void mergeFlowItfc(IRequest iRequest, HlsCusConContractCashflow cashflow) {
        init();
        JSONObject result = new JSONObject(new LinkedHashMap());
        JSONObject content = mergeFlowInfo(iRequest, cashflow);
        System.out.println(content);
        result = interfacePlatformUtils.getInterfaceRequest(content, mergeFlowUrl, "YH009流水合并接口");

        String msg = result.getString("msg");
        String code = result.getString("code");
        String data = result.getString("data");

        if (HlsCusConstant.YH007_RETURN_CODE.success.equals(code)) {
            HlsCusConContractCashflow cashflowUpdate = new HlsCusConContractCashflow();
            cashflowUpdate.setCashflowId(cashflow.getCashflowId());
            cashflowUpdate.setMergeFlowId(data);
            cashflowMapper.updateByPrimaryKeySelective(cashflowUpdate);
        }
    }

    private JSONObject mergeFlowInfo(IRequest iRequest, HlsCusConContractCashflow cashflow) {
        JSONObject content = new JSONObject(new LinkedHashMap());
        JSONArray flowList = new JSONArray();

        HlsCusCshWriteOff writeOff = new HlsCusCshWriteOff();
        writeOff.setCashflowId(cashflow.getCashflowId());
        List<HlsCusCshWriteOff> writeList = cshWriteOffMapper.select(writeOff);
        List<Long> transactionIdS = (List) writeList.stream().map(HlsCusCshWriteOff::getCshTransactionId).distinct().collect(Collectors.toList());

        for (Long id : transactionIdS) {
            HlsCusCshTransaction transaction = new HlsCusCshTransaction();
            transaction.setTransactionId(id);
            transaction = transactionService.selectByPrimaryKey(iRequest, transaction);

            JSONObject flow = new JSONObject(new LinkedHashMap());
            flow.put("flowId", transaction.getFlowNo());
            flowList.add(flow);
        }

        content.put("data", flowList);
        content.put("appId", appId);
        content.put("prnd", System.currentTimeMillis() + "");
        return content;
    }

    /**
     * description
     * DC41 收款数据推送
     */
    public void postCashflowItfc(IRequest iRequest, HlsCusCshWriteOff writeOff) {
        if (1L == writeOff.getCfItem() || 51L == writeOff.getCfItem() || 3L == writeOff.getCfItem()) {
            init();
            JSONObject result = new JSONObject(new LinkedHashMap());
            JSONObject content = cashflowInfo(iRequest, writeOff);
            result = interfacePlatformUtils.getInterfaceRequest(content, DC41ASendUrl, "DC41收款数据推送");
            if(result != null){
                String msg = result.getString("msg");
                String code = result.getString("code");
                JSONArray data = result.getJSONArray("data");

                HlsCusCshTransaction transaction = new HlsCusCshTransaction();
                transaction.setTransactionId(writeOff.getCshTransactionId());
                if (HlsCusConstant.DC51_RETURN_CODE.OK_IMPORT.equals(code)) {
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject newObject = (JSONObject) data.get(i);
                        String document = String.valueOf(newObject.get("v5"));
                        transaction.setItfcArrNumber(document);
                    }
                    transaction.setPostItfcFlag("Y");
                    transaction.setPostItfcCode(code);
                    transaction.setPostItfcMsg(msg);
                    transaction.setPostApprovedDate(new Date());
                } else {
                    transaction.setPostItfcFlag("N");
                    transaction.setPostItfcCode(code);
                    transaction.setPostItfcMsg(msg + data.toString());
                }
                transactionService.updateByPrimaryKeySelective(iRequest, transaction);
            }
        }
    }

    private JSONObject cashflowInfo(IRequest iRequest, HlsCusCshWriteOff writeOff) {
        JSONObject content = new JSONObject(new LinkedHashMap());
        writeOff = cshWriteOffService.selectByPrimaryKey(iRequest, writeOff);
        HlsCusCshTransaction hlsCusCshTransaction = new HlsCusCshTransaction();
        hlsCusCshTransaction.setTransactionId(writeOff.getCshTransactionId());
        hlsCusCshTransaction = transactionService.selectByPrimaryKey(iRequest, hlsCusCshTransaction);

        HlsCusConContract contract = new HlsCusConContract();
        contract.setContractId(writeOff.getContractId());
        contract = conContractService.selectByPrimaryKey(iRequest, contract);
        HlsCusBpMaster master = bpMasterMapper.selectByPrimaryKey(contract.getTenantId());

        //基本信息
        JSONObject header = new JSONObject(new LinkedHashMap());
        header.put("rec_type", "DC41");
        //导入ID
        header.put("p1", writeOff.getWriteOffId());
        //用户名
        header.put("p2", DC51_USER);
        //密码
        header.put("p3", DC51_USER_PWD);
        //公司
        header.put("p4", compCode);

        JSONArray lines = new JSONArray();

        int sequence = 1;

        JSONObject line1 = new JSONObject(new LinkedHashMap());
        line1.put("rec_type", "DC41A");
        line1.put("p1", sequence);//序号
        line1.put("p2", writeOff.getWriteOffId());
        line1.put("p3", "");//备用
        line1.put("p4", sequence);//行号
        line1.put("p5", compCode);//核算主体 收款数据所属公司代码
        line1.put("p6", unitCode);//部门代码
        line1.put("p7", employeeCode);//通用推送人员工号
        line1.put("p8", postOAEmployeeCode);//应付报账通用推送人员OA系统账号
        line1.put("p9", "GLD_999");//事务类别
        line1.put("p10", "CNY");//报账币种
        line1.put("p11", "CORPORATE");//汇率类型
        line1.put("p12", "1");//汇率值
        line1.put("p13", "");//申请理由

        String jeDescription;
        String line41Bdescription;
        if (writeOff.getCfItem() == 51) {
            jeDescription = "收到" + master.getBpName() + "融资租赁业务保证金";
            line41Bdescription = master.getBpName() + sdf.format(hlsCusCshTransaction.getTransactionDate()) + "融资租赁业务保证金";
        } else if (writeOff.getCfItem() == 3) {
            jeDescription = "收到" + master.getBpName() + "手续费";
            line41Bdescription = master.getBpName() + sdf.format(hlsCusCshTransaction.getTransactionDate()) + "手续费";
        } else {
            jeDescription = "收到" + master.getBpName() + "第" + writeOff.getTimes() + "期租金";
            line41Bdescription = master.getBpName() + sdf.format(hlsCusCshTransaction.getTransactionDate()) + "租金";
        }

        line1.put("p14", jeDescription);//凭证头描述
        line1.put("p15", dealDoubleAmount(writeOff.getCshWriteOffAmount()));//原币总金额
        line1.put("p16", dealDoubleAmount(writeOff.getCshWriteOffAmount()));//本币总金额
        line1.put("p17", "0");//附件张数
        line1.put("p18", "N");//下月自动冲销
        line1.put("p19", sdfYM.format(hlsCusCshTransaction.getTransactionDate()));//报账期间 YYYY-MM
        line1.put("p20", "GDHlecse");//来源系统
        lines.add(line1);

        JSONObject line2 = new JSONObject(new LinkedHashMap());
        line2.put("rec_type", "DC41B");
        sequence = sequence + 1;
        line2.put("p1", sequence);
        line2.put("p2", writeOff.getWriteOffId());
        line2.put("p3", "");//备用
        line2.put("p4", "1");//行号
        line2.put("p5", line41Bdescription);//行描述  承租人+日期+什么款（租金/保证金）
        line2.put("p6", hlsCusCshTransaction.getDescription() + "");//摘要
        line2.put("p7", hlsCusCshTransaction.getPkEbankDzd());//流水唯一性标识
        line2.put("p8", sdf.format(hlsCusCshTransaction.getTransactionDate()));//交易时间 YYYY-MM-DD
        line2.put("p9", hlsCusCshTransaction.getBpBankAccountNum());//对方银行账户
        line2.put("p10", hlsCusCshTransaction.getBpBankAccountName());//对方银行账户户名
        line2.put("p11", "CNY");//币种
        line2.put("p12", dealDoubleAmount(writeOff.getCshWriteOffAmount()));//认领金额
        lines.add(line2);

        if (writeOff.getCfItem() == 1) {
            if (writeOff.getWriteOffPrincipal() != 0d) {
                //融资租赁本金
                JSONObject line3 = new JSONObject(new LinkedHashMap());
                line3.put("rec_type", "DC41C");
                sequence = sequence + 1;
                line3.put("p1", sequence);
                line3.put("p2", writeOff.getWriteOffId());
                line3.put("p3", "");//备用
                line3.put("p4", "1");//行号
                line3.put("p5", "融资租赁本金");//描述
                line3.put("p6", "FINANCIAL_LEASE_BUSINESS");//一级业务分类
                line3.put("p7", "125");//二级业务分类
                line3.put("p8", "359");//三级业务分类
                line3.put("p9", "CNY");//币种
                line3.put("p10", dealDoubleAmount(writeOff.getWriteOffPrincipal()));//金额

                HlsCusBpMasterBankAccount bpMasterBankAccount = new HlsCusBpMasterBankAccount();
                bpMasterBankAccount.setBpId(master.getBpId());
                bpMasterBankAccount.setBankAccountNum(hlsCusCshTransaction.getBpBankAccountNum());
                List<HlsCusBpMasterBankAccount> list = bpBankAccountMapper.select(bpMasterBankAccount);
                String p11 = "0";
                if (list.size() > 0) {
                    if (null != list.get(0).getEbsBusinessCode() && "" != list.get(0).getEbsBusinessCode()) {
                        p11 = list.get(0).getEbsBusinessCode();
                    }
                }

                line3.put("p11", p11);//核算项目段
                line3.put("p12", "0");//往来段
                line3.put("p13", "0");//子科目段
                line3.put("p14", "0");//产品段
                line3.put("p15", "N");//是否代缴增值税 Y/N
                line3.put("p16", " ");//税率
                line3.put("p17", "");//税额
                lines.add(line3);
            }
            if (writeOff.getWriteOffInterest() != 0d) {
                //融资租赁利息
                JSONObject line3 = new JSONObject(new LinkedHashMap());
                line3.put("rec_type", "DC41C");
                sequence = sequence + 1;
                line3.put("p1", sequence);
                line3.put("p2", writeOff.getWriteOffId());
                line3.put("p3", "");//备用
                line3.put("p4", "1");//行号
                line3.put("p5", "利息");//描述
                line3.put("p6", "FINANCIAL_LEASE_BUSINESS");//一级业务分类
                line3.put("p7", "124");//二级业务分类
                line3.put("p8", "360");//三级业务分类
                line3.put("p9", "CNY");//币种
                line3.put("p10", dealDoubleAmount(writeOff.getWriteOffInterest()));//金额
                line3.put("p11", "0");//核算项目段
                line3.put("p12", "0");//往来段
                line3.put("p13", "0");//子科目段
                line3.put("p14", "0");//产品段
                line3.put("p15", "N");//是否代缴增值税 Y/N
                line3.put("p16", " ");//税率
                line3.put("p17", "");//税额
                lines.add(line3);
            }
        } else if (writeOff.getCfItem() == 3) {
            //融资租赁本金
            JSONObject line3 = new JSONObject(new LinkedHashMap());
            line3.put("rec_type", "DC41C");
            sequence = sequence + 1;
            line3.put("p1", sequence);
            line3.put("p2", writeOff.getWriteOffId());
            line3.put("p3", "");//备用
            line3.put("p4", "1");//行号
            line3.put("p5", "手续费");//描述
            line3.put("p6", "FINANCIAL_LEASE_BUSINESS");//一级业务分类
            line3.put("p7", "125");//二级业务分类
            line3.put("p8", "359");//三级业务分类
            line3.put("p9", "CNY");//币种
            line3.put("p10", dealDoubleAmount(writeOff.getWriteOffDueAmount()));//金额
            line3.put("p11", "0");//核算项目段
            line3.put("p12", "0");//往来段
            line3.put("p13", "0");//子科目段
            line3.put("p14", "0");//产品段
            line3.put("p15", "N");//是否代缴增值税 Y/N
            line3.put("p16", " ");//税率
            line3.put("p17", "");//税额
            lines.add(line3);
        } else {
            //保证金
            JSONObject line3 = new JSONObject(new LinkedHashMap());
            line3.put("rec_type", "DC41C");
            sequence = sequence + 1;
            line3.put("p1", sequence);
            line3.put("p2", writeOff.getWriteOffId());
            line3.put("p3", "");//备用
            line3.put("p4", "1");//行号
            line3.put("p5", "保证金");//描述
            line3.put("p6", "FINANCIAL_LEASE_BUSINESS");//一级业务分类
            line3.put("p7", "123");//二级业务分类
            line3.put("p8", "357");//三级业务分类
            line3.put("p9", "CNY");//币种
            line3.put("p10", dealDoubleAmount(writeOff.getCshWriteOffAmount()));//金额
            line3.put("p11", "0");//核算项目段
            line3.put("p12", "0");//往来段
            line3.put("p13", "0");//子科目段
            line3.put("p14", "0");//产品段
            line3.put("p15", "N");//是否代缴增值税 Y/N
            line3.put("p16", " ");//税率
            line3.put("p17", "");//税额
            lines.add(line3);
        }

        /*JSONObject line4 = new JSONObject(new LinkedHashMap());
        line4.put("rec_type", "DC41D");
        sequence = sequence + 1;
        line4.put("p1", sequence);
        line4.put("p2", jeHead.getJeTrxId());
        line4.put("p3", "");//备用
        line4.put("p4", "");//附件名称
        //附扫描件附件url
        line4.put("p5", "");
        lines.add(line4);*/

        content.put("head", header);
        content.put("line", lines);
        return content;
    }
}
