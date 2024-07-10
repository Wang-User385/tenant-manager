package com.hand.hls.partner.dto;

import com.hand.hap.system.dto.BaseDTO;
import lombok.Data;

//还款计划期次详情信息

@Data
public class RepayPlanTermInfoDTO extends BaseDTO {

    private Integer termNo;//期次号
    private String status;//期次详情状态；NOT_START_TERM - 未到期CURRENT_TERM - 当前期GRACE_TERM - 宽限期OVERDUE_TERM - 逾期ENDED - 结束
    private String lastPaidTime;//上一次还款时间；格式：yyyy-MM-dd HH:mm:ss客户、合作公司资金发生还款时存在，为国搜系统处理时间
    private String endTime;//结束时间；格式：yyyy-MM-dd HH:mm:ss客户、合作公司资金发生还款并结清该期次时存在，为国搜系统处理时间
    private String termStartDate;//开始日格式：yyyy-MM-dd
    private String termEndDate;//结束日格式：yyyy-MM-dd
    private String termRepayDate;//约定还款日格式：yyyy-MM-dd
    private String termPenaltyGraceDate;//罚息宽限日格式：yyyy-MM-dd
    private String termOvdGraceDate;//逾期宽限日格式：yyyy-MM-dd
    private String repayType;//还款方式；期次详情状态为ENDED时存在；DEDUCT - 代扣TRANSFER - 转付SUBSTITUTE - 代偿BUY_BACK - 回购PRE_SETTLE - 提前结清ACTIVE_REPAY - 主动还款OTHER - 其他
    private Integer ovdDays;//逾期天数
    private Long termNomPrin;//应还本金；单位：分
    private Long termNomInt;//应还利息；单位：分
    private Long termOvd;//应还罚息；单位：分
    private Long termFee;//应还其他费用；单位：分
    private Long termTotal;//应还总金额；单位：分
    private Long paidNomPrin;//实还本金；单位：分
    private Long paidNomInt;//实还利息；单位：分
    private Long paidOvd;//实还罚息；单位：分
    private Long paidFee;//实还其他费用；单位：分
    private Long paidTotal;//实还总金额；单位：分
    private Long restNomPrin;//剩余应还本金；单位：分
    private Long restNomInt;//剩余应还利息；单位：分
    private Long restOvd;//剩余应还罚息；单位：分
    private Long restFee;//剩余应还其他费用；单位：分
    private Long restTotal;//剩余应还总金额；单位：分
}
