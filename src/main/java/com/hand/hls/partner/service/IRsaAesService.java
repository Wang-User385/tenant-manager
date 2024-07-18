package com.hand.hls.partner.service;

import com.alibaba.fastjson.JSONObject;

public interface IRsaAesService {
    JSONObject encryptedData(JSONObject jsonObject) throws Exception;

    String decryptedData(JSONObject jsonObject) throws Exception;
}
