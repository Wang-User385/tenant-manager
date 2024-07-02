package com.hand.hls.sign.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.sign.dto.SignVerify;
import org.apache.ibatis.annotations.Param;

/**
 * description
 *
 * @author shigure 2022/11/15 18:46
 */
public interface SignVerifyMapper extends Mapper<SignVerify> {

    SignVerify queryVerifiedBp(@Param("bpId") Long bpId);
}
