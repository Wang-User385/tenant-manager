//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;



import cfca.paperless.base.client.bean.PaperSize;
import cfca.paperless.base.enums.IdType;
import cfca.paperless.base.util.*;
import cfca.paperless.base.util.validity.ValidityUtil;
import cfca.paperless.client.util.CommonUtil;
import cfca.paperless.dto.RequestHead;
import cfca.paperless.dto.bean.CircleImageStrategy;
import cfca.paperless.dto.bean.CrossPdfStrategy;
import cfca.paperless.dto.bean.CrossSealStrategy;
import cfca.paperless.dto.bean.CrossStrategy;
import cfca.paperless.dto.bean.FieldBean;
import cfca.paperless.dto.bean.ImageBean;
import cfca.paperless.dto.bean.ImageInfoBean;
import cfca.paperless.dto.bean.KeywordLocation;
import cfca.paperless.dto.bean.MultiDataBean;
import cfca.paperless.dto.bean.OfdSealStrategy;
import cfca.paperless.dto.bean.Organization;
import cfca.paperless.dto.bean.PdfBean;
import cfca.paperless.dto.bean.PdfHashBean;
import cfca.paperless.dto.bean.ProofSealStrategy;
import cfca.paperless.dto.bean.RectangleImageStrategy;
import cfca.paperless.dto.bean.SealCertBean;
import cfca.paperless.dto.bean.SealInfoBean;
import cfca.paperless.dto.bean.SealStrategy;
import cfca.paperless.dto.bean.SignInfoBean;
import cfca.paperless.dto.bean.SignLocation;
import cfca.paperless.dto.bean.SquareImageStrategy;
import cfca.paperless.dto.bean.TextBean;
import cfca.paperless.dto.bean.WaterMarkStrategy;
import cfca.paperless.dto.request.requestbody.tx10.AddCrossToPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.AddOrganizationRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.AddWaterMarkToPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.ConcatPdfListRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.DownloadTemplateRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.SynthesizeBusinessDataRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.SynthesizeMultiData2OfdRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.SynthesizeMultiDataRequestBody;
import cfca.paperless.dto.request.requestbody.tx10.UploadTemplateRequestBody;
import cfca.paperless.dto.request.requestbody.tx20.DeleteSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx20.MakeCircleSealImageRequestBody;
import cfca.paperless.dto.request.requestbody.tx20.MakeRectangleSealImageRequestBody;
import cfca.paperless.dto.request.requestbody.tx20.MakeSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx20.MakeSquareSealImageRequestBody;
import cfca.paperless.dto.request.requestbody.tx20.UpdateSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx30.TransformHtmlToPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx30.TransformImageToPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx30.TransformPdfToImageRequestBody;
import cfca.paperless.dto.request.requestbody.tx30.TransformWordToPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.ApplyAndDownloadCertRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.CalculatePdfHashRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.CompoundSealPdfListRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.CompoundSealPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.CrossPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.MessageSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.RevokePdfSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SealOfdRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SealPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SignPdfHashRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SignPdfProofHashRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SynthesizeOuterSignatureAndSealPdfRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.SynthesizeOuterSignatureRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.VerifyMessageSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.VerifyOfdSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.VerifyPdfSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.VerifyWebSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx40.WebSealRequestBody;
import cfca.paperless.dto.request.requestbody.tx50.CheckNetWorkConnRequestBody;
import cfca.paperless.dto.request.requestbody.tx50.GetP10AmountRequestBody;
import cfca.paperless.dto.request.requestbody.tx50.GetSceneCertAmountRequestBody;
import cfca.paperless.dto.request.requestbody.tx50.HeartBeatRequestBody;
import cfca.paperless.dto.request.requestbody.tx60.DownloadProofFileRequestBody;
import cfca.paperless.dto.request.requestbody.tx60.QueryProofInfoListRequestBody;
import cfca.paperless.dto.request.requestbody.tx80.SynthesizeAndCompoundSealPdfRequestBody;
import org.docx4j.wml.P;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Component
public class ValidateUtil {
    public  final Logger logger = LoggerFactory.getLogger(ValidateUtil.class);
    private ProofHashUtil proofHashUtil=new ProofHashUtil();
    public ValidateUtil() {
    }

    public  void checkRequestHead(RequestHead requestHead) throws Exception {
        if (CommonUtil.isEmpty(requestHead.getTransactionNo())) {
            logger.error("transactionNo is null");
            throw new Exception("transactionNo is null");
        } else if (CommonUtil.isEmpty(requestHead.getOrganizationCode())) {
            logger.error("organizationCode is null");
            throw new Exception("organizationCode is null");
        }
    }

    public  void doCheckTx1001(UploadTemplateRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error("inputType 取值错误");
            throw new Exception("inputType取值错误，只能是1或者2");
        } else {
            String templateFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    logger.error("inputSource is null");
                    throw new Exception(" inputSource is null");
                }

                templateFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(templateFileDataHash) && !"docx".equals(templateFileDataHash)) {
                    logger.error("所选文件不是pdf或docx文件");
                    throw new Exception("所选文件不是pdf或docx文件");
                }

                if (!templateFileDataHash.equalsIgnoreCase(requestBody.getTemplateFormat())) {
                    throw new Exception("请检查inputSource与templateFormat是否一致");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getTemplateData() == null) {
                    logger.error(" templateData is null");
                    throw new Exception(" templateData is null");
                }

                if (StringUtil.isEmpty(requestBody.getTemplateFileDataHash())) {
                    templateFileDataHash = proofHashUtil.digestAndHex(requestBody.getTemplateData());
                    requestBody.setTemplateFileDataHash(templateFileDataHash);
                }

                fileDataList.add(requestBody.getTemplateData());
                requestBody.setTemplateData((byte[])null);
            }

            logger.info("inputType={} templateFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getTemplateFileDataHash(), requestBody.getInputSource()});
            if (StringUtil.isEmpty(requestBody.getTemplateCode())) {
                logger.error("templateCode is null");
                throw new Exception("templateCode is null");
            } else if (!StringUtil.validateStrMaxLength(requestBody.getTemplateCode(), 32)) {
                logger.error("templateCode 长度超限，上限32");
                throw new Exception("templateCode 长度超限，上限32");
            } else if (StringUtil.isEmpty(requestBody.getTemplateName())) {
                logger.error("templateName is null");
                throw new Exception("templateName is null");
            } else if (!StringUtil.validateStrMaxLength(requestBody.getTemplateName(), 128)) {
                logger.error("templateName 长度超限，上限128");
                throw new Exception("templateName 长度超限，上限128");
            } else if (!"pdf".equalsIgnoreCase(requestBody.getTemplateFormat()) && !"docx".equalsIgnoreCase(requestBody.getTemplateFormat())) {
                logger.error("templateFormat 取值错误，支持pdf docx 两种类型的模板");
                throw new Exception("templateFormat 取值错误，支持pdf docx 两种类型的模板");
            } else if (!"1".equals(requestBody.getOperationType()) && !"3".equals(requestBody.getOperationType())) {
                throw new Exception("operationType 取值错误");
            }
        }
    }

    public  void doCheckTx1002(DownloadTemplateRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (StringUtil.isEmpty(requestBody.getTemplateCode())) {
            logger.error(" TemplateCode is null");
            throw new Exception(" TemplateCode is null");
        }
    }

    public  void doCheckTx1003(SynthesizeBusinessDataRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType()) && !"3".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2或者3");
            throw new Exception("inputType 取值错误，只能是1或者2或者3");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    logger.error(" inputSource is null");
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error("pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource(), requestBody.getOutputFilepath()});
            List<FieldBean> fieldBeans = requestBody.getFieldBeans();
            List<TextBean> textBeans = requestBody.getTextBeans();
            List<ImageInfoBean> imageBeans = requestBody.getImageBeans();
            checkBusinessData(fieldBeans, textBeans, imageBeans);
        }
    }

    private  void checkBusinessData(List<FieldBean> fieldBeans, List<TextBean> textBeans, List<ImageInfoBean> imageBeans) throws Exception {
        boolean flag = false;
        Iterator var4;
        if (fieldBeans != null && fieldBeans.size() > 0) {
            flag = true;
            var4 = fieldBeans.iterator();

            while(var4.hasNext()) {
                FieldBean fieldBean = (FieldBean)var4.next();
                String fieldType = fieldBean.getFieldType();
                if (!"text".equals(fieldType) && !"checkbox".equals(fieldType)) {
                    logger.error("fieldType 取值错误，文本域类型支持text,checkbox两种类型");
                    throw new Exception("fieldType 取值错误，文本域类型支持text,checkbox两种类型");
                }
            }
        }

        if (textBeans != null && textBeans.size() > 0) {
            flag = true;
            var4 = textBeans.iterator();

            while(var4.hasNext()) {
                TextBean textBean = (TextBean)var4.next();
                if (StringUtil.isEmpty(textBean.getTextValue())) {
                    logger.error("textValue is null");
                    throw new Exception("textValue is null");
                }

                if (!"2".equals(textBean.getType()) && !"3".equals(textBean.getType())) {
                    logger.error("type 取值错误，type取值只能是2或者3");
                    throw new Exception("type 取值错误，type取值只能是2或者3");
                }

                if (StringUtil.isNotEmpty(textBean.getTextFontSize()) && !StringUtil.isNaturalNumber(textBean.getTextFontSize(), 4)) {
                    logger.error("textFontSize 取值错误 ");
                    throw new Exception("textFontSize 取值错误");
                }

                if (StringUtil.isNotEmpty(textBean.getTextColor()) && !StringUtil.isColorNumber(textBean.getTextColor())) {
                    logger.error("textColor 取值错误 ");
                    throw new Exception("textColor 取值错误");
                }

                if ("2".equals(textBean.getType())) {
                    if (StringUtil.isEmpty(textBean.getPageNo())) {
                        logger.error("type=2坐标合成时，pageoNo不能为空 ");
                        throw new Exception("type=2坐标合成时，pageoNo不能为空");
                    }

                    if (!StringUtil.isNaturalNumber(textBean.getPageNo(), 6)) {
                        logger.error("pageoNo 取值错误 ");
                        throw new Exception("pageoNo 取值错误");
                    }

                    if (StringUtil.isEmpty(textBean.getLx())) {
                        textBean.setLx("0");
                    }

                    if (!StringUtil.isPositiveNumeric(textBean.getLx())) {
                        logger.error("lx 取值错误 ");
                        throw new Exception("lx 取值错误");
                    }

                    if (StringUtil.isEmpty(textBean.getLy())) {
                        textBean.setLy("0");
                    }

                    if (!StringUtil.isPositiveNumeric(textBean.getLy())) {
                        logger.error("ly 取值错误 ");
                        throw new Exception("ly 取值错误");
                    }
                } else if ("3".equals(textBean.getType())) {
                    if (StringUtil.isEmpty(textBean.getKeyword())) {
                        logger.error("type=3关键字合成时，keyword不能为空 ");
                        throw new Exception("type=3关键字合成时，keyword不能为空");
                    }

                    if (StringUtil.isEmpty(textBean.getPageNo())) {
                        textBean.setPageNo("1");
                    }

                    if (!StringUtil.isNaturalNumber(textBean.getPageNo(), 6)) {
                        logger.error("pageoNo 取值错误 ");
                        throw new Exception("pageoNo 取值错误");
                    }

                    if (StringUtil.isEmpty(textBean.getOffsetX())) {
                        textBean.setOffsetX("0");
                    }

                    if (!StringUtil.isNumeric(textBean.getOffsetX())) {
                        logger.error("offsetX 取值错误 ");
                        throw new Exception("offsetX 取值错误");
                    }

                    if (StringUtil.isEmpty(textBean.getOffsetY())) {
                        textBean.setOffsetY("0");
                    }

                    if (!StringUtil.isNumeric(textBean.getOffsetY())) {
                        logger.error("offsetY 取值错误 ");
                        throw new Exception("offsetY 取值错误");
                    }
                }
            }
        }

        if (imageBeans != null && imageBeans.size() > 0) {
            flag = true;
            int i = 1;
            Iterator var9 = imageBeans.iterator();

            while(var9.hasNext()) {
                ImageInfoBean imageBean = (ImageInfoBean)var9.next();
                if (!"1".equals(imageBean.getImageType()) && !"2".equals(imageBean.getImageType())) {
                    logger.error("inputType 取值错误，inputType取值只能是1或者2");
                    throw new Exception("inputType 取值错误，inputType取值只能是1或者2");
                }

                if (StringUtil.isEmpty(imageBean.getImageSource())) {
                    logger.error("第" + (i + 1) + "imageSource is null");
                    throw new Exception("第" + (i + 1) + " imageSource is null");
                }

                if (!"2".equals(imageBean.getType()) && !"3".equals(imageBean.getType())) {
                    logger.error("type取值错误，type取值只能是2或者3");
                    throw new Exception("type取值错误，type取值只能是2或者3");
                }

                if (StringUtil.isEmpty(imageBean.getIsCenterCoordinate())) {
                    imageBean.setIsCenterCoordinate("0");
                }

                if (StringUtil.isEmpty(imageBean.getHeight())) {
                    imageBean.setHeight("0");
                }

                if (StringUtil.isEmpty(imageBean.getWidth())) {
                    imageBean.setWidth("0");
                }

                if ("2".equals(imageBean.getType())) {
                    if (StringUtil.isEmpty(imageBean.getPageNo())) {
                        logger.error("type=2坐标合成时，pageoNo不能为空 ");
                        throw new Exception("type=2坐标合成时，pageoNo不能为空");
                    }

                    if (!StringUtil.isNaturalNumber(imageBean.getPageNo(), 6)) {
                        logger.error("pageoNo 取值错误 ");
                        throw new Exception("pageoNo 取值错误");
                    }

                    if (StringUtil.isEmpty(imageBean.getLx())) {
                        imageBean.setLx("0");
                    }

                    if (!StringUtil.isPositiveNumeric(imageBean.getLx())) {
                        logger.error("lx 取值错误 ");
                        throw new Exception("lx 取值错误");
                    }

                    if (StringUtil.isEmpty(imageBean.getLy())) {
                        imageBean.setLy("0");
                    }

                    if (!StringUtil.isPositiveNumeric(imageBean.getLy())) {
                        logger.error("ly 取值错误 ");
                        throw new Exception("ly 取值错误");
                    }
                } else if ("3".equals(imageBean.getType())) {
                    if (StringUtil.isEmpty(imageBean.getKeyword())) {
                        logger.error("type值为3坐标签章时，keyword不能为空");
                        throw new Exception("type值为3坐标签章时，keyword不能为空");
                    }

                    if (StringUtil.isEmpty(imageBean.getPageNo())) {
                        imageBean.setPageNo("0");
                    }

                    if (!StringUtil.isNaturalNumber(imageBean.getPageNo(), 6)) {
                        logger.error("pageoNo 取值错误 ");
                        throw new Exception("pageoNo 取值错误");
                    }

                    if (StringUtil.isEmpty(imageBean.getOffsetX())) {
                        imageBean.setOffsetX("0");
                    }

                    if (!StringUtil.isNumeric(imageBean.getOffsetX())) {
                        logger.error("offsetX 取值错误 ");
                        throw new Exception("offsetX 取值错误");
                    }

                    if (StringUtil.isEmpty(imageBean.getOffsetY())) {
                        imageBean.setOffsetY("0");
                    }

                    if (!StringUtil.isNumeric(imageBean.getOffsetY())) {
                        logger.error("offsetY 取值错误 ");
                        throw new Exception("offsetY 取值错误");
                    }
                }
            }
        }

        if (!flag) {
            logger.error("fieldBeans 、 textBeans 、 imageBeans all null");
            throw new Exception("fieldBeans 、 textBeans 、 imageBeans all null");
        }
    }

    public  void doCheckTx1004(SynthesizeMultiDataRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType()) && !"3".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2或者3");
            throw new Exception("inputType 取值错误，只能是1或者2或者3");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            List<MultiDataBean> multiDataBeans = requestBody.getMultiDataBeans();
            if (multiDataBeans != null && multiDataBeans.size() != 0) {
                Iterator var3 = multiDataBeans.iterator();

                MultiDataBean multiDataBean;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    multiDataBean = (MultiDataBean)var3.next();
                    if (StringUtil.isEmpty(multiDataBean.getFileName())) {
                        logger.error(" fileName is null");
                        throw new Exception(" fileName is null");
                    }

                    if (!StringUtil.validateStrMaxLength(multiDataBean.getFileName(), 128)) {
                        logger.error("fileName 长度超过128");
                        throw new Exception("fileName 长度超过128");
                    }

                    if (StringUtil.isEmpty(multiDataBean.getFileData())) {
                        logger.error(" fileData is null");
                        throw new Exception(" fileData is null");
                    }
                } while(!StringUtil.isEmpty(multiDataBean.getFileDataHash()));

                logger.error(" fileDataHash is null");
                throw new Exception(" fileDataHash is null");
            } else {
                logger.error(" multiDatas is null");
                throw new Exception(" multiDatas is null");
            }
        }
    }

    public  void doCheckTx1005(ConcatPdfListRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        List<PdfBean> pdfBeans = requestBody.getPdfBeans();
        if (pdfBeans != null && pdfBeans.size() >= 2) {
            Iterator var3 = pdfBeans.iterator();

            while(var3.hasNext()) {
                PdfBean bean = (PdfBean)var3.next();
                if (!"1".equals(bean.getInputType()) && !"2".equals(bean.getInputType())) {
                    logger.error(" inputType 取值错误，只能是1或者2");
                    throw new Exception("inputType 取值错误，只能是1或者2");
                }

                if ("1".equals(bean.getInputType())) {
                    if (StringUtil.isEmpty(bean.getInputSource())) {
                        throw new Exception(" inputSource is null");
                    }

                    String pdfSuffix = IoUtil.getFileNameSuffix(bean.getInputSource());
                    if (!"pdf".equals(pdfSuffix)) {
                        logger.error(" 所选文件不是pdf文件");
                        throw new Exception("所选文件不是pdf文件");
                    }
                } else if ("2".equals(bean.getInputType())) {
                    byte[] pdfData = bean.getPdfData();
                    if (pdfData == null) {
                        logger.error(" pdfData is null");
                        throw new Exception(" pdfData is null");
                    }

                    if (StringUtil.isEmpty(bean.getPdfFileDataHash())) {
                        String pdfFileDataHash = proofHashUtil.digestAndHex(bean.getPdfData());
                        bean.setPdfFileDataHash(pdfFileDataHash);
                    }

                    fileDataList.add(pdfData);
                    bean.setPdfData((byte[])null);
                }
            }

        } else {
            logger.error(" pdfBeans 至少需要包含两个pdf文档");
            throw new Exception("pdfBeans 至少需要包含两个pdf文档");
        }
    }

    public  void doCheckTx1006(AddWaterMarkToPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    logger.error("inputSource is null");
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            WaterMarkStrategy waterMarkStrategy = requestBody.getWaterMarkStrategy();
            if (waterMarkStrategy == null) {
                logger.error("waterMarkStrategy is null");
                throw new Exception(" waterMarkStrategy is null ");
            } else if (!StringUtil.isNaturalNumber(waterMarkStrategy.getFromPage(), 6)) {
                logger.error("fromPage 取值错误");
                throw new Exception(" fromPage 取值错误,fromPage不能为空，且必须是大于等于0的正整数");
            } else if (!StringUtil.isNaturalNumber(waterMarkStrategy.getToPage(), 6)) {
                logger.error("toPage 取值错误");
                throw new Exception(" toPage 取值错误 ,toPage不能为空，且必须是大于等于0的正整数");
            } else {
                int fromPage = Integer.parseInt(waterMarkStrategy.getFromPage());
                int toPage = Integer.parseInt(waterMarkStrategy.getToPage());
                if (fromPage > toPage) {
                    logger.error("frompage 取值 大于 toPage ");
                    throw new Exception(" frompage 取值 不应大于 toPage ");
                } else if (!StringUtil.isPositiveNumeric(waterMarkStrategy.getFitHeight())) {
                    logger.error(" fitHeight 取值 错误 ");
                    throw new Exception(" fitHeight 取值 错误 ");
                } else if (!StringUtil.isPositiveNumeric(waterMarkStrategy.getFitWidth())) {
                    logger.error(" fitWidth 取值 错误 ");
                    throw new Exception(" fitWidth 取值 错误 ");
                } else {
                    if (StringUtil.isEmpty(waterMarkStrategy.getAbsoluteX())) {
                        waterMarkStrategy.setAbsoluteX("0");
                    }

                    if (!StringUtil.isPositiveNumeric(waterMarkStrategy.getAbsoluteX())) {
                        logger.error(" absoluteX 取值 错误 ");
                        throw new Exception(" absoluteX 取值 错误 ");
                    } else {
                        if (StringUtil.isEmpty(waterMarkStrategy.getAbsoluteY())) {
                            waterMarkStrategy.setAbsoluteY("0");
                        }

                        if (!StringUtil.isPositiveNumeric(waterMarkStrategy.getAbsoluteY())) {
                            logger.error(" absoluteX 取值 错误 ");
                            throw new Exception(" absoluteX 取值 错误 ");
                        } else {
                            if (StringUtil.isEmpty(waterMarkStrategy.getApartX())) {
                                waterMarkStrategy.setApartX("-1");
                            }

                            if (!StringUtil.isNumeric(waterMarkStrategy.getApartX())) {
                                logger.error(" apartX 取值 错误 ");
                                throw new Exception(" apartX 取值 错误 ");
                            } else {
                                if (StringUtil.isEmpty(waterMarkStrategy.getApartY())) {
                                    waterMarkStrategy.setApartY("-1");
                                }

                                if (!StringUtil.isNumeric(waterMarkStrategy.getApartY())) {
                                    logger.error(" apartY 取值 错误 ");
                                    throw new Exception(" apartY 取值 错误 ");
                                } else if (StringUtil.isNotEmpty(waterMarkStrategy.getColor()) && !StringUtil.isColorNumber(waterMarkStrategy.getColor())) {
                                    logger.error(" color 取值 错误 ");
                                    throw new Exception(" color 取值 错误 ");
                                } else {
                                    if (StringUtil.isEmpty(waterMarkStrategy.getFontSize())) {
                                        waterMarkStrategy.setFontSize("12");
                                    }

                                    if (!StringUtil.isPositiveNumber(waterMarkStrategy.getFontSize(), 6)) {
                                        logger.error(" fontSize 取值 错误 ");
                                        throw new Exception(" fontSize 取值 错误 ");
                                    } else {
                                        if (StringUtil.isEmpty(waterMarkStrategy.getRotationDegree())) {
                                            waterMarkStrategy.setRotationDegree("0");
                                        }

                                        if (!StringUtil.isPositiveNumeric(waterMarkStrategy.getRotationDegree())) {
                                            logger.error(" rotationDegree 取值 错误 ");
                                            throw new Exception(" rotationDegree 取值 错误 ");
                                        } else {
                                            if (StringUtil.isEmpty(waterMarkStrategy.getIsAboveText())) {
                                                waterMarkStrategy.setIsAboveText("0");
                                            }

                                            if (CommonUtil.isEmpty(waterMarkStrategy.getWaterMarkImage()) && CommonUtil.isEmpty(waterMarkStrategy.getWaterMarkText())) {
                                                logger.error("waterMarkImage and waterMarkText both null");
                                                throw new Exception(" waterMarkImage and waterMarkText both null ");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx1007(AddCrossToPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource(), requestBody.getOutputFilepath()});
            CrossStrategy crossStrategy = requestBody.getCrossStrategy();
            if (crossStrategy == null) {
                logger.error("crossSealStrategy  is null");
                throw new Exception("crossSealStrategy is null");
            } else if (StringUtil.isEmpty(crossStrategy.getImage())) {
                logger.error("image  is null");
                throw new Exception(" image   is null");
            } else if (!"5".equals(crossStrategy.getCrossStyle()) && !"6".equals(crossStrategy.getCrossStyle()) && !"7".equals(crossStrategy.getCrossStyle())) {
                logger.error("crossStyle 取值错误");
                throw new Exception("crossStyle 取值错误");
            } else if (!StringUtil.isNaturalNumber(crossStrategy.getFromPage(), 6)) {
                logger.error("fromPage 取值错误");
                throw new Exception(" fromPage 取值错误 ");
            } else {
                if ("0".equals(crossStrategy.getFromPage())) {
                    crossStrategy.setToPage("0");
                }

                if (!StringUtil.isNaturalNumber(crossStrategy.getToPage(), 6)) {
                    logger.error("toPage 取值错误");
                    throw new Exception(" toPage 取值错误 ");
                } else {
                    int fromPage = Integer.parseInt(crossStrategy.getFromPage());
                    int toPage = Integer.parseInt(crossStrategy.getToPage());
                    if (fromPage > toPage) {
                        logger.error("frompage 取值 大于 toPage ");
                        throw new Exception(" frompage 取值 大于 toPage ");
                    } else {
                        if ("5".equals(crossStrategy.getCrossStyle()) || "6".equals(crossStrategy.getCrossStyle())) {
                            crossStrategy.setAbsoluteX("0");
                        }

                        if ("7".equals(crossStrategy.getCrossStyle())) {
                            crossStrategy.setAbsoluteY("0");
                        }

                        if (StringUtil.isEmpty(crossStrategy.getAbsoluteX())) {
                            crossStrategy.setAbsoluteX("0");
                        }

                        if (!StringUtil.isPositiveNumeric(crossStrategy.getAbsoluteX())) {
                            logger.error("absoluteX取值错误");
                            throw new Exception("absoluteX取值错误");
                        } else {
                            if (StringUtil.isEmpty(crossStrategy.getAbsoluteY())) {
                                crossStrategy.setAbsoluteY("0");
                            }

                            if (!StringUtil.isPositiveNumeric(crossStrategy.getAbsoluteY())) {
                                logger.error("absoluteY取值错误");
                                throw new Exception("absoluteY取值错误");
                            }
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx1008(AddOrganizationRequestBody requestBody) throws Exception {
        List<Organization> orgList = requestBody.getOrganizationList();
        if (orgList != null && orgList.size() != 0) {
            int n = orgList.size();
            if (n > 100) {
                logger.error("请一次添加机构数量不要超过100个");
                throw new Exception("请一次添加机构数量不要超过100个");
            } else {
                Iterator var3 = orgList.iterator();

                while(var3.hasNext()) {
                    Organization org = (Organization)var3.next();
                    String code = org.getCode();
                    if (!StringUtil.isNotEmptyAndNull(code)) {
                        logger.error("code 为空 ");
                        throw new Exception("code 为空 ");
                    }

                    if (!StringUtil.validateStrMaxLength(code, 32)) {
                        logger.error("code 长度超过32");
                        throw new Exception("code 长度超过32");
                    }

                    String name = org.getName();
                    if (!StringUtil.isNotEmptyAndNull(name)) {
                        logger.error("name is null ");
                        throw new Exception("name is null ");
                    }

                    if (!StringUtil.validateStrMaxLength(name, 128)) {
                        logger.error("name 长度超过128");
                        throw new Exception("name 长度超过128");
                    }

                    if (!StringUtil.isNotEmptyAndNull(org.getIdentificationType())) {
                        logger.error("identificationType is null");
                        throw new Exception("identificationType is null");
                    }

                    if (!StringUtil.isNotEmptyAndNull(IdType.getNameByCode(org.getIdentificationType()))) {
                        throw new Exception("identificationType 取值错误");
                    }

                    if (!StringUtil.isNotEmptyAndNull(org.getIdentificationNo())) {
                        logger.error("identificationNo is null");
                        throw new Exception("identificationNo is null ");
                    }

                    if (!StringUtil.validateStrMaxLength(org.getIdentificationNo(), 80)) {
                        logger.error("identificationNo 长度超过80");
                        throw new Exception("identificationNo 长度超过80");
                    }

                    if (StringUtil.isNotEmpty(org.getPhoneNo()) && !StringUtil.validateStrMaxLength(org.getPhoneNo(), 20)) {
                        logger.error("phoneNo 长度超过20");
                        throw new Exception("phoneNo 长度超过20");
                    }

                    if (!StringUtil.isNotEmptyAndNull(org.getCertPeriod())) {
                        org.setCertPeriod("0");
                    }

                    if (!StringUtil.isNaturalNumber(org.getCertPeriod(), 5)) {
                        logger.error("certPeriod 取值错误");
                        throw new Exception(" certPeriod 取值错误 ");
                    }

                    if (!StringUtil.isNotEmptyAndNull(org.getSceneCertPeriod())) {
                        org.setSceneCertPeriod("0");
                    }

                    if (!StringUtil.isNaturalNumber(org.getSceneCertPeriod(), 5)) {
                        logger.error("sceneCertPeriod 取值错误");
                        throw new Exception(" sceneCertPeriod 取值错误 ");
                    }

                    if (StringUtil.isNotEmptyAndNull(org.getHostWhiteList()) && !StringUtil.validateStrMaxLength(org.getHostWhiteList(), 512)) {
                        logger.error("hostWhiteList 长度超过 512");
                        throw new Exception("hostWhiteList 长度超过 512");
                    }

                    if (!StringUtil.isNotEmptyAndNull(org.getParentId())) {
                        logger.error("parentId 为空 ");
                        throw new Exception("parentId 为空 ");
                    }

                    if (!StringUtil.validateStrMaxLength(org.getParentId(), 32)) {
                        logger.error("parentId 长度超过 32");
                        throw new Exception("parentId 长度超过 32");
                    }

                    if (!StringUtil.isNotEmptyAndNull(org.getTimeStampSupported())) {
                        org.setTimeStampSupported("1");
                    }

                    if (!"0".equals(org.getTimeStampSupported()) && !"1".equals(org.getTimeStampSupported())) {
                        logger.error("timeStampSupported 正确取值为持0 或者 1");
                        throw new Exception("timeStampSupported 正确取值为持0 或者 1");
                    }

                    if (StringUtil.isNotEmptyAndNull(org.getAddress()) && !StringUtil.validateStrMaxLength(org.getAddress(), 64)) {
                        logger.error("address 长度超过 64");
                        throw new Exception("address 长度超过 64");
                    }

                    if (StringUtil.isNotEmptyAndNull(org.getProvince()) && !StringUtil.validateStrMaxLength(org.getProvince(), 32)) {
                        logger.error("province 长度超过 32");
                        throw new Exception("province 长度超过 32");
                    }

                    if (StringUtil.isNotEmptyAndNull(org.getCity()) && !StringUtil.validateStrMaxLength(org.getCity(), 32)) {
                        logger.error("city 长度超过 32");
                        throw new Exception("city 长度超过 32");
                    }

                    if (StringUtil.isNotEmptyAndNull(org.getEmail()) && !StringUtil.validateStrMaxLength(org.getEmail(), 80)) {
                        logger.error("email 长度超过 80");
                        throw new Exception("email 长度超过 80");
                    }

                    if (StringUtil.isNotEmptyAndNull(org.getRemark1()) && !StringUtil.validateStrMaxLength(org.getRemark1(), 100)) {
                        logger.error("remark 长度超过 100");
                        throw new Exception("remark 长度超过 100");
                    }

                    if (StringUtil.isNotEmpty(org.getNotBefore())) {
                        try {
                            TimeUtil.getDateInFormat(org.getNotBefore(), "yyyy-MM-dd HH:mm:ss");
                        } catch (Exception var9) {
                            logger.error("notBefore 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                            throw new Exception("notBefore 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                        }
                    }

                    if (StringUtil.isNotEmpty(org.getNotAfter())) {
                        try {
                            TimeUtil.getDateInFormat(org.getNotAfter(), "yyyy-MM-dd HH:mm:ss");
                        } catch (Exception var8) {
                            logger.error("notAfter 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                            throw new Exception("notAfter 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                        }
                    }
                }

            }
        } else {
            logger.error("organizationList is null");
            throw new Exception("organizationList is null");
        }
    }

    public  void doCheckTx1009(Organization org) throws Exception {
        if (org == null) {
            logger.error("organization  is null");
            throw new Exception("organization  is null");
        } else {
            String code = org.getCode();
            if (!StringUtil.isNotEmptyAndNull(code)) {
                logger.error("code 为空 ");
                throw new Exception("code 为空 ");
            } else if (!StringUtil.validateStrMaxLength(code, 32)) {
                logger.error("code 长度超过32");
                throw new Exception("code 长度超过32");
            } else {
                String name = org.getName();
                if (StringUtil.isNotEmpty(name) && !StringUtil.validateStrMaxLength(name, 128)) {
                    logger.error("name 长度超过128");
                    throw new Exception("name 长度超过128");
                } else if (StringUtil.isNotEmpty(org.getIdentificationType()) && !StringUtil.isNotEmptyAndNull(IdType.getNameByCode(org.getIdentificationType()))) {
                    throw new Exception("identificationType 取值错误");
                } else if (StringUtil.isNotEmpty(org.getIdentificationNo()) && !StringUtil.validateStrMaxLength(org.getIdentificationNo(), 80)) {
                    logger.error("identificationNo 长度超过80");
                    throw new Exception("identificationNo 长度超过80");
                } else if (StringUtil.isNotEmpty(org.getPhoneNo()) && !StringUtil.validateStrMaxLength(org.getPhoneNo(), 20)) {
                    logger.error("phoneNo 长度超过20");
                    throw new Exception("phoneNo 长度超过20");
                } else if (StringUtil.isNotEmpty(org.getCertPeriod()) && !StringUtil.isNaturalNumber(org.getCertPeriod(), 5)) {
                    logger.error("certPeriod 取值错误");
                    throw new Exception(" certPeriod 取值错误 ");
                } else {
                    if (StringUtil.isEmpty(org.getCertPeriod())) {
                        org.setCertPeriod("-1");
                    }

                    if (StringUtil.isNotEmpty(org.getSceneCertPeriod()) && !StringUtil.isNaturalNumber(org.getSceneCertPeriod(), 5)) {
                        logger.error("sceneCertPeriod 取值错误");
                        throw new Exception(" sceneCertPeriod 取值错误 ");
                    } else {
                        if (StringUtil.isEmpty(org.getSceneCertPeriod())) {
                            org.setSceneCertPeriod("-1");
                        }

                        if (StringUtil.isNotEmpty(org.getHostWhiteList()) && !StringUtil.validateStrMaxLength(org.getHostWhiteList(), 512)) {
                            logger.error("hostWhiteList 长度超过 512");
                            throw new Exception("hostWhiteList 长度超过 512");
                        } else if (StringUtil.isNotEmptyAndNull(org.getParentId()) && !StringUtil.validateStrMaxLength(org.getParentId(), 32)) {
                            logger.error("parentId 长度超过 32");
                            throw new Exception("parentId 长度超过 32");
                        } else if (StringUtil.isNotEmpty(org.getTimeStampSupported()) && !"0".equals(org.getTimeStampSupported()) && !"1".equals(org.getTimeStampSupported())) {
                            logger.error("timeStampSupported 正确取值为持0 或者 1");
                            throw new Exception("timeStampSupported 正确取值为持0 或者 1");
                        } else {
                            if (StringUtil.isEmpty(org.getTimeStampSupported())) {
                                org.setTimeStampSupported("-1");
                            }

                            if (StringUtil.isNotEmpty(org.getAddress()) && !StringUtil.validateStrMaxLength(org.getAddress(), 64)) {
                                logger.error("address 长度超过 64");
                                throw new Exception("address 长度超过 64");
                            } else if (StringUtil.isNotEmpty(org.getProvince()) && !StringUtil.validateStrMaxLength(org.getProvince(), 32)) {
                                logger.error("province 长度超过 32");
                                throw new Exception("province 长度超过 32");
                            } else if (StringUtil.isNotEmpty(org.getCity()) && !StringUtil.validateStrMaxLength(org.getCity(), 32)) {
                                logger.error("city 长度超过 32");
                                throw new Exception("city 长度超过 32");
                            } else if (StringUtil.isNotEmpty(org.getEmail()) && !StringUtil.validateStrMaxLength(org.getEmail(), 80)) {
                                logger.error("email 长度超过 80");
                                throw new Exception("email 长度超过 80");
                            } else if (StringUtil.isNotEmpty(org.getRemark1()) && !StringUtil.validateStrMaxLength(org.getRemark1(), 100)) {
                                logger.error("remark 长度超过 100");
                                throw new Exception("remark 长度超过 100");
                            } else {
                                if (StringUtil.isNotEmpty(org.getNotBefore())) {
                                    try {
                                        TimeUtil.getDateInFormat(org.getNotBefore(), "yyyy-MM-dd HH:mm:ss");
                                    } catch (Exception var5) {
                                        logger.error("notBefore 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                                        throw new Exception("notBefore 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                                    }
                                }

                                if (StringUtil.isNotEmpty(org.getNotAfter())) {
                                    try {
                                        TimeUtil.getDateInFormat(org.getNotAfter(), "yyyy-MM-dd HH:mm:ss");
                                    } catch (Exception var4) {
                                        logger.error("notAfter 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                                        throw new Exception("notAfter 时间格式不正确，正确格式：yyyy-MM-dd HH:mm:ss");
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx1010(Organization org) throws Exception {
        String code = org.getCode();
        if (!StringUtil.isNotEmptyAndNull(code)) {
            logger.error("code 为空 ");
            throw new Exception("code 为空 ");
        } else if (!StringUtil.validateStrMaxLength(code, 32)) {
            logger.error("code 长度超过32");
            throw new Exception("code 长度超过32");
        } else if (!StringUtil.isNotEmptyAndNull(org.getIdentificationType())) {
            logger.error("identificationType is null");
            throw new Exception("identificationType is null");
        } else if (!StringUtil.isNotEmptyAndNull(IdType.getNameByCode(org.getIdentificationType()))) {
            throw new Exception("identificationType 取值错误");
        } else if (!StringUtil.isNotEmptyAndNull(org.getIdentificationNo())) {
            logger.error("identificationNo is null");
            throw new Exception("identificationNo is null ");
        } else if (!StringUtil.validateStrMaxLength(org.getIdentificationNo(), 80)) {
            logger.error("identificationNo 长度超过80");
            throw new Exception("identificationNo 长度超过80");
        }
    }

    public  void doCheckTx1011(SynthesizeMultiData2OfdRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType()) && !"3".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2或者3");
            throw new Exception("inputType 取值错误，只能是1或者2或者3");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"ofd".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是ofd文件");
                    throw new Exception("所选文件不是ofd文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getOfdData() == null) {
                    logger.error(" ofdData is null");
                    throw new Exception(" ofdData is null");
                }

                if (StringUtil.isEmpty(requestBody.getOfdFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getOfdData());
                    requestBody.setOfdFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getOfdData());
                requestBody.setOfdData((byte[])null);
            }

            List<MultiDataBean> multiDataBeans = requestBody.getMultiDataBeans();
            if (multiDataBeans != null && multiDataBeans.size() != 0) {
                Iterator var3 = multiDataBeans.iterator();

                MultiDataBean multiDataBean;
                String fileData;
                do {
                    if (!var3.hasNext()) {
                        return;
                    }

                    multiDataBean = (MultiDataBean)var3.next();
                    if (StringUtil.isEmpty(multiDataBean.getFileName())) {
                        logger.error(" fileName is null");
                        throw new Exception(" fileName is null");
                    }

                    if (!StringUtil.validateStrMaxLength(multiDataBean.getFileName(), 128)) {
                        logger.error("fileName 长度超过128");
                        throw new Exception("fileName 长度超过128");
                    }

                    fileData = multiDataBean.getFileData();
                    if (StringUtil.isEmpty(fileData)) {
                        logger.error(" fileData is null");
                        throw new Exception(" fileData is null");
                    }
                } while(fileData.indexOf("url:") != -1 || fileData.indexOf("path:") != -1 || !StringUtil.isEmpty(multiDataBean.getFileDataHash()));

                logger.error(" fileDataHash is null");
                throw new Exception(" fileDataHash is null");
            } else {
                logger.error(" multiDatas is null");
                throw new Exception(" multiDatas is null");
            }
        }
    }

    public  void doCheckTx2001(MakeSquareSealImageRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        SquareImageStrategy squareImageStrategy = requestBody.getImageStrategy();
        if (squareImageStrategy == null) {
            logger.error("imageStrategy is null");
            throw new Exception("imageStrategy is null");
        } else if (StringUtil.isEmpty(squareImageStrategy.getImageName())) {
            logger.error("imageName is null");
            throw new Exception("imageName is null");
        } else if (!StringUtil.validateStrMaxLength(squareImageStrategy.getImageName(), 128)) {
            logger.error("imageName 长度超过128");
            throw new Exception("imageName 长度超过128");
        } else if (!"1".equals(squareImageStrategy.getImageShape()) && !"11".equals(squareImageStrategy.getImageShape())) {
            logger.error("imageShape 取值错误");
            throw new Exception("imageShape 取值错误");
        } else {
            if (StringUtil.isEmpty(squareImageStrategy.getImageHeight())) {
                squareImageStrategy.setImageHeight("100");
            }

            if (!StringUtil.isPositiveNumeric(squareImageStrategy.getImageHeight())) {
                logger.error(" imageHeight 取值 错误 ");
                throw new Exception(" imageHeight 取值 错误 ");
            } else {
                if (StringUtil.isEmpty(squareImageStrategy.getImageWidth())) {
                    squareImageStrategy.setImageWidth("100");
                }

                if (!StringUtil.isPositiveNumeric(squareImageStrategy.getImageWidth())) {
                    logger.error(" imageWidth 取值 错误 ");
                    throw new Exception(" imageWidth 取值 错误 ");
                } else {
                    if (StringUtil.isEmpty(squareImageStrategy.getColor())) {
                        squareImageStrategy.setColor("000000");
                    }

                    if (!StringUtil.isColorNumber(squareImageStrategy.getColor())) {
                        logger.error(" color 取值 错误 ");
                        throw new Exception(" color 取值 错误 ");
                    } else {
                        if (StringUtil.isEmpty(squareImageStrategy.getFontSize())) {
                            squareImageStrategy.setFontSize("12");
                        }

                        if (!StringUtil.isPositiveNumber(squareImageStrategy.getFontSize(), 6)) {
                            logger.error(" fontSize 取值 错误 ");
                            throw new Exception(" fontSize 取值 错误 ");
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx2002(MakeRectangleSealImageRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        RectangleImageStrategy rectangleImageStrategy = requestBody.getImageStrategy();
        if (rectangleImageStrategy == null) {
            logger.error("imageStrategy is null");
            throw new Exception("imageStrategy is null");
        } else if (StringUtil.isEmpty(rectangleImageStrategy.getImageName())) {
            logger.error("imageName is null");
            throw new Exception("imageName is null");
        } else if (!StringUtil.validateStrMaxLength(rectangleImageStrategy.getImageName(), 128)) {
            logger.error("imageName 长度超过128");
            throw new Exception("imageName 长度超过128");
        } else if (!"2".equals(rectangleImageStrategy.getImageShape()) && !"21".equals(rectangleImageStrategy.getImageShape())) {
            logger.error("imageShape 取值错误");
            throw new Exception("imageShape 取值错误");
        } else {
            if (StringUtil.isEmpty(rectangleImageStrategy.getImageHeight())) {
                rectangleImageStrategy.setImageHeight("100");
            }

            if (!StringUtil.isPositiveNumeric(rectangleImageStrategy.getImageHeight())) {
                logger.error(" imageHeight 取值 错误 ");
                throw new Exception(" imageHeight 取值 错误 ");
            } else {
                if (StringUtil.isEmpty(rectangleImageStrategy.getImageWidth())) {
                    rectangleImageStrategy.setImageWidth("100");
                }

                if (!StringUtil.isPositiveNumeric(rectangleImageStrategy.getImageWidth())) {
                    logger.error(" imageWidth 取值 错误 ");
                    throw new Exception(" imageWidth 取值 错误 ");
                } else {
                    if (StringUtil.isEmpty(rectangleImageStrategy.getColor())) {
                        rectangleImageStrategy.setColor("000000");
                    }

                    if (!StringUtil.isColorNumber(rectangleImageStrategy.getColor())) {
                        logger.error(" color 取值 错误 ");
                        throw new Exception(" color 取值 错误 ");
                    } else {
                        if (StringUtil.isEmpty(rectangleImageStrategy.getFontSize())) {
                            rectangleImageStrategy.setFontSize("12");
                        }

                        if (!StringUtil.isPositiveNumber(rectangleImageStrategy.getFontSize(), 6)) {
                            logger.error(" fontSize 取值 错误 ");
                            throw new Exception(" fontSize 取值 错误 ");
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx2003(MakeCircleSealImageRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        CircleImageStrategy circleImageStrategy = requestBody.getImageStrategy();
        if (circleImageStrategy == null) {
            logger.error("imageStrategy is null");
            throw new Exception("imageStrategy is null");
        } else if (StringUtil.isEmpty(circleImageStrategy.getImageName())) {
            logger.error("imageName is null");
            throw new Exception("imageName is null");
        } else if (!StringUtil.validateStrMaxLength(circleImageStrategy.getImageName(), 128)) {
            logger.error("imageName 长度超过128");
            throw new Exception("imageName 长度超过128");
        } else if (!"3".equals(circleImageStrategy.getImageShape()) && !"4".equals(circleImageStrategy.getImageShape())) {
            logger.error("imageShape 取值错误");
            throw new Exception("imageShape 取值错误");
        } else {
            if (StringUtil.isEmpty(circleImageStrategy.getUnit())) {
                circleImageStrategy.setUnit("0");
            }

            if (!"0".equals(circleImageStrategy.getUnit()) && !"1".equals(circleImageStrategy.getUnit())) {
                logger.error("unit 取值错误");
                throw new Exception("unit 取值错误，只能是0或者1");
            } else {
                if (StringUtil.isEmpty(circleImageStrategy.getDpi()) || "0".equals(circleImageStrategy.getDpi())) {
                    circleImageStrategy.setDpi("288");
                }

                if (!StringUtil.isNaturalNumber(circleImageStrategy.getDpi())) {
                    logger.error(" dpi 取值 错误 ");
                    throw new Exception(" dpi 取值 错误 ");
                } else {
                    if (StringUtil.isEmpty(circleImageStrategy.getImageHeight())) {
                        circleImageStrategy.setImageHeight("100");
                    }

                    if (!StringUtil.isPositiveNumeric(circleImageStrategy.getImageHeight())) {
                        logger.error(" imageHeight 取值 错误 ");
                        throw new Exception(" imageHeight 取值 错误 ");
                    } else {
                        if (StringUtil.isEmpty(circleImageStrategy.getImageWidth())) {
                            circleImageStrategy.setImageWidth("100");
                        }

                        if (!StringUtil.isPositiveNumeric(circleImageStrategy.getImageWidth())) {
                            logger.error(" imageWidth 取值 错误 ");
                            throw new Exception(" imageWidth 取值 错误 ");
                        } else {
                            if (StringUtil.isEmpty(circleImageStrategy.getFontSize())) {
                                circleImageStrategy.setFontSize("12");
                            }

                            if (!StringUtil.isPositiveNumber(circleImageStrategy.getFontSize(), 6)) {
                                logger.error(" fontSize 取值 错误 ");
                                throw new Exception(" fontSize 取值 错误 ");
                            } else {
                                float rx;
                                float ry;
                                if (StringUtil.isNotEmpty(circleImageStrategy.getImageName2())) {
                                    if (!StringUtil.validateStrMaxLength(circleImageStrategy.getImageName2(), 128)) {
                                        logger.error("imageName2 长度超过128");
                                        throw new Exception("imageName2 长度超过128");
                                    }

                                    if (StringUtil.isEmpty(circleImageStrategy.getImageName2FontSize())) {
                                        circleImageStrategy.setImageName2FontSize("12");
                                    }

                                    if (!StringUtil.isPositiveNumber(circleImageStrategy.getImageName2FontSize(), 6)) {
                                        logger.error(" imageName2FontSize 取值 错误 ");
                                        throw new Exception(" imageName2FontSize 取值 错误 ");
                                    }

                                    if (StringUtil.isEmpty(circleImageStrategy.getImageName2RatioX())) {
                                        circleImageStrategy.setImageName2RatioX("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(circleImageStrategy.getImageName2RatioX())) {
                                        logger.error(" imageName2RatioX 取值 错误 ");
                                        throw new Exception(" imageName2RatioX 取值 错误 ");
                                    }

                                    rx = Float.parseFloat(circleImageStrategy.getImageName2RatioX());
                                    if (rx < 0.0F || rx > 1.0F) {
                                        logger.error(" imageName2RatioX 取值 错误 ");
                                        throw new Exception(" imageName2RatioX 取值 错误 ");
                                    }

                                    if (StringUtil.isEmpty(circleImageStrategy.getImageName2RatioY())) {
                                        circleImageStrategy.setImageName2RatioY("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(circleImageStrategy.getImageName2RatioY())) {
                                        logger.error(" imageName2RatioY 取值 错误 ");
                                        throw new Exception(" imageName2RatioY 取值 错误 ");
                                    }

                                    ry = Float.parseFloat(circleImageStrategy.getImageName2RatioY());
                                    if (ry < 0.0F || ry > 1.0F) {
                                        logger.error(" imageName2RatioY 取值 错误 ");
                                        throw new Exception(" imageName2RatioY 取值 错误 ");
                                    }
                                }

                                if (StringUtil.isNotEmpty(circleImageStrategy.getBusinessCode())) {
                                    if (!StringUtil.validateStrMaxLength(circleImageStrategy.getBusinessCode(), 32)) {
                                        logger.error("businessCode 长度超过32");
                                        throw new Exception("businessCode 长度超过32");
                                    }

                                    if (StringUtil.isEmpty(circleImageStrategy.getBusinessFontSize())) {
                                        circleImageStrategy.setBusinessFontSize("12");
                                    }

                                    if (!StringUtil.isPositiveNumber(circleImageStrategy.getBusinessFontSize(), 6)) {
                                        logger.error(" businessFontSize 取值 错误 ");
                                        throw new Exception(" businessFontSize 取值 错误 ");
                                    }

                                    if (StringUtil.isEmpty(circleImageStrategy.getBusinessRatioX())) {
                                        circleImageStrategy.setBusinessRatioX("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(circleImageStrategy.getBusinessRatioX())) {
                                        logger.error(" businessRatioX 取值 错误 ");
                                        throw new Exception(" businessRatioX 取值 错误 ");
                                    }

                                    rx = Float.parseFloat(circleImageStrategy.getBusinessRatioX());
                                    if (rx < 0.0F || rx > 1.0F) {
                                        logger.error(" businessRatioX 取值 错误 ");
                                        throw new Exception(" businessRatioX 取值 错误 ");
                                    }

                                    if (StringUtil.isEmpty(circleImageStrategy.getBusinessRatioY())) {
                                        circleImageStrategy.setBusinessRatioY("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(circleImageStrategy.getBusinessRatioY())) {
                                        logger.error(" businessRatioY 取值 错误 ");
                                        throw new Exception(" businessRatioY 取值 错误 ");
                                    }

                                    ry = Float.parseFloat(circleImageStrategy.getBusinessRatioY());
                                    if (ry < 0.0F || ry > 1.0F) {
                                        logger.error(" businessRatioY 取值 错误 ");
                                        throw new Exception(" businessRatioY 取值 错误 ");
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx2004(MakeSealRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        String sealImageStr = requestBody.getSealImage();
        if (StringUtil.isEmpty(sealImageStr)) {
            logger.error("sealImage is null");
            throw new Exception(" sealImage is null ");
        } else if (requestBody.getSealImageData() == null) {
            logger.error("sealImageData is null");
            throw new Exception(" sealImageData is null ");
        } else if (!ValidityUtil.checkIsImage(requestBody.getSealImageData())) {
            logger.error("图片数据异常");
            throw new Exception(" 图片数据异常 ");
        } else {
            requestBody.setSealImageData((byte[])null);
            if (StringUtil.isEmpty(requestBody.getSealType())) {
                requestBody.setSealType("1");
            }

            if (!"1".equals(requestBody.getSealType()) && !"2".equals(requestBody.getSealType())) {
                logger.error("sealType 取值错误");
                throw new Exception(" sealType 取值错误");
            } else {
                SealCertBean sealCertBean = requestBody.getSealCert();
                if (sealCertBean == null) {
                    logger.error("sealCert is null");
                    throw new Exception(" sealCert is null");
                } else if (!"1".equals(sealCertBean.getCustomerType()) && !"2".equals(sealCertBean.getCustomerType())) {
                    logger.error("customerType 取值错误");
                    throw new Exception(" customerType 取值错误");
                } else if (!"1".equals(sealCertBean.getType()) && !"2".equals(sealCertBean.getType())) {
                    logger.error("type 取值错误");
                    throw new Exception(" type 取值错误");
                } else if (!"RSA".equals(sealCertBean.getKeyAlg()) && !"SM2".equals(sealCertBean.getKeyAlg())) {
                    logger.error("keyAlg 取值错误");
                    throw new Exception(" keyAlg 取值错误 ");
                } else {
                    if ("RSA".equals(sealCertBean.getKeyAlg())) {
                        if (!"2048".equals(sealCertBean.getKeyLength())) {
                            logger.error("keyLength 取值错误");
                            throw new Exception(" keyLength 取值错误，keyLength仅支持2048");
                        }
                    } else if ("SM2".equals(sealCertBean.getKeyAlg()) && !"256".equals(sealCertBean.getKeyLength())) {
                        logger.error("keyLength 取值错误");
                        throw new Exception(" keyLength 取值错误");
                    }

                    if (StringUtil.isEmpty(sealCertBean.getPkcs12Password())) {
                        logger.error("pkcsPassword is null");
                        throw new Exception(" pkcsPassword is null");
                    } else {
                        if ("1".equals(sealCertBean.getType())) {
                            if (StringUtil.isEmpty(sealCertBean.getPkcs12())) {
                                logger.error("pkcs12 is null");
                                throw new Exception(" pkcs12 is null");
                            }
                        } else if ("2".equals(sealCertBean.getType())) {
                            if (StringUtil.isEmpty(sealCertBean.getUserName())) {
                                logger.error("userName is null");
                                throw new Exception(" userName is null");
                            }

                            if (!StringUtil.validateStrMaxLength(sealCertBean.getUserName(), 128)) {
                                logger.error("userName 长度超过 128");
                                throw new Exception("userName 长度超过 128");
                            }

                            if (StringUtil.isEmpty(sealCertBean.getIdentificationType())) {
                                logger.error("identificationType is null");
                                throw new Exception("identificationType is null");
                            }

                            if (StringUtil.isEmpty(IdType.getNameByCode(sealCertBean.getIdentificationType()))) {
                                throw new Exception("identificationType 取值错误");
                            }

                            if (StringUtil.isEmpty(sealCertBean.getIdentificationNo())) {
                                logger.error("identificationNo is null");
                                throw new Exception("identificationNo is null ");
                            }

                            if (!StringUtil.validateStrMaxLength(sealCertBean.getIdentificationNo(), 80)) {
                                logger.error("identificationNo 长度超过80");
                                throw new Exception("identificationNo 长度超过80");
                            }

                            if (StringUtil.isNotEmpty(sealCertBean.getPhoneNo()) && !StringUtil.validateStrMaxLength(sealCertBean.getPhoneNo(), 20)) {
                                logger.error("phoneNo 长度超过20");
                                throw new Exception("phoneNo 长度超过20");
                            }
                        }

                        SealInfoBean sealInfoBean = requestBody.getSealInfo();
                        if (sealInfoBean == null) {
                            logger.error("sealInfo is null");
                            throw new Exception(" sealInfo is null");
                        } else if (StringUtil.isEmpty(sealInfoBean.getSealCode())) {
                            logger.error("sealCode is null");
                            throw new Exception("sealCode is null ");
                        } else if (!StringUtil.validateStrMaxLength(sealInfoBean.getSealCode(), 32)) {
                            logger.error("sealCode 长度超过 32");
                            throw new Exception("sealCode 长度超过 32");
                        } else if (StringUtil.isEmpty(sealInfoBean.getSealPassword())) {
                            logger.error("sealPassword is null");
                            throw new Exception("sealPassword is null");
                        } else if (!StringUtil.validateStrMaxLength(sealInfoBean.getSealPassword(), 64)) {
                            logger.error("sealPassword 长度超过64");
                            throw new Exception("sealPassword 长度超过64");
                        } else if (StringUtil.isEmpty(sealInfoBean.getSealName())) {
                            logger.error("sealName is null");
                            throw new Exception("sealName  is null");
                        } else if (!StringUtil.validateStrMaxLength(sealInfoBean.getSealName(), 128)) {
                            logger.error("sealName 长度超过128");
                            throw new Exception("sealName 长度超过128");
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx2005(UpdateSealRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"0".equals(requestBody.getSealCertUpdateFlag()) && !"1".equals(requestBody.getSealCertUpdateFlag())) {
            logger.error(" sealCertUpdateFlag 取值错误");
            throw new Exception(" sealCertUpdateFlag 取值错误");
        } else if (StringUtil.isEmpty(requestBody.getSealCode())) {
            logger.error(" sealCode is null");
            throw new Exception("sealCode is null ");
        } else if (!StringUtil.validateStrMaxLength(requestBody.getSealCode(), 32)) {
            logger.error(" sealCode 长度超过32");
            throw new Exception("sealCode 长度超过32");
        } else if (StringUtil.isEmpty(requestBody.getSealPassword())) {
            logger.error(" sealPassword is null");
            throw new Exception("sealPassword is null");
        } else if (StringUtil.isNotEmpty(requestBody.getNewSealPassword()) && !StringUtil.validateStrMaxLength(requestBody.getNewSealPassword(), 64)) {
            logger.error(" newSealPassword 长度超过64");
            throw new Exception("newSealPassword 长度超过64");
        } else if (StringUtil.isNotEmpty(requestBody.getSealName()) && !StringUtil.validateStrMaxLength(requestBody.getSealName(), 128)) {
            logger.error(" sealName 长度超过128");
            throw new Exception("sealName 长度超过128");
        }
    }

    public  void doCheckTx2006(DeleteSealRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (StringUtil.isEmpty(requestBody.getSealCode())) {
            logger.error(" sealCode is null");
            throw new Exception("sealCode is null");
        } else if (!StringUtil.validateStrMaxLength(requestBody.getSealCode(), 32)) {
            logger.error(" sealCode 长度超过 32");
            throw new Exception("sealCode 长度超过 32");
        } else if (StringUtil.isEmpty(requestBody.getSealPassword())) {
            logger.error(" sealPassword is null");
            throw new Exception("sealPassword is null");
        } else if (!"0".equals(requestBody.getRevokeFlag()) && !"1".equals(requestBody.getRevokeFlag()) && !"2".equals(requestBody.getRevokeFlag())) {
            logger.error(" revokeFlag 取值错误");
            throw new Exception(" revokeFlag 取值错误");
        }
    }

    public  void doCheckTx3001(TransformHtmlToPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else if (StringUtil.isEmpty(requestBody.getInputSource())) {
            logger.error(" inputSource is null");
            throw new Exception(" inputSource is null");
        } else {
            try {
                PaperSize.valueOf(PaperSize.class, requestBody.getSize());
            } catch (Exception var3) {
                logger.error(" size 取值错误");
                throw new Exception(" size 取值错误 ");
            }

            if (!"0".equals(requestBody.getLandscape()) && !"1".equals(requestBody.getLandscape())) {
                logger.error(" landscape 取值错误，只能是0或者1");
                throw new Exception("landscape 取值错误，只能是0或者1");
            }
        }
    }

    public  void doCheckTx3002(TransformWordToPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (StringUtil.isEmpty(requestBody.getInputSource())) {
            logger.error(" inputSource is null");
            throw new Exception(" inputSource is null");
        }
        String wordDataStr=requestBody.getInputSource();
        byte[] encode = Base64.decode(wordDataStr.getBytes(PaperlessConfig.DEFAULT_CHARSET));
        fileDataList.add(encode);
    }

    public  void doCheckTx3003(TransformImageToPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        List<ImageBean> imageBeans = requestBody.getImages();
        if (imageBeans != null && imageBeans.size() != 0) {
            int i = 1;

            for(Iterator var4 = imageBeans.iterator(); var4.hasNext(); ++i) {
                ImageBean imageBean = (ImageBean)var4.next();
                if (imageBean == null) {
                    logger.error("第" + i + "张图片数据异常");
                    throw new Exception("第" + i + "张图片数据异常");
                }

                if (!ValidityUtil.checkIsImage(imageBean.getImageData())) {
                    logger.error("第" + i + "张图片数据异常");
                    throw new Exception("第" + i + "张图片数据异常");
                }

                imageBean.setImageData((byte[])null);
            }

        } else {
            logger.error(" images is null");
            throw new Exception(" images is null");
        }
    }

    public  void doCheckTx3004(TransformPdfToImageRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource()});
            if (!StringUtil.isNaturalNumber(requestBody.getPageNo(), 6)) {
                logger.error(" pageNo 取值错误 ");
                throw new Exception(" pageNo 取值错误  ");
            }
        }
    }

    public  void doCheckTx4001(SealPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                String pdfSuffix = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfSuffix)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource(), requestBody.getOutputFilepath()});
            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error(" timestampChannel 取值错误，取值只能是0 或者 1");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else {
                SealStrategy sealStrategy = requestBody.getSealStrategy();
                if (sealStrategy == null) {
                    logger.error(" sealStrategy is null");
                    throw new Exception(" sealStrategy is null");
                } else {
                    checkSealStrategy(sealStrategy);
                }
            }
        }
    }

    public  void doCheckTx4002(CompoundSealPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                String pdfSuffix = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfSuffix)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error(" timestampChannel 取值错误，取值只能是0 或者 1");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else {
                if (StringUtil.isEmpty(requestBody.getSceneCertChannel())) {
                    requestBody.setSceneCertChannel("0");
                }

                if (!"0".equals(requestBody.getSceneCertChannel()) && !"1".equals(requestBody.getSceneCertChannel())) {
                    logger.error(" sceneCertChannel 取值错误，取值只能是0 或者 1");
                    throw new Exception("sceneCertChannel 取值错误，取值只能是0 或者 1");
                } else {
                    boolean flag = false;
                    List<SealStrategy> sealStrategies = requestBody.getSealStrategies();
                    if (sealStrategies != null && sealStrategies.size() > 0) {
                        flag = true;
                        Iterator var4 = sealStrategies.iterator();

                        while(var4.hasNext()) {
                            SealStrategy sealStrategy = (SealStrategy)var4.next();
                            if (StringUtil.isEmpty(sealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(sealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            checkSealStrategy(sealStrategy);
                        }
                    }

                    List<ProofSealStrategy> proofSealStrategies = requestBody.getProofSealStrategies();
                    if (proofSealStrategies != null && proofSealStrategies.size() > 0) {
                        flag = true;
                        List<MultiDataBean> multiDataBeans = requestBody.getMultiDatas();
                        if (multiDataBeans == null || multiDataBeans.size() == 0) {
                            logger.error(" 证据签章时，multiDataBeans 不能为空");
                            throw new Exception("证据签章时，multiDataBeans 不能为空 ");
                        }

                        Iterator var6 = multiDataBeans.iterator();

                        while(var6.hasNext()) {
                            MultiDataBean multiDataBean = (MultiDataBean)var6.next();
                            if (StringUtil.isEmpty(multiDataBean.getFileName())) {
                                logger.error(" fileName is null");
                                throw new Exception("fileName is null ");
                            }

                            if (!StringUtil.validateStrMaxLength(multiDataBean.getFileName(), 128)) {
                                logger.error(" fileName 长度超长，限长128");
                                throw new Exception("fileName 长度超长，限长128");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getFileDataHash())) {
                                logger.error("fileDataHash is null");
                                throw new Exception("fileDataHash is null");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getType())) {
                                multiDataBean.setType("0");
                            }
                        }

                        var6 = proofSealStrategies.iterator();

                        while(var6.hasNext()) {
                            ProofSealStrategy proofSealStrategy = (ProofSealStrategy)var6.next();
                            if (StringUtil.isEmpty(proofSealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(proofSealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            checkProofSealStrategy(proofSealStrategy);
                        }
                    }

                    if (!flag) {
                        logger.error(" sealStrategies and proofSealStrategies both null");
                        throw new Exception(" sealStrategies and proofSealStrategies both null");
                    }
                }
            }
        }
    }

    public  void doCheckTx4003(CompoundSealPdfListRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        List<PdfBean> pdfBeans = requestBody.getPdfBeans();
        int inputType1 = 0;
        int inputType2 = 0;

        for(int i = 0; i < pdfBeans.size(); ++i) {
            PdfBean pdfBean = (PdfBean)pdfBeans.get(i);
            if (!"1".equals(pdfBean.getInputType()) && !"2".equals(pdfBean.getInputType())) {
                logger.error("inputType 取值错误，inputType 取值只能是1 或者 2 ");
                throw new Exception("inputType 取值错误，inputType 取值只能是1 或者 2 ");
            }

            if ("1".equals(pdfBean.getInputType())) {
                ++inputType1;
                if (StringUtil.isEmpty(pdfBean.getInputSource())) {
                    logger.error("inputSource is null");
                    throw new Exception("inputSource is null");
                }

                String pdfSuffix = IoUtil.getFileNameSuffix(pdfBean.getInputSource());
                if (!"pdf".equals(pdfSuffix)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(pdfBean.getInputType())) {
                ++inputType2;
                if (pdfBean.getPdfData() == null) {
                    logger.error("第" + (i + 1) + " 个 pdfData is null ");
                    throw new Exception("第" + (i + 1) + " 个 pdfData is null ");
                }

                fileDataList.add(pdfBean.getPdfData());
                pdfBean.setPdfData((byte[])null);
            }

            if (StringUtil.isEmpty(pdfBean.getBizSerialNo())) {
                logger.error("bizSerialNo is null");
                throw new Exception("bizSerialNo is null");
            }

            if (!StringUtil.validateStrMaxLength(pdfBean.getBizSerialNo(), 32)) {
                logger.error("bizSerialNo 超过最大长度限制，最大32位");
                throw new Exception("bizSerialNo 超过最大长度限制，最大32位");
            }
        }

        if (inputType1 > 0 && inputType2 > 0) {
            throw new Exception("inputType 数据源类型在pdfBeans中只能同时存在一种");
        } else {
            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error("timestampChannel 取值错误，取值只能是0 或者 1");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else {
                if (StringUtil.isEmpty(requestBody.getSceneCertChannel())) {
                    requestBody.setSceneCertChannel("0");
                }

                if (!"0".equals(requestBody.getSceneCertChannel()) && !"1".equals(requestBody.getSceneCertChannel())) {
                    logger.error("sceneCertChannel 取值错误，取值只能是0 或者 1");
                    throw new Exception("sceneCertChannel 取值错误，取值只能是0 或者 1");
                } else {
                    boolean flag = false;
                    List<SealStrategy> sealStrategies = requestBody.getSealStrategies();
                    if (sealStrategies != null && sealStrategies.size() > 0) {
                        flag = true;

                        SealStrategy sealStrategy;
                        for(Iterator var13 = sealStrategies.iterator(); var13.hasNext(); checkSealStrategy(sealStrategy)) {
                            sealStrategy = (SealStrategy)var13.next();
                            if (StringUtil.isEmpty(sealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(sealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getPdfIndex())) {
                                sealStrategy.setPdfIndex("0");
                            }
                        }
                    }

                    List<ProofSealStrategy> proofSealStrategies = requestBody.getProofSealStrategies();
                    if (proofSealStrategies != null && proofSealStrategies.size() > 0) {
                        flag = true;
                        List<MultiDataBean> multiDataBeans = requestBody.getMultiDatas();
                        if (multiDataBeans == null || multiDataBeans.size() == 0) {
                            logger.error(" 证据签章时，multiDataBeans 不能为空");
                            throw new Exception("证据签章时，multiDataBeans 不能为空 ");
                        }

                        Iterator var9 = multiDataBeans.iterator();

                        while(var9.hasNext()) {
                            MultiDataBean multiDataBean = (MultiDataBean)var9.next();
                            if (StringUtil.isEmpty(multiDataBean.getFileName())) {
                                logger.error(" fileName is null");
                                throw new Exception("fileName is null ");
                            }

                            if (!StringUtil.validateStrMaxLength(multiDataBean.getFileName(), 128)) {
                                logger.error(" fileName 长度超长，限长128");
                                throw new Exception("fileName 长度超长，限长128");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getFileDataHash())) {
                                logger.error("fileDataHash is null");
                                throw new Exception("fileDataHash is null");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getType())) {
                                multiDataBean.setType("0");
                            }
                        }

                        ProofSealStrategy proofSealStrategy;
                        for(var9 = proofSealStrategies.iterator(); var9.hasNext(); checkProofSealStrategy(proofSealStrategy)) {
                            proofSealStrategy = (ProofSealStrategy)var9.next();
                            if (StringUtil.isEmpty(proofSealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(proofSealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            if (CommonUtil.isEmpty(proofSealStrategy.getPdfIndex())) {
                                proofSealStrategy.setPdfIndex("0");
                            }
                        }
                    }

                    if (!flag) {
                        logger.error("sealStrategies and proofSealStrategies both null");
                        throw new Exception(" sealStrategies and proofSealStrategies both null");
                    }
                }
            }
        }
    }

    public  void doCheckTx4005(VerifyPdfSealRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            if (!"1".equals(requestBody.getSealVerifyType()) && !"2".equals(requestBody.getSealVerifyType())) {
                logger.error("sealVerifyType 取值错误，sealVerifyType取值只能是1或者2");
                throw new Exception("sealVerifyType 取值错误，sealVerifyType取值只能是1或者2");
            }
        }
    }

    public  void doCheckTx4006(RevokePdfSealRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    logger.error("inputSource is null");
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            if (!StringUtil.isNaturalNumber(requestBody.getCount(), 4)) {
                logger.error(" count 取值不正确 ");
                throw new Exception(" count 取值不正确 ");
            }
        }
    }

    public  void doCheckTx4007(CalculatePdfHashRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    logger.error(" inputSource is null");
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource()});
            if (requestBody.getX509Cert() == null) {
                logger.error(" x509Cert is null");
                throw new Exception(" x509Cert is null");
            } else {
                if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                    requestBody.setTimestampChannel("0");
                }

                if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                    logger.error(" timestampChannel 取值错误，取值只能是0或者 1");
                    throw new Exception(" timestampChannel 取值错误，取值只能是0或者 1");
                } else {
                    if (StringUtil.isEmpty(requestBody.getSignatureType())) {
                        requestBody.setSignatureType("1");
                    }

                    if (!"1".equals(requestBody.getSignatureType()) && !"2".equals(requestBody.getSignatureType())) {
                        logger.error(" signatureType 取值错误，取值只能是1或者2");
                        throw new Exception(" signatureType 取值错误，取值只能是1或者2");
                    } else {
                        SealStrategy sealStrategy = requestBody.getSealStrategy();
                        if (sealStrategy == null) {
                            logger.error(" sealStrategy is null");
                            throw new Exception(" sealStrategy is null");
                        } else {
                            checkSealStrategy4Asyn(sealStrategy, false);
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx4008(SynthesizeOuterSignatureRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (requestBody.getSignature() == null) {
            logger.error(" signature is null");
            throw new Exception("signature is null");
        } else if (StringUtil.isEmpty(requestBody.getPdfId())) {
            logger.error(" pdfId is null");
            throw new Exception("pdfId is null");
        } else if (!"1".equals(requestBody.getSignatureType()) && !"2".equals(requestBody.getSignatureType())) {
            logger.error(" signatureType 取值错误，取值只能是0 或者 1");
            throw new Exception(" signatureType 取值错误，取值只能是0 或者 1");
        }
    }

    public  void doCheckTx4009(SynthesizeOuterSignatureAndSealPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (requestBody.getSignature() == null) {
            logger.error(" signature is null");
            throw new Exception("signature is null");
        } else if (StringUtil.isEmpty(requestBody.getPdfId())) {
            logger.error("pdfId is null");
            throw new Exception("pdfId is null");
        } else {
            if (StringUtil.isEmpty(requestBody.getSignatureType())) {
                requestBody.setSignatureType("1");
            }

            if (!"1".equals(requestBody.getSignatureType()) && !"2".equals(requestBody.getSignatureType())) {
                logger.error("signatureType 取值错误，取值只能是1或者 2");
                throw new Exception(" signatureType 取值错误，取值只能是1或者2");
            } else {
                if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                    requestBody.setTimestampChannel("0");
                }

                if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                    logger.error(" timestampChannel 取值错误，取值只能是0或者1");
                    throw new Exception(" timestampChannel 取值错误，取值只能是0或者1");
                } else {
                    List<SealStrategy> sealStrategies = requestBody.getSealStrategies();
                    if (sealStrategies != null && sealStrategies.size() != 0) {
                        Iterator var3 = sealStrategies.iterator();

                        while(var3.hasNext()) {
                            SealStrategy sealStrategy = (SealStrategy)var3.next();
                            if (StringUtil.isEmpty(sealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(sealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            checkSealStrategy(sealStrategy);
                        }

                    } else {
                        logger.error("sealStrategies is null");
                        throw new Exception("sealStrategies is null");
                    }
                }
            }
        }
    }

    public  void doCheckTx4011(ApplyAndDownloadCertRequestBody requestBody) throws Exception {
        if (!"1".equals(requestBody.getCustomerType()) && !"2".equals(requestBody.getCustomerType()) && !"7".equals(requestBody.getCustomerType()) && !"8".equals(requestBody.getCustomerType())) {
            logger.error("customerType 取值错误，customerType取值只能是 1或者2或者7或者8");
            throw new Exception(" customerType 取值错误，customerType取值只能是 1或者2或者7或者8 ");
        } else if (!StringUtil.isEmpty(requestBody.getIdentificationNo()) && !StringUtil.isEmpty(requestBody.getIdentificationType()) && !StringUtil.isEmpty(requestBody.getUserName())) {
            if (StringUtil.isEmpty(IdType.getNameByCode(requestBody.getIdentificationType()))) {
                throw new Exception("identificationType 取值错误");
            } else if (StringUtil.isEmpty(requestBody.getP10())) {
                logger.error("p10 is null");
                throw new Exception(" p10 is null ");
            } else if (("7".equals(requestBody.getCustomerType()) || "8".equals(requestBody.getCustomerType())) && StringUtil.isEmpty(requestBody.getSelfExtValue())) {
                logger.error("申请场景证书时，  selfExtValue 不能为空");
                throw new Exception("申请场景证书时，  selfExtValue 不能为空");
            } else if (!"RSA".equalsIgnoreCase(requestBody.getKeyAlg()) && !"SM2".equalsIgnoreCase(requestBody.getKeyAlg())) {
                logger.error("keyAlg 取值错误，只能是RSA或者SM2");
                throw new Exception("keyAlg 取值错误，只能是RSA或者SM2");
            } else {
                if ("RSA".equalsIgnoreCase(requestBody.getKeyAlg())) {
                    if (!"2048".equals(requestBody.getKeyLength())) {
                        logger.error("keyLength取值错误，keyAlg 为RSA时 keyLength仅支持2048");
                        throw new Exception("keyLength取值错误，keyAlg 为RSA时keyLength仅支持2048");
                    }
                } else if ("SM2".equalsIgnoreCase(requestBody.getKeyAlg()) && !"256".equals(requestBody.getKeyLength())) {
                    logger.error("keyLength 取值错误，keyAlg 为SM2时 keyLength 只能是256");
                    throw new Exception("keyLength 取值错误，keyAlg 为SM2时 keyLength 只能是256");
                }

            }
        } else {
            logger.error("identificationType or identificationNo or useName is null");
            throw new Exception(" identificationType or identificationNo or useName is null");
        }
    }

    public  void doCheckTx4012(CrossPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"pdf".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是pdf文件");
                    throw new Exception("所选文件不是pdf文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource(), requestBody.getOutputFilepath()});
            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error(" timestampChannel 取值错误，取值只能是0 或者 1");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else {
                CrossPdfStrategy crossStrategy = requestBody.getCrossPdfStrategy();
                if (crossStrategy == null) {
                    logger.error("crossSealStrategy  is null");
                    throw new Exception("crossSealStrategy is null");
                } else if (!"5".equals(crossStrategy.getCrossStyle()) && !"6".equals(crossStrategy.getCrossStyle()) && !"7".equals(crossStrategy.getCrossStyle())) {
                    logger.error("crossStyle 取值错误");
                    throw new Exception("crossStyle 取值错误");
                } else if (!StringUtil.isNaturalNumber(crossStrategy.getFromPage(), 6)) {
                    logger.error("fromPage 取值错误");
                    throw new Exception(" fromPage 取值错误 ");
                } else {
                    if ("0".equals(crossStrategy.getFromPage())) {
                        crossStrategy.setToPage("0");
                    }

                    if (!StringUtil.isNaturalNumber(crossStrategy.getToPage(), 6)) {
                        logger.error("toPage 取值错误");
                        throw new Exception(" toPage 取值错误 ");
                    } else {
                        int fromPage = Integer.parseInt(crossStrategy.getFromPage());
                        int toPage = Integer.parseInt(crossStrategy.getToPage());
                        if (fromPage > toPage) {
                            logger.error("frompage 取值 大于 toPage ");
                            throw new Exception(" frompage 取值 大于 toPage ");
                        } else {
                            if ("5".equals(crossStrategy.getCrossStyle()) || "6".equals(crossStrategy.getCrossStyle())) {
                                crossStrategy.setAbsoluteX("0");
                            }

                            if ("7".equals(crossStrategy.getCrossStyle())) {
                                crossStrategy.setAbsoluteY("0");
                            }

                            if (StringUtil.isEmpty(crossStrategy.getAbsoluteX())) {
                                crossStrategy.setAbsoluteX("0");
                            }

                            if (!StringUtil.isPositiveNumeric(crossStrategy.getAbsoluteX())) {
                                logger.error("absoluteX取值错误");
                                throw new Exception("absoluteX取值错误");
                            } else {
                                if (StringUtil.isEmpty(crossStrategy.getAbsoluteY())) {
                                    crossStrategy.setAbsoluteY("0");
                                }

                                if (!StringUtil.isPositiveNumeric(crossStrategy.getAbsoluteY())) {
                                    logger.error("absoluteY取值错误");
                                    throw new Exception("absoluteY取值错误");
                                } else {
                                    CrossSealStrategy sealStrategy = requestBody.getSealStrategy();
                                    if (sealStrategy == null) {
                                        logger.error(" sealStrategy is null");
                                        throw new Exception(" sealStrategy is null");
                                    } else {
                                        checkCrossSealStrategy(sealStrategy);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public  void doCheckTx4013(SignPdfProofHashRequestBody requestBody) throws Exception {
        if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
            requestBody.setTimestampChannel("0");
        }

        if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
            logger.error(" timestampChannel 取值错误，取值只能是0 或者 1");
            throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
        } else {
            if (StringUtil.isEmpty(requestBody.getSceneCertChannel())) {
                requestBody.setSceneCertChannel("0");
            }

            if (!"0".equals(requestBody.getSceneCertChannel()) && !"1".equals(requestBody.getSceneCertChannel())) {
                logger.error(" sceneCertChannel 取值错误，取值只能是0 或者 1");
                throw new Exception("sceneCertChannel 取值错误，取值只能是0 或者 1");
            } else {
                List<PdfHashBean> pdfHashs = requestBody.getPdfHashs();
                if (pdfHashs != null && pdfHashs.size() != 0) {
                    Iterator var2 = pdfHashs.iterator();

                    while(var2.hasNext()) {
                        PdfHashBean pdfHash = (PdfHashBean)var2.next();
                        if (StringUtil.isEmpty(pdfHash.getPdfSealHash())) {
                            logger.error(" pdfSealHash is null");
                            throw new Exception(" pdfSealHash is null");
                        }
                    }

                    List<MultiDataBean> proofHashs = requestBody.getProofHashs();
                    if (proofHashs != null && proofHashs.size() != 0) {
                        Iterator var6 = proofHashs.iterator();

                        while(var6.hasNext()) {
                            MultiDataBean multiDataBean = (MultiDataBean)var6.next();
                            if (StringUtil.isEmpty(multiDataBean.getFileName())) {
                                logger.error(" fileName is null");
                                throw new Exception("fileName is null ");
                            }

                            if (!StringUtil.validateStrMaxLength(multiDataBean.getFileName(), 128)) {
                                logger.error(" fileName 长度超长，限长128");
                                throw new Exception("fileName 长度超长，限长128");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getFileDataHash())) {
                                logger.error(" fileDataHash is null");
                                throw new Exception("fileDataHash is null");
                            }
                        }

                        byte[] x509Cert = requestBody.getX509Cert();
                        SignInfoBean signInfo = requestBody.getSignInfo();
                        if ((x509Cert == null || x509Cert.length == 0) && signInfo == null) {
                            throw new Exception("signInfo 和  x509Cert 不能同时为空");
                        } else {
                            if ((x509Cert == null || x509Cert.length == 0) && signInfo != null) {
                                if (StringUtil.isEmpty(signInfo.getUserName())) {
                                    logger.error("userName is null");
                                    throw new Exception(" userName is null");
                                }

                                if (!StringUtil.validateStrMaxLength(signInfo.getUserName(), 128)) {
                                    logger.error("userName 长度超过 128");
                                    throw new Exception("userName 长度超过 128");
                                }

                                if (StringUtil.isEmpty(signInfo.getIdentificationType())) {
                                    logger.error("identificationType is null");
                                    throw new Exception("identificationType is null");
                                }

                                if (StringUtil.isEmpty(IdType.getNameByCode(signInfo.getIdentificationType()))) {
                                    throw new Exception("identificationType 取值错误");
                                }

                                if (StringUtil.isEmpty(signInfo.getIdentificationNo())) {
                                    logger.error("identificationNo is null");
                                    throw new Exception("identificationNo is null ");
                                }

                                if (!StringUtil.validateStrMaxLength(signInfo.getIdentificationNo(), 80)) {
                                    logger.error("identificationNo 长度超过80");
                                    throw new Exception("identificationNo 长度超过80");
                                }
                            }

                        }
                    } else {
                        logger.error(" proofHashs");
                        throw new Exception("proofHashs is null ");
                    }
                } else {
                    logger.error(" pdfHashs");
                    throw new Exception("pdfHashs is null ");
                }
            }
        }
    }

    public  void doCheckTx4014(SignPdfHashRequestBody requestBody) throws Exception {
        List<PdfHashBean> pdfHashs = requestBody.getPdfHashs();
        if (pdfHashs != null && pdfHashs.size() != 0) {
            Iterator var2 = pdfHashs.iterator();

            PdfHashBean pdfHash;
            do {
                if (!var2.hasNext()) {
                    SealInfoBean sealInfo = requestBody.getSealInfo();
                    if (StringUtil.isEmpty(sealInfo.getSealCode())) {
                        logger.error("sealCode is null");
                        throw new Exception("sealCode is null ");
                    }

                    if (!StringUtil.validateStrMaxLength(sealInfo.getSealCode(), 32)) {
                        logger.error("sealCode 长度超过 32");
                        throw new Exception("sealCode 长度超过 32");
                    }

                    if (StringUtil.isEmpty(sealInfo.getSealPassword())) {
                        logger.error("sealPassword is null");
                        throw new Exception("sealPassword is null");
                    }

                    if (!StringUtil.validateStrMaxLength(sealInfo.getSealPassword(), 64)) {
                        logger.error("sealPassword 长度超过64");
                        throw new Exception("sealPassword 长度超过64");
                    }

                    return;
                }

                pdfHash = (PdfHashBean)var2.next();
            } while(!StringUtil.isEmpty(pdfHash.getPdfSealHash()));

            logger.error(" pdfSealHash is null");
            throw new Exception(" pdfSealHash is null");
        } else {
            logger.error(" pdfHashs");
            throw new Exception("pdfHashs is null ");
        }
    }

    public  void doCheckTx4016(WebSealRequestBody requestBody) throws Exception {
        if (StringUtil.isEmpty(requestBody.getInputSource())) {
            logger.error(" inputSource is null");
            throw new Exception(" inputSource is null");
        } else {
            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (StringUtil.isNotEmpty(requestBody.getTimestampChannel()) && !"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error(" timestampChannel 取值错误，取值只能是0 或者 1");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else if (StringUtil.isEmpty(requestBody.getSealCode())) {
                logger.error(" sealCode is null ");
                throw new Exception("sealCode is null ");
            } else if (!StringUtil.validateStrMaxLength(requestBody.getSealCode(), 32)) {
                logger.error("sealCode 长度超过 32");
                throw new Exception("sealCode 长度超过 32");
            } else if (StringUtil.isEmpty(requestBody.getSealPassword())) {
                logger.error(" sealPassword is null ");
                throw new Exception("sealPassword is null ");
            } else if (!StringUtil.validateStrMaxLength(requestBody.getSealPassword(), 64)) {
                logger.error("sealPassword 长度超过64");
                throw new Exception("sealPassword 长度超过64");
            } else if (StringUtil.isEmpty(requestBody.getHashAlg())) {
                logger.error("hashAlg is null ");
                throw new Exception(" hashAlg is null  ");
            } else if (StringUtil.isEmpty(requestBody.getSealLocation())) {
                logger.error("sealLocation is null ");
                throw new Exception(" sealLocation is null  ");
            } else if (StringUtil.isEmpty(requestBody.getSealReason())) {
                logger.error("sealReason is null ");
                throw new Exception(" sealReason is null  ");
            }
        }
    }

    public  void doCheckTx4017(MessageSealRequestBody requestBody) throws Exception {
        if (StringUtil.isEmpty(requestBody.getMessageHash())) {
            logger.error("messageHash is null ");
            throw new Exception(" messageHash is null  ");
        } else if (StringUtil.isEmpty(requestBody.getSealCode())) {
            logger.error(" sealCode is null ");
            throw new Exception("sealCode is null ");
        } else if (!StringUtil.validateStrMaxLength(requestBody.getSealCode(), 32)) {
            logger.error("sealCode 长度超过 32");
            throw new Exception("sealCode 长度超过 32");
        } else if (StringUtil.isEmpty(requestBody.getSealPassword())) {
            logger.error(" sealPassword is null ");
            throw new Exception("sealPassword is null ");
        } else if (!StringUtil.validateStrMaxLength(requestBody.getSealPassword(), 64)) {
            logger.error("sealPassword 长度超过64");
            throw new Exception("sealPassword 长度超过64");
        } else if (StringUtil.isEmpty(requestBody.getHashAlg())) {
            logger.error("hashAlg is null ");
            throw new Exception(" hashAlg is null  ");
        }
    }

    public  void doCheckTx4018(VerifyWebSealRequestBody requestBody) throws Exception {
        if (StringUtil.isEmpty(requestBody.getInputSource())) {
            logger.error(" inputSource is null");
            throw new Exception(" inputSource is null");
        } else if (StringUtil.isEmpty(requestBody.getHashAlg())) {
            logger.error("hashAlg is null ");
            throw new Exception(" hashAlg is null  ");
        } else if (StringUtil.isEmpty(requestBody.getSignature())) {
            logger.error("signature is null ");
            throw new Exception(" signature is null  ");
        }
    }

    public  void doCheckTx4019(VerifyMessageSealRequestBody requestBody) throws Exception {
        if (StringUtil.isEmpty(requestBody.getSignature())) {
            logger.error("signature is null ");
            throw new Exception(" signature is null  ");
        }
    }

    public  void doCheckTx4101(SealOfdRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String ofdFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                ofdFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"ofd".equals(ofdFileDataHash)) {
                    logger.error(" 所选文件不是ofd文件");
                    throw new Exception("所选文件不是ofd文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getOfdData() == null) {
                    logger.error(" ofdData is null");
                    throw new Exception(" ofdData is null");
                }

                if (StringUtil.isEmpty(requestBody.getOfdFileDataHash())) {
                    ofdFileDataHash = proofHashUtil.digestAndHex(requestBody.getOfdData());
                    requestBody.setOfdFileDataHash(ofdFileDataHash);
                }

                fileDataList.add(requestBody.getOfdData());
                requestBody.setOfdData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getOfdFileDataHash(), requestBody.getInputSource(), requestBody.getOutputFilepath()});
            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error(" timestampChannel 取值错误，取值只能是0 或者 1");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else {
                OfdSealStrategy ofdSealStrategy = requestBody.getSealStrategy();
                if (ofdSealStrategy == null) {
                    logger.error(" ofdSealStrategy is null");
                    throw new Exception(" ofdSealStrategy is null");
                } else {
                    checkOfdSealStrategy(ofdSealStrategy, requestBody.getOfdType());
                }
            }
        }
    }

    public  void doCheckTx4105(VerifyOfdSealRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2");
            throw new Exception("inputType 取值错误，只能是1或者2");
        } else {
            String pdfFileDataHash;
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    throw new Exception(" inputSource is null");
                }

                pdfFileDataHash = IoUtil.getFileNameSuffix(requestBody.getInputSource());
                if (!"ofd".equals(pdfFileDataHash)) {
                    logger.error(" 所选文件不是ofd文件");
                    throw new Exception("所选文件不是ofd文件");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getOfdData() == null) {
                    logger.error(" ofdData is null");
                    throw new Exception(" ofdData is null");
                }

                if (StringUtil.isEmpty(requestBody.getOfdFileDataHash())) {
                    pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getOfdData());
                    requestBody.setOfdFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getOfdData());
                requestBody.setOfdData((byte[])null);
            }

        }
    }

    public  void doCheckTx5001(CheckNetWorkConnRequestBody requestBody) throws Exception {
        if (CommonUtil.isEmpty(requestBody.getTestMsg())) {
            logger.error("testMsg is null");
            throw new Exception(" testMsg is null  ");
        }
    }

    public  void doCheckTx5002(HeartBeatRequestBody requestBody) throws Exception {
        if (CommonUtil.isEmpty(requestBody.getOrganizationCode())) {
            logger.error("organizationCode is null ");
            throw new Exception(" organizationCode is null  ");
        }
    }

    public  void doCheckTx5003(GetP10AmountRequestBody requestBody) throws Exception {
        if (CommonUtil.isEmpty(requestBody.getHashAlg())) {
            logger.error("hashAlg is null ");
            throw new Exception(" hashAlg is null  ");
        } else if (CommonUtil.isEmpty(requestBody.getKeyAlg())) {
            logger.error("keyAlg is null ");
            throw new Exception(" keyAlg is null  ");
        } else if (CommonUtil.isEmpty(requestBody.getKeyLength())) {
            logger.error("keyLength is null ");
            throw new Exception(" keyLength is null  ");
        }
    }

    public  void doCheckTx5004(GetSceneCertAmountRequestBody requestBody) throws Exception {
        if (CommonUtil.isEmpty(requestBody.getKeyAlg())) {
            logger.error("keyAlg is null ");
            throw new Exception(" keyAlg is null  ");
        }
    }

    public  void doCheckTx6001(QueryProofInfoListRequestBody requestBody) throws Exception {
        if (StringUtil.isNotEmpty(requestBody.getStartTime()) && StringUtil.isNotEmpty(requestBody.getEndTime())) {
            Date startDate = TimeUtil.getDateInFormat(requestBody.getStartTime(), "yyyyMMdd");
            Date endDate = TimeUtil.getDateInFormat(requestBody.getEndTime(), "yyyyMMdd");
            if (startDate.compareTo(endDate) > 0) {
                logger.error("startTime > endTime  ");
                throw new Exception(" startTime > endTime ");
            }

            long day = (endDate.getTime() - startDate.getTime()) / 86400000L;
            if (day > 30L) {
                logger.error("endTime - stratTime > 30 ");
                throw new Exception("  endTime - stratTime > 30");
            }
        }

    }

    public  void doCheckTx8001(SynthesizeAndCompoundSealPdfRequestBody requestBody, List<byte[]> fileDataList) throws Exception {
        if (!"1".equals(requestBody.getInputType()) && !"2".equals(requestBody.getInputType()) && !"3".equals(requestBody.getInputType())) {
            logger.error(" inputType 取值错误，只能是1或者2或者3");
            throw new Exception("inputType 取值错误，只能是1或者2或者3");
        } else {
            if ("1".equals(requestBody.getInputType())) {
                if (StringUtil.isEmpty(requestBody.getInputSource())) {
                    logger.error("inputSource is null ");
                    throw new Exception(" inputSource is null");
                }
            } else if ("2".equals(requestBody.getInputType())) {
                if (requestBody.getPdfData() == null) {
                    logger.error(" pdfData is null");
                    throw new Exception(" pdfData is null");
                }

                if (StringUtil.isEmpty(requestBody.getPdfFileDataHash())) {
                    String pdfFileDataHash = proofHashUtil.digestAndHex(requestBody.getPdfData());
                    requestBody.setPdfFileDataHash(pdfFileDataHash);
                }

                fileDataList.add(requestBody.getPdfData());
                requestBody.setPdfData((byte[])null);
            }

            logger.info("inputType={} pdfFileDataHash={} inputSource={} outputFilepath={}", new Object[]{requestBody.getInputType(), requestBody.getPdfFileDataHash(), requestBody.getInputSource(), requestBody.getOutputFilepath()});
            if (StringUtil.isEmpty(requestBody.getTimestampChannel())) {
                requestBody.setTimestampChannel("0");
            }

            if (!"0".equals(requestBody.getTimestampChannel()) && !"1".equals(requestBody.getTimestampChannel())) {
                logger.error(" timestampChannel 取值错误，取值只能是0 或者 1 ");
                throw new Exception(" timestampChannel 取值错误，取值只能是0 或者 1");
            } else {
                if (StringUtil.isEmpty(requestBody.getSceneCertChannel())) {
                    requestBody.setSceneCertChannel("0");
                }

                if (!"0".equals(requestBody.getSceneCertChannel()) && !"1".equals(requestBody.getSceneCertChannel())) {
                    logger.error(" sceneCertChannel 取值错误，取值只能是0 或者 1 ");
                    throw new Exception("sceneCertChannel 取值错误，取值只能是0 或者 1");
                } else {
                    List<FieldBean> fieldBeans = requestBody.getFieldBeans();
                    List<TextBean> textBeans = requestBody.getTextBeans();
                    List<ImageInfoBean> imageBeans = requestBody.getImageBeans();
                    checkBusinessData(fieldBeans, textBeans, imageBeans);
                    boolean flag = false;
                    List<SealStrategy> sealStrategies = requestBody.getSealStrategies();
                    if (sealStrategies != null && sealStrategies.size() > 0) {
                        flag = true;
                        Iterator var7 = sealStrategies.iterator();

                        while(var7.hasNext()) {
                            SealStrategy sealStrategy = (SealStrategy)var7.next();
                            if (StringUtil.isEmpty(sealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(sealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            checkSealStrategy(sealStrategy);
                        }
                    }

                    List<ProofSealStrategy> proofSealStrategies = requestBody.getProofSealStrategies();
                    if (proofSealStrategies != null && proofSealStrategies.size() > 0) {
                        flag = true;
                        List<MultiDataBean> multiDataBeans = requestBody.getMultiDatas();
                        if (multiDataBeans == null || multiDataBeans.size() == 0) {
                            logger.error(" multiDataBeans");
                            throw new Exception("multiDataBeans is null ");
                        }

                        Iterator var9 = multiDataBeans.iterator();

                        while(var9.hasNext()) {
                            MultiDataBean multiDataBean = (MultiDataBean)var9.next();
                            if (StringUtil.isEmpty(multiDataBean.getFileName())) {
                                logger.error(" fileName is null");
                                throw new Exception("fileName is null ");
                            }

                            if (!StringUtil.validateStrMaxLength(multiDataBean.getFileName(), 128)) {
                                logger.error(" fileName 长度超长，限长128");
                                throw new Exception("fileName 长度超长，限长128");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getFileDataHash())) {
                                logger.error(" fileDataHash is null");
                                throw new Exception("fileDataHash is null");
                            }

                            if (StringUtil.isEmpty(multiDataBean.getType())) {
                                multiDataBean.setType("0");
                            }
                        }

                        var9 = proofSealStrategies.iterator();

                        while(var9.hasNext()) {
                            ProofSealStrategy proofSealStrategy = (ProofSealStrategy)var9.next();
                            if (StringUtil.isEmpty(proofSealStrategy.getSerialNo())) {
                                logger.error(" serialNo is null");
                                throw new Exception(" serialNo is null");
                            }

                            if (!StringUtil.validateStrMaxLength(proofSealStrategy.getSerialNo(), 32)) {
                                logger.error(" serialNo 长度超长，限长32");
                                throw new Exception("serialNo 长度超长，限长32");
                            }

                            checkProofSealStrategy(proofSealStrategy);
                        }
                    }

                    if (!flag) {
                        logger.error(" sealStrategies and proofSealStrategies is null");
                        throw new Exception(" sealStrategies and proofSealStrategies is null");
                    }
                }
            }
        }
    }

    public  void doCheckTx6002(DownloadProofFileRequestBody requestBody) throws Exception {
        if (CommonUtil.isEmpty(requestBody.getBizSerialNo()) && CommonUtil.isEmpty(requestBody.getProofPdfId())) {
            logger.error(" bizSerialNo  and proofPdfId 都为空，请至少输入一个查询条件");
            throw new Exception("bizSerialNo  and proofPdfId 都为空，请至少输入一个查询条件");
        }
    }

    public  void doCheckTx6003(SealInfoBean sealInfo) throws Exception {
        if (sealInfo == null) {
            throw new Exception("sealInfo is null ");
        } else if (CommonUtil.isEmpty(sealInfo.getSealCode())) {
            logger.error(" sealCode is null ");
            throw new Exception("sealCode is null ");
        } else if (!StringUtil.validateStrMaxLength(sealInfo.getSealCode(), 32)) {
            logger.error("sealCode 长度超过 32");
            throw new Exception("sealCode 长度超过 32");
        } else if (CommonUtil.isEmpty(sealInfo.getSealPassword())) {
            logger.error(" sealPassword is null ");
            throw new Exception("sealPassword is null ");
        } else if (!StringUtil.validateStrMaxLength(sealInfo.getSealPassword(), 64)) {
            logger.error("sealPassword 长度超过64");
            throw new Exception("sealPassword 长度超过64");
        }
    }

    public  void checkCrossSealStrategy(CrossSealStrategy sealStrategy) throws Exception {
        if (!"1".equals(sealStrategy.getType()) && !"2".equals(sealStrategy.getType())) {
            logger.error(" type取值错误，取值只能是 1或者2");
            throw new Exception("type取值错误，取值只能是1或者2");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealCode())) {
            logger.error(" sealCode is null ");
            throw new Exception("sealCode is null ");
        } else if (!StringUtil.validateStrMaxLength(sealStrategy.getSealCode(), 32)) {
            logger.error("sealCode 长度超过 32");
            throw new Exception("sealCode 长度超过 32");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealPassword())) {
            logger.error(" sealPassword is null ");
            throw new Exception("sealPassword is null ");
        } else if (!StringUtil.validateStrMaxLength(sealStrategy.getSealPassword(), 64)) {
            logger.error("sealPassword 长度超过64");
            throw new Exception("sealPassword 长度超过64");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealPerson())) {
            logger.error(" sealPerson is null ");
            throw new Exception("sealPerson is null ");
        } else if (!StringUtil.validateStrMaxLength(sealStrategy.getSealPerson(), 128)) {
            logger.error(" sealPerson 长度超长，限长128");
            throw new Exception("sealPerson 长度超长，限长128");
        } else if (!"SHA-1".equals(sealStrategy.getHashAlg()) && !"SHA-256".equals(sealStrategy.getHashAlg()) && !"SHA-384".equals(sealStrategy.getHashAlg()) && !"SHA-512".equals(sealStrategy.getHashAlg()) && !"MD5".equals(sealStrategy.getHashAlg()) && !"SM3".equals(sealStrategy.getHashAlg())) {
            logger.error(" hashAlg 取值错误 ");
            throw new Exception(" hashAlg 取值错误 ");
        } else if ("2".equals(sealStrategy.getType()) && CommonUtil.isEmpty(sealStrategy.getSealImage())) {
            logger.error(" sealImage is null  ");
            throw new Exception("sealImage is null ");
        }
    }

    public  void checkOfdSealStrategy(OfdSealStrategy ofdSealStrategy, int odfType) throws Exception {
        if (!"1".equals(ofdSealStrategy.getType()) && !"2".equals(ofdSealStrategy.getType())) {
            logger.error(" type取值错误，取值只能是 1或者2");
            throw new Exception("type取值错误，取值只能是1或者2");
        } else if (CommonUtil.isEmpty(ofdSealStrategy.getSealCode())) {
            logger.error(" sealCode is null ");
            throw new Exception("sealCode is null ");
        } else if (CommonUtil.isEmpty(ofdSealStrategy.getSealPassword())) {
            logger.error(" sealPassword is null ");
            throw new Exception("sealPassword is null ");
        } else if (!"SHA-1".equals(ofdSealStrategy.getHashAlg()) && !"SHA-256".equals(ofdSealStrategy.getHashAlg()) && !"SHA-384".equals(ofdSealStrategy.getHashAlg()) && !"SHA-512".equals(ofdSealStrategy.getHashAlg()) && !"MD5".equals(ofdSealStrategy.getHashAlg()) && !"SM3".equals(ofdSealStrategy.getHashAlg())) {
            logger.error(" hashAlg 取值错误 ");
            throw new Exception(" hashAlg 取值错误 ");
        } else {
            if (StringUtil.isEmpty(ofdSealStrategy.getDisplaySize())) {
                ofdSealStrategy.setDisplaySize("0");
            }

            if (!StringUtil.isNaturalNumber(ofdSealStrategy.getDisplaySize(), 3)) {
                logger.error(" displaySize 取值错误 ");
                throw new Exception(" displaySize 取值错误 ");
            } else {
                if (StringUtil.isEmpty(ofdSealStrategy.getOffsetX())) {
                    ofdSealStrategy.setOffsetX("0");
                }

                if (StringUtil.isEmpty(ofdSealStrategy.getOffsetY())) {
                    ofdSealStrategy.setOffsetY("0");
                }

                if (StringUtil.isEmpty(ofdSealStrategy.getKeywordIndex())) {
                    ofdSealStrategy.setKeywordIndex("0");
                }

                if ("2".equals(ofdSealStrategy.getType()) && CommonUtil.isEmpty(ofdSealStrategy.getSealImage())) {
                    logger.error(" sealImage is null  ");
                    throw new Exception("sealImage is null ");
                } else {
                    if (StringUtil.isEmpty(ofdSealStrategy.getLx())) {
                        ofdSealStrategy.setLx("0");
                    }

                    if (StringUtil.isEmpty(ofdSealStrategy.getPageNo())) {
                        ofdSealStrategy.setPageNo("0");
                    }

                    if (!"2".equals(ofdSealStrategy.getSealType()) && !"3".equals(ofdSealStrategy.getSealType())) {
                        logger.error(" type取值错误，取值只能是  2 或者 3  ");
                        throw new Exception("type取值错误，取值只能是  2 或者 3 ");
                    } else {
                        if (StringUtil.isEmpty(ofdSealStrategy.getLy())) {
                            ofdSealStrategy.setLy("0");
                        }

                        if ("2".equals(ofdSealStrategy.getSealType())) {
                            List<SignLocation> signLocationList = ofdSealStrategy.getSignLocationList();
                            if (signLocationList != null && signLocationList.size() != 0) {
                                if (odfType == 3 && signLocationList.size() > 1) {
                                    throw new Exception("签电子发票章时只可签一个坐标");
                                }

                                Iterator var3 = signLocationList.iterator();

                                while(var3.hasNext()) {
                                    SignLocation sl = (SignLocation)var3.next();
                                    if (CommonUtil.isEmpty(sl.getPageNo())) {
                                        logger.error(" pageNo is null ");
                                        throw new Exception("pageNo is null");
                                    }

                                    if (!StringUtil.isNaturalNumber(sl.getPageNo(), 6)) {
                                        logger.error(" pageNo 取值错误 ");
                                        throw new Exception(" pageNo 取值错误  ");
                                    }

                                    if (!StringUtil.isPositiveNumeric(sl.getLx())) {
                                        logger.error("lx 取值错误 ");
                                        throw new Exception("lx 取值错误");
                                    }

                                    if (!StringUtil.isPositiveNumeric(sl.getLy())) {
                                        logger.error("ly 取值错误 ");
                                        throw new Exception("ly 取值错误");
                                    }
                                }
                            } else {
                                if (CommonUtil.isEmpty(ofdSealStrategy.getPageNo())) {
                                    logger.error(" pageNo is null ");
                                    throw new Exception("pageNo is null");
                                }

                                if (!StringUtil.isNaturalNumber(ofdSealStrategy.getPageNo(), 6)) {
                                    logger.error(" pageNo 取值错误 ");
                                    throw new Exception(" pageNo 取值错误  ");
                                }

                                if (!StringUtil.isPositiveNumeric(ofdSealStrategy.getLx())) {
                                    logger.error("lx 取值错误 ");
                                    throw new Exception("lx 取值错误");
                                }

                                if (!StringUtil.isPositiveNumeric(ofdSealStrategy.getLy())) {
                                    logger.error("ly 取值错误 ");
                                    throw new Exception("ly 取值错误");
                                }
                            }
                        }

                        if ("3".equals(ofdSealStrategy.getSealType())) {
                            if (StringUtil.isEmpty(ofdSealStrategy.getPageNo())) {
                                ofdSealStrategy.setPageNo("0");
                            }

                            if (!StringUtil.isNaturalNumber(ofdSealStrategy.getPageNo(), 6)) {
                                logger.error(" pageNo 取值错误 ");
                                throw new Exception(" pageNo 取值错误  ");
                            }

                            if (CommonUtil.isEmpty(ofdSealStrategy.getKeyword())) {
                                logger.error(" keyword  is null ");
                                throw new Exception("keyword  is null");
                            }

                            if (!StringUtil.isNumeric(ofdSealStrategy.getOffsetX())) {
                                logger.error("offsetX 取值错误 ");
                                throw new Exception("offsetX 取值错误");
                            }

                            if (!StringUtil.isNumeric(ofdSealStrategy.getOffsetY())) {
                                logger.error("offsetY 取值错误 ");
                                throw new Exception("offsetY 取值错误");
                            }
                        }

                    }
                }
            }
        }
    }

    public  void checkSealStrategy(SealStrategy sealStrategy) throws Exception {
        if (!"1".equals(sealStrategy.getType()) && !"2".equals(sealStrategy.getType()) && !"3".equals(sealStrategy.getType())) {
            logger.error(" type取值错误，取值只能是 1或者 2或者 3");
            throw new Exception("type取值错误，取值只能是1或者2或者3");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealCode())) {
            logger.error(" sealCode is null ");
            throw new Exception("sealCode is null ");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealPassword())) {
            logger.error(" sealPassword is null ");
            throw new Exception("sealPassword is null ");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealPerson())) {
            logger.error(" sealPerson is null ");
            throw new Exception("sealPerson is null ");
        } else if (!StringUtil.validateStrMaxLength(sealStrategy.getSealPerson(), 128)) {
            logger.error(" sealPerson 长度超长，限长128");
            throw new Exception("sealPerson 长度超长，限长128");
        } else if (!"SHA-1".equals(sealStrategy.getHashAlg()) && !"SHA-256".equals(sealStrategy.getHashAlg()) && !"SHA-384".equals(sealStrategy.getHashAlg()) && !"SHA-512".equals(sealStrategy.getHashAlg()) && !"MD5".equals(sealStrategy.getHashAlg()) && !"SM3".equals(sealStrategy.getHashAlg())) {
            logger.error(" hashAlg 取值错误 ");
            throw new Exception(" hashAlg 取值错误 ");
        } else {
            if (StringUtil.isEmpty(sealStrategy.getFillOpacity())) {
                sealStrategy.setFillOpacity("1");
            }

            if (!StringUtil.isPositiveNumeric(sealStrategy.getFillOpacity())) {
                logger.error(" fillOpacity 取值错误 ");
                throw new Exception(" fillOpacity 取值错误 ");
            } else {
                float fillOpacity = Float.parseFloat(sealStrategy.getFillOpacity());
                if (fillOpacity >= 0.0F && fillOpacity <= 1.0F) {
                    if (StringUtil.isEmpty(sealStrategy.getDisplaySize())) {
                        sealStrategy.setDisplaySize("0");
                    }

                    if (StringUtil.isNotEmpty(sealStrategy.getBusinessCode())) {
                        if (!StringUtil.validateStrMaxLength(sealStrategy.getBusinessCode(), 32)) {
                            logger.error(" businessCode 长度超长，限长32");
                            throw new Exception("businessCode 长度超长，限长32");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getBusinessFontSize())) {
                            sealStrategy.setBusinessFontSize("12");
                        }

                        if (!StringUtil.isPositiveNumber(sealStrategy.getBusinessFontSize(), 6)) {
                            logger.error(" businessFontSize 取值 错误 ");
                            throw new Exception(" businessFontSize 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getTextRectHeightPercent())) {
                            sealStrategy.setTextRectHeightPercent("0.5");
                        }

                        if (!StringUtil.isPositiveNumeric(sealStrategy.getTextRectHeightPercent())) {
                            logger.error(" textRectHeightPercent 取值 错误 ");
                            throw new Exception(" textRectHeightPercent 取值 错误 ");
                        }

                        float rx = Float.parseFloat(sealStrategy.getTextRectHeightPercent());
                        if (rx < 0.0F || rx > 1.0F) {
                            logger.error(" textRectHeightPercent 取值 错误 ");
                            throw new Exception(" textRectHeightPercent 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getBusinessColor())) {
                            sealStrategy.setBusinessColor("000000");
                        }

                        if (!StringUtil.isColorNumber(sealStrategy.getBusinessColor())) {
                            logger.error(" businessColor 取值 错误 ");
                            throw new Exception(" businessColor 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getBusinessFamily())) {
                            sealStrategy.setBusinessFamily("宋体");
                        }
                    }

                    if ("2".equals(sealStrategy.getType()) && CommonUtil.isEmpty(sealStrategy.getSealImage())) {
                        logger.error(" sealImage is null  ");
                        throw new Exception("sealImage is null ");
                    } else {
                        if ("3".equals(sealStrategy.getType())) {
                            if (CommonUtil.isEmpty(sealStrategy.getSealText())) {
                                logger.error(" sealText is null   ");
                                throw new Exception("sealText is null ");
                            }

                            if (!StringUtil.validateStrMaxLength(sealStrategy.getSealText(), 128)) {
                                logger.error(" sealText 长度超长，限长128");
                                throw new Exception("sealText 长度超长，限长128");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getWidth())) {
                                sealStrategy.setWidth("100");
                            }

                            if (!StringUtil.isPositiveNumeric(sealStrategy.getWidth())) {
                                logger.error(" width 取值 错误 ");
                                throw new Exception(" width 取值 错误 ");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getHeight())) {
                                sealStrategy.setHeight("100");
                            }

                            if (!StringUtil.isPositiveNumeric(sealStrategy.getHeight())) {
                                logger.error(" height 取值 错误 ");
                                throw new Exception(" height 取值 错误 ");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getFontSize())) {
                                sealStrategy.setFontSize("12");
                            }

                            if (!StringUtil.isPositiveNumber(sealStrategy.getFontSize(), 4)) {
                                logger.error("fontSize 取值错误 ");
                                throw new Exception("fontSize 取值错误");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getFontColor())) {
                                sealStrategy.setFontColor("000000");
                            }

                            if (!StringUtil.isColorNumber(sealStrategy.getFontColor())) {
                                logger.error("fontColor 取值错误 ");
                                throw new Exception("fontColor 取值错误");
                            }
                        }

                        if (!"1".equals(sealStrategy.getSealType()) && !"2".equals(sealStrategy.getSealType()) && !"3".equals(sealStrategy.getSealType())) {
                            logger.error(" type取值错误，取值只能是 1 或者  2 或者 3  ");
                            throw new Exception("type取值错误，取值只能是 1 或者  2 或者 3 ");
                        } else {
                            List keywordLocationList;
                            if ("1".equals(sealStrategy.getSealType())) {
                                keywordLocationList = sealStrategy.getSignatureFieldNameList();
                                if ((keywordLocationList == null || keywordLocationList.size() == 0) && CommonUtil.isEmpty(sealStrategy.getSignatureFieldName())) {
                                    logger.error(" signatureFieldName is null ");
                                    throw new Exception("signatureFieldName is null");
                                }
                            }

                            int i;
                            if ("2".equals(sealStrategy.getSealType())) {
                                keywordLocationList = sealStrategy.getSignLocationList();
                                if (keywordLocationList != null && keywordLocationList.size() != 0) {
                                    for(i = 0; i < keywordLocationList.size(); ++i) {
                                        SignLocation signLocation = (SignLocation)keywordLocationList.get(i);
                                        if (CommonUtil.isEmpty(signLocation.getPageNo())) {
                                            signLocation.setPageNo("1");
                                        }

                                        if (!StringUtil.isNaturalNumber(signLocation.getPageNo(), 6)) {
                                            logger.error(" pageNo 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " pageNo 取值错误  ");
                                        }

                                        if (StringUtil.isEmpty(signLocation.getLx())) {
                                            signLocation.setLx("0");
                                        }

                                        if (!StringUtil.isPositiveNumeric(signLocation.getLx())) {
                                            logger.error("lx 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " lx 取值错误");
                                        }

                                        if (StringUtil.isEmpty(signLocation.getLy())) {
                                            signLocation.setLy("0");
                                        }

                                        if (!StringUtil.isPositiveNumeric(signLocation.getLy())) {
                                            logger.error("ly 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " ly 取值错误");
                                        }
                                    }
                                } else {
                                    if (CommonUtil.isEmpty(sealStrategy.getPageNo())) {
                                        sealStrategy.setPageNo("1");
                                    }

                                    if (!StringUtil.isNaturalNumber(sealStrategy.getPageNo(), 6)) {
                                        logger.error(" pageNo 取值错误 ");
                                        throw new Exception(" pageNo 取值错误  ");
                                    }

                                    if (StringUtil.isEmpty(sealStrategy.getLx())) {
                                        sealStrategy.setLx("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(sealStrategy.getLx())) {
                                        logger.error("lx 取值错误 ");
                                        throw new Exception("lx 取值错误");
                                    }

                                    if (StringUtil.isEmpty(sealStrategy.getLy())) {
                                        sealStrategy.setLy("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(sealStrategy.getLy())) {
                                        logger.error("ly 取值错误 ");
                                        throw new Exception("ly 取值错误");
                                    }
                                }
                            }

                            if ("3".equals(sealStrategy.getSealType())) {
                                keywordLocationList = sealStrategy.getKeywordLocationList();
                                if (keywordLocationList != null && keywordLocationList.size() != 0) {
                                    for(i = 0; i < keywordLocationList.size(); ++i) {
                                        KeywordLocation keywordLocation = (KeywordLocation)keywordLocationList.get(i);
                                        if (StringUtil.isEmpty(keywordLocation.getPageNo())) {
                                            keywordLocation.setPageNo("0");
                                        }

                                        if (!StringUtil.isNaturalNumber(keywordLocation.getPageNo(), 6)) {
                                            logger.error(" pageNo 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " pageNo 取值错误  ");
                                        }

                                        if (CommonUtil.isEmpty(keywordLocation.getKeyword())) {
                                            logger.error(" keyword  is null ");
                                            throw new Exception("第" + (i + 1) + "keyword  is null");
                                        }

                                        if (StringUtil.isEmpty(keywordLocation.getOffsetX())) {
                                            keywordLocation.setOffsetX("0");
                                        }

                                        if (!StringUtil.isNumeric(keywordLocation.getOffsetX())) {
                                            logger.error("offsetX 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " offsetX 取值错误");
                                        }

                                        if (StringUtil.isEmpty(keywordLocation.getOffsetY())) {
                                            keywordLocation.setOffsetY("0");
                                        }

                                        if (!StringUtil.isNumeric(keywordLocation.getOffsetY())) {
                                            logger.error("offsetY 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " offsetY 取值错误");
                                        }
                                    }
                                } else {
                                    if (StringUtil.isEmpty(sealStrategy.getPageNo())) {
                                        sealStrategy.setPageNo("0");
                                    }

                                    if (!StringUtil.isNaturalNumber(sealStrategy.getPageNo(), 6)) {
                                        logger.error(" pageNo 取值错误 ");
                                        throw new Exception(" pageNo 取值错误  ");
                                    }

                                    if (CommonUtil.isEmpty(sealStrategy.getKeyword())) {
                                        logger.error(" keyword  is null ");
                                        throw new Exception("keyword  is null");
                                    }

                                    if (StringUtil.isEmpty(sealStrategy.getOffsetX())) {
                                        sealStrategy.setOffsetX("0");
                                    }

                                    if (!StringUtil.isNumeric(sealStrategy.getOffsetX())) {
                                        logger.error("offsetX 取值错误 ");
                                        throw new Exception("offsetX 取值错误");
                                    }

                                    if (StringUtil.isEmpty(sealStrategy.getOffsetY())) {
                                        sealStrategy.setOffsetY("0");
                                    }

                                    if (!StringUtil.isNumeric(sealStrategy.getOffsetY())) {
                                        logger.error("offsetY 取值错误 ");
                                        throw new Exception("offsetY 取值错误");
                                    }
                                }
                            }

                            if ("1".equals(sealStrategy.getIsAddDateText())) {
                                if (StringUtil.isEmpty(sealStrategy.getDateFontSize())) {
                                    sealStrategy.setDateFontSize("12");
                                }

                                if (!StringUtil.isPositiveNumber(sealStrategy.getDateFontSize(), 4)) {
                                    logger.error("dateFontSize 取值错误 ");
                                    throw new Exception("dateFontSize 取值错误");
                                }

                                if (StringUtil.isEmpty(sealStrategy.getDateFontColor())) {
                                    sealStrategy.setDateFontColor("000000");
                                }

                                if (!StringUtil.isColorNumber(sealStrategy.getDateFontColor())) {
                                    logger.error("dateFontColor 取值错误 ");
                                    throw new Exception("dateFontColor 取值错误");
                                }

                                if (StringUtil.isEmpty(sealStrategy.getDateRectHeight())) {
                                    sealStrategy.setDateRectHeight("40");
                                }

                                if (!StringUtil.isPositiveNumeric(sealStrategy.getDateRectHeight())) {
                                    logger.error("dateRectHeight 取值错误 ");
                                    throw new Exception("dateRectHeight 取值错误");
                                }

                                if (StringUtil.isEmpty(sealStrategy.getDateFontFamily())) {
                                    sealStrategy.setDateFontFamily("宋体");
                                }
                            }

                        }
                    }
                } else {
                    logger.error(" fillOpacity 取值错误 ");
                    throw new Exception(" fillOpacity 取值错误 ");
                }
            }
        }
    }

    public  void checkSealStrategy4Asyn(SealStrategy sealStrategy, boolean isLocal) throws Exception {
        if (!"2".equals(sealStrategy.getType()) && !"3".equals(sealStrategy.getType())) {
            logger.error(" type取值错误，取值只能是2或者 3");
            throw new Exception("type取值错误，取值只能是2或者3");
        } else if (CommonUtil.isEmpty(sealStrategy.getSealPerson())) {
            logger.error(" sealPerson is null ");
            throw new Exception("sealPerson is null ");
        } else if (!StringUtil.validateStrMaxLength(sealStrategy.getSealPerson(), 128)) {
            logger.error(" sealPerson 长度超长，限长128");
            throw new Exception("sealPerson 长度超长，限长128");
        } else if (!"SHA-1".equals(sealStrategy.getHashAlg()) && !"SHA-256".equals(sealStrategy.getHashAlg()) && !"SHA-384".equals(sealStrategy.getHashAlg()) && !"SHA-512".equals(sealStrategy.getHashAlg()) && !"MD5".equals(sealStrategy.getHashAlg()) && !"SM3".equals(sealStrategy.getHashAlg())) {
            logger.error(" hashAlg 取值错误 ");
            throw new Exception(" hashAlg 取值错误 ");
        } else {
            if (StringUtil.isEmpty(sealStrategy.getFillOpacity())) {
                sealStrategy.setFillOpacity("1");
            }

            if (!StringUtil.isPositiveNumeric(sealStrategy.getFillOpacity())) {
                logger.error(" fillOpacity 取值错误 ");
                throw new Exception(" fillOpacity 取值错误 ");
            } else {
                float fillOpacity = Float.parseFloat(sealStrategy.getFillOpacity());
                if (fillOpacity >= 0.0F && fillOpacity <= 1.0F) {
                    if (StringUtil.isEmpty(sealStrategy.getDisplaySize())) {
                        sealStrategy.setDisplaySize("0");
                    }

                    if (StringUtil.isNotEmpty(sealStrategy.getBusinessCode())) {
                        if (!StringUtil.validateStrMaxLength(sealStrategy.getBusinessCode(), 32)) {
                            logger.error(" businessCode 长度超长，限长32");
                            throw new Exception("businessCode 长度超长，限长32");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getBusinessFontSize())) {
                            sealStrategy.setBusinessFontSize("12");
                        }

                        if (!StringUtil.isPositiveNumber(sealStrategy.getBusinessFontSize(), 6)) {
                            logger.error(" businessFontSize 取值 错误 ");
                            throw new Exception(" businessFontSize 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getTextRectHeightPercent())) {
                            sealStrategy.setTextRectHeightPercent("0.5");
                        }

                        if (!StringUtil.isPositiveNumeric(sealStrategy.getTextRectHeightPercent())) {
                            logger.error(" textRectHeightPercent 取值 错误 ");
                            throw new Exception(" textRectHeightPercent 取值 错误 ");
                        }

                        float rx = Float.parseFloat(sealStrategy.getTextRectHeightPercent());
                        if (rx < 0.0F || rx > 1.0F) {
                            logger.error(" textRectHeightPercent 取值 错误 ");
                            throw new Exception(" textRectHeightPercent 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getBusinessColor())) {
                            sealStrategy.setBusinessColor("000000");
                        }

                        if (!StringUtil.isColorNumber(sealStrategy.getBusinessColor())) {
                            logger.error(" businessColor 取值 错误 ");
                            throw new Exception(" businessColor 取值 错误 ");
                        }

                        if (!StringUtil.isEmpty(sealStrategy.getBusinessFamily())) {
                            sealStrategy.setBusinessFamily("宋体");
                        }
                    }

                    if ("2".equals(sealStrategy.getType())) {
                        if (isLocal) {
                            if (sealStrategy.getSealImageData() == null) {
                                logger.error(" sealImageData is null  ");
                                throw new Exception("sealImageData is null ");
                            }
                        } else if (CommonUtil.isEmpty(sealStrategy.getSealImage())) {
                            logger.error(" sealImage is null  ");
                            throw new Exception("sealImage is null ");
                        }
                    }

                    if ("3".equals(sealStrategy.getType())) {
                        if (CommonUtil.isEmpty(sealStrategy.getSealText())) {
                            logger.error(" sealText is null   ");
                            throw new Exception("sealText is null ");
                        }

                        if (!StringUtil.validateStrMaxLength(sealStrategy.getSealText(), 128)) {
                            logger.error(" sealText 长度超长，限长128");
                            throw new Exception("sealText 长度超长，限长128");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getWidth())) {
                            sealStrategy.setWidth("100");
                        }

                        if (!StringUtil.isPositiveNumeric(sealStrategy.getWidth())) {
                            logger.error(" width 取值 错误 ");
                            throw new Exception(" width 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getHeight())) {
                            sealStrategy.setHeight("100");
                        }

                        if (!StringUtil.isPositiveNumeric(sealStrategy.getHeight())) {
                            logger.error(" height 取值 错误 ");
                            throw new Exception(" height 取值 错误 ");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getFontSize())) {
                            sealStrategy.setFontSize("12");
                        }

                        if (!StringUtil.isPositiveNumber(sealStrategy.getFontSize(), 4)) {
                            logger.error("fontSize 取值错误 ");
                            throw new Exception("fontSize 取值错误");
                        }

                        if (StringUtil.isEmpty(sealStrategy.getFontColor())) {
                            sealStrategy.setFontColor("000000");
                        }

                        if (!StringUtil.isColorNumber(sealStrategy.getFontColor())) {
                            logger.error("fontColor 取值错误 ");
                            throw new Exception("fontColor 取值错误");
                        }
                    }

                    if (!"1".equals(sealStrategy.getSealType()) && !"2".equals(sealStrategy.getSealType()) && !"3".equals(sealStrategy.getSealType())) {
                        logger.error(" type取值错误，取值只能是 1 或者  2 或者 3  ");
                        throw new Exception("type取值错误，取值只能是 1 或者  2 或者 3 ");
                    } else if ("1".equals(sealStrategy.getSealType()) && CommonUtil.isEmpty(sealStrategy.getSignatureFieldName())) {
                        logger.error(" signatureFieldName is null ");
                        throw new Exception("signatureFieldName is null");
                    } else {
                        if ("2".equals(sealStrategy.getSealType())) {
                            if (CommonUtil.isEmpty(sealStrategy.getPageNo())) {
                                logger.error(" pageNo is null ");
                                throw new Exception("pageNo is null");
                            }

                            if (!StringUtil.isNaturalNumber(sealStrategy.getPageNo(), 6)) {
                                logger.error(" pageNo 取值错误 ");
                                throw new Exception(" pageNo 取值错误  ");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getLx())) {
                                sealStrategy.setLx("0");
                            }

                            if (!StringUtil.isPositiveNumeric(sealStrategy.getLx())) {
                                logger.error("lx 取值错误 ");
                                throw new Exception("lx 取值错误");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getLy())) {
                                sealStrategy.setLy("0");
                            }

                            if (!StringUtil.isPositiveNumeric(sealStrategy.getLy())) {
                                logger.error("ly 取值错误 ");
                                throw new Exception("ly 取值错误");
                            }
                        }

                        if ("3".equals(sealStrategy.getSealType())) {
                            if (StringUtil.isEmpty(sealStrategy.getPageNo())) {
                                sealStrategy.setPageNo("0");
                            }

                            if (!StringUtil.isNaturalNumber(sealStrategy.getPageNo(), 6)) {
                                logger.error(" pageNo 取值错误 ");
                                throw new Exception(" pageNo 取值错误  ");
                            }

                            if (CommonUtil.isEmpty(sealStrategy.getKeyword())) {
                                logger.error(" keyword  is null ");
                                throw new Exception("keyword  is null");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getOffsetX())) {
                                sealStrategy.setOffsetX("0");
                            }

                            if (!StringUtil.isNumeric(sealStrategy.getOffsetX())) {
                                logger.error("offsetX 取值错误 ");
                                throw new Exception("offsetX 取值错误");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getOffsetY())) {
                                sealStrategy.setOffsetY("0");
                            }

                            if (!StringUtil.isNumeric(sealStrategy.getOffsetY())) {
                                logger.error("offsetY 取值错误 ");
                                throw new Exception("offsetY 取值错误");
                            }
                        }

                        if ("1".equals(sealStrategy.getIsAddDateText())) {
                            if (StringUtil.isEmpty(sealStrategy.getDateFontSize())) {
                                sealStrategy.setDateFontSize("12");
                            }

                            if (!StringUtil.isPositiveNumber(sealStrategy.getDateFontSize(), 4)) {
                                logger.error("dateFontSize 取值错误 ");
                                throw new Exception("dateFontSize 取值错误");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getDateFontColor())) {
                                sealStrategy.setDateFontColor("000000");
                            }

                            if (!StringUtil.isColorNumber(sealStrategy.getDateFontColor())) {
                                logger.error("dateFontColor 取值错误 ");
                                throw new Exception("dateFontColor 取值错误");
                            }

                            if (StringUtil.isEmpty(sealStrategy.getDateRectHeight())) {
                                sealStrategy.setDateRectHeight("40");
                            }

                            if (!StringUtil.isPositiveNumeric(sealStrategy.getDateRectHeight())) {
                                logger.error("dateRectHeight 取值错误 ");
                                throw new Exception("dateRectHeight 取值错误");
                            }
                        }

                    }
                } else {
                    logger.error(" fillOpacity 取值错误 ");
                    throw new Exception(" fillOpacity 取值错误 ");
                }
            }
        }
    }

    public  void checkProofSealStrategy(ProofSealStrategy proofSealStrategy) throws Exception {
        if (CommonUtil.isEmpty(proofSealStrategy.getSerialNo())) {
            logger.error(" serialNo  is null ");
            throw new Exception("serialNo  is null");
        } else if (CommonUtil.isEmpty(proofSealStrategy.getHandwritingImage()) && CommonUtil.isEmpty(proofSealStrategy.getSealText())) {
            logger.error(" handwritingImage and sealText  is null ");
            throw new Exception("handwritingImage and sealText  is null");
        } else if (CommonUtil.isEmpty(proofSealStrategy.getIdentificationType())) {
            logger.error(" identificationType is null ");
            throw new Exception("identificationType  is null");
        } else if (StringUtil.isEmpty(IdType.getNameByCode(proofSealStrategy.getIdentificationType()))) {
            throw new Exception("identificationType 取值错误");
        } else if (CommonUtil.isEmpty(proofSealStrategy.getIdentificationNo())) {
            logger.error(" identificationNo is null ");
            throw new Exception("identificationNo  is null");
        } else if (CommonUtil.isEmpty(proofSealStrategy.getSealPerson())) {
            logger.error(" sealPerson is null ");
            throw new Exception("sealPerson  is null");
        } else {
            if (StringUtil.isEmpty(proofSealStrategy.getDisplaySize())) {
                proofSealStrategy.setDisplaySize("0");
            }

            if (StringUtil.isNotEmpty(proofSealStrategy.getSealText())) {
                if (StringUtil.isEmpty(proofSealStrategy.getWidth())) {
                    proofSealStrategy.setWidth("100");
                }

                if (!StringUtil.isPositiveNumeric(proofSealStrategy.getWidth())) {
                    logger.error(" width 取值错误 ");
                    throw new Exception(" width 取值错误  ");
                }

                if (StringUtil.isEmpty(proofSealStrategy.getHeight())) {
                    proofSealStrategy.setHeight("100");
                }

                if (!StringUtil.isPositiveNumeric(proofSealStrategy.getHeight())) {
                    logger.error(" width 取值错误 ");
                    throw new Exception(" width 取值错误  ");
                }

                if (StringUtil.isEmpty(proofSealStrategy.getFontSize())) {
                    proofSealStrategy.setFontSize("12");
                }

                if (!StringUtil.isPositiveNumber(proofSealStrategy.getFontSize(), 4)) {
                    logger.error("fontSize 取值错误 ");
                    throw new Exception("fontSize 取值错误");
                }

                if (StringUtil.isEmpty(proofSealStrategy.getFontColor())) {
                    proofSealStrategy.setFontColor("000000");
                }

                if (!StringUtil.isColorNumber(proofSealStrategy.getFontColor())) {
                    logger.error("fontColor 取值错误 ");
                    throw new Exception("fontColor 取值错误");
                }
            }

            if (!"RSA".equalsIgnoreCase(proofSealStrategy.getKeyAlg()) && !"SM2".equalsIgnoreCase(proofSealStrategy.getKeyAlg())) {
                logger.error("keyAlg 取值错误，只能是RSA或者SM2");
                throw new Exception("keyAlg 取值错误，只能是RSA或者SM2");
            } else {
                if (StringUtil.isEmpty(proofSealStrategy.getFillOpacity())) {
                    proofSealStrategy.setFillOpacity("1");
                }

                if (!StringUtil.isPositiveNumeric(proofSealStrategy.getFillOpacity())) {
                    logger.error(" fillOpacity 取值错误 ");
                    throw new Exception(" fillOpacity 取值错误 ");
                } else {
                    float fillOpacity = Float.parseFloat(proofSealStrategy.getFillOpacity());
                    if (fillOpacity >= 0.0F && fillOpacity <= 1.0F) {
                        if (!"1".equals(proofSealStrategy.getSealType()) && !"2".equals(proofSealStrategy.getSealType()) && !"3".equals(proofSealStrategy.getSealType())) {
                            logger.error(" sealType 取值错误，取值只能是1 或者 2 或者 3 ");
                            throw new Exception("sealType 取值错误，取值只能是1 或者 2 或者 3");
                        } else {
                            List keywordLocationList;
                            if ("1".equals(proofSealStrategy.getSealType())) {
                                keywordLocationList = proofSealStrategy.getSignatureFieldNameList();
                                if ((keywordLocationList == null || keywordLocationList.size() == 0) && CommonUtil.isEmpty(proofSealStrategy.getSignatureFieldName())) {
                                    logger.error(" signatureFieldName is null ");
                                    throw new Exception("signatureFieldName is null");
                                }
                            }

                            int i;
                            if ("2".equals(proofSealStrategy.getSealType())) {
                                keywordLocationList = proofSealStrategy.getSignLocationList();
                                if (keywordLocationList != null && keywordLocationList.size() != 0) {
                                    for(i = 0; i < keywordLocationList.size(); ++i) {
                                        SignLocation signLocation = (SignLocation)keywordLocationList.get(i);
                                        if (CommonUtil.isEmpty(signLocation.getPageNo())) {
                                            logger.error(" pageNo is null ");
                                            throw new Exception("第 " + (i + 1) + " pageNo is null");
                                        }

                                        if (!StringUtil.isNaturalNumber(signLocation.getPageNo(), 6)) {
                                            logger.error(" pageNo 取值错误 ");
                                            throw new Exception("第 " + (i + 1) + " pageNo 取值错误  ");
                                        }

                                        if (StringUtil.isEmpty(signLocation.getLx())) {
                                            signLocation.setLx("0");
                                        }

                                        if (!StringUtil.isPositiveNumeric(signLocation.getLx())) {
                                            logger.error("lx 取值错误 ");
                                            throw new Exception("第 " + (i + 1) + " lx 取值错误");
                                        }

                                        if (StringUtil.isEmpty(signLocation.getLy())) {
                                            signLocation.setLy("0");
                                        }

                                        if (!StringUtil.isPositiveNumeric(signLocation.getLy())) {
                                            logger.error("ly 取值错误 ");
                                            throw new Exception("第 " + (i + 1) + " ly 取值错误");
                                        }
                                    }
                                } else {
                                    if (CommonUtil.isEmpty(proofSealStrategy.getPageNo())) {
                                        logger.error(" pageNo is null ");
                                        throw new Exception("pageNo is null");
                                    }

                                    if (!StringUtil.isNaturalNumber(proofSealStrategy.getPageNo(), 6)) {
                                        logger.error(" pageNo 取值错误 ");
                                        throw new Exception(" pageNo 取值错误  ");
                                    }

                                    if (StringUtil.isEmpty(proofSealStrategy.getLx())) {
                                        proofSealStrategy.setLx("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(proofSealStrategy.getLx())) {
                                        logger.error("lx 取值错误 ");
                                        throw new Exception("lx 取值错误");
                                    }

                                    if (StringUtil.isEmpty(proofSealStrategy.getLy())) {
                                        proofSealStrategy.setLy("0");
                                    }

                                    if (!StringUtil.isPositiveNumeric(proofSealStrategy.getLy())) {
                                        logger.error("ly 取值错误 ");
                                        throw new Exception("ly 取值错误");
                                    }
                                }
                            }

                            if ("3".equals(proofSealStrategy.getSealType())) {
                                keywordLocationList = proofSealStrategy.getKeywordLocationList();
                                if (keywordLocationList != null && keywordLocationList.size() != 0) {
                                    for(i = 0; i < keywordLocationList.size(); ++i) {
                                        KeywordLocation keywordLocaton = (KeywordLocation)keywordLocationList.get(i);
                                        if (StringUtil.isEmpty(keywordLocaton.getPageNo())) {
                                            keywordLocaton.setPageNo("0");
                                        }

                                        if (!StringUtil.isNaturalNumber(keywordLocaton.getPageNo(), 6)) {
                                            logger.error(" pageNo 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " pageNo 取值错误  ");
                                        }

                                        if (CommonUtil.isEmpty(keywordLocaton.getKeyword())) {
                                            logger.error(" keyword is null ");
                                            throw new Exception("第" + (i + 1) + " keyword is null ");
                                        }

                                        if (StringUtil.isEmpty(keywordLocaton.getOffsetX())) {
                                            keywordLocaton.setOffsetX("0");
                                        }

                                        if (!StringUtil.isNumeric(keywordLocaton.getOffsetX())) {
                                            logger.error("offsetX 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " offsetX 取值错误");
                                        }

                                        if (StringUtil.isEmpty(keywordLocaton.getOffsetY())) {
                                            keywordLocaton.setOffsetY("0");
                                        }

                                        if (!StringUtil.isNumeric(keywordLocaton.getOffsetY())) {
                                            logger.error("offsetY 取值错误 ");
                                            throw new Exception("第" + (i + 1) + " offsetY 取值错误");
                                        }
                                    }
                                } else {
                                    if (StringUtil.isEmpty(proofSealStrategy.getPageNo())) {
                                        proofSealStrategy.setPageNo("0");
                                    }

                                    if (!StringUtil.isNaturalNumber(proofSealStrategy.getPageNo(), 6)) {
                                        logger.error(" pageNo 取值错误 ");
                                        throw new Exception(" pageNo 取值错误  ");
                                    }

                                    if (CommonUtil.isEmpty(proofSealStrategy.getKeyword())) {
                                        logger.error(" keyword is null ");
                                        throw new Exception("keyword is null ");
                                    }

                                    if (StringUtil.isEmpty(proofSealStrategy.getOffsetX())) {
                                        proofSealStrategy.setOffsetX("0");
                                    }

                                    if (!StringUtil.isNumeric(proofSealStrategy.getOffsetX())) {
                                        logger.error("offsetX 取值错误 ");
                                        throw new Exception("offsetX 取值错误");
                                    }

                                    if (StringUtil.isEmpty(proofSealStrategy.getOffsetY())) {
                                        proofSealStrategy.setOffsetY("0");
                                    }

                                    if (!StringUtil.isNumeric(proofSealStrategy.getOffsetY())) {
                                        logger.error("offsetY 取值错误 ");
                                        throw new Exception("offsetY 取值错误");
                                    }
                                }
                            }

                            if ("1".equals(proofSealStrategy.getIsAddDateText())) {
                                if (StringUtil.isEmpty(proofSealStrategy.getDateFontSize())) {
                                    proofSealStrategy.setDateFontSize("12");
                                }

                                if (!StringUtil.isPositiveNumber(proofSealStrategy.getDateFontSize(), 4)) {
                                    logger.error("dateFontSize 取值错误 ");
                                    throw new Exception("dateFontSize 取值错误");
                                }

                                if (StringUtil.isEmpty(proofSealStrategy.getDateFontColor())) {
                                    proofSealStrategy.setDateFontColor("000000");
                                }

                                if (!StringUtil.isColorNumber(proofSealStrategy.getDateFontColor())) {
                                    logger.error("dateFontColor 取值错误 ");
                                    throw new Exception("dateFontColor 取值错误");
                                }

                                if (StringUtil.isEmpty(proofSealStrategy.getDateRectHeight())) {
                                    proofSealStrategy.setDateRectHeight("40");
                                }

                                if (!StringUtil.isPositiveNumeric(proofSealStrategy.getDateRectHeight())) {
                                    logger.error("dateRectHeight 取值错误 ");
                                    throw new Exception("dateRectHeight 取值错误");
                                }

                                if (StringUtil.isEmpty(proofSealStrategy.getDateFontFamily())) {
                                    proofSealStrategy.setDateFontFamily("宋体");
                                }
                            }

                        }
                    } else {
                        logger.error(" fillOpacity 取值错误 ");
                        throw new Exception(" fillOpacity 取值错误 ");
                    }
                }
            }
        }
    }
}
