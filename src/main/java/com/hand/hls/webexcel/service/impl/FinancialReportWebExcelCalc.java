package com.hand.hls.webexcel.service.impl;

import cfca.paperless.base.util.StringUtil;
import com.hand.hap.core.IRequest;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;
import com.hand.hls.bp.mapper.HlsBpFinancialHeaderMapper;
import com.hand.hls.bp.service.IHlsBpFinancialHeaderService;
import com.hand.hls.fnd.dto.HlsFinStatementHd;
import com.hand.hls.fnd.dto.HlsFinStatementLn;
import com.hand.hls.fnd.service.HlsFinStatementHdService;
import com.hand.hls.fnd.service.HlsFinStatementLnService;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import com.hand.hls.hls.mapper.HlsWebExcelCalcResultMapper;
import com.hand.hls.utils.HlsCusMathUtil;
import com.hand.hls.webexcel.service.IWebExcelCalcService;
import com.hand.hls.webexcel.service.IWebExcelCalcUtilService;
import hls.core.utils.exception.HlsCusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FinancialReportWebExcelCalc implements IWebExcelCalcService {

    //根据类型获取对应的实现类
    private final static String SOURCE_DOCUMENT_CATEGORY = "HLS_BP_MASTER";

    //webexcel存储的单据类型
    private final static String WEB_EXCEL_RESULT_CATEGORY = "FINANCIAL_REPORT";

    private static final String SINGLE = "SINGLE";
    private static final String MULTI_LINE = "MULTI_LINE";

    private final static String HD_SHEET_NAME = "客户财报基本信息";

    private final static String YEAR_STR = "年";
    private final static String MONTH_STR = "月";

    private final static String FIN_SHEET_NAME = "财务指标";

    @Autowired
    private IWebExcelCalcUtilService webExcelCalcUtilService;

    @Autowired
    private IHlsBpFinancialHeaderService iHlsBpFinancialHeaderService;

    @Autowired
    private HlsBpFinancialHeaderMapper hlsBpFinancialHeaderMapper;

    @Autowired
    private HlsWebExcelCalcResultMapper hlsWebExcelCalcResultMapper;

    @Autowired
    private HlsFinStatementHdService hlsFinStatementHdService;

    @Autowired
    private HlsFinStatementLnService hlsFinStatementLnService;

    private Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public HlsWebExcelCalcResult calcExcel(IRequest iRequest, HlsWebExcelCalcResult calcResult) throws HlsCusException {

        //获取未压缩的sheets
        String sheets = calcResult.getSheets();
        Map map = new HashMap();

        //设置表对应的dto路径
        map.put("hls_fin_statement_hd","com.hand.hls.fnd.dto.HlsFinStatementHd");
        map.put("hls_fin_statement_ln","com.hand.hls.fnd.dto.HlsFinStatementLn");

        //解析头配置信息
        List<Map> hdList = new ArrayList<>();
        try {
            hdList  = webExcelCalcUtilService.getExcelSingleListByHdInfo(iRequest,sheets,calcResult.getExcelId(),map,SINGLE);
        } catch (Exception e) {
            logger.info("获取excel头信息失败，{}",e.getMessage());
            throw new HlsCusException("头信息解析失败，请检查头配置" + e.getLocalizedMessage());
        }

        //解析行配置信息
        List<Map> lnList = new ArrayList<>();
        try {
            lnList  = webExcelCalcUtilService.getExcelSingleListByHdInfo(iRequest,sheets,calcResult.getExcelId(),map,MULTI_LINE);
        } catch (Exception e) {
            logger.info("获取excel行信息失败，{}",e.getMessage());
            throw new HlsCusException("行信息解析失败，请检查头配置" + e.getLocalizedMessage());
        }
        //数据校验
        Map lnListMap0 = lnList.get(0);
        List<HlsFinStatementLn> financiaSheet = (List<HlsFinStatementLn>) lnListMap0.get("资产负债表");
        //校验 资产总计= 负债合计 + 所有者权益(或股东权益) 合计
        calcaulate("1-1",financiaSheet);
        calcaulate("1-2",financiaSheet);
        calcaulate("1-3",financiaSheet);
        calcaulate("1-4",financiaSheet);
        //校验	期未现金及现金等价物余额=现金及现金等价物净增加额 + 期初现金及现金等价物余额
        Map lnListMap1 = lnList.get(2);
        financiaSheet = (List<HlsFinStatementLn>) lnListMap1.get("现金流量表");
        calcaulate("2-1",financiaSheet);
        calcaulate("2-2",financiaSheet);
        calcaulate("2-3",financiaSheet);
        calcaulate("2-4",financiaSheet);
        //校验 投资活动产生的现金流量净额=投资活动现金流入小计 - 投资活动现金流出小计
        calcaulate("3-1",financiaSheet);
        calcaulate("3-2",financiaSheet);
        calcaulate("3-3",financiaSheet);
        calcaulate("3-4",financiaSheet);
        //校验 筹资活动产生的现金流量净额=筹资活动现金流入小计 - 筹资活动现金流出小计
        calcaulate("4-1",financiaSheet);
        calcaulate("4-2",financiaSheet);
        calcaulate("4-3",financiaSheet);
        calcaulate("4-4",financiaSheet);
        //校验 经营活动产生的现金流量净额=经营活动现金流入小计 - 经营活动现金流出小计
        calcaulate("5-1",financiaSheet);
        calcaulate("5-2",financiaSheet);
        calcaulate("5-3",financiaSheet);
        calcaulate("5-4",financiaSheet);
        //校验 现金及现金等价物净增加额=经营活动产生的现金流量净额 + 投资活动产生的现金流量净额 + 筹资活动产生的现金流量净额 + 汇率变动对现金及现金等价物的影响
        calcaulate("6-1",financiaSheet);
        calcaulate("6-2",financiaSheet);
        calcaulate("6-3",financiaSheet);
        calcaulate("6-4",financiaSheet);
        //清除多余数据
        Map lnListMap2 = lnList.get(3);
        financiaSheet = (List<HlsFinStatementLn>) lnListMap2.get("财务指标");
        financiaSheet.remove(financiaSheet.size()-1);




        //插入商业伙伴关联业务表,每次导入新增一条，同时关联 hls_web_excel_calc_result,将财报计算信息保存至sheet
        HlsBpFinancialHeader hlsBpFinancialHeader = new HlsBpFinancialHeader();
        if(calcResult.getResultId() == null){
            if(calcResult.getSourceDocumentId() == null){
                throw new HlsCusException("单据信息异常,请联系管理员!");
            }

            hlsBpFinancialHeader.setBpId(calcResult.getSourceDocumentId());
            hlsBpFinancialHeader.setExportDate(new Date());
            iHlsBpFinancialHeaderService.insert(iRequest,hlsBpFinancialHeader);

            //关联webexcel计算结果表
            calcResult.setSourceDocumentId(hlsBpFinancialHeader.getHeaderId());
            calcResult.setSourceDocumentCategory(WEB_EXCEL_RESULT_CATEGORY);

        }else{

            //查询已关联的业务表信息
            calcResult = hlsWebExcelCalcResultMapper.selectByPrimaryKey(calcResult);

            hlsBpFinancialHeader.setHeaderId(calcResult.getSourceDocumentId());
            hlsBpFinancialHeader = iHlsBpFinancialHeaderService.selectByPrimaryKey(iRequest,hlsBpFinancialHeader);
            hlsBpFinancialHeader.setCheckStatus("N");
            iHlsBpFinancialHeaderService.updateByPrimaryKeySelective(iRequest,hlsBpFinancialHeader);
        }


        Map<String,Long> yearMap = new HashMap();

        //每次重算先删除已保存的业务信息
        hlsBpFinancialHeaderMapper.deleteHlsBpFinStatementHdByHeaderId(hlsBpFinancialHeader);
        for(Map result: hdList){
            if(result.get(HD_SHEET_NAME) != null){
                List<HlsFinStatementHd> statementHdList = (List<HlsFinStatementHd>) result.get(HD_SHEET_NAME);

                for(HlsFinStatementHd statementHd:statementHdList){
                    statementHd.setBpId(hlsBpFinancialHeader.getBpId());
                    statementHd.setCompanyId(iRequest.getCompanyId());
                    statementHd.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                    if(statementHd.getReportYear() != null && statementHd.getReportYear().indexOf(YEAR_STR) != -1){
                        statementHd.setFiscalYear(Long.valueOf(statementHd.getReportYear().substring(0,statementHd.getReportYear().indexOf(YEAR_STR))));
                        if(statementHd.getReportYear().indexOf(MONTH_STR) != -1){
                            statementHd.setFiscalMonth(Long.valueOf(statementHd.getReportYear().substring(statementHd.getReportYear().indexOf(YEAR_STR)+1,statementHd.getReportYear().indexOf(MONTH_STR))));
                        }
                    }

                    statementHd.setReportType(statementHdList.get(0).getReportType());
                    statementHd.setIfFinancialCompany(statementHdList.get(0).getIfFinancialCompany());

                    hlsFinStatementHdService.insert(iRequest,statementHd);
                    yearMap.put(statementHd.getReportYear(),statementHd.getFinStatementHdId());
                }

            }
        }

        //行删除及保存
        hlsBpFinancialHeaderMapper.deleteHlsBpFinStatementLnByHeaderId(hlsBpFinancialHeader);

        for(Map<String,List> result: lnList){

            for(String key : result.keySet()){
                List<HlsFinStatementLn> statementLnList = result.get(key);

                if(FIN_SHEET_NAME.equals(key)){
                    //当是财务指标的时候直接将数据插入
                    String attribute1 = "";
                    for(int i = 0; i < statementLnList.size(); i++){
                        HlsFinStatementLn statementLn = statementLnList.get(i);
                        statementLn.setLineType(key);
                        statementLn.setLineSeqNumber(Long.valueOf(i));
                        statementLn.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                        if(statementLnList.get(i).getAttribute_1() != null){
                            attribute1 = statementLnList.get(i).getAttribute_1();
                        }else{
                            statementLn.setAttribute_1(attribute1);
                        }
                        hlsFinStatementLnService.insert(iRequest, statementLn);
                    }
                }else {
                    calcStatementLn(iRequest,statementLnList,key,hlsBpFinancialHeader,yearMap);
                }

            }

        }

        return calcResult;
    }



    private void calcaulate(String index, List<HlsFinStatementLn> financiaSheet) throws HlsCusException{
        Double amount = 0D;
        Double other = 0D;
        String year = "";
        switch (index) {
            case "1-1":
                year = financiaSheet.get(0).getAttribute_2();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_2())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(60).getAttribute_2())).doubleValue(), new BigDecimal(check(financiaSheet.get(67).getAttribute_2())).doubleValue());
                break;
            case "1-2":
                year = financiaSheet.get(0).getAttribute_3();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_3())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(60).getAttribute_3())).doubleValue(), new BigDecimal(check(financiaSheet.get(67).getAttribute_3())).doubleValue());
                break;
            case "1-3":
                year = financiaSheet.get(0).getAttribute_4();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_4())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(60).getAttribute_4())).doubleValue(), new BigDecimal(check(financiaSheet.get(67).getAttribute_4())).doubleValue());
                break;
            case "1-4":
                year = financiaSheet.get(0).getAttribute_5();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_5())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(60).getAttribute_5())).doubleValue(), new BigDecimal(check(financiaSheet.get(67).getAttribute_5())).doubleValue());
                break;
        }
        if (HlsCusMathUtil.compare(amount,other) != 0){
            throw new HlsCusException(year + "资产总计 不等于 负债合计 + 所有者权益(或股东权益) 合计");
        }
        switch (index) {
            case "2-1":
                year = financiaSheet.get(0).getAttribute_2();
                amount = new BigDecimal(check(financiaSheet.get(38).getAttribute_2())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(36).getAttribute_2())).doubleValue(), new BigDecimal(check(financiaSheet.get(37).getAttribute_2())).doubleValue());
                break;
            case "2-2":
                year = financiaSheet.get(0).getAttribute_3();
                amount = new BigDecimal(check(financiaSheet.get(38).getAttribute_3())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(36).getAttribute_3())).doubleValue(), new BigDecimal(check(financiaSheet.get(37).getAttribute_3())).doubleValue());
                break;
            case "2-3":
                year = financiaSheet.get(0).getAttribute_4();
                amount = new BigDecimal(check(financiaSheet.get(38).getAttribute_4())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(36).getAttribute_4())).doubleValue(), new BigDecimal(check(financiaSheet.get(37).getAttribute_4())).doubleValue());
                break;
            case "2-4":
                year = financiaSheet.get(0).getAttribute_5();
                amount = new BigDecimal(check(financiaSheet.get(38).getAttribute_5())).doubleValue();
                other = HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(36).getAttribute_5())).doubleValue(), new BigDecimal(check(financiaSheet.get(37).getAttribute_5())).doubleValue());
                break;
        }
        if (HlsCusMathUtil.compare(amount,other) != 0){
            throw new HlsCusException(year + "期未现金及现金等价物余额 不等于 现金及现金等价物净增加额 + 期初现金及现金等价物余额");
        }
        switch (index) {
            case "3-1":
                year = financiaSheet.get(0).getAttribute_2();
                amount = new BigDecimal(check(financiaSheet.get(24).getAttribute_2())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(18).getAttribute_2())).doubleValue(), new BigDecimal(check(financiaSheet.get(23).getAttribute_2())).doubleValue());
                break;
            case "3-2":
                year = financiaSheet.get(0).getAttribute_3();
                amount = new BigDecimal(check(financiaSheet.get(24).getAttribute_3())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(18).getAttribute_3())).doubleValue(), new BigDecimal(check(financiaSheet.get(23).getAttribute_3())).doubleValue());
                break;
            case "3-3":
                year = financiaSheet.get(0).getAttribute_4();
                amount = new BigDecimal(check(financiaSheet.get(24).getAttribute_4())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(18).getAttribute_4())).doubleValue(), new BigDecimal(check(financiaSheet.get(23).getAttribute_4())).doubleValue());
                break;
            case "3-4":
                year = financiaSheet.get(0).getAttribute_5();
                amount = new BigDecimal(check(financiaSheet.get(24).getAttribute_5())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(18).getAttribute_5())).doubleValue(), new BigDecimal(check(financiaSheet.get(23).getAttribute_5())).doubleValue());
                break;
        }
        if (HlsCusMathUtil.compare(amount,other) != 0){
            throw new HlsCusException(year + "投资活动产生的现金流量净额 不等于 投资活动现金流入小计 - 投资活动现金流出小计");
        }
        switch (index) {
            case "4-1":
                year = financiaSheet.get(0).getAttribute_2();
                amount = new BigDecimal(check(financiaSheet.get(34).getAttribute_2())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(29).getAttribute_2())).doubleValue(), new BigDecimal(check(financiaSheet.get(33).getAttribute_2())).doubleValue());
                break;
            case "4-2":
                year = financiaSheet.get(0).getAttribute_3();
                amount = new BigDecimal(check(financiaSheet.get(34).getAttribute_3())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(29).getAttribute_3())).doubleValue(), new BigDecimal(check(financiaSheet.get(33).getAttribute_3())).doubleValue());
                break;
            case "4-3":
                year = financiaSheet.get(0).getAttribute_4();
                amount = new BigDecimal(check(financiaSheet.get(34).getAttribute_4())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(29).getAttribute_4())).doubleValue(), new BigDecimal(check(financiaSheet.get(33).getAttribute_4())).doubleValue());
                break;
            case "4-4":
                year = financiaSheet.get(0).getAttribute_5();
                amount = new BigDecimal(check(financiaSheet.get(34).getAttribute_5())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(29).getAttribute_5())).doubleValue(), new BigDecimal(check(financiaSheet.get(33).getAttribute_5())).doubleValue());
                break;
        }
        if (HlsCusMathUtil.compare(amount,other) != 0){
            throw new HlsCusException(year + "筹资活动产生的现金流量净额 不等于 筹资活动现金流入小计 - 筹资活动现金流出小计");
        }

        switch (index) {
            case "5-1":
                year = financiaSheet.get(0).getAttribute_2();
                amount = new BigDecimal(check(financiaSheet.get(11).getAttribute_2())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(5).getAttribute_2())).doubleValue(), new BigDecimal(check(financiaSheet.get(10).getAttribute_2())).doubleValue());
                break;
            case "5-2":
                year = financiaSheet.get(0).getAttribute_3();
                amount = new BigDecimal(check(financiaSheet.get(11).getAttribute_3())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(5).getAttribute_3())).doubleValue(), new BigDecimal(check(financiaSheet.get(10).getAttribute_3())).doubleValue());
                break;
            case "5-3":
                year = financiaSheet.get(0).getAttribute_4();
                amount = new BigDecimal(check(financiaSheet.get(11).getAttribute_4())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(5).getAttribute_4())).doubleValue(), new BigDecimal(check(financiaSheet.get(10).getAttribute_4())).doubleValue());
                break;
            case "5-4":
                year = financiaSheet.get(0).getAttribute_5();
                amount = new BigDecimal(check(financiaSheet.get(11).getAttribute_5())).doubleValue();
                other = HlsCusMathUtil.sub(new BigDecimal(check(financiaSheet.get(5).getAttribute_5())).doubleValue(), new BigDecimal(check(financiaSheet.get(10).getAttribute_5())).doubleValue());
                break;
        }
        if (HlsCusMathUtil.compare(amount,other) != 0){
            throw new HlsCusException(year + "经营活动产生的现金流量净额 不等于 经营活动现金流入小计 - 经营活动现金流出小计");
        }

        switch (index) {
            case "6-1":
                year = financiaSheet.get(0).getAttribute_2();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_2())).doubleValue();
                other = HlsCusMathUtil.add(HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(11).getAttribute_2())).doubleValue(), new BigDecimal(check(financiaSheet.get(24).getAttribute_2())).doubleValue()),HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(34).getAttribute_2())).doubleValue(),new BigDecimal(check(financiaSheet.get(35).getAttribute_2())).doubleValue()));
                break;
            case "6-2":
                year = financiaSheet.get(0).getAttribute_3();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_3())).doubleValue();
                other = HlsCusMathUtil.add(HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(11).getAttribute_3())).doubleValue(), new BigDecimal(check(financiaSheet.get(24).getAttribute_3())).doubleValue()),HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(34).getAttribute_3())).doubleValue(),new BigDecimal(check(financiaSheet.get(35).getAttribute_3())).doubleValue()));
                break;
            case "6-3":
                year = financiaSheet.get(0).getAttribute_4();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_4())).doubleValue();
                other = HlsCusMathUtil.add(HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(11).getAttribute_4())).doubleValue(), new BigDecimal(check(financiaSheet.get(24).getAttribute_4())).doubleValue()),HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(34).getAttribute_4())).doubleValue(),new BigDecimal(check(financiaSheet.get(35).getAttribute_4())).doubleValue()));
                break;
            case "6-4":
                year = financiaSheet.get(0).getAttribute_5();
                amount = new BigDecimal(check(financiaSheet.get(36).getAttribute_5())).doubleValue();
                other = HlsCusMathUtil.add(HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(11).getAttribute_5())).doubleValue(), new BigDecimal(check(financiaSheet.get(24).getAttribute_5())).doubleValue()),HlsCusMathUtil.add(new BigDecimal(check(financiaSheet.get(34).getAttribute_5())).doubleValue(),new BigDecimal(check(financiaSheet.get(35).getAttribute_5())).doubleValue()));
                break;
        }
        if (HlsCusMathUtil.compare(amount,other) != 0){
            throw new HlsCusException(year + "现金及现金等价物净增加额 不等于 经营活动产生的现金流量净额 + 投资活动产生的现金流量净额 + 筹资活动产生的现金流量净额 + 汇率变动对现金及现金等价物的影响");
        }



    }

    private String check(String val) {
        return val == null ||  "".equals(val) ? "0" : val;
    }

    void calcStatementLn(IRequest iRequest,List<HlsFinStatementLn> statementLnList,String key,HlsBpFinancialHeader hlsBpFinancialHeader,Map yearMap) throws HlsCusException {

        for(int i = 0; i < statementLnList.size(); i++){
            Long finStatementHdId = null;
            if(i > 0) {
                //第一季度
                HlsFinStatementLn lnFir = new HlsFinStatementLn();
                lnFir.setLineType(key);
                lnFir.setLineSeqNumber(Long.valueOf(i));
                lnFir.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnFir.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_2() != null && isNumeric(statementLnList.get(i).getAttribute_2())) {
                    lnFir.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_2()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_2()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_2()).toString());
                } else {
                    logger.info("导入数据失败,请检查财报配置,第一季度");
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnFir.setFinStatementHdId(finStatementHdId);

                hlsFinStatementLnService.insert(iRequest, lnFir);

                //第二季度
                HlsFinStatementLn lnSec = new HlsFinStatementLn();
                lnSec.setLineType(key);
                lnSec.setLineSeqNumber(Long.valueOf(i));
                lnSec.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnSec.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_3() != null && isNumeric(statementLnList.get(i).getAttribute_3())) {
                    lnSec.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_3()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_3()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_3()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnSec.setFinStatementHdId(finStatementHdId);

                hlsFinStatementLnService.insert(iRequest, lnSec);

                //第三季度
                HlsFinStatementLn lnThird = new HlsFinStatementLn();
                lnThird.setLineType(key);
                lnThird.setLineSeqNumber(Long.valueOf(i));
                lnThird.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnThird.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_4() != null && isNumeric(statementLnList.get(i).getAttribute_4())) {
                    lnThird.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_4()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_4()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_4()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnThird.setFinStatementHdId(finStatementHdId);

                hlsFinStatementLnService.insert(iRequest, lnThird);

                //第四季度
                HlsFinStatementLn lnFour = new HlsFinStatementLn();
                lnFour.setLineType(key);
                lnFour.setLineSeqNumber(Long.valueOf(i));
                lnFour.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnFour.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_5() != null && isNumeric(statementLnList.get(i).getAttribute_5())) {
                    lnFour.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_5()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_5()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_5()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnFour.setFinStatementHdId(finStatementHdId);
                hlsFinStatementLnService.insert(iRequest, lnFour);

                //第五季度
                HlsFinStatementLn lnFive = new HlsFinStatementLn();
                lnFive.setLineType(key);
                lnFive.setLineSeqNumber(Long.valueOf(i));
                lnFive.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnFive.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_6() != null && isNumeric(statementLnList.get(i).getAttribute_6())) {
                    lnFive.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_6()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_6()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_6()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnFive.setFinStatementHdId(finStatementHdId);
                hlsFinStatementLnService.insert(iRequest, lnFive);

                //第六季度
                HlsFinStatementLn lnSix = new HlsFinStatementLn();
                lnSix.setLineType(key);
                lnSix.setLineSeqNumber(Long.valueOf(i));
                lnSix.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnSix.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_7() != null && isNumeric(statementLnList.get(i).getAttribute_7())) {
                    lnSix.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_7()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_7()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_7()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnSix.setFinStatementHdId(finStatementHdId);
                hlsFinStatementLnService.insert(iRequest, lnSix);

                //第七季度
                HlsFinStatementLn lnSeven = new HlsFinStatementLn();
                lnSeven.setLineType(key);
                lnSeven.setLineSeqNumber(Long.valueOf(i));
                lnSeven.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnSeven.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_8() != null && isNumeric(statementLnList.get(i).getAttribute_8())) {
                    lnSeven.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_8()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_8()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_8()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnSeven.setFinStatementHdId(finStatementHdId);
                hlsFinStatementLnService.insert(iRequest, lnSeven);

                //第八季度
                HlsFinStatementLn lnEight = new HlsFinStatementLn();
                lnEight.setLineType(key);
                lnEight.setLineSeqNumber(Long.valueOf(i));
                lnEight.setHeaderId(hlsBpFinancialHeader.getHeaderId());
                lnEight.setFinStatementItemName(statementLnList.get(i).getAttribute_1());
                if (statementLnList.get(i).getAttribute_9() != null && isNumeric(statementLnList.get(i).getAttribute_9())) {
                    lnEight.setAmount(Double.valueOf(statementLnList.get(i).getAttribute_9()));
                }
                if (yearMap.get(statementLnList.get(0).getAttribute_9()) != null) {
                    finStatementHdId = Long.valueOf(yearMap.get(statementLnList.get(0).getAttribute_9()).toString());
                } else {
                    throw new HlsCusException("导入数据失败,请检查财报配置!");
                }
                lnEight.setFinStatementHdId(finStatementHdId);
                hlsFinStatementLnService.insert(iRequest, lnEight);
            }
        }
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("-?[0-9]+(.[0-9E]+)?");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

    @Override
    public String getSourceDocumentCategory() {
        return SOURCE_DOCUMENT_CATEGORY;
    }

}
