package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshDepositDeductReqHd;
import com.hand.hls.csh.dto.CshDepositDeductReqLn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CshDepositDeductReqLnMapper extends Mapper<CshDepositDeductReqLn> {

    List<CshDepositDeductReqLn> selectCshDepositDeductReqLnList(CshDepositDeductReqLn cshDepositDeductReqLn);

    Long selectUnDeductCashflowMaxTimes(CshDepositDeductReqHd cshDepositDeductReqHd);

    /**
     * 查询主键下一个值
     */
    Long queryNextPkValue();

    List<Long> queryReqLnId(@Param("reqHdId") Long reqHdId);

    /**
     * 查询承租人还款信息是否在其他非取消的行中
     * @param sourceInfoId 保证金还款信息表ID
     * @param reqHdId 本次抵扣的头ID
     * @param infoIdBatch 批次号，判断是不是同一批
     * @return 其他的抵扣记录状态
     */
    List<String> selectOtherSourceInfoId(@Param("sourceInfoId") Long sourceInfoId,@Param("reqHdId") Long reqHdId,@Param("infoIdBatch") String infoIdBatch);

    /**
     * 查询承租人还款信息是否在其他成功的行
     * @param sourceInfoId 保证金还款信息表ID
     * @param reqHdId 本次抵扣的头ID
     * @param infoIdBatch 批次号，判断是不是同一批
     * @return 其他的抵扣记录状态
     */
    List<String> selectOtherSourceInfoIdSuccess(@Param("sourceInfoId") Long sourceInfoId,@Param("reqHdId") Long reqHdId,@Param("infoIdBatch") String infoIdBatch);

    /**
     * 查询承租人还款信息是否在其他不成功的行
     * @param sourceInfoId 保证金还款信息表ID
     * @param reqHdId 本次抵扣的头ID
     * @param infoIdBatch 批次号，判断是不是同一批
     * @return 其他的抵扣记录状态
     */
    List<String> selectOtherSourceInfoIdNotSuccess(@Param("sourceInfoId") Long sourceInfoId,@Param("reqHdId") Long reqHdId,@Param("infoIdBatch") String infoIdBatch);

    /**
     * 查询承租人还款信息是否在其他撤回的行
     * @param sourceInfoId 保证金还款信息表ID
     * @param reqHdId 本次抵扣的头ID
     * @param infoIdBatch 批次号，判断是不是同一批
     * @return 其他的抵扣记录状态
     */
    List<String> selectOtherSourceInfoIdBack(@Param("sourceInfoId") Long sourceInfoId,@Param("reqHdId") Long reqHdId,@Param("infoIdBatch") String infoIdBatch);


}