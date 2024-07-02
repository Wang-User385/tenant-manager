package com.hand.hls.elecSeal.utils;
import cfca.paperless.client.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class FormDataConnectorUtils {
    public  byte[] File2byte(File filePath) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }


    /**
     * 从连接池中获取连接的超时时间--10s
     */
    private  int connectionRequestTimeout = 10000;
    /**
     * 客户端和服务器建立连接的超时时间--握手连接时间--10s
     */
    private  int connectTimeout = 60000;
    /**
     * 从对方服务接受响应流的时间
     */
    private  int socketTimeout = 60000;
    /**
     * 连接池最大连接数
     */
    private  int maxTotal = 800;
    /**
     * 每个主机的并发
     */
    private  int maxPerRoute = 20;
    private  PoolingHttpClientConnectionManager connectionManager = null;
    private  CloseableHttpClient httpClient;

    public  CloseableHttpClient getClient() {
        return httpClient;
    }

     {
        log.info("初始化http connection 连接池 ...");
        try {
            // 配置同时支持 HTTP 和 HTPPS
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(builder.build());
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslConnectionSocketFactory).build();
            connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } catch (Exception e) {
            log.error("初始化http 连接池异常", e);
            connectionManager = new PoolingHttpClientConnectionManager();
        }
        //连接池统一配置
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(maxPerRoute);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setConnectionRequestTimeout(connectionRequestTimeout).setSocketTimeout(socketTimeout).build();

        //不做重试功能
        HttpRequestRetryHandler retryHandler = new DefaultHttpRequestRetryHandler(0, false);

        httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).setRetryHandler(retryHandler).build();

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            connectionManager.closeExpiredConnections();
            connectionManager.closeIdleConnections(20, TimeUnit.SECONDS);
            log.info("回收过期的http连接完成 status：{}", connectionManager.getTotalStats());
        }, 30, 120, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("关闭 httpClient 连接");
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                log.error("关闭 httpClient 异常", e);
            }
        }));
    }

    /**
     * post请求提交form-data上传文件
     *
     * @param url
     * @param headers 请求头
     * @return
     */
    public  byte[] doPostFile(String url, Map<String, String> headers, File file) {
        HttpPost httpPost = new HttpPost(url);
        packageHeader(headers, httpPost);
        String fileName = file.getName();

        CloseableHttpResponse response = null;

        byte[] respContent = null;

        long startTime = System.currentTimeMillis();

        // 设置请求头 boundary边界不可重复，重复会导致提交失败
        String boundary = "-------------------------" + UUID.randomUUID().toString();
        httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        // 创建MultipartEntityBuilder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置字符编码
        builder.setCharset(StandardCharsets.UTF_8);
        // 模拟浏览器
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // 设置边界
        builder.setBoundary(boundary);
        // 设置multipart/form-data流文件
        builder.addPart("multipartFile", new FileBody(file));
        // application/octet-stream代表不知道是什么格式的文件
        builder.addBinaryBody("media", file, ContentType.create("application/octet-stream"), fileName);

        builder.addTextBody("mediaType", "application/octet-stream");
        builder.addTextBody("headerParamMap['TraceId']", "202208182209551126275507319096");
        builder.addTextBody("headerParamMap['jsonLength']", "36627");
        builder.addTextBody("headerParamMap['fileDataLength']", "87721");

        HttpEntity entity = builder.build();
        httpPost.setEntity(entity);

        try {
            response = httpClient.execute(httpPost);
            if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() < 400) {
                HttpEntity he = response.getEntity();
                if (he != null) {
                    respContent = toByteArray(he.getContent());
                    //respContent = EntityUtils.toString(he, "UTF-8");
                }
                Header[] headers1 = response.getAllHeaders();
                Map<String, String> responseHttpHeadersMap = new HashMap<>();
                if (null != headers) {
                    for (Header header : headers1) {
                        responseHttpHeadersMap.put(header.getName(), header.getValue());
                    }
                }
                log.info(String.valueOf(responseHttpHeadersMap));
            } else {
                log.error("对方响应的状态码不在符合的范围内!");
                throw new RuntimeException();
            }
            return respContent;
        } catch (Exception e) {
            log.error("网络访问异常,请求url地址={},响应体={},error={}", url, response, e);
            throw new RuntimeException();
        } finally {
            log.info("统一外网请求参数打印,post请求url地址={},响应={},耗时={}毫秒", url, respContent, (System.currentTimeMillis() - startTime));
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("请求链接释放异常", e);
            }
        }
    }

    /**
     * post请求提交form-data上传文件
     *
     * @param url
     * @param headers 请求头
     * @return
     */
    public  byte[] doPostFile(String url, Map<String, String> headers, File file,Map<String, String> requestHttpHeadersMap,Map<String, String> responseHttpHeadersMap) {
        log.info("doPostFile方法:--------------------------------------");
        HttpPost httpPost = new HttpPost(url);
        log.info("httpPost设置---------------");
        packageHeader(headers, httpPost);
        String fileName = file.getName();

        CloseableHttpResponse response = null;

        byte[] respContent = null;

        long startTime = System.currentTimeMillis();

        // 设置请求头 boundary边界不可重复，重复会导致提交失败
        String boundary = "-------------------------" + UUID.randomUUID().toString();
        httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

        // 创建MultipartEntityBuilder
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        // 设置字符编码
        builder.setCharset(StandardCharsets.UTF_8);
        // 模拟浏览器
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // 设置边界
        builder.setBoundary(boundary);
        // 设置multipart/form-data流文件
        builder.addPart("multipartFile", new FileBody(file));
        // application/octet-stream代表不知道是什么格式的文件
        builder.addBinaryBody("media", file, ContentType.create("application/octet-stream"), fileName);

        builder.addTextBody("mediaType", "application/octet-stream");
        builder.addTextBody("headerParamMap['TraceId']", requestHttpHeadersMap.get("TraceId"));
        builder.addTextBody("headerParamMap['jsonLength']",  requestHttpHeadersMap.get("jsonLength"));
        builder.addTextBody("headerParamMap['fileDataLength']", requestHttpHeadersMap.get("fileDataLength"));

        HttpEntity entity = builder.build();
        log.info("请求参数设置成功");
        httpPost.setEntity(entity);

        try {
            response = httpClient.execute(httpPost);
            if (response != null && response.getStatusLine() != null && response.getStatusLine().getStatusCode() < 400) {
                HttpEntity he = response.getEntity();
                if (he != null) {
                    respContent = toByteArray(he.getContent());
                    //respContent = EntityUtils.toString(he, "UTF-8");
                }
                Header[] headers1 = response.getAllHeaders();
                if (null != headers) {
                    for (Header header : headers1) {
                        responseHttpHeadersMap.put(header.getName(), header.getValue());
                    }
                }
                log.info(String.valueOf(responseHttpHeadersMap));
            } else {
                log.error("对方响应的状态码不在符合的范围内!");
                throw new RuntimeException();
            }
            return respContent;
        } catch (Exception e) {
            log.error("网络访问异常,请求url地址={},响应体={},error={}", url, response, e);
            throw new RuntimeException();
        } finally {
            log.info("统一外网请求参数打印,post请求url地址={},响应={},耗时={}毫秒", url, respContent, (System.currentTimeMillis() - startTime));
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("请求链接释放异常", e);
            }
        }
    }

    public  byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 封装请求头
     *
     * @param paramsHeads
     * @param httpMethod
     */
    public  void packageHeader(Map<String, String> paramsHeads, HttpRequestBase httpMethod) {
        if (null != paramsHeads) {
            Set<Map.Entry<String, String>> entrySet = paramsHeads.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                httpMethod.setHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private void parseHttpHeaders(Map<String, String> responseHttpHeadersMap, Header[] headers) {
        if (null != headers) {
            for (Header header : headers) {
                responseHttpHeadersMap.put(header.getName(), header.getValue());
            }
        }

    }
}
