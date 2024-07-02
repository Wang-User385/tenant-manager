package com.hand.hls.csh.mapper;


import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshDepositDeductJobLogs;
import org.apache.ibatis.annotations.Param;

/**
 * 保证金抵扣日志表mapper
 *
 * @author wuyicheng
 * @date 2020/05/17
 */
public interface CshDepositDeductJobLogsMapper extends Mapper<CshDepositDeductJobLogs> {

    /**
     * 查找非本批次是否有正在运行的任务
     * @param cshDepositDeductJobLogs 本次的批次号
     * @return 正在运行的其他批次号
     */
    String selectExecutingBatchId(CshDepositDeductJobLogs cshDepositDeductJobLogs);


    /**
     * 如果是汽车租赁保证金抵扣，同一个合同之间不应该并发
     * @param cshDepositDeductJobLogs 本次的批次号,以及本次的合同号
     * @return 正在运行的其他批次号
     */
    String selectDeductConfirmExecutingBatchId(CshDepositDeductJobLogs cshDepositDeductJobLogs);

    /**
     * 查找非本批次是否有正在运行的任务
     * @param cshDepositDeductJobLogs 本次的批次号
     * @return 正在运行的其他批次号
     */
    String selectExecutingBatchIdNew(CshDepositDeductJobLogs cshDepositDeductJobLogs);


    /**
     * 如果是汽车租赁保证金抵扣，同一个合同之间不应该并发
     * @param cshDepositDeductJobLogs 本次的批次号,以及本次的合同号
     * @return 正在运行的其他批次号
     */
    String selectDeductConfirmExecutingBatchIdNew(CshDepositDeductJobLogs cshDepositDeductJobLogs);


    /**
     * 将本批次正在运行的标志更新为成功
     * @param batchId 本次的批次号
     * @return 影响行数
     */
    int updateExecutedByBatchId(@Param("batchId") String batchId);

    /**
     * 查找非本批次是否有正在运行的任务
     * @param cshDepositDeductJobLogs 本次的批次号
     * @return 正在运行的其他批次号
     */
    String selectExecutingAllBatchId(CshDepositDeductJobLogs cshDepositDeductJobLogs);

}
