package com.hand.hls.partner.util;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AnttechBlockchainDefinAssetmanagePenetrateQueryModel;
import com.alipay.api.domain.AnttechBlockchainDefinAssetmanagePenetrateSubmitModel;
import com.alipay.api.request.AnttechBlockchainDefinAssetmanagePenetrateQueryRequest;
import com.alipay.api.request.AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest;
import com.alipay.api.response.AnttechBlockchainDefinAssetmanagePenetrateQueryResponse;
import com.alipay.api.response.AnttechBlockchainDefinAssetmanagePenetrateSubmitResponse;


import java.util.HashMap;
import java.util.UUID;

public class AlipayUtils {

    public static String SERVERURL = "http://openapi.sit.dl.alipaydev.com/gateway.do";
    public static String APPID = "2021000108666006";
    public static String PRIVATEKEY = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQDOB+7d4u08HvfvzynZGZ1FMfKi/4kravQbBd+6O38Yn/b8AfOy+vHHm6CRFOwpAGT503SliRJgSBs6vCwrdAXZct" +
            "YRzKkU7l4gCFMG8rL0kX1KWzYi3sQlsBylPay7cXVcjZiBNrxI752753L1C+JFP6yfbOSV2xn3xB4KlMRtLDcDYWHa2ffpS9nuFzY9YF5zt+63yxUhhQxe9ISeS1K+1YGrQCemjZG89" +
            "08e5kuq/ywcL/lhHB4BJ8xEp0u9OuyUTCnJy84Y/ifTsxog8BXuDRPyzEChplssNUjH1RgHjpeCOPImypGuvbXq47KhEEPUW2qho5zIX0+UcdsZJ0XRAgMBAAECggEAXZLCzSnUf1q" +
            "9VsArDHwSrquZvKf8X6jKxz8qtoVxGvkEDr7ANQi+KN8o1NvAynpwYfrE3q3bl7kIDOwLz4x5X6JFUX43SNdeDoRZWS1/U46EbfHxK3MreMZ8rBvPyK4mFGwG2KDIcQPLCt16m4rTM" +
            "IpT13B4fQsuxxXeYwXgFIiQpSbVdWadnoZvZRwDeJF3p2kMOx9THnaMNnVakBuSKgpRd18tq4MnVRLreNk5rdtEvUtRtdINUgIAjCkFISw3XgeodRYCYG04EJtO7CEQNAzirZTUXaRj" +
            "fFa1WXu33xrYyYAJdtYP/Q0fxY60xL8hcAAc2Zhaee6IsJPnuYPcBQKBgQDq549qQ/DzZV7FoIv51VIhGoJOwAI1XJtTvKBtzfjX4oKDmmjOzU4tVgCryxxU53IPwd8oXmvNKJkXVH4" +
            "q8h4ILwBLBiaSEPtuIjcVSbo7Z99xY2Vt2z/17h1pFLYdJSdP+yDs7iDQKZe7BmdduOys9bRr+OCrVwDerEP/oEziewKBgQDgiJJtsT60z7cM5Jyc7VvF6VnsRZuQQesicJ7AbJ8k7zC" +
            "jCeCv4/LLr6VUsWs2yW0KzXG5A2nJ70uBNBe+W08vOD7jNFPvyEQEh4+39IfBWVDzq13lcW1X//OU3zUoftXiqgxxtABVB6Mly6skex4KUN1uDt4I5P8oNnvCiPc9IwKBgQDQAgaz8b" +
            "++uAgI9lac/3H/kErNUydhe0SsDL7/HMH64T/zK1sdrR1J9fsYJP5MjLorC+EBDUNmY0nVJ+OlQcqoMn6O8L5c357VcoTWW/gGPL/W105szhZAPv9aGpX9DvZV06nfRCpYSkxqt4v2q" +
            "RcjPVvrtHG2J4/EnkSEar1KWwKBgQCxeuKbuD3DuGiN1WsCFBC1uMUuoLrdZW2SVIj3uyR0kmjUhutGvRze6iD6eB8yODdsEYax4sPNLcx1/ZJDEnPd9EypVWR/pcI1/l2Y374rFAmM" +
            "Akn/IhB3PcbxRxoCv3cbaqTZf5m/nIDWUE4gUP0m1FKjOzdAupoB1EcxNwiPFwKBgQCBYKck0H/Yl3Z7RHAjpxsoV5gZnljxU0WDa7/QW0xr/c2SBcFY1tr7sV3wiz3bEo35Zou7xd+" +
            "+PCetCmd+5TaQ0nNwye1L75q6x9spzb64WHMtuXfZdCB9nWcKtVx1iFNpaqY6EM8poJ2/5kPTyu+qGm7XTEJNg4gpXEtGjhLWOw==";
    public static String ALIPAYPUBLICKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApzm8gpALmzL8lG/NGO1LGiJKNPhp39pmDBdy5pTLXY/Mfre1rcbOKglL+Dnf3xEysKtjKP+dDeCOBBstiEUUgvGRC9" +
            "X8hlGMpjihGC3+aDU/lIeXUI6zNlOB7t5cOSlkPAiqyWo/SWrW+6UzJNBIYIr0AGMLo5IADRND66oc5k3YKNl6umTr2lSu6mr0yVK0QwY+rFnF6H5zeh48mBTB6bFoPndWb3w0sOB" +
            "VEMBoP7q+BKZ+EVkJpvwFl6f9jSjIMcONL0RXXQucFGg20YlmBHm94wr9LGnroENZoamndnD9h1qLu+E4hOxlbzE+pGiw9JEccHYUPsm/JCZlqrQjIwIDAQAB";

    public static String SCENECODE = "TestSC";
    public static String SUBSCENECODE = "0000000000000442";

    public static void main(String [] args) throws AlipayApiException {
        String result = "";

        //result = orderApply("GTYL202408000001");//创建穿透单：用于获取 penetrateId 2024080700101101184902

        //orderQuery();//查询穿透单

        result = orderCancel("2024080700101101184902");//取消穿透单

        System.out.println(result);

        //result = loanApply("张三","421023999999","2024080700101101184902","ALIPAYAPP");//代扣签约 拿到签约二维码字符串

        //result = loanQuery("2024080700101101184902");//查看签约结果


        //paymentApply();//发起代扣
        //paymentQuery();//代扣结果查询
        //paymentCancel();//代扣取消
    }

    //穿透单创建ORDER.APPLY
    public static String orderApply(String outOrderNo) throws AlipayApiException {
        AnttechBlockchainDefinAssetmanagePenetrateSubmitModel model =new AnttechBlockchainDefinAssetmanagePenetrateSubmitModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("ORDER.APPLY");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("outOrderNo",outOrderNo);//此参数传进件编号
        params.put("vidBizId","ANTCHAIN");//默认值ANTCHAIN
        params.put("verifyId","ANTCHAIN");//默认值ANTCHAIN
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest request = new AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest();
        request.setBizModel(model);

        AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
        AnttechBlockchainDefinAssetmanagePenetrateSubmitResponse response = alipayClient.execute(request);
        String responseBody = response.getBody();

        return responseBody;
    }

    //穿透单查询ORDER.QUERY
    public static void orderQuery(){
        AnttechBlockchainDefinAssetmanagePenetrateQueryModel model =new AnttechBlockchainDefinAssetmanagePenetrateQueryModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("ORDER.QUERY");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("penetrateId","2024080700101101184902");
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateQueryRequest request = new AnttechBlockchainDefinAssetmanagePenetrateQueryRequest();
        request.setBizModel(model);

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
            AnttechBlockchainDefinAssetmanagePenetrateQueryResponse response = alipayClient.execute(request);
            System.out.println("response:" + response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    //穿透单取消ORDER.CANCEL
    public static String orderCancel(String penetrateId) throws AlipayApiException {
        AnttechBlockchainDefinAssetmanagePenetrateSubmitModel model =new AnttechBlockchainDefinAssetmanagePenetrateSubmitModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("ORDER.CANCEL");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("penetrateId",penetrateId);
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest request = new AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest();
        request.setBizModel(model);

        AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
        AnttechBlockchainDefinAssetmanagePenetrateSubmitResponse response = alipayClient.execute(request);
        String responseBody = response.getBody();

        return responseBody;
    }


    //GT-MY-001代扣授权签约申请
    public static String loanApply(String customerName,String userCertNo,String penetrateId,String channel) throws AlipayApiException {
        AnttechBlockchainDefinAssetmanagePenetrateSubmitModel model =new AnttechBlockchainDefinAssetmanagePenetrateSubmitModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("LOAN.APPLY");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("customerName",customerName);//签约人姓名
        params.put("userCertNo",userCertNo);//签约人身份证号码
        params.put("penetrateId",penetrateId);
        //QRCODE：直接将extInfo字符串转换成⼆维码
        //ALIPAYAPP：将extInfo字符串加上前缀：https://openapi.alipay.com/gateway.do?  后转⼆维码
        params.put("channel",channel);
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest request = new AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest();
        request.setBizModel(model);

        AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
        AnttechBlockchainDefinAssetmanagePenetrateSubmitResponse response = alipayClient.execute(request);
        String responseBody = response.getBody();

        return responseBody;
    }

    //GT-MY-002代扣授权签约申请查询
    public static String loanQuery(String penetrateId) throws AlipayApiException {
        AnttechBlockchainDefinAssetmanagePenetrateQueryModel model =new AnttechBlockchainDefinAssetmanagePenetrateQueryModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("LOAN.QUERY");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("penetrateId",penetrateId);
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateQueryRequest request = new AnttechBlockchainDefinAssetmanagePenetrateQueryRequest();
        request.setBizModel(model);

        AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
        AnttechBlockchainDefinAssetmanagePenetrateQueryResponse response = alipayClient.execute(request);
        String responseBody = response.getBody();

        return responseBody;
    }

    //GT-MY-003扣款请求PAYMENT.APPLY
    public static void paymentApply(){
        AnttechBlockchainDefinAssetmanagePenetrateSubmitModel model =new AnttechBlockchainDefinAssetmanagePenetrateSubmitModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("PAYMENT.APPLY");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("penetrateId","2024080700101101184902");
        params.put("outSeqNo","GTYL202408000104001");//代扣流水号
        params.put("amount","1");//代扣金额
        params.put("subject","订单GTYL202408000104第1期租金");//支付描述
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest request = new AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest();
        request.setBizModel(model);

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
            AnttechBlockchainDefinAssetmanagePenetrateSubmitResponse response = alipayClient.execute(request);
            System.out.println("response:" + response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    //GT-MY-004扣款查询
    public static void paymentQuery(){
        AnttechBlockchainDefinAssetmanagePenetrateQueryModel model =new AnttechBlockchainDefinAssetmanagePenetrateQueryModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("PAYMENT.QUERY");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("outSeqNo","GTYL202408000104001");//代扣流水号
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateQueryRequest request = new AnttechBlockchainDefinAssetmanagePenetrateQueryRequest();
        request.setBizModel(model);

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
            AnttechBlockchainDefinAssetmanagePenetrateQueryResponse response = alipayClient.execute(request);
            System.out.println("response:" + response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    //GT-MY-005支付申请撤销
    public static void paymentCancel(){
        AnttechBlockchainDefinAssetmanagePenetrateSubmitModel model =new AnttechBlockchainDefinAssetmanagePenetrateSubmitModel();
        model.setRequestId(System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", ""));//这个请求id 必须要传，否则会报错“重复的请求编码”
        model.setFunction("PAYMENT.CANCEL");

        HashMap<String,Object> params = new HashMap<String,Object>();
        params.put("sceneCode",SCENECODE);
        params.put("subSceneCode",SUBSCENECODE);
        params.put("outSeqNo","GTYL202408000104001");//代扣流水号
        model.setBizParams(JSONObject.toJSONString(params));

        AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest request = new AnttechBlockchainDefinAssetmanagePenetrateSubmitRequest();
        request.setBizModel(model);

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(SERVERURL,APPID,PRIVATEKEY,"json","GBK",ALIPAYPUBLICKEY,"RSA2");
            AnttechBlockchainDefinAssetmanagePenetrateSubmitResponse response = alipayClient.execute(request);
            System.out.println("response:" + response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

}
