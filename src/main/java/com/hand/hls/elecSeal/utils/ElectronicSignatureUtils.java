package com.hand.hls.elecSeal.utils;

import cfca.paperless.ClientConstants;

import cfca.paperless.base.util.Base64;
import cfca.paperless.base.util.IoUtil;
import cfca.paperless.base.util.StringUtil;
import cfca.paperless.dto.RequestHead;
import cfca.paperless.dto.ResponseDto;
import cfca.paperless.dto.ResponseHead;
import cfca.paperless.dto.bean.SealStrategy;
import cfca.paperless.dto.request.requestbody.tx30.TransformWordToPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SealPdfRequestBody;
import cfca.paperless.dto.request.tx30.TransformWordToPdfRequest;
import cfca.paperless.dto.request.tx40.SealPdfRequest;
import cfca.paperless.dto.response.responsebody.tx30.TransformWordToPdfResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.SealPdfResponseBody;
import cfca.paperless.dto.response.tx30.TransformWordToPdfResponse;
import cfca.paperless.dto.response.tx40.SealPdfResponse;
import com.hand.hls.atm.dto.FndAttachment;
import com.hand.hls.elecSeal.dto.BaseConstants;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * 电子签章
 */
@Component
public class ElectronicSignatureUtils {
    @Autowired
    private PaperlessClient paperlessClient;

    public String organizationCode = "003";
    public String operationCode = "";
    public String channelCode = "";
    public String sealCode = "RSA01";
    public String sealPassword="cfca1234";
    @Value("${file.upload.dir}")
    private String uploadFileAddr;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * @Description: Word转换PDF -Word文件
     * @author: zf
     * @date: 2019年6月25日 上午11:59:42
     */
    public File TransformWordToPdfTest(File sysFile){
        try {
            String functionName = "TransformWordToPdfTest01";

            // 开始时间
            long timeStart = System.currentTimeMillis();
            System.out.println(functionName + " START.");
            String TransformWordToPdfTestFilepath=null;
            System.out.println(sysFile.getPath());
            TransformWordToPdfTestFilepath = this.doWork(sysFile.getPath());
            // 结束时间
            long timeEnd = System.currentTimeMillis();
            System.out.println(functionName + " END." + "time-taken=" + (timeEnd - timeStart));
            String resultOutputFilepath=null;
            if(StringUtil.isNotEmpty(TransformWordToPdfTestFilepath)){
                resultOutputFilepath = this.SealPdf(TransformWordToPdfTestFilepath);
            }else{
                throw new RuntimeException("world转pdf返回路径为空！");
            }

            System.out.println(resultOutputFilepath);


            File file=new File(resultOutputFilepath);
            return file;
        } catch (Exception e) {
            logger.error("world转pdf失败",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String doWork(String url) throws Exception {

        paperlessClient.setSSL(false);
        paperlessClient.setSslProtocol(PaperlessConfig.sslProtocol);
        paperlessClient.setKeyStorePath(PaperlessConfig.keyStorePath);
        paperlessClient.setKeyStorePassword(PaperlessConfig.keyStorePassword);
        paperlessClient.setTrustStorePath(PaperlessConfig.trustStorePath);
        paperlessClient.setTrustStorePassword(PaperlessConfig.trustStorePassword);

        // ------构造请求报文头------
        String transactionNo = cfca.paperless.base.util.GUIDUtil.generateId();
        String organizationCode = PaperlessConfig.organizationCode;
        String operatorCode = PaperlessConfig.operationCode;
        String channelCode = PaperlessConfig.channelCode;

        // 请求报文头
        RequestHead requestHeadBean = new RequestHead();
        requestHeadBean.setTransactionNo(transactionNo);
        requestHeadBean.setOrganizationCode(organizationCode);
        requestHeadBean.setOperatorCode(operatorCode);
        requestHeadBean.setChannelCode(channelCode);

        // ------构造请求报文体------
        String wordFilePath = url;
        byte[] wordData = FileUtils.readFileToByteArray(new File(wordFilePath));
        System.out.println("wordData:"+wordData);
        String wordDataStr =  new String(Base64.encode(wordData), PaperlessConfig.DEFAULT_CHARSET);

        // 请求报文体
        cfca.paperless.dto.request.requestbody.tx30.TransformWordToPdfRequestBody requestBodyBean = new TransformWordToPdfRequestBody();
        requestBodyBean.setInputSource(wordDataStr);
        requestBodyBean.setOutputFilepath("");

        // ------构造请求报文对象------
        cfca.paperless.dto.request.tx30.TransformWordToPdfRequest requestBean = new TransformWordToPdfRequest();
        requestBean.setHead(requestHeadBean);
        requestBean.setBody(requestBodyBean);

        // ------调用接口------
        System.out.println("world转pdf调用接口");
        ResponseDto responseDto = paperlessClient.execute(requestBean);

        // 接收响应报文对象
        TransformWordToPdfResponse responseBean = (TransformWordToPdfResponse) responseDto;

        // 响应报文头
        ResponseHead responseHeadBean = responseBean.getHead();
        // 响应报文体
        TransformWordToPdfResponseBody responseBodyBean = responseBean.getBody();

        System.out.println(responseHeadBean);

        // String transactionNo = responseHeadBean.getTransactionNo();
        String code = responseHeadBean.getCode();
        String message = responseHeadBean.getMessage();

        if (ClientConstants.CODE_SUCCESS.equals(code)) {
            System.out.println("OK.code=" + code + ",message=" + message);

            String resultOutputFilepath = responseBodyBean.getOutputFilepath();

            byte[] pdf = responseBodyBean.getPdf();
            System.out.println(pdf == null ? 0 : pdf.length);

            String resultPdfFileDataHash = responseBodyBean.getPdfFileDataHash();
            System.out.println("resultPdfFileDataHash=" + resultPdfFileDataHash);

            if (StringUtil.isNotEmpty(resultOutputFilepath)) {
                System.out.println("转换后的文件已输出到："+ resultOutputFilepath);
            } else {

                resultOutputFilepath = uploadFileAddr+"/" + cfca.paperless.base.util.GUIDUtil.generateId() + ".pdf";
                FileUtils.writeByteArrayToFile(new File(resultOutputFilepath), pdf);
                System.out.println("文件已输出到：" + resultOutputFilepath);
            }
            return resultOutputFilepath;
        } else {
            System.err.println("NG.code=" + code + ",message=" + message);
            return  null;
        }
    }

    /**
     *
     * @Description: PDF签章-机构章-使用印章绑定图片签章-坐标签章 、 空白域签章、关键字签章
     * @author: zf
     * @date: 2019年6月25日 上午11:59:18
     */
    public String SealPdf(String url){
        try {
            String functionName = "SealPdfTest01";
            // 开始时间
            long timeStart = System.currentTimeMillis();
            System.out.println(functionName + " START.");
            String resultOutputFilepath=null;
            resultOutputFilepath = this.doPdfWork(functionName,url);
            long timeEnd = System.currentTimeMillis();// 结束时间
            System.out.println("##########" + "time used:" + (timeEnd - timeStart) + "##########");
            return resultOutputFilepath;
        } catch (Exception e) {
            logger.error("pdf盖章失败",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @Description: 签章
     * @throws Exception
     * @return: void
     */
    public String doPdfWork(String functionName,String url) throws Exception {

        paperlessClient.setSSL(false);
        paperlessClient.setSslProtocol(PaperlessConfig.sslProtocol);
        paperlessClient.setKeyStorePath(PaperlessConfig.keyStorePath);
        paperlessClient.setKeyStorePassword(PaperlessConfig.keyStorePassword);
        paperlessClient.setTrustStorePath(PaperlessConfig.trustStorePath);
        paperlessClient.setTrustStorePassword(PaperlessConfig.trustStorePassword);
        /*******************************************************************************/
        // 构造请求对象
        SealPdfRequest sealPdfRequest = new SealPdfRequest();
        RequestHead requestHead = new RequestHead();
        // 业务流水号 非空
        String transactionNo = GUIDUtil.generateId();
        // 机构编码非空
        String organizationCode = PaperlessConfig.organizationCode;
        // 操作员编码 可为空
        String operatorCode = PaperlessConfig.operationCode;
        // 渠道编码 可为空
        String channelCode = PaperlessConfig.channelCode;
        // 设置属性
        requestHead.setBasicInfo(transactionNo, organizationCode, operatorCode, channelCode);

        sealPdfRequest.setHead(requestHead);
        /*******************************************************************************/
        SealPdfRequestBody requestBody = new SealPdfRequestBody();
        // 待签章文件
        String pdfFilePath = url;//D:/TestData/BlankSig.pdf
        byte[] pdfData = FileUtils.readFileToByteArray(new File(pdfFilePath));
        requestBody.setPdfData(pdfData);
        // 数据源类型 1 pdf文件路径 2 pdf文件字节流
        String inputType = cfca.paperless.base.BaseConstants.INPUT_TYPE_FILEDATA;
        // 设置数据源，数据源类型为2时，为空，数据源类型为1时，上送数据源路径（服务器可以访问到的文件路径）
        requestBody.setInputSource("");
        // 数据源类型
        requestBody.setInputType(inputType);
        // 签章后文件保存地址，不为空时，直接将签章文件保存在此地址，不再返回签章后文档数据
        requestBody.setOutputFilepath("");
        // 时间戳方式，默认为0；0：实时访问CFCA 时间戳服务；1：使用从CFCA购置并在本地部署的时间戳服务器产品；
        requestBody.setTimestampChannel(cfca.paperless.base.BaseConstants.TIME_STAMP_CHANNEL_CFCA);

        //所属业务类型编码 需要在Manager上先维护业务类型 可为空
        requestBody.setBizTypeCode("");

        /************************ 构造机构章策略 ********************************************/
        SealStrategy sealStrategy = generateSealStrategy();

        requestBody.setSealStrategy(sealStrategy);

        sealPdfRequest.setBody(requestBody);

        /************************ 请求签章 ********************************************/

        ResponseDto responseDto = paperlessClient.execute(sealPdfRequest);

        /************************ 解析响应结果 *********************************/

        SealPdfResponse sealPdfResponse = (SealPdfResponse) responseDto;

        ResponseHead responseHead = sealPdfResponse.getHead();
        SealPdfResponseBody responseBody = sealPdfResponse.getBody();

        if (ClientConstants.CODE_SUCCESS.equals(responseHead.getCode())) {

            String resultOutputFilepath = responseBody.getOutputFilepath();

            byte[] pdf = responseBody.getPdf();
            System.out.println(pdf == null ? 0 : pdf.length);

            String resultPdfFileDataHash = responseBody.getPdfFileDataHash();
            System.out.println("resultPdfFileDataHash=" + resultPdfFileDataHash);

            if (StringUtil.isNotEmpty(resultOutputFilepath)) {
                System.out.println("文件已输出到：" + resultOutputFilepath);
            } else {
                resultOutputFilepath = uploadFileAddr+"/" + cfca.paperless.base.util.GUIDUtil.generateId()+ ".pdf";
                IoUtil.write(resultOutputFilepath, pdf);
                System.out.println("文件已输出到：" + resultOutputFilepath);
            }
            return resultOutputFilepath;
        } else {
            System.out.println(functionName + "  NG,Code:" + responseHead.getCode() + ",Message:" + responseHead.getMessage());
            return null;
        }
    }

    /**
     *
     * @Description: 构造机构章签章策略 使用 ：1-印章绑定的图片；
     * @return
     * @throws Exception
     * @return: SealStrategy
     */
    private SealStrategy generateSealStrategy() throws Exception {
        PwdEncryptUtil pwdEncryptUtil=new PwdEncryptUtil();
        // 使用印章绑定图片签章
        String type = BaseConstants.SEAL_TYPE_SEAL;
        String sealCode = "yz2022072001986274";
        String sealPassword = pwdEncryptUtil.encrypto("Yh888#888");
        SealStrategy sealStrategy = new SealStrategy();

        sealStrategy.setSealInfo(type, sealCode, sealPassword);

        // 算法，非空
        String hashAlg = BaseConstants.HASHALG_SHA256;
        sealStrategy.setHashAlg(hashAlg);
        // 透明度，0-1.0f，默认1.0f,不透明
        String fillOpacity = "1";
        sealStrategy.setFillOpacity(fillOpacity);
        // 是否显示，默认1 显示
        String visible = "1";
        sealStrategy.setVisible(visible);
        //签章图片显示尺寸 单位毫米  默认为图片本身尺寸
        //签章图片显示尺寸，单位毫米；如果是圆形，则为半径实际尺寸；如果是矩形，则为较长的一边的实际尺寸；如果是不规则图形，则为较长的一边的实际尺寸

        String displaySize = "30";

        sealStrategy.setDisplaySize(displaySize);



      /*  // 业务码 businessCode ，businessCode为空，则不添加业务码，业务码相关属性都不需要设置 ;
        // 业务码内容
        String businessCode = "2020-12-22";
        // 业务码字体大小，默认12，可以为空
        String businessFontSize = "20";
        // 以图片底部为起点，向上占图片百分之多少的位置开始显示业务码，默认0.5；
        //textRectHeightPercent*imageHeight 不能小于 10
        String textRectHeightPercent = "0.5";
        // 颜色值，默认黑色 000000
        String businessColor = "000000";
        // 字体，默认宋体，可以为空
        String bussinessFamily = "SimSun-ExtB";
        // 业务码 end
        sealStrategy.setBusinessInfo(businessCode, businessFontSize, textRectHeightPercent, businessColor, bussinessFamily);
         */
        // 签章杂项信息
        // 签章人姓名，必填
        String sealPerson = "王小薇";
        // 签章地点，可以为空
        String sealLocation = "北京朝阳";
        // 签章原因，可以为空
        String sealReason = "银行账户开立";
        sealStrategy.setSealMiscInfo(sealPerson, sealLocation, sealReason);


        // businessCode 业务码和图片下方增加日期不能同时添加，
        // 默认按照"yyyy年MM月dd日"日期格式添加当前日期，addDateText不为空时采用上送值添加到图片下方
       /* String addDateText = "";
        // 字体颜色，默认为黑色，可以为空
        String dateFontColor = "FF0000";
        // 字体，默认宋体，可以为空
        String dateFontFamily = "MicrosoftYaHei";
        // 字体大小，默认12，可以为空
        String dateFontSize = "8";
        // 在图片下方扩展的高度，默认40，可以为空;建议值：比字号的两倍大一些；最小值不能小于10，最大值不能高于图片的高度
        String dateRectHeight = "";
        sealStrategy.setAddDateTextInfo(dateFontColor, dateFontFamily, dateFontSize, dateRectHeight);   */

        /************************ 坐标签章 ****************************************************/
        // 签章页码
        String page = "1";
        // x轴坐标
        String lx = "400";
        // y轴坐标
        String ly = "300";
        // 坐标签章
        sealStrategy.setSignWithCoordinate(page, lx, ly);
        /*
        List<SignLocation> signLocationList = new ArrayList<SignLocation>();
        SignLocation signLocation1 = new SignLocation(page,lx,ly);
        signLocationList.add(signLocation1);
        SignLocation signLocation2 = new SignLocation("3",lx,ly);
        signLocationList.add(signLocation2);
        sealStrategy.setSignLocationList(signLocationList);*/

        /************************ 关键字签章 ****************************************************/
        //关键字
//        String keyword ="银行签章";
        //x轴偏移，默认0
//        String offSetX = "60";
        //y轴偏移，默认0
//        String offSetY = "-60";

//        String pageNo = "1";

//        sealStrategy.setSignWithKeywordInfo(keyword, offSetX, offSetY);

        // sealStrategy.setPageNo("2");

       /* List<KeywordLocation> keywordLocationList = new ArrayList<KeywordLocation>();

        KeywordLocation keywordLocation1 = new KeywordLocation(pageNo,keyword,offSetX,offSetY);

        keyword = "个人账户";
        KeywordLocation keywordLocation2 = new KeywordLocation(keyword);

        keywordLocationList.add(keywordLocation1);
        keywordLocationList.add(keywordLocation2);

        sealStrategy.setKeywordLocationList(keywordLocationList);*/

        //sealStrategy.setKeywordPositionIndex("1");


        /************************ 空白域签章 ****************************************************/


        //空白域名称
      /*  String signatureFieldName = "SQR";
        sealStrategy.setSignWithBlankField(signatureFieldName); */

        return sealStrategy;
    }

}