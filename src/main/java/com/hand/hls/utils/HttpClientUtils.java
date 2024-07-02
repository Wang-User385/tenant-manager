package com.hand.hls.utils;


import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpClientUtils {
    private static final int MAX_TOTAL_CONN = 600;
    private static final int MAX_CONN_PER_HOST = 300;
    private static final int SOCKET_TIMEOUT = 60000;
    private static final int CONNECTION_TIMEOUT = 60000;
    private static final int CONNECTION_MANAGER_TIMEOUT = 10000;

    private static final String EMPTY_STRING = "";
    private static final String PARAM_SEPARATOR = "&";
    private static final String PARAM_KV_SEPARATOR = "=";
    private static final String URI_PARAM_SEPARATOR = "?";

    public static final String APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_JSON_URLENCODED = "application/json";
    private static CloseableHttpClient httpclient;
    private static PoolingHttpClientConnectionManager connMrg;

    /**
     * 设置定时任务清理连接
     */
    private static final ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(2);

    static {
        init();
        destroyByJvmExit();
    }

    private static void init() {
        //带链接池的httpClient比较适合这个http后端的机器数不大的情况下，将kepp alive设置的大一些，保持并发长链接
        connMrg = new PoolingHttpClientConnectionManager();
        connMrg.setMaxTotal(MAX_TOTAL_CONN); // 最大连接数
        connMrg.setDefaultMaxPerRoute(MAX_CONN_PER_HOST);//每个路由基础的连接
//        connMrg.closeExpiredConnections();//关闭超过连接保持时间的连接，并从池中移除。一次性接口，官方建议其一个新线程进行周期行调用
//        connMrg.closeIdleConnections(30, TimeUnit.SECONDS);//该方法关闭空闲时间超过timeout的连接，空闲时间从交还给连接池时开始，不管是否已过期，超过空闲时间则关闭。一次性接口，官方建议其一个新线程进行周期行调用
//        //设置到某个路由的最大连接数，会覆盖defaultMaxPerRoute
//        connMrg.setMaxPerRoute(new HttpRoute(new HttpHost("www.yeetrack.com", 80)), 50);

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECTION_TIMEOUT)//设置连接超时时间，单位毫秒。
                .setSocketTimeout(SOCKET_TIMEOUT)//请求获取数据的超时时间，单位毫秒
                .setConnectionRequestTimeout(CONNECTION_MANAGER_TIMEOUT)//设置从连接池获取连接超时时间，单位毫秒
//                .setStaleConnectionCheckEnabled(true)//检查是否为陈旧的连接，默认为true
                .build();

        ConnectionConfig connectionConfig = ConnectionConfig.custom()
                .setBufferSize(1024 * 1024)//设置buffer size
                .build();

        httpclient = HttpClients.custom()
                .setConnectionManager(connMrg)
                .setDefaultConnectionConfig(connectionConfig)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy() {//设置keepAlive的时间
                    @Override
                    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                        long keepAlive = super.getKeepAliveDuration(response, context);
                        if (keepAlive == -1) {
                            keepAlive = 10 * 1000;//10s
                        }
                        return keepAlive;
                    }
                })
//                .setRetryHandler(new DefaultHttpRequestRetryHandler()) //重试策略，但是重试使用的还是之前的链接池里的链接
                .build();

        //定时清理已被服务端关闭的连接
        scheduledService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                connMrg.closeExpiredConnections();
                connMrg.closeIdleConnections(5, TimeUnit.SECONDS);
            }
        }, 0, 5, TimeUnit.SECONDS);


    }

    //机器关闭时关闭httpclient
    private static void destroyByJvmExit() {
        Thread hook = new Thread(new Runnable() {
            public void run() {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    //
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(hook);
    }

    /**
     * 执行post请求, Content-Type为json
     * 如果map参数列表中有中文,需自行<code>URLEncoder.encode()</code>操作
     *
     * @param url
     * @param str
     * @return
     */
    public static HttpExecuteResponse doPost(String url, String str, HashMap<String, String> header) {


        StringEntity stringEntity = new StringEntity(str, "UTF-8");
        stringEntity.setContentType(APPLICATION_JSON_URLENCODED);
        stringEntity.setContentEncoding("UTF-8");

        HttpPost httpPost = new HttpPost(url);
        if (header != null && header.size() > 0) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }
        httpPost.setEntity(stringEntity);


        return executeHttpRequest(httpPost);
    }


    /**
     * 发起 http post 请求, 并设置 post body 的 parameter和header，Content-Type为application/x-www-form-urlencoded
     * 如果map参数列表中有中文,无需自行encode
     *
     * @param url
     * @param params
     * @param header
     * @return
     */
    public static HttpExecuteResponse doPost(String url, Map<String, String> params, HashMap<String, String> header) throws UnsupportedEncodingException {

        HttpPost httpPost = new HttpPost();
        httpPost.setURI(URI.create(url));

        // 设置 http post 请求参数
        if (params != null && params.entrySet().size() > 0) {
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            for (Map.Entry<String, String> parameter : params.entrySet()) {
                postParameters.add(new BasicNameValuePair(parameter.getKey(), parameter.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "utf-8"));
        }

        // 设置 header
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeHttpRequest(httpPost);
    }

    /**
     * 发起 http post 请求, body里面存放二进制,并设置post的header
     *
     * @param url
     * @param bytes
     * @param header
     * @return
     */
    public static HttpExecuteResponse doPost(String url, byte[] bytes, HashMap<String, String> header) throws UnsupportedEncodingException {

        HttpPost httpPost = new HttpPost();
        httpPost.setURI(URI.create(url));
        httpPost.setEntity(new ByteArrayEntity(bytes));

        // 设置 header
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeHttpRequest(httpPost);
    }

    /**
     * 发起 http post 请求, multipart传输文件
     *
     * @param url
     * @param bytes    文件二进制流
     * @param paraName 参数名
     * @param fileName 文件名
     * @param header
     * @return
     */
    public static HttpExecuteResponse doPostWithFile(String url, byte[] bytes, String paraName, String fileName, HashMap<String, String> header) throws UnsupportedEncodingException {

        HttpPost httpPost = new HttpPost();
        httpPost.setURI(URI.create(url));
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody(paraName, bytes, ContentType.MULTIPART_FORM_DATA, fileName);
        httpPost.setEntity(builder.build());

        // 设置 header
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                httpPost.setHeader(entry.getKey(), entry.getValue());
            }
        }

        return executeHttpRequest(httpPost);
    }

    /**
     * 执行没有参数的get方法
     *
     * @param url
     * @return
     */
    public static HttpExecuteResponse doGet(String url) {
        return doGet(url, Collections.<String, String>emptyMap());
    }

    /**
     * 如果map参数列表中有中文,需自行<code>URLEncoder.encode()</code>操作
     *
     * @param url
     * @param params
     * @return
     */
    public static HttpExecuteResponse doGet(String url, Map<String, String> params) {

        String queryString = builderQueryString(params);
        if (StringUtils.isNotBlank(queryString)) {
            url += URI_PARAM_SEPARATOR + queryString;
        }
        return executeHttpRequest(new HttpGet(URI.create(url)));
    }

    private static HttpExecuteResponse executeHttpRequest(HttpRequestBase request) {
        HttpExecuteResponse httpExecuteResponse = new HttpExecuteResponse();
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = httpclient.execute(request);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            httpExecuteResponse.setResponseCode(statusCode);
//            if (statusCode == HttpStatus.SC_OK) {
            httpExecuteResponse.setResponseBody(EntityUtils.toByteArray(httpResponse.getEntity()));
            httpExecuteResponse.setHeaders(httpResponse.getAllHeaders());

//            }
        } catch (IOException e) {
            httpExecuteResponse.setThrowable(e);
            httpExecuteResponse.setErrorMessage(e.getMessage());
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return httpExecuteResponse;
    }

    public static String builderQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return EMPTY_STRING;
        }
        StringBuilder stringBuilder = new StringBuilder();
        Joiner.on(PARAM_SEPARATOR).withKeyValueSeparator(PARAM_KV_SEPARATOR).appendTo(stringBuilder, params);
        return stringBuilder.toString();
    }

    public static String buildQueryUrl(String url, Map<String, String> params) {
        String queryString = builderQueryString(params);
        if (StringUtils.isNotBlank(queryString)) {
            url += URI_PARAM_SEPARATOR + queryString;
        }
        return url;
    }

    /**
     * 获取单个route的状态
     *
     * @param hostName
     * @return
     */
    public static String getRouteStatus(String hostName) {
        return connMrg.getStats(new HttpRoute(new HttpHost(hostName))).toString();
    }


}
