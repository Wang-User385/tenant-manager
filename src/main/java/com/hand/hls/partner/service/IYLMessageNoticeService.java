package com.hand.hls.partner.service;

import com.hand.hap.core.IRequest;
import com.hand.hls.partner.dto.AssetNeedBuybackDto;
import com.hand.hls.partner.dto.AssetNeedSubstituteDto;

public interface IYLMessageNoticeService {

    /**
     * 易靓审核结果通知
     *
     * @param projectId
     * @param scene：审核场景PRE_RISK 人工风险审核LOAN_AUDIT 放款审核MORTGAGE_MATERIAL_AUDIT 抵押材料审核
     * @return
     */
    void orderAuditResult(Long projectId, String scene, IRequest iRequest);


    /**
     * 易靓放款结果通知
     *
     * @param projectId
     * @return
     */
    void orderLoanResult(Long projectId, IRequest iRequest);


    /**
     * 易靓关单结果通知
     *
     * @param
     * @return
     */
    void orderClosedNotify(IRequest iRequest);


    /**
     * 易靓还款计划生成通知
     *
     * @param projectId
     * @return
     */
    void repayPlanCreatedNotify(Long projectId, IRequest iRequest);


    /**
     * 易靓逾期算费完成通知
     *
     * @param
     * @return
     */
    void overdueCalculateFinishedNotify(IRequest iRequest);

    /**
     * 易靓需代偿通知
     *
     * @param
     * @return
     */
    void assetNeedSubstitute(AssetNeedSubstituteDto assetNeedSubstituteDto, IRequest iRequest);


    /**
     * 易靓需回购通知
     *
     * @param
     * @return
     */
    void assetNeedBuyback(AssetNeedBuybackDto assetNeedBuyback,IRequest iRequest);


    /**
     * 易靓代扣签约结果通知
     *
     * @param projectId
     * @return
     */
    void withholdContractResult(Long projectId, IRequest iRequest);


    /**
     * 易靓期次代扣结果通知
     *
     * @param projectId
     * @return
     */
    void repayPlanRepaidNotify(Long projectId, IRequest iRequest);


}
