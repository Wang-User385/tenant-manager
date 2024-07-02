package com.hand.hls.fin.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.eft.service.HlsCusFundTransferListService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fin.dto.HlsCusLonContractRepayment;
import com.hand.hls.fin.dto.HlsCusLonContractWithdraw;
import com.hand.hls.fin.exception.HlsCusAmountOverException;
import com.hand.hls.fin.service.HlsCusLonContractQuotationService;
import com.hand.hls.fin.service.HlsCusLonContractRepaymentService;
import com.hand.hls.fin.service.HlsCusLonContractWithdrawService;
import com.hand.hls.utils.AttachmentHttpUtils;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

@Controller
public class HlsCusLonContractWithdrawController extends BaseController {

    @Autowired
    private HlsCusLonContractWithdrawService service;

    @Autowired
    IExportService excelService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private HlsCusLonContractRepaymentService hlsCusLonContractRepaymentService;

    @Autowired
    private HlsCusFundTransferListService fundTransferListService;
    @Autowired
    private HlsCusLonContractQuotationService cusLonContractQuotationService;

    @RequestMapping(value = "/ct/report/dept/query")
    @ResponseBody
    public ResponseData queryDeptReport(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryDebtReport(requestContext, map));
    }


    @RequestMapping(value = "/ct/report/foundUseOfWithdraw/query")
    @ResponseBody
    public ResponseData queryFoundUseOfWithdraw(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryFoundUseOfWithdraw(requestContext, map));
    }


    @RequestMapping(value = "/ct/report/foundUseOf/query")
    @ResponseBody
    public ResponseData queryReport2(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryFoundUseOfProject(requestContext, map));
    }


    @RequestMapping(value = "/ct/report/withdraw/detail/query")
    @ResponseBody
    public ResponseData queryReport(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryReport(requestContext, map));
    }


    @RequestMapping(value = "/hlsLon/contract/withdraw/query")
    @ResponseBody
    public ResponseData query(HlsCusLonContractWithdraw dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                              @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.select(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/withdraw/remove")
    @ResponseBody
    public ResponseData delete(HttpServletRequest request, @RequestBody List<HlsCusLonContractWithdraw> dto) {
        IRequest requestContext = createRequestContext(request);
        for (HlsCusLonContractWithdraw item :dto) {
            if (!"NEW".equalsIgnoreCase(item.getWithdrawStatus())&&!"APPROVED_RETURN".equalsIgnoreCase(item.getWithdrawStatus())){
                ResponseData rs = new ResponseData(false);
                rs.setMessage("只允许删除新建和审批退回状态的提款");
                return rs;
            }
        }
        service.batchDeleteWithdraw(requestContext,dto);
        return new ResponseData();
    }

    @RequestMapping(value = "/hlsLon/contract/withdraw/formData")
    @ResponseBody
    public ResponseData lonContractWithdrawFormData(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        //查询还款本金和还款利息的金额之和
      /*  HlsCusLonContractRepayment hlsCusLonContractRepayment=new HlsCusLonContractRepayment();
        hlsCusLonContractRepayment.setCfItem(301L);
        hlsCusLonContractRepayment.setWithdrawId(lonContractWithdraw.getWithdrawId());
        Double pricipalAmountSum=hlsCusLonContractRepaymentService.cfItemAmountSum(requestContext,hlsCusLonContractRepayment);
        hlsCusLonContractRepayment.setCfItem(302L);
        Double interestAmountSum=hlsCusLonContractRepaymentService.cfItemAmountSum(requestContext,hlsCusLonContractRepayment);
       */

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw lonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);

        HlsCusLonContractWithdraw hlsCusLonContractWithdraw=new HlsCusLonContractWithdraw();
        hlsCusLonContractWithdraw=service.lonContractWithdrawFormData(requestContext, lonContractWithdraw, session);
       /* hlsCusLonContractWithdraw.setConvertPayPrincipalAmount(pricipalAmountSum);
        hlsCusLonContractWithdraw.setConvertPayInterestAmount(interestAmountSum);*/

        List<HlsCusLonContractWithdraw> hlsCusLonContractWithdrawsList = new ArrayList<HlsCusLonContractWithdraw>();
        hlsCusLonContractWithdrawsList.add(hlsCusLonContractWithdraw);
        return new ResponseData(hlsCusLonContractWithdrawsList);
    }

    @RequestMapping(value = "/hlsLon/contract/withdraw/list")
    @ResponseBody
    public ResponseData query4LonConWithdrawList(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  @RequestParam(defaultValue = DEFAULT_PAGE) int pageNum,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw dto = param.toJavaObject(HlsCusLonContractWithdraw.class);

        return new ResponseData(service.queryContractWithdraw(requestCtx, dto, pageNum, pageSize));
    }

    @RequestMapping(value = "/hlsLon/contract/withdraw/create")
    @ResponseBody
    public ResponseData lonContractWithdrawCreate(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  HttpServletRequest request, HttpSession session)  throws HlsCusException {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw lonContractWithdraw=param.toJavaObject(HlsCusLonContractWithdraw.class);

        HlsCusLonContractWithdraw hlsCusLonContractWithdraw= service.lonContractWithdrawCreate(requestContext, lonContractWithdraw, session);
        List<HlsCusLonContractWithdraw> hlsCusLonContractWithdrawList = new ArrayList<HlsCusLonContractWithdraw>();

        hlsCusLonContractWithdrawList.add(hlsCusLonContractWithdraw);

        return new ResponseData(hlsCusLonContractWithdrawList);
    }

    //@RequestBody HlsCusLonContractWithdraw lonContractWithdraw,
    @RequestMapping(value = "/hlsLon/contract/withdraw/save")
    @ResponseBody
    public ResponseData saveLonContractWithdraw(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
        IRequest requestContext = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw lonContractWithdraw =param.toJavaObject(HlsCusLonContractWithdraw.class);

        service.save(requestContext, lonContractWithdraw);
        List<HlsCusLonContractWithdraw> lonContractWithdrawList = new ArrayList<HlsCusLonContractWithdraw>();
        lonContractWithdrawList.add(lonContractWithdraw);
        return new ResponseData(lonContractWithdrawList);
    }


    /**
     * 融资提款计算
     * @param hlsCusLonContractWithdraw
     * @param request
     * @return
     * @throws HlsCusAmountOverException
     */
    @RequestMapping(value = "/hlsLon/contract/withdraw/calcRepayment")
    @ResponseBody
    public ResponseData lonContractCalcRepayment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  HttpServletRequest request) throws Exception {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);

        service.lonContractCalcRepayment(requestCtx,hlsCusLonContractWithdraw);
        //查询还款本金和还款利息的金额之和
        HlsCusLonContractRepayment hlsCusLonContractRepayment=new HlsCusLonContractRepayment();
        hlsCusLonContractRepayment.setCfItem(301L);
        hlsCusLonContractRepayment.setWithdrawId(hlsCusLonContractWithdraw.getWithdrawId());
        Double pricipalAmountSum=hlsCusLonContractRepaymentService.cfItemAmountSum(requestCtx,hlsCusLonContractRepayment);
        hlsCusLonContractRepayment.setCfItem(302L);
        Double interestAmountSum=hlsCusLonContractRepaymentService.cfItemAmountSum(requestCtx,hlsCusLonContractRepayment);
        List<Double> list=new ArrayList<Double>();
        list.add(pricipalAmountSum);
        list.add(interestAmountSum);
        ResponseData responseData=new ResponseData();
        responseData.setRows(list);
        return responseData;
    }


    @RequestMapping(value = "/hlsLon/contract/withdrawChange/calcRepayment")
    @ResponseBody
    public ResponseData lonContractChangeCalcRepayment(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,  HttpServletRequest request) throws Exception {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);


       service.lonContractChangeCalcRepayment(requestCtx,hlsCusLonContractWithdraw);
        //查询还款本金和还款利息的金额之和

        ResponseData responseData=new ResponseData();
        return responseData;



    }
    /**
     * 查询基准利率
     * @param request
     * @param
     * @return
     */
    @RequestMapping({"/fnd/baserateset/queryForFinanceQuotation"})
    @ResponseBody
    public ResponseData queryForFinanceContract(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);
        return new ResponseData(this.service.queryForFinanceQuotation(hlsCusLonContractWithdraw));
    }
    @RequestMapping({"/hls/cus/lon/queryForFinanceQuotationTime"})
    @ResponseBody
    public ResponseData queryForFinanceQuotationTime(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request, HttpSession session) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw hlsCusLonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);
        return new ResponseData(this.service.queryForFinanceQuotationTime(hlsCusLonContractWithdraw));
    }
    //融资提款本息还款计划导入
    @RequestMapping(value = "/hls/cus/lon/contract/repayment/import", method = RequestMethod.POST)
    public Map<String, Object> lonContractRepaymentImport(HttpServletRequest request, Long headerId , Long withdrawId, Long contractId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            service.lonContractRepaymentImport(iRequest, headerId ,withdrawId,contractId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
        }
        return response;
    }


    /**
     * 融资提款行计算
     * @param hlsCusLonContractRepaymentList
     * @param request
     * @return
     * @throws HlsCusAmountOverException
     */
//    @RequestMapping(value = "/hlsLon/contract/withdraw/calcLoanRepaymentCashflow")
//    @ResponseBody
//    public ResponseData calcLoanRepaymentCashflow(@RequestBody List<HlsCusLonContractRepayment> hlsCusLonContractRepaymentList,
//                                                  HttpServletRequest request) throws HlsCusAmountOverException, HlsCusException {
//        IRequest requestCtx = createRequestContext(request);
//        return new ResponseData(service.calcLoanRepaymentCashflow(requestCtx,hlsCusLonContractRepaymentList));
//    }

    //@RequestBody HlsCusLonContractWithdraw lonContractWithdraw,
    /*提交审批*/
    @RequestMapping(value = "/hlsLon/contract/withdraw/submitApproval")
    @ResponseBody
    public ResponseData submitLonContractWithdrawToWfl(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,HttpServletRequest request, HttpSession session) throws HlsCusAmountOverException, HlsCusException {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw lonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);

        return new ResponseData(service.submitLonContractWithdrawToWfl(requestCtx,lonContractWithdraw,session));
    }

    /*-----------融资提款综合查询-----------*/
    @RequestMapping(value = "/hlsLon/contract/withdraw/WithdrawComprehensiveQuery")
    @ResponseBody
    public ResponseData WithdrawComprehensiveQuery(HlsCusLonContractWithdraw dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);
        return new ResponseData(service.withdrawComprehensiveQuery(requestCtx, dto, page, pageSize));
    }

    @RequestMapping(value = "/hlsCus/lon/contract/withdraw/export")
    public void createLonChangeReqXLS(HttpServletRequest request, @RequestParam String config,
                                      HttpServletResponse httpServletResponse, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusLonContractWithdraw.class, ColumnInfo.class);
            ExportConfig<HlsCusLonContractWithdraw, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractWithdrawMapper.withdrawComprehensiveQuery",
                    exportConfig, request, httpServletResponse, requestContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //@RequestBody HlsCusLonContractWithdraw lonContractWithdraw,
    @RequestMapping(value = "/hlsLon/contract/withdraw/cancelChangeReq")
    @ResponseBody
    public ResponseData cancelChangeReq(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw lonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);
        service.lonContractWithdrawChangeReqCancel(requestCtx,lonContractWithdraw);
        return new ResponseData();
    }

    //HlsCusLonContractWithdraw lonContractWithdraw,
    @RequestMapping(value = "/hlsLon/contract/withdraw/lonContractWithdrawChangeReq")
    @ResponseBody
    public ResponseData createChangeReq(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) throws HlsCusException {
        IRequest requestCtx = createRequestContext(request);
        JSONObject param = (JSONObject)requestData.get("parameter");
        HlsCusLonContractWithdraw lonContractWithdraw = param.toJavaObject(HlsCusLonContractWithdraw.class);
        List<HlsCusLonContractWithdraw> list=new ArrayList<>();
        list.add(service.lonConWithdrawChangeReqCreate(requestCtx,lonContractWithdraw));
        return new ResponseData(list);

    }

    @RequestMapping(value = "/ct/csh/debt/structure/chart/query")
    @ResponseBody
    public ResponseData queryDebtStructureChart(HlsCusLonContractWithdraw lonContractWithdraw, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryDebtStructureChart(requestContext,lonContractWithdraw,page,pageSize));
    }

    @RequestMapping(value = "/ct/csh/debt/structure/chart/export")
    public void createXLS(HttpServletRequest request, @RequestParam String config,
                          HttpServletResponse httpServletResponse, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusLonContractWithdraw.class, ColumnInfo.class);
            ExportConfig<HlsCusLonContractWithdraw, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractWithdrawMapper.queryDebtStructureChart",
                    exportConfig, request, httpServletResponse, requestContext);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/ct/repaymemnt/plan/report/export")
    @ResponseBody
    public ResponseData exportRepaymentPlan(HttpServletRequest request, @RequestBody Object[] objs, HttpServletResponse httpServletResponse) {
        IRequest requestContext = createRequestContext(request);
        String config = (String)objs[0];
        List<String> list = new ArrayList<>();
        list.add(exportExcelForELSX(request, (String)objs[0], (List)objs[1], "/Users/ferry/Downloads/", httpServletResponse));
        return new ResponseData(list);
    }

    @RequestMapping(value = "/ct/rpt/excel/file/download")
    public void downloanFile(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileName) throws FileReadIOException {
        try {
            String name = request.getSession().getServletContext().getRealPath("/") + "resources/excel/RPT/";
            String addHeader = "attachment;filename=\"";
            name += fileName;
            File file = new File(name);
            boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
            if (isMSIE) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
            }
            addHeader += fileName;
            addHeader += "\"";
            if (file.exists()) {
                response.addHeader("Content-Disposition", addHeader);
                response.setContentType("EXCEL;charset=UTF-8");
                response.setHeader("Accept-Ranges", "bytes");
                int fileLength = (int) file.length();
                response.setContentLength(fileLength);
                if (fileLength > 0) {
                    writeFileToResp(response, file);
                }
            } else {
                response.getWriter().write("文件不存在！");
            }
            if(!file.delete()){
                throw new FileReadIOException();
            }
        } catch (IOException e) {
            throw new FileReadIOException();
        }
    }

    private void writeFileToResp(HttpServletResponse response, File file) throws IOException {
        byte[] buf = new byte[BUFFER_SIZE];
        try (InputStream inStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream()) {
            int readLength;
            while (((readLength = inStream.read(buf)) != -1)) {
                outputStream.write(buf, 0, readLength);
            }
            outputStream.flush();
        }
    }

    public String exportExcelForELSX(HttpServletRequest request, String config, List<Map<String,Object>> list, String path, HttpServletResponse httpServletResponse){
        JSONObject jsonObject = JSON.parseObject(config);
        SXSSFWorkbook sheets = new SXSSFWorkbook();
        JSONArray columnsInfo = (JSONArray) jsonObject.get("columnsInfo");
        String title = jsonObject.getString("title") == null ? "未命名" : jsonObject.getString("title");
        String unit = jsonObject.getString("unit");
        SXSSFSheet sheet = sheets.createSheet(title);
        SXSSFRow headerRow = sheet.createRow(0);
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, columnsInfo.size()-1);
        sheet.addMergedRegion(region);
        setCell(headerRow, 0, title, "宋体", (short)12, true);
        SXSSFRow unitRow = sheet.createRow(1);
        setCell(unitRow, columnsInfo.size()-2, "单位", "宋体", (short)11, false);
        setCell(unitRow, columnsInfo.size()-1, jsonObject.getString("unit"), "宋体", (short)11, false);
        SXSSFRow titleRow = sheet.createRow(2);
        int i = 0;
        for(Object obj : columnsInfo){
            JSONObject json = (JSONObject) obj;
            setCell(titleRow, i, json.getString("title"), "宋体", (short)11, true);
            sheet.setColumnWidth(i,(json.getInteger("width") == null ? 1 : json.getInteger("width"))*40);
            i++;
        }

        i = 3;
        for(Map<String, Object> map : list){
            SXSSFRow row = sheet.createRow(i);
            int j = 0;
            for(Object obj : map.values()){
                setCell(row, j++, obj, "宋体", (short)11, false);
            }
            i++;
        }
        FileOutputStream output = null;
        String fileName = setfileName(jsonObject.getString("fileName"), ".xlsx");
        try {
            output = new FileOutputStream(request.getSession().getServletContext().getRealPath("/")+"/resources/excel/RPT/"+fileName);
            sheets.write(output);
            output.flush();
            sheets.close();
            output.close();
            return fileName;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("文件未找到");
        } catch (IOException e) {
            throw new RuntimeException("输入输出流错误");
        }

    }

    private void setCell(SXSSFRow row, int index, Object value, String fontName, short fontHeight, boolean isCenter){
        SXSSFCell cell = row.createCell(index);
        CellStyle style = row.getSheet().getWorkbook().createCellStyle();
        if(value == null){
            cell.setCellValue("");
        }else if(value instanceof String){
            cell.setCellValue((String) value);
            style.setAlignment(HorizontalAlignment.LEFT);
        }else if(value instanceof Double || value instanceof BigDecimal){
            cell.setCellValue((Double)value);
            style.setAlignment(HorizontalAlignment.RIGHT);
            style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        }else if(value instanceof Integer || value instanceof BigInteger){
            cell.setCellValue(value.toString());
            style.setAlignment(HorizontalAlignment.RIGHT);
        }else if(value instanceof Date){
            cell.setCellValue(new SimpleDateFormat("yyyy年MM月dd日").format(value));
            style.setAlignment(HorizontalAlignment.CENTER);
        }else {
            cell.setCellValue(value.toString());
        }
        if(isCenter){
            style.setAlignment(HorizontalAlignment.CENTER);
        }
        Font font = row.getSheet().getWorkbook().createFont();
        font.setFontHeightInPoints(fontHeight);
        font.setFontName(fontName);
        style.setFont(font);
        cell.setCellStyle(style);
    }

    private String setfileName(String fileName, String suffix){
        if(StringUtils.isEmpty(fileName)){
            fileName = "未命名-"+ DateFormatUtils.format(new Date(),"yyyy-MM-ddZZ");
        }
        if(StringUtils.equals(fileName,suffix)){
            return fileName+suffix;
        }
        String[] strs = fileName.split("\\.");
        return StringUtils.equals(suffix.split("\\.")[1],strs[strs.length-1]) ? fileName : (fileName+suffix);
    }

    @RequestMapping(value = "/hls/cus/lon/withdraw/info/query")
    @ResponseBody
    public ResponseData queryLonWithdrawInfo(HlsCusLonContractWithdraw lonContractWithdraw, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                             @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryLonWithdrawInfo(requestContext, lonContractWithdraw, page, pageSize));
    }

    @RequestMapping(value = "/hls/cus/lon/withdraw/detail/info/query")
    @ResponseBody
    public ResponseData queryLonWithdrawDetailInfo(HlsCusLonContractWithdraw lonContractWithdraw, @RequestParam(required = false) Integer page,
                                                   @RequestParam(required = false) Integer pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.queryLonWithdrawDetailInfo(requestContext, lonContractWithdraw, page, pageSize));
    }


    @RequestMapping(value = "/hls/cus/lon/withdraw/queryById")
    @ResponseBody
    public HlsCusLonContractWithdraw queryWithdrawById(HlsCusLonContractWithdraw lonContractWithdraw, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return service.selectByPrimaryKey(requestContext,lonContractWithdraw);
    }


    @RequestMapping(value = "/hls/cus/lon/withdraw/queryChangeReq")
    @ResponseBody
    public HlsCusLonContractWithdraw queryChangeReq(HlsCusLonContractWithdraw lonContractWithdraw, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        lonContractWithdraw= service.selectByPrimaryKey(requestContext,lonContractWithdraw);
        HlsCusLonContractWithdraw lonContractWithdrawChange=new HlsCusLonContractWithdraw();
        lonContractWithdrawChange.setWithdrawId(lonContractWithdraw.getChangeReqId());
        return service.selectByPrimaryKey(requestContext,lonContractWithdrawChange);
    }

    @RequestMapping(value = "/hlsLon/contract/withdraw/change/history/query")
    @ResponseBody
    public ResponseData withdrawChangeHistoryQuery(HlsCusLonContractWithdraw dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
                                                   @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        return new ResponseData(service.withdrawChangeHistoryQuery(requestContext, dto, page, pageSize));
    }

    @RequestMapping(value = "/lon/contract/quotation/withdraw/create")
    @ResponseBody
    public ResponseData createQuotationByWithdraw(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, BindingResult result, HttpServletRequest request) throws Exception {
        IRequest requestCtx = createRequestContext(request);
        RequestHelper.setCurrentRequest(requestCtx);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusLonContractWithdraw dto = param.toJavaObject(HlsCusLonContractWithdraw.class);
        return new ResponseData(cusLonContractQuotationService.createCalcByWithdraw(requestCtx,dto.getWithdrawId(),dto.getPriceList()));
    }
}