package com.hand.hls.prj.service;

import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.hand.hap.core.IRequest;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignRecord;

/**
 * description
 *
 * @author Lenovo 2023/08/09 15:16
 */
public interface ISignFinish {

    String getSourceDocCategory();

    boolean signFinish(SignRecord signRecord, List<SignContract> signContractList);


}
