package com.hand.hls.fnd.service.impl;

import com.github.pagehelper.PageHelper;
import com.google.common.collect.Lists;
import com.hand.hap.core.IRequest;
import com.hand.hap.excel.ExcelException;
import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;
import com.hand.hls.bp.mapper.HlsBpFinancialHeaderMapper;
import com.hand.hls.bp.service.HlsBeanRefUtilService;
import com.hand.hls.fnd.dto.*;
import com.hand.hls.fnd.mapper.*;
import com.hand.hls.fnd.service.HlsFinStatementHdService;
import com.hand.hls.fnd.service.HlsFinStatementLnService;
import com.hand.hls.utils.ExportExcelUtil;
import com.hand.hls.utils.HlsCusConstant;
import com.hand.hls.utils.MathUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by haibin on 2017/6/27.
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class HlsFinStatementHdServiceImpl extends BaseServiceImpl<HlsFinStatementHd> implements HlsFinStatementHdService {
    public static final String BASIC_FINANCE_INFO = HlsCusConstant.FinancialReportSheet.BASIC_FINANCE_INFO;
    public static final String BALANCE_SHEET = HlsCusConstant.FinancialReportSheet.BALANCE_SHEET;
    public static final String PROFIT_STATEMENT = HlsCusConstant.FinancialReportSheet.PROFIT_STATEMENT;
    public static final String CASH_FLOW_STATEMENT = HlsCusConstant.FinancialReportSheet.CASH_FLOW_STATEMENT;
    public static final String FINANCIAL_INDEX = HlsCusConstant.FinancialReportSheet.FINANCIAL_INDEX;
    public static final String FINANCIAL_INDICATOR = HlsCusConstant.FinancialReportSheet.FINANCIAL_INDICATOR;

    ThreadLocal<Long> message = new ThreadLocal<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;
    @Autowired
    private HlsFinStatementTmpltLnMapper hlsFinStatementTmpltLnMapper;
    @Autowired
    private HlsBeanRefUtilService hlsBeanRefUtilService;
    @Autowired
    private HlsFinStatementLnService hlsFinStatementLnService;
    @Autowired
    private HlsFinStatementHdMapper hlsFinStatementHdMapper;
    @Autowired
    private HlsFinStatementTmpltHdMapper hlsFinStatementTmpltHdMapper;
    @Autowired
    private HlsFinStatementLnMapper hlsFinStatementLnMapper;
    @Autowired
    private HlsBpFinancialHeaderMapper hlsBpFinancialHeaderMapper;

    @Override
    public List getList(HlsFinStatementTmpltLn hlsFinStatementTmpltLn, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
        fndInterfaceLines.setSheetName(hlsFinStatementTmpltLn.getSheetName());


        List<FndInterfaceLines> interfaceLinesList = fndInterfaceLinesMapper.fndInterfaceByHeaderIdAndSheetName(fndInterfaceLines);


        for (int i = 0; i < interfaceLinesList.size(); i++) {
            FndInterfaceLines interfaceLines = new FndInterfaceLines();
            interfaceLines = interfaceLinesList.get(i);
            int k = 2;
            try {
                String attr;
                while ((attr = BeanUtils.getProperty(interfaceLines, "attributes_" + k)) != null && !attr.equals(" ")) {
                    attr = attr.trim();
                    if (isNumber(attr) || isBigNumber(attr)) {
                        String amount;
                        BigDecimal db;
                        try {
                            db = new BigDecimal(attr);
                            amount = db.toString();
                        } catch (Exception e) {
                            amount = attr;
                        }
                        Map map = hlsBeanRefUtilService.getFieldValueMap(interfaceLines);
                        map.put("attributes_" + k, amount);
                        hlsBeanRefUtilService.setFieldValues(interfaceLines, map);
                    } else {
                        Map map = hlsBeanRefUtilService.getFieldValueMap(interfaceLines);
                        map.put("attributes_" + k, attr.trim());
                        hlsBeanRefUtilService.setFieldValues(interfaceLines, map);
                    }
                    k++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            interfaceLinesList.set(i, interfaceLines);
        }

        return interfaceLinesList;

    }

    @Override
    public List<Map<String, String>> getClounm(HlsFinStatementTmpltLn hlsFinStatementTmpltLn) {
        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
        fndInterfaceLines.setSheetName(hlsFinStatementTmpltLn.getSheetName());
        List<FndInterfaceLines> interfaceLinesList = fndInterfaceLinesMapper.fndInterColumnQuery(fndInterfaceLines);
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        FndInterfaceLines interfaceLines = new FndInterfaceLines();
        interfaceLines = interfaceLinesList.get(0);
        String attributes_1 = interfaceLines.getAttributes_1();
        String s = replaceBlank(attributes_1);
        if ("项目".equalsIgnoreCase(s) || "报告年月".equalsIgnoreCase(s)) {
            int k = 1;
            Map<String, String> column = new HashMap<String, String>();
            column.clear();
            column = hlsBeanRefUtilService.getFieldValueMap(interfaceLines);
            while (column.get("attributes_" + k) != null) {
                Map<String, String> map = new HashMap<String, String>();
                if (k < 2) {
                    map.put("attributes_" + k, column.get("attributes_" + k).trim());
                } else {
                    String data = column.get("attributes_" + k).trim();
                  /*  String datas[] = data.split("-");
                    String str = datas[0] + "-" + datas[1];*/
                    //String str = data.split("年")[0] + "-" + data.split("年")[1].split("月")[0];
                    map.put("attributes_" + k, data);
                }
                list.add(map);
                k++;
            }
        }
        return list;
    }


    @Override
    public boolean statementSubmit(IRequest iRequest, HlsFinStatementTmpltLn hlsFinStatementTmpltLn) {
        FndInterfaceLines lines = new FndInterfaceLines();
        lines.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
        lines.setSheetName(hlsFinStatementTmpltLn.getSheetName());
        //从接口表中取出对应的财报数据
        List<FndInterfaceLines> interfaceLinesList = fndInterfaceLinesMapper.fndInterColumnQuery(lines);
        FndInterfaceLines interfaceLines1 = new FndInterfaceLines();
        //当没有获取到数据时,直接返回
        if (interfaceLinesList.size() == 0) {
            return false;
        }
        //获取excel的表头(第一行)
        interfaceLines1 = interfaceLinesList.get(0);

        FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
        fndInterfaceLines.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
        List<FndInterfaceLines> interfaceLinesList1 = new ArrayList<>();
        List<FndInterfaceLines> interfaceLinesList2 = new ArrayList<>();
        List<FndInterfaceLines> interfaceLinesList3 = new ArrayList<>();
        List<FndInterfaceLines> interfaceLinesList4 = new ArrayList<>();
        List<FndInterfaceLines> interfaceLinesList5 = new ArrayList<>();
        List<FndInterfaceLines> interfaceLinesList6 = new ArrayList<>();
        //找到对应的行表的数据
        List<FndInterfaceLines> interfaceLines = fndInterfaceLinesMapper.fndInterfeLacinesQuery(fndInterfaceLines);
        //将数据添加到对应的list中
        interfaceLines.forEach(item -> {
            if (StringUtils.isNotBlank(item.getAttributes_1())) {
                String sheetName = replaceBlank(item.getSheetName());
                if (BALANCE_SHEET.equalsIgnoreCase(sheetName)) {
                    interfaceLinesList1.add(item);
                    return;
                } else if (PROFIT_STATEMENT.equalsIgnoreCase(sheetName)) {
                    interfaceLinesList2.add(item);
                    return;
                } else if (CASH_FLOW_STATEMENT.equalsIgnoreCase(sheetName)) {
                    interfaceLinesList3.add(item);
                    return;
                } else if (FINANCIAL_INDEX.equalsIgnoreCase(sheetName)) {
                    interfaceLinesList4.add(item);
                    return;
                } else if (BASIC_FINANCE_INFO.equalsIgnoreCase(sheetName)) {
                    interfaceLinesList5.add(item);
                    return;
                } else if (FINANCIAL_INDICATOR.equalsIgnoreCase(sheetName)) {
                    interfaceLinesList6.add(item);
                    return;
                }

            }
        });
        HlsFinStatementHd hlsFinStatementHd = new HlsFinStatementHd();
        hlsFinStatementHd.setFinStatementTempletHdId(hlsFinStatementTmpltLn.getFinStatementTempletHdId());
        hlsFinStatementHd.setBpId(hlsFinStatementTmpltLn.getBpId());
        hlsFinStatementHd.setCompanyId(hlsFinStatementTmpltLn.getCompanyId());
        HlsFinStatementTmpltHd hlsFinStatementTmpltHd = new HlsFinStatementTmpltHd();
        hlsFinStatementTmpltHd.setFinStatementTempletHdId(hlsFinStatementTmpltLn.getFinStatementTempletHdId());
        //获取财报模板头表
        List<HlsFinStatementTmpltHd> hlsFinStatementTmpltHdList = hlsFinStatementTmpltHdMapper.queryById(hlsFinStatementTmpltHd);
        hlsFinStatementTmpltHd = hlsFinStatementTmpltHdList.get(0);
        hlsFinStatementHd.setCurrencyCode(hlsFinStatementTmpltHd.getCurrencyCode());
        Long id1 = 0L;
        Long id2 = 0L;
        Long id3 = 0L;
        Long id4 = 0L;
        int k = 2;
        if (null != interfaceLines1) {
            Map<String, String> column = new HashMap<String, String>();
            column.clear();
            column = hlsBeanRefUtilService.getFieldValueMap(interfaceLines1);
            while (column.get("attributes_" + k) != null) {
                String data = column.get("attributes_" + k);
                //取出表头上的日期
                //判断日期格式
                Long data1 = null;
                Long data2 = null;
                String reportYear = replaceBlank(data).trim();
                //不含月 ，既取前四位为年份
                if (replaceBlank(data).trim().indexOf("月") < 0) {
                    data1 = Long.parseLong(replaceBlank(data).trim().substring(0, 4));
                } else {
                    //否则 截取 年 和 月之间的值为月份
                    int strStart = replaceBlank(data).trim().indexOf("年");
                    int strEnd = replaceBlank(data).trim().indexOf("月");
                    if (strStart < 0 && strEnd < 0) {
                        throw new IllegalArgumentException("报告年月的格式不对");
                    } else {
                        data1 = Long.parseLong(replaceBlank(data).trim().substring(0, 4));
                        data2 = Long.valueOf(replaceBlank(data).trim().substring(strStart, strEnd).substring(1));
                    }


                }

//                Long data1 = Long.parseLong(replaceBlank(data).trim().split("-")[0]);
//                Long data2 = Long.parseLong(replaceBlank(data).trim().split("-")[1]);
                HlsFinStatementHd hlsHd = new HlsFinStatementHd();
                hlsHd.setBpId(hlsFinStatementTmpltLn.getBpId());
                hlsHd.setFiscalYear(data1);
                hlsHd.setFiscalMonth(data2);
                hlsHd.setReportYear(reportYear);
                hlsHd.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
                String reportType = null;
                String ifFinancialCompany = null;
                String ifAudit = null;
                String auditInstitution = null;
                String auditResults = null;
                String ifApprovedBranch = null;

                if (k == 2) {
                    reportType = interfaceLinesList5.get(0).getAttributes_2();
                    ifFinancialCompany = interfaceLinesList5.get(1).getAttributes_2();
                    ifAudit = interfaceLinesList5.get(2).getAttributes_2();
                    auditInstitution = interfaceLinesList5.get(3).getAttributes_2();
                    auditResults = interfaceLinesList5.get(4).getAttributes_2();
                    ifApprovedBranch = interfaceLinesList5.get(5).getAttributes_2();


                } else if (k == 3) {
                    reportType = interfaceLinesList5.get(0).getAttributes_3();
                    ifFinancialCompany = interfaceLinesList5.get(1).getAttributes_3();
                    ifAudit = interfaceLinesList5.get(2).getAttributes_3();
                    auditInstitution = interfaceLinesList5.get(3).getAttributes_3();
                    auditResults = interfaceLinesList5.get(4).getAttributes_3();
                    ifApprovedBranch = interfaceLinesList5.get(5).getAttributes_3();

                } else if (k == 4) {
                    reportType = interfaceLinesList5.get(0).getAttributes_4();
                    ifFinancialCompany = interfaceLinesList5.get(1).getAttributes_4();
                    ifAudit = interfaceLinesList5.get(2).getAttributes_4();
                    auditInstitution = interfaceLinesList5.get(3).getAttributes_4();
                    auditResults = interfaceLinesList5.get(4).getAttributes_4();
                    ifApprovedBranch = interfaceLinesList5.get(5).getAttributes_4();
                } else if (k == 5) {
                    reportType = interfaceLinesList5.get(0).getAttributes_5();
                    ifFinancialCompany = interfaceLinesList5.get(1).getAttributes_5();
                    ifAudit = interfaceLinesList5.get(2).getAttributes_5();
                    auditInstitution = interfaceLinesList5.get(3).getAttributes_5();
                    auditResults = interfaceLinesList5.get(4).getAttributes_5();
                    ifApprovedBranch = interfaceLinesList5.get(5).getAttributes_5();
                }


                hlsHd.setReportType(reportType);
                hlsHd.setIfFinancialCompany(ifFinancialCompany);
                hlsHd.setIfAudit(ifAudit);
                hlsHd.setAuditInstitution(auditInstitution);
                hlsHd.setAuditResults(auditResults);
                hlsHd.setIfApprovedBranch(ifApprovedBranch);


                //根据bpId以及头id和年月,找到对应的财报头数据
                List<HlsFinStatementHd> hdList = hlsFinStatementHdMapper.hdQuery(hlsHd);
                HlsFinStatementHd hd = new HlsFinStatementHd();
                if (hdList.size() > 0) {
                    hd = hdList.get(0);
                    HlsFinStatementLn ln = new HlsFinStatementLn();
                    ln.setFinStatementHdId(hd.getFinStatementHdId());
                    //找到财报头对应的行表数据
                    List<HlsFinStatementLn> hlsFinStatementLns = hlsFinStatementLnMapper.lnQuery(ln);
                    //先遍历数据,如果行表存在对应年月的数据,则根据类型删除
                    hlsFinStatementLns.forEach(item -> {
                        String lineType = item.getLineType();
                        if (BALANCE_SHEET.equalsIgnoreCase(lineType) && interfaceLinesList1.size() > 0) {
                            hlsFinStatementLnService.deleteByPrimaryKey(item);
                        } else if (PROFIT_STATEMENT.equalsIgnoreCase(lineType) && interfaceLinesList2.size() > 0) {
                            hlsFinStatementLnService.deleteByPrimaryKey(item);
                        } else if (CASH_FLOW_STATEMENT.equalsIgnoreCase(lineType) && interfaceLinesList3.size() > 0) {
                            hlsFinStatementLnService.deleteByPrimaryKey(item);
                        } else if (FINANCIAL_INDEX.equalsIgnoreCase(lineType) && interfaceLinesList4.size() > 0) {
                            hlsFinStatementLnService.deleteByPrimaryKey(item);
                        } else if (BASIC_FINANCE_INFO.equalsIgnoreCase(lineType) && interfaceLinesList5.size() > 0) {
                            hlsFinStatementLnService.deleteByPrimaryKey(item);
                        } else if (FINANCIAL_INDICATOR.equalsIgnoreCase(lineType) && interfaceLinesList6.size() > 0) {
                            hlsFinStatementLnService.deleteByPrimaryKey(item);
                        }
                    });

                } else {
                    //如果头表不存在对应的财报信息,则将数据插入表中
                    hlsFinStatementHd.setFiscalYear(data1);
                    hlsFinStatementHd.setFiscalMonth(data2);
                    hlsFinStatementHd.set__status("add");
                    hlsFinStatementHd.setReportYear(reportYear);
                    hlsFinStatementHd.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
                    hlsFinStatementHd.setReportType(reportType);
                    hlsFinStatementHd.setIfFinancialCompany(ifFinancialCompany);
                    hlsFinStatementHd.setIfAudit(ifAudit);
                    hlsFinStatementHd.setAuditInstitution(auditInstitution);
                    hlsFinStatementHd.setAuditResults(auditResults);
                    hlsFinStatementHd.setIfApprovedBranch(ifApprovedBranch);


                    hd = self().insertSelective(iRequest, hlsFinStatementHd);
                }
                //赋值头表id
                if (2 == k) {
                    id1 = hd.getFinStatementHdId();
                } else if (3 == k) {
                    id2 = hd.getFinStatementHdId();
                } else if (4 == k) {
                    id3 = hd.getFinStatementHdId();
                } else if (5 == k) {
                    id4 = hd.getFinStatementHdId();
                }
                k++;
            }
        }
        if (id1 == 0 && id2 == 0 && id3 == 0 && id4 == 0) {
            return false;
        }
        List<HlsFinStatementLn> hlsLnList = new ArrayList<HlsFinStatementLn>();
        List<HlsFinStatementTmpltLn> list1 = new ArrayList<>();
        List<HlsFinStatementTmpltLn> list2 = new ArrayList<>();
        List<HlsFinStatementTmpltLn> list3 = new ArrayList<>();
        List<HlsFinStatementTmpltLn> list4 = new ArrayList<>();
        List<HlsFinStatementTmpltLn> list5 = new ArrayList<>();
        List<HlsFinStatementTmpltLn> list6 = new ArrayList<>();
        //找到模板行的值
        List<HlsFinStatementTmpltLn> templateLnList = hlsFinStatementTmpltLnMapper.query(hlsFinStatementTmpltLn);
        templateLnList.forEach(item -> {
            String lineType = replaceBlank(item.getLineType());
            if (BALANCE_SHEET.equalsIgnoreCase(lineType)) {
                list1.add(item);
                return;
            } else if (PROFIT_STATEMENT.equalsIgnoreCase(lineType)) {
                list2.add(item);
                return;
            } else if (CASH_FLOW_STATEMENT.equalsIgnoreCase(lineType)) {
                list3.add(item);
                return;
            } else if (FINANCIAL_INDEX.equalsIgnoreCase(lineType)) {
                list4.add(item);
                return;
            } else if (BASIC_FINANCE_INFO.equalsIgnoreCase(lineType)) {
                list5.add(item);
                return;
            } else if (FINANCIAL_INDICATOR.equalsIgnoreCase(lineType)) {
                list6.add(item);
                return;
            }
        });

        if (interfaceLinesList1.size() > 0) {
            if (interfaceLinesList1.size() != (list1.size())) {
                throw new IllegalArgumentException("模版不匹配");
            }
            List<HlsFinStatementLn> list = dealAmount(list1, interfaceLinesList1, k, id1, id2, id3, id4);
            hlsLnList.addAll(list);
        }
        if (interfaceLinesList2.size() > 0) {
            if (interfaceLinesList2.size() != (list2.size())) {
                throw new IllegalArgumentException("模版不匹配");
            }
            List<HlsFinStatementLn> list = dealAmount(list2, interfaceLinesList2, k, id1, id2, id3, id4);
            hlsLnList.addAll(list);
        }
        if (interfaceLinesList3.size() > 0) {
            if (interfaceLinesList3.size() != (list3.size())) {
                throw new IllegalArgumentException("模版不匹配");
            }
            List<HlsFinStatementLn> list = dealAmount(list3, interfaceLinesList3, k, id1, id2, id3, id4);
            hlsLnList.addAll(list);
        }
        if (interfaceLinesList4.size() > 0) {
            if (interfaceLinesList4.size() != (list4.size())) {
                throw new IllegalArgumentException("模版不匹配");
            }
            List<HlsFinStatementLn> list = dealAmount(list4, interfaceLinesList4, k, id1, id2, id3, id4);
            hlsLnList.addAll(list);
        }
        if (interfaceLinesList5.size() > 0) {
            if (interfaceLinesList5.size() != (list5.size())) {
                throw new IllegalArgumentException("模版不匹配");
            }
            List<HlsFinStatementLn> list = dealAmount(list5, interfaceLinesList5, k, id1, id2, id3, id4);
            hlsLnList.addAll(list);
        }
        if (interfaceLinesList6.size() > 0) {
            if (interfaceLinesList6.size() != (list6.size())) {
                throw new IllegalArgumentException("模版不匹配");
            }
            List<HlsFinStatementLn> list = dealAmount(list6, interfaceLinesList6, k, id1, id2, id3, id4);
            hlsLnList.addAll(list);
        }
        List<HlsFinStatementLn> lnList = new ArrayList<HlsFinStatementLn>();
        if (hlsLnList.size() == 0) {
            return false;
        }
        for (int j = 0; j < hlsLnList.size(); j++) {
            HlsFinStatementLn finStatementLn = hlsLnList.get(j);
            if (finStatementLn.getAmount() != null) {//导入时,先根据excel上对应的字段是否有amount值存储
                BigDecimal db = new BigDecimal(finStatementLn.getAmount());
                BigDecimal amount = db.setScale(4, BigDecimal.ROUND_HALF_UP);
                //String amount = db.toPlainString();
               /* finStatementLn.setAmount(Double.parseDouble(amount));
                finStatementLn.setFinalAmount(Double.parseDouble(amount));*/
                finStatementLn.setAmount(Double.parseDouble(amount.toString()));
                finStatementLn.setFinalAmount(Double.parseDouble(amount.toString()));
                finStatementLn.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
            }
            finStatementLn.setHeaderId(hlsFinStatementTmpltLn.getHeaderId());
            hlsFinStatementLnService.insertSelective(iRequest, finStatementLn);
        }
        Map<String, Long> map = new HashMap<>();
        map.put("id1", id1);
        map.put("id2", id2);
        map.put("id3", id3);
        map.put("id4", id4);
        boolean flag = statementLnUpdate(iRequest, map, hlsFinStatementTmpltLn.getFinStatementTempletHdId());
        if (!flag) {
            return false;
        }
        return true;
    }

    @Override
    public List<HlsFinStatementHd> hdQuery(HlsFinStatementHd hlsFinStatementHd, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsFinStatementHd> list = hlsFinStatementHdMapper.hdQuery(hlsFinStatementHd);
        return list;
    }

    @Override
    public List<HlsFinStatementHd> hdDistinctQuery(HlsFinStatementHd hlsFinStatementHd, int page, int pagesize) {
        PageHelper.startPage(page, pagesize);
        List<HlsFinStatementHd> list = hlsFinStatementHdMapper.hdDistinctQuery(hlsFinStatementHd);
        return list;
    }

    @Override
    public List<HlsFinStatementHd> hdGuaranQuery(HlsFinStatementHd hlsFinStatementHd, int page, int pagesize) {
        return null;
    }

    @Override
    public List<HlsFinStatementHd> hdColumnQuery(HlsFinStatementHd hlsFinStatementHd) {
        return null;
    }

    @Override
    public boolean statementDelete(IRequest iRequest, HlsFinStatementTmpltLn hlsFinStatementTmpltLn) {
        return false;
    }

    @Override
    public boolean statementLnDelete(IRequest iRequest, HlsFinStatementHd hlsFinStatementHd) {
        return false;
    }

    public static final String  ASSET_SHEET= "资产负债表";

    private static final String  PROFIT_SHEET= "利润表";

    private static final String  ADDITIONAL_INFO_SHEET= "补充资料表";

    private static final String  CASH_FLOW_SHEET= "现金流量表";

    private static final List<String> rowInfoList = Lists.newArrayList("非流动资产", "流动负债", "非流动负债", "所有者权益","投资活动产生的现金流量","筹资活动产生的现金流量","经营活动产生的现金流量");

    @Override
    public void financialVertificate(IRequest iRequest, Long hdId) throws ExcelException, SQLException, ParseException {
//        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        final List<String> sheetList = Lists.newArrayList("资产负债表", "利润表", "现金流量表", "补充资料表");
        //获取接口表数据
        HlsFinStatementLn hlsFinStatementLn = new HlsFinStatementLn();
        hlsFinStatementLn.setHeaderId(hdId);
        String excelCode = "";
        HlsBpFinancialHeader hlsBpFinancialHeader = new HlsBpFinancialHeader();
        hlsBpFinancialHeader.setHeaderId(hdId);
        hlsBpFinancialHeader = hlsBpFinancialHeaderMapper.selectByPrimaryKey(hlsBpFinancialHeader);
        List<HlsBpFinancialHeader> hlsBpFinancialHeaderList = hlsBpFinancialHeaderMapper.selectBpExportInfo(hlsBpFinancialHeader);
        if(hlsBpFinancialHeaderList.size() == 1){
            excelCode = hlsBpFinancialHeaderList.get(0).getExcelCode();
        }

        Map<String,Double> profitSheetAmount = new HashMap<>();

        Map<String,Double> additionalInfoAmount = new HashMap<>();

        if(!"FINANCE_TEMPLATE_05".equals(excelCode)) {
            for (int k = 0; k < sheetList.size(); k++) {

                String sheetName = sheetList.get(k);
                hlsFinStatementLn.setLineType(sheetName);

                HlsFinStatementHd hlsFinStatementHd = new HlsFinStatementHd();
                hlsFinStatementHd.setHeaderId(hdId);
                List<HlsFinStatementHd> statementHdList = hlsFinStatementHdMapper.hdQueryBasicBp(hlsFinStatementHd);


                for (HlsFinStatementHd statementHd : statementHdList) {

                    //资产合计
                    Double assetTotal = 0D;
                    //负债合计
                    Double liaTotal = 0D;
                    //所有者权益(或股东权益）合计
                    Double ownerTotal = 0D;
                    //期末现金及现金等价物余额
                    Double balanceCaseAmount = 0D;
                    //现金及现金等价物净增加额
                    Double netIncreaseAmount = 0D;
                    //期初现金及现金等价物余额
                    Double BalanceBeginningAmount = 0D;


                    //财报年份
                    String reportYear = statementHd.getReportYear();

                    //查询对应年份对应sheet的行信息
                    hlsFinStatementLn.setFinStatementHdId(statementHd.getFinStatementHdId());
                    List<HlsFinStatementLn> lnList = hlsFinStatementLnMapper.lnQuery(hlsFinStatementLn);

                    for (int i = 1; i < lnList.size(); i++) {

                        if (checkValidate(lnList.get(i).getFinStatementItemName()) && reportYear != null) {
                            validate("excel" + sheetName + "表格：项目信息，第" + (lnList.get(i).getLineSeqNumber() + 1) + "行不能为空", lnList.get(i).getFinStatementItemName());

                            validate("excel" + sheetName + "表格：财报年份为" + statementHd.getReportYear() + "第" + (lnList.get(i).getLineSeqNumber() + 1) + "行不能为空", lnList.get(i).getAmount());

                        }
                        //资产（资产合计） = 负债（负债合计）+ 所有者权益（所有者合计）(单位为（万元））
                        if (ASSET_SHEET.equalsIgnoreCase(sheetName)) {
                            if ("FINANCE_TEMPLATE_01".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 43) {
                                    assetTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 80) {
                                    liaTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 95) {
                                    ownerTotal = lnList.get(i).getAmount();
                                }
                            }
                            if ("FINANCE_TEMPLATE_02".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 38) {
                                    assetTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 73) {
                                    liaTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 88) {
                                    ownerTotal = lnList.get(i).getAmount();
                                }
                            }
                            if ("FINANCE_TEMPLATE_03".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 36) {
                                    assetTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 64) {
                                    liaTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 78) {
                                    ownerTotal = lnList.get(i).getAmount();
                                }
                            }
                            if ("FINANCE_TEMPLATE_04".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 31) {
                                    assetTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 57) {
                                    liaTotal = lnList.get(i).getAmount();
                                }
                                if (lnList.get(i).getLineSeqNumber() == 71) {
                                    ownerTotal = lnList.get(i).getAmount();
                                }
                            }
                        }

                        if (PROFIT_SHEET.equalsIgnoreCase(sheetName)) {
                            if ("FINANCE_TEMPLATE_01".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 18) {
                                    profitSheetAmount.put(reportYear, lnList.get(i).getAmount());
                                }
                            }
                            if ("FINANCE_TEMPLATE_02".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 18) {
                                    profitSheetAmount.put(reportYear, lnList.get(i).getAmount());
                                }
                            }
                            if ("FINANCE_TEMPLATE_03".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 8) {
                                    profitSheetAmount.put(reportYear, lnList.get(i).getAmount());
                                }
                            }
                            if ("FINANCE_TEMPLATE_04".equals(excelCode)) {
                                if (lnList.get(i).getLineSeqNumber() == 8) {
                                    profitSheetAmount.put(reportYear, lnList.get(i).getAmount());
                                }
                            }
                        }

                        if(CASH_FLOW_SHEET.equalsIgnoreCase(sheetName) && reportYear != null){
                            if(lnList.get(i).getFinStatementItemName().contains("六、期末现金及现金等价物余额")){
                                balanceCaseAmount = lnList.get(i).getAmount();
                            }
                            if(lnList.get(i).getFinStatementItemName().contains("现金及现金等价物净增加额")){
                                netIncreaseAmount = lnList.get(i).getAmount();
                            }
                            if(lnList.get(i).getFinStatementItemName().contains("期初现金及现金等价物余额")){
                                BalanceBeginningAmount = lnList.get(i).getAmount();
                            }
                        }
                    }
                    if (ADDITIONAL_INFO_SHEET.equalsIgnoreCase(sheetName)) {
                        for (int i = 0; i < lnList.size(); i++) {
                            if (lnList.get(i).getLineSeqNumber() == 1) {
                                additionalInfoAmount.put(reportYear, lnList.get(i).getAmount());
                            }
                        }
                    }

                    if (ASSET_SHEET.equalsIgnoreCase(sheetName) && reportYear != null) {
                        Double result = MathUtil.sub(assetTotal, MathUtil.add(liaTotal, ownerTotal));
                        if (result.compareTo(0D) != 0) {
                            throw new RuntimeException("excel" + ASSET_SHEET + "表格 " + statementHd.getReportYear() + " 资产合计应等于负债和所有者权益（或股东权益）合计");
                        }
                    }
                    if (CASH_FLOW_SHEET.equalsIgnoreCase(sheetName) && reportYear != null) {
                        Double result = MathUtil.sub(balanceCaseAmount, MathUtil.add(netIncreaseAmount, BalanceBeginningAmount));
                        if (result.compareTo(0D) != 0) {
                            throw new RuntimeException("excel" + CASH_FLOW_SHEET + "表格 " + statementHd.getReportYear() + " 期末现金及现金等价物余额应等于现金及现金等价物净增加额加期初现金及现金等价物余额");
                        }
                    }
                }

                //数据规范性校验 项目名称校验
                /*HlsFinStatementTmpltLn hlsFinStatementTmpltLn = new HlsFinStatementTmpltLn();
                hlsFinStatementTmpltLn.setLineType(sheetName);
                hlsFinStatementTmpltLn.setFinStatementItemName(attributes_1);
                hlsFinStatementTmpltLn.setFinStatementTempletHdId(templetHdId);
                List<HlsFinStatementTmpltLn> hlsFinStatementTmpltLnList = hlsFinStatementTmpltLnMapper.queryByItemName(hlsFinStatementTmpltLn);
                if (hlsFinStatementTmpltLnList.isEmpty()) {
                    throw new RuntimeException("excel" + sheetName + "表格第" + lineNumber + "行项目没有在系统中维护！");
                }*/

            }
        }
        if(!additionalInfoAmount.equals(profitSheetAmount)){
            throw new RuntimeException("财报中补充资料表的费用化利息支出与利润表的利息费用不相等！");
        }

    }

    private double getLineValue(){
        return 0D;
    }

    Boolean checkValidate(String str){

        for(int i = 0; i < rowInfoList.size(); i++){
            if(str.indexOf(rowInfoList.get(i)) != -1){
                return false;
            }
        }
        return true;
    }

    @Override
    public void exportFinancialReport(HttpServletRequest request, HttpServletResponse response, HlsFinStatementHd hlsFinStatementHd) throws IOException, InvocationTargetException, IllegalAccessException {
        final List<String> sheetList = Lists.newArrayList("资产负债表", "利润表", "现金流量表", "补充资料表");
        XSSFWorkbook xwork = new XSSFWorkbook();
        for(int i = 0 ;i<sheetList.size();i++){
            String sheetName = sheetList.get(i);
            FndInterfaceLines fndInterfaceLines = new FndInterfaceLines();
            fndInterfaceLines.setHeaderId(330L);
            fndInterfaceLines.setSheetName(sheetName);
            fndInterfaceLines.setReadLine(0L);
            List<FndInterfaceLines> fndInterfaceLinesList =  fndInterfaceLinesMapper.fndInterfaceLinesDetailQuery(fndInterfaceLines);

            List<HlsFinStatementHd> reportYearList =  hlsFinStatementHdMapper.hdReportYearQuery(hlsFinStatementHd);
            String attributes_2 = reportYearList.get(0).getReportYear();
            String attributes_3 = reportYearList.get(1).getReportYear();
            String attributes_4 = reportYearList.get(2).getReportYear();
            String attributes_5 = reportYearList.get(3).getReportYear();
            XSSFSheet sheet = xwork.createSheet(sheetName);
            final List<String> colNameList = Lists.newArrayList(
                    "项目",attributes_2,attributes_3,attributes_4,attributes_5);
            final List<String> colGetMethods = Lists.newArrayList(
                    "attributes_1","attributes_2", "attributes_3", "attributes_4", "attributes_5");
            int dataRowNum = ExportExcelUtil.createCommonExcelHead(xwork, sheet, null, colNameList);
            for(FndInterfaceLines fndInterfaceLines1:fndInterfaceLinesList){
                //创建列
                XSSFRow row = sheet.createRow(dataRowNum++);
                ExportExcelUtil.setData(xwork,sheet, row, fndInterfaceLines1,colGetMethods);
            }
        }


        ExportExcelUtil.IOWrite(xwork, null,request,response, "财报信息导出");

    }

    // 校验字段是否有值
    public static void validate(String message, Object... objects) {
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] == null || "".equals(objects[i]) || "null".equals(objects[i])) {
                throw new RuntimeException(message);
            }
        }
    }

    // 校验单元格字符位数字
    public static void validateStr(String message, Object... objects) {
        String reg = "^[0-9]+(.[0-9]+)?$";
        String obj = String.valueOf(objects[0]);
        Boolean flag =  obj.matches(reg);
        if(!flag){
            if(!"-".equalsIgnoreCase(replaceBlank(obj))){
                throw new RuntimeException(message);
            }
        }

      /*  for(int i = 0 ;i <objects.length ;i++){
            String str = String.valueOf(objects[i]);

            for(int k=str.length();--k>=0; ){
                int chr = str.charAt(k);
                if(chr<48 || chr>57){
                    if(!"-".equalsIgnoreCase(replaceBlank(str))){
                        if(str.matches(reg)){
                            return ;
                        }else{
                            throw new RuntimeException(message);
                        }

                    }
                }
            }
        }*/

    }

    public boolean statementLnUpdate(IRequest iRequest, Map<String, Long> map, Long finStatementTempletHdId) {
        Long id1 = map.get("id1");
        Long id2 = map.get("id2");
        Long id3 = map.get("id3");
        Long id4 = map.get("id4");
        HlsFinStatementTmpltLn hlsFinStatementTmpltLn = new HlsFinStatementTmpltLn();
        hlsFinStatementTmpltLn.setFinStatementTempletHdId(finStatementTempletHdId);
        hlsFinStatementTmpltLn.setLineType(BALANCE_SHEET);
        List<HlsFinStatementTmpltLn> list1 = hlsFinStatementTmpltLnMapper.queryByFormula(hlsFinStatementTmpltLn);
        hlsFinStatementTmpltLn.setLineType(PROFIT_STATEMENT);
        List<HlsFinStatementTmpltLn> list2 = hlsFinStatementTmpltLnMapper.queryByFormula(hlsFinStatementTmpltLn);
        hlsFinStatementTmpltLn.setLineType(CASH_FLOW_STATEMENT);
        List<HlsFinStatementTmpltLn> list3 = hlsFinStatementTmpltLnMapper.queryByFormula(hlsFinStatementTmpltLn);
        hlsFinStatementTmpltLn.setLineType(FINANCIAL_INDEX);
        List<HlsFinStatementTmpltLn> list4 = hlsFinStatementTmpltLnMapper.queryByFormula(hlsFinStatementTmpltLn);
        if (id1 != 0) {
            boolean flag = judgeIds(iRequest, list1, list2, list3, list4, id1);
            if (!flag) {
                return false;
            }
        }
        if (id2 != 0) {
            boolean flag = judgeIds(iRequest, list1, list2, list3, list4, id2);
            if (!flag) {
                return false;
            }
        }
        if (id3 != 0) {
            boolean flag = judgeIds(iRequest, list1, list2, list3, list4, id3);
            if (!flag) {
                return false;
            }
        }
        if (id4 != 0) {
            boolean flag = judgeIds(iRequest, list1, list2, list3, list4, id4);
            if (!flag) {
                return false;
            }
        }
        return true;
    }
    //
    public boolean judgeIds(IRequest iRequest, List<HlsFinStatementTmpltLn> list1, List<HlsFinStatementTmpltLn> list2, List<HlsFinStatementTmpltLn> list3, List<HlsFinStatementTmpltLn> list4, Long ids) {
        HlsFinStatementLn hlsFinStatementLn = new HlsFinStatementLn();
        hlsFinStatementLn.setFinStatementHdId(ids);
        hlsFinStatementLn.setLineType(BALANCE_SHEET);
        List<HlsFinStatementLn> lnList1 = hlsFinStatementLnMapper.lnQuery(hlsFinStatementLn);
        Map<String, List<HlsFinStatementLn>> totalList = new HashMap<>();
        message.set(ids);
        if (lnList1.size() > 0) {
            totalList.put(BALANCE_SHEET, lnList1);
            boolean flag = calculateAmount(iRequest, list1, lnList1, totalList);
            if (!flag) {
                return false;
            }
        }
        hlsFinStatementLn.setLineType(PROFIT_STATEMENT);
        List<HlsFinStatementLn> lnList2 = hlsFinStatementLnMapper.lnQuery(hlsFinStatementLn);
        if (lnList2.size() > 0) {
            totalList.put(PROFIT_STATEMENT, lnList2);
            boolean flag = calculateAmount(iRequest, list2, lnList2, totalList);
            if (!flag) {
                return false;
            }
        }
        hlsFinStatementLn.setLineType(CASH_FLOW_STATEMENT);
        List<HlsFinStatementLn> lnList3 = hlsFinStatementLnMapper.lnQuery(hlsFinStatementLn);
        if (lnList3.size() > 0) {
            totalList.put(CASH_FLOW_STATEMENT, lnList3);
            boolean flag = calculateAmount(iRequest, list3, lnList3, totalList);
            if (!flag) {
                return false;
            }
        }
        hlsFinStatementLn.setLineType(FINANCIAL_INDEX);
        List<HlsFinStatementLn> lnList4 = hlsFinStatementLnMapper.lnQuery(hlsFinStatementLn);
        if (lnList4.size() > 0) {
            totalList.put(FINANCIAL_INDEX, lnList4);
            boolean flag = calculateAmount(iRequest, list4, lnList4, totalList);
            if (!flag) {
                return false;
            }
        }
        return true;
    }

    public boolean calculateAmount(IRequest iRequest, List<HlsFinStatementTmpltLn> list, List<HlsFinStatementLn> lnList, Map<String, List<HlsFinStatementLn>> totalList) {
        list = list.stream().sorted((a, b) -> Long.compare(a.getFormulaPriority(), b.getFormulaPriority())).collect(Collectors.toList());//根据计算优先值从小到大排序
        for (int i = 0; i < list.size(); i++) {
            HlsFinStatementTmpltLn hlsFinStatementTmpltLn = list.get(i);
            for (int j = 0; j < lnList.size(); j++) {
                HlsFinStatementLn hlsFinStatementLn = lnList.get(j);
                if (hlsFinStatementLn == null)//当获取的元素为空时,直接进入下次循环
                    continue;
                if (hlsFinStatementTmpltLn.getLineSeqNumber() == hlsFinStatementLn.getLineSeqNumber()) {
                    if (hlsFinStatementLn.getAmount() == null) {
                        if (-1 != hlsFinStatementTmpltLn.getFormulaText().indexOf("[") && -1 != hlsFinStatementTmpltLn.getFormulaText().indexOf("]")) {
//                            List<String> elements = replaceByRegular(hlsFinStatementTmpltLn.getFormulaText());//获取对应的元素
//                            List<Double> strList = getEleValueByRegular(elements, lnList, totalList);
//                            //List<String> strList = extractMessage(hlsFinStatementTmpltLn.getFormulaText());
//                            List<String> signList = extractMessageByRegular(hlsFinStatementTmpltLn.getFormulaText());
//                            Double sum = 0d;
//                            for (int a = 0; a < strList.size(); a++) {
//                                if (a == 0) {
//                                    sum = strList.get(0);
//                                } else {
//                                    if (signList.get(a - 1) != null) {
//                                        Double param = strList.get(a);
//                                        if (signList.get(a - 1).equals("+")) {
//                                            sum = CalculateUtil.add(sum, param);
//                                        }
//                                        if (signList.get(a - 1).equals("-")) {
//                                            sum = CalculateUtil.sub(sum, param);
//                                        }
//                                    }
//                                }
//                            }
                            //元素运算改为js引擎
                            Double sum = getCalculateResult(hlsFinStatementTmpltLn.getFormulaText(), lnList, totalList);
                            hlsFinStatementLn.setAmount(sum);
                            hlsFinStatementLn.setFinalAmount(sum);
                            lnList.set(j, hlsFinStatementLn);
                            hlsFinStatementLn.set__status("update");
                            HlsFinStatementLn ln = hlsFinStatementLnService.updateByPrimaryKeySelective(iRequest, hlsFinStatementLn);
                            if (ln.getAmount() != sum) {
                                return false;
                            }
                        }
                    }
                    break;
                }
            }
        }
        return true;
    }

    /**
     * 获取公式计算的结果
     *
     * @param formulaText
     * @param lnList
     * @param totalList
     * @return
     */
    private Double getCalculateResult(String formulaText, List<HlsFinStatementLn> lnList, Map<String, List<HlsFinStatementLn>> totalList) {
        StringBuilder sb = new StringBuilder(formulaText);
        String element;
        String elementValue;
        while (sb.lastIndexOf("[") >= 0 && sb.lastIndexOf("]") > 0) {
            element = sb.subSequence(sb.lastIndexOf("[") + 1, sb.lastIndexOf("]")).toString();
            List<String> list = new ArrayList<>();
            list.add(element);
            List<Double> eleValueByRegular = getEleValueByRegular(list, lnList, totalList);
            elementValue = String.valueOf(eleValueByRegular.stream().findAny().orElse(0D));
            sb.replace(sb.lastIndexOf("["), sb.lastIndexOf("]") + 1, String.valueOf(elementValue));
        }
        Double result = null;
        try {
            result = excuetExpressionByJs(sb.toString());
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        //当计算到无穷值时
        if (new Double("Infinity").compareTo(result) == 0) {
            return null;
        }
        if (new Double("-Infinity").compareTo(result) == 0) {
            return null;
        }
        return result;
    }

    /**
     * @param elements
     * @param lnList
     * @param totalList
     * @return
     */
    private List<Double> getEleValueByRegular(List<String> elements, List<HlsFinStatementLn> lnList, Map<String, List<HlsFinStatementLn>> totalList) {
        List<Double> result = new ArrayList<>();
        elements.forEach(item -> {
            String[] splitArr = item.split("\\.");
            List<String> splitList = Arrays.asList(splitArr);
            if (splitList.size() == 3) {//当根据.标志截取的字符串数组的大小为3时,表示层级为3
                Long hdId = message.get();
                HlsFinStatementHd hlsFinStatementHd = hlsFinStatementHdMapper.selectByPrimaryKey(hdId);
                HlsFinStatementHd queryParam = new HlsFinStatementHd();
                queryParam.setBpId(hlsFinStatementHd.getBpId());
                queryParam.setCompanyId(hlsFinStatementHd.getCompanyId());
                List<HlsFinStatementHd> hlsFinStatementHds = hlsFinStatementHdMapper.hdQueryByTime(queryParam);
                String location = splitList.get(0).substring(1, splitList.get(0).length());
                String key = splitList.get(1);
                String position = splitList.get(2).substring(0, splitList.get(2).length());
                Integer locationIndex = Integer.parseInt(location.substring(0, location.length()));
                if (!location.startsWith("a")) {
                    locationIndex = -locationIndex;
                }
                Integer index = null;
                for (int i = 0; i < hlsFinStatementHds.size(); i++) {
                    if (hlsFinStatementHds.get(i).getFinStatementHdId().compareTo(hlsFinStatementHd.getFinStatementHdId()) == 0) {
                        index = i + locationIndex;
                        break;
                    }
                }
                if (index >= 0 && index < hlsFinStatementHds.size()) {
                    HlsFinStatementHd finStatementHd = hlsFinStatementHds.get(index);
                    Long finStatementHdId = finStatementHd.getFinStatementHdId();
                    HlsFinStatementLn finStatementLn = new HlsFinStatementLn();
                    finStatementLn.setFinStatementHdId(finStatementHdId);
                    finStatementLn.setLineType(key.toUpperCase());
                    List<HlsFinStatementLn> select = hlsFinStatementLnMapper.select(finStatementLn);
                    List<HlsFinStatementLn> collect = select.stream().sorted((a, b) -> Long.compare(a.getLineSeqNumber(), b.getLineSeqNumber())).collect(Collectors.toList());
                    String amountStr = getAmount(collect, position);
                    Double amount = Double.parseDouble(amountStr);
                    result.add(amount);
                }

            } else if (splitList.size() == 2) {//当根据.标志截取的字符串数组的大小为2时,表示层级为2
                String key = splitList.get(0);
                String position = splitList.get(1);
                List<HlsFinStatementLn> hlsFinStatementLns = totalList.get(key.toUpperCase());
                String amountStr = getAmount(hlsFinStatementLns, position);
                Double amount = Double.parseDouble(amountStr);
                result.add(amount);

            } else if (splitList.size() == 1) {//当根据.标志截取的字符串数组的大小为1时,表示层级为1
                String s = splitList.stream().findAny().get();
                String amount = getAmount(lnList, s);
                result.add(Double.parseDouble(amount));
//                int start = 0;
//                int end = 0;
//                for (int i = 0; i < s.length(); i++) {
//                    if (s.charAt(i) == '[') {
//                        start = i;
//                    } else if (s.charAt(i) == ']') {
//                        end = i;
//                        Double amount = Double.parseDouble(getAmount(lnList, s.substring(start + 1, end)));
//                        result.add(amount);
//                        break;
//                    }
//                }
            } else {
                return;
            }

        });
        return result;
    }

    public List<String> extractMessage(String msg) {
        List<String> list = new ArrayList<String>();
        int start = 0;
        int startFlag = 0;
        int endFlag = 0;

        for (int i = 0; i < msg.length(); i++) {
            if (msg.charAt(i) == '[') {
                startFlag++;
                if (startFlag == endFlag + 1) {
                    start = i;
                }
            } else if (msg.charAt(i) == ']') {
                endFlag++;
                if (endFlag == startFlag) {
                    list.add(msg.substring(start + 1, i));
                }
            } else {

            }
        }
        return list;
    }

    /**
     * @param list               模板行
     * @param interfaceLinesList 接口表中存储的值
     * @param k                  字段
     * @param id1
     * @param id2
     * @param id3
     * @param id4
     * @return
     */
    public List<HlsFinStatementLn> dealAmount(List<HlsFinStatementTmpltLn> list, List<FndInterfaceLines> interfaceLinesList, int k, Long id1, Long id2, Long id3, Long id4) {
        List<HlsFinStatementLn> lnList = new ArrayList<HlsFinStatementLn>();
        //i从1开始,因为第一行的数据存储的是表头
        for (int i = 1; i < interfaceLinesList.size(); i++) {
            FndInterfaceLines interFace = interfaceLinesList.get(i);
            String attributes_1 = interFace.getAttributes_1();
            String lineSeq = replaceBlank(attributes_1);
            List temp = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                HlsFinStatementTmpltLn item = list.get(j);
                if (lineSeq.equalsIgnoreCase(item.getFinStatementItemName())) {
                    Map map = hlsBeanRefUtilService.getFieldValueMap(item);
                    //循环保存sheet页中对应的4列
                    for (int b = 2; b < k; b++) {
                        HlsFinStatementLn result = new HlsFinStatementLn();
                        String attr = null;
                        try {
                            attr = BeanUtils.getProperty(interFace, "attributes_" + b);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        if (null == attr || "".equals(attr.trim())) {
                            map.put("sheetName", interFace.getSheetName());
                        } else {
                            if (isNumber(attr) || isBigNumber(attr)) {
                                String num = attr;
                                BigDecimal db = new BigDecimal(num.trim());
                                String amount = db.toPlainString();
                                map.put("amount", amount);
                            } else {
                                map.put("amount", null);
                            }
                            map.put("sheetName", interFace.getSheetName().trim());
                        }
                        hlsBeanRefUtilService.setFieldValues(result, map);
                        Double amount = result.getAmount();
                        //Double amount = result.getAmount() != null ? result.getAmount() : 0D;
                        if (2 == b) {
                            result.setFinStatementHdId(id1);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_2(amount.toString());
                            }
                        } else if (3 == b) {
                            result.setFinStatementHdId(id2);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_3(amount.toString());
                            }
                        } else if (4 == b) {
                            result.setFinStatementHdId(id3);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_4(amount.toString());
                            }
                        } else if (5 == b) {
                            result.setFinStatementHdId(id4);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_5(amount.toString());
                            }
                        }
                        temp.add(result);
                        lnList.add(result);
                        map.put("amount", null);
                    }
                }
                if (temp.isEmpty() && j == list.size() - 1) {
                    Map map = hlsBeanRefUtilService.getFieldValueMap(item);
                    //循环保存sheet页中对应的4列
                    for (int b = 2; b < k; b++) {
                        HlsFinStatementLn result = new HlsFinStatementLn();
                        String attr = null;
                        try {
                            attr = BeanUtils.getProperty(interFace, "attributes_" + b);

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                        if (null == attr || "".equals(attr.trim())) {
                            map.put("sheetName", interFace.getSheetName());
                        } else {
                            if (isNumber(attr) || isBigNumber(attr)) {
                                String num = attr;
                                BigDecimal db = new BigDecimal(num.trim());
                                String amount = db.toPlainString();
                                map.put("amount", amount);
                            } else {
                                map.put("amount", null);
                            }
                            map.put("sheetName", interFace.getSheetName().trim());
                        }
                        Double resultAmount = null;
                        if (map.get("amount") != null) {
                            resultAmount = Double.parseDouble(map.get("amount").toString());
                            result.setAmount(resultAmount);
                            result.setFinalAmount(resultAmount);
                        }
                        result.setAccountCode(interFace.getAttributes_1());
//                        result.setLineSeqNumber(Long.parseLong(interFace.getAttributes_2().trim()));
                        result.setFinStatementItemName(interFace.getAttributes_2());
                        //result.set
                        result.setLineType((String) map.get("lineType"));
                        String amount = "";
                        if (resultAmount != null) {
                            amount = resultAmount.toString();
                        }
                        if (2 == b) {
                            result.setFinStatementHdId(id1);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_2(amount);
                            }
                        } else if (3 == b) {
                            result.setFinStatementHdId(id2);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_3(amount);
                            }
                        } else if (4 == b) {
                            result.setFinStatementHdId(id3);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_4(amount);
                            }
                        } else if (5 == b) {
                            result.setFinStatementHdId(id4);
                            if (amount != null) {
                                interfaceLinesList.get(i).setAttributes_5(amount);
                            }
                        }
                        lnList.add(result);
                        map.put("amount", null);
                    }
                }
            }
        }

        return lnList;
    }

    public boolean isNumber(String num) {
        Pattern pattern = Pattern.compile("^(-)?\\d+(\\.\\d+)?$");
        Matcher m = pattern.matcher(num);
        boolean flag = m.find();
        return flag;
    }

    //
    public boolean isBigNumber(String num) {
        Pattern pattern = Pattern.compile("[0-9]+[a-zA-Z]+[0-9a-zA-Z]*");
        Matcher m = pattern.matcher(num);
        boolean flag = m.find();
        return flag;
    }

    //
    public String getAmount(List<HlsFinStatementLn> list, String numSeq) {
        String amount = "0";
        for (int i = 0; i < list.size(); i++) {
            HlsFinStatementLn hlsFinStatementLn = new HlsFinStatementLn();
            hlsFinStatementLn = list.get(i);
            if (hlsFinStatementLn.getLineSeqNumber() == (Long.parseLong(numSeq))) {
                if (hlsFinStatementLn.getAmount() == null) {
                    return amount;
                } else {
                    BigDecimal db = new BigDecimal(hlsFinStatementLn.getAmount());
                    amount = db.toPlainString();
                    return amount;
                }
            }
        }
        return amount;
    }


    public List<String> extractMessageByRegular(String msg) {

        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("\\+|\\-");
        Matcher m = p.matcher(msg);
        while (m.find()) {
            list.add(m.group());
        }
        return list;
    }

    /**
     * 根据—,+分割出对应的元素
     *
     * @param msg
     * @return
     */
    public List<String> replaceByRegular(String msg) {
        if (msg == null)
            return new ArrayList<>();
        String[] split = msg.split("\\+|-");
        return Arrays.asList(split);
    }

    //
    public Double add(Double sum, Double amount) {
        return sum + amount;
    }

    public Double minus(Double sum, Double amount) {
        return sum - amount;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 使用js引擎执行字符串中的计算表达式
     *
     * @param expression
     * @return
     */
    private static Double excuetExpressionByJs(String expression) throws ScriptException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
        String eval = se.eval(expression).toString();
        return Double.parseDouble(eval);
    }
}