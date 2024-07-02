package com.hand.hls.cont.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.cont.dto.HlsCusConContractBeforeRentH;

import java.util.List;
import java.util.Map;

public interface HlsCusConContractBeforeRentHMapper extends Mapper<HlsCusConContractBeforeRentH> {
    List<HlsCusConContractBeforeRentH> selectBeforeRentHInformation(HlsCusConContractBeforeRentH dto);

    List<HlsCusConContractBeforeRentH> selectBeforeRentH(HlsCusConContractBeforeRentH dto);

    List<HlsCusConContractBeforeRentH> selectBeforeRentHQuery(HlsCusConContractBeforeRentH dto);

    List<HlsCusConContractBeforeRentH> selectBeforeRentHZeroTimeFlagInformation(HlsCusConContractBeforeRentH dto);

    List<HlsCusConContractBeforeRentH> selectBeforeRentHZeroTimeFlagInformationQuery(HlsCusConContractBeforeRentH dto);

    List<HlsCusConContractBeforeRentH> selectBeforeRentHZeroTimeFlagQuery(HlsCusConContractBeforeRentH dto);

    Long selectBeforeRentHCountNum(Long companyId, Long contractId);

    Long selectBeforeRentHCount(Long contractId, Long companyId, Long cashflowId);

    void deleteRent(HlsCusConContractBeforeRentH dto);

    List<Map> selectInfoByProject();
}