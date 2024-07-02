package com.hand.hls.vat.controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.hand.hap.attachment.exception.FileReadIOException;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.exception.TokenException;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.excel.dto.ColumnInfo;
import com.hand.hap.excel.dto.ExportConfig;
import com.hand.hap.excel.service.IExportService;
import com.hand.hap.system.controllers.BaseController;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.cont.mapper.DocFileTempletRuleMapper;
import com.hand.hls.cont.service.HlsDocFileTempletService;
import com.hand.hls.fnd.components.Datasource2Json;
import com.hand.hls.ruleengine.service.IHLSRuleEngineInitService;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.dto.HlsCusAcrInvoiceHd;
import com.hand.hls.vat.mapper.HlsCusAcrInvoiceHdMapper;
import com.hand.hls.vat.service.HlsCusAcrInvoiceHdService;
import com.hand.hls.vat.service.IAcrReceiptLnService;
import leaf.bean.LeafRequestData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.StreamUtils.BUFFER_SIZE;

@Controller
public class HlsCusAcrInvoiceHdController extends BaseController {
    @Autowired
    private HlsCusAcrInvoiceHdMapper acrInvoiceHdMapper;

    @Autowired
    private HlsCusAcrInvoiceHdService hlsCusAcrInvoiceHdService;
//    @Autowired
//    private AcrInvoiceLnService acrInvoiceLnService;
//    @Autowired
//    private HlsCusAcrReceiptHdService hlsCusAcrReceiptHdService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    IExportService excelService;
    @Autowired
    private IAcrReceiptLnService acrReceiptLnService;
    @Autowired
    private DocFileTempletRuleMapper docFileTempletRuleMapper;
    @Autowired
    private Datasource2Json datasource2Json;
    @Autowired
    private IHLSRuleEngineInitService hLSRuleEngineInitService;
    @Autowired
    private HlsDocFileTempletService hlsDocFileTempletService;
//    @Autowired
//    private HlsSysAttachmentService hlsSysAttachmentService;
//
//    @RequestMapping(value = "/csh/writeOff/detail/info/query")
//    @ResponseBody
//    public ResponseData queryWriteOffDetail(HlsCusCshWriteOff dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryWriteOffDetail(requestContext, dto));
//    }
//
//    @RequestMapping(value = "/acr/receipt/ln/info/query")
//    @ResponseBody
//    public ResponseData queryAcrReceiptLn(AcrReceiptHd dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrReceiptHdService.queryAcrReceiptLn(requestContext, dto));
//    }
//
//    @RequestMapping(value = "/acr/receipt/hd/info/query")
//    @ResponseBody
//    public ResponseData queryAcrReceiptHd(AcrReceiptHd dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrReceiptHdService.queryAcrReceipt(requestContext, dto));
//    }
//
//    @RequestMapping(value = "/acp/invoice/detail/query")
//    @ResponseBody
//    public ResponseData queryAcpInvoice(HlsCusAcrInvoiceHd dto, HttpServletRequest request,
//                                        @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryAcpInvoice(requestContext, dto, page, pageSize));
//    }
//
//    @RequestMapping(value = "/acp/invoice/percent/query")
//    @ResponseBody
//    public ResponseData queryAcpInvoicePercent() {
//        List<Map<String, Object>> list = new ArrayList<>();
//        list.add(hlsCusAcrInvoiceHdService.queryAcpInvoicePercent());
//        return new ResponseData(list);
//    }
//
//    @RequestMapping(value = "/vat/receipt/create")
//    @ResponseBody
//    public ResponseData createReceipt(@RequestBody List<HlsCusAcrInvoiceHd> dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        hlsCusAcrReceiptHdService.createReceipt(requestContext, dto);
//        return new ResponseData();
//    }
//
//    @RequestMapping(value = "/vat/receipt/prep/guarantee/query")
//    @ResponseBody
//    public ResponseData queryReceiptPrepGuaranteeInfo(HlsCusAcrInvoiceHd dto, HttpServletRequest request,
//                                                      @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                      @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryReceiptPrepGuaranteeInfo(requestContext, dto, page, pageSize));
//    }

    @RequestMapping(value = "/vat/receipt/prep/info/query")
    @ResponseBody
    public ResponseData queryReceiptPrepInfo(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request,
            @RequestParam(defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
        IRequest requestContext = createRequestContext(request);
        JSONObject param = (JSONObject) requestData.get("parameter");
        HlsCusAcrInvoiceHd dto = param.toJavaObject(HlsCusAcrInvoiceHd.class);
        return new ResponseData(hlsCusAcrInvoiceHdService.queryReceiptPrepInfo(requestContext, dto, page, pageSize));
    }
//
//    @RequestMapping(value = "/receipt/query/info")
//    @ResponseBody
//    public ResponseData queryReceiptInfo(HlsCusAcrInvoiceHd dto, HttpServletRequest request,
//                                         @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.selectReceiptInfo(requestContext, dto, page, pageSize));
//    }
//
//    @RequestMapping(value = "/acr/invoice/ln/query")
//    @ResponseBody
//    public ResponseData queryInvoiceLn(HlsCusAcrInvoiceLn dto, HttpServletRequest request,
//                                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(acrInvoiceLnService.select(requestContext, dto, page, pageSize));
//    }
//
//    @RequestMapping(value = "/acr/invoice/hd/query")
//    @ResponseBody
//    public ResponseData queryInvoiceHd(HlsCusAcrInvoiceHd dto, HttpServletRequest request,
//                                       @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                       @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryInvoiceHdInfo(requestContext, dto, page, pageSize));
//    }
//
//    @RequestMapping(value = "/invoice/conditions/query")
//    @ResponseBody
//    public ResponseData queryInvoiceByConditions(HlsCusAcrInvoiceHd dto, HttpServletRequest request,
//                                                 @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryInvoiceByConditions(requestContext, dto, page, pageSize));
//    }
//
//
//
//    @RequestMapping(value = "/fct/invoice/create")
//    @ResponseBody
//    public ResponseData createFctInvoice(@RequestBody List<HlsCusAcrInvoiceHd> dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.createFctInvoice(requestContext, dto));
//    }

    @RequestMapping(value = "/prj/invoice/create")
    @ResponseBody
    public ResponseData createPrjInvoice(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData, HttpServletRequest request) {
        IRequest requestContext = createRequestContext(request);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrInvoiceHd> dto = parameter.toJavaList(HlsCusAcrInvoiceHd.class);
        return new ResponseData(hlsCusAcrInvoiceHdService.createInvoice(requestContext, dto));
    }

//    @RequestMapping(value = "/prj/invoice/detail/info")
//    @ResponseBody
//    public ResponseData queryByCashflowIds(@RequestBody HlsCusAcrInvoiceHd dto, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryByCashflowIds(requestContext, dto));
//    }
//
//    @RequestMapping(value = "/vat/fct/invoice/info/query")
//    @ResponseBody
//    public ResponseData queryInvoiceInfo(HttpServletRequest request, HlsCusFctQuotationCashflow dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                         @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryInvoiceInfo(dto, requestContext, page, pageSize));
//    }
//
//    @RequestMapping(value = "/vat/fct/invoicetitle/query")
//    @ResponseBody
//    public ResponseData queryFctInvoiceTitle(HttpServletRequest request, @RequestParam Long contractId) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.queryFctInvoiceTitle(contractId, requestContext));
//    }
//
//    //根据发票种类和时间查询
//    @RequestMapping(value = "/acr/invoice/hd/time/kind/query")
//    @ResponseBody
//    public ResponseData queryByKindTime(@RequestBody HlsCusAcrInvoiceHd dto, @RequestParam(defaultValue = DEFAULT_PAGE) int page,
//                                        @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) int pageSize, HttpServletRequest request) {
//        IRequest requestContext = createRequestContext(request);
//        return new ResponseData(hlsCusAcrInvoiceHdService.selectByKindTime(requestContext, dto, page, pageSize));
//    }
//
//    @RequestMapping(value = "/fct/vat/acrInvoiceDetail")
//    @ResponseBody
//    public ResponseData acrInvoiceDetail(@RequestBody HlsCusAcrInvoiceHd acrInvoiceHd, final HttpServletRequest request) {
//        IRequest iRequest = createRequestContext(request);
//        acrInvoiceHd.setCompanyId(iRequest.getCompanyId());
//        return new ResponseData(hlsCusAcrInvoiceHdService.acrInvoiceDetail(acrInvoiceHd, iRequest));
//    }
//
//    @RequestMapping(value = "/trx/attach/file/upload")
//    public void detail(HttpServletRequest request, HttpServletResponse response, String invoice) throws FileReadIOException, TokenException {
//        IRequest requestContext = createRequestContext(request);
//        String name = "";
//        if ("N".equalsIgnoreCase(invoice)) {
//            name = "适用无发票类型";
//        } else if ("Y".equalsIgnoreCase(invoice)) {
//            name = "适用发票类型";
//        }
//        try {
//            String fileNamePath = request.getSession().getServletContext().getRealPath("/") + "resources/excel/TRX/保理-应收账款模板.xlsx";
//            File file = new File(fileNamePath);
//            boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
//            String fileName = "保理-应收账款模板.xlsx";
//            if (isMSIE) {
//                fileName = URLEncoder.encode(fileName, "UTF-8");
//            } else {
//                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
//            }
//            if (file.exists()) {
//                response.addHeader("Content-Disposition", "attachment;filename=\"" +fileName + "\"");
//                response.setContentType("EXCEL;charset=UTF-8");
//                response.setHeader("Accept-Ranges", "bytes");
//                int fileLength = (int) file.length();
//                response.setContentLength(fileLength);
//                if (fileLength > 0) {
//                    writeFileToResp(response, file);
//                }
//            } else {
//                response.getWriter().write("文件不存在！");
//            }
//        } catch (IOException e) {
//            throw new FileReadIOException();
//        }
//    }
//
//    @RequestMapping(value = "/hlsCus/bp/excel/file/upload")
//    public void uploadBpData(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileType) throws FileReadIOException, TokenException {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            String fileNamePath = request.getSession().getServletContext().getRealPath("/") + "resources/excel/BP/";
//            String fileName = "";
//            String addHeader = "attachment;filename=\"";
//            if ("LIABILITIES".equals(fileType)) {
//                fileName = "或有负债导入模板.xlsx";
//                //addHeader += URLEncoder.encode("或有负债导入模板.xlsx", "UTF-8");
//            } else if ("FINANCING".equals(fileType)) {
//                fileName = "融资情况导入模板.xlsx";
//                //addHeader += URLEncoder.encode("融资情况导入模板.xlsx", "UTF-8");
//            } else if ("ASSETS".equals(fileType)) {
//                fileName = "股权资产导入模板.xlsx";
//                //addHeader += URLEncoder.encode("股权资产导入模板.xlsx", "UTF-8");
//            } else if (StringUtils.equals("LEGAL",fileType)){
//                fileName = "财报导入模板_企业法人.xlsx";
//                //addHeader += URLEncoder.encode("财报导入模板_企业法人.xlsx", "UTF-8");
//            }else if (StringUtils.equals("PUBLIC", fileType)){
//                fileName = "财报导入模板_事业单位.xlsx";
//                //addHeader += URLEncoder.encode("财报导入模板_事业单位.xlsx", "UTF-8");
//            }else if (StringUtils.equals("ACR_INVOICE_IMPORT_TEMPLATE", fileType)){
//                fileName = "销项发票导入模板.xls";
//                //addHeader += URLEncoder.encode("销项发票导入模板.xls", "UTF-8");
//            }else if (StringUtils.equals("ACP_INVOICE_IMPORT_TEMPLATE", fileType)){
//                fileName = "进项发票导入模板.xls";
//                //addHeader += URLEncoder.encode("进项发票导入模板.xls", "UTF-8");
//            } else{
//                throw new IllegalArgumentException("未知错误");
//            }
//            fileNamePath += fileName;
//            File file = new File(fileNamePath);
//            boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
//            if (isMSIE) {
//                //IE浏览器的乱码问题解决
//                fileName = URLEncoder.encode(fileName, "UTF-8");
//            } else {
//                //万能乱码问题解决
//                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
//            }
//            addHeader += (fileName + "\"");
//
//            if (file.exists()) {
//                response.addHeader("Content-Disposition", addHeader);
//                response.setContentType("EXCEL;charset=UTF-8");
//                response.setHeader("Accept-Ranges", "bytes");
//                int fileLength = (int) file.length();
//                response.setContentLength(fileLength);
//                if (fileLength > 0) {
//                    writeFileToResp(response, file);
//                }
//            } else {
//                response.getWriter().write("文件不存在！");
//            }
//        } catch (IOException e) {
//            throw new FileReadIOException();
//        }
//    }
//
//    @RequestMapping(value = "/hlsCus/prj/excel/file/upload")
//    public void uploadPrjData(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileType) throws FileReadIOException, TokenException {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            String fileNamePath = request.getSession().getServletContext().getRealPath("/") + "resources/excel/PRJ/";
//            String fileName = "";
//            String addHeader = "attachment;filename=\"";
//            if ("PRJ_MORTGAGE".equals(fileType)||"FCT_MORTGAGE".equalsIgnoreCase(fileType)) {
//                fileName += "抵押物财产清单导入模板.xlsx";
//                //addHeader += URLEncoder.encode("抵押物财产清单导入模板.xlsx", "UTF-8");
//            } else if ("PRJ_LEASE_ITEM".equals(fileType)) {
//                fileName += "租赁物导入模板.xlsx";
//                //addHeader += URLEncoder.encode("租赁物清单导入模板.xlsx", "UTF-8");
//            } else {
//                throw new IllegalArgumentException("未知错误");
//            }
//            fileNamePath += fileName;
//            File file = new File(fileNamePath);
//            boolean isMSIE = AttachmentHttpUtils.isMSBrowser(request);
//            if (isMSIE) {
//                fileName = URLEncoder.encode(fileName, "UTF-8");
//            } else {
//                fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
//            }
//            addHeader += (fileName + "\"");
//            if (file.exists()) {
//                response.addHeader("Content-Disposition", addHeader);
//                response.setContentType("EXCEL;charset=UTF-8");
//                response.setHeader("Accept-Ranges", "bytes");
//                int fileLength = (int) file.length();
//                response.setContentLength(fileLength);
//                if (fileLength > 0) {
//                    writeFileToResp(response, file);
//                }
//            } else {
//                response.getWriter().write("文件不存在！");
//            }
//        } catch (IOException e) {
//            throw new FileReadIOException();
//        }
//    }

//    @RequestMapping(value = "/hlsCus/fct/excel/file/upload")
//    public void uploadFctData(HttpServletRequest request, HttpServletResponse response, @RequestParam String fileType) throws FileReadIOException, TokenException {
//        IRequest requestContext = createRequestContext(request);
//        try {
//            String fileName = request.getSession().getServletContext().getRealPath("/") + "resources/excel/FCT/";
//            String addHeader = "attachment;filename=\"";
//            if ("FCT_MORTGAGE".equals(fileType)) {
//                fileName += "抵押物财产清单导入模板.xlsx";
//                addHeader += URLEncoder.encode("抵押物财产清单导入模板.xlsx", "UTF-8");
//            } else {
//                throw new IllegalArgumentException("未知错误");
//            }
//            addHeader += "\"";
//            File file = new File(fileName);
//            if (file.exists()) {
//                response.addHeader("Content-Disposition", addHeader);
//                response.setContentType("EXCEL;charset=UTF-8");
//                response.setHeader("Accept-Ranges", "bytes");
//                int fileLength = (int) file.length();
//                response.setContentLength(fileLength);
//                if (fileLength > 0) {
//                    writeFileToResp(response, file);
//                }
//            } else {
//                response.getWriter().write("文件不存在！");
//            }
//        } catch (IOException e) {
//            throw new FileReadIOException();
//        }
//    }
//
//    private void writeFileToResp(HttpServletResponse response, File file) throws FileNotFoundException, IOException {
//        byte[] buf = new byte[BUFFER_SIZE];
//        try (InputStream inStream = new FileInputStream(file);
//             ServletOutputStream outputStream = response.getOutputStream()) {
//            int readLength;
//            while (((readLength = inStream.read(buf)) != -1)) {
//                outputStream.write(buf, 0, readLength);
//            }
//            outputStream.flush();
//        }
//    }

   /* @RequestMapping(value = "/hlsCus/lon/contract/export")
    public void createXLS(HttpServletRequest request, @RequestParam String config,
                          HttpServletResponse httpServletResponse, HttpSession session) {
        IRequest requestContext = createRequestContext(request);
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class,
                    ExportConfig.class, HlsCusLonContract.class, ColumnInfo.class);
            ExportConfig<HlsCusLonContract, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
            exportConfig.getParam().setCompanyId((Long) session.getAttribute("companyId"));
            excelService.exportAndDownloadExcel("hls.core.lon.mapper.HlsCusLonContractMapper.lonContractQuery",
                    exportConfig, request, httpServletResponse, requestContext);
        } catch (IOException e) {
            e.printStackTrace();
        }vat/mapper/HlsCusAcrInvoiceHdMapper.xml
    }*/
//   @RequestMapping(value="/hlsCus/invoice/details/export")
//    public void createXLS(HttpServletRequest request, @RequestParam String config,
//                          HttpServletResponse response, HttpSession session){
//       IRequest requestContext=createRequestContext(request);
//       try{
//           JavaType type= objectMapper.getTypeFactory().constructParametrizedType(ExportConfig.class, ExportConfig.class,
//                   HlsCusAcrInvoicePkg.class, ColumnInfo.class);
//           ExportConfig<HlsCusAcrInvoicePkg, ColumnInfo> exportConfig = objectMapper.readValue(config, type);
//           exportConfig.getParam().getLsCusAcrInvoiceHd().setCompanyId((Long) session.getAttribute("companyId"));
//           excelService.exportAndDownloadExcel("hls.core.vat.mapper.HlsCusAcrInvoiceMapper.queryInvoiceExport",
//                   exportConfig, request, response, requestContext);
//       } catch (IOException e){
//           e.printStackTrace();
//       }
//   }
//
//
//    @RequestMapping(value = "/hls/cus/vat/acr/invoice/update")
//    @ResponseBody
//    public void acrInvoiceUpdate(final HttpServletRequest request) {
//        IRequest iRequest = createRequestContext(request);
//        HlsCusAcrInvoiceHd hlsCusAcrInvoiceHd = new HlsCusAcrInvoiceHd();
//        hlsCusAcrInvoiceHdService.updateInvoiceStatusAfterExport(hlsCusAcrInvoiceHd, iRequest);
//    }



    /**
     * 调息通知书批量打印
     */
    @RequestMapping(value = "/vat/acr/invoice/download")
    public ResponseData batchDownload(@ModelAttribute("_request_data") LeafRequestData requestData, HttpServletRequest request, HttpServletResponse response) {
       //@RequestParam String jsonStr
        List<HlsCusAcrInvoiceHd> dto = JSON.parseArray(JSON.toJSONString(requestData.get("parameter")), HlsCusAcrInvoiceHd.class);
        ResponseData rd = new ResponseData(false);
        try {
            hlsCusAcrInvoiceHdService.saveDocAndDownload(dto, request, response);
            rd.setSuccess(true);
            rd.setMessage("生成成功");
        } catch (Exception e) {
            e.printStackTrace();
            rd.setSuccess(false);
            rd.setMessage(e.getMessage());
        }

        return rd;

    }
//     //发票红冲
//    @RequestMapping(value = "/fct/vat/acrInvoiceRush")
//    @ResponseBody
//    public ResponseData acrInvoiceRush(@RequestBody List<HlsCusAcrInvoiceHd> acrInvoiceHdLists, final HttpServletRequest request) {
//        IRequest iRequest = createRequestContext(request);
//        for (HlsCusAcrInvoiceHd acrInvoiceHd:acrInvoiceHdLists){
//            acrInvoiceHd.setCompanyId(iRequest.getCompanyId());
//
//        }
//        return new ResponseData();
//    }

    //发票关闭
    @RequestMapping(value = "/vat/acr/invoice/close")
    @ResponseBody
    public ResponseData closeVatInvoice(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        ResponseData result = new ResponseData(true);
        JSONArray parameter = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrInvoiceHd> acrInvoiceHdList = parameter.toJavaList(HlsCusAcrInvoiceHd.class);
      try {
          hlsCusAcrInvoiceHdService.closeVatInvoice(acrInvoiceHdList);
          result.setMessage("发票关闭成功");
      }catch (Exception e) {
          e.printStackTrace();
          result.setSuccess(false);
          result.setMessage("发票关闭失败：" + e.getMessage());
      }
      return result;
    }

    // 发票导出修改发票状态
    @RequestMapping(value = "/vat/acr/invoice/excel/export")
    public void exportVatAcrInvoiceExcel(HttpServletRequest request, @RequestParam String invoiceHdIdStr, HttpServletResponse response) {
        try {
            hlsCusAcrInvoiceHdService.exportVatAcrInvoiceExcel(request,response,invoiceHdIdStr);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发票红冲
    @RequestMapping(value = "/vat/acr/invoice/reverse")
    @ResponseBody
    public ResponseData reverseVatAcrInvoice(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData) {
        ResponseData result = new ResponseData(true);
        JSONArray param = (JSONArray) requestData.get("parameter");
        List<HlsCusAcrInvoiceHd> acrInvoiceHdList = param.toJavaList(HlsCusAcrInvoiceHd.class);
        try {
            hlsCusAcrInvoiceHdService.reverseVatAcrInvoice(acrInvoiceHdList);
            result.setMessage("发票红冲成功");
        }catch (Exception e) {
            System.out.println(e.getStackTrace());
            result.setSuccess(false);
            result.setMessage("发票红冲失败：" + e.getMessage());
        }
        return result;
    }

    // 发票查询
    @RequestMapping(value = "/vat/acr/invoice/detail/query")
    @ResponseBody
    public ResponseData queryVatAcrInvoiceDetail(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE) final int page,
                                                 @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) final int pageSize) {
        PageHelper.startPage(page, pageSize);
        IRequest iRequest = RequestHelper.getCurrentRequest();
        JSONObject params = (JSONObject) requestData.get("parameter");
        Map<String, String> param = JSONObject.toJavaObject(params, Map.class);
        return new ResponseData(acrInvoiceHdMapper.queryVatAcrInvoiceDetail(param));
    }

    // 销项发票导入
    @RequestMapping(value = "/vat/acr/invoiceExcelImportConfirm", method = RequestMethod.POST)
    public Map<String, Object> importConfirm(HttpServletRequest request, Long headerId) throws IOException {
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            hlsCusAcrInvoiceHdService.acrInvoiceExcelImport(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }


    // 进项发票导入
    @RequestMapping(value = "/vat/acp/invoice/excel/import", method = RequestMethod.POST)
    public Map<String, Object> importVatAcpInvoice(HttpServletRequest request, Long headerId){
        IRequest iRequest = createRequestContext(request);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", false);
        try {
            hlsCusAcrInvoiceHdService.importVatAcpInvoice(iRequest, headerId);
            response.put("message", "导入成功");
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "导入失败！" + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }

    public Map fitParam(Map param) {
        IRequest iRequest = RequestHelper.getCurrentRequest();
//        param.put("companyId", iRequest.getCompanyId());
        int page = Integer.parseInt(String.valueOf(param.get("page")));
        int pageSize = Integer.parseInt(String.valueOf(param.get("pageSize")));
        PageHelper.startPage(page, pageSize);
        return param;
    }

    @RequestMapping(value = "/invoice/handover/update")
    @ResponseBody
    public ResponseData update(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONArray param = (JSONArray)requestData.get("parameter");
        List<HlsCusAcrInvoiceHd> dto= param.toJavaList(HlsCusAcrInvoiceHd.class);
        List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList =  hlsCusAcrInvoiceHdService.updateHandoverStatus(requestCtx, dto);
        return new ResponseData(hlsCusAcrInvoiceHdList);
    }

    @RequestMapping(value = "/invoice/handover/confirm/update")
    @ResponseBody
    public ResponseData updateInvoiceConfirm(@ModelAttribute(LEAF_PARAM_NAME) LeafRequestData requestData , HttpServletRequest request) {
        IRequest requestCtx = createRequestContext(request);

        JSONArray param = (JSONArray)requestData.get("parameter");
        List<HlsCusAcrInvoiceHd> dto= param.toJavaList(HlsCusAcrInvoiceHd.class);
        List<HlsCusAcrInvoiceHd> hlsCusAcrInvoiceHdList =  hlsCusAcrInvoiceHdService.updateHandoverConfirmStatus(requestCtx, dto);
        return new ResponseData(hlsCusAcrInvoiceHdList);
    }

}
