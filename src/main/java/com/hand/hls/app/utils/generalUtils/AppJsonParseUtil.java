package com.hand.hls.app.utils.generalUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hand.hap.system.dto.ResponseData;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author liao
 * 接口返回类型转换
 */
public class AppJsonParseUtil {

    private AppJsonParseUtil() {
    } // 类不可被实例化


    public static String objectParseJsonString(Object object) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(object));
        String jsonString = jsonObject.toString();
        return jsonString;
    }

    public static String listParseJsonString(List list) {
        // List转JSONArray
        JSONArray jsonArray = new JSONArray();
        for (Object object : list) {
            jsonArray.add(object);
        }
        String jsonString = jsonArray.toString();
        return jsonString;
    }

    /**
     * 2020/11/26
     * 由于app端对 ResponseData 存在的null /"" 两种为空的情况解析报错，
     * 在项目后期将返回格式进行转换，
     */
    public static AppResponseData toAppResponseData(ResponseData responseData) {
        // List转AppResponseData
        AppResponseData appResponseData = new AppResponseData();
        appResponseData.setCode(responseData.getCode());
        appResponseData.setMessage(responseData.getMessage());
        appResponseData.setTotal(responseData.getTotal());
        appResponseData.setSuccess(responseData.isSuccess());
        if (responseData.getRows() != null) {
            appResponseData.setRows(AppJsonParseUtil.listParseJsonString(responseData.getRows()));
        }

        return appResponseData;
    }


    public static String mapToJsonStr(Map<String, Object> maps) {

        ObjectMapper json = new ObjectMapper();
        try {
            //把map对象转成json格式的String字符串
             return  json.writeValueAsString(maps);
        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }
}
