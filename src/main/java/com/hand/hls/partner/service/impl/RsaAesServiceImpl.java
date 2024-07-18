package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hand.hls.partner.service.IRsaAesService;
import com.hand.hls.partner.util.RsaAesUtils;
import org.springframework.stereotype.Service;

@Service
public class RsaAesServiceImpl implements IRsaAesService {

    @Override
    public JSONObject encryptedData(JSONObject jsonObject) throws Exception {
        return RsaAesUtils.encryptedData(jsonObject.toString());
    }
    @Override
    public String decryptedData(JSONObject jsonObject) throws Exception{
        return RsaAesUtils.decryptedData(jsonObject);
    }
}
