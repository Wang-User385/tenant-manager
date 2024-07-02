package com.hand.hls.fnd.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.bp.dto.HlsBpFinancialHeader;
import com.hand.hls.bp.mapper.HlsBpFinancialHeaderMapper;
import com.hand.hls.fnd.dto.FndInterfaceLines;
import com.hand.hls.fnd.mapper.FndInterfaceLinesMapper;
import com.hand.hls.fnd.mapper.HlsFinStatementHdMapper;
import com.hand.hls.fnd.service.HlsFinStatementLnService;
import com.hand.hls.fnd.dto.HlsFinStatementHd;
import com.hand.hls.fnd.dto.HlsFinStatementLn;
import com.hand.hls.fnd.dto.HlsFinStatementTmpltLn;
import com.hand.hls.fnd.mapper.HlsFinStatementLnMapper;
import com.hand.hls.fnd.service.HlsFinStatementHdService;
import com.hand.hls.hls.dto.HlsWebExcelCalcResult;
import leaf.bean.LeafRequestData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by haibin on 2017/6/27.
 */
@Controller
public class HlsFinStatementHdController extends BaseController {
    private static final String SHEET_NAME_PROFIT_STATEMENT = "利润表";
    private static final String SHEET_NAME_BALANCE_SHEET = "资产负债表";
    private static final String SHEET_NAME_CASH_FLOW_STATEMENT = "现金流量表";
    private static final String SHEET_NAME_FINANCIAL_INDEX = "业务指标表";
    private static final Map<String, String> sheetNameMap = new HashMap() {
        {
            put("INCOME_STATEMENT", SHEET_NAME_PROFIT_STATEMENT);
            put("BALANCE_SHEET", SHEET_NAME_BALANCE_SHEET);
            put("CASHFLOW_STATEMENT", SHEET_NAME_CASH_FLOW_STATEMENT);
            put("SUPPLEMENTARY", SHEET_NAME_FINANCIAL_INDEX);
        }
    };

    @Autowired
    private HlsFinStatementHdService hlsFinStatementHdService;
    @Autowired
    private HlsFinStatementLnService hlsFinStatementLnService;
    @Autowired
    private HlsFinStatementLnMapper hlsFinStatementLnMapper;

    @Autowired
    private HlsBpFinancialHeaderMapper hlsBpFinancialHeaderMapper;


    @RequestMapping(value="/fnd/excel/query")
    @ResponseBody
    public ResponseData excelQuery(HlsFinStatementTmpltLn hlsFinStatementTmpltLn,
                                   @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        List list=hlsFinStatementHdService.getList(hlsFinStatementTmpltLn,page,pagesize);
        return new ResponseData(list);
    }

    @RequestMapping(value="/fnd/clounm/query")
    @ResponseBody
    public ResponseData getClounm(HlsFinStatementTmpltLn hlsFinStatementTmpltLn,
                                  HttpServletRequest request) {
        IRequest requestContext = RequestHelper.createServiceRequest(request);
        RequestHelper.setCurrentRequest(requestContext);
//        String header_id = request.getParameter("headerId");
//        String sheetName = request.getParameter("sheetName");
//        if (StringUtils.isBlank(header_id) || StringUtils.isBlank(sheetName) || sheetNameMap.get(sheetName) == null) {
//            return new ResponseData(false, "查询参数有误");
//        }
//        hlsFinStatementTmpltLn.setHeaderId(Long.parseLong(header_id));
//        hlsFinStatementTmpltLn.setSheetName(sheetNameMap.get(sheetName));
        List<Map<String, String>> list = hlsFinStatementHdService.getClounm(hlsFinStatementTmpltLn);
        return new ResponseData(list);
    }

    @RequestMapping(value="/fnd/statement/submit")
    @ResponseBody
    public ResponseData statementSubmit(
            HttpServletRequest request, HttpSession session,
            @ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
            HlsFinStatementTmpltLn hlsFinStatementTmpltLn) {
        IRequest requestContext = RequestHelper.createServiceRequest(request);
        RequestHelper.setCurrentRequest(requestContext);
        Map parameter = requestData.getParameter();
        Object fin_statement_templet_hd_id = parameter.get("fin_statement_templet_hd_id");
        Object bp_id = parameter.get("bp_id");
        Object headerId = parameter.get("headerId");
        if (StringUtils.isBlank(String.valueOf(bp_id)) || StringUtils.isBlank(String.valueOf(fin_statement_templet_hd_id))
                || StringUtils.isBlank(String.valueOf(headerId))) {
            return new ResponseData(false, "请求参数有误");
        }
        hlsFinStatementTmpltLn.setFinStatementTempletHdId(Long.valueOf(fin_statement_templet_hd_id.toString()));
        hlsFinStatementTmpltLn.setBpId(Long.valueOf(bp_id.toString()));
        hlsFinStatementTmpltLn.setHeaderId(Long.valueOf(headerId.toString()));
        Boolean b = hlsFinStatementHdService.statementSubmit(requestContext, hlsFinStatementTmpltLn);
        return new ResponseData(b);
    }







//    @RequestMapping(value="/fnd/statement/remove")
//    @ResponseBody
//    public ResponseData statementDelete(HttpServletRequest request, HttpSession session, HlsFinStatementTmpltLn hlsFinStatementTmpltLn){
//        IRequest iRequest = createRequestContext(request);
//        Long companyId=(Long) session.getAttribute("companyId");
//        hlsFinStatementTmpltLn.setCompanyId(companyId);
//        Boolean b = hlsFinStatementHdService.statementDelete(iRequest,hlsFinStatementTmpltLn);
//        return new ResponseData(b);
//    }
//
//    @RequestMapping(value="/fnd/statement/ln/remove")
//    @ResponseBody
//    public ResponseData statementLnDelete(HttpServletRequest request, HttpSession session, HlsFinStatementHd hlsFinStatementHd){
//        IRequest iRequest = createRequestContext(request);
//        Long companyId=(Long) session.getAttribute("companyId");
//        hlsFinStatementHd.setCompanyId(companyId);
//        Boolean b = hlsFinStatementHdService.statementLnDelete(iRequest,hlsFinStatementHd);
//        return new ResponseData(b);
//    }

    @RequestMapping(value="/fnd/hd/query")
    @ResponseBody
    public ResponseData hdQuery(HlsFinStatementHd hlsFinStatementHd,
                                @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        List<HlsFinStatementHd> list=hlsFinStatementHdService.hdQuery(hlsFinStatementHd,page,pagesize);
        return new ResponseData(list);
    }


    @RequestMapping(value="/fnd/hd/query/distinct")
    @ResponseBody
    public ResponseData hdDistinctQuery(HlsFinStatementHd hlsFinStatementHd,
                                @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        List<HlsFinStatementHd> list=hlsFinStatementHdService.hdDistinctQuery(hlsFinStatementHd,page,pagesize);
        return new ResponseData(list);
    }

//    @RequestMapping(value="/fnd/hd/guaran/query")
//    @ResponseBody
//    public ResponseData hdGuaranQuery(HlsFinStatementHd hlsFinStatementHd,
//                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
//        List<HlsFinStatementHd> list=hlsFinStatementHdService.hdGuaranQuery(hlsFinStatementHd,page,pagesize);
//        return new ResponseData(list);
//    }
//    @RequestMapping(value="/fnd/hd/cloumnQuery")
//    @ResponseBody
//    public ResponseData hdCloumnQuery(HlsFinStatementHd hlsFinStatementHd){
//        List<HlsFinStatementHd> list=hlsFinStatementHdService.hdColumnQuery(hlsFinStatementHd);
//        return new ResponseData(list);
//    }
//    @RequestMapping(value="/fnd/ln/query")
//    @ResponseBody
//    public ResponseData lnQuery(HlsFinStatementLn hlsFinStatementLn,
//                                @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
//        List<HlsFinStatementLn> list=hlsFinStatementLnService.lnQuery(hlsFinStatementLn,page,pagesize);
//        return new ResponseData(list);
//    }
//
//
    /**
     * @Description:重写财报的明细查看方法
     * @Author: Wty
     * @Date: Created om 下午2:42 2018/7/4
     */
    @RequestMapping(value="/hlsCus/fnd/ln/query")
    @ResponseBody
    public ResponseData hlsCusLnQuery(HlsFinStatementLn hlsFinStatementLn,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        HlsFinStatementLn hlsFinStatementLn1 = new HlsFinStatementLn();
        hlsFinStatementLn1.setFinStatementHdId(hlsFinStatementLn.getFinStatementHdId());
        hlsFinStatementLn1.setLineType(hlsFinStatementLn.getLineType());
        ResponseData rs = new ResponseData(hlsFinStatementLnService.lnQuery(hlsFinStatementLn, pagenum, pagesize));
        List<Long> total = hlsFinStatementLnMapper.lnQueryCount(hlsFinStatementLn1);
        if (CollectionUtils.isNotEmpty(total)) {
            rs.setTotal(total.get(0));
        }
        return rs;
    }

    @RequestMapping(value="/hlsCus/fnd/hd/querybp")
    @ResponseBody
    public ResponseData hlsCusHdQueryBp(HlsFinStatementLn hlsFinStatementLn,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){

        HlsFinStatementHd hlsFinStatementHd = new HlsFinStatementHd();
        hlsFinStatementHd.setHeaderId(hlsFinStatementLn.getHeaderId());
        List<HlsFinStatementHd> hlsFinStatementHdList  = hlsFinStatementHdMapper.hdQueryBasicBp(hlsFinStatementHd);

        return new ResponseData(hlsFinStatementHdList);

    }

    @Autowired
    private FndInterfaceLinesMapper fndInterfaceLinesMapper;

    @Autowired
    private HlsFinStatementHdMapper hlsFinStatementHdMapper;

    @RequestMapping(value="/hlsCus/fnd/line/query")
    @ResponseBody
    public ResponseData hlsCusLineQuery(HlsFinStatementLn hlsFinStatementLn,
                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        HlsFinStatementLn hlsFinStatementLn1 = new HlsFinStatementLn();
        hlsFinStatementLn1.setFinStatementHdId(hlsFinStatementLn.getFinStatementHdId());
        hlsFinStatementLn1.setLineType(hlsFinStatementLn.getLineType());

        Long headerId = hlsFinStatementLnMapper.lnHeaderIdQuery(hlsFinStatementLn1);
        FndInterfaceLines lines = new FndInterfaceLines();
        lines.setHeaderId(headerId);
        lines.setSheetName(hlsFinStatementLn.getLineType());
        //从接口表中取出对应的财报数据
        List<FndInterfaceLines> interfaceLinesList = fndInterfaceLinesMapper.fndInterColumnQueryDetail(lines);

       /* ResponseData rs = new ResponseData(hlsFinStatementLnService.lnQuery(hlsFinStatementLn, page, pagesize));
        List<Long> total = hlsFinStatementLnMapper.lnQueryCount(hlsFinStatementLn1);
        if (CollectionUtils.isNotEmpty(total)) {
            rs.setTotal(total.get(0));
        }*/

        return new ResponseData(interfaceLinesList);
    }

    @RequestMapping(value="/hlsCus/fnd/line/query/double")
    @ResponseBody
    public ResponseData hlsCusLineQueryDouble(HlsFinStatementLn hlsFinStatementLn,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        HlsFinStatementLn hlsFinStatementLn1 = new HlsFinStatementLn();
        hlsFinStatementLn1.setFinStatementHdId(hlsFinStatementLn.getFinStatementHdId());
        hlsFinStatementLn1.setLineType(hlsFinStatementLn.getLineType());

        Long headerId = hlsFinStatementLnMapper.lnHeaderIdQuery(hlsFinStatementLn1);
        FndInterfaceLines lines = new FndInterfaceLines();
        lines.setHeaderId(headerId);
        lines.setSheetName(hlsFinStatementLn.getLineType());
        //从接口表中取出对应的财报数据
        List<FndInterfaceLines> interfaceLinesList = fndInterfaceLinesMapper.fndInterColumnQueryDetailDouble(lines);

       /* ResponseData rs = new ResponseData(hlsFinStatementLnService.lnQuery(hlsFinStatementLn, page, pagesize));
        List<Long> total = hlsFinStatementLnMapper.lnQueryCount(hlsFinStatementLn1);
        if (CollectionUtils.isNotEmpty(total)) {
            rs.setTotal(total.get(0));
        }*/

        return new ResponseData(interfaceLinesList);
    }


    @RequestMapping(value="/hlsCus/fnd/line/indicator")
    @ResponseBody
    public ResponseData hlsCusLineQueryIndicator(HlsFinStatementLn hlsFinStatementLn,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int pagenum,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){

        List<HlsFinStatementLn> finStatementLns = new ArrayList<>();
        PageHelper.startPage(pagenum, pagesize);
        finStatementLns = hlsFinStatementLnMapper.fndLnColumnQueryFinancialIndicator(hlsFinStatementLn);

        return new ResponseData(finStatementLns);
    }

    // 财报导入检验
    @RequestMapping(value = "/bp/financial/report/verificate", method = RequestMethod.POST)
    public Map<String, Object> receiptImport(HttpServletRequest request, Long bpId ,Long headerId) throws IOException {

        IRequest iRequest = createRequestContext(request);

        HlsBpFinancialHeader hlsBpFinancialHeader = new HlsBpFinancialHeader();
        hlsBpFinancialHeader.setHeaderId(headerId);
        hlsBpFinancialHeader = hlsBpFinancialHeaderMapper.selectByPrimaryKey(hlsBpFinancialHeader);

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
//            service.receiptImport(iRequest, headerId);
            hlsFinStatementHdService.financialVertificate(iRequest,headerId);
            hlsBpFinancialHeader.setCheckStatus("Y");
            hlsBpFinancialHeaderMapper.updateByPrimaryKeySelective(hlsBpFinancialHeader);
            response.put("message", "校验成功");
            response.put("success", true);
        } catch (Exception e) {
            hlsBpFinancialHeader.setCheckStatus("N");
            hlsBpFinancialHeaderMapper.updateByPrimaryKeySelective(hlsBpFinancialHeader);
            response.put("success", false);
            response.put("message", "校验失败！" + e.getMessage());
        }
        return response;
    }

    // 财报导入删除
    @RequestMapping(value = "/bp/financial/report/delete", method = RequestMethod.POST)
    public Map<String, Object> deleteImport(HttpServletRequest request, Long bpId ,Long headerId) throws IOException {

        IRequest iRequest = createRequestContext(request);


        if(headerId != null) {
            HlsBpFinancialHeader hlsBpFinancialHeader = new HlsBpFinancialHeader();
            hlsBpFinancialHeader.setHeaderId(headerId);

            //删除头表
            hlsBpFinancialHeaderMapper.deleteByPrimaryKey(hlsBpFinancialHeader);

            //删除头行
            hlsBpFinancialHeaderMapper.deleteHlsBpFinStatementHdByHeaderId(hlsBpFinancialHeader);
            hlsBpFinancialHeaderMapper.deleteHlsBpFinStatementLnByHeaderId(hlsBpFinancialHeader);

            //删除计算结果表
            hlsBpFinancialHeaderMapper.deleteHlsWebExcelCalcResult(hlsBpFinancialHeader);
        }

        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            response.put("message", "删除成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除失败！" + e.getMessage());
        }
        return response;
    }
    @Autowired
    ObjectMapper objectMapper;

    // 财报信息导出
    @RequestMapping(value = "/bp/financial/report/export")
    public void exportFinancial(HttpServletRequest request, Long bpId , Long templetHdId ,  HttpServletResponse response) throws IOException {

   /*     IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusAbsAssetsPackage.class, ColumnInfo.class);
            ExportConfig<FndInterfaceLines, ColumnInfo> exportConfig = objectMapper.readValue(config, type);

            hlsFinStatementHdService.exportCashflow(request,httpServletResponse,exportConfig.getParam());
        } catch (IOException e) {
//            logger.info();
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }*/

        HlsFinStatementHd hlsFinStatementHd = new HlsFinStatementHd();
        hlsFinStatementHd.setBpId(bpId);
        hlsFinStatementHd.setFinStatementTempletHdId(templetHdId);

        try {
            hlsFinStatementHdService.exportFinancialReport(request,response,hlsFinStatementHd);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

/*
    @RequestMapping(value="/hlsCus/fnd/bp/info")
    @ResponseBody
    public ResponseData hlsCusLineQueryBpInfo(HlsFinStatementHd hlsFinStatementHd,
                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pagesize){
        HlsFinStatementLn hlsFinStatementLn1 = new HlsFinStatementLn();
        hlsFinStatementLn1.setFinStatementHdId(hlsFinStatementLn.getFinStatementHdId());
        hlsFinStatementLn1.setLineType(hlsFinStatementLn.getLineType());

        Long headerId = hlsFinStatementLnMapper.lnHeaderIdQuery(hlsFinStatementLn1);
        FndInterfaceLines lines = new FndInterfaceLines();
        lines.setHeaderId(headerId);
        lines.setSheetName(hlsFinStatementLn.getLineType());
        //从接口表中取出对应的财报数据
        List<FndInterfaceLines> interfaceLinesList = fndInterfaceLinesMapper.fndInterColumnQueryDetail(lines);

        ResponseData rs = new ResponseData(hlsFinStatementLnService.lnQuery(hlsFinStatementLn, page, pagesize));
        List<Long> total = hlsFinStatementLnMapper.lnQueryCount(hlsFinStatementLn1);
        if (CollectionUtils.isNotEmpty(total)) {
            rs.setTotal(total.get(0));
        }

        return new ResponseData(interfaceLinesList);

        HlsFinStatementHdMapper

//        return new ResponseData();

    }
*/

}
