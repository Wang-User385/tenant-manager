//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;


import cfca.paperless.client.util.CommonUtil;
import cfca.paperless.client.util.JsonUtil;
import cfca.paperless.dto.RequestDto;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


public abstract class AbstractTransaction {
    public static final Logger logger = LoggerFactory.getLogger(AbstractTransaction.class);

    public AbstractTransaction() {
    }

    public String handleRequest(HttpConnector httpConnector, RequestDto requestDto, List<byte[]> inputFileDataList, List<byte[]> outputFileDataList,String url) throws Exception {
        logger.info("AbstractTransaction.handleRequest start.");
        String requestJSONString = JsonUtil.obj2Json(requestDto);
        logger.debug("requestJSONString=[{}]", requestJSONString);
        byte[] requestJSONData = requestJSONString.getBytes("UTF-8");
        String requestJSONLengthString = String.valueOf(requestJSONData == null ? 0 : requestJSONData.length);
        logger.info("requestJSONLengthString={}", requestJSONLengthString);
        byte[] requestFileData = new byte[0];
        String requestFileDataLengthString = "";
        byte[] fileData;
        long requestFileDataLength;
        if (CommonUtil.isNotEmpty(inputFileDataList)) {
            int listSize = inputFileDataList.size();
            logger.info("inputFileDataList.size={}", listSize);

            for(int i = 0; i < listSize; ++i) {
                fileData = (byte[])inputFileDataList.get(i);
                requestFileData = ArrayUtils.addAll(requestFileData, fileData);
                requestFileDataLength = fileData == null ? 0L : (long)fileData.length;
                logger.info("inputFileDataList index={} requestFileDataLength={}", i, requestFileDataLength);
                requestFileDataLengthString = requestFileDataLengthString + requestFileDataLength;
                if (i < listSize - 1) {
                    requestFileDataLengthString = requestFileDataLengthString + "||";
                }
            }
        }

        logger.info("requestFileData.all.length={}", requestFileData.length);
        logger.info("requestFileDataLengthString={}", requestFileDataLengthString);
        Map<String, String> requestHttpHeadersMap = new LinkedHashMap();
        requestHttpHeadersMap.put("jsonLength", requestJSONLengthString);
        requestHttpHeadersMap.put("fileDataLength", requestFileDataLengthString);
        requestHttpHeadersMap.put("TraceId", MDC.get("ClientTraceId"));
        Map<String, String> responseHttpHeadersMap = new LinkedHashMap();
        responseHttpHeadersMap.put("jsonLength", (String) null);
        responseHttpHeadersMap.put("fileDataLength", (String) null);
        fileData = ArrayUtils.addAll(requestJSONData, requestFileData);
        logger.info("requestHttpBodyData.length={}", fileData.length);
        requestFileDataLength = System.currentTimeMillis();
        byte[] responseHttpBodyData = httpConnector.process(requestHttpHeadersMap, fileData, responseHttpHeadersMap,url);
        long end = System.currentTimeMillis();
        logger.info("httpConnector.process finish. time-taken={}ms. responseHttpBodyData.length={}", end - requestFileDataLength, responseHttpBodyData.length);
        String responseJSONLengthString = (String)responseHttpHeadersMap.get("jsonLength");
        logger.info("responseJSONLengthString={}", responseJSONLengthString);
        String responseFileDataLenthString = (String)responseHttpHeadersMap.get("fileDataLength");
        logger.info("responseFileDataLenthString={}", responseFileDataLenthString);
        byte[] responseJSONData = new byte[Integer.parseInt(responseJSONLengthString)];
        System.arraycopy(responseHttpBodyData, 0, responseJSONData, 0, Integer.parseInt(responseJSONLengthString));
        String responseJSONString = new String(responseJSONData, "UTF-8");
        logger.debug("responseJSONString=[{}]", responseJSONString);
        if (CommonUtil.isNotEmpty(responseFileDataLenthString)) {
            String[] fileDataLengthStrGroup = responseFileDataLenthString.split("\\|\\|");
            if (fileDataLengthStrGroup != null && fileDataLengthStrGroup.length > 0) {
                int position = Integer.parseInt(responseJSONLengthString);
                int listSize = fileDataLengthStrGroup.length;
                logger.info("outputFileDataList.size={}", listSize);

                for(int i = 0; i < listSize; ++i) {
                    String fileDataLengthStr = fileDataLengthStrGroup[i];
                    int fileDataLength = Integer.parseInt(fileDataLengthStr);
                    byte[] bodyBuffer = new byte[fileDataLength];
                    System.arraycopy(responseHttpBodyData, position, bodyBuffer, 0, fileDataLength);
                    logger.info("outputFileDataList index={} responseFileDataLength={}", i, fileDataLength);
                    outputFileDataList.add(bodyBuffer);
                    position += fileDataLength;
                }
            }
        }

        return responseJSONString;
    }
}
