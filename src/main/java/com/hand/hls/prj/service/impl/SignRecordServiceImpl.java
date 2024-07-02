package com.hand.hls.prj.service.impl;

import com.hand.hap.system.service.impl.BaseServiceImpl;
import com.hand.hls.prj.service.ISignRecordService;
import com.hand.hls.sign.dto.SignRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * description
 *
 * @author shigure 2022/11/29 17:28
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SignRecordServiceImpl extends BaseServiceImpl<SignRecord> implements ISignRecordService {

}
