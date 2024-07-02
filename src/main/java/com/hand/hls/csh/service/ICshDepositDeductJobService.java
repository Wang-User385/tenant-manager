package com.hand.hls.csh.service;


import com.hand.hap.core.IRequest;
import com.hand.hls.cont.dto.HlsCusConContract;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;

import java.util.List;

/**
 * 保证金抵扣逻辑接口
 *
 * @author wuyicheng
 * @date 2020/05/17
 */
public interface ICshDepositDeductJobService {

    /**
     * 开始执行跑批
     * @param iRequest 请求信息
     * @param contractNumber 可传单个合同，为空则是全量
     */
    void start(IRequest iRequest ,String contractNumber);


    /**
     * 单个合同抵扣，事务自治，多个现金事务同时成功或失败
     * @param iRequest 请求信息
     * @param conContract 合同信息
     * @param cshDepositDeductReqHdList 需要推送SAP的头行信息
     */
    void contractDepositDeduct(IRequest iRequest, HlsCusConContract conContract, List<CshDepositDeductReqHd> cshDepositDeductReqHdList);



    /**
     * 抵扣单生成完成后推送SAP
     * @param iRequest 请求信息
     * @param conContract 合同信息
     * @param cshDepositDeductReqHdList 需要推送SAP的头行信息
     * @param changeType 抵扣来源
     */
    void contractDepositDeductToSap(IRequest iRequest, HlsCusConContract conContract,List<CshDepositDeductReqHd> cshDepositDeductReqHdList,String changeType);
}
