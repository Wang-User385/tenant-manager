//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.hand.hls.elecSeal.utils;


import cfca.paperless.client.util.CommonUtil;
import cfca.paperless.client.util.JsonUtil;
import cfca.paperless.dto.RequestDto;
import cfca.paperless.dto.RequestHead;
import cfca.paperless.dto.ResponseDto;
import cfca.paperless.dto.TransType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaperlessClient {
    public static final Logger logger = LoggerFactory.getLogger(PaperlessClient.class);
    private static final String PROTOCOL_HTTP = "http://";
    private static final String PROTOCOL_HTTPS = "https://";
    private static final String CONTEXT_PATH = "/hitf/v2p/rest/invoke/R0RIQ0c6Q0ZDQS5TSUdOQVRVUkU6c2VhbFBkZg==?access_token=";
    private String host = "localhost";
    private String port = "8183";
    private int connectTimeout = 3000;
    private int readTimeout = 10000;
    private boolean isSSL = false;
    private String sslProtocol = "TLSv1.2";
    private String keyStorePath = "";
    private String keyStorePassword = "";
    private String trustStorePath = "";
    private String trustStorePassword = "";
    @Autowired
    private ValidateUtil validateUtil;
    @Autowired
    private HttpConnector httpConnector;

    public PaperlessClient() {
    }

    public PaperlessClient(String host, String port, int connectTimeout, int readTimeout) {
        this.host = host;
        this.port = port;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public ResponseDto execute(RequestDto requestDto) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("PaperlessClient.execute start.");
        String txCode = requestDto.getTxCode();
        if (CommonUtil.isEmpty(txCode)) {
            logger.error("invalid txCode [" + txCode + "]");
            throw new Exception("invalid txCode [" + txCode + "]");
        } else {
            TransType transType = TransType.getTransType(txCode);
            if (transType == null) {
                logger.error("invalid txCode [" + txCode + "]");
                throw new Exception("invalid txCode [" + txCode + "]");
            } else {
                logger.info("txCode={} description={}", txCode, transType.getDescription());
                RequestHead requestHead = requestDto.getHead();
                if (requestHead == null) {
                    logger.error("invalid RequestDto.RequestHead ");
                    throw new Exception("invalid RequestDto.RequestHead ");
                } else {
                    validateUtil.checkRequestHead(requestHead);
                    String transactionNo = requestHead.getTransactionNo();
                    logger.info("transactionNo={}", transactionNo);
                    ResponseDto responseDto = this.dispatch(transType, requestDto);
                    long end = System.currentTimeMillis();
                    logger.info("PaperlessClient.execute end. time-taken={}ms", end - start);
                    return responseDto;
                }
            }
        }
    }

    private String generateUrlString(String appendUrl) {
        String uri="";
        if("/Tx4001".equals(appendUrl)){
            uri="https://apitest-gateway.gdhcapital.com.cn/hitf/v2/rest/invoke?namespace=GDHCG&serverCode=CFCA.SIGNATURE&interfaceCode=sealPdf";
        }else{
            uri="https://apitest-gateway.gdhcapital.com.cn/hitf/v2/rest/invoke?namespace=GDHCG&serverCode=CFCA.TRANSFORM&interfaceCode=wordToPdf.transform";
        }

        return uri;
    }

    private ResponseDto dispatch(TransType transType, RequestDto requestDto) throws Exception {
        long start = System.currentTimeMillis();
        String txCode = transType.getTxCode();
        String httpMethod = transType.getHttpMethod();
        String appendUrl = transType.getAppendUrl();
        String description = transType.getDescription();
        boolean var22 = false;

        Object var13;
        try {
            String uri;
            try {
                var22 = true;
                String transactionNo = requestDto.getHead().getTransactionNo();
                MDC.put("ClientTraceId", transactionNo);
                logger.info("PaperlessClient.dispatch start. txCode={} description={} transactionNo={}", new Object[]{txCode, description, transactionNo});
                uri = this.generateUrlString(appendUrl);
                logger.info("uri={} httpMethod={} connectTimeout={} readTimeout={}", new Object[]{uri, httpMethod, this.connectTimeout, this.readTimeout});
                if (this.isSSL) {
                    logger.info("sslProtocol={} keyStorePath={} trustStorePath={}", new Object[]{this.sslProtocol, this.keyStorePath, this.trustStorePath});
                }
                httpConnector.url=uri;
                httpConnector.setSSLConfig(this.isSSL, this.sslProtocol, this.keyStorePath, this.keyStorePassword, this.trustStorePath, this.trustStorePassword);
                httpConnector.init();
                ResponseDto responseDto = null;
                switch(transType) {
                case Tx3002:
                    responseDto = (new Tx30Transaction()).doTx3002(httpConnector, requestDto,uri);
                    break;
                case Tx4001:
                    responseDto = (new Tx40Transaction()).doTx4001(httpConnector, requestDto,uri);
                    break;
                default:
                    throw new Exception("undefined txcode");
                }

                var13 = responseDto;
                var22 = false;
            } catch (Exception var23) {
                logger.error("PaperlessClient.dispatch Exception.", var23);
                logger.error("RequestDto=[{}]", JsonUtil.obj2Json(requestDto));
                uri = var23.getMessage();
                throw new Exception(uri);
            } catch (Error var24) {
                logger.error("PaperlessClient.dispatch Error.", var24);
                logger.error("RequestDto=[{}]", JsonUtil.obj2Json(requestDto));
                uri = var24.getMessage();
                throw new Exception("PaperlessClient.dispatch Error.[" + uri + "]");
            }
        } finally {
            if (var22) {
                long end = System.currentTimeMillis();
                logger.info("PaperlessClient.dispatch end. txCode={} time-taken={}ms", txCode, end - start);
                MDC.remove("ClientTraceId");
            }
        }

        long end = System.currentTimeMillis();
        logger.info("PaperlessClient.dispatch end. txCode={} time-taken={}ms", txCode, end - start);
        MDC.remove("ClientTraceId");
        return (ResponseDto)var13;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return this.readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isSSL() {
        return this.isSSL;
    }

    public void setSSL(boolean isSSL) {
        this.isSSL = isSSL;
    }

    public String getSslProtocol() {
        return this.sslProtocol;
    }

    public void setSslProtocol(String sslProtocol) {
        this.sslProtocol = sslProtocol;
    }

    public String getKeyStorePath() {
        return this.keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getTrustStorePath() {
        return this.trustStorePath;
    }

    public void setTrustStorePath(String trustStorePath) {
        this.trustStorePath = trustStorePath;
    }

    public String getTrustStorePassword() {
        return this.trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }
}
