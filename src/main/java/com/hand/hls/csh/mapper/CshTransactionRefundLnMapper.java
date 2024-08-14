package com.hand.hls.csh.mapper;

import com.hand.hap.mybatis.common.Mapper;
import com.hand.hls.csh.dto.CshTransactionRefundLn;

import java.util.List;

public interface CshTransactionRefundLnMapper extends Mapper<CshTransactionRefundLn>{
 List<CshTransactionRefundLn> detailQuery(CshTransactionRefundLn cshTransactionRefundLn);

 /**
  * 二期功能：待支付清单-退款申请 支付页面合同信息查询
  * @param cshTransactionRefundLn
  * @return
  */
 List<CshTransactionRefundLn> refundPayLineDetail(CshTransactionRefundLn cshTransactionRefundLn);

 void updatePaymentAmount(CshTransactionRefundLn cshTransactionRefundLn);
}