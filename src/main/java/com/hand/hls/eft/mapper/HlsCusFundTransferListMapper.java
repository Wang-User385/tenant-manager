package com.hand.hls.eft.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.eft.dto.HlsCusFundTransferList;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface HlsCusFundTransferListMapper extends Mapper<HlsCusFundTransferList> {


    /**
     * 调拨单查询
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>  selectFundTransferList(HlsCusFundTransferList fundTransferList);


    /**
     * 资金主页查询调拨单详情
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>  selectFundTransferListDeatil(HlsCusFundTransferList fundTransferList);


    /**
     * 财务查询的调拨单
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>  selectFundTransferFinanceList(HlsCusFundTransferList fundTransferList);


    /**
     * 复制调拨单查询
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>  selectCopyFundTransferList(HlsCusFundTransferList fundTransferList);


    /**
     * 调拨审批单查询
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>   selectApproveTransferList(HlsCusFundTransferList fundTransferList);


    /**
     * 应还代办
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectTransferTaskList(HlsCusFundTransferList fundTransferList);


    /**
     * 取出下一次流水号
     * @param transferId
     * @return
     */
    String selectTransferNumberMax(@Param("transferId") Long transferId);


    /**
     * 未来三十天账户余额
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList> selectAccountBalanceData(HlsCusFundTransferList fundTransferList);


    /**
     * 未来账户收付明细
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>  selectAccountBalanceDetail(HlsCusFundTransferList fundTransferList);


    /**
     * 查询账户详情
     * @param fundTransferList
     * @return
     */
    List<HlsCusFundTransferList>  selectBankAccountData(HlsCusFundTransferList fundTransferList);


    /**
     * 查询实际支付金额是否大于剩余金额
     * @param sourceDocCategory
     * @param sourceDocLineId
     * @return
     */
   BigDecimal selectActualPaySurplusAmount(@Param("sourceDocCategory") String sourceDocCategory, @Param("sourceDocLineId") Long sourceDocLineId);


    /**
     * 查询单据引用数量
     * @param sourceDocCategory
     * @param sourceDocLineId
     * @return
     */
   int selectSourceDocumentCount(@Param("sourceDocCategory") String sourceDocCategory, @Param("sourceDocId") Long sourceDocId, @Param("sourceDocLineId") Long sourceDocLineId);


    /**
     * 核销情况
     * @param fundTransferList
     * @return
     */
   List<HlsCusFundTransferList>  selectWriteOffDeatil(HlsCusFundTransferList fundTransferList);


    /**
     * 查询资金提交需要变更的数量
     * @param finTransferId
     * @return
     */
    int selectHasChangeCount(@Param("finTransferId") Long finTransferId);

    /**
     * 根据 sourceDocId和CFItem查询AccountSource
     * @param cfItem
     * @param sourceDocId
     * @return
     */
    String selectAccountSource(String cfItem, Long sourceDocId);

    List<HlsCusFundTransferList>  queryTransferListById(HlsCusFundTransferList fundTransferList);
    List<HlsCusFundTransferList>  queryTransferListDetail(HlsCusFundTransferList fundTransferList);
}
