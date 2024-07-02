//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;


import cfca.paperless.base.util.ProofHashUtil;
import cfca.paperless.base.util.StringUtil;
import cfca.paperless.base.util.validity.ValidityUtil;
import cfca.paperless.client.util.JsonUtil;
import cfca.paperless.dto.RequestDto;
import cfca.paperless.dto.ResponseHead;
import cfca.paperless.dto.request.requestbody.tx30.TransformWordToPdfRequestBody;
import cfca.paperless.dto.request.tx30.TransformWordToPdfRequest;
import cfca.paperless.dto.response.responsebody.tx30.TransformWordToPdfResponseBody;
import cfca.paperless.dto.response.tx30.TransformWordToPdfResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Tx30Transaction extends AbstractTransaction {
    public static final Logger logger = LoggerFactory.getLogger(Tx30Transaction.class);
    private ValidateUtil validateUtil=new ValidateUtil();
    public Tx30Transaction() {
    }


    public TransformWordToPdfResponse doTx3002(HttpConnector httpConnector, RequestDto requestDto,String url) throws Exception {
        logger.info("Tx10Transaction.doTx3002 start.");
        TransformWordToPdfRequest requestBean = (TransformWordToPdfRequest)requestDto;
        TransformWordToPdfRequestBody requestBody = requestBean.getBody();
        List<byte[]> inputFileDataList = new ArrayList();
        validateUtil.doCheckTx3002(requestBody, inputFileDataList);
        logger.info("Tx10Transaction.doTx3002 doCheckTx3002 finish.");
        List<byte[]> outputFileDataList = new ArrayList();
        String responseJSONString = super.handleRequest(httpConnector, requestDto, inputFileDataList, outputFileDataList,url);
        logger.info("Tx10Transaction.doTx3002 handleRequest finish.");
        TransformWordToPdfResponse responseBean = this.parseTx3002Result(responseJSONString, outputFileDataList);
        logger.info("Tx10Transaction.doTx3002 parseTx3002Result finish.");
        logger.info("Tx10Transaction.doTx3002 end.");
        return responseBean;
    }

    private TransformWordToPdfResponse parseTx3002Result(String responseJSONString, List<byte[]> outputFileDataList) throws Exception {
        TransformWordToPdfResponse responseBean = (TransformWordToPdfResponse)JsonUtil.json2Obj(responseJSONString, TransformWordToPdfResponse.class);
        ResponseHead head = responseBean.getHead();
        TransformWordToPdfResponseBody body = responseBean.getBody();
        if ("000000".equals(head.getCode()) && StringUtil.isEmpty(body.getOutputFilepath())) {
            byte[] pdf = (byte[])outputFileDataList.get(0);
            String pdfFileDataHash = body.getPdfFileDataHash();
            this.checkAndComparePdf(pdf, pdfFileDataHash);
            body.setPdf(pdf);
        }

        return responseBean;
    }


    private void checkAndComparePdf(byte[] pdf, String resultPdfFileDataHash) throws Exception {
        ValidityUtil.checkIsPdfFile(pdf);
        if (StringUtil.isNotEmpty(resultPdfFileDataHash)) {
            boolean checkResult = ProofHashUtil.compareHashHex(pdf, resultPdfFileDataHash);
            if (!checkResult) {
                System.err.println("pdf hash compare resultPdfFileDataHash is error.");
                throw new Exception("pdf hash compare resultPdfFileDataHash is error.");
            }
        }

    }
}
