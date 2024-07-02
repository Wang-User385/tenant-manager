package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.service.ISignContractService;
import com.hand.hls.prj.service.ISignVerifyService;
import com.hand.hls.sign.dto.SignContract;
import com.hand.hls.sign.dto.SignVerify;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author shigure 2022/11/29 17:29
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SignVerifyServiceImpl extends BaseServiceImpl<SignVerify> implements ISignVerifyService {

}
