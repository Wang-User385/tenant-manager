package com.hand.hls.fin.mapper;

import com.hand.hls.fin.dto.HlsCusLonContract;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface HlsCusLonContractMapper extends LonContractMapper<HlsCusLonContract> {
    Integer validata(HlsCusLonContract lonContract);
    List<HlsCusLonContract> lonContractChangeReqQuery(Map<String, Object> var1);

    /**
     * 更新合同结清状态
     * @return
     */
    int updateContractSettleStatus();

    int selectMajorContractNumCount(HlsCusLonContract hlsCusLonContract);

    /**
     * 提款审批个数
     * @param hlsCusLonContract
     * @return
     */
    int selectWithdrawValidStatus(HlsCusLonContract hlsCusLonContract);


    /**
     * 更新sourceCreditLineId
     * @return
     */
    int updateLonContractSourceCreditLineId(@Param("creditContractId") Long creditContractId,
                                            @Param("creditLineId") Long creditLineId,
                                            @Param("sourceCreditLineId") Long sourceCreditLineId);


    List<HlsCusLonContract> selectLonContractDataDetail(HlsCusLonContract lonContract);

    /**
     * “合同执行期从”需小于等于关联下的提款中的最小提款日，“合同执行期到”需要大于等于关联下的提款中的最大的到期日；
     * 金额：合同金额需要大于等于关联提款的提款金额总和
     * @param contractId
     * @return
     */
    HlsCusLonContract lonContractCheckQuery(@Param("contractId") Long contractId);

    List<HlsCusLonContract> selectLonContractByBpId(Long bpId);

    List<HlsCusLonContract> contractNumberCheck(String contractNumber);
}