package com.hand.hls.fin.service;

import com.hand.hap.core.IRequest;
import com.hand.hap.core.ProxySelf;
import com.hand.hap.system.service.IBaseService;
import com.hand.hls.fin.dto.HlsCusCtLonContractPledge;

import java.util.List;

public interface HlsCusCtLonContractPledgeService extends IBaseService<HlsCusCtLonContractPledge>, ProxySelf<HlsCusCtLonContractPledgeService> {


    /**
     * 质押查询
     * @param iRequest
     * @param lonContractPledge
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCtLonContractPledge> selectLonContractPledge(IRequest iRequest, HlsCusCtLonContractPledge lonContractPledge, int page, int pageSize);


    List<HlsCusCtLonContractPledge> selectContractSurpusAmount(HlsCusCtLonContractPledge lonContractPledge);

    /**
     * 删除质押合同
     * @param iRequest
     * @param lonContractPledges
     */
    void deleteContractPledge(IRequest iRequest, List<HlsCusCtLonContractPledge> lonContractPledges);

    /**
     * 更新质押合同状态
     * @param iRequest
     * @param hlsCusCtLonContractPledge
     * @param PledgeFlag
     */
    void updatePledgeFlag(IRequest iRequest, HlsCusCtLonContractPledge hlsCusCtLonContractPledge, String PledgeFlag);


    /**
     * 质押合同lov期数选择
     * @param iRequest
     * @param lonContractPledge
     * @param page
     * @param pageSize
     * @return
     */
    List<HlsCusCtLonContractPledge> selectContractCashFlow(IRequest iRequest, HlsCusCtLonContractPledge lonContractPledge, int page, int pageSize);



    int updateFctContractPledgeFlag();


    int updateConContractPledgeFlag();


    /**
     * 更新质押是否释放状态
     * @return
     */
    int updatePledgeReleaseFlag();

    /**
     * 手动释放质押合同
     * @param iRequest
     * @param lonContractPledges
     */
    void contractPledgeRelease(IRequest iRequest, List<HlsCusCtLonContractPledge> lonContractPledges);
    List<Double> queryForAmount(HlsCusCtLonContractPledge hlsCusCtLonContractPledge);

    List<HlsCusCtLonContractPledge>  queryContractNumber(IRequest requestContext, List<HlsCusCtLonContractPledge> pledges);
}