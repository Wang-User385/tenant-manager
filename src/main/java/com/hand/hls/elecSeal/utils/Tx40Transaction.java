//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;


import cfca.paperless.base.util.JsonUtil;
import cfca.paperless.base.util.StringUtil;
import cfca.paperless.base.util.validity.ValidityUtil;
import cfca.paperless.dto.RequestDto;
import cfca.paperless.dto.ResponseHead;
import cfca.paperless.dto.bean.PdfBean4Response;
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
import cfca.paperless.dto.request.tx40.ApplyAndDownloadCertRequest;
import cfca.paperless.dto.request.tx40.CalculatePdfHashRequest;
import cfca.paperless.dto.request.tx40.CompoundSealPdfListDetachedRequest;
import cfca.paperless.dto.request.tx40.CompoundSealPdfListRequest;
import cfca.paperless.dto.request.tx40.CompoundSealPdfRequest;
import cfca.paperless.dto.request.tx40.CrossPdfRequest;
import cfca.paperless.dto.request.tx40.CrossSealPdfRequest;
import cfca.paperless.dto.request.tx40.MessageSealRequest;
import cfca.paperless.dto.request.tx40.RevokePdfSealRequest;
import cfca.paperless.dto.request.tx40.SealOfdRequest;
import cfca.paperless.dto.request.tx40.SealPdfRequest;
import cfca.paperless.dto.request.tx40.SignPdfHashRequest;
import cfca.paperless.dto.request.tx40.SignPdfProofHashRequest;
import cfca.paperless.dto.request.tx40.SynthesizeOuterSignatureAndSealPdfRequest;
import cfca.paperless.dto.request.tx40.SynthesizeOuterSignatureRequest;
import cfca.paperless.dto.request.tx40.VerifyMessageSealRequest;
import cfca.paperless.dto.request.tx40.VerifyOfdSealRequest;
import cfca.paperless.dto.request.tx40.VerifyPdfSealRequest;
import cfca.paperless.dto.request.tx40.VerifyWebSealRequest;
import cfca.paperless.dto.request.tx40.WebSealRequest;
import cfca.paperless.dto.response.responsebody.tx40.CompoundSealPdfListDetachedResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.CompoundSealPdfListResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.CompoundSealPdfResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.RevokePdfSealResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.SealOfdResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.SealPdfResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.SynthesizeOuterSignatureAndSealPdfResponseBody;
import cfca.paperless.dto.response.responsebody.tx40.SynthesizeOuterSignatureResponseBody;
import cfca.paperless.dto.response.tx40.ApplyAndDownloadCertResponse;
import cfca.paperless.dto.response.tx40.CalculatePdfHashResponse;
import cfca.paperless.dto.response.tx40.CompoundSealPdfListDetachedResponse;
import cfca.paperless.dto.response.tx40.CompoundSealPdfListResponse;
import cfca.paperless.dto.response.tx40.CompoundSealPdfResponse;
import cfca.paperless.dto.response.tx40.MessageSealResponse;
import cfca.paperless.dto.response.tx40.RevokePdfSealResponse;
import cfca.paperless.dto.response.tx40.SealOfdResponse;
import cfca.paperless.dto.response.tx40.SealPdfResponse;
import cfca.paperless.dto.response.tx40.SignPdfHashResponse;
import cfca.paperless.dto.response.tx40.SynthesizeOuterSignatureAndSealPdfResponse;
import cfca.paperless.dto.response.tx40.SynthesizeOuterSignatureResponse;
import cfca.paperless.dto.response.tx40.VerifyPdfSealResponse;
import cfca.paperless.dto.response.tx40.VerifyWebSealResponse;
import cfca.paperless.dto.response.tx40.WebSealResponse;
import cfca.sadk.org.bouncycastle.jce.provider.BouncyCastleProvider;
import cfca.sadk.seal.base.bean.sign.SealVerifyResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.security.Security;
import java.util.ArrayList;
import java.util.List;
@Component
public class Tx40Transaction extends AbstractTransaction {
    public static final Logger logger = LoggerFactory.getLogger(Tx40Transaction.class);
    public static final BouncyCastleProvider provider = new BouncyCastleProvider();
    private ProofHashUtil proofHashUtil=new ProofHashUtil();
    private ValidateUtil validateUtil=new ValidateUtil();
    public Tx40Transaction() {
    }

    public SealPdfResponse doTx4001(HttpConnector httpConnector, RequestDto requestDto,String url) throws Exception {
        logger.info("Tx40Transaction.doTx4001 start.");
        SealPdfRequest requestBean = (SealPdfRequest)requestDto;
        List<byte[]> fileDataList = new ArrayList();
        SealPdfRequestBody requestBody = requestBean.getBody();
        validateUtil.doCheckTx4001(requestBody, fileDataList);
        logger.info("Tx40Transaction.doTx4001 doCheckTx4001 finish.");
        List<byte[]> outputFileDataList = new ArrayList();
        String responseJSONString = super.handleRequest(httpConnector, requestBean, fileDataList, outputFileDataList,url);
        logger.info("Tx40Transaction.doTx4001 handleRequest finish.");
        SealPdfResponse responseBean = this.parseTx4001Result(responseJSONString, outputFileDataList);
        logger.info("Tx40Transaction.doTx4001 parseTx4001Result finish.");
        logger.info("Tx40Transaction.doTx4001 end.");
        return responseBean;
    }



    private SealPdfResponse parseTx4001Result(String responseJSONString, List<byte[]> outputFileDataList) throws Exception {
        SealPdfResponse responseBean = (SealPdfResponse)JsonUtil.json2Obj(responseJSONString, SealPdfResponse.class);
        ResponseHead responseHead = responseBean.getHead();
        if ("000000".equals(responseHead.getCode())) {
            SealPdfResponseBody body = responseBean.getBody();
            if (outputFileDataList != null && outputFileDataList.size() > 0) {
                byte[] pdf = (byte[])outputFileDataList.get(0);
                String resultPdfFileDataHash = body.getPdfFileDataHash();
                this.checkAndVerifyPdf(pdf, resultPdfFileDataHash);
                body.setPdf(pdf);
            }
        }

        return responseBean;
    }

    private void checkAndVerifyPdf(byte[] pdf, String resultPdfFileDataHash) throws Exception {
        if (StringUtil.isNotEmpty(resultPdfFileDataHash)) {
            boolean checkResult = proofHashUtil.compareHashHex(pdf, resultPdfFileDataHash);
            if (!checkResult) {
                System.err.println("pdf hash compare resultPdfFileDataHash is error.");
                throw new Exception("pdf hash compare resultPdfFileDataHash is error.");
            }
        }

        ValidityUtil.checkIsPdfFile(pdf);
        SealVerifyResult verifyResult = VerifyUtil.verify(pdf);
        if (!verifyResult.getVerifyResult()) {
            logger.error("pdf verify error" + verifyResult.getFailReason());
            throw new Exception("pdf verify error" + verifyResult.getFailReason());
        }
    }

    private void checkAndComparePdf(byte[] pdf, String resultPdfFileDataHash) throws Exception {
        ValidityUtil.checkIsPdfFile(pdf);
        if (StringUtil.isNotEmpty(resultPdfFileDataHash)) {
            boolean checkResult = proofHashUtil.compareHashHex(pdf, resultPdfFileDataHash);
            if (!checkResult) {
                System.err.println("pdf hash compare resultPdfFileDataHash is error.");
                throw new Exception("pdf hash compare resultPdfFileDataHash is error.");
            }
        }

    }

    private void checkAndCompareOfd(byte[] ofd, String resultOfdFileDataHash) throws Exception {
        if (StringUtil.isNotEmpty(resultOfdFileDataHash)) {
            boolean checkResult = proofHashUtil.compareHashHex(ofd, resultOfdFileDataHash);
            if (!checkResult) {
                System.err.println("ofd hash compare resultOfdFileDataHash is error.");
                throw new Exception("ofd hash compare resultOfdFileDataHash is error.");
            }
        }

    }

    static {
        Security.addProvider(provider);
    }
}
