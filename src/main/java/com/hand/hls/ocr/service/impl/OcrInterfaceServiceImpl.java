package com.hand.hls.ocr.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.atm.dto.FndAttachmentMulti;
import com.hand.hls.atm.mapper.FndAttachmentMapper;
import com.hand.hls.atm.mapper.FndAttachmentMultiMapper;
import com.hand.hls.atm.service.IFndAttachmentService;
import com.hand.hls.common.dto.HlsCusHapInterfaceOutbound;
import com.hand.hls.common.service.HlsCusHapInterfaceOutboundService;
import com.hand.hls.exception.HlsCusException;
import com.hand.hls.fnd.service.FndCodingRuleValuesService;
import com.hand.hls.ocr.service.OcrInterfaceService;
import com.hand.hls.prj.dto.HlsCusPrjProjectLeaseItem;
import com.hand.hls.prj.mapper.HlsCusPrjProjectLeaseItemMapper;
import com.hand.hls.utils.HttpClientUtils;
import com.hand.hls.utils.HttpExecuteResponse;
import com.hand.hls.vat.dto.HlsCusAcpInvoiceLn;
import com.hand.hls.vat.service.IAcpInvoiceLnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLDecoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

@Service
@Transactional
public class OcrInterfaceServiceImpl implements OcrInterfaceService {
    @Value("${file.upload.dir:.}")
    private String savePath = ".";
    @Autowired
    private IFndAttachmentService fndAttachmentService;
    @Autowired
    private HlsCusHapInterfaceOutboundService hapInterfaceOutboundService;
    @Autowired
    private IAcpInvoiceLnService iAcpInvoiceLnService;
    @Autowired
    private HlsCusPrjProjectLeaseItemMapper hlsCusPrjProjectLeaseItemMapper;
    @Autowired
    private FndCodingRuleValuesService fndCodingRuleValuesService;
    @Autowired
    private FndAttachmentMapper fndAttachmentMapper;
    @Autowired
    private FndAttachmentMultiMapper fndAttachmentMultiMapper;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void ocrImportInvoice(HttpServletRequest request, IRequest iRequest)throws HlsCusException {
        String base64 = "";
        Long attachmentId = null;
        try {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            if (!multipartResolver.isMultipart(request)) {
                throw new HlsCusException("文件类型错误！");
            } else {
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                String sourceType = "acp_invoice_ln";
                String filename = multiRequest.getParameter("filename");
                filename = URLDecoder.decode(filename, "UTF-8");
                Iterator iter = multiRequest.getFileNames();

                while (iter.hasNext()) {
                    MultipartFile file = multiRequest.getFile(iter.next().toString());
                    if (file != null) {
                        String path = getSavePath();
                        File target = new File(path);
                        file.transferTo(target);
                        //上传附件
                        attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(filename, "UTF-8"), path, sourceType, "pkValue", file.getSize());
                        byte[] data = null;
                        // 读取图片字节数组
                        try {
                            InputStream in = new FileInputStream(path);
                            data = new byte[in.available()];
                            in.read(data);
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        // 对字节数组Base64编码
                        BASE64Encoder encoder = new BASE64Encoder();
                        // 返回Base64编码过的字节数组字符串
                        base64 = encoder.encode(Objects.requireNonNull(data));
                    }
                }
            }
        } catch (Exception e) {
            throw new HlsCusException("文件读取失败");
        }
        String url = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("OCR_INTERFACE","invoice_url");
        String appcode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("OCR_INTERFACE","app_code");

        HashMap<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        //如果需要使用本地图片，需要将图片base64码放在img后面，如果使用网络图片，则需要将网络图片url放于url参数后面
        String bodys = "{\"img\":\""+base64+"\",\"url\":\"\",\"prob\":false,\"charInfo\":false,\"rotate\":false,\"table\":false}";

        Date endDate=null;
        Date startDate=null;
        long start=0L;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response = HttpClientUtils.doPost(url,bodys, headers);
        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("ocr发票识别");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }

        try {
            HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
        }

        //分析返回值
        responseAnalysis(response);
        //插入发票表
        Long lnId = insertAcpInvoiceLn(iRequest,response.getResponseAsString());
        String pkValue = lnId == null?"xxxx":lnId.toString();
        //更新附件
        updatePkValue(attachmentId,pkValue);
    }

    /**
     * 更新附件 pkValue
     * @param attachmentId
     * @param pkValue
     */
    private void updatePkValue(Long attachmentId,String pkValue){
        FndAttachment fndAttachment  = fndAttachmentMapper.selectByPrimaryKey(attachmentId);
        String recordId = fndAttachment.getSourcePkValue();
        FndAttachmentMulti fndAttachmentMulti = new FndAttachmentMulti();
        fndAttachmentMulti.setRecordId(Long.valueOf(recordId));
        fndAttachmentMulti.setTablePkValue(pkValue);
        fndAttachmentMultiMapper.updateByPrimaryKeySelective(fndAttachmentMulti);
    }
    /**
     * 分析接口返回值，提示错误信息
     * @param response
     * @throws HlsCusException
     */
    private void responseAnalysis(HttpExecuteResponse response) throws HlsCusException{
        if(!response.isSuccess()){
            String invoiceJsonStr = response.getResponseAsString();

            JSONObject invoiceJson = null;
            try {
                invoiceJson = JSONObject.parseObject(invoiceJsonStr);
            } catch (Exception e) {
                throw new HlsCusException(invoiceJsonStr);
            }
            if(invoiceJson != null && invoiceJson.getString("error_code") != null){
                throw new HlsCusException(invoiceJson.getString("error_msg"));
            }
        }
    }

    /**
     * 解析字符串(识别结果)存到发票表里
     * @param invoiceJsonStr
     */
    private Long insertAcpInvoiceLn(IRequest iRequest,String invoiceJsonStr) throws HlsCusException{
        JSONObject invoiceJson = JSONObject.parseObject(invoiceJsonStr).getJSONObject("data");
        JSONArray jsonArray = invoiceJson.getJSONArray("发票详单");
        checkInvoice(invoiceJson.getString("发票代码"),invoiceJson.getString("发票号码"));
        for(int i=0; i<jsonArray.size(); i++){
            HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn = new HlsCusAcpInvoiceLn();
            hlsCusAcpInvoiceLn.setInvoiceType("0"); //默认专用发票
            hlsCusAcpInvoiceLn.setInvoiceCode(invoiceJson.getString("发票代码"));
            hlsCusAcpInvoiceLn.setInvoiceNumber(invoiceJson.getString("发票号码"));
            hlsCusAcpInvoiceLn.setRecordDate(invoiceJson.getDate("开票日期"));
            hlsCusAcpInvoiceLn.setBpName(invoiceJson.getString("销售方名称"));
            hlsCusAcpInvoiceLn.setTotalAmount(invoiceJson.getDouble("发票金额"));
            String refV01 = jsonArray.getJSONObject(i).getString("货物或应税劳务、服务名称");
            //获得第一个*的位置
            int index=refV01.indexOf("*");
            //根据第一个*的位置 获得第二个*的位置
            index=refV01.indexOf("*", index+1);
            //根据第二个*的位置，截取 字符串。得到结果 result
            String result=refV01.substring(index+1);

            hlsCusAcpInvoiceLn.setRefV01(result);
            String rateStr = jsonArray.getJSONObject(i).getString("税率");
            hlsCusAcpInvoiceLn.setTaxTypeRate(strToDouble(rateStr));
            hlsCusAcpInvoiceLn.setContractId(getContractId(hlsCusAcpInvoiceLn.getBpName()));
            hlsCusAcpInvoiceLn.setInvoiceStatus("NEW");
            hlsCusAcpInvoiceLn.setDocumentNumber(getAcpNumber(iRequest));
            hlsCusAcpInvoiceLn = iAcpInvoiceLnService.insert(iRequest,hlsCusAcpInvoiceLn);
            return hlsCusAcpInvoiceLn.getInvoiceLnId();
        }
        return null;
    }

    /**
     * 校验发票代码，发票号码是否重复
     * @param invoiceCode 发票代码
     * @param invoiceNumber 发票号码
     * @throws HlsCusException
     */
    private void checkInvoice(String invoiceCode,String invoiceNumber) throws HlsCusException{
        if(invoiceCode == null){
            throw new HlsCusException("未识别到发票代码！");
        }
        if(invoiceNumber == null){
            throw new HlsCusException("未识别到发票号码！");
        }
        HlsCusAcpInvoiceLn hlsCusAcpInvoiceLn = new HlsCusAcpInvoiceLn();
        hlsCusAcpInvoiceLn.setInvoiceCode(invoiceCode);
        hlsCusAcpInvoiceLn.setInvoiceNumber(invoiceNumber);
        List<HlsCusAcpInvoiceLn> result = iAcpInvoiceLnService.selectSelective(null,hlsCusAcpInvoiceLn);
        if(result.size() > 0){
            throw new HlsCusException("发票代码和发票号码与已有进项发票数据重复，请核对！");
        }
    }

    /**
     * 百分数转小数
     * @param percentage 字符串类型的百分数
     * @return
     */
    private Double strToDouble(String percentage){
        NumberFormat nf = NumberFormat.getPercentInstance();
        Number m = null;
        try {
            m = nf.parse(percentage);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return m.doubleValue();
    }

    /**
     * 获取合同信息
     * @param bpName 供应商名称
     * @return
     */
    private Long getContractId(String bpName){
        HlsCusPrjProjectLeaseItem para = new HlsCusPrjProjectLeaseItem();
        para.setVenderIdN(bpName);
        List<HlsCusPrjProjectLeaseItem> list = hlsCusPrjProjectLeaseItemMapper.queryPrjProjectLeaseItemByBpName(para);
        if(list.size() == 1){
            return list.get(0).getProjectId();
        }else{
            return null;
        }
    }

    /**
     * 获取编码
     * @param iRequest
     * @return
     */
    private String getAcpNumber(IRequest iRequest){
        //获取编码规则
        Map<String, String> params = new HashMap<String, String>();
        String documentCategory = "AP_INVOICE";
        String documentType = "ACP";
        String businessType = "ACP";
        return fndCodingRuleValuesService.getCodeRuleValue(iRequest, documentCategory, documentType, businessType, params);
    }

    /**
     * 获取图片并转换成base64格式
     * @param request
     * @return
     * @throws Exception
     */
    private String getImageBase64(HttpServletRequest request)throws Exception{
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (!multipartResolver.isMultipart(request)) {
            throw new HlsCusException("文件类型错误！");
        } else {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            Iterator iter = multiRequest.getFileNames();

            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {

                    File file1 = null;
                    file1 = File.createTempFile("temp", null);
                    file.transferTo(file1);

                    byte[] data = null;
                    // 读取图片字节数组
                    try {
                        InputStream in = new FileInputStream(file1);
                        data = new byte[in.available()];
                        in.read(data);
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 对字节数组Base64编码
                    BASE64Encoder encoder = new BASE64Encoder();
                    // 返回Base64编码过的字节数组字符串
                    return encoder.encode(Objects.requireNonNull(data));
                }
            }
        }

        return "";
    }
    private String uploadFile(HttpServletRequest request) throws Exception{
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if (!multipartResolver.isMultipart(request)) {
            throw new HlsCusException("文件类型错误！");
        } else {
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest)request;
            String sourceType = "OCR_FILE";
            String pkValue = "xxxx";
//            String sourceType = multiRequest.getParameter("source_type");
//            String pkValue = multiRequest.getParameter("pkvalue");
            String filename = multiRequest.getParameter("filename");
            filename = URLDecoder.decode(filename,"UTF-8");
            Iterator iter = multiRequest.getFileNames();
            Long attachmentId = null;

            while (iter.hasNext()) {
                MultipartFile file = multiRequest.getFile(iter.next().toString());
                if (file != null) {
                    String path = getSavePath();
                    File target = new File(path);
                    file.transferTo(target);
                    attachmentId = fndAttachmentService.uploadAttachment(URLDecoder.decode(filename, "UTF-8"), path, sourceType, pkValue, file.getSize());
                    byte[] data = null;
                    // 读取图片字节数组
                    try {
                        InputStream in = new FileInputStream(path);
                        data = new byte[in.available()];
                        in.read(data);
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 对字节数组Base64编码
                    BASE64Encoder encoder = new BASE64Encoder();
                    // 返回Base64编码过的字节数组字符串
                    System.out.println("本地图片转换Base64:" + encoder.encode(Objects.requireNonNull(data)));
                    return encoder.encode(Objects.requireNonNull(data));
                }
            }
            return "";
        }

    }
    private String getSavePath() {
        String filePath = this.savePath;
        return filePath + File.separator + this.getUUIDFileName();
    }
    @Override
    public String getSavePathDir() {
        String filePath = this.savePath;
        return filePath + File.separator;
    }

    private String getUUIDFileName() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String ocrImportTable(HttpServletRequest request, IRequest iRequest)throws HlsCusException{
        String base64 = "";
        try {
            base64 = getImageBase64(request);
        } catch (Exception e) {
            throw new HlsCusException("文件读取失败");
        }
        String url = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("OCR_INTERFACE","table_url");
        String appcode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("OCR_INTERFACE","app_code");

        HashMap<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        //如果需要使用本地图片，需要将图片base64码放在img后面，如果使用网络图片，则需要将网络图片url放于url参数后面
//        String bodys = "{\"img\":\""+base64+"\",\"configure\":\"{\"format\":\"json\",\"dir_assure\":false,\"line_less\":false}\"}";
        //请根据线上文档修改configure字段
        JSONObject configObj = new JSONObject();
        configObj.put("format", "xlsx");
        configObj.put("finance", false);
        configObj.put("dir_assure", false);
        String config_str = configObj.toString();


        // 拼装请求body的json字符串
        JSONObject requestObj = new JSONObject();
        requestObj.put("image", base64);
        if(config_str.length() > 0) {
            requestObj.put("configure", config_str);
        }
        String bodys = requestObj.toString();

        Date startDate=null;
        long start=0L;
        Date endDate=null;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response = HttpClientUtils.doPost(url,bodys, headers);

        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("ocr表格识别");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }
        HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);

        //分析返回值
        responseAnalysis(response);

        //图片转excel文件
        return imageToFile(response.getResponseAsString());
    }

    /**
     * 解析返回值生成文件
     * @param invoiceJsonStr
     * @return
     */
    private String imageToFile(String invoiceJsonStr){
        JSONObject invoiceJson = JSONObject.parseObject(invoiceJsonStr);
        BASE64Decoder decoder = new BASE64Decoder();
        String filePath = this.savePath;
        String fileName = this.getUUIDFileName()+".xlsx";
        filePath = filePath + File.separator + fileName;
        byte[] buffer = null;
        try {
            buffer = decoder.decodeBuffer(invoiceJson.getString("tables"));
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(buffer);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    @Override
    public String ocrImportPdf(HttpServletRequest request, IRequest iRequest)throws HlsCusException{
        String base64 = "";
        try {
            base64 = getImageBase64(request);
        } catch (Exception e) {
            throw new HlsCusException("文件读取失败");
        }
        String url = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("OCR_INTERFACE","pdf_url");
        String appcode = hlsCusPrjProjectLeaseItemMapper.getValueSysCode("OCR_INTERFACE","app_code");

        HashMap<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        //根据API的要求，定义相对应的Content-Type
        headers.put("Content-Type", "application/json; charset=UTF-8");
        //如果需要使用本地图片，需要将图片base64码放在img后面，如果使用网络图片，则需要将网络图片url放于url参数后面
        String bodys = "{\"fileBase64\":\""+base64+"\",\"url\":\"\",\"prob\":false,\"charInfo\":false,\"rotate\":false,\"table\":true,\"fileType\":\"word\"}";


        Date startDate=null;
        long start=0L;
        Date endDate=null;
        long end=0L;
        start = System.currentTimeMillis();
        startDate=new Date();
        //发送请求
        HttpExecuteResponse response = HttpClientUtils.doPost(url,bodys, headers);
        logger.info("---------------ocrpdf接口请求返回值-------------------");
        logger.info(response.getResponseAsString());
        if(response.getResponseCode()==-1){
            logger.error("---------------ocrpdf错误信息-------------------");
            logger.error(response.getErrorMessage());
            throw new HlsCusException(response.getErrorMessage());
        }
        end = System.currentTimeMillis();
        endDate=new Date();
        //日志信息插入
        HlsCusHapInterfaceOutbound outbound= new HlsCusHapInterfaceOutbound();
        outbound.setInterfaceName("ocr pdf识别");
        outbound.setInterfaceUrl(url);
        outbound.setRequestParameter(bodys);
        outbound.setResponseContent(response.getResponseAsString());
        outbound.setRequestTime(new Date());//请求时间
        outbound.setResponseTime(end-start);//响应时间
        outbound.setStartDate(startDate);//开始时间
        outbound.setEndDate(endDate);//结束时间
        outbound.setResponseCode(String.valueOf(response.getResponseCode()));//请求code
        outbound.setLineId(null);
        if(response.isSuccess()){
            outbound.setRequestStatus("success");
        }else{
            outbound.setRequestStatus("failure");
        }
        try{
            HlsCusHapInterfaceOutbound outboundData= hapInterfaceOutboundService.insert(iRequest,outbound);
        } catch (Exception e) {
            logger.error("-----------插入日志信息出错---------");
            e.printStackTrace();
        }
        //分析返回值
        responseAnalysis(response);

        //pdf转word文件
        return pdfToWord(response.getResponseAsString());
    }

    /**
     * 解析返回值生成word文件
     * @param invoiceJsonStr
     * @return
     */
    private String pdfToWord(String invoiceJsonStr){
        JSONObject invoiceJson = JSONObject.parseObject(invoiceJsonStr);
        BASE64Decoder decoder = new BASE64Decoder();
        String filePath = this.savePath;
        String fileName = this.getUUIDFileName()+".docx";
        filePath = filePath + File.separator + fileName;
        byte[] buffer = null;
        try {
            buffer = decoder.decodeBuffer(invoiceJson.getString("fileBase64"));
            FileOutputStream out = new FileOutputStream(filePath);
            out.write(buffer);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileName;
    }
}
