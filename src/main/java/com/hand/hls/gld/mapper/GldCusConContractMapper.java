package com.hand.hls.gld.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContract;

public interface GldCusConContractMapper extends Mapper<HlsCusConContract> {
    /**
     * queryConContractByKey
     *
     * @param hlsCusConContract HlsCusConContract
     * @return Result<all>
     */
    HlsCusConContract queryConContractByKey(HlsCusConContract hlsCusConContract);
}
