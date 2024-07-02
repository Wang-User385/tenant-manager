package com.hand.hls.wsdl.dto;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * <p>三一接口数据封装实体类
 *
 * @author luobiao
 * created by 2022/04/13 17:00
 */

public class SyFinanceInterface {
    private SyInterfaceHead header;

    private JSONObject body;

    public SyInterfaceHead getHeader() {
        return header;
    }

    public void setHeader(SyInterfaceHead header) {
        this.header = header;
    }

    public JSONObject getBody() {
        return body;
    }

    public void setBody(JSONObject body) {
        this.body = body;
    }
}
