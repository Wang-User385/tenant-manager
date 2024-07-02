package com.hand.hls.prj.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.lock.components.DatabaseLockProvider;
import com.hand.hap.system.dto.DTOStatus;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.service.impl.HlsBeanRefUtilServiceImpl;
import com.hand.hls.calc.dto.HlsCalcConfig;
import com.hand.hls.calc.dto.HlsPriceListConfigHd;
import com.hand.hls.calc.dto.HlsPriceListConfigLn;
import com.hand.hls.calc.mapper.HlsCalcConfigMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigHdMapper;
import com.hand.hls.calc.mapper.HlsPriceListConfigLnMapper;
import com.hand.hls.calc.service.HlsCalcExcelImportUtilService;
import com.hand.hls.calc.service.HlsCalcSaveService;
import com.hand.hls.calc.service.HlsCusCalcExcelImportUtilService;
import com.hand.hls.common.utils.GzipUtil;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.cont.dto.HlsCusConContractCashflow;
import com.hand.hls.cont.mapper.HlsCusConContractCashflowMapper;
import com.hand.hls.cont.mapper.HlsCusConContractMapper;
import com.hand.hls.cont.service.HlsCusConContractCashflowService;
import com.hand.hls.csh.dto.HlsCusCshWriteOff;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqHdMapper;
import com.hand.hls.csh.mapper.HlsCusCshPaymentReqLnMapper;
import com.hand.hls.csh.mapper.HlsCusCshWriteOffMapper;
import com.hand.hls.fnd.dto.HlsEmployee;
import com.hand.hls.fnd.mapper.HlsEmployeeMapper;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.prj.dto.*;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationCashflowMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationDetailsMapper;
import com.hand.hls.prj.mapper.HlsCusPrjQuotationMapper;
import com.hand.hls.prj.service.*;
import com.hand.hls.req.dto.HlsCusChangeReqInfo;
import com.hand.hls.req.mapper.HlsCusChangeReqInfoMapper;
import com.hand.hls.user.service.LoginUserInfoService;
import com.hand.hls.utils.HlsCusXirr;
import com.hand.hls.wfl.service.IActivitiStartService;
import hls.core.sys.event.service.SysEventService;
import hls.core.utils.exception.HlsCusException;
import jodd.util.ArraysUtil;
import leaf.utils.ConfigUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uncertain.composite.CompositeMap;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class HlsCusPrjQuotationServiceImpl extends BaseServiceImpl<HlsCusPrjQuotation> implements HlsCusPrjQuotationService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String KEY_INDEX = "index";
    private static final String KEY_CELLS = "cells";
    private static final String KEY_ROWS = "rows";
    public static final String KEY_VALUE = "value";
    private static final String KEY_FORMULA = "formula";
    public static final String KEY_FIELD = "field";
    private static final String PRICE_TYPE_SINGLE = "SINGLE";
    private static final String PRICE_TYPE_MULTI_LINE = "MULTI_LINE";
    private static final String KEY_FORMAT = "format";
    private static final String[] DOUBLE_FORMATS = {"0%", "0.00"};
    private static final String[] DATE_FORMATS = {"mm-dd-yy"};
    public static final String LEASE_START_DATE = "lease_start_date";
    public static final Long DUE_AMOUNT_CF_ITEM = 1L;

    private static final String sourceDocumentCategory = "PRJ_PROJECT";
    private static final String conSourceDocumentCategory = "CON_CONTRACT";
    private static final String hlsSourceDocumentCategory = "HLS_MARKETING_REPORT";
    private static final String creditSourceDocumentCategory = "HLS_CREDIT_LINE_CHANCE";

    public static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.!~*'()";
    private static final String APP_CALCULATE = "APP_CALCULATE";
    @Autowired
    private HlsCusPrjQuotationMapper prjQuotationMapper;
    @Autowired
    private HlsCusPrjChanceService hlsCusPrjChanceService;
    @Autowired
    private HlsCusPrjQuotationService hlsCusPrjQuotationService;
    @Autowired
    private HlsCusPrjQuotationDetailsService hlsCusPrjQuotationDetailsService;

    @Autowired
    private HlsCusPrjQuotationDetailsMapper hlsCusPrjQuotationDetailsMapper;

    @Autowired
    private HlsCusPrjQuotationCashflowService hlsCusPrjQuotationCashflowService;
    @Autowired
    private HlsEmployeeMapper employeeMapper;
    @Autowired
    private DatabaseLockProvider databaseLockProvider;
    @Autowired
    private LoginUserInfoService loginUserInfoService;
    @Autowired
    private SysEventService sysEventService;
    @Autowired
    private IActivitiStartService activitiStartService;
    @Autowired
    private HlsCusPrjProjectService hlsCusPrjProjectService;
    @Autowired
    private HlsCusConContractMapper hlsCusConContractMapper;
    @Autowired
    private HlsCusConContractCashflowMapper hlsCusConContractCashflowMapper;
    @Autowired
    private HlsCusCshPaymentReqLnMapper hlsCusCshPaymentReqLnMapper;
    @Autowired
    private HlsCusCshPaymentReqHdMapper hlsCusCshPaymentReqHdMapper;
    @Autowired
    private HlsCalcSaveService hlsCalcSaveService;
    @Autowired
    private HlsPriceListConfigHdMapper configHdMapper;
    @Autowired
    private HlsPriceListConfigLnMapper configLnMapper;
    @Autowired
    private HlsCalcConfigMapper priceListMapper;
    @Autowired
    private HlsCusCshWriteOffMapper hlsCusCshWriteOffMapper;
    @Autowired
    private HlsCusCalcExcelImportUtilService hlsCusCalcExcelImportUtilService;
    @Autowired
    private HlsCalcExcelImportUtilService hlsCalcExcelImportUtilService;
    @Autowired
    private HlsCusChangeReqInfoMapper hlsCusChangeReqInfoMapper;
    @Autowired
    private HlsCusConContractCashflowService hlsCusConContractCashflowService;
    @Autowired
    private HlsCusPrjQuotationSubsectionService hlsCusPrjQuotationSubsectionService;
    @Autowired
    HlsCusPrjQuotationCashflowMapper hlsCusPrjQuotationCashflowMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;

    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public ResponseData queryBaseRateNow(CompositeMap map, String var2){
        List answer;
        if (ConfigUtils.isMySQL()) {
            answer = this.prjQuotationMapper.queryBaseRateNow(map, var2);
        } else {
            answer = this.prjQuotationMapper.queryBaseRateNow(map, var2);
        }

        return new ResponseData(answer);
    }

    public HlsCusPrjQuotation updatePrjQuotation(HlsCusPrjQuotation prjQuotationDto) {
        //租赁类型
        if ("回租".equalsIgnoreCase(prjQuotationDto.getBusinessType())) {
            prjQuotationDto.setBusinessType("LEASEBACK");
        } else if ("直租".equalsIgnoreCase(prjQuotationDto.getBusinessType())) {
            prjQuotationDto.setBusinessType("LEASE");
        }
        //利率类型
        if ("固定".equalsIgnoreCase(prjQuotationDto.getIntRateType())) {
            prjQuotationDto.setIntRateType("FIXED");
        } else if ("浮动".equalsIgnoreCase(prjQuotationDto.getIntRateType())) {
            prjQuotationDto.setIntRateType("FLOATING");
        }
        //浮动方式
        if ("上调".equalsIgnoreCase(prjQuotationDto.getFloatingWay())) {
            prjQuotationDto.setFloatingWay("INCREASE");
        } else if ("上浮".equalsIgnoreCase(prjQuotationDto.getFloatingWay())) {
            prjQuotationDto.setFloatingWay("MUTIPLY");
        }
        //还租频率
//        if ("月付".equalsIgnoreCase(prjQuotationDto.getRentingFrequency())) {
//            prjQuotationDto.setRentingFrequency("MONTH");
//        } else if ("季付".equalsIgnoreCase(prjQuotationDto.getRentingFrequency())) {
//            prjQuotationDto.setRentingFrequency("QUARTER");
//        } else if ("半年付".equalsIgnoreCase(prjQuotationDto.getRentingFrequency())) {
//            prjQuotationDto.setRentingFrequency("HALF_A_YEAR");
//        } else if ("HALF_A_YEAR".equalsIgnoreCase(prjQuotationDto.getRentingFrequency())) {
//            prjQuotationDto.setRentingFrequency("YEAR");
//        }
        //还租方式
        if ("期初".equalsIgnoreCase(prjQuotationDto.getRentingMethod())) {
            prjQuotationDto.setRentingMethod("PERIOD_BENGINING");
        } else if ("期末".equalsIgnoreCase(prjQuotationDto.getRentingMethod())) {
            prjQuotationDto.setRentingMethod("PERIOD_FINAL");
        }
        //保证金还款方式
        if ("不抵扣".equalsIgnoreCase(prjQuotationDto.getDepositReturnMethod())) {
            prjQuotationDto.setDepositReturnMethod("PERIOD_FINAL_RETURN");
        } else if ("抵扣".equalsIgnoreCase(prjQuotationDto.getDepositReturnMethod())) {
            prjQuotationDto.setDepositReturnMethod("PERIOD_FINAL_DEDUCTIBLE");
        }
        //调息方式
        if ("次年一月一号".equalsIgnoreCase(prjQuotationDto.getFloatingRangeMethod())) {
            prjQuotationDto.setFloatingRangeMethod("NEXT_YEAR");
        } else if ("次日".equalsIgnoreCase(prjQuotationDto.getFloatingRangeMethod())) {
            prjQuotationDto.setFloatingRangeMethod("NEXT_DAY");
        } else if ("次期".equalsIgnoreCase(prjQuotationDto.getFloatingRangeMethod())) {
            prjQuotationDto.setFloatingRangeMethod("NEXT_TIMES");
        } else if ("不调息".equalsIgnoreCase(prjQuotationDto.getFloatingRangeMethod())) {
            prjQuotationDto.setFloatingRangeMethod("NOT");
        } else if ("次月".equalsIgnoreCase(prjQuotationDto.getFloatingRangeMethod())) {
            prjQuotationDto.setFloatingRangeMethod("NEXT_MONTH");
        }




        return prjQuotationDto;
    }


    public HlsCusPrjQuotation updatePrjQutationProfile(HlsCusPrjQuotation prjQuotationDto, IRequest iRequest) throws Exception {
        //给报价表上的penalty_profile字段、invoice_profile字段加上默认值 （值先固定）
        HlsCusPrjChance prjChance = new HlsCusPrjChance();
        prjChance.setChanceId(prjQuotationDto.getSourceDocumentId());
        prjChance = hlsCusPrjChanceService.selectByPrimaryKey(iRequest, prjChance);
        if (!"".equals(prjChance.getDocumentType()) && !"".equals(prjChance.getLeaseItemType()) && prjQuotationDto.getLeaseStartDate() != null) {
            if (prjChance.getBusinessType().equals("LEASE")) {   //直租

            } else if (prjChance.getBusinessType().equals("LEASEBACK")) {//回租

            } else if (prjChance.getBusinessType().equals("SUBLEASE")) {

            }
        }
        //当prj_project上的 leaseItemType 是
        prjQuotationDto.setPenaltyProfile("STD");

        return prjQuotationDto;
    }


    @Override
    public List<HlsCusPrjQuotation> queryPaymentTableInfo(IRequest iRequest, HlsCusPrjQuotation prjQuotation, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return prjQuotationMapper.queryPaymentTableInfo(prjQuotation);
    }

    @Override
    public List<HlsCusPrjQuotation> queryPaymentTableInfoConfirm(IRequest iRequest, HlsCusPrjQuotation prjQuotation, int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        List<HlsCusPrjQuotation> list = new ArrayList<>();
        list = prjQuotationMapper.queryPaymentTableInfoConfirm(prjQuotation);
        List<HlsCusPrjQuotation> newList = new ArrayList<>();
        if (list.size() != 1) {
            for (HlsCusPrjQuotation dt : list) {
                if (!dt.getDataClass().equalsIgnoreCase("VIRTUAL_CON")) {
                    //已被确认或付款审批通过未确认
                    if ("Y".equalsIgnoreCase(dt.getSelectedFlag())
                            && ("APPROVED".equalsIgnoreCase(dt.getPaymentStatus()))
                            && ("INCEPT".equalsIgnoreCase(dt.getContractStatus()) || "PENDING".equalsIgnoreCase(dt.getContractStatus()))) {
                        dt.setConfirmFlag("已确认");
                        newList.add(dt);
                    } else if ("Y".equalsIgnoreCase(dt.getSelectedFlag())
                            && ("APPROVING".equalsIgnoreCase(dt.getPaymentStatus()))
                            && "SIGN".equalsIgnoreCase(dt.getContractStatus())) {
                        dt.setConfirmFlag("确认中");
                        newList.add(dt);
                    } else if (("N".equalsIgnoreCase(dt.getSelectedFlag())
                            && "SIGN".equalsIgnoreCase(dt.getContractStatus())
                            && "APPROVED".equalsIgnoreCase(dt.getPaymentStatus()))) {
                        dt.setConfirmFlag("未确认");
                        newList.add(dt);
                    }

                    HlsCusConContract conContract = new HlsCusConContract();
                    conContract.setQuotationId(dt.getQuotationId());
                    conContract.setDataClass("NORMAL");
                    List<HlsCusConContract> conContractList = new ArrayList<>();
                    conContractList = hlsCusConContractMapper.select(conContract);
                    if (conContractList.size() != 0) {
                        boolean passFlag = true;
                        HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                        conContractCashflow.setContractId(conContractList.get(0).getContractId());
                        conContractCashflow.setTimes(0L);
                        List<HlsCusConContractCashflow> hlsCusConContractCashflowList = new ArrayList<>();
                        hlsCusConContractCashflowList = hlsCusConContractCashflowMapper.select(conContractCashflow);
                        for (HlsCusConContractCashflow dx : hlsCusConContractCashflowList) {
                            if (!"FULL".equalsIgnoreCase(dx.getWriteOffFlag())) {
                                passFlag = false;
                            }
                        }
                        if (passFlag) {
                            dt.setPaymentFlag("通过");
                        } else {
                            dt.setPaymentFlag("未通过");
                        }

                    } else {
                        dt.setPaymentFlag("未通过");
                    }
                }
            }
        } else if (list.size() == 1) {
            HlsCusPrjQuotation virtualPrj = new HlsCusPrjQuotation();
            virtualPrj = list.get(0);
            if ("Y".equalsIgnoreCase(virtualPrj.getSelectedFlag())
                    && ("APPROVED".equalsIgnoreCase(virtualPrj.getPaymentStatus()))
                    && ("INCEPT".equalsIgnoreCase(virtualPrj.getContractStatus()) || "PENDING".equalsIgnoreCase(virtualPrj.getContractStatus()))) {
                virtualPrj.setConfirmFlag("已确认");
                newList.add(virtualPrj);
            } else if ("Y".equalsIgnoreCase(virtualPrj.getSelectedFlag())
                    && ("APPROVING".equalsIgnoreCase(virtualPrj.getPaymentStatus()))
                    && "SIGN".equalsIgnoreCase(virtualPrj.getContractStatus())) {
                virtualPrj.setConfirmFlag("确认中");
                newList.add(virtualPrj);
            } else if (("N".equalsIgnoreCase(virtualPrj.getSelectedFlag())
                    && "SIGN".equalsIgnoreCase(virtualPrj.getContractStatus())
                    && "APPROVED".equalsIgnoreCase(virtualPrj.getPaymentStatus()))) {
                virtualPrj.setConfirmFlag("未确认");
                newList.add(virtualPrj);
            }

            HlsCusConContract conContract = new HlsCusConContract();
            conContract.setQuotationId(virtualPrj.getQuotationId());
            List<HlsCusConContract> conContractList = new ArrayList<>();
            conContractList = hlsCusConContractMapper.select(conContract);
            if (conContractList.size() != 0) {
                boolean passFlag = true;
                HlsCusConContractCashflow conContractCashflow = new HlsCusConContractCashflow();
                conContractCashflow.setContractId(conContractList.get(0).getContractId());
                conContractCashflow.setTimes(0L);
                List<HlsCusConContractCashflow> hlsCusConContractCashflowList = new ArrayList<>();
                hlsCusConContractCashflowList = hlsCusConContractCashflowMapper.select(conContractCashflow);
                for (HlsCusConContractCashflow dx : hlsCusConContractCashflowList) {
                    if (!"FULL".equalsIgnoreCase(dx.getWriteOffFlag())) {
                        passFlag = false;
                    }
                }
                if (passFlag) {
                    virtualPrj.setPaymentFlag("通过");
                } else {
                    virtualPrj.setPaymentFlag("未通过");
                }

            } else {
                virtualPrj.setPaymentFlag("未通过");
            }
        }
        return newList;
    }


    @Override
    public List<HlsCusPrjQuotation> queryPaymentTableInfoLov(IRequest iRequest, HlsCusPrjQuotation prjQuotation,
                                                             int page, int pageSize) {
        PageHelper.startPage(page, pageSize);
        return prjQuotationMapper.queryPaymentTableInfoLov(prjQuotation);
    }

    @Override
    public HlsCusPrjQuotation prjQuotationSave(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation) {
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        if (hlsCusPrjQuotation.getQuotationId() != null && hlsCusPrjQuotation.getQuotationId() != 0) {
            prjQuotation.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            prjQuotation = self().selectByPrimaryKey(iRequest, prjQuotation);
            hlsCusPrjQuotation.setObjectVersionNumber(prjQuotation.getObjectVersionNumber());
            prjQuotation = self().updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
        } else {
            hlsCusPrjQuotation.set__status(DTOStatus.ADD);
            hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation.setStatus("NEW");
            prjQuotation = self().insertSelective(iRequest, hlsCusPrjQuotation);
        }
        return prjQuotation;
    }

    @Override
    public List<HlsCusPrjQuotation> paymentTableSubmit(IRequest iRequest, List<HlsCusPrjQuotation> hlsCusPrjQuotationList) {
        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            //保存租金支付表信息
            dt = self().prjQuotationSave(iRequest, dt);
        }
        databaseLockProvider.lock(hlsCusPrjQuotationList.get(0));
        //获取申请人
        HlsEmployee employee = employeeMapper.getEmployeeCode(iRequest.getUserId());
        String employeeCode = employee.getEmployeeCode();
        iRequest.setEmployeeCode(employeeCode);
        //开始流程
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("workFlowType", "PRJ_PAYMENT_TABLE_CONFIRM_WFL");
        activitiStartService.start(iRequest, hlsCusPrjQuotationList, params);

        for (HlsCusPrjQuotation dt : hlsCusPrjQuotationList) {
            //勾选的待确认，选中且租金支付表状态为审批通过，原合同状态为签约
            if ("Y".equalsIgnoreCase(dt.getSelectedFlag())
                    && "SIGN".equalsIgnoreCase(dt.getContractStatus()) && "APPROVED".equalsIgnoreCase(dt.getPaymentStatus())
            ) {
                dt.setPaymentStatus("APPROVING");
                dt = self().prjQuotationSave(iRequest, dt);
            }
        }
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusPrjQuotationList.get(0).getSourceDocumentId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);
        Map<String, Object> paramsEvent = new HashMap<String, Object>();
        String userName = "";
        if (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode()).size() > 0) {
            userName = (loginUserInfoService.queryUserInfo(iRequest.getEmployeeCode())).get(0).getUserName();
        }
        String msg = userName + "创建了" + hlsCusPrjProject.getProjectName() + "项目的租金支付表确认审批" + hlsCusPrjProject.getProjectNumber();
        paramsEvent.put("message", msg);
        paramsEvent.put("noticeTitle", "租金支付表确认");
        paramsEvent.put("noticeType", "NOTICE");
        paramsEvent.put("url", "");
        paramsEvent.put("level", 1L);
        sysEventService.eventSave(iRequest, hlsCusPrjProject.getProjectId(), hlsCusPrjProject.getDocumentCategory(), hlsCusPrjProject.getDocumentType(), "PRJ", "PRJ_PROJECT", "P2D", paramsEvent);

        return hlsCusPrjQuotationList;
    }

    @Override
    public List<HlsCusPrjQuotation> queryCshFineInfo(HlsCusPrjQuotation prjQuotation) {
        return prjQuotationMapper.queryCshFineInfo(prjQuotation);
    }

    @Override
    public List<HlsCusPrjQuotation> queryPrjQuotationInfo(HlsCusPrjQuotation prjQuotation) {
        return prjQuotationMapper.queryPrjQuotationInfo(prjQuotation);
    }

    @Override
    public List<HlsCusPrjQuotation> queryPrjQuotationInfoForContractPlan(HlsCusPrjQuotation prjQuotation) {
        return prjQuotationMapper.queryPrjQuotationInfoForContractPlan(prjQuotation);
    }


    /*
     * 更新对应的quotation表记录的信息
     * */
    @Override
    public void updateQuotaion(HlsCusPrjQuotation quotation, List<Map> maps, HlsCalcConfig hlsCalcConfig,Boolean calcFlag) throws Exception {

        Map<String, Object> data = new HashMap<>();
        String sourceSheet;

        //获取价目表类型
        String priceList = quotation.getPriceList();

        //这里判断一下，如果当前报价存在details，则取报价表里面的sheets,否则取价目表配置的
        HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
        details.setQuotationId(quotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = hlsCusPrjQuotationDetailsMapper.select(details);

        if(CollectionUtils.isNotEmpty(detailsList) && detailsList.size() == 1){
            sourceSheet = detailsList.get(0).getSheets();
        }else{
            sourceSheet = hlsCalcConfig.getSheets();
        }

        //解压压缩过的sheets
        String stringSheets = GzipUtil.atob(sourceSheet);
        String unzipSheets = GzipUtil.unCompress(stringSheets.getBytes("iso-8859-1"));
        String jsonSheets = URLDecoder.decode(unzipSheets,"utf-8");

        //将价目表转成JSONArray
        JSONArray array = JSONArray.parseArray(jsonSheets);
        XSSFWorkbook wb = new XSSFWorkbook();
        readSheets(wb, array);

        //将map数据转成JSONArray
        JSONArray transArray = getTransObject(maps);

        List<HlsPriceListConfigLn> hlsPriceListConfigLns = getPriceListConfigLns(priceList, PRICE_TYPE_SINGLE);
        setCellFormat(transArray, hlsPriceListConfigLns);

        updateSheet(transArray, data, wb, priceList);
        writeBack(wb, array, priceList);

        HlsCusPrjQuotation hlsCusPrjQuotation = JSONObject.parseObject(JSON.toJSONString(data), HlsCusPrjQuotation.class);
        hlsCusPrjQuotation.setQuotationId(quotation.getQuotationId());

        String sheetsArray = encodeURIComponent((JSON.toJSONString(array)));
        String zipSheets = new String(GzipUtil.compress(sheetsArray),"iso-8859-1");
        String compressSheets = GzipUtil.btoa(zipSheets);

        hlsCusPrjQuotation.setSheets(JSON.toJSONString(array));
        hlsCusPrjQuotation.setCompressSheets(compressSheets);

        hlsCusPrjQuotation.setPriceList(quotation.getPriceList());
        hlsCusPrjQuotation.setSourceDocumentCategory(quotation.getSourceDocumentCategory());
        hlsCusPrjQuotation.setLeaseTimes(quotation.getLeaseTimes());
        hlsCusPrjQuotation.setVatRate(quotation.getVatRate());
        hlsCusPrjQuotation.setDataClass(quotation.getDataClass());
        if("CON_CONTRACT".equals(quotation.getSourceDocumentCategory())) {
            hlsCusPrjQuotation.setContractId(quotation.getSourceDocumentId());
        }
        hlsCusPrjQuotation.setSourceDocumentId(quotation.getSourceDocumentId());

        HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setPriceList("TEST");
        calcConfig.setSheets(compressSheets);
        priceListMapper.updateByPrimaryKeySelective(calcConfig);

        if(calcFlag) {
            hlsCalcSaveService.savePrjQuotation(RequestHelper.getCurrentRequest(true), hlsCusPrjQuotation);
        }else{
            if(quotation.getXirr() == null){
                HlsCusPrjQuotationDetails quotationDetails = new HlsCusPrjQuotationDetails();
                if(CollectionUtils.isEmpty(detailsList)) {

                    quotationDetails.setQuotationId(quotation.getQuotationId());
                    quotationDetails.setSheets(compressSheets);
                    hlsCusPrjQuotationDetailsMapper.insertSelective(quotationDetails);
                }else{
                    quotationDetails = detailsList.get(0);
                    quotationDetails.setSheets(compressSheets);
                    hlsCusPrjQuotationDetailsMapper.updateByPrimaryKeySelective(quotationDetails);
                }
            }
        }
    }

    /**
     * Description:
     *
     * @param input
     * @return
     * @see
     */
    public static String encodeURIComponent(String input)
    {
        if (null == input || "".equals(input.trim()))
        {
            return input;
        }

        int l = input.length();
        StringBuilder o = new StringBuilder(l * 3);
        try
        {
            for (int i = 0; i < l; i++ )
            {
                String e = input.substring(i, i + 1);
                if (ALLOWED_CHARS.indexOf(e) == -1)
                {
                    byte[] b = e.getBytes("utf-8");
                    o.append(getHex(b));
                    continue;
                }
                o.append(e);
            }
            return o.toString();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return input;
    }

    private static String getHex(byte buf[])
    {
        StringBuilder o = new StringBuilder(buf.length * 3);
        for (int i = 0; i < buf.length; i++ )
        {
            int n = (int)buf[i] & 0xff;
            o.append("%");
            if (n < 0x10)
            {
                o.append("0");
            }
            o.append(Long.toString(n, 16).toUpperCase());
        }
        return o.toString();
    }

    JSONArray getTransObject(List<Map> maps) {
        JSONArray jsonArray = new JSONArray();
        for (Map map : maps) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(KEY_FIELD, map.get(KEY_FIELD));
            jsonObject.put(KEY_VALUE, map.get(KEY_VALUE));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    /*
     * 将cells里面的数据更新到sheet里面
     * */
    private void updateSheet(JSONArray cells, Map<String, Object> data, XSSFWorkbook wb, String priceList) {
        List<HlsPriceListConfigLn> lns = getPriceListConfigLns(priceList,PRICE_TYPE_SINGLE);
        Map<String, HlsPriceListConfigLn> lnMap = new HashMap<>();
        for (HlsPriceListConfigLn ln : lns) {
            lnMap.put(ln.getColumnName(), ln);
        }
        for (int i = 0; cells != null && i < cells.size(); i++) {
            JSONObject row = cells.getJSONObject(i);
            String fieldName = row.getString(KEY_FIELD);
            Object value = row.get(KEY_VALUE);
            HlsPriceListConfigLn ln = lnMap.get(fieldName.toLowerCase());
            if(ln == null){
                ln = lnMap.get(fieldName.toUpperCase());
            }
            if (ln == null) {
                logger.warn("Can not get target HlsPriceListConfigLn by field: {}", fieldName);
                continue;
            }
            //根据sheet名称获取当前sheet对象
            XSSFSheet sheet = wb.getSheet(ln.getSheetName());

            CellPosition cellPosition = parsePosition(ln.getColumnCode());
            XSSFRow sheetRow = sheet.getRow(cellPosition.getRowIndex());
            if (sheetRow == null) {
                sheetRow = sheet.createRow(cellPosition.getRowIndex());
            }
            XSSFCell rowCell = sheetRow.getCell(cellPosition.getCellIndex());
            if (rowCell == null) {
                rowCell = sheetRow.createCell(cellPosition.getCellIndex());
                logger.info("request cell do not exist:" + ln.getColumnCode() + "-" + fieldName);
            }
            setCellValue(row, value, rowCell);
        }
    }

    class CellPosition {
        private int rowIndex = -1;
        private int cellIndex = -1;

        public int getRowIndex() {
            return rowIndex;
        }

        public void setRowIndex(int rowIndex) {
            this.rowIndex = rowIndex;
        }

        public int getCellIndex() {
            return cellIndex;
        }

        public void setCellIndex(int cellIndex) {
            this.cellIndex = cellIndex;
        }
    }

    /**
     * 行下标从0开始
     * 列下标从0开始
     *
     * @param position
     * @return
     */
    private CellPosition parsePosition(String position) {
        if (StringUtils.isEmpty(position)) {
            throw new RuntimeException("Empty cell position string.");
        }
        String cellString = StringUtils.replaceChars(position, "1234567890", null);
        if (StringUtils.isEmpty(cellString) || !StringUtils.isAlpha(cellString)) {
            throw new RuntimeException("Illegal cellIndex string: " + position);
        }
        String rowString = position.substring(cellString.length());
        if (StringUtils.isEmpty(rowString) || !StringUtils.isNumeric(rowString)) {
            throw new RuntimeException("Illegal rowIndex string: " + position);
        }

        CellPosition cellPosition = new CellPosition();
        char[] chars = cellString.toUpperCase(Locale.CHINA).toCharArray();
        int cellIndex = 0;
        for (int i = chars.length - 1; i >= 0; i--) {
            int value = chars[i] - 'A';
            for (int j = 0; j < chars.length - 1 - i; j++) {
                value *= 26;
            }
            cellIndex += value;
        }
        cellPosition.setRowIndex(Integer.valueOf(rowString) - 1);
        cellPosition.setCellIndex(cellIndex);
        return cellPosition;
    }

    private void setCellValue(JSONObject dataObject, Object value, XSSFCell cell) {
        if(value == null){
            logger.info("value is null "+dataObject.toJSONString());
        }else {
            if (StringUtils.isEmpty(value.toString())) {
                cell.setCellValue("");
                return;
            }
            if (ArraysUtil.contains(DOUBLE_FORMATS, dataObject.getString(KEY_FORMAT))) {
                cell.setCellValue(Double.parseDouble(value.toString()));
            } else if (ArraysUtil.contains(DATE_FORMATS, dataObject.getString(KEY_FORMAT))) {
                if (NumberUtils.isNumber(value.toString())) {
                    cell.setCellValue(HlsBeanRefUtilServiceImpl.parseDate(String.valueOf(Math.round(Double.parseDouble(value.toString())))));
                } else {
                    cell.setCellValue(HlsBeanRefUtilServiceImpl.parseDate(value.toString()));
                }
            } else {
                if (value instanceof Integer) {
                    cell.setCellValue(Integer.valueOf(value.toString()));
                } else if (value instanceof BigDecimal) {
                    cell.setCellValue(Double.valueOf(value.toString()));
                } else {
                    cell.setCellValue(value.toString());
                }
            }
        }
    }

    private List<HlsPriceListConfigLn> getPriceListConfigLns(String priceList, String type){
        HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

        ln.setTableType(type);
        ln.setPriceList(priceList);
        List<HlsPriceListConfigLn> listConfigLns = configLnMapper.selectHlsPriceListConfiglineByPriceList(ln);
        return listConfigLns;
    }

    private List<HlsPriceListConfigHd> getPriceListConfigHd(String priceList, String type) {
        HlsPriceListConfigHd hd = new HlsPriceListConfigHd();
        hd.setPriceList(priceList);
        hd.setTableType(type);
        List<HlsPriceListConfigHd> headers = configHdMapper.select(hd);
        if (CollectionUtils.isEmpty(headers)) {
            return null;
        }
        for(HlsPriceListConfigHd configHd:headers){
            HlsPriceListConfigLn ln = new HlsPriceListConfigLn();

            ln.setConfigHdId(configHd.getConfigHdId());
            configHd.setHlsPriceListConfigLns(configLnMapper.selectHlsPriceListConfiglineByPriceList(ln));
        }
        return headers;
    }

    private void setCellFormat(JSONArray transArray, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
        for (int i = 0; i < transArray.size(); i++) {
            JSONObject jsonObject = transArray.getJSONObject(i);
            setCellFormat(jsonObject, hlsPriceListConfigLns);
        }
    }

    private void setCellFormat(JSONObject object, List<HlsPriceListConfigLn> hlsPriceListConfigLns) {
        for (HlsPriceListConfigLn item : hlsPriceListConfigLns) {
            if (item.getColumnName().toLowerCase().equals(object.getString(KEY_FIELD))) {
                switch (item.getColumnType()) {
                    case "NUMBER":
                        object.put(KEY_FORMAT, DOUBLE_FORMATS[1]);
                        break;
                    case "DATE":
                        object.put(KEY_FORMAT, DATE_FORMATS[0]);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void readSheets(XSSFWorkbook wb, JSONArray array) {
        readSheets(wb, array, false);
    }

    private void readSheets(XSSFWorkbook wb, JSONArray array, boolean valueOnly) {
        for(int k = 0; k < array.size(); k++) {

            JSONObject jsonObject = array.getJSONObject(k);
            XSSFSheet sheet = wb.createSheet(jsonObject.getString("name"));

            JSONArray rows = jsonObject.getJSONArray(KEY_ROWS);
            for (int i = 0; i < rows.size(); i++) {
                JSONObject row = rows.getJSONObject(i);
                int rowIndex = row.getIntValue("index");
                XSSFRow sheetRow = sheet.createRow(rowIndex);
                JSONArray cells = row.getJSONArray(KEY_CELLS);
                for (int cellIndex = 0; cells != null && cellIndex < cells.size(); cellIndex++) {
                    JSONObject cell = cells.getJSONObject(cellIndex);
                    Object value = cell.get(KEY_VALUE);
                    int index = cell.getIntValue(KEY_INDEX);
                    if (index < 0) {
                        continue;
                    }
                    String formula = cell.getString(KEY_FORMULA);
                    if (formula != null && !valueOnly) {
                        XSSFCell rowCell = sheetRow.createCell(index);
                        if (formula.indexOf("#REF!") == -1) {
                            rowCell.setCellFormula(formula);
                        }
                    } else if (value != null) {
                        XSSFCell rowCell = sheetRow.createCell(index);
                        setCellValue(row, value, rowCell);
                    }
                }
            }
        }
    }

    public static String toDate(int days) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = new GregorianCalendar(1900, 0, -1);

        Date d = c.getTime();
        Date _d = DateUtils.addDays(d, days); //42605是距离1900年1月1日的天数
        return simpleDateFormat.format(_d);
    }

    JSONObject getJsonObjectBySheetName(String sheetName,JSONArray jsonArray){
        for(int i = 0; i < jsonArray.size(); i++){
            if(sheetName.equals(jsonArray.getJSONObject(i).getString("name"))){
                return jsonArray.getJSONObject(i);
            }
        }
        return new JSONObject();
    }
    @Override
    public void writeBack(XSSFWorkbook wb, JSONArray array, String priceList) {
        XSSFFormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

        List<HlsPriceListConfigLn> singleLines = getPriceListConfigLns(priceList,PRICE_TYPE_SINGLE);
        List<HlsPriceListConfigHd> hdList = getPriceListConfigHd(priceList, PRICE_TYPE_MULTI_LINE);

        int jsonRowIndex = 0;

        for (int i = 0; i < singleLines.size(); i++) {
            HlsPriceListConfigLn singleLine = singleLines.get(i);
            String columnCode = singleLine.getColumnCode();
            XSSFSheet sheet = wb.getSheet(singleLine.getSheetName());
            JSONObject sheetObject = getJsonObjectBySheetName(singleLine.getSheetName(),array);
            JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);

            CellPosition cellPosition = parsePosition(columnCode);
            XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
            if (row == null) {
                logger.warn("Found empty row at {}", cellPosition.getRowIndex());
                continue;
            }
            XSSFCell cell = row.getCell(cellPosition.getCellIndex());
            if (cell == null) {
                logger.warn("Found empty cell at {}", columnCode);
                continue;
            }
            if (cell.getCellTypeEnum() == CellType.FORMULA&&!("XIRR".equals(singleLine.getColumnName()))) {
                try {
                    cell = evaluator.evaluateInCell(cell);
                }catch (Exception e){
                    logger.info(e.getMessage());
                }
            }
            Object rawValue = getRawValue(cell);

            JSONObject rowObject = getRowObject(rowsObject, cellPosition);
            if (rowObject == null) {
                logger.warn("Found empty rowObject at {}", cellPosition.getRowIndex());
                continue;
            }
            JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
            if (cellsObject == null) {
                logger.warn("Found empty cellsObject at {}", cellPosition.getRowIndex());
                continue;
            }
            boolean foundCell = false;
            for (int cellIndex = 0; cellIndex < cellsObject.size(); cellIndex++) {
                JSONObject cellObject = cellsObject.getJSONObject(cellIndex);
                int intValue = cellObject.getIntValue(KEY_INDEX);
                if (intValue == cellPosition.getCellIndex()) {
                    foundCell = true;
                    if(!("XIRR".equals(singleLine.getColumnName()))){
                        cellObject.put(KEY_VALUE, rawValue);
                    }

                    break;
                }
            }
            if (!foundCell) {
//                没有找到对应的cell，创建一个
                JSONObject cellObject = new JSONObject();
                cellObject.put(KEY_VALUE, rawValue);
                cellObject.put(KEY_INDEX, cellPosition.getCellIndex());
                cellsObject.add(cellObject);
            }
        }

        for(HlsPriceListConfigHd hd:hdList) {
            String multiLineFrom = hd.getMultiLineFrom();
            String multiLineTo = hd.getMultiLineTo();
            List<HlsPriceListConfigLn> configLns = hd.getHlsPriceListConfigLns();
            if (StringUtils.isAnyEmpty(multiLineFrom, multiLineTo)) {
                logger.warn("Found empty multiLineFrom[{}] or multiLineTo[{}]", multiLineFrom, multiLineTo);
                return;
            }
            if (CollectionUtils.isEmpty(configLns)) {
                logger.warn("Found empty HlsPriceListConfigLn of multi-line", multiLineFrom, multiLineTo);
                return;
            }
            Integer from = Integer.valueOf(multiLineFrom);
            Integer to = Integer.valueOf(multiLineTo);
            for (Integer i = from; i <= to; i++) {
                boolean writeData = false;
                for (HlsPriceListConfigLn ln : configLns) {
                    String columnCode = ln.getColumnCode();
                    CellPosition cellPosition = parsePosition(columnCode + i);
                    XSSFSheet sheet = wb.getSheet(ln.getSheetName());
                    JSONObject sheetObject = getJsonObjectBySheetName(ln.getSheetName(), array);
                    JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);

                    XSSFRow row = sheet.getRow(cellPosition.getRowIndex());
                    if (row == null) {
                        logger.warn("Found empty row at {}", cellPosition.getRowIndex());
                        continue;
                    }
                    XSSFCell cell = row.getCell(cellPosition.getCellIndex());
                    if (cell == null) {
                        logger.warn("Found empty cell at {}", columnCode + i);
                        continue;
                    }
                    Object rawValue = getRawValue(cell);


                    JSONObject rowObject = getRowObject(rowsObject, cellPosition);
                    if (rowObject == null) {
                        logger.warn("Found empty rowObject at {}", cellPosition.getRowIndex());
                        continue;
                    }
                    JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
                    if (cellsObject == null) {
                        logger.warn("Found empty cellsObject at {}", cellPosition.getRowIndex());
                        continue;
                    }
                    boolean foundCell = false;
                    for (int cellIndex = 0; cellIndex < cellsObject.size(); cellIndex++) {
                        JSONObject cellObject = cellsObject.getJSONObject(cellIndex);
                        int intValue = cellObject.getIntValue(KEY_INDEX);
                        if (intValue == cellPosition.getCellIndex()) {
                            foundCell = true;
                            writeData = true;
                            cellObject.put(KEY_VALUE, rawValue);
                            break;
                        }
                    }
                    if (!foundCell) {
//                没有找到对应的cell，创建一个
                        JSONObject cellObject = new JSONObject();
                        cellObject.put(KEY_VALUE, rawValue);
                        cellObject.put(KEY_INDEX, cellPosition.getCellIndex());
                        cellsObject.add(cellObject);
                        writeData = true;
                    }
                }
                if (!writeData) {
//               FIXME: 一整行都没有写入数据，考虑一下后面都为空行
                    break;
                }
            }

            evaluator.clearAllCachedResultValues();
            for (int i = 0; i < Integer.valueOf(multiLineTo); i++) {

                for (int k = 0; k < array.size(); k++) {
                    JSONObject sheetObject = array.getJSONObject(k);
                    JSONArray rowsObject = sheetObject.getJSONArray(KEY_ROWS);
                    JSONObject rowObject = getRowObject(rowsObject, i);
                    if (rowObject == null) {
                        continue;
                    }
                    JSONArray cellsObject = rowObject.getJSONArray(KEY_CELLS);
                    if (cellsObject == null) {
                        continue;
                    }
                    XSSFSheet sheet = wb.getSheet(sheetObject.getString("name"));
                    for (int j = 0; j < cellsObject.size(); j++) {
                        XSSFCell cell = sheet.getRow(i).getCell(cellsObject.getJSONObject(j).getIntValue(KEY_INDEX));
                        if (cell != null && cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                            try {
                                CellValue cellValue = evaluator.evaluate(cell);
                                Object rawValue = getRawValue(cellValue);
                                cellsObject.getJSONObject(j).put(KEY_VALUE, rawValue);
                            }catch (Exception e){
                                logger.error(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void saveCalcFront(IRequest iRequest, HlsCusPrjQuotation quotation, String sheets) throws Exception {

        List<Map> mapList = new ArrayList<>();
        HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
        details.setQuotationId(quotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = hlsCusPrjQuotationDetailsMapper.select(details);
        if(CollectionUtils.isEmpty(detailsList)){
            details.setSheets(sheets);
            hlsCusPrjQuotationDetailsService.insertSelective(iRequest,details);
        }else if(detailsList.size() == 1){
            details = detailsList.get(0);
            details.setSheets(sheets);
            hlsCusPrjQuotationDetailsService.updateByPrimaryKeySelective(iRequest,details);
        }else{
            throw new HlsCusException("数据异常!");
        }
        HlsCalcConfig calcConfig = new HlsCalcConfig();
        calcConfig.setSheets(sheets);
        updateQuotaion(quotation,mapList,calcConfig,true);
    }

    @Override
    public String updateHistoryQuotationXirr(IRequest iRequest,HlsCusPrjQuotation hlsCusPrjQuotation) {
        StringBuilder sb = new StringBuilder();
        sb.append("id为");
        List<HlsCusPrjQuotation> list = prjQuotationMapper.selectHistoryQuotationList(hlsCusPrjQuotation);
        for(HlsCusPrjQuotation quotation:list){
            try {
                updateXirr(iRequest, quotation);
            }catch (Exception e){
                sb.append(quotation.getQuotationId());
                sb.append(",");
                logger.info("历史数据xirr计算失败！"+e.getMessage());
            }
        }
        sb.append("  计算失败！");
        return sb.toString();
    }

    private Object getRawValue(XSSFCell cell) {
        Object rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = cell.getNumericCellValue();
                break;
            case STRING:
                rawValue = cell.getStringCellValue();
                break;
            default:
                break;
        }
        return rawValue;
    }

    private String getRawValue(CellValue cell) {
        String rawValue = null;
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                rawValue = String.valueOf(cell.getNumberValue());
                break;
            case STRING:
                rawValue = cell.getStringValue();
                break;
            default:
                break;
        }
        return rawValue;
    }

    private JSONObject getRowObject(JSONArray rowsObject, CellPosition cellPosition) {
        JSONObject rowObject = null;
        for (int j = 0; j < rowsObject.size(); j++) {
            if (rowsObject.getJSONObject(j).getIntValue(KEY_INDEX) == cellPosition.getRowIndex()) {
                rowObject = rowsObject.getJSONObject(j);
            }
        }
        return rowObject;
    }

    private JSONObject getRowObject(JSONArray rowsObject, int rowIndex) {
        JSONObject rowObject = null;
        for (int j = 0; j < rowsObject.size(); j++) {
            if (rowsObject.getJSONObject(j).getIntValue(KEY_INDEX) == rowIndex) {
                rowObject = rowsObject.getJSONObject(j);
            }
        }
        return rowObject;
    }


    public static int getDays(Date end, Date start) {
        Calendar aCalendar = Calendar.getInstance();
        Calendar bCalendar = Calendar.getInstance();
        aCalendar.setTime(end);
        bCalendar.setTime(start);
        int days = 0;
        while (aCalendar.before(bCalendar)) {
            days++;
            aCalendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        if (days == 0) {
            aCalendar.setTime(start);
            bCalendar.setTime(end);
            while (aCalendar.before(bCalendar)) {
                days++;
                aCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        return days;
    }

    /**
     * 获取两个日期相差的月数
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        monthInterval %= 12;
        int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
        return monthsDiff;
    }

    void checkQuotation(HlsCusPrjQuotation quotation) throws HlsCusException {

        //校验首次放款日期和起租日期之间的月数需等于租前息期限
//        int diffMonth = getMonthDiff(quotation.getLeaseStartDate(),quotation.getFirstReleaseDate());
//        int preLeaseTerm = quotation.getPreLeaseTerm().intValue();
//        if(diffMonth != preLeaseTerm){
//            throw new HlsCusException("首次放款日期和起租日期之间的月数不等于租前息期限");
//        }

        //校验基准利率值BASE_RATE
        Double baseRate = quotation.getBaseRate();
        if(baseRate == null){
            throw new HlsCusException("基准利率值不能为空！");
        }
    }

    //报价计算前进行检查
    @Override
    public void quotationReCalcBefore(IRequest request, Long quotationId,Boolean calcFlag) throws Exception {
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        List<Map> mapList = new ArrayList<>();

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = prjQuotationMapper.selectByPrimaryKey(prjQuotation);
        if(prjQuotation.getPriceList() != prjQuotation.getOldPriceList() && prjQuotation.getPriceList().equals(prjQuotation.getOldPriceList()) ){
            hlsCusPrjQuotationDetailsMapper.deleteDetailsById(prjQuotation);
            hlsCusPrjQuotationCashflowMapper.deleteByQuotationId(prjQuotation);

        }
    }
    @Override
    public void quotationReCalc(IRequest request, Long quotationId,Boolean calcFlag) throws Exception {

        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        List<Map> mapList = new ArrayList<>();

        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        prjQuotation.setQuotationId(quotationId);
        prjQuotation = prjQuotationMapper.selectByPrimaryKey(prjQuotation);

        if(prjQuotation.getBaseRate() == null){
            prjQuotation.setBaseRate(0.0466D);
            prjQuotationMapper.updateByPrimaryKeySelective(prjQuotation);
        }

        //校验参数
        if(calcFlag) {
            checkQuotation(prjQuotation);
        }
        Map quotationMap;
        //queryQuotationInfoByQuotationIdMarketing 为通用报价查询
        if (prjQuotation.getSourceDocumentCategory().equals(hlsSourceDocumentCategory) ){
            quotationMap = (Map) prjQuotationMapper.queryQuotationInfoByQuotationIdMarketing(prjQuotation).get(0);
        }else if(prjQuotation.getSourceDocumentCategory().equals(creditSourceDocumentCategory)){
            quotationMap = (Map) prjQuotationMapper.queryQuotationInfoByQuotationIdMarketing(prjQuotation).get(0);
        }else {
            quotationMap = (Map) prjQuotationMapper.queryQuotationInfoByQuotationIdMarketing(prjQuotation).get(0);
        }



        //传入计算字段

        //报价名称
        Map map = new HashMap();
        map.put("field", "description");
        map.put("value", stringDataTran(quotationMap.get("description")));
        mapList.add(map);

        //QUOTATION_NUMBER
        Map map1 = new HashMap();
        map1.put("field", "quotation_number");
        map1.put("value", stringDataTran(quotationMap.get("quotation_number")));
        mapList.add(map1);

        //报价方案
        Map map2 = new HashMap();
        map2.put("field", "price_list_n");
        map2.put("value", stringDataTran(quotationMap.get("price_list_n")));
        mapList.add(map2);

        //报价方式
        Map map3 = new HashMap();
        map3.put("field", "price_list_type_n");
        map3.put("value", stringDataTran(quotationMap.get("price_list_type_n")));
        mapList.add(map3);

        //租赁业务类型
        Map map5 = new HashMap();
        map5.put("field", "business_type_n");
        map5.put("value", stringDataTran(quotationMap.get("business_type_n")));
        mapList.add(map5);

        //币种
        Map map8 = new HashMap();
        map8.put("field", "currency_n");
        map8.put("value", stringDataTran(quotationMap.get("currency_n")));
        mapList.add(map8);

        //利息计算方式
        Map map9 = new HashMap();
        map9.put("field", "payment_method");
        map9.put("value", stringDataTran(quotationMap.get("payment_method")));
        mapList.add(map9);
        Map map10 = new HashMap();
        map10.put("field", "payment_method_n");
        map10.put("value", stringDataTran(quotationMap.get("payment_method_n")));
        mapList.add(map10);

        //汇率
        Map map11 = new HashMap();
        map11.put("field", "exchange_rate");
        map11.put("value", doubleDataTran(quotationMap.get("exchange_rate")));
        mapList.add(map11);

        //融资金额
        Map map12 = new HashMap();
        map12.put("field", "finance_amount");
        map12.put("value", doubleDataTran(quotationMap.get("finance_amount")));
        mapList.add(map12);

        //首付款
        if(quotationMap.get("first_pay") != null &&  doubleDataTran(quotationMap.get("first_pay")) != 0) {
            Map map13 = new HashMap();
            map13.put("field", "first_pay");
            map13.put("value", doubleDataTran(quotationMap.get("first_pay")));
            mapList.add(map13);
        }

        //首付款
        if(quotationMap.get("down_payment") != null && doubleDataTran(quotationMap.get("down_payment")) != 0) {
            Map map14 = new HashMap();
            map14.put("field", "down_payment");
            map14.put("value", doubleDataTran(quotationMap.get("down_payment")));
            mapList.add(map14);
        }

        //首次放款日期
        if(quotationMap.get("first_release_date") != null) {
            Map map18 = new HashMap();
            map18.put("field", "first_release_date");
            map18.put("value", df.format(quotationMap.get("first_release_date")));
            mapList.add(map18);
        }

        //租赁期限
        Map map19 = new HashMap();
        map19.put("field", "lease_term");
        map19.put("value", doubleDataTran(quotationMap.get("lease_term")));
        mapList.add(map19);

        //租金支付频率（计算用）（月）
        Map map20 = new HashMap();
        map20.put("field", "renting_frequency_n");
        map20.put("value", stringDataTran(quotationMap.get("renting_frequency_n")));
        mapList.add(map20);

        //预计放款日
        if(quotationMap.get("lease_start_date") != null) {
            Map map21 = new HashMap();
            map21.put("field", "lease_start_date");
            map21.put("value", df.format(quotationMap.get("lease_start_date")));
            mapList.add(map21);
        }

        //租赁期届满日
        if(quotationMap.get("lease_end_date") != null) {
            Map map22 = new HashMap();
            map22.put("field", "lease_end_date");
            map22.put("value", df.format(quotationMap.get("lease_end_date")));
            mapList.add(map22);
        }

        //是否含有租前期
        Map map26 = new HashMap();
        map26.put("field", "pre_lease_flag_n");
        map26.put("value", stringDataTran(quotationMap.get("pre_lease_flag_n")));
        mapList.add(map26);

        //租前期期限（月）
        Map map27 = new HashMap();
        map27.put("field", "pre_lease_term");
        map27.put("value", doubleDataTran(quotationMap.get("pre_lease_term")));
        mapList.add(map27);

        //是否含有宽限期
        Map map28 = new HashMap();
        map28.put("field", "grace_flag_n");
        map28.put("value", stringDataTran(quotationMap.get("grace_flag_n")));
        mapList.add(map28);

        //宽限期期限（月）
        Map map29 = new HashMap();
        map29.put("field", "grace_lease_term");
        map29.put("value", doubleDataTran(quotationMap.get("grace_lease_term")));
        mapList.add(map29);

        //计算日类型
        Map map33 = new HashMap();
        map33.put("field", "calc_date_type_n");
        map33.put("value", stringDataTran(quotationMap.get("calc_date_type_n")));
        mapList.add(map33);

        //租金支付日（计算用）
        Map map34 = new HashMap();
        map34.put("field", "rental_pay_date");
        map34.put("value", quotationMap.get("rental_pay_date"));
        mapList.add(map34);

        //租金偿还方式
        Map map35 = new HashMap();
        map35.put("field", "pay_type_n");
        map35.put("value", quotationMap.get("pay_type_n"));
        mapList.add(map35);

        //利率类型
        Map map36 = new HashMap();
        map36.put("field", "int_rate_type_n");
        map36.put("value", stringDataTran(quotationMap.get("int_rate_type_n")));
        mapList.add(map36);

        //利率浮动类型
        Map map37 = new HashMap();
        map37.put("field", "float_type");
        map37.put("value", stringDataTran(quotationMap.get("float_type")));
        mapList.add(map37);

        //利率日期参考系
        Map map38 = new HashMap();
        map38.put("field", "lpr_ref_day_n");
        map38.put("value", stringDataTran(quotationMap.get("lpr_ref_day_n")));
        mapList.add(map38);

        //LPR指定日期
        if(quotationMap.get("lpr_name_date") != null) {
            Map map39 = new HashMap();
            map39.put("field", "lpr_name_date");
            map39.put("value", quotationMap.get("lpr_name_date"));
            mapList.add(map39);
        }

        //基准利率标准
        Map map40 = new HashMap();
        map40.put("field", "base_rate_type_n");
        map40.put("value", stringDataTran(quotationMap.get("base_rate_type_n")));
        mapList.add(map40);

        //基准利率值
        Map map41 = new HashMap();
        map41.put("field", "base_rate");
        map41.put("value", doubleDataTran(quotationMap.get("base_rate")));
        mapList.add(map41);

        //浮动值（BP）
        Map map42 = new HashMap();
        map42.put("field", "floating_way_rate");
        map42.put("value", doubleDataTran(quotationMap.get("floating_way_rate")));
        mapList.add(map42);

        //年利率(%)
        Map map43 = new HashMap();
        map43.put("field", "int_rate");
        map43.put("value", doubleDataTran(quotationMap.get("int_rate")));
        mapList.add(map43);

        //是否含税利率
        Map map44 = new HashMap();
        map44.put("field", "vatable_rate_flag_n");
        map44.put("value", stringDataTran(quotationMap.get("vatable_rate_flag_n")));
        mapList.add(map44);

        //是否含税本金
        Map map45 = new HashMap();
        map45.put("field", "vatable_prin_flag_n");
        map45.put("value", stringDataTran(quotationMap.get("vatable_prin_flag_n")));
        mapList.add(map45);

        //税务认定结构
        Map map46 = new HashMap();
        map46.put("field", "tax_structure_n");
        map46.put("value", stringDataTran(quotationMap.get("tax_structure_n")));
        mapList.add(map46);

        //税种
        Map map47 = new HashMap();
        map47.put("field", "tax_type_code");
        map47.put("value", stringDataTran(quotationMap.get("tax_type_code")));
        mapList.add(map47);

        //调息基准日期
        Map map48 = new HashMap();
        map48.put("field", "lpr_base_date_n");
        map48.put("value", stringDataTran(quotationMap.get("lpr_base_date_n")));
        mapList.add(map48);

        //调息指定日期
        Map map49 = new HashMap();
        map49.put("field", "lpr_fixed_date");
        map49.put("value", stringDataTran(quotationMap.get("lpr_fixed_date")));
        mapList.add(map49);

        //调息期限
        Map map50 = new HashMap();
        map50.put("field", "lpr_adjustment_term");
        map50.put("value", stringDataTran(quotationMap.get("lpr_adjustment_term")));
        mapList.add(map50);

        //调息期次
        Map map51 = new HashMap();
        map51.put("field", "lpr_adjustment_period_n");
        map51.put("value", stringDataTran(quotationMap.get("lpr_adjustment_period_n")));
        mapList.add(map51);

        //资产管理费类型
        Map map52 = new HashMap();
        map52.put("field", "asset_mgt_fee_type_n");
        map52.put("value", stringDataTran(quotationMap.get("asset_mgt_fee_type_n")));
        mapList.add(map52);

        //利息摊销方法
        Map map53 = new HashMap();
        map53.put("field", "interest_amortization_method_n");
        map53.put("value", stringDataTran(quotationMap.get("interest_amortization_method_n")));
        mapList.add(map53);

        //租金取整
        Map map54 = new HashMap();
        map54.put("field", "rental_rounding_type_n");
        map54.put("value", stringDataTran(quotationMap.get("rental_rounding_type_n")));
        mapList.add(map54);

        //留购价
        Map map55 = new HashMap();
        map55.put("field", "residual_value");
        map55.put("value", doubleDataTran(quotationMap.get("residual_value")));
        mapList.add(map55);

        //支付期数
        Map map56 = new HashMap();
        map56.put("field", "lease_times");
        map56.put("value", doubleDataTran(quotationMap.get("lease_times")));
        mapList.add(map56);

        //XIRR(含票据)
        Map map57 = new HashMap();
        map57.put("field", "xirr_paynote");
        map57.put("value", doubleDataTran(quotationMap.get("xirr_paynote")));
        mapList.add(map57);

        // IRR(含票据)
        Map map58 = new HashMap();
        map58.put("field", "irr_after_tax");
        map58.put("value", doubleDataTran(quotationMap.get("irr_after_tax")));
        mapList.add(map58);

        // 租赁物价款
        Map map59 = new HashMap();
        map59.put("field", "lease_item_amount");
        map59.put("value", doubleDataTran(quotationMap.get("lease_item_amount")));
        mapList.add(map59);

        //是否保税租赁
        Map map60 = new HashMap();
        map60.put("field", "bonded_lease_flag_n");
        map60.put("value", stringDataTran(quotationMap.get("bonded_lease_flag_n")));
        mapList.add(map60);

        //税
        Map map61 = new HashMap();
        map61.put("field", "vat_rate");
        map61.put("value", doubleDataTran(quotationMap.get("vat_rate")));
        mapList.add(map61);
        //保证金处理方式
        Map map62 = new HashMap();
        map62.put("field", "deposit_deduction_n");
        map62.put("value", stringDataTran(quotationMap.get("deposit_deduction_n")));
        mapList.add(map62);
        //税
        Map map63 = new HashMap();
        map63.put("field", "deposit_deduction");
        map63.put("value", stringDataTran(quotationMap.get("deposit_deduction")));
        mapList.add(map63);
        //现金比例
        Map map64 = new HashMap();
        map64.put("field", "cash_ratio");
        map64.put("value", doubleDataTran(quotationMap.get("cash_ratio")));
        mapList.add(map64);
        //承兑汇票比例
        Map map65 = new HashMap();
        map65.put("field", "apt_bill_ratio");
        map65.put("value", doubleDataTran(quotationMap.get("apt_bill_ratio")));
        mapList.add(map65);
        //信用证比例
        Map map66 = new HashMap();
        map66.put("field", "lf_credit_ratio");
        map66.put("value", doubleDataTran(quotationMap.get("lf_credit_ratio")));
        mapList.add(map66);

        //租金偿还方式
        Map map67 = new HashMap();
        map67.put("field", "pay_type");
        map67.put("value", quotationMap.get("pay_type"));
        mapList.add(map67);

        //保证金
        Map map68 = new HashMap();
        map68.put("field", "deposit");
        map68.put("value", doubleDataTran(prjQuotation.getDeposit()));
        mapList.add(map68);
        //首付款比例
        Map map69 = new HashMap();
        map69.put("field", "down_payment_ratio");
        map69.put("value", doubleDataTran(prjQuotation.getDownPaymentRatio()));
        mapList.add(map69);
        //保证金比例
        Map map70 = new HashMap();
        map70.put("field", "deposit_ratio");
        map70.put("value", doubleDataTran(prjQuotation.getDepositRatio()));
        mapList.add(map70);
        //手续费比例
        Map map71 = new HashMap();
        map71.put("field", "lease_charge_ratio");
        map71.put("value", doubleDataTran(prjQuotation.getLeaseChargeRatio()));
        mapList.add(map71);

        //宽限期(年)
        Map map30 = new HashMap();
        map30.put("field", "grace_term");
        map30.put("value", doubleDataTran(prjQuotation.getGraceTerm()));
        mapList.add(map30);
        //宽限期利率(年)
        Map map31 = new HashMap();
        map31.put("field", "grace_period_interest_rate");
        map31.put("value", doubleDataTran(prjQuotation.getGracePeriodInterestRate()));
        mapList.add(map31);
        //宽限期收取间隔月份
        Map map32 = new HashMap();
        map32.put("field", "grace_frequency");
        map32.put("value", doubleDataTran(prjQuotation.getGraceFrequency()));
        mapList.add(map32);
        //手续费
        Map map72 = new HashMap();
        map72.put("field", "lease_charge");
        map72.put("value", doubleDataTran(prjQuotation.getLeaseCharge()));
        mapList.add(map72);
        //资产余值
        Map map73 = new HashMap();
        map73.put("field", "assets_surplus_value");
        map73.put("value", doubleDataTran(prjQuotation.getAssetsSurplusValue()));
        mapList.add(map73);
        //其他收入
        Map map74 = new HashMap();
        map74.put("field", "other_income");
        map74.put("value", doubleDataTran(prjQuotation.getOtherIncome()));
        mapList.add(map74);
        //其他支出
        Map map75 = new HashMap();
        map75.put("field", "other_expenses");
        map75.put("value", doubleDataTran(prjQuotation.getOtherExpenses()));
        mapList.add(map75);
        //还款频率
        Map map76 = new HashMap();
        map76.put("field", "annual_pay_times_n");
        map76.put("value", stringDataTran(quotationMap.get("annual_pay_times_n")));
        mapList.add(map76);
        //租赁类型首付款比例
        Map map77 = new HashMap();
        map77.put("field", "down_payment_ratio");
        map77.put("value", doubleDataTran(quotationMap.get("down_payment_ratio")));
        mapList.add(map77);
        //租赁期限（月）
        Map map78 = new HashMap();
        map78.put("field", "lease_term_m");
        map78.put("value", doubleDataTran(prjQuotation.getLeaseTermM()));
        mapList.add(map78);

        //名义价格
        Map map79 = new HashMap();
        map79.put("field", "residual_value");
        map79.put("value", doubleDataTran(quotationMap.get("residual_value")));
        mapList.add(map79);
        //租赁业务类型
        Map map80 = new HashMap();
        map80.put("field", "business_type");
        map80.put("value", stringDataTran(quotationMap.get("business_type")));
        mapList.add(map80);

        if(quotationMap.get("price_list_n").toString().contains("零售")) {
            Map businessTypeNMap = new HashMap();
            businessTypeNMap.put("field", "business_type_n");
            String businessTypeN = "";
            if ("LEASE".equalsIgnoreCase(stringDataTran(quotationMap.get("business_type")))) {
                businessTypeN = "直接租赁";
            } else {
                businessTypeN = "售后回租";
            }
            businessTypeNMap.put("value", businessTypeN);
            mapList.add(businessTypeNMap);
        }

        //银承支付(月)
        Map acceptanceTermNMap =new HashMap();
        acceptanceTermNMap.put("field", "acceptance_term_n");
        double acceptanceTermN=0;
        switch (stringDataTran(prjQuotation.getAcceptanceTerm())){
            case "THREE":
                acceptanceTermN=3;break;
            case "SIX":
                acceptanceTermN=6;break;
            case "NINE":
                acceptanceTermN=9;break;
            case "TWELVE":
                acceptanceTermN=12;
        }
        acceptanceTermNMap.put("value", acceptanceTermN);
        mapList.add(acceptanceTermNMap);

        //银承手续费比例
        Map bankLeaseChargeRatioMap =new HashMap();
        bankLeaseChargeRatioMap.put("field", "bank_lease_charge_ratio");
        bankLeaseChargeRatioMap.put("value", doubleDataTran(quotationMap.get("bank_lease_charge_ratio")));
        mapList.add(bankLeaseChargeRatioMap);

        //银承保证金比例
        Map bankDepositRatioMap =new HashMap();
        bankDepositRatioMap.put("field", "bank_deposit_ratio");
        bankDepositRatioMap.put("value", doubleDataTran(quotationMap.get("bank_deposit_ratio")));
        mapList.add(bankDepositRatioMap);


        //投放日
        if(request.getAttribute("leaseStartDate")!=null){
            Map leaseStartDateMap = new HashMap();
            leaseStartDateMap.put("field", "lease_start_date");
            leaseStartDateMap.put("value", request.getAttribute("leaseStartDate").toString());
            mapList.add(leaseStartDateMap);
        }

        //固定日
        if(request.getAttribute("inceptFixDay")!=null){
            Map inceptFixDayMap = new HashMap();
            inceptFixDayMap.put("field", "incept_fix_day");
            inceptFixDayMap.put("value", request.getAttribute("inceptFixDay").toString());
            mapList.add(inceptFixDayMap);
        }


        //融资额
        Map financeAmountMap = new HashMap();
        financeAmountMap.put("field", "FINANCE_AMOUNT");
        financeAmountMap.put("value", doubleDataTran(quotationMap.get("finance_amount")));
        mapList.add(financeAmountMap);

        //支付方式
        Map payTypeMap = new HashMap();
        payTypeMap.put("field", "pay_type");
        payTypeMap.put("value", stringDataTran(prjQuotation.getPaymentType()));
        mapList.add(payTypeMap);
        //支付方式
        Map payTypeNMap = new HashMap();
        payTypeNMap.put("field", "payment_type_n");
        String payTypeN="";
        if("CASH".equals(stringDataTran(prjQuotation.getPaymentType()))){
            payTypeN="现金支付";
        }else{
            payTypeN="银承支付";
        }
        payTypeNMap.put("value", payTypeN);
        mapList.add(payTypeNMap);

        //是否返利
        Map repayFlagMap = new HashMap();
        repayFlagMap.put("field","repay_flag");
        repayFlagMap.put("value", stringDataTran(prjQuotation.getRepayFlag()));
        mapList.add(repayFlagMap);
        //是否返利N
        Map repayFlagNMap = new HashMap();
        repayFlagNMap.put("field","repay_flag_n");
        String repayFlagN="";
        if("Y".equals(stringDataTran(prjQuotation.getRepayFlag()))){
            repayFlagN="是";
        }else if("N".equals(stringDataTran(prjQuotation.getRepayFlag()))){
            repayFlagN="否";
        }
        repayFlagNMap.put("value", repayFlagN);
        mapList.add(repayFlagNMap);

        //调息规则
        if(request.getAttribute("floatingRangeMethodN")!=null){
            Map floatingRangeMethodNMap = new HashMap();
            floatingRangeMethodNMap.put("field", "floating_range_method_n");
            floatingRangeMethodNMap.put("value", request.getAttribute("floatingRangeMethodN").toString());
            mapList.add(floatingRangeMethodNMap);
        }

        //宽限期类型
        Map graceTypeNMap = new HashMap();
        graceTypeNMap.put("field","grace_type_n");
        String graceTypeN="";
        switch (stringDataTran(prjQuotation.getGraceType())){
            case "10": graceTypeN="无宽限";break;
            case "20": graceTypeN="前一期";break;
            case "30": graceTypeN="前二期";break;
            case "40": graceTypeN="前三期";break;
            case "50": graceTypeN="宽限期自定义";break;
            case "41": graceTypeN="前四期";break;
            case "42": graceTypeN="前五期";break;
            case "43": graceTypeN="前六期";break;
        }
        graceTypeNMap.put("value", graceTypeN);
        mapList.add(graceTypeNMap);

        //宽限期月份一
        Map graceMonthOneMap = new HashMap();
        graceMonthOneMap.put("field","grace_month_one");
        graceMonthOneMap.put("value", stringDataTran(prjQuotation.getGraceMonthOne()));
        mapList.add(graceMonthOneMap);

        //宽限期月份二
        Map graceMonthTwoMap = new HashMap();
        graceMonthTwoMap.put("field","grace_month_one");
        graceMonthTwoMap.put("value", stringDataTran(prjQuotation.getGraceMonthTwo()));
        mapList.add(graceMonthTwoMap);

        //宽限期月份三
        Map graceMonthThreeMap = new HashMap();
        graceMonthThreeMap.put("field","grace_month_one");
        graceMonthThreeMap.put("value", stringDataTran(prjQuotation.getGraceMonthThree()));
        mapList.add(graceMonthThreeMap);

        //逾期宽限类型
        if(request.getAttribute("exemptPenaltyIntN")!=null){
            Map graceFlagMap = new HashMap();
            graceFlagMap.put("field", "grace_flag_n");
            graceFlagMap.put("value", request.getAttribute("exemptPenaltyIntN").toString());
            mapList.add(graceFlagMap);
        }

        //批复利率中转
        if(request.getAttribute("intRateReplyTmp")!=null){
            Map graceFlagMap = new HashMap();
            graceFlagMap.put("field", "int_rate_reply_tmp");
            graceFlagMap.put("value", request.getAttribute("intRateReplyTmp").toString());
            mapList.add(graceFlagMap);
        }
        //批复手续费中转
        if(request.getAttribute("leaseChargeRatioReplyTmp")!=null){
            Map graceFlagMap = new HashMap();
            graceFlagMap.put("field", "lease_charge_ratio_reply_tmp");
            graceFlagMap.put("value", request.getAttribute("leaseChargeRatioReplyTmp").toString());
            mapList.add(graceFlagMap);
        }

        //保证金抵扣方式
        if(request.getAttribute("marginDeductionMethodN")!=null){
            Map marginDeductionMethodMap = new HashMap();
            marginDeductionMethodMap.put("field", "deposit_deduction_n");
            marginDeductionMethodMap.put("value", request.getAttribute("marginDeductionMethodN").toString());
            mapList.add(marginDeductionMethodMap);
        }

        //设备款
        Map leaseItemAmount = new HashMap();
        leaseItemAmount.put("field", "lease_item_amount");
        leaseItemAmount.put("value", (doubleDataTran(quotationMap.get("finance_amount"))+doubleDataTran(quotationMap.get("down_payment"))));
        mapList.add(leaseItemAmount);

        //第一期租金支付日
        if(prjQuotation.getFirstRentalPaymentDate() != null) {
            Map firstRentalPaymentDate = new HashMap();
            firstRentalPaymentDate.put("field", "first_rental_payment_date");
            firstRentalPaymentDate.put("value", df.format(prjQuotation.getFirstRentalPaymentDate()));
            mapList.add(firstRentalPaymentDate);
        }else{
            Map firstRentalPaymentDate = new HashMap();
            firstRentalPaymentDate.put("field", "first_rental_payment_date");
            firstRentalPaymentDate.put("value", "");
            mapList.add(firstRentalPaymentDate);
        }

        Map firstRental = new HashMap();
        firstRental.put("field", "first_rental");
        firstRental.put("value", doubleDataTran(prjQuotation.getFirstRental()));
        mapList.add(firstRental);

        HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
        hlsCalcConfig.setPriceList(quotationMap.get("price_list").toString());
        hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);

        updateQuotaion(prjQuotation, mapList, hlsCalcConfig,calcFlag);


        if(calcFlag) {
            updateXirr(request, prjQuotation);
        }
        //更新项目原报价字段
        HlsCusPrjQuotation cusPrjQuotation = new HlsCusPrjQuotation();
        cusPrjQuotation = prjQuotationMapper.selectByPrimaryKey(prjQuotation);
        cusPrjQuotation.setOldPriceList(prjQuotation.getPriceList());
        prjQuotationMapper.updateByPrimaryKey(cusPrjQuotation);
    }

    @Override
    public List<HlsCusPrjQuotation> queryQuotationInfo(HlsCusPrjQuotation prjQuotation) {
        List<HlsCusPrjQuotation> list = new ArrayList<>();
        if (prjQuotation.getSourceDocumentCategory().equals("CON_CONTRACT")) {
            list = prjQuotationMapper.queryConQuotationInfo(prjQuotation);
        } else {
            list = prjQuotationMapper.queryQuotationInfo(prjQuotation);
        }
        return list;
    }

    @Override
    public List<HlsCusPrjQuotation> queryQuotationInfoList(HlsCusPrjQuotation prjQuotation) {
        List<HlsCusPrjQuotation> list = new ArrayList<>();
        list = prjQuotationMapper.queryQuotationInfoList(prjQuotation);
        return list;
    }

    public Double doubleDataTran(Object var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = Double.valueOf(var.toString());
        }
        return result;
    }

    public Double doubleDataTran(Double var) {
        double result;
        if (var == null) {
            result = 0D;
        } else {
            result = var;
        }
        return result;
    }

    public String stringDataTran(Object var) {
        String result;
        if (var == null) {
            result = "";
        } else {
            result = var.toString();
        }
        return result;
    }

    /*
     * ①首先将该租金计划中所有的现金流删除掉
     * ②按照头信息计算现金流，注意：此时的融资金额取【剩余本金】，利息计算开始日期使用【变更开始日期】
     * ③计算完成之后得到的现金流中，将原始合同中已核销的现金流加入当前现金流中，然后重新进行排序
     */
    @Override
    public void quotationChangeReCalc(IRequest request, HlsCusPrjProjectInfo hlsCusPrjProjectInfo) throws Exception {
        Long times = 0L;
        Double diffAmount = 0D;
        Double depositAmount = 0D;
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusPrjProjectInfo.getHlsCusPrjProject().getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(request, hlsCusPrjProject);

        List<Map> mapList = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        HlsCusPrjQuotation prjQuotation = hlsCusPrjProjectInfo.getHlsCusPrjQuotation();
        prjQuotation.setContractId(hlsCusPrjProjectInfo.getHlsCusPrjProject().getContractId());

        //变更合同
        HlsCusConContract hlsCusConContractNew = new HlsCusConContract();
        hlsCusConContractNew.setContractId(hlsCusPrjProjectInfo.getHlsCusPrjProject().getContractId());
        hlsCusConContractNew = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNew);
        //原始合同
        HlsCusConContract hlsCusConContractNormal = new HlsCusConContract();
        hlsCusConContractNormal.setContractId(hlsCusConContractNew.getRefContractId());
        hlsCusConContractNormal = hlsCusConContractMapper.selectByPrimaryKey(hlsCusConContractNormal);
        //原始报价
        HlsCusPrjQuotation prjQuotationNormal = new HlsCusPrjQuotation();
        prjQuotationNormal.setSourceDocumentId(hlsCusConContractNormal.getContractId());
        prjQuotationNormal.setSourceDocumentCategory(conSourceDocumentCategory);
        List<HlsCusPrjQuotation> prjQuotationNormalList = prjQuotationMapper.select(prjQuotationNormal);
        //剩余本金
        HlsCusConContractCashflow hlsCusConContractCashflow = new HlsCusConContractCashflow();
        hlsCusConContractCashflow.setContractId(hlsCusConContractNormal.getContractId());
        List<HlsCusConContractCashflow> list = hlsCusConContractCashflowMapper.queryPreRepaymentChangeInfo(hlsCusConContractCashflow);
        //变更明细
        HlsCusChangeReqInfo hlsCusChangeReqInfo = new HlsCusChangeReqInfo();
        hlsCusChangeReqInfo.setChangeReqId(hlsCusConContractNormal.getChangeReqId());
        hlsCusChangeReqInfo = hlsCusChangeReqInfoMapper.selectByPrimaryKey(hlsCusChangeReqInfo);
        HlsCusCshWriteOff hlsCusCshWriteOff = new HlsCusCshWriteOff();
        hlsCusCshWriteOff.setContractId(hlsCusConContractNormal.getContractId());
        hlsCusCshWriteOff.setCfItem(DUE_AMOUNT_CF_ITEM);
        List<HlsCusCshWriteOff> hlsCusCshWriteOffList = hlsCusCshWriteOffMapper.select(hlsCusCshWriteOff);
        Long writeOffLeaseTimes = hlsCusCshWriteOffList.size() * getRentingFrequency(prjQuotationNormalList.get(0).getRentingFrequency());
        Double leaseTerm = prjQuotation.getLeaseTerm() - writeOffLeaseTimes.doubleValue();
        Double leaseTimes = Math.ceil(leaseTerm / getRentingFrequency(prjQuotation.getRentingFrequency()));
        if ("COMBINATION_QUOTATION".equals(prjQuotation.getPriceList())) {
            hlsCusPrjQuotationSubsectionService.calcPrjQuotation(request, prjQuotation, "Y", list.get(0).getResidualPrincipal(), leaseTerm);
        } else if ("LEVEL_PMT_TAX_INC_CT".equals(prjQuotation.getPriceList()) || "LEVEL_RATE_TAX_INC_CT".equals(prjQuotation.getPriceList())) {
            //传入计算字段
            //剩余本金
            Map map = new HashMap();
            map.put("field", "lease_item_amount");
            map.put("value", list.get(0).getResidualPrincipal());
            mapList.add(map);
            //合同利率
            Map map1 = new HashMap();
            map1.put("field", "vat_rate");
            map1.put("value", prjQuotation.getVatRate());
            mapList.add(map1);
            //手续费收取频率
            Map map2 = new HashMap();
            map2.put("field", "lease_charging_frequency");
            map2.put("value", stringDataTran(prjQuotation.getLeaseChargingFrequency()));
            mapList.add(map2);
            //服务费收取频率
            Map map3 = new HashMap();
            map3.put("field", "adv_servicing_frequency");
            map3.put("value", stringDataTran(prjQuotation.getAdvServicingFrequency()));
            mapList.add(map3);
            //租赁期限(月)
            Map map5 = new HashMap();
            map5.put("field", "lease_term");
            map5.put("value", leaseTerm);
            mapList.add(map5);
            //变更第一期租金支付日
            Map map6 = new HashMap();
            map6.put("field", "first_rental_pay_change_date");
            map6.put("value", df.format(prjQuotation.getFirstRentalPayChangeDate()));
            mapList.add(map6);
            //变更开始日
            Map map7 = new HashMap();
            map7.put("field", "change_date");
            map7.put("value", df.format(prjQuotation.getChangeDate()));
            mapList.add(map7);
            //租赁期数
            Map map8 = new HashMap();
            map8.put("field", "lease_times");
            map8.put("value", leaseTimes);
            mapList.add(map8);
            //留购价款
            Map map11 = new HashMap();
            map11.put("field", "residual_value");
            map11.put("value", prjQuotation.getResidualValue());
            mapList.add(map11);
            //租赁年利率
            Map map12 = new HashMap();
            map12.put("field", "int_rate");
            map12.put("value", prjQuotation.getIntRate());
            mapList.add(map12);
            //还租频率
            Map map13 = new HashMap();
            map13.put("field", "renting_frequency");
            map13.put("value", prjQuotation.getRentingFrequency());
            mapList.add(map13);
            //还租方式
            Map map14 = new HashMap();
            map14.put("field", "renting_method");
            map14.put("value", prjQuotation.getRentingMethod());
            mapList.add(map14);
//首付款
            Map map16 = new HashMap();
            map16.put("field", "down_payment");
            map16.put("value", doubleDataTran(prjQuotation.getDownPayment()));
            mapList.add(map16);
//手续费
            Map map18 = new HashMap();
            map18.put("field", "lease_charge");
            map18.put("value", doubleDataTran(prjQuotation.getLeaseCharge()));
            mapList.add(map18);
            //保证金
            Map map19 = new HashMap();
            map19.put("field", "deposit");
            map19.put("value", doubleDataTran(prjQuotation.getDeposit()));
            mapList.add(map19);
            //咨询服务费
            Map map20 = new HashMap();
            map20.put("field", "adv_service_fee");
            map20.put("value", doubleDataTran(prjQuotation.getAdvServiceFee()));
            mapList.add(map20);
//保证金退还方式
            Map map25 = new HashMap();
            map25.put("field", "deposit_return_method");
            map25.put("value", prjQuotation.getDepositReturnMethod());
            mapList.add(map25);
//        Map map26 = new HashMap();
//        map26.put("field", "first_rental_payment_date");//第一期租金支付日
//        map26.put("value", df.format(prjQuotation.getFirstRentalPaymentDate()));
//        mapList.add(map26);
//计息方式
            Map map27 = new HashMap();
            map27.put("field", "payment_method");
            map27.put("value", prjQuotation.getPaymentMethod());
            mapList.add(map27);
            Map map26 = new HashMap();
            map26.put("field", "payment_method_n");
            map26.put("value", prjQuotation.getPaymentMethodN());
            mapList.add(map26);
            //还租日
            Map map28 = new HashMap();
            map28.put("field", "payment_day");
            map28.put("value", prjQuotation.getPaymentDay());
            mapList.add(map28);
            //保险计算方式
            Map map29 = new HashMap();
            map29.put("field", "insurance_fee_method");
            map29.put("value", prjQuotation.getInsuranceFeeMethod());
            mapList.add(map29);
            //保险费
            Map map30 = new HashMap();
            map30.put("field", "insurance_fee");
            map30.put("value", doubleDataTran(prjQuotation.getInsuranceFee()));
            mapList.add(map30);
            //期末余值
            Map map33 = new HashMap();
            map33.put("field", "assets_surplus_value");
            map33.put("value", doubleDataTran(prjQuotation.getAssetsSurplusValue()));
            mapList.add(map33);
//        Map map34 = new HashMap();
//        map34.put("field", "lease_start_date");//起租日df.format(prjQuotation.getLeaseStartDate())
//        map34.put("value", df.format(prjQuotation.getLeaseStartDate()));
//        mapList.add(map34);
            HlsCalcConfig hlsCalcConfig = new HlsCalcConfig();
            hlsCalcConfig.setPriceList(prjQuotation.getPriceList());
            hlsCalcConfig = priceListMapper.selectByPrimaryKey(hlsCalcConfig);
            updateQuotaion(prjQuotation, mapList, hlsCalcConfig,true);
        }
//        //计算后现金流 变更直接插入con_contract_cashflow
        HlsCusConContractCashflow calcHlsCusConContractCashflow = new HlsCusConContractCashflow();
        calcHlsCusConContractCashflow.setContractId(prjQuotation.getContractId());
        List<HlsCusConContractCashflow> calcHlsCusConContractCashflowList = hlsCusConContractCashflowMapper.select(calcHlsCusConContractCashflow);
        //变更前已核销的现金流
        HlsCusConContractCashflow changeBeforeContractCashflow = new HlsCusConContractCashflow();
        changeBeforeContractCashflow.setContractId(hlsCusConContractNormal.getContractId());
        List<HlsCusConContractCashflow> changeBeforeContractCashflowList = hlsCusConContractCashflowMapper.select(changeBeforeContractCashflow);

        if (changeBeforeContractCashflowList.size() > 0) {
            for (int i = 0; i < changeBeforeContractCashflowList.size(); i++) {
                if (changeBeforeContractCashflowList.get(i).getWriteOffFlag().equals("PARTIAL") || changeBeforeContractCashflowList.get(i).getWriteOffFlag().equals("FULL")) {
                    HlsCusConContractCashflow dto = changeBeforeContractCashflowList.get(i);
                    dto.setCashflowId(null);
                    dto.setContractId(hlsCusConContractNew.getContractId());
                    hlsCusConContractCashflowMapper.insertSelective(dto);
                }
            }
        }
        //处理数据cf_item=51,57,0
        HlsCusConContractCashflow resultQuotationCashflowLeaseItem = new HlsCusConContractCashflow();
        resultQuotationCashflowLeaseItem.setContractId(prjQuotation.getContractId());
        resultQuotationCashflowLeaseItem.setCfItem(0L);
        resultQuotationCashflowLeaseItem.setCfType(0L);
        resultQuotationCashflowLeaseItem.setWriteOffFlag("NOT");
        List<HlsCusConContractCashflow> resultQuotationCashflowLeaseItemList = hlsCusConContractCashflowMapper.select(resultQuotationCashflowLeaseItem);
        hlsCusConContractCashflowService.batchDelete(resultQuotationCashflowLeaseItemList);
        Long[] cfItemList = new Long[]{51L, 57L};
        for (int k = 0; k < cfItemList.length; k++) {
            HlsCusConContractCashflow resultQuotationCashflowTempDeposit = new HlsCusConContractCashflow();
            resultQuotationCashflowTempDeposit.setCfItem(cfItemList[k]);
            resultQuotationCashflowTempDeposit.setContractId(prjQuotation.getContractId());
            List<HlsCusConContractCashflow> resultQuotationCashflowTempDepositTemp = new ArrayList<>();
            List<HlsCusConContractCashflow> resultQuotationCashflowTempDepositList = hlsCusConContractCashflowMapper.select(resultQuotationCashflowTempDeposit);
            if (resultQuotationCashflowTempDepositList.size() > 1) {
                for (int i = 0; i < resultQuotationCashflowTempDepositList.size(); i++) {
                    if (resultQuotationCashflowTempDepositList.get(i).getWriteOffFlag().equals("PARTIAL") || resultQuotationCashflowTempDepositList.get(i).getWriteOffFlag().equals("FULL")) {
                        depositAmount += resultQuotationCashflowTempDepositList.get(i).getDueAmount();
                        // resultQuotationCashflowTempDepositTemp=resultQuotationCashflowTempDepositList.get(i);
                        resultQuotationCashflowTempDepositTemp.add(resultQuotationCashflowTempDepositList.get(i));
                    }
                }
            }
            diffAmount = prjQuotation.getDeposit() - depositAmount;
            if (diffAmount.compareTo(0D) == 0 || diffAmount.compareTo(0D) == -1) {
                hlsCusConContractCashflowService.batchDelete(resultQuotationCashflowTempDepositTemp);
            } else {
                if (resultQuotationCashflowTempDepositTemp.size() > 0) {
                    resultQuotationCashflowTempDepositTemp.get(0).setDueAmount(diffAmount);
                    hlsCusConContractCashflowService.batchUpdate(request, resultQuotationCashflowTempDepositTemp);
                }
            }
        }
        HlsCusConContractCashflow resultQuotationCashflow = new HlsCusConContractCashflow();
        resultQuotationCashflow.setContractId(prjQuotation.getContractId());
        List<HlsCusConContractCashflow> resultQuotationCashflowList = hlsCusConContractCashflowMapper.conChangeCashflowselect(resultQuotationCashflow);
        resultQuotationCashflowList.get(0).setTimes(0L);
        for (int i = 1; i < resultQuotationCashflowList.size(); i++) {
            if (resultQuotationCashflowList.get(i).getDueDate().after(resultQuotationCashflowList.get(i - 1).getDueDate())) {
                times = times + 1;
            }
            resultQuotationCashflowList.get(i).setTimes(times);
            hlsCusConContractCashflowMapper.updateByPrimaryKeySelective(resultQuotationCashflowList.get(i));
        }
        //更新prj_quotation lease_term 和 lease_times
        HlsCusPrjQuotation prjQuotationNew = new HlsCusPrjQuotation();
        prjQuotationNew.setLeaseTimes(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getLeaseTimes());
        prjQuotationNew.setLeaseTerm(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getLeaseTerm());
        prjQuotationNew.setLeaseItemAmount(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getLeaseItemAmount());
        prjQuotationNew.setQuotationId(hlsCusPrjProjectInfo.getHlsCusPrjQuotation().getQuotationId());

        self().updateByPrimaryKeySelective(request, prjQuotationNew);
    }

    //获取还款频率
    public Long getRentingFrequency(String rentingFrequency) {
        Long resultNumber = 0L;
        if ("YEAR".equals(rentingFrequency)) {
            resultNumber = 12L;
        } else if ("HALF_A_YEAR".equals(rentingFrequency)) {
            resultNumber = 6L;
        } else if ("QUARTER".equals(rentingFrequency)) {
            resultNumber = 3L;
        } else if ("MONTH".equals(rentingFrequency)) {
            resultNumber = 1L;
        } else if ("DOUBLE_MONTH".equals(rentingFrequency)) {
            resultNumber = 2L;
        }
        return resultNumber;
    }

    @Override
    public void updateXirr(IRequest request, HlsCusPrjQuotation prjQuotation) throws IllegalArgumentException {
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());

        //20210706 君成修改，增加校验 现金流期数需要等于报价总期数
        HlsCusPrjQuotationCashflow quotationCheck = hlsCusPrjQuotationCashflowMapper.quotationCheck(hlsCusPrjQuotationCashflow);
        /*if( !"".equals(quotationCheck) && quotationCheck != null ){
            if(!quotationCheck.getLeaseTimes().equals(quotationCheck.getMaxTimes())){
                throw new IllegalArgumentException("租赁总期数不等于现金流最大期数，计算失败！" );
            }
        }*/

        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.selectIrrQuotation(hlsCusPrjQuotationCashflow);
        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowListJC = hlsCusPrjQuotationCashflowMapper.selectIrrQuotationJC(hlsCusPrjQuotationCashflow);
        if(APP_CALCULATE.equals(prjQuotation.getSourceDocumentCategory())){
            prjQuotationCashflowListJC = hlsCusPrjQuotationCashflowMapper.selectIrrQuotationJCForApp(hlsCusPrjQuotationCashflow);
        }
        List<HlsCusPrjQuotationCashflow> paynoteCashflowList = hlsCusPrjQuotationCashflowMapper.selectPaynoteIrrQuotation(hlsCusPrjQuotationCashflow);
        if (prjQuotationCashflowListJC.size() == 0) {
            return;
        }

        double[] paynoteDueAmountList = new double[prjQuotationCashflowListJC.size()];
//        double[] paynoteNetDueAmountList = new double[paynoteCashflowList.size()];

        double[] dueAmountList = new double[prjQuotationCashflowListJC.size()];
//        double[] netDueAmountList = new double[prjQuotationCashflowList.size()];

        Date[] paynoteDueAmountDateList = new Date[prjQuotationCashflowListJC.size()];
//        Date[] paynoteNetDueAmountDateList = new Date[paynoteCashflowList.size()];

        Date[] dueAmountDateList = new Date[prjQuotationCashflowListJC.size()];
//        Date[] netDueAmountDateList = new Date[prjQuotationCashflowList.size()];

//        for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
//                dueAmountList[i] = prjQuotationCashflowList.get(i).getDueAmount();
//                dueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();
//
//                netDueAmountList[i] = prjQuotationCashflowList.get(i).getNetDueAmount();
//                netDueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();
//        }
//
//        for (int i = 0; i < paynoteCashflowList.size(); i++) {
//            paynoteDueAmountList[i] = paynoteCashflowList.get(i).getDueAmount();
//            paynoteDueAmountDateList[i] = paynoteCashflowList.get(i).getDueDate();
//
//            paynoteNetDueAmountList[i] = paynoteCashflowList.get(i).getNetDueAmount();
//            paynoteNetDueAmountDateList[i] = paynoteCashflowList.get(i).getDueDate();
//        }
        for (int i = 0; i < prjQuotationCashflowListJC.size(); i++) {
            if(prjQuotationCashflowListJC.get(i).getCashflowIrr() != 0D){
                dueAmountList[i] = prjQuotationCashflowListJC.get(i).getCashflowIrr();
                dueAmountDateList[i] = prjQuotationCashflowListJC.get(i).getDueDate();

                paynoteDueAmountList[i] = prjQuotationCashflowListJC.get(i).getCashflowIrrAfterTax();
                paynoteDueAmountDateList[i] = prjQuotationCashflowListJC.get(i).getDueDate();
            }

        }

        //名义irr(复利参考值)
//        Double referenceValueIrr = hlsCusPrjQuotationCashflowService.Newtons_method(0.1, dueAmountList, dueAmountDateList);
//
//        if ("NAN".equals(referenceValueIrr)) {
//            throw new IllegalArgumentException("名义irr(复利参考值)计算失败，请联系管理员");
//        }

        //含税xirr
        Double xirr = HlsCusXirr.Newtons_method(0.1, dueAmountList, dueAmountDateList);

        //不含税xirr
//        Double netXirr = HlsCusXirr.Newtons_method(0.1, netDueAmountList, netDueAmountDateList);

        //承兑xirr
        Double paynoteXirr = HlsCusXirr.Newtons_method(0.1, paynoteDueAmountList, paynoteDueAmountDateList);

        //承兑不含税xirr
//        Double paynoteNetXirr = HlsCusXirr.Newtons_method(0.1, paynoteNetDueAmountList, paynoteNetDueAmountDateList);

        //名义irr
//        Double irr = (double) Math.round((Math.pow(referenceValueIrr + 1.0, 1.0 / 365.0) - 1.0) * 365.0 * Math.pow(10, 6)) / Math.pow(10, 6);
        //更新名义irr和xirr
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(prjQuotation.getQuotationId());
        hlsCusPrjQuotation = self().selectByPrimaryKey(request, hlsCusPrjQuotation);
//        hlsCusPrjQuotation.setIrr(irr);
        if(!Double.isNaN(xirr) && !Double.isInfinite(xirr)) {
            hlsCusPrjQuotation.setXirr(xirr);
        }
//        if(!Double.isNaN(netXirr) && !Double.isInfinite(netXirr)) {
//            hlsCusPrjQuotation.setXirrNet(netXirr);
//        }

        if(!Double.isNaN(paynoteXirr) && !Double.isInfinite(paynoteXirr)) {
            hlsCusPrjQuotation.setXirrPaynote(paynoteXirr);
        }

//        if(!Double.isNaN(paynoteNetXirr) && !Double.isInfinite(paynoteNetXirr)) {
//            hlsCusPrjQuotation.setXirrNetPaynote(paynoteNetXirr);
//        }

        self().updateByPrimaryKeySelective(request, hlsCusPrjQuotation);
    }

    @Override
    public HlsCusPrjQuotation prjQuotationCreateNew(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation) {
        HlsCusPrjQuotation prjQuotation = new HlsCusPrjQuotation();
        HlsCusPrjProject hlsCusPrjProject = new HlsCusPrjProject();
        hlsCusPrjProject.setProjectId(hlsCusPrjQuotation.getProjectId());
        hlsCusPrjProject = hlsCusPrjProjectService.selectByPrimaryKey(iRequest, hlsCusPrjProject);

        if (hlsCusPrjQuotation.getQuotationId() != null && hlsCusPrjQuotation.getQuotationId() != 0) {
            prjQuotation.setQuotationId(hlsCusPrjQuotation.getQuotationId());
            prjQuotation = self().selectByPrimaryKey(iRequest, prjQuotation);
            hlsCusPrjQuotation.setRentPaymentType("RENT_PAYMENT");
            hlsCusPrjQuotation.setDocumentId(hlsCusPrjQuotation.getProjectId());
            hlsCusPrjQuotation.setObjectVersionNumber(prjQuotation.getObjectVersionNumber());
            prjQuotation = self().updateByPrimaryKey(iRequest, hlsCusPrjQuotation);
        } else {
            hlsCusPrjQuotation.set__status(DTOStatus.ADD);
            hlsCusPrjQuotation.setSourceDocumentCategory("PRJ_PROJECT");
            hlsCusPrjQuotation.setStatus("NEW");
            //租金支付表编号 编码规则 = 项目编号+2位流水账号

            String contractNum = hlsCusPrjProject.getProjectNumber();
            HlsCusPrjProject pDto = new HlsCusPrjProject();
            pDto.setProjectNumber(contractNum);
            String num2 = this.getQuatationContractSerialNumber(iRequest, pDto);
            contractNum = contractNum + "-" + num2;

            hlsCusPrjQuotation.setPaymentNumber(contractNum);
            hlsCusPrjQuotation.setRentPaymentType("RENT_PAYMENT");
            hlsCusPrjQuotation.setDocumentId(hlsCusPrjQuotation.getProjectId());

            prjQuotation = self().insertSelective(iRequest, hlsCusPrjQuotation);
        }
        return prjQuotation;

    }

    @Override
    public HlsCusPrjQuotation quotationQuote(IRequest iRequest,HlsCusPrjQuotation hlsCusPrjQuotation,Long projectId,Long quotationId) throws HlsCusException {
        HlsCusPrjQuotation prjQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
        if(quotationId == null){
            //第一次引用
            //复制新的quotation
            createNewPrjQuotation(iRequest,prjQuotation,projectId);
        }else{
            //第二次引用
            if(quotationId.equals(prjQuotation.getQuotationId())){
                //引用自己时不操作
                throw new HlsCusException("无法引用当前项目的报价!");
            }else{
                //引用其他报价，先删自己再存其他
                HlsCusPrjQuotation oldQuotation = new HlsCusPrjQuotation();
                oldQuotation.setQuotationId(quotationId);
                oldQuotation = prjQuotationMapper.selectByPrimaryKey(oldQuotation);
                //查询旧的现金流并删除
                /*List<HlsCusPrjQuotationCashflow> oldCashflowList = hlsCusPrjQuotationCashflowService.queryQuotationCashFlowById(oldQuotation);
                hlsCusPrjQuotationCashflowService.batchDelete(oldCashflowList);*/

                hlsCusPrjQuotationCashflowService.deleteQuotationCashFlowById(oldQuotation);
                /*HlsCusPrjQuotationDetails oldDetails = new HlsCusPrjQuotationDetails();
                oldDetails.setQuotationId(quotationId);*/
                //查询旧的details并删除
                /*List<HlsCusPrjQuotationDetails> oldDetailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(oldDetails);
                hlsCusPrjQuotationDetailsService.batchDelete(oldDetailsList);*/

                hlsCusPrjQuotationDetailsService.deleteDetailsById(oldQuotation);

                String otherPrice = oldQuotation.getOtherPrice();
                String subjectMatterIntrodution = oldQuotation.getSubjectMatterIntrodution();

                //删除prj_quotation
                prjQuotationMapper.deleteByPrimaryKey(oldQuotation);

                //复制新的quotation
                prjQuotation.setCalcName(hlsCusPrjQuotation.getCalcName());
                prjQuotation.setOtherPrice(otherPrice);
                prjQuotation.setSubjectMatterIntrodution(subjectMatterIntrodution);
                createNewPrjQuotation(iRequest,prjQuotation,projectId);
            }
        }


        return hlsCusPrjQuotation;
    }

    public Boolean createNewPrjQuotation(IRequest iRequest,HlsCusPrjQuotation hlsCusPrjQuotation,Long projectId){
        //插入prj_quotation
        Long quotationId = hlsCusPrjQuotation.getQuotationId();
        HlsCusPrjQuotation newQuotation = prjQuotationMapper.selectByPrimaryKey(hlsCusPrjQuotation);
        newQuotation.setQuotationId(null);
        newQuotation.setSourceDocumentId(projectId);
        newQuotation.setSourceDocumentCategory("PRJ_PROJECT");
        newQuotation.setDataClass("PRJ_PROJECT_INVEST");
        newQuotation.setSourceQuotationId(quotationId);
        newQuotation.setCalcName(hlsCusPrjQuotation.getCalcName());
        newQuotation.setOtherPrice(hlsCusPrjQuotation.getOtherPrice());
        newQuotation.setSubjectMatterIntrodution(hlsCusPrjQuotation.getSubjectMatterIntrodution());
        prjQuotationMapper.insertSelective(newQuotation);
        //查询QuotationDetails并插入
        HlsCusPrjQuotationDetails hlsCusPrjQuotationDetails = new HlsCusPrjQuotationDetails();
        hlsCusPrjQuotationDetails.setQuotationId(quotationId);
        List<HlsCusPrjQuotationDetails> hlsCusPrjQuotationDetailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(hlsCusPrjQuotationDetails);
        for(HlsCusPrjQuotationDetails dt:hlsCusPrjQuotationDetailsList){
            //更改QuotationId()
            dt.setQuotationId(newQuotation.getQuotationId());
            hlsCusPrjQuotationDetailsService.insertSelective(iRequest,dt);
        }
        //查询出现金流并插入
        List<HlsCusPrjQuotationCashflow> hlsCusPrjQuotationCashflowList = hlsCusPrjQuotationCashflowService.queryQuotationCashFlowById(hlsCusPrjQuotation);
        for(HlsCusPrjQuotationCashflow dt:hlsCusPrjQuotationCashflowList){
            //更改QuotationId()
            dt.setQuotationId(newQuotation.getQuotationId());
            hlsCusPrjQuotationCashflowService.insertSelective(iRequest,dt);
        }
        return true;
    }

    @Override
    public List<HlsCusPrjQuotation> queryPrjQuotationByProjectId(HlsCusPrjProject hlsCusPrjProject) {
        return prjQuotationMapper.selectQuotationByProjectId(hlsCusPrjProject);
    }

    @Override
    public void saveExcel(IRequest iRequest, HlsCusPrjQuotation quotation) throws HlsCusException {

        if(quotation.getQuotationId() == null){
            throw new HlsCusException("数据异常，请联系管理员!");
        }

        HlsCusPrjQuotationDetails details = new HlsCusPrjQuotationDetails();
        details.setQuotationId(quotation.getQuotationId());
        List<HlsCusPrjQuotationDetails> detailsList = hlsCusPrjQuotationDetailsService.queryDetailsById(details);

        HlsCusPrjQuotationDetails quotationDetails = new HlsCusPrjQuotationDetails();
        if(CollectionUtils.isEmpty(detailsList)){

            quotationDetails.setQuotationId(quotation.getQuotationId());
            quotationDetails.setSheets(quotation.getCompressSheets());
            hlsCusPrjQuotationDetailsService.insert(iRequest,quotationDetails);
        }else if(detailsList.size() == 1){
            quotationDetails = detailsList.get(0);
            quotationDetails.setSheets(quotation.getCompressSheets());
            hlsCusPrjQuotationDetailsService.updateByPrimaryKeySelective(iRequest,quotationDetails);
        }else{
            throw new HlsCusException("数据异常，请联系管理员!");
        }
    }

    @Override
    public Double selectLprBaseRateByDate(IRequest iRequest, HlsCusPrjQuotation quotation) throws HlsCusException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(quotation.getLprDate() == null || quotation.getDescription() == null){
            throw new HlsCusException("参数异常,请联系管理员");
        }

        List<Map> list = prjQuotationMapper.selectLprBaseRateByDate(quotation);
        if (CollectionUtils.isEmpty(list)){
            throw new HlsCusException("根据当前日期"+simpleDateFormat.format(quotation.getLprDate())+"未查询到基准利率，请联系管理员检查lpr利率配置!");
        }else if(list.size() != 1){
            throw new HlsCusException("根据当前日期"+simpleDateFormat.format(quotation.getLprDate())+"未查询到唯一的基准利率，请联系管理员检查lpr利率配置!");
        }

        Double base_rate = Double.valueOf(list.get(0).get("base_rate").toString());

        return base_rate;
    }


    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    public String getQuatationContractSerialNumber(IRequest requestCtx, HlsCusPrjProject hlsCusPrjProject) {
        String num = hlsCusPrjProjectMapper.getQuatationContractMaxNumber(hlsCusPrjProject);
        if (StringUtils.isBlank(num)) {
            return "01";
        } else {
            int var = Integer.parseInt(num) + 1;
            num = ("" + (var + 100)).substring(("" + (var + 100)).length() - 2);
            return num;
        }
    }

    private List marginDeductionIrr(List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList, Double residualValue) {
        Double lastTimeSumAmount = prjQuotationCashflowList.get(prjQuotationCashflowList.size() - 1).getDueAmount();
        if (lastTimeSumAmount < residualValue) {
            prjQuotationCashflowList.get(prjQuotationCashflowList.size() - 1).setDueAmount(0D);
            //进行保证金抵扣
            for (int i = prjQuotationCashflowList.size() - 2; i >= 0; i--) {
                lastTimeSumAmount = lastTimeSumAmount + prjQuotationCashflowList.get(i).getDueAmount();
                if (lastTimeSumAmount < residualValue) {
                    prjQuotationCashflowList.get(i).setDueAmount(0D);
                } else {
                    prjQuotationCashflowList.get(i).setDueAmount(lastTimeSumAmount);
                    break;
                }
            }
        }
        return prjQuotationCashflowList;
    }

    @Override
    public void savePrjQuotationRecord(IRequest iRequest, HlsCusPrjQuotation quotation) throws Exception {
        if(quotation != null && quotation.getQuotationId() != null){
            HlsCusPrjQuotation databaseInfo = new HlsCusPrjQuotation();
            databaseInfo.setQuotationId(quotation.getQuotationId());
            databaseInfo = self().selectByPrimaryKey(iRequest, databaseInfo);

            if (StringUtils.isEmpty(quotation.getPriceList())) {
                throw new HlsCusException("价目表信息不能为空!");
            }

            if(databaseInfo.getOldPriceList() !=null && !databaseInfo.getOldPriceList().equals("N")){
                if (!quotation.getPriceList().equals(databaseInfo.getOldPriceList())) {

                    //更换报价后删除details信息
                    hlsCusPrjQuotationDetailsMapper.deleteDetailsById(databaseInfo);
                }
            }

            self().updateByPrimaryKeySelective(iRequest, quotation);

            //判断未计算的报价的时候将报价上的值带入excel，不计算
            if(quotation.getXirr() == null && quotation.getCalcFlag() == null){
                quotationReCalc(iRequest,quotation.getQuotationId(),false);
            }
        }

    }

    @Override
    public HlsCusPrjQuotation savePrjQuotationForApp(IRequest iRequest, HlsCusPrjQuotation quotation) throws Exception {
        if (StringUtils.isEmpty(quotation.getPriceList())) {
            throw new HlsCusException("价目表信息不能为空!");
        }
        if(quotation != null && quotation.getQuotationId() != null){
            self().updateByPrimaryKey(iRequest, quotation);
        }else{
            quotation = self().insert(iRequest,quotation);
        }
        return quotation;

    }

    @Override
    public List<Map> selectQuotationChangeInfo(IRequest iRequest, HlsCusPrjQuotation quotation,int pagenum, int pagesize) {

        PageHelper.startPage(pagenum,pagesize);
        List<Map> mapList = prjQuotationMapper.selectQuotationChangeInfo(quotation);



        return mapList;
    }

    @Override
    public List<HlsCusPrjQuotation> queryQuotationRateReq(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryQuotationRateReq(hlsCusPrjQuotation);
    }
    @Override
    public List<HlsCusPrjQuotation> queryQuotationRateReqNew(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryQuotationRateReqNew(hlsCusPrjQuotation);
    }

    @Override
    public List<HlsCusPrjQuotation> queryQuotationFloatingLnReq(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryQuotationCalcReq(hlsCusPrjQuotation);
    }
    @Override
    public List<HlsCusPrjQuotation> queryQuotationFloatingLnReqNew(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryQuotationCalcReqNew(hlsCusPrjQuotation);
    }
    @Override
    public List<HlsCusPrjQuotation> queryQuotationFloatingLnReqWfl(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryQuotationCalcReqWfl(hlsCusPrjQuotation);
    }


    @Override
    public List<HlsCusPrjQuotation> queryFloatingInterest(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryFloatingInterest(hlsCusPrjQuotation);
    }

    @Override
    public List<HlsCusPrjQuotation> queryTotalFloatingInterest(IRequest iRequest, HlsCusPrjQuotation hlsCusPrjQuotation, int pagenum, int pagesize) {
        PageHelper.startPage(pagenum, pagesize);
        return prjQuotationMapper.queryTotalFloatingInterest(hlsCusPrjQuotation);
    }

    @Override
    public void updateIrr(IRequest request, HlsCusPrjQuotation prjQuotation) throws IllegalArgumentException {
        HlsCusPrjQuotationCashflow hlsCusPrjQuotationCashflow = new HlsCusPrjQuotationCashflow();
        hlsCusPrjQuotationCashflow.setQuotationId(prjQuotation.getQuotationId());

        HlsCusPrjQuotationCashflow quotationCheck = hlsCusPrjQuotationCashflowMapper.quotationCheck(hlsCusPrjQuotationCashflow);

        List<HlsCusPrjQuotationCashflow> prjQuotationCashflowList = hlsCusPrjQuotationCashflowMapper.selectIrrQuotation(hlsCusPrjQuotationCashflow);

        if (prjQuotationCashflowList.size() == 0) {
            return;
        }

        double[] dueAmountList = new double[prjQuotationCashflowList.size()];
        Date[] dueAmountDateList = new Date[prjQuotationCashflowList.size()];

        for (int i = 0; i < prjQuotationCashflowList.size(); i++) {
                dueAmountList[i] = prjQuotationCashflowList.get(i).getDueAmount();
                dueAmountDateList[i] = prjQuotationCashflowList.get(i).getDueDate();
                if(prjQuotationCashflowList.get(i).getDueDate()==null){
                    dueAmountDateList[i]=new Date(0);
                }
        }

        //名义irr(复利参考值)
       Double referenceValueIrr = hlsCusPrjQuotationCashflowService.Newtons_method(0.1, dueAmountList, dueAmountDateList);

        if ("NAN".equals(referenceValueIrr)) {
            throw new IllegalArgumentException("名义irr(复利参考值)计算失败，请联系管理员");
        }
        HlsCusPrjQuotation hlsCusPrjQuotation = new HlsCusPrjQuotation();
        hlsCusPrjQuotation.setQuotationId(prjQuotation.getQuotationId());
        hlsCusPrjQuotation = self().selectByPrimaryKey(request, hlsCusPrjQuotation);


        if("GECALCULATOR_CASUAL_YH".equals(hlsCusPrjQuotation.getPriceList())){
            Double irr = (double) Math.round((Math.pow(referenceValueIrr + 1.0, 1.0 / 360.0) - 1.0) * 360.0 * Math.pow(10, 8)) / Math.pow(10, 8);
            hlsCusPrjQuotation.setIrr(irr);
        }

        self().updateByPrimaryKeySelective(request, hlsCusPrjQuotation);
    }
}
