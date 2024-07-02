package com.hand.hls.app.utils.generalUtils;

import com.alibaba.fastjson.JSONObject;
import com.hand.hap.system.dto.ResponseData;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * app 必输校验工具类
 * @author liao
 */
public class AppCheckRequiredUtils {
/**
 * 根据  JSONObject 进行必输验证
 *
 * */
    public static ResponseData checkRequired(JSONObject jsonObject, List<String> wordRequired) {
        ResponseData responseData = new ResponseData();
        if(jsonObject.isEmpty()){
            responseData.setSuccess(false);
            responseData.setMessage(" 传参不能为空");
            return responseData;
        }
        for (String s : wordRequired) {
            if (StringUtils.isEmpty(jsonObject.getString(s))  || jsonObject.getString(s) ==null ) {
                responseData.setSuccess(false);
                responseData.setMessage(s+ "为空");
                return responseData;

            }
        }

        responseData.setSuccess(true);
        return responseData;
    }

    /**
     * 根据map 就行必输验证
     *
     * */
    public static ResponseData checkRequiredMap(Map<String, Object> map, List<String> wordRequired) {
        ResponseData responseData = new ResponseData();
        for (String s : wordRequired) {
            if (StringUtils.isEmpty(map.get(s).toString())  || map.get(s)==null ) {
                responseData.setSuccess(false);
                responseData.setMessage(s+ "为空");
                return responseData;

            }
        }

        responseData.setSuccess(true);
        return responseData;
    }

    /**
     *
     *根据一个 对象的 list 进行必输验证
     * */
    public static ResponseData checkRequiredList(ArrayList list, List<String> wordRequired) {

        ResponseData responseData = new ResponseData();

            for(int i =0; i< list.size();i++){
                Map map   = (Map) list.get(i);
                for (String s : wordRequired) {

                if (StringUtils.isEmpty(map.get(s).toString())  || map.get(s).toString() ==null ) {
                    responseData.setSuccess(false);
                    responseData.setMessage(s+ "为空");
                    return responseData;

                }

               }
            }

        responseData.setSuccess(true);
        return responseData;

    }
}
