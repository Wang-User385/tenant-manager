package com.hand.hap.core.json;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.impl.RequestHelper;
import com.hand.hap.system.dto.ResponseData;
import com.hand.hls.utils.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: yang
 * Date: 2018/5/29
 * Time: 10:59
 */
public class ResponseDataSerializer extends JsonSerializer<ResponseData> implements ContextualSerializer {
    private Logger logger = LoggerFactory.getLogger(ResponseDataSerializer.class);

    @Override
    public void serialize(ResponseData responseData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        IRequest currentRequest = RequestHelper.getCurrentRequest(true);
        if ("Y".equals(currentRequest.getAttribute("springFlag"))) {
            Map<String, Object> result = new HashMap<>();
            result.put("totalCount", responseData.getTotal());
            List<?> rows = responseData.getRows();
            if(CollectionUtils.isNotEmpty(rows)) {
                Object snakeCaseRows = null;
                if(rows.get(0) instanceof Map) {
                    snakeCaseRows = JSON.parseArray(JsonUtils.toSnakeJsonString4Map(rows));
                }else{
                    snakeCaseRows = JSON.parseArray(JsonUtils.toSnakeJsonString(rows));
                }
                result.put("record", snakeCaseRows);
            }else{
                result.put("record", new ArrayList<>());
            }
            Map<String, Object> map = new HashMap<>();
            Map<String, Object> error = new HashMap<>();
            error.put("code",responseData.getCode());
            error.put("message",responseData.getMessage());
            map.put("success", responseData.isSuccess());
            map.put("error",error);
            map.put("result", result);
            jsonGenerator.writeObject(map);
        } else {
            jsonGenerator.writeObject(JSON.toJSON(responseData));
        }
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        return new ResponseDataSerializer();
    }
}
