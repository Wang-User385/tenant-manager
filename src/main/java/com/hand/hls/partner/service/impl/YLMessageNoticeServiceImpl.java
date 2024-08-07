package com.hand.hls.partner.service.impl;

import com.alibaba.fastjson.JSON;
import com.hand.hap.core.IRequest;
import com.hand.hap.core.mq.YLRabbitMqConfiguration;
import com.hand.hls.partner.dto.*;
import com.hand.hls.partner.mapper.LeasingNoticeMapper;
import com.hand.hls.partner.service.ILeasingNoticeService;
import com.hand.hls.partner.service.IYLMessageNoticeService;
import com.hand.hls.prj.dto.HlsCusPrjProject;
import com.hand.hls.prj.mapper.HlsCusPrjProjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class YLMessageNoticeServiceImpl implements IYLMessageNoticeService {

    @Autowired
    private LeasingNoticeMapper leasingNoticeMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ILeasingNoticeService leasingNoticeService;

    @Autowired
    private HlsCusPrjProjectMapper hlsCusPrjProjectMapper;

    /**
     * 易靓审核结果通知
     *
     * @param projectId
     * @param scene：审核场景 PRE_RISK 人工风险审核LOAN_AUDIT 放款审核MORTGAGE_MATERIAL_AUDIT 抵押材料审核
     * @return
     */
    @Override
    public void orderAuditResult(Long projectId, String scene, IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceId(projectId);
        leasingNotice.setSourceType("n001");
        leasingNotice.setScene(scene);
        leasingNotice.setResendFlag("N");
        try {
            //通过主键ID查询数据
            OrderAuditResultDto orderAuditResultDto = new OrderAuditResultDto();
            if ("PRE_RISK".equals(scene)) {
                //人工风险审核
                orderAuditResultDto = leasingNoticeMapper.queryPerRiskResult(projectId);
            } else if ("LOAN_AUDIT".equals(scene)) {
                //放款审核
                orderAuditResultDto = leasingNoticeMapper.queryLoanAuditResult(projectId);
            } else {
                //抵押材料审核 (此处projectId实际传的是contractId)
                orderAuditResultDto = leasingNoticeMapper.queryMortgageMaterialAuditResult(projectId);
            }
            if (!ObjectUtils.isEmpty(orderAuditResultDto)) {
                orderAuditResultDto.setScene(scene);
                orderAuditResultDto.setUniqueId(getUniqueId());
                leasingNotice.setNoticeBody(JSON.toJSONString(orderAuditResultDto));
                //消息推送
                noticePush(leasingNotice, "n001", orderAuditResultDto, iRequest);
            }
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 易靓放款结果通知
     *
     * @param projectId
     * @return
     */
    @Override
    public void orderLoanResult(Long projectId, IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceId(projectId);
        leasingNotice.setSourceType("n002");
        leasingNotice.setResendFlag("N");
        try {
            //通过主键ID查询数据
            OrderLoanResultDto orderLoanResultDto = leasingNoticeMapper.queryOrderLoanResult(projectId);
            if (!ObjectUtils.isEmpty(orderLoanResultDto)) {
                orderLoanResultDto.setUniqueId(getUniqueId());
                leasingNotice.setNoticeBody(JSON.toJSONString(orderLoanResultDto));
                //消息推送
                noticePush(leasingNotice, "n002", orderLoanResultDto, iRequest);
            }
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 易靓关单结果通知
     *
     * @param
     * @return
     */
    @Override
    public void orderClosedNotify(IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceType("n003");
        leasingNotice.setResendFlag("N");
        try {
            //获取需要关单的数据
            List<String> orderClosedList = leasingNoticeMapper.queryOrderClosedList();
            for (String orderNo : orderClosedList) {
                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("orderNo", orderNo);
                mapParam.put("uniqueId", getUniqueId());
                leasingNotice.setNoticeBody(JSON.toJSONString(mapParam));
                //消息推送
                noticePush(leasingNotice, "n003", mapParam, iRequest);
            }
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 易靓还款计划生成通知
     *
     * @param projectId
     * @return
     */
    @Override
    public void repayPlanCreatedNotify(Long projectId, IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceId(projectId);
        leasingNotice.setSourceType("n004");
        leasingNotice.setResendFlag("N");
        try {
            //通过主键ID获取订单编号
            String orderNo = hlsCusPrjProjectMapper.getBusinessApplyNoByProjectId(projectId);
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("orderNo", orderNo);
            mapParam.put("uniqueId", getUniqueId());
            leasingNotice.setNoticeBody(JSON.toJSONString(mapParam));
            //消息推送
            noticePush(leasingNotice, "n004", mapParam, iRequest);
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 易靓逾期算费完成通知
     *
     * @param
     * @return
     */
    @Override
    public void overdueCalculateFinishedNotify(IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceType("n005");
        leasingNotice.setResendFlag("N");
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String finishedTime = now.format(formatter);
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("finishedTime", finishedTime);
            mapParam.put("uniqueId", getUniqueId());
            leasingNotice.setNoticeBody(JSON.toJSONString(mapParam));
            //消息推送
            noticePush(leasingNotice, "n005", mapParam, iRequest);
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 易靓需代偿通知
     *
     * @param iRequest
     * @return
     */
    @Override
    public void assetNeedSubstitute(AssetNeedSubstituteDto assetNeedSubstituteDto, IRequest iRequest) {
        //获取逾期超过30天小于85天的逾期数据
        List<AssetNeedSubstituteDto> needSubstituteDtoList = leasingNoticeMapper.queryAssetNeedSubstitute(assetNeedSubstituteDto);
        if (!ObjectUtils.isEmpty(needSubstituteDtoList)) {
            //本来打算封装成一个集合多条现金流一次推送消息的，但是需求写的是现金流一条一条的推送
                /*Map map = new HashMap();
                map.put("uniqueId",getUniqueId());
                map.put("needSubstituteDtoList",needSubstituteDtoList);
                leasingNotice.setNoticeBody(JSON.toJSONString(map));
                //消息推送
                noticePush(leasingNotice, "n006", map, iRequest);*/
            needSubstituteDtoList.forEach(needSubstituteDto -> {
                LeasingNotice leasingNotice = new LeasingNotice();
                leasingNotice.setSourceId(needSubstituteDto.getCashflowId());
                leasingNotice.setSourceType("n006");
                leasingNotice.setResendFlag("N");
                try {
                    needSubstituteDto.setUniqueId(getUniqueId());
                    needSubstituteDto.setCashflowId(null);
                    leasingNotice.setNoticeBody(JSON.toJSONString(needSubstituteDto));
                    //消息推送
                    noticePush(leasingNotice, "n006", needSubstituteDto, iRequest);
                } catch (Exception e) {
                    noticeFail(leasingNotice, iRequest, e);
                }
            });
        }

    }

    /**
     * 易靓需回购通知
     *
     * @param
     * @return
     */
    @Override
    public void assetNeedBuyback(AssetNeedBuybackDto assetNeedBuyback, IRequest iRequest) {
        //查询现金流逾期85天的数据
        List<AssetNeedBuybackDto> assetNeedBuybackDtoList = leasingNoticeMapper.queryAssetNeedBuybackDto(assetNeedBuyback);
        if (!ObjectUtils.isEmpty(assetNeedBuybackDtoList)) {
            assetNeedBuybackDtoList.forEach(assetNeedBuybackDto -> {
                LeasingNotice leasingNotice = new LeasingNotice();
                leasingNotice.setSourceId(assetNeedBuybackDto.getContractId());
                leasingNotice.setSourceType("n007");
                leasingNotice.setResendFlag("N");
                try {
                    Integer[] integerArray = Arrays.stream(assetNeedBuybackDto.getTermNos().split(","))
                            .map(Integer::parseInt)
                            .toArray(Integer[]::new);
                    String termNos = JSON.toJSONString(Arrays.asList(integerArray));
                    assetNeedBuybackDto.setTermNos(termNos);
                    assetNeedBuybackDto.setUniqueId(getUniqueId());
                    assetNeedBuybackDto.setContractId(null);
                    leasingNotice.setNoticeBody(JSON.toJSONString(assetNeedBuybackDto));
                    //消息推送
                    noticePush(leasingNotice, "n007", assetNeedBuybackDto, iRequest);

                } catch (Exception e) {
                    noticeFail(leasingNotice, iRequest, e);
                }
            });

        }
    }

    /**
     * 易靓代扣签约结果通知
     *
     * @param projectId
     * @return
     */
    @Override
    public void withholdContractResult(Long projectId, IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceId(projectId);
        leasingNotice.setSourceType("n008");
        leasingNotice.setResendFlag("N");
        try {
            //通过主键ID查询签约结果数据
            Map<String, Object> withholdContractResultMap = leasingNoticeMapper.queryWithholdContractResult(projectId);
            if (!ObjectUtils.isEmpty(withholdContractResultMap)) {
                withholdContractResultMap.put("uniqueId", getUniqueId());
                leasingNotice.setNoticeBody(JSON.toJSONString(withholdContractResultMap));
                //消息推送
                noticePush(leasingNotice, "n008", withholdContractResultMap, iRequest);
            }
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 易靓期次代扣结果通知
     *
     * @param projectId
     * @return
     */
    @Override
    public void repayPlanRepaidNotify(Long projectId, IRequest iRequest) {
        LeasingNotice leasingNotice = new LeasingNotice();
        leasingNotice.setSourceId(projectId);
        leasingNotice.setSourceType("n009");
        leasingNotice.setResendFlag("N");
        try {
            //通过主键ID查询订单编号和还款方式
            Map<String, Object> repayPlanRepaidNotifyMap = leasingNoticeMapper.queryRepayPlanRepaidNotify(projectId);
            //查询还款期次
            List<RepayPlanRepaidNotifyDto> data = leasingNoticeMapper.queryTerm(projectId);
            repayPlanRepaidNotifyMap.put("date", data);
            if (!ObjectUtils.isEmpty(repayPlanRepaidNotifyMap)) {
                repayPlanRepaidNotifyMap.put("uniqueId", getUniqueId());
                leasingNotice.setNoticeBody(JSON.toJSONString(repayPlanRepaidNotifyMap));

                //消息推送
                noticePush(leasingNotice, "n009", repayPlanRepaidNotifyMap, iRequest);
            }
        } catch (Exception e) {
            noticeFail(leasingNotice, iRequest, e);
        }
    }

    /**
     * 获取UUID
     *
     * @return
     */
    private String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 消息推送
     *
     * @param leasingNotice
     * @param routingKey
     * @param msg
     * @param iRequest
     */
    private void noticePush(LeasingNotice leasingNotice, String routingKey, Object msg, IRequest iRequest) {
        rabbitTemplate.convertAndSend(YLRabbitMqConfiguration.EXCHANGE_GT_YL, routingKey, JSON.toJSONString(msg));
        leasingNotice.setDescription("消息推送成功！");
        leasingNotice.setNoticeStatus("SUCCESS");
        leasingNoticeService.insertNoticeMsg(iRequest, leasingNotice);
    }

    /**
     * 异常消息
     *
     * @param leasingNotice
     * @param iRequest
     * @param e
     */
    private void noticeFail(LeasingNotice leasingNotice, IRequest iRequest, Exception e) {
        leasingNotice.setDescription("消息推送失败！");
        leasingNotice.setNoticeStatus("FAIL");
        leasingNotice.setErrorMessage(e.getMessage() == null ? e.getStackTrace()[0].toString() : e.getMessage());
        leasingNoticeService.insertNoticeMsg(iRequest, leasingNotice);
    }
}
